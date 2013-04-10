// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static com.clarkparsia.pellet.rules.builtins.ComparisonTesters.expectedIfEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.mindswap.pellet.ABox;
import org.mindswap.pellet.Literal;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.Namespaces;

import aterm.ATermAppl;

import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;

/**
 * <p>
 * Title: String Operators
 * </p>
 * <p>
 * Description: Implementations for each of the SWRL string operators.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Ron Alford
 */ 
public class StringOperators {

	private static class Contains extends BinaryTester {
		//@Override
		protected boolean test(Literal l1, Literal l2) {
			ATermAppl l1name = l1.getTerm();
			ATermAppl l2name = l2.getTerm();
			
			String l1str = ATermUtils.getLiteralValue( l1name );
			String l2str = ATermUtils.getLiteralValue( l2name );
			
			return l1str.contains( l2str );
		}
	}
	
	private static class ContainsIgnoreCase extends BinaryTester {
		//@Override
		protected boolean test(Literal l1, Literal l2) {
			ATermAppl l1name = l1.getTerm();
			ATermAppl l2name = l2.getTerm();
			
			String l1str = ATermUtils.getLiteralValue( l1name ).toLowerCase();
			String l2str = ATermUtils.getLiteralValue( l2name ).toLowerCase();
			
			return l1str.contains( l2str );
		}
	}
	
	private static class EndsWith extends BinaryTester {
		//@Override
		protected boolean test(Literal l1, Literal l2) {
			ATermAppl l1name = l1.getTerm();
			ATermAppl l2name = l2.getTerm();
			
			String l1str = ATermUtils.getLiteralValue( l1name );
			String l2str = ATermUtils.getLiteralValue( l2name );
			
			return l1str.endsWith( l2str );
		}
	}
	
	private static class LowerCase implements StringToStringFunction {

		public String apply(String... args) {
			if ( args.length != 1 )
				return null;
			
			return args[0].toLowerCase();
		}
		
	}
	
	private static class Matches extends BinaryTester {
		//@Override
		protected boolean test(Literal l1, Literal l2) {
			ATermAppl l1name = l1.getTerm();
			ATermAppl l2name = l2.getTerm();
			
			String l1str = ATermUtils.getLiteralValue( l1name );
			String l2str = ATermUtils.getLiteralValue( l2name );
			
			boolean result = false;
			try {
				result = Pattern.matches( l2str, l1str );
				
			} catch (PatternSyntaxException e) {
				ABox.log.info( "Bad regex from builtin rule: " + l2 );
			}
			return result;
		}
	}
	
	private static class NormalizeSpace implements StringToStringFunction {

		public String apply(String... args) {
			if ( args.length != 1 ) {
				return null;
			}
			return args[0].trim().replaceAll( "\\s+", " " );
		}
		
	}
	
	private static class Replace implements StringToStringFunction {

		public String apply(String... args) {
			if ( args.length != 3 ) {
				return null;
			}
			
			String from = args[1];
			String to = args[2];
			
			return args[0].replace( from, to );
		}
		
	}
	
	private static class StartsWith extends BinaryTester {
		//@Override
		protected boolean test(Literal l1, Literal l2) {
			ATermAppl l1name = l1.getTerm();
			ATermAppl l2name = l2.getTerm();
			
			String l1str = ATermUtils.getLiteralValue( l1name );
			String l2str = ATermUtils.getLiteralValue( l2name );
			
			return l1str.startsWith( l2str );
		}
	}
	
	private static class StringConcat implements StringToStringFunction {

		public String apply(String... args) {
			StringBuffer resultBuffer = new StringBuffer();
			for ( String arg : args ) {
				resultBuffer.append( arg );
			}
			return resultBuffer.toString();
		}
		
	}
	
