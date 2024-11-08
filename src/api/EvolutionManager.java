package api;

import entity.Protein;

import java.util.ArrayList;

public abstract class EvolutionManager {

    public abstract ArrayList<Protein> fitnessProportionalSelection(ArrayList<Protein> population);

    public abstract void evolution(String sequence, int populationSize, int numberOfGenerations);
}
