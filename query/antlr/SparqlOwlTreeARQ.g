tree grammar SparqlOwlTreeARQ;

options {
	language = Java;
	tokenVocab = SparqlOwl;
	ASTLabelType = CommonTree;
}

@header{
package com.clarkparsia.sparqlowl.parser.antlr;

import java.lang.StringBuilder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.sparql.core.Prologue;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Add;
import com.hp.hpl.jena.sparql.expr.E_Bound;
import com.hp.hpl.jena.sparql.expr.E_Datatype;
import com.hp.hpl.jena.sparql.expr.E_Divide;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_Function;
import com.hp.hpl.jena.sparql.expr.E_GreaterThan;
import com.hp.hpl.jena.sparql.expr.E_GreaterThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_IsBlank;
import com.hp.hpl.jena.sparql.expr.E_IsIRI;
import com.hp.hpl.jena.sparql.expr.E_IsLiteral;
import com.hp.hpl.jena.sparql.expr.E_IsURI;
import com.hp.hpl.jena.sparql.expr.E_Lang;
import com.hp.hpl.jena.sparql.expr.E_LangMatches;
import com.hp.hpl.jena.sparql.expr.E_LessThan;
import com.hp.hpl.jena.sparql.expr.E_LessThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.E_LogicalOr;
import com.hp.hpl.jena.sparql.expr.E_Multiply;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.E_Regex;
import com.hp.hpl.jena.sparql.expr.E_SameTerm;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.E_Subtract;
import com.hp.hpl.jena.sparql.expr.E_UnaryMinus;
import com.hp.hpl.jena.sparql.expr.E_UnaryPlus;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.sparql.syntax.TemplateGroup;
import com.hp.hpl.jena.sparql.syntax.TripleCollector;
import com.hp.hpl.jena.sparql.util.LabelToNodeMap;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import org.mindswap.pellet.jena.vocabulary.OWL2;

import static com.clarkparsia.sparqlowl.parser.ParserUtilities.dropFirstAndLast;
import static com.clarkparsia.sparqlowl.parser.ParserUtilities.dropFirstAndLast3;
import static com.clarkparsia.sparqlowl.parser.ParserUtilities.sparqlUnescape;
import static com.clarkparsia.sparqlowl.parser.arq.ARQParserUtilities.createNonNegativeInteger;
import static com.clarkparsia.sparqlowl.parser.arq.ARQParserUtilities.isOWL2Datatype;
import static com.clarkparsia.sparqlowl.parser.arq.ARQParserUtilities.listToTriples;
import static com.clarkparsia.sparqlowl.parser.arq.ARQParserUtilities.XSD_BOOLEAN_FALSE;
import static com.clarkparsia.sparqlowl.parser.arq.ARQParserUtilities.XSD_BOOLEAN_TRUE;
import static com.hp.hpl.jena.sparql.util.ExprUtils.nodeToExpr;
}

@members{

	/**
	 * Used for abbreviated IRI expansion during parsing
	 */
	private Prologue prologue;

	/**
	 * Used for tracking bnodes in where clauses (which are variables)
	 */
	final private LabelToNodeMap labelToNDV = LabelToNodeMap.createVarMap();

	/**
	 * Used for tracking bnodes in construct templates (which aren't variables)
	 */
	final private LabelToNodeMap labelToBNode = LabelToNodeMap.createBNodeMap();

	private boolean inConstructTemplate;

	private Node getAnon() {
		return this.inConstructTemplate ? labelToBNode.allocNode( ) : labelToNDV.allocNode( );
	}

	private Node getAnon(String label) {
		return this.inConstructTemplate ? labelToBNode.asNode( label ) : labelToNDV.asNode( label );
	}

	protected void mismatch(IntStream input, int ttype, BitSet follow)
		throws RecognitionException {
			throw new MismatchedTokenException(ttype, input);
	}

	public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
		throws RecognitionException {
			throw e;
	}

	protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow)
		throws RecognitionException {
			throw new MismatchedTokenException( ttype, input );
	}

	public void emitErrorMessage(String msg) {
		/*
		 * Swallow the error message rather than print on stderr.
		 * This could log the message, but errors are probably more appropriately handled from the exceptions they generate.
		 */
		;
	}
}

@rulecatch {
	catch( RecognitionException rce ) {
		throw rce;
	}
}

/*
 * Manchester 2.1 "objectPropertyIRI"
 */
objectPropertyIRI
	returns [Node p, Collection<Triple> triples]
	:	^(OBJECT_PROPERTY iriRef)
		{
			$p = $iriRef.i;
			$triples = Collections.singleton( new Triple( $p, RDF.Nodes.type, OWL.ObjectProperty.asNode() ) );
		}
	;

/*
 * Manchester 2.1 "dataPropertyIRI"
 */
dataPropertyIRI
	returns [Node p, Collection<Triple> triples]
	:	^(DATA_PROPERTY iriRef)
		{
			$p = $iriRef.i;
			$triples = Collections.singleton( new Triple( $p, RDF.Nodes.type, OWL.DatatypeProperty.asNode() ) );
		}
	;

/*
 * Necessary when property type is ambiguous
 */
objectOrDataPropertyIRI
	returns [Node p]
	:	^(PROPERTY iriRef) { $p = $iriRef.i; }
	;

/*
 * See Manchester 2.3 "inverseObjectProperty"
 */
