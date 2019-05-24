package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Snail on 2018/10/22.
 */

public class PCFrames {
    @SerializedName("Pid")
    private int pId;
    @SerializedName("Length")
    private int length;
    @SerializedName("Ptype")
    private String pType;
    @SerializedName("CmdHead")
    private PCmdHead cmdHead;
    @SerializedName("CmdType")
    private List<PCmdType> cmdTypes;
    @SerializedName("CheckSum")
    private PCheckSum checkSum;
    @SerializedName("CmdFooter")
    private PCmdFooter cmdFooter;
    @SerializedName("PCmdLength")
    private PCmdLength cmdLength;

    public int getPid() {
        return pId;
    }

    public void setPid(int pid) {
        this.pId = pid;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPtype() {
        return pType;
    }

    public void setPtype(String ptype) {
        this.pType = ptype;
    }

    public PCmdHead getCmdHead() {
        return cmdHead;
    }

    public void setCmdHead(PCmdHead cmdHead) {
        this.cmdHead = cmdHead;
    }

    public List<PCmdType> getCmdtypes() {
        return cmdTypes;
    }

    public void setCmdtypes(List<PCmdType> cmdtypes) {
        this.cmdTypes = cmdtypes;
    }

    public PCheckSum getChecksum() {
        return checkSum;
    }

    public void setChecksum(PCheckSum checksum) {
        this.checkSum = checksum;
    }

    public PCmdFooter getCmdFooter() {
        return cmdFooter;
    }

    public void setCmdFooter(PCmdFooter footer) {
        this.cmdFooter = footer;
    }

    public PCmdLength getCmdLength() {
        return cmdLength;
    }

    public void setCmdLength(PCmdLength cmdLength) {
        this.cmdLength = cmdLength;
    }
}
