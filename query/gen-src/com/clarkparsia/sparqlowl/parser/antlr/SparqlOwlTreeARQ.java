// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g 2013-04-11 22:07:44

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
import com.hp.hpl.jena.sparql.syntax.TripleCollector;
import com.hp.hpl.jena.sparql.syntax.TripleCollectorBGP;
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


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class SparqlOwlTreeARQ extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALL_RESTRICTION", "ALL_VARS", "ASK", "BASE_DECL", "BLANK_NODE", "BNODE_PROPERTY_LIST", "BUILTIN_BOUND", "BUILTIN_DATATYPE", "BUILTIN_IS_BLANK", "BUILTIN_IS_IRI", "BUILTIN_IS_LITERAL", "BUILTIN_IS_URI", "BUILTIN_LANG", "BUILTIN_LANGMATCHES", "BUILTIN_REGEX_BINARY", "BUILTIN_REGEX_TERNARY", "BUILTIN_SAME_TERM", "BUILTIN_STR", "CLASS_OR_DATATYPE", "COLLECTION", "CONDITIONAL_EXPRESSION_AND", "CONDITIONAL_EXPRESSION_OR", "CONJUNCTION", "CONSTRUCT", "CONSTRUCT_TEMPLATE", "DATA_PROPERTY", "DATASETS", "DATATYPE", "DATATYPE_RESTRICTION", "DATATYPE_TERM", "DEFAULT_GRAPH", "DECIMAL_TERM", "DESCRIBE", "DISJUNCTION", "EXACT_NUMBER_RESTRICTION", "FACET_GREATER", "FACET_GREATER_EQUAL", "FACET_LANGPATTERN", "FACET_LENGTH", "FACET_LESS", "FACET_LESS_EQUAL", "FACET_MAXLENGTH", "FACET_MINLENGTH", "FACET_PATTERN", "FACET_VALUE", "FILTER", "FLOAT_TERM", "FUNCTION_ARGS", "FUNCTION_CALL", "FUNCTION_IDENTIFIER", "GRAPH_GRAPH_PATTERN", "GRAPH_IDENTIFIER", "GROUP_GRAPH_PATTERN", "INDIVIDUAL", "INDIVIDUAL_ENUMERATION", "INTEGER_TERM", "INVERSE_OBJECT_PROPERTY", "INVERSE_PROPERTY", "IRI_REF", "LIMIT_CLAUSE", "LITERAL_BOOLEAN_FALSE", "LITERAL_BOOLEAN_TRUE", "LITERAL_DECIMAL", "LITERAL_DOUBLE", "LITERAL_INTEGER", "LITERAL_LANG", "LITERAL_PLAIN", "LITERAL_TYPED", "MAX_NUMBER_RESTRICTION", "MIN_NUMBER_RESTRICTION", "MODIFIER_DISTINCT", "MODIFIER_REDUCED", "NAMED_GRAPH", "NEGATION", "NUMERIC_EXPRESSION_ADD", "NUMERIC_EXPRESSION_DIVIDE", "NUMERIC_EXPRESSION_MULTIPLY", "NUMERIC_EXPRESSION_SUBTRACT", "OBJECT", "OBJECT_PROPERTY", "OFFSET_CLAUSE", "OPTIONAL_GRAPH_PATTERN", "ORDER_CLAUSE", "ORDER_CONDITION_ASC", "ORDER_CONDITION_DESC", "ORDER_CONDITION_UNDEF", "PREFIX_DECL", "PREFIXED_NAME", "PROPERTY", "QUERY", "RDF_TYPE", "RELATIONAL_EQUAL", "RELATIONAL_GREATER", "RELATIONAL_GREATER_EQUAL", "RELATIONAL_LESS", "RELATIONAL_LESS_EQUAL", "RELATIONAL_NOT_EQUAL", "SELECT", "SELF_RESTRICTION", "SOME_RESTRICTION", "STRING_TERM", "SUBJECT", "SUBJECT_TRIPLE_GROUP", "UNARY_EXPRESSION_NEGATIVE", "UNARY_EXPRESSION_NOT", "UNARY_EXPRESSION_POSITIVE", "UNION_GRAPH_PATTERN", "VALUE_ENUMERATION", "VALUE_RESTRICTION", "VARIABLE", "VARS", "VARS_OR_IRIS", "VERB", "VERB_PAIR_GROUP", "WHERE_CLAUSE", "INVERSE_TERM", "OPEN_SQUARE_BRACE", "COMMA_TERM", "CLOSE_SQUARE_BRACE", "LENGTH_TERM", "MINLENGTH_TERM", "MAXLENGTH_TERM", "PATTERN_TERM", "LANGPATTERN_TERM", "LESS_EQUAL_TERM", "LESS_TERM", "GREATER_EQUAL_TERM", "GREATER_TERM", "OR_TERM", "AND_TERM", "NOT_TERM", "OPEN_CURLY_BRACE", "CLOSE_CURLY_BRACE", "OPEN_BRACE", "CLOSE_BRACE", "SOME_TERM", "ONLY_TERM", "VALUE_TERM", "SELF_TERM", "MIN_TERM", "INTEGER", "MAX_TERM", "EXACTLY_TERM", "BASE_TERM", "IRI_REF_TERM", "PREFIX_TERM", "PNAME_NS", "SELECT_TERM", "DISTINCT_TERM", "REDUCED_TERM", "ASTERISK_TERM", "CONSTRUCT_TERM", "DESCRIBE_TERM", "ASK_TERM", "FROM_TERM", "NAMED_TERM", "WHERE_TERM", "ORDER_TERM", "BY_TERM", "ASC_TERM", "DESC_TERM", "LIMIT_TERM", "OFFSET_TERM", "DOT_TERM", "OPTIONAL_TERM", "GRAPH_TERM", "UNION_TERM", "FILTER_TERM", "SEMICOLON_TERM", "A_TERM", "VAR1", "VAR2", "OR_OPERATOR_TERM", "AND_OPERATOR_TERM", "EQUAL_TERM", "NOT_EQUAL_TERM", "PLUS_TERM", "MINUS_TERM", "DIVIDE_TERM", "UNARY_NOT_TERM", "STR_TERM", "LANG_TERM", "LANGMATCHES_TERM", "BOUND_TERM", "SAMETERM_TERM", "ISIRI_TERM", "ISURI_TERM", "ISBLANK_TERM", "ISLITERAL_TERM", "REGEX_TERM", "LANGTAG", "DOUBLE_CARAT_TERM", "DECIMAL", "DOUBLE", "INTEGER_POSITIVE", "DECIMAL_POSITIVE", "DOUBLE_POSITIVE", "INTEGER_NEGATIVE", "DECIMAL_NEGATIVE", "DOUBLE_NEGATIVE", "TRUE_TERM", "FALSE_TERM", "STRING_LITERAL1", "STRING_LITERAL2", "STRING_LITERAL_LONG1", "STRING_LITERAL_LONG2", "PNAME_LN", "BLANK_NODE_LABEL", "EOL", "WS", "PN_PREFIX", "PN_LOCAL", "THAT_TERM", "VARNAME", "ALPHA", "ALPHANUM", "DIGIT", "EXPONENT", "ECHAR", "PN_CHARS_BASE", "PN_CHARS_U", "PN_CHARS", "COMMENT", "ANY", "POSITIVE_INTEGER", "NEGATIVE_INTEGER", "POSITIVE_DECIMAL", "NEGATIVE_DECIMAL", "POSITIVE_DOUBLE", "NEGATIVE_DOUBLE"
    };
    public static final int BUILTIN_IS_URI=15;
    public static final int VALUE_RESTRICTION=112;
    public static final int MODIFIER_REDUCED=75;
    public static final int UNION_GRAPH_PATTERN=110;
    public static final int CONSTRUCT_TEMPLATE=28;
    public static final int FALSE_TERM=205;
    public static final int AND_TERM=133;
    public static final int PREFIX_DECL=90;
    public static final int ORDER_CONDITION_ASC=87;
    public static final int FILTER_TERM=171;
    public static final int PNAME_LN=210;
    public static final int CONSTRUCT=27;
    public static final int ONLY_TERM=140;
    public static final int EOF=-1;
    public static final int ASTERISK_TERM=154;
    public static final int IRI_REF_TERM=148;
    public static final int OBJECT_PROPERTY=83;
    public static final int FACET_VALUE=48;
    public static final int EOL=212;
    public static final int FACET_LESS=43;
    public static final int A_TERM=173;
    public static final int LANG_TERM=185;
    public static final int PN_CHARS_U=224;
    public static final int MAXLENGTH_TERM=125;
    public static final int RELATIONAL_LESS=98;
    public static final int LANGPATTERN_TERM=127;
    public static final int CLOSE_CURLY_BRACE=136;
    public static final int DOUBLE_POSITIVE=200;
    public static final int CONDITIONAL_EXPRESSION_AND=24;
    public static final int BOUND_TERM=187;
    public static final int INTEGER_TERM=59;
    public static final int NOT_EQUAL_TERM=179;
    public static final int LITERAL_LANG=69;
    public static final int ALL_RESTRICTION=4;
    public static final int CONJUNCTION=26;
    public static final int DATA_PROPERTY=29;
    public static final int FACET_LENGTH=42;
    public static final int ASK=6;
    public static final int REGEX_TERM=193;
    public static final int SUBJECT_TRIPLE_GROUP=106;
    public static final int PLUS_TERM=180;
    public static final int STRING_TERM=104;
    public static final int LITERAL_BOOLEAN_FALSE=64;
    public static final int INDIVIDUAL_ENUMERATION=58;
    public static final int OPTIONAL_TERM=168;
    public static final int NAMED_GRAPH=76;
    public static final int WS=213;
    public static final int FACET_MINLENGTH=46;
    public static final int ORDER_CONDITION_DESC=88;
    public static final int BUILTIN_IS_LITERAL=14;
    public static final int OPTIONAL_GRAPH_PATTERN=85;
    public static final int AND_OPERATOR_TERM=177;
    public static final int INTEGER_POSITIVE=198;
    public static final int DESCRIBE=36;
    public static final int PN_CHARS=225;
    public static final int DATATYPE=31;
    public static final int FACET_LESS_EQUAL=44;
    public static final int GROUP_GRAPH_PATTERN=56;
    public static final int DOUBLE_NEGATIVE=203;
    public static final int MINLENGTH_TERM=124;
    public static final int FUNCTION_CALL=52;
    public static final int BUILTIN_BOUND=10;
    public static final int LIMIT_CLAUSE=63;
    public static final int SUBJECT=105;
    public static final int FACET_PATTERN=47;
    public static final int IRI_REF=62;
    public static final int LESS_EQUAL_TERM=128;
    public static final int FACET_LANGPATTERN=41;
    public static final int LANGMATCHES_TERM=186;
    public static final int FUNCTION_IDENTIFIER=53;
    public static final int GRAPH_IDENTIFIER=55;
    public static final int NEGATIVE_INTEGER=229;
    public static final int BUILTIN_SAME_TERM=20;
    public static final int LITERAL_DOUBLE=67;
    public static final int WHERE_CLAUSE=118;
    public static final int GRAPH_GRAPH_PATTERN=54;
    public static final int OFFSET_CLAUSE=84;
    public static final int NEGATIVE_DOUBLE=233;
    public static final int DECIMAL_POSITIVE=199;
    public static final int FACET_GREATER_EQUAL=40;
    public static final int MIN_TERM=143;
    public static final int LIMIT_TERM=165;
    public static final int EQUAL_TERM=178;
    public static final int REDUCED_TERM=153;
    public static final int CONSTRUCT_TERM=155;
    public static final int SOME_RESTRICTION=103;
    public static final int ISURI_TERM=190;
    public static final int POSITIVE_DOUBLE=232;
    public static final int INTEGER=144;
    public static final int NUMERIC_EXPRESSION_ADD=78;
    public static final int BLANK_NODE=8;
    public static final int CONDITIONAL_EXPRESSION_OR=25;
    public static final int NUMERIC_EXPRESSION_DIVIDE=79;
    public static final int BASE_DECL=7;
    public static final int PNAME_NS=150;
    public static final int PATTERN_TERM=126;
    public static final int ISIRI_TERM=189;
    public static final int CLOSE_BRACE=138;
    public static final int OR_OPERATOR_TERM=176;
    public static final int BUILTIN_REGEX_BINARY=18;
    public static final int DOT_TERM=167;
    public static final int MAX_TERM=145;
    public static final int DIVIDE_TERM=182;
    public static final int BUILTIN_IS_BLANK=12;
    public static final int SELECT_TERM=151;
    public static final int NAMED_TERM=159;
    public static final int STRING_LITERAL_LONG2=209;
    public static final int NEGATION=77;
    public static final int DECIMAL=196;
    public static final int STRING_LITERAL_LONG1=208;
    public static final int INVERSE_TERM=119;
    public static final int TRUE_TERM=204;
    public static final int SELF_RESTRICTION=102;
    public static final int GRAPH_TERM=169;
    public static final int POSITIVE_INTEGER=228;
    public static final int DATATYPE_RESTRICTION=32;
    public static final int VALUE_TERM=141;
    public static final int SELF_TERM=142;
    public static final int ORDER_TERM=161;
    public static final int RDF_TYPE=94;
    public static final int LANGTAG=194;
    public static final int NEGATIVE_DECIMAL=231;
    public static final int BUILTIN_LANGMATCHES=17;
    public static final int EXPONENT=221;
    public static final int CLOSE_SQUARE_BRACE=122;
    public static final int ORDER_CLAUSE=86;
    public static final int UNARY_EXPRESSION_NOT=108;
    public static final int BUILTIN_LANG=16;
    public static final int GREATER_EQUAL_TERM=130;
    public static final int FUNCTION_ARGS=51;
    public static final int VARNAME=217;
    public static final int DATATYPE_TERM=33;
    public static final int BASE_TERM=147;
    public static final int LITERAL_BOOLEAN_TRUE=65;
    public static final int CLASS_OR_DATATYPE=22;
    public static final int SAMETERM_TERM=188;
    public static final int THAT_TERM=216;
    public static final int DOUBLE_CARAT_TERM=195;
    public static final int ISLITERAL_TERM=192;
    public static final int DOUBLE=197;
    public static final int ISBLANK_TERM=191;
    public static final int GREATER_TERM=131;
    public static final int DESC_TERM=164;
    public static final int OFFSET_TERM=166;
    public static final int COMMENT=226;
    public static final int SELECT=101;
    public static final int OPEN_CURLY_BRACE=135;
    public static final int BNODE_PROPERTY_LIST=9;
    public static final int LITERAL_DECIMAL=66;
    public static final int STR_TERM=184;
    public static final int VALUE_ENUMERATION=111;
    public static final int FACET_GREATER=39;
    public static final int BLANK_NODE_LABEL=211;
    public static final int PREFIX_TERM=149;
    public static final int BUILTIN_REGEX_TERNARY=19;
    public static final int LESS_TERM=129;
    public static final int ALPHANUM=219;
    public static final int ALPHA=218;
    public static final int DISJUNCTION=37;
    public static final int DESCRIBE_TERM=156;
    public static final int LITERAL_PLAIN=70;
    public static final int SOME_TERM=139;
    public static final int UNARY_NOT_TERM=183;
    public static final int ASK_TERM=157;
    public static final int VARIABLE=113;
    public static final int PROPERTY=92;
    public static final int STRING_LITERAL2=207;
    public static final int STRING_LITERAL1=206;
    public static final int FILTER=49;
    public static final int MAX_NUMBER_RESTRICTION=72;
    public static final int QUERY=93;
    public static final int MODIFIER_DISTINCT=74;
    public static final int FACET_MAXLENGTH=45;
    public static final int BUILTIN_STR=21;
    public static final int LITERAL_INTEGER=68;
    public static final int RELATIONAL_NOT_EQUAL=100;
    public static final int MIN_NUMBER_RESTRICTION=73;
    public static final int SEMICOLON_TERM=172;
    public static final int BUILTIN_IS_IRI=13;
    public static final int PREFIXED_NAME=91;
    public static final int RELATIONAL_EQUAL=95;
    public static final int NUMERIC_EXPRESSION_MULTIPLY=80;
    public static final int BUILTIN_DATATYPE=11;
    public static final int INVERSE_OBJECT_PROPERTY=60;
    public static final int INVERSE_PROPERTY=61;
    public static final int UNARY_EXPRESSION_NEGATIVE=107;
    public static final int FLOAT_TERM=50;
    public static final int OBJECT=82;
    public static final int VERB=116;
    public static final int COMMA_TERM=121;
    public static final int LITERAL_TYPED=71;
    public static final int RELATIONAL_LESS_EQUAL=99;
    public static final int RELATIONAL_GREATER=96;
    public static final int COLLECTION=23;
    public static final int DIGIT=220;
    public static final int UNARY_EXPRESSION_POSITIVE=109;
    public static final int DEFAULT_GRAPH=34;
    public static final int NOT_TERM=134;
    public static final int RELATIONAL_GREATER_EQUAL=97;
    public static final int MINUS_TERM=181;
    public static final int VERB_PAIR_GROUP=117;
    public static final int OR_TERM=132;
    public static final int VARS=114;
    public static final int FROM_TERM=158;
    public static final int DISTINCT_TERM=152;
    public static final int INDIVIDUAL=57;
    public static final int DATASETS=30;
    public static final int INTEGER_NEGATIVE=201;
    public static final int PN_LOCAL=215;
    public static final int ASC_TERM=163;
    public static final int POSITIVE_DECIMAL=230;
    public static final int DECIMAL_TERM=35;
    public static final int OPEN_SQUARE_BRACE=120;
    public static final int ORDER_CONDITION_UNDEF=89;
    public static final int ECHAR=222;
    public static final int WHERE_TERM=160;
    public static final int ANY=227;
    public static final int PN_CHARS_BASE=223;
    public static final int VAR1=174;
    public static final int VAR2=175;
    public static final int DECIMAL_NEGATIVE=202;
    public static final int PN_PREFIX=214;
    public static final int VARS_OR_IRIS=115;
    public static final int BY_TERM=162;
    public static final int UNION_TERM=170;
    public static final int ALL_VARS=5;
    public static final int EXACT_NUMBER_RESTRICTION=38;
    public static final int EXACTLY_TERM=146;
    public static final int OPEN_BRACE=137;
    public static final int NUMERIC_EXPRESSION_SUBTRACT=81;
    public static final int LENGTH_TERM=123;

    // delegates
    // delegators


        public SparqlOwlTreeARQ(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public SparqlOwlTreeARQ(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return SparqlOwlTreeARQ.tokenNames; }
    public String getGrammarFileName() { return "/Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g"; }



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


    public static class objectPropertyIRI_return extends TreeRuleReturnScope {
        public Node p;
        public Collection<Triple> triples;
    };

    // $ANTLR start "objectPropertyIRI"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:150:1: objectPropertyIRI returns [Node p, Collection<Triple> triples] : ^( OBJECT_PROPERTY iriRef ) ;
    public final SparqlOwlTreeARQ.objectPropertyIRI_return objectPropertyIRI() throws RecognitionException {
        SparqlOwlTreeARQ.objectPropertyIRI_return retval = new SparqlOwlTreeARQ.objectPropertyIRI_return();
        retval.start = input.LT(1);

        Node iriRef1 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:152:2: ( ^( OBJECT_PROPERTY iriRef ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:152:4: ^( OBJECT_PROPERTY iriRef )
            {
            match(input,OBJECT_PROPERTY,FOLLOW_OBJECT_PROPERTY_in_objectPropertyIRI66); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_iriRef_in_objectPropertyIRI68);
            iriRef1=iriRef();

            state._fsp--;


            match(input, Token.UP, null); 

            			retval.p = iriRef1;
            			retval.triples = Collections.singleton( new Triple( retval.p, RDF.Nodes.type, OWL.ObjectProperty.asNode() ) );
            		

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "objectPropertyIRI"

    public static class dataPropertyIRI_return extends TreeRuleReturnScope {
        public Node p;
        public Collection<Triple> triples;
    };

    // $ANTLR start "dataPropertyIRI"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:162:1: dataPropertyIRI returns [Node p, Collection<Triple> triples] : ^( DATA_PROPERTY iriRef ) ;
    public final SparqlOwlTreeARQ.dataPropertyIRI_return dataPropertyIRI() throws RecognitionException {
        SparqlOwlTreeARQ.dataPropertyIRI_return retval = new SparqlOwlTreeARQ.dataPropertyIRI_return();
        retval.start = input.LT(1);

        Node iriRef2 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:164:2: ( ^( DATA_PROPERTY iriRef ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:164:4: ^( DATA_PROPERTY iriRef )
            {
            match(input,DATA_PROPERTY,FOLLOW_DATA_PROPERTY_in_dataPropertyIRI92); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_iriRef_in_dataPropertyIRI94);
            iriRef2=iriRef();

            state._fsp--;


            match(input, Token.UP, null); 

            			retval.p = iriRef2;
            			retval.triples = Collections.singleton( new Triple( retval.p, RDF.Nodes.type, OWL.DatatypeProperty.asNode() ) );
            		

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "dataPropertyIRI"


    // $ANTLR start "objectOrDataPropertyIRI"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:174:1: objectOrDataPropertyIRI returns [Node p] : ^( PROPERTY iriRef ) ;
    public final Node objectOrDataPropertyIRI() throws RecognitionException {
        Node p = null;

        Node iriRef3 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:176:2: ( ^( PROPERTY iriRef ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:176:4: ^( PROPERTY iriRef )
            {
            match(input,PROPERTY,FOLLOW_PROPERTY_in_objectOrDataPropertyIRI118); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_iriRef_in_objectOrDataPropertyIRI120);
            iriRef3=iriRef();

            state._fsp--;


            match(input, Token.UP, null); 
             p = iriRef3; 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return p;
    }
    // $ANTLR end "objectOrDataPropertyIRI"

    public static class inverseObjectProperty_return extends TreeRuleReturnScope {
        public Node p;
        public Collection<Triple> triples;
    };

    // $ANTLR start "inverseObjectProperty"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:182:1: inverseObjectProperty returns [Node p, Collection<Triple> triples] : ^( INVERSE_PROPERTY objectPropertyIRI ) ;
    public final SparqlOwlTreeARQ.inverseObjectProperty_return inverseObjectProperty() throws RecognitionException {
        SparqlOwlTreeARQ.inverseObjectProperty_return retval = new SparqlOwlTreeARQ.inverseObjectProperty_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.objectPropertyIRI_return objectPropertyIRI4 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:184:2: ( ^( INVERSE_PROPERTY objectPropertyIRI ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:184:4: ^( INVERSE_PROPERTY objectPropertyIRI )
            {
            match(input,INVERSE_PROPERTY,FOLLOW_INVERSE_PROPERTY_in_inverseObjectProperty142); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_objectPropertyIRI_in_inverseObjectProperty144);
            objectPropertyIRI4=objectPropertyIRI();

            state._fsp--;


            match(input, Token.UP, null); 

            			retval.p = getAnon( );
            			retval.triples = new ArrayList<Triple>( (objectPropertyIRI4!=null?objectPropertyIRI4.triples:null) );
            			retval.triples.add( new Triple( retval.p, OWL.inverseOf.asNode(), (objectPropertyIRI4!=null?objectPropertyIRI4.p:null) ) );
            		

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "inverseObjectProperty"

    public static class propertyExpression_return extends TreeRuleReturnScope {
        public Node p;
        public Collection<Triple> triples;
    };

    // $ANTLR start "propertyExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:195:1: propertyExpression returns [Node p, Collection<Triple> triples] : ( inverseObjectProperty | objectOrDataPropertyIRI );
    public final SparqlOwlTreeARQ.propertyExpression_return propertyExpression() throws RecognitionException {
        SparqlOwlTreeARQ.propertyExpression_return retval = new SparqlOwlTreeARQ.propertyExpression_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.inverseObjectProperty_return inverseObjectProperty5 = null;

        Node objectOrDataPropertyIRI6 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:197:2: ( inverseObjectProperty | objectOrDataPropertyIRI )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==INVERSE_PROPERTY) ) {
                alt1=1;
            }
            else if ( (LA1_0==PROPERTY) ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:197:4: inverseObjectProperty
                    {
                    pushFollow(FOLLOW_inverseObjectProperty_in_propertyExpression167);
                    inverseObjectProperty5=inverseObjectProperty();

                    state._fsp--;


                    			retval.p = (inverseObjectProperty5!=null?inverseObjectProperty5.p:null);
                    			retval.triples = (inverseObjectProperty5!=null?inverseObjectProperty5.triples:null);
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:202:4: objectOrDataPropertyIRI
                    {
                    pushFollow(FOLLOW_objectOrDataPropertyIRI_in_propertyExpression176);
                    objectOrDataPropertyIRI6=objectOrDataPropertyIRI();

                    state._fsp--;


                    			retval.p = objectOrDataPropertyIRI6;
                    			retval.triples = Collections.emptyList();
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "propertyExpression"

    public static class objectPropertyExpression_return extends TreeRuleReturnScope {
        public Node p;
        public Collection<Triple> triples;
    };

    // $ANTLR start "objectPropertyExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:212:1: objectPropertyExpression returns [Node p, Collection<Triple> triples] : ( inverseObjectProperty | objectPropertyIRI );
    public final SparqlOwlTreeARQ.objectPropertyExpression_return objectPropertyExpression() throws RecognitionException {
        SparqlOwlTreeARQ.objectPropertyExpression_return retval = new SparqlOwlTreeARQ.objectPropertyExpression_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.inverseObjectProperty_return inverseObjectProperty7 = null;

        SparqlOwlTreeARQ.objectPropertyIRI_return objectPropertyIRI8 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:214:2: ( inverseObjectProperty | objectPropertyIRI )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==INVERSE_PROPERTY) ) {
                alt2=1;
            }
            else if ( (LA2_0==OBJECT_PROPERTY) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:214:4: inverseObjectProperty
                    {
                    pushFollow(FOLLOW_inverseObjectProperty_in_objectPropertyExpression198);
                    inverseObjectProperty7=inverseObjectProperty();

                    state._fsp--;


                    			retval.p = (inverseObjectProperty7!=null?inverseObjectProperty7.p:null);
                    			retval.triples = (inverseObjectProperty7!=null?inverseObjectProperty7.triples:null);
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:219:4: objectPropertyIRI
                    {
                    pushFollow(FOLLOW_objectPropertyIRI_in_objectPropertyExpression207);
                    objectPropertyIRI8=objectPropertyIRI();

                    state._fsp--;


                    			retval.p = (objectPropertyIRI8!=null?objectPropertyIRI8.p:null);
                    			retval.triples = (objectPropertyIRI8!=null?objectPropertyIRI8.triples:null);
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "objectPropertyExpression"


    // $ANTLR start "datatype"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:230:1: datatype returns [Node n] : ( ^( DATATYPE iriRef ) | ^( DATATYPE INTEGER_TERM ) | ^( DATATYPE DECIMAL_TERM ) | ^( DATATYPE FLOAT_TERM ) | ^( DATATYPE STRING_TERM ) );
    public final Node datatype() throws RecognitionException {
        Node n = null;

        Node iriRef9 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:232:2: ( ^( DATATYPE iriRef ) | ^( DATATYPE INTEGER_TERM ) | ^( DATATYPE DECIMAL_TERM ) | ^( DATATYPE FLOAT_TERM ) | ^( DATATYPE STRING_TERM ) )
            int alt3=5;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==DATATYPE) ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case INTEGER_TERM:
                        {
                        alt3=2;
                        }
                        break;
                    case DECIMAL_TERM:
                        {
                        alt3=3;
                        }
                        break;
                    case FLOAT_TERM:
                        {
                        alt3=4;
                        }
                        break;
                    case STRING_TERM:
                        {
                        alt3=5;
                        }
                        break;
                    case IRI_REF:
                    case PREFIXED_NAME:
                        {
                        alt3=1;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 2, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:232:4: ^( DATATYPE iriRef )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_datatype230); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_iriRef_in_datatype232);
                    iriRef9=iriRef();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     n = iriRef9; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:233:4: ^( DATATYPE INTEGER_TERM )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_datatype241); 

                    match(input, Token.DOWN, null); 
                    match(input,INTEGER_TERM,FOLLOW_INTEGER_TERM_in_datatype243); 

                    match(input, Token.UP, null); 
                     n = XSD.integer.asNode(); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:234:4: ^( DATATYPE DECIMAL_TERM )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_datatype252); 

                    match(input, Token.DOWN, null); 
                    match(input,DECIMAL_TERM,FOLLOW_DECIMAL_TERM_in_datatype254); 

                    match(input, Token.UP, null); 
                     n = XSD.decimal.asNode(); 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:235:4: ^( DATATYPE FLOAT_TERM )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_datatype263); 

                    match(input, Token.DOWN, null); 
                    match(input,FLOAT_TERM,FOLLOW_FLOAT_TERM_in_datatype265); 

                    match(input, Token.UP, null); 
                     n = XSD.xfloat.asNode(); 

                    }
                    break;
                case 5 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:236:4: ^( DATATYPE STRING_TERM )
                    {
                    match(input,DATATYPE,FOLLOW_DATATYPE_in_datatype274); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING_TERM,FOLLOW_STRING_TERM_in_datatype276); 

                    match(input, Token.UP, null); 
                     n = XSD.xstring.asNode(); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "datatype"

    public static class individual_return extends TreeRuleReturnScope {
        public Node i;
        public Collection<Triple> triples;
    };

    // $ANTLR start "individual"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:242:1: individual returns [Node i, Collection<Triple> triples] : ^( INDIVIDUAL iriRef ) ;
    public final SparqlOwlTreeARQ.individual_return individual() throws RecognitionException {
        SparqlOwlTreeARQ.individual_return retval = new SparqlOwlTreeARQ.individual_return();
        retval.start = input.LT(1);

        Node iriRef10 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:244:2: ( ^( INDIVIDUAL iriRef ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:244:4: ^( INDIVIDUAL iriRef )
            {
            match(input,INDIVIDUAL,FOLLOW_INDIVIDUAL_in_individual298); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_iriRef_in_individual300);
            iriRef10=iriRef();

            state._fsp--;


            match(input, Token.UP, null); 

            			retval.i = iriRef10;
            			// FIXME: Consider adding a type owl:NamedIndividual or owl:Thing triple
            			retval.triples = Collections.emptyList();
            		

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "individual"


    // $ANTLR start "literal"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:255:1: literal returns [Node l] : ( rdfLiteral | numericLiteral | booleanLiteral );
    public final Node literal() throws RecognitionException {
        Node l = null;

        Node rdfLiteral11 = null;

        Node numericLiteral12 = null;

        Node booleanLiteral13 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:257:2: ( rdfLiteral | numericLiteral | booleanLiteral )
            int alt4=3;
            switch ( input.LA(1) ) {
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
                {
                alt4=1;
                }
                break;
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
                {
                alt4=2;
                }
                break;
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:257:4: rdfLiteral
                    {
                    pushFollow(FOLLOW_rdfLiteral_in_literal323);
                    rdfLiteral11=rdfLiteral();

                    state._fsp--;

                     l = rdfLiteral11; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:259:4: numericLiteral
                    {
                    pushFollow(FOLLOW_numericLiteral_in_literal332);
                    numericLiteral12=numericLiteral();

                    state._fsp--;

                     l = numericLiteral12; 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:261:4: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_literal341);
                    booleanLiteral13=booleanLiteral();

                    state._fsp--;

                     l = booleanLiteral13; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return l;
    }
    // $ANTLR end "literal"

    public static class datatypeRestriction_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
    };

    // $ANTLR start "datatypeRestriction"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:268:1: datatypeRestriction returns [Node n, Collection<Triple> triples] : ^( DATATYPE_RESTRICTION datatype ( ^( FACET_VALUE facet restrictionValue ) )+ ) ;
    public final SparqlOwlTreeARQ.datatypeRestriction_return datatypeRestriction() throws RecognitionException {
        SparqlOwlTreeARQ.datatypeRestriction_return retval = new SparqlOwlTreeARQ.datatypeRestriction_return();
        retval.start = input.LT(1);

        Node datatype14 = null;

        Node facet15 = null;

        Node restrictionValue16 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:270:2: ( ^( DATATYPE_RESTRICTION datatype ( ^( FACET_VALUE facet restrictionValue ) )+ ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:270:4: ^( DATATYPE_RESTRICTION datatype ( ^( FACET_VALUE facet restrictionValue ) )+ )
            {
            match(input,DATATYPE_RESTRICTION,FOLLOW_DATATYPE_RESTRICTION_in_datatypeRestriction364); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_datatype_in_datatypeRestriction366);
            datatype14=datatype();

            state._fsp--;


            				retval.triples = new ArrayList<Triple>();
            				retval.n = getAnon( );
            				retval.triples.add( new Triple( retval.n, RDF.Nodes.type, RDFS.Datatype.asNode() ) );
            				retval.triples.add( new Triple( retval.n, OWL2.onDatatype.asNode(), datatype14 ) );
            				List<Node> facetValues = new ArrayList<Node>();
            			
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:278:4: ( ^( FACET_VALUE facet restrictionValue ) )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==FACET_VALUE) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:278:6: ^( FACET_VALUE facet restrictionValue )
            	    {
            	    match(input,FACET_VALUE,FOLLOW_FACET_VALUE_in_datatypeRestriction379); 

            	    match(input, Token.DOWN, null); 
            	    pushFollow(FOLLOW_facet_in_datatypeRestriction381);
            	    facet15=facet();

            	    state._fsp--;

            	    pushFollow(FOLLOW_restrictionValue_in_datatypeRestriction383);
            	    restrictionValue16=restrictionValue();

            	    state._fsp--;


            	    match(input, Token.UP, null); 

            	    					Node y = getAnon( );
            	    					facetValues.add( y );
            	    					retval.triples.add( new Triple( y, facet15, restrictionValue16 ) );
            	    				

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            				Node list = listToTriples( facetValues, retval.triples );
            				retval.triples.add( new Triple( retval.n, OWL2.withRestrictions.asNode(), list ) );
            			

            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "datatypeRestriction"


    // $ANTLR start "facet"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:295:1: facet returns [Node n] : ( FACET_LENGTH | FACET_MINLENGTH | FACET_MAXLENGTH | FACET_PATTERN | FACET_LANGPATTERN | FACET_LESS_EQUAL | FACET_LESS | FACET_GREATER_EQUAL | FACET_GREATER );
    public final Node facet() throws RecognitionException {
        Node n = null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:297:2: ( FACET_LENGTH | FACET_MINLENGTH | FACET_MAXLENGTH | FACET_PATTERN | FACET_LANGPATTERN | FACET_LESS_EQUAL | FACET_LESS | FACET_GREATER_EQUAL | FACET_GREATER )
            int alt6=9;
            switch ( input.LA(1) ) {
            case FACET_LENGTH:
                {
                alt6=1;
                }
                break;
            case FACET_MINLENGTH:
                {
                alt6=2;
                }
                break;
            case FACET_MAXLENGTH:
                {
                alt6=3;
                }
                break;
            case FACET_PATTERN:
                {
                alt6=4;
                }
                break;
            case FACET_LANGPATTERN:
                {
                alt6=5;
                }
                break;
            case FACET_LESS_EQUAL:
                {
                alt6=6;
                }
                break;
            case FACET_LESS:
                {
                alt6=7;
                }
                break;
            case FACET_GREATER_EQUAL:
                {
                alt6=8;
                }
                break;
            case FACET_GREATER:
                {
                alt6=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:297:4: FACET_LENGTH
                    {
                    match(input,FACET_LENGTH,FOLLOW_FACET_LENGTH_in_facet423); 
                     n = OWL2.length.asNode(); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:298:4: FACET_MINLENGTH
                    {
                    match(input,FACET_MINLENGTH,FOLLOW_FACET_MINLENGTH_in_facet430); 
                     n = OWL2.minLength.asNode(); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:299:4: FACET_MAXLENGTH
                    {
                    match(input,FACET_MAXLENGTH,FOLLOW_FACET_MAXLENGTH_in_facet437); 
                     n = OWL2.maxLength.asNode(); 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:300:4: FACET_PATTERN
                    {
                    match(input,FACET_PATTERN,FOLLOW_FACET_PATTERN_in_facet444); 
                     n = OWL2.pattern.asNode(); 

                    }
                    break;
                case 5 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:301:4: FACET_LANGPATTERN
                    {
                    match(input,FACET_LANGPATTERN,FOLLOW_FACET_LANGPATTERN_in_facet451); 
                     /* FIXME: langPattern missing */ n = null; 

                    }
                    break;
                case 6 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:302:4: FACET_LESS_EQUAL
                    {
                    match(input,FACET_LESS_EQUAL,FOLLOW_FACET_LESS_EQUAL_in_facet458); 
                     n = OWL2.maxInclusive.asNode(); 

                    }
                    break;
                case 7 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:303:4: FACET_LESS
                    {
                    match(input,FACET_LESS,FOLLOW_FACET_LESS_in_facet465); 
                     n = OWL2.maxExclusive.asNode(); 

                    }
                    break;
                case 8 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:304:4: FACET_GREATER_EQUAL
                    {
                    match(input,FACET_GREATER_EQUAL,FOLLOW_FACET_GREATER_EQUAL_in_facet472); 
                     n = OWL2.minInclusive.asNode(); 

                    }
                    break;
                case 9 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:305:4: FACET_GREATER
                    {
                    match(input,FACET_GREATER,FOLLOW_FACET_GREATER_in_facet479); 
                     n = OWL2.minExclusive.asNode(); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "facet"


    // $ANTLR start "restrictionValue"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:311:1: restrictionValue returns [Node n] : literal ;
    public final Node restrictionValue() throws RecognitionException {
        Node n = null;

        Node literal17 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:313:2: ( literal )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:313:4: literal
            {
            pushFollow(FOLLOW_literal_in_restrictionValue499);
            literal17=literal();

            state._fsp--;

             n = literal17; 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "restrictionValue"

    public static class disjunction_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
        public boolean dr;
    };

    // $ANTLR start "disjunction"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:319:1: disjunction returns [Node n, Collection<Triple> triples, boolean dr] : ( ^( DISJUNCTION a= disjunction b= disjunction ) | conjunction | primary );
    public final SparqlOwlTreeARQ.disjunction_return disjunction() throws RecognitionException {
        SparqlOwlTreeARQ.disjunction_return retval = new SparqlOwlTreeARQ.disjunction_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.disjunction_return a = null;

        SparqlOwlTreeARQ.disjunction_return b = null;

        SparqlOwlTreeARQ.conjunction_return conjunction18 = null;

        SparqlOwlTreeARQ.primary_return primary19 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:321:2: ( ^( DISJUNCTION a= disjunction b= disjunction ) | conjunction | primary )
            int alt7=3;
            switch ( input.LA(1) ) {
            case DISJUNCTION:
                {
                alt7=1;
                }
                break;
            case CONJUNCTION:
                {
                alt7=2;
                }
                break;
            case ALL_RESTRICTION:
            case CLASS_OR_DATATYPE:
            case DATATYPE:
            case DATATYPE_RESTRICTION:
            case EXACT_NUMBER_RESTRICTION:
            case INDIVIDUAL_ENUMERATION:
            case MAX_NUMBER_RESTRICTION:
            case MIN_NUMBER_RESTRICTION:
            case NEGATION:
            case SELF_RESTRICTION:
            case SOME_RESTRICTION:
            case VALUE_ENUMERATION:
            case VALUE_RESTRICTION:
                {
                alt7=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:321:4: ^( DISJUNCTION a= disjunction b= disjunction )
                    {
                    match(input,DISJUNCTION,FOLLOW_DISJUNCTION_in_disjunction520); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_disjunction_in_disjunction524);
                    a=disjunction();

                    state._fsp--;

                    pushFollow(FOLLOW_disjunction_in_disjunction528);
                    b=disjunction();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.triples = new ArrayList<Triple>();
                    			final Node list = listToTriples( Arrays.asList( (a!=null?a.n:null), (b!=null?b.n:null) ), retval.triples );

                    			retval.n = getAnon( );
                    			retval.triples.add( new Triple( retval.n, OWL.unionOf.asNode(), list ) );

                    			retval.triples.addAll( (a!=null?a.triples:null) );
                    			retval.triples.addAll( (b!=null?b.triples:null) );
                    			
                    			retval.dr = (a!=null?a.dr:false) && (b!=null?b.dr:false);
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:334:4: conjunction
                    {
                    pushFollow(FOLLOW_conjunction_in_disjunction538);
                    conjunction18=conjunction();

                    state._fsp--;


                    			retval.n = (conjunction18!=null?conjunction18.n:null);
                    			retval.triples = (conjunction18!=null?conjunction18.triples:null);
                    			retval.dr = (conjunction18!=null?conjunction18.dr:false);
                    		

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:340:4: primary
                    {
                    pushFollow(FOLLOW_primary_in_disjunction547);
                    primary19=primary();

                    state._fsp--;


                    			retval.n = (primary19!=null?primary19.n:null);
                    			retval.triples = (primary19!=null?primary19.triples:null);
                    			retval.dr = (primary19!=null?primary19.dr:false);
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "disjunction"

    public static class conjunction_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
        public boolean dr;
    };

    // $ANTLR start "conjunction"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:351:1: conjunction returns [Node n, Collection<Triple> triples, boolean dr] : ^( CONJUNCTION a= disjunction b= disjunction ) ;
    public final SparqlOwlTreeARQ.conjunction_return conjunction() throws RecognitionException {
        SparqlOwlTreeARQ.conjunction_return retval = new SparqlOwlTreeARQ.conjunction_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.disjunction_return a = null;

        SparqlOwlTreeARQ.disjunction_return b = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:353:2: ( ^( CONJUNCTION a= disjunction b= disjunction ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:353:4: ^( CONJUNCTION a= disjunction b= disjunction )
            {
            match(input,CONJUNCTION,FOLLOW_CONJUNCTION_in_conjunction570); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_disjunction_in_conjunction574);
            a=disjunction();

            state._fsp--;

            pushFollow(FOLLOW_disjunction_in_conjunction578);
            b=disjunction();

            state._fsp--;


            match(input, Token.UP, null); 

            			retval.triples = new ArrayList<Triple>();
            			final Node list = listToTriples( Arrays.asList( (a!=null?a.n:null), (b!=null?b.n:null) ), retval.triples );

            			retval.n = getAnon( );
            			retval.triples.add( new Triple( retval.n, OWL.intersectionOf.asNode(), list ) );

            			retval.triples.addAll( (a!=null?a.triples:null) );
            			retval.triples.addAll( (b!=null?b.triples:null) );
            			
            			retval.dr = (a!=null?a.dr:false) && (b!=null?b.dr:false);
            		

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "conjunction"

    public static class primary_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
        public boolean dr;
    };

    // $ANTLR start "primary"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:371:1: primary returns [Node n, Collection<Triple> triples, boolean dr] : ( ^( NEGATION disjunction ) | restriction | atomic );
    public final SparqlOwlTreeARQ.primary_return primary() throws RecognitionException {
        SparqlOwlTreeARQ.primary_return retval = new SparqlOwlTreeARQ.primary_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.disjunction_return disjunction20 = null;

        SparqlOwlTreeARQ.restriction_return restriction21 = null;

        SparqlOwlTreeARQ.atomic_return atomic22 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:373:2: ( ^( NEGATION disjunction ) | restriction | atomic )
            int alt8=3;
            switch ( input.LA(1) ) {
            case NEGATION:
                {
                alt8=1;
                }
                break;
            case ALL_RESTRICTION:
            case EXACT_NUMBER_RESTRICTION:
            case MAX_NUMBER_RESTRICTION:
            case MIN_NUMBER_RESTRICTION:
            case SELF_RESTRICTION:
            case SOME_RESTRICTION:
            case VALUE_RESTRICTION:
                {
                alt8=2;
                }
                break;
            case CLASS_OR_DATATYPE:
            case DATATYPE:
            case DATATYPE_RESTRICTION:
            case INDIVIDUAL_ENUMERATION:
            case VALUE_ENUMERATION:
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:373:4: ^( NEGATION disjunction )
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_primary602); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_disjunction_in_primary604);
                    disjunction20=disjunction();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.n = getAnon( );
                    			retval.triples = new ArrayList<Triple>();
                    			retval.triples.addAll( (disjunction20!=null?disjunction20.triples:null) );
                    			if ( (disjunction20!=null?disjunction20.dr:false) ) {
                    				retval.triples.add( new Triple( retval.n, OWL2.datatypeComplementOf.asNode(), (disjunction20!=null?disjunction20.n:null) ) );
                    				retval.dr = true;
                    			}
                    			else
                    				retval.triples.add( new Triple( retval.n, OWL.complementOf.asNode(), (disjunction20!=null?disjunction20.n:null) ) );
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:385:4: restriction
                    {
                    pushFollow(FOLLOW_restriction_in_primary614);
                    restriction21=restriction();

                    state._fsp--;


                    			retval.n = (restriction21!=null?restriction21.n:null);
                    			retval.triples = (restriction21!=null?restriction21.triples:null);
                    			retval.dr = false;
                    		

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:391:4: atomic
                    {
                    pushFollow(FOLLOW_atomic_in_primary624);
                    atomic22=atomic();

                    state._fsp--;


                    			retval.n = (atomic22!=null?atomic22.n:null);
                    			retval.triples = (atomic22!=null?atomic22.triples:null);
                    			retval.dr = (atomic22!=null?atomic22.dr:false);
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class atomic_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
        public boolean dr;
    };

    // $ANTLR start "atomic"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:402:1: atomic returns [Node n, Collection<Triple> triples, boolean dr] : ( ^( CLASS_OR_DATATYPE iriRef ) | datatype | datatypeRestriction | ^( VALUE_ENUMERATION ( literal )+ ) | ^( INDIVIDUAL_ENUMERATION ( individual )+ ) );
    public final SparqlOwlTreeARQ.atomic_return atomic() throws RecognitionException {
        SparqlOwlTreeARQ.atomic_return retval = new SparqlOwlTreeARQ.atomic_return();
        retval.start = input.LT(1);

        Node iriRef23 = null;

        Node datatype24 = null;

        SparqlOwlTreeARQ.datatypeRestriction_return datatypeRestriction25 = null;

        Node literal26 = null;

        SparqlOwlTreeARQ.individual_return individual27 = null;



        		retval.dr = false;
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:407:2: ( ^( CLASS_OR_DATATYPE iriRef ) | datatype | datatypeRestriction | ^( VALUE_ENUMERATION ( literal )+ ) | ^( INDIVIDUAL_ENUMERATION ( individual )+ ) )
            int alt11=5;
            switch ( input.LA(1) ) {
            case CLASS_OR_DATATYPE:
                {
                alt11=1;
                }
                break;
            case DATATYPE:
                {
                alt11=2;
                }
                break;
            case DATATYPE_RESTRICTION:
                {
                alt11=3;
                }
                break;
            case VALUE_ENUMERATION:
                {
                alt11=4;
                }
                break;
            case INDIVIDUAL_ENUMERATION:
                {
                alt11=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:407:4: ^( CLASS_OR_DATATYPE iriRef )
                    {
                    match(input,CLASS_OR_DATATYPE,FOLLOW_CLASS_OR_DATATYPE_in_atomic653); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_iriRef_in_atomic655);
                    iriRef23=iriRef();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.n = iriRef23;
                    			retval.triples = Collections.emptyList();
                    			retval.dr = isOWL2Datatype( retval.n );
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:413:4: datatype
                    {
                    pushFollow(FOLLOW_datatype_in_atomic665);
                    datatype24=datatype();

                    state._fsp--;


                    			retval.n = datatype24;
                    			retval.triples = Collections.emptyList();
                    			retval.dr = true;
                    		

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:419:4: datatypeRestriction
                    {
                    pushFollow(FOLLOW_datatypeRestriction_in_atomic674);
                    datatypeRestriction25=datatypeRestriction();

                    state._fsp--;


                    			retval.n = (datatypeRestriction25!=null?datatypeRestriction25.n:null);
                    			retval.triples = (datatypeRestriction25!=null?datatypeRestriction25.triples:null);
                    			retval.dr = true;
                    		

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:425:4: ^( VALUE_ENUMERATION ( literal )+ )
                    {
                    match(input,VALUE_ENUMERATION,FOLLOW_VALUE_ENUMERATION_in_atomic684); 


                    				retval.triples = new ArrayList<Triple>();
                    				List<Node> ls = new ArrayList<Node>();
                    				retval.dr = true;
                    			

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:431:4: ( literal )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>=LITERAL_BOOLEAN_FALSE && LA9_0<=LITERAL_TYPED)) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:431:6: literal
                    	    {
                    	    pushFollow(FOLLOW_literal_in_atomic696);
                    	    literal26=literal();

                    	    state._fsp--;

                    	     ls.add( literal26 ); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);


                    match(input, Token.UP, null); 

                    			Node list = listToTriples( ls, retval.triples );
                    			retval.n = getAnon( );
                    			retval.triples.add( new Triple( retval.n, RDF.Nodes.type, RDFS.Datatype.asNode() ) );
                    			retval.triples.add( new Triple( retval.n, OWL.oneOf.asNode(), list ) );
                    		

                    }
                    break;
                case 5 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:439:4: ^( INDIVIDUAL_ENUMERATION ( individual )+ )
                    {
                    match(input,INDIVIDUAL_ENUMERATION,FOLLOW_INDIVIDUAL_ENUMERATION_in_atomic715); 


                    				retval.triples = new ArrayList<Triple>();
                    				List<Node> is = new ArrayList<Node>();
                    			

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:444:4: ( individual )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==INDIVIDUAL) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:444:6: individual
                    	    {
                    	    pushFollow(FOLLOW_individual_in_atomic727);
                    	    individual27=individual();

                    	    state._fsp--;

                    	     is.add( (individual27!=null?individual27.i:null) ); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);


                    match(input, Token.UP, null); 

                    			Node list = listToTriples( is, retval.triples );
                    			retval.n = getAnon( );
                    			retval.triples.add( new Triple( retval.n, RDF.Nodes.type, OWL.Class.asNode() ) );
                    			retval.triples.add( new Triple( retval.n, OWL.oneOf.asNode(), list ) );
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "atomic"

    public static class restriction_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
    };

    // $ANTLR start "restriction"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:457:1: restriction returns [Node n, Collection<Triple> triples] : ( ^( SOME_RESTRICTION propertyExpression disjunction ) | ^( ALL_RESTRICTION propertyExpression disjunction ) | ^( VALUE_RESTRICTION objectPropertyExpression individual ) | ^( VALUE_RESTRICTION dataPropertyIRI literal ) | ^( SELF_RESTRICTION objectPropertyExpression ) | ^( MIN_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) | ^( MAX_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) | ^( EXACT_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) );
    public final SparqlOwlTreeARQ.restriction_return restriction() throws RecognitionException {
        SparqlOwlTreeARQ.restriction_return retval = new SparqlOwlTreeARQ.restriction_return();
        retval.start = input.LT(1);

        CommonTree i=null;
        SparqlOwlTreeARQ.propertyExpression_return propertyExpression28 = null;

        SparqlOwlTreeARQ.disjunction_return disjunction29 = null;

        SparqlOwlTreeARQ.propertyExpression_return propertyExpression30 = null;

        SparqlOwlTreeARQ.disjunction_return disjunction31 = null;

        SparqlOwlTreeARQ.objectPropertyExpression_return objectPropertyExpression32 = null;

        SparqlOwlTreeARQ.individual_return individual33 = null;

        SparqlOwlTreeARQ.dataPropertyIRI_return dataPropertyIRI34 = null;

        Node literal35 = null;

        SparqlOwlTreeARQ.objectPropertyExpression_return objectPropertyExpression36 = null;

        SparqlOwlTreeARQ.disjunction_return disjunction37 = null;

        SparqlOwlTreeARQ.propertyExpression_return propertyExpression38 = null;

        SparqlOwlTreeARQ.disjunction_return disjunction39 = null;

        SparqlOwlTreeARQ.propertyExpression_return propertyExpression40 = null;

        SparqlOwlTreeARQ.disjunction_return disjunction41 = null;

        SparqlOwlTreeARQ.propertyExpression_return propertyExpression42 = null;



        		retval.n = getAnon( );
        		retval.triples = new ArrayList<Triple>();
        		retval.triples.add( new Triple( retval.n, RDF.Nodes.type, OWL.Restriction.asNode() ) );
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:464:2: ( ^( SOME_RESTRICTION propertyExpression disjunction ) | ^( ALL_RESTRICTION propertyExpression disjunction ) | ^( VALUE_RESTRICTION objectPropertyExpression individual ) | ^( VALUE_RESTRICTION dataPropertyIRI literal ) | ^( SELF_RESTRICTION objectPropertyExpression ) | ^( MIN_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) | ^( MAX_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) | ^( EXACT_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) )
            int alt15=8;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:464:4: ^( SOME_RESTRICTION propertyExpression disjunction )
                    {
                    match(input,SOME_RESTRICTION,FOLLOW_SOME_RESTRICTION_in_restriction765); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_propertyExpression_in_restriction767);
                    propertyExpression28=propertyExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_disjunction_in_restriction769);
                    disjunction29=disjunction();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (propertyExpression28!=null?propertyExpression28.p:null) ) );
                    			retval.triples.add( new Triple( retval.n, OWL.someValuesFrom.asNode(), (disjunction29!=null?disjunction29.n:null) ) );
                    			retval.triples.addAll( (propertyExpression28!=null?propertyExpression28.triples:null) );
                    			retval.triples.addAll( (disjunction29!=null?disjunction29.triples:null) );
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:471:4: ^( ALL_RESTRICTION propertyExpression disjunction )
                    {
                    match(input,ALL_RESTRICTION,FOLLOW_ALL_RESTRICTION_in_restriction780); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_propertyExpression_in_restriction782);
                    propertyExpression30=propertyExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_disjunction_in_restriction784);
                    disjunction31=disjunction();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (propertyExpression30!=null?propertyExpression30.p:null) ) );
                    			retval.triples.add( new Triple( retval.n, OWL.allValuesFrom.asNode(), (disjunction31!=null?disjunction31.n:null) ) );
                    			retval.triples.addAll( (propertyExpression30!=null?propertyExpression30.triples:null) );
                    			retval.triples.addAll( (disjunction31!=null?disjunction31.triples:null) );
                    		

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:478:4: ^( VALUE_RESTRICTION objectPropertyExpression individual )
                    {
                    match(input,VALUE_RESTRICTION,FOLLOW_VALUE_RESTRICTION_in_restriction795); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_objectPropertyExpression_in_restriction797);
                    objectPropertyExpression32=objectPropertyExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_individual_in_restriction799);
                    individual33=individual();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (objectPropertyExpression32!=null?objectPropertyExpression32.p:null) ) );
                    			retval.triples.add( new Triple( retval.n, OWL.hasValue.asNode(), (individual33!=null?individual33.i:null) ) );
                    			retval.triples.addAll( (objectPropertyExpression32!=null?objectPropertyExpression32.triples:null) );
                    			retval.triples.addAll( (individual33!=null?individual33.triples:null) );
                    		

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:485:4: ^( VALUE_RESTRICTION dataPropertyIRI literal )
                    {
                    match(input,VALUE_RESTRICTION,FOLLOW_VALUE_RESTRICTION_in_restriction810); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_dataPropertyIRI_in_restriction812);
                    dataPropertyIRI34=dataPropertyIRI();

                    state._fsp--;

                    pushFollow(FOLLOW_literal_in_restriction814);
                    literal35=literal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (dataPropertyIRI34!=null?dataPropertyIRI34.p:null) ) );
                    			retval.triples.add( new Triple( retval.n, OWL.hasValue.asNode(), literal35 ) );
                    			retval.triples.addAll( (dataPropertyIRI34!=null?dataPropertyIRI34.triples:null) );
                    		

                    }
                    break;
                case 5 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:491:4: ^( SELF_RESTRICTION objectPropertyExpression )
                    {
                    match(input,SELF_RESTRICTION,FOLLOW_SELF_RESTRICTION_in_restriction825); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_objectPropertyExpression_in_restriction827);
                    objectPropertyExpression36=objectPropertyExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (objectPropertyExpression36!=null?objectPropertyExpression36.p:null) ) );
                    			retval.triples.add( new Triple( retval.n, OWL2.hasSelf.asNode(), XSD_BOOLEAN_TRUE ) );
                    			retval.triples.addAll( (objectPropertyExpression36!=null?objectPropertyExpression36.triples:null) );
                    		

                    }
                    break;
                case 6 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:497:4: ^( MIN_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? )
                    {
                    match(input,MIN_NUMBER_RESTRICTION,FOLLOW_MIN_NUMBER_RESTRICTION_in_restriction838); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_propertyExpression_in_restriction840);
                    propertyExpression38=propertyExpression();

                    state._fsp--;

                    i=(CommonTree)match(input,INTEGER,FOLLOW_INTEGER_in_restriction844); 

                    				boolean dr = false;
                    				Node q = null;
                    		
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:502:4: ( disjunction )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==ALL_RESTRICTION||LA12_0==CLASS_OR_DATATYPE||LA12_0==CONJUNCTION||(LA12_0>=DATATYPE && LA12_0<=DATATYPE_RESTRICTION)||(LA12_0>=DISJUNCTION && LA12_0<=EXACT_NUMBER_RESTRICTION)||LA12_0==INDIVIDUAL_ENUMERATION||(LA12_0>=MAX_NUMBER_RESTRICTION && LA12_0<=MIN_NUMBER_RESTRICTION)||LA12_0==NEGATION||(LA12_0>=SELF_RESTRICTION && LA12_0<=SOME_RESTRICTION)||(LA12_0>=VALUE_ENUMERATION && LA12_0<=VALUE_RESTRICTION)) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:502:6: disjunction
                            {
                            pushFollow(FOLLOW_disjunction_in_restriction855);
                            disjunction37=disjunction();

                            state._fsp--;


                            					dr = (disjunction37!=null?disjunction37.dr:false);
                            					q = (disjunction37!=null?disjunction37.n:null);
                            					retval.triples.addAll( (disjunction37!=null?disjunction37.triples:null) );
                            				

                            }
                            break;

                    }


                    			Node num = createNonNegativeInteger( (i!=null?i.getText():null) );
                    			
                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (propertyExpression38!=null?propertyExpression38.p:null) ) );
                    			if ( q == null )
                    				retval.triples.add( new Triple( retval.n, OWL.minCardinality.asNode(), num ) );
                    			else {
                    				retval.triples.add( new Triple( retval.n, OWL2.minQualifiedCardinality.asNode(), num ) );
                    				retval.triples.add( new Triple( retval.n, dr
                    					? OWL2.onDataRange.asNode()
                    					: OWL2.onClass.asNode(), q ) );
                    			}

                    			retval.triples.addAll( (propertyExpression38!=null?propertyExpression38.triples:null) );
                    		

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:524:4: ^( MAX_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? )
                    {
                    match(input,MAX_NUMBER_RESTRICTION,FOLLOW_MAX_NUMBER_RESTRICTION_in_restriction878); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_propertyExpression_in_restriction880);
                    propertyExpression40=propertyExpression();

                    state._fsp--;

                    i=(CommonTree)match(input,INTEGER,FOLLOW_INTEGER_in_restriction884); 

                    				boolean dr = false;
                    				Node q = null;
                    		
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:529:4: ( disjunction )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==ALL_RESTRICTION||LA13_0==CLASS_OR_DATATYPE||LA13_0==CONJUNCTION||(LA13_0>=DATATYPE && LA13_0<=DATATYPE_RESTRICTION)||(LA13_0>=DISJUNCTION && LA13_0<=EXACT_NUMBER_RESTRICTION)||LA13_0==INDIVIDUAL_ENUMERATION||(LA13_0>=MAX_NUMBER_RESTRICTION && LA13_0<=MIN_NUMBER_RESTRICTION)||LA13_0==NEGATION||(LA13_0>=SELF_RESTRICTION && LA13_0<=SOME_RESTRICTION)||(LA13_0>=VALUE_ENUMERATION && LA13_0<=VALUE_RESTRICTION)) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:529:6: disjunction
                            {
                            pushFollow(FOLLOW_disjunction_in_restriction895);
                            disjunction39=disjunction();

                            state._fsp--;


                            					dr = (disjunction39!=null?disjunction39.dr:false);
                            					q = (disjunction39!=null?disjunction39.n:null);
                            					retval.triples.addAll( (disjunction39!=null?disjunction39.triples:null) );
                            				

                            }
                            break;

                    }


                    			Node num = createNonNegativeInteger( (i!=null?i.getText():null) );
                    			
                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (propertyExpression40!=null?propertyExpression40.p:null) ) );
                    			if ( q == null )
                    				retval.triples.add( new Triple( retval.n, OWL.maxCardinality.asNode(), num ) );
                    			else {
                    				retval.triples.add( new Triple( retval.n, OWL2.maxQualifiedCardinality.asNode(), num ) );
                    				retval.triples.add( new Triple( retval.n, dr
                    					? OWL2.onDataRange.asNode()
                    					: OWL2.onClass.asNode(), q ) );
                    			}

                    			retval.triples.addAll( (propertyExpression40!=null?propertyExpression40.triples:null) );
                    		

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:551:4: ^( EXACT_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? )
                    {
                    match(input,EXACT_NUMBER_RESTRICTION,FOLLOW_EXACT_NUMBER_RESTRICTION_in_restriction918); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_propertyExpression_in_restriction920);
                    propertyExpression42=propertyExpression();

                    state._fsp--;

                    i=(CommonTree)match(input,INTEGER,FOLLOW_INTEGER_in_restriction924); 

                    				boolean dr = false;
                    				Node q = null;
                    		
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:556:4: ( disjunction )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==ALL_RESTRICTION||LA14_0==CLASS_OR_DATATYPE||LA14_0==CONJUNCTION||(LA14_0>=DATATYPE && LA14_0<=DATATYPE_RESTRICTION)||(LA14_0>=DISJUNCTION && LA14_0<=EXACT_NUMBER_RESTRICTION)||LA14_0==INDIVIDUAL_ENUMERATION||(LA14_0>=MAX_NUMBER_RESTRICTION && LA14_0<=MIN_NUMBER_RESTRICTION)||LA14_0==NEGATION||(LA14_0>=SELF_RESTRICTION && LA14_0<=SOME_RESTRICTION)||(LA14_0>=VALUE_ENUMERATION && LA14_0<=VALUE_RESTRICTION)) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:556:6: disjunction
                            {
                            pushFollow(FOLLOW_disjunction_in_restriction935);
                            disjunction41=disjunction();

                            state._fsp--;


                            					dr = (disjunction41!=null?disjunction41.dr:false);
                            					q = (disjunction41!=null?disjunction41.n:null);
                            					retval.triples.addAll( (disjunction41!=null?disjunction41.triples:null) );
                            				

                            }
                            break;

                    }


                    			Node num = createNonNegativeInteger( (i!=null?i.getText():null) );
                    			
                    			retval.triples.add( new Triple( retval.n, OWL.onProperty.asNode(), (propertyExpression42!=null?propertyExpression42.p:null) ) );
                    			if ( q == null )
                    				retval.triples.add( new Triple( retval.n, OWL.cardinality.asNode(), num ) );
                    			else {
                    				retval.triples.add( new Triple( retval.n, OWL2.qualifiedCardinality.asNode(), num ) );
                    				retval.triples.add( new Triple( retval.n, dr
                    					? OWL2.onDataRange.asNode()
                    					: OWL2.onClass.asNode(), q ) );
                    			}

                    			retval.triples.addAll( (propertyExpression42!=null?propertyExpression42.triples:null) );
                    		

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "restriction"


    // $ANTLR start "query"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:583:1: query[Query in] returns [Query q] : ^( QUERY prologue[$q] ( selectQuery[$q] | constructQuery[$q] | describeQuery[$q] | askQuery[$q] ) ) EOF ;
    public final Query query(Query in) throws RecognitionException {
        Query q = null;


        			q = in == null ? new Query( ) : in;
        			this.prologue = q;
        			this.inConstructTemplate = false;
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:590:2: ( ^( QUERY prologue[$q] ( selectQuery[$q] | constructQuery[$q] | describeQuery[$q] | askQuery[$q] ) ) EOF )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:590:4: ^( QUERY prologue[$q] ( selectQuery[$q] | constructQuery[$q] | describeQuery[$q] | askQuery[$q] ) ) EOF
            {
            match(input,QUERY,FOLLOW_QUERY_in_query978); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_prologue_in_query983);
            prologue(q);

            state._fsp--;

            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:592:4: ( selectQuery[$q] | constructQuery[$q] | describeQuery[$q] | askQuery[$q] )
            int alt16=4;
            switch ( input.LA(1) ) {
            case SELECT:
                {
                alt16=1;
                }
                break;
            case CONSTRUCT:
                {
                alt16=2;
                }
                break;
            case DESCRIBE:
                {
                alt16=3;
                }
                break;
            case ASK:
                {
                alt16=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:592:6: selectQuery[$q]
                    {
                    pushFollow(FOLLOW_selectQuery_in_query991);
                    selectQuery(q);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:593:6: constructQuery[$q]
                    {
                    pushFollow(FOLLOW_constructQuery_in_query999);
                    constructQuery(q);

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:594:6: describeQuery[$q]
                    {
                    pushFollow(FOLLOW_describeQuery_in_query1007);
                    describeQuery(q);

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:595:6: askQuery[$q]
                    {
                    pushFollow(FOLLOW_askQuery_in_query1015);
                    askQuery(q);

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 
            match(input,EOF,FOLLOW_EOF_in_query1029); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return q;
    }
    // $ANTLR end "query"


    // $ANTLR start "prologue"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:604:1: prologue[Prologue p] : ( baseDecl )? ( prefixDecl )* ;
    public final void prologue(Prologue p) throws RecognitionException {
        String baseDecl43 = null;

        SparqlOwlTreeARQ.prefixDecl_return prefixDecl44 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:605:2: ( ( baseDecl )? ( prefixDecl )* )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:605:4: ( baseDecl )? ( prefixDecl )*
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:605:4: ( baseDecl )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==BASE_DECL) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:606:4: baseDecl
                    {
                    pushFollow(FOLLOW_baseDecl_in_prologue1048);
                    baseDecl43=baseDecl();

                    state._fsp--;

                     p.setBaseURI( baseDecl43 ); 

                    }
                    break;

            }

            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:609:3: ( prefixDecl )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==PREFIX_DECL) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:610:4: prefixDecl
            	    {
            	    pushFollow(FOLLOW_prefixDecl_in_prologue1067);
            	    prefixDecl44=prefixDecl();

            	    state._fsp--;

            	     p.setPrefix( (prefixDecl44!=null?prefixDecl44.prefix:null), (prefixDecl44!=null?prefixDecl44.expansion:null) ); 

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "prologue"


    // $ANTLR start "baseDecl"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:618:1: baseDecl returns [String base] : ^( BASE_DECL ref= IRI_REF_TERM ) ;
    public final String baseDecl() throws RecognitionException {
        String base = null;

        CommonTree ref=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:620:2: ( ^( BASE_DECL ref= IRI_REF_TERM ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:620:4: ^( BASE_DECL ref= IRI_REF_TERM )
            {
            match(input,BASE_DECL,FOLLOW_BASE_DECL_in_baseDecl1096); 

            match(input, Token.DOWN, null); 
            ref=(CommonTree)match(input,IRI_REF_TERM,FOLLOW_IRI_REF_TERM_in_baseDecl1100); 

            match(input, Token.UP, null); 
             base = (ref!=null?ref.getText():null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return base;
    }
    // $ANTLR end "baseDecl"

    public static class prefixDecl_return extends TreeRuleReturnScope {
        public String prefix;
        public String expansion;
    };

    // $ANTLR start "prefixDecl"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:627:1: prefixDecl returns [String prefix, String expansion] : ^( PREFIX_DECL pname= PNAME_NS ref= IRI_REF_TERM ) ;
    public final SparqlOwlTreeARQ.prefixDecl_return prefixDecl() throws RecognitionException {
        SparqlOwlTreeARQ.prefixDecl_return retval = new SparqlOwlTreeARQ.prefixDecl_return();
        retval.start = input.LT(1);

        CommonTree pname=null;
        CommonTree ref=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:629:2: ( ^( PREFIX_DECL pname= PNAME_NS ref= IRI_REF_TERM ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:629:4: ^( PREFIX_DECL pname= PNAME_NS ref= IRI_REF_TERM )
            {
            match(input,PREFIX_DECL,FOLLOW_PREFIX_DECL_in_prefixDecl1124); 

            match(input, Token.DOWN, null); 
            pname=(CommonTree)match(input,PNAME_NS,FOLLOW_PNAME_NS_in_prefixDecl1128); 
            ref=(CommonTree)match(input,IRI_REF_TERM,FOLLOW_IRI_REF_TERM_in_prefixDecl1132); 

            match(input, Token.UP, null); 

            		/*
            		 * Trim the final ':' off the token matched by PNAME_NS
            		 */
            		final int n = (pname!=null?pname.getText():null).length();
            		retval.prefix = ( n == 1 )
            			? ""
            			: (pname!=null?pname.getText():null).substring( 0, n - 1);
            		retval.expansion = (ref!=null?ref.getText():null);
            	

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "prefixDecl"


    // $ANTLR start "selectQuery"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:645:1: selectQuery[Query q] : ^( SELECT ( selectModifier[$q] )? selectVariableList[$q] ( datasets[$q] )? whereClause[$q] solutionModifier[$q] ) ;
    public final void selectQuery(Query q) throws RecognitionException {

        		q.setQuerySelectType();
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:649:2: ( ^( SELECT ( selectModifier[$q] )? selectVariableList[$q] ( datasets[$q] )? whereClause[$q] solutionModifier[$q] ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:649:4: ^( SELECT ( selectModifier[$q] )? selectVariableList[$q] ( datasets[$q] )? whereClause[$q] solutionModifier[$q] )
            {
            match(input,SELECT,FOLLOW_SELECT_in_selectQuery1157); 

            match(input, Token.DOWN, null); 
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:649:13: ( selectModifier[$q] )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=MODIFIER_DISTINCT && LA19_0<=MODIFIER_REDUCED)) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:649:13: selectModifier[$q]
                    {
                    pushFollow(FOLLOW_selectModifier_in_selectQuery1159);
                    selectModifier(q);

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_selectVariableList_in_selectQuery1163);
            selectVariableList(q);

            state._fsp--;

            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:649:56: ( datasets[$q] )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==DATASETS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:649:56: datasets[$q]
                    {
                    pushFollow(FOLLOW_datasets_in_selectQuery1166);
                    datasets(q);

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_whereClause_in_selectQuery1170);
            whereClause(q);

            state._fsp--;

            pushFollow(FOLLOW_solutionModifier_in_selectQuery1173);
            solutionModifier(q);

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "selectQuery"


    // $ANTLR start "selectModifier"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:652:1: selectModifier[Query q] : ( MODIFIER_DISTINCT | MODIFIER_REDUCED );
    public final void selectModifier(Query q) throws RecognitionException {
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:653:2: ( MODIFIER_DISTINCT | MODIFIER_REDUCED )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==MODIFIER_DISTINCT) ) {
                alt21=1;
            }
            else if ( (LA21_0==MODIFIER_REDUCED) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:653:4: MODIFIER_DISTINCT
                    {
                    match(input,MODIFIER_DISTINCT,FOLLOW_MODIFIER_DISTINCT_in_selectModifier1187); 
                     q.setDistinct( true ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:655:4: MODIFIER_REDUCED
                    {
                    match(input,MODIFIER_REDUCED,FOLLOW_MODIFIER_REDUCED_in_selectModifier1196); 
                     q.setReduced( true ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "selectModifier"


    // $ANTLR start "selectVariableList"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:659:1: selectVariableList[Query q] : ( ^( VARS ( var )+ ) | ALL_VARS );
    public final void selectVariableList(Query q) throws RecognitionException {
        Node var45 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:660:2: ( ^( VARS ( var )+ ) | ALL_VARS )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==VARS) ) {
                alt23=1;
            }
            else if ( (LA23_0==ALL_VARS) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:660:4: ^( VARS ( var )+ )
                    {
                    match(input,VARS,FOLLOW_VARS_in_selectVariableList1213); 

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:661:5: ( var )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==VARIABLE) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:661:6: var
                    	    {
                    	    pushFollow(FOLLOW_var_in_selectVariableList1220);
                    	    var45=var();

                    	    state._fsp--;

                    	     q.addResultVar( var45 ); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt22 >= 1 ) break loop22;
                                EarlyExitException eee =
                                    new EarlyExitException(22, input);
                                throw eee;
                        }
                        cnt22++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:665:4: ALL_VARS
                    {
                    match(input,ALL_VARS,FOLLOW_ALL_VARS_in_selectVariableList1243); 
                     q.setQueryResultStar( true ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "selectVariableList"


    // $ANTLR start "constructQuery"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:672:1: constructQuery[Query q] : ^( CONSTRUCT constructTemplate ( datasets[$q] )? whereClause[$q] solutionModifier[$q] ) ;
    public final void constructQuery(Query q) throws RecognitionException {
        Template constructTemplate46 = null;



        		q.setQueryConstructType();
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:676:2: ( ^( CONSTRUCT constructTemplate ( datasets[$q] )? whereClause[$q] solutionModifier[$q] ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:676:4: ^( CONSTRUCT constructTemplate ( datasets[$q] )? whereClause[$q] solutionModifier[$q] )
            {
            match(input,CONSTRUCT,FOLLOW_CONSTRUCT_in_constructQuery1269); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_constructTemplate_in_constructQuery1271);
            constructTemplate46=constructTemplate();

            state._fsp--;

            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:676:35: ( datasets[$q] )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==DATASETS) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:676:35: datasets[$q]
                    {
                    pushFollow(FOLLOW_datasets_in_constructQuery1273);
                    datasets(q);

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_whereClause_in_constructQuery1277);
            whereClause(q);

            state._fsp--;

            pushFollow(FOLLOW_solutionModifier_in_constructQuery1280);
            solutionModifier(q);

            state._fsp--;


            match(input, Token.UP, null); 
             q.setConstructTemplate( constructTemplate46 ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "constructQuery"


    // $ANTLR start "describeQuery"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:683:1: describeQuery[Query q] : ^( DESCRIBE describeTargets[q] ( datasets[$q] )? ( whereClause[$q] )? solutionModifier[$q] ) ;
    public final void describeQuery(Query q) throws RecognitionException {

        		q.setQueryDescribeType();
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:687:2: ( ^( DESCRIBE describeTargets[q] ( datasets[$q] )? ( whereClause[$q] )? solutionModifier[$q] ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:687:4: ^( DESCRIBE describeTargets[q] ( datasets[$q] )? ( whereClause[$q] )? solutionModifier[$q] )
            {
            match(input,DESCRIBE,FOLLOW_DESCRIBE_in_describeQuery1307); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_describeTargets_in_describeQuery1309);
            describeTargets(q);

            state._fsp--;

            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:687:34: ( datasets[$q] )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==DATASETS) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:687:34: datasets[$q]
                    {
                    pushFollow(FOLLOW_datasets_in_describeQuery1312);
                    datasets(q);

                    state._fsp--;


                    }
                    break;

            }

            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:687:48: ( whereClause[$q] )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==WHERE_CLAUSE) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:687:48: whereClause[$q]
                    {
                    pushFollow(FOLLOW_whereClause_in_describeQuery1316);
                    whereClause(q);

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_solutionModifier_in_describeQuery1320);
            solutionModifier(q);

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "describeQuery"


    // $ANTLR start "describeTargets"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:690:1: describeTargets[Query q] : ( ^( VARS_OR_IRIS ( varOrIRIref )+ ) | ALL_VARS );
    public final void describeTargets(Query q) throws RecognitionException {
        Node varOrIRIref47 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:691:2: ( ^( VARS_OR_IRIS ( varOrIRIref )+ ) | ALL_VARS )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==VARS_OR_IRIS) ) {
                alt28=1;
            }
            else if ( (LA28_0==ALL_VARS) ) {
                alt28=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:691:4: ^( VARS_OR_IRIS ( varOrIRIref )+ )
                    {
                    match(input,VARS_OR_IRIS,FOLLOW_VARS_OR_IRIS_in_describeTargets1335); 

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:692:4: ( varOrIRIref )+
                    int cnt27=0;
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==IRI_REF||LA27_0==PREFIXED_NAME||LA27_0==VARIABLE) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:692:5: varOrIRIref
                    	    {
                    	    pushFollow(FOLLOW_varOrIRIref_in_describeTargets1341);
                    	    varOrIRIref47=varOrIRIref();

                    	    state._fsp--;

                    	     q.addDescribeNode( varOrIRIref47 ); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt27 >= 1 ) break loop27;
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                throw eee;
                        }
                        cnt27++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:696:4: ALL_VARS
                    {
                    match(input,ALL_VARS,FOLLOW_ALL_VARS_in_describeTargets1363); 
                     q.setQueryResultStar( true ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "describeTargets"


    // $ANTLR start "askQuery"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:703:1: askQuery[Query q] : ^( ASK ( datasets[$q] )? whereClause[$q] ) ;
    public final void askQuery(Query q) throws RecognitionException {

        		q.setQueryAskType();
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:707:2: ( ^( ASK ( datasets[$q] )? whereClause[$q] ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:707:4: ^( ASK ( datasets[$q] )? whereClause[$q] )
            {
            match(input,ASK,FOLLOW_ASK_in_askQuery1388); 

            match(input, Token.DOWN, null); 
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:707:10: ( datasets[$q] )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==DATASETS) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:707:10: datasets[$q]
                    {
                    pushFollow(FOLLOW_datasets_in_askQuery1390);
                    datasets(q);

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_whereClause_in_askQuery1394);
            whereClause(q);

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "askQuery"


    // $ANTLR start "datasets"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:713:1: datasets[Query q] : ^( DATASETS ( datasetClause[$q] )+ ) ;
    public final void datasets(Query q) throws RecognitionException {
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:714:2: ( ^( DATASETS ( datasetClause[$q] )+ ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:714:4: ^( DATASETS ( datasetClause[$q] )+ )
            {
            match(input,DATASETS,FOLLOW_DATASETS_in_datasets1411); 

            match(input, Token.DOWN, null); 
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:714:15: ( datasetClause[$q] )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==DEFAULT_GRAPH||LA30_0==NAMED_GRAPH) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:714:15: datasetClause[$q]
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_datasets1413);
            	    datasetClause(q);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "datasets"


    // $ANTLR start "datasetClause"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:720:1: datasetClause[Query q] : ( defaultGraphClause[$q] | namedGraphClause[$q] );
    public final void datasetClause(Query q) throws RecognitionException {
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:721:2: ( defaultGraphClause[$q] | namedGraphClause[$q] )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==DEFAULT_GRAPH) ) {
                alt31=1;
            }
            else if ( (LA31_0==NAMED_GRAPH) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:721:4: defaultGraphClause[$q]
                    {
                    pushFollow(FOLLOW_defaultGraphClause_in_datasetClause1430);
                    defaultGraphClause(q);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:722:4: namedGraphClause[$q]
                    {
                    pushFollow(FOLLOW_namedGraphClause_in_datasetClause1436);
                    namedGraphClause(q);

                    state._fsp--;


                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "datasetClause"


    // $ANTLR start "defaultGraphClause"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:728:1: defaultGraphClause[Query q] : ^( DEFAULT_GRAPH s= sourceSelector ) ;
    public final void defaultGraphClause(Query q) throws RecognitionException {
        Node s = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:729:2: ( ^( DEFAULT_GRAPH s= sourceSelector ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:729:4: ^( DEFAULT_GRAPH s= sourceSelector )
            {
            match(input,DEFAULT_GRAPH,FOLLOW_DEFAULT_GRAPH_in_defaultGraphClause1452); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_sourceSelector_in_defaultGraphClause1456);
            s=sourceSelector();

            state._fsp--;


            match(input, Token.UP, null); 
             q.addGraphURI( s.getURI() ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "defaultGraphClause"


    // $ANTLR start "namedGraphClause"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:736:1: namedGraphClause[Query q] : ^( NAMED_GRAPH s= sourceSelector ) ;
    public final void namedGraphClause(Query q) throws RecognitionException {
        Node s = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:737:2: ( ^( NAMED_GRAPH s= sourceSelector ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:737:4: ^( NAMED_GRAPH s= sourceSelector )
            {
            match(input,NAMED_GRAPH,FOLLOW_NAMED_GRAPH_in_namedGraphClause1477); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_sourceSelector_in_namedGraphClause1481);
            s=sourceSelector();

            state._fsp--;


            match(input, Token.UP, null); 
             q.addNamedGraphURI( s.getURI() ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "namedGraphClause"


    // $ANTLR start "sourceSelector"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:744:1: sourceSelector returns [Node s] : iriRef ;
    public final Node sourceSelector() throws RecognitionException {
        Node s = null;

        Node iriRef48 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:746:2: ( iriRef )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:746:4: iriRef
            {
            pushFollow(FOLLOW_iriRef_in_sourceSelector1505);
            iriRef48=iriRef();

            state._fsp--;

             s = iriRef48; 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return s;
    }
    // $ANTLR end "sourceSelector"


    // $ANTLR start "whereClause"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:753:1: whereClause[Query q] : ^( WHERE_CLAUSE groupGraphPattern ) ;
    public final void whereClause(Query q) throws RecognitionException {
        ElementGroup groupGraphPattern49 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:754:2: ( ^( WHERE_CLAUSE groupGraphPattern ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:754:4: ^( WHERE_CLAUSE groupGraphPattern )
            {
            match(input,WHERE_CLAUSE,FOLLOW_WHERE_CLAUSE_in_whereClause1524); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_groupGraphPattern_in_whereClause1526);
            groupGraphPattern49=groupGraphPattern();

            state._fsp--;


            match(input, Token.UP, null); 
             q.setQueryPattern( groupGraphPattern49 ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "whereClause"


    // $ANTLR start "solutionModifier"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:762:1: solutionModifier[Query q] : ( orderClause[q] )? ( limitOffsetClauses[q] )? ;
    public final void solutionModifier(Query q) throws RecognitionException {
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:763:2: ( ( orderClause[q] )? ( limitOffsetClauses[q] )? )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:763:4: ( orderClause[q] )? ( limitOffsetClauses[q] )?
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:763:4: ( orderClause[q] )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==ORDER_CLAUSE) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:763:4: orderClause[q]
                    {
                    pushFollow(FOLLOW_orderClause_in_solutionModifier1546);
                    orderClause(q);

                    state._fsp--;


                    }
                    break;

            }

            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:763:20: ( limitOffsetClauses[q] )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==LIMIT_CLAUSE||LA33_0==OFFSET_CLAUSE) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:763:20: limitOffsetClauses[q]
                    {
                    pushFollow(FOLLOW_limitOffsetClauses_in_solutionModifier1550);
                    limitOffsetClauses(q);

                    state._fsp--;


                    }
                    break;

            }


            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "solutionModifier"


    // $ANTLR start "limitOffsetClauses"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:769:1: limitOffsetClauses[Query q] : ( limitClause ( offsetClause )? | offsetClause ( limitClause )? );
    public final void limitOffsetClauses(Query q) throws RecognitionException {
        long limitClause50 = 0;

        long offsetClause51 = 0;

        long offsetClause52 = 0;

        long limitClause53 = 0;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:770:2: ( limitClause ( offsetClause )? | offsetClause ( limitClause )? )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==LIMIT_CLAUSE) ) {
                alt36=1;
            }
            else if ( (LA36_0==OFFSET_CLAUSE) ) {
                alt36=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:770:4: limitClause ( offsetClause )?
                    {
                    pushFollow(FOLLOW_limitClause_in_limitOffsetClauses1566);
                    limitClause50=limitClause();

                    state._fsp--;

                     q.setLimit( limitClause50 ); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:771:3: ( offsetClause )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==OFFSET_CLAUSE) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:771:4: offsetClause
                            {
                            pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses1573);
                            offsetClause51=offsetClause();

                            state._fsp--;

                            q.setOffset( offsetClause51 ); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:772:4: offsetClause ( limitClause )?
                    {
                    pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses1583);
                    offsetClause52=offsetClause();

                    state._fsp--;

                     q.setOffset( offsetClause52 ); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:773:3: ( limitClause )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==LIMIT_CLAUSE) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:773:4: limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_limitOffsetClauses1590);
                            limitClause53=limitClause();

                            state._fsp--;

                             q.setLimit( limitClause53 ); 

                            }
                            break;

                    }


                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "limitOffsetClauses"


    // $ANTLR start "orderClause"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:779:1: orderClause[Query q] : ^( ORDER_CLAUSE ( orderCondition[q] )+ ) ;
    public final void orderClause(Query q) throws RecognitionException {
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:780:2: ( ^( ORDER_CLAUSE ( orderCondition[q] )+ ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:780:4: ^( ORDER_CLAUSE ( orderCondition[q] )+ )
            {
            match(input,ORDER_CLAUSE,FOLLOW_ORDER_CLAUSE_in_orderClause1610); 

            match(input, Token.DOWN, null); 
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:780:19: ( orderCondition[q] )+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=ORDER_CONDITION_ASC && LA37_0<=ORDER_CONDITION_UNDEF)) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:780:19: orderCondition[q]
            	    {
            	    pushFollow(FOLLOW_orderCondition_in_orderClause1612);
            	    orderCondition(q);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt37 >= 1 ) break loop37;
                        EarlyExitException eee =
                            new EarlyExitException(37, input);
                        throw eee;
                }
                cnt37++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "orderClause"


    // $ANTLR start "orderCondition"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:786:1: orderCondition[Query q] : ( ^( ORDER_CONDITION_ASC expression ) | ^( ORDER_CONDITION_DESC expression ) | ^( ORDER_CONDITION_UNDEF expression ) );
    public final void orderCondition(Query q) throws RecognitionException {
        Expr expression54 = null;

        Expr expression55 = null;

        Expr expression56 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:787:2: ( ^( ORDER_CONDITION_ASC expression ) | ^( ORDER_CONDITION_DESC expression ) | ^( ORDER_CONDITION_UNDEF expression ) )
            int alt38=3;
            switch ( input.LA(1) ) {
            case ORDER_CONDITION_ASC:
                {
                alt38=1;
                }
                break;
            case ORDER_CONDITION_DESC:
                {
                alt38=2;
                }
                break;
            case ORDER_CONDITION_UNDEF:
                {
                alt38=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:787:4: ^( ORDER_CONDITION_ASC expression )
                    {
                    match(input,ORDER_CONDITION_ASC,FOLLOW_ORDER_CONDITION_ASC_in_orderCondition1630); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_orderCondition1632);
                    expression54=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     q.addOrderBy( expression54, Query.ORDER_ASCENDING ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:789:4: ^( ORDER_CONDITION_DESC expression )
                    {
                    match(input,ORDER_CONDITION_DESC,FOLLOW_ORDER_CONDITION_DESC_in_orderCondition1643); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_orderCondition1645);
                    expression55=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     q.addOrderBy( expression55, Query.ORDER_DESCENDING ); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:791:4: ^( ORDER_CONDITION_UNDEF expression )
                    {
                    match(input,ORDER_CONDITION_UNDEF,FOLLOW_ORDER_CONDITION_UNDEF_in_orderCondition1656); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_orderCondition1658);
                    expression56=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     q.addOrderBy( expression56, Query.ORDER_DEFAULT ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "orderCondition"


    // $ANTLR start "limitClause"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:798:1: limitClause returns [long l] : ^( LIMIT_CLAUSE i= INTEGER ) ;
    public final long limitClause() throws RecognitionException {
        long l = 0;

        CommonTree i=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:800:2: ( ^( LIMIT_CLAUSE i= INTEGER ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:800:4: ^( LIMIT_CLAUSE i= INTEGER )
            {
            match(input,LIMIT_CLAUSE,FOLLOW_LIMIT_CLAUSE_in_limitClause1682); 

            match(input, Token.DOWN, null); 
            i=(CommonTree)match(input,INTEGER,FOLLOW_INTEGER_in_limitClause1686); 

            match(input, Token.UP, null); 
             l = Long.parseLong( (i!=null?i.getText():null) ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return l;
    }
    // $ANTLR end "limitClause"


    // $ANTLR start "offsetClause"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:807:1: offsetClause returns [long l] : ^( OFFSET_CLAUSE i= INTEGER ) ;
    public final long offsetClause() throws RecognitionException {
        long l = 0;

        CommonTree i=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:809:2: ( ^( OFFSET_CLAUSE i= INTEGER ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:809:4: ^( OFFSET_CLAUSE i= INTEGER )
            {
            match(input,OFFSET_CLAUSE,FOLLOW_OFFSET_CLAUSE_in_offsetClause1710); 

            match(input, Token.DOWN, null); 
            i=(CommonTree)match(input,INTEGER,FOLLOW_INTEGER_in_offsetClause1714); 

            match(input, Token.UP, null); 
             l = Long.parseLong( (i!=null?i.getText():null) ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return l;
    }
    // $ANTLR end "offsetClause"


    // $ANTLR start "groupGraphPattern"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:816:1: groupGraphPattern returns [ElementGroup e] : ^( GROUP_GRAPH_PATTERN (tb1= triplesBlock )? ( ( graphPatternNotTriples | filter ) (tb2= triplesBlock )? )* ) ;
    public final ElementGroup groupGraphPattern() throws RecognitionException {
        ElementGroup e = null;

        ElementTriplesBlock tb1 = null;

        ElementTriplesBlock tb2 = null;

        Element graphPatternNotTriples57 = null;

        ElementFilter filter58 = null;



        		e = new ElementGroup();
        		labelToNDV.clear();
        		labelToBNode.clear();
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:823:2: ( ^( GROUP_GRAPH_PATTERN (tb1= triplesBlock )? ( ( graphPatternNotTriples | filter ) (tb2= triplesBlock )? )* ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:823:4: ^( GROUP_GRAPH_PATTERN (tb1= triplesBlock )? ( ( graphPatternNotTriples | filter ) (tb2= triplesBlock )? )* )
            {
            match(input,GROUP_GRAPH_PATTERN,FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern1744); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:824:4: (tb1= triplesBlock )?
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==SUBJECT_TRIPLE_GROUP) ) {
                    alt39=1;
                }
                switch (alt39) {
                    case 1 :
                        // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:824:6: tb1= triplesBlock
                        {
                        pushFollow(FOLLOW_triplesBlock_in_groupGraphPattern1753);
                        tb1=triplesBlock();

                        state._fsp--;

                         e.addElement( tb1 ); 

                        }
                        break;

                }

                // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:825:4: ( ( graphPatternNotTriples | filter ) (tb2= triplesBlock )? )*
                loop42:
                do {
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==FILTER||LA42_0==GRAPH_GRAPH_PATTERN||LA42_0==GROUP_GRAPH_PATTERN||LA42_0==OPTIONAL_GRAPH_PATTERN||LA42_0==UNION_GRAPH_PATTERN) ) {
                        alt42=1;
                    }


                    switch (alt42) {
                	case 1 :
                	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:826:5: ( graphPatternNotTriples | filter ) (tb2= triplesBlock )?
                	    {
                	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:826:5: ( graphPatternNotTriples | filter )
                	    int alt40=2;
                	    int LA40_0 = input.LA(1);

                	    if ( (LA40_0==GRAPH_GRAPH_PATTERN||LA40_0==GROUP_GRAPH_PATTERN||LA40_0==OPTIONAL_GRAPH_PATTERN||LA40_0==UNION_GRAPH_PATTERN) ) {
                	        alt40=1;
                	    }
                	    else if ( (LA40_0==FILTER) ) {
                	        alt40=2;
                	    }
                	    else {
                	        NoViableAltException nvae =
                	            new NoViableAltException("", 40, 0, input);

                	        throw nvae;
                	    }
                	    switch (alt40) {
                	        case 1 :
                	            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:826:7: graphPatternNotTriples
                	            {
                	            pushFollow(FOLLOW_graphPatternNotTriples_in_groupGraphPattern1771);
                	            graphPatternNotTriples57=graphPatternNotTriples();

                	            state._fsp--;

                	             e.addElement( graphPatternNotTriples57 ); 

                	            }
                	            break;
                	        case 2 :
                	            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:827:7: filter
                	            {
                	            pushFollow(FOLLOW_filter_in_groupGraphPattern1781);
                	            filter58=filter();

                	            state._fsp--;

                	             e.addElementFilter( filter58 ); 

                	            }
                	            break;

                	    }

                	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:829:5: (tb2= triplesBlock )?
                	    int alt41=2;
                	    int LA41_0 = input.LA(1);

                	    if ( (LA41_0==SUBJECT_TRIPLE_GROUP) ) {
                	        alt41=1;
                	    }
                	    switch (alt41) {
                	        case 1 :
                	            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:829:7: tb2= triplesBlock
                	            {
                	            pushFollow(FOLLOW_triplesBlock_in_groupGraphPattern1799);
                	            tb2=triplesBlock();

                	            state._fsp--;

                	             e.addElement( tb2 ); 

                	            }
                	            break;

                	    }


                	    }
                	    break;

                	default :
                	    break loop42;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "groupGraphPattern"


    // $ANTLR start "triplesBlock"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:837:1: triplesBlock returns [ElementTriplesBlock e] : ( triplesSameSubject[$e] )+ ;
    public final ElementTriplesBlock triplesBlock() throws RecognitionException {
        ElementTriplesBlock e = null;


        		e = new ElementTriplesBlock();
         
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:843:2: ( ( triplesSameSubject[$e] )+ )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:843:4: ( triplesSameSubject[$e] )+
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:843:4: ( triplesSameSubject[$e] )+
            int cnt43=0;
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==SUBJECT_TRIPLE_GROUP) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:843:4: triplesSameSubject[$e]
            	    {
            	    pushFollow(FOLLOW_triplesSameSubject_in_triplesBlock1838);
            	    triplesSameSubject(e);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt43 >= 1 ) break loop43;
                        EarlyExitException eee =
                            new EarlyExitException(43, input);
                        throw eee;
                }
                cnt43++;
            } while (true);


            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "triplesBlock"


    // $ANTLR start "graphPatternNotTriples"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:849:1: graphPatternNotTriples returns [Element e] : ( optionalGraphPattern | groupOrUnionGraphPattern | graphGraphPattern );
    public final Element graphPatternNotTriples() throws RecognitionException {
        Element e = null;

        ElementOptional optionalGraphPattern59 = null;

        Element groupOrUnionGraphPattern60 = null;

        ElementNamedGraph graphGraphPattern61 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:851:2: ( optionalGraphPattern | groupOrUnionGraphPattern | graphGraphPattern )
            int alt44=3;
            switch ( input.LA(1) ) {
            case OPTIONAL_GRAPH_PATTERN:
                {
                alt44=1;
                }
                break;
            case GROUP_GRAPH_PATTERN:
            case UNION_GRAPH_PATTERN:
                {
                alt44=2;
                }
                break;
            case GRAPH_GRAPH_PATTERN:
                {
                alt44=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }

            switch (alt44) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:851:4: optionalGraphPattern
                    {
                    pushFollow(FOLLOW_optionalGraphPattern_in_graphPatternNotTriples1858);
                    optionalGraphPattern59=optionalGraphPattern();

                    state._fsp--;

                     e = optionalGraphPattern59; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:852:4: groupOrUnionGraphPattern
                    {
                    pushFollow(FOLLOW_groupOrUnionGraphPattern_in_graphPatternNotTriples1865);
                    groupOrUnionGraphPattern60=groupOrUnionGraphPattern();

                    state._fsp--;

                     e = groupOrUnionGraphPattern60; 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:853:4: graphGraphPattern
                    {
                    pushFollow(FOLLOW_graphGraphPattern_in_graphPatternNotTriples1872);
                    graphGraphPattern61=graphGraphPattern();

                    state._fsp--;

                     e = graphGraphPattern61; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "graphPatternNotTriples"


    // $ANTLR start "optionalGraphPattern"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:859:1: optionalGraphPattern returns [ElementOptional e] : ^( OPTIONAL_GRAPH_PATTERN groupGraphPattern ) ;
    public final ElementOptional optionalGraphPattern() throws RecognitionException {
        ElementOptional e = null;

        ElementGroup groupGraphPattern62 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:861:2: ( ^( OPTIONAL_GRAPH_PATTERN groupGraphPattern ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:861:4: ^( OPTIONAL_GRAPH_PATTERN groupGraphPattern )
            {
            match(input,OPTIONAL_GRAPH_PATTERN,FOLLOW_OPTIONAL_GRAPH_PATTERN_in_optionalGraphPattern1893); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_groupGraphPattern_in_optionalGraphPattern1895);
            groupGraphPattern62=groupGraphPattern();

            state._fsp--;


            match(input, Token.UP, null); 
             e = new ElementOptional( groupGraphPattern62 );

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "optionalGraphPattern"


    // $ANTLR start "graphGraphPattern"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:868:1: graphGraphPattern returns [ElementNamedGraph e] : ^( GRAPH_GRAPH_PATTERN ^( GRAPH_IDENTIFIER varOrIRIref ) groupGraphPattern ) ;
    public final ElementNamedGraph graphGraphPattern() throws RecognitionException {
        ElementNamedGraph e = null;

        Node varOrIRIref63 = null;

        ElementGroup groupGraphPattern64 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:870:2: ( ^( GRAPH_GRAPH_PATTERN ^( GRAPH_IDENTIFIER varOrIRIref ) groupGraphPattern ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:870:4: ^( GRAPH_GRAPH_PATTERN ^( GRAPH_IDENTIFIER varOrIRIref ) groupGraphPattern )
            {
            match(input,GRAPH_GRAPH_PATTERN,FOLLOW_GRAPH_GRAPH_PATTERN_in_graphGraphPattern1919); 

            match(input, Token.DOWN, null); 
            match(input,GRAPH_IDENTIFIER,FOLLOW_GRAPH_IDENTIFIER_in_graphGraphPattern1922); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_varOrIRIref_in_graphGraphPattern1924);
            varOrIRIref63=varOrIRIref();

            state._fsp--;


            match(input, Token.UP, null); 
            pushFollow(FOLLOW_groupGraphPattern_in_graphGraphPattern1927);
            groupGraphPattern64=groupGraphPattern();

            state._fsp--;


            match(input, Token.UP, null); 
             e = new ElementNamedGraph( varOrIRIref63, groupGraphPattern64 ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "graphGraphPattern"


    // $ANTLR start "groupOrUnionGraphPattern"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:877:1: groupOrUnionGraphPattern returns [Element e] : ( groupGraphPattern | ^( UNION_GRAPH_PATTERN a= groupOrUnionGraphPattern b= groupGraphPattern ) );
    public final Element groupOrUnionGraphPattern() throws RecognitionException {
        Element e = null;

        Element a = null;

        ElementGroup b = null;

        ElementGroup groupGraphPattern65 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:879:2: ( groupGraphPattern | ^( UNION_GRAPH_PATTERN a= groupOrUnionGraphPattern b= groupGraphPattern ) )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==GROUP_GRAPH_PATTERN) ) {
                alt45=1;
            }
            else if ( (LA45_0==UNION_GRAPH_PATTERN) ) {
                alt45=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:879:4: groupGraphPattern
                    {
                    pushFollow(FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern1950);
                    groupGraphPattern65=groupGraphPattern();

                    state._fsp--;

                     e = groupGraphPattern65; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:880:4: ^( UNION_GRAPH_PATTERN a= groupOrUnionGraphPattern b= groupGraphPattern )
                    {
                    match(input,UNION_GRAPH_PATTERN,FOLLOW_UNION_GRAPH_PATTERN_in_groupOrUnionGraphPattern1958); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_groupOrUnionGraphPattern_in_groupOrUnionGraphPattern1962);
                    a=groupOrUnionGraphPattern();

                    state._fsp--;

                    pushFollow(FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern1966);
                    b=groupGraphPattern();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			final ElementUnion u = new ElementUnion();
                    			u.addElement( a );
                    			u.addElement( b );
                    			e = u;
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "groupOrUnionGraphPattern"


    // $ANTLR start "filter"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:892:1: filter returns [ElementFilter e] : ^( FILTER constraint ) ;
    public final ElementFilter filter() throws RecognitionException {
        ElementFilter e = null;

        Expr constraint66 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:894:2: ( ^( FILTER constraint ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:894:4: ^( FILTER constraint )
            {
            match(input,FILTER,FOLLOW_FILTER_in_filter1990); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_constraint_in_filter1992);
            constraint66=constraint();

            state._fsp--;


            match(input, Token.UP, null); 
             e = new ElementFilter( constraint66 ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "filter"


    // $ANTLR start "constraint"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:901:1: constraint returns [Expr e] : expression ;
    public final Expr constraint() throws RecognitionException {
        Expr e = null;

        Expr expression67 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:903:2: ( expression )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:903:4: expression
            {
            pushFollow(FOLLOW_expression_in_constraint2015);
            expression67=expression();

            state._fsp--;

             e = expression67; 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "constraint"


    // $ANTLR start "functionCall"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:909:1: functionCall returns [Expr e] : ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) ;
    public final Expr functionCall() throws RecognitionException {
        Expr e = null;

        Node iriRef68 = null;

        ExprList argList69 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:911:2: ( ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:911:4: ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) )
            {
            match(input,FUNCTION_CALL,FOLLOW_FUNCTION_CALL_in_functionCall2036); 

            match(input, Token.DOWN, null); 
            match(input,FUNCTION_IDENTIFIER,FOLLOW_FUNCTION_IDENTIFIER_in_functionCall2039); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_iriRef_in_functionCall2041);
            iriRef68=iriRef();

            state._fsp--;


            match(input, Token.UP, null); 
            match(input,FUNCTION_ARGS,FOLLOW_FUNCTION_ARGS_in_functionCall2045); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                pushFollow(FOLLOW_argList_in_functionCall2047);
                argList69=argList();

                state._fsp--;


                match(input, Token.UP, null); 
            }

            match(input, Token.UP, null); 
             e = new E_Function( iriRef68.getURI(), argList69 ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "functionCall"


    // $ANTLR start "argList"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:918:1: argList returns [ExprList l] : ( expression )* ;
    public final ExprList argList() throws RecognitionException {
        ExprList l = null;

        Expr expression70 = null;


         l = new ExprList(); 
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:921:2: ( ( expression )* )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:921:4: ( expression )*
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:921:4: ( expression )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( ((LA46_0>=BUILTIN_BOUND && LA46_0<=BUILTIN_STR)||(LA46_0>=CONDITIONAL_EXPRESSION_AND && LA46_0<=CONDITIONAL_EXPRESSION_OR)||LA46_0==FUNCTION_CALL||LA46_0==IRI_REF||(LA46_0>=LITERAL_BOOLEAN_FALSE && LA46_0<=LITERAL_TYPED)||(LA46_0>=NUMERIC_EXPRESSION_ADD && LA46_0<=NUMERIC_EXPRESSION_SUBTRACT)||LA46_0==PREFIXED_NAME||(LA46_0>=RELATIONAL_EQUAL && LA46_0<=RELATIONAL_NOT_EQUAL)||(LA46_0>=UNARY_EXPRESSION_NEGATIVE && LA46_0<=UNARY_EXPRESSION_POSITIVE)||LA46_0==VARIABLE) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:921:6: expression
            	    {
            	    pushFollow(FOLLOW_expression_in_argList2079);
            	    expression70=expression();

            	    state._fsp--;

            	     l.add( expression70 ); 

            	    }
            	    break;

            	default :
            	    break loop46;
                }
            } while (true);


            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return l;
    }
    // $ANTLR end "argList"


    // $ANTLR start "constructTemplate"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:927:1: constructTemplate returns [Template t] : ^( CONSTRUCT_TEMPLATE ( constructTriples[tg] )? ) ;
    public final Template constructTemplate() throws RecognitionException {
        Template t = null;


        		TripleCollectorBGP tg = new TripleCollectorBGP();
        		this.inConstructTemplate = true;
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:933:2: ( ^( CONSTRUCT_TEMPLATE ( constructTriples[tg] )? ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:933:4: ^( CONSTRUCT_TEMPLATE ( constructTriples[tg] )? )
            {
            match(input,CONSTRUCT_TEMPLATE,FOLLOW_CONSTRUCT_TEMPLATE_in_constructTemplate2109); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:933:25: ( constructTriples[tg] )?
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==SUBJECT_TRIPLE_GROUP) ) {
                    alt47=1;
                }
                switch (alt47) {
                    case 1 :
                        // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:933:25: constructTriples[tg]
                        {
                        pushFollow(FOLLOW_constructTriples_in_constructTemplate2111);
                        constructTriples(tg);

                        state._fsp--;


                        }
                        break;

                }


                match(input, Token.UP, null); 
            }

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
             
            		this.inConstructTemplate = false;		
            		t = new Template(tg.getBGP()); 
            	
        }
        return t;
    }
    // $ANTLR end "constructTemplate"


    // $ANTLR start "constructTriples"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:943:1: constructTriples[TripleCollector e] : ( triplesSameSubject[$e] )+ ;
    public final void constructTriples(TripleCollector e) throws RecognitionException {
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:944:2: ( ( triplesSameSubject[$e] )+ )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:944:4: ( triplesSameSubject[$e] )+
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:944:4: ( triplesSameSubject[$e] )+
            int cnt48=0;
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==SUBJECT_TRIPLE_GROUP) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:944:4: triplesSameSubject[$e]
            	    {
            	    pushFollow(FOLLOW_triplesSameSubject_in_constructTriples2133);
            	    triplesSameSubject(e);

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt48 >= 1 ) break loop48;
                        EarlyExitException eee =
                            new EarlyExitException(48, input);
                        throw eee;
                }
                cnt48++;
            } while (true);


            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "constructTriples"


    // $ANTLR start "triplesSameSubject"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:950:1: triplesSameSubject[TripleCollector e] : ^( SUBJECT_TRIPLE_GROUP ( ( ^( SUBJECT ( varOrTerm | disjunction ) ) m= propertyListNotEmpty ) | ( ^( SUBJECT triplesNode ) (m= propertyListNotEmpty )? ) ) ) ;
    public final void triplesSameSubject(TripleCollector e) throws RecognitionException {
        SparqlOwlTreeARQ.propertyListNotEmpty_return m = null;

        Node varOrTerm71 = null;

        SparqlOwlTreeARQ.disjunction_return disjunction72 = null;

        SparqlOwlTreeARQ.triplesNode_return triplesNode73 = null;



        		Node s = null;
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:954:2: ( ^( SUBJECT_TRIPLE_GROUP ( ( ^( SUBJECT ( varOrTerm | disjunction ) ) m= propertyListNotEmpty ) | ( ^( SUBJECT triplesNode ) (m= propertyListNotEmpty )? ) ) ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:954:4: ^( SUBJECT_TRIPLE_GROUP ( ( ^( SUBJECT ( varOrTerm | disjunction ) ) m= propertyListNotEmpty ) | ( ^( SUBJECT triplesNode ) (m= propertyListNotEmpty )? ) ) )
            {
            match(input,SUBJECT_TRIPLE_GROUP,FOLLOW_SUBJECT_TRIPLE_GROUP_in_triplesSameSubject2156); 

            match(input, Token.DOWN, null); 
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:955:4: ( ( ^( SUBJECT ( varOrTerm | disjunction ) ) m= propertyListNotEmpty ) | ( ^( SUBJECT triplesNode ) (m= propertyListNotEmpty )? ) )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==SUBJECT) ) {
                int LA51_1 = input.LA(2);

                if ( (LA51_1==DOWN) ) {
                    switch ( input.LA(3) ) {
                    case ALL_RESTRICTION:
                    case BLANK_NODE:
                    case CLASS_OR_DATATYPE:
                    case CONJUNCTION:
                    case DATATYPE:
                    case DATATYPE_RESTRICTION:
                    case DISJUNCTION:
                    case EXACT_NUMBER_RESTRICTION:
                    case INDIVIDUAL_ENUMERATION:
                    case IRI_REF:
                    case LITERAL_BOOLEAN_FALSE:
                    case LITERAL_BOOLEAN_TRUE:
                    case LITERAL_DECIMAL:
                    case LITERAL_DOUBLE:
                    case LITERAL_INTEGER:
                    case LITERAL_LANG:
                    case LITERAL_PLAIN:
                    case LITERAL_TYPED:
                    case MAX_NUMBER_RESTRICTION:
                    case MIN_NUMBER_RESTRICTION:
                    case NEGATION:
                    case PREFIXED_NAME:
                    case SELF_RESTRICTION:
                    case SOME_RESTRICTION:
                    case VALUE_ENUMERATION:
                    case VALUE_RESTRICTION:
                    case VARIABLE:
                        {
                        alt51=1;
                        }
                        break;
                    case COLLECTION:
                        {
                        int LA51_4 = input.LA(4);

                        if ( (LA51_4==DOWN) ) {
                            alt51=2;
                        }
                        else if ( (LA51_4==UP) ) {
                            alt51=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 51, 4, input);

                            throw nvae;
                        }
                        }
                        break;
                    case BNODE_PROPERTY_LIST:
                        {
                        alt51=2;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 51, 2, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:955:6: ( ^( SUBJECT ( varOrTerm | disjunction ) ) m= propertyListNotEmpty )
                    {
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:955:6: ( ^( SUBJECT ( varOrTerm | disjunction ) ) m= propertyListNotEmpty )
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:955:8: ^( SUBJECT ( varOrTerm | disjunction ) ) m= propertyListNotEmpty
                    {
                    match(input,SUBJECT,FOLLOW_SUBJECT_in_triplesSameSubject2166); 

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:956:7: ( varOrTerm | disjunction )
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==BLANK_NODE||LA49_0==COLLECTION||LA49_0==IRI_REF||(LA49_0>=LITERAL_BOOLEAN_FALSE && LA49_0<=LITERAL_TYPED)||LA49_0==PREFIXED_NAME||LA49_0==VARIABLE) ) {
                        alt49=1;
                    }
                    else if ( (LA49_0==ALL_RESTRICTION||LA49_0==CLASS_OR_DATATYPE||LA49_0==CONJUNCTION||(LA49_0>=DATATYPE && LA49_0<=DATATYPE_RESTRICTION)||(LA49_0>=DISJUNCTION && LA49_0<=EXACT_NUMBER_RESTRICTION)||LA49_0==INDIVIDUAL_ENUMERATION||(LA49_0>=MAX_NUMBER_RESTRICTION && LA49_0<=MIN_NUMBER_RESTRICTION)||LA49_0==NEGATION||(LA49_0>=SELF_RESTRICTION && LA49_0<=SOME_RESTRICTION)||(LA49_0>=VALUE_ENUMERATION && LA49_0<=VALUE_RESTRICTION)) ) {
                        alt49=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 49, 0, input);

                        throw nvae;
                    }
                    switch (alt49) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:956:9: varOrTerm
                            {
                            pushFollow(FOLLOW_varOrTerm_in_triplesSameSubject2176);
                            varOrTerm71=varOrTerm();

                            state._fsp--;

                             s = varOrTerm71; 

                            }
                            break;
                        case 2 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:958:9: disjunction
                            {
                            pushFollow(FOLLOW_disjunction_in_triplesSameSubject2195);
                            disjunction72=disjunction();

                            state._fsp--;


                            								s = (disjunction72!=null?disjunction72.n:null);
                            								for ( Triple t : (disjunction72!=null?disjunction72.triples:null) )
                            									e.addTriple( t );
                            							

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    pushFollow(FOLLOW_propertyListNotEmpty_in_triplesSameSubject2228);
                    m=propertyListNotEmpty();

                    state._fsp--;


                    }


                    					for ( Map.Entry<Node,List<Node>> pair : (m!=null?m.m:null).entrySet() ) {
                    						for ( Node o : pair.getValue() )
                    							e.addTriple( new Triple( s, pair.getKey(), o ) );
                    					}
                    					for ( Triple t : (m!=null?m.triples:null) )
                    						e.addTriple( t );
                    				

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:975:6: ( ^( SUBJECT triplesNode ) (m= propertyListNotEmpty )? )
                    {
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:975:6: ( ^( SUBJECT triplesNode ) (m= propertyListNotEmpty )? )
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:975:8: ^( SUBJECT triplesNode ) (m= propertyListNotEmpty )?
                    {
                    match(input,SUBJECT,FOLLOW_SUBJECT_in_triplesSameSubject2246); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_triplesNode_in_triplesSameSubject2248);
                    triplesNode73=triplesNode();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    						for ( Triple t : (triplesNode73!=null?triplesNode73.triples:null) )
                    							e.addTriple( t );
                    						s = (triplesNode73!=null?triplesNode73.n:null);
                    					
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:981:6: (m= propertyListNotEmpty )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==VERB_PAIR_GROUP) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:981:8: m= propertyListNotEmpty
                            {
                            pushFollow(FOLLOW_propertyListNotEmpty_in_triplesSameSubject2267);
                            m=propertyListNotEmpty();

                            state._fsp--;


                            							for ( Map.Entry<Node,List<Node>> pair : (m!=null?m.m:null).entrySet() ) {
                            								for ( Node o : pair.getValue() )
                            									e.addTriple( new Triple( s, pair.getKey(), o ) );
                            							}
                            							for ( Triple t : (m!=null?m.triples:null) )
                            								e.addTriple( t );
                            						

                            }
                            break;

                    }


                    }


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return ;
    }
    // $ANTLR end "triplesSameSubject"

    public static class propertyListNotEmpty_return extends TreeRuleReturnScope {
        public Map<Node,List<Node>> m;
        public Collection<Triple> triples;
    };

    // $ANTLR start "propertyListNotEmpty"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:999:1: propertyListNotEmpty returns [Map<Node,List<Node>> m, Collection<Triple> triples] : ( ^( VERB_PAIR_GROUP verb objectList ) )+ ;
    public final SparqlOwlTreeARQ.propertyListNotEmpty_return propertyListNotEmpty() throws RecognitionException {
        SparqlOwlTreeARQ.propertyListNotEmpty_return retval = new SparqlOwlTreeARQ.propertyListNotEmpty_return();
        retval.start = input.LT(1);

        Node verb74 = null;

        SparqlOwlTreeARQ.objectList_return objectList75 = null;



        		retval.m = new LinkedHashMap<Node,List<Node>>();
        		retval.triples = new ArrayList<Triple>();
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1005:2: ( ( ^( VERB_PAIR_GROUP verb objectList ) )+ )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1005:4: ( ^( VERB_PAIR_GROUP verb objectList ) )+
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1005:4: ( ^( VERB_PAIR_GROUP verb objectList ) )+
            int cnt52=0;
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==VERB_PAIR_GROUP) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1006:4: ^( VERB_PAIR_GROUP verb objectList )
            	    {
            	    match(input,VERB_PAIR_GROUP,FOLLOW_VERB_PAIR_GROUP_in_propertyListNotEmpty2328); 

            	    match(input, Token.DOWN, null); 
            	    pushFollow(FOLLOW_verb_in_propertyListNotEmpty2330);
            	    verb74=verb();

            	    state._fsp--;

            	    pushFollow(FOLLOW_objectList_in_propertyListNotEmpty2332);
            	    objectList75=objectList();

            	    state._fsp--;


            	    match(input, Token.UP, null); 

            	    				List<Node> l = retval.m.get( verb74 );
            	    				if ( l == null )
            	    					retval.m.put( verb74, (objectList75!=null?objectList75.l:null) );
            	    				else
            	    					l.addAll( (objectList75!=null?objectList75.l:null) );
            	    				retval.triples.addAll( (objectList75!=null?objectList75.triples:null) );
            	    			

            	    }
            	    break;

            	default :
            	    if ( cnt52 >= 1 ) break loop52;
                        EarlyExitException eee =
                            new EarlyExitException(52, input);
                        throw eee;
                }
                cnt52++;
            } while (true);


            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "propertyListNotEmpty"

    public static class objectList_return extends TreeRuleReturnScope {
        public List<Node> l;
        public Collection<Triple> triples;
    };

    // $ANTLR start "objectList"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1021:1: objectList returns [List<Node> l, Collection<Triple> triples] : ( object )+ ;
    public final SparqlOwlTreeARQ.objectList_return objectList() throws RecognitionException {
        SparqlOwlTreeARQ.objectList_return retval = new SparqlOwlTreeARQ.objectList_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.object_return object76 = null;



        		retval.l = new ArrayList<Node>();
        		retval.triples = new ArrayList<Triple>();
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1027:2: ( ( object )+ )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1027:4: ( object )+
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1027:4: ( object )+
            int cnt53=0;
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==OBJECT) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1027:6: object
            	    {
            	    pushFollow(FOLLOW_object_in_objectList2369);
            	    object76=object();

            	    state._fsp--;


            	    				retval.l.add( (object76!=null?object76.n:null) );
            	    				retval.triples.addAll( (object76!=null?object76.triples:null) );
            	    			

            	    }
            	    break;

            	default :
            	    if ( cnt53 >= 1 ) break loop53;
                        EarlyExitException eee =
                            new EarlyExitException(53, input);
                        throw eee;
                }
                cnt53++;
            } while (true);


            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "objectList"

    public static class object_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
    };

    // $ANTLR start "object"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1039:1: object returns [Node n, Collection<Triple> triples] : ( ^( OBJECT graphNode ) | ^( OBJECT disjunction ) );
    public final SparqlOwlTreeARQ.object_return object() throws RecognitionException {
        SparqlOwlTreeARQ.object_return retval = new SparqlOwlTreeARQ.object_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.graphNode_return graphNode77 = null;

        SparqlOwlTreeARQ.disjunction_return disjunction78 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1041:2: ( ^( OBJECT graphNode ) | ^( OBJECT disjunction ) )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==OBJECT) ) {
                int LA54_1 = input.LA(2);

                if ( (LA54_1==DOWN) ) {
                    int LA54_2 = input.LA(3);

                    if ( ((LA54_2>=BLANK_NODE && LA54_2<=BNODE_PROPERTY_LIST)||LA54_2==COLLECTION||LA54_2==IRI_REF||(LA54_2>=LITERAL_BOOLEAN_FALSE && LA54_2<=LITERAL_TYPED)||LA54_2==PREFIXED_NAME||LA54_2==VARIABLE) ) {
                        alt54=1;
                    }
                    else if ( (LA54_2==ALL_RESTRICTION||LA54_2==CLASS_OR_DATATYPE||LA54_2==CONJUNCTION||(LA54_2>=DATATYPE && LA54_2<=DATATYPE_RESTRICTION)||(LA54_2>=DISJUNCTION && LA54_2<=EXACT_NUMBER_RESTRICTION)||LA54_2==INDIVIDUAL_ENUMERATION||(LA54_2>=MAX_NUMBER_RESTRICTION && LA54_2<=MIN_NUMBER_RESTRICTION)||LA54_2==NEGATION||(LA54_2>=SELF_RESTRICTION && LA54_2<=SOME_RESTRICTION)||(LA54_2>=VALUE_ENUMERATION && LA54_2<=VALUE_RESTRICTION)) ) {
                        alt54=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 54, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 54, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1041:4: ^( OBJECT graphNode )
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_object2398); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_graphNode_in_object2400);
                    graphNode77=graphNode();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.n = (graphNode77!=null?graphNode77.n:null);
                    			retval.triples = (graphNode77!=null?graphNode77.triples:null);
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1046:4: ^( OBJECT disjunction )
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_object2411); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_disjunction_in_object2413);
                    disjunction78=disjunction();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			retval.n = (disjunction78!=null?disjunction78.n:null);
                    			retval.triples = (disjunction78!=null?disjunction78.triples:null);
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "object"


    // $ANTLR start "verb"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1057:1: verb returns [Node v] : ( ^( VERB varOrIRIref ) | ^( VERB RDF_TYPE ) );
    public final Node verb() throws RecognitionException {
        Node v = null;

        Node varOrIRIref79 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1059:2: ( ^( VERB varOrIRIref ) | ^( VERB RDF_TYPE ) )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==VERB) ) {
                int LA55_1 = input.LA(2);

                if ( (LA55_1==DOWN) ) {
                    int LA55_2 = input.LA(3);

                    if ( (LA55_2==RDF_TYPE) ) {
                        alt55=2;
                    }
                    else if ( (LA55_2==IRI_REF||LA55_2==PREFIXED_NAME||LA55_2==VARIABLE) ) {
                        alt55=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 55, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 55, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1059:4: ^( VERB varOrIRIref )
                    {
                    match(input,VERB,FOLLOW_VERB_in_verb2439); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_varOrIRIref_in_verb2441);
                    varOrIRIref79=varOrIRIref();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     v = varOrIRIref79; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1060:4: ^( VERB RDF_TYPE )
                    {
                    match(input,VERB,FOLLOW_VERB_in_verb2450); 

                    match(input, Token.DOWN, null); 
                    match(input,RDF_TYPE,FOLLOW_RDF_TYPE_in_verb2452); 

                    match(input, Token.UP, null); 
                     v = RDF.Nodes.type; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "verb"

    public static class triplesNode_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
    };

    // $ANTLR start "triplesNode"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1066:1: triplesNode returns [Node n, Collection<Triple> triples] : ( collection | blankNodePropertyList );
    public final SparqlOwlTreeARQ.triplesNode_return triplesNode() throws RecognitionException {
        SparqlOwlTreeARQ.triplesNode_return retval = new SparqlOwlTreeARQ.triplesNode_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.collection_return collection80 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1068:2: ( collection | blankNodePropertyList )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==COLLECTION) ) {
                alt56=1;
            }
            else if ( (LA56_0==BNODE_PROPERTY_LIST) ) {
                alt56=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1068:4: collection
                    {
                    pushFollow(FOLLOW_collection_in_triplesNode2473);
                    collection80=collection();

                    state._fsp--;


                    			retval.n = (collection80!=null?collection80.n:null);
                    			retval.triples = (collection80!=null?collection80.triples:null);
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1073:4: blankNodePropertyList
                    {
                    pushFollow(FOLLOW_blankNodePropertyList_in_triplesNode2482);
                    blankNodePropertyList();

                    state._fsp--;


                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "triplesNode"

    public static class blankNodePropertyList_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
    };

    // $ANTLR start "blankNodePropertyList"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1079:1: blankNodePropertyList returns [Node n, Collection<Triple> triples] : ^( BNODE_PROPERTY_LIST m= propertyListNotEmpty ) ;
    public final SparqlOwlTreeARQ.blankNodePropertyList_return blankNodePropertyList() throws RecognitionException {
        SparqlOwlTreeARQ.blankNodePropertyList_return retval = new SparqlOwlTreeARQ.blankNodePropertyList_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.propertyListNotEmpty_return m = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1081:2: ( ^( BNODE_PROPERTY_LIST m= propertyListNotEmpty ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1081:4: ^( BNODE_PROPERTY_LIST m= propertyListNotEmpty )
            {
            match(input,BNODE_PROPERTY_LIST,FOLLOW_BNODE_PROPERTY_LIST_in_blankNodePropertyList2501); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_propertyListNotEmpty_in_blankNodePropertyList2505);
            m=propertyListNotEmpty();

            state._fsp--;


            match(input, Token.UP, null); 

            			retval.n = getAnon( );
            			for ( Map.Entry<Node,List<Node>> pair : (m!=null?m.m:null).entrySet() ) {
            				for ( Node o : pair.getValue() )
            					retval.triples.add( new Triple( retval.n, pair.getKey(), o ) );
            			}
            			retval.triples.addAll( (m!=null?m.triples:null) );
            		

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "blankNodePropertyList"

    public static class collection_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
    };

    // $ANTLR start "collection"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1095:1: collection returns [Node n, Collection<Triple> triples] : ^( COLLECTION ( graphNode )+ ) ;
    public final SparqlOwlTreeARQ.collection_return collection() throws RecognitionException {
        SparqlOwlTreeARQ.collection_return retval = new SparqlOwlTreeARQ.collection_return();
        retval.start = input.LT(1);

        SparqlOwlTreeARQ.graphNode_return graphNode81 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1097:2: ( ^( COLLECTION ( graphNode )+ ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1097:4: ^( COLLECTION ( graphNode )+ )
            {
            match(input,COLLECTION,FOLLOW_COLLECTION_in_collection2529); 


            				retval.triples = new ArrayList<Triple>();
            				List<Node> ln = new ArrayList<Node>();
            			

            match(input, Token.DOWN, null); 
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1102:4: ( graphNode )+
            int cnt57=0;
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( ((LA57_0>=BLANK_NODE && LA57_0<=BNODE_PROPERTY_LIST)||LA57_0==COLLECTION||LA57_0==IRI_REF||(LA57_0>=LITERAL_BOOLEAN_FALSE && LA57_0<=LITERAL_TYPED)||LA57_0==PREFIXED_NAME||LA57_0==VARIABLE) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1102:6: graphNode
            	    {
            	    pushFollow(FOLLOW_graphNode_in_collection2541);
            	    graphNode81=graphNode();

            	    state._fsp--;


            	    					retval.triples.addAll( (graphNode81!=null?graphNode81.triples:null) );
            	    					ln.add( (graphNode81!=null?graphNode81.n:null) );
            	    				

            	    }
            	    break;

            	default :
            	    if ( cnt57 >= 1 ) break loop57;
                        EarlyExitException eee =
                            new EarlyExitException(57, input);
                        throw eee;
                }
                cnt57++;
            } while (true);


            				retval.n = listToTriples( ln, retval.triples );
            			

            match(input, Token.UP, null); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "collection"


    // $ANTLR start "emptyCollection"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1117:1: emptyCollection returns [Node n] : COLLECTION ;
    public final Node emptyCollection() throws RecognitionException {
        Node n = null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1119:2: ( COLLECTION )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1119:4: COLLECTION
            {
            match(input,COLLECTION,FOLLOW_COLLECTION_in_emptyCollection2580); 
             n = RDF.Nodes.nil; 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "emptyCollection"

    public static class graphNode_return extends TreeRuleReturnScope {
        public Node n;
        public Collection<Triple> triples;
    };

    // $ANTLR start "graphNode"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1125:1: graphNode returns [Node n, Collection<Triple> triples] : ( varOrTerm | triplesNode );
    public final SparqlOwlTreeARQ.graphNode_return graphNode() throws RecognitionException {
        SparqlOwlTreeARQ.graphNode_return retval = new SparqlOwlTreeARQ.graphNode_return();
        retval.start = input.LT(1);

        Node varOrTerm82 = null;

        SparqlOwlTreeARQ.triplesNode_return triplesNode83 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1127:2: ( varOrTerm | triplesNode )
            int alt58=2;
            switch ( input.LA(1) ) {
            case BLANK_NODE:
            case IRI_REF:
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
            case PREFIXED_NAME:
            case VARIABLE:
                {
                alt58=1;
                }
                break;
            case COLLECTION:
                {
                int LA58_2 = input.LA(2);

                if ( (LA58_2==DOWN) ) {
                    alt58=2;
                }
                else if ( (LA58_2==UP||(LA58_2>=BLANK_NODE && LA58_2<=BNODE_PROPERTY_LIST)||LA58_2==COLLECTION||LA58_2==IRI_REF||(LA58_2>=LITERAL_BOOLEAN_FALSE && LA58_2<=LITERAL_TYPED)||LA58_2==PREFIXED_NAME||LA58_2==VARIABLE) ) {
                    alt58=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 58, 2, input);

                    throw nvae;
                }
                }
                break;
            case BNODE_PROPERTY_LIST:
                {
                alt58=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 58, 0, input);

                throw nvae;
            }

            switch (alt58) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1127:4: varOrTerm
                    {
                    pushFollow(FOLLOW_varOrTerm_in_graphNode2600);
                    varOrTerm82=varOrTerm();

                    state._fsp--;


                    			retval.n = varOrTerm82;
                    			retval.triples = Collections.emptyList();
                    		

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1132:4: triplesNode
                    {
                    pushFollow(FOLLOW_triplesNode_in_graphNode2609);
                    triplesNode83=triplesNode();

                    state._fsp--;


                    			retval.n = (triplesNode83!=null?triplesNode83.n:null);
                    			retval.triples = (triplesNode83!=null?triplesNode83.triples:null);
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "graphNode"


    // $ANTLR start "varOrTerm"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1142:1: varOrTerm returns [Node n] : ( var | graphTerm );
    public final Node varOrTerm() throws RecognitionException {
        Node n = null;

        Node var84 = null;

        Node graphTerm85 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1144:2: ( var | graphTerm )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==VARIABLE) ) {
                alt59=1;
            }
            else if ( (LA59_0==BLANK_NODE||LA59_0==COLLECTION||LA59_0==IRI_REF||(LA59_0>=LITERAL_BOOLEAN_FALSE && LA59_0<=LITERAL_TYPED)||LA59_0==PREFIXED_NAME) ) {
                alt59=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1144:4: var
                    {
                    pushFollow(FOLLOW_var_in_varOrTerm2631);
                    var84=var();

                    state._fsp--;

                     n = var84; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1146:4: graphTerm
                    {
                    pushFollow(FOLLOW_graphTerm_in_varOrTerm2640);
                    graphTerm85=graphTerm();

                    state._fsp--;

                     n = graphTerm85; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "varOrTerm"


    // $ANTLR start "varOrIRIref"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1153:1: varOrIRIref returns [Node n] : ( var | iriRef );
    public final Node varOrIRIref() throws RecognitionException {
        Node n = null;

        Node var86 = null;

        Node iriRef87 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1155:2: ( var | iriRef )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==VARIABLE) ) {
                alt60=1;
            }
            else if ( (LA60_0==IRI_REF||LA60_0==PREFIXED_NAME) ) {
                alt60=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1155:4: var
                    {
                    pushFollow(FOLLOW_var_in_varOrIRIref2662);
                    var86=var();

                    state._fsp--;

                     n = var86; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1156:4: iriRef
                    {
                    pushFollow(FOLLOW_iriRef_in_varOrIRIref2669);
                    iriRef87=iriRef();

                    state._fsp--;

                     n = iriRef87; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "varOrIRIref"


    // $ANTLR start "var"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1162:1: var returns [Node v] : ^( VARIABLE (t= VAR1 | t= VAR2 ) ) ;
    public final Node var() throws RecognitionException {
        Node v = null;

        CommonTree t=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1164:2: ( ^( VARIABLE (t= VAR1 | t= VAR2 ) ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1164:4: ^( VARIABLE (t= VAR1 | t= VAR2 ) )
            {
            match(input,VARIABLE,FOLLOW_VARIABLE_in_var2690); 

            match(input, Token.DOWN, null); 
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1164:15: (t= VAR1 | t= VAR2 )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==VAR1) ) {
                alt61=1;
            }
            else if ( (LA61_0==VAR2) ) {
                alt61=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1164:16: t= VAR1
                    {
                    t=(CommonTree)match(input,VAR1,FOLLOW_VAR1_in_var2695); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1164:23: t= VAR2
                    {
                    t=(CommonTree)match(input,VAR2,FOLLOW_VAR2_in_var2699); 

                    }
                    break;

            }


            match(input, Token.UP, null); 
             v = Var.alloc( (t!=null?t.getText():null) ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return v;
    }
    // $ANTLR end "var"


    // $ANTLR start "graphTerm"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1171:1: graphTerm returns [Node n] : ( iriRef | literal | blankNode | emptyCollection );
    public final Node graphTerm() throws RecognitionException {
        Node n = null;

        Node iriRef88 = null;

        Node literal89 = null;

        Node blankNode90 = null;

        Node emptyCollection91 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1173:2: ( iriRef | literal | blankNode | emptyCollection )
            int alt62=4;
            switch ( input.LA(1) ) {
            case IRI_REF:
            case PREFIXED_NAME:
                {
                alt62=1;
                }
                break;
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
                {
                alt62=2;
                }
                break;
            case BLANK_NODE:
                {
                alt62=3;
                }
                break;
            case COLLECTION:
                {
                alt62=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }

            switch (alt62) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1173:4: iriRef
                    {
                    pushFollow(FOLLOW_iriRef_in_graphTerm2723);
                    iriRef88=iriRef();

                    state._fsp--;

                     n = iriRef88; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1175:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_graphTerm2732);
                    literal89=literal();

                    state._fsp--;

                     n = literal89; 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1177:4: blankNode
                    {
                    pushFollow(FOLLOW_blankNode_in_graphTerm2741);
                    blankNode90=blankNode();

                    state._fsp--;

                     n = blankNode90; 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1179:4: emptyCollection
                    {
                    pushFollow(FOLLOW_emptyCollection_in_graphTerm2750);
                    emptyCollection91=emptyCollection();

                    state._fsp--;

                     n = emptyCollection91; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "graphTerm"


    // $ANTLR start "expression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1187:1: expression returns [Expr e] : ( conditionalOrExpression | conditionalAndExpression | valueLogical );
    public final Expr expression() throws RecognitionException {
        Expr e = null;

        Expr conditionalOrExpression92 = null;

        Expr conditionalAndExpression93 = null;

        Expr valueLogical94 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1189:2: ( conditionalOrExpression | conditionalAndExpression | valueLogical )
            int alt63=3;
            switch ( input.LA(1) ) {
            case CONDITIONAL_EXPRESSION_OR:
                {
                alt63=1;
                }
                break;
            case CONDITIONAL_EXPRESSION_AND:
                {
                alt63=2;
                }
                break;
            case BUILTIN_BOUND:
            case BUILTIN_DATATYPE:
            case BUILTIN_IS_BLANK:
            case BUILTIN_IS_IRI:
            case BUILTIN_IS_LITERAL:
            case BUILTIN_IS_URI:
            case BUILTIN_LANG:
            case BUILTIN_LANGMATCHES:
            case BUILTIN_REGEX_BINARY:
            case BUILTIN_REGEX_TERNARY:
            case BUILTIN_SAME_TERM:
            case BUILTIN_STR:
            case FUNCTION_CALL:
            case IRI_REF:
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
            case NUMERIC_EXPRESSION_ADD:
            case NUMERIC_EXPRESSION_DIVIDE:
            case NUMERIC_EXPRESSION_MULTIPLY:
            case NUMERIC_EXPRESSION_SUBTRACT:
            case PREFIXED_NAME:
            case RELATIONAL_EQUAL:
            case RELATIONAL_GREATER:
            case RELATIONAL_GREATER_EQUAL:
            case RELATIONAL_LESS:
            case RELATIONAL_LESS_EQUAL:
            case RELATIONAL_NOT_EQUAL:
            case UNARY_EXPRESSION_NEGATIVE:
            case UNARY_EXPRESSION_NOT:
            case UNARY_EXPRESSION_POSITIVE:
            case VARIABLE:
                {
                alt63=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }

            switch (alt63) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1189:4: conditionalOrExpression
                    {
                    pushFollow(FOLLOW_conditionalOrExpression_in_expression2773);
                    conditionalOrExpression92=conditionalOrExpression();

                    state._fsp--;

                     e = conditionalOrExpression92; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1191:4: conditionalAndExpression
                    {
                    pushFollow(FOLLOW_conditionalAndExpression_in_expression2782);
                    conditionalAndExpression93=conditionalAndExpression();

                    state._fsp--;

                     e = conditionalAndExpression93; 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1193:4: valueLogical
                    {
                    pushFollow(FOLLOW_valueLogical_in_expression2791);
                    valueLogical94=valueLogical();

                    state._fsp--;

                     e = valueLogical94; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "expression"


    // $ANTLR start "conditionalOrExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1200:1: conditionalOrExpression returns [Expr e] : ^( CONDITIONAL_EXPRESSION_OR a= expression b= expression ) ;
    public final Expr conditionalOrExpression() throws RecognitionException {
        Expr e = null;

        Expr a = null;

        Expr b = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1202:2: ( ^( CONDITIONAL_EXPRESSION_OR a= expression b= expression ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1202:4: ^( CONDITIONAL_EXPRESSION_OR a= expression b= expression )
            {
            match(input,CONDITIONAL_EXPRESSION_OR,FOLLOW_CONDITIONAL_EXPRESSION_OR_in_conditionalOrExpression2814); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_conditionalOrExpression2818);
            a=expression();

            state._fsp--;

            pushFollow(FOLLOW_expression_in_conditionalOrExpression2822);
            b=expression();

            state._fsp--;


            match(input, Token.UP, null); 
             e = new E_LogicalOr( a, b ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1209:1: conditionalAndExpression returns [Expr e] : ^( CONDITIONAL_EXPRESSION_AND a= expression b= expression ) ;
    public final Expr conditionalAndExpression() throws RecognitionException {
        Expr e = null;

        Expr a = null;

        Expr b = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1211:2: ( ^( CONDITIONAL_EXPRESSION_AND a= expression b= expression ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1211:4: ^( CONDITIONAL_EXPRESSION_AND a= expression b= expression )
            {
            match(input,CONDITIONAL_EXPRESSION_AND,FOLLOW_CONDITIONAL_EXPRESSION_AND_in_conditionalAndExpression2846); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_expression_in_conditionalAndExpression2850);
            a=expression();

            state._fsp--;

            pushFollow(FOLLOW_expression_in_conditionalAndExpression2854);
            b=expression();

            state._fsp--;


            match(input, Token.UP, null); 
             e = new E_LogicalAnd( a, b ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "valueLogical"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1218:1: valueLogical returns [Expr e] : relationalExpression ;
    public final Expr valueLogical() throws RecognitionException {
        Expr e = null;

        Expr relationalExpression95 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1220:2: ( relationalExpression )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1220:4: relationalExpression
            {
            pushFollow(FOLLOW_relationalExpression_in_valueLogical2877);
            relationalExpression95=relationalExpression();

            state._fsp--;

             e = relationalExpression95; 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "valueLogical"


    // $ANTLR start "relationalExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1227:1: relationalExpression returns [Expr e] : ( numericExpression | ^( RELATIONAL_EQUAL a= numericExpression b= numericExpression ) | ^( RELATIONAL_NOT_EQUAL a= numericExpression b= numericExpression ) | ^( RELATIONAL_LESS a= numericExpression b= numericExpression ) | ^( RELATIONAL_GREATER a= numericExpression b= numericExpression ) | ^( RELATIONAL_LESS_EQUAL a= numericExpression b= numericExpression ) | ^( RELATIONAL_GREATER_EQUAL a= numericExpression b= numericExpression ) );
    public final Expr relationalExpression() throws RecognitionException {
        Expr e = null;

        Expr a = null;

        Expr b = null;

        Expr numericExpression96 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1229:2: ( numericExpression | ^( RELATIONAL_EQUAL a= numericExpression b= numericExpression ) | ^( RELATIONAL_NOT_EQUAL a= numericExpression b= numericExpression ) | ^( RELATIONAL_LESS a= numericExpression b= numericExpression ) | ^( RELATIONAL_GREATER a= numericExpression b= numericExpression ) | ^( RELATIONAL_LESS_EQUAL a= numericExpression b= numericExpression ) | ^( RELATIONAL_GREATER_EQUAL a= numericExpression b= numericExpression ) )
            int alt64=7;
            switch ( input.LA(1) ) {
            case BUILTIN_BOUND:
            case BUILTIN_DATATYPE:
            case BUILTIN_IS_BLANK:
            case BUILTIN_IS_IRI:
            case BUILTIN_IS_LITERAL:
            case BUILTIN_IS_URI:
            case BUILTIN_LANG:
            case BUILTIN_LANGMATCHES:
            case BUILTIN_REGEX_BINARY:
            case BUILTIN_REGEX_TERNARY:
            case BUILTIN_SAME_TERM:
            case BUILTIN_STR:
            case FUNCTION_CALL:
            case IRI_REF:
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
            case NUMERIC_EXPRESSION_ADD:
            case NUMERIC_EXPRESSION_DIVIDE:
            case NUMERIC_EXPRESSION_MULTIPLY:
            case NUMERIC_EXPRESSION_SUBTRACT:
            case PREFIXED_NAME:
            case UNARY_EXPRESSION_NEGATIVE:
            case UNARY_EXPRESSION_NOT:
            case UNARY_EXPRESSION_POSITIVE:
            case VARIABLE:
                {
                alt64=1;
                }
                break;
            case RELATIONAL_EQUAL:
                {
                alt64=2;
                }
                break;
            case RELATIONAL_NOT_EQUAL:
                {
                alt64=3;
                }
                break;
            case RELATIONAL_LESS:
                {
                alt64=4;
                }
                break;
            case RELATIONAL_GREATER:
                {
                alt64=5;
                }
                break;
            case RELATIONAL_LESS_EQUAL:
                {
                alt64=6;
                }
                break;
            case RELATIONAL_GREATER_EQUAL:
                {
                alt64=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }

            switch (alt64) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1229:4: numericExpression
                    {
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2899);
                    numericExpression96=numericExpression();

                    state._fsp--;

                     e = numericExpression96; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1231:4: ^( RELATIONAL_EQUAL a= numericExpression b= numericExpression )
                    {
                    match(input,RELATIONAL_EQUAL,FOLLOW_RELATIONAL_EQUAL_in_relationalExpression2909); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2913);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2917);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Equals( a, b ); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1233:4: ^( RELATIONAL_NOT_EQUAL a= numericExpression b= numericExpression )
                    {
                    match(input,RELATIONAL_NOT_EQUAL,FOLLOW_RELATIONAL_NOT_EQUAL_in_relationalExpression2928); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2932);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2936);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_NotEquals( a, b ); 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1235:4: ^( RELATIONAL_LESS a= numericExpression b= numericExpression )
                    {
                    match(input,RELATIONAL_LESS,FOLLOW_RELATIONAL_LESS_in_relationalExpression2947); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2951);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2955);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_LessThan( a, b ); 

                    }
                    break;
                case 5 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1237:4: ^( RELATIONAL_GREATER a= numericExpression b= numericExpression )
                    {
                    match(input,RELATIONAL_GREATER,FOLLOW_RELATIONAL_GREATER_in_relationalExpression2966); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2970);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2974);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_GreaterThan( a, b ); 

                    }
                    break;
                case 6 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1239:4: ^( RELATIONAL_LESS_EQUAL a= numericExpression b= numericExpression )
                    {
                    match(input,RELATIONAL_LESS_EQUAL,FOLLOW_RELATIONAL_LESS_EQUAL_in_relationalExpression2985); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2989);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression2993);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_LessThanOrEqual( a, b ); 

                    }
                    break;
                case 7 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1241:4: ^( RELATIONAL_GREATER_EQUAL a= numericExpression b= numericExpression )
                    {
                    match(input,RELATIONAL_GREATER_EQUAL,FOLLOW_RELATIONAL_GREATER_EQUAL_in_relationalExpression3004); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3008);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3012);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_GreaterThanOrEqual( a, b ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "relationalExpression"


    // $ANTLR start "numericExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1248:1: numericExpression returns [Expr e] : ( additiveExpression | multiplicativeExpression | unaryExpression );
    public final Expr numericExpression() throws RecognitionException {
        Expr e = null;

        Expr additiveExpression97 = null;

        Expr multiplicativeExpression98 = null;

        Expr unaryExpression99 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1250:2: ( additiveExpression | multiplicativeExpression | unaryExpression )
            int alt65=3;
            switch ( input.LA(1) ) {
            case NUMERIC_EXPRESSION_ADD:
            case NUMERIC_EXPRESSION_SUBTRACT:
                {
                alt65=1;
                }
                break;
            case NUMERIC_EXPRESSION_DIVIDE:
            case NUMERIC_EXPRESSION_MULTIPLY:
                {
                alt65=2;
                }
                break;
            case BUILTIN_BOUND:
            case BUILTIN_DATATYPE:
            case BUILTIN_IS_BLANK:
            case BUILTIN_IS_IRI:
            case BUILTIN_IS_LITERAL:
            case BUILTIN_IS_URI:
            case BUILTIN_LANG:
            case BUILTIN_LANGMATCHES:
            case BUILTIN_REGEX_BINARY:
            case BUILTIN_REGEX_TERNARY:
            case BUILTIN_SAME_TERM:
            case BUILTIN_STR:
            case FUNCTION_CALL:
            case IRI_REF:
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
            case PREFIXED_NAME:
            case UNARY_EXPRESSION_NEGATIVE:
            case UNARY_EXPRESSION_NOT:
            case UNARY_EXPRESSION_POSITIVE:
            case VARIABLE:
                {
                alt65=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }

            switch (alt65) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1250:4: additiveExpression
                    {
                    pushFollow(FOLLOW_additiveExpression_in_numericExpression3035);
                    additiveExpression97=additiveExpression();

                    state._fsp--;

                     e = additiveExpression97; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1252:4: multiplicativeExpression
                    {
                    pushFollow(FOLLOW_multiplicativeExpression_in_numericExpression3044);
                    multiplicativeExpression98=multiplicativeExpression();

                    state._fsp--;

                     e = multiplicativeExpression98; 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1254:4: unaryExpression
                    {
                    pushFollow(FOLLOW_unaryExpression_in_numericExpression3053);
                    unaryExpression99=unaryExpression();

                    state._fsp--;

                     e = unaryExpression99; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "numericExpression"


    // $ANTLR start "additiveExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1261:1: additiveExpression returns [Expr e] : ( ^( NUMERIC_EXPRESSION_ADD a= numericExpression b= numericExpression ) | ^( NUMERIC_EXPRESSION_SUBTRACT a= numericExpression b= numericExpression ) );
    public final Expr additiveExpression() throws RecognitionException {
        Expr e = null;

        Expr a = null;

        Expr b = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1263:2: ( ^( NUMERIC_EXPRESSION_ADD a= numericExpression b= numericExpression ) | ^( NUMERIC_EXPRESSION_SUBTRACT a= numericExpression b= numericExpression ) )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==NUMERIC_EXPRESSION_ADD) ) {
                alt66=1;
            }
            else if ( (LA66_0==NUMERIC_EXPRESSION_SUBTRACT) ) {
                alt66=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1263:4: ^( NUMERIC_EXPRESSION_ADD a= numericExpression b= numericExpression )
                    {
                    match(input,NUMERIC_EXPRESSION_ADD,FOLLOW_NUMERIC_EXPRESSION_ADD_in_additiveExpression3076); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_additiveExpression3080);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_additiveExpression3084);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Add( a, b ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1265:4: ^( NUMERIC_EXPRESSION_SUBTRACT a= numericExpression b= numericExpression )
                    {
                    match(input,NUMERIC_EXPRESSION_SUBTRACT,FOLLOW_NUMERIC_EXPRESSION_SUBTRACT_in_additiveExpression3095); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_additiveExpression3099);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_additiveExpression3103);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Subtract( a, b ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1272:1: multiplicativeExpression returns [Expr e] : ( ^( NUMERIC_EXPRESSION_MULTIPLY a= numericExpression b= numericExpression ) | ^( NUMERIC_EXPRESSION_DIVIDE a= numericExpression b= numericExpression ) );
    public final Expr multiplicativeExpression() throws RecognitionException {
        Expr e = null;

        Expr a = null;

        Expr b = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1274:2: ( ^( NUMERIC_EXPRESSION_MULTIPLY a= numericExpression b= numericExpression ) | ^( NUMERIC_EXPRESSION_DIVIDE a= numericExpression b= numericExpression ) )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==NUMERIC_EXPRESSION_MULTIPLY) ) {
                alt67=1;
            }
            else if ( (LA67_0==NUMERIC_EXPRESSION_DIVIDE) ) {
                alt67=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 67, 0, input);

                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1274:4: ^( NUMERIC_EXPRESSION_MULTIPLY a= numericExpression b= numericExpression )
                    {
                    match(input,NUMERIC_EXPRESSION_MULTIPLY,FOLLOW_NUMERIC_EXPRESSION_MULTIPLY_in_multiplicativeExpression3127); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_multiplicativeExpression3131);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_multiplicativeExpression3135);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Multiply( a, b ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1276:4: ^( NUMERIC_EXPRESSION_DIVIDE a= numericExpression b= numericExpression )
                    {
                    match(input,NUMERIC_EXPRESSION_DIVIDE,FOLLOW_NUMERIC_EXPRESSION_DIVIDE_in_multiplicativeExpression3147); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_numericExpression_in_multiplicativeExpression3151);
                    a=numericExpression();

                    state._fsp--;

                    pushFollow(FOLLOW_numericExpression_in_multiplicativeExpression3155);
                    b=numericExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Divide( a, b ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1283:1: unaryExpression returns [Expr e] : ( ^( UNARY_EXPRESSION_NOT primaryExpression ) | ^( UNARY_EXPRESSION_POSITIVE primaryExpression ) | ^( UNARY_EXPRESSION_NEGATIVE primaryExpression ) | primaryExpression );
    public final Expr unaryExpression() throws RecognitionException {
        Expr e = null;

        Expr primaryExpression100 = null;

        Expr primaryExpression101 = null;

        Expr primaryExpression102 = null;

        Expr primaryExpression103 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1285:2: ( ^( UNARY_EXPRESSION_NOT primaryExpression ) | ^( UNARY_EXPRESSION_POSITIVE primaryExpression ) | ^( UNARY_EXPRESSION_NEGATIVE primaryExpression ) | primaryExpression )
            int alt68=4;
            switch ( input.LA(1) ) {
            case UNARY_EXPRESSION_NOT:
                {
                alt68=1;
                }
                break;
            case UNARY_EXPRESSION_POSITIVE:
                {
                alt68=2;
                }
                break;
            case UNARY_EXPRESSION_NEGATIVE:
                {
                alt68=3;
                }
                break;
            case BUILTIN_BOUND:
            case BUILTIN_DATATYPE:
            case BUILTIN_IS_BLANK:
            case BUILTIN_IS_IRI:
            case BUILTIN_IS_LITERAL:
            case BUILTIN_IS_URI:
            case BUILTIN_LANG:
            case BUILTIN_LANGMATCHES:
            case BUILTIN_REGEX_BINARY:
            case BUILTIN_REGEX_TERNARY:
            case BUILTIN_SAME_TERM:
            case BUILTIN_STR:
            case FUNCTION_CALL:
            case IRI_REF:
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
            case PREFIXED_NAME:
            case VARIABLE:
                {
                alt68=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);

                throw nvae;
            }

            switch (alt68) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1285:4: ^( UNARY_EXPRESSION_NOT primaryExpression )
                    {
                    match(input,UNARY_EXPRESSION_NOT,FOLLOW_UNARY_EXPRESSION_NOT_in_unaryExpression3180); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3182);
                    primaryExpression100=primaryExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_LogicalNot( primaryExpression100 ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1287:4: ^( UNARY_EXPRESSION_POSITIVE primaryExpression )
                    {
                    match(input,UNARY_EXPRESSION_POSITIVE,FOLLOW_UNARY_EXPRESSION_POSITIVE_in_unaryExpression3193); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3195);
                    primaryExpression101=primaryExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_UnaryPlus( primaryExpression101 ); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1289:4: ^( UNARY_EXPRESSION_NEGATIVE primaryExpression )
                    {
                    match(input,UNARY_EXPRESSION_NEGATIVE,FOLLOW_UNARY_EXPRESSION_NEGATIVE_in_unaryExpression3206); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3208);
                    primaryExpression102=primaryExpression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_UnaryMinus( primaryExpression102 ); 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1291:4: primaryExpression
                    {
                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3218);
                    primaryExpression103=primaryExpression();

                    state._fsp--;

                     e = primaryExpression103; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "unaryExpression"


    // $ANTLR start "primaryExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1300:1: primaryExpression returns [Expr e] : ( builtInCall | iriRefOrFunction | literal | var );
    public final Expr primaryExpression() throws RecognitionException {
        Expr e = null;

        Expr builtInCall104 = null;

        Expr iriRefOrFunction105 = null;

        Node literal106 = null;

        Node var107 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1302:2: ( builtInCall | iriRefOrFunction | literal | var )
            int alt69=4;
            switch ( input.LA(1) ) {
            case BUILTIN_BOUND:
            case BUILTIN_DATATYPE:
            case BUILTIN_IS_BLANK:
            case BUILTIN_IS_IRI:
            case BUILTIN_IS_LITERAL:
            case BUILTIN_IS_URI:
            case BUILTIN_LANG:
            case BUILTIN_LANGMATCHES:
            case BUILTIN_REGEX_BINARY:
            case BUILTIN_REGEX_TERNARY:
            case BUILTIN_SAME_TERM:
            case BUILTIN_STR:
                {
                alt69=1;
                }
                break;
            case FUNCTION_CALL:
            case IRI_REF:
            case PREFIXED_NAME:
                {
                alt69=2;
                }
                break;
            case LITERAL_BOOLEAN_FALSE:
            case LITERAL_BOOLEAN_TRUE:
            case LITERAL_DECIMAL:
            case LITERAL_DOUBLE:
            case LITERAL_INTEGER:
            case LITERAL_LANG:
            case LITERAL_PLAIN:
            case LITERAL_TYPED:
                {
                alt69=3;
                }
                break;
            case VARIABLE:
                {
                alt69=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }

            switch (alt69) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1302:4: builtInCall
                    {
                    pushFollow(FOLLOW_builtInCall_in_primaryExpression3240);
                    builtInCall104=builtInCall();

                    state._fsp--;

                     e = builtInCall104; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1303:4: iriRefOrFunction
                    {
                    pushFollow(FOLLOW_iriRefOrFunction_in_primaryExpression3247);
                    iriRefOrFunction105=iriRefOrFunction();

                    state._fsp--;

                     e = iriRefOrFunction105; 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1304:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_primaryExpression3254);
                    literal106=literal();

                    state._fsp--;

                     e = nodeToExpr( literal106 ); 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1305:4: var
                    {
                    pushFollow(FOLLOW_var_in_primaryExpression3261);
                    var107=var();

                    state._fsp--;

                     e = nodeToExpr( var107 ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "primaryExpression"


    // $ANTLR start "builtInCall"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1311:1: builtInCall returns [Expr e] : ( ^( BUILTIN_STR expression ) | ^( BUILTIN_LANG expression ) | ^( BUILTIN_LANGMATCHES a= expression b= expression ) | ^( BUILTIN_DATATYPE expression ) | ^( BUILTIN_BOUND var ) | ^( BUILTIN_SAME_TERM a= expression b= expression ) | ^( BUILTIN_IS_IRI expression ) | ^( BUILTIN_IS_URI expression ) | ^( BUILTIN_IS_BLANK expression ) | ^( BUILTIN_IS_LITERAL expression ) | regexExpression );
    public final Expr builtInCall() throws RecognitionException {
        Expr e = null;

        Expr a = null;

        Expr b = null;

        Expr expression108 = null;

        Expr expression109 = null;

        Expr expression110 = null;

        Node var111 = null;

        Expr expression112 = null;

        Expr expression113 = null;

        Expr expression114 = null;

        Expr expression115 = null;

        Expr regexExpression116 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1313:2: ( ^( BUILTIN_STR expression ) | ^( BUILTIN_LANG expression ) | ^( BUILTIN_LANGMATCHES a= expression b= expression ) | ^( BUILTIN_DATATYPE expression ) | ^( BUILTIN_BOUND var ) | ^( BUILTIN_SAME_TERM a= expression b= expression ) | ^( BUILTIN_IS_IRI expression ) | ^( BUILTIN_IS_URI expression ) | ^( BUILTIN_IS_BLANK expression ) | ^( BUILTIN_IS_LITERAL expression ) | regexExpression )
            int alt70=11;
            switch ( input.LA(1) ) {
            case BUILTIN_STR:
                {
                alt70=1;
                }
                break;
            case BUILTIN_LANG:
                {
                alt70=2;
                }
                break;
            case BUILTIN_LANGMATCHES:
                {
                alt70=3;
                }
                break;
            case BUILTIN_DATATYPE:
                {
                alt70=4;
                }
                break;
            case BUILTIN_BOUND:
                {
                alt70=5;
                }
                break;
            case BUILTIN_SAME_TERM:
                {
                alt70=6;
                }
                break;
            case BUILTIN_IS_IRI:
                {
                alt70=7;
                }
                break;
            case BUILTIN_IS_URI:
                {
                alt70=8;
                }
                break;
            case BUILTIN_IS_BLANK:
                {
                alt70=9;
                }
                break;
            case BUILTIN_IS_LITERAL:
                {
                alt70=10;
                }
                break;
            case BUILTIN_REGEX_BINARY:
            case BUILTIN_REGEX_TERNARY:
                {
                alt70=11;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }

            switch (alt70) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1313:4: ^( BUILTIN_STR expression )
                    {
                    match(input,BUILTIN_STR,FOLLOW_BUILTIN_STR_in_builtInCall3282); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3284);
                    expression108=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Str( expression108 ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1315:4: ^( BUILTIN_LANG expression )
                    {
                    match(input,BUILTIN_LANG,FOLLOW_BUILTIN_LANG_in_builtInCall3295); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3297);
                    expression109=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Lang( expression109 ); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1317:4: ^( BUILTIN_LANGMATCHES a= expression b= expression )
                    {
                    match(input,BUILTIN_LANGMATCHES,FOLLOW_BUILTIN_LANGMATCHES_in_builtInCall3308); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3312);
                    a=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_builtInCall3316);
                    b=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_LangMatches( a, b ); 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1319:4: ^( BUILTIN_DATATYPE expression )
                    {
                    match(input,BUILTIN_DATATYPE,FOLLOW_BUILTIN_DATATYPE_in_builtInCall3327); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3329);
                    expression110=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Datatype( expression110 ); 

                    }
                    break;
                case 5 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1321:4: ^( BUILTIN_BOUND var )
                    {
                    match(input,BUILTIN_BOUND,FOLLOW_BUILTIN_BOUND_in_builtInCall3340); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_var_in_builtInCall3342);
                    var111=var();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Bound( nodeToExpr( var111 ) ); 

                    }
                    break;
                case 6 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1323:4: ^( BUILTIN_SAME_TERM a= expression b= expression )
                    {
                    match(input,BUILTIN_SAME_TERM,FOLLOW_BUILTIN_SAME_TERM_in_builtInCall3353); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3357);
                    a=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_builtInCall3361);
                    b=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_SameTerm( a, b ); 

                    }
                    break;
                case 7 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1325:4: ^( BUILTIN_IS_IRI expression )
                    {
                    match(input,BUILTIN_IS_IRI,FOLLOW_BUILTIN_IS_IRI_in_builtInCall3372); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3374);
                    expression112=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_IsIRI( expression112 ); 

                    }
                    break;
                case 8 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1327:4: ^( BUILTIN_IS_URI expression )
                    {
                    match(input,BUILTIN_IS_URI,FOLLOW_BUILTIN_IS_URI_in_builtInCall3385); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3387);
                    expression113=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_IsURI( expression113 ); 

                    }
                    break;
                case 9 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1329:4: ^( BUILTIN_IS_BLANK expression )
                    {
                    match(input,BUILTIN_IS_BLANK,FOLLOW_BUILTIN_IS_BLANK_in_builtInCall3398); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3400);
                    expression114=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_IsBlank( expression114 ); 

                    }
                    break;
                case 10 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1331:4: ^( BUILTIN_IS_LITERAL expression )
                    {
                    match(input,BUILTIN_IS_LITERAL,FOLLOW_BUILTIN_IS_LITERAL_in_builtInCall3411); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_builtInCall3413);
                    expression115=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_IsLiteral( expression115 ); 

                    }
                    break;
                case 11 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1333:4: regexExpression
                    {
                    pushFollow(FOLLOW_regexExpression_in_builtInCall3423);
                    regexExpression116=regexExpression();

                    state._fsp--;

                     e = regexExpression116; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "builtInCall"


    // $ANTLR start "regexExpression"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1340:1: regexExpression returns [Expr e] : ( ^( BUILTIN_REGEX_BINARY a= expression b= expression ) | ^( BUILTIN_REGEX_TERNARY a= expression b= expression c= expression ) );
    public final Expr regexExpression() throws RecognitionException {
        Expr e = null;

        Expr a = null;

        Expr b = null;

        Expr c = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1342:2: ( ^( BUILTIN_REGEX_BINARY a= expression b= expression ) | ^( BUILTIN_REGEX_TERNARY a= expression b= expression c= expression ) )
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==BUILTIN_REGEX_BINARY) ) {
                alt71=1;
            }
            else if ( (LA71_0==BUILTIN_REGEX_TERNARY) ) {
                alt71=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1342:4: ^( BUILTIN_REGEX_BINARY a= expression b= expression )
                    {
                    match(input,BUILTIN_REGEX_BINARY,FOLLOW_BUILTIN_REGEX_BINARY_in_regexExpression3446); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_regexExpression3450);
                    a=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_regexExpression3454);
                    b=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Regex( a, b, null ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1344:4: ^( BUILTIN_REGEX_TERNARY a= expression b= expression c= expression )
                    {
                    match(input,BUILTIN_REGEX_TERNARY,FOLLOW_BUILTIN_REGEX_TERNARY_in_regexExpression3466); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_regexExpression3470);
                    a=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_regexExpression3474);
                    b=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_regexExpression3478);
                    c=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     e = new E_Regex( a, b, c ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "regexExpression"


    // $ANTLR start "iriRefOrFunction"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1351:1: iriRefOrFunction returns [Expr e] : ( iriRef | functionCall );
    public final Expr iriRefOrFunction() throws RecognitionException {
        Expr e = null;

        Node iriRef117 = null;

        Expr functionCall118 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1353:2: ( iriRef | functionCall )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==IRI_REF||LA72_0==PREFIXED_NAME) ) {
                alt72=1;
            }
            else if ( (LA72_0==FUNCTION_CALL) ) {
                alt72=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1353:4: iriRef
                    {
                    pushFollow(FOLLOW_iriRef_in_iriRefOrFunction3501);
                    iriRef117=iriRef();

                    state._fsp--;

                     e = nodeToExpr( iriRef117 ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1354:4: functionCall
                    {
                    pushFollow(FOLLOW_functionCall_in_iriRefOrFunction3508);
                    functionCall118=functionCall();

                    state._fsp--;

                     e = functionCall118; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return e;
    }
    // $ANTLR end "iriRefOrFunction"


    // $ANTLR start "rdfLiteral"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1361:1: rdfLiteral returns [Node l] : ( ^( LITERAL_PLAIN string ) | ^( LITERAL_LANG string lang= LANGTAG ) | ^( LITERAL_TYPED string iriRef ) );
    public final Node rdfLiteral() throws RecognitionException {
        Node l = null;

        CommonTree lang=null;
        String string119 = null;

        String string120 = null;

        Node iriRef121 = null;

        String string122 = null;


        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1363:2: ( ^( LITERAL_PLAIN string ) | ^( LITERAL_LANG string lang= LANGTAG ) | ^( LITERAL_TYPED string iriRef ) )
            int alt73=3;
            switch ( input.LA(1) ) {
            case LITERAL_PLAIN:
                {
                alt73=1;
                }
                break;
            case LITERAL_LANG:
                {
                alt73=2;
                }
                break;
            case LITERAL_TYPED:
                {
                alt73=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }

            switch (alt73) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1363:4: ^( LITERAL_PLAIN string )
                    {
                    match(input,LITERAL_PLAIN,FOLLOW_LITERAL_PLAIN_in_rdfLiteral3530); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_string_in_rdfLiteral3532);
                    string119=string();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     l = Node.createLiteral( string119 ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1365:4: ^( LITERAL_LANG string lang= LANGTAG )
                    {
                    match(input,LITERAL_LANG,FOLLOW_LITERAL_LANG_in_rdfLiteral3543); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_string_in_rdfLiteral3545);
                    string120=string();

                    state._fsp--;

                    lang=(CommonTree)match(input,LANGTAG,FOLLOW_LANGTAG_in_rdfLiteral3549); 

                    match(input, Token.UP, null); 
                     l = Node.createLiteral( string120, (lang!=null?lang.getText():null), false ); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1367:4: ^( LITERAL_TYPED string iriRef )
                    {
                    match(input,LITERAL_TYPED,FOLLOW_LITERAL_TYPED_in_rdfLiteral3560); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_string_in_rdfLiteral3562);
                    string122=string();

                    state._fsp--;

                    pushFollow(FOLLOW_iriRef_in_rdfLiteral3564);
                    iriRef121=iriRef();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    			RDFDatatype dType = TypeMapper.getInstance().getSafeTypeByName( iriRef121.getURI() );
                    			l = Node.createLiteral( string122, null, dType );
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return l;
    }
    // $ANTLR end "rdfLiteral"


    // $ANTLR start "numericLiteral"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1378:1: numericLiteral returns [Node n] : ( ^( LITERAL_INTEGER ( INTEGER | POSITIVE_INTEGER | NEGATIVE_INTEGER ) ) | ^( LITERAL_DECIMAL ( DECIMAL | POSITIVE_DECIMAL | NEGATIVE_DECIMAL ) ) | ^( LITERAL_DOUBLE ( DOUBLE | POSITIVE_DOUBLE | NEGATIVE_DOUBLE ) ) ) ;
    public final Node numericLiteral() throws RecognitionException {
        Node n = null;

        CommonTree INTEGER123=null;
        CommonTree POSITIVE_INTEGER124=null;
        CommonTree NEGATIVE_INTEGER125=null;
        CommonTree DECIMAL126=null;
        CommonTree POSITIVE_DECIMAL127=null;
        CommonTree NEGATIVE_DECIMAL128=null;
        CommonTree DOUBLE129=null;
        CommonTree POSITIVE_DOUBLE130=null;
        CommonTree NEGATIVE_DOUBLE131=null;


        		String s = null;
        		RDFDatatype t = null;
        	
        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1384:2: ( ( ^( LITERAL_INTEGER ( INTEGER | POSITIVE_INTEGER | NEGATIVE_INTEGER ) ) | ^( LITERAL_DECIMAL ( DECIMAL | POSITIVE_DECIMAL | NEGATIVE_DECIMAL ) ) | ^( LITERAL_DOUBLE ( DOUBLE | POSITIVE_DOUBLE | NEGATIVE_DOUBLE ) ) ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1384:4: ( ^( LITERAL_INTEGER ( INTEGER | POSITIVE_INTEGER | NEGATIVE_INTEGER ) ) | ^( LITERAL_DECIMAL ( DECIMAL | POSITIVE_DECIMAL | NEGATIVE_DECIMAL ) ) | ^( LITERAL_DOUBLE ( DOUBLE | POSITIVE_DOUBLE | NEGATIVE_DOUBLE ) ) )
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1384:4: ( ^( LITERAL_INTEGER ( INTEGER | POSITIVE_INTEGER | NEGATIVE_INTEGER ) ) | ^( LITERAL_DECIMAL ( DECIMAL | POSITIVE_DECIMAL | NEGATIVE_DECIMAL ) ) | ^( LITERAL_DOUBLE ( DOUBLE | POSITIVE_DOUBLE | NEGATIVE_DOUBLE ) ) )
            int alt77=3;
            switch ( input.LA(1) ) {
            case LITERAL_INTEGER:
                {
                alt77=1;
                }
                break;
            case LITERAL_DECIMAL:
                {
                alt77=2;
                }
                break;
            case LITERAL_DOUBLE:
                {
                alt77=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }

            switch (alt77) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1384:6: ^( LITERAL_INTEGER ( INTEGER | POSITIVE_INTEGER | NEGATIVE_INTEGER ) )
                    {
                    match(input,LITERAL_INTEGER,FOLLOW_LITERAL_INTEGER_in_numericLiteral3596); 

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1385:5: ( INTEGER | POSITIVE_INTEGER | NEGATIVE_INTEGER )
                    int alt74=3;
                    switch ( input.LA(1) ) {
                    case INTEGER:
                        {
                        alt74=1;
                        }
                        break;
                    case POSITIVE_INTEGER:
                        {
                        alt74=2;
                        }
                        break;
                    case NEGATIVE_INTEGER:
                        {
                        alt74=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 74, 0, input);

                        throw nvae;
                    }

                    switch (alt74) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1385:7: INTEGER
                            {
                            INTEGER123=(CommonTree)match(input,INTEGER,FOLLOW_INTEGER_in_numericLiteral3604); 
                             s = (INTEGER123!=null?INTEGER123.getText():null); 

                            }
                            break;
                        case 2 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1386:7: POSITIVE_INTEGER
                            {
                            POSITIVE_INTEGER124=(CommonTree)match(input,POSITIVE_INTEGER,FOLLOW_POSITIVE_INTEGER_in_numericLiteral3614); 
                             s = (POSITIVE_INTEGER124!=null?POSITIVE_INTEGER124.getText():null); 

                            }
                            break;
                        case 3 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1387:7: NEGATIVE_INTEGER
                            {
                            NEGATIVE_INTEGER125=(CommonTree)match(input,NEGATIVE_INTEGER,FOLLOW_NEGATIVE_INTEGER_in_numericLiteral3624); 
                             s = (NEGATIVE_INTEGER125!=null?NEGATIVE_INTEGER125.getText():null); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     t = XSDDatatype.XSDinteger ; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1391:5: ^( LITERAL_DECIMAL ( DECIMAL | POSITIVE_DECIMAL | NEGATIVE_DECIMAL ) )
                    {
                    match(input,LITERAL_DECIMAL,FOLLOW_LITERAL_DECIMAL_in_numericLiteral3649); 

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1392:5: ( DECIMAL | POSITIVE_DECIMAL | NEGATIVE_DECIMAL )
                    int alt75=3;
                    switch ( input.LA(1) ) {
                    case DECIMAL:
                        {
                        alt75=1;
                        }
                        break;
                    case POSITIVE_DECIMAL:
                        {
                        alt75=2;
                        }
                        break;
                    case NEGATIVE_DECIMAL:
                        {
                        alt75=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 75, 0, input);

                        throw nvae;
                    }

                    switch (alt75) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1392:7: DECIMAL
                            {
                            DECIMAL126=(CommonTree)match(input,DECIMAL,FOLLOW_DECIMAL_in_numericLiteral3657); 
                             s = (DECIMAL126!=null?DECIMAL126.getText():null); 

                            }
                            break;
                        case 2 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1393:7: POSITIVE_DECIMAL
                            {
                            POSITIVE_DECIMAL127=(CommonTree)match(input,POSITIVE_DECIMAL,FOLLOW_POSITIVE_DECIMAL_in_numericLiteral3667); 
                             s = (POSITIVE_DECIMAL127!=null?POSITIVE_DECIMAL127.getText():null); 

                            }
                            break;
                        case 3 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1394:7: NEGATIVE_DECIMAL
                            {
                            NEGATIVE_DECIMAL128=(CommonTree)match(input,NEGATIVE_DECIMAL,FOLLOW_NEGATIVE_DECIMAL_in_numericLiteral3677); 
                             s = (NEGATIVE_DECIMAL128!=null?NEGATIVE_DECIMAL128.getText():null); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     t = XSDDatatype.XSDdecimal ; 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1398:5: ^( LITERAL_DOUBLE ( DOUBLE | POSITIVE_DOUBLE | NEGATIVE_DOUBLE ) )
                    {
                    match(input,LITERAL_DOUBLE,FOLLOW_LITERAL_DOUBLE_in_numericLiteral3702); 

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1399:5: ( DOUBLE | POSITIVE_DOUBLE | NEGATIVE_DOUBLE )
                    int alt76=3;
                    switch ( input.LA(1) ) {
                    case DOUBLE:
                        {
                        alt76=1;
                        }
                        break;
                    case POSITIVE_DOUBLE:
                        {
                        alt76=2;
                        }
                        break;
                    case NEGATIVE_DOUBLE:
                        {
                        alt76=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 76, 0, input);

                        throw nvae;
                    }

                    switch (alt76) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1399:7: DOUBLE
                            {
                            DOUBLE129=(CommonTree)match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteral3710); 
                             s = (DOUBLE129!=null?DOUBLE129.getText():null); 

                            }
                            break;
                        case 2 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1400:7: POSITIVE_DOUBLE
                            {
                            POSITIVE_DOUBLE130=(CommonTree)match(input,POSITIVE_DOUBLE,FOLLOW_POSITIVE_DOUBLE_in_numericLiteral3720); 
                             s = (POSITIVE_DOUBLE130!=null?POSITIVE_DOUBLE130.getText():null); 

                            }
                            break;
                        case 3 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1401:7: NEGATIVE_DOUBLE
                            {
                            NEGATIVE_DOUBLE131=(CommonTree)match(input,NEGATIVE_DOUBLE,FOLLOW_NEGATIVE_DOUBLE_in_numericLiteral3730); 
                             s = (NEGATIVE_DOUBLE131!=null?NEGATIVE_DOUBLE131.getText():null); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                     t = XSDDatatype.XSDdouble ; 

                    }
                    break;

            }

             n = Node.createLiteral( s, null, t ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return n;
    }
    // $ANTLR end "numericLiteral"


    // $ANTLR start "booleanLiteral"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1412:1: booleanLiteral returns [Node b] : ( LITERAL_BOOLEAN_TRUE | LITERAL_BOOLEAN_FALSE );
    public final Node booleanLiteral() throws RecognitionException {
        Node b = null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1414:2: ( LITERAL_BOOLEAN_TRUE | LITERAL_BOOLEAN_FALSE )
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==LITERAL_BOOLEAN_TRUE) ) {
                alt78=1;
            }
            else if ( (LA78_0==LITERAL_BOOLEAN_FALSE) ) {
                alt78=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1414:4: LITERAL_BOOLEAN_TRUE
                    {
                    match(input,LITERAL_BOOLEAN_TRUE,FOLLOW_LITERAL_BOOLEAN_TRUE_in_booleanLiteral3774); 
                     b = XSD_BOOLEAN_TRUE; 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1415:4: LITERAL_BOOLEAN_FALSE
                    {
                    match(input,LITERAL_BOOLEAN_FALSE,FOLLOW_LITERAL_BOOLEAN_FALSE_in_booleanLiteral3781); 
                     b = XSD_BOOLEAN_FALSE; 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return b;
    }
    // $ANTLR end "booleanLiteral"


    // $ANTLR start "string"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1421:1: string returns [String s] : (l= STRING_LITERAL1 | l= STRING_LITERAL2 | l= STRING_LITERAL_LONG1 | l= STRING_LITERAL_LONG2 ) ;
    public final String string() throws RecognitionException {
        String s = null;

        CommonTree l=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1423:2: ( (l= STRING_LITERAL1 | l= STRING_LITERAL2 | l= STRING_LITERAL_LONG1 | l= STRING_LITERAL_LONG2 ) )
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1423:4: (l= STRING_LITERAL1 | l= STRING_LITERAL2 | l= STRING_LITERAL_LONG1 | l= STRING_LITERAL_LONG2 )
            {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1423:4: (l= STRING_LITERAL1 | l= STRING_LITERAL2 | l= STRING_LITERAL_LONG1 | l= STRING_LITERAL_LONG2 )
            int alt79=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL1:
                {
                alt79=1;
                }
                break;
            case STRING_LITERAL2:
                {
                alt79=2;
                }
                break;
            case STRING_LITERAL_LONG1:
                {
                alt79=3;
                }
                break;
            case STRING_LITERAL_LONG2:
                {
                alt79=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);

                throw nvae;
            }

            switch (alt79) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1423:6: l= STRING_LITERAL1
                    {
                    l=(CommonTree)match(input,STRING_LITERAL1,FOLLOW_STRING_LITERAL1_in_string3805); 
                     s = dropFirstAndLast( (l!=null?l.getText():null) ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1424:5: l= STRING_LITERAL2
                    {
                    l=(CommonTree)match(input,STRING_LITERAL2,FOLLOW_STRING_LITERAL2_in_string3815); 
                     s = dropFirstAndLast( (l!=null?l.getText():null) ); 

                    }
                    break;
                case 3 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1425:5: l= STRING_LITERAL_LONG1
                    {
                    l=(CommonTree)match(input,STRING_LITERAL_LONG1,FOLLOW_STRING_LITERAL_LONG1_in_string3825); 
                     s = dropFirstAndLast3( (l!=null?l.getText():null) ); 

                    }
                    break;
                case 4 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1426:5: l= STRING_LITERAL_LONG2
                    {
                    l=(CommonTree)match(input,STRING_LITERAL_LONG2,FOLLOW_STRING_LITERAL_LONG2_in_string3835); 
                     s = dropFirstAndLast3( (l!=null?l.getText():null) ); 

                    }
                    break;

            }

             s = sparqlUnescape( s ); 

            }

        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return s;
    }
    // $ANTLR end "string"


    // $ANTLR start "iriRef"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1434:2: iriRef returns [Node i] : ( ^( IRI_REF ref= IRI_REF_TERM ) | ^( PREFIXED_NAME (p= PNAME_LN | p= PNAME_NS ) ) );
    public final Node iriRef() throws RecognitionException {
        Node i = null;

        CommonTree ref=null;
        CommonTree p=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1436:2: ( ^( IRI_REF ref= IRI_REF_TERM ) | ^( PREFIXED_NAME (p= PNAME_LN | p= PNAME_NS ) ) )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==IRI_REF) ) {
                alt81=1;
            }
            else if ( (LA81_0==PREFIXED_NAME) ) {
                alt81=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1436:4: ^( IRI_REF ref= IRI_REF_TERM )
                    {
                    match(input,IRI_REF,FOLLOW_IRI_REF_in_iriRef3865); 

                    match(input, Token.DOWN, null); 
                    ref=(CommonTree)match(input,IRI_REF_TERM,FOLLOW_IRI_REF_TERM_in_iriRef3869); 

                    match(input, Token.UP, null); 
                     i = Node.createURI( (ref!=null?ref.getText():null) ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1438:4: ^( PREFIXED_NAME (p= PNAME_LN | p= PNAME_NS ) )
                    {
                    match(input,PREFIXED_NAME,FOLLOW_PREFIXED_NAME_in_iriRef3880); 

                    match(input, Token.DOWN, null); 
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1438:20: (p= PNAME_LN | p= PNAME_NS )
                    int alt80=2;
                    int LA80_0 = input.LA(1);

                    if ( (LA80_0==PNAME_LN) ) {
                        alt80=1;
                    }
                    else if ( (LA80_0==PNAME_NS) ) {
                        alt80=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 80, 0, input);

                        throw nvae;
                    }
                    switch (alt80) {
                        case 1 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1438:22: p= PNAME_LN
                            {
                            p=(CommonTree)match(input,PNAME_LN,FOLLOW_PNAME_LN_in_iriRef3886); 

                            }
                            break;
                        case 2 :
                            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1438:35: p= PNAME_NS
                            {
                            p=(CommonTree)match(input,PNAME_NS,FOLLOW_PNAME_NS_in_iriRef3892); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    			String resolved = this.prologue.expandPrefixedName( (p!=null?p.getText():null) );
                    			// FIXME: Null case
                    			i = Node.createURI( resolved );
                    		

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return i;
    }
    // $ANTLR end "iriRef"


    // $ANTLR start "blankNode"
    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1449:1: blankNode returns [Node b] : ( ^( BLANK_NODE label= BLANK_NODE_LABEL ) | BLANK_NODE );
    public final Node blankNode() throws RecognitionException {
        Node b = null;

        CommonTree label=null;

        try {
            // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1451:2: ( ^( BLANK_NODE label= BLANK_NODE_LABEL ) | BLANK_NODE )
            int alt82=2;
            int LA82_0 = input.LA(1);

            if ( (LA82_0==BLANK_NODE) ) {
                int LA82_1 = input.LA(2);

                if ( (LA82_1==DOWN) ) {
                    alt82=1;
                }
                else if ( (LA82_1==UP||(LA82_1>=BLANK_NODE && LA82_1<=BNODE_PROPERTY_LIST)||LA82_1==COLLECTION||LA82_1==IRI_REF||(LA82_1>=LITERAL_BOOLEAN_FALSE && LA82_1<=LITERAL_TYPED)||LA82_1==PREFIXED_NAME||LA82_1==VARIABLE) ) {
                    alt82=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 82, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1451:4: ^( BLANK_NODE label= BLANK_NODE_LABEL )
                    {
                    match(input,BLANK_NODE,FOLLOW_BLANK_NODE_in_blankNode3919); 

                    match(input, Token.DOWN, null); 
                    label=(CommonTree)match(input,BLANK_NODE_LABEL,FOLLOW_BLANK_NODE_LABEL_in_blankNode3923); 

                    match(input, Token.UP, null); 
                     b = getAnon( (label!=null?label.getText():null) ); 

                    }
                    break;
                case 2 :
                    // /Users/evren/projects/pellet/query/antlr/SparqlOwlTreeARQ.g:1453:4: BLANK_NODE
                    {
                    match(input,BLANK_NODE,FOLLOW_BLANK_NODE_in_blankNode3933); 
                     b = getAnon( ); 

                    }
                    break;

            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return b;
    }
    // $ANTLR end "blankNode"

    // Delegated rules


    protected DFA15 dfa15 = new DFA15(this);
    static final String DFA15_eotS =
        "\13\uffff";
    static final String DFA15_eofS =
        "\13\uffff";
    static final String DFA15_minS =
        "\1\4\2\uffff\1\2\4\uffff\1\35\2\uffff";
    static final String DFA15_maxS =
        "\1\160\2\uffff\1\2\4\uffff\1\123\2\uffff";
    static final String DFA15_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\5\1\6\1\7\1\10\1\uffff\1\3\1\4";
    static final String DFA15_specialS =
        "\13\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\2\41\uffff\1\7\41\uffff\1\6\1\5\34\uffff\1\4\1\1\10\uffff"+
            "\1\3",
            "",
            "",
            "\1\10",
            "",
            "",
            "",
            "",
            "\1\12\37\uffff\1\11\25\uffff\1\11",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "457:1: restriction returns [Node n, Collection<Triple> triples] : ( ^( SOME_RESTRICTION propertyExpression disjunction ) | ^( ALL_RESTRICTION propertyExpression disjunction ) | ^( VALUE_RESTRICTION objectPropertyExpression individual ) | ^( VALUE_RESTRICTION dataPropertyIRI literal ) | ^( SELF_RESTRICTION objectPropertyExpression ) | ^( MIN_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) | ^( MAX_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) | ^( EXACT_NUMBER_RESTRICTION propertyExpression i= INTEGER ( disjunction )? ) );";
        }
    }
 

    public static final BitSet FOLLOW_OBJECT_PROPERTY_in_objectPropertyIRI66 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iriRef_in_objectPropertyIRI68 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATA_PROPERTY_in_dataPropertyIRI92 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iriRef_in_dataPropertyIRI94 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PROPERTY_in_objectOrDataPropertyIRI118 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iriRef_in_objectOrDataPropertyIRI120 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INVERSE_PROPERTY_in_inverseObjectProperty142 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_objectPropertyIRI_in_inverseObjectProperty144 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_inverseObjectProperty_in_propertyExpression167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectOrDataPropertyIRI_in_propertyExpression176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inverseObjectProperty_in_objectPropertyExpression198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectPropertyIRI_in_objectPropertyExpression207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATATYPE_in_datatype230 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iriRef_in_datatype232 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATATYPE_in_datatype241 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_TERM_in_datatype243 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATATYPE_in_datatype252 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_TERM_in_datatype254 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATATYPE_in_datatype263 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FLOAT_TERM_in_datatype265 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATATYPE_in_datatype274 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_TERM_in_datatype276 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_INDIVIDUAL_in_individual298 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iriRef_in_individual300 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rdfLiteral_in_literal323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_literal332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATATYPE_RESTRICTION_in_datatypeRestriction364 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datatype_in_datatypeRestriction366 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_FACET_VALUE_in_datatypeRestriction379 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_facet_in_datatypeRestriction381 = new BitSet(new long[]{0x0000000000000000L,0x00000000000000FFL});
    public static final BitSet FOLLOW_restrictionValue_in_datatypeRestriction383 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FACET_LENGTH_in_facet423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_MINLENGTH_in_facet430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_MAXLENGTH_in_facet437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_PATTERN_in_facet444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_LANGPATTERN_in_facet451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_LESS_EQUAL_in_facet458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_LESS_in_facet465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_GREATER_EQUAL_in_facet472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FACET_GREATER_in_facet479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_restrictionValue499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DISJUNCTION_in_disjunction520 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_disjunction_in_disjunction524 = new BitSet(new long[]{0x0400006184400010L,0x000180C000002300L});
    public static final BitSet FOLLOW_disjunction_in_disjunction528 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_conjunction_in_disjunction538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_disjunction547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONJUNCTION_in_conjunction570 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_disjunction_in_conjunction574 = new BitSet(new long[]{0x0400006184400010L,0x000180C000002300L});
    public static final BitSet FOLLOW_disjunction_in_conjunction578 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEGATION_in_primary602 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_disjunction_in_primary604 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_restriction_in_primary614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomic_in_primary624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_OR_DATATYPE_in_atomic653 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iriRef_in_atomic655 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_datatype_in_atomic665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datatypeRestriction_in_atomic674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_ENUMERATION_in_atomic684 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_literal_in_atomic696 = new BitSet(new long[]{0x0000000000000008L,0x00000000000000FFL});
    public static final BitSet FOLLOW_INDIVIDUAL_ENUMERATION_in_atomic715 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_individual_in_atomic727 = new BitSet(new long[]{0x0200000000000008L});
    public static final BitSet FOLLOW_SOME_RESTRICTION_in_restriction765 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertyExpression_in_restriction767 = new BitSet(new long[]{0x0400006184400010L,0x000180C000002300L});
    public static final BitSet FOLLOW_disjunction_in_restriction769 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALL_RESTRICTION_in_restriction780 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertyExpression_in_restriction782 = new BitSet(new long[]{0x0400006184400010L,0x000180C000002300L});
    public static final BitSet FOLLOW_disjunction_in_restriction784 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_RESTRICTION_in_restriction795 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_objectPropertyExpression_in_restriction797 = new BitSet(new long[]{0x0200000000000008L});
    public static final BitSet FOLLOW_individual_in_restriction799 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VALUE_RESTRICTION_in_restriction810 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_dataPropertyIRI_in_restriction812 = new BitSet(new long[]{0x0000000000000000L,0x00000000000000FFL});
    public static final BitSet FOLLOW_literal_in_restriction814 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SELF_RESTRICTION_in_restriction825 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_objectPropertyExpression_in_restriction827 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MIN_NUMBER_RESTRICTION_in_restriction838 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertyExpression_in_restriction840 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_restriction844 = new BitSet(new long[]{0x0400006184400018L,0x000180C000002300L});
    public static final BitSet FOLLOW_disjunction_in_restriction855 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MAX_NUMBER_RESTRICTION_in_restriction878 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertyExpression_in_restriction880 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_restriction884 = new BitSet(new long[]{0x0400006184400018L,0x000180C000002300L});
    public static final BitSet FOLLOW_disjunction_in_restriction895 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EXACT_NUMBER_RESTRICTION_in_restriction918 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertyExpression_in_restriction920 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_restriction924 = new BitSet(new long[]{0x0400006184400018L,0x000180C000002300L});
    public static final BitSet FOLLOW_disjunction_in_restriction935 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_QUERY_in_query978 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_prologue_in_query983 = new BitSet(new long[]{0x0000001008000040L,0x0000002000000000L});
    public static final BitSet FOLLOW_selectQuery_in_query991 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_constructQuery_in_query999 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_describeQuery_in_query1007 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_askQuery_in_query1015 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EOF_in_query1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_baseDecl_in_prologue1048 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_prefixDecl_in_prologue1067 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_BASE_DECL_in_baseDecl1096 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IRI_REF_TERM_in_baseDecl1100 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREFIX_DECL_in_prefixDecl1124 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_NS_in_prefixDecl1128 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_IRI_REF_TERM_in_prefixDecl1132 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SELECT_in_selectQuery1157 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_selectModifier_in_selectQuery1159 = new BitSet(new long[]{0x0000000000000020L,0x0004000000000000L});
    public static final BitSet FOLLOW_selectVariableList_in_selectQuery1163 = new BitSet(new long[]{0x0000000040000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_datasets_in_selectQuery1166 = new BitSet(new long[]{0x0000000040000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_whereClause_in_selectQuery1170 = new BitSet(new long[]{0x8000000000000008L,0x0000000000500000L});
    public static final BitSet FOLLOW_solutionModifier_in_selectQuery1173 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_MODIFIER_DISTINCT_in_selectModifier1187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODIFIER_REDUCED_in_selectModifier1196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARS_in_selectVariableList1213 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_selectVariableList1220 = new BitSet(new long[]{0x0000000000000008L,0x0002000000000000L});
    public static final BitSet FOLLOW_ALL_VARS_in_selectVariableList1243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONSTRUCT_in_constructQuery1269 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constructTemplate_in_constructQuery1271 = new BitSet(new long[]{0x0000000040000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_datasets_in_constructQuery1273 = new BitSet(new long[]{0x0000000040000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_whereClause_in_constructQuery1277 = new BitSet(new long[]{0x8000000000000008L,0x0000000000500000L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery1280 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DESCRIBE_in_describeQuery1307 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_describeTargets_in_describeQuery1309 = new BitSet(new long[]{0x8000000040000008L,0x0040000000500000L});
    public static final BitSet FOLLOW_datasets_in_describeQuery1312 = new BitSet(new long[]{0x8000000040000008L,0x0040000000500000L});
    public static final BitSet FOLLOW_whereClause_in_describeQuery1316 = new BitSet(new long[]{0x8000000000000008L,0x0000000000500000L});
    public static final BitSet FOLLOW_solutionModifier_in_describeQuery1320 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VARS_OR_IRIS_in_describeTargets1335 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref_in_describeTargets1341 = new BitSet(new long[]{0x4000000000000008L,0x0002000008000000L});
    public static final BitSet FOLLOW_ALL_VARS_in_describeTargets1363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASK_in_askQuery1388 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasets_in_askQuery1390 = new BitSet(new long[]{0x0000000040000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_whereClause_in_askQuery1394 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DATASETS_in_datasets1411 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_datasetClause_in_datasets1413 = new BitSet(new long[]{0x0000000400000008L,0x0000000000001000L});
    public static final BitSet FOLLOW_defaultGraphClause_in_datasetClause1430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_namedGraphClause_in_datasetClause1436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_GRAPH_in_defaultGraphClause1452 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_sourceSelector_in_defaultGraphClause1456 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NAMED_GRAPH_in_namedGraphClause1477 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_sourceSelector_in_namedGraphClause1481 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iriRef_in_sourceSelector1505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_CLAUSE_in_whereClause1524 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_whereClause1526 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_orderClause_in_solutionModifier1546 = new BitSet(new long[]{0x8000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_limitOffsetClauses_in_solutionModifier1550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses1566 = new BitSet(new long[]{0x8000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses1573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses1583 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses1590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_CLAUSE_in_orderClause1610 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_orderCondition_in_orderClause1612 = new BitSet(new long[]{0x0000000000000008L,0x0000000003800000L});
    public static final BitSet FOLLOW_ORDER_CONDITION_ASC_in_orderCondition1630 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_orderCondition1632 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ORDER_CONDITION_DESC_in_orderCondition1643 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_orderCondition1645 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ORDER_CONDITION_UNDEF_in_orderCondition1656 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_orderCondition1658 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LIMIT_CLAUSE_in_limitClause1682 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_limitClause1686 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OFFSET_CLAUSE_in_offsetClause1710 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_offsetClause1714 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GROUP_GRAPH_PATTERN_in_groupGraphPattern1744 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesBlock_in_groupGraphPattern1753 = new BitSet(new long[]{0x0142000000000008L,0x0000400000200000L});
    public static final BitSet FOLLOW_graphPatternNotTriples_in_groupGraphPattern1771 = new BitSet(new long[]{0x0142000000000008L,0x0000440000200000L});
    public static final BitSet FOLLOW_filter_in_groupGraphPattern1781 = new BitSet(new long[]{0x0142000000000008L,0x0000440000200000L});
    public static final BitSet FOLLOW_triplesBlock_in_groupGraphPattern1799 = new BitSet(new long[]{0x0142000000000008L,0x0000400000200000L});
    public static final BitSet FOLLOW_triplesSameSubject_in_triplesBlock1838 = new BitSet(new long[]{0x0000000000000002L,0x0000040000000000L});
    public static final BitSet FOLLOW_optionalGraphPattern_in_graphPatternNotTriples1858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_groupOrUnionGraphPattern_in_graphPatternNotTriples1865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphGraphPattern_in_graphPatternNotTriples1872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONAL_GRAPH_PATTERN_in_optionalGraphPattern1893 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupGraphPattern_in_optionalGraphPattern1895 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GRAPH_GRAPH_PATTERN_in_graphGraphPattern1919 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GRAPH_IDENTIFIER_in_graphGraphPattern1922 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref_in_graphGraphPattern1924 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupGraphPattern_in_graphGraphPattern1927 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern1950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNION_GRAPH_PATTERN_in_groupOrUnionGraphPattern1958 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_groupOrUnionGraphPattern_in_groupOrUnionGraphPattern1962 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern1966 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FILTER_in_filter1990 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constraint_in_filter1992 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_constraint2015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_CALL_in_functionCall2036 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_FUNCTION_IDENTIFIER_in_functionCall2039 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_iriRef_in_functionCall2041 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_ARGS_in_functionCall2045 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_argList_in_functionCall2047 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_argList2079 = new BitSet(new long[]{0x40100000033FFC0AL,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_CONSTRUCT_TEMPLATE_in_constructTemplate2109 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_constructTriples_in_constructTemplate2111 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_triplesSameSubject_in_constructTriples2133 = new BitSet(new long[]{0x0000000000000002L,0x0000040000000000L});
    public static final BitSet FOLLOW_SUBJECT_TRIPLE_GROUP_in_triplesSameSubject2156 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SUBJECT_in_triplesSameSubject2166 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrTerm_in_triplesSameSubject2176 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_disjunction_in_triplesSameSubject2195 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_triplesSameSubject2228 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SUBJECT_in_triplesSameSubject2246 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_triplesNode_in_triplesSameSubject2248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_triplesSameSubject2267 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VERB_PAIR_GROUP_in_propertyListNotEmpty2328 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_verb_in_propertyListNotEmpty2330 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_objectList_in_propertyListNotEmpty2332 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_object_in_objectList2369 = new BitSet(new long[]{0x0000000000000002L,0x0000000000040000L});
    public static final BitSet FOLLOW_OBJECT_in_object2398 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_object2400 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OBJECT_in_object2411 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_disjunction_in_object2413 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VERB_in_verb2439 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_varOrIRIref_in_verb2441 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VERB_in_verb2450 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RDF_TYPE_in_verb2452 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_collection_in_triplesNode2473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blankNodePropertyList_in_triplesNode2482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BNODE_PROPERTY_LIST_in_blankNodePropertyList2501 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_blankNodePropertyList2505 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_COLLECTION_in_collection2529 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_graphNode_in_collection2541 = new BitSet(new long[]{0x4000000000800308L,0x00020000080000FFL});
    public static final BitSet FOLLOW_COLLECTION_in_emptyCollection2580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrTerm_in_graphNode2600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_graphNode2609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrTerm2631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_varOrTerm2640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref2662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_varOrIRIref2669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIABLE_in_var2690 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_VAR1_in_var2695 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR2_in_var2699 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iriRef_in_graphTerm2723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_graphTerm2732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blankNode_in_graphTerm2741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollection_in_graphTerm2750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_expression2773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_expression2782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueLogical_in_expression2791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONDITIONAL_EXPRESSION_OR_in_conditionalOrExpression2814 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalOrExpression2818 = new BitSet(new long[]{0x40100000033FFC08L,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_expression_in_conditionalOrExpression2822 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CONDITIONAL_EXPRESSION_AND_in_conditionalAndExpression2846 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalAndExpression2850 = new BitSet(new long[]{0x40100000033FFC08L,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_expression_in_conditionalAndExpression2854 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_relationalExpression_in_valueLogical2877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RELATIONAL_EQUAL_in_relationalExpression2909 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2913 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2917 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RELATIONAL_NOT_EQUAL_in_relationalExpression2928 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2932 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2936 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RELATIONAL_LESS_in_relationalExpression2947 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2951 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2955 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RELATIONAL_GREATER_in_relationalExpression2966 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2970 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2974 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RELATIONAL_LESS_EQUAL_in_relationalExpression2985 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2989 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression2993 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RELATIONAL_GREATER_EQUAL_in_relationalExpression3004 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3008 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3012 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_additiveExpression_in_numericExpression3035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_numericExpression3044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_numericExpression3053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_EXPRESSION_ADD_in_additiveExpression3076 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_additiveExpression3080 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_additiveExpression3084 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMERIC_EXPRESSION_SUBTRACT_in_additiveExpression3095 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_additiveExpression3099 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_additiveExpression3103 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMERIC_EXPRESSION_MULTIPLY_in_multiplicativeExpression3127 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_multiplicativeExpression3131 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_multiplicativeExpression3135 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NUMERIC_EXPRESSION_DIVIDE_in_multiplicativeExpression3147 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_numericExpression_in_multiplicativeExpression3151 = new BitSet(new long[]{0x40100000003FFC08L,0x000238000803C0FFL});
    public static final BitSet FOLLOW_numericExpression_in_multiplicativeExpression3155 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNARY_EXPRESSION_NOT_in_unaryExpression3180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3182 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNARY_EXPRESSION_POSITIVE_in_unaryExpression3193 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3195 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNARY_EXPRESSION_NEGATIVE_in_unaryExpression3206 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3208 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_primaryExpression3240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRefOrFunction_in_primaryExpression3247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primaryExpression3254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_primaryExpression3261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BUILTIN_STR_in_builtInCall3282 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3284 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_LANG_in_builtInCall3295 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3297 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_LANGMATCHES_in_builtInCall3308 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3312 = new BitSet(new long[]{0x40100000033FFC08L,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_expression_in_builtInCall3316 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_DATATYPE_in_builtInCall3327 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3329 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_BOUND_in_builtInCall3340 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_in_builtInCall3342 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_SAME_TERM_in_builtInCall3353 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3357 = new BitSet(new long[]{0x40100000033FFC08L,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_expression_in_builtInCall3361 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_IS_IRI_in_builtInCall3372 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3374 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_IS_URI_in_builtInCall3385 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3387 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_IS_BLANK_in_builtInCall3398 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3400 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_IS_LITERAL_in_builtInCall3411 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_builtInCall3413 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_regexExpression_in_builtInCall3423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BUILTIN_REGEX_BINARY_in_regexExpression3446 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_regexExpression3450 = new BitSet(new long[]{0x40100000033FFC08L,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_expression_in_regexExpression3454 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BUILTIN_REGEX_TERNARY_in_regexExpression3466 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_regexExpression3470 = new BitSet(new long[]{0x40100000033FFC08L,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_expression_in_regexExpression3474 = new BitSet(new long[]{0x40100000033FFC08L,0x0002381F8803C0FFL});
    public static final BitSet FOLLOW_expression_in_regexExpression3478 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_iriRef_in_iriRefOrFunction3501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_iriRefOrFunction3508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_PLAIN_in_rdfLiteral3530 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_in_rdfLiteral3532 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LITERAL_LANG_in_rdfLiteral3543 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_in_rdfLiteral3545 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LANGTAG_in_rdfLiteral3549 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LITERAL_TYPED_in_rdfLiteral3560 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_string_in_rdfLiteral3562 = new BitSet(new long[]{0x4000000000000008L,0x0002000008000000L});
    public static final BitSet FOLLOW_iriRef_in_rdfLiteral3564 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LITERAL_INTEGER_in_numericLiteral3596 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INTEGER_in_numericLiteral3604 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_INTEGER_in_numericLiteral3614 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEGATIVE_INTEGER_in_numericLiteral3624 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LITERAL_DECIMAL_in_numericLiteral3649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DECIMAL_in_numericLiteral3657 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_DECIMAL_in_numericLiteral3667 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEGATIVE_DECIMAL_in_numericLiteral3677 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LITERAL_DOUBLE_in_numericLiteral3702 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteral3710 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_DOUBLE_in_numericLiteral3720 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEGATIVE_DOUBLE_in_numericLiteral3730 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LITERAL_BOOLEAN_TRUE_in_booleanLiteral3774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LITERAL_BOOLEAN_FALSE_in_booleanLiteral3781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL1_in_string3805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL2_in_string3815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG1_in_string3825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_LONG2_in_string3835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IRI_REF_in_iriRef3865 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IRI_REF_TERM_in_iriRef3869 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PREFIXED_NAME_in_iriRef3880 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PNAME_LN_in_iriRef3886 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PNAME_NS_in_iriRef3892 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLANK_NODE_in_blankNode3919 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BLANK_NODE_LABEL_in_blankNode3923 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLANK_NODE_in_blankNode3933 = new BitSet(new long[]{0x0000000000000002L});

}