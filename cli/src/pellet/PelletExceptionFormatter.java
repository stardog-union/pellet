package pellet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class PelletExceptionFormatter {
	
	private boolean verbose = false;
	
	public PelletExceptionFormatter() {}
	
	/**
	 * Format a user-friendly exception
	 * @param e
	 */
	public String formatException(Throwable e) {
		Throwable cause = e;
		while( cause.getCause() != null ) {
			cause = cause.getCause();
		}
		
		if( !verbose ) {
			if( cause instanceof FileNotFoundException )
				return format( (FileNotFoundException) cause );
			if( cause instanceof PelletCmdException )
				return format( (PelletCmdException) cause );
			return formatGeneric( cause );
		}
		
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter( writer );
		cause.printStackTrace( pw );
		pw.close();
		return writer.toString();
		
	}

	private String format(FileNotFoundException e) {
		return "ERROR: Cannot open " + e.getMessage();
	}

	private String format(PelletCmdException e) {
		return "ERROR: " + e.getMessage();
	}
	
	/**
	 * Return a generic exception message.
	 * @param e
	 */
	private String formatGeneric(Throwable e) {
		String msg = e.getMessage();
		if( msg != null ) {
			int index = msg.indexOf( '\n', 0 );
			if( index != -1 )
				msg = msg.substring( 0, index );
		}
		
		return msg + "\nUse -v for detail.";
	}
	
	public void setVerbose( boolean verbose ) {
		this.verbose = verbose;
	}

}
