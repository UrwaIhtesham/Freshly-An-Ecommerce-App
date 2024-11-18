package com.example.l215404.freshlyanecommerceapp.Activities.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS_NAME = "LoginSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_CUSTOMER = "isCustomer";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createSession(int userId, String email, boolean isCustomer) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_CUSTOMER, isCustomer);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USER_ID);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public boolean isCustomer() {
        return sharedPreferences.getBoolean(KEY_IS_CUSTOMER, true);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