inverseObjectProperty
	returns [Node p, Collection<Triple> triples]
	:	^(INVERSE_PROPERTY objectPropertyIRI)
		{
			$p = getAnon( );
			$triples = new ArrayList<Triple>( $objectPropertyIRI.triples );
			$triples.add( new Triple( $p, OWL.inverseOf.asNode(), $objectPropertyIRI.p ) );
		}
	;

/*
 * Useful when any property is possible, regardless of type (e.g, in number restrictions)
 */
propertyExpression
	returns [Node p, Collection<Triple> triples]
	:	inverseObjectProperty
		{
			$p = $inverseObjectProperty.p;
			$triples = $inverseObjectProperty.triples;
		}
	|	objectOrDataPropertyIRI
		{
			$p = $objectOrDataPropertyIRI.p;
			$triples = Collections.emptyList();
		}
	;

/*
 * See Manchester 2.3 "objectPropertyExpression"
 */
objectPropertyExpression
	returns [Node p, Collection<Triple> triples]
	:	inverseObjectProperty
		{
			$p = $inverseObjectProperty.p;
			$triples = $inverseObjectProperty.triples;
		}
	|	objectPropertyIRI
		{
			$p = $objectPropertyIRI.p;
			$triples = $objectPropertyIRI.triples;
		}
	;

/*
 * See Manchester 2.1 "Datatype"
 *  FIXME: Manchester supports some datatypes by string reference
 */
datatype
	returns [Node n]
	:	^(DATATYPE iriRef) { $n = $iriRef.i; }
	|	^(DATATYPE INTEGER_TERM) { $n = XSD.integer.asNode(); }
	|	^(DATATYPE DECIMAL_TERM) { $n = XSD.decimal.asNode(); }
	|	^(DATATYPE FLOAT_TERM) { $n = XSD.xfloat.asNode(); }
	|	^(DATATYPE STRING_TERM) { $n = XSD.xstring.asNode(); }
	;

/*
 * See Manchester 2.1 "individual"
 */
individual
	returns [Node i, Collection<Triple> triples]
	:	^(INDIVIDUAL iriRef)
		{
			$i = $iriRef.i;
			// FIXME: Consider adding a type owl:NamedIndividual or owl:Thing triple
			$triples = Collections.emptyList();
		}
	;

/*
 * See Manchester 2.1 "literal"
 */
literal
	returns [Node l]
	:	rdfLiteral
		{ $l = $rdfLiteral.l; }
	|	numericLiteral
		{ $l = $numericLiteral.n; }
	|	booleanLiteral
		{ $l = $booleanLiteral.b; }
	;

/*
 * See Manchester 2.3 "datatypeRestriction"
 */
datatypeRestriction
	returns [Node n, Collection<Triple> triples]
	:	^(DATATYPE_RESTRICTION datatype
			{
				$triples = new ArrayList<Triple>();
				$n = getAnon( );
				$triples.add( new Triple( $n, RDF.Nodes.type, RDFS.Datatype.asNode() ) );
				$triples.add( new Triple( $n, OWL2.onDatatype.asNode(), $datatype.n ) );
				List<Node> facetValues = new ArrayList<Node>();
			}
			(	^(FACET_VALUE facet restrictionValue)
				{
					Node y = getAnon( );
					facetValues.add( y );
					$triples.add( new Triple( y, $facet.n, $restrictionValue.n ) );
				}
			)+
			{
				Node list = listToTriples( facetValues, $triples );
				$triples.add( new Triple( $n, OWL2.withRestrictions.asNode(), list ) );
			}
		)
	;

/*
 * See Manchester 2.3 "facet"
 */
facet
	returns [Node n]
	:	FACET_LENGTH { $n = OWL2.length.asNode(); }
	|	FACET_MINLENGTH { $n = OWL2.minLength.asNode(); }
	|	FACET_MAXLENGTH { $n = OWL2.maxLength.asNode(); }
	|	FACET_PATTERN { $n = OWL2.pattern.asNode(); }
	|	FACET_LANGPATTERN { /* FIXME: langPattern missing */ $n = null; }
	|	FACET_LESS_EQUAL { $n = OWL2.maxInclusive.asNode(); }
	|	FACET_LESS { $n = OWL2.maxExclusive.asNode(); }
	|	FACET_GREATER_EQUAL { $n = OWL2.minInclusive.asNode(); }
	|	FACET_GREATER { $n = OWL2.minExclusive.asNode(); }
	;

/*
 * See Manchester 2.3 "restrictionValue"
 */
restrictionValue
	returns [Node n]
	:	literal { $n = $literal.l; }
	;

/*
 * Manchester "description" and "dataRange"
 */
disjunction
	returns [Node n, Collection<Triple> triples, boolean dr]
	:	^(DISJUNCTION a=disjunction b=disjunction)
		{
			$triples = new ArrayList<Triple>();
			final Node list = listToTriples( Arrays.asList( $a.n, $b.n ), $triples );

			$n = getAnon( );
			$triples.add( new Triple( $n, OWL.unionOf.asNode(), list ) );

			$triples.addAll( $a.triples );
			$triples.addAll( $b.triples );
			
			$dr = $a.dr && $b.dr;
		}
	| conjunction
		{
			$n = $conjunction.n;
			$triples = $conjunction.triples;
			$dr = $conjunction.dr;
		}
	| primary
		{
			$n = $primary.n;
			$triples = $primary.triples;
			$dr = $primary.dr;
		}
	;

/*
 * Manchester "conjunction" and "dataConjunction"
 */
