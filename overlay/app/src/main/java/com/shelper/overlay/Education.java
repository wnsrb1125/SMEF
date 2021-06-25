package com.shelper.overlay;

import android.os.Parcel;

public class Education {

    private Integer[] soundPaint;
    private String url;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
