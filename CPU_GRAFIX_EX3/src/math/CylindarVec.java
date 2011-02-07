package math;

public class CylindarVec
{
	protected static final int MINUS_ONE = -1;
	protected static final int ZERO = 0;
	
	protected double rho;
	protected double theta;
	protected double z;
	
	/**
	 * Initialize vector to (0,0,0)
	 */
	public CylindarVec() {
		this.rho = ZERO;
		this.theta = ZERO;
		this.z = ZERO;
	}
	
	public CylindarVec(double rho, double theta, double z)
	{
		this.rho = rho;
		this.theta = theta;
		this.z = z;
	}
	
	public CylindarVec(Vec cartesianVec)
	{
		this.rho = Math.hypot(cartesianVec.x, cartesianVec.y);
		this.theta = Math.toRadians(Math.atan(cartesianVec.x / cartesianVec.y));
		this.z = cartesianVec.z;
	}
	
	public double getDail()
	{
		return this.rho;
	}
	
	public double getAngel()
	{
		return this.theta;
	}
	
	public double getZ()
	{
		return this.z;
	}
	
	public Vec toCartesian()
	{
		Vec retVal = new Vec();
		
		retVal.x = this.rho * Math.cos(this.theta);
		retVal.y = this.rho * Math.sin(this.theta);
		retVal.z = this.z;
		
		return retVal;
	}
	
	/**
	 * Returns a string that contains the values of this vector. The form is
	 * (x,y,z).
	 * 
	 * @return the String representation
	 */
	public String toString() {
		return String.format("(d%, d%, d%)", this.rho, this.theta, this.z);
	}
}
