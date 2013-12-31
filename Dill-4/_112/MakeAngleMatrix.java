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
package _112;

import geometry.JVector;
import io.MyFileInputStream;
import io.StringConverter;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class MakeAngleMatrix {

	private File in;
	private int[] spotFrame;
	private double[] spotRadius;
	public MakeAngleMatrix(File f) {
		in = f;
	}
	public JVector[] toJVectors() {
		MyFileInputStream mfis = new MyFileInputStream(in);
		Scanner s = mfis.getScanner();
		// skip header
		s.nextLine();
		Vector<Integer> frames = new Vector<Integer>();
		Vector<Double> radii = new Vector<Double>();
		Vector<JVector> positions = new Vector<JVector>();
		while(s.hasNextLine()) {
			String[] splitLine = s.nextLine().split("\t");
			frames.add(Integer.valueOf(splitLine[0]));
			double x = Double.valueOf(splitLine[1]);
			double y = Double.valueOf(splitLine[2]);
			double z = Double.valueOf(splitLine[3]);
			positions.add(new JVector(x, y, z));
			radii.add(Double.valueOf(splitLine[4]));
		}
		
		spotFrame = new int[frames.size()];
		spotRadius = new double[radii.size()];
		for(int i = 0; i < spotFrame.length; i++) {
			spotFrame[i] = frames.get(i);
			spotRadius[i] = radii.get(i);
		}
		
		return positions.toArray(new JVector[positions.size()]);
	}
	public double[][] makeAngleMatrix(JVector[] vec) {
		double[][] arr = new double[vec.length+2][vec.length+2];
		
		// init row & column labels
		for(int i = 2; i < arr.length; i++) {
			arr[i][0] = arr[0][i] = spotFrame[i-2];
			arr[i][1] = arr[1][i] = spotRadius[i-2];
		}
		
		for(int i = 2; i < arr.length; i++) {
			for(int j = 2; j < arr.length; j++) {
				arr[i][j] = JVector.angleDegrees(vec[i-2], vec[j-2]);
			}
		}
		
		return arr;
	}
	public static void main(String[] args) {
		File inFile = new File("D:\\Documents referenced in lab notebooks\\Dill-4\\112\\primary 111 reflections.txt");
		
		MakeAngleMatrix mat = new MakeAngleMatrix(inFile);
		
		JVector[] vec = mat.toJVectors();
		
		double[][] angles = mat.makeAngleMatrix(vec);
		
		for(int i = 0; i < angles.length; i++) {
			System.out.println(StringConverter.arrayToTabString(angles[i]));
		}
	}
}
