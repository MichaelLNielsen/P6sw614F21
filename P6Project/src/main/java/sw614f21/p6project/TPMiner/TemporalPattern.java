package sw614f21.p6project.TPMiner;

import java.util.ArrayList;

public class TemporalPattern {

    public ArrayList<PatternSymbol> TPattern;

    public TemporalPattern(ArrayList<PatternSymbol> tPattern) {
        TPattern = tPattern;
    }
    
    @Override
    public String toString(){
        String wholeString = "";
        int i;
        for (i = 0; i < TPattern.size() - 1; i++){
            if (TPattern.get(i).SameTimeAsPrevious == false && TPattern.get(i+1).SameTimeAsPrevious){
                wholeString = wholeString.concat("(");
            }
            wholeString = wholeString.concat(TPattern.get(i).toString());
            if (TPattern.get(i).SameTimeAsPrevious == true && TPattern.get(i+1).SameTimeAsPrevious == false){
                wholeString = wholeString.concat(")");
            }
        }
        
        wholeString = wholeString.concat(TPattern.get(i).toString());
        if (TPattern.get(i).SameTimeAsPrevious){
            wholeString = wholeString.concat(")");
        }
        
        return wholeString;
    }

}
