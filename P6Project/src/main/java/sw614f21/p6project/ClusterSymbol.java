package sw614f21.p6project;

public class ClusterSymbol extends PatternSymbol{
    
    public double Mean;
    public double Deviation;
    
    public ClusterSymbol( EventType symbolID, boolean start, double mean, double deviation){
        super(symbolID, start);
        Mean = mean;
        Deviation = deviation;
    }
}
