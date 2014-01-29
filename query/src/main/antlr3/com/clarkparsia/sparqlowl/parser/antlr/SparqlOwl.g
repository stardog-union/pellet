grammar SparqlOwl;

options {
	language = Java;
	output = AST;
	ASTLabelType = CommonTree;
}

tokens {
	ALL_RESTRICTION;
	ALL_VARS;
	ASK;
	BASE_DECL;
	BLANK_NODE;
	BNODE_PROPERTY_LIST;
	BUILTIN_BOUND;
	BUILTIN_DATATYPE;
	BUILTIN_IS_BLANK;
	BUILTIN_IS_IRI;
	BUILTIN_IS_LITERAL;
	BUILTIN_IS_URI;
	BUILTIN_LANG;
	BUILTIN_LANGMATCHES;
	BUILTIN_REGEX_BINARY;
	BUILTIN_REGEX_TERNARY;
	BUILTIN_SAME_TERM;
	BUILTIN_STR;
	CLASS_OR_DATATYPE;
	COLLECTION;
	CONDITIONAL_EXPRESSION_AND;
	CONDITIONAL_EXPRESSION_OR;
	CONJUNCTION;
	CONSTRUCT;
	CONSTRUCT_TEMPLATE;
	DATA_PROPERTY;
	DATASETS;
	DATATYPE;
	DATATYPE_RESTRICTION;
	DATATYPE_TERM;
	DEFAULT_GRAPH;
	DECIMAL_TERM;
	DESCRIBE;
	DISJUNCTION;
	EXACT_NUMBER_RESTRICTION;
	FACET_GREATER;
	FACET_GREATER_EQUAL;
	FACET_LANGPATTERN;
	FACET_LENGTH;
	FACET_LESS;
	FACET_LESS_EQUAL;
	FACET_MAXLENGTH;
	FACET_MINLENGTH;
	FACET_PATTERN;
	FACET_VALUE;
	FILTER;
	FLOAT_TERM;
	FUNCTION_ARGS;
	FUNCTION_CALL;
	FUNCTION_IDENTIFIER;
	GRAPH_GRAPH_PATTERN;
	GRAPH_IDENTIFIER;
	GROUP_GRAPH_PATTERN;
	INDIVIDUAL;
	INDIVIDUAL_ENUMERATION;
	INTEGER_TERM;
	INVERSE_OBJECT_PROPERTY;
	INVERSE_PROPERTY;
	IRI_REF;
	LIMIT_CLAUSE;
	LITERAL_BOOLEAN_FALSE;
	LITERAL_BOOLEAN_TRUE;
	LITERAL_DECIMAL;
	LITERAL_DOUBLE;
	LITERAL_INTEGER;
	LITERAL_LANG;
	LITERAL_PLAIN;
	LITERAL_TYPED;
	MAX_NUMBER_RESTRICTION;
	MIN_NUMBER_RESTRICTION;
	MODIFIER_DISTINCT;
	MODIFIER_REDUCED;
	NAMED_GRAPH;
	NEGATION;
	NUMERIC_EXPRESSION_ADD;
	NUMERIC_EXPRESSION_DIVIDE;
	NUMERIC_EXPRESSION_MULTIPLY;
	NUMERIC_EXPRESSION_SUBTRACT;
	OBJECT;
	OBJECT_PROPERTY;
	OFFSET_CLAUSE;
	OPTIONAL_GRAPH_PATTERN;
	ORDER_CLAUSE;
	ORDER_CONDITION_ASC;
	ORDER_CONDITION_DESC;
	ORDER_CONDITION_UNDEF;
	PREFIX_DECL;
	PREFIXED_NAME;
	PROPERTY;
	QUERY;
	RDF_TYPE;
	RELATIONAL_EQUAL;
	RELATIONAL_GREATER;
	RELATIONAL_GREATER_EQUAL;
	RELATIONAL_LESS;
	RELATIONAL_LESS_EQUAL;
	RELATIONAL_NOT_EQUAL;
	SELECT;
	SELF_RESTRICTION;
	SOME_RESTRICTION;
	STRING_TERM;
	SUBJECT;
	SUBJECT_TRIPLE_GROUP;
	UNARY_EXPRESSION_NEGATIVE;
	UNARY_EXPRESSION_NOT;
	UNARY_EXPRESSION_POSITIVE;
	UNION_GRAPH_PATTERN;
	VALUE_ENUMERATION;
	VALUE_RESTRICTION;
	VARIABLE;
	VARS;
	VARS_OR_IRIS;
	VERB;
	VERB_PAIR_GROUP;
	WHERE_CLAUSE;
	}

@header{
package com.clarkparsia.sparqlowl.parser.antlr;
}

@lexer::header{
package com.clarkparsia.sparqlowl.parser.antlr;
}

