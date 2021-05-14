package sw614f21.p6project;
import sw614f21.p6project.CODMiner.CODMiner;
import sw614f21.p6project.CODMiner.ClusterPattern;
import sw614f21.p6project.DataStructures.OccurrenceSequence;
import sw614f21.p6project.Preprocessing.CSVReader;
import sw614f21.p6project.TPMiner.TPMiner;
import sw614f21.p6project.TPMiner.TemporalPattern;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import sw614f21.p6project.CODMiner.ClusterSymbol;

import sw614f21.p6project.DataStructures.EndpointSequence;

public class Program {
    public static void main(String[] args) throws IOException{

        // Loading data from the CSV file and converting them to endpoint sequences:
        // ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetOccurrenceSequences();
        
        ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetBenchmarkSequences();
        ArrayList<EndpointSequence> OriginalDatabase  = CSVReader.GetEndpointSequences(occurrenceDB);
        
        // Mini synthetic data set.
        // FakeDataSet2 FS = new FakeDataSet2();
        // ArrayList<EndpointSequence> OriginalDatabase = FS.GetFakeData();
        
        
        TPMiner tpMiner = new TPMiner();
        CODMiner codMiner = new CODMiner();

        Runtime.getRuntime().gc();
        LocalTime before = LocalTime.now();
        
        System.out.println("Start tidspunkt : " + before);
        //ArrayList<TemporalPattern> patterns = tpMiner.TPMiner(1700, OriginalDatabase);
        ArrayList<ClusterPattern> patterns = codMiner.CODMiner(7000, 3600 * 1, 86400, OriginalDatabase);
        //Collections.sort(patterns);

        int timeSpent = LocalTime.now().toSecondOfDay() - before.toSecondOfDay();
        System.out.println("Before: " + before);
        System.out.println("After: " + LocalTime.now());
        System.out.println("Span in seconds: " + timeSpent);
        System.out.println("Patterns:" + patterns.size());
        for (int i = 0; i < patterns.size(); i++){
           //System.out.println("Pattern = " + patterns.get(i).TPattern.toString() + " Support: " + patterns.get(i).Support);
           System.out.println("Pattern = " + patterns.get(i).Pattern.toString());
        }
        
        
        // to create probabilistic information.
        ClusterSymbol firstSymbol = patterns.get(0).Pattern.get(0);
        HashMap<Integer, Double> probabilities = new HashMap<>();
        for (int i = 0; i <= 86400; i += 1){
            int k = i / 3600;
            double prob =  probabilities.getOrDefault(k, 0d);
            prob += firstSymbol.f(i);
            probabilities.put(k, prob);
        }
        for (int i = 0; i < probabilities.keySet().size();i++){
            //System.out.println(i + " . " + String.format("%.7f", probabilities.get(i)) );
            System.out.println( String.format("%.7f", probabilities.get(i)) );
        }
        
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
