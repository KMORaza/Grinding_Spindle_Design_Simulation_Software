package code.grindlespindledesign.simulation;

import java.util.ArrayList;
import java.util.List;

public class SpindleSimulation {
    public String simulate(SpindleParameters params) {
        StringBuilder results = new StringBuilder();
        
        results.append("=== Advanced Spindle Simulation Results ===\n\n");
        
        results.append("Spindle Type: ").append(params.getSpindleType()).append("\n");
        results.append(evaluateSpindleType(params)).append("\n");
        
        double requiredPower = calculateRequiredPower(params.getWheelDiameter(), params.getMaxSpeed());
        results.append(String.format("Power Analysis: %.2f kW required, %.2f kW provided\n", 
            requiredPower, params.getPowerRating()));
        results.append(requiredPower <= params.getPowerRating() ? 
            "Power rating sufficient\n" : 
            "Warning: Power rating may be insufficient\n");
        
        results.append("\nBearing Analysis:\n");
        results.append(evaluateBearingPerformance(params)).append("\n");
        results.append(String.format("Bearing Preload: %.0f N\n", params.getBearingPreload()));
        results.append(params.getBearingPreload() >= 300 && params.getBearingPreload() <= 1000 ? 
            "Preload within optimal range\n" : 
            "Warning: Preload may cause excessive heat or play\n");
        
        results.append("\nThermal Analysis:\n");
        double tempRise = estimateTemperatureRise(params);
        double thermalExpansion = calculateThermalExpansion(tempRise);
        results.append(String.format("Estimated temperature rise: %.1f°C\n", tempRise));
        results.append(String.format("Thermal expansion: %.4f mm\n", thermalExpansion));
        results.append(tempRise <= 30 ? 
            "Thermal performance acceptable\n" : 
            "Warning: Potential thermal issues\n");
        
        results.append("\nVibration Analysis:\n");
        double vibrationLevel = estimateVibration(params);
        double resonanceFreq = calculateResonanceFrequency(params);
        results.append(String.format("Estimated vibration level: %.2f mm/s\n", vibrationLevel));
        results.append(String.format("Resonance frequency: %.0f Hz\n", resonanceFreq));
        results.append(vibrationLevel <= 1.0 ? 
            "Vibration within ISO 1940 G1 standards\n" : 
            "Warning: Excessive vibration predicted\n");
        
        results.append("\nAlignment Analysis:\n");
        results.append(String.format("Alignment tolerance: %.4f mm\n", params.getAlignmentTolerance()));
        results.append(params.getAlignmentTolerance() <= 0.002 ? 
            "Alignment within specifications\n" : 
            "Warning: Alignment may cause chatter marks\n");
        
        results.append("\nTool Interface Analysis:\n");
        results.append("Tool Interface: ").append(params.getToolInterface()).append("\n");
        results.append(params.getToolInterface().equals("HSK") && params.getMaxSpeed() > 10000 ? 
            "HSK interface optimal for high-speed operation\n" : 
            "Tool interface suitable for specified parameters\n");
        
        results.append("\nDynamic Load Profile:\n");
        List<Double> loadProfile = generateDynamicLoadProfile(params, 10);
        results.append("Dynamic Load (N) over 10 seconds:\n");
        for (int i = 0; i < loadProfile.size(); i++) {
            results.append(String.format("t=%.1f s: %.0f N\n", i * 0.1, loadProfile.get(i)));
        }
        
        results.append("\nFatigue Analysis:\n");
        double bearingLifeHours = calculateBearingL10Life(params, loadProfile);
        results.append(String.format("Bearing L10 Life: %.0f hours\n", bearingLifeHours));
        results.append(bearingLifeHours >= 20000 ? 
            "Bearing life acceptable\n" : 
            "Warning: Short bearing life predicted\n");
        
        double spindleLifePercentage = calculateSpindleFatigueLife(params, loadProfile);
        results.append(String.format("Spindle Shaft Remaining Life: %.1f%%\n", spindleLifePercentage * 100));
        results.append(spindleLifePercentage >= 0.5 ? 
            "Spindle shaft life acceptable\n" : 
            "Warning: Spindle shaft may fail prematurely\n");
        
        results.append("\nGrinding Wheel Wear Analysis:\n");
        double initialDiameter = params.getWheelDiameter();
        double wear = calculateWheelWear(params, loadProfile, 10);
        double remainingDiameter = initialDiameter - wear;
        double wearVibration = calculateWearInducedVibration(params, wear);
        results.append(String.format("Initial Wheel Diameter: %.1f mm\n", initialDiameter));
        results.append(String.format("Remaining Wheel Diameter: %.1f mm\n", remainingDiameter));
        results.append(String.format("Wear-Induced Vibration: %.2f mm/s\n", wearVibration));
        results.append(remainingDiameter >= initialDiameter * 0.8 ? 
            "Wheel condition acceptable\n" : 
            "Warning: Excessive wheel wear detected\n");
        results.append(wearVibration <= 0.5 ? 
            "Wear-induced vibration within limits\n" : 
            "Warning: Increased vibration due to wheel imbalance\n");
        
        results.append("\nTrial Run Simulation:\n");
        results.append(simulateTrialRuns(params));
        
        return results.toString();
    }
    
