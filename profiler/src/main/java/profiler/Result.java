// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package profiler;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public class Result<Task> {
	// task that has been performed
	private Task	task;
	// size of KB in memory after task
	private DescriptiveStatistics	memory;
	// time taken to complete the task
	private DescriptiveStatistics	time;


	public Result(Task task, double time) {
		this( task, -1, time );
	}
	
	public Result(Task task, double memory, double time) {
		this.task = task;
		this.memory = new DescriptiveStatistics();
		this.memory.addValue(memory);
		this.time = new DescriptiveStatistics();
		this.time.addValue(time);
	}

	public void addIteration(double memory, double time) {
		this.memory.addValue(memory);
		this.time.addValue(time);
	}

	public void addIteration(Result<Task> other) {
		if( !task.equals( other.task ) )
			throw new IllegalArgumentException( task + " != " + other.task );

		for(double mem: other.getMemory().getValues())
			memory.addValue(mem);
		for(double t: other.getTime().getValues())
			time.addValue(t);
	}

	public int getIterations() {
		return (int)memory.getN();
	}

	public double getAvgMemory() {
		return memory.getMean();
	}

	public Task getTask() {
		return task;
	}

	public double getAvgTime() {
		return time.getMean();
	}
	
	public DescriptiveStatistics getTime(){
		return time;
	}
	
	public DescriptiveStatistics getMemory(){
		return memory;
	}
}