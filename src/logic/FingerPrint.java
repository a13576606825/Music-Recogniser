package logic;

import java.io.IOException;

import fft.Complex;

public class FingerPrint {
	private static final int FUZ_FACTOR = 2;
	
	public final static int[] RANGE = new int[] { 40, 80, 120, 180, 300 };
	public final static int START_FREQUENCY = RANGE[0];
	public final static int END_FREQUENCY = RANGE[RANGE.length-1];

	public static long[] fingerPrint(Complex[][] results) {  
		long[] hashValue = new long[results.length];
		
		double[][] highscores;
		double[][] recordPoints;
		long[][] points;
		
		// init highest score to 0 for each  
		highscores = new double[results.length][5];
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < 5; j++) {
				highscores[i][j] = 0;
			}
		}

		recordPoints = new double[results.length][END_FREQUENCY];
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < END_FREQUENCY; j++) {
				recordPoints[i][j] = 0;
			}
		}

		points = new long[results.length][5];
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < 5; j++) {
				points[i][j] = 0;
			}
		}
		
		
		for (int t = 0; t < results.length; t++) {
			for (int freq = START_FREQUENCY; freq < END_FREQUENCY; freq++) {
				// Get the magnitude:
				double mag = Math.log(results[t][freq].abs() + 1);

				// Find out which range we are in:
				int index = getIndex(freq);

				// Save the highest magnitude and corresponding frequency:
				if (mag > highscores[t][index]) {
					highscores[t][index] = mag;
					recordPoints[t][freq] = 1;
					points[t][index] = freq;
				}
			}

			long h = hash(points[t][0], points[t][1], points[t][2],points[t][3]);
			hashValue[t] = h;
		}
		return hashValue;
		
	}
	



	private static long hash(long p1, long p2, long p3, long p4) {
	    return (p4 - (p4 % FUZ_FACTOR)) * 100000000 + (p3 - (p3 % FUZ_FACTOR))
	            * 100000 + (p2 - (p2 % FUZ_FACTOR)) * 100
	            + (p1 - (p1 % FUZ_FACTOR));
	}


	// find out in which range is frequency
	private static int getIndex(int freq) {
	    int i = 0;
	    while (RANGE[i] < freq)
	        i++;
	    return i;
	}

}
