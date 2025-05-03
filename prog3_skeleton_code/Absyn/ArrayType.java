package Absyn;
import Symbol.Symbol;

public class ArrayType extends Type {
    public Type element;
    public Symbol typ;

    public ArrayType(Type e, Symbol t, int p) {
        super(p);
        element = e;
        typ = t;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
