 package com.shelper.overlay

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import java.util.concurrent.ExecutionException

class FirstPage : Activity() {
    private val REQ_PERMISSION = 1001
    private var permission1 = 0
    private var permission2 = 0
    private var permission3 = 0
    private var permission4 = 0
    private var resultt = ""
    private var id = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first2)
        permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
        if (permission1 == PackageManager.PERMISSION_DENIED
                || permission2 == PackageManager.PERMISSION_DENIED
                || permission3 == PackageManager.PERMISSION_DENIED
                || permission4 == PackageManager.PERMISSION_DENIED) {
            val intent = Intent(this@FirstPage,PermissionActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            var intent :Intent = Intent(this@FirstPage,MainActivity::class.java)
            intent.putExtra("userid", SaveSharedPreference.getId(this))
            Log.d("userid",SaveSharedPreference.getId(this).toString());
            startActivity(intent)
            finish()

//            if (SaveSharedPreference.getKakaoId(this@FirstPage).toString().length < 3) {
//                KakaoSdk.init(this, "a404b66a2cf6ce41ce86c61ee1b5b545")
//                login(this);
//                Log.d("로그인ㄴ",SaveSharedPreference.getKakaoId(this@FirstPage).toString())
//            } else {
//                // Call Next Activity
//                Log.d("로그인",SaveSharedPreference.getKakaoId(this).toString()+SaveSharedPreference.getId(this))
//                var login = AsyncLogin()
//                var tf_result = login.execute(SaveSharedPreference.getKakaoId(this),SaveSharedPreference.getId(this)).get()
//                if(tf_result == "true") {
//                    handleDeepLink()
//                } else {
//                    Toast.makeText(applicationContext,"잘못된 접근입니다.",Toast.LENGTH_LONG).show()
//                    finish()
//                }
//            }
        }
    }
    fun login(context: Context) {
        UserApiClient.instance.isKakaoTalkLoginAvailable(context);
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e("TAG", "로그인 실패", error)
                onDestroy()
            }
            else if (token != null) {
                var token0 = "${token.refreshToken}"
                var kakaoid = 0;
                Log.i("TAG", "로그인 성공 ${token.accessToken} / ${token.refreshToken}")
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("TAG", "사용자 정보 요청 실패", error)
                        onDestroy()
                    }
                    else if (user != null) {
                        kakaoid = Integer.parseInt("${user.id}")
                        Log.i("TAG", "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n연령대: ${user.kakaoAccount?.ageRange}" +
                                "\n성별: ${user.kakaoAccount?.gender}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}" +
                                user)
                        val asynclogin : AsyncLoginUpload = AsyncLoginUpload();
                        id = asynclogin.execute(user.id.toString(),user.kakaoAccount?.profile?.nickname
                                ,user.kakaoAccount?.ageRange.toString(),user.kakaoAccount?.gender.toString(),user.kakaoAccount?.email).get()!!
                        SaveSharedPreference.setTokenId(this@FirstPage,token0,id,kakaoid);
                        handleDeepLink()
                    }
                }
            }
        }
    }
    private fun handleDeepLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, OnSuccessListener { pendingDynamicLinkData ->
                    if (pendingDynamicLinkData == null) {
                        var intent :Intent = Intent(this@FirstPage,MainActivity::class.java)
                        intent.putExtra("userid", SaveSharedPreference.getId(this))
                        Log.d("userid",SaveSharedPreference.getId(this).toString());
                        if (SaveSharedPreference.getId(this) > 0) {
                            startActivity(intent)
                            finish()
                        }
                        Log.d("GAJA", "No have dynamic link"+id)
                        return@OnSuccessListener
                    }
                    val deepLink = pendingDynamicLinkData.link
                    Log.d("GAJA", "deepLink: " + deepLink!!.getQueryParameter("tt"))
                    val segment = deepLink.lastPathSegment
                    Log.d("segment", segment)
                    val task = AsyncTodo(this@FirstPage)
                    try {
                        resultt = task.execute("https://shelper3.azurewebsites.net/getjson.php?id=" + deepLink.getQueryParameter("tt")+"&userid="+id, "sound").get()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    val intent = Intent(this@FirstPage, MyService::class.java)
                    intent.putExtra("edu", resultt)
                    intent.putExtra("id", SaveSharedPreference.getId(this))
                    intent.putExtra("contents_id", deepLink.getQueryParameter("tt"))
                    startService(intent)
                    finish()
                })
                .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }
}