package sw614f21.p6project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


public class CulturedMiner {
    public ArrayList<TemporalPattern> TP = new ArrayList<TemporalPattern>();

    public ArrayList<TemporalPattern> CultureMine(int minSupport, double maxClusterDeviation){
        FakeDataSet FDS = new FakeDataSet();
        ArrayList<EndpointSequence> OriginalDatabase = FDS.GetFakeData();
        
        
        //getting the frequent endpoints.
        ArrayList<ClusterSymbol> FE = GetFrequentStartingEndpoints(OriginalDatabase, minSupport);
        
        for (int i = 0; i < FE.size(); i++){
            ClusterSymbol symbol = new ClusterSymbol(FE.get(i).SymbolID, FE.get(i).Start, FE.get(i).Mean, FE.get(i).Deviation);
            ArrayList<ClusterSymbol> tempInput = new ArrayList<ClusterSymbol>();
            tempInput.add(symbol);
            ArrayList<EndpointSequence> projectedDB = GetProjectedDB(OriginalDatabase, symbol, true);
            //TemporalPattern temp = new TemporalPattern(tempInput);
            
            //TPSpan(temp, projectedDB, minSupport);
            
            
        }
        return TP;
    }
    
    public ArrayList<ClusterSymbol> GetFrequentStartingEndpoints (ArrayList<EndpointSequence> endpointDB, int minSupport) {

        HashMap<EventType, ArrayList<Integer>> symbolCounter = new HashMap<EventType, ArrayList<Integer>>();
        // the list contains all the timestamps for each symbol.
        
        ArrayList<ClusterSymbol> resultList = new ArrayList<ClusterSymbol>();

        // counts the number of occurrences for each symbol type.
        for (int i = 0; i < endpointDB.size(); i++) {

            ArrayList<Endpoint> sequence = endpointDB.get(i).Sequence;

            for (int j = 0; j < sequence.size(); j++) {
                Endpoint endpoint = sequence.get(j);

                if (endpoint.Start) {
                    ArrayList<Integer> symboldata = symbolCounter.getOrDefault(endpoint.SymbolID, new ArrayList<Integer>());
                    symboldata.add(endpoint.Timestamp);
                    symbolCounter.put(endpoint.SymbolID, symboldata);
                }
            }

        }

        ArrayList<EndpointSequence> sequencesToBeRemoved = new ArrayList<EndpointSequence>();
        //remove infrequent symbols.
        for (int i = 0; i < endpointDB.size(); i++) {

            ArrayList<Endpoint> sequence = endpointDB.get(i).Sequence;
            ArrayList<Endpoint> endpointsToBeRemoved = new ArrayList<Endpoint>();

            for (int j = 0; j < sequence.size(); j++) {
                Endpoint endpoint = sequence.get(j);

                if (symbolCounter.get(endpoint.SymbolID).size() < minSupport) {
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
            ArrayList<Integer> symbolData = symbolCounter.get(type);
            
            if (symbolData.size() >= minSupport) {

                double mean = FindMean(symbolData);
                double deviation = Collections.max(symbolData) - Collections.min(symbolData);
                ClusterSymbol symbol = new ClusterSymbol(type, true, mean, deviation);
                
                resultList.add(symbol);
            }
        }

        return resultList;
    }
    
    public double FindMean (ArrayList<Integer> data){
        double sum= 0;
        for (int i = 0; i < data.size(); i++){
            sum += (double)data.get(i);
        }
        
        return sum/(double)data.size();
    }
    
    int DBSequenceID = 0;
    private ArrayList<EndpointSequence> GetProjectedDB(ArrayList<EndpointSequence> inputDB, PatternSymbol patternSymbol, boolean first) {
        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();

        // iterate through each sequence, to project them.
        for (int i = 0; i < inputDB.size(); i++) {
            EndpointSequence endpointSequence = inputDB.get(i);
            
            //skip ahead to the last symbol in the pattern.
            int k = 0;
            for (; k < endpointSequence.Sequence.size(); k++) {
                if (patternSymbol.SymbolID == endpointSequence.Sequence.get(k).SymbolID){
                    if (first){
                        RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                    }
                    k++;
                    break;
                }
            }
            
            if (first && k > 0){
                createSequences(endpointSequence, k, projectedDB);
            }
            else {
                EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
                for (; k < endpointSequence.Sequence.size(); k++){
                    newEndpointSequence.Sequence.add(endpointSequence.Sequence.get(k));
                }
            }
        }
        return projectedDB;
    }
    
    public void RecursivePostfixScan (EndpointSequence endpointSequence, ArrayList<EndpointSequence> projectedDB, PatternSymbol patternSymbol, int k) {
        k = k + 1;
        for (; k < endpointSequence.Sequence.size(); k++) {
            if (patternSymbol.SymbolID == endpointSequence.Sequence.get(k).SymbolID) {
                RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                k++;
                break;
            }
        }
        createSequences(endpointSequence, k, projectedDB);
    }
    
    public void createSequences (EndpointSequence endpointSequence, int position, ArrayList<EndpointSequence> projectedDB){
        // add the postfix sequences to a new sequence and add it to the output.
        EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
        int timestamp = endpointSequence.Sequence.get(position-1).Timestamp;
        
        for (; position < endpointSequence.Sequence.size(); position++){

            Endpoint oldEndpoint = endpointSequence.Sequence.get(position);
            Endpoint endpointCopy = new Endpoint(oldEndpoint.SymbolID, oldEndpoint.Timestamp - timestamp, oldEndpoint.Start, oldEndpoint.OccurrenceID);

            newEndpointSequence.Sequence.add(endpointCopy);
        }
        
        if (!newEndpointSequence.Sequence.isEmpty()) {
            projectedDB.add(newEndpointSequence);
        }
    }
    
    
}