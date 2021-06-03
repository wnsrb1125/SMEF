package com.shelper.overlay

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters
import com.google.firebase.dynamiclinks.DynamicLink.GoogleAnalyticsParameters
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters

class InformationPopupActivity : Activity() {
    var resultt: String? = null
    var id = 0
    var userid = 0;
    var picture_address = ""
    var name = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infopop)
        val imageView = findViewById<ImageView>(R.id.img)
        val execute_button = findViewById<Button>(R.id.execute)
        val share_button = findViewById<Button>(R.id.share)
        var favorite_button = findViewById<Button>(R.id.addfavorite)
        var delete_button = findViewById<Button>(R.id.delete)
        val getIntent = intent

        id = getIntent.getIntExtra("id", 0)
        name = getIntent.getStringExtra("name")
        picture_address = getIntent.getStringExtra("picture")
        userid = getIntent.getIntExtra("userid",0)
//        if (userid == id) {
//            delete_button.visibility = View.VISIBLE
//        }
        if (picture_address != "nope") {
            var uri:Uri = Uri.parse(picture_address)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            imageView?.setImageBitmap(bitmap)
        }
        favorite_button.setOnClickListener {
            val asyncAddFavorites = AsyncAddFavorites()
            var exit = asyncAddFavorites.execute(id.toString(),userid.toString()).get()
            finish()

        }
        execute_button.setOnClickListener {
            val task = AsyncTodo()
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
        val result = arrayOf("")
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://overlay.page.link")
                .setLink(Uri.parse("https://blog.naver.com/wnsrb0147/segment?tt=$id"))
                .setSocialMetaTagParameters(SocialMetaTagParameters.Builder().setTitle(name).build())
                .setSocialMetaTagParameters(SocialMetaTagParameters.Builder().setImageUrl(Uri.parse("https://yt3.ggpht.com/ytc/AAUvwngYUAT-XhTyFXemCRfD0qsKOMy3V22ZbkSE8faR=s900-c-k-c0x00ffffff-no-rj")).build())
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
                                setPackage("com.kakao.talk")
                            }
                        val chooserIntent = Intent.createChooser(intent,"Share");
                        startActivity(chooserIntent)
                    }
                }
    }
}