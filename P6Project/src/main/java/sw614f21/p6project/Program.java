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

public class Program {
    public static void main(String[] args) throws IOException{


        //TPMiner tpMiner = new TPMiner();
        CODMiner codMiner = new CODMiner();
//
        Runtime.getRuntime().gc();
        LocalTime before = LocalTime.now();
        
        System.out.println("Start tidspunkt : " + before);
        //ArrayList<TemporalPattern> patterns = tpMiner.TPMine(1700);
        ArrayList<ClusterPattern> patterns = codMiner.CODMiner(5000, 43200, 86400);

//          Random Commentar
        int timeSpent = LocalTime.now().toSecondOfDay() - before.toSecondOfDay();
        System.out.println("Before: " + before);
        System.out.println("After: " + LocalTime.now());
        System.out.println("Span in seconds: " + timeSpent);
        System.out.println("Patterns:" + patterns.size());
        System.out.println(patterns.size());
        for (int i = 0; i < patterns.size(); i++){
           System.out.println("Pattern = " + patterns.get(i).Pattern.toString());        
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
