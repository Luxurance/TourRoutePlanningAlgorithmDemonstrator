package utils;

import static org.junit.jupiter.api.Assertions.*;

import manifold.ext.rt.api.Jailbreak;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

public class GraphScalerTest {
    /* linear line with length 3 */
    private static final int[][] graph = new int[][] {
            new int[]{Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE},
            new int[]{1,                 Integer.MAX_VALUE, 1,               },
            new int[]{Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE}
    };
    private static final int scalingFactor = 3;
    private static GraphScaler scaler;

    @BeforeAll
    static void init() {
        scaler = new GraphScaler(graph, scalingFactor, 3);
    }

    @Test
    void testGetScaledGraphSize() {
        int[][] scaledGraph = scaler.getScaledGraph();
        assertEquals(graph.length * scalingFactor, scaledGraph.length);
    }

    @Test
    void testGetScaledGraphFullDensity() {
        int[][] scaledGraph = scaler.getScaledGraph();
        assertNotEquals(0, scaledGraph[0][5]);
        assertNotEquals(0, scaledGraph[1][4]);
        assertNotEquals(0, scaledGraph[2][3]);
        assertNotEquals(0, scaledGraph[3][8]);
        assertNotEquals(0, scaledGraph[4][7]);
        assertNotEquals(0, scaledGraph[5][6]);
    }
}
