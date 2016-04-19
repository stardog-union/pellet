package profiler.statistical;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import profiler.ProfileKB.Task;

/**
 * Some utilities to deal with release performance results.
 *
 * @author Pedro Oliveira <pedro@clarkparsia.com>
 */
public class ReleaseUtils
{

	/**
	 * Read release performance results from file
	 *
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static Release readFromFile(final String filename) throws Exception
	{
		System.out.println("Reading file: " + filename);
		final Release release;
		try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
		{
			release = new Release(reader.readLine(), Long.parseLong(reader.readLine()));

			final Task[] tasks = Task.values();

			String line;
			while ((line = reader.readLine()) != null)
			{
				final String[] values = line.split(";");

				final String name = values[0];
				final List<ReleaseStatistics> statistics = new ArrayList<>();

				for (int i = 1, nTasks = 0; i + 5 < values.length && nTasks < tasks.length; nTasks++, i += 6)
				{
					final ReleaseStatistics result = new ReleaseStatistics(tasks[nTasks]);

					result.addMemStat("avg", Double.parseDouble(values[i]));
					result.addMemStat("var", Double.parseDouble(values[i + 1]));
					result.addMemStat("n", Double.parseDouble(values[i + 2]));

					result.addTimeStat("avg", Double.parseDouble(values[i + 3]));
					result.addTimeStat("var", Double.parseDouble(values[i + 4]));
					result.addTimeStat("n", Double.parseDouble(values[i + 5]));

					statistics.add(result);
				}

				release.addStatistics(name, statistics);
			}
		}
		return release;
	}

	/**
	 * Write release performance results to file
	 *
	 * @param release
	 * @param filename
	 * @throws Exception
	 */
	public static void writeToFile(final Release release, final String filename) throws Exception
	{
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(filename)))
		{
			writer.write(release.getVersion());
			writer.newLine();
			writer.write(release.getReleaseDate().toString());
			writer.newLine();

			for (final Entry<String, List<ReleaseStatistics>> statistics : release.getAllStatistics().entrySet())
			{
				writer.write(statistics.getKey() + ";");
				for (final ReleaseStatistics stat : statistics.getValue())
				{
					writer.write(stat.getMemStat("avg") + ";" + stat.getMemStat("var") + ";" + stat.getMemStat("n") + ";");
					writer.write(stat.getTimeStat("avg") + ";" + stat.getTimeStat("var") + ";" + stat.getTimeStat("n") + ";");
				}
				writer.newLine();
			}
		}
	}

}
