package code.grindlespindledesign.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SpindleSimulatorPanel extends JPanel {
    private JComboBox<String> spindleTypeCombo, bearingTypeCombo, coolingTypeCombo, lubricationTypeCombo, toolInterfaceCombo;
    private JTextField powerRatingField, speedField, wheelDiameterField, preloadField, alignmentToleranceField;
    private JTextField timeDurationField;
    private JTextArea resultsArea;
    private SpindleSimulation simulation;
    private SpindleVisualizationPanel visualizationPanel;
    private SpindleMaintenance maintenance;

    public SpindleSimulatorPanel() {
        setLayout(new BorderLayout(10, 10));
        simulation = new SpindleSimulation();
        maintenance = new SpindleMaintenance();
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(11, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Spindle Configuration"));
        inputPanel.setPreferredSize(new Dimension(450, 380));
        Font inputFont = new Font("Verdana", Font.PLAIN, 14);
        
        JLabel spindleTypeLabel = new JLabel("Spindle Type:");
        spindleTypeLabel.setFont(inputFont);
        inputPanel.add(spindleTypeLabel);
        spindleTypeCombo = new JComboBox<>(new String[]{"Belt-Driven", "Direct-Drive", "Motorized"});
        spindleTypeCombo.setFont(inputFont);
        inputPanel.add(spindleTypeCombo);
        
        JLabel powerRatingLabel = new JLabel("Power Rating (kW):");
        powerRatingLabel.setFont(inputFont);
        inputPanel.add(powerRatingLabel);
        powerRatingField = new JTextField("5.0");
        powerRatingField.setFont(inputFont);
        inputPanel.add(powerRatingField);
        
        JLabel speedLabel = new JLabel("Max Speed (RPM):");
        speedLabel.setFont(inputFont);
        inputPanel.add(speedLabel);
        speedField = new JTextField("10000");
        speedField.setFont(inputFont);
        inputPanel.add(speedField);
        
        JLabel wheelDiameterLabel = new JLabel("Wheel Diameter (mm):");
        wheelDiameterLabel.setFont(inputFont);
        inputPanel.add(wheelDiameterLabel);
        wheelDiameterField = new JTextField("200");
        wheelDiameterField.setFont(inputFont);
        inputPanel.add(wheelDiameterField);
        
        JLabel bearingTypeLabel = new JLabel("Bearing Type:");
        bearingTypeLabel.setFont(inputFont);
        inputPanel.add(bearingTypeLabel);
        bearingTypeCombo = new JComboBox<>(new String[]{"Angular Contact", "Hybrid Ceramic"});
        bearingTypeCombo.setFont(inputFont);
        inputPanel.add(bearingTypeCombo);
        
        JLabel preloadLabel = new JLabel("Bearing Preload (N):");
        preloadLabel.setFont(inputFont);
        inputPanel.add(preloadLabel);
        preloadField = new JTextField("500");
        preloadField.setFont(inputFont);
        inputPanel.add(preloadField);
        
        JLabel coolingTypeLabel = new JLabel("Cooling Type:");
        coolingTypeLabel.setFont(inputFont);
        inputPanel.add(coolingTypeLabel);
        coolingTypeCombo = new JComboBox<>(new String[]{"Liquid", "Air"});
        coolingTypeCombo.setFont(inputFont);
        inputPanel.add(coolingTypeCombo);
        
        JLabel lubricationTypeLabel = new JLabel("Lubrication Type:");
        lubricationTypeLabel.setFont(inputFont);
        inputPanel.add(lubricationTypeLabel);
        lubricationTypeCombo = new JComboBox<>(new String[]{"Grease", "Oil-Mist", "Oil-Air"});
        lubricationTypeCombo.setFont(inputFont);
        inputPanel.add(lubricationTypeCombo);
        
        JLabel toolInterfaceLabel = new JLabel("Tool Interface:");
        toolInterfaceLabel.setFont(inputFont);
        inputPanel.add(toolInterfaceLabel);
        toolInterfaceCombo = new JComboBox<>(new String[]{"Precision Collet", "Hydraulic Chuck", "HSK"});
        toolInterfaceCombo.setFont(inputFont);
        inputPanel.add(toolInterfaceCombo);
        
        JLabel alignmentToleranceLabel = new JLabel("Alignment Tolerance (mm):");
        alignmentToleranceLabel.setFont(inputFont);
        inputPanel.add(alignmentToleranceLabel);
        alignmentToleranceField = new JTextField("0.001");
        alignmentToleranceField.setFont(inputFont);
        inputPanel.add(alignmentToleranceField);
        
        JLabel timeDurationLabel = new JLabel("Simulation Duration (s):");
        timeDurationLabel.setFont(inputFont);
        inputPanel.add(timeDurationLabel);
        timeDurationField = new JTextField("10");
        timeDurationField.setFont(inputFont);
        inputPanel.add(timeDurationField);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        
        JButton simulateButton = new JButton("Run");
        simulateButton.setFont(buttonFont);
        simulateButton.addActionListener(e -> runSimulation());
        buttonPanel.add(simulateButton);
        
        JButton timeBasedButton = new JButton("Time-Based");
        timeBasedButton.setFont(buttonFont);
        timeBasedButton.addActionListener(e -> runTimeBasedSimulation());
        buttonPanel.add(timeBasedButton);
        
        JButton maintenanceButton = new JButton("Maintenance Schedule");
        maintenanceButton.setFont(buttonFont);
        maintenanceButton.addActionListener(e -> showMaintenanceSchedule());
        buttonPanel.add(maintenanceButton);
        
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(buttonFont);
        resetButton.addActionListener(e -> resetInputs());
        buttonPanel.add(resetButton);
        
        JButton predictButton = new JButton("Predict Maintenance");
        predictButton.setFont(buttonFont);
        predictButton.addActionListener(e -> predictMaintenance());
        buttonPanel.add(predictButton);
        
        // Results Area
        resultsArea = new JTextArea(18, 45);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Simulation Results & Analysis"));
        resultsScrollPane.setPreferredSize(new Dimension(600, 450));
        
        // Visualization Panel
        visualizationPanel = new SpindleVisualizationPanel();
        visualizationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        visualizationPanel.setPreferredSize(new Dimension(600, 450));
        
        // Center Panel with GridBagLayout for balanced sizing
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        centerPanel.add(buttonPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.9;
        centerPanel.add(resultsScrollPane, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(visualizationPanel, gbc);
        
        add(inputPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private String validateInputs() {
        try {
            double powerRating = Double.parseDouble(powerRatingField.getText());
            if (powerRating < 0.5 || powerRating > 50.0) {
                return "Power rating must be between 0.5 and 50 kW";
            }

            int maxSpeed = Integer.parseInt(speedField.getText());
            if (maxSpeed < 1000 || maxSpeed > 30000) {
                return "Max speed must be between 1000 and 30000 RPM";
            }

            double wheelDiameter = Double.parseDouble(wheelDiameterField.getText());
            if (wheelDiameter < 50 || wheelDiameter > 1000) {
                return "Wheel diameter must be between 50 and 1000 mm";
            }

            double bearingPreload = Double.parseDouble(preloadField.getText());
            if (bearingPreload < 100 || bearingPreload > 2000) {
                return "Bearing preload must be between 100 and 2000 N";
            }

            double alignmentTolerance = Double.parseDouble(alignmentToleranceField.getText());
            if (alignmentTolerance < 0.0001 || alignmentTolerance > 0.01) {
                return "Alignment tolerance must be between 0.0001 and 0.01 mm";
            }

            double duration = Double.parseDouble(timeDurationField.getText());
            if (duration <= 0) {
                return "Simulation duration must be positive";
            }

            return "Valid";
        } catch (NumberFormatException ex) {
            return "Please enter valid numerical values for all fields";
        }
    }

    private void runSimulation() {
        String validationResult = validateInputs();
        if (!validationResult.equals("Valid")) {
            JOptionPane.showMessageDialog(this, validationResult, "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SpindleParameters params = new SpindleParameters();
            params.setSpindleType((String) spindleTypeCombo.getSelectedItem());
            params.setPowerRating(Double.parseDouble(powerRatingField.getText()));
            params.setMaxSpeed(Integer.parseInt(speedField.getText()));
            params.setWheelDiameter(Double.parseDouble(wheelDiameterField.getText()));
            params.setBearingType((String) bearingTypeCombo.getSelectedItem());
            params.setBearingPreload(Double.parseDouble(preloadField.getText()));
            params.setCoolingType((String) coolingTypeCombo.getSelectedItem());
            params.setLubricationType((String) lubricationTypeCombo.getSelectedItem());
            params.setToolInterface((String) toolInterfaceCombo.getSelectedItem());
            params.setAlignmentTolerance(Double.parseDouble(alignmentToleranceField.getText()));
            
            String results = simulation.simulate(params);
            resultsArea.setText(results);
            
            // Update visualization with balanced scenario metrics (default)
            List<Double> loadProfile = simulation.generateDynamicLoadProfile(params, 10.0, 1.0);
            double vibration = simulation.estimateVibration(params);
            double temperature = simulation.estimateTemperatureRise(params);
            double power = simulation.calculateRequiredPower(params.getWheelDiameter(), params.getMaxSpeed());
            double load = loadProfile.stream().mapToDouble(Double::doubleValue).average().orElse(simulation.estimateLoad(params));
            double bearingLife = simulation.calculateBearingL10Life(params, loadProfile);
            double spindleLife = simulation.calculateSpindleFatigueLife(params, loadProfile);
            double wheelWear = simulation.calculateWheelWear(params, loadProfile, 10.0);
            double wearVibration = simulation.calculateWearInducedVibration(params, wheelWear);
            int maintenanceNeeded = simulation.predictMaintenance(vibration + wearVibration, temperature + 20.0, load, 
                                                                 bearingLife, spindleLife, wheelWear);
            visualizationPanel.updateVisualization(vibration, temperature, power, load, bearingLife, spindleLife, wheelWear, maintenanceNeeded);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred during simulation: " + ex.getMessage(), 
                "Simulation Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void runTimeBasedSimulation() {
        String validationResult = validateInputs();
        if (!validationResult.equals("Valid")) {
            JOptionPane.showMessageDialog(this, validationResult, "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SpindleParameters params = new SpindleParameters();
            params.setSpindleType((String) spindleTypeCombo.getSelectedItem());
            params.setPowerRating(Double.parseDouble(powerRatingField.getText()));
            params.setMaxSpeed(Integer.parseInt(speedField.getText()));
            params.setWheelDiameter(Double.parseDouble(wheelDiameterField.getText()));
            params.setBearingType((String) bearingTypeCombo.getSelectedItem());
            params.setBearingPreload(Double.parseDouble(preloadField.getText()));
            params.setCoolingType((String) coolingTypeCombo.getSelectedItem());
            params.setLubricationType((String) lubricationTypeCombo.getSelectedItem());
            params.setToolInterface((String) toolInterfaceCombo.getSelectedItem());
            params.setAlignmentTolerance(Double.parseDouble(alignmentToleranceField.getText()));
            
            double duration = Double.parseDouble(timeDurationField.getText());
            
            String results = simulation.simulateTimeBased(params, duration);
            resultsArea.setText(results);
            
            List<Double> loadProfile = simulation.generateDynamicLoadProfile(params, duration, 1.0);
            double finalLoad = loadProfile.get(loadProfile.size() - 1);
            double finalVibration = simulation.estimateVibration(params, finalLoad);
            double finalTemperature = 20.0 + simulation.estimateTemperatureRise(params, finalLoad) * duration / 10.0;
            double finalPower = simulation.calculateRequiredPower(params.getWheelDiameter(), params.getMaxSpeed());
            double bearingLife = simulation.calculateBearingL10Life(params, loadProfile);
            double spindleLife = simulation.calculateSpindleFatigueLife(params, loadProfile);
            double wheelWear = simulation.calculateWheelWear(params, loadProfile, duration);
            double wearVibration = simulation.calculateWearInducedVibration(params, wheelWear);
            int maintenanceNeeded = simulation.predictMaintenance(finalVibration + wearVibration, finalTemperature, finalLoad, 
                                                                 bearingLife, spindleLife, wheelWear);
            visualizationPanel.updateVisualization(finalVibration, finalTemperature, finalPower, finalLoad, 
                                                  bearingLife, spindleLife, wheelWear, maintenanceNeeded);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred during time-based simulation: " + ex.getMessage(), 
                "Simulation Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void predictMaintenance() {
        String validationResult = validateInputs();
        if (!validationResult.equals("Valid")) {
            JOptionPane.showMessageDialog(this, validationResult, "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SpindleParameters params = new SpindleParameters();
            params.setSpindleType((String) spindleTypeCombo.getSelectedItem());
            params.setPowerRating(Double.parseDouble(powerRatingField.getText()));
            params.setMaxSpeed(Integer.parseInt(speedField.getText()));
            params.setWheelDiameter(Double.parseDouble(wheelDiameterField.getText()));
            params.setBearingType((String) bearingTypeCombo.getSelectedItem());
            params.setBearingPreload(Double.parseDouble(preloadField.getText()));
            params.setCoolingType((String) coolingTypeCombo.getSelectedItem());
            params.setLubricationType((String) lubricationTypeCombo.getSelectedItem());
            params.setToolInterface((String) toolInterfaceCombo.getSelectedItem());
            params.setAlignmentTolerance(Double.parseDouble(alignmentToleranceField.getText()));
            
            List<Double> loadProfile = simulation.generateDynamicLoadProfile(params, 1.0, 1.0);
            double vibration = simulation.estimateVibration(params);
            double temperature = simulation.estimateTemperatureRise(params) + 20.0;
            double avgLoad = loadProfile.stream().mapToDouble(Double::doubleValue).average().orElse(simulation.estimateLoad(params));
            double bearingLife = simulation.calculateBearingL10Life(params, loadProfile);
            double spindleLife = simulation.calculateSpindleFatigueLife(params, loadProfile);
            double wheelWear = simulation.calculateWheelWear(params, loadProfile, 1.0);
            double wearVibration = simulation.calculateWearInducedVibration(params, wheelWear);
            double totalVibration = vibration + wearVibration;
            
            int maintenanceNeeded = simulation.predictMaintenance(totalVibration, temperature, avgLoad, 
                                                                 bearingLife, spindleLife, wheelWear);
            
            StringBuilder results = new StringBuilder();
            results.append("=== Maintenance Prediction ===\n\n");
            results.append(String.format("Vibration: %.2f mm/s\n", totalVibration));
            results.append(String.format("Temperature: %.1f°C\n", temperature));
            results.append(String.format("Average Load: %.0f N\n", avgLoad));
            results.append(String.format("Bearing Life: %.0f hours\n", bearingLife));
            results.append(String.format("Spindle Life: %.1f%%\n", spindleLife * 100));
            results.append(String.format("Wheel Wear: %.1f mm\n", wheelWear));
            results.append(maintenanceNeeded == 1 ? 
                "Maintenance Needed: Yes (e.g., bearing replacement, wheel dressing)\n" : 
                "Maintenance Needed: No\n");
            
            resultsArea.setText(results.toString());
            visualizationPanel.updateVisualization(vibration, temperature, 0.0, avgLoad, 
                                                  bearingLife, spindleLife, wheelWear, maintenanceNeeded);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred during maintenance prediction: " + ex.getMessage(), 
                "Prediction Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showMaintenanceSchedule() {
        String validationResult = validateInputs();
        if (!validationResult.equals("Valid")) {
            JOptionPane.showMessageDialog(this, validationResult, "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SpindleParameters params = new SpindleParameters();
            params.setSpindleType((String) spindleTypeCombo.getSelectedItem());
            params.setMaxSpeed(Integer.parseInt(speedField.getText()));
            params.setBearingType((String) bearingTypeCombo.getSelectedItem());
            params.setLubricationType((String) lubricationTypeCombo.getSelectedItem());
            
            String schedule = maintenance.generateSchedule(params);
            resultsArea.setText(schedule);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred while generating maintenance schedule: " + ex.getMessage(), 
                "Maintenance Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetInputs() {
        spindleTypeCombo.setSelectedIndex(0);
        bearingTypeCombo.setSelectedIndex(0);
        coolingTypeCombo.setSelectedIndex(0);
        lubricationTypeCombo.setSelectedIndex(0);
        toolInterfaceCombo.setSelectedIndex(0);
        
        powerRatingField.setText("5.0");
        speedField.setText("10000");
        wheelDiameterField.setText("200");
        preloadField.setText("500");
        alignmentToleranceField.setText("0.001");
        timeDurationField.setText("10");
        
        resultsArea.setText("");
        visualizationPanel.reset();
    }
}