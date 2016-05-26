package aterm.pure.binary;

import aterm.AFun;
import aterm.ATerm;

class SymEntry
{
	public AFun fun;
	public int arity;
	public int nrTerms;
	public int termWidth;
	public ATerm[] terms;
	public int[] nrTopSyms;
	public int[] symWidth;
	public int[][] topSyms;
}
