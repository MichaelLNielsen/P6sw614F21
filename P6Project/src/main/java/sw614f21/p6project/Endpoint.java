package sw614f21.p6project;

/**
 *
 * @author Michael
 */
public class Endpoint extends Symbol implements Comparable<Endpoint>{
    public Integer Timestamp;
    public Integer OccurrenceID;
    public boolean Start;
    public boolean Visited = false;
    
    
    public Endpoint (EventType symbol, int timestamp, boolean start, int occurrenceID){
        SymbolID = symbol;
        Timestamp = timestamp;
        Start = start;
        OccurrenceID = occurrenceID;
    }

    @Override
    public int compareTo(Endpoint o) {
        int evaluation = Timestamp.compareTo(o.Timestamp);
        if (evaluation == 0){
            int evaluation2 = SymbolID.compareTo(o.SymbolID);
            if (evaluation2 == 0){
                return OccurrenceID.compareTo(o.OccurrenceID);
            }
            return evaluation2;
        }
        return evaluation;
    }
    @Override
    public String toString(){
        String output = SymbolID.toString() + " " + (Start ? '+' :  '-') + " " + Timestamp.toString();
        return output;
    }
}
