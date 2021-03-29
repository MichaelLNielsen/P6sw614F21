package sw614f21.p6project;

import java.util.ArrayList;

public class ClusterSymbol extends PatternSymbol{
    
    public double Mean;
    public double Deviation;
    public ArrayList<ClusterElement> ClusterElements;
    public ClusterSymbol( EventType symbolID, boolean start, double mean, double deviation){
        super(symbolID, start);
        Mean = mean;
        Deviation = deviation;
    }
    public ClusterSymbol( EventType symbolID, boolean start){
        super(symbolID, start);

    }
}
