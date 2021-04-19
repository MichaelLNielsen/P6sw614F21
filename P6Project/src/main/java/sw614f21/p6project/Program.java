package sw614f21.p6project;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Program {
    public static void main(String[] args) throws IOException{
    
        
        ArrayList<OccurrenceSequence> sequences = CSVReader.GetBenchmarkSequences();
        PrintDataStatistics(sequences);
        
//        TPMiner tpMiner = new TPMiner();
//        CulturedMiner cultMiner = new CulturedMiner();
//
//        ArrayList<ClusterPattern> patterns = cultMiner.CultureMine(1400, 86400, 800);
//        ArrayList<TemporalPattern> patterns = tpMiner.TPMine(1400);
//          Random Commentar
//        System.out.println(patterns.size());
//        for (int i = 0; i < patterns.size(); i++){
//           System.out.println("Pattern = " + patterns.get(i));
//        }

//        ArrayList<OccurrenceSequence> output = CSVReader.GetBenchmarkSequences();
//        ArrayList<EndpointSequence> endpointsequences = CSVReader.GetEndpointSequences(output);
//        for (EndpointSequence seq : endpointsequences) {
//            System.out.println(seq.Sequence);
//        }
//        System.out.println("Number of days: " + output.size());
    }


    
    public static void PrintDataStatistics(ArrayList<OccurrenceSequence> sequences){
        int numberOfSequences = sequences.size();
        double averageSequenceLength = 0;
        double standardDeviation = 0;
        int minNumberOfEvents = Integer.MAX_VALUE;
        int maxNumberOfEvents = Integer.MIN_VALUE;
        int numberOfEventTypes = CSVReader.GetHouse1Files().size() + CSVReader.GetHouse2Files().size();

        int sizeOfSequence = 0;
        for (int i = 0; i < sequences.size(); i++){
            sizeOfSequence = sequences.get(i).Sequence.size();
            
            averageSequenceLength += sizeOfSequence;
            standardDeviation += Math.pow(sizeOfSequence, 2);
            if (sizeOfSequence < minNumberOfEvents){
                minNumberOfEvents = sizeOfSequence;
            }
            if (sizeOfSequence > maxNumberOfEvents){
                maxNumberOfEvents = sizeOfSequence;
            }
        }
        
        averageSequenceLength = averageSequenceLength / numberOfSequences;
        
        standardDeviation = Math.sqrt(standardDeviation / (numberOfSequences-1));
        
        System.out.println("Number of sequences: " + numberOfSequences);
        System.out.println("Average sequence length: " + averageSequenceLength);
        System.out.println("Standard deviation: " + standardDeviation);
        System.out.println("Min length: " + minNumberOfEvents);
        System.out.println("Max length: " + maxNumberOfEvents);
        System.out.println("Number of event types: " + numberOfEventTypes);
    }
    
}
