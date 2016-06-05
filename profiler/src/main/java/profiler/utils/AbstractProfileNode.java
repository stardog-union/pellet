package profiler.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

// ----------------------------------------------------------------------------
/**
 * Abstract base class for all _node implementations in this package.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-_index.shtml">Vlad Roubtsov</a>, 2003
 */
abstract class AbstractProfileNode implements IObjectProfileNode, Comparable<AbstractProfileNode>
{
	// public: ................................................................

	// IObjectProfileNode:

	@Override
	public final int size()
	{
		return _size;
	}

	@Override
	public final IObjectProfileNode parent()
	{
		return _parent;
	}

	@Override
	public final IObjectProfileNode[] path()
	{
		IObjectProfileNode[] path = m_path;
		if (path != null)
			return path;
		else
		{
			final LinkedList<IObjectProfileNode> _path = new LinkedList<>();
			for (IObjectProfileNode node = this; node != null; node = node.parent())
				_path.addFirst(node);

			path = new IObjectProfileNode[_path.size()];
			_path.toArray(path);

			m_path = path;
			return path;
		}
	}

	@Override
	public final IObjectProfileNode root()
	{
		IObjectProfileNode node = this;
		IObjectProfileNode parent = parent();
		while (parent != null)
		{
			node = parent;
			parent = parent.parent();
		}

		return node;
	}

	@Override
	public final int pathlength()
	{
		final IObjectProfileNode[] path = m_path;
		if (path != null)
			return path.length;
		else
		{
			int result = 0;
			for (IObjectProfileNode node = this; node != null; node = node.parent())
				++result;

			return result;
		}
	}

	@Override
	public final String dump()
	{
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);

		final INodeVisitor visitor = ObjectProfileVisitors.newDefaultNodePrinter(out, null, null, ObjectProfiler.SHORT_TYPE_NAMES);
		traverse(null, visitor);

		out.flush();
		return sw.toString();
	}

	// Comparable:

	@Override
	public final int compareTo(final AbstractProfileNode obj)
	{
		return obj._size - this._size;
	}

	@Override
	public String toString()
	{
		return super.toString() + ": name = " + name() + ", size = " + size();
	}

	// protected: .............................................................

	// package: ...............................................................

	AbstractProfileNode(final IObjectProfileNode parent)
	{
		_parent = parent;
	}

	int _size;

	static final IObjectProfileNode[] EMPTY_OBJECTPROFILENODE_ARRAY = new IObjectProfileNode[0];

	// private: ...............................................................

	private final IObjectProfileNode _parent;
	private transient IObjectProfileNode[] m_path;

} // _end of class
// ----------------------------------------------------------------------------