package com.example.mindvibe;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {
    private static final String PREFS_NAME = "mindvibe_session";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_EMAIL = "email";

    private SessionManager() {
    }

    public static boolean isLoggedIn(Context context) {
        return getPreferences(context).getBoolean(KEY_LOGGED_IN, false);
    }

    public static void saveLogin(Context context, String email) {
        getPreferences(context)
                .edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public static String getEmail(Context context) {
        return getPreferences(context).getString(KEY_EMAIL, "");
    }

    public static void clearSession(Context context) {
        getPreferences(context).edit().clear().apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