	private static class StringEqualIgnoreCase extends BinaryTester {
		//@Override
		protected boolean test(Literal l1, Literal l2) {
			ATermAppl l1name = l1.getName();
			ATermAppl l2name = l2.getName();
			
			String l1str = ATermUtils.getLiteralValue( l1name );
			String l2str = ATermUtils.getLiteralValue( l2name );
			
			return l1str.equalsIgnoreCase( l2str );
		}
	}
	
	private static class StringLength implements Function {

		public Literal apply(ABox abox, Literal expected, Literal... args) {
			if ( args.length != 1 )
				return null;
			
			String val = ATermUtils.getLiteralValue( args[0].getTerm() );
			String length = Integer.toString( val.length() );
			Literal result = abox.addLiteral( ATermUtils.makeTypedLiteral( length, Namespaces.XSD + "integer" ) );
			
			return expectedIfEquals( expected, result );
		}
		
	}
	
	private static class SubString implements StringToStringFunction {
		
		public String apply(String... args) {
			if ( args.length < 2 || args.length > 3 )
				return null;
			long beginIndex = 0;
			long endIndex = args[0].length();
			try {
				Double beginDouble = new Double( args[1] );
				long begin = beginDouble.isNaN() || beginDouble > Integer.MAX_VALUE ? 
					Integer.MAX_VALUE : Math.round( beginDouble );
				long count = Integer.MAX_VALUE;
				if ( args.length == 3 ) {
					Double countDouble = new Double( args[2] );
					count = countDouble.isNaN() || countDouble < 0 ?
						0 : Math.round( countDouble );
				}
				beginIndex = Math.min( args[0].length(),
						Math.max( 0, begin - 1));
				endIndex = Math.min( args[0].length(),
						Math.max( beginIndex,  begin + count - 1));
			} catch ( NumberFormatException e ) {
				return "";
			}
			
			if ( beginIndex > Integer.MAX_VALUE )
				beginIndex = Integer.MAX_VALUE;
			if ( endIndex > Integer.MAX_VALUE )
				endIndex = Integer.MAX_VALUE;
			
			return args[0].substring( (int) beginIndex, (int) endIndex );
		}
	}
	
	private static class SubStringAfter implements StringToStringFunction {

		public String apply(String... args) {
			if ( args.length != 2 )
				return null;
			
			if ( args[1].equals( "" ) ) 
				return "";
			
			int index = args[0].indexOf( args[1] );
			if ( index < 0 )
				return "";
			
			return args[0].substring( index + args[1].length() );
		}
		
	}
	
	private static class SubStringBefore implements StringToStringFunction {

		public String apply(String... args) {
			if ( args.length != 2 )
				return null;
			
			if ( args[1].equals( "" ) ) 
				return "";
			
			int index = args[0].indexOf( args[1] );
			if ( index < 0 )
				return "";
			
			return args[0].substring( 0, index );
		}
		
	}
	
	private static class Tokenize implements BuiltIn {

		private static class TokenizeBindingHelper implements BindingHelper {

			private BuiltInAtom atom;
			private AtomDObject head;
			private String match;
			private Iterator<String> tokens;
			
			public TokenizeBindingHelper(BuiltInAtom atom) {
				this.atom = atom;
				head = null;
				match = null;
				tokens = null;
			}
			
			//@Override
			public Collection<? extends AtomVariable> getBindableVars(Collection<AtomVariable> bound) {
				head = null;
				for ( AtomDObject obj : atom.getAllArguments() ) {
					if (head == null) {
						head = obj;
						// Can only bind first argument to tokenize
						if ( !VariableUtils.isVariable( head ) )
							return Collections.emptySet();
					} else {
						// Cannot bind a variable that occurs in multiple places.
						if ( head.equals( obj ) )
							return Collections.emptySet();
					}
				}
				if ( head == null )
					return Collections.emptySet();
				return Collections.singleton( (AtomVariable) head );
			}

