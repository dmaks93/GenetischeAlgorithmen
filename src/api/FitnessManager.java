package api;

import entity.Protein;

public abstract class FitnessManager {

    public abstract void fitnessFunction(Protein protein);

    public abstract int countContacts(Protein protein);

    public abstract int countOverlapping(Protein protein);

    public abstract double calculateFitness(int contacts, int overlapping, int sequenceLength);

}
