package com.ebanswers.cmdlib.callback;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public interface StatusCallback {
    String getType();
    void statueChanged(String type, int value);
}
