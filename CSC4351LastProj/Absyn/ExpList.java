package Absyn;
import java.util.ArrayList;
import java.util.LinkedList;

import Symbol.Symbol;

public class ExpList extends Exp{
    //public ArrayList<Exp> exps; Had this at first which caused various errors throughout the code
    public LinkedList<Exp> exps;

    public ExpList(int p) {
        pos=p;
        this.exps = new ArrayList<Exp>();
    }

    public void add(Exp e) {
        this.exps.add(e);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
