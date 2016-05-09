package jjtraveler;

import java.util.Collection;

/**
 * A visitor combinator for collecting items.
 */

public interface Collector extends Visitor
{
	public Collection getCollection();
}
