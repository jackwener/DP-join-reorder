import javafx.util.Pair;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DpSub {
    int totCnt;

    void simulateDP(int s1, int s2, ArrayList<Integer> dp, ArrayList<Pair<Integer, Integer>> edges) throws InterruptedException {
        for (Pair<Integer, Integer> curEdge : edges) {
            if ((s1 & (1 << curEdge.getKey())) != 0 && (s2 & (1 << curEdge.getValue())) != 0) {
                dp.set(s1 | s2, 0);
                simulateDPCost();
                ++totCnt;
                return;
            }
            if ((s1 & (1 << curEdge.getValue())) != 0 && (s2 & (1 << curEdge.getKey())) != 0) {
                dp.set(s1 | s2, 0);
                simulateDPCost();
                ++totCnt;
                return;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DpSub DpSub = new DpSub();

        int n = 8;
        ArrayList<Integer> dp = new ArrayList<>(1 << n);
        fill(dp, 1 << n, -1);
//        ArrayList<List<Integer>> edges = new ArrayList<>();
//        edges.add(Arrays.asList(1));
//        edges.add(Arrays.asList(0, 2));
//        edges.add(Arrays.asList(1, 3));
//        edges.add(Arrays.asList(2, 4));
//        edges.add(Arrays.asList(3, 5));
//        edges.add(Arrays.asList(4, 6));
//        edges.add(Arrays.asList(5, 7));
//        edges.add(Arrays.asList(6));
        ArrayList<Pair<Integer, Integer>> allEdges = new ArrayList<>();
        allEdges.add(new Pair<>(0, 1));
        allEdges.add(new Pair<>(1, 2));
        allEdges.add(new Pair<>(2, 3));
        allEdges.add(new Pair<>(3, 4));
        allEdges.add(new Pair<>(4, 5));
        allEdges.add(new Pair<>(5, 6));
        allEdges.add(new Pair<>(6, 7));

        for (int i = 0; i < n; ++i) {
            dp.set(1 << i, 0);
        }
        DpSub.totCnt = 0;
        double startMillis = System.currentTimeMillis();

        for (int state = 1; state < (1 << n); ++state) {
            if (Integer.bitCount(state) == 1) {
                continue;
            }
            for (int sub = (state - 1) & state; sub > 0; sub = (sub - 1) & state) {
                int remain = state ^ sub;
                if (sub > remain) {
                    continue;
                }
                if (dp.get(sub) == -1 || dp.get(remain) == -1) {
                    continue;
                }
                DpSub.simulateDP(sub, remain, dp, allEdges);
            }
        }

        double endMillis = System.currentTimeMillis();
        double useTime = (endMillis - startMillis) / 1000 * 0.1;
        System.out.println("耗时：" + useTime);
    }

    static void simulateDPCost() throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(1);
    }

    public static void fill(ArrayList<Integer> list, int size, int item) {
        list.ensureCapacity(size);
        while (list.size() < size) {
            list.add(item);
        }
    }
}
