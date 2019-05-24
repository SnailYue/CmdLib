package com.ebanswers.cmdlib.protocol;

import com.ebanswers.cmdlib.protocol.bean.PCFrames;
import com.ebanswers.cmdlib.protocol.bean.PCloudControl;
import com.ebanswers.cmdlib.protocol.bean.PTConfig;
import com.ebanswers.cmdlib.protocol.bean.PTFunction;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Snail on 2018/10/19.
 */

public class Protocol {
    @SerializedName("Name")
    public String Name;
    @SerializedName("Model")
    public String Model;
    @SerializedName("Key")
    public String Key;
    @SerializedName("AllFunctions")
    public List<PTFunction> AllFunctions;
    @SerializedName("UpFunctions")
    public List<PTFunction> UpFunction;
    @SerializedName("DownFunctions")
    public List<PTFunction> DownFunction;
    @SerializedName("PCFrames")
    public List<PCFrames> PCFrames;
    @SerializedName("PTConfig")
    public PTConfig PTConfig;
    @SerializedName("PCloudControl")
    public PCloudControl PCloudControl;

    public PCloudControl getPCloudControl() {
        return PCloudControl;
    }

    public void setPCloudControl(PCloudControl PCloudControl) {
        this.PCloudControl = PCloudControl;
    }

    public Protocol(){

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        this.Model = model;
    }

    public List<PCFrames> getPCFrames() {
        return PCFrames;
    }

    public void setPCFrames(List<PCFrames> PCFrames) {
        this.PCFrames = PCFrames;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        this.Key = key;
    }

    public List<PTFunction> getAllFunctions() {
        return AllFunctions;
    }

    public void setAllFunctions(List<PTFunction> allFunctions) {
        this.AllFunctions = allFunctions;
    }

    public List<PTFunction> getUpFunction() {
        return UpFunction;
    }

    public void setUpFunction(List<PTFunction> upFunction) {
        this.UpFunction = upFunction;
    }

    public List<PTFunction> getDownFunction() {
        return DownFunction;
    }

    public void setDownFunction(List<PTFunction> downFunction) {
        this.DownFunction = downFunction;
    }

    public PTConfig getPTConfig() {
        return PTConfig;
    }

    public void setPTConfig(PTConfig PTConfig) {
        this.PTConfig = PTConfig;
    }
}
