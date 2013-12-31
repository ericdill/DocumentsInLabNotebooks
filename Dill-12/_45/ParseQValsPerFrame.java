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
package _45;

import io.MyFileInputStream;
import io.MyPrintStream;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class ParseQValsPerFrame implements Runnable {

	private String targetValsPerFrameContains = "Q values per frame";
	private String targetTimeContains = "normalized_spot.txt";
	
	private String folderRoot = "D:\\Data\\Spotpicking 6-26-13\\";
	private String delimiter = " --";
	
	private int firstTimeLine = 8;
	private double width = .075;
	private double q = 2.055;
	double qMin = q-width;
	double qMax = q+width;
	
	public void run() {
		File root = new File(folderRoot);
		File[] files = root.listFiles();
		String expt = "";
		Double[] time = null;
		int[] spotsPerRange = null;
		boolean foundTime = false;
		boolean parsedPerFrame = false;
		
		MyPrintStream mpsAll = new MyPrintStream(new File(folderRoot + File.separator + "c.txt"));
		MyPrintStream mpsFinal = new MyPrintStream(new File(folderRoot + File.separator + "d.txt"));
		for(int i = 0; i < files.length; i++) {
			expt = files[i].getName().split(delimiter)[0];
			if(files[i].getName().contains(targetTimeContains)) {
				time = getTime(files[i]);
				foundTime = true;
			}
			if(files[i].getName().contains(targetValsPerFrameContains)) {
				spotsPerRange = getSpotsPerRange(files[i]);
				parsedPerFrame = true;
			}
			
			if(foundTime && parsedPerFrame) {
				parsedPerFrame = false;
				foundTime = false;
				String out = "\t";
				
				for(int j = 0; j < time.length; j++) {
					out += time[j] + "\t";
				}
				mpsAll.println(out);
				
				out = expt + "\t";
				for(int j = 0; j < spotsPerRange.length; j++) {
					out += spotsPerRange[j] + "\t";
				}
				
				mpsAll.println(out);
				
				out = expt + "\t" + time[time.length-1] + "\t" + spotsPerRange[spotsPerRange.length-1];
				
				mpsFinal.println(out);
			}
			
			
		}
		mpsAll.close();
		mpsFinal.close();
	}
	
	private int[] getSpotsPerRange(File f) {
		MyFileInputStream mfis = new MyFileInputStream(f);
		Scanner s = mfis.getScanner();
		int numFrames = 0;
		while(s.hasNextLine()) {
			s.nextLine();
			numFrames++;
		}
		mfis.close();
		
		int[] perFrame = new int[numFrames];
		int curFrame = 0;
		mfis = new MyFileInputStream(f);
		s = mfis.getScanner();
		while(s.hasNextLine()) {
			String[] line = s.nextLine().split("\t"); 
			for(int i = 1; i < line.length; i++) {
				Double val = Double.valueOf(line[i]);
				if(val > qMin && val < qMax) {
					perFrame[curFrame]++;
				}
			}
			curFrame++;
		}
		
		
		mfis.close();
		
		return perFrame;
	}
	private Double[] getTime(File f) {
		MyFileInputStream mfis = new MyFileInputStream(f);
		Scanner s = mfis.getScanner();
		Vector<Double> time = new Vector<Double>();
		int curLine = 1;
		while(curLine++ < firstTimeLine) {
			s.nextLine();
		}
		while(s.hasNextLine()) {
			String[] line = s.nextLine().split("\t");
			
			time.add(Double.valueOf(line[0]));
			
		}
		Double[] timeSteps = new Double[time.size()];
		timeSteps = (Double[]) time.toArray(timeSteps);
		
		
		mfis.close();
		
		return timeSteps;
	}
	public static void main(String[] args) {
		new ParseQValsPerFrame().run();
	}
}
