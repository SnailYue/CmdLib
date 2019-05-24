package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Snail on 2018/11/1.
 */

public class PTMutex {
    @SerializedName("func")
    public String func;
    @SerializedName("val")
    public int val;

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
