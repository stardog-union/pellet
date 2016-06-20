package openllet.aterm.pure.binary;

import openllet.aterm.ATerm;
import openllet.aterm.ATermList;

/**
 * Structure that holds information about the state of the contained term.
 * 
 * @author Arnold Lankamp
 */
class ATermMapping
{
	public ATerm term;
	public int subTermIndex = -1;
	public boolean annosDone = false;

	/** This is for a ATermList 'nextTerm' optimalization only. */
	public ATermList nextPartOfList = null;

	ATermMapping(final ATerm term)
	{
		this.term = term;
	}
}
