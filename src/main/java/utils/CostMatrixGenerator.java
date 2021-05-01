package utils;

public class CostMatrixGenerator {

    private int[][] edges;
    private int size;

    /**
     * constructor
     * @param size cost matrix size
     * @param edges set of edges to be added in the graph
     */
    public CostMatrixGenerator (int size, int[][] edges) {
        this.edges = edges;
        this.size = size;
    }

    public int[][] getCostMatrix() {
        int[][] costMatrix = new int[this.size][this.size];
        for (int[] edge : this.edges) {
            costMatrix[edge[0]][edge[1]] = edge[2];
            costMatrix[edge[1]][edge[0]] = edge[2];
        }
        return costMatrix;
    }

    /**
     * @return the distance cost matrix of london underground example
     */
    public static int[][] getLondonUndergroundGraph() {
        return new CostMatrixGenerator(31, new int[][]{
                new int[]{0, 1, 2},
                new int[]{1, 2, 3},
                new int[]{1, 3, 9},
                new int[]{2, 3, 9},
                new int[]{3, 4, 4},
                new int[]{3, 13, 9},
                new int[]{3, 14, 10},
                new int[]{4, 5, 4},
                new int[]{5, 6, 3},
                new int[]{5, 9, 2},
                new int[]{6, 7, 1},
                new int[]{6, 8, 6},
                new int[]{6, 9, 5},
                new int[]{8, 9, 2},
                new int[]{8, 10, 12},
                new int[]{8, 12, 8},
                new int[]{8, 15, 10},
                new int[]{8, 18, 11},
                new int[]{9, 14, 11},
                new int[]{10, 11, 3},
                new int[]{10, 12, 8},
                new int[]{11, 12, 3},
                new int[]{11, 15, 9},
                new int[]{13, 14, 12},
                new int[]{14, 15, 7},
                new int[]{14, 17, 8},
                new int[]{14, 18, 4},
                new int[]{15, 28, 3},
                new int[]{15, 30, 5},
                new int[]{16, 17, 3},
                new int[]{17, 18, 4},
                new int[]{17, 21, 9},
                new int[]{18, 21, 6},
                new int[]{18, 29, 4},
                new int[]{19, 20, 1},
                new int[]{20, 21, 3},
                new int[]{21, 22, 4},
                new int[]{22, 23, 12},
                new int[]{22, 24, 14},
                new int[]{22, 26, 6},
                new int[]{23, 24, 6},
                new int[]{24, 25, 20},
                new int[]{25, 26, 5},
                new int[]{26, 27, 6},
                new int[]{27, 29, 13},
                new int[]{27, 28, 2},
                new int[]{28, 29, 10},
                new int[]{29, 30, 1}
        }).getCostMatrix();
    }
}
