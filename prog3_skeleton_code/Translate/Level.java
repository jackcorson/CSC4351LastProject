package Translate;
import Frame.*;
import Symbol.Symbol;
import Temp.Label;
import Util.BoolList;

public class Level {
  public Frame frame;
  public Level parent;
  public AccessList formals;
  private Label name;

  public Level(Frame f) {
    frame = f;
    parent = null;
    formals = null;
    name = new Label();
  }

  public Level(Level p, Symbol n, BoolList f) {
    parent = p;
    name = new Label(n.toString());
    frame = p.frame.newFrame(n, f);
    formals = allocFormals(null);  // Initialize with empty formals
  }

  private AccessList allocFormals(AccessList formals) {
    return formals;  // For now, just return the passed formals
  }

  public Label name() {
    return name;
  }

  public TranslateAccess allocLocal(boolean escape) {
    return new TranslateAccess(this, frame.allocLocal(escape));
  }
}
