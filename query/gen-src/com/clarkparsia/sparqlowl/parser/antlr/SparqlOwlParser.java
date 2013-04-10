// $ANTLR 3.2 Sep 23, 2009 12:02:23 /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g 2010-03-28 22:46:17

package com.clarkparsia.sparqlowl.parser.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

public class SparqlOwlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALL_RESTRICTION", "ALL_VARS", "ASK", "BASE_DECL", "BLANK_NODE", "BNODE_PROPERTY_LIST", "BUILTIN_BOUND", "BUILTIN_DATATYPE", "BUILTIN_IS_BLANK", "BUILTIN_IS_IRI", "BUILTIN_IS_LITERAL", "BUILTIN_IS_URI", "BUILTIN_LANG", "BUILTIN_LANGMATCHES", "BUILTIN_REGEX_BINARY", "BUILTIN_REGEX_TERNARY", "BUILTIN_SAME_TERM", "BUILTIN_STR", "CLASS_OR_DATATYPE", "COLLECTION", "CONDITIONAL_EXPRESSION_AND", "CONDITIONAL_EXPRESSION_OR", "CONJUNCTION", "CONSTRUCT", "CONSTRUCT_TEMPLATE", "DATA_PROPERTY", "DATASETS", "DATATYPE", "DATATYPE_RESTRICTION", "DATATYPE_TERM", "DEFAULT_GRAPH", "DECIMAL_TERM", "DESCRIBE", "DISJUNCTION", "EXACT_NUMBER_RESTRICTION", "FACET_GREATER", "FACET_GREATER_EQUAL", "FACET_LANGPATTERN", "FACET_LENGTH", "FACET_LESS", "FACET_LESS_EQUAL", "FACET_MAXLENGTH", "FACET_MINLENGTH", "FACET_PATTERN", "FACET_VALUE", "FILTER", "FLOAT_TERM", "FUNCTION_ARGS", "FUNCTION_CALL", "FUNCTION_IDENTIFIER", "GRAPH_GRAPH_PATTERN", "GRAPH_IDENTIFIER", "GROUP_GRAPH_PATTERN", "INDIVIDUAL", "INDIVIDUAL_ENUMERATION", "INTEGER_TERM", "INVERSE_OBJECT_PROPERTY", "INVERSE_PROPERTY", "IRI_REF", "LIMIT_CLAUSE", "LITERAL_BOOLEAN_FALSE", "LITERAL_BOOLEAN_TRUE", "LITERAL_DECIMAL", "LITERAL_DOUBLE", "LITERAL_INTEGER", "LITERAL_LANG", "LITERAL_PLAIN", "LITERAL_TYPED", "MAX_NUMBER_RESTRICTION", "MIN_NUMBER_RESTRICTION", "MODIFIER_DISTINCT", "MODIFIER_REDUCED", "NAMED_GRAPH", "NEGATION", "NUMERIC_EXPRESSION_ADD", "NUMERIC_EXPRESSION_DIVIDE", "NUMERIC_EXPRESSION_MULTIPLY", "NUMERIC_EXPRESSION_SUBTRACT", "OBJECT", "OBJECT_PROPERTY", "OFFSET_CLAUSE", "OPTIONAL_GRAPH_PATTERN", "ORDER_CLAUSE", "ORDER_CONDITION_ASC", "ORDER_CONDITION_DESC", "ORDER_CONDITION_UNDEF", "PREFIX_DECL", "PREFIXED_NAME", "PROPERTY", "QUERY", "RDF_TYPE", "RELATIONAL_EQUAL", "RELATIONAL_GREATER", "RELATIONAL_GREATER_EQUAL", "RELATIONAL_LESS", "RELATIONAL_LESS_EQUAL", "RELATIONAL_NOT_EQUAL", "SELECT", "SELF_RESTRICTION", "SOME_RESTRICTION", "STRING_TERM", "SUBJECT", "SUBJECT_TRIPLE_GROUP", "UNARY_EXPRESSION_NEGATIVE", "UNARY_EXPRESSION_NOT", "UNARY_EXPRESSION_POSITIVE", "UNION_GRAPH_PATTERN", "VALUE_ENUMERATION", "VALUE_RESTRICTION", "VARIABLE", "VARS", "VARS_OR_IRIS", "VERB", "VERB_PAIR_GROUP", "WHERE_CLAUSE", "INVERSE_TERM", "OPEN_SQUARE_BRACE", "COMMA_TERM", "CLOSE_SQUARE_BRACE", "LENGTH_TERM", "MINLENGTH_TERM", "MAXLENGTH_TERM", "PATTERN_TERM", "LANGPATTERN_TERM", "LESS_EQUAL_TERM", "LESS_TERM", "GREATER_EQUAL_TERM", "GREATER_TERM", "OR_TERM", "AND_TERM", "NOT_TERM", "OPEN_CURLY_BRACE", "CLOSE_CURLY_BRACE", "OPEN_BRACE", "CLOSE_BRACE", "SOME_TERM", "ONLY_TERM", "VALUE_TERM", "SELF_TERM", "MIN_TERM", "INTEGER", "MAX_TERM", "EXACTLY_TERM", "BASE_TERM", "IRI_REF_TERM", "PREFIX_TERM", "PNAME_NS", "SELECT_TERM", "DISTINCT_TERM", "REDUCED_TERM", "ASTERISK_TERM", "CONSTRUCT_TERM", "DESCRIBE_TERM", "ASK_TERM", "FROM_TERM", "NAMED_TERM", "WHERE_TERM", "ORDER_TERM", "BY_TERM", "ASC_TERM", "DESC_TERM", "LIMIT_TERM", "OFFSET_TERM", "DOT_TERM", "OPTIONAL_TERM", "GRAPH_TERM", "UNION_TERM", "FILTER_TERM", "SEMICOLON_TERM", "A_TERM", "VAR1", "VAR2", "OR_OPERATOR_TERM", "AND_OPERATOR_TERM", "EQUAL_TERM", "NOT_EQUAL_TERM", "PLUS_TERM", "MINUS_TERM", "DIVIDE_TERM", "UNARY_NOT_TERM", "STR_TERM", "LANG_TERM", "LANGMATCHES_TERM", "BOUND_TERM", "SAMETERM_TERM", "ISIRI_TERM", "ISURI_TERM", "ISBLANK_TERM", "ISLITERAL_TERM", "REGEX_TERM", "LANGTAG", "DOUBLE_CARAT_TERM", "DECIMAL", "DOUBLE", "INTEGER_POSITIVE", "DECIMAL_POSITIVE", "DOUBLE_POSITIVE", "INTEGER_NEGATIVE", "DECIMAL_NEGATIVE", "DOUBLE_NEGATIVE", "TRUE_TERM", "FALSE_TERM", "STRING_LITERAL1", "STRING_LITERAL2", "STRING_LITERAL_LONG1", "STRING_LITERAL_LONG2", "PNAME_LN", "BLANK_NODE_LABEL", "EOL", "WS", "PN_PREFIX", "PN_LOCAL", "THAT_TERM", "VARNAME", "ALPHA", "ALPHANUM", "DIGIT", "EXPONENT", "ECHAR", "PN_CHARS_BASE", "PN_CHARS_U", "PN_CHARS", "COMMENT", "ANY"
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
    public static final int PNAME_LN=210;
    public static final int FILTER_TERM=171;
    public static final int CONSTRUCT=27;
    public static final int ONLY_TERM=140;
    public static final int EOF=-1;
    public static final int IRI_REF_TERM=148;
    public static final int ASTERISK_TERM=154;
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
    public static final int STRING_TERM=104;
    public static final int PLUS_TERM=180;
    public static final int LITERAL_BOOLEAN_FALSE=64;
    public static final int INDIVIDUAL_ENUMERATION=58;
    public static final int WS=213;
    public static final int NAMED_GRAPH=76;
    public static final int OPTIONAL_TERM=168;
    public static final int ORDER_CONDITION_DESC=88;
    public static final int FACET_MINLENGTH=46;
    public static final int BUILTIN_IS_LITERAL=14;
    public static final int OPTIONAL_GRAPH_PATTERN=85;
    public static final int AND_OPERATOR_TERM=177;
    public static final int INTEGER_POSITIVE=198;
    public static final int DESCRIBE=36;
    public static final int PN_CHARS=225;
    public static final int DATATYPE=31;
    public static final int GROUP_GRAPH_PATTERN=56;
    public static final int FACET_LESS_EQUAL=44;
    public static final int DOUBLE_NEGATIVE=203;
    public static final int FUNCTION_CALL=52;
    public static final int MINLENGTH_TERM=124;
    public static final int BUILTIN_BOUND=10;
    public static final int LIMIT_CLAUSE=63;
    public static final int SUBJECT=105;
    public static final int IRI_REF=62;
    public static final int FACET_PATTERN=47;
    public static final int LESS_EQUAL_TERM=128;
    public static final int FACET_LANGPATTERN=41;
    public static final int LANGMATCHES_TERM=186;
    public static final int FUNCTION_IDENTIFIER=53;
    public static final int GRAPH_IDENTIFIER=55;
    public static final int LITERAL_DOUBLE=67;
    public static final int BUILTIN_SAME_TERM=20;
    public static final int WHERE_CLAUSE=118;
    public static final int GRAPH_GRAPH_PATTERN=54;
    public static final int OFFSET_CLAUSE=84;
    public static final int DECIMAL_POSITIVE=199;
    public static final int FACET_GREATER_EQUAL=40;
    public static final int MIN_TERM=143;
    public static final int LIMIT_TERM=165;
    public static final int EQUAL_TERM=178;
    public static final int REDUCED_TERM=153;
    public static final int CONSTRUCT_TERM=155;
    public static final int SOME_RESTRICTION=103;
    public static final int ISURI_TERM=190;
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
    public static final int DATATYPE_RESTRICTION=32;
    public static final int VALUE_TERM=141;
    public static final int SELF_TERM=142;
    public static final int ORDER_TERM=161;
    public static final int RDF_TYPE=94;
    public static final int LANGTAG=194;
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
    public static final int DEFAULT_GRAPH=34;
    public static final int UNARY_EXPRESSION_POSITIVE=109;
    public static final int NOT_TERM=134;
    public static final int RELATIONAL_GREATER_EQUAL=97;
    public static final int MINUS_TERM=181;
    public static final int VERB_PAIR_GROUP=117;
    public static final int OR_TERM=132;
    public static final int VARS=114;
    public static final int INDIVIDUAL=57;
    public static final int DISTINCT_TERM=152;
    public static final int FROM_TERM=158;
    public static final int INTEGER_NEGATIVE=201;
    public static final int DATASETS=30;
    public static final int PN_LOCAL=215;
    public static final int ASC_TERM=163;
    public static final int DECIMAL_TERM=35;
    public static final int OPEN_SQUARE_BRACE=120;
    public static final int ECHAR=222;
    public static final int ORDER_CONDITION_UNDEF=89;
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
    public static final int NUMERIC_EXPRESSION_SUBTRACT=81;
    public static final int OPEN_BRACE=137;
    public static final int LENGTH_TERM=123;

    // delegates
    // delegators


        public SparqlOwlParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public SparqlOwlParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[109+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return SparqlOwlParser.tokenNames; }
    public String getGrammarFileName() { return "/home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g"; }


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


    public static class objectPropertyIRI_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "objectPropertyIRI"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:179:1: objectPropertyIRI : iriRef -> ^( OBJECT_PROPERTY iriRef ) ;
    public final SparqlOwlParser.objectPropertyIRI_return objectPropertyIRI() throws RecognitionException {
        SparqlOwlParser.objectPropertyIRI_return retval = new SparqlOwlParser.objectPropertyIRI_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef1 = null;


        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:180:2: ( iriRef -> ^( OBJECT_PROPERTY iriRef ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:180:4: iriRef
            {
            pushFollow(FOLLOW_iriRef_in_objectPropertyIRI542);
            iriRef1=iriRef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_iriRef.add(iriRef1.getTree());


            // AST REWRITE
            // elements: iriRef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 181:2: -> ^( OBJECT_PROPERTY iriRef )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:181:5: ^( OBJECT_PROPERTY iriRef )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OBJECT_PROPERTY, "OBJECT_PROPERTY"), root_1);

                adaptor.addChild(root_1, stream_iriRef.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class dataPropertyIRI_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dataPropertyIRI"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:187:1: dataPropertyIRI : iriRef -> ^( DATA_PROPERTY iriRef ) ;
    public final SparqlOwlParser.dataPropertyIRI_return dataPropertyIRI() throws RecognitionException {
        SparqlOwlParser.dataPropertyIRI_return retval = new SparqlOwlParser.dataPropertyIRI_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef2 = null;


        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:188:2: ( iriRef -> ^( DATA_PROPERTY iriRef ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:188:4: iriRef
            {
            pushFollow(FOLLOW_iriRef_in_dataPropertyIRI564);
            iriRef2=iriRef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_iriRef.add(iriRef2.getTree());


            // AST REWRITE
            // elements: iriRef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 189:2: -> ^( DATA_PROPERTY iriRef )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:189:5: ^( DATA_PROPERTY iriRef )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATA_PROPERTY, "DATA_PROPERTY"), root_1);

                adaptor.addChild(root_1, stream_iriRef.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class objectOrDataPropertyIRI_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "objectOrDataPropertyIRI"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:195:1: objectOrDataPropertyIRI : iriRef -> ^( PROPERTY iriRef ) ;
    public final SparqlOwlParser.objectOrDataPropertyIRI_return objectOrDataPropertyIRI() throws RecognitionException {
        SparqlOwlParser.objectOrDataPropertyIRI_return retval = new SparqlOwlParser.objectOrDataPropertyIRI_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef3 = null;


        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:196:2: ( iriRef -> ^( PROPERTY iriRef ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:196:4: iriRef
            {
            pushFollow(FOLLOW_iriRef_in_objectOrDataPropertyIRI586);
            iriRef3=iriRef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_iriRef.add(iriRef3.getTree());


            // AST REWRITE
            // elements: iriRef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 197:2: -> ^( PROPERTY iriRef )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:197:5: ^( PROPERTY iriRef )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PROPERTY, "PROPERTY"), root_1);

                adaptor.addChild(root_1, stream_iriRef.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "objectOrDataPropertyIRI"

    public static class inverseObjectProperty_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inverseObjectProperty"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:203:1: inverseObjectProperty : INVERSE_TERM objectPropertyIRI -> ^( INVERSE_PROPERTY objectPropertyIRI ) ;
    public final SparqlOwlParser.inverseObjectProperty_return inverseObjectProperty() throws RecognitionException {
        SparqlOwlParser.inverseObjectProperty_return retval = new SparqlOwlParser.inverseObjectProperty_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INVERSE_TERM4=null;
        SparqlOwlParser.objectPropertyIRI_return objectPropertyIRI5 = null;


        CommonTree INVERSE_TERM4_tree=null;
        RewriteRuleTokenStream stream_INVERSE_TERM=new RewriteRuleTokenStream(adaptor,"token INVERSE_TERM");
        RewriteRuleSubtreeStream stream_objectPropertyIRI=new RewriteRuleSubtreeStream(adaptor,"rule objectPropertyIRI");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:204:2: ( INVERSE_TERM objectPropertyIRI -> ^( INVERSE_PROPERTY objectPropertyIRI ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:204:4: INVERSE_TERM objectPropertyIRI
            {
            INVERSE_TERM4=(Token)match(input,INVERSE_TERM,FOLLOW_INVERSE_TERM_in_inverseObjectProperty608); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INVERSE_TERM.add(INVERSE_TERM4);

            pushFollow(FOLLOW_objectPropertyIRI_in_inverseObjectProperty610);
            objectPropertyIRI5=objectPropertyIRI();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_objectPropertyIRI.add(objectPropertyIRI5.getTree());


            // AST REWRITE
            // elements: objectPropertyIRI
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 205:2: -> ^( INVERSE_PROPERTY objectPropertyIRI )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:205:5: ^( INVERSE_PROPERTY objectPropertyIRI )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INVERSE_PROPERTY, "INVERSE_PROPERTY"), root_1);

                adaptor.addChild(root_1, stream_objectPropertyIRI.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class propertyExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "propertyExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:211:1: propertyExpression : ( inverseObjectProperty | objectOrDataPropertyIRI );
    public final SparqlOwlParser.propertyExpression_return propertyExpression() throws RecognitionException {
        SparqlOwlParser.propertyExpression_return retval = new SparqlOwlParser.propertyExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.inverseObjectProperty_return inverseObjectProperty6 = null;

        SparqlOwlParser.objectOrDataPropertyIRI_return objectOrDataPropertyIRI7 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:212:2: ( inverseObjectProperty | objectOrDataPropertyIRI )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==INVERSE_TERM) ) {
                alt1=1;
            }
            else if ( (LA1_0==IRI_REF_TERM||LA1_0==PNAME_NS||LA1_0==PNAME_LN) ) {
                alt1=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:212:4: inverseObjectProperty
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_inverseObjectProperty_in_propertyExpression632);
                    inverseObjectProperty6=inverseObjectProperty();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inverseObjectProperty6.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:213:4: objectOrDataPropertyIRI
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_objectOrDataPropertyIRI_in_propertyExpression637);
                    objectOrDataPropertyIRI7=objectOrDataPropertyIRI();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, objectOrDataPropertyIRI7.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class objectPropertyExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "objectPropertyExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:219:1: objectPropertyExpression : ( inverseObjectProperty | objectPropertyIRI );
    public final SparqlOwlParser.objectPropertyExpression_return objectPropertyExpression() throws RecognitionException {
        SparqlOwlParser.objectPropertyExpression_return retval = new SparqlOwlParser.objectPropertyExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.inverseObjectProperty_return inverseObjectProperty8 = null;

        SparqlOwlParser.objectPropertyIRI_return objectPropertyIRI9 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:220:2: ( inverseObjectProperty | objectPropertyIRI )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==INVERSE_TERM) ) {
                alt2=1;
            }
            else if ( (LA2_0==IRI_REF_TERM||LA2_0==PNAME_NS||LA2_0==PNAME_LN) ) {
                alt2=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:220:4: inverseObjectProperty
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_inverseObjectProperty_in_objectPropertyExpression650);
                    inverseObjectProperty8=inverseObjectProperty();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inverseObjectProperty8.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:221:4: objectPropertyIRI
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_objectPropertyIRI_in_objectPropertyExpression655);
                    objectPropertyIRI9=objectPropertyIRI();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, objectPropertyIRI9.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class datatype_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "datatype"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:227:1: datatype : ( iriRef -> ^( DATATYPE iriRef ) | INTEGER_TERM -> ^( DATATYPE INTEGER_TERM ) | DECIMAL_TERM -> ^( DATATYPE DECIMAL_TERM ) | FLOAT_TERM -> ^( DATATYPE FLOAT_TERM ) | STRING_TERM -> ^( DATATYPE STRING_TERM ) );
    public final SparqlOwlParser.datatype_return datatype() throws RecognitionException {
        SparqlOwlParser.datatype_return retval = new SparqlOwlParser.datatype_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INTEGER_TERM11=null;
        Token DECIMAL_TERM12=null;
        Token FLOAT_TERM13=null;
        Token STRING_TERM14=null;
        SparqlOwlParser.iriRef_return iriRef10 = null;


        CommonTree INTEGER_TERM11_tree=null;
        CommonTree DECIMAL_TERM12_tree=null;
        CommonTree FLOAT_TERM13_tree=null;
        CommonTree STRING_TERM14_tree=null;
        RewriteRuleTokenStream stream_INTEGER_TERM=new RewriteRuleTokenStream(adaptor,"token INTEGER_TERM");
        RewriteRuleTokenStream stream_STRING_TERM=new RewriteRuleTokenStream(adaptor,"token STRING_TERM");
        RewriteRuleTokenStream stream_FLOAT_TERM=new RewriteRuleTokenStream(adaptor,"token FLOAT_TERM");
        RewriteRuleTokenStream stream_DECIMAL_TERM=new RewriteRuleTokenStream(adaptor,"token DECIMAL_TERM");
        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:228:2: ( iriRef -> ^( DATATYPE iriRef ) | INTEGER_TERM -> ^( DATATYPE INTEGER_TERM ) | DECIMAL_TERM -> ^( DATATYPE DECIMAL_TERM ) | FLOAT_TERM -> ^( DATATYPE FLOAT_TERM ) | STRING_TERM -> ^( DATATYPE STRING_TERM ) )
            int alt3=5;
            switch ( input.LA(1) ) {
            case IRI_REF_TERM:
            case PNAME_NS:
            case PNAME_LN:
                {
                alt3=1;
                }
                break;
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
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:228:4: iriRef
                    {
                    pushFollow(FOLLOW_iriRef_in_datatype668);
                    iriRef10=iriRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_iriRef.add(iriRef10.getTree());


                    // AST REWRITE
                    // elements: iriRef
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 228:11: -> ^( DATATYPE iriRef )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:228:14: ^( DATATYPE iriRef )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_iriRef.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:229:4: INTEGER_TERM
                    {
                    INTEGER_TERM11=(Token)match(input,INTEGER_TERM,FOLLOW_INTEGER_TERM_in_datatype681); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_INTEGER_TERM.add(INTEGER_TERM11);



                    // AST REWRITE
                    // elements: INTEGER_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 229:17: -> ^( DATATYPE INTEGER_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:229:20: ^( DATATYPE INTEGER_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_INTEGER_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:230:4: DECIMAL_TERM
                    {
                    DECIMAL_TERM12=(Token)match(input,DECIMAL_TERM,FOLLOW_DECIMAL_TERM_in_datatype694); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DECIMAL_TERM.add(DECIMAL_TERM12);



                    // AST REWRITE
                    // elements: DECIMAL_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 230:17: -> ^( DATATYPE DECIMAL_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:230:20: ^( DATATYPE DECIMAL_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_DECIMAL_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:231:4: FLOAT_TERM
                    {
                    FLOAT_TERM13=(Token)match(input,FLOAT_TERM,FOLLOW_FLOAT_TERM_in_datatype707); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FLOAT_TERM.add(FLOAT_TERM13);



                    // AST REWRITE
                    // elements: FLOAT_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 231:15: -> ^( DATATYPE FLOAT_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:231:18: ^( DATATYPE FLOAT_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_FLOAT_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:232:4: STRING_TERM
                    {
                    STRING_TERM14=(Token)match(input,STRING_TERM,FOLLOW_STRING_TERM_in_datatype720); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_TERM.add(STRING_TERM14);



                    // AST REWRITE
                    // elements: STRING_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 232:16: -> ^( DATATYPE STRING_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:232:19: ^( DATATYPE STRING_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_STRING_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "datatype"

    public static class individual_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "individual"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:238:1: individual : iriRef -> ^( INDIVIDUAL iriRef ) ;
    public final SparqlOwlParser.individual_return individual() throws RecognitionException {
        SparqlOwlParser.individual_return retval = new SparqlOwlParser.individual_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef15 = null;


        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:239:2: ( iriRef -> ^( INDIVIDUAL iriRef ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:239:4: iriRef
            {
            pushFollow(FOLLOW_iriRef_in_individual741);
            iriRef15=iriRef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_iriRef.add(iriRef15.getTree());


            // AST REWRITE
            // elements: iriRef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 240:2: -> ^( INDIVIDUAL iriRef )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:241:3: ^( INDIVIDUAL iriRef )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INDIVIDUAL, "INDIVIDUAL"), root_1);

                adaptor.addChild(root_1, stream_iriRef.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:248:1: literal : ( rdfLiteral | numericLiteral | booleanLiteral );
    public final SparqlOwlParser.literal_return literal() throws RecognitionException {
        SparqlOwlParser.literal_return retval = new SparqlOwlParser.literal_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.rdfLiteral_return rdfLiteral16 = null;

        SparqlOwlParser.numericLiteral_return numericLiteral17 = null;

        SparqlOwlParser.booleanLiteral_return booleanLiteral18 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:249:2: ( rdfLiteral | numericLiteral | booleanLiteral )
            int alt4=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
                {
                alt4=1;
                }
                break;
            case INTEGER:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
                {
                alt4=2;
                }
                break;
            case TRUE_TERM:
            case FALSE_TERM:
                {
                alt4=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:249:4: rdfLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_rdfLiteral_in_literal765);
                    rdfLiteral16=rdfLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rdfLiteral16.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:250:4: numericLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_numericLiteral_in_literal770);
                    numericLiteral17=numericLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numericLiteral17.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:251:4: booleanLiteral
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_booleanLiteral_in_literal775);
                    booleanLiteral18=booleanLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, booleanLiteral18.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class datatypeRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "datatypeRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:257:1: datatypeRestriction : datatype OPEN_SQUARE_BRACE facet restrictionValue ( COMMA_TERM facet restrictionValue )* CLOSE_SQUARE_BRACE -> ^( DATATYPE_RESTRICTION datatype ( ^( FACET_VALUE facet restrictionValue ) )+ ) ;
    public final SparqlOwlParser.datatypeRestriction_return datatypeRestriction() throws RecognitionException {
        SparqlOwlParser.datatypeRestriction_return retval = new SparqlOwlParser.datatypeRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_SQUARE_BRACE20=null;
        Token COMMA_TERM23=null;
        Token CLOSE_SQUARE_BRACE26=null;
        SparqlOwlParser.datatype_return datatype19 = null;

        SparqlOwlParser.facet_return facet21 = null;

        SparqlOwlParser.restrictionValue_return restrictionValue22 = null;

        SparqlOwlParser.facet_return facet24 = null;

        SparqlOwlParser.restrictionValue_return restrictionValue25 = null;


        CommonTree OPEN_SQUARE_BRACE20_tree=null;
        CommonTree COMMA_TERM23_tree=null;
        CommonTree CLOSE_SQUARE_BRACE26_tree=null;
        RewriteRuleTokenStream stream_CLOSE_SQUARE_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_SQUARE_BRACE");
        RewriteRuleTokenStream stream_COMMA_TERM=new RewriteRuleTokenStream(adaptor,"token COMMA_TERM");
        RewriteRuleTokenStream stream_OPEN_SQUARE_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_SQUARE_BRACE");
        RewriteRuleSubtreeStream stream_facet=new RewriteRuleSubtreeStream(adaptor,"rule facet");
        RewriteRuleSubtreeStream stream_restrictionValue=new RewriteRuleSubtreeStream(adaptor,"rule restrictionValue");
        RewriteRuleSubtreeStream stream_datatype=new RewriteRuleSubtreeStream(adaptor,"rule datatype");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:258:2: ( datatype OPEN_SQUARE_BRACE facet restrictionValue ( COMMA_TERM facet restrictionValue )* CLOSE_SQUARE_BRACE -> ^( DATATYPE_RESTRICTION datatype ( ^( FACET_VALUE facet restrictionValue ) )+ ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:258:4: datatype OPEN_SQUARE_BRACE facet restrictionValue ( COMMA_TERM facet restrictionValue )* CLOSE_SQUARE_BRACE
            {
            pushFollow(FOLLOW_datatype_in_datatypeRestriction788);
            datatype19=datatype();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_datatype.add(datatype19.getTree());
            OPEN_SQUARE_BRACE20=(Token)match(input,OPEN_SQUARE_BRACE,FOLLOW_OPEN_SQUARE_BRACE_in_datatypeRestriction790); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_SQUARE_BRACE.add(OPEN_SQUARE_BRACE20);

            pushFollow(FOLLOW_facet_in_datatypeRestriction792);
            facet21=facet();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_facet.add(facet21.getTree());
            pushFollow(FOLLOW_restrictionValue_in_datatypeRestriction794);
            restrictionValue22=restrictionValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_restrictionValue.add(restrictionValue22.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:258:54: ( COMMA_TERM facet restrictionValue )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA_TERM) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:258:56: COMMA_TERM facet restrictionValue
            	    {
            	    COMMA_TERM23=(Token)match(input,COMMA_TERM,FOLLOW_COMMA_TERM_in_datatypeRestriction798); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA_TERM.add(COMMA_TERM23);

            	    pushFollow(FOLLOW_facet_in_datatypeRestriction800);
            	    facet24=facet();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_facet.add(facet24.getTree());
            	    pushFollow(FOLLOW_restrictionValue_in_datatypeRestriction802);
            	    restrictionValue25=restrictionValue();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_restrictionValue.add(restrictionValue25.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            CLOSE_SQUARE_BRACE26=(Token)match(input,CLOSE_SQUARE_BRACE,FOLLOW_CLOSE_SQUARE_BRACE_in_datatypeRestriction807); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_SQUARE_BRACE.add(CLOSE_SQUARE_BRACE26);



            // AST REWRITE
            // elements: datatype, restrictionValue, facet
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 259:2: -> ^( DATATYPE_RESTRICTION datatype ( ^( FACET_VALUE facet restrictionValue ) )+ )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:259:5: ^( DATATYPE_RESTRICTION datatype ( ^( FACET_VALUE facet restrictionValue ) )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE_RESTRICTION, "DATATYPE_RESTRICTION"), root_1);

                adaptor.addChild(root_1, stream_datatype.nextTree());
                if ( !(stream_restrictionValue.hasNext()||stream_facet.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_restrictionValue.hasNext()||stream_facet.hasNext() ) {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:259:37: ^( FACET_VALUE facet restrictionValue )
                    {
                    CommonTree root_2 = (CommonTree)adaptor.nil();
                    root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FACET_VALUE, "FACET_VALUE"), root_2);

                    adaptor.addChild(root_2, stream_facet.nextTree());
                    adaptor.addChild(root_2, stream_restrictionValue.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_restrictionValue.reset();
                stream_facet.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class facet_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "facet"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:265:1: facet : ( LENGTH_TERM -> FACET_LENGTH | MINLENGTH_TERM -> FACET_MINLENGTH | MAXLENGTH_TERM -> FACET_MAXLENGTH | PATTERN_TERM -> FACET_PATTERN | LANGPATTERN_TERM -> FACET_LANGPATTERN | LESS_EQUAL_TERM -> FACET_LESS_EQUAL | LESS_TERM -> FACET_LESS | GREATER_EQUAL_TERM -> FACET_GREATER_EQUAL | GREATER_TERM -> FACET_GREATER );
    public final SparqlOwlParser.facet_return facet() throws RecognitionException {
        SparqlOwlParser.facet_return retval = new SparqlOwlParser.facet_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LENGTH_TERM27=null;
        Token MINLENGTH_TERM28=null;
        Token MAXLENGTH_TERM29=null;
        Token PATTERN_TERM30=null;
        Token LANGPATTERN_TERM31=null;
        Token LESS_EQUAL_TERM32=null;
        Token LESS_TERM33=null;
        Token GREATER_EQUAL_TERM34=null;
        Token GREATER_TERM35=null;

        CommonTree LENGTH_TERM27_tree=null;
        CommonTree MINLENGTH_TERM28_tree=null;
        CommonTree MAXLENGTH_TERM29_tree=null;
        CommonTree PATTERN_TERM30_tree=null;
        CommonTree LANGPATTERN_TERM31_tree=null;
        CommonTree LESS_EQUAL_TERM32_tree=null;
        CommonTree LESS_TERM33_tree=null;
        CommonTree GREATER_EQUAL_TERM34_tree=null;
        CommonTree GREATER_TERM35_tree=null;
        RewriteRuleTokenStream stream_GREATER_TERM=new RewriteRuleTokenStream(adaptor,"token GREATER_TERM");
        RewriteRuleTokenStream stream_PATTERN_TERM=new RewriteRuleTokenStream(adaptor,"token PATTERN_TERM");
        RewriteRuleTokenStream stream_MAXLENGTH_TERM=new RewriteRuleTokenStream(adaptor,"token MAXLENGTH_TERM");
        RewriteRuleTokenStream stream_LESS_EQUAL_TERM=new RewriteRuleTokenStream(adaptor,"token LESS_EQUAL_TERM");
        RewriteRuleTokenStream stream_LESS_TERM=new RewriteRuleTokenStream(adaptor,"token LESS_TERM");
        RewriteRuleTokenStream stream_GREATER_EQUAL_TERM=new RewriteRuleTokenStream(adaptor,"token GREATER_EQUAL_TERM");
        RewriteRuleTokenStream stream_MINLENGTH_TERM=new RewriteRuleTokenStream(adaptor,"token MINLENGTH_TERM");
        RewriteRuleTokenStream stream_LANGPATTERN_TERM=new RewriteRuleTokenStream(adaptor,"token LANGPATTERN_TERM");
        RewriteRuleTokenStream stream_LENGTH_TERM=new RewriteRuleTokenStream(adaptor,"token LENGTH_TERM");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:266:2: ( LENGTH_TERM -> FACET_LENGTH | MINLENGTH_TERM -> FACET_MINLENGTH | MAXLENGTH_TERM -> FACET_MAXLENGTH | PATTERN_TERM -> FACET_PATTERN | LANGPATTERN_TERM -> FACET_LANGPATTERN | LESS_EQUAL_TERM -> FACET_LESS_EQUAL | LESS_TERM -> FACET_LESS | GREATER_EQUAL_TERM -> FACET_GREATER_EQUAL | GREATER_TERM -> FACET_GREATER )
            int alt6=9;
            switch ( input.LA(1) ) {
            case LENGTH_TERM:
                {
                alt6=1;
                }
                break;
            case MINLENGTH_TERM:
                {
                alt6=2;
                }
                break;
            case MAXLENGTH_TERM:
                {
                alt6=3;
                }
                break;
            case PATTERN_TERM:
                {
                alt6=4;
                }
                break;
            case LANGPATTERN_TERM:
                {
                alt6=5;
                }
                break;
            case LESS_EQUAL_TERM:
                {
                alt6=6;
                }
                break;
            case LESS_TERM:
                {
                alt6=7;
                }
                break;
            case GREATER_EQUAL_TERM:
                {
                alt6=8;
                }
                break;
            case GREATER_TERM:
                {
                alt6=9;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:266:4: LENGTH_TERM
                    {
                    LENGTH_TERM27=(Token)match(input,LENGTH_TERM,FOLLOW_LENGTH_TERM_in_facet839); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LENGTH_TERM.add(LENGTH_TERM27);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 266:16: -> FACET_LENGTH
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_LENGTH, "FACET_LENGTH"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:267:4: MINLENGTH_TERM
                    {
                    MINLENGTH_TERM28=(Token)match(input,MINLENGTH_TERM,FOLLOW_MINLENGTH_TERM_in_facet848); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINLENGTH_TERM.add(MINLENGTH_TERM28);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 267:19: -> FACET_MINLENGTH
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_MINLENGTH, "FACET_MINLENGTH"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:268:4: MAXLENGTH_TERM
                    {
                    MAXLENGTH_TERM29=(Token)match(input,MAXLENGTH_TERM,FOLLOW_MAXLENGTH_TERM_in_facet857); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MAXLENGTH_TERM.add(MAXLENGTH_TERM29);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 268:19: -> FACET_MAXLENGTH
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_MAXLENGTH, "FACET_MAXLENGTH"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:269:4: PATTERN_TERM
                    {
                    PATTERN_TERM30=(Token)match(input,PATTERN_TERM,FOLLOW_PATTERN_TERM_in_facet866); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PATTERN_TERM.add(PATTERN_TERM30);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 269:17: -> FACET_PATTERN
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_PATTERN, "FACET_PATTERN"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:270:4: LANGPATTERN_TERM
                    {
                    LANGPATTERN_TERM31=(Token)match(input,LANGPATTERN_TERM,FOLLOW_LANGPATTERN_TERM_in_facet875); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LANGPATTERN_TERM.add(LANGPATTERN_TERM31);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 270:21: -> FACET_LANGPATTERN
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_LANGPATTERN, "FACET_LANGPATTERN"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:271:4: LESS_EQUAL_TERM
                    {
                    LESS_EQUAL_TERM32=(Token)match(input,LESS_EQUAL_TERM,FOLLOW_LESS_EQUAL_TERM_in_facet884); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LESS_EQUAL_TERM.add(LESS_EQUAL_TERM32);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 271:20: -> FACET_LESS_EQUAL
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_LESS_EQUAL, "FACET_LESS_EQUAL"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:272:4: LESS_TERM
                    {
                    LESS_TERM33=(Token)match(input,LESS_TERM,FOLLOW_LESS_TERM_in_facet893); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LESS_TERM.add(LESS_TERM33);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 272:14: -> FACET_LESS
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_LESS, "FACET_LESS"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:273:4: GREATER_EQUAL_TERM
                    {
                    GREATER_EQUAL_TERM34=(Token)match(input,GREATER_EQUAL_TERM,FOLLOW_GREATER_EQUAL_TERM_in_facet902); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER_EQUAL_TERM.add(GREATER_EQUAL_TERM34);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 273:23: -> FACET_GREATER_EQUAL
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_GREATER_EQUAL, "FACET_GREATER_EQUAL"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 9 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:274:4: GREATER_TERM
                    {
                    GREATER_TERM35=(Token)match(input,GREATER_TERM,FOLLOW_GREATER_TERM_in_facet911); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER_TERM.add(GREATER_TERM35);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 274:17: -> FACET_GREATER
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FACET_GREATER, "FACET_GREATER"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "facet"

    public static class restrictionValue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "restrictionValue"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:280:1: restrictionValue : literal ;
    public final SparqlOwlParser.restrictionValue_return restrictionValue() throws RecognitionException {
        SparqlOwlParser.restrictionValue_return retval = new SparqlOwlParser.restrictionValue_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.literal_return literal36 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:281:2: ( literal )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:281:4: literal
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_literal_in_restrictionValue928);
            literal36=literal();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, literal36.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "restrictionValue"

    public static class disjunction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "disjunction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:287:1: disjunction : ( conjunction -> conjunction ) ( OR_TERM conjunction -> ^( DISJUNCTION $disjunction conjunction ) )* ;
    public final SparqlOwlParser.disjunction_return disjunction() throws RecognitionException {
        SparqlOwlParser.disjunction_return retval = new SparqlOwlParser.disjunction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OR_TERM38=null;
        SparqlOwlParser.conjunction_return conjunction37 = null;

        SparqlOwlParser.conjunction_return conjunction39 = null;


        CommonTree OR_TERM38_tree=null;
        RewriteRuleTokenStream stream_OR_TERM=new RewriteRuleTokenStream(adaptor,"token OR_TERM");
        RewriteRuleSubtreeStream stream_conjunction=new RewriteRuleSubtreeStream(adaptor,"rule conjunction");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:288:2: ( ( conjunction -> conjunction ) ( OR_TERM conjunction -> ^( DISJUNCTION $disjunction conjunction ) )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:288:4: ( conjunction -> conjunction ) ( OR_TERM conjunction -> ^( DISJUNCTION $disjunction conjunction ) )*
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:288:4: ( conjunction -> conjunction )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:288:6: conjunction
            {
            pushFollow(FOLLOW_conjunction_in_disjunction943);
            conjunction37=conjunction();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conjunction.add(conjunction37.getTree());


            // AST REWRITE
            // elements: conjunction
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 288:18: -> conjunction
            {
                adaptor.addChild(root_0, stream_conjunction.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:289:3: ( OR_TERM conjunction -> ^( DISJUNCTION $disjunction conjunction ) )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==OR_TERM) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:289:5: OR_TERM conjunction
            	    {
            	    OR_TERM38=(Token)match(input,OR_TERM,FOLLOW_OR_TERM_in_disjunction955); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_OR_TERM.add(OR_TERM38);

            	    pushFollow(FOLLOW_conjunction_in_disjunction957);
            	    conjunction39=conjunction();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_conjunction.add(conjunction39.getTree());


            	    // AST REWRITE
            	    // elements: disjunction, conjunction
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 289:25: -> ^( DISJUNCTION $disjunction conjunction )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:289:28: ^( DISJUNCTION $disjunction conjunction )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_conjunction.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class conjunction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conjunction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:295:1: conjunction : ( primary -> primary ) ( AND_TERM primary -> ^( CONJUNCTION $conjunction primary ) )* ;
    public final SparqlOwlParser.conjunction_return conjunction() throws RecognitionException {
        SparqlOwlParser.conjunction_return retval = new SparqlOwlParser.conjunction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token AND_TERM41=null;
        SparqlOwlParser.primary_return primary40 = null;

        SparqlOwlParser.primary_return primary42 = null;


        CommonTree AND_TERM41_tree=null;
        RewriteRuleTokenStream stream_AND_TERM=new RewriteRuleTokenStream(adaptor,"token AND_TERM");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:296:2: ( ( primary -> primary ) ( AND_TERM primary -> ^( CONJUNCTION $conjunction primary ) )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:296:4: ( primary -> primary ) ( AND_TERM primary -> ^( CONJUNCTION $conjunction primary ) )*
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:296:4: ( primary -> primary )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:296:6: primary
            {
            pushFollow(FOLLOW_primary_in_conjunction986);
            primary40=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary40.getTree());


            // AST REWRITE
            // elements: primary
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 296:14: -> primary
            {
                adaptor.addChild(root_0, stream_primary.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:297:3: ( AND_TERM primary -> ^( CONJUNCTION $conjunction primary ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==AND_TERM) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:297:5: AND_TERM primary
            	    {
            	    AND_TERM41=(Token)match(input,AND_TERM,FOLLOW_AND_TERM_in_conjunction998); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_AND_TERM.add(AND_TERM41);

            	    pushFollow(FOLLOW_primary_in_conjunction1000);
            	    primary42=primary();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_primary.add(primary42.getTree());


            	    // AST REWRITE
            	    // elements: conjunction, primary
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 297:22: -> ^( CONJUNCTION $conjunction primary )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:297:25: ^( CONJUNCTION $conjunction primary )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_primary.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:303:1: primary : ( NOT_TERM ( restriction | atomic ) -> ^( NEGATION ( restriction )? ( atomic )? ) | restriction | atomic );
    public final SparqlOwlParser.primary_return primary() throws RecognitionException {
        SparqlOwlParser.primary_return retval = new SparqlOwlParser.primary_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token NOT_TERM43=null;
        SparqlOwlParser.restriction_return restriction44 = null;

        SparqlOwlParser.atomic_return atomic45 = null;

        SparqlOwlParser.restriction_return restriction46 = null;

        SparqlOwlParser.atomic_return atomic47 = null;


        CommonTree NOT_TERM43_tree=null;
        RewriteRuleTokenStream stream_NOT_TERM=new RewriteRuleTokenStream(adaptor,"token NOT_TERM");
        RewriteRuleSubtreeStream stream_restriction=new RewriteRuleSubtreeStream(adaptor,"rule restriction");
        RewriteRuleSubtreeStream stream_atomic=new RewriteRuleSubtreeStream(adaptor,"rule atomic");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:2: ( NOT_TERM ( restriction | atomic ) -> ^( NEGATION ( restriction )? ( atomic )? ) | restriction | atomic )
            int alt10=3;
            switch ( input.LA(1) ) {
            case NOT_TERM:
                {
                alt10=1;
                }
                break;
            case INVERSE_TERM:
                {
                alt10=2;
                }
                break;
            case IRI_REF_TERM:
                {
                int LA10_3 = input.LA(2);

                if ( ((LA10_3>=SOME_TERM && LA10_3<=MIN_TERM)||(LA10_3>=MAX_TERM && LA10_3<=EXACTLY_TERM)) ) {
                    alt10=2;
                }
                else if ( (LA10_3==EOF||(LA10_3>=OPEN_SQUARE_BRACE && LA10_3<=CLOSE_SQUARE_BRACE)||(LA10_3>=OR_TERM && LA10_3<=AND_TERM)||(LA10_3>=OPEN_CURLY_BRACE && LA10_3<=CLOSE_CURLY_BRACE)||LA10_3==CLOSE_BRACE||LA10_3==IRI_REF_TERM||LA10_3==PNAME_NS||(LA10_3>=DOT_TERM && LA10_3<=GRAPH_TERM)||(LA10_3>=FILTER_TERM && LA10_3<=VAR2)||LA10_3==PNAME_LN) ) {
                    alt10=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 3, input);

                    throw nvae;
                }
                }
                break;
            case PNAME_NS:
            case PNAME_LN:
                {
                int LA10_4 = input.LA(2);

                if ( ((LA10_4>=SOME_TERM && LA10_4<=MIN_TERM)||(LA10_4>=MAX_TERM && LA10_4<=EXACTLY_TERM)) ) {
                    alt10=2;
                }
                else if ( (LA10_4==EOF||(LA10_4>=OPEN_SQUARE_BRACE && LA10_4<=CLOSE_SQUARE_BRACE)||(LA10_4>=OR_TERM && LA10_4<=AND_TERM)||(LA10_4>=OPEN_CURLY_BRACE && LA10_4<=CLOSE_CURLY_BRACE)||LA10_4==CLOSE_BRACE||LA10_4==IRI_REF_TERM||LA10_4==PNAME_NS||(LA10_4>=DOT_TERM && LA10_4<=GRAPH_TERM)||(LA10_4>=FILTER_TERM && LA10_4<=VAR2)||LA10_4==PNAME_LN) ) {
                    alt10=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 4, input);

                    throw nvae;
                }
                }
                break;
            case DECIMAL_TERM:
            case FLOAT_TERM:
            case INTEGER_TERM:
            case STRING_TERM:
            case OPEN_CURLY_BRACE:
            case OPEN_BRACE:
                {
                alt10=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:4: NOT_TERM ( restriction | atomic )
                    {
                    NOT_TERM43=(Token)match(input,NOT_TERM,FOLLOW_NOT_TERM_in_primary1027); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT_TERM.add(NOT_TERM43);

                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:13: ( restriction | atomic )
                    int alt9=2;
                    switch ( input.LA(1) ) {
                    case INVERSE_TERM:
                        {
                        alt9=1;
                        }
                        break;
                    case IRI_REF_TERM:
                        {
                        int LA9_2 = input.LA(2);

                        if ( ((LA9_2>=SOME_TERM && LA9_2<=MIN_TERM)||(LA9_2>=MAX_TERM && LA9_2<=EXACTLY_TERM)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_2==EOF||(LA9_2>=OPEN_SQUARE_BRACE && LA9_2<=CLOSE_SQUARE_BRACE)||(LA9_2>=OR_TERM && LA9_2<=AND_TERM)||(LA9_2>=OPEN_CURLY_BRACE && LA9_2<=CLOSE_CURLY_BRACE)||LA9_2==CLOSE_BRACE||LA9_2==IRI_REF_TERM||LA9_2==PNAME_NS||(LA9_2>=DOT_TERM && LA9_2<=GRAPH_TERM)||(LA9_2>=FILTER_TERM && LA9_2<=VAR2)||LA9_2==PNAME_LN) ) {
                            alt9=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 2, input);

                            throw nvae;
                        }
                        }
                        break;
                    case PNAME_NS:
                    case PNAME_LN:
                        {
                        int LA9_3 = input.LA(2);

                        if ( ((LA9_3>=SOME_TERM && LA9_3<=MIN_TERM)||(LA9_3>=MAX_TERM && LA9_3<=EXACTLY_TERM)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_3==EOF||(LA9_3>=OPEN_SQUARE_BRACE && LA9_3<=CLOSE_SQUARE_BRACE)||(LA9_3>=OR_TERM && LA9_3<=AND_TERM)||(LA9_3>=OPEN_CURLY_BRACE && LA9_3<=CLOSE_CURLY_BRACE)||LA9_3==CLOSE_BRACE||LA9_3==IRI_REF_TERM||LA9_3==PNAME_NS||(LA9_3>=DOT_TERM && LA9_3<=GRAPH_TERM)||(LA9_3>=FILTER_TERM && LA9_3<=VAR2)||LA9_3==PNAME_LN) ) {
                            alt9=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 3, input);

                            throw nvae;
                        }
                        }
                        break;
                    case DECIMAL_TERM:
                    case FLOAT_TERM:
                    case INTEGER_TERM:
                    case STRING_TERM:
                    case OPEN_CURLY_BRACE:
                    case OPEN_BRACE:
                        {
                        alt9=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                        throw nvae;
                    }

                    switch (alt9) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:15: restriction
                            {
                            pushFollow(FOLLOW_restriction_in_primary1031);
                            restriction44=restriction();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_restriction.add(restriction44.getTree());

                            }
                            break;
                        case 2 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:29: atomic
                            {
                            pushFollow(FOLLOW_atomic_in_primary1035);
                            atomic45=atomic();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_atomic.add(atomic45.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: restriction, atomic
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 304:38: -> ^( NEGATION ( restriction )? ( atomic )? )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:41: ^( NEGATION ( restriction )? ( atomic )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NEGATION, "NEGATION"), root_1);

                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:52: ( restriction )?
                        if ( stream_restriction.hasNext() ) {
                            adaptor.addChild(root_1, stream_restriction.nextTree());

                        }
                        stream_restriction.reset();
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:304:65: ( atomic )?
                        if ( stream_atomic.hasNext() ) {
                            adaptor.addChild(root_1, stream_atomic.nextTree());

                        }
                        stream_atomic.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:305:4: restriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_restriction_in_primary1054);
                    restriction46=restriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, restriction46.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:306:4: atomic
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_atomic_in_primary1060);
                    atomic47=atomic();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atomic47.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class atomic_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atomic"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:312:1: atomic : ( iriRef -> ^( CLASS_OR_DATATYPE iriRef ) | INTEGER_TERM -> ^( DATATYPE INTEGER_TERM ) | DECIMAL_TERM -> ^( DATATYPE DECIMAL_TERM ) | FLOAT_TERM -> ^( DATATYPE FLOAT_TERM ) | STRING_TERM -> ^( DATATYPE STRING_TERM ) | datatypeRestriction | OPEN_CURLY_BRACE ( literal )+ CLOSE_CURLY_BRACE -> ^( VALUE_ENUMERATION ( literal )+ ) | OPEN_CURLY_BRACE ( individual )+ CLOSE_CURLY_BRACE -> ^( INDIVIDUAL_ENUMERATION ( individual )+ ) | OPEN_BRACE disjunction CLOSE_BRACE );
    public final SparqlOwlParser.atomic_return atomic() throws RecognitionException {
        SparqlOwlParser.atomic_return retval = new SparqlOwlParser.atomic_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INTEGER_TERM49=null;
        Token DECIMAL_TERM50=null;
        Token FLOAT_TERM51=null;
        Token STRING_TERM52=null;
        Token OPEN_CURLY_BRACE54=null;
        Token CLOSE_CURLY_BRACE56=null;
        Token OPEN_CURLY_BRACE57=null;
        Token CLOSE_CURLY_BRACE59=null;
        Token OPEN_BRACE60=null;
        Token CLOSE_BRACE62=null;
        SparqlOwlParser.iriRef_return iriRef48 = null;

        SparqlOwlParser.datatypeRestriction_return datatypeRestriction53 = null;

        SparqlOwlParser.literal_return literal55 = null;

        SparqlOwlParser.individual_return individual58 = null;

        SparqlOwlParser.disjunction_return disjunction61 = null;


        CommonTree INTEGER_TERM49_tree=null;
        CommonTree DECIMAL_TERM50_tree=null;
        CommonTree FLOAT_TERM51_tree=null;
        CommonTree STRING_TERM52_tree=null;
        CommonTree OPEN_CURLY_BRACE54_tree=null;
        CommonTree CLOSE_CURLY_BRACE56_tree=null;
        CommonTree OPEN_CURLY_BRACE57_tree=null;
        CommonTree CLOSE_CURLY_BRACE59_tree=null;
        CommonTree OPEN_BRACE60_tree=null;
        CommonTree CLOSE_BRACE62_tree=null;
        RewriteRuleTokenStream stream_INTEGER_TERM=new RewriteRuleTokenStream(adaptor,"token INTEGER_TERM");
        RewriteRuleTokenStream stream_STRING_TERM=new RewriteRuleTokenStream(adaptor,"token STRING_TERM");
        RewriteRuleTokenStream stream_FLOAT_TERM=new RewriteRuleTokenStream(adaptor,"token FLOAT_TERM");
        RewriteRuleTokenStream stream_OPEN_CURLY_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_CURLY_BRACE");
        RewriteRuleTokenStream stream_DECIMAL_TERM=new RewriteRuleTokenStream(adaptor,"token DECIMAL_TERM");
        RewriteRuleTokenStream stream_CLOSE_CURLY_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_CURLY_BRACE");
        RewriteRuleSubtreeStream stream_individual=new RewriteRuleSubtreeStream(adaptor,"rule individual");
        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:313:2: ( iriRef -> ^( CLASS_OR_DATATYPE iriRef ) | INTEGER_TERM -> ^( DATATYPE INTEGER_TERM ) | DECIMAL_TERM -> ^( DATATYPE DECIMAL_TERM ) | FLOAT_TERM -> ^( DATATYPE FLOAT_TERM ) | STRING_TERM -> ^( DATATYPE STRING_TERM ) | datatypeRestriction | OPEN_CURLY_BRACE ( literal )+ CLOSE_CURLY_BRACE -> ^( VALUE_ENUMERATION ( literal )+ ) | OPEN_CURLY_BRACE ( individual )+ CLOSE_CURLY_BRACE -> ^( INDIVIDUAL_ENUMERATION ( individual )+ ) | OPEN_BRACE disjunction CLOSE_BRACE )
            int alt13=9;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:313:4: iriRef
                    {
                    pushFollow(FOLLOW_iriRef_in_atomic1073);
                    iriRef48=iriRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_iriRef.add(iriRef48.getTree());


                    // AST REWRITE
                    // elements: iriRef
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 313:11: -> ^( CLASS_OR_DATATYPE iriRef )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:313:14: ^( CLASS_OR_DATATYPE iriRef )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CLASS_OR_DATATYPE, "CLASS_OR_DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_iriRef.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:314:4: INTEGER_TERM
                    {
                    INTEGER_TERM49=(Token)match(input,INTEGER_TERM,FOLLOW_INTEGER_TERM_in_atomic1086); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_INTEGER_TERM.add(INTEGER_TERM49);



                    // AST REWRITE
                    // elements: INTEGER_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 314:17: -> ^( DATATYPE INTEGER_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:314:20: ^( DATATYPE INTEGER_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_INTEGER_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:315:4: DECIMAL_TERM
                    {
                    DECIMAL_TERM50=(Token)match(input,DECIMAL_TERM,FOLLOW_DECIMAL_TERM_in_atomic1099); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DECIMAL_TERM.add(DECIMAL_TERM50);



                    // AST REWRITE
                    // elements: DECIMAL_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 315:17: -> ^( DATATYPE DECIMAL_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:315:20: ^( DATATYPE DECIMAL_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_DECIMAL_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:316:4: FLOAT_TERM
                    {
                    FLOAT_TERM51=(Token)match(input,FLOAT_TERM,FOLLOW_FLOAT_TERM_in_atomic1112); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FLOAT_TERM.add(FLOAT_TERM51);



                    // AST REWRITE
                    // elements: FLOAT_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 316:15: -> ^( DATATYPE FLOAT_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:316:18: ^( DATATYPE FLOAT_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_FLOAT_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:317:4: STRING_TERM
                    {
                    STRING_TERM52=(Token)match(input,STRING_TERM,FOLLOW_STRING_TERM_in_atomic1125); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_TERM.add(STRING_TERM52);



                    // AST REWRITE
                    // elements: STRING_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 317:16: -> ^( DATATYPE STRING_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:317:19: ^( DATATYPE STRING_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATATYPE, "DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_STRING_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:318:4: datatypeRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_datatypeRestriction_in_atomic1138);
                    datatypeRestriction53=datatypeRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, datatypeRestriction53.getTree());

                    }
                    break;
                case 7 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:319:4: OPEN_CURLY_BRACE ( literal )+ CLOSE_CURLY_BRACE
                    {
                    OPEN_CURLY_BRACE54=(Token)match(input,OPEN_CURLY_BRACE,FOLLOW_OPEN_CURLY_BRACE_in_atomic1143); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_CURLY_BRACE.add(OPEN_CURLY_BRACE54);

                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:319:21: ( literal )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==INTEGER||(LA11_0>=DECIMAL && LA11_0<=STRING_LITERAL_LONG2)) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:319:21: literal
                    	    {
                    	    pushFollow(FOLLOW_literal_in_atomic1145);
                    	    literal55=literal();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_literal.add(literal55.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);

                    CLOSE_CURLY_BRACE56=(Token)match(input,CLOSE_CURLY_BRACE,FOLLOW_CLOSE_CURLY_BRACE_in_atomic1148); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_CURLY_BRACE.add(CLOSE_CURLY_BRACE56);



                    // AST REWRITE
                    // elements: literal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 319:48: -> ^( VALUE_ENUMERATION ( literal )+ )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:319:51: ^( VALUE_ENUMERATION ( literal )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VALUE_ENUMERATION, "VALUE_ENUMERATION"), root_1);

                        if ( !(stream_literal.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_literal.hasNext() ) {
                            adaptor.addChild(root_1, stream_literal.nextTree());

                        }
                        stream_literal.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:320:4: OPEN_CURLY_BRACE ( individual )+ CLOSE_CURLY_BRACE
                    {
                    OPEN_CURLY_BRACE57=(Token)match(input,OPEN_CURLY_BRACE,FOLLOW_OPEN_CURLY_BRACE_in_atomic1162); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_CURLY_BRACE.add(OPEN_CURLY_BRACE57);

                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:320:21: ( individual )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==IRI_REF_TERM||LA12_0==PNAME_NS||LA12_0==PNAME_LN) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:320:21: individual
                    	    {
                    	    pushFollow(FOLLOW_individual_in_atomic1164);
                    	    individual58=individual();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_individual.add(individual58.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);

                    CLOSE_CURLY_BRACE59=(Token)match(input,CLOSE_CURLY_BRACE,FOLLOW_CLOSE_CURLY_BRACE_in_atomic1167); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_CURLY_BRACE.add(CLOSE_CURLY_BRACE59);



                    // AST REWRITE
                    // elements: individual
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 320:51: -> ^( INDIVIDUAL_ENUMERATION ( individual )+ )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:320:54: ^( INDIVIDUAL_ENUMERATION ( individual )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INDIVIDUAL_ENUMERATION, "INDIVIDUAL_ENUMERATION"), root_1);

                        if ( !(stream_individual.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_individual.hasNext() ) {
                            adaptor.addChild(root_1, stream_individual.nextTree());

                        }
                        stream_individual.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 9 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:321:4: OPEN_BRACE disjunction CLOSE_BRACE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    OPEN_BRACE60=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_atomic1181); if (state.failed) return retval;
                    pushFollow(FOLLOW_disjunction_in_atomic1184);
                    disjunction61=disjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, disjunction61.getTree());
                    CLOSE_BRACE62=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_atomic1186); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class restriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "restriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:327:1: restriction : ( someRestriction | allRestriction | valueRestriction | selfRestriction | numberRestriction );
    public final SparqlOwlParser.restriction_return restriction() throws RecognitionException {
        SparqlOwlParser.restriction_return retval = new SparqlOwlParser.restriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.someRestriction_return someRestriction63 = null;

        SparqlOwlParser.allRestriction_return allRestriction64 = null;

        SparqlOwlParser.valueRestriction_return valueRestriction65 = null;

        SparqlOwlParser.selfRestriction_return selfRestriction66 = null;

        SparqlOwlParser.numberRestriction_return numberRestriction67 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:328:2: ( someRestriction | allRestriction | valueRestriction | selfRestriction | numberRestriction )
            int alt14=5;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:328:4: someRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_someRestriction_in_restriction1200);
                    someRestriction63=someRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, someRestriction63.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:329:4: allRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_allRestriction_in_restriction1205);
                    allRestriction64=allRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, allRestriction64.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:330:4: valueRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_valueRestriction_in_restriction1210);
                    valueRestriction65=valueRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, valueRestriction65.getTree());

                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:331:4: selfRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_selfRestriction_in_restriction1215);
                    selfRestriction66=selfRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, selfRestriction66.getTree());

                    }
                    break;
                case 5 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:332:4: numberRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_numberRestriction_in_restriction1220);
                    numberRestriction67=numberRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numberRestriction67.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class someRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "someRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:335:1: someRestriction : propertyExpression SOME_TERM primary -> ^( SOME_RESTRICTION propertyExpression primary ) ;
    public final SparqlOwlParser.someRestriction_return someRestriction() throws RecognitionException {
        SparqlOwlParser.someRestriction_return retval = new SparqlOwlParser.someRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token SOME_TERM69=null;
        SparqlOwlParser.propertyExpression_return propertyExpression68 = null;

        SparqlOwlParser.primary_return primary70 = null;


        CommonTree SOME_TERM69_tree=null;
        RewriteRuleTokenStream stream_SOME_TERM=new RewriteRuleTokenStream(adaptor,"token SOME_TERM");
        RewriteRuleSubtreeStream stream_propertyExpression=new RewriteRuleSubtreeStream(adaptor,"rule propertyExpression");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:336:2: ( propertyExpression SOME_TERM primary -> ^( SOME_RESTRICTION propertyExpression primary ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:336:4: propertyExpression SOME_TERM primary
            {
            pushFollow(FOLLOW_propertyExpression_in_someRestriction1231);
            propertyExpression68=propertyExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_propertyExpression.add(propertyExpression68.getTree());
            SOME_TERM69=(Token)match(input,SOME_TERM,FOLLOW_SOME_TERM_in_someRestriction1233); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SOME_TERM.add(SOME_TERM69);

            pushFollow(FOLLOW_primary_in_someRestriction1235);
            primary70=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary70.getTree());


            // AST REWRITE
            // elements: primary, propertyExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 337:2: -> ^( SOME_RESTRICTION propertyExpression primary )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:337:5: ^( SOME_RESTRICTION propertyExpression primary )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SOME_RESTRICTION, "SOME_RESTRICTION"), root_1);

                adaptor.addChild(root_1, stream_propertyExpression.nextTree());
                adaptor.addChild(root_1, stream_primary.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "someRestriction"

    public static class allRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "allRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:340:1: allRestriction : propertyExpression ONLY_TERM primary -> ^( ALL_RESTRICTION propertyExpression primary ) ;
    public final SparqlOwlParser.allRestriction_return allRestriction() throws RecognitionException {
        SparqlOwlParser.allRestriction_return retval = new SparqlOwlParser.allRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ONLY_TERM72=null;
        SparqlOwlParser.propertyExpression_return propertyExpression71 = null;

        SparqlOwlParser.primary_return primary73 = null;


        CommonTree ONLY_TERM72_tree=null;
        RewriteRuleTokenStream stream_ONLY_TERM=new RewriteRuleTokenStream(adaptor,"token ONLY_TERM");
        RewriteRuleSubtreeStream stream_propertyExpression=new RewriteRuleSubtreeStream(adaptor,"rule propertyExpression");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:341:2: ( propertyExpression ONLY_TERM primary -> ^( ALL_RESTRICTION propertyExpression primary ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:341:4: propertyExpression ONLY_TERM primary
            {
            pushFollow(FOLLOW_propertyExpression_in_allRestriction1257);
            propertyExpression71=propertyExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_propertyExpression.add(propertyExpression71.getTree());
            ONLY_TERM72=(Token)match(input,ONLY_TERM,FOLLOW_ONLY_TERM_in_allRestriction1259); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ONLY_TERM.add(ONLY_TERM72);

            pushFollow(FOLLOW_primary_in_allRestriction1261);
            primary73=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary73.getTree());


            // AST REWRITE
            // elements: propertyExpression, primary
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 342:2: -> ^( ALL_RESTRICTION propertyExpression primary )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:342:5: ^( ALL_RESTRICTION propertyExpression primary )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ALL_RESTRICTION, "ALL_RESTRICTION"), root_1);

                adaptor.addChild(root_1, stream_propertyExpression.nextTree());
                adaptor.addChild(root_1, stream_primary.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "allRestriction"

    public static class valueRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "valueRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:345:1: valueRestriction : ( objectPropertyExpression VALUE_TERM individual -> ^( VALUE_RESTRICTION objectPropertyExpression individual ) | dataPropertyIRI VALUE_TERM literal -> ^( VALUE_RESTRICTION dataPropertyIRI literal ) );
    public final SparqlOwlParser.valueRestriction_return valueRestriction() throws RecognitionException {
        SparqlOwlParser.valueRestriction_return retval = new SparqlOwlParser.valueRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token VALUE_TERM75=null;
        Token VALUE_TERM78=null;
        SparqlOwlParser.objectPropertyExpression_return objectPropertyExpression74 = null;

        SparqlOwlParser.individual_return individual76 = null;

        SparqlOwlParser.dataPropertyIRI_return dataPropertyIRI77 = null;

        SparqlOwlParser.literal_return literal79 = null;


        CommonTree VALUE_TERM75_tree=null;
        CommonTree VALUE_TERM78_tree=null;
        RewriteRuleTokenStream stream_VALUE_TERM=new RewriteRuleTokenStream(adaptor,"token VALUE_TERM");
        RewriteRuleSubtreeStream stream_objectPropertyExpression=new RewriteRuleSubtreeStream(adaptor,"rule objectPropertyExpression");
        RewriteRuleSubtreeStream stream_individual=new RewriteRuleSubtreeStream(adaptor,"rule individual");
        RewriteRuleSubtreeStream stream_dataPropertyIRI=new RewriteRuleSubtreeStream(adaptor,"rule dataPropertyIRI");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:346:2: ( objectPropertyExpression VALUE_TERM individual -> ^( VALUE_RESTRICTION objectPropertyExpression individual ) | dataPropertyIRI VALUE_TERM literal -> ^( VALUE_RESTRICTION dataPropertyIRI literal ) )
            int alt15=2;
            switch ( input.LA(1) ) {
            case INVERSE_TERM:
                {
                alt15=1;
                }
                break;
            case IRI_REF_TERM:
                {
                int LA15_2 = input.LA(2);

                if ( (LA15_2==VALUE_TERM) ) {
                    int LA15_4 = input.LA(3);

                    if ( (LA15_4==INTEGER||(LA15_4>=DECIMAL && LA15_4<=STRING_LITERAL_LONG2)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_4==IRI_REF_TERM||LA15_4==PNAME_NS||LA15_4==PNAME_LN) ) {
                        alt15=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 2, input);

                    throw nvae;
                }
                }
                break;
            case PNAME_NS:
            case PNAME_LN:
                {
                int LA15_3 = input.LA(2);

                if ( (LA15_3==VALUE_TERM) ) {
                    int LA15_4 = input.LA(3);

                    if ( (LA15_4==INTEGER||(LA15_4>=DECIMAL && LA15_4<=STRING_LITERAL_LONG2)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_4==IRI_REF_TERM||LA15_4==PNAME_NS||LA15_4==PNAME_LN) ) {
                        alt15=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:346:4: objectPropertyExpression VALUE_TERM individual
                    {
                    pushFollow(FOLLOW_objectPropertyExpression_in_valueRestriction1284);
                    objectPropertyExpression74=objectPropertyExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_objectPropertyExpression.add(objectPropertyExpression74.getTree());
                    VALUE_TERM75=(Token)match(input,VALUE_TERM,FOLLOW_VALUE_TERM_in_valueRestriction1286); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_VALUE_TERM.add(VALUE_TERM75);

                    pushFollow(FOLLOW_individual_in_valueRestriction1288);
                    individual76=individual();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_individual.add(individual76.getTree());


                    // AST REWRITE
                    // elements: individual, objectPropertyExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 346:51: -> ^( VALUE_RESTRICTION objectPropertyExpression individual )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:346:54: ^( VALUE_RESTRICTION objectPropertyExpression individual )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VALUE_RESTRICTION, "VALUE_RESTRICTION"), root_1);

                        adaptor.addChild(root_1, stream_objectPropertyExpression.nextTree());
                        adaptor.addChild(root_1, stream_individual.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:347:4: dataPropertyIRI VALUE_TERM literal
                    {
                    pushFollow(FOLLOW_dataPropertyIRI_in_valueRestriction1303);
                    dataPropertyIRI77=dataPropertyIRI();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_dataPropertyIRI.add(dataPropertyIRI77.getTree());
                    VALUE_TERM78=(Token)match(input,VALUE_TERM,FOLLOW_VALUE_TERM_in_valueRestriction1305); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_VALUE_TERM.add(VALUE_TERM78);

                    pushFollow(FOLLOW_literal_in_valueRestriction1307);
                    literal79=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal79.getTree());


                    // AST REWRITE
                    // elements: literal, dataPropertyIRI
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 347:39: -> ^( VALUE_RESTRICTION dataPropertyIRI literal )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:347:42: ^( VALUE_RESTRICTION dataPropertyIRI literal )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VALUE_RESTRICTION, "VALUE_RESTRICTION"), root_1);

                        adaptor.addChild(root_1, stream_dataPropertyIRI.nextTree());
                        adaptor.addChild(root_1, stream_literal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "valueRestriction"

    public static class selfRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selfRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:350:1: selfRestriction : objectPropertyExpression SELF_TERM -> ^( SELF_RESTRICTION objectPropertyExpression ) ;
    public final SparqlOwlParser.selfRestriction_return selfRestriction() throws RecognitionException {
        SparqlOwlParser.selfRestriction_return retval = new SparqlOwlParser.selfRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token SELF_TERM81=null;
        SparqlOwlParser.objectPropertyExpression_return objectPropertyExpression80 = null;


        CommonTree SELF_TERM81_tree=null;
        RewriteRuleTokenStream stream_SELF_TERM=new RewriteRuleTokenStream(adaptor,"token SELF_TERM");
        RewriteRuleSubtreeStream stream_objectPropertyExpression=new RewriteRuleSubtreeStream(adaptor,"rule objectPropertyExpression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:351:2: ( objectPropertyExpression SELF_TERM -> ^( SELF_RESTRICTION objectPropertyExpression ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:351:4: objectPropertyExpression SELF_TERM
            {
            pushFollow(FOLLOW_objectPropertyExpression_in_selfRestriction1328);
            objectPropertyExpression80=objectPropertyExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_objectPropertyExpression.add(objectPropertyExpression80.getTree());
            SELF_TERM81=(Token)match(input,SELF_TERM,FOLLOW_SELF_TERM_in_selfRestriction1330); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SELF_TERM.add(SELF_TERM81);



            // AST REWRITE
            // elements: objectPropertyExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 352:2: -> ^( SELF_RESTRICTION objectPropertyExpression )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:352:5: ^( SELF_RESTRICTION objectPropertyExpression )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SELF_RESTRICTION, "SELF_RESTRICTION"), root_1);

                adaptor.addChild(root_1, stream_objectPropertyExpression.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "selfRestriction"

    public static class numberRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numberRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:355:1: numberRestriction : ( minNumberRestriction | maxNumberRestriction | exactNumberRestriction );
    public final SparqlOwlParser.numberRestriction_return numberRestriction() throws RecognitionException {
        SparqlOwlParser.numberRestriction_return retval = new SparqlOwlParser.numberRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.minNumberRestriction_return minNumberRestriction82 = null;

        SparqlOwlParser.maxNumberRestriction_return maxNumberRestriction83 = null;

        SparqlOwlParser.exactNumberRestriction_return exactNumberRestriction84 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:356:2: ( minNumberRestriction | maxNumberRestriction | exactNumberRestriction )
            int alt16=3;
            switch ( input.LA(1) ) {
            case INVERSE_TERM:
                {
                int LA16_1 = input.LA(2);

                if ( (LA16_1==IRI_REF_TERM) ) {
                    switch ( input.LA(3) ) {
                    case MIN_TERM:
                        {
                        alt16=1;
                        }
                        break;
                    case MAX_TERM:
                        {
                        alt16=2;
                        }
                        break;
                    case EXACTLY_TERM:
                        {
                        alt16=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 4, input);

                        throw nvae;
                    }

                }
                else if ( (LA16_1==PNAME_NS||LA16_1==PNAME_LN) ) {
                    switch ( input.LA(3) ) {
                    case EXACTLY_TERM:
                        {
                        alt16=3;
                        }
                        break;
                    case MAX_TERM:
                        {
                        alt16=2;
                        }
                        break;
                    case MIN_TERM:
                        {
                        alt16=1;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 5, input);

                        throw nvae;
                    }

                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    throw nvae;
                }
                }
                break;
            case IRI_REF_TERM:
                {
                switch ( input.LA(2) ) {
                case MAX_TERM:
                    {
                    alt16=2;
                    }
                    break;
                case MIN_TERM:
                    {
                    alt16=1;
                    }
                    break;
                case EXACTLY_TERM:
                    {
                    alt16=3;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 2, input);

                    throw nvae;
                }

                }
                break;
            case PNAME_NS:
            case PNAME_LN:
                {
                switch ( input.LA(2) ) {
                case MAX_TERM:
                    {
                    alt16=2;
                    }
                    break;
                case EXACTLY_TERM:
                    {
                    alt16=3;
                    }
                    break;
                case MIN_TERM:
                    {
                    alt16=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 3, input);

                    throw nvae;
                }

                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:356:4: minNumberRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_minNumberRestriction_in_numberRestriction1350);
                    minNumberRestriction82=minNumberRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, minNumberRestriction82.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:357:4: maxNumberRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_maxNumberRestriction_in_numberRestriction1355);
                    maxNumberRestriction83=maxNumberRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, maxNumberRestriction83.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:358:4: exactNumberRestriction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_exactNumberRestriction_in_numberRestriction1360);
                    exactNumberRestriction84=exactNumberRestriction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, exactNumberRestriction84.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "numberRestriction"

    public static class minNumberRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "minNumberRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:364:1: minNumberRestriction : propertyExpression MIN_TERM n= INTEGER ( ( primary )=> primary | ) -> ^( MIN_NUMBER_RESTRICTION propertyExpression $n ( primary )? ) ;
    public final SparqlOwlParser.minNumberRestriction_return minNumberRestriction() throws RecognitionException {
        SparqlOwlParser.minNumberRestriction_return retval = new SparqlOwlParser.minNumberRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token n=null;
        Token MIN_TERM86=null;
        SparqlOwlParser.propertyExpression_return propertyExpression85 = null;

        SparqlOwlParser.primary_return primary87 = null;


        CommonTree n_tree=null;
        CommonTree MIN_TERM86_tree=null;
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_MIN_TERM=new RewriteRuleTokenStream(adaptor,"token MIN_TERM");
        RewriteRuleSubtreeStream stream_propertyExpression=new RewriteRuleSubtreeStream(adaptor,"rule propertyExpression");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:365:2: ( propertyExpression MIN_TERM n= INTEGER ( ( primary )=> primary | ) -> ^( MIN_NUMBER_RESTRICTION propertyExpression $n ( primary )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:365:4: propertyExpression MIN_TERM n= INTEGER ( ( primary )=> primary | )
            {
            pushFollow(FOLLOW_propertyExpression_in_minNumberRestriction1373);
            propertyExpression85=propertyExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_propertyExpression.add(propertyExpression85.getTree());
            MIN_TERM86=(Token)match(input,MIN_TERM,FOLLOW_MIN_TERM_in_minNumberRestriction1375); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MIN_TERM.add(MIN_TERM86);

            n=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_minNumberRestriction1379); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INTEGER.add(n);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:366:3: ( ( primary )=> primary | )
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:366:5: ( primary )=> primary
                    {
                    pushFollow(FOLLOW_primary_in_minNumberRestriction1390);
                    primary87=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primary.add(primary87.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:368:3: 
                    {
                    }
                    break;

            }



            // AST REWRITE
            // elements: primary, n, propertyExpression
            // token labels: n
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_n=new RewriteRuleTokenStream(adaptor,"token n",n);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 369:2: -> ^( MIN_NUMBER_RESTRICTION propertyExpression $n ( primary )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:369:5: ^( MIN_NUMBER_RESTRICTION propertyExpression $n ( primary )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(MIN_NUMBER_RESTRICTION, "MIN_NUMBER_RESTRICTION"), root_1);

                adaptor.addChild(root_1, stream_propertyExpression.nextTree());
                adaptor.addChild(root_1, stream_n.nextNode());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:369:52: ( primary )?
                if ( stream_primary.hasNext() ) {
                    adaptor.addChild(root_1, stream_primary.nextTree());

                }
                stream_primary.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "minNumberRestriction"

    public static class maxNumberRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "maxNumberRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:372:1: maxNumberRestriction : propertyExpression MAX_TERM n= INTEGER ( ( primary )=> primary | ) -> ^( MAX_NUMBER_RESTRICTION propertyExpression $n ( primary )? ) ;
    public final SparqlOwlParser.maxNumberRestriction_return maxNumberRestriction() throws RecognitionException {
        SparqlOwlParser.maxNumberRestriction_return retval = new SparqlOwlParser.maxNumberRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token n=null;
        Token MAX_TERM89=null;
        SparqlOwlParser.propertyExpression_return propertyExpression88 = null;

        SparqlOwlParser.primary_return primary90 = null;


        CommonTree n_tree=null;
        CommonTree MAX_TERM89_tree=null;
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_MAX_TERM=new RewriteRuleTokenStream(adaptor,"token MAX_TERM");
        RewriteRuleSubtreeStream stream_propertyExpression=new RewriteRuleSubtreeStream(adaptor,"rule propertyExpression");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:373:2: ( propertyExpression MAX_TERM n= INTEGER ( ( primary )=> primary | ) -> ^( MAX_NUMBER_RESTRICTION propertyExpression $n ( primary )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:373:4: propertyExpression MAX_TERM n= INTEGER ( ( primary )=> primary | )
            {
            pushFollow(FOLLOW_propertyExpression_in_maxNumberRestriction1425);
            propertyExpression88=propertyExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_propertyExpression.add(propertyExpression88.getTree());
            MAX_TERM89=(Token)match(input,MAX_TERM,FOLLOW_MAX_TERM_in_maxNumberRestriction1427); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MAX_TERM.add(MAX_TERM89);

            n=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_maxNumberRestriction1431); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INTEGER.add(n);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:374:3: ( ( primary )=> primary | )
            int alt18=2;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:374:5: ( primary )=> primary
                    {
                    pushFollow(FOLLOW_primary_in_maxNumberRestriction1442);
                    primary90=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primary.add(primary90.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:376:3: 
                    {
                    }
                    break;

            }



            // AST REWRITE
            // elements: propertyExpression, n, primary
            // token labels: n
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_n=new RewriteRuleTokenStream(adaptor,"token n",n);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 377:2: -> ^( MAX_NUMBER_RESTRICTION propertyExpression $n ( primary )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:377:5: ^( MAX_NUMBER_RESTRICTION propertyExpression $n ( primary )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(MAX_NUMBER_RESTRICTION, "MAX_NUMBER_RESTRICTION"), root_1);

                adaptor.addChild(root_1, stream_propertyExpression.nextTree());
                adaptor.addChild(root_1, stream_n.nextNode());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:377:52: ( primary )?
                if ( stream_primary.hasNext() ) {
                    adaptor.addChild(root_1, stream_primary.nextTree());

                }
                stream_primary.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "maxNumberRestriction"

    public static class exactNumberRestriction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exactNumberRestriction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:380:1: exactNumberRestriction : propertyExpression EXACTLY_TERM n= INTEGER ( ( primary )=> primary | ) -> ^( EXACT_NUMBER_RESTRICTION propertyExpression $n ( primary )? ) ;
    public final SparqlOwlParser.exactNumberRestriction_return exactNumberRestriction() throws RecognitionException {
        SparqlOwlParser.exactNumberRestriction_return retval = new SparqlOwlParser.exactNumberRestriction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token n=null;
        Token EXACTLY_TERM92=null;
        SparqlOwlParser.propertyExpression_return propertyExpression91 = null;

        SparqlOwlParser.primary_return primary93 = null;


        CommonTree n_tree=null;
        CommonTree EXACTLY_TERM92_tree=null;
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_EXACTLY_TERM=new RewriteRuleTokenStream(adaptor,"token EXACTLY_TERM");
        RewriteRuleSubtreeStream stream_propertyExpression=new RewriteRuleSubtreeStream(adaptor,"rule propertyExpression");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:381:2: ( propertyExpression EXACTLY_TERM n= INTEGER ( ( primary )=> primary | ) -> ^( EXACT_NUMBER_RESTRICTION propertyExpression $n ( primary )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:381:4: propertyExpression EXACTLY_TERM n= INTEGER ( ( primary )=> primary | )
            {
            pushFollow(FOLLOW_propertyExpression_in_exactNumberRestriction1477);
            propertyExpression91=propertyExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_propertyExpression.add(propertyExpression91.getTree());
            EXACTLY_TERM92=(Token)match(input,EXACTLY_TERM,FOLLOW_EXACTLY_TERM_in_exactNumberRestriction1479); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EXACTLY_TERM.add(EXACTLY_TERM92);

            n=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_exactNumberRestriction1483); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INTEGER.add(n);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:382:3: ( ( primary )=> primary | )
            int alt19=2;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:382:5: ( primary )=> primary
                    {
                    pushFollow(FOLLOW_primary_in_exactNumberRestriction1494);
                    primary93=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primary.add(primary93.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:384:3: 
                    {
                    }
                    break;

            }



            // AST REWRITE
            // elements: primary, n, propertyExpression
            // token labels: n
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_n=new RewriteRuleTokenStream(adaptor,"token n",n);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 385:2: -> ^( EXACT_NUMBER_RESTRICTION propertyExpression $n ( primary )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:385:5: ^( EXACT_NUMBER_RESTRICTION propertyExpression $n ( primary )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(EXACT_NUMBER_RESTRICTION, "EXACT_NUMBER_RESTRICTION"), root_1);

                adaptor.addChild(root_1, stream_propertyExpression.nextTree());
                adaptor.addChild(root_1, stream_n.nextNode());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:385:54: ( primary )?
                if ( stream_primary.hasNext() ) {
                    adaptor.addChild(root_1, stream_primary.nextTree());

                }
                stream_primary.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "exactNumberRestriction"

    public static class query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "query"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:392:1: query : prologue ( selectQuery | constructQuery | describeQuery | askQuery ) EOF -> ^( QUERY ( prologue )? ( selectQuery )? ( constructQuery )? ( describeQuery )? ( askQuery )? ) ;
    public final SparqlOwlParser.query_return query() throws RecognitionException {
        SparqlOwlParser.query_return retval = new SparqlOwlParser.query_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token EOF99=null;
        SparqlOwlParser.prologue_return prologue94 = null;

        SparqlOwlParser.selectQuery_return selectQuery95 = null;

        SparqlOwlParser.constructQuery_return constructQuery96 = null;

        SparqlOwlParser.describeQuery_return describeQuery97 = null;

        SparqlOwlParser.askQuery_return askQuery98 = null;


        CommonTree EOF99_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_describeQuery=new RewriteRuleSubtreeStream(adaptor,"rule describeQuery");
        RewriteRuleSubtreeStream stream_constructQuery=new RewriteRuleSubtreeStream(adaptor,"rule constructQuery");
        RewriteRuleSubtreeStream stream_askQuery=new RewriteRuleSubtreeStream(adaptor,"rule askQuery");
        RewriteRuleSubtreeStream stream_prologue=new RewriteRuleSubtreeStream(adaptor,"rule prologue");
        RewriteRuleSubtreeStream stream_selectQuery=new RewriteRuleSubtreeStream(adaptor,"rule selectQuery");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:393:2: ( prologue ( selectQuery | constructQuery | describeQuery | askQuery ) EOF -> ^( QUERY ( prologue )? ( selectQuery )? ( constructQuery )? ( describeQuery )? ( askQuery )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:393:4: prologue ( selectQuery | constructQuery | describeQuery | askQuery ) EOF
            {
            pushFollow(FOLLOW_prologue_in_query1533);
            prologue94=prologue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_prologue.add(prologue94.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:394:3: ( selectQuery | constructQuery | describeQuery | askQuery )
            int alt20=4;
            switch ( input.LA(1) ) {
            case SELECT_TERM:
                {
                alt20=1;
                }
                break;
            case CONSTRUCT_TERM:
                {
                alt20=2;
                }
                break;
            case DESCRIBE_TERM:
                {
                alt20=3;
                }
                break;
            case ASK_TERM:
                {
                alt20=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:394:5: selectQuery
                    {
                    pushFollow(FOLLOW_selectQuery_in_query1539);
                    selectQuery95=selectQuery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_selectQuery.add(selectQuery95.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:394:19: constructQuery
                    {
                    pushFollow(FOLLOW_constructQuery_in_query1543);
                    constructQuery96=constructQuery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_constructQuery.add(constructQuery96.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:394:36: describeQuery
                    {
                    pushFollow(FOLLOW_describeQuery_in_query1547);
                    describeQuery97=describeQuery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_describeQuery.add(describeQuery97.getTree());

                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:394:52: askQuery
                    {
                    pushFollow(FOLLOW_askQuery_in_query1551);
                    askQuery98=askQuery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_askQuery.add(askQuery98.getTree());

                    }
                    break;

            }

            EOF99=(Token)match(input,EOF,FOLLOW_EOF_in_query1555); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF99);



            // AST REWRITE
            // elements: askQuery, selectQuery, prologue, describeQuery, constructQuery
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 395:2: -> ^( QUERY ( prologue )? ( selectQuery )? ( constructQuery )? ( describeQuery )? ( askQuery )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:395:5: ^( QUERY ( prologue )? ( selectQuery )? ( constructQuery )? ( describeQuery )? ( askQuery )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(QUERY, "QUERY"), root_1);

                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:395:13: ( prologue )?
                if ( stream_prologue.hasNext() ) {
                    adaptor.addChild(root_1, stream_prologue.nextTree());

                }
                stream_prologue.reset();
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:395:23: ( selectQuery )?
                if ( stream_selectQuery.hasNext() ) {
                    adaptor.addChild(root_1, stream_selectQuery.nextTree());

                }
                stream_selectQuery.reset();
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:395:36: ( constructQuery )?
                if ( stream_constructQuery.hasNext() ) {
                    adaptor.addChild(root_1, stream_constructQuery.nextTree());

                }
                stream_constructQuery.reset();
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:395:52: ( describeQuery )?
                if ( stream_describeQuery.hasNext() ) {
                    adaptor.addChild(root_1, stream_describeQuery.nextTree());

                }
                stream_describeQuery.reset();
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:395:67: ( askQuery )?
                if ( stream_askQuery.hasNext() ) {
                    adaptor.addChild(root_1, stream_askQuery.nextTree());

                }
                stream_askQuery.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "query"

    public static class prologue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prologue"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:401:1: prologue : ( baseDecl )? ( prefixDecl )* ;
    public final SparqlOwlParser.prologue_return prologue() throws RecognitionException {
        SparqlOwlParser.prologue_return retval = new SparqlOwlParser.prologue_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.baseDecl_return baseDecl100 = null;

        SparqlOwlParser.prefixDecl_return prefixDecl101 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:402:2: ( ( baseDecl )? ( prefixDecl )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:402:4: ( baseDecl )? ( prefixDecl )*
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:402:4: ( baseDecl )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==BASE_TERM) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:402:4: baseDecl
                    {
                    pushFollow(FOLLOW_baseDecl_in_prologue1591);
                    baseDecl100=baseDecl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, baseDecl100.getTree());

                    }
                    break;

            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:402:14: ( prefixDecl )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==PREFIX_TERM) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:402:14: prefixDecl
            	    {
            	    pushFollow(FOLLOW_prefixDecl_in_prologue1594);
            	    prefixDecl101=prefixDecl();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, prefixDecl101.getTree());

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "prologue"

    public static class baseDecl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "baseDecl"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:408:1: baseDecl : BASE_TERM IRI_REF_TERM -> ^( BASE_DECL IRI_REF_TERM ) ;
    public final SparqlOwlParser.baseDecl_return baseDecl() throws RecognitionException {
        SparqlOwlParser.baseDecl_return retval = new SparqlOwlParser.baseDecl_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token BASE_TERM102=null;
        Token IRI_REF_TERM103=null;

        CommonTree BASE_TERM102_tree=null;
        CommonTree IRI_REF_TERM103_tree=null;
        RewriteRuleTokenStream stream_BASE_TERM=new RewriteRuleTokenStream(adaptor,"token BASE_TERM");
        RewriteRuleTokenStream stream_IRI_REF_TERM=new RewriteRuleTokenStream(adaptor,"token IRI_REF_TERM");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:409:2: ( BASE_TERM IRI_REF_TERM -> ^( BASE_DECL IRI_REF_TERM ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:409:4: BASE_TERM IRI_REF_TERM
            {
            BASE_TERM102=(Token)match(input,BASE_TERM,FOLLOW_BASE_TERM_in_baseDecl1608); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_BASE_TERM.add(BASE_TERM102);

            IRI_REF_TERM103=(Token)match(input,IRI_REF_TERM,FOLLOW_IRI_REF_TERM_in_baseDecl1610); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IRI_REF_TERM.add(IRI_REF_TERM103);



            // AST REWRITE
            // elements: IRI_REF_TERM
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 410:2: -> ^( BASE_DECL IRI_REF_TERM )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:410:5: ^( BASE_DECL IRI_REF_TERM )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BASE_DECL, "BASE_DECL"), root_1);

                adaptor.addChild(root_1, stream_IRI_REF_TERM.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "baseDecl"

    public static class prefixDecl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prefixDecl"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:416:1: prefixDecl : PREFIX_TERM PNAME_NS IRI_REF_TERM -> ^( PREFIX_DECL PNAME_NS IRI_REF_TERM ) ;
    public final SparqlOwlParser.prefixDecl_return prefixDecl() throws RecognitionException {
        SparqlOwlParser.prefixDecl_return retval = new SparqlOwlParser.prefixDecl_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token PREFIX_TERM104=null;
        Token PNAME_NS105=null;
        Token IRI_REF_TERM106=null;

        CommonTree PREFIX_TERM104_tree=null;
        CommonTree PNAME_NS105_tree=null;
        CommonTree IRI_REF_TERM106_tree=null;
        RewriteRuleTokenStream stream_PREFIX_TERM=new RewriteRuleTokenStream(adaptor,"token PREFIX_TERM");
        RewriteRuleTokenStream stream_PNAME_NS=new RewriteRuleTokenStream(adaptor,"token PNAME_NS");
        RewriteRuleTokenStream stream_IRI_REF_TERM=new RewriteRuleTokenStream(adaptor,"token IRI_REF_TERM");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:417:2: ( PREFIX_TERM PNAME_NS IRI_REF_TERM -> ^( PREFIX_DECL PNAME_NS IRI_REF_TERM ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:417:4: PREFIX_TERM PNAME_NS IRI_REF_TERM
            {
            PREFIX_TERM104=(Token)match(input,PREFIX_TERM,FOLLOW_PREFIX_TERM_in_prefixDecl1632); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_PREFIX_TERM.add(PREFIX_TERM104);

            PNAME_NS105=(Token)match(input,PNAME_NS,FOLLOW_PNAME_NS_in_prefixDecl1634); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_PNAME_NS.add(PNAME_NS105);

            IRI_REF_TERM106=(Token)match(input,IRI_REF_TERM,FOLLOW_IRI_REF_TERM_in_prefixDecl1636); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IRI_REF_TERM.add(IRI_REF_TERM106);



            // AST REWRITE
            // elements: PNAME_NS, IRI_REF_TERM
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 418:2: -> ^( PREFIX_DECL PNAME_NS IRI_REF_TERM )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:418:5: ^( PREFIX_DECL PNAME_NS IRI_REF_TERM )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PREFIX_DECL, "PREFIX_DECL"), root_1);

                adaptor.addChild(root_1, stream_PNAME_NS.nextNode());
                adaptor.addChild(root_1, stream_IRI_REF_TERM.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class selectQuery_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selectQuery"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:424:1: selectQuery : SELECT_TERM ( selectModifier )? selectVariableList ( datasets )? whereClause solutionModifier -> ^( SELECT ( selectModifier )? selectVariableList ( datasets )? whereClause ( solutionModifier )? ) ;
    public final SparqlOwlParser.selectQuery_return selectQuery() throws RecognitionException {
        SparqlOwlParser.selectQuery_return retval = new SparqlOwlParser.selectQuery_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token SELECT_TERM107=null;
        SparqlOwlParser.selectModifier_return selectModifier108 = null;

        SparqlOwlParser.selectVariableList_return selectVariableList109 = null;

        SparqlOwlParser.datasets_return datasets110 = null;

        SparqlOwlParser.whereClause_return whereClause111 = null;

        SparqlOwlParser.solutionModifier_return solutionModifier112 = null;


        CommonTree SELECT_TERM107_tree=null;
        RewriteRuleTokenStream stream_SELECT_TERM=new RewriteRuleTokenStream(adaptor,"token SELECT_TERM");
        RewriteRuleSubtreeStream stream_whereClause=new RewriteRuleSubtreeStream(adaptor,"rule whereClause");
        RewriteRuleSubtreeStream stream_datasets=new RewriteRuleSubtreeStream(adaptor,"rule datasets");
        RewriteRuleSubtreeStream stream_solutionModifier=new RewriteRuleSubtreeStream(adaptor,"rule solutionModifier");
        RewriteRuleSubtreeStream stream_selectModifier=new RewriteRuleSubtreeStream(adaptor,"rule selectModifier");
        RewriteRuleSubtreeStream stream_selectVariableList=new RewriteRuleSubtreeStream(adaptor,"rule selectVariableList");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:425:2: ( SELECT_TERM ( selectModifier )? selectVariableList ( datasets )? whereClause solutionModifier -> ^( SELECT ( selectModifier )? selectVariableList ( datasets )? whereClause ( solutionModifier )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:425:4: SELECT_TERM ( selectModifier )? selectVariableList ( datasets )? whereClause solutionModifier
            {
            SELECT_TERM107=(Token)match(input,SELECT_TERM,FOLLOW_SELECT_TERM_in_selectQuery1660); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SELECT_TERM.add(SELECT_TERM107);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:425:16: ( selectModifier )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( ((LA23_0>=DISTINCT_TERM && LA23_0<=REDUCED_TERM)) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:425:16: selectModifier
                    {
                    pushFollow(FOLLOW_selectModifier_in_selectQuery1662);
                    selectModifier108=selectModifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_selectModifier.add(selectModifier108.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_selectVariableList_in_selectQuery1665);
            selectVariableList109=selectVariableList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_selectVariableList.add(selectVariableList109.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:425:51: ( datasets )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==FROM_TERM) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:425:51: datasets
                    {
                    pushFollow(FOLLOW_datasets_in_selectQuery1667);
                    datasets110=datasets();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_datasets.add(datasets110.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_whereClause_in_selectQuery1670);
            whereClause111=whereClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_whereClause.add(whereClause111.getTree());
            pushFollow(FOLLOW_solutionModifier_in_selectQuery1672);
            solutionModifier112=solutionModifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_solutionModifier.add(solutionModifier112.getTree());


            // AST REWRITE
            // elements: datasets, selectModifier, selectVariableList, solutionModifier, whereClause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 426:2: -> ^( SELECT ( selectModifier )? selectVariableList ( datasets )? whereClause ( solutionModifier )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:426:5: ^( SELECT ( selectModifier )? selectVariableList ( datasets )? whereClause ( solutionModifier )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SELECT, "SELECT"), root_1);

                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:426:14: ( selectModifier )?
                if ( stream_selectModifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_selectModifier.nextTree());

                }
                stream_selectModifier.reset();
                adaptor.addChild(root_1, stream_selectVariableList.nextTree());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:426:49: ( datasets )?
                if ( stream_datasets.hasNext() ) {
                    adaptor.addChild(root_1, stream_datasets.nextTree());

                }
                stream_datasets.reset();
                adaptor.addChild(root_1, stream_whereClause.nextTree());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:426:71: ( solutionModifier )?
                if ( stream_solutionModifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_solutionModifier.nextTree());

                }
                stream_solutionModifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "selectQuery"

    public static class selectModifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selectModifier"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:429:1: selectModifier : ( DISTINCT_TERM -> MODIFIER_DISTINCT | REDUCED_TERM -> MODIFIER_REDUCED );
    public final SparqlOwlParser.selectModifier_return selectModifier() throws RecognitionException {
        SparqlOwlParser.selectModifier_return retval = new SparqlOwlParser.selectModifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DISTINCT_TERM113=null;
        Token REDUCED_TERM114=null;

        CommonTree DISTINCT_TERM113_tree=null;
        CommonTree REDUCED_TERM114_tree=null;
        RewriteRuleTokenStream stream_REDUCED_TERM=new RewriteRuleTokenStream(adaptor,"token REDUCED_TERM");
        RewriteRuleTokenStream stream_DISTINCT_TERM=new RewriteRuleTokenStream(adaptor,"token DISTINCT_TERM");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:430:2: ( DISTINCT_TERM -> MODIFIER_DISTINCT | REDUCED_TERM -> MODIFIER_REDUCED )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==DISTINCT_TERM) ) {
                alt25=1;
            }
            else if ( (LA25_0==REDUCED_TERM) ) {
                alt25=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:430:4: DISTINCT_TERM
                    {
                    DISTINCT_TERM113=(Token)match(input,DISTINCT_TERM,FOLLOW_DISTINCT_TERM_in_selectModifier1703); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DISTINCT_TERM.add(DISTINCT_TERM113);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 430:18: -> MODIFIER_DISTINCT
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(MODIFIER_DISTINCT, "MODIFIER_DISTINCT"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:431:4: REDUCED_TERM
                    {
                    REDUCED_TERM114=(Token)match(input,REDUCED_TERM,FOLLOW_REDUCED_TERM_in_selectModifier1712); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_REDUCED_TERM.add(REDUCED_TERM114);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 431:17: -> MODIFIER_REDUCED
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(MODIFIER_REDUCED, "MODIFIER_REDUCED"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "selectModifier"

    public static class selectVariableList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selectVariableList"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:434:1: selectVariableList : ( ( var )+ -> ^( VARS ( var )+ ) | ASTERISK_TERM -> ALL_VARS );
    public final SparqlOwlParser.selectVariableList_return selectVariableList() throws RecognitionException {
        SparqlOwlParser.selectVariableList_return retval = new SparqlOwlParser.selectVariableList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ASTERISK_TERM116=null;
        SparqlOwlParser.var_return var115 = null;


        CommonTree ASTERISK_TERM116_tree=null;
        RewriteRuleTokenStream stream_ASTERISK_TERM=new RewriteRuleTokenStream(adaptor,"token ASTERISK_TERM");
        RewriteRuleSubtreeStream stream_var=new RewriteRuleSubtreeStream(adaptor,"rule var");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:435:2: ( ( var )+ -> ^( VARS ( var )+ ) | ASTERISK_TERM -> ALL_VARS )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0>=VAR1 && LA27_0<=VAR2)) ) {
                alt27=1;
            }
            else if ( (LA27_0==ASTERISK_TERM) ) {
                alt27=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:435:4: ( var )+
                    {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:435:4: ( var )+
                    int cnt26=0;
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( ((LA26_0>=VAR1 && LA26_0<=VAR2)) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:435:4: var
                    	    {
                    	    pushFollow(FOLLOW_var_in_selectVariableList1727);
                    	    var115=var();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_var.add(var115.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt26 >= 1 ) break loop26;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(26, input);
                                throw eee;
                        }
                        cnt26++;
                    } while (true);



                    // AST REWRITE
                    // elements: var
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 435:9: -> ^( VARS ( var )+ )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:435:12: ^( VARS ( var )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VARS, "VARS"), root_1);

                        if ( !(stream_var.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_var.hasNext() ) {
                            adaptor.addChild(root_1, stream_var.nextTree());

                        }
                        stream_var.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:436:4: ASTERISK_TERM
                    {
                    ASTERISK_TERM116=(Token)match(input,ASTERISK_TERM,FOLLOW_ASTERISK_TERM_in_selectVariableList1742); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ASTERISK_TERM.add(ASTERISK_TERM116);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 436:18: -> ALL_VARS
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(ALL_VARS, "ALL_VARS"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "selectVariableList"

    public static class constructQuery_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructQuery"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:442:1: constructQuery : CONSTRUCT_TERM constructTemplate ( datasets )? whereClause solutionModifier -> ^( CONSTRUCT constructTemplate ( datasets )? whereClause ( solutionModifier )? ) ;
    public final SparqlOwlParser.constructQuery_return constructQuery() throws RecognitionException {
        SparqlOwlParser.constructQuery_return retval = new SparqlOwlParser.constructQuery_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token CONSTRUCT_TERM117=null;
        SparqlOwlParser.constructTemplate_return constructTemplate118 = null;

        SparqlOwlParser.datasets_return datasets119 = null;

        SparqlOwlParser.whereClause_return whereClause120 = null;

        SparqlOwlParser.solutionModifier_return solutionModifier121 = null;


        CommonTree CONSTRUCT_TERM117_tree=null;
        RewriteRuleTokenStream stream_CONSTRUCT_TERM=new RewriteRuleTokenStream(adaptor,"token CONSTRUCT_TERM");
        RewriteRuleSubtreeStream stream_whereClause=new RewriteRuleSubtreeStream(adaptor,"rule whereClause");
        RewriteRuleSubtreeStream stream_datasets=new RewriteRuleSubtreeStream(adaptor,"rule datasets");
        RewriteRuleSubtreeStream stream_solutionModifier=new RewriteRuleSubtreeStream(adaptor,"rule solutionModifier");
        RewriteRuleSubtreeStream stream_constructTemplate=new RewriteRuleSubtreeStream(adaptor,"rule constructTemplate");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:443:2: ( CONSTRUCT_TERM constructTemplate ( datasets )? whereClause solutionModifier -> ^( CONSTRUCT constructTemplate ( datasets )? whereClause ( solutionModifier )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:443:4: CONSTRUCT_TERM constructTemplate ( datasets )? whereClause solutionModifier
            {
            CONSTRUCT_TERM117=(Token)match(input,CONSTRUCT_TERM,FOLLOW_CONSTRUCT_TERM_in_constructQuery1759); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CONSTRUCT_TERM.add(CONSTRUCT_TERM117);

            pushFollow(FOLLOW_constructTemplate_in_constructQuery1761);
            constructTemplate118=constructTemplate();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_constructTemplate.add(constructTemplate118.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:443:37: ( datasets )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==FROM_TERM) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:443:37: datasets
                    {
                    pushFollow(FOLLOW_datasets_in_constructQuery1763);
                    datasets119=datasets();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_datasets.add(datasets119.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_whereClause_in_constructQuery1766);
            whereClause120=whereClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_whereClause.add(whereClause120.getTree());
            pushFollow(FOLLOW_solutionModifier_in_constructQuery1768);
            solutionModifier121=solutionModifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_solutionModifier.add(solutionModifier121.getTree());


            // AST REWRITE
            // elements: datasets, constructTemplate, solutionModifier, whereClause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 444:2: -> ^( CONSTRUCT constructTemplate ( datasets )? whereClause ( solutionModifier )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:444:5: ^( CONSTRUCT constructTemplate ( datasets )? whereClause ( solutionModifier )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CONSTRUCT, "CONSTRUCT"), root_1);

                adaptor.addChild(root_1, stream_constructTemplate.nextTree());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:444:36: ( datasets )?
                if ( stream_datasets.hasNext() ) {
                    adaptor.addChild(root_1, stream_datasets.nextTree());

                }
                stream_datasets.reset();
                adaptor.addChild(root_1, stream_whereClause.nextTree());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:444:58: ( solutionModifier )?
                if ( stream_solutionModifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_solutionModifier.nextTree());

                }
                stream_solutionModifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "constructQuery"

    public static class describeQuery_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "describeQuery"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:450:1: describeQuery : DESCRIBE_TERM describeTargets ( datasets )? ( whereClause )? solutionModifier -> ^( DESCRIBE describeTargets ( datasets )? ( whereClause )? ( solutionModifier )? ) ;
    public final SparqlOwlParser.describeQuery_return describeQuery() throws RecognitionException {
        SparqlOwlParser.describeQuery_return retval = new SparqlOwlParser.describeQuery_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DESCRIBE_TERM122=null;
        SparqlOwlParser.describeTargets_return describeTargets123 = null;

        SparqlOwlParser.datasets_return datasets124 = null;

        SparqlOwlParser.whereClause_return whereClause125 = null;

        SparqlOwlParser.solutionModifier_return solutionModifier126 = null;


        CommonTree DESCRIBE_TERM122_tree=null;
        RewriteRuleTokenStream stream_DESCRIBE_TERM=new RewriteRuleTokenStream(adaptor,"token DESCRIBE_TERM");
        RewriteRuleSubtreeStream stream_whereClause=new RewriteRuleSubtreeStream(adaptor,"rule whereClause");
        RewriteRuleSubtreeStream stream_datasets=new RewriteRuleSubtreeStream(adaptor,"rule datasets");
        RewriteRuleSubtreeStream stream_solutionModifier=new RewriteRuleSubtreeStream(adaptor,"rule solutionModifier");
        RewriteRuleSubtreeStream stream_describeTargets=new RewriteRuleSubtreeStream(adaptor,"rule describeTargets");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:451:2: ( DESCRIBE_TERM describeTargets ( datasets )? ( whereClause )? solutionModifier -> ^( DESCRIBE describeTargets ( datasets )? ( whereClause )? ( solutionModifier )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:451:4: DESCRIBE_TERM describeTargets ( datasets )? ( whereClause )? solutionModifier
            {
            DESCRIBE_TERM122=(Token)match(input,DESCRIBE_TERM,FOLLOW_DESCRIBE_TERM_in_describeQuery1799); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DESCRIBE_TERM.add(DESCRIBE_TERM122);

            pushFollow(FOLLOW_describeTargets_in_describeQuery1801);
            describeTargets123=describeTargets();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_describeTargets.add(describeTargets123.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:451:34: ( datasets )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==FROM_TERM) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:451:34: datasets
                    {
                    pushFollow(FOLLOW_datasets_in_describeQuery1803);
                    datasets124=datasets();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_datasets.add(datasets124.getTree());

                    }
                    break;

            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:451:44: ( whereClause )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==OPEN_CURLY_BRACE||LA30_0==WHERE_TERM) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:451:44: whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_describeQuery1806);
                    whereClause125=whereClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_whereClause.add(whereClause125.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_solutionModifier_in_describeQuery1809);
            solutionModifier126=solutionModifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_solutionModifier.add(solutionModifier126.getTree());


            // AST REWRITE
            // elements: datasets, solutionModifier, describeTargets, whereClause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 452:2: -> ^( DESCRIBE describeTargets ( datasets )? ( whereClause )? ( solutionModifier )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:453:3: ^( DESCRIBE describeTargets ( datasets )? ( whereClause )? ( solutionModifier )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DESCRIBE, "DESCRIBE"), root_1);

                adaptor.addChild(root_1, stream_describeTargets.nextTree());
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:453:30: ( datasets )?
                if ( stream_datasets.hasNext() ) {
                    adaptor.addChild(root_1, stream_datasets.nextTree());

                }
                stream_datasets.reset();
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:453:40: ( whereClause )?
                if ( stream_whereClause.hasNext() ) {
                    adaptor.addChild(root_1, stream_whereClause.nextTree());

                }
                stream_whereClause.reset();
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:453:53: ( solutionModifier )?
                if ( stream_solutionModifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_solutionModifier.nextTree());

                }
                stream_solutionModifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "describeQuery"

    public static class describeTargets_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "describeTargets"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:456:1: describeTargets : ( ( varOrIRIref )+ -> ^( VARS_OR_IRIS ( varOrIRIref )+ ) | ASTERISK_TERM -> ALL_VARS );
    public final SparqlOwlParser.describeTargets_return describeTargets() throws RecognitionException {
        SparqlOwlParser.describeTargets_return retval = new SparqlOwlParser.describeTargets_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ASTERISK_TERM128=null;
        SparqlOwlParser.varOrIRIref_return varOrIRIref127 = null;


        CommonTree ASTERISK_TERM128_tree=null;
        RewriteRuleTokenStream stream_ASTERISK_TERM=new RewriteRuleTokenStream(adaptor,"token ASTERISK_TERM");
        RewriteRuleSubtreeStream stream_varOrIRIref=new RewriteRuleSubtreeStream(adaptor,"rule varOrIRIref");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:457:2: ( ( varOrIRIref )+ -> ^( VARS_OR_IRIS ( varOrIRIref )+ ) | ASTERISK_TERM -> ALL_VARS )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==IRI_REF_TERM||LA32_0==PNAME_NS||(LA32_0>=VAR1 && LA32_0<=VAR2)||LA32_0==PNAME_LN) ) {
                alt32=1;
            }
            else if ( (LA32_0==ASTERISK_TERM) ) {
                alt32=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:457:4: ( varOrIRIref )+
                    {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:457:4: ( varOrIRIref )+
                    int cnt31=0;
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==IRI_REF_TERM||LA31_0==PNAME_NS||(LA31_0>=VAR1 && LA31_0<=VAR2)||LA31_0==PNAME_LN) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:457:4: varOrIRIref
                    	    {
                    	    pushFollow(FOLLOW_varOrIRIref_in_describeTargets1840);
                    	    varOrIRIref127=varOrIRIref();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_varOrIRIref.add(varOrIRIref127.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt31 >= 1 ) break loop31;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(31, input);
                                throw eee;
                        }
                        cnt31++;
                    } while (true);



                    // AST REWRITE
                    // elements: varOrIRIref
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 457:17: -> ^( VARS_OR_IRIS ( varOrIRIref )+ )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:457:20: ^( VARS_OR_IRIS ( varOrIRIref )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VARS_OR_IRIS, "VARS_OR_IRIS"), root_1);

                        if ( !(stream_varOrIRIref.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_varOrIRIref.hasNext() ) {
                            adaptor.addChild(root_1, stream_varOrIRIref.nextTree());

                        }
                        stream_varOrIRIref.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:458:4: ASTERISK_TERM
                    {
                    ASTERISK_TERM128=(Token)match(input,ASTERISK_TERM,FOLLOW_ASTERISK_TERM_in_describeTargets1855); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ASTERISK_TERM.add(ASTERISK_TERM128);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 458:18: -> ALL_VARS
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(ALL_VARS, "ALL_VARS"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "describeTargets"

    public static class askQuery_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "askQuery"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:464:1: askQuery : ASK_TERM ( datasets )? whereClause -> ^( ASK ( datasets )? whereClause ) ;
    public final SparqlOwlParser.askQuery_return askQuery() throws RecognitionException {
        SparqlOwlParser.askQuery_return retval = new SparqlOwlParser.askQuery_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ASK_TERM129=null;
        SparqlOwlParser.datasets_return datasets130 = null;

        SparqlOwlParser.whereClause_return whereClause131 = null;


        CommonTree ASK_TERM129_tree=null;
        RewriteRuleTokenStream stream_ASK_TERM=new RewriteRuleTokenStream(adaptor,"token ASK_TERM");
        RewriteRuleSubtreeStream stream_whereClause=new RewriteRuleSubtreeStream(adaptor,"rule whereClause");
        RewriteRuleSubtreeStream stream_datasets=new RewriteRuleSubtreeStream(adaptor,"rule datasets");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:465:2: ( ASK_TERM ( datasets )? whereClause -> ^( ASK ( datasets )? whereClause ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:465:4: ASK_TERM ( datasets )? whereClause
            {
            ASK_TERM129=(Token)match(input,ASK_TERM,FOLLOW_ASK_TERM_in_askQuery1872); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ASK_TERM.add(ASK_TERM129);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:465:13: ( datasets )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==FROM_TERM) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:465:13: datasets
                    {
                    pushFollow(FOLLOW_datasets_in_askQuery1874);
                    datasets130=datasets();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_datasets.add(datasets130.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_whereClause_in_askQuery1877);
            whereClause131=whereClause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_whereClause.add(whereClause131.getTree());


            // AST REWRITE
            // elements: datasets, whereClause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 466:2: -> ^( ASK ( datasets )? whereClause )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:467:3: ^( ASK ( datasets )? whereClause )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ASK, "ASK"), root_1);

                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:467:9: ( datasets )?
                if ( stream_datasets.hasNext() ) {
                    adaptor.addChild(root_1, stream_datasets.nextTree());

                }
                stream_datasets.reset();
                adaptor.addChild(root_1, stream_whereClause.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "askQuery"

    public static class datasets_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "datasets"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:473:1: datasets : ( datasetClause )+ -> ^( DATASETS ( datasetClause )+ ) ;
    public final SparqlOwlParser.datasets_return datasets() throws RecognitionException {
        SparqlOwlParser.datasets_return retval = new SparqlOwlParser.datasets_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.datasetClause_return datasetClause132 = null;


        RewriteRuleSubtreeStream stream_datasetClause=new RewriteRuleSubtreeStream(adaptor,"rule datasetClause");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:474:2: ( ( datasetClause )+ -> ^( DATASETS ( datasetClause )+ ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:474:4: ( datasetClause )+
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:474:4: ( datasetClause )+
            int cnt34=0;
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==FROM_TERM) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:474:4: datasetClause
            	    {
            	    pushFollow(FOLLOW_datasetClause_in_datasets1904);
            	    datasetClause132=datasetClause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_datasetClause.add(datasetClause132.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);



            // AST REWRITE
            // elements: datasetClause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 475:2: -> ^( DATASETS ( datasetClause )+ )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:475:5: ^( DATASETS ( datasetClause )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DATASETS, "DATASETS"), root_1);

                if ( !(stream_datasetClause.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_datasetClause.hasNext() ) {
                    adaptor.addChild(root_1, stream_datasetClause.nextTree());

                }
                stream_datasetClause.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "datasets"

    public static class datasetClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "datasetClause"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:481:1: datasetClause : FROM_TERM ( defaultGraphClause | namedGraphClause ) ;
    public final SparqlOwlParser.datasetClause_return datasetClause() throws RecognitionException {
        SparqlOwlParser.datasetClause_return retval = new SparqlOwlParser.datasetClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token FROM_TERM133=null;
        SparqlOwlParser.defaultGraphClause_return defaultGraphClause134 = null;

        SparqlOwlParser.namedGraphClause_return namedGraphClause135 = null;


        CommonTree FROM_TERM133_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:482:2: ( FROM_TERM ( defaultGraphClause | namedGraphClause ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:482:4: FROM_TERM ( defaultGraphClause | namedGraphClause )
            {
            root_0 = (CommonTree)adaptor.nil();

            FROM_TERM133=(Token)match(input,FROM_TERM,FOLLOW_FROM_TERM_in_datasetClause1928); if (state.failed) return retval;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:483:3: ( defaultGraphClause | namedGraphClause )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==IRI_REF_TERM||LA35_0==PNAME_NS||LA35_0==PNAME_LN) ) {
                alt35=1;
            }
            else if ( (LA35_0==NAMED_TERM) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:483:5: defaultGraphClause
                    {
                    pushFollow(FOLLOW_defaultGraphClause_in_datasetClause1935);
                    defaultGraphClause134=defaultGraphClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, defaultGraphClause134.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:484:5: namedGraphClause
                    {
                    pushFollow(FOLLOW_namedGraphClause_in_datasetClause1941);
                    namedGraphClause135=namedGraphClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, namedGraphClause135.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "datasetClause"

    public static class defaultGraphClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "defaultGraphClause"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:491:1: defaultGraphClause : sourceSelector -> ^( DEFAULT_GRAPH sourceSelector ) ;
    public final SparqlOwlParser.defaultGraphClause_return defaultGraphClause() throws RecognitionException {
        SparqlOwlParser.defaultGraphClause_return retval = new SparqlOwlParser.defaultGraphClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.sourceSelector_return sourceSelector136 = null;


        RewriteRuleSubtreeStream stream_sourceSelector=new RewriteRuleSubtreeStream(adaptor,"rule sourceSelector");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:492:2: ( sourceSelector -> ^( DEFAULT_GRAPH sourceSelector ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:492:4: sourceSelector
            {
            pushFollow(FOLLOW_sourceSelector_in_defaultGraphClause1958);
            sourceSelector136=sourceSelector();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sourceSelector.add(sourceSelector136.getTree());


            // AST REWRITE
            // elements: sourceSelector
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 493:2: -> ^( DEFAULT_GRAPH sourceSelector )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:493:5: ^( DEFAULT_GRAPH sourceSelector )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DEFAULT_GRAPH, "DEFAULT_GRAPH"), root_1);

                adaptor.addChild(root_1, stream_sourceSelector.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "defaultGraphClause"

    public static class namedGraphClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "namedGraphClause"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:499:1: namedGraphClause : NAMED_TERM sourceSelector -> ^( NAMED_GRAPH sourceSelector ) ;
    public final SparqlOwlParser.namedGraphClause_return namedGraphClause() throws RecognitionException {
        SparqlOwlParser.namedGraphClause_return retval = new SparqlOwlParser.namedGraphClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token NAMED_TERM137=null;
        SparqlOwlParser.sourceSelector_return sourceSelector138 = null;


        CommonTree NAMED_TERM137_tree=null;
        RewriteRuleTokenStream stream_NAMED_TERM=new RewriteRuleTokenStream(adaptor,"token NAMED_TERM");
        RewriteRuleSubtreeStream stream_sourceSelector=new RewriteRuleSubtreeStream(adaptor,"rule sourceSelector");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:500:2: ( NAMED_TERM sourceSelector -> ^( NAMED_GRAPH sourceSelector ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:500:4: NAMED_TERM sourceSelector
            {
            NAMED_TERM137=(Token)match(input,NAMED_TERM,FOLLOW_NAMED_TERM_in_namedGraphClause1981); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_NAMED_TERM.add(NAMED_TERM137);

            pushFollow(FOLLOW_sourceSelector_in_namedGraphClause1983);
            sourceSelector138=sourceSelector();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sourceSelector.add(sourceSelector138.getTree());


            // AST REWRITE
            // elements: sourceSelector
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 501:2: -> ^( NAMED_GRAPH sourceSelector )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:501:5: ^( NAMED_GRAPH sourceSelector )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NAMED_GRAPH, "NAMED_GRAPH"), root_1);

                adaptor.addChild(root_1, stream_sourceSelector.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "namedGraphClause"

    public static class sourceSelector_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sourceSelector"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:507:1: sourceSelector : iriRef ;
    public final SparqlOwlParser.sourceSelector_return sourceSelector() throws RecognitionException {
        SparqlOwlParser.sourceSelector_return retval = new SparqlOwlParser.sourceSelector_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef139 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:508:2: ( iriRef )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:508:4: iriRef
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_iriRef_in_sourceSelector2006);
            iriRef139=iriRef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, iriRef139.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "sourceSelector"

    public static class whereClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "whereClause"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:514:1: whereClause : ( WHERE_TERM )? groupGraphPattern -> ^( WHERE_CLAUSE groupGraphPattern ) ;
    public final SparqlOwlParser.whereClause_return whereClause() throws RecognitionException {
        SparqlOwlParser.whereClause_return retval = new SparqlOwlParser.whereClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token WHERE_TERM140=null;
        SparqlOwlParser.groupGraphPattern_return groupGraphPattern141 = null;


        CommonTree WHERE_TERM140_tree=null;
        RewriteRuleTokenStream stream_WHERE_TERM=new RewriteRuleTokenStream(adaptor,"token WHERE_TERM");
        RewriteRuleSubtreeStream stream_groupGraphPattern=new RewriteRuleSubtreeStream(adaptor,"rule groupGraphPattern");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:515:2: ( ( WHERE_TERM )? groupGraphPattern -> ^( WHERE_CLAUSE groupGraphPattern ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:515:4: ( WHERE_TERM )? groupGraphPattern
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:515:4: ( WHERE_TERM )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==WHERE_TERM) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:515:4: WHERE_TERM
                    {
                    WHERE_TERM140=(Token)match(input,WHERE_TERM,FOLLOW_WHERE_TERM_in_whereClause2019); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_WHERE_TERM.add(WHERE_TERM140);


                    }
                    break;

            }

            pushFollow(FOLLOW_groupGraphPattern_in_whereClause2022);
            groupGraphPattern141=groupGraphPattern();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_groupGraphPattern.add(groupGraphPattern141.getTree());


            // AST REWRITE
            // elements: groupGraphPattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 516:2: -> ^( WHERE_CLAUSE groupGraphPattern )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:516:5: ^( WHERE_CLAUSE groupGraphPattern )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(WHERE_CLAUSE, "WHERE_CLAUSE"), root_1);

                adaptor.addChild(root_1, stream_groupGraphPattern.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "whereClause"

    public static class solutionModifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "solutionModifier"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:522:1: solutionModifier : ( orderClause )? ( limitOffsetClauses )? ;
    public final SparqlOwlParser.solutionModifier_return solutionModifier() throws RecognitionException {
        SparqlOwlParser.solutionModifier_return retval = new SparqlOwlParser.solutionModifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.orderClause_return orderClause142 = null;

        SparqlOwlParser.limitOffsetClauses_return limitOffsetClauses143 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:523:2: ( ( orderClause )? ( limitOffsetClauses )? )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:523:4: ( orderClause )? ( limitOffsetClauses )?
            {
            root_0 = (CommonTree)adaptor.nil();

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:523:4: ( orderClause )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==ORDER_TERM) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:523:4: orderClause
                    {
                    pushFollow(FOLLOW_orderClause_in_solutionModifier2044);
                    orderClause142=orderClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, orderClause142.getTree());

                    }
                    break;

            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:523:17: ( limitOffsetClauses )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( ((LA38_0>=LIMIT_TERM && LA38_0<=OFFSET_TERM)) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:523:17: limitOffsetClauses
                    {
                    pushFollow(FOLLOW_limitOffsetClauses_in_solutionModifier2047);
                    limitOffsetClauses143=limitOffsetClauses();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, limitOffsetClauses143.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "solutionModifier"

    public static class limitOffsetClauses_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "limitOffsetClauses"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:529:1: limitOffsetClauses : ( limitClause ( offsetClause )? | offsetClause ( limitClause )? );
    public final SparqlOwlParser.limitOffsetClauses_return limitOffsetClauses() throws RecognitionException {
        SparqlOwlParser.limitOffsetClauses_return retval = new SparqlOwlParser.limitOffsetClauses_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.limitClause_return limitClause144 = null;

        SparqlOwlParser.offsetClause_return offsetClause145 = null;

        SparqlOwlParser.offsetClause_return offsetClause146 = null;

        SparqlOwlParser.limitClause_return limitClause147 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:530:2: ( limitClause ( offsetClause )? | offsetClause ( limitClause )? )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==LIMIT_TERM) ) {
                alt41=1;
            }
            else if ( (LA41_0==OFFSET_TERM) ) {
                alt41=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:530:4: limitClause ( offsetClause )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2061);
                    limitClause144=limitClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, limitClause144.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:530:16: ( offsetClause )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==OFFSET_TERM) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:530:16: offsetClause
                            {
                            pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2063);
                            offsetClause145=offsetClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, offsetClause145.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:531:4: offsetClause ( limitClause )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses2069);
                    offsetClause146=offsetClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, offsetClause146.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:531:17: ( limitClause )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==LIMIT_TERM) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:531:17: limitClause
                            {
                            pushFollow(FOLLOW_limitClause_in_limitOffsetClauses2071);
                            limitClause147=limitClause();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, limitClause147.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "limitOffsetClauses"

    public static class orderClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "orderClause"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:537:1: orderClause : ORDER_TERM BY_TERM ( orderCondition )+ -> ^( ORDER_CLAUSE ( orderCondition )+ ) ;
    public final SparqlOwlParser.orderClause_return orderClause() throws RecognitionException {
        SparqlOwlParser.orderClause_return retval = new SparqlOwlParser.orderClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ORDER_TERM148=null;
        Token BY_TERM149=null;
        SparqlOwlParser.orderCondition_return orderCondition150 = null;


        CommonTree ORDER_TERM148_tree=null;
        CommonTree BY_TERM149_tree=null;
        RewriteRuleTokenStream stream_BY_TERM=new RewriteRuleTokenStream(adaptor,"token BY_TERM");
        RewriteRuleTokenStream stream_ORDER_TERM=new RewriteRuleTokenStream(adaptor,"token ORDER_TERM");
        RewriteRuleSubtreeStream stream_orderCondition=new RewriteRuleSubtreeStream(adaptor,"rule orderCondition");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:538:2: ( ORDER_TERM BY_TERM ( orderCondition )+ -> ^( ORDER_CLAUSE ( orderCondition )+ ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:538:4: ORDER_TERM BY_TERM ( orderCondition )+
            {
            ORDER_TERM148=(Token)match(input,ORDER_TERM,FOLLOW_ORDER_TERM_in_orderClause2085); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ORDER_TERM.add(ORDER_TERM148);

            BY_TERM149=(Token)match(input,BY_TERM,FOLLOW_BY_TERM_in_orderClause2087); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_BY_TERM.add(BY_TERM149);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:538:23: ( orderCondition )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==DATATYPE_TERM||LA42_0==OPEN_BRACE||LA42_0==IRI_REF_TERM||LA42_0==PNAME_NS||(LA42_0>=ASC_TERM && LA42_0<=DESC_TERM)||(LA42_0>=VAR1 && LA42_0<=VAR2)||(LA42_0>=STR_TERM && LA42_0<=REGEX_TERM)||LA42_0==PNAME_LN) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:538:23: orderCondition
            	    {
            	    pushFollow(FOLLOW_orderCondition_in_orderClause2089);
            	    orderCondition150=orderCondition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_orderCondition.add(orderCondition150.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt42 >= 1 ) break loop42;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(42, input);
                        throw eee;
                }
                cnt42++;
            } while (true);



            // AST REWRITE
            // elements: orderCondition
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 539:2: -> ^( ORDER_CLAUSE ( orderCondition )+ )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:539:5: ^( ORDER_CLAUSE ( orderCondition )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ORDER_CLAUSE, "ORDER_CLAUSE"), root_1);

                if ( !(stream_orderCondition.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_orderCondition.hasNext() ) {
                    adaptor.addChild(root_1, stream_orderCondition.nextTree());

                }
                stream_orderCondition.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "orderClause"

    public static class orderCondition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "orderCondition"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:545:1: orderCondition : ( ASC_TERM brackettedExpression -> ^( ORDER_CONDITION_ASC brackettedExpression ) | DESC_TERM brackettedExpression -> ^( ORDER_CONDITION_DESC brackettedExpression ) | constraint -> ^( ORDER_CONDITION_UNDEF constraint ) | var -> ^( ORDER_CONDITION_UNDEF var ) );
    public final SparqlOwlParser.orderCondition_return orderCondition() throws RecognitionException {
        SparqlOwlParser.orderCondition_return retval = new SparqlOwlParser.orderCondition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ASC_TERM151=null;
        Token DESC_TERM153=null;
        SparqlOwlParser.brackettedExpression_return brackettedExpression152 = null;

        SparqlOwlParser.brackettedExpression_return brackettedExpression154 = null;

        SparqlOwlParser.constraint_return constraint155 = null;

        SparqlOwlParser.var_return var156 = null;


        CommonTree ASC_TERM151_tree=null;
        CommonTree DESC_TERM153_tree=null;
        RewriteRuleTokenStream stream_DESC_TERM=new RewriteRuleTokenStream(adaptor,"token DESC_TERM");
        RewriteRuleTokenStream stream_ASC_TERM=new RewriteRuleTokenStream(adaptor,"token ASC_TERM");
        RewriteRuleSubtreeStream stream_var=new RewriteRuleSubtreeStream(adaptor,"rule var");
        RewriteRuleSubtreeStream stream_constraint=new RewriteRuleSubtreeStream(adaptor,"rule constraint");
        RewriteRuleSubtreeStream stream_brackettedExpression=new RewriteRuleSubtreeStream(adaptor,"rule brackettedExpression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:546:2: ( ASC_TERM brackettedExpression -> ^( ORDER_CONDITION_ASC brackettedExpression ) | DESC_TERM brackettedExpression -> ^( ORDER_CONDITION_DESC brackettedExpression ) | constraint -> ^( ORDER_CONDITION_UNDEF constraint ) | var -> ^( ORDER_CONDITION_UNDEF var ) )
            int alt43=4;
            switch ( input.LA(1) ) {
            case ASC_TERM:
                {
                alt43=1;
                }
                break;
            case DESC_TERM:
                {
                alt43=2;
                }
                break;
            case DATATYPE_TERM:
            case OPEN_BRACE:
            case IRI_REF_TERM:
            case PNAME_NS:
            case STR_TERM:
            case LANG_TERM:
            case LANGMATCHES_TERM:
            case BOUND_TERM:
            case SAMETERM_TERM:
            case ISIRI_TERM:
            case ISURI_TERM:
            case ISBLANK_TERM:
            case ISLITERAL_TERM:
            case REGEX_TERM:
            case PNAME_LN:
                {
                alt43=3;
                }
                break;
            case VAR1:
            case VAR2:
                {
                alt43=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }

            switch (alt43) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:546:4: ASC_TERM brackettedExpression
                    {
                    ASC_TERM151=(Token)match(input,ASC_TERM,FOLLOW_ASC_TERM_in_orderCondition2113); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ASC_TERM.add(ASC_TERM151);

                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition2115);
                    brackettedExpression152=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_brackettedExpression.add(brackettedExpression152.getTree());


                    // AST REWRITE
                    // elements: brackettedExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 546:34: -> ^( ORDER_CONDITION_ASC brackettedExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:546:37: ^( ORDER_CONDITION_ASC brackettedExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ORDER_CONDITION_ASC, "ORDER_CONDITION_ASC"), root_1);

                        adaptor.addChild(root_1, stream_brackettedExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:547:4: DESC_TERM brackettedExpression
                    {
                    DESC_TERM153=(Token)match(input,DESC_TERM,FOLLOW_DESC_TERM_in_orderCondition2128); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DESC_TERM.add(DESC_TERM153);

                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition2130);
                    brackettedExpression154=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_brackettedExpression.add(brackettedExpression154.getTree());


                    // AST REWRITE
                    // elements: brackettedExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 547:35: -> ^( ORDER_CONDITION_DESC brackettedExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:547:38: ^( ORDER_CONDITION_DESC brackettedExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ORDER_CONDITION_DESC, "ORDER_CONDITION_DESC"), root_1);

                        adaptor.addChild(root_1, stream_brackettedExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:548:4: constraint
                    {
                    pushFollow(FOLLOW_constraint_in_orderCondition2143);
                    constraint155=constraint();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_constraint.add(constraint155.getTree());


                    // AST REWRITE
                    // elements: constraint
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 548:15: -> ^( ORDER_CONDITION_UNDEF constraint )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:548:18: ^( ORDER_CONDITION_UNDEF constraint )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ORDER_CONDITION_UNDEF, "ORDER_CONDITION_UNDEF"), root_1);

                        adaptor.addChild(root_1, stream_constraint.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:549:4: var
                    {
                    pushFollow(FOLLOW_var_in_orderCondition2156);
                    var156=var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var.add(var156.getTree());


                    // AST REWRITE
                    // elements: var
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 549:8: -> ^( ORDER_CONDITION_UNDEF var )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:549:11: ^( ORDER_CONDITION_UNDEF var )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ORDER_CONDITION_UNDEF, "ORDER_CONDITION_UNDEF"), root_1);

                        adaptor.addChild(root_1, stream_var.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "orderCondition"

    public static class limitClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "limitClause"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:555:1: limitClause : LIMIT_TERM INTEGER -> ^( LIMIT_CLAUSE INTEGER ) ;
    public final SparqlOwlParser.limitClause_return limitClause() throws RecognitionException {
        SparqlOwlParser.limitClause_return retval = new SparqlOwlParser.limitClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LIMIT_TERM157=null;
        Token INTEGER158=null;

        CommonTree LIMIT_TERM157_tree=null;
        CommonTree INTEGER158_tree=null;
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_LIMIT_TERM=new RewriteRuleTokenStream(adaptor,"token LIMIT_TERM");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:556:2: ( LIMIT_TERM INTEGER -> ^( LIMIT_CLAUSE INTEGER ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:556:4: LIMIT_TERM INTEGER
            {
            LIMIT_TERM157=(Token)match(input,LIMIT_TERM,FOLLOW_LIMIT_TERM_in_limitClause2177); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LIMIT_TERM.add(LIMIT_TERM157);

            INTEGER158=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_limitClause2179); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INTEGER.add(INTEGER158);



            // AST REWRITE
            // elements: INTEGER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 557:2: -> ^( LIMIT_CLAUSE INTEGER )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:557:5: ^( LIMIT_CLAUSE INTEGER )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LIMIT_CLAUSE, "LIMIT_CLAUSE"), root_1);

                adaptor.addChild(root_1, stream_INTEGER.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "limitClause"

    public static class offsetClause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "offsetClause"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:563:1: offsetClause : OFFSET_TERM INTEGER -> ^( OFFSET_CLAUSE INTEGER ) ;
    public final SparqlOwlParser.offsetClause_return offsetClause() throws RecognitionException {
        SparqlOwlParser.offsetClause_return retval = new SparqlOwlParser.offsetClause_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OFFSET_TERM159=null;
        Token INTEGER160=null;

        CommonTree OFFSET_TERM159_tree=null;
        CommonTree INTEGER160_tree=null;
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_OFFSET_TERM=new RewriteRuleTokenStream(adaptor,"token OFFSET_TERM");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:564:2: ( OFFSET_TERM INTEGER -> ^( OFFSET_CLAUSE INTEGER ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:564:4: OFFSET_TERM INTEGER
            {
            OFFSET_TERM159=(Token)match(input,OFFSET_TERM,FOLLOW_OFFSET_TERM_in_offsetClause2201); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OFFSET_TERM.add(OFFSET_TERM159);

            INTEGER160=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_offsetClause2203); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_INTEGER.add(INTEGER160);



            // AST REWRITE
            // elements: INTEGER
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 565:2: -> ^( OFFSET_CLAUSE INTEGER )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:565:5: ^( OFFSET_CLAUSE INTEGER )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OFFSET_CLAUSE, "OFFSET_CLAUSE"), root_1);

                adaptor.addChild(root_1, stream_INTEGER.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "offsetClause"

    public static class groupGraphPattern_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "groupGraphPattern"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:572:1: groupGraphPattern : OPEN_CURLY_BRACE ( groupGraphPatternNoBraces )? CLOSE_CURLY_BRACE -> ^( GROUP_GRAPH_PATTERN ( groupGraphPatternNoBraces )? ) ;
    public final SparqlOwlParser.groupGraphPattern_return groupGraphPattern() throws RecognitionException {
        SparqlOwlParser.groupGraphPattern_return retval = new SparqlOwlParser.groupGraphPattern_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_CURLY_BRACE161=null;
        Token CLOSE_CURLY_BRACE163=null;
        SparqlOwlParser.groupGraphPatternNoBraces_return groupGraphPatternNoBraces162 = null;


        CommonTree OPEN_CURLY_BRACE161_tree=null;
        CommonTree CLOSE_CURLY_BRACE163_tree=null;
        RewriteRuleTokenStream stream_OPEN_CURLY_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_CURLY_BRACE");
        RewriteRuleTokenStream stream_CLOSE_CURLY_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_CURLY_BRACE");
        RewriteRuleSubtreeStream stream_groupGraphPatternNoBraces=new RewriteRuleSubtreeStream(adaptor,"rule groupGraphPatternNoBraces");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:573:2: ( OPEN_CURLY_BRACE ( groupGraphPatternNoBraces )? CLOSE_CURLY_BRACE -> ^( GROUP_GRAPH_PATTERN ( groupGraphPatternNoBraces )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:573:4: OPEN_CURLY_BRACE ( groupGraphPatternNoBraces )? CLOSE_CURLY_BRACE
            {
            OPEN_CURLY_BRACE161=(Token)match(input,OPEN_CURLY_BRACE,FOLLOW_OPEN_CURLY_BRACE_in_groupGraphPattern2225); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_CURLY_BRACE.add(OPEN_CURLY_BRACE161);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:573:21: ( groupGraphPatternNoBraces )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==DECIMAL_TERM||LA44_0==FLOAT_TERM||LA44_0==INTEGER_TERM||LA44_0==STRING_TERM||(LA44_0>=INVERSE_TERM && LA44_0<=OPEN_SQUARE_BRACE)||(LA44_0>=NOT_TERM && LA44_0<=OPEN_CURLY_BRACE)||LA44_0==OPEN_BRACE||LA44_0==INTEGER||LA44_0==IRI_REF_TERM||LA44_0==PNAME_NS||(LA44_0>=OPTIONAL_TERM && LA44_0<=GRAPH_TERM)||LA44_0==FILTER_TERM||(LA44_0>=VAR1 && LA44_0<=VAR2)||(LA44_0>=DECIMAL && LA44_0<=BLANK_NODE_LABEL)) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:573:21: groupGraphPatternNoBraces
                    {
                    pushFollow(FOLLOW_groupGraphPatternNoBraces_in_groupGraphPattern2227);
                    groupGraphPatternNoBraces162=groupGraphPatternNoBraces();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_groupGraphPatternNoBraces.add(groupGraphPatternNoBraces162.getTree());

                    }
                    break;

            }

            CLOSE_CURLY_BRACE163=(Token)match(input,CLOSE_CURLY_BRACE,FOLLOW_CLOSE_CURLY_BRACE_in_groupGraphPattern2230); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_CURLY_BRACE.add(CLOSE_CURLY_BRACE163);



            // AST REWRITE
            // elements: groupGraphPatternNoBraces
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 574:2: -> ^( GROUP_GRAPH_PATTERN ( groupGraphPatternNoBraces )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:574:5: ^( GROUP_GRAPH_PATTERN ( groupGraphPatternNoBraces )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(GROUP_GRAPH_PATTERN, "GROUP_GRAPH_PATTERN"), root_1);

                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:574:27: ( groupGraphPatternNoBraces )?
                if ( stream_groupGraphPatternNoBraces.hasNext() ) {
                    adaptor.addChild(root_1, stream_groupGraphPatternNoBraces.nextTree());

                }
                stream_groupGraphPatternNoBraces.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "groupGraphPattern"

    public static class groupGraphPatternNoBraces_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "groupGraphPatternNoBraces"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:577:1: groupGraphPatternNoBraces : ( ( graphPatternNotTriples )=> graphPatternNotTriples ( DOT_TERM )? ( groupGraphPatternNoBraces )? | filter ( DOT_TERM )? ( groupGraphPatternNoBraces )? | ( triplesSameSubject DOT_TERM )=> triplesSameSubject DOT_TERM ( groupGraphPatternNoBraces )? | triplesSameSubject ( canFollowTriplesWithoutDot )? );
    public final SparqlOwlParser.groupGraphPatternNoBraces_return groupGraphPatternNoBraces() throws RecognitionException {
        SparqlOwlParser.groupGraphPatternNoBraces_return retval = new SparqlOwlParser.groupGraphPatternNoBraces_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOT_TERM165=null;
        Token DOT_TERM168=null;
        Token DOT_TERM171=null;
        SparqlOwlParser.graphPatternNotTriples_return graphPatternNotTriples164 = null;

        SparqlOwlParser.groupGraphPatternNoBraces_return groupGraphPatternNoBraces166 = null;

        SparqlOwlParser.filter_return filter167 = null;

        SparqlOwlParser.groupGraphPatternNoBraces_return groupGraphPatternNoBraces169 = null;

        SparqlOwlParser.triplesSameSubject_return triplesSameSubject170 = null;

        SparqlOwlParser.groupGraphPatternNoBraces_return groupGraphPatternNoBraces172 = null;

        SparqlOwlParser.triplesSameSubject_return triplesSameSubject173 = null;

        SparqlOwlParser.canFollowTriplesWithoutDot_return canFollowTriplesWithoutDot174 = null;


        CommonTree DOT_TERM165_tree=null;
        CommonTree DOT_TERM168_tree=null;
        CommonTree DOT_TERM171_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:2: ( ( graphPatternNotTriples )=> graphPatternNotTriples ( DOT_TERM )? ( groupGraphPatternNoBraces )? | filter ( DOT_TERM )? ( groupGraphPatternNoBraces )? | ( triplesSameSubject DOT_TERM )=> triplesSameSubject DOT_TERM ( groupGraphPatternNoBraces )? | triplesSameSubject ( canFollowTriplesWithoutDot )? )
            int alt51=4;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:4: ( graphPatternNotTriples )=> graphPatternNotTriples ( DOT_TERM )? ( groupGraphPatternNoBraces )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_graphPatternNotTriples_in_groupGraphPatternNoBraces2258);
                    graphPatternNotTriples164=graphPatternNotTriples();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, graphPatternNotTriples164.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:64: ( DOT_TERM )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==DOT_TERM) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:64: DOT_TERM
                            {
                            DOT_TERM165=(Token)match(input,DOT_TERM,FOLLOW_DOT_TERM_in_groupGraphPatternNoBraces2260); if (state.failed) return retval;

                            }
                            break;

                    }

                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:67: ( groupGraphPatternNoBraces )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==DECIMAL_TERM||LA46_0==FLOAT_TERM||LA46_0==INTEGER_TERM||LA46_0==STRING_TERM||(LA46_0>=INVERSE_TERM && LA46_0<=OPEN_SQUARE_BRACE)||(LA46_0>=NOT_TERM && LA46_0<=OPEN_CURLY_BRACE)||LA46_0==OPEN_BRACE||LA46_0==INTEGER||LA46_0==IRI_REF_TERM||LA46_0==PNAME_NS||(LA46_0>=OPTIONAL_TERM && LA46_0<=GRAPH_TERM)||LA46_0==FILTER_TERM||(LA46_0>=VAR1 && LA46_0<=VAR2)||(LA46_0>=DECIMAL && LA46_0<=BLANK_NODE_LABEL)) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:67: groupGraphPatternNoBraces
                            {
                            pushFollow(FOLLOW_groupGraphPatternNoBraces_in_groupGraphPatternNoBraces2264);
                            groupGraphPatternNoBraces166=groupGraphPatternNoBraces();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, groupGraphPatternNoBraces166.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:579:4: filter ( DOT_TERM )? ( groupGraphPatternNoBraces )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_filter_in_groupGraphPatternNoBraces2270);
                    filter167=filter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, filter167.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:579:19: ( DOT_TERM )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==DOT_TERM) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:579:19: DOT_TERM
                            {
                            DOT_TERM168=(Token)match(input,DOT_TERM,FOLLOW_DOT_TERM_in_groupGraphPatternNoBraces2272); if (state.failed) return retval;

                            }
                            break;

                    }

                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:579:22: ( groupGraphPatternNoBraces )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==DECIMAL_TERM||LA48_0==FLOAT_TERM||LA48_0==INTEGER_TERM||LA48_0==STRING_TERM||(LA48_0>=INVERSE_TERM && LA48_0<=OPEN_SQUARE_BRACE)||(LA48_0>=NOT_TERM && LA48_0<=OPEN_CURLY_BRACE)||LA48_0==OPEN_BRACE||LA48_0==INTEGER||LA48_0==IRI_REF_TERM||LA48_0==PNAME_NS||(LA48_0>=OPTIONAL_TERM && LA48_0<=GRAPH_TERM)||LA48_0==FILTER_TERM||(LA48_0>=VAR1 && LA48_0<=VAR2)||(LA48_0>=DECIMAL && LA48_0<=BLANK_NODE_LABEL)) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:579:22: groupGraphPatternNoBraces
                            {
                            pushFollow(FOLLOW_groupGraphPatternNoBraces_in_groupGraphPatternNoBraces2276);
                            groupGraphPatternNoBraces169=groupGraphPatternNoBraces();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, groupGraphPatternNoBraces169.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:580:4: ( triplesSameSubject DOT_TERM )=> triplesSameSubject DOT_TERM ( groupGraphPatternNoBraces )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_triplesSameSubject_in_groupGraphPatternNoBraces2289);
                    triplesSameSubject170=triplesSameSubject();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, triplesSameSubject170.getTree());
                    DOT_TERM171=(Token)match(input,DOT_TERM,FOLLOW_DOT_TERM_in_groupGraphPatternNoBraces2291); if (state.failed) return retval;
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:580:65: ( groupGraphPatternNoBraces )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);

                    if ( (LA49_0==DECIMAL_TERM||LA49_0==FLOAT_TERM||LA49_0==INTEGER_TERM||LA49_0==STRING_TERM||(LA49_0>=INVERSE_TERM && LA49_0<=OPEN_SQUARE_BRACE)||(LA49_0>=NOT_TERM && LA49_0<=OPEN_CURLY_BRACE)||LA49_0==OPEN_BRACE||LA49_0==INTEGER||LA49_0==IRI_REF_TERM||LA49_0==PNAME_NS||(LA49_0>=OPTIONAL_TERM && LA49_0<=GRAPH_TERM)||LA49_0==FILTER_TERM||(LA49_0>=VAR1 && LA49_0<=VAR2)||(LA49_0>=DECIMAL && LA49_0<=BLANK_NODE_LABEL)) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:580:65: groupGraphPatternNoBraces
                            {
                            pushFollow(FOLLOW_groupGraphPatternNoBraces_in_groupGraphPatternNoBraces2294);
                            groupGraphPatternNoBraces172=groupGraphPatternNoBraces();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, groupGraphPatternNoBraces172.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:581:4: triplesSameSubject ( canFollowTriplesWithoutDot )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_triplesSameSubject_in_groupGraphPatternNoBraces2300);
                    triplesSameSubject173=triplesSameSubject();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, triplesSameSubject173.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:581:23: ( canFollowTriplesWithoutDot )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==OPEN_CURLY_BRACE||(LA50_0>=OPTIONAL_TERM && LA50_0<=GRAPH_TERM)||LA50_0==FILTER_TERM) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:581:23: canFollowTriplesWithoutDot
                            {
                            pushFollow(FOLLOW_canFollowTriplesWithoutDot_in_groupGraphPatternNoBraces2302);
                            canFollowTriplesWithoutDot174=canFollowTriplesWithoutDot();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, canFollowTriplesWithoutDot174.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "groupGraphPatternNoBraces"

    public static class canFollowTriplesWithoutDot_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "canFollowTriplesWithoutDot"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:584:1: canFollowTriplesWithoutDot : ( graphPatternNotTriples ( DOT_TERM )? ( groupGraphPatternNoBraces )? | filter ( DOT_TERM )? ( groupGraphPatternNoBraces )? );
    public final SparqlOwlParser.canFollowTriplesWithoutDot_return canFollowTriplesWithoutDot() throws RecognitionException {
        SparqlOwlParser.canFollowTriplesWithoutDot_return retval = new SparqlOwlParser.canFollowTriplesWithoutDot_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOT_TERM176=null;
        Token DOT_TERM179=null;
        SparqlOwlParser.graphPatternNotTriples_return graphPatternNotTriples175 = null;

        SparqlOwlParser.groupGraphPatternNoBraces_return groupGraphPatternNoBraces177 = null;

        SparqlOwlParser.filter_return filter178 = null;

        SparqlOwlParser.groupGraphPatternNoBraces_return groupGraphPatternNoBraces180 = null;


        CommonTree DOT_TERM176_tree=null;
        CommonTree DOT_TERM179_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:585:2: ( graphPatternNotTriples ( DOT_TERM )? ( groupGraphPatternNoBraces )? | filter ( DOT_TERM )? ( groupGraphPatternNoBraces )? )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==OPEN_CURLY_BRACE||(LA56_0>=OPTIONAL_TERM && LA56_0<=GRAPH_TERM)) ) {
                alt56=1;
            }
            else if ( (LA56_0==FILTER_TERM) ) {
                alt56=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:585:4: graphPatternNotTriples ( DOT_TERM )? ( groupGraphPatternNoBraces )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_graphPatternNotTriples_in_canFollowTriplesWithoutDot2315);
                    graphPatternNotTriples175=graphPatternNotTriples();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, graphPatternNotTriples175.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:585:35: ( DOT_TERM )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==DOT_TERM) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:585:35: DOT_TERM
                            {
                            DOT_TERM176=(Token)match(input,DOT_TERM,FOLLOW_DOT_TERM_in_canFollowTriplesWithoutDot2317); if (state.failed) return retval;

                            }
                            break;

                    }

                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:585:38: ( groupGraphPatternNoBraces )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==DECIMAL_TERM||LA53_0==FLOAT_TERM||LA53_0==INTEGER_TERM||LA53_0==STRING_TERM||(LA53_0>=INVERSE_TERM && LA53_0<=OPEN_SQUARE_BRACE)||(LA53_0>=NOT_TERM && LA53_0<=OPEN_CURLY_BRACE)||LA53_0==OPEN_BRACE||LA53_0==INTEGER||LA53_0==IRI_REF_TERM||LA53_0==PNAME_NS||(LA53_0>=OPTIONAL_TERM && LA53_0<=GRAPH_TERM)||LA53_0==FILTER_TERM||(LA53_0>=VAR1 && LA53_0<=VAR2)||(LA53_0>=DECIMAL && LA53_0<=BLANK_NODE_LABEL)) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:585:38: groupGraphPatternNoBraces
                            {
                            pushFollow(FOLLOW_groupGraphPatternNoBraces_in_canFollowTriplesWithoutDot2321);
                            groupGraphPatternNoBraces177=groupGraphPatternNoBraces();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, groupGraphPatternNoBraces177.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:586:4: filter ( DOT_TERM )? ( groupGraphPatternNoBraces )?
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_filter_in_canFollowTriplesWithoutDot2327);
                    filter178=filter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, filter178.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:586:19: ( DOT_TERM )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==DOT_TERM) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:586:19: DOT_TERM
                            {
                            DOT_TERM179=(Token)match(input,DOT_TERM,FOLLOW_DOT_TERM_in_canFollowTriplesWithoutDot2329); if (state.failed) return retval;

                            }
                            break;

                    }

                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:586:22: ( groupGraphPatternNoBraces )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==DECIMAL_TERM||LA55_0==FLOAT_TERM||LA55_0==INTEGER_TERM||LA55_0==STRING_TERM||(LA55_0>=INVERSE_TERM && LA55_0<=OPEN_SQUARE_BRACE)||(LA55_0>=NOT_TERM && LA55_0<=OPEN_CURLY_BRACE)||LA55_0==OPEN_BRACE||LA55_0==INTEGER||LA55_0==IRI_REF_TERM||LA55_0==PNAME_NS||(LA55_0>=OPTIONAL_TERM && LA55_0<=GRAPH_TERM)||LA55_0==FILTER_TERM||(LA55_0>=VAR1 && LA55_0<=VAR2)||(LA55_0>=DECIMAL && LA55_0<=BLANK_NODE_LABEL)) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:586:22: groupGraphPatternNoBraces
                            {
                            pushFollow(FOLLOW_groupGraphPatternNoBraces_in_canFollowTriplesWithoutDot2333);
                            groupGraphPatternNoBraces180=groupGraphPatternNoBraces();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, groupGraphPatternNoBraces180.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "canFollowTriplesWithoutDot"

    public static class graphPatternNotTriples_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "graphPatternNotTriples"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:592:1: graphPatternNotTriples : ( optionalGraphPattern | groupOrUnionGraphPattern | graphGraphPattern );
    public final SparqlOwlParser.graphPatternNotTriples_return graphPatternNotTriples() throws RecognitionException {
        SparqlOwlParser.graphPatternNotTriples_return retval = new SparqlOwlParser.graphPatternNotTriples_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.optionalGraphPattern_return optionalGraphPattern181 = null;

        SparqlOwlParser.groupOrUnionGraphPattern_return groupOrUnionGraphPattern182 = null;

        SparqlOwlParser.graphGraphPattern_return graphGraphPattern183 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:593:2: ( optionalGraphPattern | groupOrUnionGraphPattern | graphGraphPattern )
            int alt57=3;
            switch ( input.LA(1) ) {
            case OPTIONAL_TERM:
                {
                alt57=1;
                }
                break;
            case OPEN_CURLY_BRACE:
                {
                alt57=2;
                }
                break;
            case GRAPH_TERM:
                {
                alt57=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }

            switch (alt57) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:593:4: optionalGraphPattern
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_optionalGraphPattern_in_graphPatternNotTriples2347);
                    optionalGraphPattern181=optionalGraphPattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionalGraphPattern181.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:594:4: groupOrUnionGraphPattern
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_groupOrUnionGraphPattern_in_graphPatternNotTriples2352);
                    groupOrUnionGraphPattern182=groupOrUnionGraphPattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, groupOrUnionGraphPattern182.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:595:4: graphGraphPattern
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_graphGraphPattern_in_graphPatternNotTriples2357);
                    graphGraphPattern183=graphGraphPattern();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, graphGraphPattern183.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "graphPatternNotTriples"

    public static class optionalGraphPattern_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "optionalGraphPattern"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:601:1: optionalGraphPattern : OPTIONAL_TERM groupGraphPattern -> ^( OPTIONAL_GRAPH_PATTERN groupGraphPattern ) ;
    public final SparqlOwlParser.optionalGraphPattern_return optionalGraphPattern() throws RecognitionException {
        SparqlOwlParser.optionalGraphPattern_return retval = new SparqlOwlParser.optionalGraphPattern_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPTIONAL_TERM184=null;
        SparqlOwlParser.groupGraphPattern_return groupGraphPattern185 = null;


        CommonTree OPTIONAL_TERM184_tree=null;
        RewriteRuleTokenStream stream_OPTIONAL_TERM=new RewriteRuleTokenStream(adaptor,"token OPTIONAL_TERM");
        RewriteRuleSubtreeStream stream_groupGraphPattern=new RewriteRuleSubtreeStream(adaptor,"rule groupGraphPattern");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:602:2: ( OPTIONAL_TERM groupGraphPattern -> ^( OPTIONAL_GRAPH_PATTERN groupGraphPattern ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:602:4: OPTIONAL_TERM groupGraphPattern
            {
            OPTIONAL_TERM184=(Token)match(input,OPTIONAL_TERM,FOLLOW_OPTIONAL_TERM_in_optionalGraphPattern2370); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPTIONAL_TERM.add(OPTIONAL_TERM184);

            pushFollow(FOLLOW_groupGraphPattern_in_optionalGraphPattern2372);
            groupGraphPattern185=groupGraphPattern();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_groupGraphPattern.add(groupGraphPattern185.getTree());


            // AST REWRITE
            // elements: groupGraphPattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 603:2: -> ^( OPTIONAL_GRAPH_PATTERN groupGraphPattern )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:603:5: ^( OPTIONAL_GRAPH_PATTERN groupGraphPattern )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPTIONAL_GRAPH_PATTERN, "OPTIONAL_GRAPH_PATTERN"), root_1);

                adaptor.addChild(root_1, stream_groupGraphPattern.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "optionalGraphPattern"

    public static class graphGraphPattern_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "graphGraphPattern"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:609:1: graphGraphPattern : GRAPH_TERM varOrIRIref groupGraphPattern -> ^( GRAPH_GRAPH_PATTERN ^( GRAPH_IDENTIFIER varOrIRIref ) groupGraphPattern ) ;
    public final SparqlOwlParser.graphGraphPattern_return graphGraphPattern() throws RecognitionException {
        SparqlOwlParser.graphGraphPattern_return retval = new SparqlOwlParser.graphGraphPattern_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token GRAPH_TERM186=null;
        SparqlOwlParser.varOrIRIref_return varOrIRIref187 = null;

        SparqlOwlParser.groupGraphPattern_return groupGraphPattern188 = null;


        CommonTree GRAPH_TERM186_tree=null;
        RewriteRuleTokenStream stream_GRAPH_TERM=new RewriteRuleTokenStream(adaptor,"token GRAPH_TERM");
        RewriteRuleSubtreeStream stream_varOrIRIref=new RewriteRuleSubtreeStream(adaptor,"rule varOrIRIref");
        RewriteRuleSubtreeStream stream_groupGraphPattern=new RewriteRuleSubtreeStream(adaptor,"rule groupGraphPattern");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:610:2: ( GRAPH_TERM varOrIRIref groupGraphPattern -> ^( GRAPH_GRAPH_PATTERN ^( GRAPH_IDENTIFIER varOrIRIref ) groupGraphPattern ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:610:4: GRAPH_TERM varOrIRIref groupGraphPattern
            {
            GRAPH_TERM186=(Token)match(input,GRAPH_TERM,FOLLOW_GRAPH_TERM_in_graphGraphPattern2394); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GRAPH_TERM.add(GRAPH_TERM186);

            pushFollow(FOLLOW_varOrIRIref_in_graphGraphPattern2396);
            varOrIRIref187=varOrIRIref();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_varOrIRIref.add(varOrIRIref187.getTree());
            pushFollow(FOLLOW_groupGraphPattern_in_graphGraphPattern2398);
            groupGraphPattern188=groupGraphPattern();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_groupGraphPattern.add(groupGraphPattern188.getTree());


            // AST REWRITE
            // elements: varOrIRIref, groupGraphPattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 611:2: -> ^( GRAPH_GRAPH_PATTERN ^( GRAPH_IDENTIFIER varOrIRIref ) groupGraphPattern )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:611:5: ^( GRAPH_GRAPH_PATTERN ^( GRAPH_IDENTIFIER varOrIRIref ) groupGraphPattern )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(GRAPH_GRAPH_PATTERN, "GRAPH_GRAPH_PATTERN"), root_1);

                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:611:27: ^( GRAPH_IDENTIFIER varOrIRIref )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(GRAPH_IDENTIFIER, "GRAPH_IDENTIFIER"), root_2);

                adaptor.addChild(root_2, stream_varOrIRIref.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_groupGraphPattern.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "graphGraphPattern"

    public static class groupOrUnionGraphPattern_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "groupOrUnionGraphPattern"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:617:1: groupOrUnionGraphPattern : ( groupGraphPattern -> groupGraphPattern ) ( UNION_TERM groupGraphPattern -> ^( UNION_GRAPH_PATTERN $groupOrUnionGraphPattern groupGraphPattern ) )* ;
    public final SparqlOwlParser.groupOrUnionGraphPattern_return groupOrUnionGraphPattern() throws RecognitionException {
        SparqlOwlParser.groupOrUnionGraphPattern_return retval = new SparqlOwlParser.groupOrUnionGraphPattern_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token UNION_TERM190=null;
        SparqlOwlParser.groupGraphPattern_return groupGraphPattern189 = null;

        SparqlOwlParser.groupGraphPattern_return groupGraphPattern191 = null;


        CommonTree UNION_TERM190_tree=null;
        RewriteRuleTokenStream stream_UNION_TERM=new RewriteRuleTokenStream(adaptor,"token UNION_TERM");
        RewriteRuleSubtreeStream stream_groupGraphPattern=new RewriteRuleSubtreeStream(adaptor,"rule groupGraphPattern");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:618:2: ( ( groupGraphPattern -> groupGraphPattern ) ( UNION_TERM groupGraphPattern -> ^( UNION_GRAPH_PATTERN $groupOrUnionGraphPattern groupGraphPattern ) )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:618:4: ( groupGraphPattern -> groupGraphPattern ) ( UNION_TERM groupGraphPattern -> ^( UNION_GRAPH_PATTERN $groupOrUnionGraphPattern groupGraphPattern ) )*
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:618:4: ( groupGraphPattern -> groupGraphPattern )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:618:6: groupGraphPattern
            {
            pushFollow(FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern2428);
            groupGraphPattern189=groupGraphPattern();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_groupGraphPattern.add(groupGraphPattern189.getTree());


            // AST REWRITE
            // elements: groupGraphPattern
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 618:24: -> groupGraphPattern
            {
                adaptor.addChild(root_0, stream_groupGraphPattern.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:619:3: ( UNION_TERM groupGraphPattern -> ^( UNION_GRAPH_PATTERN $groupOrUnionGraphPattern groupGraphPattern ) )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==UNION_TERM) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:619:5: UNION_TERM groupGraphPattern
            	    {
            	    UNION_TERM190=(Token)match(input,UNION_TERM,FOLLOW_UNION_TERM_in_groupOrUnionGraphPattern2440); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_UNION_TERM.add(UNION_TERM190);

            	    pushFollow(FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern2442);
            	    groupGraphPattern191=groupGraphPattern();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_groupGraphPattern.add(groupGraphPattern191.getTree());


            	    // AST REWRITE
            	    // elements: groupGraphPattern, groupOrUnionGraphPattern
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 619:34: -> ^( UNION_GRAPH_PATTERN $groupOrUnionGraphPattern groupGraphPattern )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:619:37: ^( UNION_GRAPH_PATTERN $groupOrUnionGraphPattern groupGraphPattern )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(UNION_GRAPH_PATTERN, "UNION_GRAPH_PATTERN"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_groupGraphPattern.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "groupOrUnionGraphPattern"

    public static class filter_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "filter"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:625:1: filter : FILTER_TERM constraint -> ^( FILTER constraint ) ;
    public final SparqlOwlParser.filter_return filter() throws RecognitionException {
        SparqlOwlParser.filter_return retval = new SparqlOwlParser.filter_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token FILTER_TERM192=null;
        SparqlOwlParser.constraint_return constraint193 = null;


        CommonTree FILTER_TERM192_tree=null;
        RewriteRuleTokenStream stream_FILTER_TERM=new RewriteRuleTokenStream(adaptor,"token FILTER_TERM");
        RewriteRuleSubtreeStream stream_constraint=new RewriteRuleSubtreeStream(adaptor,"rule constraint");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:626:2: ( FILTER_TERM constraint -> ^( FILTER constraint ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:626:4: FILTER_TERM constraint
            {
            FILTER_TERM192=(Token)match(input,FILTER_TERM,FOLLOW_FILTER_TERM_in_filter2469); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FILTER_TERM.add(FILTER_TERM192);

            pushFollow(FOLLOW_constraint_in_filter2471);
            constraint193=constraint();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_constraint.add(constraint193.getTree());


            // AST REWRITE
            // elements: constraint
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 627:2: -> ^( FILTER constraint )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:627:5: ^( FILTER constraint )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FILTER, "FILTER"), root_1);

                adaptor.addChild(root_1, stream_constraint.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "filter"

    public static class constraint_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constraint"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:633:1: constraint : ( brackettedExpression | builtInCall | functionCall );
    public final SparqlOwlParser.constraint_return constraint() throws RecognitionException {
        SparqlOwlParser.constraint_return retval = new SparqlOwlParser.constraint_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.brackettedExpression_return brackettedExpression194 = null;

        SparqlOwlParser.builtInCall_return builtInCall195 = null;

        SparqlOwlParser.functionCall_return functionCall196 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:634:2: ( brackettedExpression | builtInCall | functionCall )
            int alt59=3;
            switch ( input.LA(1) ) {
            case OPEN_BRACE:
                {
                alt59=1;
                }
                break;
            case DATATYPE_TERM:
            case STR_TERM:
            case LANG_TERM:
            case LANGMATCHES_TERM:
            case BOUND_TERM:
            case SAMETERM_TERM:
            case ISIRI_TERM:
            case ISURI_TERM:
            case ISBLANK_TERM:
            case ISLITERAL_TERM:
            case REGEX_TERM:
                {
                alt59=2;
                }
                break;
            case IRI_REF_TERM:
            case PNAME_NS:
            case PNAME_LN:
                {
                alt59=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }

            switch (alt59) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:634:4: brackettedExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_brackettedExpression_in_constraint2493);
                    brackettedExpression194=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, brackettedExpression194.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:635:4: builtInCall
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_builtInCall_in_constraint2498);
                    builtInCall195=builtInCall();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, builtInCall195.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:636:4: functionCall
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_functionCall_in_constraint2503);
                    functionCall196=functionCall();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, functionCall196.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "constraint"

    public static class functionCall_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "functionCall"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:642:1: functionCall : iriRef argList -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) ;
    public final SparqlOwlParser.functionCall_return functionCall() throws RecognitionException {
        SparqlOwlParser.functionCall_return retval = new SparqlOwlParser.functionCall_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef197 = null;

        SparqlOwlParser.argList_return argList198 = null;


        RewriteRuleSubtreeStream stream_argList=new RewriteRuleSubtreeStream(adaptor,"rule argList");
        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:643:2: ( iriRef argList -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:643:4: iriRef argList
            {
            pushFollow(FOLLOW_iriRef_in_functionCall2516);
            iriRef197=iriRef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_iriRef.add(iriRef197.getTree());
            pushFollow(FOLLOW_argList_in_functionCall2518);
            argList198=argList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_argList.add(argList198.getTree());


            // AST REWRITE
            // elements: argList, iriRef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 644:2: -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:644:5: ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION_CALL, "FUNCTION_CALL"), root_1);

                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:644:21: ^( FUNCTION_IDENTIFIER iriRef )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION_IDENTIFIER, "FUNCTION_IDENTIFIER"), root_2);

                adaptor.addChild(root_2, stream_iriRef.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:644:51: ^( FUNCTION_ARGS argList )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION_ARGS, "FUNCTION_ARGS"), root_2);

                adaptor.addChild(root_2, stream_argList.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "functionCall"

    public static class argList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "argList"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:650:1: argList : ( OPEN_BRACE CLOSE_BRACE | OPEN_BRACE expression ( COMMA_TERM expression )* CLOSE_BRACE );
    public final SparqlOwlParser.argList_return argList() throws RecognitionException {
        SparqlOwlParser.argList_return retval = new SparqlOwlParser.argList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_BRACE199=null;
        Token CLOSE_BRACE200=null;
        Token OPEN_BRACE201=null;
        Token COMMA_TERM203=null;
        Token CLOSE_BRACE205=null;
        SparqlOwlParser.expression_return expression202 = null;

        SparqlOwlParser.expression_return expression204 = null;


        CommonTree OPEN_BRACE199_tree=null;
        CommonTree CLOSE_BRACE200_tree=null;
        CommonTree OPEN_BRACE201_tree=null;
        CommonTree COMMA_TERM203_tree=null;
        CommonTree CLOSE_BRACE205_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:651:2: ( OPEN_BRACE CLOSE_BRACE | OPEN_BRACE expression ( COMMA_TERM expression )* CLOSE_BRACE )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==OPEN_BRACE) ) {
                int LA61_1 = input.LA(2);

                if ( (LA61_1==CLOSE_BRACE) ) {
                    alt61=1;
                }
                else if ( (LA61_1==DATATYPE_TERM||LA61_1==OPEN_BRACE||LA61_1==INTEGER||LA61_1==IRI_REF_TERM||LA61_1==PNAME_NS||(LA61_1>=VAR1 && LA61_1<=VAR2)||(LA61_1>=PLUS_TERM && LA61_1<=MINUS_TERM)||(LA61_1>=UNARY_NOT_TERM && LA61_1<=REGEX_TERM)||(LA61_1>=DECIMAL && LA61_1<=PNAME_LN)) ) {
                    alt61=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:651:4: OPEN_BRACE CLOSE_BRACE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    OPEN_BRACE199=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_argList2550); if (state.failed) return retval;
                    CLOSE_BRACE200=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_argList2553); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:652:4: OPEN_BRACE expression ( COMMA_TERM expression )* CLOSE_BRACE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    OPEN_BRACE201=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_argList2559); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_argList2562);
                    expression202=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression202.getTree());
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:652:27: ( COMMA_TERM expression )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==COMMA_TERM) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:652:29: COMMA_TERM expression
                    	    {
                    	    COMMA_TERM203=(Token)match(input,COMMA_TERM,FOLLOW_COMMA_TERM_in_argList2566); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_expression_in_argList2569);
                    	    expression204=expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression204.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);

                    CLOSE_BRACE205=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_argList2574); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "argList"

    public static class constructTemplate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructTemplate"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:658:1: constructTemplate : OPEN_CURLY_BRACE ( constructTriples )? CLOSE_CURLY_BRACE -> ^( CONSTRUCT_TEMPLATE ( constructTriples )? ) ;
    public final SparqlOwlParser.constructTemplate_return constructTemplate() throws RecognitionException {
        SparqlOwlParser.constructTemplate_return retval = new SparqlOwlParser.constructTemplate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_CURLY_BRACE206=null;
        Token CLOSE_CURLY_BRACE208=null;
        SparqlOwlParser.constructTriples_return constructTriples207 = null;


        CommonTree OPEN_CURLY_BRACE206_tree=null;
        CommonTree CLOSE_CURLY_BRACE208_tree=null;
        RewriteRuleTokenStream stream_OPEN_CURLY_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_CURLY_BRACE");
        RewriteRuleTokenStream stream_CLOSE_CURLY_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_CURLY_BRACE");
        RewriteRuleSubtreeStream stream_constructTriples=new RewriteRuleSubtreeStream(adaptor,"rule constructTriples");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:659:2: ( OPEN_CURLY_BRACE ( constructTriples )? CLOSE_CURLY_BRACE -> ^( CONSTRUCT_TEMPLATE ( constructTriples )? ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:659:4: OPEN_CURLY_BRACE ( constructTriples )? CLOSE_CURLY_BRACE
            {
            OPEN_CURLY_BRACE206=(Token)match(input,OPEN_CURLY_BRACE,FOLLOW_OPEN_CURLY_BRACE_in_constructTemplate2588); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_CURLY_BRACE.add(OPEN_CURLY_BRACE206);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:659:21: ( constructTriples )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==DECIMAL_TERM||LA62_0==FLOAT_TERM||LA62_0==INTEGER_TERM||LA62_0==STRING_TERM||(LA62_0>=INVERSE_TERM && LA62_0<=OPEN_SQUARE_BRACE)||(LA62_0>=NOT_TERM && LA62_0<=OPEN_CURLY_BRACE)||LA62_0==OPEN_BRACE||LA62_0==INTEGER||LA62_0==IRI_REF_TERM||LA62_0==PNAME_NS||(LA62_0>=VAR1 && LA62_0<=VAR2)||(LA62_0>=DECIMAL && LA62_0<=BLANK_NODE_LABEL)) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:659:21: constructTriples
                    {
                    pushFollow(FOLLOW_constructTriples_in_constructTemplate2590);
                    constructTriples207=constructTriples();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_constructTriples.add(constructTriples207.getTree());

                    }
                    break;

            }

            CLOSE_CURLY_BRACE208=(Token)match(input,CLOSE_CURLY_BRACE,FOLLOW_CLOSE_CURLY_BRACE_in_constructTemplate2593); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_CURLY_BRACE.add(CLOSE_CURLY_BRACE208);



            // AST REWRITE
            // elements: constructTriples
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 660:3: -> ^( CONSTRUCT_TEMPLATE ( constructTriples )? )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:660:6: ^( CONSTRUCT_TEMPLATE ( constructTriples )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CONSTRUCT_TEMPLATE, "CONSTRUCT_TEMPLATE"), root_1);

                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:660:27: ( constructTriples )?
                if ( stream_constructTriples.hasNext() ) {
                    adaptor.addChild(root_1, stream_constructTriples.nextTree());

                }
                stream_constructTriples.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "constructTemplate"

    public static class constructTriples_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "constructTriples"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:666:1: constructTriples : triplesSameSubject ( DOT_TERM ( constructTriples )? )? ;
    public final SparqlOwlParser.constructTriples_return constructTriples() throws RecognitionException {
        SparqlOwlParser.constructTriples_return retval = new SparqlOwlParser.constructTriples_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token DOT_TERM210=null;
        SparqlOwlParser.triplesSameSubject_return triplesSameSubject209 = null;

        SparqlOwlParser.constructTriples_return constructTriples211 = null;


        CommonTree DOT_TERM210_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:667:2: ( triplesSameSubject ( DOT_TERM ( constructTriples )? )? )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:667:4: triplesSameSubject ( DOT_TERM ( constructTriples )? )?
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_triplesSameSubject_in_constructTriples2617);
            triplesSameSubject209=triplesSameSubject();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, triplesSameSubject209.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:667:23: ( DOT_TERM ( constructTriples )? )?
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==DOT_TERM) ) {
                alt64=1;
            }
            switch (alt64) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:667:25: DOT_TERM ( constructTriples )?
                    {
                    DOT_TERM210=(Token)match(input,DOT_TERM,FOLLOW_DOT_TERM_in_constructTriples2621); if (state.failed) return retval;
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:667:35: ( constructTriples )?
                    int alt63=2;
                    int LA63_0 = input.LA(1);

                    if ( (LA63_0==DECIMAL_TERM||LA63_0==FLOAT_TERM||LA63_0==INTEGER_TERM||LA63_0==STRING_TERM||(LA63_0>=INVERSE_TERM && LA63_0<=OPEN_SQUARE_BRACE)||(LA63_0>=NOT_TERM && LA63_0<=OPEN_CURLY_BRACE)||LA63_0==OPEN_BRACE||LA63_0==INTEGER||LA63_0==IRI_REF_TERM||LA63_0==PNAME_NS||(LA63_0>=VAR1 && LA63_0<=VAR2)||(LA63_0>=DECIMAL && LA63_0<=BLANK_NODE_LABEL)) ) {
                        alt63=1;
                    }
                    switch (alt63) {
                        case 1 :
                            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:667:35: constructTriples
                            {
                            pushFollow(FOLLOW_constructTriples_in_constructTriples2624);
                            constructTriples211=constructTriples();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, constructTriples211.getTree());

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "constructTriples"

    public static class triplesSameSubject_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "triplesSameSubject"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:673:1: triplesSameSubject options {memoize=true; } : ( ( varOrTerm propertyListNotEmpty )=> varOrTerm propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT varOrTerm ) propertyListNotEmpty ) | ( triplesNode propertyListNotEmpty )=> triplesNode propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) propertyListNotEmpty ) | ( triplesNode )=> triplesNode -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) ) | disjunction propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT disjunction ) propertyListNotEmpty ) );
    public final SparqlOwlParser.triplesSameSubject_return triplesSameSubject() throws RecognitionException {
        SparqlOwlParser.triplesSameSubject_return retval = new SparqlOwlParser.triplesSameSubject_return();
        retval.start = input.LT(1);
        int triplesSameSubject_StartIndex = input.index();
        CommonTree root_0 = null;

        SparqlOwlParser.varOrTerm_return varOrTerm212 = null;

        SparqlOwlParser.propertyListNotEmpty_return propertyListNotEmpty213 = null;

        SparqlOwlParser.triplesNode_return triplesNode214 = null;

        SparqlOwlParser.propertyListNotEmpty_return propertyListNotEmpty215 = null;

        SparqlOwlParser.triplesNode_return triplesNode216 = null;

        SparqlOwlParser.disjunction_return disjunction217 = null;

        SparqlOwlParser.propertyListNotEmpty_return propertyListNotEmpty218 = null;


        RewriteRuleSubtreeStream stream_varOrTerm=new RewriteRuleSubtreeStream(adaptor,"rule varOrTerm");
        RewriteRuleSubtreeStream stream_propertyListNotEmpty=new RewriteRuleSubtreeStream(adaptor,"rule propertyListNotEmpty");
        RewriteRuleSubtreeStream stream_disjunction=new RewriteRuleSubtreeStream(adaptor,"rule disjunction");
        RewriteRuleSubtreeStream stream_triplesNode=new RewriteRuleSubtreeStream(adaptor,"rule triplesNode");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:675:2: ( ( varOrTerm propertyListNotEmpty )=> varOrTerm propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT varOrTerm ) propertyListNotEmpty ) | ( triplesNode propertyListNotEmpty )=> triplesNode propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) propertyListNotEmpty ) | ( triplesNode )=> triplesNode -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) ) | disjunction propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT disjunction ) propertyListNotEmpty ) )
            int alt65=4;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:675:4: ( varOrTerm propertyListNotEmpty )=> varOrTerm propertyListNotEmpty
                    {
                    pushFollow(FOLLOW_varOrTerm_in_triplesSameSubject2657);
                    varOrTerm212=varOrTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_varOrTerm.add(varOrTerm212.getTree());
                    pushFollow(FOLLOW_propertyListNotEmpty_in_triplesSameSubject2659);
                    propertyListNotEmpty213=propertyListNotEmpty();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_propertyListNotEmpty.add(propertyListNotEmpty213.getTree());


                    // AST REWRITE
                    // elements: propertyListNotEmpty, varOrTerm
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 675:70: -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT varOrTerm ) propertyListNotEmpty )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:675:73: ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT varOrTerm ) propertyListNotEmpty )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT_TRIPLE_GROUP, "SUBJECT_TRIPLE_GROUP"), root_1);

                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:675:96: ^( SUBJECT varOrTerm )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT, "SUBJECT"), root_2);

                        adaptor.addChild(root_2, stream_varOrTerm.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_propertyListNotEmpty.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:676:4: ( triplesNode propertyListNotEmpty )=> triplesNode propertyListNotEmpty
                    {
                    pushFollow(FOLLOW_triplesNode_in_triplesSameSubject2685);
                    triplesNode214=triplesNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_triplesNode.add(triplesNode214.getTree());
                    pushFollow(FOLLOW_propertyListNotEmpty_in_triplesSameSubject2687);
                    propertyListNotEmpty215=propertyListNotEmpty();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_propertyListNotEmpty.add(propertyListNotEmpty215.getTree());


                    // AST REWRITE
                    // elements: propertyListNotEmpty, triplesNode
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 676:74: -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) propertyListNotEmpty )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:676:77: ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) propertyListNotEmpty )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT_TRIPLE_GROUP, "SUBJECT_TRIPLE_GROUP"), root_1);

                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:676:100: ^( SUBJECT triplesNode )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT, "SUBJECT"), root_2);

                        adaptor.addChild(root_2, stream_triplesNode.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_propertyListNotEmpty.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:677:4: ( triplesNode )=> triplesNode
                    {
                    pushFollow(FOLLOW_triplesNode_in_triplesSameSubject2711);
                    triplesNode216=triplesNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_triplesNode.add(triplesNode216.getTree());


                    // AST REWRITE
                    // elements: triplesNode
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 677:32: -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:677:35: ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT_TRIPLE_GROUP, "SUBJECT_TRIPLE_GROUP"), root_1);

                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:677:58: ^( SUBJECT triplesNode )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT, "SUBJECT"), root_2);

                        adaptor.addChild(root_2, stream_triplesNode.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:678:4: disjunction propertyListNotEmpty
                    {
                    pushFollow(FOLLOW_disjunction_in_triplesSameSubject2728);
                    disjunction217=disjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_disjunction.add(disjunction217.getTree());
                    pushFollow(FOLLOW_propertyListNotEmpty_in_triplesSameSubject2730);
                    propertyListNotEmpty218=propertyListNotEmpty();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_propertyListNotEmpty.add(propertyListNotEmpty218.getTree());


                    // AST REWRITE
                    // elements: propertyListNotEmpty, disjunction
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 678:37: -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT disjunction ) propertyListNotEmpty )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:678:40: ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT disjunction ) propertyListNotEmpty )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT_TRIPLE_GROUP, "SUBJECT_TRIPLE_GROUP"), root_1);

                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:678:63: ^( SUBJECT disjunction )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUBJECT, "SUBJECT"), root_2);

                        adaptor.addChild(root_2, stream_disjunction.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_propertyListNotEmpty.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
            if ( state.backtracking>0 ) { memoize(input, 62, triplesSameSubject_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "triplesSameSubject"

    public static class propertyListNotEmpty_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "propertyListNotEmpty"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:684:1: propertyListNotEmpty : verbObjectListPair ( SEMICOLON_TERM ( verbObjectListPair )? )* -> ( verbObjectListPair )+ ;
    public final SparqlOwlParser.propertyListNotEmpty_return propertyListNotEmpty() throws RecognitionException {
        SparqlOwlParser.propertyListNotEmpty_return retval = new SparqlOwlParser.propertyListNotEmpty_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token SEMICOLON_TERM220=null;
        SparqlOwlParser.verbObjectListPair_return verbObjectListPair219 = null;

        SparqlOwlParser.verbObjectListPair_return verbObjectListPair221 = null;


        CommonTree SEMICOLON_TERM220_tree=null;
        RewriteRuleTokenStream stream_SEMICOLON_TERM=new RewriteRuleTokenStream(adaptor,"token SEMICOLON_TERM");
        RewriteRuleSubtreeStream stream_verbObjectListPair=new RewriteRuleSubtreeStream(adaptor,"rule verbObjectListPair");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:685:2: ( verbObjectListPair ( SEMICOLON_TERM ( verbObjectListPair )? )* -> ( verbObjectListPair )+ )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:685:4: verbObjectListPair ( SEMICOLON_TERM ( verbObjectListPair )? )*
            {
            pushFollow(FOLLOW_verbObjectListPair_in_propertyListNotEmpty2757);
            verbObjectListPair219=verbObjectListPair();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_verbObjectListPair.add(verbObjectListPair219.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:685:23: ( SEMICOLON_TERM ( verbObjectListPair )? )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==SEMICOLON_TERM) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:685:25: SEMICOLON_TERM ( verbObjectListPair )?
            	    {
            	    SEMICOLON_TERM220=(Token)match(input,SEMICOLON_TERM,FOLLOW_SEMICOLON_TERM_in_propertyListNotEmpty2761); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEMICOLON_TERM.add(SEMICOLON_TERM220);

            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:685:40: ( verbObjectListPair )?
            	    int alt66=2;
            	    int LA66_0 = input.LA(1);

            	    if ( (LA66_0==IRI_REF_TERM||LA66_0==PNAME_NS||(LA66_0>=A_TERM && LA66_0<=VAR2)||LA66_0==PNAME_LN) ) {
            	        alt66=1;
            	    }
            	    switch (alt66) {
            	        case 1 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:685:42: verbObjectListPair
            	            {
            	            pushFollow(FOLLOW_verbObjectListPair_in_propertyListNotEmpty2765);
            	            verbObjectListPair221=verbObjectListPair();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_verbObjectListPair.add(verbObjectListPair221.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);



            // AST REWRITE
            // elements: verbObjectListPair
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 686:2: -> ( verbObjectListPair )+
            {
                if ( !(stream_verbObjectListPair.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_verbObjectListPair.hasNext() ) {
                    adaptor.addChild(root_0, stream_verbObjectListPair.nextTree());

                }
                stream_verbObjectListPair.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class verbObjectListPair_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "verbObjectListPair"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:692:1: verbObjectListPair : verb objectList -> ( ^( VERB_PAIR_GROUP verb objectList ) )+ ;
    public final SparqlOwlParser.verbObjectListPair_return verbObjectListPair() throws RecognitionException {
        SparqlOwlParser.verbObjectListPair_return retval = new SparqlOwlParser.verbObjectListPair_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.verb_return verb222 = null;

        SparqlOwlParser.objectList_return objectList223 = null;


        RewriteRuleSubtreeStream stream_verb=new RewriteRuleSubtreeStream(adaptor,"rule verb");
        RewriteRuleSubtreeStream stream_objectList=new RewriteRuleSubtreeStream(adaptor,"rule objectList");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:693:2: ( verb objectList -> ( ^( VERB_PAIR_GROUP verb objectList ) )+ )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:693:4: verb objectList
            {
            pushFollow(FOLLOW_verb_in_verbObjectListPair2791);
            verb222=verb();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_verb.add(verb222.getTree());
            pushFollow(FOLLOW_objectList_in_verbObjectListPair2793);
            objectList223=objectList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_objectList.add(objectList223.getTree());


            // AST REWRITE
            // elements: objectList, verb
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 694:2: -> ( ^( VERB_PAIR_GROUP verb objectList ) )+
            {
                if ( !(stream_objectList.hasNext()||stream_verb.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_objectList.hasNext()||stream_verb.hasNext() ) {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:694:5: ^( VERB_PAIR_GROUP verb objectList )
                    {
                    CommonTree root_1 = (CommonTree)adaptor.nil();
                    root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VERB_PAIR_GROUP, "VERB_PAIR_GROUP"), root_1);

                    adaptor.addChild(root_1, stream_verb.nextTree());
                    adaptor.addChild(root_1, stream_objectList.nextTree());

                    adaptor.addChild(root_0, root_1);
                    }

                }
                stream_objectList.reset();
                stream_verb.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "verbObjectListPair"

    public static class objectList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "objectList"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:700:1: objectList : object ( COMMA_TERM object )* -> ( object )+ ;
    public final SparqlOwlParser.objectList_return objectList() throws RecognitionException {
        SparqlOwlParser.objectList_return retval = new SparqlOwlParser.objectList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token COMMA_TERM225=null;
        SparqlOwlParser.object_return object224 = null;

        SparqlOwlParser.object_return object226 = null;


        CommonTree COMMA_TERM225_tree=null;
        RewriteRuleTokenStream stream_COMMA_TERM=new RewriteRuleTokenStream(adaptor,"token COMMA_TERM");
        RewriteRuleSubtreeStream stream_object=new RewriteRuleSubtreeStream(adaptor,"rule object");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:701:2: ( object ( COMMA_TERM object )* -> ( object )+ )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:701:4: object ( COMMA_TERM object )*
            {
            pushFollow(FOLLOW_object_in_objectList2818);
            object224=object();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_object.add(object224.getTree());
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:701:11: ( COMMA_TERM object )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==COMMA_TERM) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:701:13: COMMA_TERM object
            	    {
            	    COMMA_TERM225=(Token)match(input,COMMA_TERM,FOLLOW_COMMA_TERM_in_objectList2822); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA_TERM.add(COMMA_TERM225);

            	    pushFollow(FOLLOW_object_in_objectList2824);
            	    object226=object();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_object.add(object226.getTree());

            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);



            // AST REWRITE
            // elements: object
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 702:2: -> ( object )+
            {
                if ( !(stream_object.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_object.hasNext() ) {
                    adaptor.addChild(root_0, stream_object.nextTree());

                }
                stream_object.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class object_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "object"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:709:1: object : ( ( graphNode ( DOT_TERM | SEMICOLON_TERM | COMMA_TERM | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE ) )=> graphNode -> ^( OBJECT graphNode ) | disjunction -> ^( OBJECT disjunction ) );
    public final SparqlOwlParser.object_return object() throws RecognitionException {
        SparqlOwlParser.object_return retval = new SparqlOwlParser.object_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.graphNode_return graphNode227 = null;

        SparqlOwlParser.disjunction_return disjunction228 = null;


        RewriteRuleSubtreeStream stream_graphNode=new RewriteRuleSubtreeStream(adaptor,"rule graphNode");
        RewriteRuleSubtreeStream stream_disjunction=new RewriteRuleSubtreeStream(adaptor,"rule disjunction");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:710:2: ( ( graphNode ( DOT_TERM | SEMICOLON_TERM | COMMA_TERM | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE ) )=> graphNode -> ^( OBJECT graphNode ) | disjunction -> ^( OBJECT disjunction ) )
            int alt69=2;
            alt69 = dfa69.predict(input);
            switch (alt69) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:710:4: ( graphNode ( DOT_TERM | SEMICOLON_TERM | COMMA_TERM | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE ) )=> graphNode
                    {
                    pushFollow(FOLLOW_graphNode_in_object2873);
                    graphNode227=graphNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_graphNode.add(graphNode227.getTree());


                    // AST REWRITE
                    // elements: graphNode
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 710:110: -> ^( OBJECT graphNode )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:710:113: ^( OBJECT graphNode )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OBJECT, "OBJECT"), root_1);

                        adaptor.addChild(root_1, stream_graphNode.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:711:4: disjunction
                    {
                    pushFollow(FOLLOW_disjunction_in_object2886);
                    disjunction228=disjunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_disjunction.add(disjunction228.getTree());


                    // AST REWRITE
                    // elements: disjunction
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 711:16: -> ^( OBJECT disjunction )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:711:19: ^( OBJECT disjunction )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OBJECT, "OBJECT"), root_1);

                        adaptor.addChild(root_1, stream_disjunction.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class verb_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "verb"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:717:1: verb : ( varOrIRIref -> ^( VERB varOrIRIref ) | A_TERM -> ^( VERB RDF_TYPE ) );
    public final SparqlOwlParser.verb_return verb() throws RecognitionException {
        SparqlOwlParser.verb_return retval = new SparqlOwlParser.verb_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token A_TERM230=null;
        SparqlOwlParser.varOrIRIref_return varOrIRIref229 = null;


        CommonTree A_TERM230_tree=null;
        RewriteRuleTokenStream stream_A_TERM=new RewriteRuleTokenStream(adaptor,"token A_TERM");
        RewriteRuleSubtreeStream stream_varOrIRIref=new RewriteRuleSubtreeStream(adaptor,"rule varOrIRIref");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:718:2: ( varOrIRIref -> ^( VERB varOrIRIref ) | A_TERM -> ^( VERB RDF_TYPE ) )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==IRI_REF_TERM||LA70_0==PNAME_NS||(LA70_0>=VAR1 && LA70_0<=VAR2)||LA70_0==PNAME_LN) ) {
                alt70=1;
            }
            else if ( (LA70_0==A_TERM) ) {
                alt70=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:718:4: varOrIRIref
                    {
                    pushFollow(FOLLOW_varOrIRIref_in_verb2907);
                    varOrIRIref229=varOrIRIref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_varOrIRIref.add(varOrIRIref229.getTree());


                    // AST REWRITE
                    // elements: varOrIRIref
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 718:16: -> ^( VERB varOrIRIref )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:718:19: ^( VERB varOrIRIref )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VERB, "VERB"), root_1);

                        adaptor.addChild(root_1, stream_varOrIRIref.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:719:4: A_TERM
                    {
                    A_TERM230=(Token)match(input,A_TERM,FOLLOW_A_TERM_in_verb2920); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_A_TERM.add(A_TERM230);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 719:11: -> ^( VERB RDF_TYPE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:719:14: ^( VERB RDF_TYPE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VERB, "VERB"), root_1);

                        adaptor.addChild(root_1, (CommonTree)adaptor.create(RDF_TYPE, "RDF_TYPE"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "verb"

    public static class triplesNode_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "triplesNode"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:725:1: triplesNode : ( collection | blankNodePropertyList );
    public final SparqlOwlParser.triplesNode_return triplesNode() throws RecognitionException {
        SparqlOwlParser.triplesNode_return retval = new SparqlOwlParser.triplesNode_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.collection_return collection231 = null;

        SparqlOwlParser.blankNodePropertyList_return blankNodePropertyList232 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:726:2: ( collection | blankNodePropertyList )
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==OPEN_BRACE) ) {
                alt71=1;
            }
            else if ( (LA71_0==OPEN_SQUARE_BRACE) ) {
                alt71=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:726:4: collection
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_collection_in_triplesNode2941);
                    collection231=collection();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, collection231.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:727:4: blankNodePropertyList
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_blankNodePropertyList_in_triplesNode2946);
                    blankNodePropertyList232=blankNodePropertyList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, blankNodePropertyList232.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class blankNodePropertyList_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blankNodePropertyList"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:733:1: blankNodePropertyList : OPEN_SQUARE_BRACE propertyListNotEmpty CLOSE_SQUARE_BRACE -> ^( BNODE_PROPERTY_LIST propertyListNotEmpty ) ;
    public final SparqlOwlParser.blankNodePropertyList_return blankNodePropertyList() throws RecognitionException {
        SparqlOwlParser.blankNodePropertyList_return retval = new SparqlOwlParser.blankNodePropertyList_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_SQUARE_BRACE233=null;
        Token CLOSE_SQUARE_BRACE235=null;
        SparqlOwlParser.propertyListNotEmpty_return propertyListNotEmpty234 = null;


        CommonTree OPEN_SQUARE_BRACE233_tree=null;
        CommonTree CLOSE_SQUARE_BRACE235_tree=null;
        RewriteRuleTokenStream stream_CLOSE_SQUARE_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_SQUARE_BRACE");
        RewriteRuleTokenStream stream_OPEN_SQUARE_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_SQUARE_BRACE");
        RewriteRuleSubtreeStream stream_propertyListNotEmpty=new RewriteRuleSubtreeStream(adaptor,"rule propertyListNotEmpty");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:734:2: ( OPEN_SQUARE_BRACE propertyListNotEmpty CLOSE_SQUARE_BRACE -> ^( BNODE_PROPERTY_LIST propertyListNotEmpty ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:734:4: OPEN_SQUARE_BRACE propertyListNotEmpty CLOSE_SQUARE_BRACE
            {
            OPEN_SQUARE_BRACE233=(Token)match(input,OPEN_SQUARE_BRACE,FOLLOW_OPEN_SQUARE_BRACE_in_blankNodePropertyList2959); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_SQUARE_BRACE.add(OPEN_SQUARE_BRACE233);

            pushFollow(FOLLOW_propertyListNotEmpty_in_blankNodePropertyList2961);
            propertyListNotEmpty234=propertyListNotEmpty();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_propertyListNotEmpty.add(propertyListNotEmpty234.getTree());
            CLOSE_SQUARE_BRACE235=(Token)match(input,CLOSE_SQUARE_BRACE,FOLLOW_CLOSE_SQUARE_BRACE_in_blankNodePropertyList2963); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_SQUARE_BRACE.add(CLOSE_SQUARE_BRACE235);



            // AST REWRITE
            // elements: propertyListNotEmpty
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 735:2: -> ^( BNODE_PROPERTY_LIST propertyListNotEmpty )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:735:5: ^( BNODE_PROPERTY_LIST propertyListNotEmpty )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BNODE_PROPERTY_LIST, "BNODE_PROPERTY_LIST"), root_1);

                adaptor.addChild(root_1, stream_propertyListNotEmpty.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class collection_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "collection"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:741:1: collection : OPEN_BRACE ( graphNode )+ CLOSE_BRACE -> ^( COLLECTION ( graphNode )+ ) ;
    public final SparqlOwlParser.collection_return collection() throws RecognitionException {
        SparqlOwlParser.collection_return retval = new SparqlOwlParser.collection_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_BRACE236=null;
        Token CLOSE_BRACE238=null;
        SparqlOwlParser.graphNode_return graphNode237 = null;


        CommonTree OPEN_BRACE236_tree=null;
        CommonTree CLOSE_BRACE238_tree=null;
        RewriteRuleTokenStream stream_CLOSE_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_BRACE");
        RewriteRuleTokenStream stream_OPEN_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_BRACE");
        RewriteRuleSubtreeStream stream_graphNode=new RewriteRuleSubtreeStream(adaptor,"rule graphNode");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:742:2: ( OPEN_BRACE ( graphNode )+ CLOSE_BRACE -> ^( COLLECTION ( graphNode )+ ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:742:4: OPEN_BRACE ( graphNode )+ CLOSE_BRACE
            {
            OPEN_BRACE236=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_collection2985); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE236);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:742:15: ( graphNode )+
            int cnt72=0;
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==OPEN_SQUARE_BRACE||LA72_0==OPEN_BRACE||LA72_0==INTEGER||LA72_0==IRI_REF_TERM||LA72_0==PNAME_NS||(LA72_0>=VAR1 && LA72_0<=VAR2)||(LA72_0>=DECIMAL && LA72_0<=BLANK_NODE_LABEL)) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:742:15: graphNode
            	    {
            	    pushFollow(FOLLOW_graphNode_in_collection2987);
            	    graphNode237=graphNode();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_graphNode.add(graphNode237.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt72 >= 1 ) break loop72;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(72, input);
                        throw eee;
                }
                cnt72++;
            } while (true);

            CLOSE_BRACE238=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_collection2990); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE238);



            // AST REWRITE
            // elements: graphNode
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 743:2: -> ^( COLLECTION ( graphNode )+ )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:743:5: ^( COLLECTION ( graphNode )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(COLLECTION, "COLLECTION"), root_1);

                if ( !(stream_graphNode.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_graphNode.hasNext() ) {
                    adaptor.addChild(root_1, stream_graphNode.nextTree());

                }
                stream_graphNode.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class emptyCollection_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "emptyCollection"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:749:1: emptyCollection : OPEN_BRACE CLOSE_BRACE -> ^( COLLECTION ) ;
    public final SparqlOwlParser.emptyCollection_return emptyCollection() throws RecognitionException {
        SparqlOwlParser.emptyCollection_return retval = new SparqlOwlParser.emptyCollection_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_BRACE239=null;
        Token CLOSE_BRACE240=null;

        CommonTree OPEN_BRACE239_tree=null;
        CommonTree CLOSE_BRACE240_tree=null;
        RewriteRuleTokenStream stream_CLOSE_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_BRACE");
        RewriteRuleTokenStream stream_OPEN_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_BRACE");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:750:2: ( OPEN_BRACE CLOSE_BRACE -> ^( COLLECTION ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:750:4: OPEN_BRACE CLOSE_BRACE
            {
            OPEN_BRACE239=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_emptyCollection3013); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE239);

            CLOSE_BRACE240=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_emptyCollection3015); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE240);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 751:2: -> ^( COLLECTION )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:751:5: ^( COLLECTION )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(COLLECTION, "COLLECTION"), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "emptyCollection"

    public static class graphNode_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "graphNode"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:757:1: graphNode : ( varOrTerm | triplesNode );
    public final SparqlOwlParser.graphNode_return graphNode() throws RecognitionException {
        SparqlOwlParser.graphNode_return retval = new SparqlOwlParser.graphNode_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.varOrTerm_return varOrTerm241 = null;

        SparqlOwlParser.triplesNode_return triplesNode242 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:758:2: ( varOrTerm | triplesNode )
            int alt73=2;
            switch ( input.LA(1) ) {
            case INTEGER:
            case IRI_REF_TERM:
            case PNAME_NS:
            case VAR1:
            case VAR2:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
            case TRUE_TERM:
            case FALSE_TERM:
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
            case PNAME_LN:
            case BLANK_NODE_LABEL:
                {
                alt73=1;
                }
                break;
            case OPEN_SQUARE_BRACE:
                {
                int LA73_2 = input.LA(2);

                if ( (LA73_2==CLOSE_SQUARE_BRACE) ) {
                    alt73=1;
                }
                else if ( (LA73_2==IRI_REF_TERM||LA73_2==PNAME_NS||(LA73_2>=A_TERM && LA73_2<=VAR2)||LA73_2==PNAME_LN) ) {
                    alt73=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 73, 2, input);

                    throw nvae;
                }
                }
                break;
            case OPEN_BRACE:
                {
                int LA73_3 = input.LA(2);

                if ( (LA73_3==CLOSE_BRACE) ) {
                    alt73=1;
                }
                else if ( (LA73_3==OPEN_SQUARE_BRACE||LA73_3==OPEN_BRACE||LA73_3==INTEGER||LA73_3==IRI_REF_TERM||LA73_3==PNAME_NS||(LA73_3>=VAR1 && LA73_3<=VAR2)||(LA73_3>=DECIMAL && LA73_3<=BLANK_NODE_LABEL)) ) {
                    alt73=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 73, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }

            switch (alt73) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:758:4: varOrTerm
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_varOrTerm_in_graphNode3035);
                    varOrTerm241=varOrTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, varOrTerm241.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:759:4: triplesNode
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_triplesNode_in_graphNode3040);
                    triplesNode242=triplesNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, triplesNode242.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
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

    public static class varOrTerm_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "varOrTerm"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:765:1: varOrTerm : ( var | graphTerm );
    public final SparqlOwlParser.varOrTerm_return varOrTerm() throws RecognitionException {
        SparqlOwlParser.varOrTerm_return retval = new SparqlOwlParser.varOrTerm_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.var_return var243 = null;

        SparqlOwlParser.graphTerm_return graphTerm244 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:766:2: ( var | graphTerm )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( ((LA74_0>=VAR1 && LA74_0<=VAR2)) ) {
                alt74=1;
            }
            else if ( (LA74_0==OPEN_SQUARE_BRACE||LA74_0==OPEN_BRACE||LA74_0==INTEGER||LA74_0==IRI_REF_TERM||LA74_0==PNAME_NS||(LA74_0>=DECIMAL && LA74_0<=BLANK_NODE_LABEL)) ) {
                alt74=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:766:4: var
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_var_in_varOrTerm3053);
                    var243=var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var243.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:767:4: graphTerm
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_graphTerm_in_varOrTerm3058);
                    graphTerm244=graphTerm();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, graphTerm244.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "varOrTerm"

    public static class varOrIRIref_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "varOrIRIref"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:773:1: varOrIRIref : ( var | iriRef );
    public final SparqlOwlParser.varOrIRIref_return varOrIRIref() throws RecognitionException {
        SparqlOwlParser.varOrIRIref_return retval = new SparqlOwlParser.varOrIRIref_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.var_return var245 = null;

        SparqlOwlParser.iriRef_return iriRef246 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:774:2: ( var | iriRef )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( ((LA75_0>=VAR1 && LA75_0<=VAR2)) ) {
                alt75=1;
            }
            else if ( (LA75_0==IRI_REF_TERM||LA75_0==PNAME_NS||LA75_0==PNAME_LN) ) {
                alt75=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:774:4: var
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_var_in_varOrIRIref3071);
                    var245=var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var245.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:775:4: iriRef
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_iriRef_in_varOrIRIref3076);
                    iriRef246=iriRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, iriRef246.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "varOrIRIref"

    public static class var_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "var"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:781:1: var : ( VAR1 -> ^( VARIABLE VAR1 ) | VAR2 -> ^( VARIABLE VAR2 ) );
    public final SparqlOwlParser.var_return var() throws RecognitionException {
        SparqlOwlParser.var_return retval = new SparqlOwlParser.var_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token VAR1247=null;
        Token VAR2248=null;

        CommonTree VAR1247_tree=null;
        CommonTree VAR2248_tree=null;
        RewriteRuleTokenStream stream_VAR1=new RewriteRuleTokenStream(adaptor,"token VAR1");
        RewriteRuleTokenStream stream_VAR2=new RewriteRuleTokenStream(adaptor,"token VAR2");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:782:2: ( VAR1 -> ^( VARIABLE VAR1 ) | VAR2 -> ^( VARIABLE VAR2 ) )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==VAR1) ) {
                alt76=1;
            }
            else if ( (LA76_0==VAR2) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:782:4: VAR1
                    {
                    VAR1247=(Token)match(input,VAR1,FOLLOW_VAR1_in_var3089); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_VAR1.add(VAR1247);



                    // AST REWRITE
                    // elements: VAR1
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 782:9: -> ^( VARIABLE VAR1 )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:782:12: ^( VARIABLE VAR1 )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VARIABLE, "VARIABLE"), root_1);

                        adaptor.addChild(root_1, stream_VAR1.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:783:4: VAR2
                    {
                    VAR2248=(Token)match(input,VAR2,FOLLOW_VAR2_in_var3102); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_VAR2.add(VAR2248);



                    // AST REWRITE
                    // elements: VAR2
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 783:9: -> ^( VARIABLE VAR2 )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:783:12: ^( VARIABLE VAR2 )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(VARIABLE, "VARIABLE"), root_1);

                        adaptor.addChild(root_1, stream_VAR2.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "var"

    public static class graphTerm_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "graphTerm"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:789:1: graphTerm : ( iriRef | literal | blankNode | emptyCollection );
    public final SparqlOwlParser.graphTerm_return graphTerm() throws RecognitionException {
        SparqlOwlParser.graphTerm_return retval = new SparqlOwlParser.graphTerm_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef249 = null;

        SparqlOwlParser.literal_return literal250 = null;

        SparqlOwlParser.blankNode_return blankNode251 = null;

        SparqlOwlParser.emptyCollection_return emptyCollection252 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:790:2: ( iriRef | literal | blankNode | emptyCollection )
            int alt77=4;
            switch ( input.LA(1) ) {
            case IRI_REF_TERM:
            case PNAME_NS:
            case PNAME_LN:
                {
                alt77=1;
                }
                break;
            case INTEGER:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
            case TRUE_TERM:
            case FALSE_TERM:
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
                {
                alt77=2;
                }
                break;
            case OPEN_SQUARE_BRACE:
            case BLANK_NODE_LABEL:
                {
                alt77=3;
                }
                break;
            case OPEN_BRACE:
                {
                alt77=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }

            switch (alt77) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:790:4: iriRef
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_iriRef_in_graphTerm3123);
                    iriRef249=iriRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, iriRef249.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:791:4: literal
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_graphTerm3128);
                    literal250=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal250.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:792:4: blankNode
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_blankNode_in_graphTerm3133);
                    blankNode251=blankNode();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, blankNode251.getTree());

                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:793:4: emptyCollection
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_emptyCollection_in_graphTerm3138);
                    emptyCollection252=emptyCollection();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, emptyCollection252.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "graphTerm"

    public static class expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:799:1: expression : conditionalOrExpression ;
    public final SparqlOwlParser.expression_return expression() throws RecognitionException {
        SparqlOwlParser.expression_return retval = new SparqlOwlParser.expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.conditionalOrExpression_return conditionalOrExpression253 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:800:2: ( conditionalOrExpression )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:800:4: conditionalOrExpression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_conditionalOrExpression_in_expression3151);
            conditionalOrExpression253=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalOrExpression253.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalOrExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:806:1: conditionalOrExpression : ( conditionalAndExpression -> conditionalAndExpression ) ( OR_OPERATOR_TERM conditionalAndExpression -> ^( CONDITIONAL_EXPRESSION_OR $conditionalOrExpression conditionalAndExpression ) )* ;
    public final SparqlOwlParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        SparqlOwlParser.conditionalOrExpression_return retval = new SparqlOwlParser.conditionalOrExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OR_OPERATOR_TERM255=null;
        SparqlOwlParser.conditionalAndExpression_return conditionalAndExpression254 = null;

        SparqlOwlParser.conditionalAndExpression_return conditionalAndExpression256 = null;


        CommonTree OR_OPERATOR_TERM255_tree=null;
        RewriteRuleTokenStream stream_OR_OPERATOR_TERM=new RewriteRuleTokenStream(adaptor,"token OR_OPERATOR_TERM");
        RewriteRuleSubtreeStream stream_conditionalAndExpression=new RewriteRuleSubtreeStream(adaptor,"rule conditionalAndExpression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:807:2: ( ( conditionalAndExpression -> conditionalAndExpression ) ( OR_OPERATOR_TERM conditionalAndExpression -> ^( CONDITIONAL_EXPRESSION_OR $conditionalOrExpression conditionalAndExpression ) )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:807:4: ( conditionalAndExpression -> conditionalAndExpression ) ( OR_OPERATOR_TERM conditionalAndExpression -> ^( CONDITIONAL_EXPRESSION_OR $conditionalOrExpression conditionalAndExpression ) )*
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:807:4: ( conditionalAndExpression -> conditionalAndExpression )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:807:6: conditionalAndExpression
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression3166);
            conditionalAndExpression254=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditionalAndExpression.add(conditionalAndExpression254.getTree());


            // AST REWRITE
            // elements: conditionalAndExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 807:31: -> conditionalAndExpression
            {
                adaptor.addChild(root_0, stream_conditionalAndExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:808:3: ( OR_OPERATOR_TERM conditionalAndExpression -> ^( CONDITIONAL_EXPRESSION_OR $conditionalOrExpression conditionalAndExpression ) )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==OR_OPERATOR_TERM) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:808:5: OR_OPERATOR_TERM conditionalAndExpression
            	    {
            	    OR_OPERATOR_TERM255=(Token)match(input,OR_OPERATOR_TERM,FOLLOW_OR_OPERATOR_TERM_in_conditionalOrExpression3178); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_OR_OPERATOR_TERM.add(OR_OPERATOR_TERM255);

            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression3180);
            	    conditionalAndExpression256=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_conditionalAndExpression.add(conditionalAndExpression256.getTree());


            	    // AST REWRITE
            	    // elements: conditionalOrExpression, conditionalAndExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 808:47: -> ^( CONDITIONAL_EXPRESSION_OR $conditionalOrExpression conditionalAndExpression )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:808:50: ^( CONDITIONAL_EXPRESSION_OR $conditionalOrExpression conditionalAndExpression )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CONDITIONAL_EXPRESSION_OR, "CONDITIONAL_EXPRESSION_OR"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_conditionalAndExpression.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalAndExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:814:1: conditionalAndExpression : ( valueLogical -> valueLogical ) ( AND_OPERATOR_TERM valueLogical -> ^( CONDITIONAL_EXPRESSION_AND $conditionalAndExpression valueLogical ) )* ;
    public final SparqlOwlParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        SparqlOwlParser.conditionalAndExpression_return retval = new SparqlOwlParser.conditionalAndExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token AND_OPERATOR_TERM258=null;
        SparqlOwlParser.valueLogical_return valueLogical257 = null;

        SparqlOwlParser.valueLogical_return valueLogical259 = null;


        CommonTree AND_OPERATOR_TERM258_tree=null;
        RewriteRuleTokenStream stream_AND_OPERATOR_TERM=new RewriteRuleTokenStream(adaptor,"token AND_OPERATOR_TERM");
        RewriteRuleSubtreeStream stream_valueLogical=new RewriteRuleSubtreeStream(adaptor,"rule valueLogical");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:815:2: ( ( valueLogical -> valueLogical ) ( AND_OPERATOR_TERM valueLogical -> ^( CONDITIONAL_EXPRESSION_AND $conditionalAndExpression valueLogical ) )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:815:4: ( valueLogical -> valueLogical ) ( AND_OPERATOR_TERM valueLogical -> ^( CONDITIONAL_EXPRESSION_AND $conditionalAndExpression valueLogical ) )*
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:815:4: ( valueLogical -> valueLogical )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:815:6: valueLogical
            {
            pushFollow(FOLLOW_valueLogical_in_conditionalAndExpression3209);
            valueLogical257=valueLogical();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_valueLogical.add(valueLogical257.getTree());


            // AST REWRITE
            // elements: valueLogical
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 815:19: -> valueLogical
            {
                adaptor.addChild(root_0, stream_valueLogical.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:816:3: ( AND_OPERATOR_TERM valueLogical -> ^( CONDITIONAL_EXPRESSION_AND $conditionalAndExpression valueLogical ) )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==AND_OPERATOR_TERM) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:816:5: AND_OPERATOR_TERM valueLogical
            	    {
            	    AND_OPERATOR_TERM258=(Token)match(input,AND_OPERATOR_TERM,FOLLOW_AND_OPERATOR_TERM_in_conditionalAndExpression3220); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_AND_OPERATOR_TERM.add(AND_OPERATOR_TERM258);

            	    pushFollow(FOLLOW_valueLogical_in_conditionalAndExpression3222);
            	    valueLogical259=valueLogical();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_valueLogical.add(valueLogical259.getTree());


            	    // AST REWRITE
            	    // elements: conditionalAndExpression, valueLogical
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 816:36: -> ^( CONDITIONAL_EXPRESSION_AND $conditionalAndExpression valueLogical )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:816:39: ^( CONDITIONAL_EXPRESSION_AND $conditionalAndExpression valueLogical )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CONDITIONAL_EXPRESSION_AND, "CONDITIONAL_EXPRESSION_AND"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_valueLogical.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class valueLogical_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "valueLogical"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:822:1: valueLogical : relationalExpression ;
    public final SparqlOwlParser.valueLogical_return valueLogical() throws RecognitionException {
        SparqlOwlParser.valueLogical_return retval = new SparqlOwlParser.valueLogical_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.relationalExpression_return relationalExpression260 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:823:2: ( relationalExpression )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:823:4: relationalExpression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_relationalExpression_in_valueLogical3249);
            relationalExpression260=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalExpression260.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "valueLogical"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:830:1: relationalExpression : ( numericExpression -> numericExpression ) ( EQUAL_TERM numericExpression -> ^( RELATIONAL_EQUAL $relationalExpression numericExpression ) | NOT_EQUAL_TERM numericExpression -> ^( RELATIONAL_NOT_EQUAL $relationalExpression numericExpression ) | LESS_TERM numericExpression -> ^( RELATIONAL_LESS $relationalExpression numericExpression ) | GREATER_TERM numericExpression -> ^( RELATIONAL_GREATER $relationalExpression numericExpression ) | LESS_EQUAL_TERM numericExpression -> ^( RELATIONAL_LESS_EQUAL $relationalExpression numericExpression ) | GREATER_EQUAL_TERM numericExpression -> ^( RELATIONAL_GREATER_EQUAL $relationalExpression numericExpression ) )? ;
    public final SparqlOwlParser.relationalExpression_return relationalExpression() throws RecognitionException {
        SparqlOwlParser.relationalExpression_return retval = new SparqlOwlParser.relationalExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token EQUAL_TERM262=null;
        Token NOT_EQUAL_TERM264=null;
        Token LESS_TERM266=null;
        Token GREATER_TERM268=null;
        Token LESS_EQUAL_TERM270=null;
        Token GREATER_EQUAL_TERM272=null;
        SparqlOwlParser.numericExpression_return numericExpression261 = null;

        SparqlOwlParser.numericExpression_return numericExpression263 = null;

        SparqlOwlParser.numericExpression_return numericExpression265 = null;

        SparqlOwlParser.numericExpression_return numericExpression267 = null;

        SparqlOwlParser.numericExpression_return numericExpression269 = null;

        SparqlOwlParser.numericExpression_return numericExpression271 = null;

        SparqlOwlParser.numericExpression_return numericExpression273 = null;


        CommonTree EQUAL_TERM262_tree=null;
        CommonTree NOT_EQUAL_TERM264_tree=null;
        CommonTree LESS_TERM266_tree=null;
        CommonTree GREATER_TERM268_tree=null;
        CommonTree LESS_EQUAL_TERM270_tree=null;
        CommonTree GREATER_EQUAL_TERM272_tree=null;
        RewriteRuleTokenStream stream_GREATER_TERM=new RewriteRuleTokenStream(adaptor,"token GREATER_TERM");
        RewriteRuleTokenStream stream_NOT_EQUAL_TERM=new RewriteRuleTokenStream(adaptor,"token NOT_EQUAL_TERM");
        RewriteRuleTokenStream stream_LESS_EQUAL_TERM=new RewriteRuleTokenStream(adaptor,"token LESS_EQUAL_TERM");
        RewriteRuleTokenStream stream_EQUAL_TERM=new RewriteRuleTokenStream(adaptor,"token EQUAL_TERM");
        RewriteRuleTokenStream stream_LESS_TERM=new RewriteRuleTokenStream(adaptor,"token LESS_TERM");
        RewriteRuleTokenStream stream_GREATER_EQUAL_TERM=new RewriteRuleTokenStream(adaptor,"token GREATER_EQUAL_TERM");
        RewriteRuleSubtreeStream stream_numericExpression=new RewriteRuleSubtreeStream(adaptor,"rule numericExpression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:831:2: ( ( numericExpression -> numericExpression ) ( EQUAL_TERM numericExpression -> ^( RELATIONAL_EQUAL $relationalExpression numericExpression ) | NOT_EQUAL_TERM numericExpression -> ^( RELATIONAL_NOT_EQUAL $relationalExpression numericExpression ) | LESS_TERM numericExpression -> ^( RELATIONAL_LESS $relationalExpression numericExpression ) | GREATER_TERM numericExpression -> ^( RELATIONAL_GREATER $relationalExpression numericExpression ) | LESS_EQUAL_TERM numericExpression -> ^( RELATIONAL_LESS_EQUAL $relationalExpression numericExpression ) | GREATER_EQUAL_TERM numericExpression -> ^( RELATIONAL_GREATER_EQUAL $relationalExpression numericExpression ) )? )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:831:4: ( numericExpression -> numericExpression ) ( EQUAL_TERM numericExpression -> ^( RELATIONAL_EQUAL $relationalExpression numericExpression ) | NOT_EQUAL_TERM numericExpression -> ^( RELATIONAL_NOT_EQUAL $relationalExpression numericExpression ) | LESS_TERM numericExpression -> ^( RELATIONAL_LESS $relationalExpression numericExpression ) | GREATER_TERM numericExpression -> ^( RELATIONAL_GREATER $relationalExpression numericExpression ) | LESS_EQUAL_TERM numericExpression -> ^( RELATIONAL_LESS_EQUAL $relationalExpression numericExpression ) | GREATER_EQUAL_TERM numericExpression -> ^( RELATIONAL_GREATER_EQUAL $relationalExpression numericExpression ) )?
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:831:4: ( numericExpression -> numericExpression )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:831:6: numericExpression
            {
            pushFollow(FOLLOW_numericExpression_in_relationalExpression3264);
            numericExpression261=numericExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_numericExpression.add(numericExpression261.getTree());


            // AST REWRITE
            // elements: numericExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 831:24: -> numericExpression
            {
                adaptor.addChild(root_0, stream_numericExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:832:3: ( EQUAL_TERM numericExpression -> ^( RELATIONAL_EQUAL $relationalExpression numericExpression ) | NOT_EQUAL_TERM numericExpression -> ^( RELATIONAL_NOT_EQUAL $relationalExpression numericExpression ) | LESS_TERM numericExpression -> ^( RELATIONAL_LESS $relationalExpression numericExpression ) | GREATER_TERM numericExpression -> ^( RELATIONAL_GREATER $relationalExpression numericExpression ) | LESS_EQUAL_TERM numericExpression -> ^( RELATIONAL_LESS_EQUAL $relationalExpression numericExpression ) | GREATER_EQUAL_TERM numericExpression -> ^( RELATIONAL_GREATER_EQUAL $relationalExpression numericExpression ) )?
            int alt80=7;
            switch ( input.LA(1) ) {
                case EQUAL_TERM:
                    {
                    alt80=1;
                    }
                    break;
                case NOT_EQUAL_TERM:
                    {
                    alt80=2;
                    }
                    break;
                case LESS_TERM:
                    {
                    alt80=3;
                    }
                    break;
                case GREATER_TERM:
                    {
                    alt80=4;
                    }
                    break;
                case LESS_EQUAL_TERM:
                    {
                    alt80=5;
                    }
                    break;
                case GREATER_EQUAL_TERM:
                    {
                    alt80=6;
                    }
                    break;
            }

            switch (alt80) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:832:5: EQUAL_TERM numericExpression
                    {
                    EQUAL_TERM262=(Token)match(input,EQUAL_TERM,FOLLOW_EQUAL_TERM_in_relationalExpression3276); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_EQUAL_TERM.add(EQUAL_TERM262);

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3278);
                    numericExpression263=numericExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_numericExpression.add(numericExpression263.getTree());


                    // AST REWRITE
                    // elements: numericExpression, relationalExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 832:34: -> ^( RELATIONAL_EQUAL $relationalExpression numericExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:832:37: ^( RELATIONAL_EQUAL $relationalExpression numericExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(RELATIONAL_EQUAL, "RELATIONAL_EQUAL"), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_numericExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:833:5: NOT_EQUAL_TERM numericExpression
                    {
                    NOT_EQUAL_TERM264=(Token)match(input,NOT_EQUAL_TERM,FOLLOW_NOT_EQUAL_TERM_in_relationalExpression3295); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT_EQUAL_TERM.add(NOT_EQUAL_TERM264);

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3297);
                    numericExpression265=numericExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_numericExpression.add(numericExpression265.getTree());


                    // AST REWRITE
                    // elements: numericExpression, relationalExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 833:38: -> ^( RELATIONAL_NOT_EQUAL $relationalExpression numericExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:833:41: ^( RELATIONAL_NOT_EQUAL $relationalExpression numericExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(RELATIONAL_NOT_EQUAL, "RELATIONAL_NOT_EQUAL"), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_numericExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:834:5: LESS_TERM numericExpression
                    {
                    LESS_TERM266=(Token)match(input,LESS_TERM,FOLLOW_LESS_TERM_in_relationalExpression3314); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LESS_TERM.add(LESS_TERM266);

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3316);
                    numericExpression267=numericExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_numericExpression.add(numericExpression267.getTree());


                    // AST REWRITE
                    // elements: numericExpression, relationalExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 834:33: -> ^( RELATIONAL_LESS $relationalExpression numericExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:834:36: ^( RELATIONAL_LESS $relationalExpression numericExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(RELATIONAL_LESS, "RELATIONAL_LESS"), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_numericExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:835:5: GREATER_TERM numericExpression
                    {
                    GREATER_TERM268=(Token)match(input,GREATER_TERM,FOLLOW_GREATER_TERM_in_relationalExpression3333); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER_TERM.add(GREATER_TERM268);

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3335);
                    numericExpression269=numericExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_numericExpression.add(numericExpression269.getTree());


                    // AST REWRITE
                    // elements: numericExpression, relationalExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 835:36: -> ^( RELATIONAL_GREATER $relationalExpression numericExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:835:39: ^( RELATIONAL_GREATER $relationalExpression numericExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(RELATIONAL_GREATER, "RELATIONAL_GREATER"), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_numericExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:836:5: LESS_EQUAL_TERM numericExpression
                    {
                    LESS_EQUAL_TERM270=(Token)match(input,LESS_EQUAL_TERM,FOLLOW_LESS_EQUAL_TERM_in_relationalExpression3352); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LESS_EQUAL_TERM.add(LESS_EQUAL_TERM270);

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3354);
                    numericExpression271=numericExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_numericExpression.add(numericExpression271.getTree());


                    // AST REWRITE
                    // elements: numericExpression, relationalExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 836:39: -> ^( RELATIONAL_LESS_EQUAL $relationalExpression numericExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:836:42: ^( RELATIONAL_LESS_EQUAL $relationalExpression numericExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(RELATIONAL_LESS_EQUAL, "RELATIONAL_LESS_EQUAL"), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_numericExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:837:5: GREATER_EQUAL_TERM numericExpression
                    {
                    GREATER_EQUAL_TERM272=(Token)match(input,GREATER_EQUAL_TERM,FOLLOW_GREATER_EQUAL_TERM_in_relationalExpression3371); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GREATER_EQUAL_TERM.add(GREATER_EQUAL_TERM272);

                    pushFollow(FOLLOW_numericExpression_in_relationalExpression3373);
                    numericExpression273=numericExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_numericExpression.add(numericExpression273.getTree());


                    // AST REWRITE
                    // elements: relationalExpression, numericExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 837:42: -> ^( RELATIONAL_GREATER_EQUAL $relationalExpression numericExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:837:45: ^( RELATIONAL_GREATER_EQUAL $relationalExpression numericExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(RELATIONAL_GREATER_EQUAL, "RELATIONAL_GREATER_EQUAL"), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_numericExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class numericExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numericExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:844:1: numericExpression : additiveExpression ;
    public final SparqlOwlParser.numericExpression_return numericExpression() throws RecognitionException {
        SparqlOwlParser.numericExpression_return retval = new SparqlOwlParser.numericExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.additiveExpression_return additiveExpression274 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:845:2: ( additiveExpression )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:845:4: additiveExpression
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_additiveExpression_in_numericExpression3402);
            additiveExpression274=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, additiveExpression274.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "numericExpression"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:851:1: additiveExpression : ( multiplicativeExpression -> multiplicativeExpression ) ( PLUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression multiplicativeExpression ) | MINUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_SUBTRACT $additiveExpression multiplicativeExpression ) | numericLiteralPositive -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralPositive ) | numericLiteralNegative -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralNegative ) )* ;
    public final SparqlOwlParser.additiveExpression_return additiveExpression() throws RecognitionException {
        SparqlOwlParser.additiveExpression_return retval = new SparqlOwlParser.additiveExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token PLUS_TERM276=null;
        Token MINUS_TERM278=null;
        SparqlOwlParser.multiplicativeExpression_return multiplicativeExpression275 = null;

        SparqlOwlParser.multiplicativeExpression_return multiplicativeExpression277 = null;

        SparqlOwlParser.multiplicativeExpression_return multiplicativeExpression279 = null;

        SparqlOwlParser.numericLiteralPositive_return numericLiteralPositive280 = null;

        SparqlOwlParser.numericLiteralNegative_return numericLiteralNegative281 = null;


        CommonTree PLUS_TERM276_tree=null;
        CommonTree MINUS_TERM278_tree=null;
        RewriteRuleTokenStream stream_MINUS_TERM=new RewriteRuleTokenStream(adaptor,"token MINUS_TERM");
        RewriteRuleTokenStream stream_PLUS_TERM=new RewriteRuleTokenStream(adaptor,"token PLUS_TERM");
        RewriteRuleSubtreeStream stream_numericLiteralNegative=new RewriteRuleSubtreeStream(adaptor,"rule numericLiteralNegative");
        RewriteRuleSubtreeStream stream_numericLiteralPositive=new RewriteRuleSubtreeStream(adaptor,"rule numericLiteralPositive");
        RewriteRuleSubtreeStream stream_multiplicativeExpression=new RewriteRuleSubtreeStream(adaptor,"rule multiplicativeExpression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:852:2: ( ( multiplicativeExpression -> multiplicativeExpression ) ( PLUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression multiplicativeExpression ) | MINUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_SUBTRACT $additiveExpression multiplicativeExpression ) | numericLiteralPositive -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralPositive ) | numericLiteralNegative -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralNegative ) )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:852:4: ( multiplicativeExpression -> multiplicativeExpression ) ( PLUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression multiplicativeExpression ) | MINUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_SUBTRACT $additiveExpression multiplicativeExpression ) | numericLiteralPositive -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralPositive ) | numericLiteralNegative -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralNegative ) )*
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:852:4: ( multiplicativeExpression -> multiplicativeExpression )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:852:6: multiplicativeExpression
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression3417);
            multiplicativeExpression275=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_multiplicativeExpression.add(multiplicativeExpression275.getTree());


            // AST REWRITE
            // elements: multiplicativeExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 852:31: -> multiplicativeExpression
            {
                adaptor.addChild(root_0, stream_multiplicativeExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:853:3: ( PLUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression multiplicativeExpression ) | MINUS_TERM multiplicativeExpression -> ^( NUMERIC_EXPRESSION_SUBTRACT $additiveExpression multiplicativeExpression ) | numericLiteralPositive -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralPositive ) | numericLiteralNegative -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralNegative ) )*
            loop81:
            do {
                int alt81=5;
                switch ( input.LA(1) ) {
                case PLUS_TERM:
                    {
                    alt81=1;
                    }
                    break;
                case MINUS_TERM:
                    {
                    alt81=2;
                    }
                    break;
                case INTEGER_POSITIVE:
                case DECIMAL_POSITIVE:
                case DOUBLE_POSITIVE:
                    {
                    alt81=3;
                    }
                    break;
                case INTEGER_NEGATIVE:
                case DECIMAL_NEGATIVE:
                case DOUBLE_NEGATIVE:
                    {
                    alt81=4;
                    }
                    break;

                }

                switch (alt81) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:853:5: PLUS_TERM multiplicativeExpression
            	    {
            	    PLUS_TERM276=(Token)match(input,PLUS_TERM,FOLLOW_PLUS_TERM_in_additiveExpression3429); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_PLUS_TERM.add(PLUS_TERM276);

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression3431);
            	    multiplicativeExpression277=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_multiplicativeExpression.add(multiplicativeExpression277.getTree());


            	    // AST REWRITE
            	    // elements: additiveExpression, multiplicativeExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 853:40: -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression multiplicativeExpression )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:853:43: ^( NUMERIC_EXPRESSION_ADD $additiveExpression multiplicativeExpression )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NUMERIC_EXPRESSION_ADD, "NUMERIC_EXPRESSION_ADD"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_multiplicativeExpression.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 2 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:854:5: MINUS_TERM multiplicativeExpression
            	    {
            	    MINUS_TERM278=(Token)match(input,MINUS_TERM,FOLLOW_MINUS_TERM_in_additiveExpression3448); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_MINUS_TERM.add(MINUS_TERM278);

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression3450);
            	    multiplicativeExpression279=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_multiplicativeExpression.add(multiplicativeExpression279.getTree());


            	    // AST REWRITE
            	    // elements: additiveExpression, multiplicativeExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 854:41: -> ^( NUMERIC_EXPRESSION_SUBTRACT $additiveExpression multiplicativeExpression )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:854:44: ^( NUMERIC_EXPRESSION_SUBTRACT $additiveExpression multiplicativeExpression )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NUMERIC_EXPRESSION_SUBTRACT, "NUMERIC_EXPRESSION_SUBTRACT"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_multiplicativeExpression.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 3 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:855:5: numericLiteralPositive
            	    {
            	    pushFollow(FOLLOW_numericLiteralPositive_in_additiveExpression3467);
            	    numericLiteralPositive280=numericLiteralPositive();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_numericLiteralPositive.add(numericLiteralPositive280.getTree());


            	    // AST REWRITE
            	    // elements: additiveExpression, numericLiteralPositive
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 855:28: -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralPositive )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:855:31: ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralPositive )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NUMERIC_EXPRESSION_ADD, "NUMERIC_EXPRESSION_ADD"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_numericLiteralPositive.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 4 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:856:5: numericLiteralNegative
            	    {
            	    pushFollow(FOLLOW_numericLiteralNegative_in_additiveExpression3484);
            	    numericLiteralNegative281=numericLiteralNegative();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_numericLiteralNegative.add(numericLiteralNegative281.getTree());


            	    // AST REWRITE
            	    // elements: numericLiteralNegative, additiveExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 856:28: -> ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralNegative )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:856:31: ^( NUMERIC_EXPRESSION_ADD $additiveExpression numericLiteralNegative )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NUMERIC_EXPRESSION_ADD, "NUMERIC_EXPRESSION_ADD"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_numericLiteralNegative.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplicativeExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:863:1: multiplicativeExpression : ( unaryExpression -> unaryExpression ) ( ASTERISK_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_MULTIPLY $multiplicativeExpression unaryExpression ) | DIVIDE_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_DIVIDE $multiplicativeExpression unaryExpression ) )* ;
    public final SparqlOwlParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        SparqlOwlParser.multiplicativeExpression_return retval = new SparqlOwlParser.multiplicativeExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ASTERISK_TERM283=null;
        Token DIVIDE_TERM285=null;
        SparqlOwlParser.unaryExpression_return unaryExpression282 = null;

        SparqlOwlParser.unaryExpression_return unaryExpression284 = null;

        SparqlOwlParser.unaryExpression_return unaryExpression286 = null;


        CommonTree ASTERISK_TERM283_tree=null;
        CommonTree DIVIDE_TERM285_tree=null;
        RewriteRuleTokenStream stream_ASTERISK_TERM=new RewriteRuleTokenStream(adaptor,"token ASTERISK_TERM");
        RewriteRuleTokenStream stream_DIVIDE_TERM=new RewriteRuleTokenStream(adaptor,"token DIVIDE_TERM");
        RewriteRuleSubtreeStream stream_unaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:864:2: ( ( unaryExpression -> unaryExpression ) ( ASTERISK_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_MULTIPLY $multiplicativeExpression unaryExpression ) | DIVIDE_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_DIVIDE $multiplicativeExpression unaryExpression ) )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:864:4: ( unaryExpression -> unaryExpression ) ( ASTERISK_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_MULTIPLY $multiplicativeExpression unaryExpression ) | DIVIDE_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_DIVIDE $multiplicativeExpression unaryExpression ) )*
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:864:4: ( unaryExpression -> unaryExpression )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:864:6: unaryExpression
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression3515);
            unaryExpression282=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression282.getTree());


            // AST REWRITE
            // elements: unaryExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 864:22: -> unaryExpression
            {
                adaptor.addChild(root_0, stream_unaryExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:865:3: ( ASTERISK_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_MULTIPLY $multiplicativeExpression unaryExpression ) | DIVIDE_TERM unaryExpression -> ^( NUMERIC_EXPRESSION_DIVIDE $multiplicativeExpression unaryExpression ) )*
            loop82:
            do {
                int alt82=3;
                int LA82_0 = input.LA(1);

                if ( (LA82_0==ASTERISK_TERM) ) {
                    alt82=1;
                }
                else if ( (LA82_0==DIVIDE_TERM) ) {
                    alt82=2;
                }


                switch (alt82) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:865:5: ASTERISK_TERM unaryExpression
            	    {
            	    ASTERISK_TERM283=(Token)match(input,ASTERISK_TERM,FOLLOW_ASTERISK_TERM_in_multiplicativeExpression3527); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ASTERISK_TERM.add(ASTERISK_TERM283);

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression3529);
            	    unaryExpression284=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression284.getTree());


            	    // AST REWRITE
            	    // elements: multiplicativeExpression, unaryExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 865:35: -> ^( NUMERIC_EXPRESSION_MULTIPLY $multiplicativeExpression unaryExpression )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:865:38: ^( NUMERIC_EXPRESSION_MULTIPLY $multiplicativeExpression unaryExpression )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NUMERIC_EXPRESSION_MULTIPLY, "NUMERIC_EXPRESSION_MULTIPLY"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_unaryExpression.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;
            	case 2 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:866:5: DIVIDE_TERM unaryExpression
            	    {
            	    DIVIDE_TERM285=(Token)match(input,DIVIDE_TERM,FOLLOW_DIVIDE_TERM_in_multiplicativeExpression3547); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DIVIDE_TERM.add(DIVIDE_TERM285);

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression3549);
            	    unaryExpression286=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression286.getTree());


            	    // AST REWRITE
            	    // elements: unaryExpression, multiplicativeExpression
            	    // token labels: 
            	    // rule labels: retval
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            	    root_0 = (CommonTree)adaptor.nil();
            	    // 866:33: -> ^( NUMERIC_EXPRESSION_DIVIDE $multiplicativeExpression unaryExpression )
            	    {
            	        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:866:36: ^( NUMERIC_EXPRESSION_DIVIDE $multiplicativeExpression unaryExpression )
            	        {
            	        CommonTree root_1 = (CommonTree)adaptor.nil();
            	        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NUMERIC_EXPRESSION_DIVIDE, "NUMERIC_EXPRESSION_DIVIDE"), root_1);

            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_unaryExpression.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:873:1: unaryExpression : ( UNARY_NOT_TERM primaryExpression -> ^( UNARY_EXPRESSION_NOT primaryExpression ) | PLUS_TERM primaryExpression -> ^( UNARY_EXPRESSION_POSITIVE primaryExpression ) | MINUS_TERM primaryExpression -> ^( UNARY_EXPRESSION_NEGATIVE primaryExpression ) | primaryExpression );
    public final SparqlOwlParser.unaryExpression_return unaryExpression() throws RecognitionException {
        SparqlOwlParser.unaryExpression_return retval = new SparqlOwlParser.unaryExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token UNARY_NOT_TERM287=null;
        Token PLUS_TERM289=null;
        Token MINUS_TERM291=null;
        SparqlOwlParser.primaryExpression_return primaryExpression288 = null;

        SparqlOwlParser.primaryExpression_return primaryExpression290 = null;

        SparqlOwlParser.primaryExpression_return primaryExpression292 = null;

        SparqlOwlParser.primaryExpression_return primaryExpression293 = null;


        CommonTree UNARY_NOT_TERM287_tree=null;
        CommonTree PLUS_TERM289_tree=null;
        CommonTree MINUS_TERM291_tree=null;
        RewriteRuleTokenStream stream_UNARY_NOT_TERM=new RewriteRuleTokenStream(adaptor,"token UNARY_NOT_TERM");
        RewriteRuleTokenStream stream_MINUS_TERM=new RewriteRuleTokenStream(adaptor,"token MINUS_TERM");
        RewriteRuleTokenStream stream_PLUS_TERM=new RewriteRuleTokenStream(adaptor,"token PLUS_TERM");
        RewriteRuleSubtreeStream stream_primaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule primaryExpression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:874:2: ( UNARY_NOT_TERM primaryExpression -> ^( UNARY_EXPRESSION_NOT primaryExpression ) | PLUS_TERM primaryExpression -> ^( UNARY_EXPRESSION_POSITIVE primaryExpression ) | MINUS_TERM primaryExpression -> ^( UNARY_EXPRESSION_NEGATIVE primaryExpression ) | primaryExpression )
            int alt83=4;
            switch ( input.LA(1) ) {
            case UNARY_NOT_TERM:
                {
                alt83=1;
                }
                break;
            case PLUS_TERM:
                {
                alt83=2;
                }
                break;
            case MINUS_TERM:
                {
                alt83=3;
                }
                break;
            case DATATYPE_TERM:
            case OPEN_BRACE:
            case INTEGER:
            case IRI_REF_TERM:
            case PNAME_NS:
            case VAR1:
            case VAR2:
            case STR_TERM:
            case LANG_TERM:
            case LANGMATCHES_TERM:
            case BOUND_TERM:
            case SAMETERM_TERM:
            case ISIRI_TERM:
            case ISURI_TERM:
            case ISBLANK_TERM:
            case ISLITERAL_TERM:
            case REGEX_TERM:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
            case TRUE_TERM:
            case FALSE_TERM:
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
            case PNAME_LN:
                {
                alt83=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;
            }

            switch (alt83) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:874:4: UNARY_NOT_TERM primaryExpression
                    {
                    UNARY_NOT_TERM287=(Token)match(input,UNARY_NOT_TERM,FOLLOW_UNARY_NOT_TERM_in_unaryExpression3579); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_UNARY_NOT_TERM.add(UNARY_NOT_TERM287);

                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3581);
                    primaryExpression288=primaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primaryExpression.add(primaryExpression288.getTree());


                    // AST REWRITE
                    // elements: primaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 874:37: -> ^( UNARY_EXPRESSION_NOT primaryExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:874:40: ^( UNARY_EXPRESSION_NOT primaryExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(UNARY_EXPRESSION_NOT, "UNARY_EXPRESSION_NOT"), root_1);

                        adaptor.addChild(root_1, stream_primaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:875:4: PLUS_TERM primaryExpression
                    {
                    PLUS_TERM289=(Token)match(input,PLUS_TERM,FOLLOW_PLUS_TERM_in_unaryExpression3594); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS_TERM.add(PLUS_TERM289);

                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3596);
                    primaryExpression290=primaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primaryExpression.add(primaryExpression290.getTree());


                    // AST REWRITE
                    // elements: primaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 875:32: -> ^( UNARY_EXPRESSION_POSITIVE primaryExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:875:35: ^( UNARY_EXPRESSION_POSITIVE primaryExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(UNARY_EXPRESSION_POSITIVE, "UNARY_EXPRESSION_POSITIVE"), root_1);

                        adaptor.addChild(root_1, stream_primaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:876:4: MINUS_TERM primaryExpression
                    {
                    MINUS_TERM291=(Token)match(input,MINUS_TERM,FOLLOW_MINUS_TERM_in_unaryExpression3609); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_MINUS_TERM.add(MINUS_TERM291);

                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3611);
                    primaryExpression292=primaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primaryExpression.add(primaryExpression292.getTree());


                    // AST REWRITE
                    // elements: primaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 876:33: -> ^( UNARY_EXPRESSION_NEGATIVE primaryExpression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:876:36: ^( UNARY_EXPRESSION_NEGATIVE primaryExpression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(UNARY_EXPRESSION_NEGATIVE, "UNARY_EXPRESSION_NEGATIVE"), root_1);

                        adaptor.addChild(root_1, stream_primaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:877:4: primaryExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression3624);
                    primaryExpression293=primaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryExpression293.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class primaryExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primaryExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:883:1: primaryExpression : ( brackettedExpression | builtInCall | iriRefOrFunction | literal | var );
    public final SparqlOwlParser.primaryExpression_return primaryExpression() throws RecognitionException {
        SparqlOwlParser.primaryExpression_return retval = new SparqlOwlParser.primaryExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.brackettedExpression_return brackettedExpression294 = null;

        SparqlOwlParser.builtInCall_return builtInCall295 = null;

        SparqlOwlParser.iriRefOrFunction_return iriRefOrFunction296 = null;

        SparqlOwlParser.literal_return literal297 = null;

        SparqlOwlParser.var_return var298 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:884:2: ( brackettedExpression | builtInCall | iriRefOrFunction | literal | var )
            int alt84=5;
            switch ( input.LA(1) ) {
            case OPEN_BRACE:
                {
                alt84=1;
                }
                break;
            case DATATYPE_TERM:
            case STR_TERM:
            case LANG_TERM:
            case LANGMATCHES_TERM:
            case BOUND_TERM:
            case SAMETERM_TERM:
            case ISIRI_TERM:
            case ISURI_TERM:
            case ISBLANK_TERM:
            case ISLITERAL_TERM:
            case REGEX_TERM:
                {
                alt84=2;
                }
                break;
            case IRI_REF_TERM:
            case PNAME_NS:
            case PNAME_LN:
                {
                alt84=3;
                }
                break;
            case INTEGER:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
            case TRUE_TERM:
            case FALSE_TERM:
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
                {
                alt84=4;
                }
                break;
            case VAR1:
            case VAR2:
                {
                alt84=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:884:4: brackettedExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_brackettedExpression_in_primaryExpression3637);
                    brackettedExpression294=brackettedExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, brackettedExpression294.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:885:4: builtInCall
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_builtInCall_in_primaryExpression3642);
                    builtInCall295=builtInCall();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, builtInCall295.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:886:4: iriRefOrFunction
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_iriRefOrFunction_in_primaryExpression3647);
                    iriRefOrFunction296=iriRefOrFunction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, iriRefOrFunction296.getTree());

                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:887:4: literal
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primaryExpression3652);
                    literal297=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal297.getTree());

                    }
                    break;
                case 5 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:888:4: var
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_var_in_primaryExpression3657);
                    var298=var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var298.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "primaryExpression"

    public static class brackettedExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "brackettedExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:894:1: brackettedExpression : OPEN_BRACE expression CLOSE_BRACE ;
    public final SparqlOwlParser.brackettedExpression_return brackettedExpression() throws RecognitionException {
        SparqlOwlParser.brackettedExpression_return retval = new SparqlOwlParser.brackettedExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN_BRACE299=null;
        Token CLOSE_BRACE301=null;
        SparqlOwlParser.expression_return expression300 = null;


        CommonTree OPEN_BRACE299_tree=null;
        CommonTree CLOSE_BRACE301_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:895:2: ( OPEN_BRACE expression CLOSE_BRACE )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:895:4: OPEN_BRACE expression CLOSE_BRACE
            {
            root_0 = (CommonTree)adaptor.nil();

            OPEN_BRACE299=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_brackettedExpression3670); if (state.failed) return retval;
            pushFollow(FOLLOW_expression_in_brackettedExpression3673);
            expression300=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression300.getTree());
            CLOSE_BRACE301=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_brackettedExpression3675); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "brackettedExpression"

    public static class builtInCall_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "builtInCall"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:901:1: builtInCall : ( STR_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_STR expression ) | LANG_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_LANG expression ) | LANGMATCHES_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE -> ^( BUILTIN_LANGMATCHES ( expression )+ ) | DATATYPE_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_DATATYPE expression ) | BOUND_TERM OPEN_BRACE var CLOSE_BRACE -> ^( BUILTIN_BOUND var ) | SAMETERM_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE -> ^( BUILTIN_SAME_TERM ( expression )+ ) | ISIRI_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_IRI expression ) | ISURI_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_URI expression ) | ISBLANK_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_BLANK expression ) | ISLITERAL_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_LITERAL expression ) | regexExpression );
    public final SparqlOwlParser.builtInCall_return builtInCall() throws RecognitionException {
        SparqlOwlParser.builtInCall_return retval = new SparqlOwlParser.builtInCall_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token STR_TERM302=null;
        Token OPEN_BRACE303=null;
        Token CLOSE_BRACE305=null;
        Token LANG_TERM306=null;
        Token OPEN_BRACE307=null;
        Token CLOSE_BRACE309=null;
        Token LANGMATCHES_TERM310=null;
        Token OPEN_BRACE311=null;
        Token COMMA_TERM313=null;
        Token CLOSE_BRACE315=null;
        Token DATATYPE_TERM316=null;
        Token OPEN_BRACE317=null;
        Token CLOSE_BRACE319=null;
        Token BOUND_TERM320=null;
        Token OPEN_BRACE321=null;
        Token CLOSE_BRACE323=null;
        Token SAMETERM_TERM324=null;
        Token OPEN_BRACE325=null;
        Token COMMA_TERM327=null;
        Token CLOSE_BRACE329=null;
        Token ISIRI_TERM330=null;
        Token OPEN_BRACE331=null;
        Token CLOSE_BRACE333=null;
        Token ISURI_TERM334=null;
        Token OPEN_BRACE335=null;
        Token CLOSE_BRACE337=null;
        Token ISBLANK_TERM338=null;
        Token OPEN_BRACE339=null;
        Token CLOSE_BRACE341=null;
        Token ISLITERAL_TERM342=null;
        Token OPEN_BRACE343=null;
        Token CLOSE_BRACE345=null;
        SparqlOwlParser.expression_return expression304 = null;

        SparqlOwlParser.expression_return expression308 = null;

        SparqlOwlParser.expression_return expression312 = null;

        SparqlOwlParser.expression_return expression314 = null;

        SparqlOwlParser.expression_return expression318 = null;

        SparqlOwlParser.var_return var322 = null;

        SparqlOwlParser.expression_return expression326 = null;

        SparqlOwlParser.expression_return expression328 = null;

        SparqlOwlParser.expression_return expression332 = null;

        SparqlOwlParser.expression_return expression336 = null;

        SparqlOwlParser.expression_return expression340 = null;

        SparqlOwlParser.expression_return expression344 = null;

        SparqlOwlParser.regexExpression_return regexExpression346 = null;


        CommonTree STR_TERM302_tree=null;
        CommonTree OPEN_BRACE303_tree=null;
        CommonTree CLOSE_BRACE305_tree=null;
        CommonTree LANG_TERM306_tree=null;
        CommonTree OPEN_BRACE307_tree=null;
        CommonTree CLOSE_BRACE309_tree=null;
        CommonTree LANGMATCHES_TERM310_tree=null;
        CommonTree OPEN_BRACE311_tree=null;
        CommonTree COMMA_TERM313_tree=null;
        CommonTree CLOSE_BRACE315_tree=null;
        CommonTree DATATYPE_TERM316_tree=null;
        CommonTree OPEN_BRACE317_tree=null;
        CommonTree CLOSE_BRACE319_tree=null;
        CommonTree BOUND_TERM320_tree=null;
        CommonTree OPEN_BRACE321_tree=null;
        CommonTree CLOSE_BRACE323_tree=null;
        CommonTree SAMETERM_TERM324_tree=null;
        CommonTree OPEN_BRACE325_tree=null;
        CommonTree COMMA_TERM327_tree=null;
        CommonTree CLOSE_BRACE329_tree=null;
        CommonTree ISIRI_TERM330_tree=null;
        CommonTree OPEN_BRACE331_tree=null;
        CommonTree CLOSE_BRACE333_tree=null;
        CommonTree ISURI_TERM334_tree=null;
        CommonTree OPEN_BRACE335_tree=null;
        CommonTree CLOSE_BRACE337_tree=null;
        CommonTree ISBLANK_TERM338_tree=null;
        CommonTree OPEN_BRACE339_tree=null;
        CommonTree CLOSE_BRACE341_tree=null;
        CommonTree ISLITERAL_TERM342_tree=null;
        CommonTree OPEN_BRACE343_tree=null;
        CommonTree CLOSE_BRACE345_tree=null;
        RewriteRuleTokenStream stream_BOUND_TERM=new RewriteRuleTokenStream(adaptor,"token BOUND_TERM");
        RewriteRuleTokenStream stream_COMMA_TERM=new RewriteRuleTokenStream(adaptor,"token COMMA_TERM");
        RewriteRuleTokenStream stream_SAMETERM_TERM=new RewriteRuleTokenStream(adaptor,"token SAMETERM_TERM");
        RewriteRuleTokenStream stream_STR_TERM=new RewriteRuleTokenStream(adaptor,"token STR_TERM");
        RewriteRuleTokenStream stream_LANGMATCHES_TERM=new RewriteRuleTokenStream(adaptor,"token LANGMATCHES_TERM");
        RewriteRuleTokenStream stream_LANG_TERM=new RewriteRuleTokenStream(adaptor,"token LANG_TERM");
        RewriteRuleTokenStream stream_ISLITERAL_TERM=new RewriteRuleTokenStream(adaptor,"token ISLITERAL_TERM");
        RewriteRuleTokenStream stream_ISBLANK_TERM=new RewriteRuleTokenStream(adaptor,"token ISBLANK_TERM");
        RewriteRuleTokenStream stream_ISIRI_TERM=new RewriteRuleTokenStream(adaptor,"token ISIRI_TERM");
        RewriteRuleTokenStream stream_CLOSE_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_BRACE");
        RewriteRuleTokenStream stream_OPEN_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_BRACE");
        RewriteRuleTokenStream stream_ISURI_TERM=new RewriteRuleTokenStream(adaptor,"token ISURI_TERM");
        RewriteRuleTokenStream stream_DATATYPE_TERM=new RewriteRuleTokenStream(adaptor,"token DATATYPE_TERM");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_var=new RewriteRuleSubtreeStream(adaptor,"rule var");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:902:2: ( STR_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_STR expression ) | LANG_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_LANG expression ) | LANGMATCHES_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE -> ^( BUILTIN_LANGMATCHES ( expression )+ ) | DATATYPE_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_DATATYPE expression ) | BOUND_TERM OPEN_BRACE var CLOSE_BRACE -> ^( BUILTIN_BOUND var ) | SAMETERM_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE -> ^( BUILTIN_SAME_TERM ( expression )+ ) | ISIRI_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_IRI expression ) | ISURI_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_URI expression ) | ISBLANK_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_BLANK expression ) | ISLITERAL_TERM OPEN_BRACE expression CLOSE_BRACE -> ^( BUILTIN_IS_LITERAL expression ) | regexExpression )
            int alt85=11;
            switch ( input.LA(1) ) {
            case STR_TERM:
                {
                alt85=1;
                }
                break;
            case LANG_TERM:
                {
                alt85=2;
                }
                break;
            case LANGMATCHES_TERM:
                {
                alt85=3;
                }
                break;
            case DATATYPE_TERM:
                {
                alt85=4;
                }
                break;
            case BOUND_TERM:
                {
                alt85=5;
                }
                break;
            case SAMETERM_TERM:
                {
                alt85=6;
                }
                break;
            case ISIRI_TERM:
                {
                alt85=7;
                }
                break;
            case ISURI_TERM:
                {
                alt85=8;
                }
                break;
            case ISBLANK_TERM:
                {
                alt85=9;
                }
                break;
            case ISLITERAL_TERM:
                {
                alt85=10;
                }
                break;
            case REGEX_TERM:
                {
                alt85=11;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;
            }

            switch (alt85) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:902:4: STR_TERM OPEN_BRACE expression CLOSE_BRACE
                    {
                    STR_TERM302=(Token)match(input,STR_TERM,FOLLOW_STR_TERM_in_builtInCall3689); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STR_TERM.add(STR_TERM302);

                    OPEN_BRACE303=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3691); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE303);

                    pushFollow(FOLLOW_expression_in_builtInCall3693);
                    expression304=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression304.getTree());
                    CLOSE_BRACE305=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3695); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE305);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 902:47: -> ^( BUILTIN_STR expression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:902:50: ^( BUILTIN_STR expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_STR, "BUILTIN_STR"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:903:4: LANG_TERM OPEN_BRACE expression CLOSE_BRACE
                    {
                    LANG_TERM306=(Token)match(input,LANG_TERM,FOLLOW_LANG_TERM_in_builtInCall3708); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LANG_TERM.add(LANG_TERM306);

                    OPEN_BRACE307=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3710); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE307);

                    pushFollow(FOLLOW_expression_in_builtInCall3712);
                    expression308=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression308.getTree());
                    CLOSE_BRACE309=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3714); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE309);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 903:48: -> ^( BUILTIN_LANG expression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:903:51: ^( BUILTIN_LANG expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_LANG, "BUILTIN_LANG"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:904:4: LANGMATCHES_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE
                    {
                    LANGMATCHES_TERM310=(Token)match(input,LANGMATCHES_TERM,FOLLOW_LANGMATCHES_TERM_in_builtInCall3727); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LANGMATCHES_TERM.add(LANGMATCHES_TERM310);

                    OPEN_BRACE311=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3729); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE311);

                    pushFollow(FOLLOW_expression_in_builtInCall3731);
                    expression312=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression312.getTree());
                    COMMA_TERM313=(Token)match(input,COMMA_TERM,FOLLOW_COMMA_TERM_in_builtInCall3733); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA_TERM.add(COMMA_TERM313);

                    pushFollow(FOLLOW_expression_in_builtInCall3735);
                    expression314=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression314.getTree());
                    CLOSE_BRACE315=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3737); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE315);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 904:77: -> ^( BUILTIN_LANGMATCHES ( expression )+ )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:904:80: ^( BUILTIN_LANGMATCHES ( expression )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_LANGMATCHES, "BUILTIN_LANGMATCHES"), root_1);

                        if ( !(stream_expression.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:905:4: DATATYPE_TERM OPEN_BRACE expression CLOSE_BRACE
                    {
                    DATATYPE_TERM316=(Token)match(input,DATATYPE_TERM,FOLLOW_DATATYPE_TERM_in_builtInCall3751); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DATATYPE_TERM.add(DATATYPE_TERM316);

                    OPEN_BRACE317=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3753); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE317);

                    pushFollow(FOLLOW_expression_in_builtInCall3755);
                    expression318=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression318.getTree());
                    CLOSE_BRACE319=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3757); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE319);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 905:52: -> ^( BUILTIN_DATATYPE expression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:905:55: ^( BUILTIN_DATATYPE expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_DATATYPE, "BUILTIN_DATATYPE"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:906:4: BOUND_TERM OPEN_BRACE var CLOSE_BRACE
                    {
                    BOUND_TERM320=(Token)match(input,BOUND_TERM,FOLLOW_BOUND_TERM_in_builtInCall3770); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BOUND_TERM.add(BOUND_TERM320);

                    OPEN_BRACE321=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3772); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE321);

                    pushFollow(FOLLOW_var_in_builtInCall3774);
                    var322=var();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var.add(var322.getTree());
                    CLOSE_BRACE323=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3776); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE323);



                    // AST REWRITE
                    // elements: var
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 906:42: -> ^( BUILTIN_BOUND var )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:906:45: ^( BUILTIN_BOUND var )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_BOUND, "BUILTIN_BOUND"), root_1);

                        adaptor.addChild(root_1, stream_var.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:907:4: SAMETERM_TERM OPEN_BRACE expression COMMA_TERM expression CLOSE_BRACE
                    {
                    SAMETERM_TERM324=(Token)match(input,SAMETERM_TERM,FOLLOW_SAMETERM_TERM_in_builtInCall3789); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SAMETERM_TERM.add(SAMETERM_TERM324);

                    OPEN_BRACE325=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3791); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE325);

                    pushFollow(FOLLOW_expression_in_builtInCall3793);
                    expression326=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression326.getTree());
                    COMMA_TERM327=(Token)match(input,COMMA_TERM,FOLLOW_COMMA_TERM_in_builtInCall3795); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA_TERM.add(COMMA_TERM327);

                    pushFollow(FOLLOW_expression_in_builtInCall3797);
                    expression328=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression328.getTree());
                    CLOSE_BRACE329=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3799); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE329);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 907:74: -> ^( BUILTIN_SAME_TERM ( expression )+ )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:907:77: ^( BUILTIN_SAME_TERM ( expression )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_SAME_TERM, "BUILTIN_SAME_TERM"), root_1);

                        if ( !(stream_expression.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:908:4: ISIRI_TERM OPEN_BRACE expression CLOSE_BRACE
                    {
                    ISIRI_TERM330=(Token)match(input,ISIRI_TERM,FOLLOW_ISIRI_TERM_in_builtInCall3813); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ISIRI_TERM.add(ISIRI_TERM330);

                    OPEN_BRACE331=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3815); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE331);

                    pushFollow(FOLLOW_expression_in_builtInCall3817);
                    expression332=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression332.getTree());
                    CLOSE_BRACE333=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3819); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE333);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 908:49: -> ^( BUILTIN_IS_IRI expression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:908:52: ^( BUILTIN_IS_IRI expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_IS_IRI, "BUILTIN_IS_IRI"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:909:4: ISURI_TERM OPEN_BRACE expression CLOSE_BRACE
                    {
                    ISURI_TERM334=(Token)match(input,ISURI_TERM,FOLLOW_ISURI_TERM_in_builtInCall3832); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ISURI_TERM.add(ISURI_TERM334);

                    OPEN_BRACE335=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3834); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE335);

                    pushFollow(FOLLOW_expression_in_builtInCall3836);
                    expression336=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression336.getTree());
                    CLOSE_BRACE337=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3838); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE337);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 909:49: -> ^( BUILTIN_IS_URI expression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:909:52: ^( BUILTIN_IS_URI expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_IS_URI, "BUILTIN_IS_URI"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 9 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:910:4: ISBLANK_TERM OPEN_BRACE expression CLOSE_BRACE
                    {
                    ISBLANK_TERM338=(Token)match(input,ISBLANK_TERM,FOLLOW_ISBLANK_TERM_in_builtInCall3851); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ISBLANK_TERM.add(ISBLANK_TERM338);

                    OPEN_BRACE339=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3853); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE339);

                    pushFollow(FOLLOW_expression_in_builtInCall3855);
                    expression340=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression340.getTree());
                    CLOSE_BRACE341=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3857); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE341);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 910:51: -> ^( BUILTIN_IS_BLANK expression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:910:54: ^( BUILTIN_IS_BLANK expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_IS_BLANK, "BUILTIN_IS_BLANK"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 10 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:911:4: ISLITERAL_TERM OPEN_BRACE expression CLOSE_BRACE
                    {
                    ISLITERAL_TERM342=(Token)match(input,ISLITERAL_TERM,FOLLOW_ISLITERAL_TERM_in_builtInCall3870); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ISLITERAL_TERM.add(ISLITERAL_TERM342);

                    OPEN_BRACE343=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_builtInCall3872); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE343);

                    pushFollow(FOLLOW_expression_in_builtInCall3874);
                    expression344=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression344.getTree());
                    CLOSE_BRACE345=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_builtInCall3876); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE345);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 911:53: -> ^( BUILTIN_IS_LITERAL expression )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:911:56: ^( BUILTIN_IS_LITERAL expression )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_IS_LITERAL, "BUILTIN_IS_LITERAL"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 11 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:912:4: regexExpression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_regexExpression_in_builtInCall3889);
                    regexExpression346=regexExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, regexExpression346.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "builtInCall"

    public static class regexExpression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "regexExpression"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:918:1: regexExpression : ( REGEX_TERM OPEN_BRACE a= expression COMMA_TERM b= expression -> ^( BUILTIN_REGEX_BINARY $a $b) ) ( COMMA_TERM c= expression -> ^( BUILTIN_REGEX_TERNARY $a $b $c) )? CLOSE_BRACE ;
    public final SparqlOwlParser.regexExpression_return regexExpression() throws RecognitionException {
        SparqlOwlParser.regexExpression_return retval = new SparqlOwlParser.regexExpression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token REGEX_TERM347=null;
        Token OPEN_BRACE348=null;
        Token COMMA_TERM349=null;
        Token COMMA_TERM350=null;
        Token CLOSE_BRACE351=null;
        SparqlOwlParser.expression_return a = null;

        SparqlOwlParser.expression_return b = null;

        SparqlOwlParser.expression_return c = null;


        CommonTree REGEX_TERM347_tree=null;
        CommonTree OPEN_BRACE348_tree=null;
        CommonTree COMMA_TERM349_tree=null;
        CommonTree COMMA_TERM350_tree=null;
        CommonTree CLOSE_BRACE351_tree=null;
        RewriteRuleTokenStream stream_REGEX_TERM=new RewriteRuleTokenStream(adaptor,"token REGEX_TERM");
        RewriteRuleTokenStream stream_CLOSE_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_BRACE");
        RewriteRuleTokenStream stream_COMMA_TERM=new RewriteRuleTokenStream(adaptor,"token COMMA_TERM");
        RewriteRuleTokenStream stream_OPEN_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_BRACE");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:919:2: ( ( REGEX_TERM OPEN_BRACE a= expression COMMA_TERM b= expression -> ^( BUILTIN_REGEX_BINARY $a $b) ) ( COMMA_TERM c= expression -> ^( BUILTIN_REGEX_TERNARY $a $b $c) )? CLOSE_BRACE )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:919:4: ( REGEX_TERM OPEN_BRACE a= expression COMMA_TERM b= expression -> ^( BUILTIN_REGEX_BINARY $a $b) ) ( COMMA_TERM c= expression -> ^( BUILTIN_REGEX_TERNARY $a $b $c) )? CLOSE_BRACE
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:919:4: ( REGEX_TERM OPEN_BRACE a= expression COMMA_TERM b= expression -> ^( BUILTIN_REGEX_BINARY $a $b) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:919:6: REGEX_TERM OPEN_BRACE a= expression COMMA_TERM b= expression
            {
            REGEX_TERM347=(Token)match(input,REGEX_TERM,FOLLOW_REGEX_TERM_in_regexExpression3904); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_REGEX_TERM.add(REGEX_TERM347);

            OPEN_BRACE348=(Token)match(input,OPEN_BRACE,FOLLOW_OPEN_BRACE_in_regexExpression3906); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPEN_BRACE.add(OPEN_BRACE348);

            pushFollow(FOLLOW_expression_in_regexExpression3910);
            a=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(a.getTree());
            COMMA_TERM349=(Token)match(input,COMMA_TERM,FOLLOW_COMMA_TERM_in_regexExpression3912); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COMMA_TERM.add(COMMA_TERM349);

            pushFollow(FOLLOW_expression_in_regexExpression3916);
            b=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expression.add(b.getTree());


            // AST REWRITE
            // elements: a, b
            // token labels: 
            // rule labels: retval, b, a
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_b=new RewriteRuleSubtreeStream(adaptor,"rule b",b!=null?b.tree:null);
            RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 919:65: -> ^( BUILTIN_REGEX_BINARY $a $b)
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:919:68: ^( BUILTIN_REGEX_BINARY $a $b)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_REGEX_BINARY, "BUILTIN_REGEX_BINARY"), root_1);

                adaptor.addChild(root_1, stream_a.nextTree());
                adaptor.addChild(root_1, stream_b.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:920:3: ( COMMA_TERM c= expression -> ^( BUILTIN_REGEX_TERNARY $a $b $c) )?
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==COMMA_TERM) ) {
                alt86=1;
            }
            switch (alt86) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:920:5: COMMA_TERM c= expression
                    {
                    COMMA_TERM350=(Token)match(input,COMMA_TERM,FOLLOW_COMMA_TERM_in_regexExpression3936); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COMMA_TERM.add(COMMA_TERM350);

                    pushFollow(FOLLOW_expression_in_regexExpression3940);
                    c=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(c.getTree());


                    // AST REWRITE
                    // elements: c, b, a
                    // token labels: 
                    // rule labels: retval, b, c, a
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_b=new RewriteRuleSubtreeStream(adaptor,"rule b",b!=null?b.tree:null);
                    RewriteRuleSubtreeStream stream_c=new RewriteRuleSubtreeStream(adaptor,"rule c",c!=null?c.tree:null);
                    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 920:29: -> ^( BUILTIN_REGEX_TERNARY $a $b $c)
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:920:32: ^( BUILTIN_REGEX_TERNARY $a $b $c)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BUILTIN_REGEX_TERNARY, "BUILTIN_REGEX_TERNARY"), root_1);

                        adaptor.addChild(root_1, stream_a.nextTree());
                        adaptor.addChild(root_1, stream_b.nextTree());
                        adaptor.addChild(root_1, stream_c.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }

            CLOSE_BRACE351=(Token)match(input,CLOSE_BRACE,FOLLOW_CLOSE_BRACE_in_regexExpression3962); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CLOSE_BRACE.add(CLOSE_BRACE351);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "regexExpression"

    public static class iriRefOrFunction_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "iriRefOrFunction"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:927:1: iriRefOrFunction : ( iriRef -> iriRef ) ( argList -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) )? ;
    public final SparqlOwlParser.iriRefOrFunction_return iriRefOrFunction() throws RecognitionException {
        SparqlOwlParser.iriRefOrFunction_return retval = new SparqlOwlParser.iriRefOrFunction_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.iriRef_return iriRef352 = null;

        SparqlOwlParser.argList_return argList353 = null;


        RewriteRuleSubtreeStream stream_argList=new RewriteRuleSubtreeStream(adaptor,"rule argList");
        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:928:2: ( ( iriRef -> iriRef ) ( argList -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) )? )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:928:4: ( iriRef -> iriRef ) ( argList -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) )?
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:928:4: ( iriRef -> iriRef )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:928:6: iriRef
            {
            pushFollow(FOLLOW_iriRef_in_iriRefOrFunction3977);
            iriRef352=iriRef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_iriRef.add(iriRef352.getTree());


            // AST REWRITE
            // elements: iriRef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 928:13: -> iriRef
            {
                adaptor.addChild(root_0, stream_iriRef.nextTree());

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:929:3: ( argList -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) ) )?
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==OPEN_BRACE) ) {
                alt87=1;
            }
            switch (alt87) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:929:5: argList
                    {
                    pushFollow(FOLLOW_argList_in_iriRefOrFunction3989);
                    argList353=argList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_argList.add(argList353.getTree());


                    // AST REWRITE
                    // elements: iriRef, argList
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 929:13: -> ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:929:16: ^( FUNCTION_CALL ^( FUNCTION_IDENTIFIER iriRef ) ^( FUNCTION_ARGS argList ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION_CALL, "FUNCTION_CALL"), root_1);

                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:929:32: ^( FUNCTION_IDENTIFIER iriRef )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION_IDENTIFIER, "FUNCTION_IDENTIFIER"), root_2);

                        adaptor.addChild(root_2, stream_iriRef.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:929:62: ^( FUNCTION_ARGS argList )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION_ARGS, "FUNCTION_ARGS"), root_2);

                        adaptor.addChild(root_2, stream_argList.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "iriRefOrFunction"

    public static class rdfLiteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rdfLiteral"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:935:1: rdfLiteral : ( string -> ^( LITERAL_PLAIN string ) ) ( LANGTAG -> ^( LITERAL_LANG string LANGTAG ) | DOUBLE_CARAT_TERM iriRef -> ^( LITERAL_TYPED string iriRef ) )? ;
    public final SparqlOwlParser.rdfLiteral_return rdfLiteral() throws RecognitionException {
        SparqlOwlParser.rdfLiteral_return retval = new SparqlOwlParser.rdfLiteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token LANGTAG355=null;
        Token DOUBLE_CARAT_TERM356=null;
        SparqlOwlParser.string_return string354 = null;

        SparqlOwlParser.iriRef_return iriRef357 = null;


        CommonTree LANGTAG355_tree=null;
        CommonTree DOUBLE_CARAT_TERM356_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_CARAT_TERM=new RewriteRuleTokenStream(adaptor,"token DOUBLE_CARAT_TERM");
        RewriteRuleTokenStream stream_LANGTAG=new RewriteRuleTokenStream(adaptor,"token LANGTAG");
        RewriteRuleSubtreeStream stream_string=new RewriteRuleSubtreeStream(adaptor,"rule string");
        RewriteRuleSubtreeStream stream_iriRef=new RewriteRuleSubtreeStream(adaptor,"rule iriRef");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:936:2: ( ( string -> ^( LITERAL_PLAIN string ) ) ( LANGTAG -> ^( LITERAL_LANG string LANGTAG ) | DOUBLE_CARAT_TERM iriRef -> ^( LITERAL_TYPED string iriRef ) )? )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:936:4: ( string -> ^( LITERAL_PLAIN string ) ) ( LANGTAG -> ^( LITERAL_LANG string LANGTAG ) | DOUBLE_CARAT_TERM iriRef -> ^( LITERAL_TYPED string iriRef ) )?
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:936:4: ( string -> ^( LITERAL_PLAIN string ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:936:6: string
            {
            pushFollow(FOLLOW_string_in_rdfLiteral4025);
            string354=string();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_string.add(string354.getTree());


            // AST REWRITE
            // elements: string
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 936:13: -> ^( LITERAL_PLAIN string )
            {
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:936:16: ^( LITERAL_PLAIN string )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_PLAIN, "LITERAL_PLAIN"), root_1);

                adaptor.addChild(root_1, stream_string.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:937:3: ( LANGTAG -> ^( LITERAL_LANG string LANGTAG ) | DOUBLE_CARAT_TERM iriRef -> ^( LITERAL_TYPED string iriRef ) )?
            int alt88=3;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==LANGTAG) ) {
                alt88=1;
            }
            else if ( (LA88_0==DOUBLE_CARAT_TERM) ) {
                alt88=2;
            }
            switch (alt88) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:937:5: LANGTAG
                    {
                    LANGTAG355=(Token)match(input,LANGTAG,FOLLOW_LANGTAG_in_rdfLiteral4041); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LANGTAG.add(LANGTAG355);



                    // AST REWRITE
                    // elements: string, LANGTAG
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 937:13: -> ^( LITERAL_LANG string LANGTAG )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:937:16: ^( LITERAL_LANG string LANGTAG )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_LANG, "LITERAL_LANG"), root_1);

                        adaptor.addChild(root_1, stream_string.nextTree());
                        adaptor.addChild(root_1, stream_LANGTAG.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:938:5: DOUBLE_CARAT_TERM iriRef
                    {
                    DOUBLE_CARAT_TERM356=(Token)match(input,DOUBLE_CARAT_TERM,FOLLOW_DOUBLE_CARAT_TERM_in_rdfLiteral4057); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOUBLE_CARAT_TERM.add(DOUBLE_CARAT_TERM356);

                    pushFollow(FOLLOW_iriRef_in_rdfLiteral4059);
                    iriRef357=iriRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_iriRef.add(iriRef357.getTree());


                    // AST REWRITE
                    // elements: iriRef, string
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 938:30: -> ^( LITERAL_TYPED string iriRef )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:938:33: ^( LITERAL_TYPED string iriRef )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_TYPED, "LITERAL_TYPED"), root_1);

                        adaptor.addChild(root_1, stream_string.nextTree());
                        adaptor.addChild(root_1, stream_iriRef.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "rdfLiteral"

    public static class numericLiteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numericLiteral"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:945:1: numericLiteral : ( numericLiteralUnsigned | numericLiteralPositive | numericLiteralNegative );
    public final SparqlOwlParser.numericLiteral_return numericLiteral() throws RecognitionException {
        SparqlOwlParser.numericLiteral_return retval = new SparqlOwlParser.numericLiteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        SparqlOwlParser.numericLiteralUnsigned_return numericLiteralUnsigned358 = null;

        SparqlOwlParser.numericLiteralPositive_return numericLiteralPositive359 = null;

        SparqlOwlParser.numericLiteralNegative_return numericLiteralNegative360 = null;



        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:946:2: ( numericLiteralUnsigned | numericLiteralPositive | numericLiteralNegative )
            int alt89=3;
            switch ( input.LA(1) ) {
            case INTEGER:
            case DECIMAL:
            case DOUBLE:
                {
                alt89=1;
                }
                break;
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
                {
                alt89=2;
                }
                break;
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
                {
                alt89=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 89, 0, input);

                throw nvae;
            }

            switch (alt89) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:946:4: numericLiteralUnsigned
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_numericLiteralUnsigned_in_numericLiteral4087);
                    numericLiteralUnsigned358=numericLiteralUnsigned();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numericLiteralUnsigned358.getTree());

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:947:4: numericLiteralPositive
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_numericLiteralPositive_in_numericLiteral4092);
                    numericLiteralPositive359=numericLiteralPositive();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numericLiteralPositive359.getTree());

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:948:4: numericLiteralNegative
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_numericLiteralNegative_in_numericLiteral4097);
                    numericLiteralNegative360=numericLiteralNegative();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numericLiteralNegative360.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "numericLiteral"

    public static class numericLiteralUnsigned_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numericLiteralUnsigned"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:954:1: numericLiteralUnsigned : ( INTEGER -> ^( LITERAL_INTEGER INTEGER ) | DECIMAL -> ^( LITERAL_DECIMAL DECIMAL ) | DOUBLE -> ^( LITERAL_DOUBLE DOUBLE ) );
    public final SparqlOwlParser.numericLiteralUnsigned_return numericLiteralUnsigned() throws RecognitionException {
        SparqlOwlParser.numericLiteralUnsigned_return retval = new SparqlOwlParser.numericLiteralUnsigned_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INTEGER361=null;
        Token DECIMAL362=null;
        Token DOUBLE363=null;

        CommonTree INTEGER361_tree=null;
        CommonTree DECIMAL362_tree=null;
        CommonTree DOUBLE363_tree=null;
        RewriteRuleTokenStream stream_INTEGER=new RewriteRuleTokenStream(adaptor,"token INTEGER");
        RewriteRuleTokenStream stream_DOUBLE=new RewriteRuleTokenStream(adaptor,"token DOUBLE");
        RewriteRuleTokenStream stream_DECIMAL=new RewriteRuleTokenStream(adaptor,"token DECIMAL");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:955:2: ( INTEGER -> ^( LITERAL_INTEGER INTEGER ) | DECIMAL -> ^( LITERAL_DECIMAL DECIMAL ) | DOUBLE -> ^( LITERAL_DOUBLE DOUBLE ) )
            int alt90=3;
            switch ( input.LA(1) ) {
            case INTEGER:
                {
                alt90=1;
                }
                break;
            case DECIMAL:
                {
                alt90=2;
                }
                break;
            case DOUBLE:
                {
                alt90=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;
            }

            switch (alt90) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:955:4: INTEGER
                    {
                    INTEGER361=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_numericLiteralUnsigned4110); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_INTEGER.add(INTEGER361);



                    // AST REWRITE
                    // elements: INTEGER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 955:12: -> ^( LITERAL_INTEGER INTEGER )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:955:15: ^( LITERAL_INTEGER INTEGER )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_INTEGER, "LITERAL_INTEGER"), root_1);

                        adaptor.addChild(root_1, stream_INTEGER.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:956:4: DECIMAL
                    {
                    DECIMAL362=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_numericLiteralUnsigned4123); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DECIMAL.add(DECIMAL362);



                    // AST REWRITE
                    // elements: DECIMAL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 956:12: -> ^( LITERAL_DECIMAL DECIMAL )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:956:15: ^( LITERAL_DECIMAL DECIMAL )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_DECIMAL, "LITERAL_DECIMAL"), root_1);

                        adaptor.addChild(root_1, stream_DECIMAL.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:957:4: DOUBLE
                    {
                    DOUBLE363=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_numericLiteralUnsigned4136); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOUBLE.add(DOUBLE363);



                    // AST REWRITE
                    // elements: DOUBLE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 957:11: -> ^( LITERAL_DOUBLE DOUBLE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:957:14: ^( LITERAL_DOUBLE DOUBLE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_DOUBLE, "LITERAL_DOUBLE"), root_1);

                        adaptor.addChild(root_1, stream_DOUBLE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "numericLiteralUnsigned"

    public static class numericLiteralPositive_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numericLiteralPositive"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:963:1: numericLiteralPositive : ( INTEGER_POSITIVE -> ^( LITERAL_INTEGER INTEGER_POSITIVE ) | DECIMAL_POSITIVE -> ^( LITERAL_DECIMAL DECIMAL_POSITIVE ) | DOUBLE_POSITIVE -> ^( LITERAL_DOUBLE DOUBLE_POSITIVE ) );
    public final SparqlOwlParser.numericLiteralPositive_return numericLiteralPositive() throws RecognitionException {
        SparqlOwlParser.numericLiteralPositive_return retval = new SparqlOwlParser.numericLiteralPositive_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INTEGER_POSITIVE364=null;
        Token DECIMAL_POSITIVE365=null;
        Token DOUBLE_POSITIVE366=null;

        CommonTree INTEGER_POSITIVE364_tree=null;
        CommonTree DECIMAL_POSITIVE365_tree=null;
        CommonTree DOUBLE_POSITIVE366_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_POSITIVE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_POSITIVE");
        RewriteRuleTokenStream stream_DECIMAL_POSITIVE=new RewriteRuleTokenStream(adaptor,"token DECIMAL_POSITIVE");
        RewriteRuleTokenStream stream_INTEGER_POSITIVE=new RewriteRuleTokenStream(adaptor,"token INTEGER_POSITIVE");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:964:2: ( INTEGER_POSITIVE -> ^( LITERAL_INTEGER INTEGER_POSITIVE ) | DECIMAL_POSITIVE -> ^( LITERAL_DECIMAL DECIMAL_POSITIVE ) | DOUBLE_POSITIVE -> ^( LITERAL_DOUBLE DOUBLE_POSITIVE ) )
            int alt91=3;
            switch ( input.LA(1) ) {
            case INTEGER_POSITIVE:
                {
                alt91=1;
                }
                break;
            case DECIMAL_POSITIVE:
                {
                alt91=2;
                }
                break;
            case DOUBLE_POSITIVE:
                {
                alt91=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }

            switch (alt91) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:964:4: INTEGER_POSITIVE
                    {
                    INTEGER_POSITIVE364=(Token)match(input,INTEGER_POSITIVE,FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive4157); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_INTEGER_POSITIVE.add(INTEGER_POSITIVE364);



                    // AST REWRITE
                    // elements: INTEGER_POSITIVE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 964:21: -> ^( LITERAL_INTEGER INTEGER_POSITIVE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:964:24: ^( LITERAL_INTEGER INTEGER_POSITIVE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_INTEGER, "LITERAL_INTEGER"), root_1);

                        adaptor.addChild(root_1, stream_INTEGER_POSITIVE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:965:4: DECIMAL_POSITIVE
                    {
                    DECIMAL_POSITIVE365=(Token)match(input,DECIMAL_POSITIVE,FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive4170); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DECIMAL_POSITIVE.add(DECIMAL_POSITIVE365);



                    // AST REWRITE
                    // elements: DECIMAL_POSITIVE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 965:21: -> ^( LITERAL_DECIMAL DECIMAL_POSITIVE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:965:24: ^( LITERAL_DECIMAL DECIMAL_POSITIVE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_DECIMAL, "LITERAL_DECIMAL"), root_1);

                        adaptor.addChild(root_1, stream_DECIMAL_POSITIVE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:966:4: DOUBLE_POSITIVE
                    {
                    DOUBLE_POSITIVE366=(Token)match(input,DOUBLE_POSITIVE,FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive4183); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOUBLE_POSITIVE.add(DOUBLE_POSITIVE366);



                    // AST REWRITE
                    // elements: DOUBLE_POSITIVE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 966:20: -> ^( LITERAL_DOUBLE DOUBLE_POSITIVE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:966:23: ^( LITERAL_DOUBLE DOUBLE_POSITIVE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_DOUBLE, "LITERAL_DOUBLE"), root_1);

                        adaptor.addChild(root_1, stream_DOUBLE_POSITIVE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "numericLiteralPositive"

    public static class numericLiteralNegative_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numericLiteralNegative"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:972:1: numericLiteralNegative : ( INTEGER_NEGATIVE -> ^( LITERAL_INTEGER INTEGER_NEGATIVE ) | DECIMAL_NEGATIVE -> ^( LITERAL_DECIMAL DECIMAL_NEGATIVE ) | DOUBLE_NEGATIVE -> ^( LITERAL_DOUBLE DOUBLE_NEGATIVE ) );
    public final SparqlOwlParser.numericLiteralNegative_return numericLiteralNegative() throws RecognitionException {
        SparqlOwlParser.numericLiteralNegative_return retval = new SparqlOwlParser.numericLiteralNegative_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INTEGER_NEGATIVE367=null;
        Token DECIMAL_NEGATIVE368=null;
        Token DOUBLE_NEGATIVE369=null;

        CommonTree INTEGER_NEGATIVE367_tree=null;
        CommonTree DECIMAL_NEGATIVE368_tree=null;
        CommonTree DOUBLE_NEGATIVE369_tree=null;
        RewriteRuleTokenStream stream_DOUBLE_NEGATIVE=new RewriteRuleTokenStream(adaptor,"token DOUBLE_NEGATIVE");
        RewriteRuleTokenStream stream_DECIMAL_NEGATIVE=new RewriteRuleTokenStream(adaptor,"token DECIMAL_NEGATIVE");
        RewriteRuleTokenStream stream_INTEGER_NEGATIVE=new RewriteRuleTokenStream(adaptor,"token INTEGER_NEGATIVE");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:973:2: ( INTEGER_NEGATIVE -> ^( LITERAL_INTEGER INTEGER_NEGATIVE ) | DECIMAL_NEGATIVE -> ^( LITERAL_DECIMAL DECIMAL_NEGATIVE ) | DOUBLE_NEGATIVE -> ^( LITERAL_DOUBLE DOUBLE_NEGATIVE ) )
            int alt92=3;
            switch ( input.LA(1) ) {
            case INTEGER_NEGATIVE:
                {
                alt92=1;
                }
                break;
            case DECIMAL_NEGATIVE:
                {
                alt92=2;
                }
                break;
            case DOUBLE_NEGATIVE:
                {
                alt92=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }

            switch (alt92) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:973:4: INTEGER_NEGATIVE
                    {
                    INTEGER_NEGATIVE367=(Token)match(input,INTEGER_NEGATIVE,FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative4204); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_INTEGER_NEGATIVE.add(INTEGER_NEGATIVE367);



                    // AST REWRITE
                    // elements: INTEGER_NEGATIVE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 973:21: -> ^( LITERAL_INTEGER INTEGER_NEGATIVE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:973:24: ^( LITERAL_INTEGER INTEGER_NEGATIVE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_INTEGER, "LITERAL_INTEGER"), root_1);

                        adaptor.addChild(root_1, stream_INTEGER_NEGATIVE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:974:4: DECIMAL_NEGATIVE
                    {
                    DECIMAL_NEGATIVE368=(Token)match(input,DECIMAL_NEGATIVE,FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative4217); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DECIMAL_NEGATIVE.add(DECIMAL_NEGATIVE368);



                    // AST REWRITE
                    // elements: DECIMAL_NEGATIVE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 974:21: -> ^( LITERAL_DECIMAL DECIMAL_NEGATIVE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:974:24: ^( LITERAL_DECIMAL DECIMAL_NEGATIVE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_DECIMAL, "LITERAL_DECIMAL"), root_1);

                        adaptor.addChild(root_1, stream_DECIMAL_NEGATIVE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:975:4: DOUBLE_NEGATIVE
                    {
                    DOUBLE_NEGATIVE369=(Token)match(input,DOUBLE_NEGATIVE,FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative4230); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOUBLE_NEGATIVE.add(DOUBLE_NEGATIVE369);



                    // AST REWRITE
                    // elements: DOUBLE_NEGATIVE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 975:20: -> ^( LITERAL_DOUBLE DOUBLE_NEGATIVE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:975:23: ^( LITERAL_DOUBLE DOUBLE_NEGATIVE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_DOUBLE, "LITERAL_DOUBLE"), root_1);

                        adaptor.addChild(root_1, stream_DOUBLE_NEGATIVE.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "numericLiteralNegative"

    public static class booleanLiteral_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "booleanLiteral"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:981:1: booleanLiteral : ( TRUE_TERM -> ^( LITERAL_BOOLEAN_TRUE ) | FALSE_TERM -> ^( LITERAL_BOOLEAN_FALSE ) );
    public final SparqlOwlParser.booleanLiteral_return booleanLiteral() throws RecognitionException {
        SparqlOwlParser.booleanLiteral_return retval = new SparqlOwlParser.booleanLiteral_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token TRUE_TERM370=null;
        Token FALSE_TERM371=null;

        CommonTree TRUE_TERM370_tree=null;
        CommonTree FALSE_TERM371_tree=null;
        RewriteRuleTokenStream stream_TRUE_TERM=new RewriteRuleTokenStream(adaptor,"token TRUE_TERM");
        RewriteRuleTokenStream stream_FALSE_TERM=new RewriteRuleTokenStream(adaptor,"token FALSE_TERM");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:982:2: ( TRUE_TERM -> ^( LITERAL_BOOLEAN_TRUE ) | FALSE_TERM -> ^( LITERAL_BOOLEAN_FALSE ) )
            int alt93=2;
            int LA93_0 = input.LA(1);

            if ( (LA93_0==TRUE_TERM) ) {
                alt93=1;
            }
            else if ( (LA93_0==FALSE_TERM) ) {
                alt93=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }
            switch (alt93) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:982:4: TRUE_TERM
                    {
                    TRUE_TERM370=(Token)match(input,TRUE_TERM,FOLLOW_TRUE_TERM_in_booleanLiteral4251); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TRUE_TERM.add(TRUE_TERM370);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 982:14: -> ^( LITERAL_BOOLEAN_TRUE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:982:17: ^( LITERAL_BOOLEAN_TRUE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_BOOLEAN_TRUE, "LITERAL_BOOLEAN_TRUE"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:983:4: FALSE_TERM
                    {
                    FALSE_TERM371=(Token)match(input,FALSE_TERM,FOLLOW_FALSE_TERM_in_booleanLiteral4262); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FALSE_TERM.add(FALSE_TERM371);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 983:15: -> ^( LITERAL_BOOLEAN_FALSE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:983:18: ^( LITERAL_BOOLEAN_FALSE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LITERAL_BOOLEAN_FALSE, "LITERAL_BOOLEAN_FALSE"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "booleanLiteral"

    public static class string_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "string"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:989:1: string : ( STRING_LITERAL1 | STRING_LITERAL2 | STRING_LITERAL_LONG1 | STRING_LITERAL_LONG2 );
    public final SparqlOwlParser.string_return string() throws RecognitionException {
        SparqlOwlParser.string_return retval = new SparqlOwlParser.string_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set372=null;

        CommonTree set372_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:990:2: ( STRING_LITERAL1 | STRING_LITERAL2 | STRING_LITERAL_LONG1 | STRING_LITERAL_LONG2 )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set372=(Token)input.LT(1);
            if ( (input.LA(1)>=STRING_LITERAL1 && input.LA(1)<=STRING_LITERAL_LONG2) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set372));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "string"

    public static class iriRef_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "iriRef"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1000:1: iriRef : ( IRI_REF_TERM -> ^( IRI_REF IRI_REF_TERM ) | prefixedName -> ^( PREFIXED_NAME prefixedName ) );
    public final SparqlOwlParser.iriRef_return iriRef() throws RecognitionException {
        SparqlOwlParser.iriRef_return retval = new SparqlOwlParser.iriRef_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token IRI_REF_TERM373=null;
        SparqlOwlParser.prefixedName_return prefixedName374 = null;


        CommonTree IRI_REF_TERM373_tree=null;
        RewriteRuleTokenStream stream_IRI_REF_TERM=new RewriteRuleTokenStream(adaptor,"token IRI_REF_TERM");
        RewriteRuleSubtreeStream stream_prefixedName=new RewriteRuleSubtreeStream(adaptor,"rule prefixedName");
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1001:2: ( IRI_REF_TERM -> ^( IRI_REF IRI_REF_TERM ) | prefixedName -> ^( PREFIXED_NAME prefixedName ) )
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( (LA94_0==IRI_REF_TERM) ) {
                alt94=1;
            }
            else if ( (LA94_0==PNAME_NS||LA94_0==PNAME_LN) ) {
                alt94=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }
            switch (alt94) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1001:4: IRI_REF_TERM
                    {
                    IRI_REF_TERM373=(Token)match(input,IRI_REF_TERM,FOLLOW_IRI_REF_TERM_in_iriRef4309); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IRI_REF_TERM.add(IRI_REF_TERM373);



                    // AST REWRITE
                    // elements: IRI_REF_TERM
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 1001:17: -> ^( IRI_REF IRI_REF_TERM )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1001:20: ^( IRI_REF IRI_REF_TERM )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(IRI_REF, "IRI_REF"), root_1);

                        adaptor.addChild(root_1, stream_IRI_REF_TERM.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1002:4: prefixedName
                    {
                    pushFollow(FOLLOW_prefixedName_in_iriRef4322);
                    prefixedName374=prefixedName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_prefixedName.add(prefixedName374.getTree());


                    // AST REWRITE
                    // elements: prefixedName
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 1002:17: -> ^( PREFIXED_NAME prefixedName )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1002:20: ^( PREFIXED_NAME prefixedName )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PREFIXED_NAME, "PREFIXED_NAME"), root_1);

                        adaptor.addChild(root_1, stream_prefixedName.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "iriRef"

    public static class prefixedName_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prefixedName"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1008:1: prefixedName : ( PNAME_LN | PNAME_NS );
    public final SparqlOwlParser.prefixedName_return prefixedName() throws RecognitionException {
        SparqlOwlParser.prefixedName_return retval = new SparqlOwlParser.prefixedName_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set375=null;

        CommonTree set375_tree=null;

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1009:2: ( PNAME_LN | PNAME_NS )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set375=(Token)input.LT(1);
            if ( input.LA(1)==PNAME_NS||input.LA(1)==PNAME_LN ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set375));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "prefixedName"

    public static class blankNode_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blankNode"
    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1016:1: blankNode : ( BLANK_NODE_LABEL -> ^( BLANK_NODE BLANK_NODE_LABEL ) | OPEN_SQUARE_BRACE CLOSE_SQUARE_BRACE -> ^( BLANK_NODE ) );
    public final SparqlOwlParser.blankNode_return blankNode() throws RecognitionException {
        SparqlOwlParser.blankNode_return retval = new SparqlOwlParser.blankNode_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token BLANK_NODE_LABEL376=null;
        Token OPEN_SQUARE_BRACE377=null;
        Token CLOSE_SQUARE_BRACE378=null;

        CommonTree BLANK_NODE_LABEL376_tree=null;
        CommonTree OPEN_SQUARE_BRACE377_tree=null;
        CommonTree CLOSE_SQUARE_BRACE378_tree=null;
        RewriteRuleTokenStream stream_CLOSE_SQUARE_BRACE=new RewriteRuleTokenStream(adaptor,"token CLOSE_SQUARE_BRACE");
        RewriteRuleTokenStream stream_OPEN_SQUARE_BRACE=new RewriteRuleTokenStream(adaptor,"token OPEN_SQUARE_BRACE");
        RewriteRuleTokenStream stream_BLANK_NODE_LABEL=new RewriteRuleTokenStream(adaptor,"token BLANK_NODE_LABEL");

        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1017:2: ( BLANK_NODE_LABEL -> ^( BLANK_NODE BLANK_NODE_LABEL ) | OPEN_SQUARE_BRACE CLOSE_SQUARE_BRACE -> ^( BLANK_NODE ) )
            int alt95=2;
            int LA95_0 = input.LA(1);

            if ( (LA95_0==BLANK_NODE_LABEL) ) {
                alt95=1;
            }
            else if ( (LA95_0==OPEN_SQUARE_BRACE) ) {
                alt95=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 95, 0, input);

                throw nvae;
            }
            switch (alt95) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1017:4: BLANK_NODE_LABEL
                    {
                    BLANK_NODE_LABEL376=(Token)match(input,BLANK_NODE_LABEL,FOLLOW_BLANK_NODE_LABEL_in_blankNode4361); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BLANK_NODE_LABEL.add(BLANK_NODE_LABEL376);



                    // AST REWRITE
                    // elements: BLANK_NODE_LABEL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 1017:21: -> ^( BLANK_NODE BLANK_NODE_LABEL )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1017:24: ^( BLANK_NODE BLANK_NODE_LABEL )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLANK_NODE, "BLANK_NODE"), root_1);

                        adaptor.addChild(root_1, stream_BLANK_NODE_LABEL.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1018:4: OPEN_SQUARE_BRACE CLOSE_SQUARE_BRACE
                    {
                    OPEN_SQUARE_BRACE377=(Token)match(input,OPEN_SQUARE_BRACE,FOLLOW_OPEN_SQUARE_BRACE_in_blankNode4374); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OPEN_SQUARE_BRACE.add(OPEN_SQUARE_BRACE377);

                    CLOSE_SQUARE_BRACE378=(Token)match(input,CLOSE_SQUARE_BRACE,FOLLOW_CLOSE_SQUARE_BRACE_in_blankNode4376); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_SQUARE_BRACE.add(CLOSE_SQUARE_BRACE378);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 1018:41: -> ^( BLANK_NODE )
                    {
                        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1018:44: ^( BLANK_NODE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLANK_NODE, "BLANK_NODE"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        	catch( RecognitionException rce ) {
        		throw rce;
        	}
        finally {
        }
        return retval;
    }
    // $ANTLR end "blankNode"

    // $ANTLR start synpred1_SparqlOwl
    public final void synpred1_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:366:5: ( primary )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:366:6: primary
        {
        pushFollow(FOLLOW_primary_in_synpred1_SparqlOwl1386);
        primary();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_SparqlOwl

    // $ANTLR start synpred2_SparqlOwl
    public final void synpred2_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:374:5: ( primary )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:374:6: primary
        {
        pushFollow(FOLLOW_primary_in_synpred2_SparqlOwl1438);
        primary();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_SparqlOwl

    // $ANTLR start synpred3_SparqlOwl
    public final void synpred3_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:382:5: ( primary )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:382:6: primary
        {
        pushFollow(FOLLOW_primary_in_synpred3_SparqlOwl1490);
        primary();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_SparqlOwl

    // $ANTLR start synpred4_SparqlOwl
    public final void synpred4_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:4: ( graphPatternNotTriples )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:578:6: graphPatternNotTriples
        {
        pushFollow(FOLLOW_graphPatternNotTriples_in_synpred4_SparqlOwl2253);
        graphPatternNotTriples();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_SparqlOwl

    // $ANTLR start synpred5_SparqlOwl
    public final void synpred5_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:580:4: ( triplesSameSubject DOT_TERM )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:580:5: triplesSameSubject DOT_TERM
        {
        pushFollow(FOLLOW_triplesSameSubject_in_synpred5_SparqlOwl2283);
        triplesSameSubject();

        state._fsp--;
        if (state.failed) return ;
        match(input,DOT_TERM,FOLLOW_DOT_TERM_in_synpred5_SparqlOwl2285); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_SparqlOwl

    // $ANTLR start synpred6_SparqlOwl
    public final void synpred6_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:675:4: ( varOrTerm propertyListNotEmpty )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:675:5: varOrTerm propertyListNotEmpty
        {
        pushFollow(FOLLOW_varOrTerm_in_synpred6_SparqlOwl2651);
        varOrTerm();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_propertyListNotEmpty_in_synpred6_SparqlOwl2653);
        propertyListNotEmpty();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_SparqlOwl

    // $ANTLR start synpred7_SparqlOwl
    public final void synpred7_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:676:4: ( triplesNode propertyListNotEmpty )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:676:5: triplesNode propertyListNotEmpty
        {
        pushFollow(FOLLOW_triplesNode_in_synpred7_SparqlOwl2679);
        triplesNode();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_propertyListNotEmpty_in_synpred7_SparqlOwl2681);
        propertyListNotEmpty();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_SparqlOwl

    // $ANTLR start synpred8_SparqlOwl
    public final void synpred8_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:677:4: ( triplesNode )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:677:5: triplesNode
        {
        pushFollow(FOLLOW_triplesNode_in_synpred8_SparqlOwl2707);
        triplesNode();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_SparqlOwl

    // $ANTLR start synpred9_SparqlOwl
    public final void synpred9_SparqlOwl_fragment() throws RecognitionException {   
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:710:4: ( graphNode ( DOT_TERM | SEMICOLON_TERM | COMMA_TERM | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE ) )
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:710:5: graphNode ( DOT_TERM | SEMICOLON_TERM | COMMA_TERM | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE )
        {
        pushFollow(FOLLOW_graphNode_in_synpred9_SparqlOwl2847);
        graphNode();

        state._fsp--;
        if (state.failed) return ;
        if ( input.LA(1)==COMMA_TERM||(input.LA(1)>=OPEN_CURLY_BRACE && input.LA(1)<=CLOSE_CURLY_BRACE)||input.LA(1)==DOT_TERM||input.LA(1)==SEMICOLON_TERM ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }
    }
    // $ANTLR end synpred9_SparqlOwl

    // Delegated rules

    public final boolean synpred3_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_SparqlOwl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_SparqlOwl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA13 dfa13 = new DFA13(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA18 dfa18 = new DFA18(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA69 dfa69 = new DFA69(this);
    static final String DFA13_eotS =
        "\21\uffff";
    static final String DFA13_eofS =
        "\1\uffff\2\11\1\13\1\14\1\15\1\16\12\uffff";
    static final String DFA13_minS =
        "\1\43\6\170\1\u0090\11\uffff";
    static final String DFA13_maxS =
        "\10\u00d2\11\uffff";
    static final String DFA13_acceptS =
        "\10\uffff\1\11\1\1\1\6\1\2\1\3\1\4\1\5\1\10\1\7";
    static final String DFA13_specialS =
        "\21\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\4\16\uffff\1\5\10\uffff\1\3\54\uffff\1\6\36\uffff\1\7\1\uffff"+
            "\1\10\12\uffff\1\1\1\uffff\1\2\73\uffff\1\2",
            "\1\12\2\11\11\uffff\2\11\1\uffff\2\11\1\uffff\1\11\11\uffff"+
            "\1\11\1\uffff\1\11\20\uffff\3\11\1\uffff\5\11\42\uffff\1\11",
            "\1\12\2\11\11\uffff\2\11\1\uffff\2\11\1\uffff\1\11\11\uffff"+
            "\1\11\1\uffff\1\11\20\uffff\3\11\1\uffff\5\11\42\uffff\1\11",
            "\1\12\2\13\11\uffff\2\13\1\uffff\2\13\1\uffff\1\13\11\uffff"+
            "\1\13\1\uffff\1\13\20\uffff\3\13\1\uffff\5\13\42\uffff\1\13",
            "\1\12\2\14\11\uffff\2\14\1\uffff\2\14\1\uffff\1\14\11\uffff"+
            "\1\14\1\uffff\1\14\20\uffff\3\14\1\uffff\5\14\42\uffff\1\14",
            "\1\12\2\15\11\uffff\2\15\1\uffff\2\15\1\uffff\1\15\11\uffff"+
            "\1\15\1\uffff\1\15\20\uffff\3\15\1\uffff\5\15\42\uffff\1\15",
            "\1\12\2\16\11\uffff\2\16\1\uffff\2\16\1\uffff\1\16\11\uffff"+
            "\1\16\1\uffff\1\16\20\uffff\3\16\1\uffff\5\16\42\uffff\1\16",
            "\1\20\3\uffff\1\17\1\uffff\1\17\55\uffff\16\20\1\17",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "312:1: atomic : ( iriRef -> ^( CLASS_OR_DATATYPE iriRef ) | INTEGER_TERM -> ^( DATATYPE INTEGER_TERM ) | DECIMAL_TERM -> ^( DATATYPE DECIMAL_TERM ) | FLOAT_TERM -> ^( DATATYPE FLOAT_TERM ) | STRING_TERM -> ^( DATATYPE STRING_TERM ) | datatypeRestriction | OPEN_CURLY_BRACE ( literal )+ CLOSE_CURLY_BRACE -> ^( VALUE_ENUMERATION ( literal )+ ) | OPEN_CURLY_BRACE ( individual )+ CLOSE_CURLY_BRACE -> ^( INDIVIDUAL_ENUMERATION ( individual )+ ) | OPEN_BRACE disjunction CLOSE_BRACE );";
        }
    }
    static final String DFA14_eotS =
        "\13\uffff";
    static final String DFA14_eofS =
        "\13\uffff";
    static final String DFA14_minS =
        "\1\167\1\u0094\4\u008b\5\uffff";
    static final String DFA14_maxS =
        "\2\u00d2\4\u0092\5\uffff";
    static final String DFA14_acceptS =
        "\6\uffff\1\5\1\3\1\1\1\4\1\2";
    static final String DFA14_specialS =
        "\13\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\1\34\uffff\1\2\1\uffff\1\3\73\uffff\1\3",
            "\1\4\1\uffff\1\5\73\uffff\1\5",
            "\1\10\1\12\1\7\1\11\1\6\1\uffff\2\6",
            "\1\10\1\12\1\7\1\11\1\6\1\uffff\2\6",
            "\1\10\1\12\1\7\1\11\1\6\1\uffff\2\6",
            "\1\10\1\12\1\7\1\11\1\6\1\uffff\2\6",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "327:1: restriction : ( someRestriction | allRestriction | valueRestriction | selfRestriction | numberRestriction );";
        }
    }
    static final String DFA17_eotS =
        "\32\uffff";
    static final String DFA17_eofS =
        "\1\13\31\uffff";
    static final String DFA17_minS =
        "\1\43\2\uffff\2\0\4\uffff\1\0\20\uffff";
    static final String DFA17_maxS =
        "\1\u00d2\2\uffff\2\0\4\uffff\1\0\20\uffff";
    static final String DFA17_acceptS =
        "\1\uffff\2\1\2\uffff\4\1\1\uffff\1\1\1\2\16\uffff";
    static final String DFA17_specialS =
        "\1\0\2\uffff\1\1\1\2\4\uffff\1\3\20\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\6\16\uffff\1\7\10\uffff\1\5\54\uffff\1\10\16\uffff\1\2\1"+
            "\uffff\2\13\11\uffff\2\13\1\1\1\11\1\13\1\12\1\13\11\uffff\1"+
            "\3\1\uffff\1\4\20\uffff\3\13\1\uffff\5\13\42\uffff\1\4",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "366:3: ( ( primary )=> primary | )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA17_0 = input.LA(1);

                         
                        int index17_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA17_0==NOT_TERM) && (synpred1_SparqlOwl())) {s = 1;}

                        else if ( (LA17_0==INVERSE_TERM) && (synpred1_SparqlOwl())) {s = 2;}

                        else if ( (LA17_0==IRI_REF_TERM) ) {s = 3;}

                        else if ( (LA17_0==PNAME_NS||LA17_0==PNAME_LN) ) {s = 4;}

                        else if ( (LA17_0==INTEGER_TERM) && (synpred1_SparqlOwl())) {s = 5;}

                        else if ( (LA17_0==DECIMAL_TERM) && (synpred1_SparqlOwl())) {s = 6;}

                        else if ( (LA17_0==FLOAT_TERM) && (synpred1_SparqlOwl())) {s = 7;}

                        else if ( (LA17_0==STRING_TERM) && (synpred1_SparqlOwl())) {s = 8;}

                        else if ( (LA17_0==OPEN_CURLY_BRACE) ) {s = 9;}

                        else if ( (LA17_0==OPEN_BRACE) && (synpred1_SparqlOwl())) {s = 10;}

                        else if ( (LA17_0==EOF||(LA17_0>=COMMA_TERM && LA17_0<=CLOSE_SQUARE_BRACE)||(LA17_0>=OR_TERM && LA17_0<=AND_TERM)||LA17_0==CLOSE_CURLY_BRACE||LA17_0==CLOSE_BRACE||(LA17_0>=DOT_TERM && LA17_0<=GRAPH_TERM)||(LA17_0>=FILTER_TERM && LA17_0<=VAR2)) ) {s = 11;}

                         
                        input.seek(index17_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA17_3 = input.LA(1);

                         
                        int index17_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index17_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA17_4 = input.LA(1);

                         
                        int index17_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index17_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA17_9 = input.LA(1);

                         
                        int index17_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index17_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 17, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA18_eotS =
        "\32\uffff";
    static final String DFA18_eofS =
        "\1\13\31\uffff";
    static final String DFA18_minS =
        "\1\43\2\uffff\2\0\4\uffff\1\0\20\uffff";
    static final String DFA18_maxS =
        "\1\u00d2\2\uffff\2\0\4\uffff\1\0\20\uffff";
    static final String DFA18_acceptS =
        "\1\uffff\2\1\2\uffff\4\1\1\uffff\1\1\1\2\16\uffff";
    static final String DFA18_specialS =
        "\1\0\2\uffff\1\1\1\2\4\uffff\1\3\20\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\6\16\uffff\1\7\10\uffff\1\5\54\uffff\1\10\16\uffff\1\2\1"+
            "\uffff\2\13\11\uffff\2\13\1\1\1\11\1\13\1\12\1\13\11\uffff\1"+
            "\3\1\uffff\1\4\20\uffff\3\13\1\uffff\5\13\42\uffff\1\4",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "374:3: ( ( primary )=> primary | )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA18_0 = input.LA(1);

                         
                        int index18_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA18_0==NOT_TERM) && (synpred2_SparqlOwl())) {s = 1;}

                        else if ( (LA18_0==INVERSE_TERM) && (synpred2_SparqlOwl())) {s = 2;}

                        else if ( (LA18_0==IRI_REF_TERM) ) {s = 3;}

                        else if ( (LA18_0==PNAME_NS||LA18_0==PNAME_LN) ) {s = 4;}

                        else if ( (LA18_0==INTEGER_TERM) && (synpred2_SparqlOwl())) {s = 5;}

                        else if ( (LA18_0==DECIMAL_TERM) && (synpred2_SparqlOwl())) {s = 6;}

                        else if ( (LA18_0==FLOAT_TERM) && (synpred2_SparqlOwl())) {s = 7;}

                        else if ( (LA18_0==STRING_TERM) && (synpred2_SparqlOwl())) {s = 8;}

                        else if ( (LA18_0==OPEN_CURLY_BRACE) ) {s = 9;}

                        else if ( (LA18_0==OPEN_BRACE) && (synpred2_SparqlOwl())) {s = 10;}

                        else if ( (LA18_0==EOF||(LA18_0>=COMMA_TERM && LA18_0<=CLOSE_SQUARE_BRACE)||(LA18_0>=OR_TERM && LA18_0<=AND_TERM)||LA18_0==CLOSE_CURLY_BRACE||LA18_0==CLOSE_BRACE||(LA18_0>=DOT_TERM && LA18_0<=GRAPH_TERM)||(LA18_0>=FILTER_TERM && LA18_0<=VAR2)) ) {s = 11;}

                         
                        input.seek(index18_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA18_3 = input.LA(1);

                         
                        int index18_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index18_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA18_4 = input.LA(1);

                         
                        int index18_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index18_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA18_9 = input.LA(1);

                         
                        int index18_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index18_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 18, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA19_eotS =
        "\32\uffff";
    static final String DFA19_eofS =
        "\1\13\31\uffff";
    static final String DFA19_minS =
        "\1\43\2\uffff\2\0\4\uffff\1\0\20\uffff";
    static final String DFA19_maxS =
        "\1\u00d2\2\uffff\2\0\4\uffff\1\0\20\uffff";
    static final String DFA19_acceptS =
        "\1\uffff\2\1\2\uffff\4\1\1\uffff\1\1\1\2\16\uffff";
    static final String DFA19_specialS =
        "\1\0\2\uffff\1\1\1\2\4\uffff\1\3\20\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\6\16\uffff\1\7\10\uffff\1\5\54\uffff\1\10\16\uffff\1\2\1"+
            "\uffff\2\13\11\uffff\2\13\1\1\1\11\1\13\1\12\1\13\11\uffff\1"+
            "\3\1\uffff\1\4\20\uffff\3\13\1\uffff\5\13\42\uffff\1\4",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "382:3: ( ( primary )=> primary | )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA19_0 = input.LA(1);

                         
                        int index19_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA19_0==NOT_TERM) && (synpred3_SparqlOwl())) {s = 1;}

                        else if ( (LA19_0==INVERSE_TERM) && (synpred3_SparqlOwl())) {s = 2;}

                        else if ( (LA19_0==IRI_REF_TERM) ) {s = 3;}

                        else if ( (LA19_0==PNAME_NS||LA19_0==PNAME_LN) ) {s = 4;}

                        else if ( (LA19_0==INTEGER_TERM) && (synpred3_SparqlOwl())) {s = 5;}

                        else if ( (LA19_0==DECIMAL_TERM) && (synpred3_SparqlOwl())) {s = 6;}

                        else if ( (LA19_0==FLOAT_TERM) && (synpred3_SparqlOwl())) {s = 7;}

                        else if ( (LA19_0==STRING_TERM) && (synpred3_SparqlOwl())) {s = 8;}

                        else if ( (LA19_0==OPEN_CURLY_BRACE) ) {s = 9;}

                        else if ( (LA19_0==OPEN_BRACE) && (synpred3_SparqlOwl())) {s = 10;}

                        else if ( (LA19_0==EOF||(LA19_0>=COMMA_TERM && LA19_0<=CLOSE_SQUARE_BRACE)||(LA19_0>=OR_TERM && LA19_0<=AND_TERM)||LA19_0==CLOSE_CURLY_BRACE||LA19_0==CLOSE_BRACE||(LA19_0>=DOT_TERM && LA19_0<=GRAPH_TERM)||(LA19_0>=FILTER_TERM && LA19_0<=VAR2)) ) {s = 11;}

                         
                        input.seek(index19_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA19_3 = input.LA(1);

                         
                        int index19_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index19_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA19_4 = input.LA(1);

                         
                        int index19_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index19_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA19_9 = input.LA(1);

                         
                        int index19_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_SparqlOwl()) ) {s = 10;}

                        else if ( (true) ) {s = 11;}

                         
                        input.seek(index19_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA51_eotS =
        "\40\uffff";
    static final String DFA51_eofS =
        "\40\uffff";
    static final String DFA51_minS =
        "\1\43\1\uffff\1\0\2\uffff\31\0\2\uffff";
    static final String DFA51_maxS =
        "\1\u00d3\1\uffff\1\0\2\uffff\31\0\2\uffff";
    static final String DFA51_acceptS =
        "\1\uffff\1\1\1\uffff\1\1\1\2\31\uffff\1\3\1\4";
    static final String DFA51_specialS =
        "\1\0\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12"+
        "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27"+
        "\1\30\1\31\1\32\2\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\33\16\uffff\1\34\10\uffff\1\32\54\uffff\1\35\16\uffff\1\31"+
            "\1\26\15\uffff\1\30\1\2\1\uffff\1\27\6\uffff\1\12\3\uffff\1"+
            "\7\1\uffff\1\10\21\uffff\1\1\1\3\1\uffff\1\4\2\uffff\1\5\1\6"+
            "\24\uffff\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24"+
            "\4\11\1\10\1\25",
            "",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "577:1: groupGraphPatternNoBraces : ( ( graphPatternNotTriples )=> graphPatternNotTriples ( DOT_TERM )? ( groupGraphPatternNoBraces )? | filter ( DOT_TERM )? ( groupGraphPatternNoBraces )? | ( triplesSameSubject DOT_TERM )=> triplesSameSubject DOT_TERM ( groupGraphPatternNoBraces )? | triplesSameSubject ( canFollowTriplesWithoutDot )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_0 = input.LA(1);

                         
                        int index51_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA51_0==OPTIONAL_TERM) && (synpred4_SparqlOwl())) {s = 1;}

                        else if ( (LA51_0==OPEN_CURLY_BRACE) ) {s = 2;}

                        else if ( (LA51_0==GRAPH_TERM) && (synpred4_SparqlOwl())) {s = 3;}

                        else if ( (LA51_0==FILTER_TERM) ) {s = 4;}

                        else if ( (LA51_0==VAR1) ) {s = 5;}

                        else if ( (LA51_0==VAR2) ) {s = 6;}

                        else if ( (LA51_0==IRI_REF_TERM) ) {s = 7;}

                        else if ( (LA51_0==PNAME_NS||LA51_0==PNAME_LN) ) {s = 8;}

                        else if ( ((LA51_0>=STRING_LITERAL1 && LA51_0<=STRING_LITERAL_LONG2)) ) {s = 9;}

                        else if ( (LA51_0==INTEGER) ) {s = 10;}

                        else if ( (LA51_0==DECIMAL) ) {s = 11;}

                        else if ( (LA51_0==DOUBLE) ) {s = 12;}

                        else if ( (LA51_0==INTEGER_POSITIVE) ) {s = 13;}

                        else if ( (LA51_0==DECIMAL_POSITIVE) ) {s = 14;}

                        else if ( (LA51_0==DOUBLE_POSITIVE) ) {s = 15;}

                        else if ( (LA51_0==INTEGER_NEGATIVE) ) {s = 16;}

                        else if ( (LA51_0==DECIMAL_NEGATIVE) ) {s = 17;}

                        else if ( (LA51_0==DOUBLE_NEGATIVE) ) {s = 18;}

                        else if ( (LA51_0==TRUE_TERM) ) {s = 19;}

                        else if ( (LA51_0==FALSE_TERM) ) {s = 20;}

                        else if ( (LA51_0==BLANK_NODE_LABEL) ) {s = 21;}

                        else if ( (LA51_0==OPEN_SQUARE_BRACE) ) {s = 22;}

                        else if ( (LA51_0==OPEN_BRACE) ) {s = 23;}

                        else if ( (LA51_0==NOT_TERM) ) {s = 24;}

                        else if ( (LA51_0==INVERSE_TERM) ) {s = 25;}

                        else if ( (LA51_0==INTEGER_TERM) ) {s = 26;}

                        else if ( (LA51_0==DECIMAL_TERM) ) {s = 27;}

                        else if ( (LA51_0==FLOAT_TERM) ) {s = 28;}

                        else if ( (LA51_0==STRING_TERM) ) {s = 29;}

                         
                        input.seek(index51_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA51_2 = input.LA(1);

                         
                        int index51_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_SparqlOwl()) ) {s = 3;}

                        else if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA51_5 = input.LA(1);

                         
                        int index51_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_5);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA51_6 = input.LA(1);

                         
                        int index51_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_6);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA51_7 = input.LA(1);

                         
                        int index51_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_7);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA51_8 = input.LA(1);

                         
                        int index51_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_8);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA51_9 = input.LA(1);

                         
                        int index51_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_9);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA51_10 = input.LA(1);

                         
                        int index51_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_10);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA51_11 = input.LA(1);

                         
                        int index51_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_11);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA51_12 = input.LA(1);

                         
                        int index51_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_12);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA51_13 = input.LA(1);

                         
                        int index51_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_13);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA51_14 = input.LA(1);

                         
                        int index51_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_14);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA51_15 = input.LA(1);

                         
                        int index51_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_15);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA51_16 = input.LA(1);

                         
                        int index51_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_16);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA51_17 = input.LA(1);

                         
                        int index51_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_17);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA51_18 = input.LA(1);

                         
                        int index51_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_18);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA51_19 = input.LA(1);

                         
                        int index51_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_19);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA51_20 = input.LA(1);

                         
                        int index51_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_20);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA51_21 = input.LA(1);

                         
                        int index51_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_21);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA51_22 = input.LA(1);

                         
                        int index51_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_22);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA51_23 = input.LA(1);

                         
                        int index51_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_23);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA51_24 = input.LA(1);

                         
                        int index51_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_24);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA51_25 = input.LA(1);

                         
                        int index51_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_25);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA51_26 = input.LA(1);

                         
                        int index51_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_26);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA51_27 = input.LA(1);

                         
                        int index51_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_27);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA51_28 = input.LA(1);

                         
                        int index51_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_28);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA51_29 = input.LA(1);

                         
                        int index51_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_SparqlOwl()) ) {s = 30;}

                        else if ( (true) ) {s = 31;}

                         
                        input.seek(index51_29);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 51, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA65_eotS =
        "\35\uffff";
    static final String DFA65_eofS =
        "\35\uffff";
    static final String DFA65_minS =
        "\1\43\2\uffff\2\0\15\uffff\2\0\11\uffff";
    static final String DFA65_maxS =
        "\1\u00d3\2\uffff\2\0\15\uffff\2\0\11\uffff";
    static final String DFA65_acceptS =
        "\1\uffff\2\1\2\uffff\15\1\2\uffff\1\4\6\uffff\1\2\1\3";
    static final String DFA65_specialS =
        "\1\0\2\uffff\1\1\1\2\15\uffff\1\3\1\4\11\uffff}>";
    static final String[] DFA65_transitionS = {
            "\1\24\16\uffff\1\24\10\uffff\1\24\54\uffff\1\24\16\uffff\1\24"+
            "\1\22\15\uffff\2\24\1\uffff\1\23\6\uffff\1\6\3\uffff\1\3\1\uffff"+
            "\1\4\27\uffff\1\1\1\2\24\uffff\1\7\1\10\1\11\1\12\1\13\1\14"+
            "\1\15\1\16\1\17\1\20\4\5\1\4\1\21",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA65_eot = DFA.unpackEncodedString(DFA65_eotS);
    static final short[] DFA65_eof = DFA.unpackEncodedString(DFA65_eofS);
    static final char[] DFA65_min = DFA.unpackEncodedStringToUnsignedChars(DFA65_minS);
    static final char[] DFA65_max = DFA.unpackEncodedStringToUnsignedChars(DFA65_maxS);
    static final short[] DFA65_accept = DFA.unpackEncodedString(DFA65_acceptS);
    static final short[] DFA65_special = DFA.unpackEncodedString(DFA65_specialS);
    static final short[][] DFA65_transition;

    static {
        int numStates = DFA65_transitionS.length;
        DFA65_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA65_transition[i] = DFA.unpackEncodedString(DFA65_transitionS[i]);
        }
    }

    class DFA65 extends DFA {

        public DFA65(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA65_eot;
            this.eof = DFA65_eof;
            this.min = DFA65_min;
            this.max = DFA65_max;
            this.accept = DFA65_accept;
            this.special = DFA65_special;
            this.transition = DFA65_transition;
        }
        public String getDescription() {
            return "673:1: triplesSameSubject options {memoize=true; } : ( ( varOrTerm propertyListNotEmpty )=> varOrTerm propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT varOrTerm ) propertyListNotEmpty ) | ( triplesNode propertyListNotEmpty )=> triplesNode propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) propertyListNotEmpty ) | ( triplesNode )=> triplesNode -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT triplesNode ) ) | disjunction propertyListNotEmpty -> ^( SUBJECT_TRIPLE_GROUP ^( SUBJECT disjunction ) propertyListNotEmpty ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA65_0 = input.LA(1);

                         
                        int index65_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA65_0==VAR1) && (synpred6_SparqlOwl())) {s = 1;}

                        else if ( (LA65_0==VAR2) && (synpred6_SparqlOwl())) {s = 2;}

                        else if ( (LA65_0==IRI_REF_TERM) ) {s = 3;}

                        else if ( (LA65_0==PNAME_NS||LA65_0==PNAME_LN) ) {s = 4;}

                        else if ( ((LA65_0>=STRING_LITERAL1 && LA65_0<=STRING_LITERAL_LONG2)) && (synpred6_SparqlOwl())) {s = 5;}

                        else if ( (LA65_0==INTEGER) && (synpred6_SparqlOwl())) {s = 6;}

                        else if ( (LA65_0==DECIMAL) && (synpred6_SparqlOwl())) {s = 7;}

                        else if ( (LA65_0==DOUBLE) && (synpred6_SparqlOwl())) {s = 8;}

                        else if ( (LA65_0==INTEGER_POSITIVE) && (synpred6_SparqlOwl())) {s = 9;}

                        else if ( (LA65_0==DECIMAL_POSITIVE) && (synpred6_SparqlOwl())) {s = 10;}

                        else if ( (LA65_0==DOUBLE_POSITIVE) && (synpred6_SparqlOwl())) {s = 11;}

                        else if ( (LA65_0==INTEGER_NEGATIVE) && (synpred6_SparqlOwl())) {s = 12;}

                        else if ( (LA65_0==DECIMAL_NEGATIVE) && (synpred6_SparqlOwl())) {s = 13;}

                        else if ( (LA65_0==DOUBLE_NEGATIVE) && (synpred6_SparqlOwl())) {s = 14;}

                        else if ( (LA65_0==TRUE_TERM) && (synpred6_SparqlOwl())) {s = 15;}

                        else if ( (LA65_0==FALSE_TERM) && (synpred6_SparqlOwl())) {s = 16;}

                        else if ( (LA65_0==BLANK_NODE_LABEL) && (synpred6_SparqlOwl())) {s = 17;}

                        else if ( (LA65_0==OPEN_SQUARE_BRACE) ) {s = 18;}

                        else if ( (LA65_0==OPEN_BRACE) ) {s = 19;}

                        else if ( (LA65_0==DECIMAL_TERM||LA65_0==FLOAT_TERM||LA65_0==INTEGER_TERM||LA65_0==STRING_TERM||LA65_0==INVERSE_TERM||(LA65_0>=NOT_TERM && LA65_0<=OPEN_CURLY_BRACE)) ) {s = 20;}

                         
                        input.seek(index65_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA65_3 = input.LA(1);

                         
                        int index65_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_SparqlOwl()) ) {s = 17;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index65_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA65_4 = input.LA(1);

                         
                        int index65_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_SparqlOwl()) ) {s = 17;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index65_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA65_18 = input.LA(1);

                         
                        int index65_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_SparqlOwl()) ) {s = 17;}

                        else if ( (synpred7_SparqlOwl()) ) {s = 27;}

                        else if ( (synpred8_SparqlOwl()) ) {s = 28;}

                         
                        input.seek(index65_18);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA65_19 = input.LA(1);

                         
                        int index65_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_SparqlOwl()) ) {s = 17;}

                        else if ( (synpred7_SparqlOwl()) ) {s = 27;}

                        else if ( (synpred8_SparqlOwl()) ) {s = 28;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index65_19);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 65, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA69_eotS =
        "\33\uffff";
    static final String DFA69_eofS =
        "\33\uffff";
    static final String DFA69_minS =
        "\1\43\2\uffff\2\0\16\uffff\1\0\7\uffff";
    static final String DFA69_maxS =
        "\1\u00d3\2\uffff\2\0\16\uffff\1\0\7\uffff";
    static final String DFA69_acceptS =
        "\1\uffff\2\1\2\uffff\16\1\1\uffff\1\2\6\uffff";
    static final String DFA69_specialS =
        "\1\0\2\uffff\1\1\1\2\16\uffff\1\3\7\uffff}>";
    static final String[] DFA69_transitionS = {
            "\1\24\16\uffff\1\24\10\uffff\1\24\54\uffff\1\24\16\uffff\1\24"+
            "\1\22\15\uffff\2\24\1\uffff\1\23\6\uffff\1\6\3\uffff\1\3\1\uffff"+
            "\1\4\27\uffff\1\1\1\2\24\uffff\1\7\1\10\1\11\1\12\1\13\1\14"+
            "\1\15\1\16\1\17\1\20\4\5\1\4\1\21",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA69_eot = DFA.unpackEncodedString(DFA69_eotS);
    static final short[] DFA69_eof = DFA.unpackEncodedString(DFA69_eofS);
    static final char[] DFA69_min = DFA.unpackEncodedStringToUnsignedChars(DFA69_minS);
    static final char[] DFA69_max = DFA.unpackEncodedStringToUnsignedChars(DFA69_maxS);
    static final short[] DFA69_accept = DFA.unpackEncodedString(DFA69_acceptS);
    static final short[] DFA69_special = DFA.unpackEncodedString(DFA69_specialS);
    static final short[][] DFA69_transition;

    static {
        int numStates = DFA69_transitionS.length;
        DFA69_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA69_transition[i] = DFA.unpackEncodedString(DFA69_transitionS[i]);
        }
    }

    class DFA69 extends DFA {

        public DFA69(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 69;
            this.eot = DFA69_eot;
            this.eof = DFA69_eof;
            this.min = DFA69_min;
            this.max = DFA69_max;
            this.accept = DFA69_accept;
            this.special = DFA69_special;
            this.transition = DFA69_transition;
        }
        public String getDescription() {
            return "709:1: object : ( ( graphNode ( DOT_TERM | SEMICOLON_TERM | COMMA_TERM | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE ) )=> graphNode -> ^( OBJECT graphNode ) | disjunction -> ^( OBJECT disjunction ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA69_0 = input.LA(1);

                         
                        int index69_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA69_0==VAR1) && (synpred9_SparqlOwl())) {s = 1;}

                        else if ( (LA69_0==VAR2) && (synpred9_SparqlOwl())) {s = 2;}

                        else if ( (LA69_0==IRI_REF_TERM) ) {s = 3;}

                        else if ( (LA69_0==PNAME_NS||LA69_0==PNAME_LN) ) {s = 4;}

                        else if ( ((LA69_0>=STRING_LITERAL1 && LA69_0<=STRING_LITERAL_LONG2)) && (synpred9_SparqlOwl())) {s = 5;}

                        else if ( (LA69_0==INTEGER) && (synpred9_SparqlOwl())) {s = 6;}

                        else if ( (LA69_0==DECIMAL) && (synpred9_SparqlOwl())) {s = 7;}

                        else if ( (LA69_0==DOUBLE) && (synpred9_SparqlOwl())) {s = 8;}

                        else if ( (LA69_0==INTEGER_POSITIVE) && (synpred9_SparqlOwl())) {s = 9;}

                        else if ( (LA69_0==DECIMAL_POSITIVE) && (synpred9_SparqlOwl())) {s = 10;}

                        else if ( (LA69_0==DOUBLE_POSITIVE) && (synpred9_SparqlOwl())) {s = 11;}

                        else if ( (LA69_0==INTEGER_NEGATIVE) && (synpred9_SparqlOwl())) {s = 12;}

                        else if ( (LA69_0==DECIMAL_NEGATIVE) && (synpred9_SparqlOwl())) {s = 13;}

                        else if ( (LA69_0==DOUBLE_NEGATIVE) && (synpred9_SparqlOwl())) {s = 14;}

                        else if ( (LA69_0==TRUE_TERM) && (synpred9_SparqlOwl())) {s = 15;}

                        else if ( (LA69_0==FALSE_TERM) && (synpred9_SparqlOwl())) {s = 16;}

                        else if ( (LA69_0==BLANK_NODE_LABEL) && (synpred9_SparqlOwl())) {s = 17;}

                        else if ( (LA69_0==OPEN_SQUARE_BRACE) && (synpred9_SparqlOwl())) {s = 18;}

                        else if ( (LA69_0==OPEN_BRACE) ) {s = 19;}

                        else if ( (LA69_0==DECIMAL_TERM||LA69_0==FLOAT_TERM||LA69_0==INTEGER_TERM||LA69_0==STRING_TERM||LA69_0==INVERSE_TERM||(LA69_0>=NOT_TERM && LA69_0<=OPEN_CURLY_BRACE)) ) {s = 20;}

                         
                        input.seek(index69_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA69_3 = input.LA(1);

                         
                        int index69_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_SparqlOwl()) ) {s = 18;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index69_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA69_4 = input.LA(1);

                         
                        int index69_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_SparqlOwl()) ) {s = 18;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index69_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA69_19 = input.LA(1);

                         
                        int index69_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_SparqlOwl()) ) {s = 18;}

                        else if ( (true) ) {s = 20;}

                         
                        input.seek(index69_19);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 69, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_iriRef_in_objectPropertyIRI542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_dataPropertyIRI564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_objectOrDataPropertyIRI586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INVERSE_TERM_in_inverseObjectProperty608 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_objectPropertyIRI_in_inverseObjectProperty610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inverseObjectProperty_in_propertyExpression632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectOrDataPropertyIRI_in_propertyExpression637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inverseObjectProperty_in_objectPropertyExpression650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectPropertyIRI_in_objectPropertyExpression655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_datatype668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_TERM_in_datatype681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_TERM_in_datatype694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_TERM_in_datatype707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_TERM_in_datatype720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_individual741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rdfLiteral_in_literal765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteral_in_literal770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datatype_in_datatypeRestriction788 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_OPEN_SQUARE_BRACE_in_datatypeRestriction790 = new BitSet(new long[]{0x0000000000000000L,0xF800000000000000L,0x000000000000000FL});
    public static final BitSet FOLLOW_facet_in_datatypeRestriction792 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L,0x000000000003FFF0L});
    public static final BitSet FOLLOW_restrictionValue_in_datatypeRestriction794 = new BitSet(new long[]{0x0000000000000000L,0x0600000000000000L});
    public static final BitSet FOLLOW_COMMA_TERM_in_datatypeRestriction798 = new BitSet(new long[]{0x0000000000000000L,0xF800000000000000L,0x000000000000000FL});
    public static final BitSet FOLLOW_facet_in_datatypeRestriction800 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L,0x000000000003FFF0L});
    public static final BitSet FOLLOW_restrictionValue_in_datatypeRestriction802 = new BitSet(new long[]{0x0000000000000000L,0x0600000000000000L});
    public static final BitSet FOLLOW_CLOSE_SQUARE_BRACE_in_datatypeRestriction807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_TERM_in_facet839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINLENGTH_TERM_in_facet848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAXLENGTH_TERM_in_facet857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PATTERN_TERM_in_facet866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LANGPATTERN_TERM_in_facet875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUAL_TERM_in_facet884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_TERM_in_facet893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUAL_TERM_in_facet902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_TERM_in_facet911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_restrictionValue928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conjunction_in_disjunction943 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_OR_TERM_in_disjunction955 = new BitSet(new long[]{0x0804000800000000L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_conjunction_in_disjunction957 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_primary_in_conjunction986 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_AND_TERM_in_conjunction998 = new BitSet(new long[]{0x0804000800000000L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_primary_in_conjunction1000 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_NOT_TERM_in_primary1027 = new BitSet(new long[]{0x0804000800000000L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_restriction_in_primary1031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomic_in_primary1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_restriction_in_primary1054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomic_in_primary1060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_atomic1073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_TERM_in_atomic1086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_TERM_in_atomic1099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_TERM_in_atomic1112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_TERM_in_atomic1125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datatypeRestriction_in_atomic1138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_CURLY_BRACE_in_atomic1143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L,0x000000000003FFF0L});
    public static final BitSet FOLLOW_literal_in_atomic1145 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010100L,0x000000000003FFF0L});
    public static final BitSet FOLLOW_CLOSE_CURLY_BRACE_in_atomic1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_CURLY_BRACE_in_atomic1162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_individual_in_atomic1164 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000500100L,0x0000000000040000L});
    public static final BitSet FOLLOW_CLOSE_CURLY_BRACE_in_atomic1167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_atomic1181 = new BitSet(new long[]{0x0804000800000000L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_disjunction_in_atomic1184 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_atomic1186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_someRestriction_in_restriction1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_allRestriction_in_restriction1205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueRestriction_in_restriction1210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selfRestriction_in_restriction1215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numberRestriction_in_restriction1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyExpression_in_someRestriction1231 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_SOME_TERM_in_someRestriction1233 = new BitSet(new long[]{0x0804000800000000L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_primary_in_someRestriction1235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyExpression_in_allRestriction1257 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_ONLY_TERM_in_allRestriction1259 = new BitSet(new long[]{0x0804000800000000L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_primary_in_allRestriction1261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectPropertyExpression_in_valueRestriction1284 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_VALUE_TERM_in_valueRestriction1286 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_individual_in_valueRestriction1288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dataPropertyIRI_in_valueRestriction1303 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_VALUE_TERM_in_valueRestriction1305 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L,0x000000000003FFF0L});
    public static final BitSet FOLLOW_literal_in_valueRestriction1307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectPropertyExpression_in_selfRestriction1328 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_SELF_TERM_in_selfRestriction1330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_minNumberRestriction_in_numberRestriction1350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_maxNumberRestriction_in_numberRestriction1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exactNumberRestriction_in_numberRestriction1360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyExpression_in_minNumberRestriction1373 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_MIN_TERM_in_minNumberRestriction1375 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_minNumberRestriction1379 = new BitSet(new long[]{0x0804000800000002L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_primary_in_minNumberRestriction1390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyExpression_in_maxNumberRestriction1425 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_MAX_TERM_in_maxNumberRestriction1427 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_maxNumberRestriction1431 = new BitSet(new long[]{0x0804000800000002L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_primary_in_maxNumberRestriction1442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_propertyExpression_in_exactNumberRestriction1477 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_EXACTLY_TERM_in_exactNumberRestriction1479 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_exactNumberRestriction1483 = new BitSet(new long[]{0x0804000800000002L,0x0080010000000000L,0x00000000005002C0L,0x0000000000040000L});
    public static final BitSet FOLLOW_primary_in_exactNumberRestriction1494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prologue_in_query1533 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000038800000L});
    public static final BitSet FOLLOW_selectQuery_in_query1539 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_constructQuery_in_query1543 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_describeQuery_in_query1547 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_askQuery_in_query1551 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_query1555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_baseDecl_in_prologue1591 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_prefixDecl_in_prologue1594 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_BASE_TERM_in_baseDecl1608 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_IRI_REF_TERM_in_baseDecl1610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PREFIX_TERM_in_prefixDecl1632 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_PNAME_NS_in_prefixDecl1634 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_IRI_REF_TERM_in_prefixDecl1636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_TERM_in_selectQuery1660 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000C00007000000L});
    public static final BitSet FOLLOW_selectModifier_in_selectQuery1662 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000C00007000000L});
    public static final BitSet FOLLOW_selectVariableList_in_selectQuery1665 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_datasets_in_selectQuery1667 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_whereClause_in_selectQuery1670 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000006200000000L});
    public static final BitSet FOLLOW_solutionModifier_in_selectQuery1672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DISTINCT_TERM_in_selectModifier1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REDUCED_TERM_in_selectModifier1712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_selectVariableList1727 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000C00000000000L});
    public static final BitSet FOLLOW_ASTERISK_TERM_in_selectVariableList1742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONSTRUCT_TERM_in_constructQuery1759 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_constructTemplate_in_constructQuery1761 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_datasets_in_constructQuery1763 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_whereClause_in_constructQuery1766 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000006200000000L});
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery1768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESCRIBE_TERM_in_describeQuery1799 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000C00004500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_describeTargets_in_describeQuery1801 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000006340000080L});
    public static final BitSet FOLLOW_datasets_in_describeQuery1803 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000006340000080L});
    public static final BitSet FOLLOW_whereClause_in_describeQuery1806 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000006200000000L});
    public static final BitSet FOLLOW_solutionModifier_in_describeQuery1809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrIRIref_in_describeTargets1840 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000C00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_ASTERISK_TERM_in_describeTargets1855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASK_TERM_in_askQuery1872 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_datasets_in_askQuery1874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_whereClause_in_askQuery1877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datasetClause_in_datasets1904 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000040000000L});
    public static final BitSet FOLLOW_FROM_TERM_in_datasetClause1928 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000080500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_defaultGraphClause_in_datasetClause1935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_namedGraphClause_in_datasetClause1941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sourceSelector_in_defaultGraphClause1958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_TERM_in_namedGraphClause1981 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_sourceSelector_in_namedGraphClause1983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_sourceSelector2006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_TERM_in_whereClause2019 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_groupGraphPattern_in_whereClause2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_orderClause_in_solutionModifier2044 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000006000000000L});
    public static final BitSet FOLLOW_limitOffsetClauses_in_solutionModifier2047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2061 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000006000000000L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses2069 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses2071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_TERM_in_orderClause2085 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_BY_TERM_in_orderClause2087 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFF00C01800500200L,0x0000000000040003L});
    public static final BitSet FOLLOW_orderCondition_in_orderClause2089 = new BitSet(new long[]{0x0000000200000002L,0x0000000000000000L,0xFF00C01800500200L,0x0000000000040003L});
    public static final BitSet FOLLOW_ASC_TERM_in_orderCondition2113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition2115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_TERM_in_orderCondition2128 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition2130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_orderCondition2143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_orderCondition2156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIMIT_TERM_in_limitClause2177 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_limitClause2179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OFFSET_TERM_in_offsetClause2201 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_INTEGER_in_offsetClause2203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_CURLY_BRACE_in_groupGraphPattern2225 = new BitSet(new long[]{0x0804000800000000L,0x0180010000000000L,0x0000CB01405103C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_groupGraphPatternNoBraces_in_groupGraphPattern2227 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSE_CURLY_BRACE_in_groupGraphPattern2230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphPatternNotTriples_in_groupGraphPatternNoBraces2258 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB81405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_DOT_TERM_in_groupGraphPatternNoBraces2260 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB01405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_groupGraphPatternNoBraces_in_groupGraphPatternNoBraces2264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_filter_in_groupGraphPatternNoBraces2270 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB81405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_DOT_TERM_in_groupGraphPatternNoBraces2272 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB01405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_groupGraphPatternNoBraces_in_groupGraphPatternNoBraces2276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesSameSubject_in_groupGraphPatternNoBraces2289 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_DOT_TERM_in_groupGraphPatternNoBraces2291 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB01405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_groupGraphPatternNoBraces_in_groupGraphPatternNoBraces2294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesSameSubject_in_groupGraphPatternNoBraces2300 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x00000B0140000080L});
    public static final BitSet FOLLOW_canFollowTriplesWithoutDot_in_groupGraphPatternNoBraces2302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphPatternNotTriples_in_canFollowTriplesWithoutDot2315 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB81405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_DOT_TERM_in_canFollowTriplesWithoutDot2317 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB01405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_groupGraphPatternNoBraces_in_canFollowTriplesWithoutDot2321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_filter_in_canFollowTriplesWithoutDot2327 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB81405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_DOT_TERM_in_canFollowTriplesWithoutDot2329 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000CB01405102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_groupGraphPatternNoBraces_in_canFollowTriplesWithoutDot2333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionalGraphPattern_in_graphPatternNotTriples2347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_groupOrUnionGraphPattern_in_graphPatternNotTriples2352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphGraphPattern_in_graphPatternNotTriples2357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONAL_TERM_in_optionalGraphPattern2370 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_groupGraphPattern_in_optionalGraphPattern2372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GRAPH_TERM_in_graphGraphPattern2394 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000C00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_varOrIRIref_in_graphGraphPattern2396 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_groupGraphPattern_in_graphGraphPattern2398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern2428 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_UNION_TERM_in_groupOrUnionGraphPattern2440 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000140000080L});
    public static final BitSet FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern2442 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000040000000000L});
    public static final BitSet FOLLOW_FILTER_TERM_in_filter2469 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFF00000000500200L,0x0000000000040003L});
    public static final BitSet FOLLOW_constraint_in_filter2471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_brackettedExpression_in_constraint2493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_constraint2498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_constraint2503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_functionCall2516 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_argList_in_functionCall2518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_argList2550 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_argList2553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_argList2559 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_argList2562 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_TERM_in_argList2566 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_argList2569 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_argList2574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_CURLY_BRACE_in_constructTemplate2588 = new BitSet(new long[]{0x0804000800000000L,0x0180010000000000L,0x0000C000005103C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_constructTriples_in_constructTemplate2590 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_CLOSE_CURLY_BRACE_in_constructTemplate2593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesSameSubject_in_constructTriples2617 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_DOT_TERM_in_constructTriples2621 = new BitSet(new long[]{0x0804000800000002L,0x0180010000000000L,0x0000C000005102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_constructTriples_in_constructTriples2624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrTerm_in_triplesSameSubject2657 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000E00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_triplesSameSubject2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_triplesSameSubject2685 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000E00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_triplesSameSubject2687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_triplesSameSubject2711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_disjunction_in_triplesSameSubject2728 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000E00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_triplesSameSubject2730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_verbObjectListPair_in_propertyListNotEmpty2757 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_SEMICOLON_TERM_in_propertyListNotEmpty2761 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000F00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_verbObjectListPair_in_propertyListNotEmpty2765 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000100000000000L});
    public static final BitSet FOLLOW_verb_in_verbObjectListPair2791 = new BitSet(new long[]{0x0804000800000000L,0x0180010000000000L,0x0000C000005102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_objectList_in_verbObjectListPair2793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_object_in_objectList2818 = new BitSet(new long[]{0x0000000000000002L,0x0200000000000000L});
    public static final BitSet FOLLOW_COMMA_TERM_in_objectList2822 = new BitSet(new long[]{0x0804000800000000L,0x0180010000000000L,0x0000C000005102C0L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_object_in_objectList2824 = new BitSet(new long[]{0x0000000000000002L,0x0200000000000000L});
    public static final BitSet FOLLOW_graphNode_in_object2873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_disjunction_in_object2886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrIRIref_in_verb2907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_A_TERM_in_verb2920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collection_in_triplesNode2941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blankNodePropertyList_in_triplesNode2946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_SQUARE_BRACE_in_blankNodePropertyList2959 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000E00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_blankNodePropertyList2961 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_CLOSE_SQUARE_BRACE_in_blankNodePropertyList2963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_collection2985 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000C00000510200L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_graphNode_in_collection2987 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L,0x0000C00000510600L,0x00000000000FFFF0L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_collection2990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_emptyCollection3013 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_emptyCollection3015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrTerm_in_graphNode3035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_graphNode3040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrTerm3053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphTerm_in_varOrTerm3058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_varOrIRIref3071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_varOrIRIref3076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR1_in_var3089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR2_in_var3102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_graphTerm3123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_graphTerm3128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_blankNode_in_graphTerm3133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollection_in_graphTerm3138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_expression3151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression3166 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_OR_OPERATOR_TERM_in_conditionalOrExpression3178 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression3180 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0001000000000000L});
    public static final BitSet FOLLOW_valueLogical_in_conditionalAndExpression3209 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_AND_OPERATOR_TERM_in_conditionalAndExpression3220 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_valueLogical_in_conditionalAndExpression3222 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_relationalExpression_in_valueLogical3249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3264 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x000C00000000000FL});
    public static final BitSet FOLLOW_EQUAL_TERM_in_relationalExpression3276 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TERM_in_relationalExpression3295 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_TERM_in_relationalExpression3314 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_TERM_in_relationalExpression3333 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUAL_TERM_in_relationalExpression3352 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUAL_TERM_in_relationalExpression3371 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression3373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_numericExpression3402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression3417 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0030000000010000L,0x0000000000000FF0L});
    public static final BitSet FOLLOW_PLUS_TERM_in_additiveExpression3429 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression3431 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0030000000010000L,0x0000000000000FF0L});
    public static final BitSet FOLLOW_MINUS_TERM_in_additiveExpression3448 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression3450 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0030000000010000L,0x0000000000000FF0L});
    public static final BitSet FOLLOW_numericLiteralPositive_in_additiveExpression3467 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0030000000010000L,0x0000000000000FF0L});
    public static final BitSet FOLLOW_numericLiteralNegative_in_additiveExpression3484 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0030000000010000L,0x0000000000000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression3515 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0040000004000000L});
    public static final BitSet FOLLOW_ASTERISK_TERM_in_multiplicativeExpression3527 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression3529 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0040000004000000L});
    public static final BitSet FOLLOW_DIVIDE_TERM_in_multiplicativeExpression3547 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression3549 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0040000004000000L});
    public static final BitSet FOLLOW_UNARY_NOT_TERM_in_unaryExpression3579 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_TERM_in_unaryExpression3594 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_TERM_in_unaryExpression3609 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression3624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_brackettedExpression_in_primaryExpression3637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_builtInCall_in_primaryExpression3642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRefOrFunction_in_primaryExpression3647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primaryExpression3652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_in_primaryExpression3657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_brackettedExpression3670 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_brackettedExpression3673 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_brackettedExpression3675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STR_TERM_in_builtInCall3689 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3691 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3693 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LANG_TERM_in_builtInCall3708 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3710 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3712 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LANGMATCHES_TERM_in_builtInCall3727 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3729 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3731 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_COMMA_TERM_in_builtInCall3733 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3735 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATATYPE_TERM_in_builtInCall3751 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3753 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3755 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOUND_TERM_in_builtInCall3770 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3772 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000C00000000000L});
    public static final BitSet FOLLOW_var_in_builtInCall3774 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SAMETERM_TERM_in_builtInCall3789 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3791 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3793 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_COMMA_TERM_in_builtInCall3795 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3797 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ISIRI_TERM_in_builtInCall3813 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3815 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3817 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ISURI_TERM_in_builtInCall3832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3834 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3836 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ISBLANK_TERM_in_builtInCall3851 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3853 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3855 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ISLITERAL_TERM_in_builtInCall3870 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall3872 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_builtInCall3874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall3876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_regexExpression_in_builtInCall3889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REGEX_TERM_in_regexExpression3904 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_OPEN_BRACE_in_regexExpression3906 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_regexExpression3910 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_COMMA_TERM_in_regexExpression3912 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_regexExpression3916 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_COMMA_TERM_in_regexExpression3936 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000000L,0xFFB0C00000510200L,0x000000000007FFF3L});
    public static final BitSet FOLLOW_expression_in_regexExpression3940 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_CLOSE_BRACE_in_regexExpression3962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iriRef_in_iriRefOrFunction3977 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_argList_in_iriRefOrFunction3989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_in_rdfLiteral4025 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x000000000000000CL});
    public static final BitSet FOLLOW_LANGTAG_in_rdfLiteral4041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_CARAT_TERM_in_rdfLiteral4057 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_iriRef_in_rdfLiteral4059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralUnsigned_in_numericLiteral4087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralPositive_in_numericLiteral4092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numericLiteralNegative_in_numericLiteral4097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_numericLiteralUnsigned4110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_in_numericLiteralUnsigned4123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_numericLiteralUnsigned4136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_POSITIVE_in_numericLiteralPositive4157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_POSITIVE_in_numericLiteralPositive4170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_POSITIVE_in_numericLiteralPositive4183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_NEGATIVE_in_numericLiteralNegative4204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_NEGATIVE_in_numericLiteralNegative4217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_NEGATIVE_in_numericLiteralNegative4230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_TERM_in_booleanLiteral4251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_TERM_in_booleanLiteral4262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_string0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IRI_REF_TERM_in_iriRef4309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prefixedName_in_iriRef4322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_prefixedName0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLANK_NODE_LABEL_in_blankNode4361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_SQUARE_BRACE_in_blankNode4374 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_CLOSE_SQUARE_BRACE_in_blankNode4376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_synpred1_SparqlOwl1386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_synpred2_SparqlOwl1438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_synpred3_SparqlOwl1490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphPatternNotTriples_in_synpred4_SparqlOwl2253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesSameSubject_in_synpred5_SparqlOwl2283 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_DOT_TERM_in_synpred5_SparqlOwl2285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_varOrTerm_in_synpred6_SparqlOwl2651 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000E00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_synpred6_SparqlOwl2653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_synpred7_SparqlOwl2679 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000E00000500000L,0x0000000000040000L});
    public static final BitSet FOLLOW_propertyListNotEmpty_in_synpred7_SparqlOwl2681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_triplesNode_in_synpred8_SparqlOwl2707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_graphNode_in_synpred9_SparqlOwl2847 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L,0x0000108000000180L});
    public static final BitSet FOLLOW_set_in_synpred9_SparqlOwl2849 = new BitSet(new long[]{0x0000000000000002L});

}