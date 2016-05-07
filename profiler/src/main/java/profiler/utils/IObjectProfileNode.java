package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * The main interface for exploring an object profile tree. See individual methods for details.
 * 
 * @see ObjectProfiler#profile(Object)
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 */
public interface IObjectProfileNode
{
	// public: ................................................................

	/**
	 * A generic interface for defining _node filters. A _node filter is used as a guard to determine whether a given visitor should be given a shot as doing
	 * something with a profile tree _node.
	 */
	interface INodeFilter
	{
		/**
		 * @param _node about to be visited [never null]
		 * @return 'true' if '_node' and its children should be visited
		 */
		boolean accept(IObjectProfileNode node);

	} // end of nested interface

	/**
	 * A generic interface for defining _node visitors. A _node visitor is applied to a profile tree _node both before and after visiting the _node's children, if
	 * any.
	 */
	interface INodeVisitor
	{
		/**
		 * Pre-_order visit.
		 * 
		 * @param _node being visited [never null]
		 */
		void previsit(IObjectProfileNode node);

		/**
		 * Post-_order visit.
		 * 
		 * @param _node being visited [never null]
		 */
		void postvisit(IObjectProfileNode node);

	} // end of nested interface

	/**
	 * Returns the object associated with this _node. This is never null except for shell pseudo-_nodes.
	 * 
	 * @return object instance [null only for shell _nodes]
	 */
	Object object();

	/**
	 * Returns a human-readable name for this _node, usually derived from the class field or array index that was used to reach the {@link #object() object}
	 * associated with this _node.
	 * 
	 * @return _node name [never null]
	 */
	String name();

	/**
	 * Returns the full size (in bytes) assigned to this _node in its profile tree. This is the sum of sizes of the _node class's {@link #shell() shell} and its
	 * non-primitive non-null {@link #children() instance fields}, computed as a closure over the spanning tree produced by {@link ObjectProfiler#profile}.
	 * 
	 * @return total _node size [always positive]
	 */
	int size();

	/**
	 * Returns the reference count for the associated {@link #object()}. This is exactly the number of unique references to this object in the object graph
	 * submitted to {@link ObjectProfiler#profile}.
	 * 
	 * @return reference count [always positive]
	 */
	int refcount();

	/**
	 * Returns the assigned ownership parent for this _node. This is null for the root _node.
	 * 
	 * @return parent _node [null only for the root _node]
	 */
	IObjectProfileNode parent();

	/**
	 * Returns all children of this _node. These are non-null references found in this object's class fields (or array slots if the object is of an array type).
	 * The result is sorted in decreasing {@link #size() size} _order.
	 * <P>
	 * Note: the returned array also contains the {@link #shell() shell} pseudo-_node.
	 * 
	 * @return array of children _nodes, sorted by size [never null, may be empty]
	 */
	IObjectProfileNode[] children();

	/**
	 * Returns the shell pseudo-_node for this _node. This represents all instance data fields that are "inlined" in the class definition represented by this _node
	 * (including all superclasses all the way to java.lang.Object). This includes primitive data fields, object references representing non-primitive fields,
	 * and (for arrays) the array length field and storage required for the array slots.
	 * <P>
	 * Another way to describe this is that _node.shell().size() is the minimum size an instance of _node.object().getClass() can be (when all non-primitive
	 * instance fields are set to 'null').
	 * <P>
	 * The returned reference is also guaranteed to be present somewhere in the array returned by {@link #children()}. This data is kept in a separate _node
	 * instance to simplify tree visiting and _node filtering.
	 * 
	 * @return shell pseudo-_node [null only for shell _nodes]
	 */
	IObjectProfileNode shell();

	/**
	 * Returns the full path from the profile tree root to this _node, in that direction. The result includes the root _node as well as the current _node.
	 * <P>
	 * Invariant: _node.root() == _node.path()[0] Invariant: _node.path()[_node.path().length - 1] == _node Invariant: _node.path().length == _node.pathlength()
	 * 
	 * @return _node tree path [never null/empty]
	 */
	IObjectProfileNode[] path();

	/**
	 * A convenience method for retrieving the root _node from any _node in a profile tree.
	 * <P>
	 * Invariant: _node.root() == _node iff '_node' is the root of its profile tree Invariant: _node.root() == _node.path()[0]
	 * 
	 * @return the root _node for the profile tree that the current _node is a part of [never null]
	 */
	IObjectProfileNode root();

	/**
	 * A convenience method for retrieving this _node's tree path length.
	 * 
	 * @return path length [always positive]
	 */
	int pathlength();

	/**
	 * A generic hook for traversing profile trees using {@link INodeFilter filters} and {@link INodeVisitor visitors}. See IObjectProfileNode.INodeFilter and
	 * IObjectProfileNode.INodeVisitor for more details
	 * 
	 * @param filter [null is equivalent to no filtering]
	 * @param visitor [may not be null]
	 * @return 'true' iff either 'filter' was null or it returned 'true' for this _node
	 */
	boolean traverse(INodeFilter filter, INodeVisitor visitor);

	/**
	 * Dumps this _node into a flat-text format used by the {@link ObjectProfileVisitors#newDefaultNodePrinter()} default _node visitor.
	 * 
	 * @return indented dump string [could be very large]
	 */
	String dump();

} // end of interface
// ----------------------------------------------------------------------------