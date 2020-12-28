package com.example.overlay;

import android.os.Parcel;

public class Education {

    private Integer[] soundPaint;

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
