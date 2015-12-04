package com.example.thibautg.libreaudioview;

/**
 * Created by thibautg on 18/11/15.
 */
public class Globals {

    public static int outputFrameWidth = 160;
    public static int outputFrameHeight = 120;

    public static int lenghtMixing = 50;

    public static int sampleRate = 8000;
    public static int numSamples = 272; //(= 32ms at 8khz)
    public static int numValues = numSamples*2;//(*2 for stereo)
}
