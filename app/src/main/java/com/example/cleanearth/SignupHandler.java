package com.example.cleanearth;

import android.content.Context;

public class SignupHandler {
    private final DBHelper dbHelper;

    public SignupHandler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean register(String email, String password) {
        // 이미 존재하는 이메일인지 확인
        if (userExists(email)) {
            return false;
        }
        String hashedPassword = HashUtil.sha256(password);
        return dbHelper.insertUser(email, hashedPassword);
    }

    private boolean userExists(String email) {
        // 존재 여부를 비밀번호 없이 체크
        return dbHelper.checkUser(email, ""); // 약간의 트릭이지만 기존 DBHelper 구조에 맞춰서 처리
    }
}