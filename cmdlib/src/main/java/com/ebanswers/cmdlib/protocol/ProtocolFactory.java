package com.ebanswers.cmdlib.protocol;

import android.content.Context;
import android.util.SparseArray;

import com.ebanswers.cmdlib.ConstansCommand;
import com.ebanswers.cmdlib.callback.ViewUIManager;
import com.ebanswers.cmdlib.exception.CommandException;
import com.ebanswers.cmdlib.exception.TRDException;
import com.ebanswers.cmdlib.protocol.bean.PCFrames;
import com.ebanswers.cmdlib.protocol.bean.PCmdType;
import com.ebanswers.cmdlib.protocol.bean.PTFunction;
import com.ebanswers.cmdlib.utils.ByteUtil;
import com.ebanswers.cmdlib.utils.CRC16Util;
import com.ebanswers.cmdlib.utils.CRC8Util;
import com.ebanswers.cmdlib.utils.SUMUtil;
import com.ebanswers.cmdlib.utils.HexUtil;
import com.ebanswers.cmdlib.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by Snail on 2018/10/19.
 */

public class ProtocolFactory {
    private static final String TAG = "ProtocolFactory";
    private volatile Protocol protocol;
    /**
     * 全功能集
     */
    private ConcurrentMap<String, PTFunction> allFunctionsMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, String> allFunctionsNoMap = new ConcurrentHashMap<>();

    /**
     * 上行帧功能集
     */
    private ConcurrentMap<String, PTFunction> upFunctionsMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, String> upFunctionsNoMap = new ConcurrentHashMap<>();
    /**
     * 下行帧功能集
     */
    private ConcurrentMap<String, PTFunction> downFunctionsMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, String> downFunctionsNoMap = new ConcurrentHashMap<>();

    /**
     * 帧格式相关配置
     */
    public ConcurrentMap<Integer, PCFrames> pcFrameMap = new ConcurrentHashMap<>();

    public LinkedList<Byte> commands = new LinkedList<>();


    /**
     * 帧类型集合
     */
    public SparseArray<PCmdType> pCmdTypes = new SparseArray<>();

    /**
     * 下行帧帧头
     */
    public static byte[] mFrameHead = new byte[]{};
    /**
     * 上行帧帧头
     */
    public static byte[] mFrameHeadResponse = new byte[]{};

    /**
     * 上行帧与下行帧的总长度
     */
    public int mFrameUpAllLength, mFrameDownAllLength;

    /**
     * 上行帧与下行帧数据位的长度
     */
    public int mFrameCount, mFrameUpDataLen, mFrameDownDataLen;
    /**
     * 上行校验帧的起始位与结束位
     */
    public int mCheckUpIndex, mCheckUpEndIndex;
    /**
     * 下行校验帧的起始位与结束位
     */
    public int mCheckDownIndex, mCheckDownEndIndex;

    public int mCheckLen;

    /**
     * 检验方式
     */
    public String mCheckType;
    /**
     * 是否发送心跳
     */
    public boolean sendHeart;
    /**
     * 心跳频率
     */
    public int heartFrequency = 1000;
    /**
     * 支持全指令
     */
    public boolean supportAllSerial = true;
    /**
     * 是否重发
     */
    public boolean isResend;
    /**
     * 是否多类型指令
     */
    public boolean isSupportTypes;
    /**
     * 重发频率
     */
    public int resendInterval = 0;
    /**
     * 重发间隔
     */
    public int resendTimes = 0;
    /**
     * 是否支持单指令
     */
    public boolean supportSingleContorl = false;
    /**
     * 串口名称
     */
    public String serialName = "/dev/ttyS1";
    /**
     * 串口波特率
     */
    public int serialBaudrate = 9600;
    /**
     * 串口数据位
     */
    public int serialDataSize = 8;
    /**
     * 串口校验位
     */
    public int serialParity = -1;
    /**
     * 串口停止位
     */
    public int serialStopBit = 1;

    /**
     * socketUrl
     */
    public static String socketUrl;
    /**
     * socketPort
     */
    public static int socketPort;

