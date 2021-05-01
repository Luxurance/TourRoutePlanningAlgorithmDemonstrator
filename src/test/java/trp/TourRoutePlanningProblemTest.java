package trp;

import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TourRoutePlanningProblemTest {

    private final static int PENALTY = 10000;

    private static List<TourPlace> places;
    private static TourRoutePlanningProblem problem;

    private static Method calGaussianPop;
    private static Method evaluatePlaceProperties;
    private static Method calMeanSdPop;

    @BeforeAll
    static void init() throws NoSuchMethodException {
        /* define the graph topology */
        int[][] costMatrix = new int[][] {
                new int[]{Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE, Integer.MAX_VALUE},
                new int[]{1,                 Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE},
                new int[]{Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE, 1                },
                new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 1,                 Integer.MAX_VALUE}
        };
        /* define the places' properties */
        double[][] propertyMatrix = new double[][]{
                new double[]{0.767, 95, 0},
                new double[]{0.786, 55, 7},
                new double[]{0.035, 43, 0},
                new double[]{0.963, 69, 14},
        };
        /* add places into the list */
        places = new ArrayList<>();
        for (int i = 0; i < propertyMatrix.length; i++) {
            double popularity = propertyMatrix[i][0];
            int attractionCount = (int) propertyMatrix[i][1];
            int quarantinePeriod = (int) propertyMatrix[i][2];
            places.add(new TourPlace(i, "Place?".replace("?", Integer.toString(i)), popularity, attractionCount, quarantinePeriod));
        }

        problem = new TourRoutePlanningProblem(costMatrix, places, 0, 3, PENALTY);

        Class<TourRoutePlanningProblem> tourRoutePlanningProblem = TourRoutePlanningProblem.class;

        calGaussianPop = tourRoutePlanningProblem.getDeclaredMethod("calGaussianPop", double.class);
        calGaussianPop.setAccessible(true);

        evaluatePlaceProperties = tourRoutePlanningProblem.getDeclaredMethod("evaluatePlaceProperties",
                PermutationSolution.class, int.class);
        evaluatePlaceProperties.setAccessible(true);

        calMeanSdPop = tourRoutePlanningProblem.getDeclaredMethod("calMeanSdPop", List.class);
        calMeanSdPop.setAccessible(true);
    }

    @Test
    void testGetLength() {
        assertEquals(places.size(), problem.getLength());
    }

    @Test
    void evaluateInvalidRoute() {
        PermutationSolution<Integer> solution = new IntegerPermutationSolution(4, 4){{
            setVariable(0, 1);
            setVariable(1, 0);
            setVariable(2, 3);
            setVariable(3, 2);
        }};
        problem.evaluate(solution);
        assertTrue(solution.getObjective(0) >= PENALTY);
    }

    @Test
    void evaluateValidRoute() {
        PermutationSolution<Integer> solution = new IntegerPermutationSolution(4, 4){{
            setVariable(0, 0);
            setVariable(1, 1);
            setVariable(2, 2);
            setVariable(3, 3);
        }};
        problem.evaluate(solution);
        assertTrue(solution.getObjective(0) < PENALTY);
    }

    @Test
    void testEvaluatePlaceProperties() throws InvocationTargetException, IllegalAccessException {
        PermutationSolution<Integer> solution = new IntegerPermutationSolution(4, 4){{
            setVariable(0, 0);
            setVariable(1, 3);
            setVariable(2, 1);
            setVariable(3, 2);
        }};
        assertArrayEquals(new double[]{
                (Double) calGaussianPop.invoke(problem, places.get(0).getPopularity())  + (Double) calGaussianPop.invoke(problem, places.get(3).getPopularity()),
                places.get(0).getAttractionsCount() + places.get(3).getAttractionsCount(),
                places.get(0).getQuarantinePeriod() + places.get(3).getQuarantinePeriod()
        }, (double[]) evaluatePlaceProperties.invoke(problem, solution, 1));
    }

    @Test
    void testCalGaussian() throws InvocationTargetException, IllegalAccessException {
        /* first arg calculated by external tool */
        assertEquals(0.268, round((double) calGaussianPop.invoke(problem, 0.035) * 1000.0) / 1000.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCalMeanSdPop() throws InvocationTargetException, IllegalAccessException {
        Pair<Double, Double> pair = (Pair<Double, Double>) calMeanSdPop.invoke(problem, places);
        /* first args by calculator */
        assertEquals(0.63775, round(pair.getFirst() * 100000.0) / 100000.0);
        assertEquals(0.35629, round(pair.getSecond() * 100000.0) / 100000.0);
    }
}
