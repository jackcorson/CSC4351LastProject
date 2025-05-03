package Mips;
import Temp.Temp;
import Temp.TempList;
import Temp.Label;
import Temp.LabelList;
import java.util.Hashtable;

public class Codegen {
  MipsFrame frame;
  public Codegen(MipsFrame f) {frame = f;}

  private Assem.InstrList ilist = null, last = null;

  private void emit(Assem.Instr inst) {
    if (last != null)
      last = last.tail = new Assem.InstrList(inst, null);
    else {
      if (ilist != null)
	throw new Error("Codegen.emit");
      last = ilist = new Assem.InstrList(inst, null);
    }
  }

  Assem.InstrList codegen(Tree.Stm s) {
    munchStm(s);
    Assem.InstrList l = ilist;
    ilist = last = null;
    return l;
  }

  static Assem.Instr OPER(String a, TempList d, TempList s, LabelList j) {
    return new Assem.OPER("\t" + a, d, s, j);
  }
  static Assem.Instr OPER(String a, TempList d, TempList s) {
    return new Assem.OPER("\t" + a, d, s);
  }
  static Assem.Instr MOVE(String a, Temp d, Temp s) {
    return new Assem.MOVE("\t" + a, d, s);
  }

  static TempList L(Temp h) {
    return new TempList(h, null);
  }
  static TempList L(Temp h, TempList t) {
    return new TempList(h, t);
  }

  void munchStm(Tree.Stm s) {
    if (s instanceof Tree.MOVE) 
      munchStm((Tree.MOVE)s);
    else if (s instanceof Tree.UEXP)
      munchStm((Tree.UEXP)s);
    else if (s instanceof Tree.JUMP)
      munchStm((Tree.JUMP)s);
    else if (s instanceof Tree.CJUMP)
      munchStm((Tree.CJUMP)s);
    else if (s instanceof Tree.LABEL)
      munchStm((Tree.LABEL)s);
    else
      throw new Error("Codegen.munchStm");
  }

  void munchStm(Tree.MOVE s) {
    Tree.Exp dst = s.dst;
    Tree.Exp src = s.src;
    
    if (dst instanceof Tree.MEM) {
      Tree.MEM mem = (Tree.MEM)dst;
      Temp srcTemp = munchExp(src);
      
      // Handle different sized stores
      switch(dst.size) {
        case 1: // byte
          emit(OPER("sb `s0 0(`s1)", null, L(srcTemp, L(munchExp(mem.exp)))));
          break;
        case 2: // short
          emit(OPER("sh `s0 0(`s1)", null, L(srcTemp, L(munchExp(mem.exp)))));
          break;
        case 4: // int/pointer
          emit(OPER("sw `s0 0(`s1)", null, L(srcTemp, L(munchExp(mem.exp)))));
          break;
        case 8: // long long
          Temp high = new Temp();
          Temp low = new Temp();
          emit(OPER("srl `d0 `s0 32", L(high), L(srcTemp)));
          emit(OPER("and `d0 `s0 0xFFFFFFFF", L(low), L(srcTemp)));
          emit(OPER("sw `s0 0(`s1)", null, L(high, L(munchExp(mem.exp)))));
          emit(OPER("sw `s0 4(`s1)", null, L(low, L(munchExp(mem.exp)))));
          break;
      }
    } else if (dst instanceof Tree.TEMP) {
      emit(MOVE("move `d0 `s0", ((Tree.TEMP)dst).temp, munchExp(src)));
    }
  }

  void munchStm(Tree.UEXP s) {
    munchExp(s.exp);
  }

  void munchStm(Tree.JUMP s) {
    emit(OPER("j " + s.targets.head.toString(), null, null, s.targets));
  }

  private static String[] CJUMP = new String[10];
  static {
    CJUMP[Tree.CJUMP.EQ ] = "beq";
    CJUMP[Tree.CJUMP.NE ] = "bne";
    CJUMP[Tree.CJUMP.LT ] = "blt";
    CJUMP[Tree.CJUMP.GT ] = "bgt";
    CJUMP[Tree.CJUMP.LE ] = "ble";
    CJUMP[Tree.CJUMP.GE ] = "bge";
    CJUMP[Tree.CJUMP.ULT] = "bltu";
    CJUMP[Tree.CJUMP.ULE] = "bleu";
    CJUMP[Tree.CJUMP.UGT] = "bgtu";
    CJUMP[Tree.CJUMP.UGE] = "bgeu";
  }

  void munchStm(Tree.CJUMP s) {
    Temp left = munchExp(s.left);
    Temp right = munchExp(s.right);
    emit(OPER(CJUMP[s.relop] + " `s0 `s1 " + s.iftrue,
              null, L(left, L(right)), new LabelList(s.iftrue, new LabelList(s.iffalse, null))));
  }

  void munchStm(Tree.LABEL l) {
    emit(new Assem.LABEL(l.label.toString() + ":", l.label));
  }

