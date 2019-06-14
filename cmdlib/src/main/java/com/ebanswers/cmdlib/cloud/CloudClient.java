package com.ebanswers.cmdlib.cloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ebanswers.cmdlib.ConstansCommand;
import com.ebanswers.cmdlib.protocol.ProtocolFactory;
import com.ebanswers.cmdlib.utils.FileUtil;
import com.ebanswers.cmdlib.utils.HexUtil;
import com.ebanswers.cmdlib.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public class CloudClient {
    public final static String TAG = "CloudClient";
    private String mSocketUrl,mSocketKey;
    private int mSocketPort;
    private Socket mSocket;
    private static CloudClient mInstance;
    private ScheduledExecutorService cloudThread = Executors.newSingleThreadScheduledExecutor();

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private int reConnect = 0;
    private static byte serialNumber;


    public CloudClient(String url, int port, String key){
        this.mSocketUrl = url;
        this.mSocketPort = port;
        this.mSocketKey = key;
        startConnectSocket();
    }

    public static void init(){
        getInstance();
    }

    //单例
    public static CloudClient getInstance(){
        synchronized (CloudClient.class){
            if (mInstance == null){
                mInstance = new CloudClient(ProtocolFactory.getSocketUrl(),ProtocolFactory.getSocketPort(),ProtocolFactory.getSocketKey());
            }
            return mInstance;
        }
    }

    //开始Socket连接
    public void startConnectSocket(){
        if (mSocketUrl != null && mSocketPort != 0){
            cloudThread.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (mSocket == null){
                        connectSocket();
                    }else if (mSocket != null){
                        if (mSocket.isClosed()){
                            connectSocket();
                        }
                    }
                }
            },0,10, TimeUnit.SECONDS);
        }
    }

    //Socket连接
    public void connectSocket(){
        LogUtil.d(TAG, "connectSocket: ");
        interruptSocket();
        try {
            mSocket = new Socket(mSocketUrl,mSocketPort);
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            sendMacAddress();
            initReadConnect();//初始化读取流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMacAddress(){  //上传物理地址
        if (mOutputStream == null)
            return;
        String mackey = FileUtil.getMac().replaceAll(":", "") + mSocketKey;;
        if (!TextUtils.isEmpty(mackey)) {
            byte[] dataSend = mackey.getBytes();
            byte[] data = new byte[27];
            data[0] = (byte) 0x5a;
            data[1] = (byte) 0xa5;
            data[2] = 0x01;
            data[3] = ConstansCommand.TYPE_HAND_SHAKE;
            data[4] = 0x12;
            data[5] = 0x00;
            for (int i = 0; i < dataSend.length; i++) {
                data[6 + i] = dataSend[i];
            }
            int sum = 0;
            for (int m = 0; m < data.length - 1; m++) {
                sum += data[m];
            }
            data[26] = (byte) sum;
            PrintWriter pw = new PrintWriter(mOutputStream);
            pw.write(HexUtil.bytesToHexString(data));
            pw.flush();
        }
    }

    //初始化Socket
    private void interruptSocket() {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
                if (mInputStream != null){
                    mInputStream.close();
                }
                if (mOutputStream != null){
                    mOutputStream.close();
                }
                mInputStream = null;
                mOutputStream = null;
                mSocket = null;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    //接收服务端的帧指令
    private void initReadConnect() {
        LogUtil.d(TAG, "initReadConnect()");
        byte[] data = new byte[100];
        while (mSocket != null && !mSocket.isClosed() && !mSocket.isInputShutdown()) {
            if (mInputStream != null) {
                try {
                    serialNumber = data[2];
                    int length = mInputStream.read(data);
                    LogUtil.d(TAG, "initReadConnect: " + HexUtil.bytesToHexString(data));
                    onSocketReadListener.onSocketReadListener(length, data);
                } catch (Exception e) {
                    e.printStackTrace();
                    interruptSocket();
                }
            }

        }
    }

    //獲取流水號
    public byte addSerialNumber(){
        serialNumber ++;
        return serialNumber;
    }

    //发送帧指令给服务端
    public void sendCommands(byte[] data) {
        if (data == null)
            return;
        //指令不相同再发送给服务器
        if (mOutputStream != null) {
            try {
                mOutputStream.write(data);
                mOutputStream.flush();
            } catch (IOException e) {
                reConnect++;
                if (reConnect <= 3){
                    connectSocket();
                }
                LogUtil.d(TAG, "sendCommandMsg: failed");
            }
        }
    }

    //協議轉關
    public void sendDataCommands(byte[] data){
        if (data == null){
            return;
        }
        byte[] allData = new byte[23];
        allData[0] = (byte)0x5a;
        allData[1] = (byte)0xa5;
        allData[2] = serialNumber;
        allData[3] = ConstansCommand.TYPE_CONTROL_ALL;
        allData[4] = (byte)0x12;
        allData[5] = (byte)0x01;
        for (int i = 0;i < data.length;i++){
            allData[6 + i] = data[i];
        }
        allData[22] = getCheckSUM(allData,0,allData.length);
        LogUtil.d(TAG, "sendDataCommands: " + HexUtil.bytesToHexString(allData));
        sendCommands(allData);
    }

    //結果嗎
    private byte getCheckSUM(byte[] datas, int start_pid, int end_pid) {
        byte data = 0;
        if (end_pid > datas.length) {
            return 0;
        }
        for (int i = start_pid; i < end_pid; i++) {
            data += datas[i];
        }
        return data;
    }

    //判断Socket连接是否断开
    private Boolean isServerClose(Socket socket) {
        try {
            if (socket != null){
                socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            }
            LogUtil.d(TAG, "isServerClose false");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "isServerClose true");
            return true;
        }
    }

    private OnSocketReadListener onSocketReadListener;

    public void setOnSocketReadListener(OnSocketReadListener onSocketReadListener) {
        this.onSocketReadListener = onSocketReadListener;
    }

    public void clearOnSocketReadListener() {
        this.onSocketReadListener = null;
    }

    public interface OnSocketReadListener {
        void onSocketReadListener(int length, byte[] bytes);
    }

    private class WifiRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            connectSocket();
            LogUtil.d(TAG, "connectService:WifiRecevier");
        }
    }
}
