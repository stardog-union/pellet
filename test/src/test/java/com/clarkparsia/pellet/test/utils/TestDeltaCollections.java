/**
 * 
 */
package com.clarkparsia.pellet.test.utils;

import java.util.Map;
import java.util.Set;

import com.clarkparsia.pellet.utils.DeltaMap;
import com.clarkparsia.pellet.utils.DeltaSet;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 */
public class TestDeltaCollections {
	@Test
	public void deltaSet() {
		Set<Integer> base = ImmutableSet.of(1, 2, 3, 4, 5);
		DeltaSet<Integer> delta = new DeltaSet<Integer>(base);

		assertTrue(delta.add(6));
		assertTrue(delta.add(7));
		assertTrue(delta.remove(3));
		assertTrue(delta.remove(5));
		assertFalse(delta.add(2));
		assertTrue(delta.add(5));
		assertFalse(delta.remove(9));
		assertTrue(delta.remove(7));

		Set<Integer> expected = ImmutableSet.of(1, 2, 4, 5, 6);
		assertSetEquals(expected, delta);

		assertFalse(delta.contains(3));
		assertFalse(delta.contains(7));

		delta.clear();
		assertSetEquals(ImmutableSet.<Integer>of(), delta);

		delta.reset();
		assertSetEquals(base, delta);
	}

	@Test
	public void deltaMap() {
		Map<Integer, String> base = ImmutableMap.of(1, "1", 2, "2", 3, "3", 4, "4", 5, "5");
		DeltaMap<Integer, String> delta = new DeltaMap<Integer, String>(base);

		assertEquals(null, delta.put(6, "6"));
		assertEquals(null, delta.put(7, "7"));
		assertEquals(7, delta.size());
		assertEquals("3", delta.remove(3));
		assertEquals("5", delta.remove(5));
		assertEquals("2", delta.put(2, "2a"));
		assertEquals(5, delta.size());
		assertEquals(null, delta.put(5, "5b"));
		assertEquals(null, delta.remove(9));
		assertEquals("2a", delta.put(2, "2b"));
		assertEquals("7", delta.remove(7));

		assertEquals(5, delta.size());

		Map<Integer, String> expected = ImmutableMap.of(1, "1", 2, "2b", 4, "4", 5, "5b", 6, "6");
		assertMapEquals(expected, delta);

		assertNull(delta.get(3));
		assertNull(delta.get(7));

		delta.clear();
		assertMapEquals(ImmutableMap.<Integer, String>of(), delta);

		delta.reset();
		assertMapEquals(base, delta);
	}

	private <T> void assertSetEquals(Set<T> expected, Set<T> actual) {
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
		for (T i : expected) {
			assertTrue(actual.contains(i));
		}
	}

	private <K, V> void assertMapEquals(Map<K, V> expected, Map<K, V> actual) {
		assertEquals(expected.size(), actual.size());
		assertEquals(expected, actual);
		for (Map.Entry<K, V> e : expected.entrySet()) {
			assertEquals(e.getValue(), actual.get(e.getKey()));
		}
	}
	
}
