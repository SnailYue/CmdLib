package com.ebanswers.cmdlib.callback;

/**
 * @author created by Snail
 * date:2019/5/24
 * email:yuesnail@gmail.com
 */
public interface CmdListener {
    void readCmdData(byte[] bytes,int len);
}
