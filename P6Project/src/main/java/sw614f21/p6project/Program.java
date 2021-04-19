package sw614f21.p6project;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Program {
    public static void main(String[] args) throws IOException{
    
        TPMiner tpMiner = new TPMiner();
//        CulturedMiner cultMiner = new CulturedMiner();
//
//        ArrayList<ClusterPattern> patterns = cultMiner.CultureMine(1400, 86400, 800);
        ArrayList<TemporalPattern> patterns = tpMiner.TPMine(1400);
//          Random Commentar
        System.out.println(patterns.size());
        for (int i = 0; i < patterns.size(); i++){
           System.out.println("Pattern = " + patterns.get(i));
        }

//        ArrayList<OccurrenceSequence> output = CSVReader.GetBenchmarkSequences();
//        ArrayList<EndpointSequence> endpointsequences = CSVReader.GetEndpointSequences(output);
//        for (EndpointSequence seq : endpointsequences) {
//            System.out.println(seq.Sequence);
//        }
//        System.out.println("Number of days: " + output.size());
    }


}