conjunction
	returns [Node n, Collection<Triple> triples, boolean dr]
	:	^(CONJUNCTION a=disjunction b=disjunction)
		{
			$triples = new ArrayList<Triple>();
			final Node list = listToTriples( Arrays.asList( $a.n, $b.n ), $triples );

			$n = getAnon( );
			$triples.add( new Triple( $n, OWL.intersectionOf.asNode(), list ) );

			$triples.addAll( $a.triples );
			$triples.addAll( $b.triples );
			
			$dr = $a.dr && $b.dr;
		}
	;

/*
 * Manchester "conjunction" and "dataPrimary"
 */
primary
	returns [Node n, Collection<Triple> triples, boolean dr]
	:	^(NEGATION disjunction)
		{
			$n = getAnon( );
			$triples = new ArrayList<Triple>();
			$triples.addAll( $disjunction.triples );
			if ( $disjunction.dr ) {
				$triples.add( new Triple( $n, OWL2.datatypeComplementOf.asNode(), $disjunction.n ) );
				$dr = true;
			}
			else
				$triples.add( new Triple( $n, OWL.complementOf.asNode(), $disjunction.n ) );
		}
	|	restriction 
		{
			$n = $restriction.n;
			$triples = $restriction.triples;
			$dr = false;
		}
	|	atomic
		{
			$n = $atomic.n;
			$triples = $atomic.triples;
			$dr = $atomic.dr;
		}
	;

/*
 * Manchester "atomic" and "dataAtomic"
 */
atomic
	returns [Node n, Collection<Triple> triples, boolean dr]
	@init {
		$dr = false;
	}
	:	^(CLASS_OR_DATATYPE iriRef)
		{
			$n = $iriRef.i;
			$triples = Collections.emptyList();
			$dr = isOWL2Datatype( $n );
		}
	| datatype
		{
			$n = $datatype.n;
			$triples = Collections.emptyList();
			$dr = true;
		}
	|	datatypeRestriction
		{
			$n = $datatypeRestriction.n;
			$triples = $datatypeRestriction.triples;
			$dr = true;
		}
	|	^(VALUE_ENUMERATION
			{
				$triples = new ArrayList<Triple>();
				List<Node> ls = new ArrayList<Node>();
				$dr = true;
			}
			( literal { ls.add( $literal.l ); } )+
		)
		{
			Node list = listToTriples( ls, $triples );
			$n = getAnon( );
			$triples.add( new Triple( $n, RDF.Nodes.type, RDFS.Datatype.asNode() ) );
			$triples.add( new Triple( $n, OWL.oneOf.asNode(), list ) );
		}
	|	^(INDIVIDUAL_ENUMERATION
			{
				$triples = new ArrayList<Triple>();
				List<Node> is = new ArrayList<Node>();
			}
			( individual { is.add( $individual.i ); } )+
		)
		{
			Node list = listToTriples( is, $triples );
			$n = getAnon( );
			$triples.add( new Triple( $n, RDF.Nodes.type, OWL.Class.asNode() ) );
			$triples.add( new Triple( $n, OWL.oneOf.asNode(), list ) );
		}
	;

/*
 * Manchester 2.4 "restriction"
 */
restriction
	returns [Node n, Collection<Triple> triples]
	@init {
		$n = getAnon( );
		$triples = new ArrayList<Triple>();
		$triples.add( new Triple( $n, RDF.Nodes.type, OWL.Restriction.asNode() ) );
	}
	:	^(SOME_RESTRICTION propertyExpression disjunction)
		{
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $propertyExpression.p ) );
			$triples.add( new Triple( $n, OWL.someValuesFrom.asNode(), $disjunction.n ) );
			$triples.addAll( $propertyExpression.triples );
			$triples.addAll( $disjunction.triples );
		}
	|	^(ALL_RESTRICTION propertyExpression disjunction)
		{
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $propertyExpression.p ) );
			$triples.add( new Triple( $n, OWL.allValuesFrom.asNode(), $disjunction.n ) );
			$triples.addAll( $propertyExpression.triples );
			$triples.addAll( $disjunction.triples );
		}
	|	^(VALUE_RESTRICTION objectPropertyExpression individual)
		{
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $objectPropertyExpression.p ) );
			$triples.add( new Triple( $n, OWL.hasValue.asNode(), $individual.i ) );
			$triples.addAll( $objectPropertyExpression.triples );
			$triples.addAll( $individual.triples );
		}
	|	^(VALUE_RESTRICTION dataPropertyIRI literal)
		{
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $dataPropertyIRI.p ) );
			$triples.add( new Triple( $n, OWL.hasValue.asNode(), $literal.l ) );
			$triples.addAll( $dataPropertyIRI.triples );
		}
	|	^(SELF_RESTRICTION objectPropertyExpression)
		{
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $objectPropertyExpression.p ) );
			$triples.add( new Triple( $n, OWL2.hasSelf.asNode(), XSD_BOOLEAN_TRUE ) );
			$triples.addAll( $objectPropertyExpression.triples );
		}
	|	^(MIN_NUMBER_RESTRICTION propertyExpression i=INTEGER
		{
				boolean dr = false;
				Node q = null;
		}
			(	disjunction
				{
					dr = $disjunction.dr;
					q = $disjunction.n;
					$triples.addAll( $disjunction.triples );
				} )?
		{
			Node num = createNonNegativeInteger( $i.text );
			
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $propertyExpression.p ) );
			if ( q == null )
				$triples.add( new Triple( $n, OWL.minCardinality.asNode(), num ) );
			else {
				$triples.add( new Triple( $n, OWL2.minQualifiedCardinality.asNode(), num ) );
				$triples.add( new Triple( $n, dr
					? OWL2.onDataRange.asNode()
					: OWL2.onClass.asNode(), q ) );
			}

			$triples.addAll( $propertyExpression.triples );
		}
		)
	|	^(MAX_NUMBER_RESTRICTION propertyExpression i=INTEGER
		{
				boolean dr = false;
				Node q = null;
		}
			(	disjunction
				{
					dr = $disjunction.dr;
					q = $disjunction.n;
					$triples.addAll( $disjunction.triples );
				} )?
		{
			Node num = createNonNegativeInteger( $i.text );
			
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $propertyExpression.p ) );
			if ( q == null )
				$triples.add( new Triple( $n, OWL.maxCardinality.asNode(), num ) );
			else {
				$triples.add( new Triple( $n, OWL2.maxQualifiedCardinality.asNode(), num ) );
				$triples.add( new Triple( $n, dr
					? OWL2.onDataRange.asNode()
					: OWL2.onClass.asNode(), q ) );
			}

			$triples.addAll( $propertyExpression.triples );
		}
		)
	|	^(EXACT_NUMBER_RESTRICTION propertyExpression i=INTEGER
		{
				boolean dr = false;
				Node q = null;
		}
			(	disjunction
				{
					dr = $disjunction.dr;
					q = $disjunction.n;
					$triples.addAll( $disjunction.triples );
				} )?
		{
			Node num = createNonNegativeInteger( $i.text );
			
			$triples.add( new Triple( $n, OWL.onProperty.asNode(), $propertyExpression.p ) );
			if ( q == null )
				$triples.add( new Triple( $n, OWL.cardinality.asNode(), num ) );
			else {
				$triples.add( new Triple( $n, OWL2.qualifiedCardinality.asNode(), num ) );
				$triples.add( new Triple( $n, dr
					? OWL2.onDataRange.asNode()
					: OWL2.onClass.asNode(), q ) );
			}

			$triples.addAll( $propertyExpression.triples );
		}
		)
	;

