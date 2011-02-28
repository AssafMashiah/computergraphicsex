

import java.awt.Color;
import java.util.Scanner;

/**
 * 3D vector class that contains three doubles. Could be used to represent
 * Vectors but also Colors.
 * 
 */
public class Vec {
	
	protected static final int MINUS_ONE = -1;
	protected static final int ZERO = 0;

	/**
	 * Vector constuctor  
	 * @param s
	 *            String - containing the x,y,z coordinates
	 */
	public Vec(String s) {
		Scanner s = new Scanner(s);
		x = s.nextDouble();
		y = s.nextDouble();
		z = s.nextDouble();
	}

	/**
	 * Converts the vector to a color by clamping the values to 1. 
	 */
//    >>> WHAT IF x,y,z are negative?
	public Color toColor() {
		float r = (float) (x > 1 ? 1 : x);
		float g = (float) (y > 1 ? 1 : y);
		float b = (float) (z > 1 ? 1 : z);
		return new Color(r, g, b);
	}


	/**
	 * Vector data. Allowed to be accessed publicly for performance reasons
	 */
	public double x, y, z;

	/**
	 * Initialize vector to (0,0,0)
	 */
	public Vec() {
		this.x = ZERO;
		this.y = ZERO;
		this.z = ZERO;
	}

	/**
	 * Initialize vector to given coordinates
	 * 
	 * @param x
	 *            Scalar
	 * @param y
	 *            Scalar
	 * @param z
	 *            Scalar
	 */
	public Vec(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Initialize vector values to given vector (copy by value)
	 * 
	 * @param v
	 *            Vector
	 */
	public Vec(Vec v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	/**
	 * Calculates the reflection of the vector in relation a given surface
	 * normal. The vector points at the surface and the result points away.
	 * 
	 * @return The reflected vector
	 */
	public Vec reflect(Vec normal) {
		  Vec u = new Vec(this.x, this.y ,this.z);
		  double refSize = 2.0 * u.dotProd(normal);
		  Vec res = new Vec(normal);
		  res.scale(refSize);
		  return  sub(u,res);
	}

	/**
	 * Adds a to vector
	 * 
	 * @param a
	 *            Vector
	 */
	public void add(Vec a) {
		
	}

	/**
	 * Subtracts from vector
	 * 
	 * @param a
	 *            Vector
	 */
	public void sub(Vec a) {
		
	}

	/**
	 * Multiplies & Accumulates vector with given vector and a. v := v + s*a
	 * 
	 * @param s
	 *            Scalar
	 * @param a
	 *            Vector
	 */
	public void mac(double s, Vec a) {
		
	}

	/**
	 * Multiplies vector with scalar. v := s*v
	 * 
	 * @param s
	 *            Scalar
	 */
	public void scale(double s) {
		
	}

	/**
	 * Pairwise multiplies with anther vector
	 * 
	 * @param a
	 *            Vector
	 */
	public void scale(Vec a) {
		
	}

	/**
	 * Inverses vector all cordinares are times -1
	 * 
	 * @return Vector
	 */
	public void negate() {
		
	}

	/**
	 * Computes the vector's magnitude
	 * 
	 * @return Scalar
	 */
	public double length() {
		
	}

	/**
	 * Computes the vector's magnitude squared. Used for performance gain.
	 * 
	 * @return Scalar
	 */
	public double lengthSquared() {
		
	}

	/**
	 * Computes the dot product between two vectors
	 * 
	 * @param a
	 *            Vector
	 * @return Scalar
	 */
	public double dotProd(Vec a) {
		
	}

	/**
	 * Normalizes the vector to have length 1. Throws exception if magnitude is
	 * zero.
	 * 
	 * @throws ArithmeticException
	 */
	public void normalize() throws ArithmeticException {
		
	}

	/**
	 * Compares to a given vector
	 * 
	 * @param a
	 *            Vector
	 * @return True if have same values, false otherwise
	 */
	public boolean equals(Vec a) {
		
	}

	/**
	 * Returns the angle in radians between this vector and the vector
	 * parameter; the return value is constrained to the range [0,PI].
	 * 
	 * @param v1
	 *            the other vector
	 * @return the angle in radians in the range [0,PI]
	 */
	public final double angle(Vec v1) {
		
	}

	/**
	 * Computes the Euclidean distance between two points
	 * 
	 * @param a
	 *            Point1
	 * @param b
	 *            Point2
	 * @return Scalar
	 */
	static public double distance(Vec a, Vec b) {
		
	}

	/**
	 * Computes the cross product between two vectors using the right hand rule
	 * 
	 * @param a
	 *            Vector1
	 * @param b
	 *            Vector2
	 * @return Vector1 x Vector2
	 */
	public static Vec crossProd(Vec a, Vec b) {
		
	}

	/**
	 * Adds vectors a and b
	 * 
	 * @param a
	 *            Vector
	 * @param b
	 *            Vector
	 * @return a+b
	 */
	public static Vec add(Vec a, Vec b) {
		
	}

	/**
	 * Subtracts vector b from a
	 * 
	 * @param a
	 *            Vector
	 * @param b
	 *            Vector
	 * @return a-b
	 */
	public static Vec sub(Vec a, Vec b) {
		
	}

	/**
	 * Inverses vector's direction
	 * 
	 * @param a
	 *            Vector
	 * @return -1*a
	 */
	public static Vec negate(Vec a) {
		
	}

	/**
	 * Scales vector a by scalar s
	 * 
	 * @param s
	 *            Scalar
	 * @param a
	 *            Vector
	 * @return s*a
	 */
	public static Vec scale(double s, Vec a) {
		
	}

	/**
	 * Pair-wise scales vector a by vector b
	 * 
	 * @param a
	 *            Vector
	 * @param b
	 *            Vector
	 * @return a.*b
	 */
	public static Vec scale(Vec a, Vec b) {
		
	}

	/**
	 * Compares vector a to vector b
	 * 
	 * @param a
	 *            Vector
	 * @param b
	 *            Vector
	 * @return a==b
	 */
	public static boolean equals(Vec a, Vec b) {
		
	}

	/**
	 * Dot product of a and b
	 * 
	 * @param a
	 *            Vector
	 * @param b
	 *            Vector
	 * @return a.b
	 */
	public static double dotProd(Vec a, Vec b) {
		
	}

	/**
	 * Returns a string that contains the values of this vector. The form is
	 * (x,y,z).
	 * 
	 * @return the String representation
	 */
	public String toString() {
		return String.format("(d%, d%, d%)", this.x, this.y, this.z);
	}

	@Override
	public Vec clone() {
	
	}
}
