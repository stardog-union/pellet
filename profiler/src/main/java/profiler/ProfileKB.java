// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package profiler;

import static profiler.ProfileUtils.error;

import com.clarkparsia.pellet.owlapiv3.OWLAPILoader;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.mindswap.pellet.KBLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.FileUtils;
import org.mindswap.pellet.utils.MemUtils;
import org.mindswap.pellet.utils.VersionInfo;
import profiler.utils.IObjectProfileNode;
import profiler.utils.ObjectProfiler;

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
public class ProfileKB
{
	public enum LoaderType
	{
		JENA, OWLAPI
	}

	public enum MemoryProfiling
	{
		APPROX, ALL_SIZE, ALL_VERBOSE, KB_SIZE, KB_VERBOSE, NONE
	}

	public enum Task
	{
		Parse(false), Load(false), Consistency(true), Classify(false), Realize(true);

		private boolean requiresInstances;

		Task(boolean requiresInstances)
		{
			this.requiresInstances = requiresInstances;
		}

		boolean requiresInstances()
		{
			return requiresInstances;
		}
	}

	public static void main(String[] args) throws Exception
	{
		new ProfileKB().run(args);
	}

	public static List<String> readConfigFile(String configFile) throws IOException
	{
		final List<String> datasets = new ArrayList<String>();

		try (final BufferedReader in = new BufferedReader(new FileReader(configFile)))
		{
			String line = null;

			while ((line = in.readLine()) != null && line.length() > 0)
			{
				datasets.add(line);
			}
		}

		return datasets;
	}

	private double memPercentageLimit = 0.05;

	private int iterations = 1;

	private MemoryProfiling memoryProfiling = MemoryProfiling.APPROX;

	private Task task = Task.Consistency;

	private LoaderType loaderType = LoaderType.JENA;

	private boolean imports = true;

	private final PrintWriter out = new PrintWriter(System.out);

	public ProfileKB()
	{
	}

	public void setMemoryProfiling(MemoryProfiling memoryProfiling)
	{
		this.memoryProfiling = memoryProfiling;
	}

	public void setTask(Task task)
	{
		this.task = task;
	}

	public void setLoaderType(LoaderType loaderType)
	{
		this.loaderType = loaderType;
	}

	public List<String> parseArgs(String[] args) throws Exception
	{
		List<String> datasets = null;

		final LongOpt[] longopts = new LongOpt[9];
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("memory", LongOpt.REQUIRED_ARGUMENT, null, 'm');
		longopts[2] = new LongOpt("task", LongOpt.REQUIRED_ARGUMENT, null, 't');
		longopts[3] = new LongOpt("percentage", LongOpt.REQUIRED_ARGUMENT, null, 'p');
		longopts[4] = new LongOpt("repeat", LongOpt.REQUIRED_ARGUMENT, null, 'r');
		longopts[5] = new LongOpt("ontology", LongOpt.REQUIRED_ARGUMENT, null, 'o');
		longopts[6] = new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f');
		longopts[7] = new LongOpt("loader", LongOpt.REQUIRED_ARGUMENT, null, 'l');
		longopts[8] = new LongOpt("imports", LongOpt.REQUIRED_ARGUMENT, null, 'i');

		final Getopt g = new Getopt(ProfileKB.class.toString(), args, "hm:t:p:r:o:f:l:i:", longopts);

		try
		{
			int c;
			while ((c = g.getopt()) != -1)
			{
				switch (c)
				{
					case 'h':
						System.exit(0);

					case 'l':
						final String interfaceName = g.getOptarg().toUpperCase();
						try
						{
							loaderType = LoaderType.valueOf(interfaceName);
						}
						catch (final IllegalArgumentException e)
						{
							error("Task " + interfaceName + " is not one of " + Arrays.toString(LoaderType.values()));
						}
						break;

					case 't':
						final String taskName = g.getOptarg();
						try
						{
							task = Task.valueOf(taskName);
						}
						catch (final IllegalArgumentException e)
						{
							error("Task " + taskName + " is not one of " + Arrays.toString(Task.values()));
						}
						break;

					case 'p':
						memPercentageLimit = Double.parseDouble(g.getOptarg());
						break;

					case 'm':
						final String s = g.getOptarg();
						try
						{
							memoryProfiling = MemoryProfiling.valueOf(s.toUpperCase());
						}
						catch (final IllegalArgumentException e)
						{
							error("Memory profiling " + s + " is not one of " + Arrays.toString(MemoryProfiling.values()));
						}
						break;

					case 'r':
						iterations = Integer.parseInt(g.getOptarg());
						break;

					case 'f':
						final String configFile = g.getOptarg();
						datasets = readConfigFile(configFile);
						break;

					case 'o':
						final String ontology = g.getOptarg();
						datasets = Arrays.asList(ontology);
						break;

					case 'i':
						imports = Boolean.parseBoolean(g.getOptarg());
						break;

					case '?':
						error("The option '" + (char) g.getOptopt() + "' is not valid");

					default:
						error("Unrecognized option: " + (char) c);
				}
			}
		}
		catch (final NumberFormatException e)
		{
			error("Invalid number: " + e);
		}

		if (datasets == null)
		{
			error("No config file (-f) or input ontology (-o) provided!");
		}

		return datasets;
	}

