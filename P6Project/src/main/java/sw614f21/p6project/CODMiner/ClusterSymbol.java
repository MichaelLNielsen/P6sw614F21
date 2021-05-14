package sw614f21.p6project.CODMiner;

import sw614f21.p6project.TPMiner.PatternSymbol;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Collections;

public class ClusterSymbol extends PatternSymbol {
    public NumberFormat doubleFormat = new DecimalFormat("###.##");
    public ArrayList<Integer> symbolData;
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
    
    
    public double f (Integer x){
        double h = h();
        double constant = 1 / (symbolData.size() * h);
        double sum = 0;
        for (int i = 0; i < symbolData.size(); i++){
            sum = sum + K(((x-symbolData.get(i)) / h));
        }
        return constant * sum;
    }
    
    public double K(double x){
        double constant = 1 / Math.sqrt(2 * Math.PI);
        double exponent = Math.exp(-0.5 * Math.pow(x, 2));
        return constant * exponent;
    }
    
    public double h(){
        Integer max = Collections.max(symbolData);
        Integer min = Collections.min(symbolData);
        return (max-min)/Math.sqrt(symbolData.size());
    }
    
    
    
    @Override
    public String toString() {
        //return EventID.concat((Start ? "+" : "-").concat(" Mean: ").concat(doubleFormat.format(Mean)).concat(" Deviation: ").concat(doubleFormat.format(Deviation)));
        return EventID.concat((Start ? "+" : "-"));
    }
}
