package com.example.cleanearth.java;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(useremail TEXT PRIMARY KEY, password TEXT, nickname TEXT, gender TEXT, birthdate TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean insertUser(String useremail, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("useremail", useremail);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean checkUser(String useremail, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE useremail=? AND password=?", new String[]{useremail, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}

