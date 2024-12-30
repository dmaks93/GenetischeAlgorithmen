package impl;

import api.EvolutionManager;
import api.Graphics;
import entity.AminoAcid;
import entity.Protein;
import types.AcidType;
import types.Direction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Random;
import java.util.Locale;

public class EvolutionManagerImpl extends EvolutionManager {
    AcidManagerImpl acidManager = new AcidManagerImpl();
    ProteinManagerImpl proteinManager = new ProteinManagerImpl();
    FitnessManagerImpl fitnessManager = new FitnessManagerImpl();
    Protein bestEverProtein = new Protein();
    double populationAverageFitness = 0.0;
    double bestFitnessOfGeneration = 0.0;
    double bestFitnessEver = 0.0;
    int bestEverContacts = 0;
    int bestEverOverlappings = 0;
    static int prevX = 0;
    static int prevY = 0;
    ArrayList<AcidType> acidTypes = new ArrayList<>();
    int numberOfCrossovers;
    int length;
    static int gen = 1;
    private final DecimalFormat df = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.GERMANY));

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
    public void crossover(ArrayList<Protein> population) {
        Random random = new Random();
        int firstCandidateInPopulation;
        int secondCandidateInPopulation;
        int splitPosition = random.nextInt(length - 1) + 1;
        Direction oppositeDirection;
        Protein firstCandidate;
        Protein secondCandidate;

        do {
            firstCandidateInPopulation = random.nextInt(population.size());
            secondCandidateInPopulation = random.nextInt(population.size());
        } while (firstCandidateInPopulation == secondCandidateInPopulation);

        firstCandidate = population.get(firstCandidateInPopulation);
        secondCandidate = population.get(secondCandidateInPopulation);

        // Get the amino acids from each candidate
        ArrayList<AminoAcid> firstAcids = firstCandidate.getAminoAcids();
        ArrayList<AminoAcid> secondAcids = secondCandidate.getAminoAcids();

        // Perform the crossover
        ArrayList<AminoAcid> firstLeft = new ArrayList<>(firstAcids.subList(0, splitPosition));
        ArrayList<AminoAcid> firstRight = new ArrayList<>(firstAcids.subList(splitPosition, firstAcids.size()));

        ArrayList<AminoAcid> secondLeft = new ArrayList<>(secondAcids.subList(0, splitPosition));
        ArrayList<AminoAcid> secondRight = new ArrayList<>(secondAcids.subList(splitPosition, secondAcids.size()));

        // Update connecting acids directions firstLeft / secondRight
        AminoAcid firstLeftLast = firstLeft.getLast();
        AminoAcid secondRightFirst = secondRight.getFirst();

        oppositeDirection = proteinManager.getOppositeDirection(secondRightFirst.getPreviousAcidDirection());
        firstLeftLast.setNextAcidDirection(oppositeDirection);

        // Update connecting acids directions secondLeft / firstRight
        AminoAcid secondLeftLast = secondLeft.getLast();
        AminoAcid firstRightFirst = firstRight.getFirst();

        oppositeDirection = proteinManager.getOppositeDirection(firstRightFirst.getPreviousAcidDirection());
        secondLeftLast.setNextAcidDirection(oppositeDirection);

        // Swap the segments
        firstLeft.addAll(secondRight);
        secondLeft.addAll(firstRight);

        // Update coordinates here
        for (int i = 1; i < length; i++) {
            adaptCoordinates(firstLeft.get(i));
        }
        prevX = 0;
        prevY = 0;
        for (int j = 0; j < length; j++) {
            adaptCoordinates(secondLeft.get(j));
        }
        prevX = 0;
        prevY = 0;

        // Update the candidates with the new sequences
        firstCandidate.setAminoAcids(firstLeft);
        secondCandidate.setAminoAcids(secondLeft);
    }

    @Override
    public void mutate(ArrayList<Protein> population) {
        prevX = 0;
        prevY = 0;
        Random random = new Random();
        int candidateInPopulation;
        int acidInSequence;
        candidateInPopulation = random.nextInt(population.size());
        acidInSequence = random.nextInt(length - 1);
        ArrayList<AminoAcid> acids = population.get(candidateInPopulation).getAminoAcids();
        AminoAcid firstAcid = acids.get(acidInSequence);
        AminoAcid secondAcid = acids.get(acidInSequence + 1);

        Protein observer = population.get(candidateInPopulation);

        Direction mutatedDirection;
        boolean isValid = false;

        mutatedDirection = proteinManager.directionTranslator(random.nextInt(1000) + 1);

        if (acidInSequence == 0 || acidInSequence == length- 2) {
            firstAcid.setNextAcidDirection(mutatedDirection);
            secondAcid.setPreviousAcidDirection(proteinManager.getOppositeDirection(mutatedDirection));
            isValid = true;
        } else {
            isValid = proteinManager.directionValidityCheck(mutatedDirection, secondAcid.getNextAcidDirection());
            if (isValid) {
                firstAcid.setNextAcidDirection(mutatedDirection);
                secondAcid.setPreviousAcidDirection(proteinManager.getOppositeDirection(mutatedDirection));
            }
        }
        if (isValid) {
            for (int i = 1; i < length; i++) {
                adaptCoordinates(acids.get(i));
            }
        }
    }

    @Override
    public void evolution(String sequence, int populationSize, int numberOfGenerations, double crossoverRate, double mutationRate) {
        ArrayList<Protein> population = new ArrayList<>();
        ArrayList<AminoAcid> acidSequence = acidManager.createAcidSequence(sequence);
        length = sequence.length();
        numberOfCrossovers = (int) (populationSize * crossoverRate / 2);
        int numberOfMutations = (int) (populationSize * length * mutationRate);

        for (int i = 0; i < sequence.length(); i++) {
            AcidType currentType = AcidType.UNKNOWN;
            if (sequence.charAt(i) == '0')
                currentType = AcidType.BLACK;
            if (sequence.charAt(i) == '1')
                currentType = AcidType.WHITE;
            acidTypes.add(currentType);
        }

        for (int i = 0; i < numberOfGenerations - 1; i++) {
            if (i == 0) {
                for (int j = 0; j < populationSize; j++) {
                    Protein protein;
                    population.add(protein = new Protein(proteinManager.createProtein(acidSequence)));
                    protein.setGeneration(i);
                    protein.setAcidTypes(acidTypes);
                }
            }
            for (int k = 0; k < populationSize; k++) {
                fitnessManager.fitnessFunction(population.get(k), acidTypes);
            }
            if (i == 0)
                this.collectData(population, true, gen);

            population = this.fitnessProportionalSelection(population);

            // Here figure out if crossover happens or not based on probability
            for (int j = 0; j < numberOfCrossovers; j++) {
                crossover(population);
            }

            for (int k = 0; k < numberOfMutations; k++) {
                mutate(population);
            }

            // Here figure out if mutation happens or not based on probability
            // Mutation should be adjusted here if needed
            // Calculate fitness for protein here

            // Evaluation has to be added
        }
        System.out.println(bestEverProtein.getContacts());
        System.out.println(bestEverProtein.getOverlapping());
        System.out.println(bestEverProtein.getFitness());
        Graphics graphics = new Graphics(bestEverProtein, sequence);
        graphics.drawProtein();
    }

    private void adaptCoordinates(AminoAcid acid) {
        Direction prevDirection = acid.getPreviousAcidDirection();

        if (prevDirection == Direction.Up) {
            acid.setCoordinates(prevX, prevY - 1);
            prevY = prevY - 1;
        }
        if (prevDirection == Direction.Down) {
            acid.setCoordinates(prevX, prevY + 1);
            prevY = prevY + 1;
        }
        if (prevDirection == Direction.Left) {
            acid.setCoordinates(prevX + 1, prevY);
            prevX = prevX + 1;
        }
        if (prevDirection == Direction.Right) {
            acid.setCoordinates(prevX - 1, prevY);
            prevX = prevX - 1;
        }
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
            bestEverProtein = new Protein(currentFittestProtein);

            // Assuming `contacts` and `overlapping` can be retrieved from the protein:
            bestEverContacts = bestEverProtein.getContacts(); // Replace with actual method if different
            bestEverOverlappings = bestEverProtein.getOverlapping(); // Replace with actual method if different
        }
        this.logData(isFirstLine, generation, populationAverageFitness, bestFitnessOfGeneration, bestFitnessEver, bestEverContacts, bestEverOverlappings);
    }

    private void logData(boolean isFirstLine, int gen, double averageGenFit, double bestGenFit, double bestEverFit, int bestEverContacts, int bestEverOverlappings) {
        // Set the path to the "output" folder
        String outputFolder = "output";
        File outputDirectory = new File(outputFolder);

        // Check if the directory exists, and create it if it doesn't
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();  // Creates the output folder if it doesn't exist
        }

        // Create the file in the "output" folder
        String csvFile = outputFolder + File.separator + "evolution_data.csv";
        File file = new File(csvFile);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!file.exists() || isFirstLine) {
                writer.write("Generation;AvgGenFitness;BstGenFitness;BstEverFitness;BstEverContacts;BstEverOverlapping\n");
                isFirstLine = false;
            }

            writer.write(gen + ";" + df.format(averageGenFit) + ";" + df.format(bestGenFit) + ";" + df.format(bestEverFit) + ";" + bestEverContacts + ";" + bestEverOverlappings + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
