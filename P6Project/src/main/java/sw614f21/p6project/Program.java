package sw614f21.p6project;
import java.io.IOException;
import java.util.ArrayList;

public class Program {
    public static void main(String[] args) throws IOException{
        
        
        ArrayList<Integer> k = new ArrayList<Integer>();
        
  
        ArrayList<PatternSymbol> testliste = new ArrayList<PatternSymbol>();
        
        TPMiner tpMiner = new TPMiner();

        ArrayList<TemporalPattern> patterns = tpMiner.TPMine(300);

        System.out.println(patterns.size());
        for (int i = 0; i < patterns.size(); i++){
            System.out.println("Pattern = " + patterns.get(i).TPattern);
        }
        
    }
}