/*
 * SPARQL A.8[1]
 */
query[Query in]
	returns [Query q]
	@init {
			$q = $in == null ? new Query( ) : $in;
			this.prologue = $q;
			this.inConstructTemplate = false;
	}
	:	^(QUERY
			prologue[$q]
			(	selectQuery[$q]
			|	constructQuery[$q]
			|	describeQuery[$q]
			|	askQuery[$q]
			)
		)
		EOF
	;

/*
 * SPARQL A.8[2]
 */
prologue[Prologue p]
	:	(
			baseDecl
			{ $p.setBaseURI( $baseDecl.base ); }
		)?
		(
			prefixDecl
			{ $p.setPrefix( $prefixDecl.prefix, $prefixDecl.expansion ); }
		)*
	;

/*
 * SPARQL A.8[3]
 */
baseDecl
	returns [String base]
	:	^(BASE_DECL ref=IRI_REF_TERM)
		{ $base = $ref.text; }
	;

/*
 * SPARQL A.8[4]
 */
prefixDecl
	returns [String prefix, String expansion]
	:	^(PREFIX_DECL pname=PNAME_NS ref=IRI_REF_TERM)
	{
		/*
		 * Trim the final ':' off the token matched by PNAME_NS
		 */
		final int n = $pname.text.length();
		$prefix = ( n == 1 )
			? ""
			: $pname.text.substring( 0, n - 1);
		$expansion = $ref.text;
	}
	;

/*
 * SPARQL A.8[5], split over 3 rules
 */
selectQuery[Query q]
	@init {
		$q.setQuerySelectType();
	}
	:	^(SELECT selectModifier[$q]? selectVariableList[$q] datasets[$q]? whereClause[$q] solutionModifier[$q])
	;

selectModifier[Query q]
	:	MODIFIER_DISTINCT
		{ $q.setDistinct( true ); }
	|	MODIFIER_REDUCED
		{ $q.setReduced( true ); }
	;

selectVariableList[Query q]
	:	^(VARS
				(var
				{ $q.addResultVar( $var.v ); }
				)+
			)
	|	ALL_VARS
		{ $q.setQueryResultStar( true ); }
	;

/*
 * SPARQL A.8[6]
 */
constructQuery[Query q]
	@init {
		$q.setQueryConstructType();
	}
	:	^( CONSTRUCT constructTemplate datasets[$q]? whereClause[$q] solutionModifier[$q])
		{ $q.setConstructTemplate( $constructTemplate.t ); }
	;

/*
 * SPARQL A.8[7], split over 2 rules
 */
describeQuery[Query q]
	@init {
		$q.setQueryDescribeType();
	}
	:	^(DESCRIBE describeTargets[q] datasets[$q]? whereClause[$q]? solutionModifier[$q])
	;

describeTargets[Query q]
	:	^(VARS_OR_IRIS
			(varOrIRIref
				{ $q.addDescribeNode( $varOrIRIref.n ); }
			)+
			)
	|	ALL_VARS
		{ $q.setQueryResultStar( true ); }
	;

/*
 * SPARQL A.8[8]
 */
askQuery[Query q]
	@init {
		$q.setQueryAskType();
	}
	:	^(ASK datasets[$q]? whereClause[$q])
	;

/*
 * Use instead of datasetClause* which appears in A.8[5]-[8] to push all datasets under a single tree node
 */
datasets[Query q]
	:	^(DATASETS datasetClause[$q]+)
	;

/*
 * SPARQL A.8[9]
 */
datasetClause[Query q]
	:	defaultGraphClause[$q]
	|	namedGraphClause[$q]
	;

