package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * A shell pseudo-_node implementation for an array class.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 */
final class ArrayShellProfileNode extends AbstractShellProfileNode
{
	// public: ................................................................

	@Override
	public String name()
	{
		return "<shell: " + ObjectProfiler.typeName(m_type, ObjectProfiler.SHORT_TYPE_NAMES) + ", length=" + m_length + ">";
	}

	// protected: .............................................................

	// package: ...............................................................

	ArrayShellProfileNode(final IObjectProfileNode parent, final Class type, final int length)
	{
		super(parent);

		m_type = type;
		m_length = length;
	}

	// private: ...............................................................

	private final Class m_type;
	private final int m_length;

} // _end of class
// ----------------------------------------------------------------------------