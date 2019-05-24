package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Snail on 2018/10/22.
 */

public class PCheckSum {
    @SerializedName("UpStartPid")
    private int upStartPid;
    @SerializedName("UpEndPid")
    private int upEndPid;
    @SerializedName("DownStartPid")
    private int downStartPid;
    @SerializedName("DownEndPid")
    private int downEndPid;
    @SerializedName("Type")
    private String Type;

    public int getUpStartPid() {
        return upStartPid;
    }

    public void setUpStartPid(int upStartPid) {
        this.upStartPid = upStartPid;
    }

    public int getUpEndPid() {
        return upEndPid;
    }

    public void setUpEndPid(int upEndPid) {
        this.upEndPid = upEndPid;
    }

    public int getDownStartPid() {
        return downStartPid;
    }

    public void setDownStartPid(int downStartPid) {
        this.downStartPid = downStartPid;
    }

    public int getDownEndPid() {
        return downEndPid;
    }

    public void setDownEndPid(int downEndPid) {
        this.downEndPid = downEndPid;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }


}
