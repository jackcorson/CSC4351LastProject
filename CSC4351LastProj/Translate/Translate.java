package Translate;

import Frame.Access;
import Frame.Frame;
import Symbol.Symbol;
import Temp.Label;
import Temp.Temp;
import Tree.*;

import java.util.HashMap;

public class Translate {
    public Frame frame;
    private Frag frags;
    private HashMap<Symbol, TranslateAccess> globals = new HashMap<>();
    private Level currentLevel;

    public Translate(Frame f) {
        this.frame = f;
        currentLevel = new Level(frame);
    }

    public Frame getFrame() {
        return frame;
    }

    public void procEntryExit(Level level, Exp body) {
        Frame myframe = level.frame;
        Tree.Exp bodyExp = body.unEx();
        Tree.Stm bodyStm;
        if (bodyExp != null)
            bodyStm = MOVE(TEMP(myframe.RV()), bodyExp);
        else
            bodyStm = body.unNx();
        ProcFrag frag = new ProcFrag(myframe.procEntryExit1(bodyStm), myframe);
        frag.next = frags;
        frags = frag;
    }

    public Frag getResult() {
        return frags;
    }

    private static Tree.Exp CONST(int value) {
        return new Tree.CONST(value);
    }

    private static Tree.Exp NAME(Label label) {
        return new Tree.NAME(label);
    }

    private static Tree.Exp TEMP(Temp temp) {
        return new Tree.TEMP(temp);
    }