/*
 * SPARQL A.8[10]
 */
defaultGraphClause[Query q]
	:	^(DEFAULT_GRAPH s=sourceSelector )
		{ $q.addGraphURI( $s.s.getURI() ); }
	;

/*
 * SPARQL A.8[11]
 */
namedGraphClause[Query q]
	:	^(NAMED_GRAPH s=sourceSelector )
		{ $q.addNamedGraphURI( $s.s.getURI() ); }
	;

/*
 * SPARQL A.8[12]
 */
sourceSelector
	returns [Node s]
	:	iriRef
		{ $s = $iriRef.i; }
	;

/*
 * SPARQL A.8[13]
 */
whereClause[Query q]
	:	^(WHERE_CLAUSE groupGraphPattern)
		{ $q.setQueryPattern( $groupGraphPattern.e ); }
	;


/*
 * SPARQL A.8[14]
 */
solutionModifier[Query q]
	:	orderClause[q]? limitOffsetClauses[q]?
	;

/*
 * SPARQL A.8[15]
 */
limitOffsetClauses[Query q]
	:	limitClause { q.setLimit( $limitClause.l ); }
		(offsetClause {q.setOffset( $offsetClause.l ); } )?
	|	offsetClause { q.setOffset( $offsetClause.l ); }
		(limitClause { q.setLimit( $limitClause.l ); } )?
	;

/*
 * SPARQL A.8[16]
 */
orderClause[Query q]
	:	^(ORDER_CLAUSE orderCondition[q]+)
	;

/*
 * SPARQL A.8[17]
 */
orderCondition[Query q]
	:	^(ORDER_CONDITION_ASC expression)
		{ q.addOrderBy( $expression.e, Query.ORDER_ASCENDING ); }
	|	^(ORDER_CONDITION_DESC expression)
		{ q.addOrderBy( $expression.e, Query.ORDER_DESCENDING ); }
	|	^(ORDER_CONDITION_UNDEF expression)
		{ q.addOrderBy( $expression.e, Query.ORDER_DEFAULT ); }
	;

/*
 * SPARQL A.8[18]
 */
limitClause
	returns [long l]
	:	^(LIMIT_CLAUSE i=INTEGER)
		{ $l = Long.parseLong( $i.text ); }
	;

/*
 * SPARQL A.8[19]
 */
offsetClause
	returns [long l]
	:	^(OFFSET_CLAUSE i=INTEGER)
		{ $l = Long.parseLong( $i.text ); }
	;

/*
 * SPARQL A.8[20]
 */
groupGraphPattern
	returns [ElementGroup e]
	@init {
		$e = new ElementGroup();
		labelToNDV.clear();
		labelToBNode.clear();
	}
	:	^(GROUP_GRAPH_PATTERN
			( tb1=triplesBlock { $e.addElement( $tb1.e ); } )?
			(
				(	graphPatternNotTriples { $e.addElement( $graphPatternNotTriples.e ); }
				|	filter { $e.addElementFilter( $filter.e ); }
				)
				( tb2=triplesBlock { $e.addElement( $tb2.e ); } )?
			)*
		)
	;

/*
 * SPARQL A.8[21]
 */
triplesBlock
	returns [ElementTriplesBlock e]
	@init {
		$e = new ElementTriplesBlock();
 
	}
	:	triplesSameSubject[$e]+
	;

/*
 * SPARQL A.8[22]
 */
graphPatternNotTriples
	returns [Element e]
	:	optionalGraphPattern { $e = $optionalGraphPattern.e; }
	|	groupOrUnionGraphPattern { $e = $groupOrUnionGraphPattern.e; }
	|	graphGraphPattern { $e = $graphGraphPattern.e; }
	;

/*
 * SPARQL A.8[23]
 */
optionalGraphPattern
	returns [ElementOptional e]
	:	^(OPTIONAL_GRAPH_PATTERN groupGraphPattern)
		{ $e = new ElementOptional( $groupGraphPattern.e );}
	;

/*
 * SPARQL A.8[24]
 */
graphGraphPattern
	returns [ElementNamedGraph e]
	:	^(GRAPH_GRAPH_PATTERN ^(GRAPH_IDENTIFIER varOrIRIref) groupGraphPattern)
		{ $e = new ElementNamedGraph( $varOrIRIref.n, $groupGraphPattern.e ); }
	;

/*
 * SPARQL A.8[25]
 */
groupOrUnionGraphPattern
	returns [Element e]
	:	groupGraphPattern { $e = $groupGraphPattern.e; }
	|	^(UNION_GRAPH_PATTERN a=groupOrUnionGraphPattern b=groupGraphPattern)
		{
			final ElementUnion u = new ElementUnion();
			u.addElement( $a.e );
			u.addElement( $b.e );
			$e = u;
		}
	;

/*
 * SPARQL A.8[26]
 */
filter
	returns [ElementFilter e]
	:	^(FILTER constraint)
		{ $e = new ElementFilter( $constraint.e ); }
	;

/*
 * SPARQL A.8[27]
 */
constraint
	returns [Expr e]
	:	expression { $e = $expression.e; }
	;

/*
 * SPARQL A.8[28]
 */
functionCall
	returns [Expr e]
	:	^(FUNCTION_CALL ^(FUNCTION_IDENTIFIER iriRef) ^(FUNCTION_ARGS argList))
		{ $e = new E_Function( $iriRef.i.getURI(), $argList.l ); }
	;

/*
 * SPARQL A.8[29];
 */
