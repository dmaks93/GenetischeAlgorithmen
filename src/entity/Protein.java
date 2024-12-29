package entity;

import types.AcidType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Protein {
    private double fitness;
    private int contacts;
    private int overlapping;
    private Set<String> hhBonds;
    private Set<String> overlaps;
    private ArrayList<AminoAcid> aminoAcids;
    private ArrayList<AcidType> acidTypes;
    private int generation;

    public Protein() {}

    public Protein(ArrayList<AminoAcid> aminoAcids) {
        this.aminoAcids = aminoAcids;
    }

    public Protein(Protein protein) {
        this.fitness = protein.fitness;
        this.contacts = protein.contacts;
        this.overlapping = protein.overlapping;

        // Deep copy of aminoAcids list
        this.aminoAcids = new ArrayList<>();
        for (AminoAcid aa : protein.aminoAcids) {
            this.aminoAcids.add(new AminoAcid(aa));
        }

        if (protein.acidTypes != null) {
            this.acidTypes = new ArrayList<>(protein.acidTypes);
        } else {
            this.acidTypes = new ArrayList<>();
        }

        // Deep copy of hhBonds
        if (protein.hhBonds != null) {
            this.hhBonds = new HashSet<>(protein.hhBonds); // Creates a new Set with copied values
        } else {
            this.hhBonds = new HashSet<>();
        }

        // Deep copy of overlaps
        if (protein.overlaps != null) {
            this.overlaps = new HashSet<>(protein.overlaps); // Creates a new Set with copied values
        } else {
            this.overlaps = new HashSet<>();
        }
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getContacts() {
        return contacts;
    }

    public void setContacts(int contacts) {
        this.contacts = contacts;
    }

    public int getOverlapping() {
        return overlapping;
    }

    public void setOverlapping(int overlapping) {
        this.overlapping = overlapping;
    }

    public Set<String> getHhBonds() { return hhBonds; }

    public void setHhBonds(Set<String> hhBonds) { this.hhBonds = hhBonds; }

    public Set<String> getOverlaps() { return overlaps; }

    public void setOverlaps(Set<String> overlaps) { this.overlaps = overlaps; }

    public ArrayList<AminoAcid> getAminoAcids() { return aminoAcids; }

    public int getGeneration() { return generation; }

    public void setGeneration(int generation) { this.generation = generation; }

    public void setAminoAcids(ArrayList<AminoAcid> aminoAcids) { this.aminoAcids = aminoAcids; }

    public void setAcidTypes(ArrayList<AcidType> acidTypes) { this.acidTypes = acidTypes; }

}
