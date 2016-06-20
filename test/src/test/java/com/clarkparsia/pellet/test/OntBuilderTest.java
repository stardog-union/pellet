/**
 *
 */
package com.clarkparsia.pellet.test;

import static com.clarkparsia.pellet.utils.TermFactory.term;

import com.clarkparsia.pellet.rules.model.AtomIConstant;
import com.clarkparsia.pellet.rules.model.ClassAtom;
import com.clarkparsia.pellet.rules.model.Rule;
import com.clarkparsia.pellet.utils.OntBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import openllet.aterm.ATermAppl;
import org.junit.Assert;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.utils.ATermUtils;

/**
 * @author Pavel Klinov
 */
public class OntBuilderTest
{

	// tests that the build looks up individuals in the original KB
	@Test
	public void testLookupIndividuals()
	{
		PelletOptions.KEEP_ABOX_ASSERTIONS = true;

		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl C = term("C");
		final ATermAppl D = term("D");
		final ATermAppl i = term("i");
		final ATermAppl j = term("j");

		kb.addClass(C);
		kb.addClass(D);
		kb.addIndividual(i);
		kb.addIndividual(j);
		kb.addSubClass(C, D);
		kb.addType(i, C);
		kb.addType(j, D);
		final Rule rule = new Rule(Collections.singleton(new ClassAtom(C, new AtomIConstant(i))), Collections.singleton(new ClassAtom(D, new AtomIConstant(j))));

		kb.addRule(rule);

		final OntBuilder builder = new OntBuilder(kb);

		final Set<ATermAppl> rules = new HashSet<>();

		rules.add(ATermUtils.makeRule(new ATermAppl[] { ATermUtils.makeTypeAtom(i, C) }, new ATermAppl[] { ATermUtils.makeTypeAtom(j, D) }));

		final KnowledgeBase copy = builder.build(rules);

		Assert.assertEquals(1, copy.getRules().size());
	}

}
