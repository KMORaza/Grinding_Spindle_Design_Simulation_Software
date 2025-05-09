package code.grindlespindledesign.simulation;
public class SpindleSimulation {
    public String simulate(SpindleParameters params) {
        StringBuilder results = new StringBuilder();
        
        results.append("=== Advanced Spindle Simulation Results ===\n\n");
        
        // Spindle Type Analysis
        results.append("Spindle Type: ").append(params.getSpindleType()).append("\n");
        results.append(evaluateSpindleType(params)).append("\n");
        
        // Power Rating Check
        double requiredPower = calculateRequiredPower(params.getWheelDiameter(), params.getMaxSpeed());
        results.append(String.format("Power Analysis: %.2f kW required, %.2f kW provided\n", 
            requiredPower, params.getPowerRating()));
        results.append(requiredPower <= params.getPowerRating() ? 
            "Power rating sufficient\n" : 
            "Warning: Power rating may be insufficient\n");
        
        // Bearing Analysis
        results.append("\nBearing Analysis:\n");
        results.append(evaluateBearingPerformance(params)).append("\n");
        results.append(String.format("Bearing Preload: %.0f N\n", params.getBearingPreload()));
        results.append(params.getBearingPreload() >= 300 && params.getBearingPreload() <= 1000 ? 
            "Preload within optimal range\n" : 
            "Warning: Preload may cause excessive heat or play\n");
        
        // Thermal Analysis
        results.append("\nThermal Analysis:\n");
        double tempRise = estimateTemperatureRise(params);
        double thermalExpansion = calculateThermalExpansion(tempRise);
        results.append(String.format("Estimated temperature rise: %.1f°C\n", tempRise));
        results.append(String.format("Thermal expansion: %.4f mm\n", thermalExpansion));
        results.append(tempRise <= 30 ? 
            "Thermal performance acceptable\n" : 
            "Warning: Potential thermal issues\n");
        
        // Vibration Analysis
        results.append("\nVibration Analysis:\n");
        double vibrationLevel = estimateVibration(params);
        double resonanceFreq = calculateResonanceFrequency(params);
        results.append(String.format("Estimated vibration level: %.2f mm/s\n", vibrationLevel));
        results.append(String.format("Resonance frequency: %.0f Hz\n", resonanceFreq));
        results.append(vibrationLevel <= 1.0 ? 
            "Vibration within ISO 1940 G1 standards\n" : 
            "Warning: Excessive vibration predicted\n");
        
        // Alignment Analysis
        results.append("\nAlignment Analysis:\n");
        results.append(String.format("Alignment tolerance: %.4f mm\n", params.getAlignmentTolerance()));
        results.append(params.getAlignmentTolerance() <= 0.002 ? 
            "Alignment within specifications\n" : 
            "Warning: Alignment may cause chatter marks\n");
        
        // Tool Interface Analysis
        results.append("\nTool Interface Analysis:\n");
        results.append("Tool Interface: ").append(params.getToolInterface()).append("\n");
        results.append(params.getToolInterface().equals("HSK") && params.getMaxSpeed() > 10000 ? 
            "HSK interface optimal for high-speed operation\n" : 
            "Tool interface suitable for specified parameters\n");
        
        // Trial Run Simulation
        results.append("\nTrial Run Simulation:\n");
        results.append(simulateTrialRuns(params));
        
        return results.toString();
    }
    
    private String evaluateSpindleType(SpindleParameters params) {
        String type = params.getSpindleType();
        if (type.equals("Motorized") && params.getMaxSpeed() > 15000) {
            return "Motorized spindle optimal for high-speed precision grinding";
        } else if (type.equals("Belt-Driven") && params.getMaxSpeed() <= 8000) {
            return "Belt-driven spindle cost-effective for high-torque applications";
        } else if (type.equals("Direct-Drive")) {
            return "Direct-drive spindle balances speed and torque effectively";
        }
        return "Spindle type may not be optimal for specified parameters";
    }
    
    public double calculateRequiredPower(double wheelDiameter, int speed) {
        // Enhanced power calculation with material and wheel factors
        double materialFactor = 1.2; // Assume medium-hard material
        return (wheelDiameter / 1000.0) * (speed / 1000.0) * 2.5 * materialFactor;
    }
    
    private String evaluateBearingPerformance(SpindleParameters params) {
        if (params.getBearingType().equals("Hybrid Ceramic") && params.getMaxSpeed() > 10000) {
            return "Hybrid ceramic bearings optimal for high-speed, low-friction operation";
        } else if (params.getBearingType().equals("Angular Contact")) {
            return "Angular contact bearings provide excellent rigidity for medium speeds";
        }
        return "Bearing type may need review for optimal performance";
    }
    
    public double estimateTemperatureRise(SpindleParameters params) {
        // Enhanced thermal model with preload and lubrication effects
        double baseTemp = params.getCoolingType().equals("Liquid") ? 18.0 : 22.0;
        double speedFactor = params.getMaxSpeed() / 10000.0;
        double preloadFactor = params.getBearingPreload() / 500.0;
        return baseTemp + (speedFactor * 5.0) + (preloadFactor * 2.0);
    }
    
    private double calculateThermalExpansion(double tempRise) {
        // Simplified thermal expansion for steel shaft (coefficient ~12e-6 /°C)
        double shaftLength = 0.2; // Assume 200 mm shaft
        double thermalCoefficient = 12e-6;
        return shaftLength * thermalCoefficient * tempRise;
    }
    
    public double estimateVibration(SpindleParameters params) {
        // Enhanced vibration model with alignment and tool interface
        double baseVibration = params.getBearingType().equals("Hybrid Ceramic") ? 0.4 : 0.6;
        double speedFactor = params.getMaxSpeed() / 10000.0;
        double alignmentFactor = params.getAlignmentTolerance() > 0.002 ? 1.2 : 1.0;
        double toolFactor = params.getToolInterface().equals("HSK") ? 0.9 : 1.0;
        return baseVibration * speedFactor * alignmentFactor * toolFactor;
    }
    
    private double calculateResonanceFrequency(SpindleParameters params) {
        // Simplified FEA-based resonance calculation
        double stiffness = params.getBearingType().equals("Hybrid Ceramic") ? 1.5e8 : 1.2e8; // N/m
        double mass = params.getWheelDiameter() / 1000.0 * 2.0; // kg
        return Math.sqrt(stiffness / mass) / (2 * Math.PI);
    }
    
    public double estimateLoad(SpindleParameters params) {
        // Estimate radial load based on wheel and speed
        return (params.getWheelDiameter() / 1000.0) * (params.getMaxSpeed() / 1000.0) * 100.0;
    }
    
    private String simulateTrialRuns(SpindleParameters params) {
        StringBuilder trials = new StringBuilder();
        int[] speeds = {params.getMaxSpeed() / 2, params.getMaxSpeed() * 3 / 4, params.getMaxSpeed()};
        for (int speed : speeds) {
            double power = calculateRequiredPower(params.getWheelDiameter(), speed);
            double vibration = estimateVibration(new SpindleParameters() {{
                setBearingType(params.getBearingType());
                setMaxSpeed(speed);
                setAlignmentTolerance(params.getAlignmentTolerance());
                setToolInterface(params.getToolInterface());
            }});
            trials.append(String.format("Trial at %d RPM: Power = %.2f kW, Vibration = %.2f mm/s\n", 
                speed, power, vibration));
        }
        return trials.toString();
    }
}