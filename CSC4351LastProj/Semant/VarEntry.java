package Semant;
import Types.Type;
import Translate.Access;

class VarEntry extends Entry {
    public Access access;
    public Type ty;

    VarEntry(Type t) {
        ty = t;
        access = null;
    }

    VarEntry(Access a, Type t) {
        access = a;
        ty = t;
    }
}
