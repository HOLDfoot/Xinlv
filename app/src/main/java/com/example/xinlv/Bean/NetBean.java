package com.example.xinlv.Bean;

/**
 * Created by foot on 2016/3/27.
 */
public class NetBean {
    private String mac;
    private String beats;
    private String time;
    private String num;

    public NetBean(String mac, String beats, String time, String num) {
        this.mac = mac;
        this.beats = beats;
        this.time = time;
        this.num = num;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getBeats() {
        return beats;
    }

    public void setBeats(String beats) {
        this.beats = beats;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
