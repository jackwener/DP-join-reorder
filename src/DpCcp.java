import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DpCcp {
    ArrayList<Integer> vertexToLabel;
    ArrayList<Integer> labelToVertex;
    int[] neighbor = new int[1 << 15];

    void bfsAddLabel(int n, ArrayList<List<Integer>> edges) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        vertexToLabel = new ArrayList<>(n);
        ensureSize(vertexToLabel, n);
        labelToVertex = new ArrayList<>(n);
        ensureSize(labelToVertex, n);

        int label = 0;
        boolean[] visited = new boolean[n];
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (visited[cur]) continue;

            visited[cur] = true;
            vertexToLabel.set(cur, label);
            labelToVertex.set(label, cur);

            label++;

            for (Integer neighbor : edges.get(cur)) {
                if (visited[neighbor]) continue;
                queue.add(neighbor);
            }
        }
    }

    // get the direct neighbor list of given nodes.
    int getNeighbor(int vertexSet, ArrayList<List<Integer>> edges) {
        if (neighbor[vertexSet] != -1) {
            return neighbor[vertexSet];
        }
        int ans = 0;
        if (vertexSet > 0) {
            int firstOne = Integer.numberOfTrailingZeros(vertexSet);
            ans = getNeighbor(vertexSet ^ (1 << firstOne), edges);
            for (Integer x : edges.get(vertexToLabel.get(firstOne))) {
                ans |= (1 << labelToVertex.get(x));
            }
            ans &= ~vertexSet;
        }
        return neighbor[vertexSet] = ans;
    }

    void enumerateCsg(int n, ArrayList<List<Integer>> edges, ArrayList<Integer> sg) {
        // 按 label 递减遍历点
        for (int i = n - 1; i >= 0; --i) {
            sg.add(1 << i);
            enumerateCsgRec(1 << i, smallThan(i), edges, sg);
        }
    }

    void enumerateCsgRec(int curNodeSet, int excludeNodeSet, ArrayList<List<Integer>> edges, ArrayList<Integer> sg) {
        int neighbor = getNeighbor(curNodeSet, edges);
        neighbor &= ~excludeNodeSet;
        ArrayList<Integer> allSubSet = new ArrayList<>(1 << Integer.bitCount(neighbor));
        for (int nowSet = neighbor; nowSet > 0; nowSet = (nowSet - 1) & neighbor) {
            allSubSet.add(nowSet);
        }
        Collections.reverse(allSubSet);

        // 从小的开始
        for (Integer subSet : allSubSet) {
            sg.add(subSet | curNodeSet);
        }
        // 从小的开始
        for (Integer subSet : allSubSet) {
            enumerateCsgRec(curNodeSet | subSet, excludeNodeSet | neighbor, edges, sg);
        }
    }

    void enumerateCmp(int setToBePaired, ArrayList<List<Integer>> edges, ArrayList<Integer> paired) {
        int minOfSet = Integer.numberOfTrailingZeros(setToBePaired);
        int setToExclude = smallThan(minOfSet) | setToBePaired;
        int N = getNeighbor(setToBePaired, edges) & (~setToExclude);
        // System.out.println(setToBePaired + " " + minOfSet + " " + setToExclude + " " + N);

        ArrayList<Integer> nodesOfSet = mask2vector(N);
        Collections.reverse(nodesOfSet);
        for (Integer now : nodesOfSet) {
            paired.add(1 << now);
            enumerateCsgRec(1 << now, setToExclude | (smallThan(now) & N), edges, paired);
        }
    }

    ArrayList<Integer> mask2vector(int nodeSet) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 0; nodeSet > 0; ++i, nodeSet >>= 1) {
            if ((nodeSet & 1) == 1) {
                ret.add(i);
            }
        }
        return ret;
    }

    // 返回 小于 n 的 bitset
    // 4 -> ... 0000 1111
    // 00001111 = 15
    // 15 这个 bitset 包含 0 1 2 3
    int smallThan(int n) {
        return (1 << n) - 1;
    }

    public static void ensureSize(ArrayList<?> list, int size) {
        list.ensureCapacity(size);
        while (list.size() < size) {
            list.add(null);
        }
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException, NumberFormatException {
        DpCcp dpCcp = new DpCcp();

        URL path = DpCcp.class.getResource("edges");
        assert path != null;
        File file = new File(path.getFile());
        Scanner reader = new Scanner(file);
        String firstLine = reader.nextLine();
        String[] s = firstLine.split("\\s+");
        int n = Integer.parseInt(s[0]);

        ArrayList<List<Integer>> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            edges.add(new ArrayList<>());
        }

        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            String[] line = data.split("\\s+");
            System.out.println(Arrays.toString(line));
            assert line.length == 2;
            int src = Integer.parseInt(line[0]);
            int dst = Integer.parseInt(line[1]);
            edges.get(src).add(dst);
        }

        double startMillis = System.currentTimeMillis();

        dpCcp.bfsAddLabel(n, edges);

        int totCnt = 0;
        Arrays.fill(dpCcp.neighbor, -1);
        ArrayList<Integer> subGraphSet = new ArrayList<>();
        ArrayList<Integer> paired = new ArrayList<>();

        dpCcp.enumerateCsg(n, edges, subGraphSet);

        for (Integer nowSet : subGraphSet) {
            System.out.println("now set:" + nowSet + ", actual nodes: " + nowSet);

            paired.clear();
            dpCcp.enumerateCmp(nowSet, edges, paired);
            totCnt += paired.size();
            for (Integer pairedSet : paired) {
                //printForCheck(nowSet, pairedSet);
                System.out.println("nowSet: " + dpCcp.mask2vector(nowSet));
                System.out.println("pairedSet: " + dpCcp.mask2vector(pairedSet));
                simulateDPCost();
            }
        }


        double endMillis = System.currentTimeMillis();
        double useTime = (endMillis - startMillis) / 1000 * 0.1;
        System.out.println("耗时：" + useTime);
    }

    static void simulateDPCost() throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(1);
    }
}