  Temp munchExp(Tree.Exp s) {
    if (s instanceof Tree.CONST)
      return munchExp((Tree.CONST)s);
    else if (s instanceof Tree.NAME)
      return munchExp((Tree.NAME)s);
    else if (s instanceof Tree.TEMP)
      return munchExp((Tree.TEMP)s);
    else if (s instanceof Tree.BINOP)
      return munchExp((Tree.BINOP)s);
    else if (s instanceof Tree.MEM)
      return munchExp((Tree.MEM)s);
    else if (s instanceof Tree.CALL)
      return munchExp((Tree.CALL)s);
    else
      throw new Error("Codegen.munchExp");
  }

  Temp munchExp(Tree.CONST e) {
    Temp result = new Temp();
    emit(OPER("li `d0 " + e.value, L(result), null));
    return result;
  }

  Temp munchExp(Tree.NAME e) {
    Temp result = new Temp();
    emit(OPER("la `d0 " + e.label.toString(), L(result), null));
    return result;
  }

  Temp munchExp(Tree.TEMP e) {
    if (e.temp == frame.FP) {
      Temp t = new Temp();
      emit(OPER("addu `d0 `s0 " + frame.name + "_framesize",
		L(t), L(frame.SP)));
      return t;
    }
    return e.temp;
  }

  private static String[] BINOP = new String[10];
  static {
    BINOP[Tree.BINOP.PLUS   ] = "add";
    BINOP[Tree.BINOP.MINUS  ] = "sub";
    BINOP[Tree.BINOP.MUL    ] = "mulo";
    BINOP[Tree.BINOP.DIV    ] = "div";
    BINOP[Tree.BINOP.AND    ] = "and";
    BINOP[Tree.BINOP.OR     ] = "or";
    BINOP[Tree.BINOP.LSHIFT ] = "sll";
    BINOP[Tree.BINOP.RSHIFT ] = "srl";
    BINOP[Tree.BINOP.ARSHIFT] = "sra";
    BINOP[Tree.BINOP.XOR    ] = "xor";
  }

  private static int shift(int i) {
    int shift = 0;
    if ((i >= 2) && ((i & (i - 1)) == 0)) {
      while (i > 1) {
	shift += 1;
	i >>= 1;
      }
    }
    return shift;
  }

  Temp munchExp(Tree.BINOP e) {
    Temp result = new Temp();
    Temp left = munchExp(e.left);
    Temp right = munchExp(e.right);
    
    if (e.right instanceof Tree.CONST) {
      int value = ((Tree.CONST)e.right).value;
      switch(e.binop) {
        case Tree.BINOP.PLUS:
          emit(OPER("addi `d0 `s0 " + value, L(result), L(left)));
          break;
        case Tree.BINOP.MINUS:
          emit(OPER("addi `d0 `s0 " + (-value), L(result), L(left)));
          break;
        case Tree.BINOP.MUL:
          emit(OPER("mul `d0 `s0 " + value, L(result), L(left)));
          break;
        case Tree.BINOP.AND:
          emit(OPER("andi `d0 `s0 " + value, L(result), L(left)));
          break;
        default:
          emit(OPER(BINOP[e.binop] + " `d0 `s0 `s1", L(result), L(left, L(right))));
      }
    } else {
      emit(OPER(BINOP[e.binop] + " `d0 `s0 `s1", L(result), L(left, L(right))));
    }
    return result;
  }

  Temp munchExp(Tree.MEM e) {
    Temp result = new Temp();
    
    // Handle different sized loads
    switch(e.size) {
      case 1: // byte
        emit(OPER("lb `d0 0(`s0)", L(result), L(munchExp(e.exp))));
        break;
      case 2: // short
        emit(OPER("lh `d0 0(`s0)", L(result), L(munchExp(e.exp))));
        break;
      case 4: // int/pointer
        emit(OPER("lw `d0 0(`s0)", L(result), L(munchExp(e.exp))));
        break;
      case 8: // long long
        Temp high = new Temp();
        Temp low = new Temp();
        emit(OPER("lw `d0 0(`s0)", L(high), L(munchExp(e.exp))));
        emit(OPER("lw `d0 4(`s0)", L(low), L(munchExp(e.exp))));
        emit(OPER("sll `d0 `s0 32", L(result), L(high)));
        emit(OPER("or `d0 `s0 `s1", L(result), L(result, L(low))));
        break;
    }
    return result;
  }

  Temp munchExp(Tree.CALL s) {
    TempList l = munchArgs(0, s.args);
    emit(OPER("jal " + ((Tree.NAME)s.func).label.toString(),
              L(frame.RA, L(frame.RV)), l));
    return frame.RV;
  }

  private TempList munchArgs(int i, Tree.ExpList args) {
    if (args == null)
      return null;
    Temp src = munchExp(args.head);
    if (i > frame.maxArgs)
      frame.maxArgs = i;
    switch (i) {
      case 0:
        emit(MOVE("move `d0 `s0", frame.A0, src));
        break;
      case 1:
        emit(MOVE("move `d0 `s0", frame.A1, src));
        break;
      case 2:
        emit(MOVE("move `d0 `s0", frame.A2, src));
        break;
      case 3:
        emit(MOVE("move `d0 `s0", frame.A3, src));
        break;
      default:
        emit(OPER("sw `s0 " + (i-1)*frame.wordSize() + "(`s1)",
                  null, L(src, L(frame.SP))));
        break;
    }
    return L(src, munchArgs(i+1, args.tail));
  }
}