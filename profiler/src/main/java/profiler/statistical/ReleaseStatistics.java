package profiler.statistical;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import profiler.ProfileKB.Task;
import profiler.Result;

/**
 * Provides some statistics about the performance of a certain task
 * 
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 */
public class ReleaseStatistics
{

	private final Task task;
	private final Map<String, Double> memStats;
	private final Map<String, Double> timeStats;

	public ReleaseStatistics(final Task task)
	{
		this.task = task;
		memStats = new LinkedHashMap<>();
		timeStats = new LinkedHashMap<>();
	}

	public ReleaseStatistics(final Result<Task> task)
	{
		this(task.getTask());

		final DescriptiveStatistics mem = task.getMemory();
		addMemStat("avg", mem.getMean());
		addMemStat("var", mem.getVariance());
		addMemStat("n", mem.getN());

		final DescriptiveStatistics time = task.getTime();
		addTimeStat("avg", time.getMean());
		addTimeStat("var", mem.getVariance());
		addTimeStat("n", mem.getN());
	}

	public Task getTask()
	{
		return task;
	}

	public double getMemStat(final String name)
	{
		return memStats.get(name);
	}

	public double getTimeStat(final String name)
	{
		return timeStats.get(name);
	}

	public Map<String, Double> getMemStats()
	{
		return memStats;
	}

	public Map<String, Double> getTimeStats()
	{
		return timeStats;
	}

	public void addMemStat(final String name, final double value)
	{
		memStats.put(name, value);
	}

	public void addTimeStat(final String name, final double value)
	{
		timeStats.put(name, value);
	}

}
