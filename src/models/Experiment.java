package models;

import java.io.IOException;


public class Experiment {
	private Integer generations;
	private Integer popSize;
	private Double mutationRate;
	private boolean latinHypercube;
	private boolean uniformCrossover;
	private String key;
	private Double p;
	
	public Experiment(String key, Integer generations, Integer popSize, Double mutationRate, boolean latinHypercube,
			boolean uniformCrossover, Double p) {
		super();
		this.generations = generations;
		this.popSize = popSize;
		this.mutationRate = mutationRate;
		this.latinHypercube = latinHypercube;
		this.uniformCrossover = uniformCrossover;
		this.key = key;
		this.p = p;
	}
	public Double getP() {
		return p;
	}
	public void setP(Double p) {
		this.p = p;
	}
	public Integer getGenerations() {
		return generations;
	}
	public void setGenerations(Integer generations) {
		this.generations = generations;
	}
	public Integer getPopSize() {
		return popSize;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setPopSize(Integer popSize) {
		this.popSize = popSize;
	}
	public Double getMutationRate() {
		return mutationRate;
	}
	public void setMutationRate(Double mutationRate) {
		this.mutationRate = mutationRate;
	}
	public boolean isLatinHypercube() {
		return latinHypercube;
	}
	public void setLatinHypercube(boolean latinHypercube) {
		this.latinHypercube = latinHypercube;
	}
	public boolean isUniformCrossover() {
		return uniformCrossover;
	}
	public void setUniformCrossover(boolean uniformCrossover) {
		this.uniformCrossover = uniformCrossover;
	}
	
	
}