argList
	returns [ExprList l]
	@init { $l = new ExprList(); }
	:	( expression { $l.add( $expression.e ); } )*
	;

/*
 * SPARQL A.8[30];
 */
constructTemplate
	returns [Template t]
	@init {
		TemplateGroup tg = new TemplateGroup();
		$t = tg;
		this.inConstructTemplate = true;
	}
	:	^(CONSTRUCT_TEMPLATE constructTriples[tg]?)
	;
	finally { this.inConstructTemplate = false; }

/*
 * SPARQL A.8[31];
 */
constructTriples[TemplateGroup e]
	:	triplesSameSubject[$e]+
	;

/*
 * SPARQL A.8[32],[34] + addition to support a Manchester Syntax description as the subject
 */
triplesSameSubject[TripleCollector e]
	@init {
		Node s = null;
	}
	:	^(SUBJECT_TRIPLE_GROUP
			(	( ^(SUBJECT
						( varOrTerm
							{ s = $varOrTerm.n; }
						| disjunction
							{
								s = $disjunction.n;
								for ( Triple t : $disjunction.triples )
									$e.addTriple( t );
							}
						)
					)
					m=propertyListNotEmpty )
				{
					for ( Map.Entry<Node,List<Node>> pair : $m.m.entrySet() ) {
						for ( Node o : pair.getValue() )
							$e.addTriple( new Triple( s, pair.getKey(), o ) );
					}
					for ( Triple t : $m.triples )
						$e.addTriple( t );
				}
			|	( ^(SUBJECT triplesNode)
					{
						for ( Triple t : $triplesNode.triples )
							$e.addTriple( t );
						s = $triplesNode.n;
					}
					(	m=propertyListNotEmpty
						{
							for ( Map.Entry<Node,List<Node>> pair : $m.m.entrySet() ) {
								for ( Node o : pair.getValue() )
									$e.addTriple( new Triple( s, pair.getKey(), o ) );
							}
							for ( Triple t : $m.triples )
								$e.addTriple( t );
						}
					)?
				)
			)
		)
	;

/*
 * SPARQL A.8[33]
 */
propertyListNotEmpty
	returns [Map<Node,List<Node>> m, Collection<Triple> triples]
	@init {
		$m = new LinkedHashMap<Node,List<Node>>();
		$triples = new ArrayList<Triple>();
	}
	:	(
			^(VERB_PAIR_GROUP verb objectList)
			{
				List<Node> l = $m.get( $verb.v );
				if ( l == null )
					$m.put( $verb.v, $objectList.l );
				else
					l.addAll( $objectList.l );
				$triples.addAll( $objectList.triples );
			}
		)+
	;

/*
 * SPARQL A.8[35]
 */
objectList
	returns [List<Node> l, Collection<Triple> triples]
	@init {
		$l = new ArrayList<Node>();
		$triples = new ArrayList<Triple>();
	}
	:	( object
			{
				$l.add( $object.n );
				$triples.addAll( $object.triples );
			}
		)+
	;

/*
 * SPARQL A.8[36] + addition to support Manchester Syntax description.
 * Syntactic predicate looks ahead one token to resolve ambiguities that things like :p some :q would introduce
 */
object
	returns [Node n, Collection<Triple> triples]
	:	^(OBJECT graphNode)
		{
			$n = $graphNode.n;
			$triples = $graphNode.triples;
		}
	|	^(OBJECT disjunction)
		{
			$n = $disjunction.n;
			$triples = $disjunction.triples;
		}
	
	;

/*
 * SPARQL A.8[37]
 */
verb
	returns [Node v]
	:	^(VERB varOrIRIref) { $v = $varOrIRIref.n; }
	|	^(VERB RDF_TYPE) { $v = RDF.Nodes.type; }
	;

/*
 * SPARQL A.8[38]
 */
triplesNode
	returns [Node n, Collection<Triple> triples]
	:	collection
		{
			$n = $collection.n;
			$triples = $collection.triples;
		}
	|	blankNodePropertyList
	;

/*
 * SPARQL A.8[39]
 */
blankNodePropertyList
	returns [Node n, Collection<Triple> triples]
	:	^(BNODE_PROPERTY_LIST m=propertyListNotEmpty)
		{
			$n = getAnon( );
			for ( Map.Entry<Node,List<Node>> pair : $m.m.entrySet() ) {
				for ( Node o : pair.getValue() )
					$triples.add( new Triple( $n, pair.getKey(), o ) );
			}
			$triples.addAll( $m.triples );
		}
	;

/*
 * SPARQL A.8[40]
 */
collection
	returns [Node n, Collection<Triple> triples]
	:	^(COLLECTION
			{
				$triples = new ArrayList<Triple>();
				List<Node> ln = new ArrayList<Node>();
			}
			(	graphNode
				{
					$triples.addAll( $graphNode.triples );
					ln.add( $graphNode.n );
				}
			)+
			{
				$n = listToTriples( ln, $triples );
			}
		)
	;

/*
 * See SPARQL A.8[92]
 */
emptyCollection
	returns [Node n]
	:	COLLECTION { $n = RDF.Nodes.nil; }
	;

/*
 * SPARQL A.8[41]
 */
graphNode
	returns [Node n, Collection<Triple> triples]
	:	varOrTerm
		{
			$n = $varOrTerm.n;
			$triples = Collections.emptyList();
		}
	|	triplesNode
		{
			$n = $triplesNode.n;
			$triples = $triplesNode.triples;
		}
	;

/*
 * SPARQL A.8[42]
 */
