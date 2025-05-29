package com.example.cleanearth.java;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;
    private Camera.PictureCallback pictureCallback;

    private void setCameraDisplayOrientation() {
        int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }


    public CameraPreview(Context context) {
        super(context);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        // 🔄 화면 회전 보정
        setCameraDisplayOrientation();

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle changes if needed
    }

    public void setPictureCallback(Camera.PictureCallback callback) {
        this.pictureCallback = callback;
    }

    public void takePicture() {
        if (camera != null) {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    java.io.File pictureFileDir = new java.io.File(getContext().getFilesDir(), "images");

                    if (!pictureFileDir.exists()) {
                        pictureFileDir.mkdirs();
                    }

                    String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
                    java.io.File pictureFile = new java.io.File(pictureFileDir, filename);

                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(pictureFile)) {
                        fos.write(data);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }

                    if (pictureCallback != null) {
                        pictureCallback.onPictureTaken(data, camera);
                    }

                    camera.startPreview();
                }
            });
        }
    }
}
