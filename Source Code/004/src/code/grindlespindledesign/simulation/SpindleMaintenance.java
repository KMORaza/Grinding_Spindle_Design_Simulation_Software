package code.grindlespindledesign.simulation;

public class SpindleMaintenance {
    public String generateSchedule(SpindleParameters params) {
        StringBuilder schedule = new StringBuilder();
        
        schedule.append("=== Spindle Maintenance Schedule ===\n\n");
        
        // Bearing Inspection
        int bearingInterval = params.getBearingType().equals("Hybrid Ceramic") ? 2000 : 1500; // Hours
        schedule.append(String.format("Bearing Inspection: Every %d operating hours\n", bearingInterval));
        schedule.append(" - Check for wear, preload, and runout\n");
        schedule.append(" - Verify ABEC 7 precision standards\n\n");
        
        // Lubrication Maintenance
        String lubricationType = params.getLubricationType();
        schedule.append("Lubrication Maintenance:\n");
        if (lubricationType.equals("Grease")) {
            schedule.append(" - Replace grease every 1000 hours\n");
        } else if (lubricationType.equals("Oil-Mist")) {
            schedule.append(" - Check oil-mist system every 500 hours\n");
        } else {
            schedule.append(" - Monitor oil-air system every 300 hours\n");
        }
        schedule.append(" - Ensure no contamination in lubricant\n\n");
        
        // Vibration Monitoring
        schedule.append("Vibration Monitoring:\n");
        schedule.append(" - Install vibration sensors for continuous monitoring\n");
        schedule.append(" - Check for anomalies every 100 hours\n");
        schedule.append(" - Maintain ISO 1940 G1 balance grade\n\n");
        
        // Alignment Check
        schedule.append("Alignment Check:\n");
        schedule.append(" - Verify alignment with laser tools every 500 hours\n");
        schedule.append(" - Ensure concentricity and parallelism\n\n");
        
        // General Maintenance
        schedule.append("General Maintenance:\n");
        schedule.append(" - Inspect spindle housing for cracks every 2000 hours\n");
        schedule.append(" - Dress grinding wheel every 50 hours to maintain geometry\n");
        schedule.append(" - Log performance trends for predictive maintenance\n");
        
        return schedule.toString();
    }
}