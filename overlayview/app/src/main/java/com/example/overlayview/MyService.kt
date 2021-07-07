package com.example.overlayview

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView


 class MyService:Service() {

internal var wm:WindowManager? = null
internal var mView:View? = null

override fun onBind(intent:Intent):IBinder? {
return null
}

override fun onCreate() {
super.onCreate()
val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

val params = WindowManager.LayoutParams(
 /*ViewGroup.LayoutParams.MATCH_PARENT*/300,
ViewGroup.LayoutParams.WRAP_CONTENT,
WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
PixelFormat.TRANSLUCENT)

params.gravity = Gravity.LEFT or Gravity.TOP
mView = inflate!!.inflate(R.layout.view_in_service, null)
val textView = mView!!.findViewById(R.id.textView) as TextView
val bt = mView!!.findViewById(R.id.bt) as ImageButton
bt.setOnClickListener {
    bt.setImageResource(R.mipmap.ic_launcher_round)
    textView.text = "on click!!"
}
    wm!!.addView(mView, params)
}

override fun onDestroy() {
super.onDestroy()
if (wm != null)
{
if (mView != null)
{
wm!!.removeView(mView)
mView = null
}
wm = null
}
}
}