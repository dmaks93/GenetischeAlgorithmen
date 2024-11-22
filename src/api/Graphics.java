package api;

import entity.AminoAcid;
import entity.Protein;
import entity.Coordinates;
import types.Direction;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


public class Graphics {
    int height = 4000;
    int width = 4000;
    int cellSize = 80;
    int x = width / 2;
    int y = height / 2;
    double fitness;
    int contacts;
    int overlappings;
    String sequence;
    ArrayList<AminoAcid> aminoAcids;
    ArrayList<Coordinates> coordinates = new ArrayList<>();


    public Graphics(Protein protein, String sequence) {
        this.fitness = protein.getFitness();
        this.contacts = protein.getContacts();
        this.overlappings = protein.getOverlapping();
        this.sequence = sequence;
        this.aminoAcids = protein.aminoAcids;
        for (int i = 0; i < aminoAcids.size(); i++) {
            coordinates.add(new Coordinates(aminoAcids.get(i).getCoordinates().getX(), aminoAcids.get(i).getCoordinates().getY()));
        }
    }

    public void drawProtein() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.YELLOW);
        g2.fillRect(0, 0, width, height);

        this.writeText(g2, 50, 50, "Generation: ");
        this.writeText(g2, 50, 100, "There are " + contacts + " hydrophobic contacts");
        this.writeText(g2, 50, 150, "There are " + overlappings + " overlappings");
        this.writeText(g2, 50, 200, "Fitness of this protein is: " + fitness);


        for (int i = 0; i < sequence.length(); i++) {
            int currX = coordinates.get(i).getX();
            int currY = coordinates.get(i).getY();
            int tX = 0;
            int tY = 0;

            if (sequence.charAt(i) == '0') {
                g2.setColor(Color.BLACK);
            } else if (sequence.charAt(i) == '1') {
                g2.setColor(Color.WHITE);
            }
            g2.fillRect(x, y, cellSize, cellSize);

            g2.setColor(Color.GREEN);
            String label = String.valueOf(i);
            for (int j = 0; j < i; j++) {
                tX = coordinates.get(j).getX();
                tY = coordinates.get(j).getY();
                if (currX == tX && currY == tY) {
                    label = String.valueOf(j) + ", " + label;
                }
            }
            Font font = new Font("Serif", Font.PLAIN, 20);
            g2.setFont(font);
            FontMetrics metrics = g2.getFontMetrics();
            int ascent = metrics.getAscent();
            int labelWidth = metrics.stringWidth(label);
            g2.drawString(label, x + cellSize / 2 - labelWidth / 2, y + cellSize / 2 + ascent / 2);

            g2.setColor(Color.BLUE);
            if (aminoAcids.get(i).getNextAcidDirection() == Direction.Up) {
                g2.drawLine(x + cellSize / 2, y, x + cellSize / 2, y - 150);
                y -= 150;
            } else if (aminoAcids.get(i).getNextAcidDirection() == Direction.Down) {
                g2.drawLine(x + cellSize / 2, y + cellSize, x + cellSize / 2, y + cellSize + 150);
                y += 150;
            } else if (aminoAcids.get(i).getNextAcidDirection() == Direction.Left) {
                g2.drawLine(x, y + cellSize / 2, x - 150, y + cellSize / 2);
                x -= 150;
            } else if (aminoAcids.get(i).getNextAcidDirection() == Direction.Right) {
                g2.drawLine(x + cellSize, y + cellSize / 2, x + cellSize + 150, y + cellSize / 2);
                x += 150;
            }
        }

        String folder = "output";
        String filename = "image.png";

        if (new File(folder).exists() == false)
            new File(folder).mkdirs();
        try {
            ImageIO.write(image, "png", new File(folder + File.separator + filename));
        } catch (
                IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void writeText(Graphics2D graphic, int x, int y, String label) {
        graphic.setColor(new Color(0, 0, 0));
        Font font = new Font("Serif", Font.PLAIN, 40);
        graphic.setFont(font);
        FontMetrics metrics = graphic.getFontMetrics();
        int ascent = metrics.getAscent();
        graphic.drawString(label, x, y + ascent / 2);
    }
}