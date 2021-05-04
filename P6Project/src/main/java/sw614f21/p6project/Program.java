package sw614f21.p6project;
import sw614f21.p6project.CODMiner.CODMiner;
import sw614f21.p6project.CODMiner.ClusterPattern;
import sw614f21.p6project.DataStructures.OccurrenceSequence;
import sw614f21.p6project.Preprocessing.CSVReader;
import sw614f21.p6project.TPMiner.TPMiner;
import sw614f21.p6project.TPMiner.TemporalPattern;

import java.io.IOException;
import java.util.ArrayList;

public class Program {
    public static void main(String[] args) throws IOException{

        CODMiner cultMiner = new CODMiner();
        ArrayList<ClusterPattern> patterns = cultMiner.CODMiner(2, 8, 800);

        System.out.println("CODMiner:");
        for (int i = 0; i < patterns.size(); i++){
           System.out.println("Pattern = " + patterns.get(i).Pattern.toString());
        }

        TPMiner tpMiner = new TPMiner();
        ArrayList<TemporalPattern> tpatterns = tpMiner.TPMiner(2);

        System.out.println("TPMiner:");
        for (int i = 0; i < tpatterns.size(); i++){
            System.out.println("Pattern = " + tpatterns.get(i).TPattern.toString());
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
