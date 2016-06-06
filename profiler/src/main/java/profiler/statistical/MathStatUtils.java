package profiler.statistical;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathRuntimeException;
import org.apache.commons.math3.exception.util.DummyLocalizable;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.TTest;

/**
 * Some statistical utilities based on the Apache Commons Math package
 * 
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 */
public class MathStatUtils extends TTest
{
	private final TDistribution _distribution = new TDistribution(1);

	/**
	 * Performs a two-sided t-test evaluating the null hypothesis that two samples are drawn from populations with the same mean, with significance level alpha.
	 */
	public boolean tTest(final double m1, final double m2, final double v1, final double v2, final double n1, final double n2, final double alpha)
	{
		checkSignificanceLevel(alpha);
		return (tTest(m1, m2, v1, v2, n1, n2) < alpha);
	}

	/**
	 * Computes p-value for 2-sided, 1-sample t-test
	 */
	public boolean tTest(final double m, final double mu, final double v, final double n, final double alpha)
	{
		checkSignificanceLevel(alpha);
		return (tTest(m, mu, v, n) < alpha);
	}

	/**
	 * 1-sample t-test confidence interval
	 */
	public double[] confidenceInterval(final double[] sample, final double alpha)
	{
		return confidenceInterval(StatUtils.mean(sample), StatUtils.variance(sample), sample.length, alpha);
	}

	/**
	 * 1-sample t-test confidence interval
	 */
	public double[] confidenceInterval(final double m, final double v, final double n, final double alpha)
	{
		checkSignificanceLevel(alpha);
		//_distribution.setDegreesOfFreedom(n - 1);
		final double t = Math.abs(_distribution.inverseCumulativeProbability(alpha / 2));
		final double val = t * Math.sqrt(v / n);
		return new double[] { m - val, m + val };
	}

	private void checkSignificanceLevel(final double alpha) throws IllegalArgumentException
	{
		if ((alpha <= 0) || (alpha > 0.5))
			throw new MathRuntimeException(new DummyLocalizable("out of bounds significance level {0}, must be between {1} and {2}"), alpha, 0.0, 0.5);
	}
}
