package sw614f21.p6project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TPMiner {

    public ArrayList<TemporalPattern> TP = new ArrayList<TemporalPattern>();


    public ArrayList<TemporalPattern> TPMine (int minSupport) throws IOException {

        ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetOccurrenceSequences();
        ArrayList<EndpointSequence> endpointDB = CSVReader.GetEndpointSequences(occurrenceDB);

        // Find all frequent endpoints, and remove infrequent endpoints in DB.
        ArrayList<PatternSymbol> FE = GetFrequentStartingEndpoints (endpointDB, minSupport);


        for (int i = 0; i < FE.size(); i++) {
            PatternSymbol symbol = FE.get(i);
            ArrayList<EndpointSequence> projectedDB = GetProjectedDB(endpointDB, symbol);
            ArrayList<PatternSymbol> tempInput = new ArrayList<PatternSymbol>();
            tempInput.add(symbol);
            TemporalPattern temp = new TemporalPattern(tempInput);
            
            TPSpan(temp, projectedDB, minSupport);
        }
        return TP;
    }


    public ArrayList<PatternSymbol> GetFrequentStartingEndpoints (ArrayList<EndpointSequence> endpointDB, int minSupport) {

        HashMap<EventType, Integer> symbolCounter = new HashMap<EventType, Integer>();
        ArrayList<PatternSymbol> resultList = new ArrayList<PatternSymbol>();

        // counts the number of occurrences for each symbol type.
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
        //remove infrequent symbols.
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
    //local variable for giving projected database sequences unique IDs.
    public int DBSequenceID = 0;
    private ArrayList<EndpointSequence> GetProjectedDB(ArrayList<EndpointSequence> endpointDB, PatternSymbol patternSymbol) {

        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();
        
        // iterate through each sequence, to project them.
        for (int i = 0; i < endpointDB.size(); i++) {
            EndpointSequence endpointSequence = endpointDB.get(i);
            
            //skip ahead to the last symbol in the pattern.
            int k = 0;
            for (; k < endpointSequence.Sequence.size(); k++){
                if(patternSymbol.SymbolID == endpointSequence.Sequence.get(k).SymbolID){
                    RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                    break;
                }
            }
            
            //Backtrack until the first endpoint with the same timestamp as the last symbol in the pattern sequence.
            if (k != endpointSequence.Sequence.size()){
                while ( k > 0 && endpointSequence.Sequence.get(k-1).Timestamp == endpointSequence.Sequence.get(k).Timestamp){
                    k--;
                }
            }
            
            // add the postfix sequences to a new sequence and add it to the output.
            EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
            for (; k < endpointSequence.Sequence.size(); k++){
                newEndpointSequence.Sequence.add(endpointSequence.Sequence.get(k));
            }
            if (!newEndpointSequence.Sequence.isEmpty()){
                projectedDB.add(newEndpointSequence);
            }
        }
        return projectedDB;
    }
    
    public void RecursivePostfixScan (EndpointSequence endpointSequence, ArrayList<EndpointSequence> projectedDB, PatternSymbol patternSymbol, int k){
        k = k + 1;
        for (; k < endpointSequence.Sequence.size(); k++){
            if(patternSymbol.SymbolID == endpointSequence.Sequence.get(k).SymbolID){
                RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                break;
            }
        }

        //Backtrack until the first endpoint with the same timestamp as the last symbol in the pattern sequence.
        if (k != endpointSequence.Sequence.size()){
            while ( k > 0 && endpointSequence.Sequence.get(k-1).Timestamp == endpointSequence.Sequence.get(k).Timestamp){
                k--;
            }
        }

        // add the postfix sequences to a new sequence and add it to the output.
        EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
        for (; k < endpointSequence.Sequence.size(); k++){
            newEndpointSequence.Sequence.add(endpointSequence.Sequence.get(k));
        }
        if (!newEndpointSequence.Sequence.isEmpty()){
            projectedDB.add(newEndpointSequence);
        }
    }

    public void TPSpan(TemporalPattern alpha, ArrayList<EndpointSequence> database, int minSupport){
        ArrayList<PatternSymbol> FE = new ArrayList<PatternSymbol>();
        FE = CountSupport(alpha, database, minSupport);
        FE = PointPruning(FE, alpha);
        
        for (int i = 0; i< FE.size(); i++){
            TemporalPattern alphaPrime = new TemporalPattern(alpha.TPattern);
            alphaPrime.TPattern.add(FE.get(i));
            
            if (IsTemporalPattern(alphaPrime)){
                TP.add(alphaPrime);
            }
            
            ArrayList<EndpointSequence> projectedDatabase = DBConstruct(alphaPrime, database);
            TPSpan(alphaPrime, projectedDatabase, minSupport);
        }
    }
    
    
    public ArrayList<PatternSymbol> CountSupport(TemporalPattern alpha , ArrayList<EndpointSequence> database, int minSupport){
        
        HashMap<PatternSymbol, Integer> symbolCounter = new HashMap<PatternSymbol, Integer>();
        
        ArrayList<PatternSymbol> output = new ArrayList<PatternSymbol>();
        
        for (int i = 0; i < database.size(); i++){
            EndpointSequence sequence = database.get(i);
            
            for (int j =0 ; j < sequence.Sequence.size(); j++){
                PatternSymbol PS = new PatternSymbol(sequence.Sequence.get(j).SymbolID, sequence.Sequence.get(j).Start);
                PatternSymbol symbolFromHashMap = AlreadyInHashMap(symbolCounter, PS);
                
                if (symbolFromHashMap != null){
                    PS = symbolFromHashMap;
                }
                
                int count = symbolCounter.getOrDefault(PS, 0);
                symbolCounter.put(PS, count + 1);
                
                if (PS.Start == false){
                    if (IsInAlpha (alpha, PS)){
                        break;
                    }
                }
            }
        }
        // check for min support.
        ArrayList<PatternSymbol> keys = new ArrayList<PatternSymbol> (symbolCounter.keySet());
        for (int i = 0; i < keys.size(); i++){
            if (symbolCounter.get(keys.get(i)) >= minSupport){
                output.add(keys.get(i));
            }
        }
        
        return output;
    }
    
    private boolean IsInAlpha(TemporalPattern alpha, PatternSymbol PS){
        for (int i = 0; i < alpha.TPattern.size(); i++){
            PatternSymbol alphaSymbol = alpha.TPattern.get(i);
            if (alphaSymbol.SymbolID == PS.SymbolID && alphaSymbol.Start && PS.Start == false ){
                return true;
            }
        }
        return false;
    }
    
    private PatternSymbol AlreadyInHashMap (HashMap<PatternSymbol, Integer> hashMap, PatternSymbol symbol) {
        ArrayList<PatternSymbol> keys = new ArrayList<PatternSymbol> (hashMap.keySet());
        
        for (int i = 0; i < keys.size(); i++){
            if (ComparePatternSymbols(keys.get(i), symbol)){
                //if we find another element in the hashmap which has the same symbol id and start/finishing tag then return it.
                return keys.get(i);
            }
        }
        //if not found in the hashmap return null
        return null;
    }
    
    private boolean ComparePatternSymbols (PatternSymbol symbol1, PatternSymbol symbol2){
        return (symbol1.Start == symbol2.Start && symbol1.SymbolID == symbol2.SymbolID);
    }
    
    public ArrayList<PatternSymbol> PointPruning (ArrayList<PatternSymbol> FE, TemporalPattern alpha){
        ArrayList<PatternSymbol> output = new ArrayList<PatternSymbol>();
        
        for (int i = 0; i < FE.size(); i++ ){
            PatternSymbol PS = FE.get(i);
            if (PS.Start == false){
                if (IsInAlpha(alpha, PS)){
                    output.add(PS);
                }
            }
            else {
                output.add(PS);
            }
        }
        return output;
    }
    
    public boolean IsTemporalPattern(TemporalPattern alpha){
        // go through each symbol in alpha.
        for (int i = 0; i < alpha.TPattern.size(); i++){
            PatternSymbol symbol = alpha.TPattern.get(i);
            if (symbol.Start){
                boolean hasPartner = false;
                //check the rest of the pattern to see if the starting symbol has a matching finishing symbol.
                for (int j = i; j < alpha.TPattern.size(); j++){
                    if (symbol.SymbolID == alpha.TPattern.get(j).SymbolID && !alpha.TPattern.get(j).Start){
                        hasPartner = true;
                    }
                }
                if (hasPartner == false){
                    return false;
                }
            }
        }
        return true;
    }
    
    public ArrayList<EndpointSequence> DBConstruct (TemporalPattern alpha, ArrayList<EndpointSequence> database){
        ArrayList<EndpointSequence> projectedDatabase = new ArrayList<EndpointSequence>();
        projectedDatabase = GetProjectedDB(database , alpha.TPattern.get(alpha.TPattern.size()-1));
        
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
                        PatternSymbol PS = new PatternSymbol(sequence.get(j).SymbolID, sequence.get(j).Start);
                        correspondingEndpoint = IsInAlpha(alpha, PS);
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
    
}