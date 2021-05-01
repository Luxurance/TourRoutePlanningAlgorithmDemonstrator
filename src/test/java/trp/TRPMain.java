package trp;

import org.math.plot.Plot2DPanel;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import utils.CostMatrixGenerator;
import utils.GraphScaler;
import utils.TourPlacesGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TRPMain {
    public static void main(String[] args) {
        Problem<PermutationSolution<Integer>> problem;
        MeasurableNSGAII<PermutationSolution<Integer>> algorithm;
        CrossoverOperator<PermutationSolution<Integer>> crossover;
        MutationOperator<PermutationSolution<Integer>> mutation;
        SelectionOperator<List<PermutationSolution<Integer>>,PermutationSolution<Integer>> selection;

        /* initialise measures */
        double[]   distanceCosts;
        double[] popularityCosts;
        double[] attractionCosts;
        double[] quarantineCosts;

        /* define the cost matrix */
        int[][] baseMatrix = new int[][]{
                new int[]{0, 4, 2, 0, 0, 0},
                new int[]{4, 0, 5, 10, 0 ,0},
                new int[]{2, 5, 0, 0, 3, 0},
                new int[]{0, 10, 0, 0, 4, 11},
                new int[]{0, 0, 3, 4, 0, 0},
                new int[]{0, 0, 0, 11, 0, 0}
        };

        /* To be adjusted to experiment */
        int scalingFactor = 3;
        int density = 3;
        int source = 5;
        int target = 27;
        int iterationAllowed = 500000;
        int populationSize = 100;
        double crossoverProbability = 0.9;
        double mutationProbability = 0.9;
        int invalidSolutionPenalty = 10000;
        int ingredient = 6; /* TRPCrossover + TRPMutation */
        /* repeating experiment for precision */
        int sampleSize = 50;
        /* use artificial graph or dataset-derived */
        boolean useDerived = true;

        /* candidate hyper-parameter list */
        double[] probabilities = new double[]{0.3, 0.6, 0.9};
        int[] populationSizes = new int[]{20, 40, 60, 80, 100, 120, 140, 160, 180};
        int[] invalidSolutionsPenalties = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
        int[] ingredients = new int[]{1,2,3,6}; /* mod 2,3 */

        /* color list for plotting */
        Color[] colors = new Color[]{Color.BLACK, Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.MAGENTA,
                                     Color.RED, Color.ORANGE, Color.PINK};

        /* generate larger matrix */
        int[][] scaledMatrix = useDerived?
                CostMatrixGenerator.getLondonUndergroundGraph() :
                new GraphScaler(baseMatrix, scalingFactor, density).getScaledGraph();

        int[][] scaledMatrixCopy = scaledMatrix.clone();

        /* replace 0 with inf */
        for (int i = 0; i < scaledMatrix.length; i++) {
            for (int j = 0; j < scaledMatrix[0].length; j++) {
                scaledMatrix[i][j] = scaledMatrix[i][j] == 0? Integer.MAX_VALUE : scaledMatrix[i][j];
            }
        }

        /* define the places' properties */
        double[][] propertyMatrix = new double[][]{
                new double[]{0.767, 95, 0},
                new double[]{0.786, 55, 7},
                new double[]{0.035, 43, 0},
                new double[]{0.963, 69, 14},
                new double[]{0.584, 74, 21},
                new double[]{0.081, 65, 0}
        };

        /* scale the properties matrix as well */
        double[][] scaledPropertyMatrix = new double[propertyMatrix.length * scalingFactor][propertyMatrix[0].length];
        for (int i = 0; i < scalingFactor * propertyMatrix.length; ++i) {
            scaledPropertyMatrix[i] = propertyMatrix[i % propertyMatrix.length];
        }
        System.out.println(Arrays.deepToString(scaledPropertyMatrix));

        /* get tour places from public dataset */
        List<TourPlace> derivedPlaces = null;
        try {
            derivedPlaces = new TourPlacesGenerator("src/main/resources/transformedData/tour_places.csv").getTourPlaces();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* add places into the list */
        List<TourPlace> places = new ArrayList<>();
        for (int i = 0; i < propertyMatrix.length * scalingFactor; i++) {
            double popularity = scaledPropertyMatrix[i][0];
            int attractionCount = (int) scaledPropertyMatrix[i][1];
            int quarantinePeriod = (int) scaledPropertyMatrix[i][2];
            places.add(new TourPlace(i, "Place?".replace("?", Integer.toString(i)), popularity, attractionCount, quarantinePeriod));
        }

        /* plot graph for measures */
        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();
        plot.setFont(new Font("Arial", Font.BOLD, 16));
        plot.setAxisLabel(0, "Generations");
        plot.setAxisLabel(1, "Distance Costs");

        // initialise x with increasing numbers until the size of costs
        double[] x = new double[iterationAllowed/populationSize-1];
        for (int i = 0; i < x.length; ++i) x[i] = i;

        /* record how many times a node is chosen to pass by */
        double[] stats = new double[scaledMatrix.length + 1];
        int validCount = 0;

    /*================================================================================================================*/
        int colorIndex = 0;
        double[] costsSums = null;
        for (int i = 0; i < sampleSize; ++i) {

            /* define the problem to optimise */
            problem = new TourRoutePlanningProblem(scaledMatrix, useDerived? derivedPlaces : places, source, target, invalidSolutionPenalty);

            /* crossover parameters */
            crossover = (ingredient % 2 == 0) ?
                    new TRPCrossover(crossoverProbability, 1000, scaledMatrixCopy) :
                    new PMXCrossover(crossoverProbability);

            /* mutation parameter */
            mutation = (ingredient % 3 == 0) ?
                    new TRPMutation(mutationProbability, scaledMatrixCopy) :
                    new PermutationSwapMutation<>(mutationProbability);

            /* create selection */
            selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(
                    new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

            /* pass the components to the algorithm */
//        algorithm = new NSGAIIBuilder<PermutationSolution<Integer>>(problem, crossover, mutation, 100)
//                .setSelectionOperator(selection)
//                .setMaxEvaluations(iterationAllowed)
//                .build();
            algorithm = new MeasurableNSGAII<PermutationSolution<Integer>>(
                    problem,
                    iterationAllowed,
                    populationSize, populationSize, populationSize,
                    crossover, mutation, selection);

            /* run the algorithm directly */
            long startTime = System.currentTimeMillis();
            algorithm.run();

            /* get results */
            List<PermutationSolution<Integer>> population = algorithm.getResult();
            /* add stats */
            double[] newStats = getResultStats(population, target, invalidSolutionPenalty);
            stats = sumArrays(stats, newStats);
            validCount += newStats[newStats.length - 1];
            /* get measures */
            distanceCosts = algorithm.getDistanceCosts();
            popularityCosts = algorithm.getPopularityCosts();
            attractionCosts = algorithm.getAttractionCosts();
            quarantineCosts = algorithm.getQuarantineCosts();

            costsSums = (costsSums == null) ? distanceCosts : sumArrays(costsSums, distanceCosts);

            long timeComputed = System.currentTimeMillis() - startTime;
            System.out.printf("Result pupulation is:\n %s\n", population);
            System.out.printf("Computing time: %d milliseconds\n", timeComputed);

        }
        double[] costsAvgs = avgArray(costsSums, sampleSize);
        // add a line plot to the PlotPanel
        plot.addLinePlot("distanceCosts", colors[colorIndex], x, costsAvgs);
        ++colorIndex;
    /*================================================================================================================*/
        System.out.println("number of valid solution: " + validCount);
        System.out.println(Arrays.toString(stats));
        stats = avgArray(stats, validCount);
        System.out.println(Arrays.toString(stats));

        // put the PlotPanel in a JFrame, as a JPanel
        JFrame frame = new JFrame("a plot panel");
        frame.setSize(800, 800);
        frame.setContentPane(plot);
        frame.setVisible(true);
    }

    public static double[] sumArrays(double[] arr1, double[] arr2) {
        if (arr1.length != arr2.length) return null;
        double[] result = new double[arr1.length];
        for (int i = 0; i < arr1.length; ++i) result[i] = arr1[i] + arr2[i];
        return result;
    }

    public static double[] avgArray(double[] arr, int sampleSize) {
        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; ++i) result[i] = arr[i] / sampleSize;
        return result;
    }

    public static double[] mulArray(double[] arr, double factor) {
        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; ++i) result[i] = arr[i] * factor;
        return result;
    }

    public static double[] getResultStats(List<PermutationSolution<Integer>> results, int destination, int penalty) {
        double[] resultStats = new double[results.get(0).getNumberOfVariables() + 1];
        double validCount = 0;
        for (PermutationSolution<Integer> result : results) {
            if (result.getObjective(0) >= penalty) continue;
            for (int i = 0; i < result.getNumberOfVariables(); ++i) {
                ++resultStats[result.getVariable(i)];
                if (result.getVariable(i) == destination) break;
            }
            ++validCount;
        }
        resultStats[resultStats.length - 1] = validCount;
        assert resultStats[1] == resultStats[resultStats.length-1];
        return resultStats;
    }
}
