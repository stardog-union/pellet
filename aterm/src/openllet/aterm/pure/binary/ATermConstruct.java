package openllet.aterm.pure.binary;

import openllet.aterm.ATerm;
import openllet.aterm.ATermList;

/**
 * A structure that contains all information we need for reconstructing a term.
 * 
 * @author Arnold Lankamp
 */
class ATermConstruct
{
	public int type;

	public int termIndex = 0;
	public ATerm tempTerm = null;

	public int subTermIndex = 0;
	public ATerm[] subTerms = null;

	public boolean hasAnnos;
	public ATermList annos;

	ATermConstruct(final int type, final boolean hasAnnos, final int termIndex)
	{
		this.type = type;
		this.hasAnnos = hasAnnos;
		this.termIndex = termIndex;
	}
}
