package code.grindlespindledesign.simulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpindleVisualizationPanel extends JPanel {
    private double vibration;
    private double temperature;
    private double power;
    private double load;
    private List<Double> powerHistory;
    private static final int MAX_HISTORY = 50;
    private double rotationAngle = 0;

    public SpindleVisualizationPanel() {
        setPreferredSize(new Dimension(600, 450)); // Size unchanged
        
        // Animation timer for rotating spindle
        Timer timer = new Timer(50, e -> {
            rotationAngle += 5;
            if (rotationAngle >= 360) rotationAngle = 0;
            repaint();
        });
        timer.start();
        
        vibration = 0.0;
        temperature = 0.0;
        power = 0.0;
        load = 0.0;
        powerHistory = new ArrayList<>();
    }

    public void updateVisualization(double vibration, double temperature, double power, double load) {
        this.vibration = vibration;
        this.temperature = temperature;
        this.power = power;
        this.load = load;
        
        // Update power history
        powerHistory.add(power);
        if (powerHistory.size() > MAX_HISTORY) {
            powerHistory.remove(0);
        }
        
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 20; // Account for border padding
        int height = getHeight() - 40; // Account for title and border
        int padding = 15; // Padding unchanged
        int titleHeight = 20; // Approximate height of titled border text

        // Background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 3D-like Spindle Representation
        int spindleX = padding + 15;
        int spindleY = padding + titleHeight;
        int spindleWidth = 300; // Unchanged
        int spindleHeight = 200; // Unchanged
        g2d.setColor(Color.GRAY);
        g2d.fillOval(spindleX, spindleY, spindleWidth, spindleHeight); // Spindle body
        g2d.setColor(Color.BLACK);
        g2d.translate(spindleX + spindleWidth / 2, spindleY + spindleHeight / 2);
        g2d.rotate(Math.toRadians(rotationAngle));
        g2d.fillRect(-37, -20, 75, 40); // Unchanged
        g2d.setTransform(new java.awt.geom.AffineTransform()); // Reset transform

        // Vibration Bar
        int barWidth = width / 5;
        g2d.setColor(new Color(0, 102, 204));
        int vibrationHeight = (int) (vibration * 60); // Unchanged
        g2d.fillRect(padding + spindleWidth + padding + 15, height - vibrationHeight + titleHeight, 
            barWidth / 2, vibrationHeight);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 22)); // Increased from 16 to 18
        g2d.drawString("Vibration: " + String.format("%.2f mm/s", vibration), 
            padding + spindleWidth + padding + 10, height + titleHeight + 270);

        // Temperature Gauge
        int gaugeSize = 150; // Unchanged
        int gaugeX = padding + spindleWidth + barWidth + padding + 15;
        int gaugeY = height - gaugeSize + titleHeight - 15;
        g2d.setColor(new Color(204, 0, 0));
        int tempAngle = (int) (temperature * 3);
        g2d.fillArc(gaugeX, gaugeY, gaugeSize, gaugeSize, 90, -tempAngle);
        g2d.setColor(Color.WHITE);
        g2d.drawArc(gaugeX, gaugeY, gaugeSize, gaugeSize, 90, -90);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 22)); // Increased from 16 to 18
        g2d.drawString("Temp: " + String.format("%.1f °C", temperature), 
            gaugeX, height + titleHeight + 300);

        // Power Consumption Graph
        int graphX = padding + spindleWidth + barWidth + gaugeSize + padding + 15;
        int graphWidth = width - spindleWidth - barWidth - gaugeSize - padding * 3;
        int graphHeight = height / 2 - padding;
        g2d.setColor(Color.WHITE);
        g2d.drawRect(graphX, padding + titleHeight, graphWidth, graphHeight);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 22)); // Increased from 16 to 18
        g2d.drawString("Power (kW)", graphX, padding + titleHeight - 5);

        if (!powerHistory.isEmpty()) {
            g2d.setColor(new Color(0, 153, 0));
            double maxPower = powerHistory.stream().mapToDouble(Double::doubleValue).max().orElse(10.0);
            for (int i = 1; i < powerHistory.size(); i++) {
                int x1 = graphX + (i - 1) * graphWidth / MAX_HISTORY;
                int x2 = graphX + i * graphWidth / MAX_HISTORY;
                int y1 = padding + titleHeight + graphHeight - 
                    (int) ((powerHistory.get(i - 1) / maxPower) * graphHeight);
                int y2 = padding + titleHeight + graphHeight - 
                    (int) ((powerHistory.get(i) / maxPower) * graphHeight);
                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        // Load Indicator
        int loadX = graphX;
        int loadY = padding + titleHeight + graphHeight + padding;
        g2d.setColor(new Color(255, 153, 0));
        int loadWidth = (int) (load / 1000.0 * graphWidth); // Unchanged
        g2d.fillRect(loadX, loadY, loadWidth, 24);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(loadX, loadY, graphWidth, 24);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 22)); // Increased from 16 to 18
        g2d.drawString("Load: " + String.format("%.0f N", load), loadX, loadY + 40);
    }
}