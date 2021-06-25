package com.shelper.overlay

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import java.util.concurrent.ExecutionException
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

class MainActivity : AppCompatActivity() {
    private val result: String? = null
    private var result2: String? = null
    private var search_word: String? = null
    private var id = 0
    private var name = ""
    private var uri = "nono"
    private var resultt = ""
    private var backKeyPressedTime: Long = 0
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this@MainActivity){}
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-5645529602526796/5541822630", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
        if (intent.hasExtra("userid")) {
            id = intent.getIntExtra("userid",0)
        }
        if (intent.hasExtra("myservice")) {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
            finish()
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
        val webView : WebView = findViewById<View>(R.id.webView) as WebView
        //webView.webViewClient = WebViewClient()
        //webView.webChromeClient = WebChromeClient()
        webView.loadUrl("https://shelper3.azurewebsites.net/ad.html")

        val webSetting : WebSettings = webView.settings
        webSetting.javaScriptEnabled = true

        val editText = findViewById<EditText>(R.id.editText2)
        val search = findViewById<View>(R.id.search_button) as Button
        search.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
            val asyncSearch = AsyncSearch(this@MainActivity)
            search_word = editText.text.toString()
            try {
                result2 = asyncSearch.execute(search_word).get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.putExtra("search_result", result2)
            intent.putExtra("userid",id)
            intent.putExtra("search",search_word)
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
       // handleDeepLink()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("createe","create")
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mycontents -> {

                val asyncMycontents = AsyncMycontents()
                val mycontent_result = asyncMycontents.execute(id.toString()).get()
                val intent = Intent(this@MainActivity, MyActivity::class.java)
                intent.putExtra("search_result", mycontent_result)
                intent.putExtra("userid",id)
                startActivity(intent)
                true
            }
            R.id.makecontents -> {
                val intent = Intent(this@MainActivity, PopupActivity::class.java)
                startActivityForResult(intent, 123)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

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
                    Log.d("uri1",uri)
                    startMakeServ()
                }
            }
        }
    }

    companion object {
        var firstActivity: Activity? = null
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1
    }
}

