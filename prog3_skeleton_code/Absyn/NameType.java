package Absyn;
import Symbol.Symbol;

public class NameType extends Type {
    public Symbol name;
    public int pos;

    public NameType(int p, Symbol n) {
        super(p);
        pos = p;
        name = n;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}