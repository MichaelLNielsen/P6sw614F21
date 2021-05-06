package sw614f21.p6project.CODMiner;

import java.util.ArrayList;

public class ClusterPattern implements Comparable<ClusterPattern>  {
    public ArrayList<ClusterSymbol> Pattern = new ArrayList<ClusterSymbol>();
    public int Support;

    @Override
    public int compareTo(ClusterPattern o) {
        return o.Support - Support;
    }
}
