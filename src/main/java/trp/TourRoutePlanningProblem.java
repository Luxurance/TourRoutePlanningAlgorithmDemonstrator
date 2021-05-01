package trp;
import org.apache.commons.math3.util.Pair;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import java.util.List;

/* Fixed target version */
public class TourRoutePlanningProblem extends AbstractIntegerPermutationProblem {

    private int NON_NEIGHBOUR_PENALTY;
    private int INCORRECT_ENDPOINT_PENALTY;

    private int[][] costMatrix;
    private List<TourPlace> places;
    private int source;
    private int target;

    private double meanPop;
    private double sdPop;

    public TourRoutePlanningProblem(int[][] costMatrix, List<TourPlace> places, int source, int target, int penalty) {
        this(costMatrix.length);
        this.costMatrix = costMatrix;
        this.places = places;
        this.source = source;
        this.target = target;

        Pair<Double, Double> meanVarPair = this.calMeanSdPop(places);
        this.meanPop = meanVarPair.getFirst();
        this.sdPop = meanVarPair.getSecond();

        this.NON_NEIGHBOUR_PENALTY = penalty;
        this.INCORRECT_ENDPOINT_PENALTY = penalty;
    }

    private TourRoutePlanningProblem(int nodeNum) {
        setNumberOfVariables(nodeNum);
        setNumberOfObjectives(4); // evaluation
        setName("TRP");
    }

    @Override
    public int getLength() {
        return costMatrix.length;
    }

    @Override
    public void evaluate(PermutationSolution<Integer> solution) {
        long f = 0;
        int endIndex = solution.getNumberOfVariables() - 1;
        /* Check source before evaluation */
        if (!this.isSource(solution.getVariable(0))) {
            f += INCORRECT_ENDPOINT_PENALTY;
        }

        for (int i = 0; i < solution.getNumberOfVariables() - 1; i++) {
            int cost = this.costMatrix[solution.getVariable(i)][solution.getVariable(i+1)];
            /* Add cost of each pair of neighbouring nodes */
            f += (cost == Integer.MAX_VALUE)? NON_NEIGHBOUR_PENALTY : cost;
            /* Check the end of solution */
            if (solution.getVariable(i + 1) == this.target) {
                endIndex = i + 1;
                break;
            }
        }
        /* Target not match */
        if (!this.isTarget(solution.getVariable(endIndex))) {
            f += INCORRECT_ENDPOINT_PENALTY;
        }
        /* Valid solution */
        solution.setObjective(0, f); /* distance */
        double[] properties = this.evaluatePlaceProperties(solution, endIndex);
        solution.setObjective(1, -properties[0]); /* popularity */
        solution.setObjective(2, -properties[1]); /* attraction */
        solution.setObjective(3, properties[2]); /* quarantine */
    }

    /* Can be rewritten to suit new design */
    @Override
    public PermutationSolution<Integer> createSolution() {
        return super.createSolution();
    }

    /* Evaluate two objective simultaneously in one traversal */
    private double[] evaluatePlaceProperties(PermutationSolution<Integer> solution, int endIndex) {
        double[] results = new double[3];
        for (int i = 0; i < endIndex + 1; i++) {
            /* get corresponding properties */
            int nodeNum = solution.getVariable(i);
            results[0] += this.calGaussianPop(this.places.get(nodeNum).getPopularity());
            results[1] += this.places.get(nodeNum).getAttractionsCount();
            results[2] += this.places.get(nodeNum).getQuarantinePeriod();
        }
        return results;
    }

    private double calGaussianPop(double x) {
        double result = (1 / (Math.sqrt(2 * Math.PI) * this.sdPop))
                * Math.exp(-0.5 * Math.pow((x - this.meanPop) / this.sdPop, 2));
        return result;
    }

    /* Calculate pair of mean and sd */
    private Pair<Double, Double> calMeanSdPop(List<TourPlace> places) {
        double mean = 0;
        double sd = 0;
        int len = places.size();
        for (TourPlace place : places) {
            double pop = place.getPopularity();
            mean += pop;
        }
        mean = mean / len;
        for (TourPlace place : places) {
            double dev = place.getPopularity() - mean;
            sd += Math.pow(dev, 2);
        }
        sd = Math.sqrt(sd / len);
        return new Pair<>(mean, sd);
    }

    private boolean isSource(int node) {
        return node == this.source;
    }

    private boolean isTarget(int node) {
        return node == this.target;
    }
}
