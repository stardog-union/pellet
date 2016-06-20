/*
 * Copyright (c) 2002-2007, CWI and INRIA
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package openllet.aterm.pure.binary.test;

import java.nio.ByteBuffer;
import openllet.aterm.ATerm;
import openllet.aterm.VisitFailure;
import openllet.aterm.pure.PureFactory;
import openllet.aterm.pure.binary.BinaryReader;
import openllet.aterm.pure.binary.BinaryWriter;
import org.junit.Before;
import org.junit.Test;

public class TestBinaryFormat{
	private PureFactory pureFactory = null;
	
	public TestBinaryFormat(){
		super();
		
		setUp();
	}
	
	@Before
	public void setUp(){
		pureFactory = new PureFactory();
	}
	
	@Test
	public void testWriting() throws Exception{
		// A term
		ATerm input = pureFactory.parse("line(box(rect(2), square(4, 3)), circle(6))");
		byte[] expectedResult = new byte[]{1, 2, 4, 108, 105, 110, 101, 1, 2, 3, 98, 111, 120, 1, 1, 4, 114, 101, 99, 116, 2, 2, 1, 2, 6, 115, 113, 117, 97, 114, 101, 2, 4, 2, 3, 1, 1, 6, 99, 105, 114, 99, 108, 101, 2, 6};
		write(input, expectedResult);
		
		// A openllet.shared.hash term
		input = pureFactory.parse("line(line(), line())");
		expectedResult = new byte[]{1, 2, 4, 108, 105, 110, 101, 1, 0, 4, 108, 105, 110, 101, -128, 1};
		write(input, expectedResult);
		
		// A term with signature sharing
		input = pureFactory.parse("line(line(0), line(1))");
		expectedResult = new byte[]{1, 2, 4, 108, 105, 110, 101, 1, 1, 4, 108, 105, 110, 101, 2, 0, 65, 1, 2, 1};
		write(input, expectedResult);
		
		// A term with annotations
		input = pureFactory.parse("line(10, 11{childAnno}){termAnno{annoOfAnno}}");
		expectedResult = new byte[]{17, 2, 4, 108, 105, 110, 101, 2, 10, 18, 11, 4, 1, 1, 0, 9, 99, 104, 105, 108, 100, 65, 110, 110, 111, 4, 1, 17, 0, 8, 116, 101, 114, 109, 65, 110, 110, 111, 4, 1, 1, 0, 10, 97, 110, 110, 111, 79, 102, 65, 110, 110, 111};
		write(input, expectedResult);
		
		// Signed integer
		input = pureFactory.parse("integer(-1)");
		expectedResult = new byte[]{1, 1, 7, 105, 110, 116, 101, 103, 101, 114, 2, -1, -1, -1, -1, 15};
		write(input, expectedResult);
		
		// Signed double
		input = pureFactory.parse("real(-1.0)");
		expectedResult = new byte[]{1, 1, 4, 114, 101, 97, 108, 3, 0, 0, 0, 0, 0, 0, -16, -65};
		write(input, expectedResult);
	}
	
	public void write(ATerm input, byte[] expectedResult) throws Exception{
		ByteBuffer buffer = ByteBuffer.allocate(expectedResult.length + 10);
		BinaryWriter bw = new BinaryWriter(input);
		bw.serialize(buffer);
		byte[] result = new byte[buffer.limit()];
		buffer.get(result);
		
		int expectedResultLength = expectedResult.length;
		int resultLength = result.length;
		if(expectedResultLength != resultLength){
			log("The number of written bytes didn't match the number of _expected bytes. Was: "+resultLength+", _expected: "+expectedResultLength);
			return;
		}
		for(int i = 0; i < resultLength; i++){
			if(result[i] != expectedResult[i]){
				log("Written data didn't match the _expected data. Index: "+i+", was "+result[i]+", _expected "+expectedResult[i]);
				return;
			}
		}
		
		log("Writing OK for: "+input);
	}
	
    @Test
	public void testReading(){
		// A term
		byte[] input = new byte[]{1, 2, 4, 108, 105, 110, 101, 1, 2, 3, 98, 111, 120, 1, 1, 4, 114, 101, 99, 116, 2, 2, 1, 2, 6, 115, 113, 117, 97, 114, 101, 2, 4, 2, 3, 1, 1, 6, 99, 105, 114, 99, 108, 101, 2, 6};
		ATerm expectedResult = pureFactory.parse("line(box(rect(2), square(4, 3)), circle(6))");
		read(input, expectedResult);
		
		// A openllet.shared.hash term
		input = new byte[]{1, 2, 4, 108, 105, 110, 101, 1, 0, 4, 108, 105, 110, 101, -128, 1};
		expectedResult = pureFactory.parse("line(line(), line())");
		read(input, expectedResult);
		
		// A term with signature sharing
		input = new byte[]{1, 2, 4, 108, 105, 110, 101, 1, 1, 4, 108, 105, 110, 101, 2, 0, 65, 1, 2, 1};
		expectedResult = pureFactory.parse("line(line(0), line(1))");
		read(input, expectedResult);
		
		// A term with annotations
		input = new byte[]{17, 2, 4, 108, 105, 110, 101, 2, 10, 18, 11, 4, 1, 1, 0, 9, 99, 104, 105, 108, 100, 65, 110, 110, 111, 4, 1, 17, 0, 8, 116, 101, 114, 109, 65, 110, 110, 111, 4, 1, 1, 0, 10, 97, 110, 110, 111, 79, 102, 65, 110, 110, 111};
		expectedResult = pureFactory.parse("line(10, 11{childAnno}){termAnno{annoOfAnno}}");
		read(input, expectedResult);
		
		// Signed integer
		input = new byte[]{1, 1, 7, 105, 110, 116, 101, 103, 101, 114, 2, -1, -1, -1, -1, 15};
		expectedResult = pureFactory.parse("integer(-1)");
		read(input, expectedResult);
		
		// Signed double
		input = new byte[]{1, 1, 4, 114, 101, 97, 108, 3, 0, 0, 0, 0, 0, 0, -16, -65};
		expectedResult = pureFactory.parse("real(-1.0)");
		read(input, expectedResult);
	}
	
	public void read(byte[] input, ATerm expectedResult){
		ByteBuffer buffer = ByteBuffer.allocate(input.length);
		buffer.put(input);
		buffer.flip();
		
		BinaryReader binaryReader = new BinaryReader(pureFactory);
		binaryReader.deserialize(buffer);
		ATerm result = binaryReader.getRoot();
		
		if(result != expectedResult){
			log("The result didn't match the _expected result.");
			/*log("Was: "+result+", _expected: "+expectedResult);*/
			return;
		}
		
		log("Reading OK for: "+expectedResult);
	}
	
    @Test
	public void testChunkification() throws VisitFailure{
		ATerm in = makeBigDummyTerm(2500);
		ByteBuffer buffer = ByteBuffer.allocate(1000);
		BinaryWriter bw = new BinaryWriter(in);
		BinaryReader binaryReader = new BinaryReader(pureFactory);
		
		while(!binaryReader.isDone()){
			buffer.clear();
			bw.serialize(buffer);
			binaryReader.deserialize(buffer);
		}
		
		ATerm result = binaryReader.getRoot();
		
		if(result == in) log("Chunkification OK");
		else log("Chunkification FAILED");
	}
	
	private ATerm makeBigDummyTerm(int x){
		byte[] b = new byte[x];
		
		for(int i = 2; i < b.length - 1; i++){
			b[i] = 'x';
		}
		b[0] = 'a';
		b[1] = '(';
		b[b.length - 1] = ')';
		
		String s = new String(b);
		
		return pureFactory.parse(s);
	}
	
	private static void log(String message){
		System.out.println(message);
	}
	
	public static void main(String[] args) throws Exception{
		TestBinaryFormat tbf = new TestBinaryFormat();
		tbf.testWriting();
		log("");
		tbf.testReading();
		log("");
		tbf.testChunkification();
	}
}

