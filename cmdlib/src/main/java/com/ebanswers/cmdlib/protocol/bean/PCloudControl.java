package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Snail on 2018/10/26.
 */

public class PCloudControl {
    @SerializedName("SocketUrl")
    public String socketUrl;
    @SerializedName("SocketPort")
    public int socketPort;
    @SerializedName("SocketKey")
    public String socketKey;

    public String getSocketKey() {
        return socketKey;
    }

    public void setSocketKey(String socketKey) {
        this.socketKey = socketKey;
    }

    public String getSocketUrl() {
        return socketUrl;
    }

    public void setSocketUrl(String socketUrl) {
        this.socketUrl = socketUrl;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }
}
