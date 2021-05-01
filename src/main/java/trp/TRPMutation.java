package trp;

import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class TRPMutation extends PermutationSwapMutation<Integer> {
    /** super class does not allow direct inheritance */
    protected double mutationProbability;
    protected RandomGenerator<Double> mutationRandomGenerator;
    protected BoundedRandomGenerator<Integer> positionRandomGenerator;
    protected int[][] graph;

    private static final int MAX_ITER = 100;

    /** Constructor */
    public TRPMutation(double mutationProbability, int[][] graph) {
        this(mutationProbability,
             () -> JMetalRandom.getInstance().nextDouble(),
             (a, b) -> JMetalRandom.getInstance().nextInt(a, b),
             graph);
    }

    /** Constructor */
    public TRPMutation(double mutationProbability, RandomGenerator<Double> randomGenerator, int[][] graph) {
        this(mutationProbability,
             randomGenerator,
             BoundedRandomGenerator.fromDoubleToInteger(randomGenerator),
             graph);
    }

    /** Constructor */
    public TRPMutation(
            double mutationProbability,
            RandomGenerator<Double> mutationRandomGenerator,
            BoundedRandomGenerator<Integer> positionRandomGenerator,
            int[][] graph) {
        super(mutationProbability, mutationRandomGenerator, positionRandomGenerator);
        Check.probabilityIsValid(mutationProbability);
        this.mutationProbability = mutationProbability;
        this.mutationRandomGenerator = mutationRandomGenerator;
        this.positionRandomGenerator = positionRandomGenerator;
        this.graph = graph;
    }

    /**
     * This version of mutation generates a random position in the solution, it then try finding an alternative subpath
     * to reach the next next node, substituting the obtained new node with the replaced node
     * @param solution the solution to perform mutation
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doMutation(PermutationSolution solution) {
        int permutationLength = solution.getNumberOfVariables();
        /* not performing mutation */
        if (this.mutationRandomGenerator.getRandomValue() >= this.mutationProbability) {
            return;
        }
        /* find desire pos and nextPos */
        int pos = -1;
        int nextPos;
        while ((nextPos = this.getDesiredRepPos(solution, pos)) == -1)
            pos = this.positionRandomGenerator.getRandomValue(0, permutationLength);
        /* swap previous next pos and desired nextPos */
        int tmp = (int) solution.getVariable(pos + 1);
        solution.setVariable(pos + 1, solution.getVariable(nextPos));
        solution.setVariable(nextPos, tmp);
    }

    /**
     * Get a preferable position to be replaced given the current replacing position
     * (result in valid neighbour in routing solution)
     * @param solution
     * @param pos
     * @return -1 if invalid, the index to substitute if exists
     */
    private int getDesiredRepPos(PermutationSolution<Integer> solution, int pos) {
        if (pos < 0) return -1;
        if (pos >= solution.getNumberOfVariables() - 2) return -1;

        int nextPos = pos + 1;
        int nextNextPos = pos + 2;

        int graphSize = graph.length;
        int index = this.positionRandomGenerator.getRandomValue(0, graphSize);
        int counter = 0;
        /* fetch the alternative index accessible to the nextNextPos */
        for ( ; index >= graphSize || index == nextPos || index == pos || graph[pos][index] == 0 || graph[index][nextNextPos] == 0;
                index = this.positionRandomGenerator.getRandomValue(0, graphSize), ++counter) {
            /* computational threshold reached */
            if (counter >= MAX_ITER) {
                index = -1;
                break;
            }
        }

        return index;
    }
}
