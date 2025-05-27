package com.example.cleanearth.java;

import android.content.Context;

public class LoginHandler {
    private final DBHelper dbHelper;

    public LoginHandler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean login(String email, String password) {
        String hashedPassword = HashUtil.sha256(password);
        return dbHelper.checkUser(email, hashedPassword);
    }
}
