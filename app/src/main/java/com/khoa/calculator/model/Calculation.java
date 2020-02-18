package com.khoa.calculator.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "calculation")
public class Calculation {

    @PrimaryKey
    private long timeStamp;
    private ArrayList<String> nodeList;
    private String result;

    @Ignore
    public Calculation(ArrayList<String> nodeList, String result, long timeStamp) {
        this.nodeList = nodeList;
        this.result = result;
        this.timeStamp = timeStamp;
    }

    public Calculation() {
    }

    public ArrayList<String> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<String> nodeList) {
        this.nodeList = nodeList;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Ignore
    public String toCalculation(){
        StringBuilder cal = new StringBuilder();
        for(String str : nodeList) cal.append(str);
        return cal.toString();
    }
}
