package sw614f21.p6project.Preprocessing;

import sw614f21.p6project.DataStructures.Endpoint;
import sw614f21.p6project.DataStructures.EndpointSequence;

import java.util.ArrayList;
import java.util.Collections;

public class FakeDataSet {
    ArrayList<Endpoint> sequence1 = new ArrayList<Endpoint>();
    ArrayList<Endpoint> sequence2 = new ArrayList<Endpoint>();
    ArrayList<Endpoint> sequence3 = new ArrayList<Endpoint>();
    ArrayList<Endpoint> sequence4 = new ArrayList<Endpoint>();
    
    public ArrayList<EndpointSequence> GetFakeData (){
        // create sequence1:
        Endpoint s1AStart = new Endpoint("A", 1, true, 1);
        Endpoint s1AFinish = new Endpoint("A", 4, false, 1);
        
        Endpoint s1BStart = new Endpoint("B", 3, true, 1);
        Endpoint s1BFinish = new Endpoint("B", 6, false, 1);
        
        Endpoint s1CStart = new Endpoint("C", 8, true, 1);
        Endpoint s1CFinish = new Endpoint("C", 12, false, 1);
        
        Endpoint s1DStart = new Endpoint("D", 8, true, 1);
        Endpoint s1DFinish = new Endpoint("D", 12, false, 1);
        
        sequence1.add(s1AStart);
        sequence1.add(s1AFinish);
        
        sequence1.add(s1BStart);
        sequence1.add(s1BFinish);
        
        sequence1.add(s1CStart);
        sequence1.add(s1CFinish);
        
        sequence1.add(s1DStart);
        sequence1.add(s1DFinish);
        
        // create sequence2:
        
        Endpoint s2BStart = new Endpoint("B", 3, true, 1);
        Endpoint s2BFinish = new Endpoint("B", 6, false, 1);
        
        Endpoint s2CStart = new Endpoint("C", 8, true, 1);
        Endpoint s2CFinish = new Endpoint("C", 12, false, 1);
        
        Endpoint s2DStart = new Endpoint("D", 8, true, 1);
        Endpoint s2DFinish = new Endpoint("D", 12, false, 1);
        
        sequence2.add(s2BStart);
        sequence2.add(s2BFinish);
        
        sequence2.add(s2CStart);
        sequence2.add(s2CFinish);
        
        sequence2.add(s2DStart);
        sequence2.add(s2DFinish);
        
        // create sequence3:
        
        Endpoint s3CStart = new Endpoint("C", 6, true, 1);
        Endpoint s3CFinish = new Endpoint("C", 8, false, 1);
        
        Endpoint s3DStart = new Endpoint("D", 6, true, 1);
        Endpoint s3DFinish = new Endpoint("D", 8, false, 1);
        
        sequence3.add(s3CStart);
        sequence3.add(s3CFinish);
        
        sequence3.add(s3DStart);
        sequence3.add(s3DFinish);
        
        // create sequence 4:
        
        Endpoint s4AStart = new Endpoint("A", 1, true, 1);
        Endpoint s4AFinish = new Endpoint("A", 3, false, 1);
        
        Endpoint s4CStart = new Endpoint("C", 6, true, 1);
        Endpoint s4CFinish = new Endpoint("C", 8, false, 1);
        
        Endpoint s4DStart = new Endpoint("D", 6, true, 1);
        Endpoint s4DFinish = new Endpoint("D", 8, false, 1);
        
        Endpoint s4EStart = new Endpoint("E", 12, true, 1);
        Endpoint s4EFinish = new Endpoint("E", 14, false, 1);
        
        sequence4.add(s4AStart);
        sequence4.add(s4AFinish);
        
        sequence4.add(s4CStart);
        sequence4.add(s4CFinish);
        
        sequence4.add(s4DStart);
        sequence4.add(s4DFinish);
        
        sequence4.add(s4EStart);
        sequence4.add(s4EFinish);
        
        
        Collections.sort(sequence1);
        Collections.sort(sequence2);
        Collections.sort(sequence3);
        Collections.sort(sequence4);
        
        EndpointSequence S1 = new EndpointSequence(1, sequence1);
        EndpointSequence S2 = new EndpointSequence(2, sequence2);
        EndpointSequence S3 = new EndpointSequence(3, sequence3);
        EndpointSequence S4 = new EndpointSequence(4, sequence4);
        
        ArrayList<EndpointSequence> output = new ArrayList<EndpointSequence>();
        
        output.add(S1);
        output.add(S2);
        output.add(S3);
        output.add(S4);
        
        return output;
    }
}
