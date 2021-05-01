package utils;

import org.uma.jmetal.util.JMetalException;

import java.util.Arrays;
import java.util.Random;

public class GraphScaler {

    private final int[][] adjacencyMatrix;

    /* the number of duplicates - 1 */
    private final int scalingFactor;

    /* the number of connection to neighbouring duplicated graphs */
    private final int density;

    private final Random random;

    public GraphScaler(int[][] adjacencyMatrix, int scalingFactor, int density) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.scalingFactor = scalingFactor;
        this.density = density;
        this.random = new Random();
        /* if so undefine behavior on the graph */
        if (this.density > adjacencyMatrix.length) {
            throw new JMetalException("Density should not be larger than the sub-graph node number.");
        }
    }

    public int[][] getScaledGraph() {
        int oldGraphSize = this.adjacencyMatrix.length;
        int newGraphSize = this.scalingFactor * oldGraphSize;
        int[][] newGraph = new int[newGraphSize][newGraphSize];
        /* initialise the matrix with 0 values */
        for (int[] row : newGraph) {
            Arrays.fill(row, 0);
        }

        for (int i = 0; i < newGraphSize; i++) {
            int copyIndex = i % oldGraphSize;
            int subgraphIndex = i / oldGraphSize;
            /* Copy the previous graph as subgraph*/
            for (int j = 0; j < oldGraphSize; j++) {
                newGraph[i][j + subgraphIndex * oldGraphSize] = this.adjacencyMatrix[copyIndex][j];
            }
            /* make connections to the previous graph */
            if (i >= oldGraphSize && copyIndex < this.density) {
                /* connecting point of previous sub-graph */
                int prevIndex = (i - 1 - 2 * copyIndex);
                int distance = random.nextInt(10) + 1; /* non-zero */
                newGraph[prevIndex][i] = distance;
                newGraph[i][prevIndex] = distance;
            }
        }

        return newGraph;
    }

}
