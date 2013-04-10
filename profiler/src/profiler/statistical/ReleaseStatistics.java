package profiler.statistical;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import profiler.Result;
import profiler.ProfileKB.Task;

/**
 * Provides some statistics about the performance of a certain task
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 *
 */
public class ReleaseStatistics {

	private Task task;
	private Map<String, Double> memStats;
	private Map<String, Double> timeStats;

	public ReleaseStatistics(Task task)
	{		
		this.task = task;
		memStats = new LinkedHashMap<String, Double>();
		timeStats = new LinkedHashMap<String, Double>();
	}

	public ReleaseStatistics(Result<Task> task)
	{
		this(task.getTask());
		
		DescriptiveStatistics mem = task.getMemory();
		addMemStat("avg",mem.getMean());
		addMemStat("var",mem.getVariance());
		addMemStat("n",mem.getN());
		
		DescriptiveStatistics time = task.getTime();
		addTimeStat("avg", time.getMean());
		addTimeStat("var", mem.getVariance());
		addTimeStat("n", mem.getN());
	}

	public Task getTask()
	{
		return task;
	}

	public double getMemStat(String name)
	{
		return memStats.get(name);
	}

	public double getTimeStat(String name)
	{
		return timeStats.get(name);
	}

	public Map<String, Double> getMemStats() {
		return memStats;
	}

	public Map<String, Double> getTimeStats() {
		return timeStats;
	}

	public void addMemStat(String name, double value)
	{
		memStats.put(name, value);
	}

	public void addTimeStat(String name, double value)
	{
		timeStats.put(name, value);
	}

}
