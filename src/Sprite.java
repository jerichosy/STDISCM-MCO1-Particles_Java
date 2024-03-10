import javax.swing.*;
import java.awt.*;

public class Sprite {
    private ImageIcon image;
    private int x;
    private int y;
    private int width;
    private int height;

    public Sprite(String imagePath, int x, int y, int width,  int height) {
        this.image = new ImageIcon(imagePath);
        this.image.setImage(this.image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g, Component observer){
        image.paintIcon(observer, g, x, y);
    }

    public  void move(int dx, int dy){
        x += dx;
        y += dy;
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
}
