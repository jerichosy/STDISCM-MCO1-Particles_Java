import javax.swing.*;
import java.awt.*;

public class Sprite {
    private ImageIcon image;
    private int x = ParticleSimulatorGUI.WINDOW_WIDTH / 2;
    private int y = ParticleSimulatorGUI.WINDOW_HEIGHT / 2;

    private int drawX;

    private int drawY;
    private int width;
    private int height;

    private boolean willSpawn = false;

    private int excessX = 0;
    private int excessY = 0;

    public static final int PERIPHERY_WIDTH = 33;
    public static final int PERIPHERY_HEIGHT = 19;

    private final int MID_PERIHERAL_WIDTH = PERIPHERY_WIDTH / 2;

    private final int MID_PERIHERAL_HEIGHT = PERIPHERY_HEIGHT / 2;

    public Sprite(String imagePath, int drawX, int drawY, int width,  int height) {
        this.image = new ImageIcon(imagePath);
        this.image.setImage(this.image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        this.drawX = drawX;
        this.drawY = drawY;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g, Component observer){
        image.paintIcon(observer, g, drawX, drawY);
    }

    public  void move(int dx, int dy){
        drawX += dx;
        drawY += dy;
    }

//    public void move(int dx, int dy) {
//        int newX = drawX + dx;
//        int newY = drawY + dy;
//
//        // Ensure sprite stays centered in its local canvas
//        int canvasWidth = 500;
//        int canvasHeight = 360;
//
//        // Calculate bounds for sprite's movement within the local canvas
//        int minX = canvasWidth / 2;
//        int maxX = ParticleSimulatorGUI.WINDOW_WIDTH - canvasWidth / 2;
//        int minY = canvasHeight / 2;
//        int maxY = ParticleSimulatorGUI.WINDOW_HEIGHT - canvasHeight / 2;
//
//        // Update sprite position within the bounds
//        drawX = Math.max(minX, Math.min(maxX, newX));
//        drawY = Math.max(minY, Math.min(maxY, newY));
//
//        System.out.println("newX: " + newX + ", newY: " + newY);
//        System.out.println("minX: " + minX + ", maxX: " + maxX + ", minY: " + minY + ", maxY: " + maxY);
//        System.out.println("dx: " + dx + ", dy: " + dy);
//    }
    public void updatePosition(int x, int y){

        this.x += x;
        excessX = Math.abs(Math.min(Math.min(0, this.x - MID_PERIHERAL_WIDTH), ParticleSimulatorGUI.WINDOW_WIDTH - (this.x + MID_PERIHERAL_WIDTH)));

        this.y += y;
        excessY = Math.abs(Math.min(Math.min(0, this.y - MID_PERIHERAL_HEIGHT), ParticleSimulatorGUI.WINDOW_HEIGHT - (this.y + MID_PERIHERAL_HEIGHT)));


        printPosition();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getDrawX() {
        return drawX;
    }

    public void setDrawX(int drawX) {
        this.drawX = drawX;
    }

    public int getDrawY() {
        return drawY;
    }

    public void setDrawY(int drawY) {
        this.drawY = drawY;
    }

    public boolean isWillSpawn() {
        return willSpawn;
    }

    public void setWillSpawn(boolean willSpawn) {
        this.willSpawn = willSpawn;

        if (willSpawn){
            drawX = ParticleSimulatorGUI.WINDOW_WIDTH / 2;
            drawY = ParticleSimulatorGUI.WINDOW_HEIGHT / 2;
        }
    }


    public void printPosition(){
        System.out.printf(
                "Position X: %d, Position Y: %d, ExcessX: %d, Excess: %d%n", x, y, excessX, excessY
        );
    }

    public int getExcessX() {
        return excessX;
    }

    public int getExcessY() {
        return excessY;
    }
}

// UL -> X: 623,  Y: 350
// LL -> X: 623,  Y: 369
// UR -> X: 656,  Y: 350
// LR -> X: 656,  Y: 369
