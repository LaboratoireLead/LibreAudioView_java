# LibreAudioView_java
This is the first public version of the LibreAudioView software initiative. For more information, read:
http://journal.frontiersin.org/article/10.3389/fict.2015.00020/full

This work has been partly founded by the Burgundy Region (France) with the contrat n° 2014-9201AAO047S05164.

If the application closes at its first launch, it is probably due to your smartphone that is blocking access to the camera. To set this go to settings > applications > LibreAudioView > Permissions > allow camera.

The System LibreAudioView uses openCv librairies. There are two different ways to use openCv.
OpenCV can be statically added to the application or dynamically linked using Opencv Manager.
By default the system uses static libraries. You can change it simply in the MainActivity as explained in comments of the file.
