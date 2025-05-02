package Mips;

class InFrame extends Frame.Access {
  int offset;
  Temp.Label name;  // For global variables
  int size;  // Size in bytes

  InFrame(int o) {
    super();
    offset = o;
    size = 4;  // Default word size
  }

  InFrame(int o, Temp.Label l) {
    this(o, l, 4);  // Default to word size
  }

  InFrame(int o, Temp.Label l, int s) {
    super(l);
    offset = o;
    name = l;
    size = s;
  }

  public Tree.Exp exp(Tree.Exp framePtr) {
    Tree.Exp addr;
    if (name != null) {
      // Global variable
      addr = new Tree.NAME(name);
    } else {
      addr = new Tree.BINOP(Tree.BINOP.PLUS,
                           framePtr,
                           new Tree.CONST(offset));
    }
    
    Tree.MEM mem = new Tree.MEM(addr);
    mem.size = size;  // Set the size for proper load/store generation
    return mem;
  }

  public String toString() {
    if (name != null) {
      return name.toString() + "(size=" + size + ")";
    }
    return offset + "(size=" + size + ")";
  }
}