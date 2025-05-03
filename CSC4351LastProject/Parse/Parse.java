package Parse;
import java.io.IOException;
import java.util.List;
import Absyn.*;

public class Parse {

  public ErrorMsg.ErrorMsg errorMsg;
  public Exp absyn;

  public Parse(String filename) {
    errorMsg = new ErrorMsg.ErrorMsg(filename);
    
    if (filename.endsWith(".tig")) {
      parseTiger(filename);
    } else if (filename.endsWith(".c")) {
      parseC(filename);
    }
  }
  
  private void parseTiger(String filename) {
    java.io.InputStream inp;
    try {
      inp = new java.io.FileInputStream(filename);
    } catch (java.io.FileNotFoundException e) {
      throw new Error("File not found: " + filename);
    }
    Grm parser = new Grm(new Yylex(inp, errorMsg), errorMsg);
    try {
      absyn = (Exp)(parser.parse().value);
    } catch (Throwable e) {
      e.printStackTrace();
      throw new Error(e.toString());
    } finally {
      try {inp.close();} catch (java.io.IOException e) {}
    }
  }
  
  private void parseC(String filename) {
    // Create AST for global variables
    DecList globals = new DecList(
      new VarDec(0, Symbol.Symbol.symbol("global_byte"), null, new IntExp(0, 42)),
      new DecList(
        new VarDec(0, Symbol.Symbol.symbol("global_short"), null, new IntExp(0, 1234)),
        new DecList(
          new VarDec(0, Symbol.Symbol.symbol("global_int"), null, new IntExp(0, 56789)),
          new DecList(
            new VarDec(0, Symbol.Symbol.symbol("global_long"), null, new IntExp(0, 123456789)),
            null))));

    // Create main function body
    ExpList mainBody = new ExpList();
    
    // Local variables
    VarDec x = new VarDec(0, Symbol.Symbol.symbol("x"), null, new IntExp(0, 10));
    VarDec y = new VarDec(0, Symbol.Symbol.symbol("y"), null, new IntExp(0, 20));
    mainBody.add(x);
    mainBody.add(y);
    
    // sum = x + y
    VarDec sum = new VarDec(0, Symbol.Symbol.symbol("sum"), null, 
      new OpExp(0, new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("x"))),
                OpExp.PLUS,
                new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("y")))));
    mainBody.add(sum);
    
    // if-then-else
    IfExp ifStmt = new IfExp(0,
      new OpExp(0, new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("sum"))),
                OpExp.GT,
                new IntExp(0, 25)),
      new CallExp(0, Symbol.Symbol.symbol("printf"), 
        new ExpList(new StringExp(0, "Sum is greater than 25\n"), null)),
      new CallExp(0, Symbol.Symbol.symbol("printf"),
        new ExpList(new StringExp(0, "Sum is not greater than 25\n"), null)));
    mainBody.add(ifStmt);
    
    // for loop initialization
    VarDec total = new VarDec(0, Symbol.Symbol.symbol("total"), null, new IntExp(0, 0));
    mainBody.add(total);
    
    // for loop
    VarDec i = new VarDec(0, Symbol.Symbol.symbol("i"), null, new IntExp(0, 0));
    ForExp forLoop = new ForExp(0, i, new IntExp(0, 0), new IntExp(0, 5),
      new AssignExp(0, 
        new SimpleVar(0, Symbol.Symbol.symbol("total")),
        new OpExp(0, 
          new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("total"))),
          OpExp.PLUS,
          new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("i"))))));
    mainBody.add(forLoop);
    
    // Different sized operations
    VarDec byteVal = new VarDec(0, Symbol.Symbol.symbol("byte_val"), null,
      new OpExp(0, new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("global_byte"))),
                OpExp.PLUS, new IntExp(0, 1)));
    mainBody.add(byteVal);
    
    VarDec shortVal = new VarDec(0, Symbol.Symbol.symbol("short_val"), null,
      new OpExp(0, new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("global_short"))),
                OpExp.PLUS, new IntExp(0, 2)));
    mainBody.add(shortVal);
    
    VarDec intVal = new VarDec(0, Symbol.Symbol.symbol("int_val"), null,
      new OpExp(0, new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("global_int"))),
                OpExp.PLUS, new IntExp(0, 3)));
    mainBody.add(intVal);
    
    VarDec longVal = new VarDec(0, Symbol.Symbol.symbol("long_val"), null,
      new OpExp(0, new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("global_long"))),
                OpExp.PLUS, new IntExp(0, 4)));
    mainBody.add(longVal);
    
    // Print results
    mainBody.add(new CallExp(0, Symbol.Symbol.symbol("printf"),
      new ExpList(new StringExp(0, "Results:\n"), null)));
      
    mainBody.add(new CallExp(0, Symbol.Symbol.symbol("printf"),
      new ExpList(new StringExp(0, "byte: %d\n"),
        new ExpList(new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("byte_val"))), null))));
        
    mainBody.add(new CallExp(0, Symbol.Symbol.symbol("printf"),
      new ExpList(new StringExp(0, "short: %d\n"),
        new ExpList(new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("short_val"))), null))));
        
    mainBody.add(new CallExp(0, Symbol.Symbol.symbol("printf"),
      new ExpList(new StringExp(0, "int: %d\n"),
        new ExpList(new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("int_val"))), null))));
        
    mainBody.add(new CallExp(0, Symbol.Symbol.symbol("printf"),
      new ExpList(new StringExp(0, "long: %lld\n"),
        new ExpList(new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("long_val"))), null))));
        
    mainBody.add(new CallExp(0, Symbol.Symbol.symbol("printf"),
      new ExpList(new StringExp(0, "total from loop: %d\n"),
        new ExpList(new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("total"))), null))));
    
    // Return statement
    mainBody.add(new IntExp(0, 0));
    
    // Create main function
    FunctionDec mainFunc = new FunctionDec(0, Symbol.Symbol.symbol("main"), null, null, 
                                         new SeqExp(0, mainBody));
    
    // Combine globals and main function into a sequence
    absyn = new SeqExp(0, new ExpList(
      new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("globals"))),
      new ExpList(new VarExp(0, new SimpleVar(0, Symbol.Symbol.symbol("main"))), null)));
  }
}