// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package profiler;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class Result<Task>
{
	// task that has been performed
	private Task _task;
	// size of KB in memory after task
	private DescriptiveStatistics _memory;
	// time taken to complete the task
	private DescriptiveStatistics _time;

	public Result(final Task task, final double time)
	{
		this(task, -1, time);
	}

	public Result(final Task task, final double memory, final double time)
	{
		this._task = task;
		this._memory = new DescriptiveStatistics();
		this._memory.addValue(memory);
		this._time = new DescriptiveStatistics();
		this._time.addValue(time);
	}

	public void addIteration(final double memory, final double time)
	{
		this._memory.addValue(memory);
		this._time.addValue(time);
	}

	public void addIteration(final Result<Task> other)
	{
		if (!_task.equals(other._task))
			throw new IllegalArgumentException(_task + " != " + other._task);

		for (final double mem : other.getMemory().getValues())
			_memory.addValue(mem);
		for (final double t : other.getTime().getValues())
			_time.addValue(t);
	}

	public int getIterations()
	{
		return (int) _memory.getN();
	}

	public double getAvgMemory()
	{
		return _memory.getMean();
	}

	public Task getTask()
	{
		return _task;
	}

	public double getAvgTime()
	{
		return _time.getMean();
	}

	public DescriptiveStatistics getTime()
	{
		return _time;
	}

	public DescriptiveStatistics getMemory()
	{
		return _memory;
	}
}
