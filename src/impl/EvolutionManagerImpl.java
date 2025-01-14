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
    Protein currentBestCandidate;
    double popAvgFitness = 0.0;
    double prevPopAvgFitness = 0.0;
    double bestFitnessOfGeneration = 0.0;
    double bestFitnessEver = 0.0;
    int bestEverContacts = 0;
    int bestEverOverlappings = 0;
    static int prevX = 0;
    static int prevY = 0;
    ArrayList<AcidType> acidTypes = new ArrayList<>();
    int numberOfCrossovers;
    int numberOfMutations;
    double mutationRate;
    int length;
    int popSize;
    int gen = 1;
    private final DecimalFormat df = new DecimalFormat("#,##0.000000", DecimalFormatSymbols.getInstance(Locale.GERMANY));

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
    public ArrayList<Protein> tournamentSelection(ArrayList<Protein> population) {
        Random random = new Random();
        gen++;
        ArrayList<Protein> selectedMembers = new ArrayList<>();
        int winProbability = 85;
        selectedMembers.add(new Protein(currentBestCandidate)); // Make sure that current best of the generation is selected
        Protein firstCandidate;
        Protein secondCandidate;
        Protein winner = null;
        Protein loser = null;
        int t;

        for (int i = 1; i < population.size(); i++) {
            firstCandidate = population.remove(random.nextInt(population.size()));
            secondCandidate = population.remove(random.nextInt(population.size()));
            t = random.nextInt(101);

            if (firstCandidate.getFitness() >= secondCandidate.getFitness()) {
                winner = firstCandidate;
                loser = secondCandidate;
            } else if (firstCandidate.getFitness() < secondCandidate.getFitness()) {
                winner = secondCandidate;
                loser = firstCandidate;
            }

            if (winProbability >= t) {
                selectedMembers.add(new Protein(winner));
            } else {
                selectedMembers.add(new Protein(loser));
            }
            population.add(firstCandidate);
            population.add(secondCandidate);
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
    public void evolution(String sequence, int populationSize, int numberOfGenerations, double cRate, double mRate, boolean ts) {
        ArrayList<Protein> population = new ArrayList<>();
        ArrayList<AminoAcid> acidSequence = acidManager.createAcidSequence(sequence);
        length = sequence.length();
        popSize = populationSize;
        numberOfCrossovers = (int) (populationSize * cRate / 2);
        mutationRate = mRate;
        numberOfMutations = (int) (populationSize * length * mutationRate);
        long secondsToRun = 15;
        long start = System.currentTimeMillis();
        long runtime = secondsToRun*1000;

        for (int i = 0; i < sequence.length(); i++) {
            AcidType currentType = AcidType.UNKNOWN;
            if (sequence.charAt(i) == '0')
                currentType = AcidType.WHITE;
            if (sequence.charAt(i) == '1')
                currentType = AcidType.BLACK;
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

            if(ts){
                population = this.tournamentSelection(population);
            }
            if(!ts) {
                population = this.fitnessProportionalSelection(population);
            }

            // Here figure out if crossover happens or not based on probability
            for (int j = 0; j < numberOfCrossovers; j++) {
                crossover(population);
            }

            for (int k = 0; k < numberOfMutations; k++) {
                mutate(population);
            }
        }

        System.out.println(bestEverProtein.getContacts());
        System.out.println(bestEverProtein.getOverlapping());
        System.out.println(bestEverProtein.getFitness());

        Graphics graphics = new Graphics(bestEverProtein, sequence);
        graphics.drawProtein();

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
        if (!isFirstLine) {
            prevPopAvgFitness = popAvgFitness;
        }
        currentBestCandidate = currentFittestProtein;
        // Calculate the average fitness for the generation
        popAvgFitness = accumulatedFitness / population.size();

      if (!isFirstLine) {
          double difference = popAvgFitness - prevPopAvgFitness;
          if (difference > 0) {
              if (difference < popAvgFitness * 0.05)
                  mutationRate += 0.00025;
              else if (difference < popAvgFitness * 0.1)
                  mutationRate += 0.0005;
             else if (difference > popAvgFitness * 0.15)
                 mutationRate -= 0.00025;
          }
          else if (difference < 0) {
              if (difference < -popAvgFitness * 0.04)
                  mutationRate -= 0.0005;
              else if (difference <= -popAvgFitness * 0.07)
                  mutationRate -= 0.001;
              else if (difference <= -popAvgFitness * 0.1)
                  mutationRate -= 0.01;
          }
          if (mutationRate <= 0)
              mutationRate = 0.01;
      }

        // Update the best fitness of this generation
        bestFitnessOfGeneration = currentBestFitness;

        // Check if this is the best fitness ever
        if (currentBestFitness > bestFitnessEver) {
            bestFitnessEver = currentBestFitness;
            bestEverProtein = new Protein(currentFittestProtein);
            if (bestEverContacts < bestEverProtein.getContacts())
                bestEverContacts = bestEverProtein.getContacts();
            if (bestEverOverlappings > bestEverProtein.getOverlapping())
                bestEverOverlappings = bestEverProtein.getOverlapping();
        }

        numberOfMutations = (int) (popSize * length * mutationRate);

        this.logData(isFirstLine, generation, popAvgFitness, bestFitnessOfGeneration, bestFitnessEver, bestEverContacts, bestEverOverlappings, mutationRate);
    }

    private void logData(boolean isFirstLine, int gen, double averageGenFit, double bestGenFit, double bestEverFit, int bestEverContacts, int bestEverOverlappings, double mutationRate) {
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
                writer.write("Generation;AvgGenFitness;BstGenFitness;BstEverFitness;BstEverContacts;BstEverOverlapping;mRate\n");
                isFirstLine = false;
            }

            writer.write(gen + ";" + df.format(averageGenFit) + ";" + df.format(bestGenFit) + ";" + df.format(bestEverFit) + ";" + bestEverContacts + ";" + bestEverOverlappings + ";" + df.format(mutationRate) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
