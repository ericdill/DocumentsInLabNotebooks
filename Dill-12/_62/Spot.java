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

import statistics.Statistics;

public class Spot {

	public double[][] array;
	
	public int startFrame, endFrame, spotIdx;
	
	public double[] Q;
	
	/**
	 * Estimate the peak location by summing each pixel across all time. The pixel with the 
	 * maximum value is the peak location.
	 * @return
	 */
	public int estimatePeakLocation() {
		double[] vals = new double[Q.length];
		
		for(int i = 0; i < array.length; i++)
			vals[i] = Statistics.sum(array[i]);
		
		int maxIdx = 0;
		double maxVal = -Double.MAX_VALUE;
		for(int i = 0; i < vals.length; i++) {
			if(vals[i] > maxVal) {
				maxVal = vals[i];
				maxIdx = i;
			}
		}
		
		return maxIdx;
	}
}
