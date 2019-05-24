package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Snail on 2018/10/22.
 */

public class PCmdType {
    @SerializedName("Code")
    private String Code;
    @SerializedName("Type")
    private String Type;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }


}
