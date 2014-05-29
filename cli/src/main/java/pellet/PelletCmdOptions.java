// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package pellet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title: PelletCmdOptions
 * </p>
 * <p>
 * Description: Essentially a set of PelletCmdOption
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class PelletCmdOptions {

	private Map<String, PelletCmdOption>	options;
	private Map<String, PelletCmdOption>	shortOptions;
	private Set<PelletCmdOption>			mandatory;

	public PelletCmdOptions() {
		options = new LinkedHashMap<String, PelletCmdOption>();
		shortOptions = new HashMap<String, PelletCmdOption>();
		mandatory = new HashSet<PelletCmdOption>();
	}

	public void add(PelletCmdOption option) {
		String shortOption = option.getShortOption();
		String longOption = option.getLongOption();

		if( options.containsKey( longOption ) )
			throw new PelletCmdException( "Duplicate long option for command: " + longOption );
		else if( shortOption != null && shortOptions.containsKey( shortOption ) )
			throw new PelletCmdException( "Duplicate short option for command: " + shortOption );

		shortOptions.put( shortOption, option );
		options.put( longOption, option );

		if( option.isMandatory() )
			mandatory.add( option );
	}

	public PelletCmdOption getOption(String key) {
		// If key is short option then this matches
		PelletCmdOption option = shortOptions.get( key );

		// Else, key is long option, retrieve its short option
		if( option == null )
			option = options.get( key );

		return option;
	}

	public Set<PelletCmdOption> getMandatoryOptions() {
		return mandatory;
	}

	public Collection<PelletCmdOption> getOptions() {
		return options.values();
	}
}
