package api;

import entity.Protein;

import java.util.ArrayList;

public abstract class EvolutionManager {

    public abstract ArrayList<Protein> fitnessProportionalSelection(ArrayList<Protein> population);

    public abstract void crossover(ArrayList<Protein> population, int length);

    public abstract void mutate();

    public abstract void evolution(String sequence, int populationSize, int numberOfGenerations);
}
