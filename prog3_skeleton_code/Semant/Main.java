package Semant;
import Absyn.DeclarationList;
import Parse.Parse;

public class Main {

  public static void main(String argv[])  {
    for (int i = 0; i < argv.length; ++i) {
      String filename = argv[i];
      if (argv.length > 1)
	System.out.println("***Processing: " + filename);
      Parse parse = new Parse(filename);
      Semant semant = new Semant(parse.errorMsg);
      semant.transProg((DeclarationList)parse.absyn);


      /* We are not giving out the print code, therefore
       * this is being commented out. The parser should run
       * without any trouble, it just wont print output.

      PrintWriter writer = new PrintWriter(System.out);
      Absyn.Print printer = new Absyn.Print(writer);
      printer.prExp(parse.absyn, 0);
      writer.println();
      writer.flush();
      */

      // If you want to print output for prog3, you will need to
      // uncomment the above and implement it yourself, or come up
      // with some other way to show your results.
    }
  }
}
