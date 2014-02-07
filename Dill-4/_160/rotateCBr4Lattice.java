package _160;

import geometry.JVector;

public class rotateCBr4Lattice {

	public static void main(String[] args) {
		JVector x = new JVector( 1.21,  -0.5365, 0.7661);
		JVector y = new JVector( 0.3301, 1.643,  0.6307);
		JVector z = new JVector(-1.21,  -0.4576, 1.982);
		
//		x.unit_inPlace();
//		y.unit_inPlace();
//		z.unit_inPlace();
		
		JVector[] arr = {x, y, z};
		
		System.out.println("Original Axes");

		for(int i = 0; i < arr.length; i++)
			System.out.println(arr[i]);
		
		/* Rotation 1 */
		JVector xyProj = new JVector(x.i, x.j, 0);
		
		double angle1 = JVector.angleDegrees(x, xyProj);
		
		JVector axis1 = JVector.cross(x, xyProj);
		
		for(int i = 0; i < arr.length; i++)
			arr[i] = JVector.rotate(arr[i], axis1, JVector.zero, angle1);

		System.out.println("\nAxes after rotation 1");
		for(int i = 0; i < arr.length; i++)
			System.out.println(arr[i]);

		/* Rotation 2 */		
		double angle2 = JVector.angleDegrees(arr[0], JVector.x);
		
		JVector axis2 = JVector.z;
		
		for(int i = 0; i < arr.length; i++)
			arr[i] = JVector.rotate(arr[i], axis2, JVector.zero, angle2);
		
		System.out.println("\nAxes after rotation 2");
		for(int i = 0; i < arr.length; i++)
			System.out.println(arr[i]);	
		
		/* Rotation 3 */
		double angle3 = -JVector.angleDegrees(arr[1], JVector.y);
		
		JVector axis3 = JVector.x;
		
		for(int i = 0; i < arr.length; i++)
			arr[i] = JVector.rotate(arr[i], JVector.x, JVector.zero, angle3);
		
		System.out.println("\nAxes after rotation 3");
		for(int i = 0; i < arr.length; i++)
			System.out.println(arr[i]);	
		
		System.out.println("\n-----------------------\n");
		System.out.println("Rotation axis 1: (x,y,z,phi)\t" + axis1.unit() + "," + angle1*Math.PI/180);
		System.out.println("Rotation axis 1: (x,y,z,phi)\t" + axis2.unit() + "," + angle2*Math.PI/180);
		System.out.println("Rotation axis 1: (x,y,z,phi)\t" + axis3.unit() + "," + angle3*Math.PI/180);
	}
}
