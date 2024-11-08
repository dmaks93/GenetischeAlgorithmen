package api;

import entity.AminoAcid;
import entity.Protein;
import types.AcidType;
import types.Direction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Graphics {

    private final int cellSize = 40;  // The size of each square representing an amino acid
    private int offsetX = Integer.MAX_VALUE;
    private int offsetY = Integer.MAX_VALUE;

    public void visualizeProtein(Protein protein, String outputPath) {
        // Calculate the bounding box and offset for the protein
        calculateOffsets(protein);

        // Determine the image size based on the bounding box
        int width = (offsetX + cellSize) * cellSize + 100;  // Adding padding
        int height = (offsetY + cellSize) * cellSize + 100; // Adding padding

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g2.setColor(Color.YELLOW);
        g2.fillRect(0, 0, width, height);

        for (int i = 0; i < protein.aminoAcids.size(); i++) {
            int x = normalizeX(protein.aminoAcids.get(i).getCoordinates().getX());
            int y = normalizeY(protein.aminoAcids.get(i).getCoordinates().getY());
            int xD = x;
            int yD = y;

            Direction nextDirection = null;
            if (i != protein.aminoAcids.size() - 1)
                nextDirection = protein.aminoAcids.get(i).getNextAcid();

            this.drawAminoAcid(g2, protein.aminoAcids.get(i), x, y);
            switch (nextDirection) {
                case Up:
                    yD += cellSize;
                    break;
                case Down:
                    yD -= cellSize;
                    break;
                case Left:
                    xD -= cellSize;
                    break;
                case Right:
                    xD += cellSize;
                    break;
                case null:
                    break;
            }
            drawConnection(g2, x, y, xD, yD);
            x = xD;
            y = yD;
        }

        // Display fitness, overlapping, and H/H bonds
        drawProteinInfo(g2, protein, height);

        // Save the image
        saveImage(image, outputPath);
    }

    private void calculateOffsets(Protein protein) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Calculate the min and max X and Y coordinates
        for (AminoAcid aa : protein.aminoAcids) {
            int x = aa.getCoordinates().getX();
            int y = aa.getCoordinates().getY();
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        // Set the offset values based on bounding box
        offsetX = -minX;
        offsetY = -minY;
    }

    private int normalizeX(int coordinate) {
        return (coordinate + offsetX) * cellSize + 50;
    }

    private int normalizeY(int coordinate) {
        return (coordinate + offsetY) * cellSize + 50;
    }

    private void drawAminoAcid(Graphics2D g2, AminoAcid aa, int x, int y) {
        // Set color based on amino acid type
        if (aa.getType() == AcidType.BLACK) {
            g2.setColor(Color.BLACK);
        } else if (aa.getType() == AcidType.WHITE) {
            g2.setColor(Color.WHITE);
        } else {
            g2.setColor(Color.GRAY); // For UNKNOWN type
        }

        // Draw the amino acid as a square
        g2.fillRect(x + 1, y + 1, cellSize, cellSize);

        // Draw the index inside the square
        g2.setColor(aa.getType() == AcidType.BLACK ? Color.WHITE : Color.BLACK);
        g2.setFont(new Font("Serif", Font.PLAIN, 12));
        g2.drawString(String.valueOf(aa.getAcidIndex()), x + cellSize / 3, y + cellSize / 2 + 5);
    }

    private void drawConnection(Graphics2D g2, int x1, int y1, int x2, int y2) {
        // Draw the connection line
        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(2)); // Make the line a bit thicker
        g2.drawLine(x1 + cellSize / 2, y1 + cellSize / 2, x2 + cellSize / 2, y2 + cellSize / 2);

    }

    private void drawProteinInfo(Graphics2D g2, Protein protein, int height) {
        g2.setFont(new Font("Serif", Font.PLAIN, 20));
        g2.setColor(Color.BLACK);
        g2.drawString("Fitness: " + protein.getFitness(), 10, height - 60);
        g2.drawString("Overlapping: " + protein.getOverlapping(), 10, height - 40);
        g2.drawString("H/H Bonds: " + protein.getContacts(), 10, height - 20);
    }

    private void saveImage(BufferedImage image, String outputPath) {
        try {
            ImageIO.write(image, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}