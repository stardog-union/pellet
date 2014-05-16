// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.Arrays;

import org.mindswap.pellet.DependencySet;

/**
 * 
 * @author Evren Sirin
 */
public abstract class Token {

	public abstract WME get(int index);

	public abstract DependencySet getDepends(boolean doExplanation);

	public abstract boolean dependsOn(int branch);

	private static class ListToken extends Token  {
		private ListToken next;
		private WME wme;
		private int index;
		
		public ListToken(WME wme, ListToken tok) {
		    this.wme = wme;
		    this.next = tok;
		    this.index = (tok == null) ? 0 : tok.index + 1;
	    }

		/**
		 * {@inheritDoc}
		 */
		@Override
	    public WME get(int index) {
			for (ListToken t = this; t != null; t = t.next) {	        
	            if (t.index == index) {
	            	return t.wme;
	            }
			}
			
			throw new IndexOutOfBoundsException(index + " > " + this.index);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
	    public DependencySet getDepends(boolean doExplanation) {
			DependencySet ds = DependencySet.INDEPENDENT;
			
			for (ListToken t = this; t != null; t = t.next) {	        
	            ds = ds.union(t.wme.getDepends(), doExplanation );
			}		
			
			return ds;
		}

	    /**
		 * {@inheritDoc}
		 */
	    @Override
	    public boolean dependsOn(int branch) {
	    	for (ListToken t = this; t != null; t = t.next) {	        
	            if (t.wme.dependsOn(branch)) {
	            	return true;
	            }
			}
		    return false;
	    }
	    
	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        sb.append('[');
	        for (ListToken t = this; t != null; t = t.next) {	        
	            sb.append(t.wme.toString());
	            sb.append(',');
			}
	        sb.setCharAt(sb.length() - 1, ']');
	        return sb.toString();
	    }
	}

	private static class ArrayToken extends Token  {
		private WME[] wmes;
		
		public ArrayToken(WME wme, ArrayToken tok) {
			int l = tok == null ? 0 : tok.wmes.length;
		    this.wmes = tok == null ? new WME[1] : Arrays.copyOf(tok.wmes, l + 1);
		    this.wmes[l] = wme;
	    }

		/**
		 * {@inheritDoc}
		 */
		@Override
	    public WME get(int index) {
			if(index >= wmes.length)
				throw new ArrayIndexOutOfBoundsException();
			return wmes[index];
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
	    public DependencySet getDepends(boolean doExplanation) {
			DependencySet ds = DependencySet.INDEPENDENT;
			
			for (WME wme : wmes) {	        
	            ds = ds.union(wme.getDepends(), doExplanation );
			}		
			
			return ds;
		}

	    /**
		 * {@inheritDoc}
		 */
	    @Override
	    public boolean dependsOn(int branch) {
	    	for (WME wme : wmes) {      
	            if (wme.dependsOn(branch)) {
	            	return true;
	            }
			}
		    return false;
	    }
	    
	    @Override
	    public String toString() {
	        return Arrays.toString(wmes);
	    }
	}

    public static Token create(WME wme, Token token) {
//	    return new ListToken(wme, (ListToken) token);
	    return new ArrayToken(wme, (ArrayToken) token);
    }

}