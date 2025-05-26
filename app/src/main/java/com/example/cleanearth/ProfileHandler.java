package com.example.cleanearth;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ProfileHandler {
    private final DBHelper dbHelper;

    public ProfileHandler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean saveProfile(String email, String nickname, String gender, String birthdate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname", nickname);
        values.put("gender", gender);
        values.put("birthdate", birthdate);

        int result = db.update("users", values, "useremail=?", new String[]{email});
        return result > 0; // 업데이트 성공 여부 반환
    }
}
