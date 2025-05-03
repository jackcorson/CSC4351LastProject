package Translate;
import Temp.Label;
import Temp.Temp;

class IfThenElseExp extends Exp {
  Exp cond, a, b;
  Label t = new Label();
  Label f = new Label();
  Label join = new Label();

  IfThenElseExp(Exp cc, Exp aa, Exp bb) {
    cond = cc; 
    a = aa; 
    b = bb;
  }

  Tree.Stm unCx(Label tt, Label ff) {
    // This is the naive implementation; you should extend it to eliminate
    // unnecessary JUMP nodes
    Tree.Stm aStm = a.unCx(tt, ff);
    Tree.Stm bStm = b.unCx(tt, ff);

    Tree.Stm condStm = cond.unCx(t, f);

    if (aStm == null && bStm == null)
      return condStm;
    if (aStm == null)
      return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(f), bStm));
    if (bStm == null)
      return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(t), aStm));
    return new Tree.SEQ(condStm,
			new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), aStm),
				     new Tree.SEQ(new Tree.LABEL(f), bStm)));
  }

  Tree.Exp unEx() {

    Temp r = new Temp();
    Tree.Exp aExp = a.unEx();
    if (aExp == null)
      return null;
    Tree.Exp bExp = b.unEx();
    if (bExp == null)
      return null;
    return new Tree.ESEQ
      (new Tree.SEQ
       (new Tree.SEQ(cond.unCx(t, f),
		     new Tree.SEQ(new Tree.SEQ
				  (new Tree.LABEL(t),
				   new Tree.SEQ
				   (new Tree.MOVE(new Tree.TEMP(r), aExp),
				    new Tree.JUMP(join))),
				  new Tree.SEQ
				  (new Tree.LABEL(f),
				   new Tree.SEQ
				   (new Tree.MOVE(new Tree.TEMP(r), bExp),
				    new Tree.JUMP(join))))),
	new Tree.LABEL(join)),
       new Tree.TEMP(r));

  }


  Tree.Stm unNx() {

    Tree.Stm aStm = a.unNx();
    if (aStm == null)
      t = join;
    else
      aStm = new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), aStm),
			  new Tree.JUMP(join));

    Tree.Stm bStm = b.unNx();
    if (bStm == null)
      f = join;
    else
      bStm = new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(f), bStm),
			  new Tree.JUMP(join));
    
    if (aStm == null && bStm == null)
      return cond.unNx();

    Tree.Stm condStm = cond.unCx(t, f);

    if (aStm == null)
      return new Tree.SEQ(new Tree.SEQ(condStm, bStm), new Tree.LABEL(join));

    if (bStm == null)
      return new Tree.SEQ(new Tree.SEQ(condStm, aStm), new Tree.LABEL(join));

    return new Tree.SEQ(new Tree.SEQ(condStm, new Tree.SEQ(aStm, bStm)),
			new Tree.LABEL(join));
      
  }
}