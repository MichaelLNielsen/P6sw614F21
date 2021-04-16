package sw614f21.p6project;

import java.util.Objects;

public class PatternSymbol extends Symbol {

    public boolean Start;
    public boolean SameTimeAsPrevious;
    public String PreviousSymbol;

    public PatternSymbol(String symbolID, boolean start) {
        EventID = symbolID;
        Start = start;
        SameTimeAsPrevious = false;
    }


    @Override
    public String toString() {
        return EventID.concat((Start ? "+" : "-"));
    }


    @Override
    public int hashCode(){
        return Objects.hash(EventID, Start, SameTimeAsPrevious);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternSymbol that = (PatternSymbol) o;
        return Start == that.Start && EventID.equals(that.EventID) && SameTimeAsPrevious == that.SameTimeAsPrevious;
    }
}
