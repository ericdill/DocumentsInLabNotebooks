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
package _32;

import io.MyFileInputStream;
import io.MyPrintStream;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class Read1D {

	private File key, output, interpolated, contour, difference;
	private String[] keyNames;
	private Double[] key_t0, key_k, key_temp;
	private File[] inputs;
	private Double[][] original_t, new_t, alpha;
	private String[] fileNames;
	private Vector<KineticFile> files;
	private double[] interpolatedTime, interpolatedAverage;
	
	public Read1D(File folder, File key, String outputFolder) {
		inputs = folder.listFiles();
		output = new File(outputFolder + "rawOutput.txt");
		interpolated = new File(outputFolder + "interpolatedOutput.txt");
		difference = new File(outputFolder + "interpolatedOutput_Difference.txt");
		contour = new File(outputFolder + "contour.txt");
		this.key = key;
		parseKey();
		readFiles();
		sortByTemperature();
		normalize();
		interpolate();
		calcAverage();
		printToFile();
		printContour();
	}
	
	private void interpolate() {
		double minX=0, maxX=0, minStep=1;
		KineticFile xf;
		double curMin, curMax, curStep;
		for(int i = 0; i < files.size(); i++) {
			xf = files.get(i);
			curMin = xf.new_t[0];
			curMax = xf.new_t[xf.new_t.length-1];
			curStep = xf.new_t[1] - xf.new_t[0];
			if(minX > curMin) {
				minX = curMin;
			}
			if(maxX < curMax) {
				maxX = curMax;
			}
			if(curStep != 0 && minStep > curStep) {
				minStep = curStep;
			}
		}
		
		for(int i = 0; i < files.size(); i++) {
			files.get(i).interpolate(-5, maxX, .01);
		}
	}
	private void parseKey() {
		Vector<String> names = new Vector<String>();
		Vector<Double> t0 = new Vector<Double>();
		Vector<Double> k = new Vector<Double>();
		Vector<Double> T = new Vector<Double>();
		
		MyFileInputStream mfis = new MyFileInputStream(key);
		Scanner s = mfis.getScanner();
		s.nextLine();
		while(s.hasNextLine()) {
			names.add(s.next());
			k.add(s.nextDouble());
			t0.add(s.nextDouble());
			T.add(s.nextDouble());
		}
		keyNames = new String[names.size()];
		keyNames = names.toArray(keyNames);
		
		key_t0 = new Double[t0.size()];
		key_t0 = t0.toArray(key_t0);
		
		key_k = new Double[k.size()];
		key_k = k.toArray(key_k);
		
		key_temp = new Double[T.size()];
		key_temp = T.toArray(key_temp);
		
		mfis.close();
	}
	private void calcAverage() {
		int len = files.get(0).interpolatedX.length;
		interpolatedTime = new double[len];
		interpolatedAverage = new double[len];
		KineticFile xf;
		for(int i = 0; i < files.size(); i++) {
			xf = files.get(i);
			for(int line = 0; line < len; line++) {
				if(i == 0) {
					interpolatedTime[line] = xf.interpolatedX[line];
					interpolatedAverage[line] = 0;
				}
				interpolatedAverage[line] += xf.interpolatedY[line];
			}
		}
		for(int line = 0; line < len; line++) {
			interpolatedAverage[line] /= files.size();
		}
		
		for(int i = 0; i < files.size(); i++) {
			files.get(i).calcDifference(interpolatedAverage);
		}
	}
	private void normalize() {
		for(int i = 0; i < files.size(); i++) {
			files.get(i).normalize_time();
		}
	}
	private void readFiles() {
		Vector<Double> cur_t, cur_alpha;
		MyFileInputStream mfis;
		Scanner s;
		KineticFile xf;
		files = new Vector<KineticFile>();
		for(int i = 0; i < inputs.length; i++) {
			cur_t = new Vector<Double>();
			cur_alpha = new Vector<Double>();
			xf = new KineticFile();
			if(inputs[i].isFile()) {
				xf.f = inputs[i];
				xf.fName = inputs[i].getName();
				setVals(xf);
				mfis = new MyFileInputStream(xf.f);
				s = mfis.getScanner();
				
				while(s.hasNextLine()) {
					cur_t.add(Double.valueOf(s.next()));
					cur_alpha.add(Double.valueOf(s.next()));
				}
				xf.original_t = new Double[cur_t.size()];
				xf.original_t = cur_t.toArray(xf.original_t);
				
				xf.alpha = new Double[cur_alpha.size()];
				xf.alpha = cur_alpha.toArray(xf.alpha);
				
				mfis.close();
				files.add(xf);
			}
		}
	}
	
	private void setVals(KineticFile xf) {
		for(int i = 0; i < keyNames.length; i++) {
			if(xf.fName.compareTo(keyNames[i]) == 0) {
				xf.k = key_k[i];
				xf.t0 = key_t0[i];
				xf.T = key_temp[i];
			}
		}
	}
	
	private double[] getVals(String fName) {
		for(int i = 0; i < keyNames.length; i++) {
			if(fName.compareTo(keyNames[i]) == 0) {
				return new double[] {key_k[i], key_t0[i], key_temp[i]};
			}
		}
		return null;
	}
	private void printContour() {
		MyPrintStream mps = new MyPrintStream(contour);
		int maxLen = files.get(0).interpolatedX.length;
		KineticFile xf;
		mps.println("index\treduced time\talpha\tname\tk\tisotherm temp\tt0");
		for(int i = 0; i < files.size(); i++) {
			xf = files.get(i);
			String name = xf.fName + "\t" + xf.k + "\t" + xf.T + "\t" + xf.t0;
			for(int line = 0; line < maxLen; line++) {
				mps.println(i + "\t" + xf.getLine_difference(line) + "\t" + name);
			}
		}
	}
	private void printToFile() {
		MyPrintStream mps = new MyPrintStream(output);
		MyPrintStream mps2 = new MyPrintStream(interpolated);
		MyPrintStream mps3 = new MyPrintStream(difference);
		Vector<String> lines = new Vector<String>();
		Vector<String> lines2 = new Vector<String>();
		Vector<String> lines3 = new Vector<String>();
		Double[][] arr = new_t;
		int jMax = 0;
		for(int i = 0; i < files.size(); i++) {
			KineticFile xf = files.get(i);
			if(jMax < xf.alpha.length) { jMax = xf.alpha.length; }
		}
		String line = "";
		String line1 = "";
		String line2 = "";
		String line3 = "";
		for(int i = 0; i < files.size(); i++) {
			KineticFile xf = files.get(i);
			line += xf.fName + "\t" + xf.fName + "\t";
			line1 += xf.k + "\t" + xf.k + "\t"; 
			line2 += xf.t0 + "\t" + xf.t0 + "\t"; 
			line3 += xf.T + "\t" + xf.T + "\t";
		}
		
		lines.add(line);
		lines.add(line1);
		lines.add(line2);
		lines.add(line3);
		line = "file name\t";
		line1 = "k\t";
		line2 = "t0\t";
		line3 = "T\t";
		for(int i = 0; i < files.size(); i++) {
			KineticFile xf = files.get(i);
			line += xf.fName + "\t";
			line1 += xf.k + "\t"; 
			line2 += xf.t0 + "\t"; 
			line3 += xf.T + "\t";
		}
		lines2.add(line);
		lines2.add(line1);
		lines2.add(line2);
		lines2.add(line3);
		
		lines3.add(line);
		lines3.add(line1);
		lines3.add(line2);
		lines3.add(line3);
		for(int j = 0; j < jMax; j++) {
			line = "";
			for(int i = 0; i < files.size(); i++) {
				KineticFile xf = files.get(i);
				line += xf.getLine(j);
			}
			lines.add(line);
		}
		
		int numLines = files.get(0).interpolatedX.length;
		for(int j = 0; j < numLines; j++) {
			line2 = files.get(0).interpolatedX[j] + "\t";
			line3 = files.get(0).interpolatedX[j] + "\t";
			for(int i = 0; i < files.size(); i++) {
				KineticFile xf = files.get(i);
				line2 += xf.interpolatedY[j] + "\t";
				line3 += xf.interpolatedDifference[j] + "\t";
			}
			lines2.add(line2);
			lines3.add(line3);
		}
		
		for(int i = 0; i < lines.size(); i++) {
			mps.println(lines.get(i));
		}
		for(int i = 0; i < lines2.size(); i++) {
			mps2.println(lines2.get(i));
		}
		for(int i = 0; i < lines2.size(); i++) {
			mps3.println(lines3.get(i));
		}
	}
	
	private void sortByTemperature() {
		Vector<KineticFile> sorted = new Vector<KineticFile>();
		
		KineticFile xf;
		int insertionPosition = 0;
		double curTemp, compareTemp;
		boolean found = false;
		for(int i = 0; i < files.size(); i++) {
			xf = files.get(i);
			insertionPosition = 0;
			curTemp = xf.T;
			found = false;
			for(int j = 0; j < sorted.size() && !found; j++) {
				compareTemp = sorted.get(j).T;
				insertionPosition = j+1;
//				System.out.println("curFile: " + xf.fName + "\tcurTemp: " + curTemp + "\tcompareTemp: " + compareTemp + "\tinsertionPosition: " + insertionPosition);
				if(curTemp < compareTemp) {
					found = true;
					insertionPosition--;
				}
			}
			sorted.add(insertionPosition, xf);
//			System.out.println("Current order:");
//			for(int j = 0; j < sorted.size(); j++) {
//				System.out.println(sorted.get(j).fName + "\t" + sorted.get(j).T);
//			}
		}
		files = sorted;
	}
	
	public static void main(String[] args) {
		File folder = new File("E:\\Documents referenced in lab notebooks\\Dill-12\\32\\1d transformations\\");
		File key = new File("E:\\Documents referenced in lab notebooks\\Dill-12\\32\\t0_k_key.txt");
		String outputFolder = key.getParent() + File.separator;
		new Read1D(folder, key, outputFolder);
	}
}

