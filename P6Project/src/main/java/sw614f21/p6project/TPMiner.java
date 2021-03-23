package sw614f21.p6project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TPMiner {

    public ArrayList<TemporalPattern> TP = new ArrayList<TemporalPattern>();


    public void TPMine (int minSupport) throws IOException {

        ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetOccurrenceSequences();
        ArrayList<EndpointSequence> endpointDB = CSVReader.GetEndpointSequences(occurrenceDB);


        // Find all frequent endpoints, and remove infrequent endpoints in DB.
        GetFrequentStartingEndpoints (endpointDB, minSupport);

    }

    public ArrayList<PatternSymbol> GetFrequentStartingEndpoints (ArrayList<EndpointSequence> endpointDB, int minSupport) {

        HashMap<EventType, Integer> symbolCounter = new HashMap<EventType, Integer>();

        for (int i = 0; i < endpointDB.size(); i++) {

            ArrayList<Endpoint> sequence =  endpointDB.get(i).Sequence;

            for (int j = 0; j < sequence.size(); j++) {
                Endpoint endpoint = sequence.get(j);

                if (endpoint.Start) {
                    int count = symbolCounter.getOrDefault(endpoint.SymbolID, 0);
                    symbolCounter.put(endpoint.SymbolID, count + 1);
                }
            }

        }

//        System.out.println(symbolCounter);

        for (EndpointSequence es : endpointDB){
            System.out.print(es.Sequence.size() + " ");
        }
        System.out.println("");

        for (int i = 0; i < endpointDB.size(); i++) {

            ArrayList<Endpoint> sequence = endpointDB.get(i).Sequence;
            ArrayList<Endpoint> toBeRemoved = new ArrayList<Endpoint>();

            for (int j = 0; j < sequence.size(); j++) {
                Endpoint endpoint = sequence.get(j);

                if (symbolCounter.get(endpoint.SymbolID) < minSupport) {
                    toBeRemoved.add(endpoint);
                }
            }

            sequence.removeAll(toBeRemoved);

        }

        // Husk at fjerne helt tomme sekvenser også!
        for (EndpointSequence es : endpointDB){
            System.out.print(es.Sequence.size() + " ");
        }
        System.out.println("");

        return null;
    }


}
