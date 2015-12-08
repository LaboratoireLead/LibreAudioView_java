package com.example.thibautg.libreaudioview;

import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

/**
 * Created by thibautg on 18/11/15.
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

public class VideoProcessing {

    private static final String TAG = "VideoProcessing";

    protected Mat mRgba = null;
    protected Mat mInputMat = null;
    protected Mat mOutputGrayMat = null;
    protected Mat mInputGray = null;
    private int width = Globals.outputFrameWidth;
    private int height = Globals.outputFrameHeight;
    private Mat mPreviousMat;
    private Mat mDiffMat2;
    private boolean mBoolFirstImage = true;
    private Size windowSize = new Size(3,3);
    Sonifier mSonifier = null;

    /**
     *
     * @param audioOutput
     */
    public VideoProcessing(AudioOutput audioOutput){
        mInputMat = new Mat(height + height / 2, width, CvType.CV_8UC1);
        mRgba = new Mat(height, width, CvType.CV_8U, new Scalar(4));
        mPreviousMat =  new Mat(height, width, CvType.CV_8UC1);
        mDiffMat2 =  new Mat(height, width, CvType.CV_8UC1);
        mInputGray =  new Mat(height, width, CvType.CV_8UC1);
        mPreviousMat =  new Mat(height, width, CvType.CV_8UC1);
        mOutputGrayMat = new Mat(height, width, CvType.CV_8UC1);
        mSonifier = new Sonifier(audioOutput);
    }

    /**
     *
     * @param data
     * @param width
     * @param height
     * @param bmp
     */
    public void convertByteArrayToBitmap(byte[] data, int width, int height, Bitmap bmp) {
        //Log.i(TAG, "convertByteArrayToBitmap");
        Mat usedMat = mOutputGrayMat;
        //Mat usedMat = mInputGray
        try {
            Imgproc.cvtColor(usedMat, mRgba, Imgproc.COLOR_GRAY2RGBA);
            Utils.matToBitmap(mRgba, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());

        }
    }

    /**
     *
     * @param data
     */
    public void processFrame(byte[] data) {
        mInputMat.put(0, 0, data);
        mInputGray = mInputMat.submat(0, mInputMat.height() * 2 / 3, 0, mInputMat.width());
        Imgproc.GaussianBlur(mInputGray, mInputGray, windowSize, 0.6, 0.6);
        Core.absdiff(mInputGray, mPreviousMat, mDiffMat2);
        mInputGray.copyTo(mPreviousMat);
        Imgproc.threshold(mDiffMat2, mOutputGrayMat, 80, 255, 0);

        if (mBoolFirstImage) {
            mOutputGrayMat.setTo(new Scalar(0));
            mBoolFirstImage = false;
        }

        mSonifier.sonifyFrame(mOutputGrayMat);
    }
}
