package profiler.utils;

import java.util.Arrays;

// ----------------------------------------------------------------------------
/**
 * A non-shell profile tree _node implementation. This implementation trades off some object orientation "niceness" to achieve more memory compactness.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-_index.shtml">Vlad Roubtsov</a>, 2003
 */
final class ObjectProfileNode extends AbstractProfileNode
{
	// public: ................................................................    

	@Override
	public Object object()
	{
		return _obj;
	}

	@Override
	public String name()
	{
		return _link == null ? ObjectProfiler.INPUT_OBJECT_NAME : _link.name();
	}

	@Override
	public IObjectProfileNode shell()
	{
		return _shell;
	}

	@Override
	public IObjectProfileNode[] children()
	{
		return _children;
	}

	@Override
	public int refcount()
	{
		return _refcount;
	}

	@Override
	public boolean traverse(final INodeFilter filter, final INodeVisitor visitor)
	{
		if ((visitor != null) && ((filter == null) || filter.accept(this)))
		{
			visitor.previsit(this);

			final IObjectProfileNode[] children = _children;
			for (final IObjectProfileNode element : children)
				element.traverse(filter, visitor);

			visitor.postvisit(this);

			return true;
		}

		return false;
	}

	// protected: .............................................................

	// package: ...............................................................

	/*
	 * This method manages the vector in m_children field for an unfinished _node.
	 */
	void addFieldRef(final IObjectProfileNode node)
	{
		// [m_size is the child count]

		IObjectProfileNode[] children = _children;
		final int childrenLength = children.length;
		if (_size >= childrenLength)
		{
			final IObjectProfileNode[] newchildren = new IObjectProfileNode[Math.max(1, childrenLength << 1)];
			System.arraycopy(children, 0, newchildren, 0, childrenLength);
			_children = children = newchildren;
		}
		children[_size++] = node;
	}

	/*
	 * This method is called once on every _node to lock it down into its
	 * immutable and most compact representation during phase 2 of profile
	 * tree construction.
	 */
	void finish()
	{
		final int childCount = _size; // m_size is the child count for a non-shell _node
		if (childCount > 0)
		{
			if (childCount < _children.length)
			{
				final IObjectProfileNode[] newadj = new IObjectProfileNode[childCount];
				System.arraycopy(_children, 0, newadj, 0, childCount);

				_children = newadj;
			}

			Arrays.sort(_children);

			int size = 0;
			for (int i = 0; i < childCount; ++i)
				size += _children[i].size();
			_size = size; // m_size is the full _node size for all _nodes
		}
	}

	ObjectProfileNode(final ObjectProfileNode parent, final Object obj, final ILink link)
	{
		super(parent);

		_obj = obj;
		_link = link;
		_refcount = 1;
		_children = EMPTY_OBJECTPROFILENODE_ARRAY;
	}

	final ILink _link;
	final Object _obj;
	int _refcount;
	AbstractShellProfileNode _shell;
	IObjectProfileNode[] _children;

	// private: ...............................................................

} // _end of class
// ----------------------------------------------------------------------------