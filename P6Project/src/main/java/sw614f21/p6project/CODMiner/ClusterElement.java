package sw614f21.p6project.CODMiner;

import sw614f21.p6project.DataStructures.EndpointSequence;

public class ClusterElement implements Comparable<ClusterElement> {
    public int TimeStamp;
    public EndpointSequence Sequence;

    public ClusterElement(int timeStamp, EndpointSequence sequence) {
        this.TimeStamp = timeStamp;
        this.Sequence = sequence;
    }

    @Override
    public int compareTo(ClusterElement o) {
        return TimeStamp - o.TimeStamp;
    }

}
