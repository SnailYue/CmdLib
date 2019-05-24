package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Snail on 2018/10/19.
 */

public class PTConfig {
    @SerializedName("SendHeart")
    private boolean sendHeart;
    @SerializedName("HeartFrequency")
    private int heartFrequency;
    @SerializedName("SupportAllSerial")
    private boolean supportAllSerial;
    @SerializedName("ResendInterval")
    private int resendInterval;
    @SerializedName("ResendTimes")
    private int resendTimes;
    @SerializedName("SupportSingleContorl")
    private boolean supportSingleContorl;
    @SerializedName("SendResponse")
    private boolean sendResponse;
    @SerializedName("IsReSend")
    private boolean IsReSend;
    @SerializedName("IsStandard")
    private boolean isStandard;
    @SerializedName("IsChangeStatusSelf")
    private boolean isChangeStatusSelf;
    @SerializedName("AnalyzeData")
    private boolean analyzeData;
    @SerializedName("SerialName")
    public String serialName = "/dev/ttyS1";
    @SerializedName("SerialBaudrate")
    public int serial_baudrate = 9600;
    @SerializedName("SerialCsize")
    public int serial_csize = 8;
    @SerializedName("SerialParity")
    public int serial_parity = -1;
    @SerializedName("SerialStopBits")
    public int serial_stopbits = 1;
    @SerializedName("SupportFrameType")
    public boolean supportFrameType;
    @SerializedName("IsSupportCloud")
    public boolean isSupportCloud;

    public boolean isSupportCloud() {
        return isSupportCloud;
    }

    public void setSupportCloud(boolean supportCloud) {
        this.isSupportCloud = supportCloud;
    }

    public boolean isSupportFrameType() {
        return supportFrameType;
    }

    public void setSupportFrameType(boolean supportFrameType) {
        this.supportFrameType = supportFrameType;
    }

    public boolean isSendHeart() {
        return sendHeart;
    }

    public void setSendHeart(boolean sendHeart) {
        this.sendHeart = sendHeart;
    }

    public int getHeartFrequency() {
        return heartFrequency;
    }

    public void setHeartFrequency(int heartFrequency) {
        this.heartFrequency = heartFrequency;
    }

    public boolean isSupportAllSerial() {
        return supportAllSerial;
    }

    public void setSupportAllSerial(boolean supportAllSerial) {
        this.supportAllSerial = supportAllSerial;
    }

    public int getResendInterval() {
        return resendInterval;
    }

    public void setResendInterval(int resendInterval) {
        this.resendInterval = resendInterval;
    }

    public int getResendTimes() {
        return resendTimes;
    }

    public void setResendTimes(int resendTimes) {
        this.resendTimes = resendTimes;
    }

    public boolean isSupportSingleContorl() {
        return supportSingleContorl;
    }

    public void setSupportSingleContorl(boolean supportSingleContorl) {
        this.supportSingleContorl = supportSingleContorl;
    }

    public boolean isSendResponse() {
        return sendResponse;
    }

    public void setSendResponse(boolean sendResponse) {
        this.sendResponse = sendResponse;
    }

    public boolean isReSend() {
        return IsReSend;
    }

    public void setReSend(boolean reSend) {
        this.IsReSend = reSend;
    }

    public boolean isStandard() {
        return isStandard;
    }

    public void setStandard(boolean standard) {
        this.isStandard = standard;
    }

    public boolean isChangeStatusSelf() {
        return isChangeStatusSelf;
    }

    public void setChangeStatusSelf(boolean changeStatusSelf) {
        this.isChangeStatusSelf = changeStatusSelf;
    }

    public boolean isAnalyzeData() {
        return analyzeData;
    }

    public void setAnalyzeData(boolean analyzeData) {
        this.analyzeData = analyzeData;
    }

    public String getSerialName() {
        return serialName;
    }

    public void setSerialName(String serialName) {
        this.serialName = serialName;
    }

    public int getSerial_baudrate() {
        return serial_baudrate;
    }

    public void setSerial_baudrate(int serial_baudrate) {
        this.serial_baudrate = serial_baudrate;
    }

    public int getSerial_csize() {
        return serial_csize;
    }

    public void setSerial_csize(int serial_csize) {
        this.serial_csize = serial_csize;
    }

    public int getSerial_parity() {
        return serial_parity;
    }

    public void setSerial_parity(int serial_parity) {
        this.serial_parity = serial_parity;
    }

    public int getSerial_stopbits() {
        return serial_stopbits;
    }

    public void setSerial_stopbits(int serial_stopbits) {
        this.serial_stopbits = serial_stopbits;
    }
}
