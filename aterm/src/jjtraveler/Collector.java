package jjtraveler;

import java.util.Collection;

/**
 * A visitor combinator for collecting items.
 */

public interface Collector<T extends Visitable> extends Visitor<T>
{
	public Collection<T> getCollection();
}
