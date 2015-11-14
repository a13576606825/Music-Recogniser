package input;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.sound.sampled.*;

import utility.Utils;

public class Recorder {

	private boolean running = false;;
	private ByteArrayOutputStream out = new ByteArrayOutputStream();


	private synchronized TargetDataLine getLine(AudioFormat audioFormat) {
		TargetDataLine res = null;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,
				audioFormat);
		try {
			res = (TargetDataLine) AudioSystem.getLine(info);
			res.open(audioFormat);
			return res;
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private synchronized void rawplay(final AudioFormat targetFormat) {

		// In another thread I start:
		Thread listeningThread = new Thread(new Runnable() {
			public void run() {

				
				
				TargetDataLine line = getLine(targetFormat);
				
				if (line == null) {
					return;
				}
				
				
				out = new ByteArrayOutputStream();
				int numBytesRead;
				byte[] data = new byte[line.getBufferSize()];

				// Begin audio capture.
				line.start();
				running = true;
				// Here, stopped is a global boolean set by another thread.
				while (running) {
					
					numBytesRead = line.read(data, 0, data.length);
					out.write(data, 0, numBytesRead);
				} 
				
			}
		});

		listeningThread.start();

	}

	public void start() {
		rawplay(WaveIO.FORMAT);
	}

	public void stop() {
		running = false;
		save(getSaveFile());
	}
	
	public File getSaveFile() {
		File fileOut = new File(Utils.tempFolder+"/recording");
		if(fileOut.exists()) {
			fileOut.delete();
			try {
				fileOut.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fileOut;
	}
	public void save(File wavFile) {
		byte tempWaveByte[] = out.toByteArray();
//		Utils.debug(tempWaveByte);
        new WaveIO().writeWave(tempWaveByte, wavFile.getAbsolutePath());
    }

}
