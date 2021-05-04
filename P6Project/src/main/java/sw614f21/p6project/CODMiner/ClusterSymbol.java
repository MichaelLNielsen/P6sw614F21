package sw614f21.p6project.CODMiner;

import sw614f21.p6project.TPMiner.PatternSymbol;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class ClusterSymbol extends PatternSymbol {
    public NumberFormat doubleFormat = new DecimalFormat("###.##");
    public double Mean;
    public double Deviation;
    public ArrayList<ClusterElement> ClusterElements;
    public ClusterSymbol(String symbolID, boolean start, double mean, double deviation){
        super(symbolID, start);
        Mean = mean;
        Deviation = deviation;
        
    }
    public ClusterSymbol( String symbolID, boolean start){
        super(symbolID, start);

    }
    
    @Override
    public String toString() {
        return EventID.concat((Start ? "+" : "-").concat(" Mean: ").concat(doubleFormat.format(Mean)).concat(" Deviation: ").concat(doubleFormat.format(Deviation)));
    }
}
