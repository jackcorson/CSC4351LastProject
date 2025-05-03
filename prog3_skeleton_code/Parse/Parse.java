package Parse;

public class Parse {

  public ErrorMsg.ErrorMsg errorMsg;
  public Absyn.Exp absyn;

  public Parse(String filename) {
      errorMsg = new ErrorMsg.ErrorMsg(filename);
      java.io.InputStream inp;
      try {
	inp=new java.io.FileInputStream(filename);
      } catch (java.io.FileNotFoundException e) {
	throw new Error("File not found: " + filename);
      }
      System.out.println("Parsing file: " + filename);
      Grm parser = new Grm(new Yylex(inp,errorMsg), errorMsg);
      /* open input files, etc. here */

      try {
	System.out.println("Starting debug parse...");
	absyn = (Absyn.Exp)(parser.debug_parse().value);
	System.out.println("Parse successful!");
      } catch (Throwable e) {
	System.err.println("Error parsing file " + filename + ":");
	String msg = e.getMessage();
	if (msg != null) {
	    System.err.println("Error message: " + msg);
	    if (!msg.contains("$")) {  // Filter out bytecode
		System.err.println(msg);
	    }
	}
	System.err.println("Stack trace:");
	e.printStackTrace();
	throw new Error("Parse error in " + filename);
      } 
      finally {
	try {inp.close();} catch (java.io.IOException e) {}
      }
  }
}