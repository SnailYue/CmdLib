package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author created by Snail
 * date:2019/5/23
 * email:yuesnail@gmail.com
 */
public class PCmdLength {
    @SerializedName("DownCmdLength")
    private int downLength;
    @SerializedName("UpCmdLength")
    private int upLength;

    public int getDownLength() {
        return downLength;
    }

    public void setDownLength(int downLength) {
        this.downLength = downLength;
    }

    public int getUpLength() {
        return upLength;
    }

    public void setUpLength(int upLength) {
        this.upLength = upLength;
    }
}
