package com.ebanswers.cmdlib.timer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public class WorkTimer {
    private static ConcurrentHashMap<String,TimerTask> timerTaskMap = new ConcurrentHashMap<>();

    private static WorkTimer instance = null;

    private WorkTimer(){

    }

    public synchronized static void setTimer(String name, TimerTask.TimerTaskListener timerTaskListener){
        TimerTask timerTask = new TimerTask(name,1000,timerTaskListener);
        if(null == timerTaskMap.get(name)) {
            timerTaskMap.put(name, timerTask);
        }
    }

    public synchronized static void clearTimer(String name){
        if(null != timerTaskMap.get(name)) {
            timerTaskMap.remove(name);
        }
    }

    public synchronized static TimerTask getTimer(String dry_timer) {
        return timerTaskMap.get(dry_timer);
    }

    public static String showTime(int time) {
        String str_time = "00:00:00";
        if (0 != time) {
            int hour = time / 3600;
            int min = time % 3600 / 60;
            int sec = time % 3600 % 60;
            str_time = (hour > 10 ? hour + "" : "0" + hour) + ":" + (min > 10 ? min + "" : "0" + min) + ":" + (sec > 10 ? sec + "" : "0" + sec);
        }
       return str_time;
    }
}
