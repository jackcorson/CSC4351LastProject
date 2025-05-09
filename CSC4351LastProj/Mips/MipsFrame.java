package Mips;
import Frame.Access;
import Frame.AccessList;
import Frame.Frame;
import Frame.Proc;
import Symbol.Symbol;
import Temp.Label;
import Temp.Temp;
import Temp.TempList;
import Util.BoolList;
import java.util.Hashtable;

public class MipsFrame implements Frame {

  private int count = 0;
  protected String name;
  private AccessList formals;

  public Frame newFrame(Symbol name, BoolList formals) {
    Label label;
    if (name == null)
      label = new Label();
    else if (this.name != null)
      label = new Label(this.name + "." + name + "." + count++);
    else
      label = new Label(name.toString());
    return new MipsFrame(label, formals);
  }

  public MipsFrame() {}
  private MipsFrame(Label n, Util.BoolList f) {
    name = n.toString();
    formals = allocFormals(0, f);
  }

  private static final int wordSize = 4;
  public int wordSize() { return wordSize; }

  private int offset = 0;
  public Access allocLocal(boolean escape) {
    if (escape) {
      offset -= wordSize;
      return new InFrame(offset);
    } else
      return new InReg(new Temp());
  }

  private AccessList allocFormals(int offset, Util.BoolList formals) {
    if (formals == null)
      return null;
    Access a;
    if (formals.head)
      a = new InFrame(offset);
    else
      a = new InReg(new Temp());
    return new AccessList(a, allocFormals(offset + wordSize, formals.tail));
  }

  static final Temp ZERO = new Temp(); // zero reg
  static final Temp AT = new Temp(); // reserved for assembler
  static final Temp V0 = new Temp(); // function result
  static final Temp V1 = new Temp(); // second function result
  static final Temp A0 = new Temp(); // argument1
  static final Temp A1 = new Temp(); // argument2
  static final Temp A2 = new Temp(); // argument3
  static final Temp A3 = new Temp(); // argument4
  static final Temp T0 = new Temp(); // caller-saved
  static final Temp T1 = new Temp();
  static final Temp T2 = new Temp();
  static final Temp T3 = new Temp();
  static final Temp T4 = new Temp();
  static final Temp T5 = new Temp();
  static final Temp T6 = new Temp();
  static final Temp T7 = new Temp();
  static final Temp S0 = new Temp(); // callee-saved
  static final Temp S1 = new Temp();
  static final Temp S2 = new Temp();
  static final Temp S3 = new Temp();
  static final Temp S4 = new Temp();
  static final Temp S5 = new Temp();
  static final Temp S6 = new Temp();
  static final Temp S7 = new Temp();
  static final Temp T8 = new Temp(); // caller-saved
  static final Temp T9 = new Temp();
  static final Temp K0 = new Temp(); // reserved for OS kernel
  static final Temp K1 = new Temp(); // reserved for OS kernel
  static final Temp GP = new Temp(); // pointer to global area
  static final Temp SP = new Temp(); // stack pointer
  static final Temp FP = new Temp(); // virtual frame pointer (eliminated)
  static final Temp S8 = new Temp(); // actual frame pointer
  static final Temp RA = new Temp(); // return address

  public Temp FP() { return FP; }
  public Temp RV() { return V0; }

  private static Hashtable labels = new Hashtable();
  public Tree.Exp externalCall(String func, Tree.ExpList args) {
    String u = func.intern();
    Label l = (Label)labels.get(u);
    if (l == null) {
      l = new Label("_" + u);
      labels.put(u, l);
    }
    return new Tree.CALL(new Tree.NAME(l), args);
  }

  public Tree.Stm procEntryExit1(Tree.Stm body) {
    body = saveTemps(argRegs, formals, body);
    TempList callees = L(RA, calleeSaves);
    // change true to false if your register allocator handles spills
    AccessList a = allocCallees(callees, true);
    body = saveTemps(callees, a, body);
    body = restoreTemps(callees, a, body);
    return body;
  }

  public Tree.Exp string(Label label, String str) {
    // Create string data segment entry
    return new Tree.NAME(label);
  }

  private static final Label badPtr = new Label("_BADPTR");
  public Label badPtr() {
    return badPtr;
  }

  private static final Label badSub = new Label("_BADSUB");
  public Label badSub() {
    return badSub;
  }

