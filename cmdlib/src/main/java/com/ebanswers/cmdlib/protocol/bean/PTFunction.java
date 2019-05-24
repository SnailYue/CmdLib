package com.ebanswers.cmdlib.protocol.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Snail on 2018/10/19.
 */

public class PTFunction {
    @SerializedName("Id")
    public int Id;
    @SerializedName("No")
    public int No;
    @SerializedName("Name")
    public String Name;
    @SerializedName("Title")
    public String Title;
    @SerializedName("Length")
    public int Length;
    @SerializedName("Cur_Value")
    public int Cur_Value;
    @SerializedName("Last_Value")
    public int Last_Value;
    @SerializedName("Cloud_Value")
    public int Value;
    @SerializedName("Values")
    public int[] Values;
    @SerializedName("Value_des")
    public List<PValueDes> Value_Des;
    @SerializedName("Value_Change")
    public boolean ValueChange;
    @SerializedName("No_Change_Notify")
    public boolean NoChangeNotify;

    public boolean isNoChangeNotify() {
        return NoChangeNotify;
    }

    public void setNoChangeNotify(boolean noChangeNotify) {
        NoChangeNotify = noChangeNotify;
    }

    public boolean isValueChange() {
        return ValueChange;
    }

    public void setValueChange(boolean valueChange) {
        ValueChange = valueChange;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        this.No = no;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        this.Length = length;
    }

    public int getCur_Value() {
        return Cur_Value;
    }

    public void setCur_Value(int cur_Value) {
        this.Cur_Value = cur_Value;
    }

    public int getLast_Value() {
        return Last_Value;
    }

    public void setLast_Value(int last_Value) {
        this.Last_Value = last_Value;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        this.Value = value;
    }

    public int[] getValues() {
        return Values;
    }

    public void setValues(int[] values) {
        this.Values = values;
    }

    public List<PValueDes> getValue_Des() {
        return Value_Des;
    }

    public void setValue_des(List<PValueDes> value_des) {
        this.Value_Des = value_des;
    }


}
