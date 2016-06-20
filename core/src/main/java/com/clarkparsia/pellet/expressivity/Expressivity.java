// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package com.clarkparsia.pellet.expressivity;

import java.util.HashSet;
import java.util.Set;
import openllet.aterm.ATermAppl;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin, Harris Lin
 */
public class Expressivity
{
	/**
	 * not (owl:complementOf) is used directly or indirectly
	 */
	private boolean hasNegation = false;
	private boolean hasAllValues = false;
	private boolean hasDisjointClasses = false;

	/**
	 * An inverse property has been defined or a property has been defined as InverseFunctional
	 */
	private boolean hasInverse = false;
	private boolean hasFunctionality = false;
	private boolean hasCardinality = false;
	private boolean hasCardinalityQ = false;
	private boolean hasFunctionalityD = false;
	private boolean hasCardinalityD = false;
	private boolean hasTransitivity = false;
	private boolean hasRoleHierarchy = false;
	private boolean hasReflexivity = false;
	private boolean hasIrreflexivity = false;
	private boolean hasDisjointRoles = false;
	private boolean hasAsymmetry = false;
	private boolean hasComplexSubRoles = false;
	private boolean hasDatatype = false;
	private boolean hasUserDefinedDatatype = false;

	private boolean hasKeys = false;

	private boolean hasDomain = false;
	private boolean hasRange = false;

	private boolean hasIndividual = false;
	/**
	 * The set of individuals in the ABox that have been used as nominals, i.e. in an owl:oneOf enumeration or target of owl:hasValue restriction
	 */
	private Set<ATermAppl> nominals = new HashSet<>();

	private Set<ATermAppl> anonInverses = new HashSet<>();

	public Expressivity()
	{
	}

	public Expressivity(final Expressivity other)
	{
		hasNegation = other.hasNegation;
		hasAllValues = other.hasAllValues;
		hasDisjointClasses = other.hasDisjointClasses;
		hasInverse = other.hasInverse;
		hasFunctionality = other.hasFunctionality;
		hasCardinality = other.hasCardinality;
		hasCardinalityQ = other.hasCardinalityQ;
		hasFunctionalityD = other.hasFunctionalityD;
		hasCardinalityD = other.hasCardinalityD;
		hasTransitivity = other.hasTransitivity;
		hasRoleHierarchy = other.hasRoleHierarchy;
		hasReflexivity = other.hasReflexivity;
		hasIrreflexivity = other.hasIrreflexivity;
		hasDisjointRoles = other.hasDisjointRoles;
		hasAsymmetry = other.hasAsymmetry;
		hasComplexSubRoles = other.hasComplexSubRoles;
		hasDatatype = other.hasDatatype;
		hasKeys = other.hasKeys;
		hasDomain = other.hasDomain;
		hasRange = other.hasRange;
		hasIndividual = other.hasIndividual;
		nominals = new HashSet<>(other.nominals);
		anonInverses = new HashSet<>(other.anonInverses);
	}

	public boolean isEL()
	{
		return !hasNegation && !hasAllValues && !hasInverse && !hasFunctionality && !hasCardinality && !hasCardinalityQ && !hasFunctionalityD && !hasCardinalityD && !hasIrreflexivity && !hasDisjointRoles && !hasAsymmetry && !hasDatatype && !hasKeys && !hasIndividual && nominals.isEmpty();
	}

	@Override
	public String toString()
	{
		String dl = "";

		if (isEL())
		{
			dl = "EL";

			if (hasComplexSubRoles || hasReflexivity || hasDomain || hasRange || hasDisjointClasses)
				dl += "+";
			else
				if (hasRoleHierarchy)
					dl += "H";
		}
		else
		{
			dl = "AL";

			if (hasNegation)
				dl = "ALC";

			if (hasTransitivity)
				dl += "R+";

			if (dl.equals("ALCR+"))
				dl = "S";

			if (hasComplexSubRoles)
				dl = "SR";
			else
				if (hasRoleHierarchy)
					dl += "H";

			if (hasNominal())
				dl += "O";

			if (hasInverse)
				dl += "I";

			if (hasCardinalityQ)
				dl += "Q";
			else
				if (hasCardinality)
					dl += "N";
				else
					if (hasFunctionality)
						dl += "F";

			if (hasDatatype)
				if (hasKeys)
					dl += "(Dk)";
				else
					dl += "(D)";
		}

		return dl;
	}

	/**
	 * @return Returns the hasNegation.
	 */
	public boolean hasNegation()
	{
		return hasNegation;
	}

	public void setHasNegation(final boolean v)
	{
		hasNegation = v;
	}

	/**
	 * @return Returns the hasAllValues.
	 */
	public boolean hasAllValues()
	{
		return hasAllValues;
	}

	public void setHasAllValues(final boolean v)
	{
		hasAllValues = v;
	}

	/**
	 * @return Returns the hasDisjointClasses.
	 */
	public boolean hasDisjointClasses()
	{
		return hasDisjointClasses;
	}

	public void setHasDisjointClasses(final boolean v)
	{
		hasDisjointClasses = v;
	}

