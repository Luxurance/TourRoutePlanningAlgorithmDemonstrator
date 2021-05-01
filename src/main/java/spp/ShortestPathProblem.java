package spp;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class ShortestPathProblem extends AbstractIntegerPermutationProblem {

    private int[][] costMatrix;
    private int source;
    private int target;

    public ShortestPathProblem(int[][] costMatrix, int source, int target) {
        this(costMatrix.length);
        this.costMatrix = costMatrix;
        this.source = source;
        this.target = target;
    }

    private ShortestPathProblem(int nodeNum) {
        setNumberOfVariables(nodeNum);
        setNumberOfObjectives(1);
        setName("SPP");

    }

    @Override
    public int getLength() {
        return costMatrix.length;
    }

    @Override
    public void evaluate(PermutationSolution<Integer> solution) {
        int f = 0;
        int endIndex = solution.getNumberOfVariables() - 1;

        /* Check source before evaluation */
        if (solution.getVariable(0) != this.source) {
            solution.setObjective(0, Integer.MAX_VALUE);
            return;
        }

        for (int i = 0; i < solution.getNumberOfVariables() - 1; i++) {
            int cost = this.costMatrix[solution.getVariable(i)][solution.getVariable(i+1)];

            /* Solution contains unreachable path */
            if (cost == Integer.MAX_VALUE) {
                /* End of solution */
                if (solution.getVariable(i) == this.target){
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
        if (solution.getVariable(endIndex) != this.target) {
            solution.setObjective(0, Integer.MAX_VALUE);
        } else {
            /* Flip the sign to maximise */
            solution.setObjective(0, f);
        }
    }
}
