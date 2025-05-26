package com.example.cleanearth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserProfileHandler {
    private final DBHelper dbHelper;

    public UserProfileHandler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public User getUserProfile(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nickname, gender, birthdate FROM users WHERE useremail=?", new String[]{email});
        if (cursor.moveToFirst()) {
            String nickname = cursor.getString(0);
            String gender = cursor.getString(1);
            String birthdate = cursor.getString(2);
            cursor.close();
            return new User(email, nickname, gender, birthdate);
        }
        cursor.close();
        return null;
    }
}