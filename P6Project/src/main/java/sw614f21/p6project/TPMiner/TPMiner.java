package sw614f21.p6project.TPMiner;

import sw614f21.p6project.DataStructures.Endpoint;
import sw614f21.p6project.DataStructures.EndpointSequence;
import sw614f21.p6project.DataStructures.OccurrenceSequence;
import sw614f21.p6project.Preprocessing.CSVReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TPMiner {

    public ArrayList<EndpointSequence> OriginalDatabase = new ArrayList<EndpointSequence>();
    public ArrayList<TemporalPattern> TP = new ArrayList<TemporalPattern>();
    // Local variable for giving projected database sequences unique IDs.
    public int DBSequenceID = 0;

    public ArrayList<TemporalPattern> TPMiner(int minSupport) throws IOException {

        // Loading data from the CSV file and converting them to endpoint sequences:
        // ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetOccurrenceSequences();
        ArrayList<OccurrenceSequence> occurrenceDB = CSVReader.GetBenchmarkSequences();
        OriginalDatabase = CSVReader.GetEndpointSequences(occurrenceDB);
        // FakeDataSet FS = new FakeDataSet();
        // OriginalDatabase = FS.GetFakeData();

        // Encode endpoints happening at the same time in the endpoint sequences:
        CSVReader.FormTuples(OriginalDatabase);
        
        // Find all frequent endpoints, and remove infrequent endpoints in DB.
        ArrayList<PatternSymbol> FE = GetFrequentStartingEndpoints (OriginalDatabase, minSupport);

        // For each frequent starting symbol, frequent suffixes are found in the DB to recursively form patterns:
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

        // Counts the number of occurrences for each starting symbol type by going through each sequence while keeping score in a hashmap.
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

        // Infrequent symbols are removed from the database by iterating over sequences and symbols.
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

            // Completely empty sequences after symbol deletions are flagged for removal.
            if (sequence.isEmpty()) {
                sequencesToBeRemoved.add(endpointDB.get(i));
            }
        }
        endpointDB.removeAll(sequencesToBeRemoved);


        ArrayList<String> keys = new ArrayList<String>(symbolCounter.keySet());

        // The result list is formed by the frequent starting symbols found.
        for (int i = 0; i < keys.size(); i++) {
            String type = keys.get(i);
            if (symbolCounter.get(type) >= minSupport) {

                PatternSymbol symbol = new PatternSymbol(type, true);
                resultList.add(symbol);
            }
        }

        return resultList;
    }

    private ArrayList<EndpointSequence> GetProjectedDB(ArrayList<EndpointSequence> inputDB, PatternSymbol patternSymbol, boolean first) {

        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();

        // Iterate over sequences in the database.
        for (int i = 0; i < inputDB.size(); i++) {
            EndpointSequence ES = inputDB.get(i);
            
            // Iterate over symbols in the current sequence.
            int k = 0;
            for (; k < ES.Sequence.size(); k++) {
                // Check if the current endpoint matches the pattern symbol to be projected from.
                if (patternSymbol.EventID.equals(ES.Sequence.get(k).EventID) && patternSymbol.Start == ES.Sequence.get(k).Start) {
                    boolean foundTupleMember = false;

                    // If the current pattern under construction is a tuple, then the endpoint sequence must also contain this tuple
                    // in order to be projected with respect to this pattern.
                    if (patternSymbol.SameTimeAsPrevious){
                        for (int l = 0; l < ES.Sequence.get(k).TupleMembers.size(); l++) {
                            if (ES.Sequence.get(k).TupleMembers.get(l).EventID == patternSymbol.PreviousSymbol) {
                                foundTupleMember = true;
                            }
                        }
                    }
                    // If the tuple member was found or if this is not a requirement, a suffix is found.
                    // First time this function is called, the pattern symbol's SameTimeAsPrevious = false.
                    if (!patternSymbol.SameTimeAsPrevious || foundTupleMember) {
                        // Only look for further suffixes recursively on the starting symbol of the pattern.
                        if (first) {
                            RecursivePostfixScan(ES, projectedDB, patternSymbol, k);
                        }
                        // Skip to the next symbol after the match, since the symbol to be projected from should not be repeated.
                        k++;
                        break;
                    }
                }
            }
            
            // Add the suffix sequences to a new sequence and add it to the output.
            EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
            for (; k < ES.Sequence.size(); k++){
                newEndpointSequence.Sequence.add(ES.Sequence.get(k));
            }
            // Only add non-empty sequences to the output.
            // Empty sequences occur when the scan for a match above reaches the end of the sequence.
            if (!newEndpointSequence.Sequence.isEmpty()){
                projectedDB.add(newEndpointSequence);
            }
        }
        return projectedDB;
    }
    
    public void RecursivePostfixScan (EndpointSequence endpointSequence, ArrayList<EndpointSequence> projectedDB, PatternSymbol patternSymbol, int k) {
        // Only called on the first database projection. Works similarly to GetProjectedDataBase.
        k = k + 1;
        for (; k < endpointSequence.Sequence.size(); k++) {
            if (patternSymbol.EventID.equals(endpointSequence.Sequence.get(k).EventID) && patternSymbol.Start == endpointSequence.Sequence.get(k).Start) {
                // The checks for tuple members are omitted, since this case doesn't occur when this method is called.
                RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                k++;
                break;
            }
        }

        // Add the postfix sequences to a new sequence and add it to the output.
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
        // Gets endpoints with sufficient support.
        FE = CountSupport(alpha, database, minSupport);

        // Remove finishing endpoints from FE that are not found in alpha.
        FE = PointPruning(FE, alpha);

        // Go through each frequent endpoint found after pruning.
        for (int i = 0; i < FE.size(); i++) {
            TemporalPattern alphaPrime = new TemporalPattern(new ArrayList<PatternSymbol>(alpha.TPattern));
            alphaPrime.TPattern.add(FE.get(i));

            // Check if appending the current frequent endpoint to alpha forms a valid pattern, and add it, if that's the case.
            if (IsTemporalPattern(alphaPrime)){
                TP.add(alphaPrime);
            }

            // Project the database further with respect to alpha prime.
            ArrayList<EndpointSequence> projectedDatabase = DBConstruct(database, alphaPrime);

            // Look for pattern extensions in the new projected database.
            TPSpan(alphaPrime, projectedDatabase, minSupport);


        }
        // Clear database in order to save memory.
        database.clear();
    }
    
    
    public ArrayList<PatternSymbol> CountSupport(TemporalPattern alpha, ArrayList<EndpointSequence> database, int minSupport){

        // symbolCounter is used to keep track of the support of each symbol found in the database.
        HashMap<PatternSymbol, Integer> symbolCounter = new HashMap<PatternSymbol, Integer>();
        // The eventID of the final symbol in alpha.
        String lastSymbol = alpha.TPattern.get(alpha.TPattern.size() - 1).EventID;
        ArrayList<PatternSymbol> output = new ArrayList<PatternSymbol>();

        // Go thorugh each sequence in the DB.
        for (int i = 0; i < database.size(); i++){
            ArrayList<Endpoint> sequence = database.get(i).Sequence;

            // Go through each endpoint in the current sequence.
            for (int j = 0; j < sequence.size(); j++){
                Endpoint ep = sequence.get(j);

                PatternSymbol PS = new PatternSymbol(ep.EventID, ep.Start);

                // Go though the endpoints occuring at the same time as the current one
                // in order to check whether or not this endpoint extends a tuple in the pattern under construction.
                for (int k = 0; k < ep.TupleMembers.size(); k++) {
                    if (ep.TupleMembers.get(k).EventID == lastSymbol) {
                        PS.SameTimeAsPrevious = true;
                        PS.PreviousSymbol = lastSymbol;
                        break;
                    }
                }

                // Increment the support for the endpoint found. Please note that SameTimeAsPrevious is used for hashing as well.
                int count = symbolCounter.getOrDefault(PS, 0);
                symbolCounter.put(PS, count + 1);

                // Stop scanning through this sequence if a finishing endpoint corresponding to a starting endpoint in alpha is found.
                if (PS.Start == false){
                    if (IsInAlpha (alpha, PS)){
                        break;
                    }
                }
            }
        }
        // Add pattern symbols found with sufficient support to the output list.
        ArrayList<PatternSymbol> keys = new ArrayList<PatternSymbol> (symbolCounter.keySet());
        for (int i = 0; i < keys.size(); i++){

            if (symbolCounter.get(keys.get(i)) >= minSupport){
                output.add(keys.get(i));
            }
        }

        return output;
    }

    private boolean IsInAlpha(TemporalPattern alpha, PatternSymbol PS){
        // Check for a corresponding starting endpoint in alpha with the same EventID.
        for (int i = 0; i < alpha.TPattern.size(); i++){
            PatternSymbol alphaSymbol = alpha.TPattern.get(i);
            // 'PS.Start == false' is a sanity check, since this condition is checked before calling.
            if (alphaSymbol.EventID.equals(PS.EventID) && alphaSymbol.Start && PS.Start == false) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PatternSymbol> PointPruning (ArrayList<PatternSymbol> FE, TemporalPattern alpha){
        ArrayList<PatternSymbol> output = new ArrayList<PatternSymbol>();

        // Finishing endpoints without their corresponding starting endpoints in alpha are removed from FE.
        // Starting endpoints are retained by default.
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

        // A copy of alpha is created in order to keep track of found partner symbols.
        // Also to avoid deleting the original alpha.
        ArrayList<PatternSymbol> tempAlpha = new ArrayList<>(alpha.TPattern);

        while(!tempAlpha.isEmpty()){
            // Valid patterns can't contain an uneven number of endpoints.
            if (tempAlpha.size() % 2 != 0){
                return false;
            }
            PatternSymbol symbol = tempAlpha.get(0);
            // Look for a corresponding endpoint to the current symbol, if it's a starting endpoint.
            if (symbol.Start){
                boolean hasPartner = false;
                // Check the rest of the pattern to see if the starting symbol has a matching finishing endpoint.
                // Note that starting from index 1 is always 'the rest' due to the ongoing symbol deletion.
                for (int j = 1; j < tempAlpha.size(); j++){
                    // Immediately return false if we arrive at another starting endpoint.
                    if (symbol.EventID.equals(tempAlpha.get(j).EventID) && tempAlpha.get(j).Start){
                        return false;
                    }
                    // In this case, the partner symbol is found, and the partners are deleted from the worklist.
                    if (symbol.EventID.equals(tempAlpha.get(j).EventID) && !tempAlpha.get(j).Start){
                        hasPartner = true;

                        tempAlpha.remove(tempAlpha.get(j));
                        tempAlpha.remove(tempAlpha.get(0));
                        break;
                    }
                }
                // If we scan to the end without finding a partner, then return false.
                if (hasPartner == false){
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }
    
    public ArrayList<EndpointSequence> DBConstruct (ArrayList<EndpointSequence> inputDB, TemporalPattern alpha){
        ArrayList<EndpointSequence> projectedDatabase = new ArrayList<EndpointSequence>();

        // Project the database with respect to the final symbol in alpha. Parameter first = false in order to prevent recursing again.
        projectedDatabase = GetProjectedDB(inputDB, alpha.TPattern.get(alpha.TPattern.size() - 1), false);
        
        // Remove finishing endpoints without the starting endpoint in alpha or in their sequence prefix.
        for (int i = 0; i < projectedDatabase.size(); i++){
            
            ArrayList<Endpoint> sequence = projectedDatabase.get(i).Sequence;
            ArrayList<Endpoint> pruneList = new ArrayList<Endpoint>();

            // Scan backwards from the last endpoint in the sequence.
            for (int j = sequence.size() - 1; j >= 0; j--) {
                boolean correspondingEndpoint = false;
                // Only finishing endpoints need to pass a check.
                if (sequence.get(j).Start == false) {

                    // Look for the corresponding starting endpoint in the sequence.
                    for (int k = j; k >= 0; k--) {
                        if (sequence.get(k).Start == true && sequence.get(j).EventID.equals(sequence.get(k).EventID) && sequence.get(j).OccurrenceID == sequence.get(k).OccurrenceID){
                            correspondingEndpoint = true;
                        }
                    }
                    // Look for the corresponding starting endpoint in alpha.
                    if (correspondingEndpoint == false) {
                        PatternSymbol PS = new PatternSymbol(sequence.get(j).EventID, sequence.get(j).Start);
                        correspondingEndpoint = IsInAlpha(alpha, PS);
                    }
                    // Flag endpoint for deletion if no partner is found.
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
