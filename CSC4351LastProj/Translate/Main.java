package Translate;
import Frame.*;
import Symbol.Symbol;
import java.io.*;

class Main {
  static Frame frame = new Mips.MipsFrame();

  static void prStm(java.io.PrintWriter writer, Tree.Stm stm, int d) {
    for (int i = 0; i < d; i++)
      writer.print(' ');
    writer.println(stm.toString());
  }

  static void prFrag(java.io.PrintWriter writer, Frag f) {
    if (f == null)
      return;
    if (f instanceof ProcFrag) {
      ProcFrag p = (ProcFrag)f;
      writer.println("PROC " + p.frame.name());
      Tree.Print print = new Tree.Print(writer);
      print.prStm(p.body);
      writer.println();
    }
    prFrag(writer, f.next);
  }

  public static void main(String args[]) throws java.io.IOException {
    String filename = args[0];
    PrintWriter writer = new PrintWriter(new FileWriter(filename + ".s"));
    Translate translate = new Translate(frame);
    // ... rest of main ...
  }
}
