package profiler.utils;

import java.lang.reflect.Field;

// ----------------------------------------------------------------------------
/**
 * An {@link ILink} implementation for tree links created by class instance fields.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-_index.shtml">Vlad Roubtsov</a>, 2003
 */
final class ClassFieldLink implements ILink
{
	// public: ................................................................

	// ILink:

	@Override
	public String name()
	{
		return ObjectProfiler.fieldName(_field, ObjectProfiler.SHORT_TYPE_NAMES);
	}

	// protected: .............................................................

	// package: ...............................................................

	ClassFieldLink(final Field field)
	{
		_field = field;
	}

	// private: ...............................................................

	private final Field _field;

} // _end of class
// ----------------------------------------------------------------------------