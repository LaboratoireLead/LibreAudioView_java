package com.example.thibautg.libreaudioview;


/**
 * Created by thibautg on 17/11/15.

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
import org.opencv.core.Mat;
import java.util.Arrays;

public class Sonifier {
    static final String TAG = "Sonifier";

    private int width = Globals.outputFrameWidth ;
    private int height = Globals.outputFrameHeight ;
    protected int listIDHotPix[] = null;
    protected int nbHotPix = 0;
    protected byte mBuff[] = null;
    protected short generatedShortSnd[] = null;
    protected float generatedFloatSnd[] = null;
    protected float mAmplitude = 10;
    protected float foatPI = (float)Math.PI;
    protected float previousGeneratedFloatSnd[] = null;
    protected float listSound[][] = null;
    private AudioOutput mAudioOutput = null;
    /**
     *
     * @param audioOutput
     */
    public Sonifier(AudioOutput audioOutput) {
        mAudioOutput = audioOutput;
        int nbValues = Globals.numValues+Globals.lenghtMixing*2;
        generatedFloatSnd = new float[nbValues];
        generatedShortSnd = new short[nbValues];
        previousGeneratedFloatSnd = new float[nbValues];
        listIDHotPix = new int[width*height];
        mBuff = new byte[width*height];
        listSound = new float[Globals.outputFrameWidth*Globals.outputFrameHeight][nbValues];
        init();
    }

    /**
     *
     * @param idRow
     * @param idCol
     * @return
     */
    public float getFreq(int idRow, int idCol) {
        idRow = 119-idRow;

        float nbPixLine = (float)width;
        float nbPixCol = (float)height;

        float zmin = (float)2.50280542986;
        float zmax = (float)14.4980269058;

        float xVal_tmp = (idRow*nbPixLine+idCol)/(float) (nbPixCol*nbPixLine);
        float xVal = (xVal_tmp+idRow/(float)nbPixCol)/(float)2.;
        float valueBark = zmin+(zmax-zmin)*xVal;
        float valueHzFromBark = (float)1960.*(valueBark+(float)0.53)/((float)26.28-valueBark);
        return valueHzFromBark;
    }

    /**
     * list sound is the database of sounds
     */
    public void init() {
        int nbCols = Globals.outputFrameWidth;
        int nbLines = Globals.outputFrameHeight;
        int nbSample = Globals.numSamples+Globals.lenghtMixing;
        float nbTone = nbLines*nbCols;

        for (int idLine = 0; idLine < nbLines; ++idLine) {
            float phase = 2 * foatPI*(float)Math.random();

            for (int idColumn = 0; idColumn < nbCols; ++idColumn) {
                float XVal = (float) idColumn/(float) nbCols;
                float balance = (float)1/((float)1.+(float)Math.exp((float)-2.6*(XVal-(float)0.5)));
                int IDPix = idLine*nbCols + idColumn;
                float frequency = getFreq(idLine, idColumn);
                float posXFromCenter = XVal-(float)0.5;
                float delay = (float)0.0875/(float)343.*((float)Math.sin(posXFromCenter*foatPI)+posXFromCenter*foatPI);

                for (int idSample = 0; idSample < nbSample; ++idSample) {
                    float time = (float)idSample / ((float)Globals.sampleRate);
                    listSound[IDPix][idSample*2] = ((float)1.-balance)*(float) Math.sin( phase + 2 * foatPI * frequency * (time-delay/2));//gauche
                    listSound[IDPix][idSample*2+1] = balance*(float) Math.sin( phase + 2 * foatPI * frequency * (time+delay/2));//droite
                }
            }
        }

    }

    /**
     *
     * @param processedFrame
     */
    public void sonifyFrame(Mat processedFrame) {

        Arrays.fill(generatedFloatSnd, 0);
        processedFrame.get(0, 0, mBuff);

        int nbRows = Globals.outputFrameHeight;
        int nbCols  = Globals.outputFrameWidth;
        int IDPix = 0;
        int cptSonifiedPix = 0;
        nbHotPix = 0;

        //Record IDs of active pixels
        for (int idLine = 0; idLine < nbRows; ++idLine) {
            for (int idColumn = 0; idColumn < nbCols; ++idColumn) {
                IDPix = idLine*nbCols + idColumn;
                if (mBuff[IDPix]!=0) {
                    listIDHotPix[nbHotPix] = IDPix;
                    nbHotPix += 1;
                }
                IDPix += 1;
            }
        }
        //down: to leave if compression

        int totalNbSimplification = 10000000;
        int maxNbUsedSimplification = 10000000;

        float factorCompression = 500;
        float nbKeptHotPixel = factorCompression*(1-factorCompression/((float)nbHotPix+factorCompression));
        float ratio = (float) nbKeptHotPixel/ (float)nbHotPix;

        totalNbSimplification = 100;
        maxNbUsedSimplification = (int) (ratio*100);

        int cptSimplification = 0;
        for (int idHotPix = 0; idHotPix<nbHotPix; ++idHotPix) {
            IDPix = listIDHotPix[idHotPix];

            if (cptSimplification == totalNbSimplification - 1) {
                cptSimplification = 0;
            }

            if ((cptSimplification < maxNbUsedSimplification)) {
                for (int idValue = 0; idValue < generatedFloatSnd.length; ++idValue) {
                    generatedFloatSnd[idValue] += listSound[IDPix][idValue];
                }
            }
            cptSimplification += 1;
        }
        //up: to leave if compression

        //dow: to leave if no compression
        /*for (int idHotPix = 0; idHotPix<nbHotPix; ++idHotPix) {
            IDPix = listIDHotPix[idHotPix];
            for (int idValue=0; idValue<generatedFloatSnd.length; ++idValue) {
                generatedFloatSnd[idValue] += listSound[IDPix][idValue];
            }
        }*/
        //up: to leave if no compression

        loops:
        for (int idLine = 0; idLine < nbRows; ++idLine) {
            for (int idColumn = 0; idColumn < nbCols; ++idColumn) {
                IDPix = idLine * nbCols + idColumn;
                if (mBuff[IDPix] != 0) {
                    for (int idValue = 0; idValue < generatedFloatSnd.length; ++idValue) {
                        generatedFloatSnd[idValue] += listSound[IDPix][idValue];
                    }
                    cptSonifiedPix += 1;
                    if (cptSonifiedPix == 100) {
                        break loops;
                    }
                }
            }
        }
        for (int idValue=0; idValue<generatedShortSnd.length; ++idValue) {
            generatedShortSnd[idValue] = (short) (mAmplitude*generatedFloatSnd[idValue]);
        }
        mAudioOutput.pushSound(generatedShortSnd);
    }
}