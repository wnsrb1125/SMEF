package com.shelper.overlay;

import android.os.Parcel;

public class Education {

    private Integer[] soundPaint;
    private Integer seconds;

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    protected Education(Parcel in) {
    }

    public Education() {
    }

    public Integer[] getSoundPaint() {
        return soundPaint;
    }

    public void setSoundPaint(Integer[] ref) {
        this.soundPaint = ref;
    }

}
