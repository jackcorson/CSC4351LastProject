package Absyn;

public class Int extends Exp {
    public int value;
    public int lin;

    public Int(int line, int v) {
        value = v;
        lin = line;
    }

    @Override
    public void accept(Visitor v) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }
}