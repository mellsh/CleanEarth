package com.example.cleanearth;

import android.content.Context;

public class LoginHandler {
    private final DBHelper dbHelper;

    public LoginHandler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean login(String email, String password) {
        return dbHelper.checkUser(email, password);
    }
}