    public String simulateTimeBased(SpindleParameters params, double duration) {
        StringBuilder results = new StringBuilder();
        results.append(String.format("=== Time-Based Spindle Simulation (Duration: %.1f s) ===\n\n", duration));
        
        double timeStep = 0.1;
        int steps = (int) (duration / timeStep);
        List<Double> vibrationHistory = new ArrayList<>();
        List<Double> temperatureHistory = new ArrayList<>();
        List<Double> loadProfile = generateDynamicLoadProfile(params, duration);
        
        double currentTemp = 20.0;
        for (int i = 0; i < steps; i++) {
            double load = loadProfile.get(i);
            double vibration = estimateVibration(params, load);
            currentTemp += estimateTemperatureRise(params, load) * timeStep / 10.0;
            vibrationHistory.add(vibration);
            temperatureHistory.add(currentTemp);
            
            if (i % 10 == 0) {
                results.append(String.format("t=%.1f s: Vibration=%.2f mm/s, Temperature=%.1f°C, Load=%.0f N\n",
                    i * timeStep, vibration, currentTemp, load));
            }
        }
        
        double avgVibration = vibrationHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double maxVibration = vibrationHistory.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double avgTemp = temperatureHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double maxTemp = temperatureHistory.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        
        results.append("\nSummary:\n");
        results.append(String.format("Average Vibration: %.2f mm/s\n", avgVibration));
        results.append(String.format("Maximum Vibration: %.2f mm/s\n", maxVibration));
        results.append(String.format("Average Temperature: %.1f°C\n", avgTemp));
        results.append(String.format("Maximum Temperature: %.1f°C\n", maxTemp));
        
        results.append("\nFatigue Analysis:\n");
        double bearingLifeHours = calculateBearingL10Life(params, loadProfile);
        results.append(String.format("Bearing L10 Life: %.0f hours\n", bearingLifeHours));
        results.append(bearingLifeHours >= 20000 ? 
            "Bearing life acceptable\n" : 
            "Warning: Short bearing life predicted\n");
        
        double spindleLifePercentage = calculateSpindleFatigueLife(params, loadProfile);
        results.append(String.format("Spindle Shaft Remaining Life: %.1f%%\n", spindleLifePercentage * 100));
        results.append(spindleLifePercentage >= 0.5 ? 
            "Spindle shaft life acceptable\n" : 
            "Warning: Spindle shaft may fail prematurely\n");
        
        results.append("\nGrinding Wheel Wear Analysis:\n");
        double initialDiameter = params.getWheelDiameter();
        double wear = calculateWheelWear(params, loadProfile, duration);
        double remainingDiameter = initialDiameter - wear;
        double wearVibration = calculateWearInducedVibration(params, wear);
        results.append(String.format("Initial Wheel Diameter: %.1f mm\n", initialDiameter));
        results.append(String.format("Remaining Wheel Diameter: %.1f mm\n", remainingDiameter));
        results.append(String.format("Wear-Induced Vibration: %.2f mm/s\n", wearVibration));
        results.append(remainingDiameter >= initialDiameter * 0.8 ? 
            "Wheel condition acceptable\n" : 
            "Warning: Excessive wheel wear detected\n");
        results.append(wearVibration <= 0.5 ? 
            "Wear-induced vibration within limits\n" : 
            "Warning: Increased vibration due to wheel imbalance\n");
        
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
        double materialFactor = 1.2;
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
        double baseTemp = params.getCoolingType().equals("Liquid") ? 18.0 : 22.0;
        double speedFactor = params.getMaxSpeed() / 10000.0;
        double preloadFactor = params.getBearingPreload() / 500.0;
        return baseTemp + (speedFactor * 5.0) + (preloadFactor * 2.0);
    }
    
