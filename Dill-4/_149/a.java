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
package _149;

import io.MyPrintStream;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import bravaisLattice.FCC;
import geometry.JVector;

public class a {

	private int notebookNumber = 4;
	private int pageNumber = 149;
	private String index = "a";
	private String outputFolder;
		
	public a() {
		outputFolder = "D:\\Documents referenced in lab notebooks\\Dill-" +
				notebookNumber + File.separator + pageNumber + File.separator + index;
	}
	
	public void run() {
		double len = 25;
		
		JVector axis = JVector.xy;
		Vector<JVector[]> basisSet = new Vector<JVector[]>();
		
		basisSet.add(JVector.v111s);
		basisSet.add(JVector.v220s);
		
		
		
		double angle = -90;
		double maxLen = new JVector(5, 5, 5).length();
		JVector[] crystal1 = FCC.getAllowedReflections(maxLen);
		crystal1 = JVector.v111s;
		Vector<JVector> reflections = new Vector<JVector>();
		for(int i = 0; i < crystal1.length; i++)
			reflections.add((JVector) crystal1[i].clone());
		crystal1 = JVector.v220s;
		for(int i = 0; i < crystal1.length; i++)
			reflections.add((JVector) crystal1[i].clone());
		
		crystal1 = new JVector[reflections.size()];
		reflections.toArray(crystal1);
		
		Vector<JVector> crystal2 = new Vector<JVector>();
		
		for(int i = 0; i < crystal1.length; i++) {
			crystal2.add(JVector.rotate(crystal1[i], axis, JVector.zero, angle));
		}
		File f = new File(outputFolder);
		f.mkdirs();
		f = new File(f + File.separator + "twin_110.inp");
		MyPrintStream mps = new MyPrintStream(f);
		mps.println("FIELDS LAB COO TYP RAD FLC");

		int type = 2;
		double radius = 1;
		Color c = Color.red;
		
		// ORIGIN
		JVector zero = JVector.zero;
		mps.println((int) zero.i + "" + (int) zero.j + "" + (int) zero.k +
				" " + JVector.multiply(zero, len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		// CRYSTAL 1
		for(JVector vec : crystal1) {
			mps.println((int) vec.i + "" + (int) vec.j + "" + (int) vec.k +
					" " + JVector.multiply(vec, len).toStringSpace(2) + " " + type + " " +
					radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		}
		// CRYSTAL 2
		type = 10;
		c = Color.blue;
		int idx = 0;
		for(JVector vec : crystal2) {
			JVector vec1 = crystal1[idx++];
			mps.println((int) vec1.i + "" + (int) vec1.j + "" + (int) vec1.k +
					" " + JVector.multiply(vec, len).toStringSpace(2) + " " + type + " " +
					radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		}
		// TWIN AXIS
		len = 10;
		type = 18;
		len *= len;
		c = Color.black;
		JVector vec = new JVector(1, 1, 0);
		vec.unit_inPlace();
		vec.multiply(len);
		mps.println("twin+ " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		vec.multiply(-1);
		mps.println("twin- " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		

		// CAPILLARY ROTATION AXIS
		type = 36;
		c = Color.yellow;
		JVector cap = new JVector(0, 2, -1);
		cap.unit_inPlace();
		cap.multiply(len);
		
		mps.println("cap+ " + JVector.multiply(cap.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		cap.multiply(-1);
		mps.println("cap- " + JVector.multiply(cap.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		
		
		// RELATIONSHIP TO DIFFRACTION IMAGES
		double startingAngle = 43;
		JVector v43 = new JVector(5, -5, -5);
		JVector vZero = JVector.rotate(v43, cap, JVector.zero, -startingAngle);
		vZero.multiply(10);
		angle = 0;
		
//		mps.println((angle+startingAngle)%360 + " " + JVector.multiply(v43.unit(), len).toStringSpace(2) + " " + 
//				type + " " + radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
//		
		len *= 3;
		c = Color.ORANGE;
		type = 40;
		for(angle = 0; angle < 360; angle += 5) {
			JVector handle = JVector.rotate(vZero, cap, JVector.zero, angle);
			handle.add(cap);
			mps.println(angle + " " + handle.toStringSpace(2) + " " + 
					type + " " + radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
			
		}
		mps.close();
		
	}
	public static void main(String[] args) {
		a a = new a();
		a.run();
	}
}
