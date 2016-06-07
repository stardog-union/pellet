// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.katk.tools.Log;
import org.mindswap.pellet.KRSSLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.TimeoutException;
import org.mindswap.pellet.output.TableData;
import org.mindswap.pellet.utils.AlphaNumericComparator;
import org.mindswap.pellet.utils.PatternFilter;
import org.mindswap.pellet.utils.Timer;

/**
 * Parse and test the cases from DL benchmark suite. This class provides parsing for KRSS files.
 *
 * @author Evren Sirin
 */
public class DLBenchmarkTest
{
	public static Logger _logger = Log.getLogger(DLBenchmarkTest.class);

	public static boolean PRINT_TIME = false;
	public static boolean PRINT_TREE = false;

	// time limits for different kind of tests
	public static int SAT_LIMIT = 10;
	public static int TBOX_LIMIT = 20;
	public static int ABOX_LIMIT = 50;

	public static boolean FAST = false;
	public static boolean FORCE_UPPERCASE = true;

	private final KRSSLoader _loader;
	private KnowledgeBase _kb;

	public DLBenchmarkTest()
	{
		_loader = new KRSSLoader();
		_loader.setForceUppercase(FORCE_UPPERCASE);
	}

	public KnowledgeBase getKB()
	{
		return _kb;
	}

	public KnowledgeBase initKB(final long timeout)
	{
		final KnowledgeBase kb = new KnowledgeBase();
		kb.setTimeout(timeout * 1000);
		return kb;
	}

	public void doAllTBoxTests(final String dirName)
	{
		doAllTBoxTests(dirName, new PatternFilter("*.akb"));
	}

	public void doAllTBoxTests(final String dirName, final FileFilter filter)
	{
		final File dir = new File(dirName);
		final File[] files = dir.listFiles(filter);
		Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);

		final TableData table = new TableData(Arrays.asList(new String[] { "Name", "Size", "Time" }));
		for (int i = 0; i < files.length; i++)
		{
			System.out.print((i + 1) + ") ");

			final List<Object> data = new ArrayList<>();
			data.add(files[i]); // Adding a File (Name)
			try
			{
				doTBoxTest(files[i].toString());
				data.add(Integer.valueOf(_kb.getClasses().size())); // Adding an Integer. (Size)
				data.add(_kb.timers.getTimer("test").getTotal() + ""); // Adding a String. (Time)
			}
			catch (final Exception | OutOfMemoryError | StackOverflowError e)
			{
				Log.error(_logger, e);
			}
			catch (final Error e)
			{
				e.printStackTrace(System.err);
			}
			table.add(data);
		}