@members {
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

@lexer::members {
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
	:	iriRef
	->	^(OBJECT_PROPERTY iriRef)
	;

/*
 * Manchester 2.1 "dataPropertyIRI"
 */
dataPropertyIRI
	:	iriRef
	->	^(DATA_PROPERTY iriRef)
	;

/*
 * Necessary when property type is ambiguous
 */
objectOrDataPropertyIRI
	:	iriRef
	->	^(PROPERTY iriRef)
	;

/*
 * See Manchester 2.3 "inverseObjectProperty"
 */
inverseObjectProperty
	:	INVERSE_TERM objectPropertyIRI
	->	^(INVERSE_PROPERTY objectPropertyIRI)
	;

/*
 * Useful when any property is possible, regardless of type (e.g, in number restrictions)
 */
propertyExpression
	:	inverseObjectProperty
	|	objectOrDataPropertyIRI
	;

/*
 * See Manchester 2.3 "objectPropertyExpression"
 */
objectPropertyExpression
	:	inverseObjectProperty
	|	objectPropertyIRI
	;

/*
 * See Manchester 2.1 "Datatype"
 */
datatype
	:	iriRef -> ^(DATATYPE iriRef)
	|	INTEGER_TERM -> ^(DATATYPE INTEGER_TERM)
	|	DECIMAL_TERM -> ^(DATATYPE DECIMAL_TERM)
	|	FLOAT_TERM -> ^(DATATYPE FLOAT_TERM)
	|	STRING_TERM -> ^(DATATYPE STRING_TERM)
	;

/*
 * See Manchester 2.1 "individual"
 */
individual
	:	iriRef
	->
		^(INDIVIDUAL iriRef)
	;

/*
 * See Manchester 2.1 "literal"
 *  FIXME: Manchester supports some literals that SPARQL does not
 */
literal
	:	rdfLiteral
	|	numericLiteral
	|	booleanLiteral
	;

/*
 * See Manchester 2.3 "datatypeRestriction"
 */
datatypeRestriction
	:	datatype OPEN_SQUARE_BRACE facet restrictionValue ( COMMA_TERM facet restrictionValue )* CLOSE_SQUARE_BRACE
	->	^(DATATYPE_RESTRICTION datatype ^(FACET_VALUE facet restrictionValue)+ )
	;

/*
 * See Manchester 2.3 "facet"
 */
facet
	:	LENGTH_TERM -> FACET_LENGTH
	|	MINLENGTH_TERM -> FACET_MINLENGTH
	|	MAXLENGTH_TERM -> FACET_MAXLENGTH
	|	PATTERN_TERM -> FACET_PATTERN
	|	LANGPATTERN_TERM -> FACET_LANGPATTERN
	|	LESS_EQUAL_TERM -> FACET_LESS_EQUAL
	|	LESS_TERM -> FACET_LESS
	|	GREATER_EQUAL_TERM -> FACET_GREATER_EQUAL
	|	GREATER_TERM -> FACET_GREATER
	;

/*
 * See Manchester 2.3 "restrictionValue"
 */
restrictionValue
	:	literal
	;

/*
 * Manchester "description" and "dataRange"
 */
disjunction
	:	( conjunction -> conjunction )
		( OR_TERM conjunction -> ^(DISJUNCTION $disjunction conjunction) )*
	;

/*
 * Manchester "conjunction" and "dataConjunction"
 */
conjunction
	:	( primary -> primary )
		( AND_TERM primary -> ^(CONJUNCTION $conjunction primary) )*
	;

/*
 * Manchester "conjunction" and "dataPrimary"
 */
primary
	:	NOT_TERM ( restriction | atomic ) -> ^(NEGATION restriction? atomic?)
	|	restriction 
	|	atomic
	;

/*
 * Manchester "atomic" and "dataAtomic"
 */
atomic
	:	iriRef -> ^(CLASS_OR_DATATYPE iriRef)
	|	INTEGER_TERM -> ^(DATATYPE INTEGER_TERM)
	|	DECIMAL_TERM -> ^(DATATYPE DECIMAL_TERM)
	|	FLOAT_TERM -> ^(DATATYPE FLOAT_TERM)
	|	STRING_TERM -> ^(DATATYPE STRING_TERM)
	|	datatypeRestriction
	|	OPEN_CURLY_BRACE literal+ CLOSE_CURLY_BRACE -> ^(VALUE_ENUMERATION literal+)
	|	OPEN_CURLY_BRACE individual+ CLOSE_CURLY_BRACE -> ^(INDIVIDUAL_ENUMERATION individual+)
	|	OPEN_BRACE! disjunction CLOSE_BRACE!
	;

/*
 * Manchester 2.4 "restriction", with sub rules used for the different types
 */
restriction
	:	someRestriction
	|	allRestriction
	|	valueRestriction
	|	selfRestriction
	|	numberRestriction
	;

someRestriction
	:	propertyExpression SOME_TERM primary
	->	^(SOME_RESTRICTION propertyExpression primary)
	;

allRestriction
	:	propertyExpression ONLY_TERM primary
	->	^(ALL_RESTRICTION propertyExpression primary)
	;
	
valueRestriction
	:	objectPropertyExpression VALUE_TERM individual -> ^(VALUE_RESTRICTION objectPropertyExpression individual)
	|	dataPropertyIRI VALUE_TERM literal -> ^(VALUE_RESTRICTION dataPropertyIRI literal)
	;

selfRestriction
	:	objectPropertyExpression SELF_TERM
	->	^(SELF_RESTRICTION objectPropertyExpression)
	;

numberRestriction
	:	minNumberRestriction
	|	maxNumberRestriction
	|	exactNumberRestriction
	;

/*
 * A syntactic predicate is used to look ahead greedily in case it is a QCR
 */
minNumberRestriction
	:	propertyExpression MIN_TERM n=INTEGER
		(	(primary)=> primary
		|	
		)
	->	^(MIN_NUMBER_RESTRICTION propertyExpression $n primary?)
	;

maxNumberRestriction
	:	propertyExpression MAX_TERM n=INTEGER
		(	(primary)=> primary
		|	
		)
	->	^(MAX_NUMBER_RESTRICTION propertyExpression $n primary?)
	;

exactNumberRestriction
	:	propertyExpression EXACTLY_TERM n=INTEGER
		(	(primary)=> primary
		|	
		)
	->	^(EXACT_NUMBER_RESTRICTION propertyExpression $n primary?)
	;

/* SPARQL */
/*
 * SPARQL A.8[1]
 */
query
	:	prologue
		( selectQuery | constructQuery | describeQuery | askQuery ) EOF
	->	^(QUERY prologue? selectQuery? constructQuery? describeQuery? askQuery? )
	;

/*
 * SPARQL A.8[2]
 */
prologue
	:	baseDecl? prefixDecl*
	;

/*
 * SPARQL A.8[3]
 */
baseDecl
	:	BASE_TERM IRI_REF_TERM
	->	^(BASE_DECL IRI_REF_TERM)
	;

/*
 * SPARQL A.8[4]
 */
prefixDecl
	:	PREFIX_TERM PNAME_NS IRI_REF_TERM
	->	^(PREFIX_DECL PNAME_NS IRI_REF_TERM)
	;

/*
 * SPARQL A.8[5], split over 3 rules to make AST construction easier
 */
selectQuery
	:	SELECT_TERM selectModifier? selectVariableList datasets? whereClause solutionModifier
	->	^(SELECT selectModifier? selectVariableList datasets? whereClause solutionModifier?)
	;

selectModifier
	:	DISTINCT_TERM -> MODIFIER_DISTINCT
	|	REDUCED_TERM -> MODIFIER_REDUCED
	;

selectVariableList
	:	var+ -> ^(VARS var+)
	|	ASTERISK_TERM -> ALL_VARS
	;

/*
 * SPARQL A.8[6]
 */
constructQuery
	:	CONSTRUCT_TERM constructTemplate datasets? whereClause solutionModifier
	->	^( CONSTRUCT constructTemplate datasets? whereClause solutionModifier?)
	;

/*
 * SPARQL A.8[7], split over 2 rules to make AST construction easier
 */
describeQuery
	:	DESCRIBE_TERM describeTargets datasets? whereClause? solutionModifier
	->
		^(DESCRIBE describeTargets datasets? whereClause? solutionModifier?)
	;

describeTargets
	:	varOrIRIref+ -> ^(VARS_OR_IRIS varOrIRIref+)
	|	ASTERISK_TERM -> ALL_VARS
	;

/*
 * SPARQL A.8[8]
 */
askQuery
	:	ASK_TERM datasets? whereClause
	->
		^(ASK datasets? whereClause)
	;

/*
 * Use instead of datasetClause* which appears in A.8[5]-[8] to push all datasets under a single tree node
 */
datasets
	:	datasetClause+
	->	^(DATASETS datasetClause+)
	;

/*
 * SPARQL A.8[9]
 */
datasetClause
	:	FROM_TERM!
		(	defaultGraphClause
		|	namedGraphClause
		)
	;

/*
 * SPARQL A.8[10]
 */
defaultGraphClause
	:	sourceSelector
	->	^(DEFAULT_GRAPH sourceSelector )
	;

/*
 * SPARQL A.8[11]
 */
namedGraphClause
	:	NAMED_TERM sourceSelector
	->	^(NAMED_GRAPH sourceSelector )
	;

/*
 * SPARQL A.8[12]
 */
sourceSelector
	:	iriRef
	;

/*
 * SPARQL A.8[13]
 */
whereClause
	:	WHERE_TERM? groupGraphPattern
	->	^(WHERE_CLAUSE groupGraphPattern)
	;

/*
 * SPARQL A.8[14]
 */
solutionModifier
	:	orderClause? limitOffsetClauses?
	;

/*
 * SPARQL A.8[15]
 */
limitOffsetClauses
	:	limitClause offsetClause?
	|	offsetClause limitClause?
	;

/*
 * SPARQL A.8[16]
 */
orderClause
	:	ORDER_TERM BY_TERM orderCondition+
	->	^(ORDER_CLAUSE orderCondition+)
	;

/*
 * SPARQL A.8[17]
 */
orderCondition
	:	ASC_TERM brackettedExpression -> ^(ORDER_CONDITION_ASC brackettedExpression)
	|	DESC_TERM brackettedExpression -> ^(ORDER_CONDITION_DESC brackettedExpression)
	|	constraint -> ^(ORDER_CONDITION_UNDEF constraint)
	|	var -> ^(ORDER_CONDITION_UNDEF var)
	;

/*
 * SPARQL A.8[18]
 */
limitClause
	:	LIMIT_TERM INTEGER
	->	^(LIMIT_CLAUSE INTEGER)
	;

/*
 * SPARQL A.8[19]
 */
offsetClause
	:	OFFSET_TERM INTEGER
	->	^(OFFSET_CLAUSE INTEGER)
	;

/*
 * See SPARQL A.8[20],[21].  Syntactic predicates necessary so that ambiguity for { or ( introduced by Manchester
 * syntax descriptions as the subject of a triple
 */
groupGraphPattern
	:	OPEN_CURLY_BRACE groupGraphPatternNoBraces? CLOSE_CURLY_BRACE
	->	^(GROUP_GRAPH_PATTERN groupGraphPatternNoBraces?)
	;

groupGraphPatternNoBraces
	:	( graphPatternNotTriples )=> graphPatternNotTriples DOT_TERM!? groupGraphPatternNoBraces?
	|	filter DOT_TERM!? groupGraphPatternNoBraces?
	|	(triplesSameSubject DOT_TERM)=> triplesSameSubject DOT_TERM! groupGraphPatternNoBraces?
	|	triplesSameSubject canFollowTriplesWithoutDot?
	;
	
canFollowTriplesWithoutDot
	:	graphPatternNotTriples DOT_TERM!? groupGraphPatternNoBraces?
	|	filter DOT_TERM!? groupGraphPatternNoBraces?
	;

/*
 * SPARQL A.8[22]
 */
graphPatternNotTriples
	:	optionalGraphPattern
	|	groupOrUnionGraphPattern
	|	graphGraphPattern
	;

/*
 * SPARQL A.8[23]
 */
optionalGraphPattern
	:	OPTIONAL_TERM groupGraphPattern
	->	^(OPTIONAL_GRAPH_PATTERN groupGraphPattern)
	;

/*
 * SPARQL A.8[24]
 */
graphGraphPattern
	:	GRAPH_TERM varOrIRIref groupGraphPattern
	->	^(GRAPH_GRAPH_PATTERN ^(GRAPH_IDENTIFIER varOrIRIref) groupGraphPattern)
	;

/*
 * SPARQL A.8[25]
 */
groupOrUnionGraphPattern
	:	( groupGraphPattern -> groupGraphPattern )
		( UNION_TERM groupGraphPattern -> ^(UNION_GRAPH_PATTERN $groupOrUnionGraphPattern groupGraphPattern) )*
	;

/*
 * SPARQL A.8[26]
 */
filter
	:	FILTER_TERM constraint
	->	^(FILTER constraint)
	;

/*
 * SPARQL A.8[27]
 */
constraint
	:	brackettedExpression
	|	builtInCall
	|	functionCall
	;

/*
 * SPARQL A.8[28]
 */
functionCall
	:	iriRef argList
	->	^(FUNCTION_CALL ^(FUNCTION_IDENTIFIER iriRef) ^(FUNCTION_ARGS argList))
	;

/*
 * SPARQL A.8[29];
 */
argList
	:	OPEN_BRACE! CLOSE_BRACE!
	|	OPEN_BRACE! expression ( COMMA_TERM! expression )* CLOSE_BRACE!
	;

/*
 * SPARQL A.8[30];
 */
constructTemplate
	:	OPEN_CURLY_BRACE constructTriples? CLOSE_CURLY_BRACE
		->	^(CONSTRUCT_TEMPLATE constructTriples?)
	;

/*
 * SPARQL A.8[31];
 */
constructTriples
	:	triplesSameSubject ( DOT_TERM! constructTriples? )?
	;

/*
 * SPARQL A.8[32],[34] + addition to support a Manchester Syntax description as the subject
 */
triplesSameSubject
options { memoize=true; }
	:	(varOrTerm propertyListNotEmpty)=> varOrTerm propertyListNotEmpty -> ^(SUBJECT_TRIPLE_GROUP ^(SUBJECT varOrTerm) propertyListNotEmpty)
	|	(triplesNode propertyListNotEmpty)=> triplesNode propertyListNotEmpty -> ^(SUBJECT_TRIPLE_GROUP ^(SUBJECT triplesNode) propertyListNotEmpty)
	|	(triplesNode)=> triplesNode -> ^(SUBJECT_TRIPLE_GROUP ^(SUBJECT triplesNode))
	|	disjunction propertyListNotEmpty -> ^(SUBJECT_TRIPLE_GROUP ^(SUBJECT disjunction) propertyListNotEmpty)
	;

/*
 * SPARQL A.8[33], split over two rules to make AST construction easier
 */
propertyListNotEmpty
	:	verbObjectListPair ( SEMICOLON_TERM ( verbObjectListPair )? )*
	-> 	verbObjectListPair+
	;

/*
 * See SPARQL A.8[33]
 */
verbObjectListPair
	:	verb objectList
	->	^(VERB_PAIR_GROUP verb objectList)+
	;

/*
 * SPARQL A.8[35]
 */
objectList
	:	object ( COMMA_TERM object )*
	->	object+
	;

/*
 * SPARQL A.8[36] + addition to support Manchester Syntax description.
 * Syntactic predicate looks ahead one token to resolve ambiguities that things like :p some :q would introduce
 */
object
	:	(graphNode ( DOT_TERM | SEMICOLON_TERM | COMMA_TERM | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE ))=> graphNode -> ^(OBJECT graphNode)
	|	disjunction -> ^(OBJECT disjunction)
	;

/*
 * SPARQL A.8[37]
 */
verb
	:	varOrIRIref -> ^(VERB varOrIRIref)
	|	A_TERM -> ^(VERB RDF_TYPE)
	;

/*
 * SPARQL A.8[38]
 */
triplesNode
	:	collection
	|	blankNodePropertyList
	;

/*
 * SPARQL A.8[39]
 */
blankNodePropertyList
	:	OPEN_SQUARE_BRACE propertyListNotEmpty CLOSE_SQUARE_BRACE
	->	^(BNODE_PROPERTY_LIST propertyListNotEmpty)
	;

/*
 * SPARQL A.8[40]
 */
collection
	:	OPEN_BRACE graphNode+ CLOSE_BRACE
	->	^(COLLECTION graphNode+)
	;

/*
 * See SPARQL A.8[92], NIL; This parser rule introduced to generate AST node
 */
emptyCollection
	:	OPEN_BRACE CLOSE_BRACE
	->	^(COLLECTION)
	;

/*
 * SPARQL A.8[41]
 */
graphNode
	:	varOrTerm
	|	triplesNode
	;

/*
 * SPARQL A.8[42]
 */
varOrTerm
	:	var
	|	graphTerm
	;

/*
 * SPARQL A.8[43]
 */
varOrIRIref
	:	var
	|	iriRef
	;

/*
 * SPARQL A.8[44]
 */
var
	:	VAR1 -> ^(VARIABLE VAR1)
	|	VAR2 -> ^(VARIABLE VAR2)
	;

/*
 * SPARQL A.8[45], modified to use the 'literal' and 'emptyCollection' rules
 */
graphTerm
	:	iriRef
	|	literal
	|	blankNode
	|	emptyCollection
	;

/*
 * SPARQL A.8[46]
 */
expression
	:	conditionalOrExpression
	;

/*
 * SPARQL A.8[47]
 */
conditionalOrExpression
	:	( conditionalAndExpression -> conditionalAndExpression )
		( OR_OPERATOR_TERM conditionalAndExpression -> ^(CONDITIONAL_EXPRESSION_OR $conditionalOrExpression conditionalAndExpression) )*
	;

/*
 * SPARQL A.8[48]
 */
conditionalAndExpression
	:	( valueLogical -> valueLogical)
		( AND_OPERATOR_TERM valueLogical -> ^(CONDITIONAL_EXPRESSION_AND $conditionalAndExpression valueLogical) )*
	;

/*
 * SPARQL A.8[49]
 */
valueLogical
	:	relationalExpression
	;

/*
 * SPARQL A.8[50]
 *  AST is unary or a binary subtree rooted on the relational operator
 */
relationalExpression
	:	( numericExpression -> numericExpression )
		(	EQUAL_TERM numericExpression -> ^(RELATIONAL_EQUAL $relationalExpression numericExpression)
		|	NOT_EQUAL_TERM numericExpression -> ^(RELATIONAL_NOT_EQUAL $relationalExpression numericExpression)
		|	LESS_TERM numericExpression -> ^(RELATIONAL_LESS $relationalExpression numericExpression)
		|	GREATER_TERM numericExpression -> ^(RELATIONAL_GREATER $relationalExpression numericExpression)
		|	LESS_EQUAL_TERM numericExpression -> ^(RELATIONAL_LESS_EQUAL $relationalExpression numericExpression)
		|	GREATER_EQUAL_TERM numericExpression -> ^(RELATIONAL_GREATER_EQUAL $relationalExpression numericExpression)
		)?
	;

/*
 * SPARQL A.8[51]
 */
numericExpression
	:	additiveExpression
	;

/*
 * SPARQL A.8[52]
 */
additiveExpression
	:	( multiplicativeExpression -> multiplicativeExpression )
		(	PLUS_TERM multiplicativeExpression -> ^(NUMERIC_EXPRESSION_ADD $additiveExpression multiplicativeExpression)
		|	MINUS_TERM multiplicativeExpression -> ^(NUMERIC_EXPRESSION_SUBTRACT $additiveExpression multiplicativeExpression)
		|	numericLiteralPositive -> ^(NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralPositive)
		|	numericLiteralNegative -> ^(NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralNegative)
		)*
	;

/*
 * SPARQL A.8[53]
 */
multiplicativeExpression
	:	( unaryExpression -> unaryExpression )
		(	ASTERISK_TERM unaryExpression -> ^(NUMERIC_EXPRESSION_MULTIPLY $multiplicativeExpression unaryExpression )
		|	DIVIDE_TERM unaryExpression -> ^(NUMERIC_EXPRESSION_DIVIDE $multiplicativeExpression unaryExpression )
		)*
	;

/*
 * SPARQL A.8[54]
 */
unaryExpression
	:	UNARY_NOT_TERM primaryExpression -> ^(UNARY_EXPRESSION_NOT primaryExpression)
	|	PLUS_TERM primaryExpression -> ^(UNARY_EXPRESSION_POSITIVE primaryExpression)
	|	MINUS_TERM primaryExpression -> ^(UNARY_EXPRESSION_NEGATIVE primaryExpression)
	|	primaryExpression
	;

/*
 * SPARQL A.8[55], modified to use 'literal' rule
 */
primaryExpression
	:	brackettedExpression
	|	builtInCall
	|	iriRefOrFunction
	|	literal
	|	var
	;

/*
 * SPARQL A.8[56]
 */
brackettedExpression
	:	OPEN_BRACE! expression CLOSE_BRACE!
	;

/*
 * SPARQL A.8[57]
 */
builtInCall
	:	STR_TERM OPEN_BRACE expression CLOSE_BRACE -> ^(BUILTIN_STR expression)
	|	LANG_TERM OPEN_BRACE expression CLOSE_BRACE -> ^(BUILTIN_LANG expression)
	|	LANGMATCHES_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE -> ^(BUILTIN_LANGMATCHES expression+)
	|	DATATYPE_TERM OPEN_BRACE expression CLOSE_BRACE -> ^(BUILTIN_DATATYPE expression)
	|	BOUND_TERM OPEN_BRACE var CLOSE_BRACE -> ^(BUILTIN_BOUND var)
	|	SAMETERM_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE -> ^(BUILTIN_SAME_TERM expression+)
	|	ISIRI_TERM OPEN_BRACE expression CLOSE_BRACE -> ^(BUILTIN_IS_IRI expression)
	|	ISURI_TERM OPEN_BRACE expression CLOSE_BRACE -> ^(BUILTIN_IS_URI expression)
	|	ISBLANK_TERM OPEN_BRACE expression CLOSE_BRACE -> ^(BUILTIN_IS_BLANK expression)
	|	ISLITERAL_TERM OPEN_BRACE expression CLOSE_BRACE -> ^(BUILTIN_IS_LITERAL expression)
	|	regexExpression
	;

/*
 * SPARQL A.8[58]; Complicated somewhat to produce different AST nodes in binary for ternary versions
 */
regexExpression
	:	( REGEX_TERM OPEN_BRACE a=expression COMMA_TERM b=expression -> ^(BUILTIN_REGEX_BINARY $a $b) )
		( COMMA_TERM c=expression -> ^(BUILTIN_REGEX_TERNARY $a $b $c) )?
		CLOSE_BRACE
	;

/*
 * SPARQL A.8[59]; If argList is present, AST matches function rule, else matches iriRef
 */
iriRefOrFunction
	:	( iriRef -> iriRef )
		( argList -> ^(FUNCTION_CALL ^(FUNCTION_IDENTIFIER iriRef) ^(FUNCTION_ARGS argList)) )?
	;

/*
 * SPARQL A.8[60]
 */
rdfLiteral
	:	( string -> ^(LITERAL_PLAIN string) )
		(	LANGTAG -> ^(LITERAL_LANG string LANGTAG)
		|	DOUBLE_CARAT_TERM iriRef -> ^(LITERAL_TYPED string iriRef)
		)?
	;

/*
 * SPARQL A.8[61]
 */
numericLiteral
	:	numericLiteralUnsigned
	|	numericLiteralPositive
	|	numericLiteralNegative
	;

/*
 * SPARQL A.8[62]
 */
numericLiteralUnsigned
	:	INTEGER -> ^(LITERAL_INTEGER INTEGER)
	|	DECIMAL -> ^(LITERAL_DECIMAL DECIMAL)
	|	DOUBLE -> ^(LITERAL_DOUBLE DOUBLE)
	;

/*
 * SPARQL A.8[63]
 */
numericLiteralPositive
	:	INTEGER_POSITIVE -> ^(LITERAL_INTEGER INTEGER_POSITIVE)
	|	DECIMAL_POSITIVE -> ^(LITERAL_DECIMAL DECIMAL_POSITIVE)
	|	DOUBLE_POSITIVE -> ^(LITERAL_DOUBLE DOUBLE_POSITIVE)
	;

/*
 * SPARQL A.8[64]
 */
numericLiteralNegative
	:	INTEGER_NEGATIVE -> ^(LITERAL_INTEGER INTEGER_NEGATIVE)
	|	DECIMAL_NEGATIVE -> ^(LITERAL_DECIMAL DECIMAL_NEGATIVE)
	|	DOUBLE_NEGATIVE -> ^(LITERAL_DOUBLE DOUBLE_NEGATIVE)
	;

/*
 * SPARQL A.8[65]
 */
booleanLiteral
	:	TRUE_TERM -> ^(LITERAL_BOOLEAN_TRUE)
	|	FALSE_TERM -> ^(LITERAL_BOOLEAN_FALSE)
	;

/*
 * SPARQL A.8[66]
 */
string
	:	STRING_LITERAL1
	|	STRING_LITERAL2
	|	STRING_LITERAL_LONG1
	|	STRING_LITERAL_LONG2
	;

/*
 * SPARQL A.8[67]
 * Unlike the Manchester spec, do not permit "simpleIRI" (those without even a : prefix)
 */
iriRef
	:	IRI_REF_TERM -> ^(IRI_REF IRI_REF_TERM)
	|	prefixedName -> ^(PREFIXED_NAME prefixedName)
	;

/*
 * SPARQL A.8[68]
 */
prefixedName
	:	PNAME_LN
	|	PNAME_NS
	;

/*
 * SPARQL A.8[69]; In AST, labelled blank nodes have one child, anonymous have none
 */
blankNode
	:	BLANK_NODE_LABEL -> ^(BLANK_NODE BLANK_NODE_LABEL)
	|	OPEN_SQUARE_BRACE CLOSE_SQUARE_BRACE -> ^(BLANK_NODE)
	;



/* Lexer Grammar */

/*
 * SPARQL A.8[93]; See also SPARQL A.3; Whitespace splits tokens and is discarded
 */
WS
	:	(' '| '\t'| EOL)+ { $channel=HIDDEN; }
	;

/*
 * SPARQL A.8[71]
 */
PNAME_NS
	:	PN_PREFIX? ':'
	;

/*
 * SPARQL A.8[72]
 */
PNAME_LN
	:	PNAME_NS PN_LOCAL
	;

/*
 * Manchester 'integer' builtin datatype
 */
INTEGER_TERM
	:	('I'|'i')('N'|'n')('T'|'t')('E'|'e')('G'|'g')('E'|'e')('R'|'r')
	;

/*
 * Manchester 'decimal' builtin datatype
 */
DECIMAL_TERM
	:	('D'|'d')('E'|'e')('C'|'c')('I'|'i')('M'|'m')('A'|'a')('L'|'l')
	;

/*
 * Manchester 'float' builtin datatype
 */
FLOAT_TERM
	:	('F'|'f')('L'|'l')('O'|'o')('A'|'a')('T'|'t')
	;

/*
 * Manchester 'string' builtin datatype
 */
STRING_TERM
	:	('S'|'s')('T'|'t')('R'|'r')('I'|'i')('N'|'n')('G'|'g')
	;

/*
 * Manchester 'length' facet
 */
LENGTH_TERM
	:	('L'|'l')('E'|'e')('N'|'n')('G'|'g')('T'|'t')('H'|'h')
	;

/*
 * Manchester 'minlength' facet
 */
MINLENGTH_TERM
	:	('M'|'m')('I'|'i')('N'|'n')('L'|'l')('E'|'e')('N'|'n')('G'|'g')('T'|'t')('H'|'h')
	;

/*
 * Manchester 'maxlength' facet
 */
MAXLENGTH_TERM
	:	('M'|'m')('A'|'a')('X'|'x')('L'|'l')('E'|'e')('N'|'n')('G'|'g')('T'|'t')('H'|'h')
	;

/*
 * Manchester 'pattern' facet
 */
PATTERN_TERM
	:	('P'|'p')('A'|'a')('T'|'t')('T'|'t')('E'|'e')('R'|'r')('N'|'n')
	;

/*
 * Manchester 'langpattern' facet
 */
LANGPATTERN_TERM
	:	('L'|'l')('A'|'a')('N'|'n')('G'|'g')('P'|'p')('A'|'a')('T'|'t')('T'|'t')('E'|'e')('R'|'r')('N'|'n')
	;

/*
 * Manchester 'inverse' keyword, used to create inverse object properties
 */
INVERSE_TERM
	:	('I'|'i')('N'|'n')('V'|'v')('E'|'e')('R'|'r')('S'|'s')('E'|'e')
	;

/*
 * Manchester 'or' keyword, used to create disjunction class descriptions or data ranges
 */
OR_TERM
	:	('O'|'o')('R'|'r')
	;

/*
 * Manchester 'and' keyword, used to create conjunction class descriptions or data ranges
 */
AND_TERM
	:	('A'|'a')('N'|'n')('D'|'d')
	;

/*
 * Manchester 'that' keyword, used to create restrictions of named classes
 */
THAT_TERM
	:	('T'|'t')('H'|'h')('A'|'a')('T'|'t')
	;

/*
 * Manchester 'not' keyword, used to negate class descriptions or data ranges
 */
NOT_TERM
	:	('N'|'n')('O'|'o')('T'|'t')
	;

/*
 * Manchester 'some' keyword, used to create existential class expressions
 */
SOME_TERM
	:	('S'|'s')('O'|'o')('M'|'m')('E'|'e')
	;

/*
 * Manchester 'only' keyword, used to create universal class expressions
 */
ONLY_TERM
	:	('O'|'o')('N'|'n')('L'|'l')('Y'|'y')
	;

/*
 * Manchester 'value' keyword, used to create individual or literal value class expressions
 */
VALUE_TERM
	:	('V'|'v')('A'|'a')('L'|'l')('U'|'u')('E'|'e')
	;

/*
 * Manchester 'self' keyword, used to create self restrictions
 */
SELF_TERM
	:	('S'|'s')('E'|'e')('L'|'l')('F'|'f')
	;

/*
 * Manchester 'min' keyword, used to create minimum cardinality restrictions
 */
MIN_TERM
	:	('M'|'m')('I'|'i')('N'|'n')
	;

/*
 * Manchester 'max' keyword, used to create minimum cardinality restrictions
 */
MAX_TERM
	:	('M'|'m')('A'|'a')('X'|'x')
	;

/*
 * Manchester 'exactly' keyword, used to create minimum cardinality restrictions
 */
EXACTLY_TERM
	:	('E'|'e')('X'|'x')('A'|'a')('C'|'c')('T'|'t')('L'|'l')('Y'|'y')
	;

/*
 * SPARQL 'base' keyword, used in SPARQL A.8[3]
 */
BASE_TERM
	:	('B'|'b')('A'|'a')('S'|'s')('E'|'e')
	;

/*
 * SPARQL 'prefix' keyword, used in SPARQL A.8[4]
 */
PREFIX_TERM
	:	('P'|'p')('R'|'r')('E'|'e')('F'|'f')('I'|'i')('X'|'x')
	;

/*
 * SPARQL 'select' keyword, used in SPARQL A.8[5]
 */
SELECT_TERM
	:	('S'|'s')('E'|'e')('L'|'l')('E'|'e')('C'|'c')('T'|'t')
	;

/*
 * SPARQL 'distinct' keyword, used in SPARQL A.8[5]
 */
DISTINCT_TERM
	:	('D'|'d')('I'|'i')('S'|'s')('T'|'t')('I'|'i')('N'|'n')('C'|'c')('T'|'t')
	;

/*
 * SPARQL 'reduced' keyword, used in SPARQL A.8[5]
 */
REDUCED_TERM
	:	('R'|'r')('E'|'e')('D'|'d')('U'|'u')('C'|'c')('E'|'e')('D'|'d')
	;

/*
 * SPARQL 'construct' keyword, used in SPARQL A.8[6]
 */
CONSTRUCT_TERM
	:	('C'|'c')('O'|'o')('N'|'n')('S'|'s')('T'|'t')('R'|'r')('U'|'u')('C'|'c')('T'|'t')
	;

/*
 * SPARQL 'describe' keyword, used in SPARQL A.8[7]
 */
DESCRIBE_TERM
	:	('D'|'d')('E'|'e')('S'|'s')('C'|'c')('R'|'r')('I'|'i')('B'|'b')('E'|'e')
	;

/*
 * SPARQL 'ask' keyword, used in SPARQL A.8[8]
 */
ASK_TERM
	:	('A'|'a')('S'|'s')('K'|'k')
	;

/*
 * SPARQL 'from' keyword, used in SPARQL A.8[9]
 */
FROM_TERM
	:	('F'|'f')('R'|'r')('O'|'o')('M'|'m')
	;

/*
 * SPARQL 'named' keyword, used in SPARQL A.8[11]
 */
NAMED_TERM
	:	('N'|'n')('A'|'a')('M'|'m')('E'|'e')('D'|'d')
	;   

/*
 * SPARQL 'where' keyword, used in SPARQL A.8[13]
 */
WHERE_TERM
	:	('W'|'w')('H'|'h')('E'|'e')('R'|'r')('E'|'e')
	;

/*
 * SPARQL 'order' keyword, used in SPARQL A.8[16]
 */
ORDER_TERM
	:	('O'|'o')('R'|'r')('D'|'d')('E'|'e')('R'|'r')
	;

/*
 * SPARQL 'by' keyword, used in SPARQL A.8[16]
 */
BY_TERM
	:	('B'|'b')('Y'|'y')
	;

/*
 * SPARQL 'asc' keyword, used in SPARQL A.8[17]
 */
ASC_TERM
	:	('A'|'a')('S'|'s')('C'|'c')
	;

/*
 * SPARQL 'desc' keyword, used in SPARQL A.8[17]
 */
DESC_TERM
	:	('D'|'d')('E'|'e')('S'|'s')('C'|'c')
	;

/*
 * SPARQL 'limit' keyword, used in SPARQL A.8[18]
 */
LIMIT_TERM
	:	('L'|'l')('I'|'i')('M'|'m')('I'|'i')('T'|'t')
	;

/*
 * SPARQL 'offset' keyword, used in SPARQL A.8[19]
 */
OFFSET_TERM
	:	('O'|'o')('F'|'f')('F'|'f')('S'|'s')('E'|'e')('T'|'t')
	;

/*
 * SPARQL 'optional' keyword, used in SPARQL A.8[23]
 */
OPTIONAL_TERM
	:	('O'|'o')('P'|'p')('T'|'t')('I'|'i')('O'|'o')('N'|'n')('A'|'a')('L'|'l')
	;  

/*
 * SPARQL 'graph' keyword, used in SPARQL A.8[24]
 */
GRAPH_TERM
	:	('G'|'g')('R'|'r')('A'|'a')('P'|'p')('H'|'h')
	;   

/*
 * SPARQL 'union' keyword, used in SPARQL A.8[25]
 */
UNION_TERM
	:	('U'|'u')('N'|'n')('I'|'i')('O'|'o')('N'|'n')
	;

/*
 * SPARQL 'filter' keyword, used in SPARQL A.8[26]
 */
FILTER_TERM
	:	('F'|'f')('I'|'i')('L'|'l')('T'|'t')('E'|'e')('R'|'r')
	;

/*
 * SPARQL 'a' keyword, used in SPARQL A.8[37]
 *  Note that the SPARQL grammar is explicit about only matching a lowercase 'a' (see A.8 para #2)
 */
A_TERM
	:	'a'
	;

/*
 * SPARQL 'str' builtin, used in SPARQL A.8[57]
 */
STR_TERM
	:	('S'|'s')('T'|'t')('R'|'r')
	;

/*
 * SPARQL 'str' builtin, used in SPARQL A.8[57]
 */
LANG_TERM
	:	('L'|'l')('A'|'a')('N'|'n')('G'|'g')
	;

/*
 * SPARQL 'langMatches' builtin, used in SPARQL A.8[57]
 */
LANGMATCHES_TERM
	:	('L'|'l')('A'|'a')('N'|'n')('G'|'g')('M'|'m')('A'|'a')('T'|'t')('C'|'c')('H'|'h')('E'|'e')('S'|'s')
	;

/*
 * SPARQL 'datatype' builtin, used in SPARQL A.8[57]
 */
DATATYPE_TERM
	:	('D'|'d')('A'|'a')('T'|'t')('A'|'a')('T'|'t')('Y'|'y')('P'|'p')('E'|'e')
	;

/*
 * SPARQL 'bound' builtin, used in SPARQL A.8[57]
 */
BOUND_TERM
	:	('B'|'b')('O'|'o')('U'|'u')('N'|'n')('D'|'d')
	;

/*
 * SPARQL 'sameTerm' builtin, used in SPARQL A.8[57]
 */
SAMETERM_TERM
	:	('S'|'s')('A'|'a')('M'|'m')('E'|'e')('T'|'t')('E'|'e')('R'|'r')('M'|'m')
	;

/*
 * SPARQL 'isIRI' builtin, used in SPARQL A.8[57]
 */
ISIRI_TERM
	:	('I'|'i')('S'|'s')('I'|'i')('R'|'r')('I'|'i')
	;

/*
 * SPARQL 'isURI' builtin, used in SPARQL A.8[57]
 */
ISURI_TERM
	:	('I'|'i')('S'|'s')('U'|'u')('R'|'r')('I'|'i')
	;

/*
 * SPARQL 'isBlank' builtin, used in SPARQL A.8[57]
 */
ISBLANK_TERM
	:	('I'|'i')('S'|'s')('B'|'b')('L'|'l')('A'|'a')('N'|'n')('K'|'k')
	;

/*
 * SPARQL 'isLiteral' builtin, used in SPARQL A.8[57]
 */
ISLITERAL_TERM
	:	('I'|'i')('S'|'s')('L'|'l')('I'|'i')('T'|'t')('E'|'e')('R'|'r')('A'|'a')('L'|'l')
	;

/*
 * SPARQL 'regex' builtin, used in SPARQL A.8[58]
 */
REGEX_TERM
	:	('R'|'r')('E'|'e')('G'|'g')('E'|'e')('X'|'x')
	;

/*
 * SPARQL 'true' boolean literal, used in SPARQL A.8[65]
 */
TRUE_TERM
	:	('T'|'t')('R'|'r')('U'|'u')('E'|'e')
	;

/*
 * SPARQL 'false' boolean literal, used in SPARQL A.8[66]
 */
FALSE_TERM
	:	('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e')
	;

/*
 * SPARQL A.8[70]; use an action to strip the enclosing '<' and '>'
 */
IRI_REF_TERM
	:	LESS_TERM ( options {greedy=false;} : ~(LESS_TERM | GREATER_TERM | '"' | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | '|' | '^' | '\\' | '`' | ('\u0000'..'\u0020')) )* GREATER_TERM
		{ setText($text.substring(1, $text.length() - 1)); }
	;

/*
 * SPARQL A.8[73]; use an action to drop the '_:' prefix
 */
BLANK_NODE_LABEL
	:	'_:' label=PN_LOCAL { setText($label.text); }
	;

/*
 * SPARQL A.8[74]; use an action to drop the '?' prefix
 */
VAR1
	:	'?' name=VARNAME { setText($name.text); }
	;

/*
 * SPARQL A.8[74]; use an action to drop the '$' prefix
 */
VAR2
	:	'$' name=VARNAME { setText($name.text); }
	;

/*
 * SPARQL A.8[76]; use an action to drop the '@' prefix
 */
LANGTAG
	:	'@' ALPHA+ (MINUS_TERM ALPHANUM+)*
		{ setText($text.substring(1, $text.length())); }
	;

/*
 * SPARQL A.8[77]
 */
INTEGER
	:	DIGIT+
	;

/*
 * SPARQL A.8[78]
 */
DECIMAL
	:	DIGIT+ DOT_TERM DIGIT*
	|	DOT_TERM DIGIT+
	;

/*
 * SPARQL A.8[79]
 */
DOUBLE
	:	DIGIT+ DOT_TERM DIGIT* EXPONENT
	|	DOT_TERM DIGIT+ EXPONENT
	|	DIGIT+ EXPONENT
	;

/*
 * SPARQL A.8[80]
 */
INTEGER_POSITIVE
	:	PLUS_TERM INTEGER
	;

/*
 * SPARQL A.8[81]
 */
DECIMAL_POSITIVE
	:	PLUS_TERM DECIMAL
	;

/*
 * SPARQL A.8[82]
 */
DOUBLE_POSITIVE
	:	PLUS_TERM DOUBLE
	;

/*
 * SPARQL A.8[83]
 */
INTEGER_NEGATIVE
	:	MINUS_TERM INTEGER
	;

/*
 * SPARQL A.8[84]
 */
DECIMAL_NEGATIVE
	:	MINUS_TERM DECIMAL
	;

/*
 * SPARQL A.8[85]
 */
DOUBLE_NEGATIVE
	:	MINUS_TERM DOUBLE
	;

/*
 * SPARQL A.8[86]
 */
fragment
EXPONENT
	:	('e'|'E') (PLUS_TERM|MINUS_TERM)? DIGIT+
	;

/*
 * SPARQL A.8[87]
 */
STRING_LITERAL1
	:	'\'' ( options {greedy=false;} : ~('\u0027' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '\''
	;

/*
 * SPARQL A.8[88]
 */
STRING_LITERAL2
	:	'"'  ( options {greedy=false;} : ~('\u0022' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '"'
	;

/*
 * SPARQL A.8[89]
 */
STRING_LITERAL_LONG1
	:	  '\'\'\'' ( options {greedy=false;} : ( '\'' | '\'\'' )? ( ~('\''|'\\') | ECHAR ) )* '\'\'\''
	;

/*
 * SPARQL A.8[90]
 */
STRING_LITERAL_LONG2
	:	  '"""' ( options {greedy=false;} : ( '"' | '""' )? ( ~('"'|'\\') | ECHAR ) )* '"""'
	;

/*
 * SPARQL A.8[91]; See also SPARQL A.7
 */
fragment
ECHAR
	:	'\\' ('t' | 'b' | 'n' | 'r' | 'f' | '\\' | '"' | '\'')
	;

/*
 * SPARQL A.8[95]
 */
fragment
PN_CHARS_BASE
	:	ALPHA
	|	'\u00C0'..'\u00D6'
	|	'\u00D8'..'\u00F6'
	|	'\u00F8'..'\u02FF'
	|	'\u0370'..'\u037D'
	|	'\u037F'..'\u1FFF'
	|	'\u200C'..'\u200D'
	|	'\u2070'..'\u218F'
	|	'\u2C00'..'\u2FEF'
	|	'\u3001'..'\uD7FF'
	|	'\uF900'..'\uFDCF'
	|	'\uFDF0'..'\uFFFD'
	;

/*
 * SPARQL A.8[96]
 */
fragment
PN_CHARS_U
	:	PN_CHARS_BASE
	|	'_'
	;

/*
 * SPARQL A.8[97]
 */
fragment
VARNAME
	:	(	PN_CHARS_U
		|	DIGIT
		)
		(	PN_CHARS_U
		|	DIGIT
		|	'\u00B7'
		|	'\u0300'..'\u036F'
		|	'\u203F'..'\u2040'
		)*
	;

/*
 * SPARQL A.8[98]
 */
fragment
PN_CHARS
	:	PN_CHARS_U
	|	MINUS_TERM
	|	DIGIT
	|	'\u00B7' 
	|	'\u0300'..'\u036F'
	|	'\u203F'..'\u2040'
	;

/*
 * SPARQL A.8[99]
 */
fragment
PN_PREFIX
	:	PN_CHARS_BASE (
		(	PN_CHARS
		|	DOT_TERM
		)* PN_CHARS)?
	;

/*
 * SPARQL A.8[100]
 */
fragment
PN_LOCAL
	:	(	PN_CHARS_U
		|	DIGIT
		) (
		(	PN_CHARS
		|	DOT_TERM
		)* PN_CHARS)?
	;

/*
 * Added for convenience
 */
fragment
ALPHA
	:	'A'..'Z'
	|	'a'..'z'
	;

/*
 * Added for convenience
 */
fragment
DIGIT
	:	'0'..'9'
	;

/*
 * Added for convenience
 */
fragment
ALPHANUM
	:	ALPHA
	|	DIGIT
	;
/*
 * SPARQL A.4
 */
COMMENT 
	:	'#' ( options{greedy=false;} : .)* EOL { $channel=HIDDEN; }
	;

/*
 * Added for convenience
 */
fragment
EOL
	:	'\n' | '\r'
	;

DOUBLE_CARAT_TERM
	:	'^^'
	;

LESS_EQUAL_TERM
	:	'<='
	;

GREATER_EQUAL_TERM
	:	'>='
	;

NOT_EQUAL_TERM
	:	'!='
	;

AND_OPERATOR_TERM
	:	'&&'
	;

OR_OPERATOR_TERM
	:	'||'
	;

OPEN_BRACE
	:	'('
	;

CLOSE_BRACE
	:	')'
	;

OPEN_CURLY_BRACE
	:	'{'
	;

CLOSE_CURLY_BRACE
	:	'}'
	;

OPEN_SQUARE_BRACE
	:	'['
	;

CLOSE_SQUARE_BRACE
	:	']'
	;

SEMICOLON_TERM
	:	';'
	;

DOT_TERM
	:	'.'
	;

PLUS_TERM
	:	'+'
	;

MINUS_TERM
	:	'-'
	;

ASTERISK_TERM
	:	'*'
	;

COMMA_TERM
	:	','
	;

UNARY_NOT_TERM
	:	'!'
	;

DIVIDE_TERM
	:	'/'
	;

EQUAL_TERM
	:	'='
	;

LESS_TERM
	:	'<'
	;

GREATER_TERM
	:	'>'
	;

ANY
	:	.
	;
