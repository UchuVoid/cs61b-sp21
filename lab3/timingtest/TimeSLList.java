package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        Stopwatch sw =new Stopwatch();
        AList<Integer> Ns=new AList();
        AList<Double> times=new AList();
        AList<Integer> opCounts=new AList();
        SLList<Integer> list=new SLList<>();
        int bei =1;
        for (int i = 1; i <= 64000; i++) {
            list.addLast(i);
            if (i==1000*bei) {
                bei*=2;
                Ns.addLast(list.size());
                opCounts.addLast(i);
                times.addLast(sw.elapsedTime());
            }
        }
        printTimingTable(Ns,times,opCounts);
    }

}
