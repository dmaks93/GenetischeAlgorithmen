package api;

import entity.Protein;
import types.AcidType;

import java.util.ArrayList;

public abstract class FitnessManager {

    public abstract void fitnessFunction(Protein protein, ArrayList<AcidType> types);

    public abstract int countContacts(Protein protein,  ArrayList<AcidType> types);

    public abstract int countOverlapping(Protein protein);

    public abstract double calculateFitness(int contacts, int overlapping, int sequenceLength);

}
