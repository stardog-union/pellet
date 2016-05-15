package jjtraveler;

/**
 * Counter of the number of nested occurrences of a construct recognized by the
 * argument visitor on a single given path.
 *
 * A typical example of its usage is for counting the maximum nesting level of
 * if-statements in a program.
 *
 * @author Arie van Deursen, CWI
 */
public class NestingDepth<T extends Visitable> implements Visitor<T>, Cloneable
{

	Visitor<T> nestingRecognizer;

	Visitor<T> goOnWhileSuccess = new Identity<>();

	int nestingLevel = 0;

	int maxNestingDepth = 0;

	/**
	 * Create a nesting counter given the recognizer argument. The recognizer
	 * fails at all nodes, except for the ones recognized, at which it succeeds.
	 */
	public NestingDepth(final Visitor<T> nestingRecognizer, final Visitor<T> goOn)
	{
		this.nestingRecognizer = nestingRecognizer;
		goOnWhileSuccess = goOn;
	}

	/**
	 * Create a nesting counter given the recognizer argument.
	 */
	public NestingDepth(final Visitor<T> nestingRecognizer)
	{
		this.nestingRecognizer = nestingRecognizer;
	}

	/**
	 * Restart a visitor after having recognized a relevant construct.
	 */
	private NestingDepth<T> restart()
	{
		@SuppressWarnings("unchecked")
		final NestingDepth<T> nextDepth = (NestingDepth<T>) clone();
		nextDepth.maxNestingDepth = Math.max(maxNestingDepth, nestingLevel + 1);
		nextDepth.nestingLevel++;
		return nextDepth;
	}

	@Override
	public Object clone()
	{
		final NestingDepth<T> theClone = new NestingDepth<>(nestingRecognizer, goOnWhileSuccess);
		theClone.nestingLevel = nestingLevel;
		theClone.maxNestingDepth = maxNestingDepth;
		return theClone;
	}

	private NestingDepth<T> apply(final T x)
	{
		(new GuaranteeSuccess<>(new All<>(this))).visit(x);
		return this;
	}

	/**
	 * Return the maximum nesting depth found.
	 */
	public int getDepth()
	{
		return maxNestingDepth;
	}

	/**
	 * Apply the nesting depth counter to a given visitable.
	 */
	@Override
	public T visit(final T x)
	{
		if (countingShouldContinue(x))
			if (isNestingConstruct(x))
				maxNestingDepth = restart().apply(x).getDepth();
			else
				apply(x);
		return x;
	}

	protected boolean countingShouldContinue(final T x)
	{
		boolean goOn = false;
		try
		{
			goOnWhileSuccess.visit(x);
			goOn = true;
		}
		catch (final VisitFailure stopNow)
		{
			goOn = false;
		}
		return goOn;
	}

	protected boolean isNestingConstruct(final T x)
	{
		boolean isNesting = false;
		try
		{
			nestingRecognizer.visit(x);
			isNesting = true;
		}
		catch (final VisitFailure noNestingConstructFound)
		{
			isNesting = false;
		}
		return isNesting;
	}
}
