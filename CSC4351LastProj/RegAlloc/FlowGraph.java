package RegAlloc;

import Graph.*;
import Temp.*;

public abstract class FlowGraph extends Graph {
    // Get the list of temporaries defined by this node
    public abstract TempList def(Node node);
    
    // Get the list of temporaries used by this node
    public abstract TempList use(Node node);
    
    // True if this node represents a move instruction
    public abstract boolean isMove(Node node);
    
    // Get the list of temporaries that are live out at this node
    public abstract TempList liveOut(Node node);
} 