  private static final Hashtable tempMap = new Hashtable(32);
  static {
    tempMap.put(ZERO, "$0");
    tempMap.put(AT,   "$at");
    tempMap.put(V0,   "$v0");
    tempMap.put(V1,   "$v1");
    tempMap.put(A0,   "$a0");
    tempMap.put(A1,   "$a1");
    tempMap.put(A2,   "$a2");
    tempMap.put(A3,   "$a3");
    tempMap.put(T0,   "$t0");
    tempMap.put(T1,   "$t1");
    tempMap.put(T2,   "$t2");
    tempMap.put(T3,   "$t3");
    tempMap.put(T4,   "$t4");
    tempMap.put(T5,   "$t5");
    tempMap.put(T6,   "$t6");
    tempMap.put(T7,   "$t7");
    tempMap.put(S0,   "$s0");
    tempMap.put(S1,   "$s1");
    tempMap.put(S2,   "$s2");
    tempMap.put(S3,   "$s3");
    tempMap.put(S4,   "$s4");
    tempMap.put(S5,   "$s5");
    tempMap.put(S6,   "$s6");
    tempMap.put(S7,   "$s7");
    tempMap.put(T8,   "$t8");
    tempMap.put(T9,   "$t9");
    tempMap.put(K0,   "$k0");
    tempMap.put(K1,   "$k1");
    tempMap.put(GP,   "$gp");
    tempMap.put(SP,   "$sp");
    tempMap.put(FP,   "$fp");	// should be virtual
    tempMap.put(S8,   "$s8");	// $s8 is alias for $fp (when virtual)
    tempMap.put(RA,   "$ra");
  }
  public String tempMap(Temp temp) {
    return (String)tempMap.get(temp);
  }

  static TempList L(Temp h, TempList t) {
    return new TempList(h, t);
  }
  static TempList L(Temp h) {
    return new TempList(h, null);
  }
  static TempList L(TempList a, TempList b) {
    return new TempList(a, b);
  }

  // Register lists: must not overlap and must include every register that
  // might show up in Assem instructions
  static TempList specialRegs, argRegs, callerSaves, calleeSaves;
  {
    // registers dedicated to special purposes
    specialRegs = L(ZERO,L(AT,L(K0,L(K1,L(GP,L(FP,L(SP,L(RA))))))));
    // registers in which to pass outgoing arguments (including static link)
    argRegs     = L(A0,L(A1,L(A2,L(A3))));
    // registers that the called procedure (callee) must preserve for caller
    calleeSaves = L(S0,L(S1,L(S2,L(S3,L(S4,L(S5,L(S6,L(S7,L(S8)))))))));
    // registers that the callee may trash
    callerSaves = L(T0,L(T1,L(T2,L(T3,L(T4,L(T5,L(T6,L(T7,L(T8,L(T9))))))))));
    callerSaves = L(V0,L(V1, callerSaves));
  }

  static Assem.InstrList append(Assem.InstrList a, Assem.InstrList b) {
    if (a == null)
      return b;
    Assem.InstrList p;
    for (p = a; p.tail != null; p = p.tail);
    p.tail = b;
    return a;
  }

  static TempList calldefs, returnSink;
  {
    // registers defined by a call
    calldefs = L(RA, L(argRegs, callerSaves));
    // registers live on return
    returnSink = L(V0, L(specialRegs, calleeSaves));
  }

  public Assem.InstrList procEntryExit2(Assem.InstrList body) {
    return append(body,
                  new Assem.InstrList(
                    new Assem.OPER("", null, returnSink), null));
  }

  int maxArgs = 0;
  public Proc procEntryExit3(Assem.InstrList body) {
    int frameSize = maxArgs * wordSize - offset;
    String pre = "\t.text\n" + name + ":\n" + name + "_framesize=" + frameSize;
    String post = "\tj $ra";
    if (frameSize != 0) {
      pre += "\n\tsubu $sp " + name + "_framesize";
      post = "\taddu $sp " + name + "_framesize\n" + post;
    }
    return new Proc(pre, body, post);
  }

  public Assem.InstrList codegen(Tree.Stm stm) {
    return (new Codegen(this)).codegen(stm);
  }
  static TempList registers;
  {
    registers = L(argRegs, specialRegs);
    registers = L(calleeSaves, registers);
    registers = L(callerSaves, registers);
  }
  public TempList registers() {
    return registers;
  }

  private AccessList allocCallees(TempList t, boolean spill) {
    if (t == null)
      return null;
    return new AccessList(allocLocal(spill), allocCallees(t.tail, spill));
  }

  private Tree.Stm saveTemps(TempList t, AccessList a, Tree.Stm body) {
    if (t == null || a == null)
      return body;
    body = saveTemps(t.tail, a.tail, body);
    Tree.Stm move = new Tree.MOVE(a.head.exp(new Tree.TEMP(FP)),
				  new Tree.TEMP(t.head));
    if (body == null)
      return move;
    return new Tree.SEQ(move, body);
  }

  private Tree.Stm restoreTemps(TempList t, AccessList a, Tree.Stm body) {
    if (t == null || a == null)
      return body;
    body = restoreTemps(t.tail, a.tail, body);
    Tree.Stm move = new Tree.MOVE(new Tree.TEMP(t.head),
				  a.head.exp(new Tree.TEMP(FP)));
    if (body == null)
      return move;
    return new Tree.SEQ(body, move);
  }

  public Access allocGlobal(Label label, int size) {
    return new InFrame(0); 
  }

  public Tree.Exp data(Label label, int size) {
    return new Tree.MEM(new Tree.NAME(label));
  }

  public Label name() {
    return new Label(name);
  }
}
