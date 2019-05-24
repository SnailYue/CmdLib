package com.ebanswers.cmdlib.callback;

import android.text.TextUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public class ViewUIManager {
    private CopyOnWriteArrayList<StatusCallback> statusCallbacks;
    private CopyOnWriteArrayList<ErrorCallback> errorCallbacks;
    private CopyOnWriteArrayList<ParamCallback> paramCallbacks;

    private CopyOnWriteArrayList<CmdTypeDataListener> cmdTypeDataListeners;

    private Map<String, LinkedList<StatusCallback>> statusListMap;
    private Map<String, LinkedList<ErrorCallback>> errorListMap;
    private Map<String, LinkedList<ParamCallback>> paramListMap;
    private Map<String, LinkedList<CmdTypeDataListener>> cmdTypeDataListenersMap;

    private static ViewUIManager instance;

    private ViewUIManager() {
        statusCallbacks = new CopyOnWriteArrayList<>();
        errorCallbacks = new CopyOnWriteArrayList<>();
        paramCallbacks = new CopyOnWriteArrayList<>();
        cmdTypeDataListeners = new CopyOnWriteArrayList<>();
    }

    public static class viewHolder {
        private static final ViewUIManager instance = new ViewUIManager();
    }

    public static ViewUIManager getInstance() {
        return viewHolder.instance;
    }

    public void bind(StatusCallback statusCallback) {
        statusCallbacks.add(statusCallback);
    }

    public void bind(ErrorCallback callback) {
        errorCallbacks.add(callback);
    }

    public void bind(ParamCallback callback) {
        paramCallbacks.add(callback);
    }

    public void bind(CmdTypeDataListener callback) {
        cmdTypeDataListeners.add(callback);
    }

    public void unbind(StatusCallback statusCallback) {
        statusCallbacks.remove(statusCallback);
    }

    public void unbind(ErrorCallback callback) {
        errorCallbacks.remove(callback);
    }

    public void unbind(ParamCallback callback) {
        paramCallbacks.remove(callback);
    }

    public void unbind(CmdTypeDataListener callback) {
        cmdTypeDataListeners.remove(callback);
    }

    public void bind(String device, StatusCallback statusCallback) {
        if (null == statusListMap) {
            statusListMap = new LinkedHashMap<>();
        }
        if (null == statusListMap.get(device)) {
            LinkedList<StatusCallback> statusCallbacks = new LinkedList<>();
            statusCallbacks.add(statusCallback);
            statusListMap.put(device, statusCallbacks);
        } else {
            statusListMap.get(device).add(statusCallback);
        }
    }

    public void bind(String device, ErrorCallback callback) {
        if (null == errorListMap) {
            errorListMap = new LinkedHashMap<>();
        }
        if (null == errorListMap.get(device)) {
            LinkedList<ErrorCallback> callbacks = new LinkedList<>();
            callbacks.add(callback);
            errorListMap.put(device, callbacks);
        } else {
            errorListMap.get(device).add(callback);
        }
    }

    public void bind(String device, CmdTypeDataListener callback) {
        if (null == cmdTypeDataListenersMap) {
            cmdTypeDataListenersMap = new LinkedHashMap<>();
        }
        if (null == cmdTypeDataListenersMap.get(device)) {
            LinkedList<CmdTypeDataListener> callbacks = new LinkedList<>();
            callbacks.add(callback);
            cmdTypeDataListenersMap.put(device, callbacks);
        } else {
            cmdTypeDataListenersMap.get(device).add(callback);
        }
    }

    public void bind(String device, ParamCallback callback) {
        if (null == paramListMap) {
            paramListMap = new LinkedHashMap<>();
        }
        if (null == paramListMap.get(device)) {
            LinkedList<ParamCallback> callbacks = new LinkedList<>();
            callbacks.add(callback);
            paramListMap.put(device, callbacks);
        } else {
            paramListMap.get(device).add(callback);
        }
    }

    public void unbind(String device, StatusCallback statusCallback) {
        if (null != statusListMap && null != statusListMap.get(device)) {
            statusListMap.get(device).remove(device);
        }
    }

    public void unbind(String device, ErrorCallback callback) {
        if (null != errorListMap && null != errorListMap.get(device)) {
            errorListMap.get(device).remove(device);
        }

    }

    public void unbind(String device, CmdTypeDataListener callback) {
        if (null != cmdTypeDataListenersMap && null != cmdTypeDataListenersMap.get(device)) {
            cmdTypeDataListenersMap.get(device).remove(device);
        }
    }

    public void unbind(String device, ParamCallback callback) {
        if (null != paramListMap && null != paramListMap.get(device)) {
            paramListMap.get(device).remove(device);
        }
    }


    public void notifyCmdData(String type, byte[] data) {
        for (CmdTypeDataListener callback : cmdTypeDataListeners) {
            if (null != callback && (callback.getType().equals(type) || TextUtils.isEmpty(callback.getType()))) {
                callback.readData(type, data);
            }
        }
    }

    public void notifyCmdData(String device, String type, byte[] data) {
        if (device.isEmpty()) {
            notifyCmdData(type, data);
            return;
        }
        if (null == cmdTypeDataListenersMap) {
            return;
        }

        LinkedList<CmdTypeDataListener> pcallbacks = cmdTypeDataListenersMap.get(device);
        if (null != pcallbacks) {
            for (CmdTypeDataListener callback : pcallbacks) {
                if (null != callback && null != callback.getType()
                        && (callback.getType().equals(type) || TextUtils.isEmpty(callback.getType()))) {
                    callback.readData(type, data);
                }
            }
        }

    }

    public void notifyParam(String device, String name, String val) {
        if (null == paramListMap) {
            return;
        }
        LinkedList<ParamCallback> pcallbacks = paramListMap.get(device);
        if (null != pcallbacks) {
            for (ParamCallback callback : pcallbacks) {
                if (null != callback && null != callback.getType()
                        && (callback.getType().equals(name) || TextUtils.isEmpty(callback.getType()))) {
                    callback.valueChanged(val);
                }
            }
        }

    }

    public void notifyStatus(String device, String name, int val) throws JSONException {
        if (device.isEmpty()) {
            for (StatusCallback callback : statusCallbacks) {
                if (null != callback && (callback.getType().equals(name) || TextUtils.isEmpty(callback.getType()))) {
                    callback.statueChanged(name, val);
                }
            }
        } else if (null != statusListMap) {
            LinkedList<StatusCallback> callbacks = statusListMap.get(device);
            if (null != callbacks && callbacks.size() > 0) {
                for (StatusCallback callback : callbacks) {
                    if (null != callback && (callback.getType().equals(name) || TextUtils.isEmpty(callback.getType()))) {
                        callback.statueChanged(name, val);
                    }
                }
            }
        }
    }

    public void notifyError(String device, String name, int val) throws JSONException {

        if (TextUtils.isEmpty(device)) {
            for (ErrorCallback callback : errorCallbacks) {
                if (null != callback) {
                    callback.findError(name, val);
                }
            }
        } else if (null != errorListMap) {
            LinkedList<ErrorCallback> ecallbacks = errorListMap.get(device);
            if (null != ecallbacks && ecallbacks.size() > 0) {
                for (ErrorCallback callback : ecallbacks) {
                    if (null != callback) {
                        callback.findError(name, val);
                    }
                }
            }
        }

    }

}
