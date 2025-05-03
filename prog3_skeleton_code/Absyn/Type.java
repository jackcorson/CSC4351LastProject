package Absyn;
import Symbol.Symbol;

abstract public class Type extends Absyn {
    public int pos;

    public Type(int p) {
        pos = p;
    }

    abstract public void accept(Visitor v);
}
