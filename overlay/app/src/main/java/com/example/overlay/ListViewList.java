package com.example.overlay;

import android.graphics.drawable.Drawable;

public class ListViewList {
    private Drawable iconDrawable;
    private String name;
    private int userid;
    private int id;

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContents(int id, int userid, String name) {
        this.id = id;
        this.userid = userid;
        this.name = name;
    }
}
