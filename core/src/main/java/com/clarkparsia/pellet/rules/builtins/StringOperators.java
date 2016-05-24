// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.builtins;

import static com.clarkparsia.pellet.rules.builtins.ComparisonTesters.expectedIfEquals;

import aterm.ATermAppl;
import com.clarkparsia.pellet.rules.BindingHelper;
import com.clarkparsia.pellet.rules.VariableBinding;
import com.clarkparsia.pellet.rules.VariableUtils;
import com.clarkparsia.pellet.rules.model.AtomDObject;
import com.clarkparsia.pellet.rules.model.AtomVariable;
import com.clarkparsia.pellet.rules.model.BuiltInAtom;
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
public class StringOperators
{

	private static class Contains extends BinaryTester
	{
		//@Override
		@Override
		protected boolean test(final Literal l1, final Literal l2)
		{
			final ATermAppl l1name = l1.getTerm();
			final ATermAppl l2name = l2.getTerm();

			final String l1str = ATermUtils.getLiteralValue(l1name);
			final String l2str = ATermUtils.getLiteralValue(l2name);

			return l1str.contains(l2str);
		}
	}

	private static class ContainsIgnoreCase extends BinaryTester
	{
		//@Override
		@Override
		protected boolean test(final Literal l1, final Literal l2)
		{
			final ATermAppl l1name = l1.getTerm();
			final ATermAppl l2name = l2.getTerm();

			final String l1str = ATermUtils.getLiteralValue(l1name).toLowerCase();
			final String l2str = ATermUtils.getLiteralValue(l2name).toLowerCase();

			return l1str.contains(l2str);
		}
	}

	private static class EndsWith extends BinaryTester
	{
		//@Override
		@Override
		protected boolean test(final Literal l1, final Literal l2)
		{
			final ATermAppl l1name = l1.getTerm();
			final ATermAppl l2name = l2.getTerm();

			final String l1str = ATermUtils.getLiteralValue(l1name);
			final String l2str = ATermUtils.getLiteralValue(l2name);

			return l1str.endsWith(l2str);
		}
	}

