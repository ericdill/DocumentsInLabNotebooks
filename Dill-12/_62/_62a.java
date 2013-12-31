/*******************************************************************************
 * Copyright (c) 2013 Eric Dill -- eddill@ncsu.edu. North Carolina State University. All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Eric Dill -- eddill@ncsu.edu - Developer
 ******************************************************************************/
package _62;

import io.MyFileInputStream;
import io.ReadFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFileChooser;

public class _62a {

	private Vector<Spot> spots = new Vector<Spot>();
	
	private double[][] sliceVsTime;
	
	private double max, min, k, t0, n;
	
	private double x0, y0, timePerFrame, d, pixelSize, wavelength;
	
	private double tNucBulk, kBulk, VBulk;
	
	private double[] alpha;
	
	private int[] frame;
	
	private File[] inputFiles;
	
	private String delimiter = "\t";
	
	private String 
			xCenter = "x0",
			yCenter = "y0",
			sampleToDetector = "sample to detector",
			theWavelength = "wavelength",
			thePixelSize = "pixel size",
			bulkRateConstant = "kb",
			bulkVolume = "Vb",
			dimensionality = "n",
			bulkT0 = "t0",
			keyStop = "key stop";
	
	public _62a(File[] inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	public void readFile(File f) {
		MyFileInputStream mfis = new MyFileInputStream(f);
		
		Scanner s = mfis.getScanner();
		
		String[] line = s.nextLine().split(delimiter);
		
		while(line.length == 2) {
			if(line[0].compareTo(xCenter) == 0)
				x0 = Double.valueOf(line[1]);
			
			else if(line[0].compareTo(yCenter) == 0)
				y0 = Double.valueOf(line[1]);
			
			else if(line[0].compareTo(sampleToDetector) == 0)
				d = Double.valueOf(line[1]);

			else if(line[0].compareTo(theWavelength) == 0)
				wavelength = Double.valueOf(line[1]);
			
			else if(line[0].compareTo(thePixelSize) == 0)
				pixelSize = Double.valueOf(line[1]);
			
			else if(line[0].compareTo(bulkRateConstant) == 0)
				kBulk = Double.valueOf(line[1]);
			
			else if(line[0].compareTo(bulkVolume) == 0)
				VBulk = Double.valueOf(line[1]);
			
			else if(line[0].compareTo(dimensionality) == 0)
				n = Double.valueOf(line[1]);
			
			else if(line[0].compareTo(bulkT0) == 0)
				tNucBulk = Double.valueOf(line[1]);
			
			line = s.nextLine().split(delimiter);
		}
		
		// read in the individiual spots
		int spotIdx = Integer.valueOf(line[0]);
		String[] header = s.nextLine().split(delimiter);
		int numFrames = header.length-2;
		Vector<String[]> lines = new Vector<String[]>();
		while(s.hasNextLine()) {
			line = s.nextLine().split(delimiter);
			if(line.length == 1) {
				Spot spot = new Spot();
				spot.spotIdx = spotIdx;
				spot.startFrame = Integer.valueOf(header[2]);
				spot.endFrame = Integer.valueOf(header[numFrames+1]);
				int numQ = lines.size();
				spot.Q = new double[numQ];
				spot.array = new double[numQ][numFrames];
				int curRow = 0;
				
				// get the new spot index before overwriting the line variable
				spotIdx = Integer.valueOf(line[0]);
				while(lines.size() > 0) {
					line = lines.remove(0);
					// determine Q for this line
					double x = Double.valueOf(line[0]);
					double y = Double.valueOf(line[1]);
					double twoTheta = Math.atan(Math.sqrt(Math.pow(x-x0,2)+ Math.pow(y-y0,2))*pixelSize/d);
					double Q = 4 * Math.PI * Math.sin(twoTheta/2.) / wavelength; 
					spot.Q[curRow] = Q;
					
					// parse the data into a 2D array
					for(int i = 2; i < line.length; i++) {
						spot.array[curRow][i-2] = Double.valueOf(line[i]);
					}
					curRow++;
				}
				spots.add(spot);
				lines.clear();
				// skip the header line
				s.nextLine();
			} else {
				lines.add(line);
			}
		}
		
	}
	
	public void run() {
		for(File f : inputFiles) {
			// Read File
			readFile(f);
			// Find peak location
			for(Spot spot : spots) {
				System.out.println("Estimated peak location for spot " + spot.spotIdx + " is " + spot.Q[spot.estimatePeakLocation()]);
			}
			System.out.println("File reading complete");
			
			// Fit gaussian to each peak
		
			// output gaussian fits and parameters
		
			// sum all pixels within 2 sigma of peak max as function of time
		
			// estimate KJMA parameters
			
			// fit to KJMA model
		
			// output time, alpha, KJMA fit
		}
	}
	public static void main(String[] args) {
		
		String delimiter = "\t";
		double k = 0.01;
		double t0 = 160;
		double n = 3;
		double timePerFrame = 1;
		
		double tNucBulk = 160; 
		double kBulk = 0.04;
		double VBulk = 1*10^-4;
		// select files to analyze
		
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setDialogTitle("Select Path Files from Ramdog");
		int returnVal = chooser.showOpenDialog(null);
		
		File[] files = null;
		
		switch(returnVal) {
		case JFileChooser.APPROVE_OPTION:
			files = chooser.getSelectedFiles();
			break;
		case JFileChooser.CANCEL_OPTION:
			System.err.println("No files selected...  Exiting...");
			System.exit(1);
		}
		
		_62a runner = new _62a(files);
		
		runner.delimiter = delimiter;
		runner.kBulk = kBulk;
		runner.VBulk = VBulk;
		runner.n = n;
		runner.run();
	}
}
