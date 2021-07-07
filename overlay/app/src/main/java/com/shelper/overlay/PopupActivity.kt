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
import android.widget.Toast
import org.json.JSONException
import java.io.File

class PopupActivity : Activity() {
    var et: EditText? = null
    var uploadButton: Button? = null;
    var bt: Button? = null;
    var uri : String? = "";
    var imageView : ImageView? = null;
    val PICK_FROM_ALBUM = 1;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
        setContentView(R.layout.activity_popup)
        window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title)
        bt = findViewById(R.id.sendbt)
        et = findViewById(R.id.editText)
        imageView = findViewById(R.id.tth) as ImageView
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
            if(et!!.text.toString().length > 0) {
                val name = et?.getText().toString()
                val intent = Intent()
                intent.putExtra("name", name)
                intent.putExtra("uri",uri)
                setResult(123, intent)
                finish()
            }else {
                Toast.makeText(applicationContext,"제목을 입력하거나, 썸네일을 등록해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            PICK_FROM_ALBUM -> {
                var currentUri : Uri? = data?.data
                try {
                    uri = getRealPathFromURI(currentUri)
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentUri)
                    imageView?.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    uri = "nono"
                }


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