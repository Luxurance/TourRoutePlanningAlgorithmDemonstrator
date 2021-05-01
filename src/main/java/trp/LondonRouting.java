package trp;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
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
import utils.CostMatrixGenerator;
import utils.LondonPlaceNumMappings;
import utils.TourPlacesGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LondonRouting {

    private int[][] originDistanceMatrix;
    private int[][] targetDistanceMatrix;
    private List<TourPlace> places;
    private final int source;
    private final int destination;
    private final int routeNum;
    private NSGAII<PermutationSolution<Integer>> algorithm;

    private final int INVALID_PENALTY = 10000;
    private final int MAX_EVALUATION  = 500000;
    private final int POPULATION_SIZE = 100;
    private final int MAX_RERUN_TIMES = 5;

    public LondonRouting(String crossover, String mutation,
                         String source, String destination, int routeNum) throws IOException {
        this.source = LondonPlaceNumMappings.placeNumMap.get(source);
        this.destination = LondonPlaceNumMappings.placeNumMap.get(destination);
        this.routeNum = routeNum;
        loadOriginDistanceMatrix();
        loadTourPlaces();
        cleanDistanceMatrix(); /* must come before algorithm configuration */
        configureAlgorithm(crossover, mutation);
    }

    private void loadTourPlaces() throws IOException {
        TourPlacesGenerator generator = new TourPlacesGenerator("src/main/resources/transformedData/tour_places.csv");
        places = generator.getTourPlaces();
        generator.close();
    }

    private void loadOriginDistanceMatrix() {
        originDistanceMatrix = CostMatrixGenerator.getLondonUndergroundGraph();
    }

    /**
     * Initialise the algorithm class by equipping its ingredients
     * @param crossover
     * @param mutation
     */
    private void configureAlgorithm(String crossover, String mutation) {
        Problem<PermutationSolution<Integer>> problem = new TourRoutePlanningProblem(
                targetDistanceMatrix, places, source, destination, INVALID_PENALTY);
        CrossoverOperator<PermutationSolution<Integer>> crossoverOperator = ("TRP".equals(crossover))?
                new TRPCrossover(0.9, 100, originDistanceMatrix) :
                new PMXCrossover(0.9);
        MutationOperator<PermutationSolution<Integer>> mutationOperator = ("TRP".equals(mutation))?
                new TRPMutation(0.9, originDistanceMatrix) :
                new PermutationSwapMutation<>(0.9);
        SelectionOperator<List<PermutationSolution<Integer>>,PermutationSolution<Integer>> selectionOperator =
                new BinaryTournamentSelection<PermutationSolution<Integer>>(
                        new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());
        algorithm = new NSGAIIBuilder<PermutationSolution<Integer>>(problem, crossoverOperator, mutationOperator, POPULATION_SIZE)
                .setSelectionOperator(selectionOperator)
                .setMaxEvaluations(MAX_EVALUATION)
                .build();
    }

    /**
     * Replace 0 with inf for algorithmic usage
     */
    private void cleanDistanceMatrix() {
        targetDistanceMatrix = originDistanceMatrix.clone();
        for (int i = 0; i < targetDistanceMatrix.length; i++) {
            for (int j = 0; j < targetDistanceMatrix[0].length; j++) {
                targetDistanceMatrix[i][j] = targetDistanceMatrix[i][j] == 0? Integer.MAX_VALUE : targetDistanceMatrix[i][j];
            }
        }
    }

    /**
     * Get the trajectories representing in text as required
     * @return routes stored in list
     */
    public List<List<String>> getTrajectories() {
        List<List<Integer>> integerResults = getIntegerValidResults();
        List<List<String>> textResults = new ArrayList<>();
        for (List<Integer> integerResult : integerResults) {
            textResults.add(integerResultToTextResult(integerResult));
        }
        return textResults;
    }

    /**
     * Convert numerical route into text one
     * @param integerResult numerical route
     * @return single result in text
     */
    private List<String> integerResultToTextResult(List<Integer> integerResult) {
        List<String> textResult = new ArrayList<>();
        for (Integer node : integerResult) {
            textResult.add(LondonPlaceNumMappings.numPlaceMap.get(node));
        }
        return textResult;
    }

    /**
     * Get numerical results in the amount as required (if possible)
     * @return lists of numerical routing result
     */
    private List<List<Integer>> getIntegerValidResults() {
        List<List<Integer>> integerResults = new ArrayList<>();
        /* re-run the algorithm if valid routes not enough */
        int count = 0;
        while (integerResults.size() < routeNum) {
            algorithm.run();
            List<PermutationSolution<Integer>> rawResults = algorithm.getResult();
            List<List<Integer>> validResults = extractValidResult(rawResults);
            for (List<Integer> validResult : validResults) {
                if (!existResult(integerResults, validResult)) integerResults.add(validResult);
            }
            /* "best effort" and prevent looping forever in corner cases */
            if (++count >= MAX_RERUN_TIMES) break;
        }
        return integerResults;
    }

    /**
     * @param existingResults container of existing results
     * @param validResult result to be checked existence
     * @return true if 2nd param already exist in 1st
     */
    private boolean existResult(List<List<Integer>> existingResults, List<Integer> validResult) {
        for (List<Integer> existingResult : existingResults) {
            if (existingResult.equals(validResult)) return true;
        }
        return false;
    }

    /**
     * @param rawResults unfiltered results
     * @return list containing only valid results
     */
    private List<List<Integer>> extractValidResult(List<PermutationSolution<Integer>> rawResults) {
        List<List<Integer>> integerResults = new ArrayList<>();
        for (PermutationSolution<Integer> rawResult : rawResults) {
            /* ignore invalid result */
            if (!isValidRoute(rawResult)) continue;
            /* store the elements in order */
            List<Integer> integerResult = new ArrayList<>();
            for (int i = 0; i < rawResult.getNumberOfVariables(); ++i) {
                integerResult.add(rawResult.getVariable(i));
                /* stop at destination */
                if (rawResult.getVariable(i) == destination) break;
            }
            integerResults.add(integerResult);
        }
        return integerResults;
    }

    /**
     * @param solution routing solution
     * @return true if route is valid
     */
    private boolean isValidRoute(PermutationSolution<Integer> solution) {
        return solution.getObjective(0) < INVALID_PENALTY;
    }
}
