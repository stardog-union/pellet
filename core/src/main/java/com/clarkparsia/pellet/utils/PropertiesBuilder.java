// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.utils;

import java.util.Properties;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: Convenience class to build Properties objects.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class PropertiesBuilder {
	private Properties properties = new Properties();

	public PropertiesBuilder() {		
	}

	public PropertiesBuilder(Properties defaults) {	
		this.properties = new Properties( defaults );
	}
	
	public PropertiesBuilder set(String key, String value) {
		properties.setProperty( key, value );
		return this;
	}
	
	public Properties build() {
		return properties;
	}	
	
	public static Properties singleton(String key, String value) {
		return new PropertiesBuilder().set( key, value ).build();
	}
}
