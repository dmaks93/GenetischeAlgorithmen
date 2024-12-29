package api;

import java.util.ArrayList;

import entity.AminoAcid;
import entity.Protein;
import types.Direction;


public abstract class ProteinManager {

    public abstract Protein proteinCreator(ArrayList<AminoAcid> sequence, ArrayList<Direction> directions);

    public abstract Protein createProtein(ArrayList<AminoAcid> sequence);

    public abstract Direction getOppositeDirection(Direction direction);

    public abstract void calculateCoordinateMovement(AminoAcid acid);

    public abstract ArrayList<Direction> createDirectionSequence(int directionSequenceLength);

    public abstract  Direction directionTranslator(int directionAsInteger);

    public abstract boolean directionValidityCheck(Direction currentDirection, Direction previousDirection);

}
