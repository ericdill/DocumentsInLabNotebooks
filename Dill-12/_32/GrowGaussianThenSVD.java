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

import jama.Matrix;
import jama.SingularValueDecomposition;

import java.io.File;

import matrix.SVD;

import io.MyPrintStream;
import io.StringConverter;
import equations.Avrami;
import equations.Equation;

public class GrowGaussianThenSVD implements Runnable {

	private double k = 0.1,
			t0 = 10,
			n=3,
			x0=.75,
			sigma=0.1,
			x_max = 1.5,
			x_step=.01,
			t_step=1,
			total_time = 100;
	private Equation avrami = new Avrami(new double[] {k, t0, n});
	private double[][] data;
	private double[] time, pos;
	private double evaluateGaussian(double t, double x) {
		double A = avrami.evaluate(t);
		double val = A / Math.sqrt(2*Math.PI) / sigma * Math.exp(-0.5 * Math.pow((x-x0)/sigma, 2));
		
		return val;
	}
	public void run() {
		int num_t = (int) Math.rint(total_time / t_step);
		int num_x = (int) Math.rint(x_max / x_step);
		
		data = new double[num_x][num_t];
		time = new double[num_t];
		pos = new double[num_x];
		
		double t = 0, x, val;
		for(int i = 0; i < data.length; i++) {
			x = ((double) i ) * x_step;
			pos[i] = x;
			for(int j = 0; j < data[i].length; j++) {
				t = j*t_step;
				if(i == 0) {
					time[j] = t;
				}
				val = evaluateGaussian(t, x);
				data[i][j] = val;
			}
		}
		doSVD();
		printData();
	}
	private void doSVD() {
		Matrix m = new Matrix(data);
		SingularValueDecomposition svd = m.svd();
		
		double[][] U = svd.getU().getArray();
		double[][] V = svd.getV().getArray();
		double[] S = svd.getSingularValues();
		
		MyPrintStream mps = new MyPrintStream(new File("EDD_12-32b -- svd.txt"));
		
		mps.println("Singular Values\n" + StringConverter.arrayToTabString(S));
		
		mps.println("\nLeft Singular Vectors\n");
		for(int i = 0; i < U.length; i++) {
			mps.println(pos[i] + "\t" + StringConverter.arrayToTabString(U[i]));
		}
		mps.println("\nRight Singular Vectors\n");
		for(int i = 0; i < V.length; i++) {
			mps.println(time[i] + "\t" + StringConverter.arrayToTabString(V[i]));
		}
		
		mps.close();
		
	}
	private void printData() {
		MyPrintStream mps = new MyPrintStream(new File("EDD_12-32b -- raw.txt"));
		mps.println("\t" + StringConverter.arrayToTabString(time));
		for(int i = 0; i < data.length; i++) {
			mps.println(pos[i] + "\t" + StringConverter.arrayToTabString(data[i]));
		}
		
		mps.close();
	}
	public static void main(String[] args) {
		new GrowGaussianThenSVD().run();
	}
}
