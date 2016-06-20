package com.clarkparsia.pellet.test.query;

import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.DatatypeAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.DomainAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.InverseOfAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.ObjectPropertyAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.PropertyValueAtom;
import static com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory.RangeAtom;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.BOTTOM_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_DATA_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.TOP_OBJECT_PROPERTY;
import static com.clarkparsia.pellet.utils.TermFactory.literal;
import static com.clarkparsia.pellet.utils.TermFactory.var;

import com.clarkparsia.pellet.datatypes.Datatypes;
import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.parser.ARQParser;
import openllet.aterm.ATermAppl;
import org.junit.Test;
import org.mindswap.pellet.utils.Namespaces;

public class TestMiscQueries extends AbstractQueryTest
{

	@Test
	public void domainQuery1()
	{
		classes(_C, _D);
		objectProperties(_p, _q);
		dataProperties(_r);

		_kb.addSubProperty(_q, _p);
		_kb.addDomain(_p, _C);

		final ATermAppl pv = var("pv");
		final ATermAppl cv = var("cv");

		final Query query = query(select(pv, cv), where(DomainAtom(pv, cv)));

		testQuery(query, new ATermAppl[][] { { _p, TOP }, { _q, TOP }, { _r, TOP }, { TOP_OBJECT_PROPERTY, TOP }, { TOP_DATA_PROPERTY, TOP }, { BOTTOM_OBJECT_PROPERTY, TOP }, { BOTTOM_DATA_PROPERTY, TOP }, { BOTTOM_DATA_PROPERTY, BOTTOM }, { BOTTOM_OBJECT_PROPERTY, BOTTOM }, { _p, _C }, { _q, _C }, { BOTTOM_DATA_PROPERTY, _C }, { BOTTOM_OBJECT_PROPERTY, _C }, { BOTTOM_DATA_PROPERTY, _D }, { BOTTOM_OBJECT_PROPERTY, _D } });

	}

	@Test
	public void domainQuery2()
	{
		classes(_C, _D);
		objectProperties(_p, _q);
		dataProperties(_r);

		_kb.addSubProperty(_q, _p);
		_kb.addDomain(_p, _C);

		final ATermAppl cv = var("cv");

		final Query query = query(select(cv), where(DomainAtom(_q, cv)));

		testQuery(query, new ATermAppl[][] { { TOP }, { _C } });

	}

	@Test
	public void domainQuery3()
	{
		classes(_C, _D);
		objectProperties(_p, _q);
		dataProperties(_r);

		_kb.addSubProperty(_q, _p);
		_kb.addDomain(_p, _C);

		final ATermAppl pv = var("pv");

		final Query query = query(select(pv), where(DomainAtom(pv, _C)));

		testQuery(query, new ATermAppl[][] { { _p }, { _q }, { BOTTOM_OBJECT_PROPERTY }, { BOTTOM_DATA_PROPERTY } });

	}

	@Test
	public void rangeQuery1()
	{
		classes(_C, _D);
		objectProperties(_p, _q);
		dataProperties(_r);

		_kb.addSubProperty(_q, _p);
		_kb.addRange(_p, _C);

		final ATermAppl pv = var("pv");
		final ATermAppl cv = var("cv");

		final Query query = query(select(pv, cv), where(RangeAtom(pv, cv), ObjectPropertyAtom(pv)));

		testQuery(query, new ATermAppl[][] { { _p, TOP }, { _q, TOP }, { TOP_OBJECT_PROPERTY, TOP }, { BOTTOM_OBJECT_PROPERTY, TOP }, { BOTTOM_OBJECT_PROPERTY, BOTTOM }, { _p, _C }, { _q, _C }, { BOTTOM_OBJECT_PROPERTY, _C }, { BOTTOM_OBJECT_PROPERTY, _D } });

	}

	@Test
	public void rangeQuery2()
	{
		classes(_C, _D);
		objectProperties(_p, _q);
		dataProperties(_r);

		_kb.addSubProperty(_q, _p);
		_kb.addRange(_p, _C);

		final ATermAppl cv = var("cv");

		final Query query = query(select(cv), where(RangeAtom(_q, cv)));

		testQuery(query, new ATermAppl[][] { { TOP }, { _C } });

	}

	@Test
	public void datatypeQuery()
	{
		dataProperties(_p);
		individuals(_a, _b, _c);

		_kb.addPropertyValue(_p, _a, literal(3));
		_kb.addPropertyValue(_p, _b, literal(300));
		_kb.addPropertyValue(_p, _b, literal("3"));

		final Query query1 = query(select(x), where(PropertyValueAtom(x, _p, y), DatatypeAtom(y, Datatypes.INTEGER)));

		testQuery(query1, new ATermAppl[][] { { _a }, { _b } });

		final Query query2 = query(select(x), where(PropertyValueAtom(x, _p, y), DatatypeAtom(y, Datatypes.BYTE)));

		testQuery(query2, new ATermAppl[][] { { _a } });

	}

	@Test
	public void classQuery()
	{
		classes(_A, _B, _C);

		final Query query1 = new ARQParser().parse("PREFIX rdf: <" + Namespaces.RDF + "> " + "PREFIX owl: <" + Namespaces.OWL + "> " + "SELECT ?c WHERE { ?c rdf:type owl:Class }", _kb);

		testQuery(query1, new ATermAppl[][] { { _A }, { _B }, { _C }, { TOP }, { BOTTOM } });
	}

	@Test
	public void inverseQuery()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addInverseProperty(_q, _p);
		_kb.addSymmetricProperty(_r);

		final ATermAppl v = var("v");

		final Query query = query(select(v), where(InverseOfAtom(_q, v)));

		testQuery(query, new ATermAppl[][] { { _p } });

	}

	@Test
	public void symmetricQuery()
	{
		classes(_C, _D);
		objectProperties(_p, _q, _r);

		_kb.addInverseProperty(_q, _p);
		_kb.addSymmetricProperty(_r);

		final ATermAppl v = var("v");

		final Query query = query(select(v), where(InverseOfAtom(v, v)));

		testQuery(query, new ATermAppl[][] { { _r }, { TOP_OBJECT_PROPERTY }, { BOTTOM_OBJECT_PROPERTY } });

	}
}
