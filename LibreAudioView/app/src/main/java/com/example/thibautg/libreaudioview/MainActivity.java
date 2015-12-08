package com.example.thibautg.libreaudioview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * * This file is part of LibreAudioView.

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
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Sample::Activity";
    private CameraView mCameraView = null;
    private CustomCamera mCustomCamera = null;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        //call openCv
        /**
         *
         * @param status
         */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    AudioOutput audioOutput = startSoundStreaming();
                    startCamera(audioOutput);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /**
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
        //Log.i(TAG, "onResume");
    }

    /**
     *
     * @return
     * start Sound Streaming in AudioOutput
     */
    public AudioOutput startSoundStreaming() {
        AudioOutput audioOutput = new AudioOutput();
        audioOutput.startSoundStreaming();
        return audioOutput;
    }

    /**
     *
     * @param audioOutput
     * start camera
     * setContentView
     */
    public void startCamera(AudioOutput audioOutput) {
        mCustomCamera = new CustomCamera(audioOutput);
        mCustomCamera.startCamera();
        mCameraView = new CameraView(this, mCustomCamera);//create a SurfaceView to show camera data
        setContentView(mCameraView);
    }

    /**
     *
     * @param savedInstanceState
     * open cv is loaded
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mOpenCVCallBack)) {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
    }

    /**
     *
     */
    @Override
    protected void onDestroy ()
    {
        super.onDestroy();
        mCustomCamera.stopCamera();
        mCameraView.mThreadRun = false;
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
