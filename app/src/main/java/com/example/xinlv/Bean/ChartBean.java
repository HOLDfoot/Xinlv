package com.example.xinlv.Bean;

/**
 * Created by foot on 2016/4/3.
 */
public class ChartBean {

    private String time;
    private int beats;

    public ChartBean(String time, int beats) {
        this.time = time;
        this.beats = beats;
    }

    public int getBeats() {
        return beats;
    }

    public void setBeats(int beats) {
        this.beats = beats;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
