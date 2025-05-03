package Translate;
import Frame.Access;
import Tree.Exp;

public class TranslateAccess {
    Level home;
    Access frameAccess;
    
    TranslateAccess(Level h, Access a) {
        home = h;
        frameAccess = a;
    }
    
    public String toString() {
        return "[" + home.name() + "," + frameAccess.toString() + "]";
    }

    public Tree.Exp exp(Tree.Exp fp) {
        return frameAccess.exp(fp);
    }
}
