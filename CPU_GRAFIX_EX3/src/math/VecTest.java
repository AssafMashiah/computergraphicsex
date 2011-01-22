package math;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test suite for the Vec 3d vector class
 *
 */
public class VecTest {

	/**
	 * Test method for {@link lab05.math.Vec#Vec(double, double, double)}.
	 */
	@Test
	public void testVecDoubleDoubleDouble() {
		Vec v1 = new Vec(3,2,1);
		assertTrue(Vec.equals(v1, new Vec(3,2,1)));
	}

	/**
	 * Test method for {@link lab05.math.Vec#Vec(lab05.math.Vec)}.
	 */
	@Test
	public void testVecVec() {
		Vec v1 = new Vec(3,2,1);
		Vec v2 = new Vec(v1);
		assertTrue(Vec.equals(v1, v2));
	}

	/**
	 * Test method for {@link lab05.math.Vec#reflect(lab05.math.Vec)}.
	 */
	@Test
	public void testReflect() {
		Vec v1 = new Vec(10,20,30);
		Vec n = new Vec(5,2,4);
		n.normalize();
		
		Vec v2 = v1.reflect(n);
		
		// Project on n with the same length
		assertEquals(0, Vec.dotProd(v1, n)+Vec.dotProd(v2, n),1e-10);
		
		// Distance between them equals twice the projection length
		assertEquals(Math.abs(2*Vec.dotProd(v1,n)), Vec.distance(v1, v2),1e-10);
	}

	/**
	 * Test method for {@link lab05.math.Vec#add(lab05.math.Vec)}.
	 */
	@Test
	public void testAddVec() {
		Vec v = new Vec(10,20,30);
		v.add(new Vec(1,-3,-7));
		assertTrue(v.equals(new Vec(11,17,23)));		
	}

	
	/**
	 * Test method for {@link lab05.math.Vec#sub(Vec)}.
	 */
	@Test
	public void testSubVec() {
		Vec v = new Vec(10,20,30);
		v.sub(new Vec(1,-3,-7));
		assertTrue(v.equals(new Vec(9,23,37)));				
	}
	
	/**
	 * Test method for {@link lab05.math.Vec#mac(double, lab05.math.Vec)}.
	 */
	@Test
	public void testMac() {
		Vec v = new Vec(10,20,30);
		v.mac(-2,new Vec(1,-3,-7));
		assertTrue(v.equals(new Vec(8,26,44)));		
	}

	/**
	 * Test method for {@link lab05.math.Vec#scale(double)}.
	 */
	@Test
	public void testScaleDouble() {
		Vec v = new Vec(10,20,30);
		v.scale(10);
		assertTrue(v.equals(new Vec(100,200,300)));		
	}

	/**
	 * Test method for {@link lab05.math.Vec#scale(lab05.math.Vec)}.
	 */
	@Test
	public void testScaleVec() {
		Vec v = new Vec(10,20,30);
		v.scale(new Vec(1,-1,0));
		assertTrue(v.equals(new Vec(10,-20,0)));			
	}

	/**
	 * Test method for {@link lab05.math.Vec#negate()}.
	 */
	@Test
	public void testNegate() {
		Vec v = new Vec(10,20,30);
		v.negate();
		assertTrue(v.equals(new Vec(-10,-20,-30)));		
	}

	/**
	 * Test method for {@link lab05.math.Vec#length()}.
	 */
	@Test
	public void testLength() {
		Vec v = new Vec(3.5,-5,10);		
		assertTrue(
				v.length()==Math.sqrt(v.x*v.x+v.y*v.y+v.z*v.z)
				);		
	}

	/**
	 * Test method for {@link lab05.math.Vec#lengthSquared()}.
	 */
	@Test
	public void testLengthSquared() {
		Vec v = new Vec(3.5,-5,10);		
		assertTrue(
				v.lengthSquared()==(v.x*v.x+v.y*v.y+v.z*v.z)
				);		
	}

	/**
	 * Test method for {@link lab05.math.Vec#dotProd(lab05.math.Vec)}.
	 */
	@Test
	public void testDotProdVec() {
		Vec v1 = new Vec(3.5,-5,10);		
		Vec v2 = new Vec(2.5,7,0.5);
		assertTrue(
				v1.dotProd(v2)==(v1.x*v2.x+v1.y*v2.y+v1.z*v2.z)
				);				
	}

	/**
	 * Test method for {@link lab05.math.Vec#normalize()}.
	 */
	@Test
	public void testNormalize() {
		Vec v = new Vec(3.5,-5,10);
		v.normalize();
		assertEquals(1, v.length(),1e-10);
		
		v = new Vec(0,0,0);
		try {
			v.normalize();
			fail("Didn't throw divide by zero exception!");
		} catch (ArithmeticException e) {
			assertTrue(true);			
		}
	}

