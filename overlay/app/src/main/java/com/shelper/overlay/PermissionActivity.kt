package com.shelper.overlay

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class PermissionActivity : Activity() {

    private val REQ_PERMISSION = 1001
    private var permission1 = 0
    private var permission2 = 0
    private var permission3 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val PERMISSIONS = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        )
        val permission_button = findViewById<View>(R.id.permission_button)
        permission_button.setOnClickListener {
            ActivityCompat.requestPermissions(this@PermissionActivity, PERMISSIONS, REQ_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionActivity.ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE -> if (!Settings.canDrawOverlays(this)) {
                val intent = Intent()
                setResult(456, intent)
                finish()
            }
            REQ_PERMISSION -> if (grantResults[0] == 0 && grantResults[1] == 0
               && grantResults[2] == 0) {
                val intent = Intent(applicationContext, FirstPage::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                Log.d("빡치네", Arrays.toString(grantResults))
                finish()
            }
        }
    }
    companion object {
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1

    }
}