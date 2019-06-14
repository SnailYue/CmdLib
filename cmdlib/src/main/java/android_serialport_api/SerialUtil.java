package android_serialport_api;



import com.ebanswers.cmdlib.callback.SerialPortConnectedListener;
import com.ebanswers.cmdlib.utils.HexUtil;
import com.ebanswers.cmdlib.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public class SerialUtil {
    private static final String TAG = "SerialUtil";
    private int BAUD_RATE = 9600;
    private String TTY = "/dev/ttyS1";
    private int FLAG = 0;
    private SerialPort serialPort = null;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private SerialPotReadCallBack readCallBack;
    private String errorMsg;
    public boolean isOpen = false;
    private Thread readThread = null;
    private SerialPortConnectedListener listener;
    private volatile long receiveCmdTime = System.currentTimeMillis();
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public SerialUtil() throws Exception {
        throw new Exception("tty、baudRate、flag is null ?");
    }

    public SerialUtil(String tty, int baudRate, int flag) {
        this.openSerial(tty, baudRate, flag);
    }

    /**
     * 打开串口
     * @param tty
     * @param baudRate
     * @param flag
     * @return
     */
    public boolean openSerial(String tty, int baudRate, int flag) {
        TTY = tty;
        BAUD_RATE = baudRate;
        FLAG = flag;
        try {
            this.serialPort = new SerialPort(new File(tty), baudRate, flag);
            this.mInputStream = this.serialPort.getInputStream();
            this.mOutputStream = this.serialPort.getOutputStream();
            LogUtil.d(TAG, "open success:" + tty);
        } catch (IOException var5) {
            LogUtil.e(TAG, "open failed:" + var5.toString());
            this.errorMsg = var5.toString();
            this.isOpen = false;
            return false;
        } catch (SecurityException var6) {
            LogUtil.e(TAG, "open failed:have no read/write permission to the serial port");
            this.isOpen = false;
            this.errorMsg = var6.toString();
            return false;
        }
        checkIsConneted();
        this.isOpen = true;
        this.errorMsg = null;
        return true;
    }

    /**
     * 获取错误信息
     * @return
     */
    public String getErrorMsg() {
        return this.errorMsg;
    }

    /**
     * 发送帧指令
     * @param cmds
     */
    public void sendCommands(byte[] cmds) {
        LogUtil.d(TAG, "sendCommands: " + HexUtil.bytesToHexString(cmds));
        if (null != this.mOutputStream) {
            try {
                this.mOutputStream.write(cmds);
                this.mOutputStream.flush();
            } catch (IOException var3) {
                LogUtil.e(TAG, "sendCommands failed:" + var3.toString());
                this.errorMsg = var3.toString();
            }
        } else {
            LogUtil.e(TAG, "sendCommands failed:null");
        }

    }

    /**
     * 初始化串口帧指令的接收
     * @param callBack
     */
    public void initReadThread(SerialPotReadCallBack callBack) {
        this.readCallBack = callBack;
        if (null != SerialUtil.this.mInputStream) {
            this.readThread = new Thread() {
                @Override
                public void run() {
                    byte[] data = new byte[100];
                    while (!this.isInterrupted()) {
                        try {
                            if (null != SerialUtil.this.mInputStream && null != data) {
                                int len = SerialUtil.this.mInputStream.read(data);
                                if (len > 0) {
                                    if (SerialUtil.this.readCallBack != null) {
                                        SerialUtil.this.readCallBack.readData(len, data);
                                        receiveCmdTime = System.currentTimeMillis();
                                    }
                                }
                            }
                        } catch (IOException var3) {
                            this.interrupt();
                            LogUtil.e(TAG, "readData error:" + var3.toString());
                        }
                    }

                }
            };
            this.readThread.start();
        } else {
            LogUtil.e(TAG, "initReadThread failed:null");
        }
    }

    /**
     * 停止接收帧指令
     */
    public void stopReadThread() {
        if (null != this.readThread) {
            this.readThread.interrupt();
            this.readThread = null;
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        try {
            if (null != this.mInputStream) {
                this.mInputStream.close();
                this.mInputStream = null;
            }

            if (null != this.mOutputStream) {
                this.mOutputStream.close();
                this.mOutputStream = null;
            }

            if (null != this.serialPort) {
                this.serialPort.close();
                this.serialPort = null;
            }

            this.isOpen = false;
        } catch (IOException var2) {
            this.isOpen = false;
            LogUtil.e(TAG, "closeSerialPort failed:" + var2.toString());
        }

    }

    /**
     * 检测通讯是否正常
     * 每5s检测一次串口是否通讯正常
     */
    public void checkIsConneted() {
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                boolean status = System.currentTimeMillis() - receiveCmdTime < 2000 ? true : false;
                if (status && null != listener) {
                    listener.connected();
                }
                if (!status && null != listener) {
                    listener.disConnected();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * 设置帧指令连接状态监听
     * @param listener
     */
    public void setConnectedListener(SerialPortConnectedListener listener) {
        this.listener = listener;
    }

    /**
     * 重置接收到帧指令的时间
     */
    public void resetReceiveTime() {
        this.receiveCmdTime = System.currentTimeMillis();
    }

    public interface SerialPotReadCallBack {
        void readData(int var1, byte[] var2);
    }
}
