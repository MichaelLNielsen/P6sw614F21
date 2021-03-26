package sw614f21.p6project;

import java.util.ArrayList;

public class FakeDataSet {
    ArrayList<Endpoint> sequence1 = new ArrayList<Endpoint>();
    ArrayList<Endpoint> sequence2 = new ArrayList<Endpoint>();
    ArrayList<Endpoint> sequence3 = new ArrayList<Endpoint>();
    ArrayList<Endpoint> sequence4 = new ArrayList<Endpoint>();
    
    public ArrayList<EndpointSequence> GetFakeData (){
        // create sequence1:
        Endpoint s1AStart = new Endpoint(EventType.HeatPumpOn, 1, true, 1);
        Endpoint s1AFinish = new Endpoint(EventType.HeatPumpOn, 4, false, 1);
        
        Endpoint s1BStart = new Endpoint(EventType.PowerPlugsDiningRoomOn, 3, true, 1);
        Endpoint s1BFinish = new Endpoint(EventType.PowerPlugsDiningRoomOn, 6, false, 1);
        
        Endpoint s1CStart = new Endpoint(EventType.PowerPlugsAtticOn, 8, true, 1);
        Endpoint s1CFinish = new Endpoint(EventType.PowerPlugsAtticOn, 12, false, 1);
        
        Endpoint s1DStart = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 8, true, 1);
        Endpoint s1DFinish = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 12, false, 1);
        
        sequence1.add(s1AStart);
        sequence1.add(s1AFinish);
        
        sequence1.add(s1BStart);
        sequence1.add(s1BFinish);
        
        sequence1.add(s1CStart);
        sequence1.add(s1CFinish);
        
        sequence1.add(s1DStart);
        sequence1.add(s1DFinish);
        
        // create sequence2:
        
        Endpoint s2BStart = new Endpoint(EventType.PowerPlugsDiningRoomOn, 3, true, 1);
        Endpoint s2BFinish = new Endpoint(EventType.PowerPlugsDiningRoomOn, 6, false, 1);
        
        Endpoint s2CStart = new Endpoint(EventType.PowerPlugsAtticOn, 8, true, 1);
        Endpoint s2CFinish = new Endpoint(EventType.PowerPlugsAtticOn, 12, false, 1);
        
        Endpoint s2DStart = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 8, true, 1);
        Endpoint s2DFinish = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 12, false, 1);
        
        sequence2.add(s2BStart);
        sequence2.add(s2BFinish);
        
        sequence2.add(s2CStart);
        sequence2.add(s2CFinish);
        
        sequence2.add(s2DStart);
        sequence2.add(s2DFinish);
        
        // create sequence3:
        
        Endpoint s3CStart = new Endpoint(EventType.PowerPlugsAtticOn, 6, true, 1);
        Endpoint s3CFinish = new Endpoint(EventType.PowerPlugsAtticOn, 8, false, 1);
        
        Endpoint s3DStart = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 6, true, 1);
        Endpoint s3DFinish = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 8, false, 1);
        
        sequence3.add(s3CStart);
        sequence3.add(s3CFinish);
        
        sequence3.add(s3DStart);
        sequence3.add(s3DFinish);
        
        // create sequence 4:
        
        Endpoint s4AStart = new Endpoint(EventType.HeatPumpOn, 1, true, 1);
        Endpoint s4AFinish = new Endpoint(EventType.HeatPumpOn, 3, false, 1);
        
        Endpoint s4CStart = new Endpoint(EventType.PowerPlugsAtticOn, 6, true, 1);
        Endpoint s4CFinish = new Endpoint(EventType.PowerPlugsAtticOn, 8, false, 1);
        
        Endpoint s4DStart = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 6, true, 1);
        Endpoint s4DFinish = new Endpoint(EventType.PowerPlugsMasterBedroomOn, 8, false, 1);
        
        Endpoint s4EStart = new Endpoint(EventType.PowerPlugsBedroom2On, 12, true, 1);
        Endpoint s4EFinish = new Endpoint(EventType.PowerPlugsBedroom2On, 14, false, 1);
        
        sequence4.add(s4AStart);
        sequence4.add(s4AFinish);
        
        sequence4.add(s4CStart);
        sequence4.add(s4CFinish);
        
        sequence4.add(s4DStart);
        sequence4.add(s4DFinish);
        
        sequence4.add(s4EStart);
        sequence4.add(s4EFinish);
        
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