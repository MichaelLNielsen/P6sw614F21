package sw614f21.p6project;


import java.util.ArrayList;

public class OccurrenceSequence {

    public ArrayList<SymbolOccurrence> Sequence;
    public int ID;

    public OccurrenceSequence(int ID, ArrayList<SymbolOccurrence> Sequence) {
        this.ID = ID;
        this.Sequence = Sequence;
    }

}