    private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) {
        return new Tree.BINOP(binop, left, right);
    }

    private static Tree.Exp MEM(Tree.Exp exp) {
        return new Tree.MEM(exp);
    }

    private static Tree.Exp CALL(Tree.Exp func, Tree.ExpList args) {
        return new Tree.CALL(func, args);
    }

    private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
        if (stm == null)
            return exp;
        return new Tree.ESEQ(stm, exp);
    }

    private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) {
        return new Tree.MOVE(dst, src);
    }

    private static Tree.Stm UEXP(Tree.Exp exp) {
        return new Tree.UEXP(exp);
    }

    private static Tree.Stm JUMP(Label target) {
        return new Tree.JUMP(target);
    }

    private static Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t, Label f) {
        return new Tree.CJUMP(relop, l, r, t, f);
    }

    private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) {
        if (left == null)
            return right;
        if (right == null)
            return left;
        return new Tree.SEQ(left, right);
    }

    private static Tree.Stm LABEL(Label label) {
        return new Tree.LABEL(label);
    }

    private static Tree.ExpList ExpList(Tree.Exp head, Tree.ExpList tail) {
        return new Tree.ExpList(head, tail);
    }

    private static Tree.ExpList ExpList(Tree.Exp head) {
        return ExpList(head, null);
    }

    private static Tree.ExpList ExpList(ExpList exp) {
        if (exp == null)
            return null;
        return ExpList(exp.head.unEx(), ExpList(exp.tail));
    }

    public Exp Error() {
        return new Ex(CONST(0));
    }

    public Exp SimpleVar(TranslateAccess access, Level level) {
        if (access == null) return Error();
        Tree.Exp fp = TEMP(frame.FP());
        return new Ex(MEM(access.exp(fp)));
    }

    public Exp GlobalVar(Symbol name, int size) {
        TranslateAccess access = globals.get(name);
        if (access == null) {
            Label label = new Label(name.toString());
            Access frameAccess = frame.allocGlobal(label, size);
            access = new TranslateAccess(currentLevel, frameAccess);
            globals.put(name, access);
            DataFrag frag = new DataFrag(frame.data(label, size));
            frag.next = frags;
            frags = frag;
            return new Ex(frame.data(label, size));
        }
        return new Ex(access.exp(new CONST(0)));
    }

    public Exp FieldVar(Exp record, int index) {
        Label badOne = frame.badPtr();
        Label okOne = new Label();
        Temp r = new Temp();
        index = index * frame.wordSize();
        return new Ex(ESEQ(SEQ(MOVE(TEMP(r), record.unEx()),
                        SEQ(CJUMP(CJUMP.EQ, TEMP(r), CONST(0), badOne, okOne), LABEL(okOne))),
                MEM(BINOP(BINOP.PLUS, TEMP(r), CONST(index)))));
    }

    public Exp SubscriptVar(Exp array, Exp index) {
        Label badOne = frame.badSub();
        Label checking = new Label();
        Label okOne = new Label();
        Temp a = new Temp();
        Temp i = new Temp();
        int size = frame.wordSize();

        return new Ex(ESEQ(SEQ(MOVE(TEMP(a), array.unEx()),
                SEQ(MOVE(TEMP(i), index.unEx()), SEQ(CJUMP(CJUMP.LT, TEMP(i), CONST(0), badOne, checking),
                        SEQ(LABEL(checking), SEQ(CJUMP(CJUMP.GT, TEMP(i), MEM(BINOP(BINOP.PLUS, TEMP(a), CONST(-size))),
                                badOne, okOne), LABEL(okOne)))))), MEM(BINOP(BINOP.PLUS, TEMP(a), BINOP(BINOP.MUL, TEMP(i),
                CONST(size))))));
    }

    public Exp NilExp() {
        return new Ex(CONST(0));
    }

    public Exp IntExp(int value) {
        return new Ex(CONST(value));
    }

    private java.util.Hashtable strings = new java.util.Hashtable();

    public Exp StringExp(String s) {
        Label label = new Label();
        DataFrag frag = new DataFrag(frame.string(label, s));
        frag.next = frags;
        frags = frag;
        return new Ex(frame.string(label, s));
    }

    private Tree.Exp CallExp(Symbol f, ExpList args, Level from) {
        return frame.externalCall(f.toString(), ExpList(args));
    }

    private Tree.Exp CallExp(Level f, ExpList args, Level from) {
        throw new Error("Translate.CallExp unimplemented");
    }

    public Exp FunExp(Symbol f, ExpList args, Level from) {
        return new Ex(CallExp(f, args, from));
    }

    public Exp FunExp(Level f, ExpList args, Level from) {
        return new Ex(CallExp(f, args, from));
    }

    public Exp ProcExp(Symbol f, ExpList args, Level from) {
        return new Nx(UEXP(CallExp(f, args, from)));
    }

    public Exp ProcExp(Level f, ExpList args, Level from) {
        return new Nx(UEXP(CallExp(f, args, from)));
    }

    public Exp OpExp(int op, Exp left, Exp right) {
        Tree.Exp l = left.unEx();
        Tree.Exp r = right.unEx();
        if (l == null || r == null) return Error();

        switch (op) {
            case BINOP.PLUS:
            case BINOP.MINUS:
            case BINOP.MUL:
            case BINOP.DIV:
            case BINOP.AND:
            case BINOP.OR:
            case BINOP.LSHIFT:
            case BINOP.RSHIFT:
            case BINOP.ARSHIFT:
            case BINOP.XOR:
                return new Ex(BINOP(op, l, r));
            default:
                return new RelCx(op, l, r);
        }
    }

    public Exp StrOpExp(int op, Exp left, Exp right) {
        Tree.Exp cmp = frame.externalCall("strcmp", ExpList(left.unEx(),
                ExpList(right.unEx())));

        switch (op) {
            case Absyn.OpExp.GT:
                return new RelCx(CJUMP.GT, cmp, CONST(0));
            case Absyn.OpExp.LT:
                return new RelCx(CJUMP.LT, cmp, CONST(0));
            case Absyn.OpExp.GE:
                return new RelCx(CJUMP.GE, cmp, CONST(0));
            case Absyn.OpExp.LE:
                return new RelCx(CJUMP.LE, cmp, CONST(0));
            case Absyn.OpExp.EQ:
                return new RelCx(CJUMP.EQ, cmp, CONST(0));
            case Absyn.OpExp.NEQ:
                return new RelCx(CJUMP.NE, cmp, CONST(0));
            default:
                throw new Error("Translate.StrOpExp");
        }
    }

    public Exp RecordExp(ExpList init) {
        int size = 0;
        for (ExpList exp = init; exp != null; exp = exp.tail, size++) {
        }

        Temp temp = new Temp();
        return new Ex(ESEQ(SEQ(MOVE(TEMP(temp), frame.externalCall("allocRecord",
                ExpList(CONST(size)))), initRecord(temp, 0, init, frame.wordSize())), TEMP(temp)));

    }

    private Tree.Stm initRecord(Temp r, int i, ExpList init, int wordSize) {
        if (init == null)
            return null;
        return
                SEQ(MOVE(MEM(BINOP(BINOP.PLUS, TEMP(r), CONST(i))), init.head.unEx()),
                        initRecord(r, i + wordSize, init.tail, wordSize));
    }

    public Exp SeqExp(ExpList e) {
        if (e == null) {
            return new Nx(null);
        }

        Tree.Stm stem = null;
        while (e.tail != null) {
            e = e.tail;
            stem = SEQ(stem, e.head.unNx());
        }

        Tree.Exp result = e.head.unEx();
        if (result == null) {
            return new Nx(SEQ(stem, e.head.unNx()));
        }

        return new Ex(ESEQ(stem, result));

    }

    public Exp AssignExp(Exp lhs, Exp rhs) {
        return new Nx(MOVE(lhs.unEx(), rhs.unEx()));
    }

    public Exp IfExp(Exp cc, Exp aa, Exp bb) {
        Label t = new Label();
        Label f = new Label();
        Label join = new Label();
        Temp r = new Temp();

        if (bb == null) {  // if-then
            return new Nx(SEQ(cc.unCx(t, f),
                    SEQ(LABEL(t),
                            SEQ(aa.unNx(),
                                    SEQ(LABEL(f),
                                            null)))));
        } else {  // if-then-else
            return new Nx(SEQ(cc.unCx(t, f),
                    SEQ(LABEL(t),
                            SEQ(aa.unNx(),
                                    SEQ(JUMP(join),
                                            SEQ(LABEL(f),
                                                    SEQ(bb.unNx(),
                                                            LABEL(join))))))));
        }
    }

    public Exp WhileExp(Exp test, Exp body, Label done) {
        Label c = new Label();
        Label b = new Label();
        return new Nx(SEQ(SEQ(SEQ(LABEL(c), test.unCx(b, done)),
                        SEQ(SEQ(LABEL(b), body.unNx()), JUMP(c))),
                LABEL(done)));
    }

    public Exp ForExp(TranslateAccess i, Exp lo, Exp hi, Exp body, Label done) {
        Label test = new Label();
        Label incr = new Label();

        return new Nx(SEQ(
                MOVE(i.exp(TEMP(frame.FP())), lo.unEx()),
                SEQ(LABEL(test),
                        SEQ(CJUMP(CJUMP.LE, i.exp(TEMP(frame.FP())), hi.unEx(), incr, done),
                                SEQ(LABEL(incr),
                                        SEQ(body.unNx(),
                                                SEQ(MOVE(i.exp(TEMP(frame.FP())),
                                                                BINOP(BINOP.PLUS, i.exp(TEMP(frame.FP())), CONST(1))),
                                                        JUMP(test))))))));
    }

    public Exp BreakExp(Label done) {
        return new Nx(JUMP(done));
    }

    public Exp LetExp(ExpList lets, Exp body) {
        Tree.Stm stm = null;
        for (ExpList e = lets; e != null; e = e.tail)
            stm = SEQ(stm, e.head.unNx());
        Tree.Exp result = body.unEx();
        if (result == null)
            return new Nx(SEQ(stm, body.unNx()));
        return new Ex(ESEQ(stm, result));
    }

    public Exp ArrayExp(Exp size, Exp init) {
        return new Ex
                (frame.externalCall("initArray",
                        ExpList(size.unEx(), ExpList(init.unEx()))));
    }

    public Exp VarDec(TranslateAccess a, Exp init) {
        return new Nx(MOVE(a.exp(TEMP(frame.FP())), init.unEx()));
    }

    public Exp TypeDec() {
        return new Nx(null);
    }

    public Exp FunctionDec() {
        return new Nx(null);
    }

    public void addCRuntime() {
        Label label = new Label("_framesize");
        DataFrag frag = new DataFrag(frame.data(label, 4));  // 4 bytes for word size
        frag.next = frags;
        frags = frag;
    }
}