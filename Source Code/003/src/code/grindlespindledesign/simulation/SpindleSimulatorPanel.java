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
    
    private void runSimulation() {
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
            
            double vibration = simulation.estimateVibration(params);
            double temperature = simulation.estimateTemperatureRise(params);
            double power = simulation.calculateRequiredPower(params.getWheelDiameter(), params.getMaxSpeed());
            double load = simulation.estimateLoad(params);
            List<Double> loadProfile = simulation.generateDynamicLoadProfile(params, 10);
            double bearingLife = simulation.calculateBearingL10Life(params, loadProfile);
            double spindleLife = simulation.calculateSpindleFatigueLife(params, loadProfile);
            double wheelWear = simulation.calculateWheelWear(params, loadProfile, 10);
            double wearVibration = simulation.calculateWearInducedVibration(params, wheelWear);
            visualizationPanel.updateVisualization(vibration, temperature, power, load, bearingLife, spindleLife, wheelWear);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numerical values", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void runTimeBasedSimulation() {
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
            if (duration <= 0) {
                throw new NumberFormatException("Duration must be positive");
            }
            
            String results = simulation.simulateTimeBased(params, duration);
            resultsArea.setText(results);
            
            List<Double> loadProfile = simulation.generateDynamicLoadProfile(params, duration);
            double finalLoad = loadProfile.get(loadProfile.size() - 1);
            double finalVibration = simulation.estimateVibration(params, finalLoad);
            double finalTemperature = 20.0 + simulation.estimateTemperatureRise(params, finalLoad) * duration / 10.0;
            double finalPower = simulation.calculateRequiredPower(params.getWheelDiameter(), params.getMaxSpeed());
            double bearingLife = simulation.calculateBearingL10Life(params, loadProfile);
            double spindleLife = simulation.calculateSpindleFatigueLife(params, loadProfile);
            double wheelWear = simulation.calculateWheelWear(params, loadProfile, duration);
            double wearVibration = simulation.calculateWearInducedVibration(params, wheelWear);
            visualizationPanel.updateVisualization(finalVibration, finalTemperature, finalPower, finalLoad, bearingLife, spindleLife, wheelWear);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numerical values for all fields", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showMaintenanceSchedule() {
        try {
            SpindleParameters params = new SpindleParameters();
            params.setSpindleType((String) spindleTypeCombo.getSelectedItem());
            params.setMaxSpeed(Integer.parseInt(speedField.getText()));
            params.setBearingType((String) bearingTypeCombo.getSelectedItem());
            params.setLubricationType((String) lubricationTypeCombo.getSelectedItem());
            
            String schedule = maintenance.generateSchedule(params);
            resultsArea.setText(schedule);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numerical values", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetInputs() {
        // Reset combo boxes to first item
        spindleTypeCombo.setSelectedIndex(0);
        bearingTypeCombo.setSelectedIndex(0);
        coolingTypeCombo.setSelectedIndex(0);
        lubricationTypeCombo.setSelectedIndex(0);
        toolInterfaceCombo.setSelectedIndex(0);
        
        // Reset text fields to default values
        powerRatingField.setText("5.0");
        speedField.setText("10000");
        wheelDiameterField.setText("200");
        preloadField.setText("500");
        alignmentToleranceField.setText("0.001");
        timeDurationField.setText("10");
        
        // Clear results area
        resultsArea.setText("");
        
        // Reset visualization panel
        visualizationPanel.reset();
    }
}