    public static String socketKey;
    /**
     * 是否在点击的时候改变自身的值
     */
    private boolean isChangeStatusSelf = false;
    /**
     * 水流号
     */
    private static byte serialNumber;
    /**
     * 是否支持流水号
     */
    private static boolean isSupportSerialNumber;

    public ProtocolFactory() {
    }

    /**
     * 初始化协议
     *
     * @param context
     * @param filename
     * @throws TRDException
     */
    public void initProtocol(Context context, String filename) throws TRDException {
        StringBuilder json = new StringBuilder();
        if (filename.contains(".json")) {
            try {
                if (null != context.getAssets()) {
                    json.append(getAssetsToString(context.getAssets().open(filename)));
                }
            } catch (IOException e) {
                throw new TRDException(filename + "文件打开失败");
            }
            Type type = new TypeToken<Protocol>() {
            }.getType();
            protocol = new Gson().fromJson(json.toString(), type);
            initPTConfig();
        } else {
            throw new TRDException("文件格式错误，请放入JSON格式的文件");
        }
    }

    /**
     * 初始化配置
     */
    public void initPTConfig() {
        if (null != protocol.getPTConfig()) {
            this.sendHeart = protocol.getPTConfig().isSendHeart();
            this.heartFrequency = protocol.getPTConfig().getHeartFrequency();
            this.supportAllSerial = protocol.getPTConfig().isSupportAllSerial();
            this.isResend = protocol.getPTConfig().isReSend();
            this.resendInterval = protocol.getPTConfig().getResendInterval();
            this.resendTimes = protocol.getPTConfig().getResendTimes();
            this.supportSingleContorl = protocol.getPTConfig().isSupportSingleContorl();
            this.serialName = protocol.getPTConfig().getSerialName();
            this.serialBaudrate = protocol.getPTConfig().getSerial_baudrate();
            this.serialDataSize = protocol.getPTConfig().getSerial_csize();
            this.serialParity = protocol.getPTConfig().getSerial_parity();
            this.serialStopBit = protocol.getPTConfig().getSerial_stopbits();
            this.isChangeStatusSelf = protocol.getPTConfig().isChangeStatusSelf();
            this.isSupportTypes = protocol.getPTConfig().isSupportFrameType();
            socketUrl = protocol.getPCloudControl().getSocketUrl();
            socketPort = protocol.getPCloudControl().getSocketPort();
            socketKey = protocol.getPCloudControl().getSocketKey();
            initPCFrames();
            initFunctions();
        }
    }

    /**
     * 初始化帧
     */
    public void initPCFrames() {
        List<PCFrames> framesList = null;
        if (null != protocol) {
            framesList = protocol.getPCFrames();
        }
        if (null != framesList) {
            mFrameUpAllLength = 0;
            mFrameDownAllLength = 0;
            pcFrameMap.clear();
            pCmdTypes.clear();
            mFrameCount = framesList.size();

            if (mFrameCount > 0) {
                for (int i = 0; i < mFrameCount; i++) {
                    PCFrames pcFrames = framesList.get(i);
                    pcFrameMap.put(pcFrames.getPid(), pcFrames);
                    LogUtil.d(TAG, "initFrames: " + pcFrames.getPid() + "," + pcFrames.getLength());

                    if (pcFrames.getPtype().equals(ConstansCommand.FRAME_LENGTH)) {
                        mFrameDownAllLength = pcFrames.getCmdLength().getDownLength();
                        mFrameUpAllLength = pcFrames.getCmdLength().getUpLength();
                    }
                    if (pcFrames.getPtype().equals(ConstansCommand.FRAME_HEAD)) {
                        mFrameHead = HexUtil.hexStr2Bytes(pcFrames.getCmdHead().getSendCode());
                        mFrameHeadResponse = HexUtil.hexStr2Bytes(pcFrames.getCmdHead().getResponseCode());
                    }
                    if (pcFrames.getPtype().equals(ConstansCommand.FRAME_CHECKSUM)) {
                        mCheckUpIndex = pcFrames.getChecksum().getUpStartPid();
                        mCheckUpEndIndex = pcFrames.getChecksum().getUpEndPid();
                        mCheckDownIndex = pcFrames.getChecksum().getDownStartPid();
                        mCheckDownEndIndex = pcFrames.getChecksum().getDownEndPid();
                        mCheckLen = pcFrames.getLength();
                        mCheckType = pcFrames.getChecksum().getType();
                    }
                    if (pcFrames.getPtype().equals(ConstansCommand.FRAME_UP_CMDDATA)) {
                        mFrameUpDataLen = pcFrames.getLength();
                    }
                    if (pcFrames.getPtype().equals(ConstansCommand.FRAME_DOWN_CMDDATA)) {
                        mFrameDownDataLen = pcFrames.getLength();
                    }
                    if (pcFrames.getPtype().equals(ConstansCommand.FRAME_CMDTYPE)) {
                        for (int j = 0; j < pcFrames.getCmdtypes().size(); j++) {
                            pCmdTypes.put(j, pcFrames.getCmdtypes().get(j));
                        }
                    }
                }
                LogUtil.d(TAG, "initFrames: mFrameUpAllLength:" + mFrameUpAllLength + " ,mFrameDownAllLength" + mFrameDownAllLength);
            }
        }
    }


