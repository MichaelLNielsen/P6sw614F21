package sw614f21.p6project;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class ClusterSymbol extends PatternSymbol{
    public NumberFormat doubleFormat = new DecimalFormat("###.##");
    public double Mean;
    public double Deviation;
    public ArrayList<ClusterElement> ClusterElements;
    public ClusterSymbol(EventType symbolID, boolean start, double mean, double deviation){
        super(symbolID, start);
        Mean = mean;
        Deviation = deviation;
        
    }
    public ClusterSymbol( EventType symbolID, boolean start){
        super(symbolID, start);

    }
    
    @Override
    public String toString() {
        return SymbolID.toString().concat((Start ? "+" : "-").concat(" Mean: ").concat(doubleFormat.format(Mean)).concat(" Deviation: ").concat(doubleFormat.format(Deviation)));
    }
}
