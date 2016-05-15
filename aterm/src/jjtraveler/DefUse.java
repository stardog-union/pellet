package jjtraveler;

import java.util.Collection;
import java.util.HashSet;

/**
 * A generic algorithm for use-def analysis.
 */

public class DefUse<T extends Visitable> extends TopDown<T>
{
	Collector<T> use;
	Collector<T> def;

	/**
	 * @param use visitor combinator that collects used entities.
	 * @param def visitor combinator that collects defined entities.
	 */
	public DefUse(final Collector<T> use, final Collector<T> def)
	{
		super(new Sequence<>(use, def));
		this.use = use;
		this.def = def;
	}

	/**
	 * Return those entities that are defined, but not used.
	 */
	public Collection<T> getUnused()
	{
		final HashSet<T> result = new HashSet<>();
		result.addAll(def.getCollection());
		result.removeAll(use.getCollection());
		return result;
	}

	/**
	 * Return those entities that are used, but not defined.
	 */
	public Collection<T> getUndefined()
	{
		final HashSet<T> result = new HashSet<>();
		result.addAll(use.getCollection());
		result.removeAll(def.getCollection());
		return result;
	}

}
