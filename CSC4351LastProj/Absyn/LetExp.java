package Absyn;
import java.util.ArrayList;

public class LetExp extends Exp {
    public ArrayList<Dec> decs;
    public Exp body;

    public LetExp(ArrayList<Dec> d, Exp b) {
        decs = d;
        body = b;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
} 