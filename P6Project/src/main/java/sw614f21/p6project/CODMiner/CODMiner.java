package sw614f21.p6project.CODMiner;

import sw614f21.p6project.DataStructures.Endpoint;
import sw614f21.p6project.DataStructures.EndpointSequence;
import sw614f21.p6project.TPMiner.PatternSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;

public class CODMiner {
    public ArrayList<ClusterPattern> TP = new ArrayList<ClusterPattern>();
    public int GapConstraint;
    int DBSequenceID = 0;

    public ArrayList<ClusterPattern> CODMiner(int minSupport, double maxClusterDeviation, int gapConstraint, ArrayList<EndpointSequence> OriginalDatabase) throws IOException{

        GapConstraint = gapConstraint;
        // Getting the frequent endpoints.
        ArrayList<ClusterSymbol> FE = GetFrequentStartingEndpoints(OriginalDatabase, minSupport);

        // Find more patterns extended from the current endpoint.
        for (int i = 0; i < FE.size(); i++){
            ClusterSymbol symbol = new ClusterSymbol(FE.get(i).EventID, FE.get(i).Start, FE.get(i).Mean, FE.get(i).Deviation);
            ArrayList<EndpointSequence> projectedDB = GetProjectedDB(OriginalDatabase, symbol);

            ClusterPattern temp = new ClusterPattern();
            temp.Pattern.add(symbol);
            
            TPSpan(temp, projectedDB, minSupport, maxClusterDeviation);

        }

        return TP;
    }
    
