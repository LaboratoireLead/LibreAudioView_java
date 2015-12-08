package com.example.thibautg.libreaudioview;

/**
 * Created by thibautg on 18/11/15.

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
public class Globals {

    public static int outputFrameWidth = 160;
    public static int outputFrameHeight = 120;

    public static int lenghtMixing = 50;

    public static int sampleRate = 8000;
    public static int numSamples = 272; //(= 32ms at 8khz)
    public static int numValues = numSamples*2;//(*2 for stereo)
}
