import javax.swing.*;
import java.awt.*;

public class Sprite {
    private ImageIcon image;
    public static int x = ParticleSimulatorGUI.WINDOW_WIDTH / 2;
    public static int y = ParticleSimulatorGUI.WINDOW_HEIGHT / 2;

    private int drawX;

    private int drawY;
    private int width;
    private int height;

    private boolean willSpawn = false;



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

    public void updatePosition(int x, int y){
        this.x += x;
        this.y += y;

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
                "Position X: %d, Position Y: %d%n", x, y
        );
    }
}
