package sw614f21.p6project;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class TPMiner {

    public ArrayList<EndpointSequence> OriginalDatabase = new ArrayList<EndpointSequence>();
    public ArrayList<TemporalPattern> TP = new ArrayList<TemporalPattern>();


    public ArrayList<TemporalPattern> TPMine (int minSupport) throws IOException {

        //ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetOccurrenceSequences();
        ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetBenchmarkSequences();
        OriginalDatabase = CSVReader.GetEndpointSequences(occurrenceDB);

        //Fake dataset:
        
        //FakeDataSet FS = new FakeDataSet();
        //OriginalDatabase = FS.GetFakeData();
        CSVReader.FormTuples(OriginalDatabase);

//        for (EndpointSequence es : OriginalDatabase) {
//            for (Endpoint ep : es.Sequence) {
//                System.out.println("Endpoint: " + ep);
//                System.out.println("TupleMembers" + ep.TupleMembers);
//            }
//        }
        
        // Find all frequent endpoints, and remove infrequent endpoints in DB.
        ArrayList<PatternSymbol> FE = GetFrequentStartingEndpoints (OriginalDatabase, minSupport);

        for (int i = 0; i < FE.size(); i++) {
            PatternSymbol symbol = FE.get(i);
            ArrayList<PatternSymbol> tempInput = new ArrayList<PatternSymbol>();
            tempInput.add(symbol);
            ArrayList<EndpointSequence> projectedDB = GetProjectedDB(OriginalDatabase, symbol, true);
            TemporalPattern temp = new TemporalPattern(tempInput);
            
            TPSpan(temp, projectedDB, minSupport);
        }
        
        
        return TP;
    }


    public ArrayList<PatternSymbol> GetFrequentStartingEndpoints (ArrayList<EndpointSequence> endpointDB, int minSupport) {

        HashMap<String, Integer> symbolCounter = new HashMap<String, Integer>();
        ArrayList<PatternSymbol> resultList = new ArrayList<PatternSymbol>();

        // counts the number of occurrences for each symbol type.
        for (int i = 0; i < endpointDB.size(); i++) {

            ArrayList<Endpoint> sequence = endpointDB.get(i).Sequence;

            for (int j = 0; j < sequence.size(); j++) {
                Endpoint endpoint = sequence.get(j);

                if (endpoint.Start) {
                    int count = symbolCounter.getOrDefault(endpoint.EventID, 0);
                    symbolCounter.put(endpoint.EventID, count + 1);
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

                if (symbolCounter.get(endpoint.EventID) < minSupport) {
                    endpointsToBeRemoved.add(endpoint);
                }
            }
            sequence.removeAll(endpointsToBeRemoved);

            if (sequence.isEmpty()) {
                sequencesToBeRemoved.add(endpointDB.get(i));
            }
        }
        endpointDB.removeAll(sequencesToBeRemoved);


        ArrayList<String> keys = new ArrayList<String>(symbolCounter.keySet());

        for (int i = 0; i < keys.size(); i++) {
            String type = keys.get(i);
            if (symbolCounter.get(type) >= minSupport) {

                PatternSymbol symbol = new PatternSymbol(type, true);
                resultList.add(symbol);
            }
        }

        return resultList;
    }
    //local variable for giving projected database sequences unique IDs.
    public int DBSequenceID = 0;
    private ArrayList<EndpointSequence> GetProjectedDB(ArrayList<EndpointSequence> inputDB, PatternSymbol patternSymbol, boolean first) {

        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();

        // iterate through each sequence, to project them.
        for (int i = 0; i < inputDB.size(); i++) {
            EndpointSequence ES = inputDB.get(i);
            
            //skip ahead to the last symbol in the pattern.
            int k = 0;
            for (; k < ES.Sequence.size(); k++) {
                if (patternSymbol.EventID.equals(ES.Sequence.get(k).EventID) && patternSymbol.Start == ES.Sequence.get(k).Start) {
                    boolean found = false;

                    if (patternSymbol.SameTimeAsPrevious){
                        for (int l = 0; l < ES.Sequence.get(k).TupleMembers.size(); l++) {
                            if (ES.Sequence.get(k).TupleMembers.get(l).EventID == patternSymbol.PreviousSymbol) {
                                found = true;
                            }
                        }
                    }
                    if (!patternSymbol.SameTimeAsPrevious || found) {
                        if (first) {
                            RecursivePostfixScan(ES, projectedDB, patternSymbol, k);
                        }
                        k++;
                        break;
                    }
                }
            }
            
            // add the postfix sequences to a new sequence and add it to the output.
            EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
            for (; k < ES.Sequence.size(); k++){
                newEndpointSequence.Sequence.add(ES.Sequence.get(k));
            }
            if (!newEndpointSequence.Sequence.isEmpty()){
                projectedDB.add(newEndpointSequence);
            }
        }
        return projectedDB;
    }
    
    public void RecursivePostfixScan (EndpointSequence endpointSequence, ArrayList<EndpointSequence> projectedDB, PatternSymbol patternSymbol, int k) {
        k = k + 1;
        for (; k < endpointSequence.Sequence.size(); k++) {
            if (patternSymbol.EventID.equals(endpointSequence.Sequence.get(k).EventID) && patternSymbol.Start == endpointSequence.Sequence.get(k).Start) {
                RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                k++;
                break;
            }
        }

        // add the postfix sequences to a new sequence and add it to the output.
        EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
        for (; k < endpointSequence.Sequence.size(); k++) {
            newEndpointSequence.Sequence.add(endpointSequence.Sequence.get(k));
        }
        if (!newEndpointSequence.Sequence.isEmpty()) {
            projectedDB.add(newEndpointSequence);
        }
    }


    public void TPSpan (TemporalPattern alpha, ArrayList<EndpointSequence> database, int minSupport){
        ArrayList<PatternSymbol> FE = new ArrayList<PatternSymbol>();
        FE = CountSupport(alpha, database, minSupport);
        FE = PointPruning(FE, alpha);
        for (int i = 0; i< FE.size(); i++) {
            TemporalPattern alphaPrime = new TemporalPattern(new ArrayList<PatternSymbol>(alpha.TPattern));
            alphaPrime.TPattern.add(FE.get(i));
            
            if (IsTemporalPattern(alphaPrime)){
                TP.add(alphaPrime);
            }
            
            ArrayList<EndpointSequence> projectedDatabase = DBConstruct(database, alphaPrime);
            TPSpan(alphaPrime, projectedDatabase, minSupport);


        }
        database.clear();
    }
    
    
    public ArrayList<PatternSymbol> CountSupport(TemporalPattern alpha, ArrayList<EndpointSequence> database, int minSupport){
        
        HashMap<PatternSymbol, Integer> symbolCounter = new HashMap<PatternSymbol, Integer>();
        String lastSymbol = alpha.TPattern.get(alpha.TPattern.size() - 1).EventID;
        ArrayList<PatternSymbol> output = new ArrayList<PatternSymbol>();
        
        for (int i = 0; i < database.size(); i++){
            ArrayList<Endpoint> sequence = database.get(i).Sequence;
            
            for (int j = 0; j < sequence.size(); j++){
                Endpoint ep = sequence.get(j);

                PatternSymbol PS = new PatternSymbol(ep.EventID, ep.Start);

                for (int k = 0; k < ep.TupleMembers.size(); k++) {
                    if (ep.TupleMembers.get(k).EventID == lastSymbol) {
                        PS.SameTimeAsPrevious = true;
                        PS.PreviousSymbol = lastSymbol;
                        break;
                    }
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
            if (alphaSymbol.EventID.equals(PS.EventID) && alphaSymbol.Start && PS.Start == false ){
                return true;
            }
        }
        return false;
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
        ArrayList<PatternSymbol> tempAlpha = new ArrayList<>(alpha.TPattern);
        while(!tempAlpha.isEmpty()){
            if (tempAlpha.size() %2 != 0){
                return false;
            }
            PatternSymbol symbol = tempAlpha.get(0);
            if (symbol.Start){
                boolean hasPartner = false;
                //check the rest of the pattern to see if the starting symbol has a matching finishing symbol.
                for (int j = 1; j < tempAlpha.size(); j++){
                    if (symbol.EventID.equals(tempAlpha.get(j).EventID) && tempAlpha.get(j).Start){
                        return false;
                    }
                    if (symbol.EventID.equals(tempAlpha.get(j).EventID) && !tempAlpha.get(j).Start){
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
    
    public ArrayList<EndpointSequence> DBConstruct (ArrayList<EndpointSequence> inputDB, TemporalPattern alpha){
        ArrayList<EndpointSequence> projectedDatabase = new ArrayList<EndpointSequence>();
        projectedDatabase = GetProjectedDB(inputDB, alpha.TPattern.get(alpha.TPattern.size() - 1), false);
        
        // remove finishing endpoint.
        for (int i = 0; i < projectedDatabase.size(); i++){
            
            ArrayList<Endpoint> sequence = projectedDatabase.get(i).Sequence;
            ArrayList<Endpoint> pruneList = new ArrayList<Endpoint>();
            for (int j = sequence.size() - 1; j >= 0; j--) {
                boolean correspondingEndpoint = false;
                if (sequence.get(j).Start == false) {

                    for (int k = j; k >= 0; k--) {
                        if (sequence.get(k).Start == true && sequence.get(j).EventID.equals(sequence.get(k).EventID) && sequence.get(j).OccurrenceID == sequence.get(k).OccurrenceID){
                            correspondingEndpoint = true;
                        }
                    }
                    if (correspondingEndpoint == false) {
                        PatternSymbol PS = new PatternSymbol(sequence.get(j).EventID, sequence.get(j).Start);
                        correspondingEndpoint = IsInAlpha(alpha, PS);
                    }
                    if (correspondingEndpoint == false) {
                        pruneList.add(sequence.get(j));
                    }
                }
            }
            sequence.removeAll(pruneList);
        }
        return projectedDatabase;
    }
    
}
