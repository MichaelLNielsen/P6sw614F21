package sw614f21.p6project.CODMiner;

public class SumOfSquares {

    public double i = 0;
    public double mean = 0;
    public double d = 0;

    public double Increment(int x){
        i++;
        d += (i - 1) / i * Math.pow(x - mean, 2);
        mean = (x + (i - 1) * mean) / i;
        return d;
    }

}
