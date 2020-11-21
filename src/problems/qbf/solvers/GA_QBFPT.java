package problems.qbf.solvers;

import solutions.Solution;

import java.io.IOException;

import java.util.Arrays;

public class GA_QBFPT extends GA_QBF {

    private final Integer triples[][];

    private final Integer pi1_g = 131;
    private final Integer pi2_g = 1031;
    private final Integer pi1_h = 193;
    private final Integer pi2_h = 1093;

    private Integer l(Integer u, Integer pi1, Integer pi2) {
        return 1 + ((pi1 * (u - 1) + pi2) % ObjFunction.getDomainSize());
    }

    private Integer g(Integer u, Integer l_u) {
        if (l_u != u) {
            return l_u;
        } else {
            return 1 + (l_u % ObjFunction.getDomainSize());
        }
    }

    private Integer h(Integer u, Integer l_u, Integer g_u) {
        int size = ObjFunction.getDomainSize();
        if (l_u != u && l_u != g_u) {
            return l_u;
        } else if ((1 + (l_u % size)) != u && (1 + (l_u % size)) != g_u) {
            return 1 + (l_u % size);
        } else {
            return 1 + ((l_u + 1) % size);
        }
    }
    /**
     * Constructor for the GA_QBF class. The QBF objective function is passed as
     * argument for the superclass constructor.
     *
     * @param generations  Maximum number of generations.
     * @param popSize      Size of the population.
     * @param mutationRate The mutation rate.
     * @param filename     Name of the file for which the objective function parameters
     *                     should be read.
     * @throws IOException Necessary for I/O operations.
     */
    public GA_QBFPT(Integer generations, Integer popSize, Double mutationRate, String filename) throws IOException {
        super(generations, popSize, mutationRate, filename);

        Integer l_u_g, l_u_h, g_u, h_u;
        int size = ObjFunction.getDomainSize();
        triples = new Integer[size][3];

        for (int u = 1; u <= size; u++) {
            l_u_g = l(u,pi1_g,pi2_g);
            l_u_h = l(u,pi1_h,pi2_h);
            g_u = g(u,l_u_g);
            h_u = h(u,l_u_h,g_u);
            triples[u-1][0] = u - 1;
            triples[u-1][1] = g_u - 1;
            triples[u-1][2] = h_u - 1;
            Arrays.sort(triples[u-1]);
        }

    }

    //Checa se o cromossomo forma uma tripla proibida
    public boolean isProhibited(Chromosome candidate){
        for(Integer[] triple : triples){
            int element1 = candidate.get(triple[0]);
            int element2 = candidate.get(triple[1]);
            int element3 = candidate.get(triple[2]);
            if(element1 == 1 && element2 == 1 && element3 == 1){
                //System.out.println("Prohibited!");
                //System.out.println(triple[0].toString() + ", " +triple[1].toString() + ", " + triple[2].toString() + ", ");
                return true;
            }
        }
        return false;
    }

    @Override
    public Solution<Integer> solve() {

        /* starts the initial population */
        Population population = initializePopulation();

        bestChromosome = getBestChromosome(population);
        bestSol = decode(bestChromosome);
        System.out.println("(Gen. " + 0 + ") BestSol = " + bestSol);
        /*
         * enters the main loop and repeats until a given number of generations
         */
        for (int g = 1; g <= generations; g++) {

            Population parents = selectParents(population);

            Population offsprings = crossover(parents);

            Population mutants = mutate(offsprings);

            Population newpopulation = selectPopulation(mutants);

            population = newpopulation;

            bestChromosome = getBestChromosome(population);

            if (fitness(bestChromosome) > bestSol.cost) {
                bestSol = decode(bestChromosome);
                if (verbose)
                    System.out.println("(Gen. " + g + ") BestSol = " + bestSol);
            }

        }

        return bestSol;
    }

    @Override
    protected Chromosome generateRandomChromosome() {
        Chromosome chromosome;
        do {
            chromosome = new Chromosome();
            for (int i = 0; i < chromosomeSize; i++) {
                chromosome.add(rng.nextInt(2));
            }

        } while (isProhibited(chromosome));

        return chromosome;
    }

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GA_QBFPT ga = new GA_QBFPT(1000, 100, 1.0 / 100.0, "instances/qbf020");
        Solution<Integer> bestSol = ga.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

    }
}
