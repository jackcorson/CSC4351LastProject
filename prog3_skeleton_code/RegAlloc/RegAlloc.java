package RegAlloc;
import Temp.*;
import Graph.*;
import java.util.*;

public class RegAlloc implements TempMap {
  Frame.Frame frame;
  Assem.InstrList instrs;
  private HashMap<Temp, String> colorMap = new HashMap<>();
  private TempList spilledNodes = null;
  private HashMap<Temp, Integer> sizeMap = new HashMap<>();

  public RegAlloc(Frame.Frame f, Assem.InstrList i) {
    frame = f;
    instrs = i;
    allocateRegisters();
  }

  private void allocateRegisters() {
    // Build interference graph
    FlowGraph flowGraph = new AssemFlowGraph(instrs);
    InterferenceGraph interferenceGraph = new InterferenceGraph(flowGraph);
    
    // Get list of available registers from frame
    TempList registers = frame.registers();
    
    // Color the graph
    Color coloring = new Color(interferenceGraph, frame.registers());
    
    // Handle register assignment
    TempList temps = coloring.spills();
    if (temps != null) {
      spilledNodes = temps;
      // In a real implementation, we would:
      // 1. Rewrite the instruction stream to spill these temps to memory
      // 2. Rebuild the interference graph
      // 3. Try coloring again
    }
    
    // Store the coloring
    for (Node node : interferenceGraph.nodes()) {
      Temp temp = ((TempNode)node).temp;
      String color = coloring.color(temp);
      if (color != null) {
        colorMap.put(temp, color);
      }
    }
  }

  public String tempMap(Temp t) {
    // First check our coloring
    String color = colorMap.get(t);
    if (color != null) return color;
    
    // Then check frame's mapping
    String s = frame.tempMap(t);
    if (s != null) return s;
    
    // Finally fall back to temp's string representation
    return t.toString();
  }

  public Assem.InstrList instrs() {
    return instrs;
  }

  public void show(java.io.PrintWriter out, TempMap map) {
    out.println("Register Allocation:");
    for (Map.Entry<Temp, String> entry : colorMap.entrySet()) {
      out.println(entry.getKey() + " -> " + entry.getValue());
    }
    if (spilledNodes != null) {
      out.println("\nSpilled Nodes:");
      for (TempList l = spilledNodes; l != null; l = l.tail) {
        out.println(l.head);
      }
    }
  }
}

class AssemFlowGraph extends FlowGraph {
  private HashMap<Node, TempList> defMap = new HashMap<>();
  private HashMap<Node, TempList> useMap = new HashMap<>();
  private HashMap<Node, Boolean> moveMap = new HashMap<>();
  private HashMap<Node, TempList> liveOutMap = new HashMap<>();
  private HashMap<Node, Assem.Instr> instrMap = new HashMap<>();

  public AssemFlowGraph(Assem.InstrList instrs) {
    // Build the flow graph from assembly instructions
    Node prev = null;
    for (Assem.InstrList l = instrs; l != null; l = l.tail) {
      Node n = new Node();
      addNode(n);
      instrMap.put(n, l.head);
      
      if (l.head instanceof Assem.MOVE) {
        Assem.MOVE m = (Assem.MOVE)l.head;
        defMap.put(n, new TempList(m.dst, null));
        useMap.put(n, new TempList(m.src, null));
        moveMap.put(n, true);
      } else if (l.head instanceof Assem.OPER) {
        Assem.OPER o = (Assem.OPER)l.head;
        defMap.put(n, o.dst);
        useMap.put(n, o.src);
        moveMap.put(n, false);
      } else {
        defMap.put(n, null);
        useMap.put(n, null);
        moveMap.put(n, false);
      }
      
      // Add edge from previous instruction if not a jump
      if (prev != null) {
        prev.addEdge(n);
      }
      prev = n;
    }
  }

  public TempList def(Node node) {
    return defMap.get(node);
  }

  public TempList use(Node node) {
    return useMap.get(node);
  }

  public boolean isMove(Node node) {
    return moveMap.getOrDefault(node, false);
  }

  public TempList liveOut(Node node) {
    return liveOutMap.get(node);
  }
}

class InterferenceGraph extends Graph {
  public InterferenceGraph(FlowGraph flow) {
    // Build interference graph from flow graph
    // Two temps interfere if one is live at the definition of the other
  }
}

class Color {
  private HashMap<Temp, String> coloring = new HashMap<>();
  private TempList spills = null;

  public Color(InterferenceGraph ig, TempList registers) {
    // Implement graph coloring algorithm
    // Use Chaitin's algorithm or similar
  }

  public String color(Temp temp) {
    return coloring.get(temp);
  }

  public TempList spills() {
    return spills;
  }
} 