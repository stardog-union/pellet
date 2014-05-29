// Copyright (c) 2006 - 2010, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules;

import org.mindswap.pellet.DependencySet;

import com.clarkparsia.pellet.rules.model.Rule;

public class PartialBinding {
	private Rule rule;
	private VariableBinding binding;
	private DependencySet ds;
	
    public PartialBinding(Rule rule, VariableBinding binding, DependencySet ds) {
	    this.rule = rule;
	    this.binding = binding;
	    this.ds = ds;
    }

	Rule getRule() {
    	return rule;
    }

    VariableBinding getBinding() {
    	return binding;
    }

    DependencySet getDependencySet() {
    	return ds;
    }

    int getBranch() {
    	return ds.max();
    }
}