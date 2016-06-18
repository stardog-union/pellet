package com.clarkparsia.pellint.test.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellint.rdfxml.DoubtfulSet;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.util.Collections;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class DoubtfulSetTest
{
	private DoubtfulSet<String> _set;
	private String[] _str;

	@Before
	public void setUp()
	{
		_set = new DoubtfulSet<>();
		_str = new String[5];
		for (int i = 0; i < _str.length; i++)
			_str[i] = "A" + i;
	}

	@Test
	public void testAdd()
	{
		assertTrue(_set.isEmpty());

		_set.add(_str[0]);

		assertEquals(1, _set.size());
		assertTrue(_set.contains(_str[0]));
		assertFalse(_set.containsDefinite(_str[0]));
		assertFalse(_set.contains(_str[1]));
		assertFalse(_set.containsDefinite(_str[1]));
		assertEquals(Collections.singleton(_str[0]), _set.getDoubtfulElements());
		assertTrue(_set.getDefiniteElements().isEmpty());

		_set.add(_str[0]);
		assertEquals(1, _set.size());
	}

	@Test
	public void testAddDefinite()
	{
		_set.add(_str[0]);
		_set.addDefinite(_str[1]);
		_set.add(_str[2]);
		_set.addDefinite(_str[2]);
		_set.addDefinite(_str[3]);
		_set.add(_str[3]);

		assertEquals(4, _set.size());
		assertFalse(_set.containsDefinite(_str[0]));
		assertTrue(_set.containsDefinite(_str[1]));
		assertTrue(_set.containsDefinite(_str[2]));
		assertTrue(_set.containsDefinite(_str[3]));
		assertEquals(Collections.singleton(_str[0]), _set.getDoubtfulElements());
		assertEquals(CollectionUtil.asSet(_str[1], _str[2], _str[3]), _set.getDefiniteElements());
	}

	@Test
	public void testRemove()
	{
		_set.add(_str[0]);
		_set.add(_str[1]);
		_set.addDefinite(_str[2]);
		_set.addDefinite(_str[3]);
		_set.remove(_str[0]);
		_set.remove(_str[2]);
		_set.remove(_str[4]);

		assertEquals(Collections.singleton(_str[1]), _set.getDoubtfulElements());
		assertEquals(Collections.singleton(_str[3]), _set.getDefiniteElements());

		_set.clear();
		assertTrue(_set.getDoubtfulElements().isEmpty());
		assertTrue(_set.getDefiniteElements().isEmpty());
	}

	@Test
	public void testIteration()
	{
		_set.add(_str[0]);
		_set.add(_str[1]);
		_set.addDefinite(_str[2]);
		_set.addDefinite(_str[3]);

		final Set<String> set = CollectionUtil.makeSet();
		for (int i = 0; i <= 3; i++)
			set.add(_str[i]);

		assertTrue(set.equals(_set));
		assertTrue(_set.equals(set));
	}
}
