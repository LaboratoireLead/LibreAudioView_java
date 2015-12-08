package com.example.thibautg.libreaudioview;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import java.io.IOException;

/**
 * Created by thibautg on 23/10/15.
 *
 * This file is part of LibreAudioView.

 * LibreAudioView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * LibreAudioView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with LibreAudioView. If not, see <http://www.gnu.org/licenses/>.
 */

public class CustomCamera {

    private static final String TAG = "CustomCamera";
    private VideoProcessing mVideoProcessing = null;
    private Camera mCamera;
    private byte[] mBuffer;
    private byte[] mFrame;
    private int width = Globals.outputFrameWidth;
    private int height = Globals.outputFrameHeight;
    SurfaceTexture texture = new SurfaceTexture(10);

    /**
     *
     * @param audioOutput
     */
    public CustomCamera(AudioOutput audioOutput) {
         mVideoProcessing = new VideoProcessing(audioOutput);
    }

    /**
     * call open camera
     * and if no error :
     * acquisition parameters
     * buffer and set callback
     * preview
     */
    public void startCamera() {
        openCamera();
        if (mCamera != null) {
            setupAcquisitionParameters();
            allocateBufferAndSetCallback();
            startCameraPreview();
        }
    }

    /**
     * open camera
     */
    public void openCamera() {
        mCamera = Camera.open();
        if (mCamera == null) {
            Log.e(TAG, "Can't open camera!");
        }
    }


    /**
     * acquisition parameters
     */
    public void setupAcquisitionParameters() {
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(width, height);
        mCamera.setParameters(params);
    }

    /**
     *allocate Buffer And Set Callback
     */
    public void allocateBufferAndSetCallback() {

        // Now allocate the buffer
        Camera.Parameters params = mCamera.getParameters();
        int tmpSize = params.getPreviewSize().width * params.getPreviewSize().height;
        tmpSize = (int) ((float) tmpSize * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8.);
        mBuffer = new byte[tmpSize];
        mFrame = new byte[tmpSize];
        mCamera.addCallbackBuffer(mBuffer);

        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (CustomCamera.this) {
                    mVideoProcessing.processFrame(data);
                }
                camera.addCallbackBuffer(mBuffer);
            }
        });
    }

    /**
     *
     * @param image
     * convert frame to bmp
     * call convert byte array to bitmap
     */
    public void convertFrameToBmp(Bitmap image) {
        synchronized (CustomCamera.this) {
            mVideoProcessing.convertByteArrayToBitmap(mFrame, width, height, image);
        }
    }

    /**
     *
     */
    public void stopCamera() {
        mCamera.stopPreview();
        mCamera.release();
    }

    /**
     *start Camera Preview
     */
    public void startCameraPreview() {
        // Now we can start a preview
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                mCamera.setPreviewTexture(texture);
            else
                mCamera.setPreviewDisplay(null);
        } catch (IOException e) {
            Log.e(TAG, "mCamera.setPreviewDisplay/setPreviewTexture fails: " + e);
        }
        mCamera.startPreview();
    }

}