			//@Override
			public Collection<? extends AtomVariable> getPrerequisiteVars(
					Collection<AtomVariable> bound) {
				Collection<AtomVariable> vars = VariableUtils.getVars( atom );
				vars.removeAll( getBindableVars( bound ) );
				return vars;
			}

			//@Override
			public void rebind(VariableBinding newBinding) {
				if ( atom.getAllArguments().size() < 3 ) {
					tokens = null;
					return;
				}
				
				Literal matchLit = newBinding.get( atom.getAllArguments().get( 0 ) );
				if ( matchLit != null ) {
					match = ATermUtils.getLiteralValue( matchLit.getTerm() );
				}
				
				String splittingString = ATermUtils.getLiteralValue(
						newBinding.get( atom.getAllArguments().get(1) ).getTerm() );
				
				String splittingPattern = ATermUtils.getLiteralValue( 
						newBinding.get( atom.getAllArguments().get(2) ).getTerm() );
				
				String[] splits = splittingString.split( splittingPattern );
				tokens = Arrays.asList( splits ).iterator();
				if ( match != null ) {
					while ( tokens.hasNext() ) {
						String token = tokens.next();
						if ( token.equals( match ) ) {
							tokens = Collections.singleton( token ).iterator();
							break;
						}
					}
				}
				
			}

			//@Override
			public boolean selectNextBinding() {
				if ( tokens != null && tokens.hasNext() ) {
					match = tokens.next();
					return true;
				}
				return false;
			}

			//@Override
			public void setCurrentBinding(VariableBinding currentBinding) {
				if ( VariableUtils.isVariable( head ) ) {
					ATermAppl resultTerm = ATermUtils.makePlainLiteral( match );
					Literal resultLit = currentBinding.getABox().addLiteral( resultTerm );
					currentBinding.set( head, resultLit );
				}
			}
			
		}
		
		public Tokenize() {}
		
		//@Override
		public BindingHelper createHelper(BuiltInAtom atom) {
			// TODO Auto-generated method stub
			return new TokenizeBindingHelper( atom );
		}

	}

	private static class Translate implements StringToStringFunction {

		public String apply(String... args) {
			if ( args.length != 3 )
				return null;
			
			String src = args[1];
			String dst = args[2];
			
			// Possibly not the most efficient solution.
			StringBuffer result = new StringBuffer();
			for ( char c : args[0].toCharArray() ) {
				int replPos = src.indexOf( c );
				if ( replPos < 0 ) {
					result.append( c );
				} else if ( replPos < dst.length() ) {
					result.append( dst.charAt( replPos ) );
				}
			}
			
			return result.toString();
		}
		
	}
	
	private static class UpperCase implements StringToStringFunction {
		
		public String apply(String... args) {
			if ( args.length != 1 )
				return null;
			return args[0].toUpperCase();
		}
		
	}
	
	public final static Tester contains = new Contains();
	public final static Tester containsIgnoreCase = new ContainsIgnoreCase();
	public final static Tester endsWith = new EndsWith();
	public final static Function lowerCase = new StringFunctionAdapter( new LowerCase() );
	public final static Tester matches = new Matches();
	public final static Function normalizeSpace = new StringFunctionAdapter( new NormalizeSpace() );
	public final static Function replace = new StringFunctionAdapter( new Replace() );
	public final static Tester startsWith = new StartsWith();
	public final static Function stringConcat = new StringFunctionAdapter( new StringConcat() );
	public final static Tester stringEqualIgnoreCase = new StringEqualIgnoreCase();
	public final static Function stringLength = new StringLength();
	public final static Function substring = new StringFunctionAdapter( new SubString() );
	public final static Function substringAfter = new StringFunctionAdapter( new SubStringAfter() );
	public final static Function substringBefore = new StringFunctionAdapter( new SubStringBefore() );
	public final static BuiltIn tokenize = new Tokenize();
	public final static Function translate = new StringFunctionAdapter( new Translate() );
	public final static Function upperCase = new StringFunctionAdapter( new UpperCase() );
	
	
}
