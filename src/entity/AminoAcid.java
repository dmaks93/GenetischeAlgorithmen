package entity;


import types.Direction;

public class AminoAcid {
    private final int acidIndex;
    private Coordinates coordinates;
    private Direction previousAcidDirection;
    private Direction nextAcidDirection;


    public AminoAcid(int acidIndex) {
        this.acidIndex = acidIndex;
        this.coordinates = new Coordinates();
    }

    public AminoAcid(AminoAcid aa) {
        this.acidIndex = aa.acidIndex;
        this.coordinates = new Coordinates(aa.coordinates); // Assuming Coordinates has a copy constructor
        this.previousAcidDirection = aa.previousAcidDirection;
        this.nextAcidDirection = aa.nextAcidDirection;
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

    public Direction getPreviousAcidDirection() {
        return this.previousAcidDirection;
    }

    public void setPreviousAcidDirection(Direction previousAcidDirection) {
        this.previousAcidDirection = previousAcidDirection;
    }

    public Direction getNextAcidDirection() {
        return this.nextAcidDirection;
    }

    public void setNextAcidDirection(Direction nextAcidDirection) {
        this.nextAcidDirection = nextAcidDirection;
    }
}
