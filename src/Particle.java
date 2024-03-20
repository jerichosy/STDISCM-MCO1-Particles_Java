import java.awt.*;

public class Particle {
    private int x, y; // Position
    private double velocity;
    private double angle; // In degrees

    private boolean isMagnified = false;

    //    private boolean isInExplorerMode = false;
    public static final int PERIPHERY_WIDTH = 33;
    public static final int PERIPHERY_HEIGHT = 19;

    public static int gridWidth = ParticleSimulatorGUI.WINDOW_WIDTH / PERIPHERY_WIDTH;
    public static int gridHeight = ParticleSimulatorGUI.WINDOW_HEIGHT / PERIPHERY_HEIGHT;


    public Particle(int x, int y, double velocity, double angle, int WINDOW_HEIGHT) {
        this.x = x;
        this.y = WINDOW_HEIGHT - y;
        this.velocity = velocity;
        this.angle = angle;
    }

    public void setMagnified(boolean toggle) {
        this.isMagnified = toggle;
    }

    public void update(double deltaTime) {
        // Convert velocity from pixels per second to pixels per update
        double velocityPerUpdate = velocity * deltaTime;

        // Update the position based on velocity and angle
        int nextX = x + (int) (velocityPerUpdate * Math.cos(Math.toRadians(angle)));
        int nextY = y - (int) (velocityPerUpdate * Math.sin(Math.toRadians(angle)));

        // Check and handle collisions with the window boundaries
        // Ensure the particle bounces off the window boundaries properly
        // Check the nextX and nextY for collisions, not the current x and y
        if (nextX <= 0) {
            angle = 180 - angle;
            nextX = 0; // Correct position to stay within bounds
        } else if (nextX >= ParticleSimulatorGUI.WINDOW_WIDTH) {
            angle = 180 - angle;
            nextX = ParticleSimulatorGUI.WINDOW_WIDTH; // Correct position
        }

        if (nextY <= 0) {
            angle = 360 - angle;
            nextY = 0; // Correct position to stay within bounds
        } else if (nextY >= ParticleSimulatorGUI.WINDOW_HEIGHT) {
            angle = 360 - angle;
            nextY = ParticleSimulatorGUI.WINDOW_HEIGHT; // Correct position
        }

        // Update positions after handling collisions
        x = nextX;
        y = nextY;

    }

    private boolean checkIntersection(
            int x1, int y1, int x2, int y2, // Line segment 1 (particle's movement)
            int x3, int y3, int x4, int y4) { // Line segment 2 (wall)
        // Calculate parts of the equations to check intersection
        int den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (den == 0) return false; // Lines are parallel

        int tNum = (x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4);
        int uNum = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3));

        double t = tNum / (double) den;
        double u = uNum / (double) den;

        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            return true; // Intersection occurs
        }
        return false;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        if (isMagnified) {
            g.fillOval(x - offsetX, y - offsetY, gridHeight, gridHeight); // Draw particle with magnified size and apply offset
        } else {
            g.fillOval(x - offsetX, y - offsetY, 5, 5); // Draw particle with default size and apply offset
        }
    }


    // Getter methods to use in the GUI for drawing
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}