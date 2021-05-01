package trp;

import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class TRPCrossover extends PMXCrossover {

    /* set as private in super class and not accessible */
    private final BoundedRandomGenerator<Integer> cuttingPointRandomGenerator;
    private final RandomGenerator<Double> crossoverRandomGenerator;
    private final int cuttingPointTrials;
    private final int[][] graph;

    public TRPCrossover(double crossoverProbability, int cuttingPointTrials, int[][] graph) {
        this(crossoverProbability, cuttingPointTrials, graph,
                () -> JMetalRandom.getInstance().nextDouble(),
                (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
    }

    public TRPCrossover(double crossoverProbability, int cuttingPointTrials, int[][] graph,
                        RandomGenerator<Double> crossoverRandomGenerator, BoundedRandomGenerator<Integer> cuttingPointRandomGenerator) {
        super(crossoverProbability, crossoverRandomGenerator, cuttingPointRandomGenerator);
        this.crossoverRandomGenerator = crossoverRandomGenerator;
        this.cuttingPointRandomGenerator = cuttingPointRandomGenerator;
        this.cuttingPointTrials = cuttingPointTrials;
        this.graph = graph;
    }

    /**
     * Derived from: https://jmetal-doc.readthedocs.io/en/latest/org/uma/jmetal/operator/impl/crossover/PMXCrossover.html
     * Extend the original function to provide guidance for TRP convergence
     * */
    @Override
    public List<PermutationSolution<Integer>> doCrossover(double probability, List<PermutationSolution<Integer>> parents) {
        List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);

        offspring.add((PermutationSolution<Integer>) parents.get(0).copy()) ;
        offspring.add((PermutationSolution<Integer>) parents.get(1).copy()) ;

        int permutationLength = parents.get(0).getNumberOfVariables() ;

        if (crossoverRandomGenerator.getRandomValue() < probability) {
            int cuttingPoint1 = 0;
            int cuttingPoint2 = 0;

            // STEP 1: Get two cutting points
            while (checkValidCuttingPoint(parents, cuttingPoint1))
                cuttingPoint1 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
            while (checkValidCuttingPoint(parents, cuttingPoint2) || cuttingPoint2 == cuttingPoint1)
                cuttingPoint2 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);

            if (cuttingPoint1 > cuttingPoint2) {
                int swap;
                swap = cuttingPoint1;
                cuttingPoint1 = cuttingPoint2;
                cuttingPoint2 = swap;
            }

            // STEP 2: Get the subchains to interchange
            int replacement1[] = new int[permutationLength];
            int replacement2[] = new int[permutationLength];
            for (int i = 0; i < permutationLength; i++)
                replacement1[i] = replacement2[i] = -1;

            // STEP 3: Interchange
            for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
                offspring.get(0).setVariable(i, parents.get(1).getVariable(i));
                offspring.get(1).setVariable(i, parents.get(0).getVariable(i));

                replacement1[parents.get(1).getVariable(i)] = parents.get(0).getVariable(i) ;
                replacement2[parents.get(0).getVariable(i)] = parents.get(1).getVariable(i) ;
            }

            // STEP 4: Repair offspring
            for (int i = 0; i < permutationLength; i++) {
                if ((i >= cuttingPoint1) && (i <= cuttingPoint2))
                    continue;

                int n1 = parents.get(0).getVariable(i);
                int m1 = replacement1[n1];

                int n2 = parents.get(1).getVariable(i);
                int m2 = replacement2[n2];

                while (m1 != -1) {
                    n1 = m1;
                    m1 = replacement1[m1];
                }

                while (m2 != -1) {
                    n2 = m2;
                    m2 = replacement2[m2];
                }

                offspring.get(0).setVariable(i, n1);
                offspring.get(1).setVariable(i, n2);
            }
        }

        return offspring;
    }

    /**
     * Check if the cutting point is one desired (results in valid pair in the route)
     * @param parents list of two parent solutions
     * @param cuttingPoint an index
     * @return NOT VALID: true, VALID: false
     */
    private boolean checkValidCuttingPoint(List<PermutationSolution<Integer>> parents, int cuttingPoint) {
        /* check for bound */
        if (cuttingPoint == 0) return true;
        /* cutting point position is accessible to its neighbouring point in graph */
        return (this.graph[parents.get(0).getVariable(cuttingPoint - 1)][parents.get(1).getVariable(cuttingPoint)] == 0) ||
               (this.graph[parents.get(1).getVariable(cuttingPoint - 1)][parents.get(0).getVariable(cuttingPoint)] == 0);
    }

    /**
     * Get the element to be swapped given the two cutting point, to preserve uniqueness
     * @param local
     * @param remote
     * @param cuttingPoint1
     * @param cuttingPoint2
     * @return an array of two swapping position
     */
    @Deprecated
    private int[] getIndicesToSwap(PermutationSolution<Integer> local, PermutationSolution<Integer> remote,
                                   int cuttingPoint1, int cuttingPoint2) {
        int[] indices = new int[cuttingPoint2 - cuttingPoint1 + 1];
        for (int pos = cuttingPoint1, i = 0; pos < cuttingPoint2; ++pos, ++i) {
            indices[i] = getSolutionIndex(local, remote.getVariable(pos));
        }
        return indices;
    }

    /**
     * Get the index of a element in the routing solution
     * @param solution
     * @param node the node value sought
     * @return the index of the sought value, not found: -1
     */
    private int getSolutionIndex(PermutationSolution<Integer> solution, int node) {
        for (int i = 0; i < solution.getNumberOfVariables(); ++i) {
            if (node == (int) solution.getVariable(i)) return i;
        }
        return -1;
    }
}
