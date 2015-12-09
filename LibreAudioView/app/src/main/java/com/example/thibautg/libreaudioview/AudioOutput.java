package com.example.thibautg.libreaudioview;

import android.media.AudioManager;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by thibautg on 17/11/15.

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
public class AudioOutput {
    static final String TAG = "AudioOutput";

    public boolean mThreadRun = true;
    private short[] mSound;
    private short[] mSoundOutput;
    private boolean mImageBufferHasChanged = false;

    private Object lock = new Object();

    /**
     *
     */
    public AudioOutput() {
        mSound = new short[Globals.numValues+Globals.lenghtMixing*2];
        mSoundOutput = new short[Globals.numValues+Globals.lenghtMixing*2];
    }

    /**
     * start thread
     */
    void startSoundStreaming() {
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                playSound();
            }
        });
        thread.start();
    }

    /**
     *
     * @param newSound
     */
    void pushSound(short[] newSound) {
        synchronized (lock) {
            //Log.v(TAG, "pushSound ");
            System.arraycopy( newSound, 0, mSound, 0, newSound.length );
            mImageBufferHasChanged = true;
        }
    }

    /**
     * start play sound
     */
    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                Globals.sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, 4096,//numBytes,
                AudioTrack.MODE_STREAM);
        audioTrack.play();
        int nbWritten = 0;
        while (mThreadRun) {
            synchronized (lock) {
                if (mImageBufferHasChanged) {
                    System.arraycopy(mSound, 0, mSoundOutput, 0, mSound.length);
                    mImageBufferHasChanged = false;
                }
            }
            nbWritten = audioTrack.write(mSoundOutput, 0, mSoundOutput.length);
        }
        audioTrack.stop();
    }
}