	private void print(final IObjectProfileNode node)
	{
		final StringBuilder sb = new StringBuilder();

		for (int p = 0, pLimit = node.pathlength(); p < pLimit; ++p)
			sb.append("  ");

		final IObjectProfileNode root = node.root();
		final IObjectProfileNode[] children = node.children();

		final double sizeInMB = ProfileUtils.mb(node.size());

		sb.append(String.format("%.2f", sizeInMB));
		if (node != root) // root node is always 100% of the overall size
		{
			final double percent = (double) node.size() / root.size();

			if (percent <= memPercentageLimit)
				return;

			sb.append(" (");
			sb.append(String.format("%2.1f%%", 100 * percent));
			sb.append(")");
		}

		sb.append(" -> ");

		String name = node.name();
		final int lastDot = name.lastIndexOf('.');
		if (lastDot >= 0)
			name = name.substring(lastDot + 1);

		sb.append(name);

		if (node.object() != null) // skip shell pseudo-nodes
		{
			if (node.name().endsWith("#table") || node.name().endsWith("#elementData"))
			{
				IObjectProfileNode shell = null;
				final int n = children.length - 1;
				for (int i = n; i >= 0; i--)
				{
					shell = children[i];
					if (shell.object() == null)
						break;
				}
				if (shell != null)
				{
					final int size = node.size() - shell.size();
					final double avg = (double) size / n;

					sb.append(" children: " + n + " avg: " + avg + " " + shell.name() + " " + ProfileUtils.mb(shell.size()));
				}
			}
			else
			{
				sb.append(" : ");
				sb.append(ObjectProfiler.typeName(node.object().getClass(), true));

				if (node.refcount() > 1) // show refcount only when it's > 1
				{
					sb.append(", refcount=");
					sb.append(node.refcount());
				}
			}
		}

		out.println(sb);
		out.flush();

		for (final IObjectProfileNode child : children)
		{
			print(child);
		}
	}

	private double printProfile(KnowledgeBase kb, KBLoader loader, String header)
	{
		long mem = 0;

		Object obj = loader;
		switch (memoryProfiling)
		{
			case NONE:
				break;
			case KB_SIZE:
				obj = kb;
				// Fall through
			case ALL_SIZE:
				System.out.println(header);
				mem = ObjectProfiler.sizeof(obj, ATermUtils.getFactory());
				MemUtils.printMemory("Size: ", mem);
				break;
			case KB_VERBOSE:
				obj = kb;
				// Fall through
			case ALL_VERBOSE:
				System.out.println(header);
				final IObjectProfileNode profile = ObjectProfiler.profile(obj);
				print(profile);
				mem = profile.size();
				for (final IObjectProfileNode node : profile.children())
				{
					if (node.object() != null && node.object().equals(ATermUtils.getFactory()))
					{
						mem -= node.size();
						break;
					}
				}
				break;
			case APPROX:
				System.out.println(header);
				MemUtils.printMemory("Total: ", MemUtils.totalMemory());
				MemUtils.printMemory("Free : ", MemUtils.freeMemory());
				MemUtils.printMemory("Used*: ", MemUtils.totalMemory() - MemUtils.freeMemory());
				MemUtils.runGC();
				mem = MemUtils.usedMemory();
				MemUtils.printMemory("Used : ", mem);
				break;
		}

		System.out.println();

		return ProfileUtils.mb(mem);
	}

	public Collection<Result<Task>> profile(String... files)
	{
		final KBLoader loader = (loaderType == LoaderType.JENA) ? new JenaLoader() : new OWLAPILoader();

		loader.setIgnoreImports(!imports);

		final KnowledgeBase kb = loader.getKB();

		final List<Result<Task>> results = new ArrayList<Result<Task>>();

		for (int i = 0; i <= task.ordinal(); i++)
		{
			final Task task = Task.values()[i];

			final long start = System.currentTimeMillis();

			switch (task)
			{
				case Parse:
					loader.parse(FileUtils.getFileURIsFromRegex(files).toArray(new String[0]));
					break;

				case Load:
					loader.load();
					ProfileUtils.printCounts(kb);
					break;

				case Consistency:
					kb.isConsistent();
					ProfileUtils.printCounts(kb.getABox());
					break;

				case Classify:
					kb.classify();
					break;

				case Realize:
					kb.realize();
					break;

				default:
					throw new AssertionError("This task does not exist: " + task);
			}

			final double time = (System.currentTimeMillis() - start) / 1000.0;
			final double mem = task.requiresInstances() && kb.getABox().isEmpty() ? results.get(results.size() - 1).getAvgMemory() : printProfile(kb, loader, "After " + task);
			results.add(new Result<Task>(task, mem, time));
		}
		kb.timers.print();
		try
		{
			//System.in.read();
		}
		catch (final Exception e)
		{

		}

		loader.clear();

		return results;
	}

	public void run(String[] args)
	{
		try
		{
			final List<String> datasets = parseArgs(args);

			final int colCount = memoryProfiling == MemoryProfiling.NONE ? 1 : 2;
			final int colWidth = 8;
			final ResultList<Task> results = new ResultList<Task>(colCount, colWidth);
			for (int i = 0; i < iterations; i++)
			{
				System.out.println("\n\n\nITERATION: " + (i + 1) + "\n\n\n");

				for (final String dataset : datasets)
				{
					try
					{
						final String[] files = dataset.split(" ");
						final String name = files[0];

						final Collection<Result<Task>> currResults = profile(files);

						results.addResult(name, currResults);

						System.out.println("\n\n\nRESULT " + (i + 1) + ":");
						System.out.println("Version: " + VersionInfo.getInstance().getVersionString());
						results.print();
					}
					catch (final RuntimeException e)
					{
						e.printStackTrace();
					}
				}

				//				MemUtils.runGC();
			}
		}
		catch (final Throwable t)
		{
			t.printStackTrace();
		}
	}
}
