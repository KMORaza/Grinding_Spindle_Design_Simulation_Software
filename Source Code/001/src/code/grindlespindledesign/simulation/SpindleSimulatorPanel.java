package code.grindlespindledesign.simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SpindleSimulatorPanel extends JPanel {
    private JComboBox<String> spindleTypeCombo, bearingTypeCombo, coolingTypeCombo, lubricationTypeCombo, toolInterfaceCombo;
    private JTextField powerRatingField, speedField, wheelDiameterField, preloadField, alignmentToleranceField;
    private JTextArea resultsArea;
    private SpindleSimulation simulation;
    private SpindleVisualizationPanel visualizationPanel;
    private SpindleMaintenance maintenance;

    public SpindleSimulatorPanel() {
        setLayout(new BorderLayout(10, 10));
        simulation = new SpindleSimulation();
        maintenance = new SpindleMaintenance();
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(10, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Spindle Configuration"));
        inputPanel.setPreferredSize(new Dimension(450, 350));
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
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font buttonFont = new Font("Noto Sans Emoji", Font.BOLD, 14);
        Font maintenanceButtonFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
        
        
        JButton simulateButton = new JButton("▶");
        simulateButton.setFont(buttonFont);
        simulateButton.addActionListener(e -> runSimulation());
        buttonPanel.add(simulateButton);
        
        JButton maintenanceButton = new JButton("🛠 Maintenance Schedule");
        maintenanceButton.setFont(maintenanceButtonFont);
        maintenanceButton.addActionListener(e -> showMaintenanceSchedule());
        buttonPanel.add(maintenanceButton);
        
        // Results Area
        resultsArea = new JTextArea(18, 45);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Simulation Results & Analysis"));
        resultsScrollPane.setPreferredSize(new Dimension(600, 450));
        
        // Visualization Panel
        visualizationPanel = new SpindleVisualizationPanel();
        visualizationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Removed titled border
        visualizationPanel.setPreferredSize(new Dimension(600, 450));
        
        // Center Panel with GridBagLayout for balanced sizing
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Add button panel at top
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        centerPanel.add(buttonPanel, gbc);
        
        // Add results and visualization side by side
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.9;
        centerPanel.add(resultsScrollPane, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(visualizationPanel, gbc);
        
        // Main layout
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
            
            // Update visualization
            double vibration = simulation.estimateVibration(params);
            double temperature = simulation.estimateTemperatureRise(params);
            double power = simulation.calculateRequiredPower(params.getWheelDiameter(), params.getMaxSpeed());
            double load = simulation.estimateLoad(params);
            visualizationPanel.updateVisualization(vibration, temperature, power, load);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numerical values", 
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
}