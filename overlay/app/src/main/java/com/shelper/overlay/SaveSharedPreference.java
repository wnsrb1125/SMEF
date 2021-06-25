package com.shelper.overlay;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String token = "token";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setTokenId(Context ctx, String token, int id, int kakaoid) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(token, token);
        editor.putInt("id",id);
        editor.putInt("kakaoid",kakaoid);
        editor.commit();
    }

    public static int getKakaoId(Context ctx) {
        return getSharedPreferences(ctx).getInt("kakaoid", 0);
    }

    public static int getId(Context ctx) {
        return getSharedPreferences(ctx).getInt("id", 0);
    }

    // 저장된 정보 가져오기
    public static String getToken(Context ctx) {
        return getSharedPreferences(ctx).getString(token, "");
    }

    // 로그아웃
    public static void clearToken(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