    /**
     * 初始化协议中的功能集合
     */
    public void initFunctions() {
        /**
         * 全功能集
         */
        if (null != protocol.getAllFunctions() && 0 != protocol.getAllFunctions().size()) {
            int len = protocol.getAllFunctions().size();
            for (int i = 0; i < len; i++) {
                allFunctionsMap.put(protocol.getAllFunctions().get(i).getName(), protocol.getAllFunctions().get(i));
                allFunctionsNoMap.put(protocol.getAllFunctions().get(i).getNo(), protocol.getAllFunctions().get(i).getName());
            }
        }
        /**
         * 上行帧功能集
         */
        if (null != protocol.getUpFunction() && 0 != protocol.getUpFunction().size()) {
            int len = protocol.getUpFunction().size();
            for (int i = 0; i < len; i++) {
                upFunctionsMap.put(protocol.getUpFunction().get(i).getName(), protocol.getUpFunction().get(i));
                upFunctionsNoMap.put(protocol.getUpFunction().get(i).getNo(), protocol.getUpFunction().get(i).getName());
            }
        }
        /**
         * 下行帧功能集
         */
        if (null != protocol.getDownFunction() && 0 != protocol.getDownFunction().size()) {
            int len = protocol.getDownFunction().size();
            for (int i = 0; i < len; i++) {
                downFunctionsMap.put(protocol.getDownFunction().get(i).getName(), protocol.getDownFunction().get(i));
                downFunctionsNoMap.put(protocol.getDownFunction().get(i).getNo(), protocol.getDownFunction().get(i).getName());
            }
        }
    }

    /**
     * 获取下行帧帧头
     *
     * @return
     */
    public static byte[] getFrameHead() {
        return mFrameHead;
    }

    /**
     * 获取帧尾
     *
     * @return
     */
    public static byte[] getFrameFooter() {
        return mFrameHeadResponse;
    }

    /**
     * 获取流水号
     */
    public byte getSerialNumber() {
        return serialNumber;
    }

    /**
     * 获取下行控制帧集合
     *
     * @return
     */
    public List<PTFunction> getProtocolDownFunctions() {
        return protocol.getDownFunction();
    }

    /**
     * 获取上行控制帧集合
     *
     * @return
     */
    public List<PTFunction> getProtocolUpFunctions() {
        return protocol.getUpFunction();
    }

    /**
     * 获取上行控制帧中指定位的长度
     *
     * @param code
     * @return
     */
    public Byte getUpFunctionBit(int code) {
        if (null == protocol) {
            return 0;
        }
        String name = upFunctionsNoMap.get(code);
        if (null == upFunctionsMap.get(name)) {
            return 0;
        }
        int len = upFunctionsMap.get(name).getLength();
        return (byte) len;
    }

    /**
     * 获取上行控制帧指定的功能名
     *
     * @param code
     * @return
     */
    public String getUpFunctionName(int code) {
        if (null == protocol) {
            return "";
        } else {
            return null == upFunctionsNoMap.get(code) ? "" : upFunctionsNoMap.get(code);
        }
    }

