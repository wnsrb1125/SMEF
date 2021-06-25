package com.shelper.overlay

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.dynamiclinks.DynamicLink.*
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class InformationPopupActivity : Activity() {
    var resultt: String? = null
    var id = 0
    var userid = 0;
    var contents_userid = 0;
    var picture_address = ""
    var name = ""
    var image_path : String = "www"
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "infopop"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
        setContentView(R.layout.activity_infopop)
        val imageView = findViewById<ImageView>(R.id.img)
        val execute_button = findViewById<Button>(R.id.execute)
        val share_button = findViewById<Button>(R.id.share)
        var favorite_button = findViewById<Button>(R.id.addfavorite)
        var delete_button = findViewById<Button>(R.id.delete)
        var tv = findViewById<View>(R.id.popup_tv) as TextView
        val getIntent = intent

        image_path = getIntent.getStringExtra("image_path")
        id = getIntent.getIntExtra("id", 0)
        name = getIntent.getStringExtra("name")
        picture_address = getIntent.getStringExtra("picture")
        userid = getIntent.getIntExtra("userid",0)
        contents_userid = getIntent.getIntExtra("contents_userid",0)
        if (userid == contents_userid) {
            delete_button.visibility = View.VISIBLE
        }
        Log.d("asdasd1",image_path)
        tv.setText("제목: "+ name)
        if (picture_address != "nope") {
            var uri:Uri = Uri.parse(picture_address)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            imageView?.setImageBitmap(bitmap)
        }
        favorite_button.setOnClickListener {
            val asyncAddFavorites = AsyncAddFavorites()
            asyncAddFavorites.execute(id.toString(),userid.toString())
            finish()

        }
        delete_button.setOnClickListener{
            var dlg = AlertDialog.Builder(this@InformationPopupActivity)
            dlg.setMessage("삭제 하시겠습니까?")
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                var asyncDelete = AsyncDeleteContents()
                asyncDelete.execute(id.toString())
            })
            dlg.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->

            })
            dlg.show()
        }
        execute_button.setOnClickListener {
            MobileAds.initialize(this@InformationPopupActivity){}
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
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
            val task = AsyncTodo(this@InformationPopupActivity)
            try {
                resultt = task.execute("https://shelper3.azurewebsites.net/getjson.php?id=$id&userid=$userid", "sound").get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            startServ(id)
        }
        share_button.setOnClickListener { DoDynamicLink() }
    }

    private fun startServ(id: Int) {
        val intent = Intent(this@InformationPopupActivity, MyService::class.java)
        intent.putExtra("edu", resultt)
        intent.putExtra("contents_id", id)
        startService(intent)
    }


    fun DoDynamicLink() {
        if(image_path == "null") {
            image_path = "https://yt3.ggpht.com/ytc/AAUvwngYUAT-XhTyFXemCRfD0qsKOMy3V22ZbkSE8faR=s900-c-k-c0x00ffffff-no-rj"
        }
        val result = arrayOf("")
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://overlay.page.link")
                .setLink(Uri.parse("https://blog.naver.com/wnsrb0147/segment?tt=$id"))
                .setSocialMetaTagParameters(SocialMetaTagParameters.Builder().setTitle(name).build())
                .setSocialMetaTagParameters(SocialMetaTagParameters.Builder().setImageUrl(Uri.parse(image_path)).build())
                .setGoogleAnalyticsParameters(GoogleAnalyticsParameters.Builder().setContent("sdd").build())
                .setAndroidParameters(AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val shortLink = task.result!!.shortLink.toString()
//                        val defaultFeed = FeedTemplate(
//                                content = Content(
//                                        title = "딸기 치즈 케익",
//                                        description = "#케익 #딸기 #삼평동 #카페 #분위기 #소개팅",
//                                        imageUrl = "http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
//                                        link = Link(
//                                                webUrl = "https://developers.kakao.com",
//                                                mobileWebUrl = task.result!!.shortLink.toString()
//                                        )
//                                ),
//                                social = Social(
//                                        likeCount = 286,
//                                        commentCount = 45,
//                                        sharedCount = 845
//                                )
//                        )
//                        val url = "https://shelper.overlay.com/callback";
//
//                        LinkClient.instance.defaultTemplate(this, defaultFeed) { linkResult, error ->
//                            if (error != null) {
//                                Log.e("TAG", "카카오링크 보내기 실패", error)
//                            }
//                            else if (linkResult != null) {
//                                Log.d("TAG", "카카오링크 보내기 성공 ${linkResult.intent}")
//                                startActivity(linkResult.intent)
//
//                                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
//                                Log.w("TAG", "Warning Msg: ${linkResult.warningMsg}")
//                                Log.w("TAG", "Argument Msg: ${linkResult.argumentMsg}")
//                            }
//                        }
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT,shortLink.toString())
                                //setPackage("com.kakao.talk")
                            }
                        val chooserIntent = Intent.createChooser(intent,"Share");
                        startActivity(chooserIntent)
                    }
                }
    }
}