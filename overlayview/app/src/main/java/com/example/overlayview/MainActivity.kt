package com.example.overlayview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings.canDrawOverlays
import android.os.Build
import android.annotation.TargetApi
import android.media.Image
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_in_service.*
import android.provider.Settings.canDrawOverlays
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION





class MainActivity : AppCompatActivity() {

    private val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bt_start = findViewById<View>(R.id.bt_start) as ImageButton
        bt_start.setOnClickListener(View.OnClickListener {
            checkPermission()
            //startService(new Intent(MainActivity.this, AlwaysOnTopService.class));
        })

  /*      val bt_stop = findViewById<View>(R.id.bt_stop) as ImageButton
        bt_stop.setOnClickListener(View.OnClickListener {
            stopService(
                Intent(
                    this@MainActivity,
                    MyService::class.java
                )
            )
        })*/
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            } else {
                startService(Intent(this@MainActivity, MyService::class.java))
            }
        } else {
            startService(Intent(this@MainActivity, MyService::class.java))
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리

            } else {
                startService(Intent(this@MainActivity, MyService::class.java))
            }
        }
    }

}
