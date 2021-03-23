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
        ArrayList<PatternSymbol> FE = GetFrequentStartingEndpoints (endpointDB, minSupport);


        for (int i = 0; i < FE.size(); i++) {
            ArrayList<PatternSymbol> symbol = new ArrayList<PatternSymbol>();
            symbol.add(FE.get(i));

            ArrayList<EndpointSequence> projectedDB = GetProjectedDB(endpointDB, symbol);


        }

    }


    public ArrayList<PatternSymbol> GetFrequentStartingEndpoints (ArrayList<EndpointSequence> endpointDB, int minSupport) {

        HashMap<EventType, Integer> symbolCounter = new HashMap<EventType, Integer>();
        ArrayList<PatternSymbol> resultList = new ArrayList<PatternSymbol>();

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

        ArrayList<EndpointSequence> sequencesToBeRemoved = new ArrayList<EndpointSequence>();

        for (int i = 0; i < endpointDB.size(); i++) {

            ArrayList<Endpoint> sequence = endpointDB.get(i).Sequence;
            ArrayList<Endpoint> endpointsToBeRemoved = new ArrayList<Endpoint>();

            for (int j = 0; j < sequence.size(); j++) {
                Endpoint endpoint = sequence.get(j);

                if (symbolCounter.get(endpoint.SymbolID) < minSupport) {
                    endpointsToBeRemoved.add(endpoint);
                }
            }

            sequence.removeAll(endpointsToBeRemoved);

            if (sequence.isEmpty()) {
                sequencesToBeRemoved.add(endpointDB.get(i));
            }

        }

        endpointDB.removeAll(sequencesToBeRemoved);


        ArrayList<EventType> keys = new ArrayList<EventType>(symbolCounter.keySet());


        for (int i = 0; i < keys.size(); i++) {
            EventType type = keys.get(i);
            if (symbolCounter.get(type) >= minSupport) {

                PatternSymbol symbol = new PatternSymbol(type, true);
                resultList.add(symbol);
            }
        }
        System.out.println(resultList);
        System.out.println(resultList.size());

        return resultList;
    }


    private ArrayList<EndpointSequence> GetProjectedDB(ArrayList<EndpointSequence> endpointDB, ArrayList<PatternSymbol> patternSymbols) {

        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();
        int IDCounter = 0; // Måske lave det til en global variabel, hvis vi laver rekursion.

        for (int i = 0; i < endpointDB.size(); i++) {
            EndpointSequence endpointSequence = endpointDB.get(i);

            int k = 0;
            for (int j = 0; j < patternSymbols.size(); j++ ){

                for (; k < endpointSequence.Sequence.size(); k++){
                    if(patternSymbols.get(j).SymbolID != endpointSequence.Sequence.get(k).SymbolID){
                        continue;
                    }
                    else {
                        // Måske lave rekursion, hver gang vi møder et FE-symbol
                        break;
                    }
                }
            }
            EndpointSequence newEndpointSequence = new EndpointSequence(IDCounter++, new ArrayList<Endpoint>());
            for (; k < endpointSequence.Sequence.size(); k++){
                newEndpointSequence.Sequence.add(endpointSequence.Sequence.get(k));
            }
            if (!newEndpointSequence.Sequence.isEmpty()){
                projectedDB.add(newEndpointSequence);
            }

        }


        return null;

    }
}
