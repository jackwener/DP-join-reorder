import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        DpSub DpSub = new DpSub();

        URL path = DpCcp.class.getResource("edges");
        assert path != null;
        File file = new File(path.getFile());
        Scanner reader = new Scanner(file);
        String firstLine = reader.nextLine();
        String[] s = firstLine.split("\\s+");
        int n = Integer.parseInt(s[0]);

        ArrayList<Integer> dp = new ArrayList<>(1 << n);
        fill(dp, 1 << n, -1);

        ArrayList<Pair<Integer, Integer>> allEdges = new ArrayList<>();
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            String[] line = data.split("\\s+");
            System.out.println(Arrays.toString(line));
            assert line.length == 2;
            int src = Integer.parseInt(line[0]);
            int dst = Integer.parseInt(line[1]);
            allEdges.add(new Pair<>(src, dst));
        }

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
