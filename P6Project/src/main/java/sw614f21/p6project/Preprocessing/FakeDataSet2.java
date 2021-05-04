package sw614f21.p6project.Preprocessing;

import sw614f21.p6project.DataStructures.Endpoint;
import sw614f21.p6project.DataStructures.EndpointSequence;

import java.util.ArrayList;
import java.util.Collections;

public class FakeDataSet2 {
    ArrayList<Endpoint> sequence1 = new ArrayList<Endpoint>();
    ArrayList<Endpoint> sequence2 = new ArrayList<Endpoint>();


    public ArrayList<EndpointSequence> GetFakeData (){
        // create sequence1:
        Endpoint s1AStart = new Endpoint("A", 1, true, 1);
        Endpoint s1AFinish = new Endpoint("A", 4, false, 1);

        Endpoint s1BStart = new Endpoint("B", 300, true, 1);
        Endpoint s1BFinish = new Endpoint("B", 320, false, 1);

        sequence1.add(s1AStart);
        sequence1.add(s1AFinish);

        sequence1.add(s1BStart);
        sequence1.add(s1BFinish);


        // create sequence2:

        Endpoint s2AStart = new Endpoint("A", 1, true, 1);
        Endpoint s2AFinish = new Endpoint("A", 4, false, 1);

        Endpoint s2AAStart = new Endpoint("A", 50, true, 2);
        Endpoint s2AAFinish = new Endpoint("A", 70, false, 2);

        Endpoint s2AAAStart = new Endpoint("A", 200, true, 3);
        Endpoint s2AAAFinish = new Endpoint("A", 220, false, 3);

        Endpoint s2BStart = new Endpoint("B", 300, true, 1);
        Endpoint s2BFinish = new Endpoint("B", 320, false, 1);

        sequence2.add(s2AStart);
        sequence2.add(s2AFinish);

        sequence2.add(s2AAStart);
        sequence2.add(s2AAFinish);

        sequence2.add(s2AAAStart);
        sequence2.add(s2AAAFinish);

        sequence2.add(s2BStart);
        sequence2.add(s2BFinish);


        Collections.sort(sequence1);
        Collections.sort(sequence2);


        EndpointSequence S1 = new EndpointSequence(1, sequence1);
        EndpointSequence S2 = new EndpointSequence(2, sequence2);

        ArrayList<EndpointSequence> output = new ArrayList<EndpointSequence>();

        output.add(S1);
        output.add(S2);

        return output;
    }
}
