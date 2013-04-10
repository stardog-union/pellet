package org.mindswap.pellet.utils.iterator;

import java.util.Iterator;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Clark & Parsia, LLC. <http://www.clarkparsia.com></p>
 *
 * @author Evren Sirin
 */
public abstract class MapIterator<F,T> implements Iterator<T> {
	private Iterator<F> iterator;

	public MapIterator(Iterator<F> iterator) {
		this.iterator = iterator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public T next() {
		return map( iterator.next() );
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		iterator.remove();
	}
	
	public abstract T map(F obj);
}
