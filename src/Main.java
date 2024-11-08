import entity.*;
import impl.*;
import types.*;


import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        String l1_seq_1 = "01001110";

        ArrayList<AminoAcid> l1_acidSequence_1 = new ArrayList<>();
        ArrayList<AminoAcid> l1_acidSequence_2 = new ArrayList<>();

        ArrayList<Direction> l1_directionSequence_1 = new ArrayList<>();
        Protein l1_protein_1 = new Protein();
        l1_directionSequence_1.add(Direction.Up);
        l1_directionSequence_1.add(Direction.Left);
        l1_directionSequence_1.add(Direction.Down);
        l1_directionSequence_1.add(Direction.Left);
        l1_directionSequence_1.add(Direction.Up);
        l1_directionSequence_1.add(Direction.Up);
        l1_directionSequence_1.add(Direction.Right);

        ArrayList<Direction> l1_directionSequence_2 = new ArrayList<>();
        Protein l1_protein_2 = new Protein();
        l1_directionSequence_2.add(Direction.Up);
        l1_directionSequence_2.add(Direction.Left);
        l1_directionSequence_2.add(Direction.Down);
        l1_directionSequence_2.add(Direction.Left);
        l1_directionSequence_2.add(Direction.Down);
        l1_directionSequence_2.add(Direction.Right);
        l1_directionSequence_2.add(Direction.Up);

        AcidManagerImpl l1_AcidManager = new AcidManagerImpl();
        ProteinManagerImpl l1_ProteinManager = new ProteinManagerImpl();
        FitnessManagerImpl l1_FitnessManager = new FitnessManagerImpl();

        l1_acidSequence_1 = l1_AcidManager.createAcidSequence(l1_seq_1);
        l1_acidSequence_2 = l1_AcidManager.createAcidSequence(l1_seq_1);

        l1_protein_1 = l1_ProteinManager.proteinCreator(l1_acidSequence_1, l1_directionSequence_1);
        l1_protein_2 = l1_ProteinManager.proteinCreator(l1_acidSequence_2, l1_directionSequence_2);

        l1_FitnessManager.fitnessFunction(l1_protein_1);
        System.out.println();
        l1_FitnessManager.fitnessFunction(l1_protein_2);
    }
}