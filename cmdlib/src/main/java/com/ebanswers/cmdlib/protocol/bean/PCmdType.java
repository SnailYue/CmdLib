package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Snail on 2018/10/22.
 */

public class PCmdType {
    @SerializedName("Code")
    private byte Code;
    @SerializedName("Type")
    private String Type;
    @SerializedName("IsAnalysis")
    private boolean IsAnalysic;

    public byte getCode() {
        return Code;
    }

    public void setCode(byte code) {
        this.Code = code;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public boolean isAnalysic() {
        return IsAnalysic;
    }

    public void setAnalysic(boolean analysic) {
        IsAnalysic = analysic;
    }
}
