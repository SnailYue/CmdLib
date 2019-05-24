package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Snail on 2018/10/22.
 */

public class PCmdHead {
    @SerializedName( "Send")
    private String sendCode;

    @SerializedName("Response")
    private String responseCode;
    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }


}
