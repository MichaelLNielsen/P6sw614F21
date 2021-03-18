package sw614f21.p6project;
import java.io.IOException;
import java.util.ArrayList;

public class Program {
    public static void main(String[] args) throws IOException{
        
//        System.out.print("We did it!");

        ArrayList<OccurrenceSequence> dataBase = CSVReader.GetOccurrenceSequences();
        for (int i = 0; i < dataBase.size(); i++){
            ArrayList<SymbolOccurrence> sequence =dataBase.get(i).Sequence;
            System.out.print(i + ": ");

            for (int j = 0; j < sequence.size(); j++){
                System.out.print(sequence.get(j).SymbolID+" ");

            }
            System.out.println("");

        }

    }
}
