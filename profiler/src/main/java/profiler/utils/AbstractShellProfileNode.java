package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * Abstract base class for all shell pseudo-node implementations in this package. It is used primarily to lower memory consumption by shell nodes.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 */
abstract class AbstractShellProfileNode extends AbstractProfileNode
{
	// public: ................................................................    

	@Override
	public final Object object()
	{
		return null;
	}

	@Override
	public final IObjectProfileNode shell()
	{
		return null;
	}

	@Override
	public final IObjectProfileNode[] children()
	{
		return EMPTY_OBJECTPROFILENODE_ARRAY;
	}

	@Override
	public final int refcount()
	{
		return 0;
	}

	@Override
	public final boolean traverse(final INodeFilter filter, final INodeVisitor visitor)
	{
		if ((visitor != null) && ((filter == null) || filter.accept(this)))
		{
			visitor.previsit(this);
			visitor.postvisit(this);

			return true;
		}

		return false;
	}

	// protected: .............................................................

	// package: ...............................................................

	AbstractShellProfileNode(final IObjectProfileNode parent)
	{
		super(parent);
	}

	// private: ...............................................................

} // end of class
// ----------------------------------------------------------------------------