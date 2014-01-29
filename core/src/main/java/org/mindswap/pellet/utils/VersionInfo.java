/*
 * Created on Mar 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mindswap.pellet.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ronwalf
 * 
 * Automatic (from ant) version information for Pellet
 */
public class VersionInfo {
    private Properties versionProperties = null;

    private static String UNKNOWN = "(unknown)";

    public VersionInfo() {
        versionProperties = new Properties();
        // System.out.print(VersionInfo.class.getResource(""));

        InputStream vstream = VersionInfo.class
            .getResourceAsStream( "/org/mindswap/pellet/version.properties" );
        if( vstream != null ) {
            try {
                versionProperties.load( vstream );
            }
            catch( IOException e ) {
                System.err.println( "Could not load version properties:" );
                e.printStackTrace();
            } 
            finally {
            	try {
					vstream.close();
				} catch (IOException e) {
					System.err.println( "Could not close version properties:" );
	                e.printStackTrace();
				}
            }
        }
    }
    
    public static final VersionInfo getInstance() {
    	return new VersionInfo();
    }

    public String getVersionString() {
        return versionProperties.getProperty( "org.mindswap.pellet.version", "(unreleased)" );
    }

    public String getReleaseDate() {
        return versionProperties.getProperty( "org.mindswap.pellet.releaseDate", UNKNOWN );
    }
    
    public String toString() {
		return "Version: " + getVersionString()  + " Released: " + getReleaseDate();
    }
}
