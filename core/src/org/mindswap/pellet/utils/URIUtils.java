/*
 * Created on May 6, 2004
 */
package org.mindswap.pellet.utils;

import java.net.URI;

/**
 * Utility functions for URI's.
 * 
 * 
 * @author Evren Sirin
 */
public class URIUtils {
	public static String getQName(String uri) {
	    return getFilePart(uri) + ":" + getLocalName(uri);
	}
	
	public static String getFilePart(String uri) {
		try {
			URI u = URI.create(uri);
			uri = u.getPath();
		}
		catch (Exception e) {
			return "http://invalid/uri/";
		}
	
		//if(uri.length() == 0) return
		int begin = uri.lastIndexOf("/");
		int end = uri.lastIndexOf(".");
	
		if (end == -1 || begin > end)
			end = uri.length();
	
		return uri.substring(begin + 1, end);
	}

	/**
	 * Return the local name of a URI. This function is not equiavlent to
	 * URI.getFragment() because it tries to handle handle slashy URI's 
	 * such as the ones found in Dublin Core. It is equiavalent to
	 * getLocalName(uri.toString()).
	 * 
	 * @param uri
	 * @return
	 */
	public static String getLocalName(URI uri) {
		return getLocalName(uri.toString());
	}
	
	/**
	 * Return the local name of a URI string. This naive implementation splits
	 * the URI from the position of a '#' character or the last occurunce of
	 * '/' character. If neither of these characters are found, the parameter
	 * itself is returned. 
	 * 
	 * @param uri
	 * @return
	 */
	public static String getLocalName(String uri) {
		int index = splitPos(uri);
		
		if(index == -1) return uri;
		
		return uri.substring(index + 1);
	}

	public static String getNameSpace(URI uri) {
		return getNameSpace(uri.toString());
	}
	
	public static String getNameSpace(String uri) {
		int index = uri.indexOf("#");
		
		if(index == -1) return uri;
		
		return uri.substring(0, index);
	}

	private static int splitPos(String uri) {
		int pos = uri.indexOf("#");
		
		if(pos == -1) pos = uri.lastIndexOf("/");
		
		return pos; 
	}
}
