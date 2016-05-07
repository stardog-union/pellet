package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * Interface used internally for memory-efficient representations of names of profile tree links between profile tree _nodes.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 */
interface ILink
{
	// public: ................................................................

	/**
	 * Returns the string that will be used for a {@link IObjectProfileNode#name()} implementation. It is expected that the implementation will generate the
	 * return on every call to this method and not keep in memory.
	 */
	String name();

} // end of interface
// ----------------------------------------------------------------------------