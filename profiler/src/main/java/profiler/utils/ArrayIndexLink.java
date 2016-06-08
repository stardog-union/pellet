package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * An {@link ILink} implementation for tree links created by array fields.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-_index.shtml">Vlad Roubtsov</a>, 2003
 */
final class ArrayIndexLink implements ILink
{
	// public: ................................................................

	// ILink:

	@Override
	public String name()
	{
		final StringBuffer s = new StringBuffer();

		ILink l = this;
		while (l instanceof ArrayIndexLink)
		{
			final ArrayIndexLink asl = (ArrayIndexLink) l;

			s.insert(0, ']');
			s.insert(0, asl._index);
			s.insert(0, '[');

			l = asl._container;
		}

		s.insert(0, l != null ? l.name() : ObjectProfiler.INPUT_OBJECT_NAME);

		return s.toString();
	}

	// protected: .............................................................

	// package: ...............................................................

	ArrayIndexLink(final ILink container, final int index)
	{
		_container = container;
		_index = index;
	}

	// private: ...............................................................

	private final ILink _container;
	private final int _index;

} // _end of class
// ----------------------------------------------------------------------------