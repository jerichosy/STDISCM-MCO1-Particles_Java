import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticleSimulatorGUI extends JPanel implements KeyListener {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    private List<Particle> particles = new CopyOnWriteArrayList<>(); // Thread-safe ArrayList ideal for occasional writes
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private long lastTime = System.currentTimeMillis();
    private int frames = 0;
    private String fps = "FPS: 0";
    private String particleCount = "Particle Count: 0";
    private boolean isPaused = false;
    private boolean isInDeveloperMode = true;
    private Sprite sprite = new Sprite("src/Images/sprite.png", Particle.gridWidth , Particle.gridHeight);

    private long lastUpdateTime = System.currentTimeMillis();


    public ParticleSimulatorGUI() {

        // Swing Timer for animation
        // Analogy: This is just like specifying an FPS limit in a video game instead of uncapped FPS.
        new Timer(13, e -> updateAndRepaint()).start(); // ~60 FPS

        // Timer to update FPS counter every 0.5 seconds
        new Timer(500, e -> {
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - lastTime;
            fps = String.format("FPS: %.1f", frames * 1000.0 / delta);
            //System.out.println(frames + " frames in the last " + delta + " ms");
            frames = 0; // Reset frame count
            lastTime = currentTime;
        }).start();

        this.addKeyListener(this);


    }

    private void updateAndRepaint() {
        if (!isPaused) {
            long currentTime = System.currentTimeMillis();
            double deltaTime = (currentTime - lastUpdateTime) / 1000.0; // Time in seconds
            lastUpdateTime = currentTime;

            // Submit each particle's run method for parallel execution
            particles.forEach(particle -> executor.submit(() -> particle.update(deltaTime))); // At 60k particles, this takes ~3ms

            // Update particle count string with the current size of the particles list
            particleCount = "Particle Count: " + particles.size();

            repaint(); // Re-draw GUI with updated particle positions
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw a thin border on the canvas
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        for (Particle particle : particles) {
            particle.setMagnified(!isInDeveloperMode);
            particle.draw(g, sprite.getX(), sprite.getY(), sprite.getExcessX(), sprite.getExcessY()); // Let each particle draw itself
        } // At 60k particles, this takes 110-120ms


        frames++; // Increment frame count

        if (sprite.isWillSpawn()) {
            int excessX = sprite.getExcessX() * Particle.gridHeight ;
            int excessY = sprite.getExcessY() * Particle.gridHeight ;

            // Fill the areas outside the visible canvas with black color
            if (excessY < 0) {
                g.fillRect(0, 0, getWidth(), -excessY);
            }
            if (excessX < 0) {
                g.fillRect(0, 0, -excessX, getHeight());
            }
            if (excessY > 0) {
                g.fillRect(0, getHeight() - excessY -16, getWidth(), excessY);
            }
            if (excessX > 0) {
                g.fillRect(getWidth() - excessX, 0, excessX, getHeight());
            }
//            int excessX = sprite.getExcessX();
//            int excessY = sprite.getExcessY();
//            if (excessY < 0) {
//                g.setColor(Color.BLACK);
//                g.fillRect(0, 0, getWidth(), (-excessY * Particle.gridHeight));
//            }
//            if (excessX < 0) {
//                g.setColor(Color.BLACK);
//                g.fillRect(0, 0, (-excessX * Particle.gridHeight) + 16, getHeight()); // 16 yung hard code bug fix !!!!
//            }
//            if (excessY > 0) {
//                g.setColor(Color.BLACK);
//                g.fillRect(0, getHeight() - (excessY * Particle.gridHeight), getWidth(), excessY);
//            }
//            if (excessX > 0) {
//                g.setColor(Color.BLACK);
//                g.fillRect(getWidth() - (excessX * Particle.gridHeight) + 16, 0, excessX, getHeight()); // 16 yung hard code bug fix !!!!
//            }

            sprite.draw(g, this);
        }

        // Draw a semi-transparent background for the FPS counter for better readability
        g.setColor(new Color(0, 0, 0, 128)); // Black with 50% opacity
        g.fillRect(5, 5, 100, 20); // Adjust the size according to your needs
        // Set the color for the FPS text
        g.setColor(Color.WHITE); // White color for the text
        g.drawString(fps, 10, 20); // Draw FPS counter on screen

        // Draw a semi-transparent background for the Particle Count counter for better readability
        g.setColor(new Color(0, 0, 0, 128)); // Black with 50% opacity
        g.fillRect(5, 30, 150, 20); // Adjust the size according to your needs
        // Set the color for the Particle Count text
        g.setColor(Color.WHITE); // White color for the text
        g.drawString(particleCount, 10, 45); // Draw Particle Count on screen

        // Draw a background with dynamic color for the pause state for better readability
        g.setColor(isPaused ? Color.RED : new Color(0, 255, 0)); // Red if paused, Green if not
        g.fillRect(5, 55, 150, 20); // Adjust size as needed
        // Set the color for the pause state text
        g.setColor(Color.WHITE);
        g.drawString("Renderer Paused: " + isPaused, 10, 70);

        // Draw a semi-transparent background for the Developer/Explorer mode for better readability
        g.setColor(new Color(0, 0, 0, 128)); // Black with 50% opacity
        g.fillRect(5, 85, 150, 20); // Adjust size as needed
        // Set the color for the Developer/Explorer mode text
        g.setColor(Color.WHITE);
        g.drawString(isInDeveloperMode ? "Developer Mode" : "Explorer Mode", 10, 100);

        if (!isInDeveloperMode) {
            // Draw a semi-transparent background for the Sprite position coords for better readability
            g.setColor(new Color(0, 0, 0, 128)); // Black with 50% opacity
            g.fillRect(5, 110, 150, 20); // Adjust size as needed
            // Set the color for the Sprite position coords text
            g.setColor(Color.CYAN);
            String spriteCoordinates = String.format("Sprite X: %d, Y: %d", sprite.getX(), sprite.getY());
            g.drawString(spriteCoordinates, 10, 125);
        }
    }

    public void addParticlesLinear(int n, Point startPoint, Point endPoint, double velocity, double angle) {
        double deltaX = (double)(endPoint.x - startPoint.x) / (n - 1);
        double deltaY = (double)(endPoint.y - startPoint.y) / (n - 1);

        for (int i = 0; i < n; i++) {
            int x = startPoint.x + (int)(i * deltaX);
            int y = startPoint.y + (int)(i * deltaY);
            particles.add(new Particle(x, y, velocity, angle, WINDOW_HEIGHT));
        }
    }
    public void addParticlesAngular(int n, Point startPoint, double velocity, double startAngle, double endAngle) {
        double deltaAngle = (endAngle - startAngle) / (n - 1);

        for (int i = 0; i < n; i++) {
            double angle = startAngle + i * deltaAngle;
            particles.add(new Particle(startPoint.x, startPoint.y, velocity, angle, WINDOW_HEIGHT));
        }
    }
    public void addParticlesVelocity(int n, Point startPoint, double startVelocity, double endVelocity, double angle) {
        double deltaVelocity = (endVelocity - startVelocity) / (n - 1);

        for (int i = 0; i < n; i++) {
            double velocity = startVelocity + i * deltaVelocity;
            particles.add(new Particle(startPoint.x, startPoint.y, velocity, angle, WINDOW_HEIGHT));
        }
    }
    private void setupControlPanel(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if(isInDeveloperMode){
            // Section for Adding Linear Particles
            JPanel panelLinear = createPanelForLinearParticles();
            panel.add(panelLinear);

            // Section for Adding Angular Distribution Particles
            JPanel panelAngular = createPanelForAngularParticles();
            panel.add(panelAngular);

            // Section for Adding Velocity Distribution Particles
            JPanel panelVelocity = createPanelForVelocityParticles();
            panel.add(panelVelocity);
        }

        JPanel panelToggle = createPanelForClearAndPause();
        panel.add(panelToggle);

        JPanel panelModeToggle = createPanelForModeToggle();
        panel.add(panelModeToggle);

    }
    private void togglePause(JButton pauseButton){
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Resume Renderer" : "Pause Renderer"); // Update button text based on pause state
        repaint();  // This is needed for the pause state to be updated on the screen

        if(!isPaused){
            setFocusable(true);
            requestFocusInWindow();
        }
    }
    private void clearParticles(){
        particles.clear();
//        repaint();

        if(!isPaused){
            setFocusable(true);
            requestFocusInWindow();
        }
    }
    private void toggleDeveloperExplorerMode(JButton modeToggleButton) {
        isInDeveloperMode = !isInDeveloperMode;
        modeToggleButton.setText(isInDeveloperMode ? "Switch to Explorer Mode" : "Switch to Developer Mode");
        JPanel currentContainerPanel = (JPanel) modeToggleButton.getParent().getParent();

        if (!isInDeveloperMode){
            sprite.setWillSpawn(true);
            sprite.printPosition();

            // only enable key listening on explorer mode
            setFocusable(true);
            requestFocusInWindow();

            currentContainerPanel.getComponent(0).setVisible(false);
            currentContainerPanel.getComponent(1).setVisible(false);
            currentContainerPanel.getComponent(2).setVisible(false);

        } else {
            sprite.setWillSpawn(false);
            currentContainerPanel.getComponent(0).setVisible(true);
            currentContainerPanel.getComponent(1).setVisible(true);
            currentContainerPanel.getComponent(2).setVisible(true);
        }

//        repaint();
    }
    private JPanel createPanelForLinearParticles() {
        JPanel panel = new JPanel(new FlowLayout());

        JTextField nField = new JTextField("1280", 5);
        JTextField startXField = new JTextField("0", 5);
        JTextField startYField = new JTextField("360", 5);
        JTextField endXField = new JTextField("1280", 5);
        JTextField endYField = new JTextField("360", 5);
        JTextField velocityField = new JTextField("1", 5);
        JTextField angleField = new JTextField("0", 5);
        JButton addButton = new JButton("Add Linear");

        addButton.addActionListener(e -> {
            int n = Integer.parseInt(nField.getText());
            Point startPoint = new Point(Integer.parseInt(startXField.getText()), Integer.parseInt(startYField.getText()));
            Point endPoint = new Point(Integer.parseInt(endXField.getText()), Integer.parseInt(endYField.getText()));
            double velocity = Double.parseDouble(velocityField.getText());
            double angle = Double.parseDouble(angleField.getText());

            addParticlesLinear(n, startPoint, endPoint, velocity, angle); // Ensure this method exists and is properly called
        });

        panel.add(new JLabel("N:"));
        panel.add(nField);
        panel.add(new JLabel("Start X:"));
        panel.add(startXField);
        panel.add(new JLabel("Start Y:"));
        panel.add(startYField);
        panel.add(new JLabel("End X:"));
        panel.add(endXField);
        panel.add(new JLabel("End Y:"));
        panel.add(endYField);
        panel.add(new JLabel("Velocity:"));
        panel.add(velocityField);
        panel.add(new JLabel("Angle:"));
        panel.add(angleField);
        panel.add(addButton);

        return panel;
    }
    private JPanel createPanelForAngularParticles() {
        JPanel panel = new JPanel(new FlowLayout());

        JTextField nField = new JTextField("50", 5);
        JTextField startXField = new JTextField("640", 5); // Center X
        JTextField startYField = new JTextField("360", 5); // Center Y
        JTextField startAngleField = new JTextField("0", 5);
        JTextField endAngleField = new JTextField("360", 5);
        JTextField velocityField = new JTextField("750", 5);
        JButton addButton = new JButton("Add Angular");

        addButton.addActionListener(e -> {
            int n = Integer.parseInt(nField.getText());
            Point startPoint = new Point(Integer.parseInt(startXField.getText()), Integer.parseInt(startYField.getText()));
            double startAngle = Double.parseDouble(startAngleField.getText());
            double endAngle = Double.parseDouble(endAngleField.getText());
            double velocity = Double.parseDouble(velocityField.getText());

            addParticlesAngular(n, startPoint, velocity, startAngle, endAngle);
        });

        panel.add(new JLabel("N: "));
        panel.add(nField);
        panel.add(new JLabel("Start X: "));
        panel.add(startXField);
        panel.add(new JLabel("Start Y: "));
        panel.add(startYField);
        panel.add(new JLabel("Start Angle: "));
        panel.add(startAngleField);
        panel.add(new JLabel("End Angle: "));
        panel.add(endAngleField);
        panel.add(new JLabel("Velocity: "));
        panel.add(velocityField);
        panel.add(addButton);

        return panel;
    }
    private JPanel createPanelForVelocityParticles() {
        JPanel panel = new JPanel(new FlowLayout());

        JTextField nField = new JTextField("50", 5);
        JTextField startXField = new JTextField("0", 5); // Center X
        JTextField startYField = new JTextField("0", 5); // Center Y
        JTextField startVelocityField = new JTextField("500", 5);
        JTextField endVelocityField = new JTextField("1000", 5);
        JTextField angleField = new JTextField("45", 5); // Straight up
        JButton addButton = new JButton("Add Velocity");

        addButton.addActionListener(e -> {
            int n = Integer.parseInt(nField.getText());
            Point startPoint = new Point(Integer.parseInt(startXField.getText()), Integer.parseInt(startYField.getText()));
            double startVelocity = Double.parseDouble(startVelocityField.getText());
            double endVelocity = Double.parseDouble(endVelocityField.getText());
            double angle = Double.parseDouble(angleField.getText());

            addParticlesVelocity(n, startPoint, startVelocity, endVelocity, angle);
        });

        panel.add(new JLabel("N: "));
        panel.add(nField);
        panel.add(new JLabel("Start X: "));
        panel.add(startXField);
        panel.add(new JLabel("Start Y: "));
        panel.add(startYField);
        panel.add(new JLabel("Start Vel: "));
        panel.add(startVelocityField);
        panel.add(new JLabel("End Vel: "));
        panel.add(endVelocityField);
        panel.add(new JLabel("Angle: "));
        panel.add(angleField);
        panel.add(addButton);

        return panel;
    }
    private JPanel createPanelForClearAndPause(){
        JPanel panel = new JPanel(new FlowLayout());

        // Button to clear Particles
        JButton clearParticlesButton = new JButton("Clear Particles");
        clearParticlesButton.addActionListener(e->clearParticles());
        clearParticlesButton.setPreferredSize(new Dimension(120,30));
        panel.add(clearParticlesButton);

        // Pause btn
        JButton pauseButton = new JButton("Pause Renderer");
        pauseButton.addActionListener(e->togglePause(pauseButton));
        pauseButton.setPreferredSize(new Dimension(140,30));
        panel.add(pauseButton);

        return panel;
    }
    private JPanel createPanelForModeToggle() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton modeToggleButton = new JButton("Switch to Explorer Mode");
        modeToggleButton.addActionListener(e -> toggleDeveloperExplorerMode(modeToggleButton));
        panel.add(modeToggleButton);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Particle Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create a container panel with BoxLayout along the Y_AXIS
            JPanel containerPanel = new JPanel();
            containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

            ParticleSimulatorGUI simulatorGUI = new ParticleSimulatorGUI();
            simulatorGUI.setPreferredSize(new Dimension(ParticleSimulatorGUI.WINDOW_WIDTH, ParticleSimulatorGUI.WINDOW_HEIGHT));

            // Setup and add the control panel at the top
            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
            simulatorGUI.setupControlPanel(controlPanel);

            // Add the control panel and simulatorGUI to the containerPanel
            containerPanel.add(controlPanel);
            containerPanel.add(simulatorGUI);

            frame.add(containerPanel);  // Add the containerPanel to the frame

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int displacement = 1;

        if(sprite != null) {
            switch (keyCode){
                case KeyEvent.VK_UP:
                    if(sprite.getY() > 0){
                        //sprite.move(0, -displacementY);
                        sprite.updatePosition(0, -displacement);
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(sprite.getY() < WINDOW_HEIGHT){
                        //sprite.move(0, displacementY);
                        sprite.updatePosition(0, displacement);
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if(sprite.getX() > 0){
                        //sprite.move(-displacementX, 0);
                        sprite.updatePosition(-displacement, 0);
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(sprite.getX() < WINDOW_WIDTH) {
                        //sprite.move(displacementX, 0);
                        sprite.updatePosition(displacement, 0);
                    }
                    break;
            }

//            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}