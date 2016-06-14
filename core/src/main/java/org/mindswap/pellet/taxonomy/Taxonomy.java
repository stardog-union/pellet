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

package org.mindswap.pellet.taxonomy;

import com.clarkparsia.pellet.utils.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.katk.tools.Log;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.utils.Bool;

/*
 * Created on Aug 13, 2003
 */

/**
 * @author Evren Sirin
 */
public class Taxonomy<T>
{
	private class DatumEquivalentsPairIterator<U> implements Iterator<Map.Entry<Set<U>, Object>>
	{

		private final Iterator<TaxonomyNode<U>> _i;
		private final Object _key;

		public DatumEquivalentsPairIterator(final Taxonomy<U> t, final Object key)
		{
			this._key = key;
			_i = t.getNodes().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return _i.hasNext();
		}

		@Override
		public Entry<Set<U>, Object> next()
		{
			final TaxonomyNode<U> current = _i.next();
			return new SimpleImmutableEntry<>(Collections.unmodifiableSet(current.getEquivalents()), current.getDatum(_key));
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private class DepthFirstDatumOnlyIterator<U> implements Iterator<Object>
	{

		private final Object _key;
		private final List<TaxonomyNode<U>> _pending;
		private final Set<TaxonomyNode<U>> _visited;

		public DepthFirstDatumOnlyIterator(final Taxonomy<U> t, final U u, final Object key)
		{
			this._key = key;
			_visited = new HashSet<>();
			_pending = new ArrayList<>();
			final TaxonomyNode<U> node = t.getNode(u);
			if (node != null)
				_pending.add(node);
		}

		@Override
		public boolean hasNext()
		{
			return !_pending.isEmpty();
		}

		@Override
		public Object next()
		{
			if (_pending.isEmpty())
				throw new NoSuchElementException();

			final TaxonomyNode<U> current = _pending.remove(_pending.size() - 1);
			_visited.add(current);
			for (final TaxonomyNode<U> sub : current.getSubs())
				if (!_visited.contains(sub))
					_pending.add(sub);

			return current.getDatum(_key);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	private class SimpleImmutableEntry<K, V> implements Map.Entry<K, V>
	{

		private final K _key;
		private final V _value;

		public SimpleImmutableEntry(final K key, final V value)
		{
			super();
			this._key = key;
			this._value = value;
		}

		@Override
		public K getKey()
		{
			return _key;
		}

		@Override
		public V getValue()
		{
			return _value;
		}

		@Override
		public V setValue(final V value)
		{
			throw new UnsupportedOperationException();
		}
	}

	public static final Logger _logger = Log.getLogger(Taxonomy.class);

	private static final boolean SUB = true;

	private static final boolean SUPER = false;

	public static final boolean TOP_DOWN = true;

	protected TaxonomyNode<T> _bottomNode;
	protected Map<T, TaxonomyNode<T>> _nodes;
	protected TaxonomyNode<T> _topNode;

	protected short _depth = 0;
	protected int totalBranching = 0;

	public Taxonomy()
	{
		this(null, null, null);
	}

	public Taxonomy(final Collection<T> elements, final T top, final T bottom)
	{
		_nodes = CollectionUtils.makeMap();

		if (top == null)
			_topNode = new TaxonomyNode<>((T) null, /* hidden = */true);
		else
		{
			_topNode = new TaxonomyNode<>(top, /* hidden = */false);
			_nodes.put(top, _topNode);
		}

		if (bottom == null)
			_bottomNode = new TaxonomyNode<>((T) null, /* hidden = */true);
		else
		{
			_bottomNode = new TaxonomyNode<>(bottom, /* hidden = */false);
			_nodes.put(bottom, _bottomNode);
		}

		if (elements == null || elements.isEmpty())
			_topNode.addSub(_bottomNode);
		else
			for (final T t : elements)
				addNode(t, /* hidden = */false);

		// precaution to avoid creating an invalid taxonomy is now done by
		// calling assertValid function because the taxonomy might be invalid
		// during the merge operation but it is guaranteed to be valid after
		// the merge is completed. so we check for validity at the very _end
		// TOP_NODE.setSupers( Collections.EMPTY_LIST );
		// BOTTOM_NODE.setSubs( Collections.EMPTY_LIST );
	}

	public void addEquivalentNode(final T t, final TaxonomyNode<T> node)
	{
		node.addEquivalent(t);
		_nodes.put(t, node);
	}

	/**
	 * Add a collection of elements equivalent to an element already in the taxonomy.
	 */
	public void addEquivalents(final T t, final Collection<T> eqs)
	{

		assert _nodes.keySet().contains(t) : "Element " + t.toString() + " not in taxonomy";

		final TaxonomyNode<T> node = _nodes.get(t);
		for (final T eq : eqs)
		{
			assert !_nodes.keySet().contains(eq) : "Element " + eq.toString() + " alread in taxonomy";
			node.addEquivalent(eq);
			_nodes.put(eq, node);
		}
	}

	/**
	 * Add a _node with known supers and subs. Any direct relations between subs and supers are removed.
	 *
	 * @param equivalents a non-empty set of equivalent elements defining the _node (one of which becomes the label)
	 * @param sups collection of supers, all of which must already exist in the taxonomy
	 * @param subs collection of subs, all of which must already exist in the taxonomy
	 * @param hidden indicates hidden or not
	 * @return the new _node
	 */
	public TaxonomyNode<T> addNode(final Collection<T> equivalents, final Collection<T> sups, final Collection<T> subs, final boolean hidden)
	{

		assert !equivalents.isEmpty() : "Taxonomy _nodes must have at least one element";
		assert _nodes.keySet().containsAll(sups) : "At least one super element not in taxonomy";
		assert _nodes.keySet().containsAll(subs) : "At least one sub element not in taxonomy";

		final TaxonomyNode<T> node = new TaxonomyNode<>(equivalents, hidden);
		for (final T t : equivalents)
			_nodes.put(t, node);

		short depth = 1;

		// Super handling
		{
			/*
			 * Note the special case when no supers are provided and top is
			 * hidden. Top points to the new _node, but not the reverse
			 */
			if (sups.isEmpty())
			{
				if (_topNode.isHidden())
				{
					_topNode.addSub(node);
					if (_topNode.getSubs().size() == 2)
						_topNode.removeSub(_bottomNode);
				}
				else
					node.addSupers(Collections.singleton(_topNode));

				totalBranching += 1;
			}
			else
			{
				final Set<TaxonomyNode<T>> supNodes = new HashSet<>();
				for (final T sup : sups)
				{
					final TaxonomyNode<T> supNode = _nodes.get(sup);
					if (supNode._depth >= depth)
						depth = (short) (supNode._depth + 1);
					supNodes.add(supNode);
				}
				node._depth = depth;
				if (depth > this._depth)
					this._depth = depth;
				node.addSupers(supNodes);

				totalBranching += supNodes.size();
			}
		}

		// Sub handling
		{
			Set<TaxonomyNode<T>> subNodes;
			if (subs.isEmpty())
			{
				if (_bottomNode.isHidden())
				{
					_bottomNode.addSupers(Collections.singleton(node));
					_bottomNode.getSupers().removeAll(node.getSupers());
				}
				else
					node.addSub(_bottomNode);

				totalBranching += 1;
			}
			else
			{
				subNodes = new HashSet<>();
				for (final T sub : subs)
					subNodes.add(_nodes.get(sub));
				node.addSubs(subNodes);

				totalBranching += subNodes.size();
			}
		}

		node.removeMultiplePaths();

		return node;
	}

	public TaxonomyNode<T> addNode(final T t, final boolean hidden)
	{
		final TaxonomyNode<T> node = new TaxonomyNode<>(t, hidden);
		_topNode.addSub(node);
		node.addSub(_bottomNode);
		_nodes.put(t, node);
		return node;
	}

	/**
	 * Add a collection of elements as subs to an element
	 */
	public void addSuper(final Collection<T> subs, final T sup)
	{

		assert _nodes.keySet().containsAll(subs) : "At least one sub element not in taxonomy";
		assert _nodes.keySet().contains(sup) : "Super element " + sup.toString() + " not in taxonomy";

		final Set<TaxonomyNode<T>> subNodes = new HashSet<>();
		for (final T sub : subs)
			subNodes.add(_nodes.get(sub));
		final TaxonomyNode<T> supNode = _nodes.get(sup);

		for (final TaxonomyNode<T> subNode : subNodes)
			if (subNode.getSupers().size() == 1 && subNode.getSupers().contains(_topNode))
				_topNode.removeSub(subNode);

		if (supNode.getSubs().size() == 1 && supNode.getSubs().contains(_bottomNode))
			supNode.removeSub(_bottomNode);

		supNode.addSubs(subNodes);

	}

	/**
	 * Add a sub/super relation
	 */
	public void addSuper(final T sub, final T sup)
	{

		assert _nodes.keySet().contains(sub) : "Sub element " + sub.toString() + " not in taxonomy";
		assert _nodes.keySet().contains(sup) : "Super element " + sup.toString() + " not in taxonomy";

		final TaxonomyNode<T> subNode = _nodes.get(sub);
		final TaxonomyNode<T> supNode = _nodes.get(sup);
		if (subNode.equals(supNode))
			throw new InternalReasonerException("Equivalent elements cannot have sub/super relationship");

		if (subNode.getSupers().size() == 1 && subNode.getSupers().iterator().next() == _topNode)
			_topNode.removeSub(subNode);

		if (supNode.getSubs().size() == 1 && supNode.getSubs().iterator().next() == _bottomNode)
			supNode.removeSub(_bottomNode);

		supNode.addSub(subNode);
	}

	/**
	 * Add a collection of supers to an element
	 */
	public void addSupers(final T sub, final Collection<T> sups)
	{

		assert _nodes.keySet().contains(sub) : "Sub element " + sub.toString() + " not in taxonomy";
		assert _nodes.keySet().containsAll(sups) : "At least one super element not in taxonomy";

		final TaxonomyNode<T> subNode = _nodes.get(sub);
		final Set<TaxonomyNode<T>> supNodes = new HashSet<>();
		for (final T sup : sups)
			supNodes.add(_nodes.get(sup));

		if (subNode.getSupers().size() == 1 && subNode.getSupers().contains(_topNode))
			_topNode.removeSub(subNode);

		for (final TaxonomyNode<T> supNode : supNodes)
			if (supNode.getSubs().size() == 1 && supNode.getSubs().contains(_bottomNode))
				supNode.removeSub(_bottomNode);

		subNode.addSupers(supNodes);
	}

	public void assertValid()
	{
		assert _topNode.getSupers().isEmpty() : "Top _node in the taxonomy has parents";
		assert _bottomNode.getSubs().isEmpty() : "Bottom _node in the taxonomy has children";
	}

	/**
	 * Given a list of concepts, find all the Least Common Ancestors (LCA). Note that a taxonomy is DAG not a tree so we do not have a unique LCA but a set of
	 * LCA.
	 */
	public List<T> computeLCA(final List<T> list)
	{
		// FIXME does not work when one of the elements is an ancestor of the
		// rest
		// TODO what to do with equivalent classes?
		// TODO improve efficiency

		if (list.isEmpty())
			return null;

		// get the first concept
		T t = list.get(0);

		// add all its ancestor as possible LCA candidates
		final List<T> ancestors = new ArrayList<>(getFlattenedSupers(t, /* direct = */false));

		for (int i = 1; (i < list.size()) && (ancestors.size() > 0); i++)
		{
			t = list.get(i);

			// take the intersection of possible candidates to get rid of
			// uncommon ancestors
			ancestors.retainAll(getFlattenedSupers(t, /* direct = */false));
		}

		final Set<T> toBeRemoved = new HashSet<>();

		// we have all common ancestors now remove the ones that have
		// descendants in the list
		for (final T a : ancestors)
		{

			if (toBeRemoved.contains(a))
				continue;

			final Set<T> supers = getFlattenedSupers(a, /* direct = */false);
			toBeRemoved.addAll(supers);
		}

		ancestors.removeAll(toBeRemoved);

		return ancestors;
	}

	public boolean contains(final T t)
	{
		return _nodes.containsKey(t);
	}

	/**
	 * Iterate over _nodes in taxonomy (no specific _order)returning pair of equivalence set and datum associated with {@code _key} for each. Useful, e.g., to
	 * collect equivalence sets matching some _condition on the datum (as in all classes which have a particular instances)
	 *
	 * @param _key _key associated with datum returned
	 * @return iterator over equivalence set, datum pairs
	 */
	public Iterator<Map.Entry<Set<T>, Object>> datumEquivalentsPair(final Object key)
	{
		return new DatumEquivalentsPairIterator<>(this, key);
	}

	/**
	 * Iterate down taxonomy in a _depth first traversal, beginning with class {@code c}, returning only datum associated with {@code _key} for each. Useful,
	 * e.g., to collect datum values in a transitive closure (as in all instances of a class).
	 *
	 * @param t starting location in taxonomy
	 * @param _key _key associated with datum returned
	 * @return datum iterator
	 */
	public Iterator<Object> depthFirstDatumOnly(final T t, final Object key)
	{
		return new DepthFirstDatumOnlyIterator<>(this, t, key);
	}

	/**
	 * Returns all the classes that are equivalent to class c. Class c itself is included in the result.
	 *
	 * @param t class whose equivalent classes are found
	 * @return A set of ATerm objects
	 */
	public Set<T> getAllEquivalents(final T t)
	{
		final TaxonomyNode<T> node = _nodes.get(t);

		if (node == null)
			return new HashSet<>();

		final Set<T> result = new HashSet<>(node.getEquivalents());

		return result;
	}

	public TaxonomyNode<T> getBottom()
	{
		return _bottomNode;
	}

	public Set<T> getClasses()
	{
		return _nodes.keySet();
	}

	/**
	 * Get datum on taxonomy elements associated with {@code _key}
	 *
	 * @param t identifies the taxonomy element
	 * @param _key identifies the specific datum
	 * @return the datum (or {@code null} if none is associated with {@code _key})
	 */
	public Object getDatum(final T t, final Object key)
	{
		final TaxonomyNode<T> node = _nodes.get(t);
		return (node == null) ? null : node.getDatum(key);
	}

	/**
	 * Returns all the classes that are equivalent to class c. Class c itself is NOT included in the result.
	 *
	 * @param t class whose equivalent classes are found
	 * @return A set of ATerm objects
	 */
	public Set<T> getEquivalents(final T t)
	{
		final Set<T> result = getAllEquivalents(t);
		result.remove(t);

		return result;
	}

	/**
	 * As in {@link #getSubs(Object, boolean)} except the return _value is the union of nested sets
	 */
	public Set<T> getFlattenedSubs(final T t, final boolean direct)
	{
		return getFlattenedSubSupers(t, direct, SUB);
	}

	/**
	 * Use {@link #getFlattenedSubs(Object, boolean)} or {@link #getFlattenedSupers(Object, boolean)} this method will become private
	 */
	private Set<T> getFlattenedSubSupers(final T t, final boolean direct, final boolean subOrSuper)
	{
		TaxonomyNode<T> node = _nodes.get(t);

		final Set<T> result = new HashSet<>();

		final List<TaxonomyNode<T>> visit = new ArrayList<>();
		visit.addAll((subOrSuper == SUB) ? node.getSubs() : node.getSupers());

		for (int i = 0; i < visit.size(); i++)
		{
			node = visit.get(i);

			if (node.isHidden())
				continue;

			final Set<T> add = node.getEquivalents();
			result.addAll(add);

			if (!direct)
				visit.addAll((subOrSuper == SUB) ? node.getSubs() : node.getSupers());
		}

		return result;
	}

	/**
	 * As in {@link #getSupers(Object, boolean)} except the return _value is the union of nested sets
	 */
	public Set<T> getFlattenedSupers(final T t, final boolean direct)
	{
		return getFlattenedSubSupers(t, direct, SUPER);
	}

	public TaxonomyNode<T> getNode(final T t)
	{
		return _nodes.get(t);
	}

	public Collection<TaxonomyNode<T>> getNodes()
	{
		return _nodes.values();
	}

	/**
	 * Returns all the (named) subclasses of class c. The class c itself is not included in the list but all the other classes that are equivalent to c are put
	 * into the list. Also note that the returned list will always have at least one element, that is the BOTTOM concept. By definition BOTTOM concept is
	 * subclass of every concept. This function is equivalent to calling getSubClasses(c, true).
	 *
	 * @param t class whose subclasses are returned
	 * @return A set of sets, where each set in the collection represents an equivalence class. The elements of the inner class are ATermAppl objects.
	 */
	public Set<Set<T>> getSubs(final T t)
	{
		return getSubs(t, false);
	}

	/**
	 * Returns the (named) subclasses of class c. Depending on the second parameter the resulting list will include either all subclasses or only the direct
	 * subclasses. A class d is a direct subclass of c iff
	 * <ol>
	 * <li>d is subclass of c</li>
	 * <li>there is no other class x different from c and d such that x is subclass of c and d is subclass of x</li>
	 * </ol>
	 * The class c itself is not included in the list but all the other classes that are sameAs c are put into the list. Also note that the returned list will
	 * always have at least one element. The list will either include one other concept from the hierarchy or the BOTTOM concept if no other class is subsumed
	 * by c. By definition BOTTOM concept is subclass of every concept.
	 *
	 * @param t Class whose subclasses are found
	 * @param direct If true return only direct subclasses elese return all the subclasses
	 * @return A set of sets, where each set in the collection represents an equivalence class. The elements of the inner class are ATermAppl objects.
	 */
	public Set<Set<T>> getSubs(final T t, final boolean direct)
	{
		return getSubSupers(t, direct, SUB);
	}

	/**
	 * Use {@link #getSubs(Object, boolean)} or {@link #getSupers(Object, boolean)}.
	 */
	private Set<Set<T>> getSubSupers(final T t, final boolean direct, final boolean subOrSuper)
	{
		TaxonomyNode<T> node = _nodes.get(t);

		if (node == null)
			return Collections.emptySet();

		final Set<Set<T>> result = new HashSet<>();

		final List<TaxonomyNode<T>> visit = new ArrayList<>();
		visit.addAll((subOrSuper == SUB) ? node.getSubs() : node.getSupers());

		for (int i = 0; i < visit.size(); i++)
		{
			node = visit.get(i);

			if (node.isHidden())
				continue;

			final Set<T> add = new HashSet<>(node.getEquivalents());
			if (!add.isEmpty())
				result.add(add);

			if (!direct)
				visit.addAll((subOrSuper == SUB) ? node.getSubs() : node.getSupers());
		}

		return result;
	}

	/**
	 * Returns all the superclasses (implicitly or explicitly defined) of class c. The class c itself is not included in the list. but all the other classes
	 * that are sameAs c are put into the list. Also note that the returned list will always have at least one element, that is TOP concept. By definition TOP
	 * concept is superclass of every concept. This function is equivalent to calling getSuperClasses(c, true).
	 *
	 * @param t class whose superclasses are returned
	 * @return A set of sets, where each set in the collection represents an equivalence class. The elements of the inner class are ATermAppl objects.
	 */
	public Set<Set<T>> getSupers(final T t)
	{
		return getSupers(t, false);
	}

	/**
	 * Returns the (named) superclasses of class c. Depending on the second parameter the resulting list will include either all or only the direct
	 * superclasses. A class d is a direct superclass of c iff
	 * <ol>
	 * <li>d is superclass of c</li>
	 * <li>there is no other class x such that x is superclass of c and d is superclass of x</li>
	 * </ol>
	 * The class c itself is not included in the list but all the other classes that are sameAs c are put into the list. Also note that the returned list will
	 * always have at least one element. The list will either include one other concept from the hierarchy or the TOP concept if no other class subsumes c. By
	 * definition TOP concept is superclass of every concept.
	 *
	 * @param t Class whose subclasses are found
	 * @param direct If true return all the superclasses else return only direct superclasses
	 * @return A set of sets, where each set in the collection represents an equivalence class. The elements of the inner class are ATermAppl objects.
	 */
	public Set<Set<T>> getSupers(final T t, final boolean direct)
	{
		return getSubSupers(t, direct, SUPER);
	}

	public Stream<Set<T>> supers(final T t, final boolean direct)
	{
		return getSubSupers(t, direct, SUPER).stream();
	}

	public TaxonomyNode<T> getTop()
	{
		return _topNode;
	}

	/**
	 * Checks if x is equivalent to y
	 *
	 * @param x Name of the first class
	 * @param y Name of the second class
	 * @return true if x is equivalent to y
	 */
	public Bool isEquivalent(final T x, final T y)
	{
		final TaxonomyNode<T> nodeX = _nodes.get(x);
		final TaxonomyNode<T> nodeY = _nodes.get(y);

		if (nodeX == null || nodeY == null)
			return Bool.UNKNOWN;
		else
			if (nodeX.equals(nodeY))
				return Bool.TRUE;
			else
				return Bool.FALSE;
	}

	/**
	 * Checks if x has an ancestor y.
	 *
	 * @param x Name of the _node
	 * @param y Name of the ancestor ode
	 * @return true if x has an ancestor y
	 */
	public Bool isSubNodeOf(final T x, final T y)
	{
		final TaxonomyNode<T> nodeX = _nodes.get(x);
		final TaxonomyNode<T> nodeY = _nodes.get(y);

		if (nodeX == null || nodeY == null)
			return Bool.UNKNOWN;
		else
			if (nodeX.equals(nodeY))
				return Bool.TRUE;

		if (nodeX.isHidden())
		{
			if (nodeY.isHidden())
				return Bool.UNKNOWN;
			else
				return getFlattenedSupers(x, /* direct = */false).contains(y) ? Bool.TRUE : Bool.FALSE;
		}
		else
			return getFlattenedSubs(y, false).contains(x) ? Bool.TRUE : Bool.FALSE;
	}

	public void merge(final TaxonomyNode<T> node1, final TaxonomyNode<T> node2)
	{
		final List<TaxonomyNode<T>> mergeList = new ArrayList<>(2);
		mergeList.add(node1);
		mergeList.add(node2);

		final TaxonomyNode<T> node = mergeNodes(mergeList);

		removeCycles(node);
	}

	private TaxonomyNode<T> mergeNodes(final List<TaxonomyNode<T>> mergeList)
	{

		assert mergeList.size() > 1 : "Attempt to merge less than two _nodes";

		if (_logger.isLoggable(Level.FINER))
			_logger.finer("Merge " + mergeList);

		TaxonomyNode<T> node = null;
		if (mergeList.contains(_topNode))
			node = _topNode;
		else
			if (mergeList.contains(_bottomNode))
				node = _bottomNode;
			else
				node = mergeList.get(0);

		final Set<TaxonomyNode<T>> merged = new HashSet<>();
		merged.add(node);

		for (final TaxonomyNode<T> other : mergeList)
		{

			if (merged.contains(other))
				continue;
			else
				merged.add(other);

			for (final TaxonomyNode<T> sub : other.getSubs())
				if ((sub != _bottomNode) && !mergeList.contains(sub))
				{
					if ((node.getSubs().size() == 1) && (node.getSubs().iterator().next() == _bottomNode))
						node.removeSub(_bottomNode);
					node.addSub(sub);
				}

			for (final TaxonomyNode<T> sup : other.getSupers())
				if ((sup != _topNode) && !mergeList.contains(sup))
				{
					if ((node.getSupers().size() == 1) && (node.getSupers().iterator().next() == _topNode))
						_topNode.removeSub(node);
					sup.addSub(node);
				}

			other.disconnect();

			for (final T t : other.getEquivalents())
				addEquivalentNode(t, node);

		}

		node.clearData();

		if (node != _topNode && node.getSupers().isEmpty())
			_topNode.addSub(node);

		if (node != _bottomNode && node.getSubs().isEmpty())
			node.addSub(_bottomNode);

		return node;
	}

	/**
	 * Set a datum _value associated with {@code _key} on a taxonomy element
	 *
	 * @param t identifies the taxonomy element
	 * @param _key identifies the datum
	 * @param _value the datum
	 * @return previous _value of datum or {@code null} if not set
	 */
	public Object putDatum(final T t, final Object key, final Object value)
	{
		final TaxonomyNode<T> node = _nodes.get(t);
		if (node == null)
			throw new RuntimeException(t + " is an unknown class!");

		return node.putDatum(key, value);
	}

	/**
	 * Remove an element from the taxonomy.
	 */
	public void remove(final T t)
	{
		assert _nodes.containsKey(t) : "Element not contained in taxonomy";

		final TaxonomyNode<T> node = _nodes.remove(t);
		if (node.getEquivalents().size() == 1)
		{
			final Collection<TaxonomyNode<T>> subs = node.getSubs();
			final Collection<TaxonomyNode<T>> supers = node.getSupers();
			node.disconnect();
			for (final TaxonomyNode<T> sup : supers)
				sup.addSubs(subs);
		}
		else
			node.removeEquivalent(t);
	}

	/**
	 * Walk through the super _nodes of the given _node and when a cycle is detected merge all the _nodes in that path
	 */
	public void removeCycles(final TaxonomyNode<T> node)
	{
		if (!_nodes.get(node.getName()).equals(node))
			throw new InternalReasonerException("This _node does not exist in the taxonomy: " + node.getName());
		removeCycles(node, new ArrayList<TaxonomyNode<T>>());
	}

	/**
	 * Given a _node and (a possibly empty) path of sub _nodes, remove cycles by merging all the _nodes in the path.
	 */
	private boolean removeCycles(final TaxonomyNode<T> node, final List<TaxonomyNode<T>> path)
	{
		// cycle detected
		if (path.contains(node))
		{
			mergeNodes(path);
			return true;
		}
		else
		{
			// no cycle yet, add this _node to the path and continue
			path.add(node);

			final List<TaxonomyNode<T>> supers = new ArrayList<>(node.getSupers());
			for (int i = 0; i < supers.size();)
			{
				final TaxonomyNode<T> sup = supers.get(i);
				// remove cycles involving super _node
				removeCycles(sup, path);
				// if the super has been removed then no need
				// to increment the _index
				if (i < supers.size() && supers.get(i).equals(sup))
					i++;
			}

			// remove the _node from the path
			path.remove(path.size() - 1);

			return false;
		}
	}

	public Object removeDatum(final T t, final Object key)
	{
		return getNode(t).removeDatum(key);
	}

	/**
	 * Clear existing supers for an element and set to a new collection
	 */
	public void resetSupers(final T t, final Collection<T> supers)
	{

		assert _nodes.keySet().contains(t) : "Element " + t.toString() + " not in taxonomy";
		assert _nodes.keySet().containsAll(supers) : "Supers not all contained in taxonomy";

		final TaxonomyNode<T> node = _nodes.get(t);

		final List<TaxonomyNode<T>> initial = new ArrayList<>(node.getSupers());
		for (final TaxonomyNode<T> n : initial)
			n.removeSub(node);

		if (supers.isEmpty())
			_topNode.addSub(node);
		else
		{
			final Set<TaxonomyNode<T>> added = new HashSet<>();
			for (final T sup : supers)
			{
				final TaxonomyNode<T> n = _nodes.get(sup);
				if (added.add(n))
					n.addSub(node);
			}
		}
	}

	/**
	 * Sort the _nodes in the taxonomy using topological ordering starting from top to bottom.
	 *
	 * @param includeEquivalents If false the equivalents in a _node will be ignored and only the name of the _node will be added to the result
	 * @return List of _node names sorted in topological ordering
	 */
	public List<T> topologocialSort(final boolean includeEquivalents)
	{
		return topologocialSort(includeEquivalents, null);
	}

	/**
	 * Sort the _nodes in the taxonomy using topological ordering starting from top to bottom.
	 *
	 * @param includeEquivalents If false the equivalents in a _node will be ignored and only the name of the _node will be added to the result
	 * @param comparator comparator to use sort the _nodes at same level, <code>null</code> if no special ordering is needed
	 * @return List of _node names sorted in topological ordering
	 */
	public List<T> topologocialSort(final boolean includeEquivalents, final Comparator<? super T> comparator)
	{
		final Map<TaxonomyNode<T>, Integer> degrees = new HashMap<>();
		final Map<T, TaxonomyNode<T>> nodesPending = comparator == null ? new HashMap<>() : new TreeMap<>(comparator);
		final Set<TaxonomyNode<T>> nodesLeft = new HashSet<>();
		final List<T> nodesSorted = new ArrayList<>();

		_logger.fine("Topological sort...");

		for (final TaxonomyNode<T> node : _nodes.values())
		{
			if (node.isHidden())
				continue;

			nodesLeft.add(node);
			final int degree = node.getSupers().size();
			if (degree == 0)
			{
				nodesPending.put(node.getName(), node);
				degrees.put(node, 0);
			}
			else
				degrees.put(node, Integer.valueOf(degree));
		}

		for (int i = 0, size = nodesLeft.size(); i < size; i++)
		{
			if (nodesPending.isEmpty())
				throw new InternalReasonerException("Cycle detected in the taxonomy!");

			final TaxonomyNode<T> node = nodesPending.values().iterator().next();

			final int deg = degrees.get(node);
			if (deg != 0)
				throw new InternalReasonerException("Cycle detected in the taxonomy " + node + " " + deg + " " + nodesSorted.size() + " " + _nodes.size());

			nodesPending.remove(node.getName());
			nodesLeft.remove(node);
			if (includeEquivalents)
				nodesSorted.addAll(node.getEquivalents());
			else
				nodesSorted.add(node.getName());

			for (final TaxonomyNode<T> sub : node.getSubs())
			{
				final int degree = degrees.get(sub);
				if (degree == 1)
				{
					nodesPending.put(sub.getName(), sub);
					degrees.put(sub, 0);
				}
				else
					degrees.put(sub, degree - 1);
			}
		}

		if (!nodesLeft.isEmpty())
			throw new InternalReasonerException("Failed to sort elements: " + nodesLeft);

		_logger.fine("done");

		return nodesSorted;
	}
}