class KineticFile {
	public File f;
	public String fName;
	public Double[] alpha;
	public Double[] original_t;
	public Double[] new_t;
	public double k, t0, T;
	public Double[] interpolatedX, interpolatedY, interpolatedDifference;
	
	public String getLine(int idx) {
		if(idx >= alpha.length) {
			return "\t\t";
		}
		return new_t[idx] + "\t" + alpha[idx] + "\t";
	}

	public String getLine_interpolated(int idx) {
		if(idx >= interpolatedY.length) {
			return "\t\t";
		}
		String val = interpolatedX[idx] + "\t" + interpolatedY[idx];
		return val + "\t";
	}
	public String getLine_difference(int idx) {
		if(idx >= interpolatedY.length) {
			return "\t\t";
		}
		String val = interpolatedX[idx] + "\t" + interpolatedDifference[idx];
		return val + "\t";
	}
	public void normalize_time() {
		new_t = new Double[original_t.length];
		double tHalf = Math.exp((Math.log(-1*Math.log(0.5)) - 3 * Math.log(k))/3);
		for(int i = 0; i < original_t.length; i++) {
			new_t[i] = (original_t[i] - t0) / tHalf;
		}
	}
	
	public void calcDifference(double[] average) {
		interpolatedDifference = new Double[average.length];
		
		for(int i = 0; i < average.length; i++) {
			interpolatedDifference[i] = interpolatedY[i] - average[i];
		}
	}
	public void interpolate(double xMin, double xMax, double newStepSize) {
		double x1=0, y1=0, x2=0, y2=0, xn=0, yn=0, dx=1;
		
		int numSteps = (int) Math.rint((xMax-xMin)/newStepSize);
		
		interpolatedX = new Double[numSteps];
		interpolatedY = new Double[numSteps];
		
		for(int i = 0; i < interpolatedX.length; i++) {
			xn = xMin + i*newStepSize;
			
			interpolatedX[i] = xn;
			
			if(xn <= new_t[0]) {
				interpolatedY[i] = 0.;
			} else if(xn >= new_t[new_t.length-1]) {
				interpolatedY[i] = 1.;
			} else {
				boolean found = false;
				for(int j = 1; j < new_t.length && !found; j++) {
					x1 = new_t[j-1];
					x2 = new_t[j];
					dx = x2-x1;
					if(xn > x1 && xn < x2) {
						found = true;
						y1 = alpha[j-1];
						y2 = alpha[j];
					}
				}
				
				yn =  y2 * (xn - x1)/dx + y1 * (x2-xn)/dx;
				interpolatedY[i] = yn;
				System.out.println(interpolatedY[i]);
			}
		}
	}
}

class DSCFile {
	
}
