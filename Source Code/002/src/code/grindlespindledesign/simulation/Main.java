package code.grindlespindledesign.simulation;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set Windows Classic Look and Feel
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            } catch (Exception e) {
                System.err.println("Failed to set Windows Classic Look and Feel: " + e.getMessage());
                // Fallback to default look and feel if Windows Classic is unavailable
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JFrame frame = new JFrame("Grinding Spindle Design Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 410); // Increased size to accommodate larger panels
            frame.setLocationRelativeTo(null);
            
            SpindleSimulatorPanel simulatorPanel = new SpindleSimulatorPanel();
            frame.add(simulatorPanel);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}