    public double estimateTemperatureRise(SpindleParameters params, double load) {
        double baseTemp = params.getCoolingType().equals("Liquid") ? 18.0 : 22.0;
        double speedFactor = params.getMaxSpeed() / 10000.0;
        double preloadFactor = params.getBearingPreload() / 500.0;
        double loadFactor = load / 1000.0;
        return baseTemp + (speedFactor * 5.0) + (preloadFactor * 2.0) + (loadFactor * 2.0);
    }
    
    private double calculateThermalExpansion(double tempRise) {
        double shaftLength = 0.2;
        double thermalCoefficient = 12e-6;
        return shaftLength * thermalCoefficient * tempRise;
    }
    
    public double estimateVibration(SpindleParameters params) {
        double baseVibration = params.getBearingType().equals("Hybrid Ceramic") ? 0.4 : 0.6;
        double speedFactor = params.getMaxSpeed() / 10000.0;
        double alignmentFactor = params.getAlignmentTolerance() > 0.002 ? 1.2 : 1.0;
        double toolFactor = params.getToolInterface().equals("HSK") ? 0.9 : 1.0;
        return baseVibration * speedFactor * alignmentFactor * toolFactor;
    }
    
    public double estimateVibration(SpindleParameters params, double load) {
        double baseVibration = params.getBearingType().equals("Hybrid Ceramic") ? 0.4 : 0.6;
        double speedFactor = params.getMaxSpeed() / 10000.0;
        double alignmentFactor = params.getAlignmentTolerance() > 0.002 ? 1.2 : 1.0;
        double toolFactor = params.getToolInterface().equals("HSK") ? 0.9 : 1.0;
        double loadFactor = 1.0 + (load / 1000.0) * 0.5;
        return baseVibration * speedFactor * alignmentFactor * toolFactor * loadFactor;
    }
    
    private double calculateResonanceFrequency(SpindleParameters params) {
        double stiffness = params.getBearingType().equals("Hybrid Ceramic") ? 1.5e8 : 1.2e8;
        double mass = params.getWheelDiameter() / 1000.0 * 2.0;
        return Math.sqrt(stiffness / mass) / (2 * Math.PI);
    }
    
    public double estimateLoad(SpindleParameters params) {
        return (params.getWheelDiameter() / 1000.0) * (params.getMaxSpeed() / 1000.0) * 100.0;
    }
    
