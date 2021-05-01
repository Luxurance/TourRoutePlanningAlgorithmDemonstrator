package trp;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.Comparator;
import java.util.List;

public class MeasurableNSGAII<S extends Solution<?>> extends NSGAII {

    /* arrays recording measure along the optimisation process */
    private double[]   distanceCosts;
    private double[] popularityCosts;
    private double[] attractionCosts;
    private double[] quarantineCosts;

    public MeasurableNSGAII(Problem problem, int maxEvaluations, int populationSize, int matingPoolSize, int offspringPopulationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator) {
        this(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, new DominanceComparator(), new SequentialSolutionListEvaluator());
    }

    public MeasurableNSGAII(Problem problem, int maxEvaluations, int populationSize, int matingPoolSize, int offspringPopulationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator, Comparator dominanceComparator, SolutionListEvaluator evaluator) {
        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator);
        this.distanceCosts = new double[maxEvaluations/populationSize - 1];
        this.popularityCosts = new double[maxEvaluations/populationSize - 1];
        this.attractionCosts = new double[maxEvaluations/populationSize - 1];
        this.quarantineCosts = new double[maxEvaluations/populationSize - 1];
    }

    /**
     * Derived from: https://jmetal-doc.readthedocs.io/en/latest/org/uma/jmetal/algorithm/impl/AbstractEvolutionaryAlgorithm.html
     * retaining the functionality from parents but adding in measure gathering procedure
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        List<S> offspringPopulation;
        List<S> matingPopulation;

        population = createInitialPopulation();
        population = evaluatePopulation(population);
        initProgress();
        int iterations = 0;
        while (!isStoppingConditionReached()) {
            /* store objective values in the arrays */
            distanceCosts[iterations] = getAvgFitness(population, 0);
            popularityCosts[iterations] = getAvgFitness(population, 1);
            attractionCosts[iterations] = getAvgFitness(population, 2);
            quarantineCosts[iterations] = getAvgFitness(population, 3);
            ++iterations;

            matingPopulation = selection(population);
            offspringPopulation = reproduction(matingPopulation);
            offspringPopulation = evaluatePopulation(offspringPopulation);
            population = replacement(population, offspringPopulation);
            updateProgress();
        }
    }

    /**
     * Get the average value of a particular fitness function
     * @param population the current GA population
     * @param fitnessIndex the index of fitness function required
     * @return the average fitness score
     */
    private double getAvgFitness(List<S> population, int fitnessIndex) {
        double sum = 0;
        for (S s : population) {
            sum += s.getObjective(fitnessIndex);
        }
        return sum / population.size();
    }

    public double[] getDistanceCosts() {
        return distanceCosts;
    }

    public double[] getPopularityCosts() {
        return popularityCosts;
    }

    public double[] getAttractionCosts() {
        return attractionCosts;
    }

    public double[] getQuarantineCosts() {
        return quarantineCosts;
    }

}
