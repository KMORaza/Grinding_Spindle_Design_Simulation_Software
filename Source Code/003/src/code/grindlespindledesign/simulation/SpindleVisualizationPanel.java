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
    private double bearingLife;
    private double spindleLife;
    private double wheelWear;
    private List<Double> powerHistory;
    private static final int MAX_HISTORY = 50;
    private double rotationAngle = 0;

    public SpindleVisualizationPanel() {
        setPreferredSize(new Dimension(600, 450));
        
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
        bearingLife = 20000.0;
        spindleLife = 1.0;
        wheelWear = 0.0;
        powerHistory = new ArrayList<>();
    }

    public void updateVisualization(double vibration, double temperature, double power, double load, 
                                  double bearingLife, double spindleLife, double wheelWear) {
        this.vibration = vibration;
        this.temperature = temperature;
        this.power = power;
        this.load = load;
        this.bearingLife = bearingLife;
        this.spindleLife = spindleLife;
        this.wheelWear = wheelWear;
        
        powerHistory.add(power);
        if (powerHistory.size() > MAX_HISTORY) {
            powerHistory.remove(0);
        }
        
        repaint();
    }

    public void reset() {
        vibration = 0.0;
        temperature = 0.0;
        power = 0.0;
        load = 0.0;
        bearingLife = 20000.0;
        spindleLife = 1.0;
        wheelWear = 0.0;
        powerHistory.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 20;
        int height = getHeight() - 40;
        int padding = 15;
        int titleHeight = 20;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Spindle animation
        int spindleX = padding + 15;
        int spindleY = padding + titleHeight;
        int spindleWidth = 300;
        int spindleHeight = 200;
        g2d.setColor(Color.GRAY);
        g2d.fillOval(spindleX, spindleY, spindleWidth, spindleHeight);
        g2d.setColor(Color.BLACK);
        g2d.translate(spindleX + spindleWidth / 2, spindleY + spindleHeight / 2);
        g2d.rotate(Math.toRadians(rotationAngle));
        g2d.fillRect(-37, -20, 75, 40);
        g2d.setTransform(new java.awt.geom.AffineTransform());

        // Vibration bar (including wear-induced vibration)
        int barWidth = width / 5;
        g2d.setColor(new Color(0, 102, 204));
        double totalVibration = vibration + calculateWearInducedVibration(wheelWear);
        int vibrationHeight = (int) (totalVibration * 60);
        g2d.fillRect(padding + spindleWidth + padding + 15, height - vibrationHeight + titleHeight, 
            barWidth / 2, vibrationHeight);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 18));
        g2d.drawString("Vib: " + String.format("%.2f mm/s", totalVibration), 
            padding + spindleWidth + padding + 10, height + titleHeight + 270);

        // Temperature gauge
        int gaugeSize = 150;
        int gaugeX = padding + spindleWidth + barWidth + padding + 15;
        int gaugeY = height - gaugeSize + titleHeight - 15;
        g2d.setColor(new Color(204, 0, 0));
        int tempAngle = (int) (temperature * 3);
        g2d.fillArc(gaugeX, gaugeY, gaugeSize, gaugeSize, 90, -tempAngle);
        g2d.setColor(Color.WHITE);
        g2d.drawArc(gaugeX, gaugeY, gaugeSize, gaugeSize, 90, -90);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 18));
        g2d.drawString("Temp: " + String.format("%.1f °C", temperature), 
            gaugeX, height + titleHeight + 300);

        // Power graph
        int graphX = padding + spindleWidth + barWidth + gaugeSize + padding + 15;
        int graphWidth = width - spindleWidth - barWidth - gaugeSize - padding * 3;
        int graphHeight = height / 2 - padding;
        g2d.setColor(Color.WHITE);
        g2d.drawRect(graphX, padding + titleHeight, graphWidth, graphHeight);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 18));
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

        // Load bar
        int loadX = graphX;
        int loadY = padding + titleHeight + graphHeight + padding;
        g2d.setColor(new Color(255, 153, 0));
        int loadWidth = (int) (load / 1000.0 * graphWidth);
        g2d.fillRect(loadX, loadY, loadWidth, 24);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(loadX, loadY, graphWidth, 24);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 18));
        g2d.drawString("Load: " + String.format("%.0f N", load), loadX, loadY + 40);

        // Bearing life gauge
        int bearingGaugeX = padding + spindleWidth + padding + 15;
        int bearingGaugeY = height - gaugeSize + titleHeight + 50;
        g2d.setColor(new Color(0, 204, 204));
        int bearingLifeAngle = (int) (Math.min(bearingLife / 50000.0, 1.0) * 90);
        g2d.fillArc(bearingGaugeX, bearingGaugeY, gaugeSize, gaugeSize, 90, -bearingLifeAngle);
        g2d.setColor(Color.WHITE);
        g2d.drawArc(bearingGaugeX, bearingGaugeY, gaugeSize, gaugeSize, 90, -90);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 18));
        g2d.drawString("Bearing Life: " + String.format("%.0f h", bearingLife), 
            bearingGaugeX, bearingGaugeY + gaugeSize + 20);

        // Spindle life gauge
        int spindleGaugeX = gaugeX;
        int spindleGaugeY = bearingGaugeY;
        g2d.setColor(new Color(204, 204, 0));
        int spindleLifeAngle = (int) (spindleLife * 90);
        g2d.fillArc(spindleGaugeX, spindleGaugeY, gaugeSize, gaugeSize, 90, -spindleLifeAngle);
        g2d.setColor(Color.WHITE);
        g2d.drawArc(spindleGaugeX, spindleGaugeY, gaugeSize, gaugeSize, 90, -90);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 18));
        g2d.drawString("Spindle Life: " + String.format("%.1f%%", spindleLife * 100), 
            spindleGaugeX, spindleGaugeY + gaugeSize + 20);

        // Wheel condition gauge
        int wheelGaugeX = gaugeX + gaugeSize + padding;
        int wheelGaugeY = bearingGaugeY;
        g2d.setColor(new Color(153, 0, 153));
        double wheelCondition = Math.max(0.0, 1.0 - wheelWear / 40.0);
        int wheelConditionAngle = (int) (wheelCondition * 90);
        g2d.fillArc(wheelGaugeX, wheelGaugeY, gaugeSize, gaugeSize, 90, -wheelConditionAngle);
        g2d.setColor(Color.WHITE);
        g2d.drawArc(wheelGaugeX, wheelGaugeY, gaugeSize, gaugeSize, 90, -90);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 18));
        g2d.drawString("Wheel Condition: " + String.format("%.1f%%", wheelCondition * 100), 
            wheelGaugeX, wheelGaugeY + gaugeSize + 20);
    }

    private double calculateWearInducedVibration(double wear) {
        return Math.min(wear * 0.05, 2.0);
    }
}