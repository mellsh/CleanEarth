package com.example.cleanearth.java;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class SimpleImageAnalyzer {
    // Listener interface for Kotlin to receive analysis results
    public interface AnalysisResultListener {
        void onAnalysisResult(String label, float confidence);
    }

    // ResultListener interface for analyzeImageFile
    public interface ResultListener {
        void onResult(String label, float confidence);
        void onError(String errorMessage);
    }

    private static AnalysisResultListener resultListener;

    // SimpleImageAnalyzer.java 내부에 추가
    public static void analyzeImageFile(String imagePath, ResultListener listener) {
        new Thread(() -> {
            try {
                System.out.println("파일에서 이미지 로드 중...");

                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    listener.onError("오류: 이미지 파일이 존재하지 않습니다");
                    return;
                }

                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap == null) {
                    listener.onError("오류: 이미지를 로드할 수 없습니다");
                    return;
                }

                System.out.println("이미지 크기: " + bitmap.getWidth() + "x" + bitmap.getHeight());

                // Flask 서버로 전송
                String result = sendBitmapToFlask(bitmap);

                // 결과 파싱
                JSONObject jsonResult = new JSONObject(result);
                String label = jsonResult.getString("label");
                double confidence = jsonResult.getDouble("confidence");

                System.out.println("감지된 객체: " + label);
                System.out.println("신뢰도: " + (confidence * 100) + "%");

                // 결과 전달
                listener.onResult(label, (float) confidence);

            } catch (Exception e) {
                e.printStackTrace();
                listener.onError("오류: " + e.getMessage());
            }
        }).start();
    }

    public static void setAnalysisResultListener(AnalysisResultListener listener) {
        resultListener = listener;
    }
    private static final String FLASK_URL = "http://172.30.1.35:5002/predict"; // 로컬 에뮬레이터용 주소

    // drawable 리소스 ID로 분석 (기존 방식)
    public static String analyzeDrawableImage(Context context, int drawableResourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResourceId);
        return analyzeBitmap(bitmap);
    }

    // Bitmap 직접 분석 (신규 방식)
    public static String analyzeDrawableImage(Context context, Bitmap bitmap) {
        return analyzeBitmap(bitmap);
    }

    // 공통 분석 로직
    private static String analyzeBitmap(Bitmap bitmap) {
        try {
            String result = sendBitmapToFlask(bitmap);
            JSONObject jsonResult = new JSONObject(result);
            String label = jsonResult.getString("label");
            double confidence = jsonResult.getDouble("confidence");

            System.out.println("감지된 객체: " + label);
            System.out.println("신뢰도: " + (confidence * 100) + "%");

            // Call the listener if it's set
            if (resultListener != null) {
                resultListener.onAnalysisResult(label, (float) confidence);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "오류: " + e.getMessage();
        }
    }

    // Flask 서버로 이미지 전송
    private static String sendBitmapToFlask(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        URL url = new URL(FLASK_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        String boundary = "----FormBoundary" + System.currentTimeMillis();
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream outputStream = connection.getOutputStream()) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n");
            writer.append("Content-Type: image/jpeg\r\n");
            writer.append("\r\n").flush();

            outputStream.write(imageBytes);
            outputStream.flush();

            writer.append("\r\n").flush();
            writer.append("--").append(boundary).append("--").append("\r\n").flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new IOException("Flask 서버 응답 오류: " + responseCode);
        }
    }
}