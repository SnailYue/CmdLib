package com.ebanswers.cmdlib;

import android.content.Context;

import com.ebanswers.cmdlib.callback.CmdListener;
import com.ebanswers.cmdlib.callback.SerialPortConnectedListener;
import com.ebanswers.cmdlib.cloud.CloudClient;
import com.ebanswers.cmdlib.exception.CommandException;
import com.ebanswers.cmdlib.exception.TRDException;
import com.ebanswers.cmdlib.protocol.ProtocolFactory;
import com.ebanswers.cmdlib.utils.HexUtils;
import com.ebanswers.cmdlib.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android_serialport_api.SerialUtil;


/**
 * Created by Snail on 2018/10/17.
 */

public class Command {
    private static final String TAG = "Command";
    private static WeakReference<Context> mContext;
    private volatile ProtocolFactory protocolFactory;
    private CommandReceiver commandReceiver;
    private ExecutorService parseThread = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService sendThread = Executors.newScheduledThreadPool(1);
    private SerialUtil serialUtil;
    private LinkedList<Byte> data = new LinkedList<>();
    private static byte[] dataResult;
    private static int sum = 0;
    private volatile Future futureHeart;
    private volatile Future futureResend;
    private CmdListener cmdListener;

    public Command() {
        protocolFactory = new ProtocolFactory();
        commandReceiver = new CommandReceiver(protocolFactory);
    }

    public static void init(Context context) {
        mContext = new WeakReference<>(context);
    }

    public static class CommandHolder {
        private static final Command command = new Command();
    }

    public static Command getInstance() {
        return CommandHolder.command;
    }

    /**
     * 初始化协议配置文件
     *
     * @param context
     * @param fileName
     * @throws TRDException
     */
    public void setProtocol(Context context, String fileName) throws TRDException {
        protocolFactory.initProtocol(context, fileName);
        if (null == mContext) {
            mContext = new WeakReference<>(context);
        }
        if (protocolFactory.supportAllSerial) {
            parseThread.execute(new Runnable() {
                @Override
                public void run() {
                    serialUtil = new SerialUtil(protocolFactory.serialName, protocolFactory.serialBaudrate, protocolFactory.serialDataSize);
                    if (serialUtil.isOpen) {
                        initCommandReceiver();
                    }
                }
            });
        }
        startHeart();
    }


    /**
     * 接收到上行帧指令
     */
    private void initCommandReceiver() {
        if (null != serialUtil) {
            dataResult = new byte[protocolFactory.mFrameUpAllLength];
            serialUtil.initReadThread(new SerialUtil.SerialPotReadCallBack() {
                @Override
                public void readData(final int var1, byte[] var2) {
                    /**
                     * 为避免出现电控端的一条帧指令出现分多次发送的情况，需对指令做拼接处理
                     */
                    for (int i = 0; i < var1; i++) {
                        if (sum + i >= protocolFactory.mFrameUpAllLength) {
                            sum = 0;
                            break;
                        }
                        dataResult[sum + i] = var2[i];
                    }
                    sum += var1;
                    /**
                     * 帧头校验及帧长度校验
                     */
                    if (dataResult[0] == ProtocolFactory.getFrameHead()[0] && sum == protocolFactory.mFrameUpAllLength) {
                        parseData(sum, dataResult);
                        sum = 0;
                    } else if (dataResult[0] != ProtocolFactory.getFrameHead()[0]) {
                        sum = 0;
                    }
                }
            });
        }
    }

    /**
     * 校验帧指令
     *
     * @param len
     * @param buffer
     */
    private void parseData(final int len, final byte[] buffer) {
        LogUtils.d(TAG, "readData: " + HexUtils.bytesToHexString(buffer));
        data.clear();
        if (len != 0) {
            /**
             *  截取需要校验的部分
             */
            for (int i = protocolFactory.mCheckUpIndex; i <= protocolFactory.mCheckUpEndIndex; i++) {
                data.add(buffer[i - 1]);
            }
            if (protocolFactory.doCalculateCheckData(data, protocolFactory.mCheckUpIndex, protocolFactory.mCheckUpEndIndex)[0] == buffer[protocolFactory.mCheckUpEndIndex]) {
                commandReceiver.analyzeData(buffer);
                if (null != cmdListener) {
                    cmdListener.readCmdData(buffer, buffer.length);
                }
            }
        }
    }

    /**
     * 发送帧指令
     *
     * @param bytes
     * @throws CommandException
     */
    public void send(final byte[] bytes) throws CommandException {
        LogUtils.d(TAG, "send: " + HexUtils.bytesToHexString(bytes));
        if (protocolFactory.supportAllSerial) {
            if (null == serialUtil || !serialUtil.isOpen) {
                throw new CommandException("串口打开失败");
            }
            if (null != serialUtil && serialUtil.isOpen) {
                serialUtil.sendCommands(bytes);
            }
        }
    }

