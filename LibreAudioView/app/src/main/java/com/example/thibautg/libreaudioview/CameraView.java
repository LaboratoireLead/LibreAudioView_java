package com.example.thibautg.libreaudioview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.util.Log;

/**
 * Created by thibautg on 22/10/15.

 * This file is part of LibreAudioView.

 * LibreAudioView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * libreAudioView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with LibreAudioView. If not, see <http://www.gnu.org/licenses/>.

 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

     private int width = Globals.outputFrameWidth ;
     private int height = Globals.outputFrameHeight ;
     private SurfaceHolder mHolder;
     public boolean mThreadRun = true;
     private Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
     private CustomCamera mCustomCamera = null;

    private static final String TAG = "CameraView";

     /**
      *
      * @param context
      * @param customCamera
      */
    public CameraView(Context context, CustomCamera customCamera){
        super(context);
        mCustomCamera = customCamera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

     /////// Surface...

     /**
      *
      * @param _holder
      * @param format
      * @param width
      * @param height
      */
     public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
     }

     /**
      *
      * @param surfaceHolder
      */
     @Override
     public void surfaceCreated(SurfaceHolder surfaceHolder) {
         //Log.i(TAG, "surfaceCreated");
         startDisplay();
     }

     /**
      *
      * @param surfaceHolder
      */
     @Override
     public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
         //mCamera.stopPreview();
         //mCamera.release();
     }

     /**
      *
      */
     public void startDisplay() {
         (new Thread(this)).start();
     }

     /**
      * start processing thread
      */
     public void run() {
        mThreadRun = true;
        while (mThreadRun) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCustomCamera.convertFrameToBmp(bmp);
            Canvas canvas = mHolder.lockCanvas();

            if (canvas != null) {
                canvas.drawBitmap(bmp, (canvas.getWidth() - width) / 2, (canvas.getHeight() - height) / 2, null);
                mHolder.unlockCanvasAndPost(canvas);
            }
            else {
                Log.i(TAG, "canvas == null ");
            }
        }
    }
}
