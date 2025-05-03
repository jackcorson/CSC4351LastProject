package Absyn;
import java.io.PrintWriter;

public class Print implements Visitor {
    PrintWriter writer;
    int indent = 0;

    public Print(PrintWriter w) {
        writer = w;
    }

    void indent() {
        for (int i = 0; i < indent; i++)
            writer.print(" ");
    }

    public void visit(VarExp e) {
        indent(); writer.println("VarExp(");
        indent += 2; e.var.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(CallExp e) {
        indent(); writer.println("CallExp(");
        indent += 2;
        if (e.func instanceof IdExp)
            writer.println(((IdExp)e.func).name.toString());
        else
            e.func.accept(this);
        for (Exp arg : e.args) {
            arg.accept(this);
        }
        indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(OpExp e) {
        indent(); writer.println("OpExp(");
        indent += 2; e.left.accept(this);
        writer.println(e.oper);
        e.right.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(RecordExp e) {
        indent(); writer.println("RecordExp(");
        indent += 2; writer.println(e.typ.toString());
        for (Efield f : e.fields) {
            writer.println(f.name.toString());
            f.exp.accept(this);
        }
        indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(SeqExp e) {
        indent(); writer.println("SeqExp(");
        indent += 2;
        for (Exp exp : e.list) {
            exp.accept(this);
        }
        indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(AssignExp e) {
        indent(); writer.println("AssignExp(");
        indent += 2; e.var.accept(this);
        e.exp.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(IfExp e) {
        indent(); writer.println("IfExp(");
        indent += 2; e.test.accept(this);
        e.thenclause.accept(this);
        if (e.elseclause != null) e.elseclause.accept(this);
        indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(WhileExp e) {
        indent(); writer.println("WhileExp(");
        indent += 2; e.test.accept(this);
        e.body.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(ForExp e) {
        indent(); writer.println("ForExp(");
        indent += 2;
        writer.println(e.var.toString());
        e.hi.accept(this);
        e.body.accept(this);
        indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(LetExp e) {
        indent(); writer.println("LetExp(");
        indent += 2;
        for (Dec d : e.decs) {
            d.accept(this);
        }
        e.body.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(ArrayExp e) {
        indent(); writer.println("ArrayExp(");
        indent += 2; writer.println(e.typ.toString());
        e.size.accept(this);
        e.init.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(IntExp e) {
        indent(); writer.println("IntExp(" + e.value + ")");
    }

    public void visit(IdExp e) {
        indent(); writer.println("IdExp(" + e.name.toString() + ")");
    }

    public void visit(SimpleVar v) {
        indent(); writer.println("SimpleVar(" + v.name.toString() + ")");
    }

    public void visit(FieldVar v) {
        indent(); writer.println("FieldVar(");
        indent += 2;
        v.var.accept(this);
        writer.println(v.field.toString());
        indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(SubscriptVar v) {
        indent(); writer.println("SubscriptVar(");
        indent += 2; v.var.accept(this);
        v.index.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(VarDec v) {
        indent(); writer.println("VarDec(");
        indent += 2; writer.println(v.name.toString());
        if (v.typ != null) writer.println(v.typ.toString());
        v.init.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(TypeDec t) {
        indent(); writer.println("TypeDec(");
        indent += 2; writer.println(t.name.toString());
        t.ty.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(FunctionDec f) {
        indent(); writer.println("FunctionDec(");
        indent += 2; writer.println(f.name.toString());
        for (FieldList p = f.params; p != null; p = p.tail) {
            indent(); writer.println(p.name.toString());
            writer.println(p.typ.toString());
        }
        if (f.result != null) writer.println(f.result.toString());
        f.body.accept(this); indent -= 2;
        indent(); writer.println(")");
    }

    public void visit(ArrayType t) {
        indent(); writer.println("ArrayType(" + t.typ.toString() + ")");
    }

    public void visit(NameType t) {
        indent(); writer.println("NameType(" + t.name.toString() + ")");
    }

    // Other visit methods required by interface but not used
    public void visit(Postfix v) {}
    public void visit(Id v) {}
    public void visit(ArgumentList v) {}
    public void visit(ArrayExpression v) {}
    public void visit(AssignmentExpression v) {}
    public void visit(BinOp v) {}
    public void visit(Char v) {}
    public void visit(CommaExpression v) {}
    public void visit(EmptyExpression v) {}
    public void visit(ExpList v) {}
    public void visit(FuncExpression v) {}
    public void visit(ParenExpression v) {}
    public void visit(SizeofExpression v) {}
    public void visit(StringLit v) {}
    public void visit(UnaryExpression v) {}

    public void prExp(Exp e, int d) {
        indent = d;
        e.accept(this);
    }
} 