    public List<Double> generateDynamicLoadProfile(SpindleParameters params, double duration) {
        List<Double> loadProfile = new ArrayList<>();
        double baseLoad = estimateLoad(params);
        double timeStep = 0.1;
        int steps = (int) (duration / timeStep);
        
        for (int i = 0; i < steps; i++) {
            double time = i * timeStep;
            double variation = Math.sin(2 * Math.PI * time / 2.0) * 0.3;
            double load = baseLoad * (1.0 + variation);
            if (Math.random() < 0.1) {
                load *= 1.5;
            }
            loadProfile.add(Math.max(0, load));
        }
        return loadProfile;
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
    
    public double calculateBearingL10Life(SpindleParameters params, List<Double> loadProfile) {
        double C = params.getBearingType().equals("Hybrid Ceramic") ? 50.0 : 40.0;
        double avgLoad = loadProfile.stream().mapToDouble(Double::doubleValue).average().orElse(estimateLoad(params));
        double P = (avgLoad + params.getBearingPreload()) / 1000.0;
        double lifeAdjustmentFactor = 1.0;
        if (params.getLubricationType().equals("Grease")) {
            lifeAdjustmentFactor *= 0.8;
        } else if (params.getLubricationType().equals("Oil-Air")) {
            lifeAdjustmentFactor *= 1.2;
        }
        if (params.getCoolingType().equals("Liquid")) {
            lifeAdjustmentFactor *= 1.1;
        }
        double L10 = Math.pow(C / P, 3) * 1_000_000;
        double L10h = L10 / (60.0 * params.getMaxSpeed()) * lifeAdjustmentFactor;
        return Math.max(1000, L10h);
    }
    
    public double calculateSpindleFatigueLife(SpindleParameters params, List<Double> loadProfile) {
        double a = 20.0;
        double b = 6.0;
        double ultimateStrength = 800e6;
        double shaftDiameter = 0.05;
        double sectionModulus = Math.PI * Math.pow(shaftDiameter, 3) / 32;
        double totalDamage = 0.0;
        
        for (double load : loadProfile) {
            double moment = load * 0.1;
            double stress = moment / sectionModulus;
            double logN = a - b * Math.log10(stress / 1e6);
            double N = Math.pow(10, logN);
            double cycles = 1.0;
            totalDamage += cycles / N;
        }
        
        double remainingLife = 1.0 - totalDamage;
        return Math.max(0.0, Math.min(1.0, remainingLife));
    }
    
    public double calculateWheelWear(SpindleParameters params, List<Double> loadProfile, double duration) {
        double wearCoefficient = 1e-6; // mm³/(N·m)
        double wheelDiameter = params.getWheelDiameter() / 1000.0; // m
        double wheelThickness = 0.02; // m
        double avgLoad = loadProfile.stream().mapToDouble(Double::doubleValue).average().orElse(estimateLoad(params));
        double peripheralSpeed = Math.PI * wheelDiameter * params.getMaxSpeed() / 60.0; // m/s
        double slidingDistance = peripheralSpeed * duration; // m
        double wearVolume = wearCoefficient * avgLoad * slidingDistance; // mm³
        double diameterReduction = wearVolume / (Math.PI * wheelDiameter * wheelThickness * 1000.0); // mm
        return Math.min(diameterReduction, params.getWheelDiameter() * 0.2); // Cap at 20% reduction
    }
    
    public double calculateWearInducedVibration(SpindleParameters params, double wear) {
        double wheelDiameter = params.getWheelDiameter() / 1000.0; // m
        double wheelThickness = 0.02; // m
        double density = 2500.0; // kg/m³
        double wearVolume = wear * Math.PI * wheelDiameter * wheelThickness * 1000.0; // mm³
        double imbalanceMass = density * wearVolume * 1e-9; // kg
        double wheelMass = density * Math.PI * Math.pow(wheelDiameter / 2, 2) * wheelThickness; // kg
        double eccentricity = (imbalanceMass * (wheelDiameter / 2)) / wheelMass; // m
        double omega = 2 * Math.PI * params.getMaxSpeed() / 60.0; // rad/s
        double imbalanceForce = imbalanceMass * Math.pow(omega, 2) * eccentricity; // N
        double systemStiffness = 1e8; // N/m
        double vibrationAmplitude = imbalanceForce / systemStiffness * 1000.0; // mm/s
        return Math.min(vibrationAmplitude, 2.0); // Cap at 2 mm/s
    }
}