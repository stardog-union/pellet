package profiler.statistical;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import profiler.ProfileKB.Task;
import profiler.Result;

/**
 * Provides some statistics about the performance of a certain task
 * 
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 */
public class ReleaseStatistics
{

	private final Task _task;
	private final Map<String, Double> _memStats;
	private final Map<String, Double> _timeStats;

	public ReleaseStatistics(final Task task)
	{
		this._task = task;
		_memStats = new LinkedHashMap<>();
		_timeStats = new LinkedHashMap<>();
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
		return _task;
	}

	public double getMemStat(final String name)
	{
		return _memStats.get(name);
	}

	public double getTimeStat(final String name)
	{
		return _timeStats.get(name);
	}

	public Map<String, Double> getMemStats()
	{
		return _memStats;
	}

	public Map<String, Double> getTimeStats()
	{
		return _timeStats;
	}

	public void addMemStat(final String name, final double value)
	{
		_memStats.put(name, value);
	}

	public void addTimeStat(final String name, final double value)
	{
		_timeStats.put(name, value);
	}

}
