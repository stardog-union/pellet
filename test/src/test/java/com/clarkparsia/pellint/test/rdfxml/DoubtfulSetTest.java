package com.clarkparsia.pellint.test.rdfxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.clarkparsia.pellint.rdfxml.DoubtfulSet;
import com.clarkparsia.pellint.util.CollectionUtil;

/**
 * <p>
 * Title: 
 * </p>
 * <p>
 * Description: 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Harris Lin
 */
public class DoubtfulSetTest {
	private DoubtfulSet<String> m_Set;
	private String[] m_Str;
	
	@Before
	public void setUp() {
		m_Set = new DoubtfulSet<String>();
		m_Str = new String[5];
		for (int i = 0; i < m_Str.length; i++) {
			m_Str[i] = "A" + i;
		}
	}
	
	@Test
	public void testAdd() {
		assertTrue(m_Set.isEmpty());
		
		m_Set.add(m_Str[0]);
		
		assertEquals(1, m_Set.size());
		assertTrue(m_Set.contains(m_Str[0]));
		assertFalse(m_Set.containsDefinite(m_Str[0]));
		assertFalse(m_Set.contains(m_Str[1]));
		assertFalse(m_Set.containsDefinite(m_Str[1]));
		assertEquals(Collections.singleton(m_Str[0]), m_Set.getDoubtfulElements());
		assertTrue(m_Set.getDefiniteElements().isEmpty());
		
		m_Set.add(m_Str[0]);
		assertEquals(1, m_Set.size());
	}

	@Test
	public void testAddDefinite() {
		m_Set.add(m_Str[0]);
		m_Set.addDefinite(m_Str[1]);
		m_Set.add(m_Str[2]);
		m_Set.addDefinite(m_Str[2]);
		m_Set.addDefinite(m_Str[3]);
		m_Set.add(m_Str[3]);
		
		assertEquals(4, m_Set.size());
		assertFalse(m_Set.containsDefinite(m_Str[0]));
		assertTrue(m_Set.containsDefinite(m_Str[1]));
		assertTrue(m_Set.containsDefinite(m_Str[2]));
		assertTrue(m_Set.containsDefinite(m_Str[3]));
		assertEquals(Collections.singleton(m_Str[0]), m_Set.getDoubtfulElements());
		assertEquals(CollectionUtil.asSet(m_Str[1], m_Str[2], m_Str[3]), m_Set.getDefiniteElements());
	}

	@Test
	public void testRemove() {
		m_Set.add(m_Str[0]);
		m_Set.add(m_Str[1]);
		m_Set.addDefinite(m_Str[2]);
		m_Set.addDefinite(m_Str[3]);
		m_Set.remove(m_Str[0]);
		m_Set.remove(m_Str[2]);
		m_Set.remove(m_Str[4]);
		
		assertEquals(Collections.singleton(m_Str[1]), m_Set.getDoubtfulElements());
		assertEquals(Collections.singleton(m_Str[3]), m_Set.getDefiniteElements());

		m_Set.clear();
		assertTrue(m_Set.getDoubtfulElements().isEmpty());
		assertTrue(m_Set.getDefiniteElements().isEmpty());
	}
	
	@Test
	public void testIteration() {
		m_Set.add(m_Str[0]);
		m_Set.add(m_Str[1]);
		m_Set.addDefinite(m_Str[2]);
		m_Set.addDefinite(m_Str[3]);
		
		Set<String> set = CollectionUtil.makeSet();
		for (int i = 0; i <= 3; i++) {
			set.add(m_Str[i]);
		}
		
		assertTrue(set.equals(m_Set));
		assertTrue(m_Set.equals(set));
	}
}
