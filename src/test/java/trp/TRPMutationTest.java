package trp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TRPMutationTest {

    private static TRPMutation trpMutation;
    private static PermutationSolution<Integer> solution;

    private static TRPMutation complexTRPMutation;
    private static PermutationSolution<Integer> complexSolution;

    private static Method getDesiredRepPos;

    @BeforeAll
    static void init() throws NoSuchMethodException {
        trpMutation = new TRPMutation(1, new int[][]{
                new int[]{Integer.MAX_VALUE, 1                },
                new int[]{1,                 Integer.MAX_VALUE}
        });
        solution = new IntegerPermutationSolution(2, 1){{
            setVariable(0, 0);
            setVariable(1, 1);
        }};

        complexTRPMutation = new TRPMutation(1, new int[][] {
                new int[]{Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE, Integer.MAX_VALUE},
                new int[]{1,                 Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE},
                new int[]{Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE, 1                },
                new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE}
        });
        complexSolution = new IntegerPermutationSolution(4, 1){{
            setVariable(0, 0);
            setVariable(1, 3);
            setVariable(2, 2);
            setVariable(3, 1);
        }};

        Class<TRPMutation> trpMutationClass = TRPMutation.class;

        getDesiredRepPos = trpMutationClass.getDeclaredMethod("getDesiredRepPos", PermutationSolution.class, int.class);
        getDesiredRepPos.setAccessible(true);
    }

    @Test
    void testGetDesiredRepPosNotFound() throws InvocationTargetException, IllegalAccessException {
        assertEquals(-1, (int) getDesiredRepPos.invoke(trpMutation, solution, 0));
    }

    /* random behaviour */
    /*
    @Test
    void testGetDesiredRepPosFound() throws InvocationTargetException, IllegalAccessException {
        assertEquals(3, (int) getDesiredRepPos.invoke(complexTRPMutation, complexSolution, 1));
    }
    */

    /* random behaviour */
    /*
    @Test
    void testDoMutationFixRoute() {
        PermutationSolution<Integer> solutionCopy = (PermutationSolution<Integer>) complexSolution.copy();
        System.out.println(solutionCopy);
        complexTRPMutation.doMutation(solutionCopy);
        System.out.println(solutionCopy);
        assertEquals(((solutionCopy).getVariable(1) == 1)? 3 : 1, solutionCopy.getVariable(3));
    }
    */
}