	private static class LowerCase implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length != 1)
				return null;

			return args[0].toLowerCase();
		}

	}

	private static class Matches extends BinaryTester
	{
		//@Override
		@Override
		protected boolean test(final Literal l1, final Literal l2)
		{
			final ATermAppl l1name = l1.getTerm();
			final ATermAppl l2name = l2.getTerm();

			final String l1str = ATermUtils.getLiteralValue(l1name);
			final String l2str = ATermUtils.getLiteralValue(l2name);

			boolean result = false;
			try
			{
				result = Pattern.matches(l2str, l1str);

			}
			catch (final PatternSyntaxException e)
			{
				ABox._logger.info("Bad regex from builtin rule: " + l2);
			}
			return result;
		}
	}

	private static class NormalizeSpace implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length != 1)
				return null;
			return args[0].trim().replaceAll("\\s+", " ");
		}

	}

	private static class Replace implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length != 3)
				return null;

			final String from = args[1];
			final String to = args[2];

			return args[0].replace(from, to);
		}

	}

	private static class StartsWith extends BinaryTester
	{
		//@Override
		@Override
		protected boolean test(final Literal l1, final Literal l2)
		{
			final ATermAppl l1name = l1.getTerm();
			final ATermAppl l2name = l2.getTerm();

			final String l1str = ATermUtils.getLiteralValue(l1name);
			final String l2str = ATermUtils.getLiteralValue(l2name);

			return l1str.startsWith(l2str);
		}
	}

	private static class StringConcat implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			final StringBuffer resultBuffer = new StringBuffer();
			for (final String arg : args)
				resultBuffer.append(arg);
			return resultBuffer.toString();
		}

	}

	private static class StringEqualIgnoreCase extends BinaryTester
	{
		//@Override
		@Override
		protected boolean test(final Literal l1, final Literal l2)
		{
			final ATermAppl l1name = l1.getName();
			final ATermAppl l2name = l2.getName();

			final String l1str = ATermUtils.getLiteralValue(l1name);
			final String l2str = ATermUtils.getLiteralValue(l2name);

			return l1str.equalsIgnoreCase(l2str);
		}
	}

	private static class StringLength implements Function
	{

		@Override
		public Literal apply(final ABox abox, final Literal expected, final Literal... args)
		{
			if (args.length != 1)
				return null;

			final String val = ATermUtils.getLiteralValue(args[0].getTerm());
			final String length = Integer.toString(val.length());
			final Literal result = abox.addLiteral(ATermUtils.makeTypedLiteral(length, Namespaces.XSD + "integer"));

			return expectedIfEquals(expected, result);
		}

	}

	private static class SubString implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length < 2 || args.length > 3)
				return null;
			long beginIndex = 0;
			long endIndex = args[0].length();
			try
			{
				final Double beginDouble = new Double(args[1]);
				final long begin = beginDouble.isNaN() || beginDouble > Integer.MAX_VALUE ? Integer.MAX_VALUE : Math.round(beginDouble);
				long count = Integer.MAX_VALUE;
				if (args.length == 3)
				{
					final Double countDouble = new Double(args[2]);
					count = countDouble.isNaN() || countDouble < 0 ? 0 : Math.round(countDouble);
				}
				beginIndex = Math.min(args[0].length(), Math.max(0, begin - 1));
				endIndex = Math.min(args[0].length(), Math.max(beginIndex, begin + count - 1));
			}
			catch (final NumberFormatException e)
			{
				return "";
			}

			if (beginIndex > Integer.MAX_VALUE)
				beginIndex = Integer.MAX_VALUE;
			if (endIndex > Integer.MAX_VALUE)
				endIndex = Integer.MAX_VALUE;

			return args[0].substring((int) beginIndex, (int) endIndex);
		}
	}

	private static class SubStringAfter implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length != 2)
				return null;

			if (args[1].equals(""))
				return "";

			final int index = args[0].indexOf(args[1]);
			if (index < 0)
				return "";

			return args[0].substring(index + args[1].length());
		}

	}

	private static class SubStringBefore implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length != 2)
				return null;

			if (args[1].equals(""))
				return "";

			final int index = args[0].indexOf(args[1]);
			if (index < 0)
				return "";

			return args[0].substring(0, index);
		}

	}

	private static class Tokenize implements BuiltIn
	{

		private static class TokenizeBindingHelper implements BindingHelper
		{

			private final BuiltInAtom _atom;
			private AtomDObject _head;
			private String _match;
			private Iterator<String> _tokens;

			public TokenizeBindingHelper(final BuiltInAtom atom)
			{
				this._atom = atom;
				_head = null;
				_match = null;
				_tokens = null;
			}

			//@Override
			@Override
			public Collection<? extends AtomVariable> getBindableVars(final Collection<AtomVariable> bound)
			{
				_head = null;
				for (final AtomDObject obj : _atom.getAllArguments())
					if (_head == null)
					{
						_head = obj;
						// Can only bind first argument to tokenize
						if (!VariableUtils.isVariable(_head))
							return Collections.emptySet();
					}
					else
						// Cannot bind a variable that occurs in multiple places.
						if (_head.equals(obj))
							return Collections.emptySet();
				if (_head == null)
					return Collections.emptySet();
				return Collections.singleton((AtomVariable) _head);
			}

			//@Override
			@Override
			public Collection<? extends AtomVariable> getPrerequisiteVars(final Collection<AtomVariable> bound)
			{
				final Collection<AtomVariable> vars = VariableUtils.getVars(_atom);
				vars.removeAll(getBindableVars(bound));
				return vars;
			}

			//@Override
			@Override
			public void rebind(final VariableBinding newBinding)
			{
				if (_atom.getAllArguments().size() < 3)
				{
					_tokens = null;
					return;
				}

				final Literal matchLit = newBinding.get(_atom.getAllArguments().get(0));
				if (matchLit != null)
					_match = ATermUtils.getLiteralValue(matchLit.getTerm());

				final String splittingString = ATermUtils.getLiteralValue(newBinding.get(_atom.getAllArguments().get(1)).getTerm());

				final String splittingPattern = ATermUtils.getLiteralValue(newBinding.get(_atom.getAllArguments().get(2)).getTerm());

				final String[] splits = splittingString.split(splittingPattern);
				_tokens = Arrays.asList(splits).iterator();
				if (_match != null)
					while (_tokens.hasNext())
					{
						final String token = _tokens.next();
						if (token.equals(_match))
						{
							_tokens = Collections.singleton(token).iterator();
							break;
						}
					}

			}

			//@Override
			@Override
			public boolean selectNextBinding()
			{
				if (_tokens != null && _tokens.hasNext())
				{
					_match = _tokens.next();
					return true;
				}
				return false;
			}

			//@Override
			@Override
			public void setCurrentBinding(final VariableBinding currentBinding)
			{
				if (VariableUtils.isVariable(_head))
				{
					final ATermAppl resultTerm = ATermUtils.makePlainLiteral(_match);
					final Literal resultLit = currentBinding.getABox().addLiteral(resultTerm);
					currentBinding.set(_head, resultLit);
				}
			}

		}

		public Tokenize()
		{
		}

		@Override
		public BindingHelper createHelper(final BuiltInAtom atom)
		{
			return new TokenizeBindingHelper(atom);
		}

		@Override
		public boolean apply(final ABox abox, final Literal[] args)
		{
			return false;
		}
	}

	private static class Translate implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length != 3)
				return null;

			final String src = args[1];
			final String dst = args[2];

			// Possibly not the most efficient solution.
			final StringBuffer result = new StringBuffer();
			for (final char c : args[0].toCharArray())
			{
				final int replPos = src.indexOf(c);
				if (replPos < 0)
					result.append(c);
				else
					if (replPos < dst.length())
						result.append(dst.charAt(replPos));
			}

			return result.toString();
		}

	}

	private static class UpperCase implements StringToStringFunction
	{

		@Override
		public String apply(final String... args)
		{
			if (args.length != 1)
				return null;
			return args[0].toUpperCase();
		}

	}

	public final static Tester contains = new Contains();
	public final static Tester containsIgnoreCase = new ContainsIgnoreCase();
	public final static Tester endsWith = new EndsWith();
	public final static Function lowerCase = new StringFunctionAdapter(new LowerCase());
	public final static Tester matches = new Matches();
	public final static Function normalizeSpace = new StringFunctionAdapter(new NormalizeSpace());
	public final static Function replace = new StringFunctionAdapter(new Replace());
	public final static Tester startsWith = new StartsWith();
	public final static Function stringConcat = new StringFunctionAdapter(new StringConcat());
	public final static Tester stringEqualIgnoreCase = new StringEqualIgnoreCase();
	public final static Function stringLength = new StringLength();
	public final static Function substring = new StringFunctionAdapter(new SubString());
	public final static Function substringAfter = new StringFunctionAdapter(new SubStringAfter());
	public final static Function substringBefore = new StringFunctionAdapter(new SubStringBefore());
	public final static BuiltIn tokenize = new Tokenize();
	public final static Function translate = new StringFunctionAdapter(new Translate());
	public final static Function upperCase = new StringFunctionAdapter(new UpperCase());

}