    /**
     * 改变全集合中数据的值
     *
     * @param name
     * @param value
     */
    public void setAllFunctionsMap(String name, int value) {
        /**
         * 设置上一个状态值
         */
        allFunctionsMap.get(name).setLast_Value(allFunctionsMap.get(name).getCur_Value());
        /**
         * 设置当前状态值
         */
        allFunctionsMap.get(name).setCur_Value(value);
    }


    /**
     * 遍历全集合，当状态值发生改变时，通知UI进行状态的更新
     *
     * @throws JSONException
     */
    public void notifyStatusChanges() throws JSONException {
        int size = allFunctionsNoMap.size();
        for (int i = 0; i < size; i++) {
            if (allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).isNoChangeNotify()) {
                ViewUIManager.getInstance().notifyStatus("", allFunctionsNoMap.get(i + 1), allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).getCur_Value());
                allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).setLast_Value(allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).getCur_Value());
            } else {
                if (allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).getLast_Value() != allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).getCur_Value()) {
                    ViewUIManager.getInstance().notifyStatus("", allFunctionsNoMap.get(i + 1), allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).getCur_Value());
                    allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).setLast_Value(allFunctionsMap.get(allFunctionsNoMap.get(i + 1)).getCur_Value());
                }
            }
        }
    }

    /**
     * 帧头校验
     *
     * @param datas
     * @param start
     * @return
     */
    public int isHead(LinkedList<Byte> datas, int start) {
        int endindex = 0;
        int len = mFrameHead.length;
        if (null != datas && (datas.size() > len + start)) {
            for (int i = 0; i < len; i++) {
                if (datas.get(i + start) == mFrameHead[i]) {
                    endindex++;
                }
            }
            if (endindex < len) {
                endindex = 0;
            }
        }
        return endindex + start;
    }

    /**
     * 校验数据
     *
     * @param data
     * @param checkindex
     * @param checkendindex
     * @param len
     * @return
     */
    public boolean checkData(LinkedList<Byte> data, int checkindex, int checkendindex, int len) {
        LogUtil.d(TAG, "checkData: size = " + data.size() + ",checkendindex = " + checkendindex + " checkindex = " + checkindex);
        int check = 0;
        switch (mCheckType) {
            case ConstansCommand.CHECK_SUM:
                check = SUMUtil.getCheckSUM(data, checkindex, checkendindex);
                check = (check & 0xff);
                break;
            case ConstansCommand.CHECK_CRC8:
                check = CRC8Util.calcCrc8(data, checkindex, checkendindex);
                break;
            case ConstansCommand.CHECK_CRC16:
                check = CRC16Util.calcCrc16(data, checkindex, checkendindex);
                break;
            default:
                break;
        }
        int oldcheck = 0;
        for (int i = 0; i < len; i++) {
            oldcheck += (((data.get(i + checkendindex) & 0xff) << (i - 1) * 8));
        }
        return oldcheck == check;
    }

    /**
     * 获取指定类型的帧
     *
     * @param type
     * @param command
     * @param isResend
     * @return
     * @throws CommandException
     */
    public byte[] getCommands(String type, Map<String, Object> command, boolean isResend) throws CommandException {
        return getCommands(type, command, isResend ? serialNumber : addSerialNumber(), false, isResend);
    }

    /**
     * 获取流水号
     *
     * @return
     */
    public byte addSerialNumber() {
        serialNumber++;
        return serialNumber;
    }

    /**
     * 获取帧指令
     *
     * @param type
     * @param command
     * @param serial
     * @param isResponse
     * @param isresend
     * @return
     * @throws CommandException
     */
    public synchronized byte[] getCommands(String type, Map<String, Object> command, int serial, boolean isResponse, boolean isresend) throws CommandException {
        commands.clear();
        int sizepcf = pcFrameMap.size();
        byte[] cmd = new byte[0];
        try {
            cmd = getCmdData(type, command, isResponse, isresend);
            LogUtil.d(TAG, "getCommands: " + HexUtil.bytesToHexString(cmd));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < sizepcf; i++) {
            PCFrames pcFrame = pcFrameMap.get(i + 1);
            if (null != pcFrame && !ConstansCommand.FRAME_UP_CMDDATA.equals(pcFrame.getPtype())) {
                int pcflen = pcFrame.getLength();
                byte[] pcfData = new byte[pcflen];
                switch (pcFrame.getPtype()) {
                    case ConstansCommand.FRAME_HEAD:
                        String data = isResponse ? pcFrame.getCmdHead().getResponseCode() : pcFrame.getCmdHead().getSendCode();
                        pcfData = HexUtil.hexStr2Bytes(data);
                        break;
                    case ConstansCommand.FRAME_SERIAL_NUMBER:
                        pcfData = ByteUtil.putInt(pcfData, serial);
                        break;
                    case ConstansCommand.FRAME_LENGTH:
                        pcfData = new byte[]{null == cmd ? 0 : (byte) (isResponse ? cmd.length + 1 : cmd.length + 2)};
                        break;
                    case ConstansCommand.FRAME_CMDTYPE:
                        pcfData = getCmdTypeByte(type);
                        break;
                    case ConstansCommand.FRAME_DOWN_CMDDATA:
                        if (isResponse) {
                            commands.add((byte) 0x00);
                        }
                        if (cmd != null) {
                            pcfData = cmd;
                            pcflen = cmd.length;
                        }
                        break;
                    case ConstansCommand.FRAME_CHECKSUM:
                        pcfData = doCalculateCheckData(commands, mCheckDownIndex, mCheckDownEndIndex);
                        break;
                    case ConstansCommand.FRAME_FOOTER:
                        String foot = isResponse ? pcFrame.getCmdFooter().getResponseCode() : pcFrame.getCmdFooter().getSendCode();
                        pcfData = HexUtil.hexStr2Bytes(foot);
                        break;
                    default:
                        break;
                }
                if (pcfData.length == pcflen) {
                    for (int j = 0; j < pcflen; j++) {
                        commands.add(pcfData[j]);
                    }
                } else {
                    LogUtil.e(TAG, "getCommands: Frame data length error" + pcFrame.getPtype());
                }
            }
        }
        LogUtil.d(TAG, "getCommands: Commands = " + commands.size());
        byte[] data = new byte[commands.size()];
        for (int i = 0; i < commands.size(); i++) {
            data[i] = commands.get(i);
        }
        return data;
    }


    /**
     * 用于处理单一帧格式和多种帧格式
     *
     * @param type
     * @param command
     * @param isResponse
     * @param isresend
     * @return
     * @throws CommandException
     * @throws JSONException
     */
    public byte[] getCmdData(String type, Map<String, Object> command, boolean isResponse, boolean isresend) throws CommandException, JSONException {
        byte[] data = null;
        /**
         * 单一帧格式
         */
        if (!isSupportTypes) {
            data = checkMutexValue(command, isresend);
        }
        /**
         * 多种帧格式
         */
        else {
            switch (type) {
                case ConstansCommand.CMDTYPE_HEART:
                    data = checkMutexValue(command, isresend);
                    break;
                case ConstansCommand.CMDTYPE_CONTROL:
                    data = checkMutexValue(command, isresend);
                    break;
                case ConstansCommand.CMDTYPE_QUERY:
                    data = checkMutexValue(command, isresend);
                    break;
                case ConstansCommand.CMDTYPE_SHAKE:
                    data = checkMutexValue(command, isresend);
                    break;
                case ConstansCommand.CMDTYPE_ERROR:
                    data = checkMutexValue(command, isresend);
                    break;
                default:
                    data = checkMutexValue(command, isresend);
                    break;
            }
        }
        return data;
    }

    /**
     * 检查互斥功能的值
     *
     * @param command
     * @param isresend
     * @throws JSONException
     */
    public byte[] checkMutexValue(Map<String, Object> command, boolean isresend) throws JSONException {
        byte[] data = null;
        if (null != command) {
            changeMutexValue(command);
        }
        data = getCmdControlData(command, isresend);
        return data;
    }

    /**
     * 计算校验码
     *
     * @param datas
     * @param startIndex
     * @param endIndex
     * @return
     */
    public byte[] doCalculateCheckData(LinkedList<Byte> datas, int startIndex, int endIndex) {
        byte[] data = new byte[mCheckLen];
        switch (mCheckType) {
            case ConstansCommand.CHECK_SUM:
                data[0] = SUMUtil.getCheckSUM(datas, startIndex, endIndex);
                break;
            case ConstansCommand.CHECK_CRC8:
                data[0] = CRC8Util.calcCrc8(datas, startIndex, endIndex);
                break;
            case ConstansCommand.CHECK_CRC16:
                int check = CRC16Util.calcCrc16(datas, startIndex, endIndex);
                data[0] = (byte) (check & 0x00FF);
                if (mCheckLen == 2) {
                    data[1] = (byte) ((check & 0xFF00) >> 8);
                }
                break;
            default:
                data[0] = 0x00;
                break;
        }
        return data;
    }

    /**
     * 合成控制位数据
     *
     * @param command
     * @param isHeart
     * @return
     * @throws JSONException
     */
    public byte[] getCmdControlData(Map<String, Object> command, boolean isHeart) throws JSONException {
        /**
         * 获取下行指令数字的长度
         */
        int size = getDownProtocolFunctions().size();
        /**
         * 获取下行指令的位数
         */
        byte len = getDownProtocalFuctionLen();
        LinkedList<Byte> cmd = new LinkedList<>();
        int index = 0;
        int id = 0;
        int offset = 0;
        int result = 0;
        while (index < len * 8 && id < size) {
            id++;
            int bit = getDownFunctionBit(id);
            /**
             * 所占字节长度，不足一字节的算一字节计算,因为下面的移位循环计数从1开始的。所以再次加一。
             */
            int bl = (bit / 8 + ((bit % 8 == 0) ? 0 : 1)) + 1;
            /**
             * 当前位移字节长度
             */
            offset = index / 8;
            /**
             * 当前位移长度
             */
            index += bit;
            int value = 0;
            if (!isHeart) {
                String name = getDownFunctionName(id);
                if (null != command && command.containsKey(name)) {
                    value = (int) command.get(name);
                    /**
                     * 改变自身状态
                     */
                    if (isChangeStatusSelf) {
                        if (allFunctionsMap.get(name).isValueChange()) {
                            allFunctionsMap.get(name).setLast_Value(allFunctionsMap.get(name).getCur_Value());
                            allFunctionsMap.get(name).setCur_Value(value);
                        }
                    }
                } else {
                    value = null == allFunctionsMap.get(name) ? 0 : allFunctionsMap.get(name).getCur_Value();
                }
            }
            for (int i = 1; i < bl; i++) {
                if (cmd.size() < (i - 1 + offset)) {
                    cmd.add((byte) result);
                    result = 0;
                }
                /**
                 * 右移 的长度---- 8的整数
                 */
                int rshift = (bl - 1 - i) * 8;
                byte rv = (byte) ((value >> rshift) & 0xFF);
                /**
                 * 大于一个字节的 ，右移动累加
                 */
                int lshift = ((i + offset) * 8 - index);
                byte ret = (byte) (rv << (lshift > 0 ? lshift : 0) & 0xff);
                /**
                 * 将该位上的值左移，取值
                 */
                result = result | ret;
            }
        }
        if (cmd.size() < len) {
            cmd.add((byte) result);
            result = 0;
        }
        byte[] data = new byte[cmd.size()];
        for (int i = 0; i < cmd.size(); i++) {
            data[i] = cmd.get(i);
        }
        LogUtil.d(TAG, "getCmdControlData: " + HexUtil.bytesToHexString(data));
        return data;
    }

    /**
     * 更改互斥的功能的值
     *
     * @param command
     */
    public void changeMutexValue(Map<String, Object> command) {
        String name;
        int size = downFunctionsMap.size();
        int count = 1;
        while (count <= size) {
            name = getDownFunctionName(count);
            if (command.containsKey(name) && null != allFunctionsMap.get(name) && null != allFunctionsMap.get(name).getValue_Des()) {
                int desSize = allFunctionsMap.get(name).getValue_Des().size();
                for (int i = 0; i < desSize; i++) {
                    if ((null != allFunctionsMap.get(name).getValue_Des().get(i).getMutex()) && (1 == (int) command.get(name))) {
                        int mutexSize = allFunctionsMap.get(name).getValue_Des().get(i).getMutex().size();
                        for (int j = 0; j < mutexSize; j++) {
                            String key = allFunctionsMap.get(name).getValue_Des().get(i).getMutex().get(j).getFunc();
                            allFunctionsMap.get(key).setLast_Value(allFunctionsMap.get(name).getCur_Value());
                            allFunctionsMap.get(key).setCur_Value(allFunctionsMap.get(name).getValue_Des().get(i).getMutex().get(j).getVal());
                        }
                    }
                }
            }
            count++;
        }
    }


    /**
     * 获取下行控制帧的功能集合
     *
     * @return
     */
    public List<PTFunction> getDownProtocolFunctions() {
        return protocol.getDownFunction();
    }

    /**
     * 获取下行指令的总长度
     *
     * @return
     */
    public byte getDownProtocalFuctionLen() {
        if (null == protocol) {
            return 0;
        }
        int bit = 0;
        int len = downFunctionsMap.size();
        if (len > 0) {
            for (int i = 1; i <= len; i++) {
                bit += downFunctionsMap.get(downFunctionsNoMap.get(i)).getLength();
            }
        }
        return (byte) (bit / 8 + ((bit % 8 > 0) ? 1 : 0));
    }


    /**
     * 获取下行指令中指定位的名字
     *
     * @param code
     * @return
     */
    public String getDownFunctionName(int code) {
        if (null == protocol) {
            return "";
        }
        return downFunctionsNoMap.get(code);
    }

    /**
     * 获取下行指令中指定位的长度
     *
     * @param code
     * @return
     */
    public Byte getDownFunctionBit(int code) {
        if (null == protocol) {
            return 0;
        }
        if (null == downFunctionsMap.get(downFunctionsNoMap.get(code))) {
            return 0;
        }
        return (byte) downFunctionsMap.get(downFunctionsNoMap.get(code)).getLength();
    }

    /**
     * 获取下行指令中指定功能的id
     *
     * @param name
     * @return
     */
    public byte getDownFunctionId(String name) {
        if (null == protocol) {
            return 0;
        }
        if (null == downFunctionsMap.get(name)) {
            return 0;
        }
        return (byte) downFunctionsMap.get(name).getNo();
    }

    /**
     * 获取Assets文件夹中的文件
     *
     * @param is
     * @return
     */
    public StringBuilder getAssetsToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    /**
     * 获取Socket地址
     *
     * @return
     */
    public static String getSocketUrl() {
        return socketUrl;
    }

    /**
     * 获取Socket端口号
     *
     * @return
     */
    public static int getSocketPort() {
        return socketPort;
    }

    /**
     * 获取socket的key
     *
     * @return
     */
    public static String getSocketKey() {
        return socketKey;
    }

    /**
     * 是否支持流水号
     *
     * @return
     */
    public boolean isSupportSerialNumber() {
        if (null != this.pcFrameMap && pcFrameMap.size() > 0) {
            for (int i = 0; i < pcFrameMap.size(); i++) {
                if (pcFrameMap.get(i + 1).getPtype().equals(ConstansCommand.FRAME_SERIAL_NUMBER)) {
                    isSupportSerialNumber = true;
                    break;
                } else {
                    isSupportSerialNumber = false;
                }
            }
        }
        return isSupportSerialNumber;
    }

    /**
     * 获取全功能帧指令集合
     *
     * @return
     */
    public ConcurrentMap<String, PTFunction> getAllFunctionsMap() {
        return allFunctionsMap;
    }

    /**
     * 根据帧类型获取帧码
     *
     * @param type
     * @return
     */
    public byte[] getCmdTypeByte(String type) {
        byte[] typeCode = new byte[1];
        for (int i = 0; i < pCmdTypes.size(); i++) {
            PCmdType pCmdType = pCmdTypes.get(i);
            if (type.equals(pCmdType.getType())) {
                typeCode[0] = pCmdType.getCode();
                break;
            }
        }
        return typeCode;
    }

    /**
     * 获取帧类型集合
     *
     * @return
     */
    public SparseArray<PCmdType> getpCmdTypes() {
        return pCmdTypes;
    }
}