varOrTerm
	returns [Node n]
	:	var
		{ $n = $var.v; }
	|	graphTerm
		{ $n = $graphTerm.n; }
	;

/*
 * SPARQL A.8[43]
 */
varOrIRIref
	returns [Node n]
	:	var { $n = $var.v; }
	|	iriRef { $n = $iriRef.i; }
	;

/*
 * SPARQL A.8[44]
 */
var
	returns [Node v]
	:	^(VARIABLE (t=VAR1|t=VAR2))
		{ $v = Var.alloc( $t.text ); }
	;

/*
 * SPARQL A.8[45], modified to use the 'literal' and 'emptyCollection' rules
 */
graphTerm
	returns [Node n]
	:	iriRef
		{ $n = $iriRef.i; }
	|	literal
		{ $n = $literal.l; }
	|	blankNode
		{ $n = $blankNode.b; }
	|	emptyCollection
		{ $n = $emptyCollection.n; }
	;


/*
 * SPARQL A.8[46]
 */
expression
	returns [Expr e]
	:	conditionalOrExpression
		{ $e = $conditionalOrExpression.e; }
	|	conditionalAndExpression
		{ $e = $conditionalAndExpression.e; }
	| valueLogical
		{ $e = $valueLogical.e; }
	;

/*
 * SPARQL A.8[47]
 */
conditionalOrExpression
	returns [Expr e]
	:	^(CONDITIONAL_EXPRESSION_OR a=expression b=expression)
		{ $e = new E_LogicalOr( $a.e, $b.e ); }
	;

/*
 * SPARQL A.8[48]
 */
conditionalAndExpression
	returns [Expr e]
	:	^(CONDITIONAL_EXPRESSION_AND a=expression b=expression)
		{ $e = new E_LogicalAnd( $a.e, $b.e ); }
	;

/*
 * SPARQL A.8[49]
 */
valueLogical
	returns [Expr e]
	:	relationalExpression
		{ $e = $relationalExpression.e; }
	;

/*
 * SPARQL A.8[50]
 */
relationalExpression
	returns [Expr e]
	: numericExpression
		{ $e = $numericExpression.e; }
	|	^(RELATIONAL_EQUAL a=numericExpression b=numericExpression)
		{ $e = new E_Equals( $a.e, $b.e ); }
	|	^(RELATIONAL_NOT_EQUAL a=numericExpression b=numericExpression)
		{ $e = new E_NotEquals( $a.e, $b.e ); }
	|	^(RELATIONAL_LESS a=numericExpression b=numericExpression)
		{ $e = new E_LessThan( $a.e, $b.e ); }
	|	^(RELATIONAL_GREATER a=numericExpression b=numericExpression)
		{ $e = new E_GreaterThan( $a.e, $b.e ); }
	|	^(RELATIONAL_LESS_EQUAL a=numericExpression b=numericExpression)
		{ $e = new E_LessThanOrEqual( $a.e, $b.e ); }
	|	^(RELATIONAL_GREATER_EQUAL a=numericExpression b=numericExpression)
		{ $e = new E_GreaterThanOrEqual( $a.e, $b.e ); }
	;

/*
 * SPARQL A.8[51]
 */
numericExpression
	returns [Expr e]
	:	additiveExpression
		{ $e = $additiveExpression.e; }
	|	multiplicativeExpression
		{ $e = $multiplicativeExpression.e; }
	| unaryExpression
		{ $e = $unaryExpression.e; }
	;

/*
 * SPARQL A.8[52]
 */
additiveExpression
	returns [Expr e]
	:	^(NUMERIC_EXPRESSION_ADD a=numericExpression b=numericExpression)
		{ $e = new E_Add( $a.e, $b.e ); }
	|	^(NUMERIC_EXPRESSION_SUBTRACT a=numericExpression b=numericExpression)
		{ $e = new E_Subtract( $a.e, $b.e ); }
	;

/*
 * SPARQL A.8[53]
 */
multiplicativeExpression
	returns [Expr e]
	:	^(NUMERIC_EXPRESSION_MULTIPLY a=numericExpression b=numericExpression )
		{ $e = new E_Multiply( $a.e, $b.e ); }
	|	^(NUMERIC_EXPRESSION_DIVIDE a=numericExpression b=numericExpression )
		{ $e = new E_Divide( $a.e, $b.e ); }
	;

/*
 * SPARQL A.8[54]
 */
unaryExpression
	returns [Expr e]
	:	^(UNARY_EXPRESSION_NOT primaryExpression)
		{ $e = new E_LogicalNot( $primaryExpression.e ); }
	|	^(UNARY_EXPRESSION_POSITIVE primaryExpression)
		{ $e = new E_UnaryPlus( $primaryExpression.e ); }
	|	^(UNARY_EXPRESSION_NEGATIVE primaryExpression)
		{ $e = new E_UnaryMinus( $primaryExpression.e ); }
	|	primaryExpression
		{ $e = $primaryExpression.e; }
	;

/*
 * SPARQL A.8[55]
 *  modified to use 'literal' rule
 *  bracketted expression dropped because it is synonymous with expression in the AST
 */
primaryExpression
	returns [Expr e]
	:	builtInCall { $e = $builtInCall.e; }
	|	iriRefOrFunction { $e = $iriRefOrFunction.e; }
	|	literal { $e = nodeToExpr( $literal.l ); }
	|	var { $e = nodeToExpr( $var.v ); }
	;

/*
 * SPARQL A.8[57]
 */
