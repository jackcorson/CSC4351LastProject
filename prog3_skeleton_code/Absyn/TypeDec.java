package Absyn;
import Symbol.Symbol;

public class TypeDec extends Dec {
  public Symbol name;
  public Type ty;
  public TypeDec next;
  public boolean leaf;

  public TypeDec(Symbol n, Type t, TypeDec x, int p) {
    super(p);
    name = n;
    ty = t;
    next = x;
    leaf = false;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
} 