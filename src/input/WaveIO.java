package input;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import utility.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by workshop on 9/18/2015.
 */
public class WaveIO {
    /**
     * encoding method "pulse-code modulation"  predefined encoding
     */
    public final static AudioFormat.Encoding SAMPLE_ENCODING = AudioFormat.Encoding.PCM_SIGNED;
    /**
     * capture amound of sample per second, can be changed in the constructor
     * 44.1 kHz
     */
    public final static float SAMPLE_RATE = 44100.0F;
    /**
     * sample size 16 bits
     */
    public final static int SAMPLE_BITS = 16;
    /**
     * mono
     */
    public final static int SAMPLE_CHANNELS = 1;
    /**
     * frame rate = 44100
     */
    public final static float SAMPLE_FRAME_RATE = 44100.0F;
    /**
     * frame size = 2
     */
    public final static int SAMPLE_FRAME_SIZE = 2;
    /**
     * use little-endian<br>
     * 00000100 00000001<br>
     * first byte 00000001<br>
     * second byte 00000100
     */
    public final static boolean SAMPLE_BIG_ENDIAN = false;
    /**
     * audio format
     */
    public final static AudioFormat FORMAT = new AudioFormat(SAMPLE_ENCODING, SAMPLE_RATE,SAMPLE_BITS,SAMPLE_CHANNELS,SAMPLE_FRAME_SIZE,SAMPLE_FRAME_RATE,SAMPLE_BIG_ENDIAN);

    /**
     * write to wave file<br>
     * calls: none<br>
     * called by: train
     * @param sample the data in 16bits integer (short) array
     * @param path the save location
     */
    public void writeWave(byte sampleByte[], String path){
        //inputting data to a wav file

        try {
            ByteArrayInputStream sampleByteArrayInputStream = new ByteArrayInputStream(sampleByte);
            AudioInputStream sampleAudioInputStream = new AudioInputStream (sampleByteArrayInputStream, FORMAT, sampleByte.length / SAMPLE_FRAME_SIZE);
            if (AudioSystem.write(sampleAudioInputStream, AudioFileFormat.Type.WAVE, new File(path + ".wav")) == -1){
                System.out.println("Unable to write to file");
            }
/*
            else{
                System.out.println("Finish writing to file");
            }
*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public byte[] readMp3(String path){
    	File file = new File(path);
    	AudioInputStream in;
    	
    	int byteRead = 16000 * 2;
		byte waveByte[] = new byte[byteRead];
		int numByteRead;
		try {
			
			ByteArrayOutputStream readByteArrayOutputStream = new ByteArrayOutputStream();
			
			
			in = AudioSystem.getAudioInputStream(file);
			AudioInputStream din = null;
	    	AudioFormat baseFormat = in.getFormat();
	    	AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
	    	                                            baseFormat.getSampleRate(),
	    	                                            16,
	    	                                            baseFormat.getChannels(),
	    	                                            baseFormat.getChannels() * 2,
	    	                                            baseFormat.getSampleRate(),
	    	                                            false);
	    	din = AudioSystem.getAudioInputStream(decodedFormat, in);
	    	while ((numByteRead = din.read(waveByte, 0, waveByte.length)) != -1){
	    		readByteArrayOutputStream.write(waveByte,0,numByteRead);
	    	}
	    	byte tempWaveByte[] = readByteArrayOutputStream.toByteArray();
	    	return tempWaveByte;
	    	
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waveByte = new byte[1];
        return waveByte;
    	
    }
    /**
     * loading wave file<br>
     * calls: none<br>
     * called by: train
     * @param path of the input wave file
     * @return a short array of anysize containing the amplitudes in the wave file
     */
    public byte[] readWave(String path){


        /**
         * define a file object with the location given
         */
        File fileRead = new File(path);

        /**
         * initial buffer size
         */
        int byteRead = 16000 * 2;

        /**
         * array used to temporary store the read data
         */
        byte waveByte[] = new byte[byteRead];

        /**
         * array used to store the return data
         */
//        short waveShort[];

        /**
         * store the number of bytes read from the wave file
         */
        int numByteRead;

        try {
            /**
             * Byte array that used to store data read from the wave file
             */
            ByteArrayOutputStream readByteArrayOutputStream = new ByteArrayOutputStream();

            /**
             * open the wave file
             */
            AudioInputStream readAudioInputStream = AudioSystem.getAudioInputStream(fileRead);

            while ((numByteRead = readAudioInputStream.read(waveByte, 0, waveByte.length)) != -1){
                readByteArrayOutputStream.write(waveByte,0,numByteRead);
            }

            /**
             * temporary array to store data in readByteArrayOutputStream
             */
            byte tempWaveByte[] = readByteArrayOutputStream.toByteArray();
            return tempWaveByte;
            /*
            waveShort = new short[tempWaveByte.length / 2];
            

            //convert 2 bytes into a short
            for (int c = 0 ; c < waveShort.length ; c++){
                waveShort[c] = (short)((tempWaveByte[2 * c + 1] << 8) + (tempWaveByte[2 * c] >= 0 ? tempWaveByte[2 * c] : tempWaveByte[2 * c] + 256));
            }
            return waveShort;
            */
        }
        catch (Exception e){
            e.printStackTrace();
        }
        waveByte = new byte[1];
        return waveByte;
    }

	public byte[] read(String absolutePath) {
		File file = new File(absolutePath);
		if(file.exists()) {
			if(Utils.getExtension(file).equalsIgnoreCase(Utils.mp3) ) {
				return readMp3(absolutePath);
			} else {
				return readWave(absolutePath);
			} 
		}
		byte[] waveByte = new byte[1];
        return waveByte;
	}
}
