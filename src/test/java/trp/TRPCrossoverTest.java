package trp;

import static org.junit.jupiter.api.Assertions.*;

import manifold.ext.rt.api.Jailbreak;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TRPCrossoverTest {

    private static TRPCrossover trpCrossover;
    private static PermutationSolution<Integer> solution;
    private static List<PermutationSolution<Integer>> parents;

    private static Method getSolutionIndex;
    private static Method checkValidCuttingPoint;

    @BeforeAll
    static void init() throws NoSuchMethodException {
        trpCrossover = new TRPCrossover(1, 100, new int[][]{
                new int[]{Integer.MAX_VALUE, 1                },
                new int[]{1,                 Integer.MAX_VALUE}
        });
        solution = new IntegerPermutationSolution(2, 1);
        parents = new ArrayList<PermutationSolution<Integer>>(){{
            add(solution);
            add(solution);
        }};

        Class<TRPCrossover> trpCrossoverClass = TRPCrossover.class;

        getSolutionIndex = trpCrossoverClass.getDeclaredMethod("getSolutionIndex", PermutationSolution.class, int.class);
        getSolutionIndex.setAccessible(true);

        checkValidCuttingPoint = trpCrossoverClass.getDeclaredMethod("checkValidCuttingPoint", List.class, int.class);
        checkValidCuttingPoint.setAccessible(true);
    }

    @Test
    void testGetSolutionIndexNotFound () throws InvocationTargetException, IllegalAccessException {
        assertEquals(-1, (int) getSolutionIndex.invoke(trpCrossover, solution, Integer.MAX_VALUE));
    }

    @Test
    void testGetSolutionIndexFound() throws InvocationTargetException, IllegalAccessException {
        assertEquals((solution.getVariable(0) == 0)? 0 : 1, (int) getSolutionIndex.invoke(trpCrossover, solution, 0));
    }

    @Test
    void testCheckValidCuttingPointNotValid() throws InvocationTargetException, IllegalAccessException {
        assertTrue((boolean) checkValidCuttingPoint.invoke(trpCrossover, parents, 0));
    }

    @Test
    void testCheckValidCuttingPointValid() throws InvocationTargetException, IllegalAccessException {
        assertFalse((boolean) checkValidCuttingPoint.invoke(trpCrossover, parents, 1));
    }

}
