package com.ebanswers.cmdlib.callback;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public interface CmdTypeDataListener {
    String getType();
    void readData(String type, byte[] data);
}
