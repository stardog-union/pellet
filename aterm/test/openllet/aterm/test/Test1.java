/*
 * Copyright (c) 2002-2007, CWI and INRIA
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package openllet.aterm.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import openllet.aterm.AFun;
import openllet.aterm.ATerm;
import openllet.aterm.ATermAppl;
import openllet.aterm.ATermFactory;
import openllet.aterm.ATermInt;
import openllet.aterm.ATermList;
import openllet.aterm.ATermLong;
import openllet.aterm.ATermReal;
import openllet.aterm.ParseError;
import openllet.aterm.pure.PureFactory;
import org.junit.Before;
import org.junit.Test;

public class Test1
{
	private ATermFactory factory;

	@Before
	public void setUp() throws Exception
	{
		factory = new PureFactory();
	}

	public Test1()
	{
	}

	void assertTrue(final boolean condition)
	{
		if (!condition) { throw new AssertionError("assertion failed."); }
	}

	private static void test(final boolean cond, final String id)
	{
		if (cond)
		{
			System.out.println("\ttest " + id + " ok!");
		}
		else
		{
			throw new AssertionError("test " + id + " failed.");
		}
	}

	@Test
	public void testMakeInt()
	{
		final ATermInt[] term = new ATermInt[2];

		term[0] = factory.makeInt(3);
		term[1] = factory.makeInt(3);

		assertTrue(term[0].getType() == ATerm.INT);
		assertTrue(term[0].getInt() == 3);
		assertTrue(term[0] == term[1]);

		assertTrue(term[0].toString().equals("3"));

		List<Object> result = term[0].match("3");
		assertTrue(result != null && result.size() == 0);

		result = term[0].match("<int>");
		assertTrue(result != null && result.size() == 1 && result.get(0).equals(new Integer(3)));

		System.out.println("pass: testMakeInt");
	}

	@Test
	public void testMakeLong()
	{
		final ATermLong[] term = new ATermLong[2];

		term[0] = factory.makeLong(3);
		term[1] = factory.makeLong(3);

		assertTrue(term[0].getType() == ATerm.LONG);
		assertTrue(term[0].getLong() == 3);
		assertTrue(term[0] == term[1]);

		assertTrue(term[0].toString().equals("3"));

		List<Object> result = term[0].match("3L");
		assertTrue(result != null && result.size() == 0);

		result = term[0].match("<long>");
		assertTrue(result != null && result.size() == 1 && result.get(0).equals(new Long(3)));

		System.out.println("pass: testMakeLong");
	}

	@Test
	public void testMakeReal()
	{
		final ATermReal[] term = new ATermReal[2];

		term[0] = factory.makeReal(Math.PI);
		term[1] = factory.makeReal(Math.PI);

		assertTrue(term[0].getType() == ATerm.REAL);
		assertTrue(term[0].getReal() == Math.PI);
		assertTrue(term[0] == term[1]);

		final List<Object> result = term[0].match("<real>");
		assertTrue(result != null && result.size() == 1 && result.get(0).equals(new Double(Math.PI)));

		System.out.println("pass: testMakeReal");
	}

	@Test
	public void testMakeAppl()
	{
		final AFun symmies[] = new AFun[4];
		final ATermAppl apples[] = new ATermAppl[16];

		symmies[0] = factory.makeAFun("f0", 0, false);
		symmies[1] = factory.makeAFun("f1", 1, false);
		symmies[2] = factory.makeAFun("f6", 6, false);
		symmies[3] = factory.makeAFun("f10", 10, false);

		apples[0] = factory.makeAppl(symmies[0]);
		apples[1] = factory.makeAppl(symmies[1], apples[0]);
		apples[2] = factory.makeAppl(symmies[1], apples[1]);
		apples[3] = factory.makeAppl(symmies[1], apples[0]);
		apples[4] = factory.makeAppl(symmies[2], new ATerm[] { apples[0], apples[0], apples[1], apples[0], apples[0], apples[1] });
		apples[5] = factory.makeAppl(symmies[3], new ATerm[] { apples[0], apples[1], apples[0], apples[1], apples[0], apples[1], apples[0], apples[1], apples[0], apples[1] });
		apples[6] = apples[2].setArgument(apples[0], 0);

		assertTrue(apples[6].isEqual(apples[1]));
		assertTrue(apples[1].isEqual(apples[3]));
		assertTrue(!apples[2].isEqual(apples[1]));
		assertTrue(!apples[2].isEqual(apples[6]));
		assertTrue(!apples[1].isEqual(apples[2]));
		assertTrue(!apples[2].isEqual(apples[3]));
		assertTrue(!apples[0].isEqual(apples[1]));

		System.out.println("pass: testMakeAppl");
	}

	@Test
	public void testParser()
	{
		final ATerm[] T = new ATerm[20];
		int index = 0;

		T[index++] = factory.parse("g");
		T[index++] = factory.parse("f()");
		T[index++] = factory.parse("f(1)");
		T[index++] = factory.parse("\"f\"(1)");
		T[index++] = factory.parse("\"subject\"(<str>)");
		T[index++] = factory.parse("f(1,2,<int>)");
		T[index++] = factory.parse("[]");
		T[index++] = factory.parse("[1]");
		T[index++] = factory.parse("[1,2]");
		T[index++] = factory.parse("[1,3.5,4e6,123.21E-3,-12]");
		T[index++] = factory.parse("[1,a,f(1)]");
		T[index++] = factory.parse("(1)");
		T[index++] = factory.parse("[()]");
		T[index++] = factory.parse("[\"f\"()]");
		T[index++] = factory.parse("[1, \"a\", f(1), \"g\"(a,\"b\")]");

		for (int i = 0; i < index; i++)
		{
			System.out.println("term " + i + ": " + T[i]);
		}

	}

	@Test
	public void testParseError()
	{
		try
		{
			factory.parse("f(\"");
		}
		catch (final ParseError e)
		{
			if (!e.getMessage().startsWith("Unterminated quoted function symbol")) { throw e; }
		}
	}

	@Test
	public void testFileParser()
	{
		try
		{
			/*
			FileOutputStream output = new FileOutputStream("testFileParser.txt");
			String s = _factory.parse("f(a,g(b))").toString();
			output.write(s);
			output.close();
			 */
			try (FileInputStream input = new FileInputStream("testFileParser.txt"))
			{
				final ATerm result = factory.readFromTextFile(input);
				System.out.println("result = " + result);
			}
		}
		catch (final FileNotFoundException e1)
		{
			System.out.println(e1);
		}
		catch (final IOException e2)
		{
			System.out.println(e2);
		}
	}

	@Test
	public void testMakeList()
	{
		final ATerm[] T = new ATerm[10];
		final ATermList[] Ts = new ATermList[10];

		//System.out.println("testing ATermList class");
		T[0] = factory.parse("[0,1,2,3,4,5,4,3,2,1]");
		Ts[0] = (ATermList) T[0];
		T[1] = factory.parse("[]");
		Ts[1] = factory.makeList();
		T[2] = factory.parse("[1,2,3]");
		Ts[2] = (ATermList) T[2];
		T[3] = factory.parse("[4,5,6]");
		Ts[3] = (ATermList) T[3];
		T[4] = factory.parse("[1,2,3,4,5,6]");
		Ts[4] = (ATermList) T[4];

		//    T[5] = _factory.parse("[1 , 2 , 3 , 4,5,6,7]");
		T[5] = factory.parse("[1,2,3,4,5,6,7]");
		Ts[5] = (ATermList) T[5];

		//T[6] = _factory.parse("f(abc{[label,val]})");

		// test length
		test(Ts[0].getLength() == 10, "length-1");

		// test search
		test(Ts[0].indexOf(factory.makeInt(2), 0) == 2, "indexOf-1");
		test(Ts[0].indexOf(factory.makeInt(10), 0) == -1, "indexOf-2");
		test(Ts[0].indexOf(factory.makeInt(0), 0) == 0, "indexOf-3");
		test(Ts[0].indexOf(factory.makeInt(5), 0) == 5, "indexOf-4");

		// test lastIndexOf

		test(Ts[0].lastIndexOf(factory.makeInt(1), -1) == 9, "lastIndexOf-1");
		test(Ts[0].lastIndexOf(factory.makeInt(0), -1) == 0, "lastIndexOf-2");
		test(Ts[0].lastIndexOf(factory.makeInt(10), -1) == -1, "lastIndexOf-3");

		// test concat
		test(Ts[2].concat(Ts[3]).equals(Ts[4]), "concat-1");
		test(Ts[0].concat(Ts[1]).equals(Ts[0]), "concat-2");

		// test append
		test(Ts[4].append(factory.makeInt(7)).equals(Ts[5]), "append-1");

		// test insert
		Ts[7] = Ts[3].insert(factory.parse("3"));
		Ts[7] = Ts[7].insert(factory.parse("2"));
		Ts[7] = Ts[7].insert(factory.parse("1"));
		test(Ts[7].equals(Ts[4]), "insert-1");

		test(Ts[1].insert(factory.parse("1")).equals(factory.parse("[1]")), "insert-2");

		test(Ts[4].insertAt(factory.parse("7"), Ts[4].getLength()).equals(Ts[5]), "insert-3");

		// Test prefix/last
		test(Ts[5].getPrefix().equals(Ts[4]), "prefix-1");
		test(Ts[5].getLast().equals(factory.parse("7")), "last-1");

		Ts[8] = factory.makeList();
		Ts[9] = (ATermList) Ts[8].setAnnotations(Ts[8].getAnnotations());
		System.out.println("Ts[8].hash = " + Ts[8].hashCode());
		System.out.println("Ts[9].hash = " + Ts[9].hashCode());
		test(Ts[8].equals(Ts[9]), "empty-1");

		Ts[8] = factory.makeList().getAnnotations();
		Ts[9] = (ATermList) Ts[8].setAnnotations(Ts[8].getAnnotations());
		System.out.println("Ts[8].hash = " + Ts[8].hashCode());
		System.out.println("Ts[9].hash = " + Ts[9].hashCode());
		test(Ts[8].equals(Ts[9]), "empty-2");
		System.out.println("pass: testMakeList");
	}

	@Test
	public void testPatternMatch()
	{
		final ATerm[] T = new ATerm[10];
		T[0] = factory.parse("f(1,2,3)");
		T[1] = factory.parse("[1,2,3]");
		T[2] = factory.parse("f(a,\"abc\",2.3,<abc>)");
		T[3] = factory.parse("f(a,[])");

		test(T[0].match("f(1,2,3)") != null, "match-1a");

		List<Object> result = T[1].match("<term>");
		//System.out.println("result = " + result);
		test(result != null && result.get(0).equals(T[1]), "match-1b");

		result = T[1].match("[<list>]");
		//System.out.println("result = " + result);
		test(result != null && result.get(0).equals(T[1]), "match-1c");

		result = T[1].match("[<int>,<list>]");
		//System.out.println("result = " + result);
		test(result != null && result.get(0).equals(new Integer(1)) && result.get(1).equals(factory.parse("[2,3]")), "match-1d");

		//result = T[1].match("[<list>,2,<int>]");
		//System.out.println("result = " + result);

		result = factory.parse("f(a)").match("f(<term>)");
		//System.out.println("result = " + result);
		test(result != null && result.get(0).equals(factory.parse("a")), "match-2a");

		result = factory.parse("f(a)").match("<term>");
		//System.out.println("result = " + result);
		test(result != null && result.get(0).equals(factory.parse("f(a)")), "match-2b");

		result = factory.parse("f(a)").match("<fun(<term>)>");
		//System.out.println("result = " + result);
		test(result != null && result.get(0).equals("f") && result.get(1).equals(factory.parse("a")), "match-2c");

		result = factory.parse("a").match("<fun>");
		//System.out.println("result = " + result);
		test(result != null && result.get(0).equals("a"), "match-2d");

		//result = _factory.parse("f(<abc>)").match("f(<placeholder>)");
		//System.out.println("result = " + result);
		//test(result != null &&
		// result.get(0).equals(_factory.parse("<abc>")), "match-2e");

		result = T[0].match("f(1,<int>,3)");
		test(result != null && result.size() == 1 && result.get(0).equals(new Integer(2)), "match-3");

		result = T[2].match("f(<term>,<term>,<real>,<placeholder>)");
		//System.out.println("result = " + result); 
		test(result != null && result.size() == 4, "match-4a");

		test(result != null && result.get(0).equals(factory.parse("a")), "match-4b");
		test(result != null && result.get(1).equals(factory.parse("\"abc\"")), "match-4c");
		test(result != null && result.get(2).equals(new Double(2.3)), "match-4d");
		//test(result.get(3).equals(_factory.parse("<abc>")), "match-4e"); 

		result = T[1].match("[<list>]");
		test(result != null && result.size() == 1 && result.get(0).equals(T[1]), "match-6a");

		result = T[1].match("[<int>,<list>]");
		test(result != null && result.size() == 2 && result.get(0).equals(new Integer(1)), "match-6b");
		test(result != null && result.get(1).equals(factory.parse("[2,3]")), "match-6c");

		final ATerm empty = factory.makeList();
		result = empty.match("[]");
		//System.out.println("result = " + result);
		test(result != null && result.size() == 0, "match-6d");

		result = empty.match("[<list>]");
		//System.out.println("result = " + result);
		test(result.get(0).equals(factory.parse("[]")), "match-6e");

		result = T[0].match("<fun(<int>,<list>)>");
		test(result != null && result.size() == 3, "match-7a");
		test(result != null && result.get(0).equals("f"), "match-7b");
		test(result != null && result.get(1).equals(new Integer(1)), "match-7c");
		test(result != null && result.get(2).equals(factory.parse("[2,3]")), "match-7d");

		result = T[3].match("f(<term>,[<list>])");
		test(result != null && result.size() == 2, "match-8a");
		test(result != null && result.get(0).equals(factory.parse("a")), "match-8b");
		test(result != null && result.get(1) != null, "match-8c");
		test(result != null && ((ATermList) result.get(1)).getLength() == 0, "match-8d");

		/*
		result = T[0].match("<f>"); 
		System.out.println("result = " + result);  
		test(result != null && result.size()==1 &&  
		   result.get(0).equals(T[0]), "match-8"); 

		result = T[0].match("<f(1,2,<int>)>");
		System.out.println("result = " + result);  
		test(result != null && result.size() == 2, "match-9a"); 
		test(result.get(0).equals(T[0]), "match9b");  
		test(result.get(1).equals(new Integer(3)), "match-9b");
		 */

		result = factory.parse("fib(suc(suc(suc(suc(suc(suc(suc(suc(suc(suc(zero())))))))))))").match("fib(suc(<term()>))");
		//System.out.println("result = " + result); 

		System.out.println("pass: testPatternMatch");
	}

	@Test
	public void testPatternMake()
	{
		final List<Object> list = new ArrayList<>();
		ATerm result;

		list.clear();
		result = factory.make("23", list);
		System.out.println("\tresult = " + result);

		list.clear();
		result = factory.make("3.14", list);
		System.out.println("\tresult = " + result);

		list.clear();
		result = factory.make("[1,2,3]", list);
		System.out.println("\tresult = " + result);

		list.clear();
		result = factory.make("GL(\"toto\")", list);
		System.out.println("\tresult = " + result);

		list.clear();
		list.add(new Integer(1));
		result = factory.make("<int>", list);
		System.out.println("\tresult = " + result);

		list.clear();
		list.add(new Double(3.14));
		result = factory.make("<real>", list);
		System.out.println("\tresult = " + result);

		list.clear();
		list.add(factory.parse("f(a,b,c)"));
		result = factory.make("<term>", list);
		System.out.println("\tresult = " + result);

		list.clear();
		list.add(factory.parse("f(a,b,c)"));
		list.add(new Integer(3));
		list.add(factory.parse("<abc>"));
		result = factory.make("[<term>,2,<int>,3.14,<placeholder>]", list);
		System.out.println("\tresult = " + result);

		list.clear();
		list.add(factory.parse("b"));
		list.add(new Integer(4));
		result = factory.make("f(1,<term>,c,<int>)", list);
		System.out.println("\tresult = " + result);

		list.clear();
		list.add(factory.parse("b"));
		list.add(new Integer(4));
		result = factory.make("f(1,g(<term>),c,h(<int>))", list);
		System.out.println("\tresult = " + result);

		//Ts[8] = _factory.parse();
		list.clear();
		list.add(factory.parse("1"));
		list.add(factory.parse("[]"));
		result = factory.make("[<term>,<list>]", list);
		System.out.println("\tresult = " + result);
		test(((ATermList) result).getFirst() == factory.parse("1"), "make-1a");
		test(((ATermList) result).getLength() == 1, "make-1b");

		/*
		list.add(new Integer(1));
		test(_factory.make("<int>", list).equals(T[0]), "make-1");

		list.clear(); list.add(T[3]);
		test(_factory.make("<term>", list).equals(T[3]), "make-2");

		list.clear(); list.add( "b");
		test(_factory.make("<appl>", list).equals(T[4]), "make-3");

		list.clear(); list.add(new Double(3.14));
		test(_factory.make("<real>", list).equals(
		_factory.makeReal(3.14)), "make-4");

		list.clear(); list.add(_factory.makeAppl(
		_factory.makeAFun("real",0,false)));
		test(_factory.make("<placeholder>", list).equals(
		_factory.parse("<real>")), "make-5");

		list.clear(); list.add(T[7]);
		test(_factory.make("[<list>]", list).equals(T[7]), "make-6");

		list.clear();
		list.add(T[3]);
		list.add("b");
		list.add(_factory.makeList(T[5], _factory.makeList()));
		test(_factory.make("f(<term>,<appl>,<list>)", list).equals(T[6]), "make-7");

		list.clear();
		list.add("f");
		list.add(new Integer(2));
		test(_factory.make("<appl(1,<int>,3)>", list).equals(T[8]), "make-8");
		 */

		System.out.println("pass: testPatternMake");
	}

	@Test
	public void testMaxTerm()
	{
		final AFun f = factory.makeAFun("f", 1, false);
		final AFun a = factory.makeAFun("a", 0, false);

		final int size = 500;
		final ATerm[] array1 = new ATerm[size];
		final ATerm[] array2 = new ATerm[size];

		final long start = System.currentTimeMillis();
		System.out.println("array1");
		for (int i = 0; i < size; i++)
		{
			if (i % 100 == 0)
			{
				System.out.print(i + "  ");
			}

			final int idx = i % 10;
			array1[idx] = factory.makeAppl(a);
			for (int j = 0; j < 2 * i; j++)
			{
				array1[idx] = factory.makeAppl(f, array1[idx]);
			}
			//System.out.println("array[" + i + "] = " + array[i]);
		}

		System.out.println("\narray2");
		for (int i = 0; i < size; i++)
		{
			if (i % 100 == 0)
			{
				System.out.print(i + "  ");
			}

			final int idx = i % 10;
			array2[idx] = factory.makeAppl(a);
			for (int j = 0; j < 2 * i; j++)
			{
				array2[idx] = factory.makeAppl(f, array2[idx]);
			}
			//System.out.println("array[" + i + "] = " + array[i]);
		}

		System.out.println("\ntest");
		for (int i = 0; i < size; i++)
		{
			if (i % 500 == 0)
			{
				System.out.print(i + "  ");
			}

			final int idx = i % 10;
			if (array1[idx] != array2[idx])
			{
				System.out.println("array1[" + idx + "] = " + array1[idx]);
				System.out.println("array2[" + idx + "] = " + array2[idx]);
				throw new RuntimeException("i = " + idx);
			}
		}
		final long end = System.currentTimeMillis();

		System.out.println("\ntest " + size + " ok in " + (end - start) + " ms");
		System.out.println(factory);
	}

	public void testFib()
	{
		final TestFib t = TestFib.newTestFib(factory);
		t.test1();
		t.test2();
		t.test3(10);
		System.out.println(factory.toString());
	}

	public void testPrimes(final int n)
	{
		final TestPrimes t = TestPrimes.newTestPrimes(factory);
		final long start = System.currentTimeMillis();
		final ATermList l = t.getPrimes(n);
		final long end = System.currentTimeMillis();

		System.out.println("primes(" + n + ") in " + (end - start) + " ms");
		//System.out.println(" primes(" + n + ") = " + l);
		System.out.println("#primes(" + n + ") = " + l.getLength());
		System.out.println(factory);
	}

	@Test
	public void testFibInterpreted()
	{
		final TestFibInterpreted t = TestFibInterpreted.newTestFibInterpreted(factory);
		t.initRules();
		t.test1(12);

		System.out.println(factory);
	}

	@Test
	public void testAll()
	{

		testMakeInt();
		testMakeLong();
		testMakeReal();
		testMakeAppl();
		testParser();
		testParseError();
		testMakeList();
		testPatternMatch();
		testPatternMake();
		testFib();
		testPrimes(2000);
		//testFibInterpreted();
	}

}