	/**
	 * Test method for {@link lab05.math.Vec#angle(lab05.math.Vec)}.
	 */
	@Test
	public void testAngle() {
		Vec v1 = new Vec(3.5,-5,10);		
		Vec v2 = new Vec(2.5,7,0.5);
		assertEquals(
				v1.angle(v2),
				Math.acos(Vec.dotProd(v1, v2)/(v1.length()*v2.length())), 
				1e-10
				);		
		assertFalse(Double.isNaN(v1.angle(v2)));
	}

	/**
	 * Test method for {@link lab05.math.Vec#distance(lab05.math.Vec, lab05.math.Vec)}.
	 */
	@Test
	public void testDistance() {
		Vec v1 = new Vec(3.5,-5,10);		
		Vec v2 = new Vec(2.5,7,0.5);
		Vec v3 = new Vec(v2);
		v3.mac(-1, v1);
		assertEquals(v3.length(),Vec.distance(v1, v2), 1e-10);
		
		assertFalse(Double.isNaN(v3.length()));
	}

	/**
	 * Test method for {@link lab05.math.Vec#crossProd(lab05.math.Vec, lab05.math.Vec)}.
	 */
	@Test
	public void testCrossProd() {
		Vec v1 = new Vec(3.5,-5,10);		
		Vec v2 = new Vec(2.5,7,0.5);
		Vec v3 = Vec.crossProd(v1, v2);
		assertEquals(0, Vec.dotProd(v3, v2),1e-10);
		assertEquals(0, Vec.dotProd(v3, v1),1e-10);
		
		Vec v4 = Vec.crossProd(v2, v1);
		assertEquals(0, Vec.add(v3, v4).length(),1e-10);
	}

	/**
	 * Test method for {@link lab05.math.Vec#add(lab05.math.Vec, lab05.math.Vec)}.
	 */
	@Test
	public void testAddVecVec() {
		Vec v = Vec.add(new Vec(1,-3,-7),new Vec(10,20,30));
		assertNotNull(v);
		assertTrue(v.equals(new Vec(11,17,23)));		
	}

	/**
	 * Test method for {@link lab05.math.Vec#sub(lab05.math.Vec, lab05.math.Vec)}.
	 */
	@Test
	public void testSub() {
		Vec v = Vec.sub(new Vec(1,-3,-7),new Vec(10,20,30));
		assertNotNull(v);
		assertTrue(v.equals(new Vec(-9,-23,-37)));		
	}

	/**
	 * Test method for {@link lab05.math.Vec#negate(lab05.math.Vec)}.
	 */
	@Test
	public void testNegateVec() {
		Vec v = Vec.negate(new Vec(1,-3,-7));
		assertNotNull(v);
		assertTrue(v.equals(new Vec(-1,3,7)));		
	}

	/**
	 * Test method for {@link lab05.math.Vec#scale(double, lab05.math.Vec)}.
	 */
	@Test
	public void testScaleDoubleVec() {
		Vec v = Vec.scale(10,new Vec(10,20,30));
		assertNotNull(v);
		assertTrue(v.equals(new Vec(100,200,300)));		
	}

	/**
	 * Test method for {@link lab05.math.Vec#scale(lab05.math.Vec, lab05.math.Vec)}.
	 */
	@Test
	public void testScaleVecVec() {
		Vec v = Vec.scale(new Vec(-1,0,1),new Vec(10,20,30));
		assertNotNull(v);
		assertTrue(v.equals(new Vec(-10,0,30)));		
	}
	
	/**
	 * Test method for {@link lab05.math.Vec#equals(lab05.math.Vec, lab05.math.Vec)}.
	 */
	@Test
	public void testEqualsVecVec() {
		Vec v1 = new Vec(3.5,-5,10);		
		Vec v2 = new Vec(2.5,7,0.5);
		Vec v3 = new Vec(2.5,7,0.5);		
		assertTrue(Vec.equals(v1,v1));
		assertTrue(Vec.equals(v2,v2));
		assertTrue(Vec.equals(v2,v3));
		assertTrue(Vec.equals(v3,v2));
		assertFalse(Vec.equals(v1,v3));
		assertFalse(Vec.equals(v2,v1));
	}

	/**
	 * Test method for {@link lab05.math.Vec#dotProd(lab05.math.Vec, lab05.math.Vec)}.
	 */
	@Test
	public void testDotProdVecVec() {
		Vec v1 = new Vec(3.5,-5,10);		
		Vec v2 = new Vec(2.5,7,0.5);
		assertTrue(
				Vec.dotProd(v1,v2)==(v1.x*v2.x+v1.y*v2.y+v1.z*v2.z)
				);				
	}
}
