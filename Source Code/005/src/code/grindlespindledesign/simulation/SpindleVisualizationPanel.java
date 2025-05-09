package code.grindlespindledesign.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class SpindleVisualizationPanel extends JPanel {
    private List<ScenarioData> scenarioDataList;
    private JTabbedPane tabbedPane;
    private Timer animationTimer;

    // Inner class to hold data for each scenario
    private static class ScenarioData {
        String name;
        double vibration;
        double temperature;
        double power;
        double load;
        double bearingLife;
        double spindleLife;
        double wheelWear;
        int maintenanceNeeded;
        int maxSpeed;
        double wheelDiameter;

        ScenarioData(String name, double vibration, double temperature, double power, double load,
                     double bearingLife, double spindleLife, double wheelWear, int maintenanceNeeded,
                     int maxSpeed, double wheelDiameter) {
            this.name = name;
            this.vibration = vibration;
            this.temperature = temperature;
            this.power = power;
            this.load = load;
            this.bearingLife = bearingLife;
            this.spindleLife = spindleLife;
            this.wheelWear = wheelWear;
            this.maintenanceNeeded = maintenanceNeeded;
            this.maxSpeed = maxSpeed;
            this.wheelDiameter = wheelDiameter;
        }
    }

    // Inner class for spindle animation
    private class SpindleCanvas extends JPanel {
        private ScenarioData data;
        private double rotationAngle;
        private double vibrationOffsetX;
        private double vibrationOffsetY;
        private boolean vibrationDirectionX;
        private boolean vibrationDirectionY;

        SpindleCanvas(ScenarioData data) {
            this.data = data;
            this.rotationAngle = 0.0;
            this.vibrationOffsetX = 0.0;
            this.vibrationOffsetY = 0.0;
            this.vibrationDirectionX = true;
            this.vibrationDirectionY = true;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(400, 300));
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2 + (int) vibrationOffsetX;
            int centerY = height / 2 + (int) vibrationOffsetY;
            int shaftLength = 200;
            int shaftRadius = 12;
            int wheelRadius = (int) (data.wheelDiameter / 4);
            int bearingRadius = 18;
            int housingWidth = shaftLength + 40;
            int housingHeight = shaftRadius * 3;

            // Vibration animation
            double vibrationAmplitude = data.vibration > 1.0 ? data.vibration * 5 : 0;
            if (vibrationAmplitude > 0) {
                vibrationOffsetX += vibrationDirectionX ? 0.3 : -0.3;
                vibrationOffsetY += vibrationDirectionY ? 0.3 : -0.3;
                if (Math.abs(vibrationOffsetX) >= vibrationAmplitude) vibrationDirectionX = !vibrationDirectionX;
                if (Math.abs(vibrationOffsetY) >= vibrationAmplitude) vibrationDirectionY = !vibrationDirectionY;
            } else {
                vibrationOffsetX = vibrationOffsetY = 0;
            }

            // Draw housing with flanges
            g2d.setColor(new Color(80, 80, 80));
            g2d.fillRect(centerX - housingWidth / 2, centerY - housingHeight / 2, housingWidth, housingHeight);
            g2d.setColor(new Color(60, 60, 60));
            int flangeWidth = 20;
            g2d.fillRect(centerX - housingWidth / 2 - flangeWidth, centerY - housingHeight / 2, flangeWidth, housingHeight);
            g2d.fillRect(centerX + housingWidth / 2, centerY - housingHeight / 2, flangeWidth, housingHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(centerX - housingWidth / 2, centerY - housingHeight / 2, housingWidth, housingHeight);

            // Draw bearings as annular rings
            GradientPaint bearingGradient = new GradientPaint(
                centerX - shaftLength / 2, centerY - bearingRadius, new Color(180, 180, 180),
                centerX - shaftLength / 2, centerY + bearingRadius, new Color(120, 120, 120));
            g2d.setPaint(bearingGradient);
            Ellipse2D bearing1 = new Ellipse2D.Double(centerX - shaftLength / 2 - bearingRadius, centerY - bearingRadius, bearingRadius * 2, bearingRadius * 2);
            Ellipse2D bearing2 = new Ellipse2D.Double(centerX + shaftLength / 2 - bearingRadius, centerY - bearingRadius, bearingRadius * 2, bearingRadius * 2);
            g2d.fill(bearing1);
            g2d.fill(bearing2);
            g2d.setColor(new Color(100, 100, 100));
            g2d.fillOval(centerX - shaftLength / 2 - bearingRadius / 2, centerY - bearingRadius / 2, bearingRadius, bearingRadius);
            g2d.fillOval(centerX + shaftLength / 2 - bearingRadius / 2, centerY - bearingRadius / 2, bearingRadius, bearingRadius);

            // Draw cylindrical shaft with texture
            GradientPaint shaftGradient = new GradientPaint(
                centerX - shaftLength / 2, centerY - shaftRadius, new Color(200, 200, 200),
                centerX - shaftLength / 2, centerY + shaftRadius, new Color(140, 140, 140));
            g2d.setPaint(shaftGradient);
            RoundRectangle2D shaft = new RoundRectangle2D.Double(
                centerX - shaftLength / 2, centerY - shaftRadius, shaftLength, shaftRadius * 2, shaftRadius, shaftRadius);
            g2d.fill(shaft);
            // Add texture lines
            g2d.setColor(new Color(120, 120, 120));
            for (int x = centerX - shaftLength / 2 + 5; x < centerX + shaftLength / 2 - 5; x += 10) {
                g2d.drawLine(x, centerY - shaftRadius, x, centerY + shaftRadius);
            }

            // Draw grinding wheel with hub and texture
            AffineTransform originalTransform = g2d.getTransform();
            g2d.translate(centerX + shaftLength / 2 - wheelRadius, centerY);
            g2d.rotate(Math.toRadians(rotationAngle));
            // Wheel hub
            GradientPaint hubGradient = new GradientPaint(
                -wheelRadius / 2, 0, new Color(160, 160, 160),
                wheelRadius / 2, 0, new Color(100, 100, 100));
            g2d.setPaint(hubGradient);
            g2d.fillOval(-wheelRadius / 2, -wheelRadius / 2, wheelRadius, wheelRadius);
            // Wheel abrasive surface
            GradientPaint wheelGradient = new GradientPaint(
                -wheelRadius, 0, new Color(180, 180, 180),
                wheelRadius, 0, new Color(120, 120, 120));
            g2d.setPaint(wheelGradient);
            g2d.fillOval(-wheelRadius, -wheelRadius, wheelRadius * 2, wheelRadius * 2);
            // Texture for abrasive surface
            g2d.setColor(new Color(100, 100, 100, 100));
            for (int i = 0; i < 360; i += 15) {
                g2d.rotate(Math.toRadians(i));
                g2d.drawLine(wheelRadius / 2, 0, wheelRadius, 0);
                g2d.rotate(Math.toRadians(-i));
            }
            g2d.setColor(Color.BLACK);
            g2d.drawOval(-wheelRadius, -wheelRadius, wheelRadius * 2, wheelRadius * 2);
            g2d.setTransform(originalTransform);

            // Temperature effect on shaft and bearings
            float tempFactor = (float) Math.min(data.temperature / 100.0, 1.0);
            Color tempColor = new Color(tempFactor, 0, 1.0f - tempFactor);
            g2d.setColor(tempColor);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g2d.fill(shaft);
            g2d.fill(bearing1);
            g2d.fill(bearing2);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            // Maintenance warning
            if (data.maintenanceNeeded == 1) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Verdana", Font.BOLD, 14));
                g2d.drawString("MAINTENANCE NEEDED", centerX - housingWidth / 2, centerY - housingHeight / 2 - 20);
            }

            // Component labels (subtle)
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.setFont(new Font("Verdana", Font.PLAIN, 10));
            g2d.drawString("Shaft", centerX - 20, centerY + shaftRadius + 20);
            g2d.drawString("Wheel", centerX + shaftLength / 2 - wheelRadius - 20, centerY - wheelRadius - 10);
            g2d.drawString("Bearings", centerX - shaftLength / 2 - bearingRadius - 10, centerY + bearingRadius + 20);

            // Update animation
            rotationAngle += data.maxSpeed / 1000.0;
            if (rotationAngle >= 360) rotationAngle -= 360;
        }
    }

    // Inner class for metric bars
    private class MetricsCanvas extends JPanel {
        private ScenarioData data;

        MetricsCanvas(ScenarioData data) {
            this.data = data;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(400, 300));
            setBorder(BorderFactory.createTitledBorder("Performance Metrics"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int barHeight = 30;
            int gap = 10;
            int x = 20;
            int y = 20;
            int barWidthMax = width - 150;

            Font labelFont = new Font("Verdana", Font.PLAIN, 12);
            Font valueFont = new Font("Verdana", Font.BOLD, 12);
            g2d.setFont(labelFont);

            // Thresholds
            double vibrationThreshold = 1.0;
            double temperatureThreshold = 50.0;
            double bearingLifeThreshold = 20000;
            double spindleLifeThreshold = 0.5;
            double wheelWearThreshold = 40.0;

            // Draw bars
            drawMetricBar(g2d, x, y, barWidthMax, barHeight, "Vibration (mm/s)", data.vibration, vibrationThreshold, 2.0);
            y += barHeight + gap;
            drawMetricBar(g2d, x, y, barWidthMax, barHeight, "Temperature (°C)", data.temperature, temperatureThreshold, 100.0);
            y += barHeight + gap;
            drawMetricBar(g2d, x, y, barWidthMax, barHeight, "Power (kW)", data.power, data.power * 1.2, data.power * 2.0);
            y += barHeight + gap;
            drawMetricBar(g2d, x, y, barWidthMax, barHeight, "Load (N)", data.load, data.load * 1.5, data.load * 2.0);
            y += barHeight + gap;
            drawMetricBar(g2d, x, y, barWidthMax, barHeight, "Bearing Life (hours)", data.bearingLife, bearingLifeThreshold, 50000.0);
            y += barHeight + gap;
            drawMetricBar(g2d, x, y, barWidthMax, barHeight, "Spindle Life (%)", data.spindleLife * 100, spindleLifeThreshold * 100, 100.0);
            y += barHeight + gap;
            drawMetricBar(g2d, x, y, barWidthMax, barHeight, "Wheel Wear (mm)", data.wheelWear, wheelWearThreshold, 100.0);
            y += barHeight + gap;

            // Maintenance indicator
            g2d.setFont(valueFont);
            String maintenanceText = data.maintenanceNeeded == 1 ? "Maintenance Needed: Yes" : "Maintenance Needed: No";
            Color maintenanceColor = data.maintenanceNeeded == 1 ? Color.RED : Color.GREEN;
            g2d.setColor(maintenanceColor);
            g2d.drawString(maintenanceText, x, y + 20);
        }

        private void drawMetricBar(Graphics2D g2d, int x, int y, int maxWidth, int height,
                                  String label, double value, double threshold, double maxValue) {
            Font labelFont = new Font("Verdana", Font.PLAIN, 12);
            Font valueFont = new Font("Verdana", Font.BOLD, 12);

            g2d.setFont(labelFont);
            g2d.setColor(Color.BLACK);
            g2d.drawString(label, x, y + height / 2 + 5);

            int barWidth = (int) (maxWidth * (value / maxValue));
            barWidth = Math.min(barWidth, maxWidth);

            Color barColor = value > threshold ? Color.RED : Color.GREEN;
            g2d.setColor(barColor);
            g2d.fillRect(x + 150, y, barWidth, height);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + 150, y, maxWidth, height);

            g2d.setFont(valueFont);
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("%.2f", value), x + maxWidth + 160, y + height / 2 + 5);
        }
    }

    // Inner class for scenario panel
    private class ScenarioPanel extends JPanel {
        ScenarioPanel(ScenarioData data) {
            setLayout(new BorderLayout());

            // Content panel with vertical layout
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);

            // Metrics canvas
            MetricsCanvas metricsCanvas = new MetricsCanvas(data);
            contentPanel.add(metricsCanvas);
            contentPanel.add(Box.createVerticalStrut(10)); // Spacer

            // Spindle canvas
            SpindleCanvas spindleCanvas = new SpindleCanvas(data);
            contentPanel.add(spindleCanvas);

            // Scroll pane
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            add(scrollPane, BorderLayout.CENTER);
        }
    }

    public SpindleVisualizationPanel() {
        scenarioDataList = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Simulation Visualization"));
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        animationTimer = new Timer(16, e -> {
            for (Component tab : tabbedPane.getComponents()) {
                if (tab instanceof ScenarioPanel) {
                    ((ScenarioPanel) tab).repaint();
                }
            }
        });
        animationTimer.start();

        reset();
    }

    public void updateVisualization(double vibration, double temperature, double power, double load,
                                   double bearingLife, double spindleLife, double wheelWear, int maintenanceNeeded) {
        scenarioDataList.clear();
        tabbedPane.removeAll();

        scenarioDataList.add(new ScenarioData("Balanced", vibration, temperature, power, load,
                                             bearingLife, spindleLife, wheelWear, maintenanceNeeded, 10000, 200));
        scenarioDataList.add(new ScenarioData("High-Speed", vibration * 1.2, temperature * 1.1, power, load * 0.8,
                                             bearingLife * 0.9, spindleLife * 0.95, wheelWear * 1.1, maintenanceNeeded, 12000, 200));
        scenarioDataList.add(new ScenarioData("High-Torque", vibration * 0.8, temperature * 1.2, power, load * 1.2,
                                             bearingLife * 0.85, spindleLife * 0.9, wheelWear * 1.2, maintenanceNeeded, 8000, 200));

        for (ScenarioData data : scenarioDataList) {
            ScenarioPanel panel = new ScenarioPanel(data);
            tabbedPane.addTab(data.name, panel);
        }

        revalidate();
        repaint();
    }

    public void reset() {
        scenarioDataList.clear();
        tabbedPane.removeAll();
        tabbedPane.addTab("No Data", new JPanel());
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}