package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * A shell pseudo-_node implementation for a non-array class.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-_index.shtml">Vlad Roubtsov</a>, 2003
 */
final class ObjectShellProfileNode extends AbstractShellProfileNode
{
	// public: ................................................................    

	@Override
	public String name()
	{
		return "<shell: " + _primitiveFieldCount + " prim/" + _refFieldCount + " ref fields>";
	}

	// protected: .............................................................

	// package: ...............................................................

	ObjectShellProfileNode(final IObjectProfileNode parent, final int primitiveFieldCount, final int refFieldCount)
	{
		super(parent);

		_primitiveFieldCount = primitiveFieldCount;
		_refFieldCount = refFieldCount;
	}

	// private: ...............................................................

	private final int _primitiveFieldCount, _refFieldCount;

} // _end of class
// ----------------------------------------------------------------------------