	/**
	 * @return Returns the hasInverse.
	 */
	public boolean hasInverse()
	{
		return hasInverse;
	}

	public void setHasInverse(final boolean v)
	{
		hasInverse = v;
	}

	/**
	 * @return Returns the hasFunctionality.
	 */
	public boolean hasFunctionality()
	{
		return hasFunctionality;
	}

	public void setHasFunctionality(final boolean v)
	{
		hasFunctionality = v;
	}

	/**
	 * @return Returns the hasCardinality.
	 */
	public boolean hasCardinality()
	{
		return hasCardinality;
	}

	public void setHasCardinality(final boolean v)
	{
		hasCardinality = v;
	}

	/**
	 * @return Returns the hasCardinality.
	 */
	public boolean hasCardinalityQ()
	{
		return hasCardinalityQ;
	}

	public void setHasCardinalityQ(final boolean v)
	{
		hasCardinalityQ = v;
	}

	/**
	 * Returns true if a cardinality restriction (less than or equal to 1) is defined on any datatype property
	 */
	public boolean hasFunctionalityD()
	{
		return hasFunctionalityD;
	}

	public void setHasFunctionalityD(final boolean v)
	{
		hasFunctionalityD = v;
	}

	/**
	 * Returns true if a cardinality restriction (greater than 1) is defined on any datatype property
	 */
	public boolean hasCardinalityD()
	{
		return hasCardinalityD;
	}

	public void setHasCardinalityD(final boolean v)
	{
		hasCardinalityD = v;
	}

	/**
	 * @return Returns the hasTransitivity.
	 */
	public boolean hasTransitivity()
	{
		return hasTransitivity;
	}

	public void setHasTransitivity(final boolean v)
	{
		hasTransitivity = v;
	}

	/**
	 * @return Returns the hasRoleHierarchy.
	 */
	public boolean hasRoleHierarchy()
	{
		return hasRoleHierarchy;
	}

	public void setHasRoleHierarchy(final boolean v)
	{
		hasRoleHierarchy = v;
	}

	public boolean hasReflexivity()
	{
		return hasReflexivity;
	}

	public void setHasReflexivity(final boolean v)
	{
		hasReflexivity = v;
	}

	public boolean hasIrreflexivity()
	{
		return hasIrreflexivity;
	}

	public void setHasIrreflexivity(final boolean v)
	{
		hasIrreflexivity = v;
	}

	public boolean hasDisjointRoles()
	{
		return hasDisjointRoles;
	}

	public void setHasDisjointRoles(final boolean v)
	{
		hasDisjointRoles = v;
	}

	/**
	 * @deprecated Use {@link #hasAsymmmetry()}
	 */
	@Deprecated
	public boolean hasAntiSymmmetry()
	{
		return hasAsymmetry;
	}

	public boolean hasAsymmmetry()
	{
		return hasAsymmetry;
	}

	/**
	 * @deprecated Use {@link #setHasAsymmetry(boolean)}
	 */
	@Deprecated
	public void setHasAntiSymmetry(final boolean v)
	{
		hasAsymmetry = v;
	}

	public void setHasAsymmetry(final boolean v)
	{
		hasAsymmetry = v;
	}

	public boolean hasComplexSubRoles()
	{
		return hasComplexSubRoles;
	}

	public void setHasComplexSubRoles(final boolean v)
	{
		hasComplexSubRoles = v;
	}

	/**
	 * @return Returns the hasDatatype.
	 */
	public boolean hasDatatype()
	{
		return hasDatatype;
	}

	public void setHasDatatype(final boolean v)
	{
		hasDatatype = v;
	}

	public boolean hasUserDefinedDatatype()
	{
		return hasUserDefinedDatatype;
	}

	public void setHasUserDefinedDatatype(final boolean v)
	{
		if (v)
			setHasDatatype(true);
		hasUserDefinedDatatype = v;
	}

	public boolean hasKeys()
	{
		return hasKeys;
	}

	public void setHasKeys(final boolean v)
	{
		hasKeys = v;
	}

	public boolean hasDomain()
	{
		return hasDomain;
	}

	public void setHasDomain(final boolean v)
	{
		hasDomain = v;
	}

	public boolean hasRange()
	{
		return hasRange;
	}

	public void setHasRange(final boolean v)
	{
		hasRange = v;
	}

	public boolean hasIndividual()
	{
		return hasIndividual;
	}

	public void setHasIndividual(final boolean v)
	{
		hasIndividual = v;
	}

	public boolean hasNominal()
	{
		return !nominals.isEmpty();
	}

	public Set<ATermAppl> getNominals()
	{
		return nominals;
	}

	public void addNominal(final ATermAppl n)
	{
		nominals.add(n);
	}

	/**
	 * Returns every property p such that inv(p) is used in an axiom in the KB. The named inverses are not considered.
	 *
	 * @return the set of properties whose anonymous inverse is used
	 */
	public Set<ATermAppl> getAnonInverses()
	{
		return anonInverses;
	}

	public void addAnonInverse(final ATermAppl p)
	{
		anonInverses.add(p);
	}
}