    public ArrayList<ClusterSymbol> GetFrequentStartingEndpoints (ArrayList<EndpointSequence> endpointDB, int minSupport) {

        HashMap<String, ArrayList<Integer>> symbolCounter = new HashMap<String, ArrayList<Integer>>();
        // This list contains all the timestamps for each symbol in order to calculate statistics.
        ArrayList<ClusterSymbol> resultList = new ArrayList<ClusterSymbol>();

        // Counts the number of occurrences for each starting symbol type by going through each sequence while keeping score in a hashmap.
        for (int i = 0; i < endpointDB.size(); i++) {

            ArrayList<Endpoint> sequence = endpointDB.get(i).Sequence;

            for (int j = 0; j < sequence.size(); j++) {
                Endpoint endpoint = sequence.get(j);

                if (endpoint.Start) {
                    ArrayList<Integer> symboldata = symbolCounter.getOrDefault(endpoint.EventID, new ArrayList<Integer>());
                    symboldata.add(endpoint.Timestamp);
                    symbolCounter.put(endpoint.EventID, symboldata);
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

                if (symbolCounter.get(endpoint.EventID).size() < minSupport) {
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

        for (int i = 0; i < keys.size(); i++) {
            String type = keys.get(i);
            ArrayList<Integer> symbolData = symbolCounter.get(type);

            // The result list is formed by the frequent starting symbols found.
            // Furthermore, descriptive statistics are calculated.
            if (symbolData.size() >= minSupport) {

                double mean = FindMean(symbolData);
                double deviation = GetIntegerDeviation(symbolData, mean);
                ClusterSymbol symbol = new ClusterSymbol(type, true, mean, deviation);
                
                resultList.add(symbol);
            }
        }

        return resultList;
    }

    // Calculates the mean.
    public double FindMean (ArrayList<Integer> data){
        double sum = 0;
        for (int i = 0; i < data.size(); i++){
            sum += (double)data.get(i);
        }
        
        return sum/(double)data.size();
    }
    
    private ArrayList<EndpointSequence> GetProjectedDB(ArrayList<EndpointSequence> inputDB, ClusterSymbol patternSymbol) {
        ArrayList<EndpointSequence> projectedDB = new ArrayList<EndpointSequence>();

        // Iterate through each sequence, to project them.
        for (int i = 0; i < inputDB.size(); i++) {
            EndpointSequence endpointSequence = inputDB.get(i);
            
            // Skip ahead to the last symbol in the pattern.
            int k = 0;
            for (; k < endpointSequence.Sequence.size(); k++) {
                if (patternSymbol.EventID.equals(endpointSequence.Sequence.get(k).EventID) && patternSymbol.Start == endpointSequence.Sequence.get(k).Start){
                    RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                    k++;
                    break;
                }
            }

            // Form sequences based on the foudn position k.
            CreateSequences(endpointSequence, k, projectedDB);

        }
        return projectedDB;
    }
    
    public void RecursivePostfixScan (EndpointSequence endpointSequence, ArrayList<EndpointSequence> projectedDB, ClusterSymbol patternSymbol, int k) {
        // Recursive postfix search. Works very similarly to GetProjectedDB.
        k = k + 1;
        for (; k < endpointSequence.Sequence.size(); k++) {
            if (patternSymbol.EventID.equals(endpointSequence.Sequence.get(k).EventID) && patternSymbol.Start == endpointSequence.Sequence.get(k).Start) {
                RecursivePostfixScan(endpointSequence, projectedDB, patternSymbol, k);
                k++;
                break;
            }
        }
        CreateSequences(endpointSequence, k, projectedDB);
    }
    
    public void CreateSequences(EndpointSequence endpointSequence, int position, ArrayList<EndpointSequence> projectedDB){
        // Add the postfix sequences to a new sequence and add it to the output.
        EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());

        // Save the timestamp of the found symbol at position (k) - 1. Used for offsetting the timestamps of the suffixes.
        int timestamp = endpointSequence.Sequence.get(position - 1).Timestamp;

        // Create copies of the endpoints in the suffixes with their timestamps offset by timestamp.
        for (; position < endpointSequence.Sequence.size(); position++){

            Endpoint oldEndpoint = endpointSequence.Sequence.get(position);
            Endpoint endpointCopy = new Endpoint(oldEndpoint.EventID, oldEndpoint.Timestamp - timestamp, oldEndpoint.Start, oldEndpoint.OccurrenceID);
            // Stop projecting if the current timestamp exceeds the GapConstraint parameter.
            if (endpointCopy.Timestamp > GapConstraint){
                break;
            }
            newEndpointSequence.Sequence.add(endpointCopy);
        }
        // Only add non-empty sequences to the projected database.
        if (!newEndpointSequence.Sequence.isEmpty()) {
            projectedDB.add(newEndpointSequence);
        }
    }

    public void TPSpan(ClusterPattern alpha, ArrayList<EndpointSequence> database, int minSupport, double maxClusterDeviation){
        ArrayList<ClusterSymbol> FE = new ArrayList<ClusterSymbol>();

        // Gets endpoints with sufficient support.
        FE = CountSupport(alpha, database, minSupport);

        // Remove finishing endpoints from FE that are not found in alpha.
        FE = PointPruning(FE, alpha);

        // Find frequent clusters of each frequent endpoint.
        FE = Clustering(FE, minSupport, maxClusterDeviation);

        // Go through each frequent endpoint found after pruning.
        for (int i = 0; i< FE.size(); i++){
            ClusterPattern alphaPrime = new ClusterPattern();
            alphaPrime.Pattern.addAll(alpha.Pattern);
            alphaPrime.Pattern.add(FE.get(i));

            // Check if appending the current frequent endpoint to alpha forms a valid pattern, and add it, if that's the case.
            if (IsClusterPattern(alphaPrime)){
                ClusterPattern newPattern = new ClusterPattern();
                newPattern.Pattern.addAll(alphaPrime.Pattern);
                newPattern.Support = FE.get(i).ClusterElements.size();
                TP.add(newPattern);
            }

            // Project the database further with respect to alpha prime.
            ArrayList<EndpointSequence> projectedDatabase = DBConstruct(database, alphaPrime);
            FE.get(i).ClusterElements = null;

            // Look for pattern extensions in the new projected database.
            TPSpan(alphaPrime, projectedDatabase, minSupport, maxClusterDeviation);
        }
        // Clear database in order to save memory.
        database.clear();
    }



    public ArrayList<ClusterSymbol> CountSupport(ClusterPattern alpha, ArrayList<EndpointSequence> database, int minSupport) {

        // symbolCounter is used to keep track of the support of each symbol found in the database.
        ArrayList<ClusterSymbol> symbolCounter = new ArrayList<ClusterSymbol>();

        // Go thorugh each sequence in the DB.
        for (int i = 0; i < database.size(); i++){
            EndpointSequence sequence = database.get(i);

            // Go through each endpoint in the current sequence.
            for (int j = 0; j < sequence.Sequence.size(); j++){
                ClusterSymbol CS = new ClusterSymbol(sequence.Sequence.get(j).EventID, sequence.Sequence.get(j).Start);

                int position = symbolCounter.indexOf(CS);
                
                // If CS is not found (indexOF returns -1), add CS to SymbolCounter.
                if (position == -1){
                    CS.ClusterElements = new ArrayList<ClusterElement>();
                    position = symbolCounter.size();
                    symbolCounter.add(CS);
                }


                ClusterElement element = new ClusterElement(sequence.Sequence.get(j).Timestamp, sequence);
                // Each unique endpoint in symbolCounter can only be counted once per sequence.
                boolean SequenceInList = false;
                for (int l = 0; l < symbolCounter.get(position).ClusterElements.size(); l++){
                    if (symbolCounter.get(position).ClusterElements.get(l).Sequence == element.Sequence){
                        SequenceInList = true;
                    }
                }
                if (SequenceInList == false){
                    // Append information about the cluster element to its corresponding ClusterSymbol in symbolCounter.
                    symbolCounter.get(position).ClusterElements.add(element);
                }
                
                // Stop scanning through this sequence if a finishing endpoint corresponding to a starting endpoint in alpha is found.
                if (CS.Start == false){
                    if (IsInAlpha (alpha, CS)){
                        break;
                    }
                }
            }
        }

        // Add pattern symbols found with sufficient support to the output list. Support equals the size of the ClusterElements list.
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
        // Check for a corresponding starting endpoint in alpha with the same EventID.
        for (int i = alpha.Pattern.size() - 1; i >= 0; i--){
            ClusterSymbol alphaSymbol = alpha.Pattern.get(i);
            // 'PS.Start == false' is a sanity check, since this condition is checked before calling.
            // If the event ID of alphaSymbol matches, then true is returned for a starting endpoint and false otherwise.
            if (alphaSymbol.EventID.equals(CS.EventID) && CS.Start == false){
                return alphaSymbol.Start;
            }
        }
        return false;
    }

    public ArrayList<ClusterSymbol> PointPruning (ArrayList<ClusterSymbol> FE, ClusterPattern alpha){
        ArrayList<ClusterSymbol> output = new ArrayList<ClusterSymbol>();

        // Finishing endpoints without their corresponding starting endpoints in alpha are removed from FE.
        // Starting endpoints are retained by default.
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
        // A copy of alpha is created in order to keep track of found partner symbols.
        // Also to avoid deleting the original alpha.
        ArrayList<ClusterSymbol> tempAlpha = new ArrayList<>(alpha.Pattern);

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

    public ArrayList<EndpointSequence> DBConstruct (ArrayList<EndpointSequence> inputDB, ClusterPattern alpha){
        ArrayList<EndpointSequence> projectedDatabase = new ArrayList<EndpointSequence>();

        // Project the database with respect to the final symbol in alpha. Parameter first = false in order to prevent recursing again.
        projectedDatabase = GetClusterProjectedDB(alpha.Pattern.get(alpha.Pattern.size() - 1));

        // Remove finishing endpoints without the starting endpoint in alpha or in their sequence prefix.
        for (int i = 0; i < projectedDatabase.size(); i++){

            ArrayList<Endpoint> sequence = projectedDatabase.get(i).Sequence;
            ArrayList<Endpoint> pruneList = new ArrayList<Endpoint>();

            // Scan backwards from the last endpoint in the sequence.
            for (int j = sequence.size() - 1; j >= 0; j--){
                boolean correspondingEndpoint = false;
                // Only finishing endpoints need to pass a check.
                if (sequence.get(j).Start == false){

                    // Look for the corresponding starting endpoint in the sequence.
                    for (int k = j; k >= 0; k--){
                        if (sequence.get(k).Start == true && sequence.get(j).EventID.equals(sequence.get(k).EventID) && sequence.get(j).OccurrenceID == sequence.get(k).OccurrenceID){
                            correspondingEndpoint = true;
                        }
                    }
                    // Look for the corresponding starting endpoint in alpha.
                    if (correspondingEndpoint == false){
                        ClusterSymbol CS = new ClusterSymbol(sequence.get(j).EventID, sequence.get(j).Start);
                        correspondingEndpoint = IsInAlpha(alpha, CS);
                    }
                    // Flag endpoint for deletion if no partner is found.
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

        // Iterate through each sequence contained in the cluster connected to the cluster symbol.
        for (int i = 0; i < clusterSymbol.ClusterElements.size(); i++) {
            EndpointSequence endpointSequence = clusterSymbol.ClusterElements.get(i).Sequence;

            // Skip ahead to the final symbol in the pattern.
            int k = 0;
            for (; k < endpointSequence.Sequence.size(); k++) {
                Endpoint EP = endpointSequence.Sequence.get(k);
                if (clusterSymbol.EventID.equals(EP.EventID) && clusterSymbol.Start == EP.Start){
                    k++;
                    break;
                }
            }

            // Create new endpoint sequence with the suffix.
            EndpointSequence newEndpointSequence = new EndpointSequence(DBSequenceID++, new ArrayList<Endpoint>());
            for (; k < endpointSequence.Sequence.size(); k++){
                newEndpointSequence.Sequence.add(endpointSequence.Sequence.get(k));
            }

            // Only add new non-empty sequences to the result.
            if (newEndpointSequence.Sequence.size() > 0){
                projectedDB.add(newEndpointSequence);
            }
            
        }
        return projectedDB;
    }

    private ArrayList<ClusterSymbol> Clustering(ArrayList<ClusterSymbol> fe, int minSupport, double maxClusterDeviation) {

        ArrayList<ClusterSymbol> output = new ArrayList<ClusterSymbol>();

        // Go through each frequent endpoint to form clusters.
        for (int i = 0; i < fe.size(); i++){

            // Perform clustering on timestamps.
            ArrayList<ClusterSymbol> clusters = GetClusters(fe.get(i), maxClusterDeviation);

            // Only add clusters with sufficient minimum support to the output.
            for (int j = 0; j < clusters.size(); j++){
                ClusterSymbol cluster = clusters.get(j);
                if (cluster.ClusterElements.size() >= minSupport) {
                    output.add(cluster);
                }
            }

        }

        return output;
    }

    private ArrayList<ClusterSymbol> GetClusters(ClusterSymbol symbol, double maxClusterDeviation) {

        ArrayList<ClusterSymbol> output = null;
        Collections.sort(symbol.ClusterElements);
        ArrayList<ClusterElement> elements = symbol.ClusterElements;
        int n = elements.size();

        // Initializing array D and B for saving minimum deviation and index values, respectively.
        double[][] D = new double[n + 1][n + 1];
        int[][] B = new int[n + 1][n + 1];

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++){
                D[i][j] = Double.POSITIVE_INFINITY;
                B[i][j] = -1;
            }
        }
        D[0][0] = 0;

        // Dynamic programming for clustering.
        // m iterates over number of clusters, i over problem size, and j over possible splits.
        // The sum of squares is calcuated incrementally to improve performance.
        for (int m = 1; m <= n; m++) {
            for (int i = 1; i <= n; i++) {
                SumOfSquares ss = new SumOfSquares();
                for (int j = i; j >= m; j--) {
                    double comp = D[j - 1][m - 1] + ss.Increment(elements.get(j - 1).TimeStamp);
                    if (comp < D[i][m]){
                        D[i][m] = comp;
                        B[i][m] = j;
                    }
                }
            }

            // Backtrack with array B to form clusters for testing.
            output = ClusterSymbols(m, B, symbol);

            // Break early if each cluster satisfies the cluster deviation threshold.
            if (ClustersWithinThreshold(output, maxClusterDeviation)){
                break;
            }
        }

        return output;

    }



    private ArrayList<ClusterSymbol> ClusterSymbols(int m, int[][] B, ClusterSymbol symbol){

        ArrayList<Integer> leftmostElements = new ArrayList<Integer>();
        ArrayList<ClusterSymbol> output = new ArrayList<ClusterSymbol>();
        ArrayList<ClusterElement> elements = symbol.ClusterElements;

        int n = elements.size();
        // Backtrack in array B in order to find the optimal splits.
        // B[n][k] containes the one-based index of the leftmost element of the kth cluster with n elements.
        for (int k = m; k >= 1; k--) {
            leftmostElements.add(0, B[n][k] - 1);
            n = B[n][k] - 1;
        }
        // Add n as a sentinel value used in the for loop below.
        leftmostElements.add(elements.size());

        // Form new clusters based on the leftmostelements list.
        for (int i = 0; i < leftmostElements.size() - 1; i++) {
            ClusterSymbol newCluster = new ClusterSymbol(symbol.EventID, symbol.Start);
            newCluster.ClusterElements = new ArrayList<ClusterElement>();

            // Add all elements belonging to the cluster to its element list.
            for (int j = leftmostElements.get(i); j < leftmostElements.get(i + 1); j++){
                newCluster.ClusterElements.add(elements.get(j));
            }

            output.add(newCluster);
        }

        return output;
    }

    private boolean ClustersWithinThreshold(ArrayList<ClusterSymbol> output, double maxClusterDeviation) {

        // Go through each cluster in output, and immediately return false, if one of them doesn't satisfy the deviation threshold.
        for (int i = 0; i < output.size(); i++) {
            // Cluster mean is calculated as well, for the output patterns.
            output.get(i).Mean = GetClusterMean(output.get(i).ClusterElements);
            output.get(i).Deviation = GetClusterDeviation2(output.get(i).ClusterElements, output.get(i).Mean);
            if (output.get(i).Deviation > maxClusterDeviation){
                return false;
            }
        }
        // Return true if all clusters have satisfy the deviation threshold.
        return true;
    }

    private double GetClusterMean (ArrayList<ClusterElement> elements) {
        // Calculate the mean timestamp of the input cluster.
        double sum = 0;
        for (int i = 0; i < elements.size(); i++){
            sum += (double)elements.get(i).TimeStamp;
        }

        return sum / (double)elements.size();
    }

    private double GetClusterDeviation(ArrayList<ClusterElement> elements) {
        // Calculate the difference between the maximum and minimum timestamp. No currently in use.
        return elements.get(elements.size() - 1).TimeStamp - elements.get(0).TimeStamp;
    }
    
    private double GetClusterDeviation2(ArrayList<ClusterElement> elements, double mean){
        // The deviation within a singleton cluster defaults to 0, in order to avoid zero-division.
        if (elements.size() == 1) {
            return 0;
        }
        // Otherwise, calculate the sample standard deviation of the cluster.
        else {
            double sum = 0;
            for (int i = 0; i < elements.size();i++){
                sum += Math.pow(elements.get(i).TimeStamp - mean, 2);
            }
            sum = sum / (elements.size() - 1.0);
            sum = Math.sqrt(sum);
            return sum;
        }
    }
    
    private double GetIntegerDeviation(ArrayList<Integer> elements, double mean){
        // The deviation within a singleton cluster defaults to 0, in order to avoid zero-division.
        if (elements.size() == 1) {
            return 0;
        }
        // Otherwise, calculate the sample standard deviation of the cluster.
        else {
            double sum = 0;
            for (int i = 0; i < elements.size();i++){
                sum += Math.pow(elements.get(i)- mean ,2);
            }
            sum = sum / (elements.size() - 1.0);
            sum = Math.sqrt(sum);
            return sum;
        }
    }
    
}