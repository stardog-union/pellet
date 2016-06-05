package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * A shell pseudo-_node implementation for an array class.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-_index.shtml">Vlad Roubtsov</a>, 2003
 */
final class ArrayShellProfileNode extends AbstractShellProfileNode
{
	private final Class<?> _type;
	private final int _length;

	@Override
	public String name()
	{
		return "<shell: " + ObjectProfiler.typeName(_type, ObjectProfiler.SHORT_TYPE_NAMES) + ", length=" + _length + ">";
	}

	ArrayShellProfileNode(final IObjectProfileNode parent, final Class<?> type, final int length)
	{
		super(parent);

		_type = type;
		_length = length;
	}
}
