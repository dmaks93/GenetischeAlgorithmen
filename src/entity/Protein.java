package entity;

import java.util.ArrayList;

public class Protein {
    private double fitness;
    private int contacts;
    private int overlapping;
    public ArrayList<AminoAcid> aminoAcids;

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
}
