package sw614f21.p6project;

import java.util.Objects;

public class PatternSymbol extends Symbol {

    public boolean Start;

    public PatternSymbol(EventType symbolID, boolean start) {
        SymbolID = symbolID;
        Start = start;
    }

    @Override
    public String toString() {
        return SymbolID.toString().concat((Start ? "+" : "-"));
    }


    @Override
    public int hashCode(){
        return Objects.hash(SymbolID, Start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternSymbol that = (PatternSymbol) o;
        return Start == that.Start && SymbolID == that.SymbolID;
    }
}
