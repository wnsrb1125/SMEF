package com.shelper.overlay

import android.graphics.drawable.Drawable

class ProfileViewList {
    private var iconDrawable: Drawable? = null
    private var name: String? = null
    private var userid = 0
    private var id = 0
    private var views = 0
    private var timestamp: String? = null

    fun getViews(): Int {
        return views
    }

    fun setViews(views: Int) {
        this.views = views
    }

    fun getTimestamp(): String? {
        return timestamp
    }

    fun setTimestamp(timestamp: String?) {
        this.timestamp = timestamp
    }

    fun getIconDrawable(): Drawable? {
        return iconDrawable
    }

    fun setIconDrawable(iconDrawable: Drawable?) {
        this.iconDrawable = iconDrawable
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getUserid(): Int {
        return userid
    }

    fun setUserid(userid: Int) {
        this.userid = userid
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun setContents(id: Int, userid: Int, name: String?) {
        this.id = id
        this.userid = userid
        this.name = name
    }

}