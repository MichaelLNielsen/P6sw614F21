package sw614f21.p6project;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Program {
    public static void main(String[] args) throws IOException{
        
        //TPMiner tpMiner = new TPMiner();
//        CulturedMiner cultMiner = new CulturedMiner();
//
//        ArrayList<ClusterPattern> patterns = cultMiner.CultureMine(100, 1, 1);
////        ArrayList<TemporalPattern> patterns = tpMiner.TPMine(2);
//        System.out.println(patterns.size());
//        for (int i = 0; i < patterns.size(); i++){
//           System.out.println("Pattern = " + patterns.get(i).Pattern );
//        }

        ArrayList<OccurrenceSequence> output = CSVReader.GetBenchmarkSequences();
        for (OccurrenceSequence seq : output) {
            System.out.println(seq.Sequence);
        }
        System.out.println("Number of days: " + output.size());
    }


}
