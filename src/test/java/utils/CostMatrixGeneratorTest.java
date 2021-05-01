package utils;

import static org.junit.jupiter.api.Assertions.*;

import manifold.ext.rt.api.Jailbreak;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

public class CostMatrixGeneratorTest {
    private static CostMatrixGenerator generator;

    @BeforeAll
    static void init() {
        /* triangle with edges of 10 */
        generator = new CostMatrixGenerator(3, new int[][]{
                new int[]{0, 1, 10},
                new int[]{1, 2, 10},
                new int[]{0, 2, 10}
        });
    }

    @Test
    void testGetCostMatrix() {
        int[][] graph = generator.getCostMatrix();
        for (int i = 0; i < graph.length; ++i) {
            for (int j = 0; j < graph.length; ++j) {
                if (i != j) assertEquals(10, graph[i][j]);
            }
        }
    }

}
