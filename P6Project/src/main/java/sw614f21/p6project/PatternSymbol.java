package sw614f21.p6project;

public class PatternSymbol extends Symbol {

    public boolean Start;

    public PatternSymbol(EventType symbolID, boolean start) {
        SymbolID = symbolID;
        Start = start;
    }

    @Override
    public String toString() {
        return SymbolID.toString() + (Start ? "+" : "-");
    }
}
