package sw614f21.p6project.DataStructures;

public class SymbolOccurrence extends Symbol {

    public int StartingTime;
    public int FinishingTime;


    public SymbolOccurrence (String SymbolID, int StartingTime){
        this.EventID = SymbolID;
        this.StartingTime = StartingTime;
    }

    @Override
    public String toString() {
        return "(" + EventID + ", " + StartingTime + ", " + FinishingTime + ")";
    }
}
