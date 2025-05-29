package com.example.cleanearth.java;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

public class LocalImageAnalyzer {

    public static void analyzeLatestImage(Context context) {
        try {
            File imageDir = new File(context.getFilesDir(), "images");
            if (!imageDir.exists()) {
                Log.e("Analyzer", "디렉토리가 존재하지 않음: " + imageDir.getAbsolutePath());
                return;
            }

            File[] imageFiles = imageDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".png") ||
                            name.toLowerCase().endsWith(".jpg") ||
                            name.toLowerCase().endsWith(".jpeg")
            );

            if (imageFiles == null || imageFiles.length == 0) {
                Log.e("Analyzer", "이미지 파일이 없습니다.");
                return;
            }

            // 가장 최근 파일 찾기
            File latestFile = imageFiles[0];
            for (File file : imageFiles) {
                if (file.lastModified() > latestFile.lastModified()) {
                    latestFile = file;
                }
            }

            Log.d("Analyzer", "분석할 이미지 경로: " + latestFile.getAbsolutePath());

            Bitmap bitmap;
            try (FileInputStream fis = new FileInputStream(latestFile)) {
                bitmap = BitmapFactory.decodeStream(fis);
            }

            if (bitmap == null) {
                Log.e("Analyzer", "Bitmap 변환 실패");
                return;
            }

            String result = SimpleImageAnalyzer.analyzeDrawableImage(context, bitmap);
            Log.d("Analyzer", "분석 결과: " + result);

        } catch (Exception e) {
            Log.e("Analyzer", "이미지 분석 중 오류 발생: " + e.getMessage(), e);
        }
    }
}