    /**
     * 发送心跳帧
     */
    public void startHeart() {
        if (protocolFactory.sendHeart) {
            futureHeart = sendThread.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendAllStatus();
                    } catch (CommandException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000, protocolFactory.heartFrequency, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 获取当前帧的状态
     *
     * @throws CommandException
     */
    public void sendAllStatus() throws CommandException {
        send(protocolFactory.getCommands(ConstansCommand.CMDTYPE_HEART, null, false));
    }

    /**
     * 发送控制帧调用
     *
     * @param values
     * @throws ConnectException
     * @throws CommandException
     */
    public void control(final ConcurrentHashMap<String, Object> values, final String type) throws ConnectException, CommandException {
        sendThread.execute(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> map = new HashMap<>();
                Iterator iterator = values.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String key = (String) entry.getKey();
                    Integer value = (Integer) entry.getValue();
                    map.put(key, value);
                }
                try {
                    sendThread.awaitTermination(100, TimeUnit.MILLISECONDS);
                    controlAll(map, type);
                    sendThread.awaitTermination(100, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 全指令控制
     *
     * @param values
     * @throws ConnectException
     * @throws CommandException
     */
    public void controlAll(Map<String, Object> values, String type) throws ConnectException, CommandException {
        Iterator iterator = values.keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            int id = protocolFactory.getDownFunctionId(name);
            if (id < 0) {
                throw new ConnectException(name + "非法， 功能名称不在TRD中定义");
            }
        }
        byte[] bytes = protocolFactory.getCommands(type, values, false);
        sendCommands(protocolFactory.getSerialNumber(), bytes);
    }

    /**
     * 发送指令
     *
     * @param serial
     * @param buffer
     * @throws CommandException
     */
    public void sendCommands(byte serial, byte[] buffer) throws CommandException {
        send(buffer);
        if (protocolFactory.isResend) {
            reSendCommand(serial, buffer);
        }
    }

    /**
     * 重发帧指令
     *
     * @param serial
     * @param buffer
     */
    public void reSendCommand(byte serial, final byte[] buffer) {
        final int[] i = {0};
        if (protocolFactory.resendTimes > 0) {
            futureResend = Executors.newScheduledThreadPool(1)
                    .scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                send(buffer);
                                i[0]++;
                                if (i[0] >= protocolFactory.resendTimes) {
                                    futureResend.cancel(true);
                                    futureResend = null;
                                }
                            } catch (CommandException e) {
                                e.printStackTrace();
                            }

                        }
                    }, protocolFactory.resendInterval, protocolFactory.resendInterval, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 发送响应帧
     *
     * @param type
     * @param serial
     * @throws CommandException
     */
    public void sendResponse(String type, int serial) throws CommandException {
        byte[] bytes = getResponse(type, serial);
        send(bytes);
    }

    /**
     * 获取响应帧
     *
     * @param type
     * @param serial
     * @return
     * @throws CommandException
     */
    public byte[] getResponse(String type, int serial) throws CommandException {
        return protocolFactory.getCommands(type, null, serial, true, true);
    }

    /**
     * 获取指定功能的状态，上层要获取各功能状态的值均通过此方法获取
     *
     * @param key
     * @return
     */
    public int getFunctionStatus(String key) {
        int value = 0;
        if (null != protocolFactory && protocolFactory.getAllFunctionsMap().size() > 0) {
            if (null != protocolFactory.getAllFunctionsMap().get(key)) {
                value = protocolFactory.getAllFunctionsMap().get(key).getCur_Value();
            }
        }
        return value;
    }

    /**
     * 接收云端接收过来的数据
     */
    public void getCloudData() {
        CloudClient.getInstance().setOnSocketReadListener(new CloudClient.OnSocketReadListener() {
            @Override
            public void onSocketReadListener(int length, byte[] bytes) {
                LogUtils.d(TAG, "onSocketReadListener: length = " + length + "cmd = " + HexUtils.bytesToHexString(bytes));
                parasDataType(bytes);
            }
        });
    }

    /**
     * 解析云端的数据
     *
     * @param bytes
     */
    public void parasDataType(byte[] bytes) {
        byte[] data = new byte[16];
        switch (bytes[3]) {
            /**
             * 心跳帧
             */
            case ConstansCommand.TYPE_HEART:
                break;
            /**握手帧
             *
             */
            case ConstansCommand.TYPE_HAND_SHAKE:
                break;
            /**
             * 查询帧
             */
            case ConstansCommand.TYPE_SEARCH_ALL:
                break;
            /**
             * 全功能控制帧
             */
            case ConstansCommand.TYPE_CONTROL_ALL:
                break;
            /**
             * 多功能控制帧
             */
            case ConstansCommand.TYPE_CONTROL:
                for (int i = 4; i < 16; i++) {
                    data[i - 4] = bytes[i];
                }
                commandReceiver.analyzeAllStatus(data.length, data);
                try {
                    sendAllStatus();   //发送数据
                } catch (CommandException e) {
                    e.printStackTrace();
                }
                break;
            /**
             * 故障帧
             */
            case ConstansCommand.TYPE_ERROR:
                break;
            default:
                break;
        }
    }

    /**
     * 停止发送帧指令
     */
    public void stopCommand() {
        if (null != futureHeart) {
            futureHeart.cancel(true);
        }
    }

    /**
     * 继续发送帧指令
     */
    public void startCommand() {
        startHeart();
    }


    /**
     * 设置完整指令接听接口
     *
     * @param listener
     */
    public void setCmdListener(CmdListener listener) {
        this.cmdListener = listener;
    }

    /**
     * 串口连接状态监听
     *
     * @param listener
     */
    public void setOnSerialPortConnectedListener(SerialPortConnectedListener listener) {
        if (null != serialUtil) {
            serialUtil.setConnectedListener(listener);
        }
    }

}
