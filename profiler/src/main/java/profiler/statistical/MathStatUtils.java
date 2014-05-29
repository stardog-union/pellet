package profiler.statistical;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Some statistical utilities based on the Apache Commons Math package
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 *
 */
public class MathStatUtils extends TTestImpl{
	
	private TDistribution distribution;

	public MathStatUtils(){
		distribution = new TDistributionImpl(1);
	}	

	/**
	 * Performs a two-sided t-test evaluating the null hypothesis that two samples are drawn from populations with the same mean, with significance level alpha.
	 * @param m1
	 * @param m2
	 * @param v1
	 * @param v2
	 * @param n1
	 * @param n2
	 * @param alpha
	 * @return
	 * @throws MathException
	 */
	public boolean tTest(double m1, double m2, double v1, double v2, double n1, double n2, double alpha) throws MathException{
		checkSignificanceLevel(alpha);
		return (tTest(m1, m2,v1, v2,n1, n2) < alpha);
	}

	/**
	 * Computes p-value for 2-sided, 1-sample t-test
	 * @param m
	 * @param mu
	 * @param v
	 * @param n
	 * @param alpha
	 * @return
	 * @throws MathException
	 */
	public boolean tTest(double m, double mu, double v, double n, double alpha) throws MathException{
		checkSignificanceLevel(alpha);
		return (tTest(m,mu,v,n) < alpha);
	}

	/**
	 * 1-sample t-test confidence interval
	 * @param sample
	 * @param alpha
	 * @return
	 * @throws MathException
	 */
	public double[] confidenceInterval(double[] sample, double alpha) throws MathException{
		return confidenceInterval(StatUtils.mean(sample), StatUtils.variance(sample), sample.length, alpha);
	}

	/**
	 * 1-sample t-test confidence interval
	 * @param m
	 * @param v
	 * @param n
	 * @param alpha
	 * @return
	 * @throws MathException
	 */
	public double[] confidenceInterval(double m, double v, double n, double alpha) throws MathException {
		checkSignificanceLevel(alpha);
		distribution.setDegreesOfFreedom(n-1);
		double t = Math.abs(distribution.inverseCumulativeProbability(alpha/2));
		double val = t * Math.sqrt(v / n);
		return new double[]{m-val,m+val};
	}

	private void checkSignificanceLevel(final double alpha) throws IllegalArgumentException {
		if ((alpha <= 0) || (alpha > 0.5)) {
			throw MathRuntimeException.createIllegalArgumentException("out of bounds significance level {0}, must be between {1} and {2}",alpha, 0.0, 0.5);
		}
	}
}
