package spp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

public class ShortestPathProblemTest {

    private static ShortestPathProblem shortestPathProblem;

    @BeforeAll
    static void init() {
        shortestPathProblem = new ShortestPathProblem(new int[][]{
                new int[]{Integer.MAX_VALUE, 2               },
                new int[]{2,                 Integer.MAX_VALUE}
        }, 0, 1);
    }

    @Test
    void testGetLengthTwo() {
        assertEquals(2, shortestPathProblem.getLength());
    }

    @Test
    void testEvaluateValidOrInvalid() {
        /* initialise with random permutation sequence */
        PermutationSolution<Integer> solution = new IntegerPermutationSolution(2, 1);
        shortestPathProblem.evaluate(solution);
        assertEquals(solution.getObjective(0), (solution.getVariable(0) == 0) ? 2 : Integer.MAX_VALUE);
    }
}
