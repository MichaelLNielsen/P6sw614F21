package sw614f21.p6project;

public class SymbolOccurrence extends Symbol {

    public int StartingTime;
    public int FinishingTime;

    public SymbolOccurrence (EventType SymbolID, int StartingTime, int FinishingTime){
        this.SymbolID = SymbolID;
        this.StartingTime = StartingTime;
        this.FinishingTime = FinishingTime;
    }


}
