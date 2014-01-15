// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.test.utils;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.Role;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATermAppl;

public class TestUtils {

	
	static Random rand;
	
	static{
		//		get random number generator
		rand = new Random(System.currentTimeMillis());
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static ATermAppl selectRandomConcept(Individual ind){
		
		//get all classes
		Set types = ind.getTypes();		
		ATermAppl clazz = null;
		int MAX = 20;
		int count = 0;
		do{			
			count++;
			//get index for concept
			int index = rand.nextInt(types.size());
			
			//get the concept
			for(Iterator it = types.iterator(); it.hasNext();){			
				clazz = (ATermAppl)it.next();
			}
		}while(((clazz == ATermUtils.TOP) || (clazz == ATermUtils.BOTTOM)) && count < MAX);

		return clazz;
	}
	
	
	
	/**
	 * @param args
	 */
	public static ATermAppl selectRandomConcept(KnowledgeBase kb){
		
		//get all classes
		List classes = new ArrayList(kb.getTBox().getAllClasses());
		ATermAppl clazz = null;
		
		do{
			
			
			
			//get index for concept
			int index = rand.nextInt(classes.size());
			
			clazz = (ATermAppl)classes.get(index);
		}while((clazz == ATermUtils.TOP) || (clazz == ATermUtils.BOTTOM));

		return clazz;
	}


	
	/**
	 * @param args
	 */
	public static ATermAppl selectRandomObjectProperty(KnowledgeBase kb){
		
		//get all classes
		List roles = new ArrayList(kb.getRBox().getRoles());
		Role role = null;		
		do{
			
			//get index for concept
			int index = rand.nextInt(roles.size());
			
			role = (Role)roles.get(index);
			
		}while(!role.isObjectRole());

		
		
		return role.getName();
	}
	
	/**
	 * @param args
	 */
	public static ATermAppl selectRandomIndividual(KnowledgeBase kb){
		
		//get all ind
		List inds = new ArrayList(kb.getIndividuals());
		
		//get index for concept
		int index = rand.nextInt(inds.size());
		
		return (ATermAppl)inds.get(index);

	}
	
   public static InputStream copyResourceToFileStream(File testDir, String resource) throws Exception {
       return new FileInputStream(copyResourceToFile(testDir, resource));
    }
   
   public static String copyResourceToFile(File testDir, String resource) throws Exception {
        String filename = resource.substring(resource.lastIndexOf('/'));
        String directory = resource.substring(0, resource.lastIndexOf('/'));
        File nextDirectory = new File(testDir, directory);
        nextDirectory.mkdirs();
        File nextFile = new File(nextDirectory, filename);
        nextFile.createNewFile();
        
        InputStream inputStream = TestUtils.class.getResourceAsStream(resource);
        assertNotNull("Missing test resource: "+resource, inputStream);
        
        IOUtils.copy(inputStream, new FileOutputStream(nextFile));
        return nextFile.getAbsolutePath();
    }
}
