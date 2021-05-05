package sw614f21.p6project;
import sw614f21.p6project.CODMiner.*;
import sw614f21.p6project.DataStructures.OccurrenceSequence;
import sw614f21.p6project.Preprocessing.CSVReader;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import sw614f21.p6project.TPMiner.*;

public class Program {
    public static void main(String[] args) throws IOException{
    
        
        
        
        TPMiner tpMiner = new TPMiner();
        CODMiner codMiner = new CODMiner();
        LocalTime before = LocalTime.now();
        System.out.println("Start: " + before);
        
//        ArrayList<ClusterPattern> patterns = codMiner.CODMiner(2000, 3600, 86400);
        ArrayList<TemporalPattern> patterns = tpMiner.TPMiner(2000);

        LocalTime after = LocalTime.now();
        System.out.println("Before: " + before);
        System.out.println("After: " + after);
        System.out.println("Span in seconds: " + (after.toSecondOfDay() - before.toSecondOfDay()));
        System.out.println("Patterns: " + patterns.size());
        
        //printing the patterns.
        for (int i = 0; i < patterns.size(); i++){
           System.out.println("Pattern = " + patterns.get(i));
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
