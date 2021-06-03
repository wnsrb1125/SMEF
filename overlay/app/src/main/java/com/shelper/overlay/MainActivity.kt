package com.shelper.overlay

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import java.util.concurrent.ExecutionException

class MainActivity : AppCompatActivity() {
    private val result: String? = null
    private var result2: String? = null
    private var search_word: String? = null
    private var id = 0
    private var name = ""
    private var uri = ""
    private var resultt = ""
    private var backKeyPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra("userid")) {
            id = intent.getIntExtra("userid",0)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 체크
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")).putExtra("edu", result)
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
        setContentView(R.layout.activity_main)
        firstActivity = this@MainActivity
        val bt_make = findViewById<View>(R.id.make) as Button
        bt_make.setOnClickListener {
            val intent = Intent(this@MainActivity, PopupActivity::class.java)
            startActivityForResult(intent, 123)
        }
        val editText = findViewById<EditText>(R.id.editText2)
        val search = findViewById<View>(R.id.search_button) as Button
        search.setOnClickListener {
            val asyncSearch = AsyncSearch()
            search_word = editText.text.toString()
            try {
                result2 = asyncSearch.execute(search_word).get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.putExtra("search_result", result2)
            intent.putExtra("userid",id)
            startActivity(intent)
        }
        val favorites = findViewById<View>(R.id.favorite)
        favorites.setOnClickListener{
            val asyncFavorites = AsyncFavorites()
            val favorites_result = asyncFavorites.execute(id.toString()).get()
            val intent = Intent(this@MainActivity, FavoritesActivity::class.java)
            intent.putExtra("search_result", favorites_result)
            intent.putExtra("userid",id)
            startActivity(intent)
        }
        val recent_button = findViewById<View>(R.id.recent)
        recent_button.setOnClickListener {
            val asyncRecents = AsyncRecents()
            val recents_result = asyncRecents.execute(id.toString()).get()
            val intent = Intent(this@MainActivity, RecentActivity::class.java)
            intent.putExtra("search_result", recents_result)
            intent.putExtra("userid",id)
            startActivity(intent)
        }
       // handleDeepLink()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            finish()
        }

    }

    private fun startMakeServ() {
        val intent = Intent(this@MainActivity, MakeService::class.java)
        intent.putExtra("name", name)
        intent.putExtra("uriuri",uri)
        intent.putExtra("userid",id)
        startService(intent)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE -> if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리
            } else {
                startService(Intent(this@MainActivity, MyService::class.java))
            }
            123 -> {
                if (data != null) {
                    name = data!!.getStringExtra("name")
                    uri = data!!.getStringExtra("uri")
                    startMakeServ()
                }
            }
        }
    }

    private fun handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, OnSuccessListener { pendingDynamicLinkData ->
                    if (pendingDynamicLinkData == null) {
                        Log.d("GAJA", "No have dynamic link")
                        return@OnSuccessListener
                    }
                    val deepLink = pendingDynamicLinkData.link
                    Log.d("GAJA", "deepLink: " + deepLink!!.getQueryParameter("tt"))
                    val segment = deepLink.lastPathSegment
                    Log.d("segment", segment)
                    val task = AsyncTodo()
                    try {
                        resultt = task.execute("https://shelper3.azurewebsites.net/getjson.php?id=" + deepLink.getQueryParameter("tt"), "sound"+"&userid=" + id).get()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    val intent = Intent(this@MainActivity, MyService::class.java)
                    intent.putExtra("edu", resultt)
                    intent.putExtra("id", id)
                    startService(intent)
                })
                .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }

    companion object {
        var firstActivity: Activity? = null
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1
    }
}

