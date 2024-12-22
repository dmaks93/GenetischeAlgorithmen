package impl;

import api.ProteinManager;
import entity.AminoAcid;
import entity.Protein;
import types.Direction;

import java.util.ArrayList;
import java.util.Random;


public class ProteinManagerImpl extends ProteinManager {
    private ArrayList<AminoAcid> proteinSequence;
     int xCoordinate = 0;
     int yCoordinate = 0;

    @Override
    public Protein proteinCreator(ArrayList<AminoAcid> sequence, ArrayList<Direction> directions) {
        proteinSequence = new ArrayList<>();
        xCoordinate = 0;
        yCoordinate = 0;
        AminoAcid acid;
        Direction prevDirection;
        for (int i = 0; i < sequence.size(); i++) {
            acid = new AminoAcid(sequence.get(i));
            if (i != sequence.size() - 1)
                acid.setNextAcidDirection(directions.get(i));
            if (i != 0) {
                prevDirection = this.getOppositeDirection(directions.get(i - 1));
                acid.setPreviousAcidDirection(prevDirection);
            }
            this.calculateCoordinateMovement(acid);
            proteinSequence.add(acid);
        }
        return new Protein(proteinSequence);
    }

    @Override
    public Protein createProtein(ArrayList<AminoAcid> sequence) {
        Protein protein = new Protein(this.proteinCreator(sequence, this.createDirectionSequence(sequence.size() - 1)));
        return protein;
    }

    @Override
    public Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case Up:
                return Direction.Down;
            case Down:
                return Direction.Up;
            case Left:
                return Direction.Right;
            case Right:
                return Direction.Left;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    @Override
    public void calculateCoordinateMovement(AminoAcid acid) {
        Direction prevDirection = acid.getPreviousAcidDirection();

        if (prevDirection == Direction.Up) {
            acid.setCoordinates(xCoordinate, yCoordinate - 1);
            yCoordinate = yCoordinate - 1;
        }
        if (prevDirection == Direction.Down) {
            acid.setCoordinates(xCoordinate, yCoordinate + 1);
            yCoordinate = yCoordinate + 1;
        }
        if (prevDirection == Direction.Left) {
            acid.setCoordinates(xCoordinate + 1, yCoordinate);
            xCoordinate = xCoordinate + 1;
        }
        if (prevDirection == Direction.Right) {
            acid.setCoordinates(xCoordinate - 1, yCoordinate);
            xCoordinate = xCoordinate - 1;
        }
    }

    @Override
    public ArrayList<Direction> createDirectionSequence(int directionSequenceLength) {
        ArrayList<Direction> directions = new ArrayList<>();
        Direction currentDirection = null;
        Direction prevDirection;
        int directionAsInteger;
        Random randIntGenerator = new Random();
        boolean validDirection = false;

        for (int i = 0; i < directionSequenceLength; i++) {
            while (!validDirection) {
                validDirection = true;
                directionAsInteger = randIntGenerator.nextInt(1000);
                currentDirection = this.directionTranslator(directionAsInteger);
                if (i > 0) {
                    prevDirection = directions.get(i - 1);
                    validDirection = this.directionValidityCheck(currentDirection, prevDirection);
                }
            }
            directions.add(currentDirection);
            validDirection = false;
        }
        return directions;
    }

    private Direction directionTranslator(int directionAsInteger) {
        Direction direction = null;
        if (directionAsInteger <= 250) {
            direction = Direction.Left;
        } else if (directionAsInteger <= 500) {
            direction = Direction.Right;
        } else if (directionAsInteger <= 750) {
            direction = Direction.Up;
        } else if (directionAsInteger <= 1000) {
            direction = Direction.Down;
        }
        return direction;
    }

    private boolean directionValidityCheck(Direction currentDirection, Direction previousDirection) {
        if (currentDirection == Direction.Left && previousDirection == Direction.Right)
            return false;
        if (currentDirection == Direction.Right && previousDirection == Direction.Left)
            return false;
        if (currentDirection == Direction.Up && previousDirection == Direction.Down)
            return false;
        if (currentDirection == Direction.Down && previousDirection == Direction.Up)
            return false;
        return true;
    }
}
