package spp;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import spp.ShortestPathProblem;

import java.util.List;

public class SPPMain {
    public static void main(String[] args) {
        Problem<PermutationSolution<Integer>> problem;
        Algorithm<List<PermutationSolution<Integer>>> algorithm;
        CrossoverOperator<PermutationSolution<Integer>> crossover;
        MutationOperator<PermutationSolution<Integer>> mutation;
        SelectionOperator<List<PermutationSolution<Integer>>,PermutationSolution<Integer>> selection;

        /* define the cost matrix */
        int[][] costMatrix = new int[][]{
                new int[]{0, 4, 2, 0, 0, 0},
                new int[]{4, 0, 5, 10, 0 ,0},
                new int[]{2, 5, 0, 0, 3, 0},
                new int[]{0, 10, 0, 0, 4, 11},
                new int[]{0, 0, 3, 4, 0, 0},
                new int[]{0, 0, 0, 11, 0, 0}
        };

        /* replace 0 to inf */
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                costMatrix[i][j] = costMatrix[i][j] == 0? Integer.MAX_VALUE : costMatrix[i][j];
            }
        }

        /* define the problem to optimise */
        problem = new ShortestPathProblem(costMatrix, 0, 3);

        /* crossover parameters */
        double crossoverProbability = 0.9;
        crossover = new PMXCrossover(crossoverProbability);

        /* mutation parameter */
        double mutationProbability = 0.9;
        mutation = new PermutationSwapMutation<>(mutationProbability);

        /* create selection */
        selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(
                new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

        /* pass the components to the algorithm */
        algorithm = new NSGAIIBuilder<PermutationSolution<Integer>>(problem, crossover, mutation, 100)
                .setSelectionOperator(selection)
                .setMaxEvaluations(25000)
                .build();

        /* run the algorithm directly */
        long startTime = System.currentTimeMillis();
        algorithm.run();

        /* get results */
        List<PermutationSolution<Integer>> population = algorithm.getResult();
        long timeComputed = System.currentTimeMillis() - startTime;
        System.out.printf("Result pupulation is:\n %s\n", population);
        System.out.printf("Computing time: %d milliseconds\n", timeComputed);

        System.out.println(evaluate(costMatrix, 0, 3, new int[]{0, 2, 4, 3, 2, 5}));
    }

    public static int evaluate(int[][] costMatrix, int source, int target, int[] solution) {
        int f = 0;
        int endIndex = solution.length - 1;

        /* Check source before evaluation */
        if (solution[0] != source) {
            return Integer.MAX_VALUE;
        }

        for (int i = 0; i < solution.length - 1; i++) {
            int cost = costMatrix[solution[i]][solution[i+1]];

            /* Solution contains unreachable path */
            if (cost == Integer.MAX_VALUE) {
                /* End of solution */
                if (solution[i] == target){
                    endIndex = i;
                    break;
                }
                f = cost;
                break;
            }

            /* Add cost of each pair of neighbouring nodes */
            f += cost;

        }

        /* Target not match */
        if (solution[endIndex] != target) {
            return Integer.MAX_VALUE;
        } else {
            /* Flip the sign to maximise */
            return f;
        }
    }
}