		System.out.print(table);
	}

	public boolean doTBoxTest(final String fileParam) throws Exception
	{
		String file = fileParam;
		String ext = ".tkb";
		int index = file.lastIndexOf('.');
		if (index != -1)
		{
			ext = file.substring(index);
			file = file.substring(0, index);
		}
		index = file.lastIndexOf(File.separator);
		final String displayName = (index == -1) ? file : file.substring(index + 1);

		if (_logger.isLoggable(Level.INFO))
			System.out.print(displayName + " ");

		_loader.clear();
		_loader.getKB().timers.resetAll();
		_kb = _loader.createKB(file + ext);
		_kb.setTimeout(TBOX_LIMIT * 1000);

		final Timer t = _kb.timers.startTimer("test");

		if (_logger.isLoggable(Level.INFO))
			System.out.print("preparing...");

		_kb.prepare();

		if (_logger.isLoggable(Level.INFO))
			System.out.print("classifying...");

		_kb.classify();

		t.stop();

		if (PRINT_TREE)
			_kb.printClassTree();

		if (_logger.isLoggable(Level.INFO))
			System.out.print("verifying...");

		_loader.verifyTBox(file + ".tree", _kb);

		if (_logger.isLoggable(Level.INFO))
			System.out.print("done");

		if (_logger.isLoggable(Level.INFO))
		{
			System.out.print(" Prepare " + _kb.timers.getTimer("preprocessing").getTotal());
			System.out.print(" Classify " + _kb.timers.getTimer("classify").getTotal());

			System.out.println(" " + t.getTotal());
		}

		if (PRINT_TIME)
			_kb.timers.print();

		return true;
	}

	public void doAllSatTests(final String dirName)
	{
		final File dir = new File(dirName);
		final String[] files = dir.list();

		for (int i = 0; i < files.length; i++)
		{
			System.out.print((i + 1) + ") " + files[i] + " ");

			try
			{
				final int count = doSatTest(dirName + files[i]);
				System.out.println(count);
			}
			catch (final TimeoutException e)
			{
				System.out.println(" ** Timeout ** ");
				System.out.println();
			}
			catch (final Exception e)
			{
				e.printStackTrace(System.err);
				System.out.println();
			}
			catch (final OutOfMemoryError e)
			{
				System.out.println(" ** Out of Memory ** ");
				System.out.println();
			}
			catch (final Error e)
			{
				e.printStackTrace(System.err);
			}
		}
	}

	// TODO : check why is this disable.
	public int doSatTest(@SuppressWarnings("unused") final String file)
	{
		final int count = 0;

		//		System.err.println("Sat test currently disabled!");
		//
		//		final StreamTokenizer in = initTokenizer(file);
		//
		//		final boolean result = file.endsWith("n.alc");
		//
		//		for (; count < 21; count++)
		//		{
		//			_kb = initKB(SAT_LIMIT);
		//
		//			final ATermAppl c = parseExpr(in);
		//
		//			long time = System.currentTimeMillis();
		//			final boolean sat = _kb.isSatisfiable(c);
		//			time = System.currentTimeMillis() - time;
		//
		//			if (sat != result)
		//				throw new RuntimeException("Consistency error");
		//			else
		//				System.out.print("(" + (count + 1) + ":" + time + ")");
		//		}

		return count;
	}

	public boolean doABoxTest(final String fileParam) throws Exception
	{
		String file = fileParam;
		String ext = ".tkb";
		int index = file.lastIndexOf('.');
		if (index != -1)
		{
			ext = file.substring(index);
			file = file.substring(0, index);
		}
		index = file.lastIndexOf(File.separator);
		final String displayName = (index == -1) ? file : file.substring(index + 1);
		System.out.print(displayName + " ");

		_kb = _loader.createKB(file + ext);
		_kb.timers.resetAll();
		_kb.setTimeout(ABOX_LIMIT * 1000);

		final Timer t = _kb.timers.startTimer("test");

		System.out.print("preparing...");

		_kb.prepare();

		if (!FAST)
		{
			System.out.print("classifying...");
			_kb.realize();
		}

		t.stop();

		System.out.print("verifying...");
		_loader.verifyABox(file + ".query", _kb);

		System.out.print("done");

		System.out.print(" Prepare " + _kb.timers.getTimer("preprocessing").getTotal());
		System.out.print(" Classify " + _kb.timers.getTimer("classify").getTotal());

		System.out.println(" " + t.getTotal());

		if (PRINT_TIME)
			_kb.timers.print();

		return true;
	}

	public void doAllABoxTests(final String dirName)
	{
		doAllABoxTests(dirName, "*.akb");
	}

	public void doAllABoxTests(final String dirName, final String pattern)
	{
		final File dir = new File(dirName);
		final File[] files = dir.listFiles(new PatternFilter(pattern));
		Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);

		for (int i = 0; i < files.length; i++)
		{
			System.out.print((i + 1) + ") ");
			try
			{
				doABoxTest(files[i].getAbsolutePath());
			}
			catch (final TimeoutException e)
			{
				System.out.println(" ***** Timeout ***** ");
				System.out.println();
			}
			catch (final Exception e)
			{
				e.printStackTrace(System.err);
				System.out.println();
			}
			catch (final OutOfMemoryError e)
			{
				System.out.println(" ***** Out of Memory ***** ");
				System.out.println();
			}
			catch (final Error e)
			{
				e.printStackTrace(System.err);
			}
		}
	}

	public static void usage()
	{
		System.out.println("DLTest - Run the tests in DL-benchmark suite");
		System.out.println("");
		System.out.println("Usage: java DLTest [-timing] <input> <type>");
		System.out.println("   input    A single file or a directory that contains");
		System.out.println("            a set of test files");
		System.out.println("   type     Type of the test, one of [sat, tbox, abox]");
	}

	public final static void main(final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			usage();
			return;
		}

		int base = 0;
		if (args[0].equals("-timing"))
		{
			DLBenchmarkTest.PRINT_TIME = true;
			base = 1;
		}
		else
			if (args.length != 2)
			{
				System.out.println("Invalid arguments");
				usage();
				return;
			}

		final String in = args[base + 0];
		final String type = args[base + 1];

		final File file = new File(in);

		if (!file.exists())
			throw new FileNotFoundException(file + " does not exist!");

		final boolean singleTest = file.isFile();

		final DLBenchmarkTest test = new DLBenchmarkTest();
		if (type.equals("sat"))
		{
			if (singleTest)
				test.doSatTest(in);
			else
				test.doAllSatTests(in);
		}
		else
			if (type.equals("tbox"))
			{
				if (singleTest)
					test.doTBoxTest(in);
				else
					test.doAllTBoxTests(in);
			}
			else
				if (type.equals("abox"))
				{
					if (singleTest)
						test.doABoxTest(in);
					else
						test.doAllABoxTests(in);
				}
				else
					usage();
	}
}
