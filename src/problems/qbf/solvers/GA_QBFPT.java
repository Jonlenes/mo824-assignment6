package problems.qbf.solvers;

import solutions.Solution;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class GA_QBFPT extends GA_QBF {

    private final Integer triples[][];

    private final Integer pi1_g = 131;
    private final Integer pi2_g = 1031;
    private final Integer pi1_h = 193;
    private final Integer pi2_h = 1093;
    private boolean latinHypercube = false;
    private boolean uniformCrossover = false;
    private double p = 0;

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
    public GA_QBFPT(Integer generations, Integer popSize, Double mutationRate, String filename, 
    		boolean latinHypercube, boolean uniformCrossover, Double p) throws IOException {
        super(generations, popSize, mutationRate, filename);
        this.latinHypercube = latinHypercube;
        this.uniformCrossover = uniformCrossover;
        this.p = p;
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
    public boolean isProhibited(Chromosome candidate, boolean verbose){
        for(Integer[] triple : triples){
            int element1 = candidate.get(triple[0]);
            int element2 = candidate.get(triple[1]);
            int element3 = candidate.get(triple[2]);
            if(element1 == 1 && element2 == 1 && element3 == 1){
                if(verbose) {
                    System.out.println("Prohibited! " + decode(candidate));
                    System.out.println(triple[0].toString() + ", " + triple[1].toString() + ", " + triple[2].toString() + ", ");
                }
                return true;
            }
        }
        return false;
    }

    public Solution<Integer> solve() {

    	/* Limitar o tempo em aproximadamente 30 min*/
		Integer timeoutSeconds = 1800;
		Timer timer = new Timer();
		
		class RemindTask extends TimerTask{
			private Boolean timeout = false;
			
			public Boolean timeout() {
				return this.timeout;
			}
			
			public void run() {
	            timer.cancel();
	            timeout = true;
	        }
		};
		
		RemindTask task = new RemindTask();
		// This function use milliseconds
		timer.schedule(task, timeoutSeconds*1000);
		
    	
    	
    	/* starts the initial population */
        // populacao inicial nao contem triplas proibidas
        Population population = initializePopulation();

        bestChromosome = getBestChromosome(population);
        bestSol = decode(bestChromosome);
        System.out.println("(Gen. " + 0 + ") BestSol = " + bestSol);
        /*
         * enters the main loop and repeats until a given number of generations
         */
        for (int g = 1; g <= generations; g++) {

            Population parents = selectParents(population);
            Population offsprings = null;

            if (uniformCrossover) {
                offsprings = uniform_crossover(parents, p);
            } else {
                offsprings = crossover(parents);
            }

            Population mutants = mutate(offsprings);

            Population newpopulation = selectPopulation(mutants);

            population = newpopulation;

            bestChromosome = getBestChromosome(population);


            if (fitness(bestChromosome) > bestSol.cost) {
                bestSol = decode(bestChromosome);
                if (verbose)
                    System.out.println("(Gen. " + g + ") BestSol = " + bestSol);

                if(isProhibited(bestChromosome, true)){
                    System.out.println("ERROR! Bestsol is prohibited! " + bestSol);
                }
            }
            
			//Verifica se deu o timeout
			if(task.timeout()) {
				System.out.println("Timeout");
				break;
			}

        }

        return bestSol;
    }

    @Override
    protected Chromosome generateRandomChromosome() {
        Chromosome chromosome = new Chromosome();
         {
        	for (int i = 0; i < chromosomeSize; i++) {
        		if (latinHypercube)
        			chromosome.add(rng.nextInt(popSize) % 2);
        		else
        			chromosome.add(rng.nextInt(2));
            }
            if(isProhibited(chromosome, false)){
              chromosome = repairProhibitedChromosome(chromosome);
            }
        }
        
        return chromosome;
    }

    public Chromosome repairProhibitedChromosome(Chromosome candidate){
        do{
            for(Integer[] triple : triples){
                int element1 = candidate.get(triple[0]);
                int element2 = candidate.get(triple[1]);
                int element3 = candidate.get(triple[2]);
                if(element1 == 1 && element2 == 1 && element3 == 1){
                    int element = rng.nextInt(3);
                    mutateGene(candidate, triple[element]);
                    break;
                }
            }
        }while(isProhibited(candidate, false));
        return candidate;
    }

    @Override
    protected Population crossover(Population parents) {

        Population offsprings = new Population();

        for (int i = 0; i < popSize; i = i + 2) {

            Chromosome parent1 = parents.get(i);
            Chromosome parent2 = parents.get(i + 1);

            int crosspoint1 = rng.nextInt(chromosomeSize + 1);
            int crosspoint2 = crosspoint1 + rng.nextInt((chromosomeSize + 1) - crosspoint1);

            Chromosome offspring1 = new Chromosome();
            Chromosome offspring2 = new Chromosome();

            for (int j = 0; j < chromosomeSize; j++) {
                if (j >= crosspoint1 && j < crosspoint2) {
                    offspring1.add(parent2.get(j));
                    offspring2.add(parent1.get(j));
                } else {
                    offspring1.add(parent1.get(j));
                    offspring2.add(parent2.get(j));
                }
            }
            if(!(isProhibited(offspring1, false))){
                offsprings.add(offspring1);
            }
            else{
                offsprings.add(parent1);
            }
            if(!(isProhibited(offspring2, false))){
                offsprings.add(offspring2);
            }
            else{
                offsprings.add(parent2);
            }
        }

        return offsprings;

    }

    protected Population uniform_crossover(Population parents, double p) {
        Population offsprings = new Population();

        for (int i = 0; i < popSize; i = i + 2) {
            Chromosome parent1 = parents.get(i);
            Chromosome parent2 = parents.get(i + 1);

            /* Cria máscara de booleanos para o uniform crossover com base em p */
            ArrayList<Boolean> mask = new ArrayList<Boolean>(chromosomeSize);
            for (int j = 0; j < chromosomeSize; j++) {
                boolean r = rng.nextDouble() < p;
                mask.add(r);
            }

            Chromosome offspring1 = new Chromosome();
            Chromosome offspring2 = new Chromosome();

            /* Faz o crossover com base na máscara */
            for (int j = 0; j < chromosomeSize; j++) {
                if (mask.get(j)) {
                    offspring1.add(parent2.get(j));
                    offspring2.add(parent1.get(j));
                } else {
                    offspring1.add(parent1.get(j));
                    offspring2.add(parent2.get(j));
                }
            }

            if (!(isProhibited(offspring1, false))) {
                offsprings.add(offspring1);
            } else {
                offsprings.add(parent1);
            }

            if (!(isProhibited(offspring2, false))) {
                offsprings.add(offspring2);
            } else {
                offsprings.add(parent2);
            }
        }

        return offsprings;
    }

    @Override
    protected Population mutate(Population offsprings) {

        for (Chromosome c : offsprings) {
            for (int locus = 0; locus < chromosomeSize; locus++) {
                if (rng.nextDouble() < mutationRate) {
                    //deep copy
                    Chromosome temp = (Chromosome) c.clone();
                    mutateGene(temp, locus);
                    // if the mutation does not result in a prohibited triple
                    // commit the mutation to the original chromosome
                    if(!(isProhibited(temp, false))){
                        mutateGene(c, locus);
                    }
                }
            }
        }

        return offsprings;
    }

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GA_QBFPT ga = new GA_QBFPT(1000, 100, 1.0 / 100.0, "instances/qbf060", true, false, 0.0);
        Solution<Integer> bestSol = ga.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");

    }
}
