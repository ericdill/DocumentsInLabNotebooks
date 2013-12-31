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
package _116;

import io.MyPrintStream;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import geometry.JVector;

public class b {

	public static void main(String[] args) {
		double len = 10;
		
		JVector[] v111s = JVector.v111s;
		
		JVector axis = JVector.xy;
		
		double angle = 90;
		Vector<JVector> crystal2 = new Vector<JVector>();
		
		for(int i = 0; i < v111s.length; i++) {
			crystal2.add(JVector.rotate(v111s[i], axis, JVector.zero, 90));
		}
		MyPrintStream mps = new MyPrintStream(new File("D:\\Documents referenced in lab notebooks\\Dill-4\\116\\b\\twin.inp"));
		mps.println("FIELDS LAB COO TYP RAD FLC");
		// CRYSTAL 1
		int type = 2;
		double radius = 1;
		Color c = Color.red;
		for(JVector vec : v111s) {
			mps.println((int) vec.i + "" + (int) vec.j + "" + (int) vec.k +
					" " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
					radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		}
		// CRYSTAL 2
		type = 10;
		c = Color.blue;
		int idx = 0;
		for(JVector vec : crystal2) {
			JVector vec1 = v111s[idx++];
			mps.println((int) vec1.i + "" + (int) vec1.j + "" + (int) vec1.k +
					" " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
					radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		}
		// TWIN AXIS
		type = 18;
		len = 15;
		c = Color.black;
		JVector vec = new JVector(1, 1, 0);
		vec.unit_inPlace();
		vec.multiply(len);
		mps.println("twin+ " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		vec.multiply(-1);
		mps.println("twin- " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		

		type = 36;
		c = Color.yellow;
		vec = new JVector(0, -2, 1);
		vec.unit_inPlace();
		vec.multiply(len);
		mps.println("cap+ " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());

		// CAPILLARY ROTATION AXIS
		vec.multiply(-1);
		mps.println("cap- " + JVector.multiply(vec.unit(), len).toStringSpace(2) + " " + type + " " +
				radius + " " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
		
		mps.close();
	}
}
