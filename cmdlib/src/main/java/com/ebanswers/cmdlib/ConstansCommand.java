package com.ebanswers.cmdlib;

/**
 * Created by air
 *
 * @link https://github.com/LiShiHui24740
 * On 2018/3/20 下午1:43.
 * @description:
 */

public class ConstansCommand {
    /**
     心跳帧
     */
    public static final byte TYPE_HEART = 0x00;
    /**
     * 握手命令帧
     */
    public static final byte TYPE_HAND_SHAKE = 0x01;

    /**
     * 状态查询帧(全功能)
     */
    public static final byte TYPE_SEARCH_ALL = 0x20;

    /**
     * 状态控制命令帧(全功能)
     */
    public static final byte TYPE_CONTROL_ALL = 0x21;
    /**
     * 状态控制命令帧(多功能)
     */
    public static final byte TYPE_CONTROL = 0x31;

    /**
     * 故障报警帧
     */
    public static final byte TYPE_ERROR = (byte) 0xff;

    //=======================================
    /**
     * 帧头
     */
    public static final String FRAME_HEAD = "CmdHead";
    /**
     * 序列号
     */
    public static final String FRAME_SERIAL_NUMBER = "SerialNumber";
    /**
     * 数据类型位
     */
    public static final String FRAME_CMDTYPE  = "CmdType";
    /**
     * 数据位
     */
    public static final String FRAME_UP_CMDDATA = "CmdUpData";
    /**
     * 数据位
     */
    public static final String FRAME_DOWN_CMDDATA = "CmdDownData";
    /**
     * 长度位
     */
    public static final String FRAME_LENGTH = "CmdLength";
    /**
     * 检验位
     */
    public static final String FRAME_CHECKSUM = "CheckSum";
    /**
     * 帧尾
     */
    public static final String FRAME_FOOTER = "CmdFooter";


    //===========================数据类型=========
    //心跳帧
    public static final String CMDTYPE_HEART = "HEART";
    //全指令控制帧
    public static final String CMDTYPE_CONTROL = "CONTROL";
    //单指令控制帧
    public static final String CMDTYPE_CONTROL_SIGNLE = "CONTROL_SIGNLE";
    //状态查询帧
    public static final String CMDTYPE_QUERY = "QUERY";
    //握手帧
    public static final String CMDTYPE_SHAKE = "SHAKE";
    //故障上报帧
    public static final String CMDTYPE_ERROR = "ERROR";
    //云端数据
    public static final String CMDTYPE_CLOUD = "CLOUD";
    //muc控制帧
    public static final String CMDTYPE_MUC_CONTROL = "MUC_CONTROL";

    /**
     * 校验算法
     */
    //CRC16
    public static final String CHECK_CRC16 = "CRC16";
    //CRC8
    public static final String CHECK_CRC8 = "CRC8";
    //SUM
    public static final String CHECK_SUM = "SUM";

    //通讯故障
    public static final String COMMUNICATION_ERROR = "COMMUNICATION_ERROR";
}

