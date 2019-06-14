package com.ebanswers.cmdlib.utils;

import java.util.LinkedList;

/**
 * @author created by Snail
 * date:2019/6/14
 * email:yuesnail@gmail.com
 */
public class SUMUtil {

    /**
     * 累加方式校验方式
     *
     * @param datas
     * @param start_pid
     * @param end_pid
     * @return
     */
    public static byte getCheckSUM(LinkedList<Byte> datas, int start_pid, int end_pid) {
        byte data = 0;
        if (end_pid >= datas.size()) {
            return 0;
        }
        for (int i = start_pid; i <= end_pid; i++) {
            data += datas.get(i);
        }
        return data;
    }
}
