package com.clarkparsia.owlwg.testcase.filter;

import com.clarkparsia.owlwg.testcase.Status;
import com.clarkparsia.owlwg.testcase.TestCase;

/**
 * <p>
 * Title: Status Filter Condition
 * </p>
 * <p>
 * Description: Filter _condition to match tests with a particular _status (or no _status).
 * </p>
 * <p>
 * Copyright: Copyright &copy; 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <a href="http://clarkparsia.com/"/>http://clarkparsia.com/</a>
 * </p>
 * 
 * @author Mike Smith &lt;msmith@clarkparsia.com&gt;
 */
public class StatusFilter implements FilterCondition
{

	public final static StatusFilter APPROVED, EXTRACREDIT, NOSTATUS, PROPOSED, REJECTED;

	static
	{
		APPROVED = new StatusFilter(Status.APPROVED);
		EXTRACREDIT = new StatusFilter(Status.EXTRACREDIT);
		NOSTATUS = new StatusFilter(null);
		PROPOSED = new StatusFilter(Status.PROPOSED);
		REJECTED = new StatusFilter(Status.REJECTED);
	}

	final private Status _status;

	/**
	 * @param _status {@link Status} for test case or <code>null</code> if filter should match cases that have no _status
	 */
	public StatusFilter(Status status)
	{
		this._status = status;
	}

	@Override
	public boolean accepts(TestCase<?> testcase)
	{
		return testcase.getStatus() == _status;
	}

	@Override
	public String toString()
	{
		return (_status == null) ? "NO-STATUS" : _status.toString();
	}

}
