// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class FileUtils {
    public static boolean exists( String file ) {
        return new File( file ).exists();
    }
    
    public static String readURL(URL fileURL) throws IOException {
		return readAll(new InputStreamReader(fileURL.openStream()));
	}
		
	public static String readFile(File file) throws FileNotFoundException, IOException {
		return readAll(new FileReader(file));
	}
	
	public static String readFile(String fileName) throws FileNotFoundException, IOException {
		return readAll(new FileReader(fileName));
	}
	
	public static String readAll(Reader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();
				
		BufferedReader in = new BufferedReader(reader);
		int ch;
		while ((ch = in.read()) > -1) {
			buffer.append((char)ch);
		}
		in.close();

		return buffer.toString();
	}

    public static String toURI(String fileName) {
    	if ( com.hp.hpl.jena.util.FileUtils.isURI( fileName ) )
    		return fileName;
    
    	File localFile = new File(fileName);
    	if (!localFile.exists())
    		throw new RuntimeException(new FileNotFoundException(localFile.getAbsolutePath()));
    
    	try {
    		return localFile.toURI().toURL().toExternalForm();
    	} catch (MalformedURLException e) {
    		throw new RuntimeException(fileName + " is not a valid URI");
    	}
    }

	/**
	 * <p>
	 * Creates a collection of URIs from a given regex list. The given list can
	 * contain either absolute (local or remote) URIs or a Java regex expression
	 * for a local path. If a regex is given all the files whose name matches
	 * the regex will be added to the resulting list.
	 * </p>
	 * <p>
	 * The regular expressions supported by this function are Java regular
	 * expressions. If we want to get the URIS for all the files in a directory
	 * we need to pass <code>/path/to/dir/.*</code>
	 * </p>
	 * 
	 * @param fileNameRegexList
	 *            list of regular expressions for fiel URIs
	 * @return list of file URIs matching the given regular expressions
	 */
	public static Collection<String> getFileURIsFromRegex(String... fileNameRegexList) {
		Collection<String> uris = new ArrayList<String>();
	
		for( String fileNameRegex : fileNameRegexList ) {
			File file = new File( fileNameRegex );
			File dir = file.getParentFile();
	
			if( dir != null && dir.exists() ) {
				final String filter = file.getName();
	
				File[] files = dir.listFiles( new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return dir != null && name.matches( filter );
					}
				} );
						
				if( files.length == 0 )
					throw new RuntimeException("File not found: " + fileNameRegex );
				
				Arrays.sort( files, AlphaNumericComparator.CASE_INSENSITIVE );
	
				for( File f : files ) {
					uris.add( f.toURI().toString() );
				}
			}
			else {
				if( file.exists() )
					uris.add( file.toURI().toString() );
				else if( URI.create( fileNameRegex ) == null )
					throw new RuntimeException( new FileNotFoundException( fileNameRegex ) );
				else
					uris.add( fileNameRegex );
			}
		}
	
		return uris;
	}	
	
	/**
	 * Creates a collection of URIs from a given list of files.
	 * 
	 * @param fileNameList the list of files
	 * @return a list of URIs
	 */
	public static Collection<String> getFileURIs(String... fileNameList) {
		Collection<String> uris = new ArrayList<String>();

		for( String fileName : fileNameList ) {
			uris.add( getFileURI( fileName ) );
		}

		return uris;
	}	
	
	/**
	 * Creates a URI from a given file name.
	 * 
	 * @param fileNameList the list of files
	 * @return a string representing the file URI
	 */
	public static String getFileURI(String fileName) {
		File file = new File( fileName );
		File dir = file.getParentFile();

		if( file.exists() ) {
			return file.toURI().toString();
		}
		else if( dir != null && dir.exists() ) {
			throw new RuntimeException( new FileNotFoundException( fileName ) );
		}
		else if( URI.create( fileName ) == null ) {
			throw new RuntimeException( new FileNotFoundException( fileName ) );
		}
		else {
			return fileName;
		}
	}
}
