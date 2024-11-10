package entity;


import types.AcidType;
import types.Direction;

public class AminoAcid {
    private final int acidIndex;
    private Coordinates coordinates;
    private Direction previousAcid;
    private Direction nextAcid;


    public AminoAcid(int acidIndex) {
        this.acidIndex = acidIndex;
        this.coordinates = new Coordinates();
    }

    public AminoAcid(AminoAcid aa) {
        this.acidIndex = aa.acidIndex;
        this.coordinates = new Coordinates(aa.coordinates); // Assuming Coordinates has a copy constructor
        this.previousAcid = aa.previousAcid;
        this.nextAcid = aa.nextAcid;
    }

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

    public int getAcidIndex() {
        return acidIndex;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int x, int y) {
        this.coordinates.setX(x);
        this.coordinates.setY(y);
    }

    public Direction getPreviousAcid() {
        return this.previousAcid;
    }

    public void setPreviousAcid(Direction previousAcid) {
        this.previousAcid = previousAcid;
    }

    public Direction getNextAcid() {
        return this.nextAcid;
    }

    public void setNextAcid(Direction nextAcid) {
        this.nextAcid = nextAcid;
    }
}
