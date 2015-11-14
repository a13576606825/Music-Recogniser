package logic;

import fft.Complex;
import fft.FFT;
import input.WaveIO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utility.Utils;


public class Analyzer {
	public static final int CHUNK_SIZE = 1024*4; 
	private static WaveIO waveIO = new WaveIO();
	
	
	Map<Long, List<DataPoint>> dataHashMap;
	Map<Integer, Map<Integer, Integer>> matchMap; // Map<SongId, Map<Offset, Count>>
	public Analyzer() {
		readDataFromFile();
	}
	private void readDataFromFile() {
		dataHashMap = DatabaseManager.readDataHashMap();
		if(dataHashMap == null) {
			trainDatabase();
			writeDataToFile();
		}
		
	}
	public void writeDataToFile() {
		DatabaseManager.storeDataHashMap(dataHashMap);
		
	}
	public String analyze(File file) {
		// reset match hashMap
		matchMap = new HashMap<Integer, Map<Integer, Integer>>(); 
		
		long[] hashTags = FingerPrint.fingerPrint((performFFT(file)));
		
		for(int t=0; t<hashTags.length; t++) {
			
			long h = hashTags[t];
			List<DataPoint> listPoints = null;
			
			if ((listPoints = dataHashMap.get(h)) != null) {
				for (DataPoint dP : listPoints) {
					int offset = Math.abs(dP.getTime() - t);
					Map<Integer, Integer> tmpMap = null;
					if ((tmpMap = this.matchMap.get(dP.getSongId())) == null) {
						tmpMap = new HashMap<Integer, Integer>();
						tmpMap.put(offset, 1);
						matchMap.put(dP.getSongId(), tmpMap);
					} else {
						Integer count = tmpMap.get(offset);
						if (count == null) {
							tmpMap.put(offset, new Integer(1));
						} else {
							tmpMap.put(offset, new Integer(count + 1));
						}
					}
				}
			}	
		}
		
		
		List<DataPoint> listPoints;
		int bestCount = 0;
		int bestSong = -1;
		for (int id = 0; id < FileSystem.getSongList().size(); id++) {
			
			System.out.println("check match map for song id: " + id);
			Map<Integer, Integer> tmpMap = matchMap.get(id);
			if(tmpMap == null) {
				 tmpMap = new HashMap<Integer, Integer>();
			}
			int bestCountForSong = 0;

			for (Map.Entry<Integer, Integer> entry : tmpMap.entrySet()) {
				if (entry.getValue() > bestCountForSong) {
					bestCountForSong = entry.getValue();
				}
				System.out.println("Time offset = " + entry.getKey()
						+ ", Count = " + entry.getValue());
			}

			if (bestCountForSong > bestCount) {
				bestCount = bestCountForSong;
				bestSong = id;
			}
		}

		return("Best song: " + FileSystem.getSongById(bestSong));
		
	}

	private Complex[][] performFFT(File f) {
		Utils.debug("performFFT on "+ f.getName() + " start");
		
		Utils.debug("readWave on "+ f.getName() + " start");
		
		byte[] audio = waveIO.read(f.getAbsolutePath());
		Utils.debug("readWave on "+ f.getName() + " complete");
		
		final int totalSize = audio.length;
		Utils.debug("readWave on "+ f.getName() + " has size " + totalSize);
		
		int amountPossible = totalSize/CHUNK_SIZE;
		Utils.debug("readWave on "+ f.getName() + " has possible amount " + amountPossible);
		//When turning into frequency domain we'll need complex numbers:
		Complex[][] results = new Complex[amountPossible][];

		//For all the chunks:
		for(int times = 0;times < amountPossible; times++) {
			//Utils.debug( f.getName() + " FFT on chunk " + times);
		    Complex[] complex = new Complex[CHUNK_SIZE];
		    for(int i = 0;i < CHUNK_SIZE;i++) {
		        //Put the time domain data into a complex number with imaginary part as 0:
		        complex[i] = new Complex(audio[(times*CHUNK_SIZE)+i], 0);
		    }
		    //Perform FFT analysis on the chunk:
		    results[times] = FFT.fft(complex);
		}
		Utils.debug("performFFT on "+ f.getName() + " end");
		return results;
	}
	
	private void trainAudio(File file) {
		String fileName = file.getName();
		int songId = FileSystem.getSongId(fileName);
		
		long[] hashTags = FingerPrint.fingerPrint((performFFT(file)));
		Utils.debug("FingerPrint on "+ file.getName() + " end");
		
		for(int t=0; t<hashTags.length; t++) {
			// for each chunk of data
			long h = hashTags[t];
			List<DataPoint> listPoints = null;
			if ((listPoints = dataHashMap.get(h)) == null) {
				listPoints = new ArrayList<DataPoint>();
				DataPoint point = new DataPoint(songId, t);
				listPoints.add(point);
				dataHashMap.put(h, listPoints);
			} else {
				DataPoint point = new DataPoint(songId, t);
				listPoints.add(point); // add a new point 
			}
		}
	}
	
	public void trainDatabase() {
		Utils.debug("-----start train database music-------");
		dataHashMap = new HashMap<Long, List<DataPoint>>() ;
		
		for(File file: FileSystem.getSongList()) {
			
			Utils.debug("train " + file.getName() + " start");
			trainAudio(file); 	
			Utils.debug("train " + file.getName() + " completed");
		}
		Utils.debug("-----complete train database music-------");
			
	}
}
