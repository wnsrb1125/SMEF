package com.shelper.overlay

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import java.io.File

class PopupActivity : Activity() {
    var et: EditText? = null
    var uploadButton: Button? = null;
    var bt: Button? = null;
    var uri : String? = null;
    var imageView : ImageView? = null;
    val PICK_FROM_CAMERA = 0;
    val PICK_FROM_ALBUM = 1;
    val CROP_FROM_CAMERA = 2;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
        setContentView(R.layout.activity_popup)
        bt = findViewById(R.id.sendbt)
        et = findViewById(R.id.editText)
        imageView = findViewById(R.id.tth)
        uploadButton = findViewById(R.id.uploadImage)

        uploadButton?.setOnClickListener{
            val intent: Intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            val chooserIntent: Intent = Intent.createChooser(intent,"Select Image")
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(chooserIntent,PICK_FROM_ALBUM)
        }

        bt?.setOnClickListener {
            val name = et?.getText().toString()
            val intent = Intent()
            intent.putExtra("name", name)
            intent.putExtra("uri",uri)
            setResult(123, intent)
            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            PICK_FROM_ALBUM -> {
                var currentUri : Uri? = data?.data
                uri = getRealPathFromURI(currentUri)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentUri)
                imageView?.setImageBitmap(bitmap)

            }
        }
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri!!, proj, null, null, null)
        cursor!!.moveToNext()
        val path: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
        val uri = Uri.fromFile(File(path))
        cursor.close()
        return path
    }


    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}