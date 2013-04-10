// $ANTLR 3.2 Sep 23, 2009 12:02:23 /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g 2010-03-28 22:46:21

package com.clarkparsia.sparqlowl.parser.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class SparqlOwlLexer extends Lexer {
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
    public static final int BUILTIN_SAME_TERM=20;
    public static final int LITERAL_DOUBLE=67;
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

    	public void emitErrorMessage(String msg) {
    		/*
    		 * Swallow the error message rather than print on stderr.
    		 * This could log the message, but errors are probably more appropriately handled from the exceptions they generate.
    		 */
    		;
    	}


    // delegates
    // delegators

    public SparqlOwlLexer() {;} 
    public SparqlOwlLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public SparqlOwlLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g"; }

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1029:2: ( ( ' ' | '\\t' | EOL )+ )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1029:4: ( ' ' | '\\t' | EOL )+
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1029:4: ( ' ' | '\\t' | EOL )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\t' && LA1_0<='\n')||LA1_0=='\r'||LA1_0==' ') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

             _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "PNAME_NS"
    public final void mPNAME_NS() throws RecognitionException {
        try {
            int _type = PNAME_NS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1036:2: ( ( PN_PREFIX )? ':' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1036:4: ( PN_PREFIX )? ':'
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1036:4: ( PN_PREFIX )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>='A' && LA2_0<='Z')||(LA2_0>='a' && LA2_0<='z')||(LA2_0>='\u00C0' && LA2_0<='\u00D6')||(LA2_0>='\u00D8' && LA2_0<='\u00F6')||(LA2_0>='\u00F8' && LA2_0<='\u02FF')||(LA2_0>='\u0370' && LA2_0<='\u037D')||(LA2_0>='\u037F' && LA2_0<='\u1FFF')||(LA2_0>='\u200C' && LA2_0<='\u200D')||(LA2_0>='\u2070' && LA2_0<='\u218F')||(LA2_0>='\u2C00' && LA2_0<='\u2FEF')||(LA2_0>='\u3001' && LA2_0<='\uD7FF')||(LA2_0>='\uF900' && LA2_0<='\uFDCF')||(LA2_0>='\uFDF0' && LA2_0<='\uFFFD')) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1036:4: PN_PREFIX
                    {
                    mPN_PREFIX(); 

                    }
                    break;

            }

            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PNAME_NS"

    // $ANTLR start "PNAME_LN"
    public final void mPNAME_LN() throws RecognitionException {
        try {
            int _type = PNAME_LN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1043:2: ( PNAME_NS PN_LOCAL )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1043:4: PNAME_NS PN_LOCAL
            {
            mPNAME_NS(); 
            mPN_LOCAL(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PNAME_LN"

    // $ANTLR start "INTEGER_TERM"
    public final void mINTEGER_TERM() throws RecognitionException {
        try {
            int _type = INTEGER_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1050:2: ( ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'G' | 'g' ) ( 'E' | 'e' ) ( 'R' | 'r' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1050:4: ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'G' | 'g' ) ( 'E' | 'e' ) ( 'R' | 'r' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER_TERM"

    // $ANTLR start "DECIMAL_TERM"
    public final void mDECIMAL_TERM() throws RecognitionException {
        try {
            int _type = DECIMAL_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1057:2: ( ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'C' | 'c' ) ( 'I' | 'i' ) ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'L' | 'l' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1057:4: ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'C' | 'c' ) ( 'I' | 'i' ) ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'L' | 'l' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECIMAL_TERM"

    // $ANTLR start "FLOAT_TERM"
    public final void mFLOAT_TERM() throws RecognitionException {
        try {
            int _type = FLOAT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1064:2: ( ( 'F' | 'f' ) ( 'L' | 'l' ) ( 'O' | 'o' ) ( 'A' | 'a' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1064:4: ( 'F' | 'f' ) ( 'L' | 'l' ) ( 'O' | 'o' ) ( 'A' | 'a' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT_TERM"

    // $ANTLR start "STRING_TERM"
    public final void mSTRING_TERM() throws RecognitionException {
        try {
            int _type = STRING_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1071:2: ( ( 'S' | 's' ) ( 'T' | 't' ) ( 'R' | 'r' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'G' | 'g' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1071:4: ( 'S' | 's' ) ( 'T' | 't' ) ( 'R' | 'r' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'G' | 'g' )
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_TERM"

    // $ANTLR start "LENGTH_TERM"
    public final void mLENGTH_TERM() throws RecognitionException {
        try {
            int _type = LENGTH_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1078:2: ( ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'T' | 't' ) ( 'H' | 'h' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1078:4: ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'T' | 't' ) ( 'H' | 'h' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LENGTH_TERM"

    // $ANTLR start "MINLENGTH_TERM"
    public final void mMINLENGTH_TERM() throws RecognitionException {
        try {
            int _type = MINLENGTH_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1085:2: ( ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'T' | 't' ) ( 'H' | 'h' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1085:4: ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'T' | 't' ) ( 'H' | 'h' )
            {
            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINLENGTH_TERM"

    // $ANTLR start "MAXLENGTH_TERM"
    public final void mMAXLENGTH_TERM() throws RecognitionException {
        try {
            int _type = MAXLENGTH_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1092:2: ( ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'X' | 'x' ) ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'T' | 't' ) ( 'H' | 'h' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1092:4: ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'X' | 'x' ) ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'T' | 't' ) ( 'H' | 'h' )
            {
            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MAXLENGTH_TERM"

    // $ANTLR start "PATTERN_TERM"
    public final void mPATTERN_TERM() throws RecognitionException {
        try {
            int _type = PATTERN_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1099:2: ( ( 'P' | 'p' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'N' | 'n' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1099:4: ( 'P' | 'p' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'N' | 'n' )
            {
            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PATTERN_TERM"

    // $ANTLR start "LANGPATTERN_TERM"
    public final void mLANGPATTERN_TERM() throws RecognitionException {
        try {
            int _type = LANGPATTERN_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1106:2: ( ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'P' | 'p' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'N' | 'n' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1106:4: ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'P' | 'p' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'N' | 'n' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LANGPATTERN_TERM"

    // $ANTLR start "INVERSE_TERM"
    public final void mINVERSE_TERM() throws RecognitionException {
        try {
            int _type = INVERSE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1113:2: ( ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'V' | 'v' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'S' | 's' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1113:4: ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'V' | 'v' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'S' | 's' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='V'||input.LA(1)=='v' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INVERSE_TERM"

    // $ANTLR start "OR_TERM"
    public final void mOR_TERM() throws RecognitionException {
        try {
            int _type = OR_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1120:2: ( ( 'O' | 'o' ) ( 'R' | 'r' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1120:4: ( 'O' | 'o' ) ( 'R' | 'r' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR_TERM"

    // $ANTLR start "AND_TERM"
    public final void mAND_TERM() throws RecognitionException {
        try {
            int _type = AND_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1127:2: ( ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1127:4: ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'D' | 'd' )
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND_TERM"

    // $ANTLR start "THAT_TERM"
    public final void mTHAT_TERM() throws RecognitionException {
        try {
            int _type = THAT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1134:2: ( ( 'T' | 't' ) ( 'H' | 'h' ) ( 'A' | 'a' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1134:4: ( 'T' | 't' ) ( 'H' | 'h' ) ( 'A' | 'a' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THAT_TERM"

    // $ANTLR start "NOT_TERM"
    public final void mNOT_TERM() throws RecognitionException {
        try {
            int _type = NOT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1141:2: ( ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1141:4: ( 'N' | 'n' ) ( 'O' | 'o' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT_TERM"

    // $ANTLR start "SOME_TERM"
    public final void mSOME_TERM() throws RecognitionException {
        try {
            int _type = SOME_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1148:2: ( ( 'S' | 's' ) ( 'O' | 'o' ) ( 'M' | 'm' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1148:4: ( 'S' | 's' ) ( 'O' | 'o' ) ( 'M' | 'm' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SOME_TERM"

    // $ANTLR start "ONLY_TERM"
    public final void mONLY_TERM() throws RecognitionException {
        try {
            int _type = ONLY_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1155:2: ( ( 'O' | 'o' ) ( 'N' | 'n' ) ( 'L' | 'l' ) ( 'Y' | 'y' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1155:4: ( 'O' | 'o' ) ( 'N' | 'n' ) ( 'L' | 'l' ) ( 'Y' | 'y' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='Y'||input.LA(1)=='y' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ONLY_TERM"

    // $ANTLR start "VALUE_TERM"
    public final void mVALUE_TERM() throws RecognitionException {
        try {
            int _type = VALUE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1162:2: ( ( 'V' | 'v' ) ( 'A' | 'a' ) ( 'L' | 'l' ) ( 'U' | 'u' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1162:4: ( 'V' | 'v' ) ( 'A' | 'a' ) ( 'L' | 'l' ) ( 'U' | 'u' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='V'||input.LA(1)=='v' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VALUE_TERM"

    // $ANTLR start "SELF_TERM"
    public final void mSELF_TERM() throws RecognitionException {
        try {
            int _type = SELF_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1169:2: ( ( 'S' | 's' ) ( 'E' | 'e' ) ( 'L' | 'l' ) ( 'F' | 'f' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1169:4: ( 'S' | 's' ) ( 'E' | 'e' ) ( 'L' | 'l' ) ( 'F' | 'f' )
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SELF_TERM"

    // $ANTLR start "MIN_TERM"
    public final void mMIN_TERM() throws RecognitionException {
        try {
            int _type = MIN_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1176:2: ( ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1176:4: ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'N' | 'n' )
            {
            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MIN_TERM"

    // $ANTLR start "MAX_TERM"
    public final void mMAX_TERM() throws RecognitionException {
        try {
            int _type = MAX_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1183:2: ( ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'X' | 'x' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1183:4: ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'X' | 'x' )
            {
            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MAX_TERM"

    // $ANTLR start "EXACTLY_TERM"
    public final void mEXACTLY_TERM() throws RecognitionException {
        try {
            int _type = EXACTLY_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1190:2: ( ( 'E' | 'e' ) ( 'X' | 'x' ) ( 'A' | 'a' ) ( 'C' | 'c' ) ( 'T' | 't' ) ( 'L' | 'l' ) ( 'Y' | 'y' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1190:4: ( 'E' | 'e' ) ( 'X' | 'x' ) ( 'A' | 'a' ) ( 'C' | 'c' ) ( 'T' | 't' ) ( 'L' | 'l' ) ( 'Y' | 'y' )
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='Y'||input.LA(1)=='y' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXACTLY_TERM"

    // $ANTLR start "BASE_TERM"
    public final void mBASE_TERM() throws RecognitionException {
        try {
            int _type = BASE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1197:2: ( ( 'B' | 'b' ) ( 'A' | 'a' ) ( 'S' | 's' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1197:4: ( 'B' | 'b' ) ( 'A' | 'a' ) ( 'S' | 's' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BASE_TERM"

    // $ANTLR start "PREFIX_TERM"
    public final void mPREFIX_TERM() throws RecognitionException {
        try {
            int _type = PREFIX_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1204:2: ( ( 'P' | 'p' ) ( 'R' | 'r' ) ( 'E' | 'e' ) ( 'F' | 'f' ) ( 'I' | 'i' ) ( 'X' | 'x' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1204:4: ( 'P' | 'p' ) ( 'R' | 'r' ) ( 'E' | 'e' ) ( 'F' | 'f' ) ( 'I' | 'i' ) ( 'X' | 'x' )
            {
            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PREFIX_TERM"

    // $ANTLR start "SELECT_TERM"
    public final void mSELECT_TERM() throws RecognitionException {
        try {
            int _type = SELECT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1211:2: ( ( 'S' | 's' ) ( 'E' | 'e' ) ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'C' | 'c' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1211:4: ( 'S' | 's' ) ( 'E' | 'e' ) ( 'L' | 'l' ) ( 'E' | 'e' ) ( 'C' | 'c' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SELECT_TERM"

    // $ANTLR start "DISTINCT_TERM"
    public final void mDISTINCT_TERM() throws RecognitionException {
        try {
            int _type = DISTINCT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1218:2: ( ( 'D' | 'd' ) ( 'I' | 'i' ) ( 'S' | 's' ) ( 'T' | 't' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'C' | 'c' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1218:4: ( 'D' | 'd' ) ( 'I' | 'i' ) ( 'S' | 's' ) ( 'T' | 't' ) ( 'I' | 'i' ) ( 'N' | 'n' ) ( 'C' | 'c' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DISTINCT_TERM"

    // $ANTLR start "REDUCED_TERM"
    public final void mREDUCED_TERM() throws RecognitionException {
        try {
            int _type = REDUCED_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1225:2: ( ( 'R' | 'r' ) ( 'E' | 'e' ) ( 'D' | 'd' ) ( 'U' | 'u' ) ( 'C' | 'c' ) ( 'E' | 'e' ) ( 'D' | 'd' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1225:4: ( 'R' | 'r' ) ( 'E' | 'e' ) ( 'D' | 'd' ) ( 'U' | 'u' ) ( 'C' | 'c' ) ( 'E' | 'e' ) ( 'D' | 'd' )
            {
            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REDUCED_TERM"

    // $ANTLR start "CONSTRUCT_TERM"
    public final void mCONSTRUCT_TERM() throws RecognitionException {
        try {
            int _type = CONSTRUCT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1232:2: ( ( 'C' | 'c' ) ( 'O' | 'o' ) ( 'N' | 'n' ) ( 'S' | 's' ) ( 'T' | 't' ) ( 'R' | 'r' ) ( 'U' | 'u' ) ( 'C' | 'c' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1232:4: ( 'C' | 'c' ) ( 'O' | 'o' ) ( 'N' | 'n' ) ( 'S' | 's' ) ( 'T' | 't' ) ( 'R' | 'r' ) ( 'U' | 'u' ) ( 'C' | 'c' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONSTRUCT_TERM"

    // $ANTLR start "DESCRIBE_TERM"
    public final void mDESCRIBE_TERM() throws RecognitionException {
        try {
            int _type = DESCRIBE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1239:2: ( ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'S' | 's' ) ( 'C' | 'c' ) ( 'R' | 'r' ) ( 'I' | 'i' ) ( 'B' | 'b' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1239:4: ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'S' | 's' ) ( 'C' | 'c' ) ( 'R' | 'r' ) ( 'I' | 'i' ) ( 'B' | 'b' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DESCRIBE_TERM"

    // $ANTLR start "ASK_TERM"
    public final void mASK_TERM() throws RecognitionException {
        try {
            int _type = ASK_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1246:2: ( ( 'A' | 'a' ) ( 'S' | 's' ) ( 'K' | 'k' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1246:4: ( 'A' | 'a' ) ( 'S' | 's' ) ( 'K' | 'k' )
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='K'||input.LA(1)=='k' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASK_TERM"

    // $ANTLR start "FROM_TERM"
    public final void mFROM_TERM() throws RecognitionException {
        try {
            int _type = FROM_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1253:2: ( ( 'F' | 'f' ) ( 'R' | 'r' ) ( 'O' | 'o' ) ( 'M' | 'm' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1253:4: ( 'F' | 'f' ) ( 'R' | 'r' ) ( 'O' | 'o' ) ( 'M' | 'm' )
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FROM_TERM"

    // $ANTLR start "NAMED_TERM"
    public final void mNAMED_TERM() throws RecognitionException {
        try {
            int _type = NAMED_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1260:2: ( ( 'N' | 'n' ) ( 'A' | 'a' ) ( 'M' | 'm' ) ( 'E' | 'e' ) ( 'D' | 'd' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1260:4: ( 'N' | 'n' ) ( 'A' | 'a' ) ( 'M' | 'm' ) ( 'E' | 'e' ) ( 'D' | 'd' )
            {
            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAMED_TERM"

    // $ANTLR start "WHERE_TERM"
    public final void mWHERE_TERM() throws RecognitionException {
        try {
            int _type = WHERE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1267:2: ( ( 'W' | 'w' ) ( 'H' | 'h' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1267:4: ( 'W' | 'w' ) ( 'H' | 'h' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='W'||input.LA(1)=='w' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHERE_TERM"

    // $ANTLR start "ORDER_TERM"
    public final void mORDER_TERM() throws RecognitionException {
        try {
            int _type = ORDER_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1274:2: ( ( 'O' | 'o' ) ( 'R' | 'r' ) ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'R' | 'r' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1274:4: ( 'O' | 'o' ) ( 'R' | 'r' ) ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'R' | 'r' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ORDER_TERM"

    // $ANTLR start "BY_TERM"
    public final void mBY_TERM() throws RecognitionException {
        try {
            int _type = BY_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1281:2: ( ( 'B' | 'b' ) ( 'Y' | 'y' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1281:4: ( 'B' | 'b' ) ( 'Y' | 'y' )
            {
            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='Y'||input.LA(1)=='y' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BY_TERM"

    // $ANTLR start "ASC_TERM"
    public final void mASC_TERM() throws RecognitionException {
        try {
            int _type = ASC_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1288:2: ( ( 'A' | 'a' ) ( 'S' | 's' ) ( 'C' | 'c' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1288:4: ( 'A' | 'a' ) ( 'S' | 's' ) ( 'C' | 'c' )
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASC_TERM"

    // $ANTLR start "DESC_TERM"
    public final void mDESC_TERM() throws RecognitionException {
        try {
            int _type = DESC_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1295:2: ( ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'S' | 's' ) ( 'C' | 'c' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1295:4: ( 'D' | 'd' ) ( 'E' | 'e' ) ( 'S' | 's' ) ( 'C' | 'c' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DESC_TERM"

    // $ANTLR start "LIMIT_TERM"
    public final void mLIMIT_TERM() throws RecognitionException {
        try {
            int _type = LIMIT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1302:2: ( ( 'L' | 'l' ) ( 'I' | 'i' ) ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1302:4: ( 'L' | 'l' ) ( 'I' | 'i' ) ( 'M' | 'm' ) ( 'I' | 'i' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LIMIT_TERM"

    // $ANTLR start "OFFSET_TERM"
    public final void mOFFSET_TERM() throws RecognitionException {
        try {
            int _type = OFFSET_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1309:2: ( ( 'O' | 'o' ) ( 'F' | 'f' ) ( 'F' | 'f' ) ( 'S' | 's' ) ( 'E' | 'e' ) ( 'T' | 't' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1309:4: ( 'O' | 'o' ) ( 'F' | 'f' ) ( 'F' | 'f' ) ( 'S' | 's' ) ( 'E' | 'e' ) ( 'T' | 't' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OFFSET_TERM"

    // $ANTLR start "OPTIONAL_TERM"
    public final void mOPTIONAL_TERM() throws RecognitionException {
        try {
            int _type = OPTIONAL_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1316:2: ( ( 'O' | 'o' ) ( 'P' | 'p' ) ( 'T' | 't' ) ( 'I' | 'i' ) ( 'O' | 'o' ) ( 'N' | 'n' ) ( 'A' | 'a' ) ( 'L' | 'l' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1316:4: ( 'O' | 'o' ) ( 'P' | 'p' ) ( 'T' | 't' ) ( 'I' | 'i' ) ( 'O' | 'o' ) ( 'N' | 'n' ) ( 'A' | 'a' ) ( 'L' | 'l' )
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPTIONAL_TERM"

    // $ANTLR start "GRAPH_TERM"
    public final void mGRAPH_TERM() throws RecognitionException {
        try {
            int _type = GRAPH_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1323:2: ( ( 'G' | 'g' ) ( 'R' | 'r' ) ( 'A' | 'a' ) ( 'P' | 'p' ) ( 'H' | 'h' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1323:4: ( 'G' | 'g' ) ( 'R' | 'r' ) ( 'A' | 'a' ) ( 'P' | 'p' ) ( 'H' | 'h' )
            {
            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GRAPH_TERM"

    // $ANTLR start "UNION_TERM"
    public final void mUNION_TERM() throws RecognitionException {
        try {
            int _type = UNION_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1330:2: ( ( 'U' | 'u' ) ( 'N' | 'n' ) ( 'I' | 'i' ) ( 'O' | 'o' ) ( 'N' | 'n' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1330:4: ( 'U' | 'u' ) ( 'N' | 'n' ) ( 'I' | 'i' ) ( 'O' | 'o' ) ( 'N' | 'n' )
            {
            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNION_TERM"

    // $ANTLR start "FILTER_TERM"
    public final void mFILTER_TERM() throws RecognitionException {
        try {
            int _type = FILTER_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1337:2: ( ( 'F' | 'f' ) ( 'I' | 'i' ) ( 'L' | 'l' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1337:4: ( 'F' | 'f' ) ( 'I' | 'i' ) ( 'L' | 'l' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' )
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FILTER_TERM"

    // $ANTLR start "A_TERM"
    public final void mA_TERM() throws RecognitionException {
        try {
            int _type = A_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1345:2: ( 'a' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1345:4: 'a'
            {
            match('a'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "A_TERM"

    // $ANTLR start "STR_TERM"
    public final void mSTR_TERM() throws RecognitionException {
        try {
            int _type = STR_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1352:2: ( ( 'S' | 's' ) ( 'T' | 't' ) ( 'R' | 'r' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1352:4: ( 'S' | 's' ) ( 'T' | 't' ) ( 'R' | 'r' )
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STR_TERM"

    // $ANTLR start "LANG_TERM"
    public final void mLANG_TERM() throws RecognitionException {
        try {
            int _type = LANG_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1359:2: ( ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'G' | 'g' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1359:4: ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'G' | 'g' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LANG_TERM"

    // $ANTLR start "LANGMATCHES_TERM"
    public final void mLANGMATCHES_TERM() throws RecognitionException {
        try {
            int _type = LANGMATCHES_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1366:2: ( ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'C' | 'c' ) ( 'H' | 'h' ) ( 'E' | 'e' ) ( 'S' | 's' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1366:4: ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'G' | 'g' ) ( 'M' | 'm' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'C' | 'c' ) ( 'H' | 'h' ) ( 'E' | 'e' ) ( 'S' | 's' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LANGMATCHES_TERM"

    // $ANTLR start "DATATYPE_TERM"
    public final void mDATATYPE_TERM() throws RecognitionException {
        try {
            int _type = DATATYPE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1373:2: ( ( 'D' | 'd' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'Y' | 'y' ) ( 'P' | 'p' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1373:4: ( 'D' | 'd' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'A' | 'a' ) ( 'T' | 't' ) ( 'Y' | 'y' ) ( 'P' | 'p' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='Y'||input.LA(1)=='y' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DATATYPE_TERM"

    // $ANTLR start "BOUND_TERM"
    public final void mBOUND_TERM() throws RecognitionException {
        try {
            int _type = BOUND_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1380:2: ( ( 'B' | 'b' ) ( 'O' | 'o' ) ( 'U' | 'u' ) ( 'N' | 'n' ) ( 'D' | 'd' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1380:4: ( 'B' | 'b' ) ( 'O' | 'o' ) ( 'U' | 'u' ) ( 'N' | 'n' ) ( 'D' | 'd' )
            {
            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOUND_TERM"

    // $ANTLR start "SAMETERM_TERM"
    public final void mSAMETERM_TERM() throws RecognitionException {
        try {
            int _type = SAMETERM_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1387:2: ( ( 'S' | 's' ) ( 'A' | 'a' ) ( 'M' | 'm' ) ( 'E' | 'e' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'M' | 'm' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1387:4: ( 'S' | 's' ) ( 'A' | 'a' ) ( 'M' | 'm' ) ( 'E' | 'e' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'M' | 'm' )
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SAMETERM_TERM"

    // $ANTLR start "ISIRI_TERM"
    public final void mISIRI_TERM() throws RecognitionException {
        try {
            int _type = ISIRI_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1394:2: ( ( 'I' | 'i' ) ( 'S' | 's' ) ( 'I' | 'i' ) ( 'R' | 'r' ) ( 'I' | 'i' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1394:4: ( 'I' | 'i' ) ( 'S' | 's' ) ( 'I' | 'i' ) ( 'R' | 'r' ) ( 'I' | 'i' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ISIRI_TERM"

    // $ANTLR start "ISURI_TERM"
    public final void mISURI_TERM() throws RecognitionException {
        try {
            int _type = ISURI_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1401:2: ( ( 'I' | 'i' ) ( 'S' | 's' ) ( 'U' | 'u' ) ( 'R' | 'r' ) ( 'I' | 'i' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1401:4: ( 'I' | 'i' ) ( 'S' | 's' ) ( 'U' | 'u' ) ( 'R' | 'r' ) ( 'I' | 'i' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ISURI_TERM"

    // $ANTLR start "ISBLANK_TERM"
    public final void mISBLANK_TERM() throws RecognitionException {
        try {
            int _type = ISBLANK_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1408:2: ( ( 'I' | 'i' ) ( 'S' | 's' ) ( 'B' | 'b' ) ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'K' | 'k' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1408:4: ( 'I' | 'i' ) ( 'S' | 's' ) ( 'B' | 'b' ) ( 'L' | 'l' ) ( 'A' | 'a' ) ( 'N' | 'n' ) ( 'K' | 'k' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='K'||input.LA(1)=='k' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ISBLANK_TERM"

    // $ANTLR start "ISLITERAL_TERM"
    public final void mISLITERAL_TERM() throws RecognitionException {
        try {
            int _type = ISLITERAL_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1415:2: ( ( 'I' | 'i' ) ( 'S' | 's' ) ( 'L' | 'l' ) ( 'I' | 'i' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'A' | 'a' ) ( 'L' | 'l' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1415:4: ( 'I' | 'i' ) ( 'S' | 's' ) ( 'L' | 'l' ) ( 'I' | 'i' ) ( 'T' | 't' ) ( 'E' | 'e' ) ( 'R' | 'r' ) ( 'A' | 'a' ) ( 'L' | 'l' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ISLITERAL_TERM"

    // $ANTLR start "REGEX_TERM"
    public final void mREGEX_TERM() throws RecognitionException {
        try {
            int _type = REGEX_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1422:2: ( ( 'R' | 'r' ) ( 'E' | 'e' ) ( 'G' | 'g' ) ( 'E' | 'e' ) ( 'X' | 'x' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1422:4: ( 'R' | 'r' ) ( 'E' | 'e' ) ( 'G' | 'g' ) ( 'E' | 'e' ) ( 'X' | 'x' )
            {
            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REGEX_TERM"

    // $ANTLR start "TRUE_TERM"
    public final void mTRUE_TERM() throws RecognitionException {
        try {
            int _type = TRUE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1429:2: ( ( 'T' | 't' ) ( 'R' | 'r' ) ( 'U' | 'u' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1429:4: ( 'T' | 't' ) ( 'R' | 'r' ) ( 'U' | 'u' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE_TERM"

    // $ANTLR start "FALSE_TERM"
    public final void mFALSE_TERM() throws RecognitionException {
        try {
            int _type = FALSE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1436:2: ( ( 'F' | 'f' ) ( 'A' | 'a' ) ( 'L' | 'l' ) ( 'S' | 's' ) ( 'E' | 'e' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1436:4: ( 'F' | 'f' ) ( 'A' | 'a' ) ( 'L' | 'l' ) ( 'S' | 's' ) ( 'E' | 'e' )
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE_TERM"

    // $ANTLR start "IRI_REF_TERM"
    public final void mIRI_REF_TERM() throws RecognitionException {
        try {
            int _type = IRI_REF_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1443:2: ( LESS_TERM ( options {greedy=false; } : ~ ( LESS_TERM | GREATER_TERM | '\"' | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | '|' | '^' | '\\\\' | '`' | ( '\\u0000' .. '\\u0020' ) ) )* GREATER_TERM )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1443:4: LESS_TERM ( options {greedy=false; } : ~ ( LESS_TERM | GREATER_TERM | '\"' | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | '|' | '^' | '\\\\' | '`' | ( '\\u0000' .. '\\u0020' ) ) )* GREATER_TERM
            {
            mLESS_TERM(); 
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1443:14: ( options {greedy=false; } : ~ ( LESS_TERM | GREATER_TERM | '\"' | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | '|' | '^' | '\\\\' | '`' | ( '\\u0000' .. '\\u0020' ) ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='!'||(LA3_0>='#' && LA3_0<=';')||LA3_0=='='||(LA3_0>='?' && LA3_0<='[')||LA3_0==']'||LA3_0=='_'||(LA3_0>='a' && LA3_0<='z')||(LA3_0>='~' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }
                else if ( (LA3_0=='>') ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1443:42: ~ ( LESS_TERM | GREATER_TERM | '\"' | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | '|' | '^' | '\\\\' | '`' | ( '\\u0000' .. '\\u0020' ) )
            	    {
            	    if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<=';')||input.LA(1)=='='||(input.LA(1)>='?' && input.LA(1)<='[')||input.LA(1)==']'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='~' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            mGREATER_TERM(); 
             setText(getText().substring(1, getText().length() - 1)); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IRI_REF_TERM"

    // $ANTLR start "BLANK_NODE_LABEL"
    public final void mBLANK_NODE_LABEL() throws RecognitionException {
        try {
            int _type = BLANK_NODE_LABEL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken label=null;

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1451:2: ( '_:' label= PN_LOCAL )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1451:4: '_:' label= PN_LOCAL
            {
            match("_:"); 

            int labelStart2460 = getCharIndex();
            mPN_LOCAL(); 
            label = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, labelStart2460, getCharIndex()-1);
             setText((label!=null?label.getText():null)); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BLANK_NODE_LABEL"

    // $ANTLR start "VAR1"
    public final void mVAR1() throws RecognitionException {
        try {
            int _type = VAR1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken name=null;

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1458:2: ( '?' name= VARNAME )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1458:4: '?' name= VARNAME
            {
            match('?'); 
            int nameStart2480 = getCharIndex();
            mVARNAME(); 
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart2480, getCharIndex()-1);
             setText((name!=null?name.getText():null)); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VAR1"

    // $ANTLR start "VAR2"
    public final void mVAR2() throws RecognitionException {
        try {
            int _type = VAR2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken name=null;

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1465:2: ( '$' name= VARNAME )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1465:4: '$' name= VARNAME
            {
            match('$'); 
            int nameStart2500 = getCharIndex();
            mVARNAME(); 
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart2500, getCharIndex()-1);
             setText((name!=null?name.getText():null)); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VAR2"

    // $ANTLR start "LANGTAG"
    public final void mLANGTAG() throws RecognitionException {
        try {
            int _type = LANGTAG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:2: ( '@' ( ALPHA )+ ( MINUS_TERM ( ALPHANUM )+ )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:4: '@' ( ALPHA )+ ( MINUS_TERM ( ALPHANUM )+ )*
            {
            match('@'); 
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:8: ( ALPHA )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='A' && LA4_0<='Z')||(LA4_0>='a' && LA4_0<='z')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:8: ALPHA
            	    {
            	    mALPHA(); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:15: ( MINUS_TERM ( ALPHANUM )+ )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0=='-') ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:16: MINUS_TERM ( ALPHANUM )+
            	    {
            	    mMINUS_TERM(); 
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:27: ( ALPHANUM )+
            	    int cnt5=0;
            	    loop5:
            	    do {
            	        int alt5=2;
            	        int LA5_0 = input.LA(1);

            	        if ( ((LA5_0>='0' && LA5_0<='9')||(LA5_0>='A' && LA5_0<='Z')||(LA5_0>='a' && LA5_0<='z')) ) {
            	            alt5=1;
            	        }


            	        switch (alt5) {
            	    	case 1 :
            	    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1472:27: ALPHANUM
            	    	    {
            	    	    mALPHANUM(); 

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


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

             setText(getText().substring(1, getText().length())); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LANGTAG"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1480:2: ( ( DIGIT )+ )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1480:4: ( DIGIT )+
            {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1480:4: ( DIGIT )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1480:4: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "DECIMAL"
    public final void mDECIMAL() throws RecognitionException {
        try {
            int _type = DECIMAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1487:2: ( ( DIGIT )+ DOT_TERM ( DIGIT )* | DOT_TERM ( DIGIT )+ )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                alt11=1;
            }
            else if ( (LA11_0=='.') ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1487:4: ( DIGIT )+ DOT_TERM ( DIGIT )*
                    {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1487:4: ( DIGIT )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1487:4: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    mDOT_TERM(); 
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1487:20: ( DIGIT )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1487:20: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1488:4: DOT_TERM ( DIGIT )+
                    {
                    mDOT_TERM(); 
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1488:13: ( DIGIT )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>='0' && LA10_0<='9')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1488:13: DIGIT
                    	    {
                    	    mDIGIT(); 

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


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECIMAL"

    // $ANTLR start "DOUBLE"
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1495:2: ( ( DIGIT )+ DOT_TERM ( DIGIT )* EXPONENT | DOT_TERM ( DIGIT )+ EXPONENT | ( DIGIT )+ EXPONENT )
            int alt16=3;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1495:4: ( DIGIT )+ DOT_TERM ( DIGIT )* EXPONENT
                    {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1495:4: ( DIGIT )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1495:4: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);

                    mDOT_TERM(); 
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1495:20: ( DIGIT )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>='0' && LA13_0<='9')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1495:20: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;
                case 2 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1496:4: DOT_TERM ( DIGIT )+ EXPONENT
                    {
                    mDOT_TERM(); 
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1496:13: ( DIGIT )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>='0' && LA14_0<='9')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1496:13: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;
                case 3 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1497:4: ( DIGIT )+ EXPONENT
                    {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1497:4: ( DIGIT )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1497:4: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt15 >= 1 ) break loop15;
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE"

    // $ANTLR start "INTEGER_POSITIVE"
    public final void mINTEGER_POSITIVE() throws RecognitionException {
        try {
            int _type = INTEGER_POSITIVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1504:2: ( PLUS_TERM INTEGER )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1504:4: PLUS_TERM INTEGER
            {
            mPLUS_TERM(); 
            mINTEGER(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER_POSITIVE"

    // $ANTLR start "DECIMAL_POSITIVE"
    public final void mDECIMAL_POSITIVE() throws RecognitionException {
        try {
            int _type = DECIMAL_POSITIVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1511:2: ( PLUS_TERM DECIMAL )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1511:4: PLUS_TERM DECIMAL
            {
            mPLUS_TERM(); 
            mDECIMAL(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECIMAL_POSITIVE"

    // $ANTLR start "DOUBLE_POSITIVE"
    public final void mDOUBLE_POSITIVE() throws RecognitionException {
        try {
            int _type = DOUBLE_POSITIVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1518:2: ( PLUS_TERM DOUBLE )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1518:4: PLUS_TERM DOUBLE
            {
            mPLUS_TERM(); 
            mDOUBLE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_POSITIVE"

    // $ANTLR start "INTEGER_NEGATIVE"
    public final void mINTEGER_NEGATIVE() throws RecognitionException {
        try {
            int _type = INTEGER_NEGATIVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1525:2: ( MINUS_TERM INTEGER )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1525:4: MINUS_TERM INTEGER
            {
            mMINUS_TERM(); 
            mINTEGER(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER_NEGATIVE"

    // $ANTLR start "DECIMAL_NEGATIVE"
    public final void mDECIMAL_NEGATIVE() throws RecognitionException {
        try {
            int _type = DECIMAL_NEGATIVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1532:2: ( MINUS_TERM DECIMAL )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1532:4: MINUS_TERM DECIMAL
            {
            mMINUS_TERM(); 
            mDECIMAL(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECIMAL_NEGATIVE"

    // $ANTLR start "DOUBLE_NEGATIVE"
    public final void mDOUBLE_NEGATIVE() throws RecognitionException {
        try {
            int _type = DOUBLE_NEGATIVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1539:2: ( MINUS_TERM DOUBLE )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1539:4: MINUS_TERM DOUBLE
            {
            mMINUS_TERM(); 
            mDOUBLE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_NEGATIVE"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1547:2: ( ( 'e' | 'E' ) ( PLUS_TERM | MINUS_TERM )? ( DIGIT )+ )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1547:4: ( 'e' | 'E' ) ( PLUS_TERM | MINUS_TERM )? ( DIGIT )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1547:14: ( PLUS_TERM | MINUS_TERM )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0=='+'||LA17_0=='-') ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1547:38: ( DIGIT )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>='0' && LA18_0<='9')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1547:38: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "STRING_LITERAL1"
    public final void mSTRING_LITERAL1() throws RecognitionException {
        try {
            int _type = STRING_LITERAL1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1554:2: ( '\\'' ( options {greedy=false; } : ~ ( '\\u0027' | '\\u005C' | '\\u000A' | '\\u000D' ) | ECHAR )* '\\'' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1554:4: '\\'' ( options {greedy=false; } : ~ ( '\\u0027' | '\\u005C' | '\\u000A' | '\\u000D' ) | ECHAR )* '\\''
            {
            match('\''); 
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1554:9: ( options {greedy=false; } : ~ ( '\\u0027' | '\\u005C' | '\\u000A' | '\\u000D' ) | ECHAR )*
            loop19:
            do {
                int alt19=3;
                int LA19_0 = input.LA(1);

                if ( ((LA19_0>='\u0000' && LA19_0<='\t')||(LA19_0>='\u000B' && LA19_0<='\f')||(LA19_0>='\u000E' && LA19_0<='&')||(LA19_0>='(' && LA19_0<='[')||(LA19_0>=']' && LA19_0<='\uFFFF')) ) {
                    alt19=1;
                }
                else if ( (LA19_0=='\\') ) {
                    alt19=2;
                }
                else if ( (LA19_0=='\'') ) {
                    alt19=3;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1554:37: ~ ( '\\u0027' | '\\u005C' | '\\u000A' | '\\u000D' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;
            	case 2 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1554:84: ECHAR
            	    {
            	    mECHAR(); 

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL1"

    // $ANTLR start "STRING_LITERAL2"
    public final void mSTRING_LITERAL2() throws RecognitionException {
        try {
            int _type = STRING_LITERAL2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1561:2: ( '\"' ( options {greedy=false; } : ~ ( '\\u0022' | '\\u005C' | '\\u000A' | '\\u000D' ) | ECHAR )* '\"' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1561:4: '\"' ( options {greedy=false; } : ~ ( '\\u0022' | '\\u005C' | '\\u000A' | '\\u000D' ) | ECHAR )* '\"'
            {
            match('\"'); 
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1561:9: ( options {greedy=false; } : ~ ( '\\u0022' | '\\u005C' | '\\u000A' | '\\u000D' ) | ECHAR )*
            loop20:
            do {
                int alt20=3;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>='\u0000' && LA20_0<='\t')||(LA20_0>='\u000B' && LA20_0<='\f')||(LA20_0>='\u000E' && LA20_0<='!')||(LA20_0>='#' && LA20_0<='[')||(LA20_0>=']' && LA20_0<='\uFFFF')) ) {
                    alt20=1;
                }
                else if ( (LA20_0=='\\') ) {
                    alt20=2;
                }
                else if ( (LA20_0=='\"') ) {
                    alt20=3;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1561:37: ~ ( '\\u0022' | '\\u005C' | '\\u000A' | '\\u000D' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;
            	case 2 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1561:84: ECHAR
            	    {
            	    mECHAR(); 

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL2"

    // $ANTLR start "STRING_LITERAL_LONG1"
    public final void mSTRING_LITERAL_LONG1() throws RecognitionException {
        try {
            int _type = STRING_LITERAL_LONG1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:2: ( '\\'\\'\\'' ( options {greedy=false; } : ( '\\'' | '\\'\\'' )? (~ ( '\\'' | '\\\\' ) | ECHAR ) )* '\\'\\'\\'' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:6: '\\'\\'\\'' ( options {greedy=false; } : ( '\\'' | '\\'\\'' )? (~ ( '\\'' | '\\\\' ) | ECHAR ) )* '\\'\\'\\''
            {
            match("'''"); 

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:15: ( options {greedy=false; } : ( '\\'' | '\\'\\'' )? (~ ( '\\'' | '\\\\' ) | ECHAR ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0=='\'') ) {
                    int LA23_1 = input.LA(2);

                    if ( (LA23_1=='\'') ) {
                        int LA23_3 = input.LA(3);

                        if ( (LA23_3=='\'') ) {
                            alt23=2;
                        }
                        else if ( ((LA23_3>='\u0000' && LA23_3<='&')||(LA23_3>='(' && LA23_3<='\uFFFF')) ) {
                            alt23=1;
                        }


                    }
                    else if ( ((LA23_1>='\u0000' && LA23_1<='&')||(LA23_1>='(' && LA23_1<='\uFFFF')) ) {
                        alt23=1;
                    }


                }
                else if ( ((LA23_0>='\u0000' && LA23_0<='&')||(LA23_0>='(' && LA23_0<='\uFFFF')) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:43: ( '\\'' | '\\'\\'' )? (~ ( '\\'' | '\\\\' ) | ECHAR )
            	    {
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:43: ( '\\'' | '\\'\\'' )?
            	    int alt21=3;
            	    int LA21_0 = input.LA(1);

            	    if ( (LA21_0=='\'') ) {
            	        int LA21_1 = input.LA(2);

            	        if ( (LA21_1=='\'') ) {
            	            alt21=2;
            	        }
            	        else if ( ((LA21_1>='\u0000' && LA21_1<='&')||(LA21_1>='(' && LA21_1<='\uFFFF')) ) {
            	            alt21=1;
            	        }
            	    }
            	    switch (alt21) {
            	        case 1 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:45: '\\''
            	            {
            	            match('\''); 

            	            }
            	            break;
            	        case 2 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:52: '\\'\\''
            	            {
            	            match("''"); 


            	            }
            	            break;

            	    }

            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:62: (~ ( '\\'' | '\\\\' ) | ECHAR )
            	    int alt22=2;
            	    int LA22_0 = input.LA(1);

            	    if ( ((LA22_0>='\u0000' && LA22_0<='&')||(LA22_0>='(' && LA22_0<='[')||(LA22_0>=']' && LA22_0<='\uFFFF')) ) {
            	        alt22=1;
            	    }
            	    else if ( (LA22_0=='\\') ) {
            	        alt22=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 22, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt22) {
            	        case 1 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:64: ~ ( '\\'' | '\\\\' )
            	            {
            	            if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	                input.consume();

            	            }
            	            else {
            	                MismatchedSetException mse = new MismatchedSetException(null,input);
            	                recover(mse);
            	                throw mse;}


            	            }
            	            break;
            	        case 2 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1568:79: ECHAR
            	            {
            	            mECHAR(); 

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            match("'''"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL_LONG1"

    // $ANTLR start "STRING_LITERAL_LONG2"
    public final void mSTRING_LITERAL_LONG2() throws RecognitionException {
        try {
            int _type = STRING_LITERAL_LONG2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:2: ( '\"\"\"' ( options {greedy=false; } : ( '\"' | '\"\"' )? (~ ( '\"' | '\\\\' ) | ECHAR ) )* '\"\"\"' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:6: '\"\"\"' ( options {greedy=false; } : ( '\"' | '\"\"' )? (~ ( '\"' | '\\\\' ) | ECHAR ) )* '\"\"\"'
            {
            match("\"\"\""); 

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:12: ( options {greedy=false; } : ( '\"' | '\"\"' )? (~ ( '\"' | '\\\\' ) | ECHAR ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='\"') ) {
                    int LA26_1 = input.LA(2);

                    if ( (LA26_1=='\"') ) {
                        int LA26_3 = input.LA(3);

                        if ( (LA26_3=='\"') ) {
                            alt26=2;
                        }
                        else if ( ((LA26_3>='\u0000' && LA26_3<='!')||(LA26_3>='#' && LA26_3<='\uFFFF')) ) {
                            alt26=1;
                        }


                    }
                    else if ( ((LA26_1>='\u0000' && LA26_1<='!')||(LA26_1>='#' && LA26_1<='\uFFFF')) ) {
                        alt26=1;
                    }


                }
                else if ( ((LA26_0>='\u0000' && LA26_0<='!')||(LA26_0>='#' && LA26_0<='\uFFFF')) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:40: ( '\"' | '\"\"' )? (~ ( '\"' | '\\\\' ) | ECHAR )
            	    {
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:40: ( '\"' | '\"\"' )?
            	    int alt24=3;
            	    int LA24_0 = input.LA(1);

            	    if ( (LA24_0=='\"') ) {
            	        int LA24_1 = input.LA(2);

            	        if ( (LA24_1=='\"') ) {
            	            alt24=2;
            	        }
            	        else if ( ((LA24_1>='\u0000' && LA24_1<='!')||(LA24_1>='#' && LA24_1<='\uFFFF')) ) {
            	            alt24=1;
            	        }
            	    }
            	    switch (alt24) {
            	        case 1 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:42: '\"'
            	            {
            	            match('\"'); 

            	            }
            	            break;
            	        case 2 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:48: '\"\"'
            	            {
            	            match("\"\""); 


            	            }
            	            break;

            	    }

            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:56: (~ ( '\"' | '\\\\' ) | ECHAR )
            	    int alt25=2;
            	    int LA25_0 = input.LA(1);

            	    if ( ((LA25_0>='\u0000' && LA25_0<='!')||(LA25_0>='#' && LA25_0<='[')||(LA25_0>=']' && LA25_0<='\uFFFF')) ) {
            	        alt25=1;
            	    }
            	    else if ( (LA25_0=='\\') ) {
            	        alt25=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 25, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt25) {
            	        case 1 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:58: ~ ( '\"' | '\\\\' )
            	            {
            	            if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	                input.consume();

            	            }
            	            else {
            	                MismatchedSetException mse = new MismatchedSetException(null,input);
            	                recover(mse);
            	                throw mse;}


            	            }
            	            break;
            	        case 2 :
            	            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1575:72: ECHAR
            	            {
            	            mECHAR(); 

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            match("\"\"\""); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL_LONG2"

    // $ANTLR start "ECHAR"
    public final void mECHAR() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1583:2: ( '\\\\' ( 't' | 'b' | 'n' | 'r' | 'f' | '\\\\' | '\"' | '\\'' ) )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1583:4: '\\\\' ( 't' | 'b' | 'n' | 'r' | 'f' | '\\\\' | '\"' | '\\'' )
            {
            match('\\'); 
            if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "ECHAR"

    // $ANTLR start "PN_CHARS_BASE"
    public final void mPN_CHARS_BASE() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1591:2: ( ALPHA | '\\u00C0' .. '\\u00D6' | '\\u00D8' .. '\\u00F6' | '\\u00F8' .. '\\u02FF' | '\\u0370' .. '\\u037D' | '\\u037F' .. '\\u1FFF' | '\\u200C' .. '\\u200D' | '\\u2070' .. '\\u218F' | '\\u2C00' .. '\\u2FEF' | '\\u3001' .. '\\uD7FF' | '\\uF900' .. '\\uFDCF' | '\\uFDF0' .. '\\uFFFD' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u02FF')||(input.LA(1)>='\u0370' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "PN_CHARS_BASE"

    // $ANTLR start "PN_CHARS_U"
    public final void mPN_CHARS_U() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1610:2: ( PN_CHARS_BASE | '_' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u02FF')||(input.LA(1)>='\u0370' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "PN_CHARS_U"

    // $ANTLR start "VARNAME"
    public final void mVARNAME() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1619:2: ( ( PN_CHARS_U | DIGIT ) ( PN_CHARS_U | DIGIT | '\\u00B7' | '\\u0300' .. '\\u036F' | '\\u203F' .. '\\u2040' )* )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1619:4: ( PN_CHARS_U | DIGIT ) ( PN_CHARS_U | DIGIT | '\\u00B7' | '\\u0300' .. '\\u036F' | '\\u203F' .. '\\u2040' )*
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u02FF')||(input.LA(1)>='\u0370' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1622:3: ( PN_CHARS_U | DIGIT | '\\u00B7' | '\\u0300' .. '\\u036F' | '\\u203F' .. '\\u2040' )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0>='0' && LA27_0<='9')||(LA27_0>='A' && LA27_0<='Z')||LA27_0=='_'||(LA27_0>='a' && LA27_0<='z')||LA27_0=='\u00B7'||(LA27_0>='\u00C0' && LA27_0<='\u00D6')||(LA27_0>='\u00D8' && LA27_0<='\u00F6')||(LA27_0>='\u00F8' && LA27_0<='\u037D')||(LA27_0>='\u037F' && LA27_0<='\u1FFF')||(LA27_0>='\u200C' && LA27_0<='\u200D')||(LA27_0>='\u203F' && LA27_0<='\u2040')||(LA27_0>='\u2070' && LA27_0<='\u218F')||(LA27_0>='\u2C00' && LA27_0<='\u2FEF')||(LA27_0>='\u3001' && LA27_0<='\uD7FF')||(LA27_0>='\uF900' && LA27_0<='\uFDCF')||(LA27_0>='\uFDF0' && LA27_0<='\uFFFD')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='\u00B7'||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u203F' && input.LA(1)<='\u2040')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "VARNAME"

    // $ANTLR start "PN_CHARS"
    public final void mPN_CHARS() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1635:2: ( PN_CHARS_U | MINUS_TERM | DIGIT | '\\u00B7' | '\\u0300' .. '\\u036F' | '\\u203F' .. '\\u2040' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            if ( input.LA(1)=='-'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='\u00B7'||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u203F' && input.LA(1)<='\u2040')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "PN_CHARS"

    // $ANTLR start "PN_PREFIX"
    public final void mPN_PREFIX() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1648:2: ( PN_CHARS_BASE ( ( PN_CHARS | DOT_TERM )* PN_CHARS )? )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1648:4: PN_CHARS_BASE ( ( PN_CHARS | DOT_TERM )* PN_CHARS )?
            {
            mPN_CHARS_BASE(); 
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1648:18: ( ( PN_CHARS | DOT_TERM )* PN_CHARS )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( ((LA29_0>='-' && LA29_0<='.')||(LA29_0>='0' && LA29_0<='9')||(LA29_0>='A' && LA29_0<='Z')||LA29_0=='_'||(LA29_0>='a' && LA29_0<='z')||LA29_0=='\u00B7'||(LA29_0>='\u00C0' && LA29_0<='\u00D6')||(LA29_0>='\u00D8' && LA29_0<='\u00F6')||(LA29_0>='\u00F8' && LA29_0<='\u037D')||(LA29_0>='\u037F' && LA29_0<='\u1FFF')||(LA29_0>='\u200C' && LA29_0<='\u200D')||(LA29_0>='\u203F' && LA29_0<='\u2040')||(LA29_0>='\u2070' && LA29_0<='\u218F')||(LA29_0>='\u2C00' && LA29_0<='\u2FEF')||(LA29_0>='\u3001' && LA29_0<='\uD7FF')||(LA29_0>='\uF900' && LA29_0<='\uFDCF')||(LA29_0>='\uFDF0' && LA29_0<='\uFFFD')) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1649:3: ( PN_CHARS | DOT_TERM )* PN_CHARS
                    {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1649:3: ( PN_CHARS | DOT_TERM )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0=='-'||(LA28_0>='0' && LA28_0<='9')||(LA28_0>='A' && LA28_0<='Z')||LA28_0=='_'||(LA28_0>='a' && LA28_0<='z')||LA28_0=='\u00B7'||(LA28_0>='\u00C0' && LA28_0<='\u00D6')||(LA28_0>='\u00D8' && LA28_0<='\u00F6')||(LA28_0>='\u00F8' && LA28_0<='\u037D')||(LA28_0>='\u037F' && LA28_0<='\u1FFF')||(LA28_0>='\u200C' && LA28_0<='\u200D')||(LA28_0>='\u203F' && LA28_0<='\u2040')||(LA28_0>='\u2070' && LA28_0<='\u218F')||(LA28_0>='\u2C00' && LA28_0<='\u2FEF')||(LA28_0>='\u3001' && LA28_0<='\uD7FF')||(LA28_0>='\uF900' && LA28_0<='\uFDCF')||(LA28_0>='\uFDF0' && LA28_0<='\uFFFD')) ) {
                            int LA28_1 = input.LA(2);

                            if ( ((LA28_1>='-' && LA28_1<='.')||(LA28_1>='0' && LA28_1<='9')||(LA28_1>='A' && LA28_1<='Z')||LA28_1=='_'||(LA28_1>='a' && LA28_1<='z')||LA28_1=='\u00B7'||(LA28_1>='\u00C0' && LA28_1<='\u00D6')||(LA28_1>='\u00D8' && LA28_1<='\u00F6')||(LA28_1>='\u00F8' && LA28_1<='\u037D')||(LA28_1>='\u037F' && LA28_1<='\u1FFF')||(LA28_1>='\u200C' && LA28_1<='\u200D')||(LA28_1>='\u203F' && LA28_1<='\u2040')||(LA28_1>='\u2070' && LA28_1<='\u218F')||(LA28_1>='\u2C00' && LA28_1<='\u2FEF')||(LA28_1>='\u3001' && LA28_1<='\uD7FF')||(LA28_1>='\uF900' && LA28_1<='\uFDCF')||(LA28_1>='\uFDF0' && LA28_1<='\uFFFD')) ) {
                                alt28=1;
                            }


                        }
                        else if ( (LA28_0=='.') ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
                    	    {
                    	    if ( (input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='\u00B7'||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u203F' && input.LA(1)<='\u2040')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);

                    mPN_CHARS(); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "PN_PREFIX"

    // $ANTLR start "PN_LOCAL"
    public final void mPN_LOCAL() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1659:2: ( ( PN_CHARS_U | DIGIT ) ( ( PN_CHARS | DOT_TERM )* PN_CHARS )? )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1659:4: ( PN_CHARS_U | DIGIT ) ( ( PN_CHARS | DOT_TERM )* PN_CHARS )?
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u02FF')||(input.LA(1)>='\u0370' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1661:5: ( ( PN_CHARS | DOT_TERM )* PN_CHARS )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( ((LA31_0>='-' && LA31_0<='.')||(LA31_0>='0' && LA31_0<='9')||(LA31_0>='A' && LA31_0<='Z')||LA31_0=='_'||(LA31_0>='a' && LA31_0<='z')||LA31_0=='\u00B7'||(LA31_0>='\u00C0' && LA31_0<='\u00D6')||(LA31_0>='\u00D8' && LA31_0<='\u00F6')||(LA31_0>='\u00F8' && LA31_0<='\u037D')||(LA31_0>='\u037F' && LA31_0<='\u1FFF')||(LA31_0>='\u200C' && LA31_0<='\u200D')||(LA31_0>='\u203F' && LA31_0<='\u2040')||(LA31_0>='\u2070' && LA31_0<='\u218F')||(LA31_0>='\u2C00' && LA31_0<='\u2FEF')||(LA31_0>='\u3001' && LA31_0<='\uD7FF')||(LA31_0>='\uF900' && LA31_0<='\uFDCF')||(LA31_0>='\uFDF0' && LA31_0<='\uFFFD')) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1662:3: ( PN_CHARS | DOT_TERM )* PN_CHARS
                    {
                    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1662:3: ( PN_CHARS | DOT_TERM )*
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0=='-'||(LA30_0>='0' && LA30_0<='9')||(LA30_0>='A' && LA30_0<='Z')||LA30_0=='_'||(LA30_0>='a' && LA30_0<='z')||LA30_0=='\u00B7'||(LA30_0>='\u00C0' && LA30_0<='\u00D6')||(LA30_0>='\u00D8' && LA30_0<='\u00F6')||(LA30_0>='\u00F8' && LA30_0<='\u037D')||(LA30_0>='\u037F' && LA30_0<='\u1FFF')||(LA30_0>='\u200C' && LA30_0<='\u200D')||(LA30_0>='\u203F' && LA30_0<='\u2040')||(LA30_0>='\u2070' && LA30_0<='\u218F')||(LA30_0>='\u2C00' && LA30_0<='\u2FEF')||(LA30_0>='\u3001' && LA30_0<='\uD7FF')||(LA30_0>='\uF900' && LA30_0<='\uFDCF')||(LA30_0>='\uFDF0' && LA30_0<='\uFFFD')) ) {
                            int LA30_1 = input.LA(2);

                            if ( ((LA30_1>='-' && LA30_1<='.')||(LA30_1>='0' && LA30_1<='9')||(LA30_1>='A' && LA30_1<='Z')||LA30_1=='_'||(LA30_1>='a' && LA30_1<='z')||LA30_1=='\u00B7'||(LA30_1>='\u00C0' && LA30_1<='\u00D6')||(LA30_1>='\u00D8' && LA30_1<='\u00F6')||(LA30_1>='\u00F8' && LA30_1<='\u037D')||(LA30_1>='\u037F' && LA30_1<='\u1FFF')||(LA30_1>='\u200C' && LA30_1<='\u200D')||(LA30_1>='\u203F' && LA30_1<='\u2040')||(LA30_1>='\u2070' && LA30_1<='\u218F')||(LA30_1>='\u2C00' && LA30_1<='\u2FEF')||(LA30_1>='\u3001' && LA30_1<='\uD7FF')||(LA30_1>='\uF900' && LA30_1<='\uFDCF')||(LA30_1>='\uFDF0' && LA30_1<='\uFFFD')) ) {
                                alt30=1;
                            }


                        }
                        else if ( (LA30_0=='.') ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
                    	    {
                    	    if ( (input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||input.LA(1)=='\u00B7'||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u037D')||(input.LA(1)>='\u037F' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200D')||(input.LA(1)>='\u203F' && input.LA(1)<='\u2040')||(input.LA(1)>='\u2070' && input.LA(1)<='\u218F')||(input.LA(1)>='\u2C00' && input.LA(1)<='\u2FEF')||(input.LA(1)>='\u3001' && input.LA(1)<='\uD7FF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFDCF')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFFFD') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop30;
                        }
                    } while (true);

                    mPN_CHARS(); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "PN_LOCAL"

    // $ANTLR start "ALPHA"
    public final void mALPHA() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1672:2: ( 'A' .. 'Z' | 'a' .. 'z' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "ALPHA"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1681:2: ( '0' .. '9' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1681:4: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "ALPHANUM"
    public final void mALPHANUM() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1689:2: ( ALPHA | DIGIT )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "ALPHANUM"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1696:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1696:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); 
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1696:8: ( options {greedy=false; } : . )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0=='\n'||LA32_0=='\r') ) {
                    alt32=2;
                }
                else if ( ((LA32_0>='\u0000' && LA32_0<='\t')||(LA32_0>='\u000B' && LA32_0<='\f')||(LA32_0>='\u000E' && LA32_0<='\uFFFF')) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1696:35: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);

            mEOL(); 
             _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "EOL"
    public final void mEOL() throws RecognitionException {
        try {
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1704:2: ( '\\n' | '\\r' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:
            {
            if ( input.LA(1)=='\n'||input.LA(1)=='\r' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "EOL"

    // $ANTLR start "DOUBLE_CARAT_TERM"
    public final void mDOUBLE_CARAT_TERM() throws RecognitionException {
        try {
            int _type = DOUBLE_CARAT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1708:2: ( '^^' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1708:4: '^^'
            {
            match("^^"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_CARAT_TERM"

    // $ANTLR start "LESS_EQUAL_TERM"
    public final void mLESS_EQUAL_TERM() throws RecognitionException {
        try {
            int _type = LESS_EQUAL_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1712:2: ( '<=' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1712:4: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS_EQUAL_TERM"

    // $ANTLR start "GREATER_EQUAL_TERM"
    public final void mGREATER_EQUAL_TERM() throws RecognitionException {
        try {
            int _type = GREATER_EQUAL_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1716:2: ( '>=' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1716:4: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER_EQUAL_TERM"

    // $ANTLR start "NOT_EQUAL_TERM"
    public final void mNOT_EQUAL_TERM() throws RecognitionException {
        try {
            int _type = NOT_EQUAL_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1720:2: ( '!=' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1720:4: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT_EQUAL_TERM"

    // $ANTLR start "AND_OPERATOR_TERM"
    public final void mAND_OPERATOR_TERM() throws RecognitionException {
        try {
            int _type = AND_OPERATOR_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1724:2: ( '&&' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1724:4: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND_OPERATOR_TERM"

    // $ANTLR start "OR_OPERATOR_TERM"
    public final void mOR_OPERATOR_TERM() throws RecognitionException {
        try {
            int _type = OR_OPERATOR_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1728:2: ( '||' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1728:4: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR_OPERATOR_TERM"

    // $ANTLR start "OPEN_BRACE"
    public final void mOPEN_BRACE() throws RecognitionException {
        try {
            int _type = OPEN_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1732:2: ( '(' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1732:4: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPEN_BRACE"

    // $ANTLR start "CLOSE_BRACE"
    public final void mCLOSE_BRACE() throws RecognitionException {
        try {
            int _type = CLOSE_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1736:2: ( ')' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1736:4: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLOSE_BRACE"

    // $ANTLR start "OPEN_CURLY_BRACE"
    public final void mOPEN_CURLY_BRACE() throws RecognitionException {
        try {
            int _type = OPEN_CURLY_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1740:2: ( '{' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1740:4: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPEN_CURLY_BRACE"

    // $ANTLR start "CLOSE_CURLY_BRACE"
    public final void mCLOSE_CURLY_BRACE() throws RecognitionException {
        try {
            int _type = CLOSE_CURLY_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1744:2: ( '}' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1744:4: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLOSE_CURLY_BRACE"

    // $ANTLR start "OPEN_SQUARE_BRACE"
    public final void mOPEN_SQUARE_BRACE() throws RecognitionException {
        try {
            int _type = OPEN_SQUARE_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1748:2: ( '[' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1748:4: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPEN_SQUARE_BRACE"

    // $ANTLR start "CLOSE_SQUARE_BRACE"
    public final void mCLOSE_SQUARE_BRACE() throws RecognitionException {
        try {
            int _type = CLOSE_SQUARE_BRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1752:2: ( ']' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1752:4: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLOSE_SQUARE_BRACE"

    // $ANTLR start "SEMICOLON_TERM"
    public final void mSEMICOLON_TERM() throws RecognitionException {
        try {
            int _type = SEMICOLON_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1756:2: ( ';' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1756:4: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON_TERM"

    // $ANTLR start "DOT_TERM"
    public final void mDOT_TERM() throws RecognitionException {
        try {
            int _type = DOT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1760:2: ( '.' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1760:4: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT_TERM"

    // $ANTLR start "PLUS_TERM"
    public final void mPLUS_TERM() throws RecognitionException {
        try {
            int _type = PLUS_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1764:2: ( '+' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1764:4: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS_TERM"

    // $ANTLR start "MINUS_TERM"
    public final void mMINUS_TERM() throws RecognitionException {
        try {
            int _type = MINUS_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1768:2: ( '-' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1768:4: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS_TERM"

    // $ANTLR start "ASTERISK_TERM"
    public final void mASTERISK_TERM() throws RecognitionException {
        try {
            int _type = ASTERISK_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1772:2: ( '*' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1772:4: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASTERISK_TERM"

    // $ANTLR start "COMMA_TERM"
    public final void mCOMMA_TERM() throws RecognitionException {
        try {
            int _type = COMMA_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1776:2: ( ',' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1776:4: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA_TERM"

    // $ANTLR start "UNARY_NOT_TERM"
    public final void mUNARY_NOT_TERM() throws RecognitionException {
        try {
            int _type = UNARY_NOT_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1780:2: ( '!' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1780:4: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNARY_NOT_TERM"

    // $ANTLR start "DIVIDE_TERM"
    public final void mDIVIDE_TERM() throws RecognitionException {
        try {
            int _type = DIVIDE_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1784:2: ( '/' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1784:4: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIVIDE_TERM"

    // $ANTLR start "EQUAL_TERM"
    public final void mEQUAL_TERM() throws RecognitionException {
        try {
            int _type = EQUAL_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1788:2: ( '=' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1788:4: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUAL_TERM"

    // $ANTLR start "LESS_TERM"
    public final void mLESS_TERM() throws RecognitionException {
        try {
            int _type = LESS_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1792:2: ( '<' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1792:4: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS_TERM"

    // $ANTLR start "GREATER_TERM"
    public final void mGREATER_TERM() throws RecognitionException {
        try {
            int _type = GREATER_TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1796:2: ( '>' )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1796:4: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER_TERM"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1800:2: ( . )
            // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1800:4: .
            {
            matchAny(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ANY"

    public void mTokens() throws RecognitionException {
        // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:8: ( WS | PNAME_NS | PNAME_LN | INTEGER_TERM | DECIMAL_TERM | FLOAT_TERM | STRING_TERM | LENGTH_TERM | MINLENGTH_TERM | MAXLENGTH_TERM | PATTERN_TERM | LANGPATTERN_TERM | INVERSE_TERM | OR_TERM | AND_TERM | THAT_TERM | NOT_TERM | SOME_TERM | ONLY_TERM | VALUE_TERM | SELF_TERM | MIN_TERM | MAX_TERM | EXACTLY_TERM | BASE_TERM | PREFIX_TERM | SELECT_TERM | DISTINCT_TERM | REDUCED_TERM | CONSTRUCT_TERM | DESCRIBE_TERM | ASK_TERM | FROM_TERM | NAMED_TERM | WHERE_TERM | ORDER_TERM | BY_TERM | ASC_TERM | DESC_TERM | LIMIT_TERM | OFFSET_TERM | OPTIONAL_TERM | GRAPH_TERM | UNION_TERM | FILTER_TERM | A_TERM | STR_TERM | LANG_TERM | LANGMATCHES_TERM | DATATYPE_TERM | BOUND_TERM | SAMETERM_TERM | ISIRI_TERM | ISURI_TERM | ISBLANK_TERM | ISLITERAL_TERM | REGEX_TERM | TRUE_TERM | FALSE_TERM | IRI_REF_TERM | BLANK_NODE_LABEL | VAR1 | VAR2 | LANGTAG | INTEGER | DECIMAL | DOUBLE | INTEGER_POSITIVE | DECIMAL_POSITIVE | DOUBLE_POSITIVE | INTEGER_NEGATIVE | DECIMAL_NEGATIVE | DOUBLE_NEGATIVE | STRING_LITERAL1 | STRING_LITERAL2 | STRING_LITERAL_LONG1 | STRING_LITERAL_LONG2 | COMMENT | DOUBLE_CARAT_TERM | LESS_EQUAL_TERM | GREATER_EQUAL_TERM | NOT_EQUAL_TERM | AND_OPERATOR_TERM | OR_OPERATOR_TERM | OPEN_BRACE | CLOSE_BRACE | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | OPEN_SQUARE_BRACE | CLOSE_SQUARE_BRACE | SEMICOLON_TERM | DOT_TERM | PLUS_TERM | MINUS_TERM | ASTERISK_TERM | COMMA_TERM | UNARY_NOT_TERM | DIVIDE_TERM | EQUAL_TERM | LESS_TERM | GREATER_TERM | ANY )
        int alt33=102;
        alt33 = dfa33.predict(input);
        switch (alt33) {
            case 1 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:10: WS
                {
                mWS(); 

                }
                break;
            case 2 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:13: PNAME_NS
                {
                mPNAME_NS(); 

                }
                break;
            case 3 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:22: PNAME_LN
                {
                mPNAME_LN(); 

                }
                break;
            case 4 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:31: INTEGER_TERM
                {
                mINTEGER_TERM(); 

                }
                break;
            case 5 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:44: DECIMAL_TERM
                {
                mDECIMAL_TERM(); 

                }
                break;
            case 6 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:57: FLOAT_TERM
                {
                mFLOAT_TERM(); 

                }
                break;
            case 7 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:68: STRING_TERM
                {
                mSTRING_TERM(); 

                }
                break;
            case 8 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:80: LENGTH_TERM
                {
                mLENGTH_TERM(); 

                }
                break;
            case 9 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:92: MINLENGTH_TERM
                {
                mMINLENGTH_TERM(); 

                }
                break;
            case 10 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:107: MAXLENGTH_TERM
                {
                mMAXLENGTH_TERM(); 

                }
                break;
            case 11 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:122: PATTERN_TERM
                {
                mPATTERN_TERM(); 

                }
                break;
            case 12 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:135: LANGPATTERN_TERM
                {
                mLANGPATTERN_TERM(); 

                }
                break;
            case 13 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:152: INVERSE_TERM
                {
                mINVERSE_TERM(); 

                }
                break;
            case 14 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:165: OR_TERM
                {
                mOR_TERM(); 

                }
                break;
            case 15 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:173: AND_TERM
                {
                mAND_TERM(); 

                }
                break;
            case 16 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:182: THAT_TERM
                {
                mTHAT_TERM(); 

                }
                break;
            case 17 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:192: NOT_TERM
                {
                mNOT_TERM(); 

                }
                break;
            case 18 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:201: SOME_TERM
                {
                mSOME_TERM(); 

                }
                break;
            case 19 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:211: ONLY_TERM
                {
                mONLY_TERM(); 

                }
                break;
            case 20 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:221: VALUE_TERM
                {
                mVALUE_TERM(); 

                }
                break;
            case 21 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:232: SELF_TERM
                {
                mSELF_TERM(); 

                }
                break;
            case 22 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:242: MIN_TERM
                {
                mMIN_TERM(); 

                }
                break;
            case 23 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:251: MAX_TERM
                {
                mMAX_TERM(); 

                }
                break;
            case 24 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:260: EXACTLY_TERM
                {
                mEXACTLY_TERM(); 

                }
                break;
            case 25 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:273: BASE_TERM
                {
                mBASE_TERM(); 

                }
                break;
            case 26 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:283: PREFIX_TERM
                {
                mPREFIX_TERM(); 

                }
                break;
            case 27 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:295: SELECT_TERM
                {
                mSELECT_TERM(); 

                }
                break;
            case 28 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:307: DISTINCT_TERM
                {
                mDISTINCT_TERM(); 

                }
                break;
            case 29 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:321: REDUCED_TERM
                {
                mREDUCED_TERM(); 

                }
                break;
            case 30 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:334: CONSTRUCT_TERM
                {
                mCONSTRUCT_TERM(); 

                }
                break;
            case 31 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:349: DESCRIBE_TERM
                {
                mDESCRIBE_TERM(); 

                }
                break;
            case 32 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:363: ASK_TERM
                {
                mASK_TERM(); 

                }
                break;
            case 33 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:372: FROM_TERM
                {
                mFROM_TERM(); 

                }
                break;
            case 34 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:382: NAMED_TERM
                {
                mNAMED_TERM(); 

                }
                break;
            case 35 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:393: WHERE_TERM
                {
                mWHERE_TERM(); 

                }
                break;
            case 36 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:404: ORDER_TERM
                {
                mORDER_TERM(); 

                }
                break;
            case 37 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:415: BY_TERM
                {
                mBY_TERM(); 

                }
                break;
            case 38 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:423: ASC_TERM
                {
                mASC_TERM(); 

                }
                break;
            case 39 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:432: DESC_TERM
                {
                mDESC_TERM(); 

                }
                break;
            case 40 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:442: LIMIT_TERM
                {
                mLIMIT_TERM(); 

                }
                break;
            case 41 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:453: OFFSET_TERM
                {
                mOFFSET_TERM(); 

                }
                break;
            case 42 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:465: OPTIONAL_TERM
                {
                mOPTIONAL_TERM(); 

                }
                break;
            case 43 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:479: GRAPH_TERM
                {
                mGRAPH_TERM(); 

                }
                break;
            case 44 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:490: UNION_TERM
                {
                mUNION_TERM(); 

                }
                break;
            case 45 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:501: FILTER_TERM
                {
                mFILTER_TERM(); 

                }
                break;
            case 46 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:513: A_TERM
                {
                mA_TERM(); 

                }
                break;
            case 47 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:520: STR_TERM
                {
                mSTR_TERM(); 

                }
                break;
            case 48 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:529: LANG_TERM
                {
                mLANG_TERM(); 

                }
                break;
            case 49 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:539: LANGMATCHES_TERM
                {
                mLANGMATCHES_TERM(); 

                }
                break;
            case 50 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:556: DATATYPE_TERM
                {
                mDATATYPE_TERM(); 

                }
                break;
            case 51 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:570: BOUND_TERM
                {
                mBOUND_TERM(); 

                }
                break;
            case 52 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:581: SAMETERM_TERM
                {
                mSAMETERM_TERM(); 

                }
                break;
            case 53 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:595: ISIRI_TERM
                {
                mISIRI_TERM(); 

                }
                break;
            case 54 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:606: ISURI_TERM
                {
                mISURI_TERM(); 

                }
                break;
            case 55 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:617: ISBLANK_TERM
                {
                mISBLANK_TERM(); 

                }
                break;
            case 56 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:630: ISLITERAL_TERM
                {
                mISLITERAL_TERM(); 

                }
                break;
            case 57 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:645: REGEX_TERM
                {
                mREGEX_TERM(); 

                }
                break;
            case 58 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:656: TRUE_TERM
                {
                mTRUE_TERM(); 

                }
                break;
            case 59 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:666: FALSE_TERM
                {
                mFALSE_TERM(); 

                }
                break;
            case 60 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:677: IRI_REF_TERM
                {
                mIRI_REF_TERM(); 

                }
                break;
            case 61 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:690: BLANK_NODE_LABEL
                {
                mBLANK_NODE_LABEL(); 

                }
                break;
            case 62 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:707: VAR1
                {
                mVAR1(); 

                }
                break;
            case 63 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:712: VAR2
                {
                mVAR2(); 

                }
                break;
            case 64 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:717: LANGTAG
                {
                mLANGTAG(); 

                }
                break;
            case 65 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:725: INTEGER
                {
                mINTEGER(); 

                }
                break;
            case 66 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:733: DECIMAL
                {
                mDECIMAL(); 

                }
                break;
            case 67 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:741: DOUBLE
                {
                mDOUBLE(); 

                }
                break;
            case 68 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:748: INTEGER_POSITIVE
                {
                mINTEGER_POSITIVE(); 

                }
                break;
            case 69 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:765: DECIMAL_POSITIVE
                {
                mDECIMAL_POSITIVE(); 

                }
                break;
            case 70 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:782: DOUBLE_POSITIVE
                {
                mDOUBLE_POSITIVE(); 

                }
                break;
            case 71 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:798: INTEGER_NEGATIVE
                {
                mINTEGER_NEGATIVE(); 

                }
                break;
            case 72 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:815: DECIMAL_NEGATIVE
                {
                mDECIMAL_NEGATIVE(); 

                }
                break;
            case 73 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:832: DOUBLE_NEGATIVE
                {
                mDOUBLE_NEGATIVE(); 

                }
                break;
            case 74 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:848: STRING_LITERAL1
                {
                mSTRING_LITERAL1(); 

                }
                break;
            case 75 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:864: STRING_LITERAL2
                {
                mSTRING_LITERAL2(); 

                }
                break;
            case 76 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:880: STRING_LITERAL_LONG1
                {
                mSTRING_LITERAL_LONG1(); 

                }
                break;
            case 77 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:901: STRING_LITERAL_LONG2
                {
                mSTRING_LITERAL_LONG2(); 

                }
                break;
            case 78 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:922: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 79 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:930: DOUBLE_CARAT_TERM
                {
                mDOUBLE_CARAT_TERM(); 

                }
                break;
            case 80 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:948: LESS_EQUAL_TERM
                {
                mLESS_EQUAL_TERM(); 

                }
                break;
            case 81 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:964: GREATER_EQUAL_TERM
                {
                mGREATER_EQUAL_TERM(); 

                }
                break;
            case 82 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:983: NOT_EQUAL_TERM
                {
                mNOT_EQUAL_TERM(); 

                }
                break;
            case 83 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:998: AND_OPERATOR_TERM
                {
                mAND_OPERATOR_TERM(); 

                }
                break;
            case 84 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1016: OR_OPERATOR_TERM
                {
                mOR_OPERATOR_TERM(); 

                }
                break;
            case 85 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1033: OPEN_BRACE
                {
                mOPEN_BRACE(); 

                }
                break;
            case 86 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1044: CLOSE_BRACE
                {
                mCLOSE_BRACE(); 

                }
                break;
            case 87 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1056: OPEN_CURLY_BRACE
                {
                mOPEN_CURLY_BRACE(); 

                }
                break;
            case 88 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1073: CLOSE_CURLY_BRACE
                {
                mCLOSE_CURLY_BRACE(); 

                }
                break;
            case 89 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1091: OPEN_SQUARE_BRACE
                {
                mOPEN_SQUARE_BRACE(); 

                }
                break;
            case 90 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1109: CLOSE_SQUARE_BRACE
                {
                mCLOSE_SQUARE_BRACE(); 

                }
                break;
            case 91 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1128: SEMICOLON_TERM
                {
                mSEMICOLON_TERM(); 

                }
                break;
            case 92 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1143: DOT_TERM
                {
                mDOT_TERM(); 

                }
                break;
            case 93 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1152: PLUS_TERM
                {
                mPLUS_TERM(); 

                }
                break;
            case 94 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1162: MINUS_TERM
                {
                mMINUS_TERM(); 

                }
                break;
            case 95 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1173: ASTERISK_TERM
                {
                mASTERISK_TERM(); 

                }
                break;
            case 96 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1187: COMMA_TERM
                {
                mCOMMA_TERM(); 

                }
                break;
            case 97 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1198: UNARY_NOT_TERM
                {
                mUNARY_NOT_TERM(); 

                }
                break;
            case 98 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1213: DIVIDE_TERM
                {
                mDIVIDE_TERM(); 

                }
                break;
            case 99 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1225: EQUAL_TERM
                {
                mEQUAL_TERM(); 

                }
                break;
            case 100 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1236: LESS_TERM
                {
                mLESS_TERM(); 

                }
                break;
            case 101 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1246: GREATER_TERM
                {
                mGREATER_TERM(); 

                }
                break;
            case 102 :
                // /home/msmith/devel/pellet-git/query/antlr/SparqlOwl.g:1:1259: ANY
                {
                mANY(); 

                }
                break;

        }

    }


    protected DFA16 dfa16 = new DFA16(this);
    protected DFA33 dfa33 = new DFA33(this);
    static final String DFA16_eotS =
        "\5\uffff";
    static final String DFA16_eofS =
        "\5\uffff";
    static final String DFA16_minS =
        "\2\56\3\uffff";
    static final String DFA16_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA16_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA16_specialS =
        "\5\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "1494:1: DOUBLE : ( ( DIGIT )+ DOT_TERM ( DIGIT )* EXPONENT | DOT_TERM ( DIGIT )+ EXPONENT | ( DIGIT )+ EXPONENT );";
        }
    }
    static final String DFA33_eotS =
        "\2\uffff\1\64\1\73\7\64\1\125\14\64\1\145\4\64\1\153\1\157\1\161"+
        "\1\164\4\64\1\176\1\u0080\2\64\21\uffff\1\73\24\uffff\1\u00a8\15"+
        "\uffff\1\u00b6\6\uffff\1\u00be\7\uffff\1\u00bf\1\153\2\uffff\1\u00bf"+
        "\1\uffff\1\u00c1\2\uffff\1\u00c5\1\uffff\1\170\1\uffff\1\172\42"+
        "\uffff\1\u00da\6\uffff\1\u00e3\1\u00e5\7\uffff\1\u00ec\1\u00ed\1"+
        "\u00ee\2\uffff\1\u00f1\16\uffff\1\u00bf\2\uffff\2\u00fd\1\uffff"+
        "\1\u00ff\1\uffff\1\u00ff\11\uffff\1\u0109\3\uffff\1\u010d\4\uffff"+
        "\1\u0111\1\u0112\3\uffff\1\u0118\10\uffff\1\u011f\5\uffff\1\u0122"+
        "\1\u0123\4\uffff\1\u0127\10\uffff\1\u00fd\1\uffff\1\u00ff\2\uffff"+
        "\1\u0131\1\u0132\7\uffff\1\u0139\2\uffff\1\u013b\11\uffff\1\u0142"+
        "\4\uffff\1\u0147\5\uffff\1\u014a\1\u014b\2\uffff\1\u014d\1\uffff"+
        "\1\u014f\1\uffff\1\u0151\1\u0152\1\u0153\13\uffff\1\u015c\1\uffff"+
        "\1\u015d\1\u015e\1\uffff\1\u0160\6\uffff\1\u0166\1\uffff\1\u0167"+
        "\13\uffff\1\u016c\1\u016d\1\u016e\1\uffff\1\u0170\14\uffff\1\u0179"+
        "\3\uffff\1\u017b\1\u017c\6\uffff\1\u017f\1\u0180\1\u0181\1\u0182"+
        "\5\uffff\1\u0187\3\uffff\1\u0189\6\uffff\1\u018c\1\u018d\1\uffff"+
        "\1\u018e\6\uffff\1\u0191\1\u0192\2\uffff";
    static final String DFA33_eofS =
        "\u0193\uffff";
    static final String DFA33_minS =
        "\1\0\1\uffff\1\55\1\60\24\55\1\41\1\72\2\60\1\101\1\56\1\60\2\56"+
        "\3\0\1\136\2\75\1\46\1\174\15\uffff\4\55\1\60\2\uffff\30\55\1\uffff"+
        "\16\55\1\41\7\uffff\1\60\1\56\2\uffff\1\60\1\uffff\1\56\1\60\1\uffff"+
        "\1\56\1\60\1\47\1\uffff\1\42\24\uffff\32\55\1\uffff\15\55\1\uffff"+
        "\7\55\2\uffff\1\60\2\uffff\2\60\1\uffff\1\60\1\uffff\1\60\2\uffff"+
        "\17\55\1\uffff\10\55\1\uffff\1\55\1\uffff\6\55\3\uffff\2\55\1\uffff"+
        "\13\55\1\uffff\1\60\1\uffff\1\60\10\55\1\uffff\3\55\1\uffff\3\55"+
        "\2\uffff\5\55\1\uffff\6\55\1\uffff\2\55\2\uffff\3\55\1\uffff\11"+
        "\55\2\uffff\6\55\1\uffff\1\55\1\uffff\6\55\1\uffff\4\55\1\uffff"+
        "\2\55\2\uffff\1\55\1\uffff\1\55\1\uffff\1\55\3\uffff\10\55\3\uffff"+
        "\1\55\1\uffff\5\55\2\uffff\4\55\3\uffff\1\55\1\uffff\10\55\1\uffff"+
        "\1\55\2\uffff\2\55\4\uffff\4\55\1\uffff\1\55\1\uffff\2\55\3\uffff"+
        "\2\55\2\uffff";
    static final String DFA33_maxS =
        "\1\uffff\1\uffff\26\ufffd\1\uffff\1\72\2\ufffd\1\172\1\145\3\71"+
        "\3\uffff\1\136\2\75\1\46\1\174\15\uffff\5\ufffd\2\uffff\30\ufffd"+
        "\1\uffff\16\ufffd\1\uffff\7\uffff\2\145\2\uffff\1\145\1\uffff\1"+
        "\145\1\71\1\uffff\1\145\1\71\1\47\1\uffff\1\42\24\uffff\32\ufffd"+
        "\1\uffff\15\ufffd\1\uffff\7\ufffd\2\uffff\1\145\2\uffff\2\145\1"+
        "\uffff\1\145\1\uffff\1\145\2\uffff\17\ufffd\1\uffff\10\ufffd\1\uffff"+
        "\1\ufffd\1\uffff\6\ufffd\3\uffff\2\ufffd\1\uffff\13\ufffd\1\uffff"+
        "\1\145\1\uffff\1\145\10\ufffd\1\uffff\3\ufffd\1\uffff\3\ufffd\2"+
        "\uffff\5\ufffd\1\uffff\6\ufffd\1\uffff\2\ufffd\2\uffff\3\ufffd\1"+
        "\uffff\11\ufffd\2\uffff\6\ufffd\1\uffff\1\ufffd\1\uffff\6\ufffd"+
        "\1\uffff\4\ufffd\1\uffff\2\ufffd\2\uffff\1\ufffd\1\uffff\1\ufffd"+
        "\1\uffff\1\ufffd\3\uffff\10\ufffd\3\uffff\1\ufffd\1\uffff\5\ufffd"+
        "\2\uffff\4\ufffd\3\uffff\1\ufffd\1\uffff\10\ufffd\1\uffff\1\ufffd"+
        "\2\uffff\2\ufffd\4\uffff\4\ufffd\1\uffff\1\ufffd\1\uffff\2\ufffd"+
        "\3\uffff\2\ufffd\2\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\1\47\uffff\1\125\1\126\1\127\1\130\1\131\1\132\1\133"+
        "\1\137\1\140\1\142\1\143\1\146\1\1\5\uffff\1\2\1\3\30\uffff\1\56"+
        "\17\uffff\1\144\1\74\1\75\1\76\1\77\1\100\1\101\2\uffff\1\103\1"+
        "\134\1\uffff\1\135\2\uffff\1\136\3\uffff\1\112\1\uffff\1\113\1\116"+
        "\1\117\1\121\1\145\1\122\1\141\1\123\1\124\1\125\1\126\1\127\1\130"+
        "\1\131\1\132\1\133\1\137\1\140\1\142\1\143\32\uffff\1\16\15\uffff"+
        "\1\45\7\uffff\1\120\1\102\1\uffff\1\104\1\106\2\uffff\1\107\1\uffff"+
        "\1\111\1\uffff\1\114\1\115\17\uffff\1\57\10\uffff\1\26\1\uffff\1"+
        "\27\6\uffff\1\17\1\40\1\46\2\uffff\1\21\13\uffff\1\105\1\uffff\1"+
        "\110\11\uffff\1\47\3\uffff\1\41\3\uffff\1\22\1\25\5\uffff\1\60\6"+
        "\uffff\1\23\2\uffff\1\20\1\72\3\uffff\1\31\11\uffff\1\65\1\66\6"+
        "\uffff\1\6\1\uffff\1\73\6\uffff\1\50\4\uffff\1\44\2\uffff\1\42\1"+
        "\24\1\uffff\1\63\1\uffff\1\71\1\uffff\1\43\1\53\1\54\10\uffff\1"+
        "\55\1\7\1\33\1\uffff\1\10\5\uffff\1\32\1\51\4\uffff\1\4\1\15\1\67"+
        "\1\uffff\1\5\10\uffff\1\13\1\uffff\1\30\1\35\2\uffff\1\37\1\34\1"+
        "\62\1\64\4\uffff\1\52\1\uffff\1\70\2\uffff\1\11\1\12\1\36\2\uffff"+
        "\1\14\1\61";
    static final String DFA33_specialS =
        "\1\2\40\uffff\1\1\1\3\1\0\u016f\uffff}>";
    static final String[] DFA33_transitionS = {
            "\11\64\2\1\2\64\1\1\22\64\1\1\1\46\1\42\1\43\1\33\1\64\1\47"+
            "\1\41\1\51\1\52\1\60\1\37\1\61\1\40\1\36\1\62\12\35\1\3\1\57"+
            "\1\30\1\63\1\45\1\32\1\34\1\27\1\20\1\22\1\4\1\17\1\5\1\24\1"+
            "\26\1\2\2\26\1\7\1\10\1\15\1\12\1\11\1\26\1\21\1\6\1\14\1\25"+
            "\1\16\1\23\3\26\1\55\1\64\1\56\1\44\1\31\1\64\1\13\1\20\1\22"+
            "\1\4\1\17\1\5\1\24\1\26\1\2\2\26\1\7\1\10\1\15\1\12\1\11\1\26"+
            "\1\21\1\6\1\14\1\25\1\16\1\23\3\26\1\53\1\50\1\54\102\64\27"+
            "\26\1\64\37\26\1\64\u0208\26\160\64\16\26\1\64\u1c81\26\14\64"+
            "\2\26\142\64\u0120\26\u0a70\64\u03f0\26\21\64\ua7ff\26\u2100"+
            "\64\u04d0\26\40\64\u020e\26\2\64",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\66\4\70\1\67"+
            "\7\70\4\uffff\1\70\1\uffff\15\70\1\66\4\70\1\67\7\70\74\uffff"+
            "\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff"+
            "\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70"+
            "\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff"+
            "\u020e\70",
            "\12\74\7\uffff\32\74\4\uffff\1\74\1\uffff\32\74\105\uffff\27"+
            "\74\1\uffff\37\74\1\uffff\u0208\74\160\uffff\16\74\1\uffff\u1c81"+
            "\74\14\uffff\2\74\142\uffff\u0120\74\u0a70\uffff\u03f0\74\21"+
            "\uffff\ua7ff\74\u2100\uffff\u04d0\74\40\uffff\u020e\74",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\77\3\70\1\75\3\70\1"+
            "\76\21\70\4\uffff\1\70\1\uffff\1\77\3\70\1\75\3\70\1\76\21\70"+
            "\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70"+
            "\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\103\7\70\1\102\2\70"+
            "\1\100\5\70\1\101\10\70\4\uffff\1\70\1\uffff\1\103\7\70\1\102"+
            "\2\70\1\100\5\70\1\101\10\70\74\uffff\1\70\10\uffff\27\70\1"+
            "\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70"+
            "\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff"+
            "\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\107\3\70\1\106\11\70"+
            "\1\105\4\70\1\104\6\70\4\uffff\1\70\1\uffff\1\107\3\70\1\106"+
            "\11\70\1\105\4\70\1\104\6\70\74\uffff\1\70\10\uffff\27\70\1"+
            "\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70"+
            "\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff"+
            "\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\111\3\70\1\110\3\70"+
            "\1\112\21\70\4\uffff\1\70\1\uffff\1\111\3\70\1\110\3\70\1\112"+
            "\21\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286"+
            "\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\114\7\70\1\113\21\70"+
            "\4\uffff\1\70\1\uffff\1\114\7\70\1\113\21\70\74\uffff\1\70\10"+
            "\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70"+
            "\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0"+
            "\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\115\20\70\1\116\10"+
            "\70\4\uffff\1\70\1\uffff\1\115\20\70\1\116\10\70\74\uffff\1"+
            "\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81"+
            "\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff"+
            "\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e"+
            "\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\5\70\1\121\7\70\1\120"+
            "\1\70\1\122\1\70\1\117\10\70\4\uffff\1\70\1\uffff\5\70\1\121"+
            "\7\70\1\120\1\70\1\122\1\70\1\117\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\123\4\70\1\124"+
            "\7\70\4\uffff\1\70\1\uffff\15\70\1\123\4\70\1\124\7\70\74\uffff"+
            "\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff"+
            "\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70"+
            "\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff"+
            "\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\7\70\1\126\11\70\1\127"+
            "\10\70\4\uffff\1\70\1\uffff\7\70\1\126\11\70\1\127\10\70\74"+
            "\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1"+
            "\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70"+
            "\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70"+
            "\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\131\15\70\1\130\13"+
            "\70\4\uffff\1\70\1\uffff\1\131\15\70\1\130\13\70\74\uffff\1"+
            "\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81"+
            "\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff"+
            "\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e"+
            "\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\132\31\70\4\uffff\1"+
            "\70\1\uffff\1\132\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\27\70\1\133\2\70\4\uffff"+
            "\1\70\1\uffff\27\70\1\133\2\70\74\uffff\1\70\10\uffff\27\70"+
            "\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2"+
            "\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\134\15\70\1\136\11"+
            "\70\1\135\1\70\4\uffff\1\70\1\uffff\1\134\15\70\1\136\11\70"+
            "\1\135\1\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\137\25\70\4\uffff"+
            "\1\70\1\uffff\4\70\1\137\25\70\74\uffff\1\70\10\uffff\27\70"+
            "\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2"+
            "\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\16\70\1\140\13\70\4\uffff"+
            "\1\70\1\uffff\16\70\1\140\13\70\74\uffff\1\70\10\uffff\27\70"+
            "\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2"+
            "\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\7\70\1\141\22\70\4\uffff"+
            "\1\70\1\uffff\7\70\1\141\22\70\74\uffff\1\70\10\uffff\27\70"+
            "\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2"+
            "\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\142\10\70\4\uffff"+
            "\1\70\1\uffff\21\70\1\142\10\70\74\uffff\1\70\10\uffff\27\70"+
            "\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2"+
            "\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\143\14\70\4\uffff"+
            "\1\70\1\uffff\15\70\1\143\14\70\74\uffff\1\70\10\uffff\27\70"+
            "\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2"+
            "\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\123\4\70\1\124"+
            "\7\70\4\uffff\1\70\1\uffff\15\70\1\123\4\70\1\124\7\70\74\uffff"+
            "\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff"+
            "\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70"+
            "\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff"+
            "\u020e\70",
            "\1\146\1\uffff\31\146\1\uffff\1\144\36\146\1\uffff\1\146\1"+
            "\uffff\1\146\1\uffff\32\146\3\uffff\uff82\146",
            "\1\147",
            "\12\150\7\uffff\32\150\4\uffff\1\150\1\uffff\32\150\105\uffff"+
            "\27\150\1\uffff\37\150\1\uffff\u0208\150\160\uffff\16\150\1"+
            "\uffff\u1c81\150\14\uffff\2\150\142\uffff\u0120\150\u0a70\uffff"+
            "\u03f0\150\21\uffff\ua7ff\150\u2100\uffff\u04d0\150\40\uffff"+
            "\u020e\150",
            "\12\151\7\uffff\32\151\4\uffff\1\151\1\uffff\32\151\105\uffff"+
            "\27\151\1\uffff\37\151\1\uffff\u0208\151\160\uffff\16\151\1"+
            "\uffff\u1c81\151\14\uffff\2\151\142\uffff\u0120\151\u0a70\uffff"+
            "\u03f0\151\21\uffff\ua7ff\151\u2100\uffff\u04d0\151\40\uffff"+
            "\u020e\151",
            "\32\152\6\uffff\32\152",
            "\1\154\1\uffff\12\155\13\uffff\1\156\37\uffff\1\156",
            "\12\160",
            "\1\163\1\uffff\12\162",
            "\1\166\1\uffff\12\165",
            "\12\170\1\uffff\2\170\1\uffff\31\170\1\167\uffd8\170",
            "\12\172\1\uffff\2\172\1\uffff\24\172\1\171\uffdd\172",
            "\0\173",
            "\1\174",
            "\1\175",
            "\1\177",
            "\1\u0081",
            "\1\u0082",
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
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u008e\1\70\1"+
            "\u008f\4\70\4\uffff\1\70\1\uffff\23\70\1\u008e\1\70\1\u008f"+
            "\4\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286"+
            "\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\70\1\u0092\6\70\1\u0090"+
            "\2\70\1\u0093\10\70\1\u0091\5\70\4\uffff\1\70\1\uffff\1\70\1"+
            "\u0092\6\70\1\u0090\2\70\1\u0093\10\70\1\u0091\5\70\74\uffff"+
            "\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff"+
            "\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70"+
            "\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff"+
            "\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\7\uffff\32\70\4\uffff\1\70\1\uffff"+
            "\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286"+
            "\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "\12\74\7\uffff\32\74\4\uffff\1\74\1\uffff\32\74\105\uffff\27"+
            "\74\1\uffff\37\74\1\uffff\u0208\74\160\uffff\16\74\1\uffff\u1c81"+
            "\74\14\uffff\2\74\142\uffff\u0120\74\u0a70\uffff\u03f0\74\21"+
            "\uffff\ua7ff\74\u2100\uffff\u04d0\74\40\uffff\u020e\74",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u0094\17\70\1"+
            "\u0095\7\70\4\uffff\1\70\1\uffff\2\70\1\u0094\17\70\1\u0095"+
            "\7\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286"+
            "\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\22\70\1\u0096\7\70\4"+
            "\uffff\1\70\1\uffff\22\70\1\u0096\7\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0097\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0097\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\16\70\1\u0098\13\70\4"+
            "\uffff\1\70\1\uffff\16\70\1\u0098\13\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\16\70\1\u0099\13\70\4"+
            "\uffff\1\70\1\uffff\16\70\1\u0099\13\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u009a\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u009a\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u009b\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u009b\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u009c\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u009c\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u009d\15\70\4"+
            "\uffff\1\70\1\uffff\14\70\1\u009d\15\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u009e\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u009e\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u009f\15\70\4"+
            "\uffff\1\70\1\uffff\14\70\1\u009f\15\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u00a0\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u00a0\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u00a1\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u00a1\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u00a2\15\70\4"+
            "\uffff\1\70\1\uffff\14\70\1\u00a2\15\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u00a3\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u00a3\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\27\70\1\u00a4\2\70\4"+
            "\uffff\1\70\1\uffff\27\70\1\u00a4\2\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u00a5\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u00a5\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00a6\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00a6\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\3\70\1\u00a7\26\70\4"+
            "\uffff\1\70\1\uffff\3\70\1\u00a7\26\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u00a9\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u00a9\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\5\70\1\u00aa\24\70\4"+
            "\uffff\1\70\1\uffff\5\70\1\u00aa\24\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u00ab\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u00ab\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\3\70\1\u00ac\26\70\4"+
            "\uffff\1\70\1\uffff\3\70\1\u00ac\26\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u00ae\7\70\1\u00ad"+
            "\17\70\4\uffff\1\70\1\uffff\2\70\1\u00ae\7\70\1\u00ad\17\70"+
            "\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70"+
            "\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u00af\31\70\4\uffff"+
            "\1\70\1\uffff\1\u00af\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\24\70\1\u00b0\5\70\4"+
            "\uffff\1\70\1\uffff\24\70\1\u00b0\5\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u00b1\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u00b1\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u00b2\15\70\4"+
            "\uffff\1\70\1\uffff\14\70\1\u00b2\15\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u00b3\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u00b3\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u00b4\31\70\4\uffff"+
            "\1\70\1\uffff\1\u00b4\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\22\70\1\u00b5\7\70\4"+
            "\uffff\1\70\1\uffff\22\70\1\u00b5\7\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\24\70\1\u00b7\5\70\4"+
            "\uffff\1\70\1\uffff\24\70\1\u00b7\5\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\3\70\1\u00b8\2\70\1\u00b9"+
            "\23\70\4\uffff\1\70\1\uffff\3\70\1\u00b8\2\70\1\u00b9\23\70"+
            "\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70"+
            "\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u00ba\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u00ba\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00bb\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00bb\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u00bc\31\70\4\uffff"+
            "\1\70\1\uffff\1\u00bc\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u00bd\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u00bd\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\146\1\uffff\31\146\1\uffff\37\146\1\uffff\1\146\1\uffff"+
            "\1\146\1\uffff\32\146\3\uffff\uff82\146",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\u00c0\13\uffff\1\156\37\uffff\1\156",
            "\1\154\1\uffff\12\155\13\uffff\1\156\37\uffff\1\156",
            "",
            "",
            "\12\160\13\uffff\1\156\37\uffff\1\156",
            "",
            "\1\u00c3\1\uffff\12\162\13\uffff\1\u00c2\37\uffff\1\u00c2",
            "\12\u00c4",
            "",
            "\1\u00c6\1\uffff\12\165\13\uffff\1\u00c7\37\uffff\1\u00c7",
            "\12\u00c8",
            "\1\u00c9",
            "",
            "\1\u00ca",
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
            "",
            "",
            "",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00cb\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00cb\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00cc\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00cc\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u00cd\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u00cd\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u00ce\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u00ce\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u00cf\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u00cf\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u00d0\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u00d0\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u00d1\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u00d1\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u00d2\27\70\4"+
            "\uffff\1\70\1\uffff\2\70\1\u00d2\27\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u00d3\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u00d3\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u00d4\31\70\4\uffff"+
            "\1\70\1\uffff\1\u00d4\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u00d5\31\70\4\uffff"+
            "\1\70\1\uffff\1\u00d5\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u00d6\15\70\4"+
            "\uffff\1\70\1\uffff\14\70\1\u00d6\15\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u00d7\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u00d7\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\22\70\1\u00d8\7\70\4"+
            "\uffff\1\70\1\uffff\22\70\1\u00d8\7\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u00d9\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u00d9\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00db\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00db\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00dd\1\u00dc"+
            "\24\70\4\uffff\1\70\1\uffff\4\70\1\u00dd\1\u00dc\24\70\74\uffff"+
            "\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff"+
            "\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70"+
            "\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff"+
            "\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00de\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00de\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\6\70\1\u00df\23\70\4"+
            "\uffff\1\70\1\uffff\6\70\1\u00df\23\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\6\70\1\u00e0\23\70\4"+
            "\uffff\1\70\1\uffff\6\70\1\u00e0\23\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u00e1\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u00e1\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u00e2\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u00e2\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u00e4\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u00e4\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u00e6\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u00e6\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\5\70\1\u00e7\24\70\4"+
            "\uffff\1\70\1\uffff\5\70\1\u00e7\24\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00e8\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00e8\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\30\70\1\u00e9\1\70\4"+
            "\uffff\1\70\1\uffff\30\70\1\u00e9\1\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\22\70\1\u00ea\7\70\4"+
            "\uffff\1\70\1\uffff\22\70\1\u00ea\7\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u00eb\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u00eb\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u00ef\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u00ef\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00f0\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00f0\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00f2\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00f2\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\24\70\1\u00f3\5\70\4"+
            "\uffff\1\70\1\uffff\24\70\1\u00f3\5\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u00f4\27\70\4"+
            "\uffff\1\70\1\uffff\2\70\1\u00f4\27\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00f5\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00f5\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u00f6\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u00f6\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\24\70\1\u00f7\5\70\4"+
            "\uffff\1\70\1\uffff\24\70\1\u00f7\5\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u00f8\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u00f8\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\22\70\1\u00f9\7\70\4"+
            "\uffff\1\70\1\uffff\22\70\1\u00f9\7\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u00fa\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u00fa\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\17\70\1\u00fb\12\70\4"+
            "\uffff\1\70\1\uffff\17\70\1\u00fb\12\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\16\70\1\u00fc\13\70\4"+
            "\uffff\1\70\1\uffff\16\70\1\u00fc\13\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "\12\u00c0\13\uffff\1\156\37\uffff\1\156",
            "",
            "",
            "\12\u00fe\13\uffff\1\u00c2\37\uffff\1\u00c2",
            "\12\u00c4\13\uffff\1\u00c2\37\uffff\1\u00c2",
            "",
            "\12\u0100\13\uffff\1\u00c7\37\uffff\1\u00c7",
            "",
            "\12\u00c8\13\uffff\1\u00c7\37\uffff\1\u00c7",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\6\70\1\u0101\23\70\4"+
            "\uffff\1\70\1\uffff\6\70\1\u0101\23\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u0102\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u0102\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u0103\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u0103\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u0104\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u0104\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u0105\31\70\4\uffff"+
            "\1\70\1\uffff\1\u0105\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0106\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0106\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u0107\15\70\4"+
            "\uffff\1\70\1\uffff\14\70\1\u0107\15\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u0108\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u0108\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u010a\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u010a\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u010b\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u010b\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u010c\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u010c\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u010e\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u010e\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u010f\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u010f\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u0110\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u0110\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u0113\27\70\4"+
            "\uffff\1\70\1\uffff\2\70\1\u0113\27\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0114\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0114\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0115\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0115\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u0117\2\70\1"+
            "\u0116\12\70\4\uffff\1\70\1\uffff\14\70\1\u0117\2\70\1\u0116"+
            "\12\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff\u0286"+
            "\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff\u0120"+
            "\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff\u04d0"+
            "\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0119\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0119\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u011a\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u011a\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u011b\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u011b\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u011c\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u011c\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u011d\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u011d\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u011e\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u011e\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u0120\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u0120\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\16\70\1\u0121\13\70\4"+
            "\uffff\1\70\1\uffff\16\70\1\u0121\13\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\3\70\1\u0124\26\70\4"+
            "\uffff\1\70\1\uffff\3\70\1\u0124\26\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u0125\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u0125\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0126\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0126\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\3\70\1\u0128\26\70\4"+
            "\uffff\1\70\1\uffff\3\70\1\u0128\26\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u0129\27\70\4"+
            "\uffff\1\70\1\uffff\2\70\1\u0129\27\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\27\70\1\u012a\2\70\4"+
            "\uffff\1\70\1\uffff\27\70\1\u012a\2\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u012b\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u012b\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u012c\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u012c\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\7\70\1\u012d\22\70\4"+
            "\uffff\1\70\1\uffff\7\70\1\u012d\22\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u012e\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u012e\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\12\u00fe\13\uffff\1\u00c2\37\uffff\1\u00c2",
            "",
            "\12\u0100\13\uffff\1\u00c7\37\uffff\1\u00c7",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u012f\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u012f\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\22\70\1\u0130\7\70\4"+
            "\uffff\1\70\1\uffff\22\70\1\u0130\7\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u0133\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u0133\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u0134\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u0134\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u0135\31\70\4\uffff"+
            "\1\70\1\uffff\1\u0135\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\10\70\1\u0136\21\70\4"+
            "\uffff\1\70\1\uffff\10\70\1\u0136\21\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u0137\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u0137\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\30\70\1\u0138\1\70\4"+
            "\uffff\1\70\1\uffff\30\70\1\u0138\1\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u013a\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u013a\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\6\70\1\u013c\23\70\4"+
            "\uffff\1\70\1\uffff\6\70\1\u013c\23\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u013d\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u013d\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u013e\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u013e\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\7\70\1\u013f\22\70\4"+
            "\uffff\1\70\1\uffff\7\70\1\u013f\22\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u0140\31\70\4\uffff"+
            "\1\70\1\uffff\1\u0140\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u0141\31\70\4\uffff"+
            "\1\70\1\uffff\1\u0141\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u0143\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u0143\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u0144\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u0144\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u0145\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u0145\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\27\70\1\u0146\2\70\4"+
            "\uffff\1\70\1\uffff\27\70\1\u0146\2\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0148\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0148\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u0149\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u0149\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u014c\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u014c\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u014e\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u014e\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u0150\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u0150\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u0154\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u0154\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u0155\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u0155\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\12\70\1\u0156\17\70\4"+
            "\uffff\1\70\1\uffff\12\70\1\u0156\17\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u0157\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u0157\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u0158\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u0158\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\70\1\u0159\30\70\4"+
            "\uffff\1\70\1\uffff\1\70\1\u0159\30\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u015a\27\70\4"+
            "\uffff\1\70\1\uffff\2\70\1\u015a\27\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\17\70\1\u015b\12\70\4"+
            "\uffff\1\70\1\uffff\17\70\1\u015b\12\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u015f\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u015f\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0161\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0161\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0162\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0162\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\6\70\1\u0163\23\70\4"+
            "\uffff\1\70\1\uffff\6\70\1\u0163\23\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\6\70\1\u0164\23\70\4"+
            "\uffff\1\70\1\uffff\6\70\1\u0164\23\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u0165\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u0165\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u0168\31\70\4\uffff"+
            "\1\70\1\uffff\1\u0168\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\30\70\1\u0169\1\70\4"+
            "\uffff\1\70\1\uffff\30\70\1\u0169\1\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\3\70\1\u016a\26\70\4"+
            "\uffff\1\70\1\uffff\3\70\1\u016a\26\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\24\70\1\u016b\5\70\4"+
            "\uffff\1\70\1\uffff\24\70\1\u016b\5\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\1\u016f\31\70\4\uffff"+
            "\1\70\1\uffff\1\u016f\31\70\74\uffff\1\70\10\uffff\27\70\1\uffff"+
            "\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff"+
            "\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff"+
            "\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u0171\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u0171\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0172\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0172\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u0173\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u0173\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\14\70\1\u0174\15\70\4"+
            "\uffff\1\70\1\uffff\14\70\1\u0174\15\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0175\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0175\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u0176\27\70\4"+
            "\uffff\1\70\1\uffff\2\70\1\u0176\27\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0177\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0177\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0178\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0178\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u017a\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u017a\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\2\70\1\u017d\27\70\4"+
            "\uffff\1\70\1\uffff\2\70\1\u017d\27\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\13\70\1\u017e\16\70\4"+
            "\uffff\1\70\1\uffff\13\70\1\u017e\16\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u0183\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u0183\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\7\70\1\u0184\22\70\4"+
            "\uffff\1\70\1\uffff\7\70\1\u0184\22\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\7\70\1\u0185\22\70\4"+
            "\uffff\1\70\1\uffff\7\70\1\u0185\22\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\7\70\1\u0186\22\70\4"+
            "\uffff\1\70\1\uffff\7\70\1\u0186\22\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\23\70\1\u0188\6\70\4"+
            "\uffff\1\70\1\uffff\23\70\1\u0188\6\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\21\70\1\u018a\10\70\4"+
            "\uffff\1\70\1\uffff\21\70\1\u018a\10\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\4\70\1\u018b\25\70\4"+
            "\uffff\1\70\1\uffff\4\70\1\u018b\25\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\15\70\1\u018f\14\70\4"+
            "\uffff\1\70\1\uffff\15\70\1\u018f\14\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\22\70\1\u0190\7\70\4"+
            "\uffff\1\70\1\uffff\22\70\1\u0190\7\70\74\uffff\1\70\10\uffff"+
            "\27\70\1\uffff\37\70\1\uffff\u0286\70\1\uffff\u1c81\70\14\uffff"+
            "\2\70\61\uffff\2\70\57\uffff\u0120\70\u0a70\uffff\u03f0\70\21"+
            "\uffff\ua7ff\70\u2100\uffff\u04d0\70\40\uffff\u020e\70",
            "",
            "",
            "",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "\1\70\1\71\1\uffff\12\70\1\72\6\uffff\32\70\4\uffff\1\70\1"+
            "\uffff\32\70\74\uffff\1\70\10\uffff\27\70\1\uffff\37\70\1\uffff"+
            "\u0286\70\1\uffff\u1c81\70\14\uffff\2\70\61\uffff\2\70\57\uffff"+
            "\u0120\70\u0a70\uffff\u03f0\70\21\uffff\ua7ff\70\u2100\uffff"+
            "\u04d0\70\40\uffff\u020e\70",
            "",
            ""
    };

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( WS | PNAME_NS | PNAME_LN | INTEGER_TERM | DECIMAL_TERM | FLOAT_TERM | STRING_TERM | LENGTH_TERM | MINLENGTH_TERM | MAXLENGTH_TERM | PATTERN_TERM | LANGPATTERN_TERM | INVERSE_TERM | OR_TERM | AND_TERM | THAT_TERM | NOT_TERM | SOME_TERM | ONLY_TERM | VALUE_TERM | SELF_TERM | MIN_TERM | MAX_TERM | EXACTLY_TERM | BASE_TERM | PREFIX_TERM | SELECT_TERM | DISTINCT_TERM | REDUCED_TERM | CONSTRUCT_TERM | DESCRIBE_TERM | ASK_TERM | FROM_TERM | NAMED_TERM | WHERE_TERM | ORDER_TERM | BY_TERM | ASC_TERM | DESC_TERM | LIMIT_TERM | OFFSET_TERM | OPTIONAL_TERM | GRAPH_TERM | UNION_TERM | FILTER_TERM | A_TERM | STR_TERM | LANG_TERM | LANGMATCHES_TERM | DATATYPE_TERM | BOUND_TERM | SAMETERM_TERM | ISIRI_TERM | ISURI_TERM | ISBLANK_TERM | ISLITERAL_TERM | REGEX_TERM | TRUE_TERM | FALSE_TERM | IRI_REF_TERM | BLANK_NODE_LABEL | VAR1 | VAR2 | LANGTAG | INTEGER | DECIMAL | DOUBLE | INTEGER_POSITIVE | DECIMAL_POSITIVE | DOUBLE_POSITIVE | INTEGER_NEGATIVE | DECIMAL_NEGATIVE | DOUBLE_NEGATIVE | STRING_LITERAL1 | STRING_LITERAL2 | STRING_LITERAL_LONG1 | STRING_LITERAL_LONG2 | COMMENT | DOUBLE_CARAT_TERM | LESS_EQUAL_TERM | GREATER_EQUAL_TERM | NOT_EQUAL_TERM | AND_OPERATOR_TERM | OR_OPERATOR_TERM | OPEN_BRACE | CLOSE_BRACE | OPEN_CURLY_BRACE | CLOSE_CURLY_BRACE | OPEN_SQUARE_BRACE | CLOSE_SQUARE_BRACE | SEMICOLON_TERM | DOT_TERM | PLUS_TERM | MINUS_TERM | ASTERISK_TERM | COMMA_TERM | UNARY_NOT_TERM | DIVIDE_TERM | EQUAL_TERM | LESS_TERM | GREATER_TERM | ANY );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA33_35 = input.LA(1);

                        s = -1;
                        if ( ((LA33_35>='\u0000' && LA33_35<='\uFFFF')) ) {s = 123;}

                        else s = 52;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA33_33 = input.LA(1);

                        s = -1;
                        if ( (LA33_33=='\'') ) {s = 119;}

                        else if ( ((LA33_33>='\u0000' && LA33_33<='\t')||(LA33_33>='\u000B' && LA33_33<='\f')||(LA33_33>='\u000E' && LA33_33<='&')||(LA33_33>='(' && LA33_33<='\uFFFF')) ) {s = 120;}

                        else s = 52;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA33_0 = input.LA(1);

                        s = -1;
                        if ( ((LA33_0>='\t' && LA33_0<='\n')||LA33_0=='\r'||LA33_0==' ') ) {s = 1;}

                        else if ( (LA33_0=='I'||LA33_0=='i') ) {s = 2;}

                        else if ( (LA33_0==':') ) {s = 3;}

                        else if ( (LA33_0=='D'||LA33_0=='d') ) {s = 4;}

                        else if ( (LA33_0=='F'||LA33_0=='f') ) {s = 5;}

                        else if ( (LA33_0=='S'||LA33_0=='s') ) {s = 6;}

                        else if ( (LA33_0=='L'||LA33_0=='l') ) {s = 7;}

                        else if ( (LA33_0=='M'||LA33_0=='m') ) {s = 8;}

                        else if ( (LA33_0=='P'||LA33_0=='p') ) {s = 9;}

                        else if ( (LA33_0=='O'||LA33_0=='o') ) {s = 10;}

                        else if ( (LA33_0=='a') ) {s = 11;}

                        else if ( (LA33_0=='T'||LA33_0=='t') ) {s = 12;}

                        else if ( (LA33_0=='N'||LA33_0=='n') ) {s = 13;}

                        else if ( (LA33_0=='V'||LA33_0=='v') ) {s = 14;}

                        else if ( (LA33_0=='E'||LA33_0=='e') ) {s = 15;}

                        else if ( (LA33_0=='B'||LA33_0=='b') ) {s = 16;}

                        else if ( (LA33_0=='R'||LA33_0=='r') ) {s = 17;}

                        else if ( (LA33_0=='C'||LA33_0=='c') ) {s = 18;}

                        else if ( (LA33_0=='W'||LA33_0=='w') ) {s = 19;}

                        else if ( (LA33_0=='G'||LA33_0=='g') ) {s = 20;}

                        else if ( (LA33_0=='U'||LA33_0=='u') ) {s = 21;}

                        else if ( (LA33_0=='H'||(LA33_0>='J' && LA33_0<='K')||LA33_0=='Q'||(LA33_0>='X' && LA33_0<='Z')||LA33_0=='h'||(LA33_0>='j' && LA33_0<='k')||LA33_0=='q'||(LA33_0>='x' && LA33_0<='z')||(LA33_0>='\u00C0' && LA33_0<='\u00D6')||(LA33_0>='\u00D8' && LA33_0<='\u00F6')||(LA33_0>='\u00F8' && LA33_0<='\u02FF')||(LA33_0>='\u0370' && LA33_0<='\u037D')||(LA33_0>='\u037F' && LA33_0<='\u1FFF')||(LA33_0>='\u200C' && LA33_0<='\u200D')||(LA33_0>='\u2070' && LA33_0<='\u218F')||(LA33_0>='\u2C00' && LA33_0<='\u2FEF')||(LA33_0>='\u3001' && LA33_0<='\uD7FF')||(LA33_0>='\uF900' && LA33_0<='\uFDCF')||(LA33_0>='\uFDF0' && LA33_0<='\uFFFD')) ) {s = 22;}

                        else if ( (LA33_0=='A') ) {s = 23;}

                        else if ( (LA33_0=='<') ) {s = 24;}

                        else if ( (LA33_0=='_') ) {s = 25;}

                        else if ( (LA33_0=='?') ) {s = 26;}

                        else if ( (LA33_0=='$') ) {s = 27;}

                        else if ( (LA33_0=='@') ) {s = 28;}

                        else if ( ((LA33_0>='0' && LA33_0<='9')) ) {s = 29;}

                        else if ( (LA33_0=='.') ) {s = 30;}

                        else if ( (LA33_0=='+') ) {s = 31;}

                        else if ( (LA33_0=='-') ) {s = 32;}

                        else if ( (LA33_0=='\'') ) {s = 33;}

                        else if ( (LA33_0=='\"') ) {s = 34;}

                        else if ( (LA33_0=='#') ) {s = 35;}

                        else if ( (LA33_0=='^') ) {s = 36;}

                        else if ( (LA33_0=='>') ) {s = 37;}

                        else if ( (LA33_0=='!') ) {s = 38;}

                        else if ( (LA33_0=='&') ) {s = 39;}

                        else if ( (LA33_0=='|') ) {s = 40;}

                        else if ( (LA33_0=='(') ) {s = 41;}

                        else if ( (LA33_0==')') ) {s = 42;}

                        else if ( (LA33_0=='{') ) {s = 43;}

                        else if ( (LA33_0=='}') ) {s = 44;}

                        else if ( (LA33_0=='[') ) {s = 45;}

                        else if ( (LA33_0==']') ) {s = 46;}

                        else if ( (LA33_0==';') ) {s = 47;}

                        else if ( (LA33_0=='*') ) {s = 48;}

                        else if ( (LA33_0==',') ) {s = 49;}

                        else if ( (LA33_0=='/') ) {s = 50;}

                        else if ( (LA33_0=='=') ) {s = 51;}

                        else if ( ((LA33_0>='\u0000' && LA33_0<='\b')||(LA33_0>='\u000B' && LA33_0<='\f')||(LA33_0>='\u000E' && LA33_0<='\u001F')||LA33_0=='%'||LA33_0=='\\'||LA33_0=='`'||(LA33_0>='~' && LA33_0<='\u00BF')||LA33_0=='\u00D7'||LA33_0=='\u00F7'||(LA33_0>='\u0300' && LA33_0<='\u036F')||LA33_0=='\u037E'||(LA33_0>='\u2000' && LA33_0<='\u200B')||(LA33_0>='\u200E' && LA33_0<='\u206F')||(LA33_0>='\u2190' && LA33_0<='\u2BFF')||(LA33_0>='\u2FF0' && LA33_0<='\u3000')||(LA33_0>='\uD800' && LA33_0<='\uF8FF')||(LA33_0>='\uFDD0' && LA33_0<='\uFDEF')||(LA33_0>='\uFFFE' && LA33_0<='\uFFFF')) ) {s = 52;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA33_34 = input.LA(1);

                        s = -1;
                        if ( (LA33_34=='\"') ) {s = 121;}

                        else if ( ((LA33_34>='\u0000' && LA33_34<='\t')||(LA33_34>='\u000B' && LA33_34<='\f')||(LA33_34>='\u000E' && LA33_34<='!')||(LA33_34>='#' && LA33_34<='\uFFFF')) ) {s = 122;}

                        else s = 52;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 33, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}