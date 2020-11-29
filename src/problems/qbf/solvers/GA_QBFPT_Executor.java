package problems.qbf.solvers;

import models.Experiment;
import solutions.Solution;

import java.io.FileWriter;
import java.io.IOException;

public class GA_QBFPT_Executor {

    public static void main(String[] args) throws IOException {

        // Params
        String[] instances = {"qbf020", "qbf040", "qbf060", "qbf080", "qbf100", "qbf200", "qbf400"};

        // Experiments
        Experiment[] experiments = {
        		// latinHypercube
                new Experiment("latinHypercube_100_1", 1000, 100, 1.0/100, true, false, 0.0),
                new Experiment("latinHypercube_200_2", 1000, 200, 2.0/100, true, false, 0.0),
                new Experiment("latinHypercube_100_2", 1000, 100, 2.0/100, true, false, 0.0),
                
                // Padrao
                new Experiment("padrao_100_1", 1000, 100, 1.0/100, false, false, 0.0),
                new Experiment("padrao_200_2", 1000, 200, 2.0/100, false, false, 0.0),
                new Experiment("padrao_100_2", 1000, 100, 2.0/100, false, false, 0.0),
                
                // uniformCrossover
                new Experiment("uniformCrossover_100_1_0.25", 1000, 100, 1.0/100, false, true, 0.25),
                new Experiment("uniformCrossover_200_2_0.25", 1000, 200, 2.0/100, false, true, 0.25),
                new Experiment("uniformCrossover_100_1_0.50", 1000, 100, 1.0/100, false, true, 0.50),
                new Experiment("uniformCrossover_200_2_0.50", 1000, 200, 2.0/100, false, true, 0.50),
                
        };


        for (String instance : instances) {
            FileWriter fileWriter = new FileWriter("results/" + instance + ".txt");
            for (Experiment experiment: experiments) {
                try {
                    String expName = experiment.getKey();
                    System.out.println("\n\nINSTANCE:" + instance + "\tRUNNING EXPERIMENT: " + expName + "\n");

                    GA_QBFPT gaModel = new GA_QBFPT(1000, experiment.getPopSize(), experiment.getMutationRate(), 
                    		"instances/" + instance, experiment.isLatinHypercube(), experiment.isUniformCrossover(),
                    		experiment.getP());
                    executeInstance(expName, gaModel, fileWriter);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error reading instance or writing in file: "+instance);
                }
            }
            fileWriter.close();
        }
    }

    public static void executeInstance(String title, GA_QBFPT ga, FileWriter fileWriter) {
        long startTime = System.currentTimeMillis();
        Solution<Integer> bestSol = ga.solve();
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double time = (double)totalTime/(double)1000;

        System.out.println("Best Val = " + bestSol);
        System.out.println("Time = "+ time + " seg");

        if(fileWriter != null) {
            try {
                fileWriter.append(title + "\n");
                fileWriter.append("Best solution: " + bestSol + "\n");
                fileWriter.append("Time: " + time + "seg \n\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error writing in file: "+title);
            }
        }
    }
}