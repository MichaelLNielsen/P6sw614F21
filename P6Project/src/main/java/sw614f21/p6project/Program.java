package sw614f21.p6project;
import java.io.IOException;
import java.util.ArrayList;

public class Program {
    public static void main(String[] args) throws IOException{
        
        //TPMiner tpMiner = new TPMiner();
        CulturedMiner cultMiner = new CulturedMiner();

        cultMiner.CultureMine(1,3);
        //ArrayList<TemporalPattern> patterns = tpMiner.TPMine(2);

        //for (int i = 0; i < patterns.size(); i++){
       //    System.out.println("Pattern = " + patterns.get(i).TPattern);
        //}
    }
}
