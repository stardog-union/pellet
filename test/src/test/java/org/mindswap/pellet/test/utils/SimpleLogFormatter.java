package org.mindswap.pellet.test.utils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class SimpleLogFormatter extends Formatter {
	private final static DateFormat TIME_FORMAT = new SimpleDateFormat( "hh:mm:ss.SSS" );
	
	@Override
	public String format(LogRecord record) {
		String stack = getStackTrace( record.getThrown() );
		
		return "[" + record.getLevel() + " " 
//				+ className(record.getSourceClassName()) + "."
//				+ record.getSourceMethodName() + " - " 
				+  TIME_FORMAT.format( record.getMillis() ) + "] "
				+ record.getMessage() + "\n" 
				+ stack;
	}
	
	private String getStackTrace(Throwable t) {
		if( t == null )
			return "";
		
		StringWriter sw = new StringWriter();
		t.printStackTrace( new PrintWriter( sw ) );
		return sw.toString();
	}
	
	private String className(String s) {
		return s.substring( s.lastIndexOf( "." ) + 1 );
	}
}
