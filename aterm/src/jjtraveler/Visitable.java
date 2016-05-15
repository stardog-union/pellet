package jjtraveler;

/**
 * Any class that needs to be visitable by a visitor should implement
 * the Visitable interface.
 */

public interface Visitable
{

	/**
	 * Returns the number of children of any visitable.
	 */
	public abstract int getChildCount();

	/**
	 * Returns the ith child of any visitable. Counting starts
	 * at 0. Thus, to get the last child of a visitable with n
	 * children, use getChild(n-1).
	 */
	public abstract <T extends Visitable> T getChildAt(int i);

	/**
	 * Replaces the ith child of any visitable, and returns this
	 * visitable. Counting starts at 0. Thus, to set the last child of
	 * a visitable with n children, use setChild(n-1).
	 */
	public abstract <T extends Visitable> T setChildAt(int i, Visitable child);

}
