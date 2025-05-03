package Semant;
import Types.Type;
import Types.RECORD;
import Translate.Level;

class FunEntry extends Entry {
    public RECORD formals;
    public Type result;
    public Level level;

    FunEntry(Level l, RECORD f, Type r) {
        level = l;
        formals = f;
        result = r;
    }

    FunEntry(RECORD f, Type r) {
        this(null, f, r);
    }
}
