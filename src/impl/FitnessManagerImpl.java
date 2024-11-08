package impl;

import api.FitnessManager;
import entity.AminoAcid;
import entity.Protein;
import types.AcidType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class FitnessManagerImpl extends FitnessManager {
    // Variables to store H/H bonds and overlaps
    private Set<String> hhBonds = new HashSet<>();
    private Set<String> overlaps = new HashSet<>();

    @Override
    public void fitnessFunction(Protein protein) {
        // Clear previous data
        hhBonds.clear();
        overlaps.clear();

        int contacts = this.countContacts(protein);
        int overlapping = this.countOverlapping(protein);
        int sequenceLenght = protein.aminoAcids.size();
        double fitness = this.calculateFitness(contacts, overlapping, sequenceLenght);

        System.out.println("H/H Bonds: " + hhBonds);
        System.out.println("Overlapping: " + overlaps);
        System.out.println("Contacts: " + contacts);
        System.out.println("Overlapping: " + overlapping);
        System.out.println("Sequence length: " + sequenceLenght);
        System.out.println("Fitness: " + fitness);
        System.out.println();
        System.out.println();

        protein.setFitness(fitness);
        protein.setContacts(contacts);
        protein.setOverlapping(overlapping);

        //  return fitness;
    }

    @Override
    public int countContacts(Protein protein) {
        int contacts = 0;
        Set<String> contactSet = new HashSet<>(); // To ensure unique contacts

        ArrayList<AminoAcid> aminoAcids = protein.aminoAcids;

        for (int i = 0; i < aminoAcids.size(); i++) {
            AminoAcid currentAcid = aminoAcids.get(i);

            if (currentAcid.getType() == AcidType.BLACK) {
                int currentX = currentAcid.getCoordinates().getX();
                int currentY = currentAcid.getCoordinates().getY();

                // Check all possible neighboring positions
                int[][] neighbors = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

                for (int[] neighbor : neighbors) { // for each neighbour possibility
                    int neighborX = currentX + neighbor[0];
                    int neighborY = currentY + neighbor[1];

                    for (int j = 0; j < aminoAcids.size(); j++) {
                        if (i != j) { // Ensure not to observe yourself
                            AminoAcid otherAcid = aminoAcids.get(j);

                            if (otherAcid.getType() == AcidType.BLACK && // check if it is black and neighbor
                                    otherAcid.getCoordinates().getX() == neighborX &&
                                    otherAcid.getCoordinates().getY() == neighborY) {

                                // Ensure they are not sequentially connected
                                if (j != i - 1 && j != i + 1) {
                                    String contactKey = createContactKey(currentAcid.getAcidIndex(), otherAcid.getAcidIndex());
                                    if (!contactSet.contains(contactKey)) {
                                        contactSet.add(contactKey);
                                        contacts++;
                                        hhBonds.add("Bond between " + currentAcid.getAcidIndex() + " and " + otherAcid.getAcidIndex());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return contacts;
    }

    @Override
    public int countOverlapping(Protein protein) {
        int overlappings = 0;
        Map<String, Set<Integer>> positionMap = new HashMap<>(); //

        for (AminoAcid acid : protein.aminoAcids) {
            String positionKey = acid.getCoordinates().getX() + "-" + acid.getCoordinates().getY();

            if (!positionMap.containsKey(positionKey)) {
                positionMap.put(positionKey, new HashSet<>());
            }

            positionMap.get(positionKey).add(acid.getAcidIndex());

            // n - 1
            if (positionMap.get(positionKey).size() > 1) {
                overlappings += positionMap.get(positionKey).size() - 1;
                overlaps.add("Overlap at (" + acid.getCoordinates().getX() + ", " + acid.getCoordinates().getY() + ") between amino acids: " + positionMap.get(positionKey));
            }
        }
        return overlappings;
    }

    @Override
    public double calculateFitness(int contacts, int overlapping, int sequenceLength) {
        double fitness;
        double badTrait = 1.0;
        if (overlapping == 1)
            badTrait = 2.0;
        if (overlapping > 1)
            badTrait = overlapping * overlapping;

        fitness = 1.0 + ((double) contacts / badTrait) + (double) sequenceLength / badTrait + (double) contacts / sequenceLength;
        fitness = fitness * 10;
        return fitness;
    }

    private String createContactKey(int acidId1, int acidId2) {
        // Create a key where the smaller ID comes first
        if (acidId1 < acidId2) {
            // If acidId1 is smaller, format as "acidId1-acidId2"
            return acidId1 + "-" + acidId2;
        } else {
            // If acidId2 is smaller, format as "acidId2-acidId1"
            return acidId2 + "-" + acidId1;
        }
    }
}