builtInCall
	returns [Expr e]
	:	^(BUILTIN_STR expression)
		{ $e = new E_Str( $expression.e ); }
	|	^(BUILTIN_LANG expression)
		{ $e = new E_Lang( $expression.e ); }
	|	^(BUILTIN_LANGMATCHES a=expression b=expression)
		{ $e = new E_LangMatches( $a.e, $b.e ); }
	|	^(BUILTIN_DATATYPE expression)
		{ $e = new E_Datatype( $expression.e ); }
	|	^(BUILTIN_BOUND var)
		{ $e = new E_Bound( nodeToExpr( $var.v ) ); }
	|	^(BUILTIN_SAME_TERM a=expression b=expression)
		{ $e = new E_SameTerm( $a.e, $b.e ); }
	|	^(BUILTIN_IS_IRI expression)
		{ $e = new E_IsIRI( $expression.e ); }
	|	^(BUILTIN_IS_URI expression)
		{ $e = new E_IsURI( $expression.e ); }
	|	^(BUILTIN_IS_BLANK expression)
		{ $e = new E_IsBlank( $expression.e ); }
	|	^(BUILTIN_IS_LITERAL expression)
		{ $e = new E_IsLiteral( $expression.e ); }
	|	regexExpression
		{ $e = $regexExpression.e; }
	;

/*
 * SPARQL A.8[58]
 */
regexExpression
	returns [Expr e]
	:	^(BUILTIN_REGEX_BINARY a=expression b=expression )
		{ $e = new E_Regex( $a.e, $b.e, null ); }
	|	^(BUILTIN_REGEX_TERNARY a=expression b=expression c=expression)
		{ $e = new E_Regex( $a.e, $b.e, $c.e ); }
	;

/*
 * SPARQL A.8[59]
 */
iriRefOrFunction
	returns [Expr e]
	:	iriRef { $e = nodeToExpr( $iriRef.i ); }
	|	functionCall { $e = $functionCall.e; }
	;


/*
 * SPARQL A.8[60]
 */
rdfLiteral
	returns [Node l]
	:	^(LITERAL_PLAIN string)
		{ $l = Node.createLiteral( $string.s ); }
	| ^(LITERAL_LANG string lang=LANGTAG)
		{ $l = Node.createLiteral( $string.s, $lang.text, false ); }
	|	^(LITERAL_TYPED string iriRef)
		{
			RDFDatatype dType = TypeMapper.getInstance().getSafeTypeByName( $iriRef.i.getURI() );
			$l = Node.createLiteral( $string.s, null, dType );
		}
	;

/*
 * SPARQL A.8[61]-[64]
 *  See also SPARQL 4.1.2 for datatype mapping
 */
numericLiteral
	returns [Node n]
	@init {
		String s = null;
		RDFDatatype t = null;
	}
	:	(	^(LITERAL_INTEGER
				( INTEGER { s = $INTEGER.text; }
				| POSITIVE_INTEGER { s = $POSITIVE_INTEGER.text; }
				| NEGATIVE_INTEGER { s = $NEGATIVE_INTEGER.text; }
				)
			)
			{ t = XSDDatatype.XSDinteger ; }
		|	^(LITERAL_DECIMAL
				( DECIMAL { s = $DECIMAL.text; }
				| POSITIVE_DECIMAL { s = $POSITIVE_DECIMAL.text; }
				| NEGATIVE_DECIMAL { s = $NEGATIVE_DECIMAL.text; }
				)
			)
			{ t = XSDDatatype.XSDdecimal ; }
		|	^(LITERAL_DOUBLE
				( DOUBLE { s = $DOUBLE.text; }
				| POSITIVE_DOUBLE { s = $POSITIVE_DOUBLE.text; }
				| NEGATIVE_DOUBLE { s = $NEGATIVE_DOUBLE.text; }
				)
			)
			{ t = XSDDatatype.XSDdouble ; }
		)
		{ $n = Node.createLiteral( s, null, t ); }
	;

/*
 * SPARQL A.8[65]
 */
booleanLiteral
	returns [Node b]
	:	LITERAL_BOOLEAN_TRUE { $b = XSD_BOOLEAN_TRUE; }
	|	LITERAL_BOOLEAN_FALSE { $b = XSD_BOOLEAN_FALSE; }
	;

/*
 * SPARQL A.8[66]
 */
string
	returns [String s]
	:	(	l=STRING_LITERAL1 { $s = dropFirstAndLast( $l.text ); }
		|	l=STRING_LITERAL2 { $s = dropFirstAndLast( $l.text ); }
		|	l=STRING_LITERAL_LONG1 { $s = dropFirstAndLast3( $l.text ); }
		|	l=STRING_LITERAL_LONG2 { $s = dropFirstAndLast3( $l.text ); }
		)
		{ $s = sparqlUnescape( $s ); }
	;

/*
 * SPARQL A.8[67]
 */
 iriRef
	returns [Node i]
	:	^(IRI_REF ref=IRI_REF_TERM)
		{ $i = Node.createURI( $ref.text ); }
	| ^(PREFIXED_NAME ( p=PNAME_LN | p=PNAME_NS ) )
		{
			String resolved = this.prologue.expandPrefixedName( $p.text );
			// FIXME: Null case
			$i = Node.createURI( resolved );
		}
	;

/*
 * SPARQL A.8[69]
 */
blankNode
	returns [Node b]
	:	^(BLANK_NODE label=BLANK_NODE_LABEL)
		{ $b = getAnon( $label.text ); }
	|	BLANK_NODE
		{ $b = getAnon( ); }
	;