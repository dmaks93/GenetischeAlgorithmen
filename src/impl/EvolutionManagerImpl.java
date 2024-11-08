package impl;

import api.EvolutionManager;
import api.Graphics;
import entity.AminoAcid;
import entity.Protein;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class EvolutionManagerImpl extends EvolutionManager {
    AcidManagerImpl acidManager = new AcidManagerImpl();
    ProteinManagerImpl proteinManager = new ProteinManagerImpl();
    FitnessManagerImpl fitnessManager = new FitnessManagerImpl();
    Graphics graphics = new Graphics();
    Protein fittestProtein;
    double populationAverageFitness = 0.0;
    double bestFitnessOfGeneration = 0.0;
    double bestFitnessEver = 0.0;
    int contacts = 0;
    int overlapping = 0;
    static int gen = 1;

    @Override
    public ArrayList<Protein> fitnessProportionalSelection(ArrayList<Protein> population) {
        gen++;
        ArrayList<Protein> selectedMembers = new ArrayList<>();
        double[] rouletteWheel = new double[population.size()];
        double accumulatedFitnessValue = 0.0;

        Random random = new Random();
        double randomFitnessValue;

        for (int i = 0; i < population.size(); i++) {
            accumulatedFitnessValue += population.get(i).getFitness();
            rouletteWheel[i] = accumulatedFitnessValue;
        }

        for (int i = 0; i < population.size(); i++) {
            randomFitnessValue = random.nextDouble() * accumulatedFitnessValue;

            for (int j = 0; j < rouletteWheel.length; j++) {
                if (randomFitnessValue <= rouletteWheel[j]) {
                    Protein protein = new Protein(population.get(j));
                    selectedMembers.add(protein);
                    break;
                }
            }
        }
        population.clear();
        population = null;
        this.collectData(selectedMembers, false, gen);
        return selectedMembers;
    }

    @Override
    public void evolution(String sequence, int populationSize, int numberOfGenerations) {
        ArrayList<Protein> population = new ArrayList<>();
        ArrayList<AminoAcid> acidSequence = acidManager.createAcidSequence(sequence);

        for (int i = 1; i < numberOfGenerations; i++) {
            if (i == 1) {
                for (int j = 0; j < populationSize; j++ ) {
                    population.add(new Protein(proteinManager.createProtein(acidSequence)));
                }
            }
            for (int k = 0; k < populationSize; k++) {
                fitnessManager.fitnessFunction(population.get(k));
            }
            if (i == 1)
                this.collectData(population, true, gen);

            population = this.fitnessProportionalSelection(population);
        }
        System.out.println(fittestProtein.getContacts());
        System.out.println(fittestProtein.getOverlapping());
        System.out.println(fittestProtein.getFitness());
        graphics.visualizeProtein(fittestProtein, "./protein_visualization.png");

    }

    private void collectData(ArrayList<Protein> population, boolean isFirstLine, int generation) {
        double accumulatedFitness = 0.0;
        double currentBestFitness = 0.0;
        Protein currentFittestProtein = new Protein();

        // Calculate total fitness and find the best fitness in the generation
        for (Protein protein : population) {
            double fitness = protein.getFitness();
            accumulatedFitness += fitness;

            if (fitness > currentBestFitness) {
                currentBestFitness = fitness;
                currentFittestProtein = new Protein(protein);
            }
        }

        // Calculate the average fitness for the generation
        populationAverageFitness = accumulatedFitness / population.size();

        // Update the best fitness of this generation
        bestFitnessOfGeneration = currentBestFitness;

        // Check if this is the best fitness ever
        if (currentBestFitness > bestFitnessEver) {
            bestFitnessEver = currentBestFitness;
            fittestProtein = new Protein(currentFittestProtein);

            // Assuming `contacts` and `overlapping` can be retrieved from the protein:
            contacts = currentFittestProtein.getContacts(); // Replace with actual method if different
            overlapping = currentFittestProtein.getOverlapping(); // Replace with actual method if different
        }
        this.logData(isFirstLine, generation, populationAverageFitness, bestFitnessOfGeneration, bestFitnessEver, contacts, overlapping);
    }

    private void logData(boolean isFirstLine, int gen, double averageGenFit, double bestGenFit, double bestEverFit, int mostEverCont, int mostEverOver) {
        String csvFile = "evolution_data.csv"; // File name
        File file = new File(csvFile);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!file.exists() || isFirstLine) {
                writer.write("Generation,AvgGenFitness,BstGenFitness,BstEverFitness,Contacts,Overlapping\n");
                isFirstLine = false;
            }

            writer.write(gen + "," + averageGenFit + "," + bestGenFit + "," + bestEverFit + "," + mostEverCont + "," + mostEverOver + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
