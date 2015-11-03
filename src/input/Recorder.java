package input;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

import javax.sound.sampled.TargetDataLine;

import utility.Utils;

public class Recorder {

	private boolean running = false;;
	private ByteArrayOutputStream out = new ByteArrayOutputStream();

	private static AudioFormat getFormat() {
		float sampleRate = 44100;
		int sampleSizeInBits = 8;
		int channels = 1; // mono
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

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

	private synchronized void rawplay(AudioFormat targetFormat) {
		// In another thread I start:
		Thread listeningThread = new Thread(new Runnable() {
			public void run() {

				TargetDataLine line = getLine(targetFormat);
				if (line == null) {
					return;
				}

				out = new ByteArrayOutputStream();
				int numBytesRead;
				byte[] data = new byte[line.getBufferSize() / 5];

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
		rawplay(getFormat());
	}

	public OutputStream stop() {
		running = false;
		return out;
	}

}
