package com.shelper.overlay

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
    private var resultt = ""
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val PERMISSIONS = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        )
        permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission1 == PackageManager.PERMISSION_DENIED
                || permission2 == PackageManager.PERMISSION_DENIED
                || permission3 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@FirstPage, PERMISSIONS, REQ_PERMISSION)
        }
        KakaoSdk.init(this, "a404b66a2cf6ce41ce86c61ee1b5b545")
        login(this);
    }
    fun login(context: Context) {
        UserApiClient.instance.isKakaoTalkLoginAvailable(context);
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e("TAG", "로그인 실패", error)
                onDestroy()
            }
            else if (token != null) {
                Log.i("TAG", "로그인 성공 ${token.accessToken}")
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("TAG", "사용자 정보 요청 실패", error)
                        onDestroy()
                    }
                    else if (user != null) {
                        Log.i("TAG", "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}" +
                                user)
                        val asynclogin : AsyncLoginUpload = AsyncLoginUpload();
                        id = asynclogin.execute(user.id.toString(),user.kakaoAccount?.profile?.nickname).get()!!
                        handleDeepLink()
                        var intent :Intent = Intent(this@FirstPage,MainActivity::class.java)
                        intent.putExtra("userid",id)
                        Log.d("userid",id.toString());
                        if (id > 0) {
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE -> if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리
            } else {
                startService(Intent(this@FirstPage, MyService::class.java))
            }
            REQ_PERMISSION -> if (permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "권한 동의 거부시 사용이 불가합니다.", Toast.LENGTH_SHORT).show()
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
                        resultt = task.execute("https://shelper3.azurewebsites.net/getjson.php?id=" + deepLink.getQueryParameter("tt")+"&userid="+id, "sound").get()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    val intent = Intent(this@FirstPage, MyService::class.java)
                    intent.putExtra("edu", resultt)
                    intent.putExtra("id", id)
                    intent.putExtra("contents_id", deepLink.getQueryParameter("tt"))
                    startService(intent)
                })
                .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }
    companion object {
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1

    }
}