package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Snail on 2018/10/19.
 */

public class PValueDes {
    @SerializedName("data")
    public int data;
    @SerializedName("desc")
    public String des;
    @SerializedName("mutex")
    public List<PTMutex> mutex;

    public int getVal() {
        return data;
    }

    public void setVal(int data) {
        this.data = data;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public List<PTMutex> getMutex() {
        return mutex;
    }

    public void setMutex(List<PTMutex> mutex) {
        this.mutex = mutex;
    }
}
