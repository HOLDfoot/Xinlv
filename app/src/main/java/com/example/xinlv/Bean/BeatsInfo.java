package com.example.xinlv.Bean;

/**
 * Created by user on 16-3-24.
 */
public class BeatsInfo {
    private int time;
    private  int  beats;

    public BeatsInfo(){}
    public BeatsInfo(int beats, int time) {
        this.beats = beats;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getBeats() {
        return beats;
    }

    public void setBeats(int beats) {
        this.beats = beats;
    }
}
