// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import org.mindswap.pellet.Node;
import org.mindswap.pellet.utils.ATermUtils;

public abstract class NodeProvider {
	public abstract Node getNode(WME wme, Token token);	
	
	public static class ConstantNodeProvider extends NodeProvider {
		private final Node node;
		
		public ConstantNodeProvider(Node node) {
	        this.node = node;
        }

		@Override
		public Node getNode(WME wme, Token token) {
			return node;
		}
		@Override
        public int hashCode() {
	        return node.getName().hashCode();
        }

		@Override
        public boolean equals(Object obj) {
	        if (this == obj) {
		        return true;
	        }
	        if (!(obj instanceof ConstantNodeProvider)) {
		        return false;
	        }
	        ConstantNodeProvider other = (ConstantNodeProvider) obj;
	        return node.getName().equals(other.node.getName());
        }
		
		@Override
		public String toString() {
		    return ATermUtils.toString(node.getName());
		}
	}
	
	public static class TokenNodeProvider extends NodeProvider {
		private final int indexWME;
		private final int indexArg;
		
		public TokenNodeProvider(int indexWME, int indexArg) {
	        this.indexWME = indexWME;
	        this.indexArg = indexArg;
        }

		@Override
		public Node getNode(WME wme, Token token) {
			return token.get(indexWME).getArg(indexArg);
		}
		
		@Override
        public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + indexArg;
	        result = prime * result + indexWME;
	        return result;
        }

		@Override
        public boolean equals(Object obj) {
	        if (this == obj) {
		        return true;
	        }
	        if (!(obj instanceof TokenNodeProvider)) {
		        return false;
	        }
	        TokenNodeProvider other = (TokenNodeProvider) obj;
	        return (indexArg != other.indexArg) && (indexWME != other.indexWME);
        }

		@Override
		public String toString() {
		    return "token["+ indexWME + "]." + indexArg;
		}
	}
	
	public static class WMENodeProvider extends NodeProvider {
		private final int indexArg;
		
		public WMENodeProvider(int indexArg) {
	        this.indexArg = indexArg;
        }

		@Override
		public Node getNode(WME wme, Token token) {
			return wme.getArg(indexArg);
		}
		
		public int getIndexArg() {
			return indexArg;
		}

		@Override
        public int hashCode() {
	        return indexArg;
        }

		@Override
        public boolean equals(Object obj) {
	        if (this == obj) {
		        return true;
	        }
	        if (!(obj instanceof WMENodeProvider)) {
		        return false;
	        }
	        WMENodeProvider other = (WMENodeProvider) obj;
	        return indexArg == other.indexArg;
        }

		@Override
		public String toString() {
		    return "tuple." + indexArg;
		}
	}
}