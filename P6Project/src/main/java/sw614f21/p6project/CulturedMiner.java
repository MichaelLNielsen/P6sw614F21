package sw614f21.p6project;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


public class CulturedMiner {
    public ArrayList<ClusterPattern> TP = new ArrayList<ClusterPattern>();

    public ArrayList<ClusterPattern> CultureMine(int minSupport, double maxClusterDeviation){
        FakeDataSet FDS = new FakeDataSet();
        ArrayList<EndpointSequence> OriginalDatabase = FDS.GetFakeData();
        
        
        //getting the frequent endpoints.
        ArrayList<ClusterSymbol> FE = GetFrequentStartingEndpoints(OriginalDatabase, minSupport);
        
        for (int i = 0; i < FE.size(); i++){
            ClusterSymbol symbol = new ClusterSymbol(FE.get(i).SymbolID, FE.get(i).Start, FE.get(i).Mean, FE.get(i).Deviation);
            ArrayList<EndpointSequence> projectedDB = GetProjectedDB(OriginalDatabase, symbol, true);


            ClusterPattern temp = new ClusterPattern();
            temp.Pattern.add(symbol);
            
            TPSpan(temp, projectedDB, minSupport, maxClusterDeviation);

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
    private ArrayList<EndpointSequence> GetProjectedDB(ArrayList<EndpointSequence> inputDB, ClusterSymbol patternSymbol, boolean first) {
        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();

        // iterate through each sequence, to project them.
        for (int i = 0; i < inputDB.size(); i++) {
            EndpointSequence endpointSequence = inputDB.get(i);
            
            //skip ahead to the last symbol in the pattern.
            int k = 0;
            for (; k < endpointSequence.Sequence.size(); k++) {
                if (patternSymbol.SymbolID == endpointSequence.Sequence.get(k).SymbolID && patternSymbol.Start == endpointSequence.Sequence.get(k).Start){
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
    
    public void RecursivePostfixScan (EndpointSequence endpointSequence, ArrayList<EndpointSequence> projectedDB, ClusterSymbol patternSymbol, int k) {
        k = k + 1;
        for (; k < endpointSequence.Sequence.size(); k++) {
            if (patternSymbol.SymbolID == endpointSequence.Sequence.get(k).SymbolID && patternSymbol.Start == endpointSequence.Sequence.get(k).Start) {
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

    public void TPSpan(ClusterPattern alpha, ArrayList<EndpointSequence> database, int minSupport, double maxClusterDeviation){
        ArrayList<ClusterSymbol> FE = new ArrayList<ClusterSymbol>();
        FE = CountSupport(alpha, database, minSupport);
        FE = PointPruning(FE, alpha);
        // Obtained clusters here.

        for (int i = 0; i< FE.size(); i++){
            ClusterPattern alphaPrime = new ClusterPattern();
            alphaPrime.Pattern.addAll(alpha.Pattern);
            alphaPrime.Pattern.add(FE.get(i));

            if (IsClusterPattern(alphaPrime)){
                ClusterPattern newPattern = new ClusterPattern();
                newPattern.Pattern.addAll(alphaPrime.Pattern);
                TP.add(alphaPrime);
            }

            ArrayList<EndpointSequence> projectedDatabase = DBConstruct(database, alphaPrime);
            TPSpan(alphaPrime, projectedDatabase, minSupport, maxClusterDeviation);
        }
        database.clear();
    }

    public ArrayList<ClusterSymbol> CountSupport(ClusterPattern alpha, ArrayList<EndpointSequence> database, int minSupport) {

        ArrayList<ClusterSymbol> symbolCounter = new ArrayList<ClusterSymbol>();

        for (int i = 0; i < database.size(); i++){
            EndpointSequence sequence = database.get(i);

            for (int j = 0; j < sequence.Sequence.size(); j++){
                ClusterSymbol CS = new ClusterSymbol(sequence.Sequence.get(j).SymbolID, sequence.Sequence.get(j).Start);

                int position = symbolCounter.indexOf(CS);

                if (position == -1){
                    CS.ClusterElements = new ArrayList<ClusterElement>();
                    position = symbolCounter.size();
                    symbolCounter.add(CS);
                }

                ClusterElement element = new ClusterElement(sequence.Sequence.get(j).Timestamp, sequence);
                symbolCounter.get(position).ClusterElements.add(element);

                if (CS.Start == false){
                    if (IsInAlpha (alpha, CS)){
                        break;
                    }
                }
            }
        }
        // check for min support.

        ArrayList<ClusterSymbol> symbolsToRemove = new ArrayList<ClusterSymbol>();
        for (int i = 0; i < symbolCounter.size(); i++){
            if (symbolCounter.get(i).ClusterElements.size() < minSupport){
                symbolsToRemove.add(symbolCounter.get(i));
            }
        }
        symbolCounter.removeAll(symbolsToRemove);
        return symbolCounter;
    }

    private boolean IsInAlpha(ClusterPattern alpha, ClusterSymbol CS){
        for (int i = 0; i < alpha.Pattern.size(); i++){
            ClusterSymbol alphaSymbol = alpha.Pattern.get(i);
            if (alphaSymbol.SymbolID == CS.SymbolID && alphaSymbol.Start && CS.Start == false ){
                return true;
            }
        }
        return false;
    }

    public ArrayList<ClusterSymbol> PointPruning (ArrayList<ClusterSymbol> FE, ClusterPattern alpha){
        ArrayList<ClusterSymbol> output = new ArrayList<ClusterSymbol>();
        for (int i = 0; i < FE.size(); i++ ){
            ClusterSymbol CS = FE.get(i);
            if (!CS.Start){
                if (IsInAlpha(alpha, CS)){
                    output.add(CS);
                }
            }
            else {
                output.add(CS);
            }
        }
        return output;
    }

    public boolean IsClusterPattern(ClusterPattern alpha){
        // go through each symbol in alpha.
        ArrayList<ClusterSymbol> tempAlpha = new ArrayList<>(alpha.Pattern);
        while(!tempAlpha.isEmpty()){
            if (tempAlpha.size() % 2 != 0){
                return false;
            }
            PatternSymbol symbol = tempAlpha.get(0);
            if (symbol.Start){
                boolean hasPartner = false;
                //check the rest of the pattern to see if the starting symbol has a matching finishing symbol.
                for (int j = 1; j < tempAlpha.size(); j++){
                    if (symbol.SymbolID == tempAlpha.get(j).SymbolID && tempAlpha.get(j).Start){
                        return false;
                    }
                    if (symbol.SymbolID == tempAlpha.get(j).SymbolID && !tempAlpha.get(j).Start){
                        hasPartner = true;

                        tempAlpha.remove(tempAlpha.get(j));
                        tempAlpha.remove(tempAlpha.get(0));
                        break;
                    }
                }
                if (hasPartner == false){
                    return false;
                }
            }
            else {
                return false;
            }
        }
        //System.out.println("alpha = " + alpha.TPattern);
        return true;
    }

    public ArrayList<EndpointSequence> DBConstruct (ArrayList<EndpointSequence> inputDB, ClusterPattern alpha){
        ArrayList<EndpointSequence> projectedDatabase = new ArrayList<EndpointSequence>();
        projectedDatabase = GetClusterProjectedDB(alpha.Pattern.get(alpha.Pattern.size() - 1));

        // remove finishing endpoint.
        for (int i = 0; i < projectedDatabase.size(); i++){

            ArrayList<Endpoint> sequence = projectedDatabase.get(i).Sequence;
            ArrayList<Endpoint> pruneList = new ArrayList<Endpoint>();
            for (int j = sequence.size()-1; j >= 0; j--){
                boolean correspondingEndpoint = false;
                if (sequence.get(j).Start == false){

                    for (int k = j; k >= 0; k--){
                        if (sequence.get(k).Start == true && sequence.get(j).SymbolID == sequence.get(k).SymbolID && sequence.get(j).OccurrenceID == sequence.get(k).OccurrenceID){
                            correspondingEndpoint = true;
                        }
                    }
                    if (correspondingEndpoint == false){
                        ClusterSymbol CS = new ClusterSymbol(sequence.get(j).SymbolID, sequence.get(j).Start);
                        correspondingEndpoint = IsInAlpha(alpha, CS);
                    }
                    if (correspondingEndpoint == false){
                        pruneList.add(sequence.get(j));
                    }
                }
            }
            sequence.removeAll(pruneList);
        }
        return projectedDatabase;
    }

    private ArrayList<EndpointSequence> GetClusterProjectedDB(ClusterSymbol clusterSymbol) {
        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();

        // iterate through each sequence, to project them.
        for (int i = 0; i < clusterSymbol.ClusterElements.size(); i++) {
            EndpointSequence endpointSequence = clusterSymbol.ClusterElements.get(i).Sequence;

            //skip ahead to the last symbol in the pattern.
            int k = 0;
            for (; k < endpointSequence.Sequence.size(); k++) {
                if (clusterSymbol.SymbolID == endpointSequence.Sequence.get(k).SymbolID && clusterSymbol.Start == endpointSequence.Sequence.get(k).Start){
                    k++;
                    break;
                }
            }

            if (k > 0){
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



}