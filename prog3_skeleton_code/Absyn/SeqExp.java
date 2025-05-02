package Absyn;
import java.util.List;
import java.util.ArrayList;

public class SeqExp extends Exp {
    public ExpList list;

    public SeqExp(int p, ExpList l) {
        pos = p;
        list = l;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
} 