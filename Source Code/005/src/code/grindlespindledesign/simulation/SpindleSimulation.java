package code.grindlespindledesign.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpindleSimulation {
    private static List<DataPoint> historicalData = new ArrayList<>();
    private static final Random random = new Random();

    // DataPoint class to store feature vectors and labels
    private static class DataPoint {
        double vibration;
        double temperature;
        double load;
        double bearingLife;
        double spindleLife;
        double wheelWear;
        int label; // 1 = maintenance needed, 0 = no maintenance

        DataPoint(double vibration, double temperature, double load, double bearingLife, 
                 double spindleLife, double wheelWear, int label) {
            this.vibration = vibration;
            this.temperature = temperature;
            this.load = load;
            this.bearingLife = bearingLife;
            this.spindleLife = spindleLife;
            this.wheelWear = wheelWear;
            this.label = label;
        }
    }

    // Simulation Scenario class to define test cases
    private static class SimulationScenario {
        String name;
        double speedFactor; // Multiplier for maxSpeed (e.g., 1.0 = maxSpeed, 0.5 = half maxSpeed)
        double loadFactor;  // Multiplier for estimated load
        double duration;    // Simulation duration in seconds

        SimulationScenario(String name, double speedFactor, double loadFactor, double duration) {
            this.name = name;
            this.speedFactor = speedFactor;
            this.loadFactor = loadFactor;
            this.duration = duration;
        }
    }

    public String simulate(SpindleParameters params) {
        // Validate parameters
        String validationResult = validateParameters(params);
        if (!validationResult.equals("Valid")) {
            return validationResult;
        }

        // Define scenarios
        List<SimulationScenario> scenarios = new ArrayList<>();
        scenarios.add(new SimulationScenario("High-Speed", 1.0, 0.8, 10.0));
        scenarios.add(new SimulationScenario("High-Torque", 0.6, 1.2, 10.0));
        scenarios.add(new SimulationScenario("Balanced", 0.8, 1.0, 10.0));

        StringBuilder comprehensiveReport = new StringBuilder();
        comprehensiveReport.append("=== Systematic Spindle Simulation Results ===\n\n");

        for (SimulationScenario scenario : scenarios) {
            comprehensiveReport.append(runSimulationStage(params, scenario));
        }

        comprehensiveReport.append(generateComprehensiveReport(params, scenarios));
        return comprehensiveReport.toString();
    }

    private String runSimulationStage(SpindleParameters params, SimulationScenario scenario) {
        StringBuilder results = new StringBuilder();
        results.append(String.format("=== Scenario: %s ===\n\n", scenario.name));

        // Adjust parameters for scenario
        SpindleParameters adjustedParams = new SpindleParameters();
        adjustedParams.setSpindleType(params.getSpindleType());
        adjustedParams.setPowerRating(params.getPowerRating());
        adjustedParams.setMaxSpeed((int) (params.getMaxSpeed() * scenario.speedFactor));
        adjustedParams.setWheelDiameter(params.getWheelDiameter());
        adjustedParams.setBearingType(params.getBearingType());
        adjustedParams.setBearingPreload(params.getBearingPreload());
        adjustedParams.setCoolingType(params.getCoolingType());
        adjustedParams.setLubricationType(params.getLubricationType());
        adjustedParams.setToolInterface(params.getToolInterface());
        adjustedParams.setAlignmentTolerance(params.getAlignmentTolerance());

        // Power Analysis
        double requiredPower = calculateRequiredPower(adjustedParams.getWheelDiameter(), adjustedParams.getMaxSpeed());
        results.append(String.format("Power Analysis: %.2f kW required, %.2f kW provided\n", 
            requiredPower, adjustedParams.getPowerRating()));
        results.append(requiredPower <= adjustedParams.getPowerRating() ? 
            "Power rating sufficient\n" : 
            "Warning: Power rating may be insufficient\n");

        // Bearing Analysis
        results.append("\nBearing Analysis:\n");
        results.append(evaluateBearingPerformance(adjustedParams)).append("\n");
        results.append(String.format("Bearing Preload: %.0f N\n", adjustedParams.getBearingPreload()));
        results.append(adjustedParams.getBearingPreload() >= 300 && adjustedParams.getBearingPreload() <= 1000 ? 
            "Preload within optimal range\n" : 
            "Warning: Preload may cause excessive heat or play\n");

        // Thermal Analysis
        results.append("\nThermal Analysis:\n");
        double tempRise = estimateTemperatureRise(adjustedParams);
        double thermalExpansion = calculateThermalExpansion(tempRise);
        results.append(String.format("Estimated temperature rise: %.1f°C\n", tempRise));
        results.append(String.format("Thermal expansion: %.4f mm\n", thermalExpansion));
        results.append(tempRise <= 30 ? 
            "Thermal performance acceptable\n" : 
            "Warning: Potential thermal issues\n");

        // Vibration Analysis
        results.append("\nVibration Analysis:\n");
        double vibrationLevel = estimateVibration(adjustedParams);
        double resonanceFreq = calculateResonanceFrequency(adjustedParams);
        results.append(String.format("Estimated vibration level: %.2f mm/s\n", vibrationLevel));
        results.append(String.format("Resonance frequency: %.0f Hz\n", resonanceFreq));
        results.append(vibrationLevel <= 1.0 ? 
            "Vibration within ISO 1940 G1 standards\n" : 
            "Warning: Excessive vibration predicted\n");

        // Alignment Analysis
        results.append("\nAlignment Analysis:\n");
        results.append(String.format("Alignment tolerance: %.4f mm\n", adjustedParams.getAlignmentTolerance()));
        results.append(adjustedParams.getAlignmentTolerance() <= 0.002 ? 
            "Alignment within specifications\n" : 
            "Warning: Alignment may cause chatter marks\n");

        // Tool Interface Analysis
        results.append("\nTool Interface Analysis:\n");
        results.append("Tool Interface: ").append(adjustedParams.getToolInterface()).append("\n");
        results.append(adjustedParams.getToolInterface().equals("HSK") && adjustedParams.getMaxSpeed() > 10000 ? 
            "HSK interface optimal for high-speed operation\n" : 
            "Tool interface suitable for specified parameters\n");

        // Dynamic Load Profile
        results.append("\nDynamic Load Profile:\n");
        List<Double> loadProfile = generateDynamicLoadProfile(adjustedParams, scenario.duration, scenario.loadFactor);
        results.append(String.format("Dynamic Load (N) over %.1f seconds:\n", scenario.duration));
        for (int i = 0; i < loadProfile.size(); i++) {
            results.append(String.format("t=%.1f s: %.0f N\n", i * 0.1, loadProfile.get(i)));
        }

        // Fatigue Analysis
        results.append("\nFatigue Analysis:\n");
        double bearingLifeHours = calculateBearingL10Life(adjustedParams, loadProfile);
        results.append(String.format("Bearing L10 Life: %.0f hours\n", bearingLifeHours));
        results.append(bearingLifeHours >= 20000 ? 
            "Bearing life acceptable\n" : 
            "Warning: Short bearing life predicted\n");

        double spindleLifePercentage = calculateSpindleFatigueLife(adjustedParams, loadProfile);
        results.append(String.format("Spindle Shaft Remaining Life: %.1f%%\n", spindleLifePercentage * 100));
        results.append(spindleLifePercentage >= 0.5 ? 
            "Spindle shaft life acceptable\n" : 
            "Warning: Spindle shaft may fail prematurely\n");

        // Grinding Wheel Wear Analysis
        results.append("\nGrinding Wheel Wear Analysis:\n");
        double initialDiameter = adjustedParams.getWheelDiameter();
        double wear = calculateWheelWear(adjustedParams, loadProfile, scenario.duration);
        double remainingDiameter = initialDiameter - wear;
        double wearVibration = calculateWearInducedVibration(adjustedParams, wear);
        results.append(String.format("Initial Wheel Diameter: %.1f mm\n", initialDiameter));
        results.append(String.format("Remaining Wheel Diameter: %.1f mm\n", remainingDiameter));
        results.append(String.format("Wear-Induced Vibration: %.2f mm/s\n", wearVibration));
        results.append(remainingDiameter >= initialDiameter * 0.8 ? 
            "Wheel condition acceptable\n" : 
            "Warning: Excessive wheel wear detected\n");
        results.append(wearVibration <= 0.5 ? 
            "Wear-induced vibration within limits\n" : 
            "Warning: Increased vibration due to wheel imbalance\n");

        // Maintenance Prediction
        results.append("\nMaintenance Prediction:\n");
        if (historicalData.isEmpty()) {
            generateHistoricalData();
        }
        double totalVibration = vibrationLevel + wearVibration;
        double avgLoad = loadProfile.stream().mapToDouble(Double::doubleValue).average().orElse(estimateLoad(adjustedParams));
        int maintenanceNeeded = predictMaintenance(totalVibration, tempRise + 20.0, avgLoad, 
                                                  bearingLifeHours, spindleLifePercentage, wear);
        results.append(maintenanceNeeded == 1 ? 
            "Maintenance Needed: Yes (e.g., bearing replacement, wheel dressing)\n" : 
            "Maintenance Needed: No\n");

        // Add to historical data
        int label = (totalVibration > 1.0 || bearingLifeHours < 5000 || spindleLifePercentage < 0.5 || 
                     wear > initialDiameter * 0.2) ? 1 : 0;
        historicalData.add(new DataPoint(totalVibration, tempRise + 20.0, avgLoad, 
                                         bearingLifeHours, spindleLifePercentage, wear, label));

        results.append("\n");
        return results.toString();
    }

    private String generateComprehensiveReport(SpindleParameters params, List<SimulationScenario> scenarios) {
        StringBuilder report = new StringBuilder();
        report.append("=== Comprehensive Analysis ===\n\n");

        report.append("Spindle Type: ").append(params.getSpindleType()).append("\n");
        report.append(evaluateSpindleType(params)).append("\n\n");

        // Summarize key metrics across scenarios
        report.append("Summary Across Scenarios:\n");
        for (SimulationScenario scenario : scenarios) {
            SpindleParameters adjustedParams = new SpindleParameters();
            adjustedParams.setSpindleType(params.getSpindleType());
            adjustedParams.setPowerRating(params.getPowerRating());
            adjustedParams.setMaxSpeed((int) (params.getMaxSpeed() * scenario.speedFactor));
            adjustedParams.setWheelDiameter(params.getWheelDiameter());
            adjustedParams.setBearingType(params.getBearingType());
            adjustedParams.setBearingPreload(params.getBearingPreload());
            adjustedParams.setCoolingType(params.getCoolingType());
            adjustedParams.setLubricationType(params.getLubricationType());
            adjustedParams.setToolInterface(params.getToolInterface());
            adjustedParams.setAlignmentTolerance(params.getAlignmentTolerance());

            List<Double> loadProfile = generateDynamicLoadProfile(adjustedParams, scenario.duration, scenario.loadFactor);
            double vibration = estimateVibration(adjustedParams);
            double tempRise = estimateTemperatureRise(adjustedParams);
            double bearingLife = calculateBearingL10Life(adjustedParams, loadProfile);
            double spindleLife = calculateSpindleFatigueLife(adjustedParams, loadProfile);
            double wheelWear = calculateWheelWear(adjustedParams, loadProfile, scenario.duration);
            double wearVibration = calculateWearInducedVibration(adjustedParams, wheelWear);

            report.append(String.format("Scenario: %s\n", scenario.name));
            report.append(String.format(" - Vibration: %.2f mm/s\n", vibration + wearVibration));
            report.append(String.format(" - Temperature Rise: %.1f°C\n", tempRise));
            report.append(String.format(" - Bearing Life: %.0f hours\n", bearingLife));
            report.append(String.format(" - Spindle Life: %.1f%%\n", spindleLife * 100));
            report.append(String.format(" - Wheel Wear: %.1f mm\n", wheelWear));
            report.append("\n");
        }

        // Recommendations
        report.append("Recommendations:\n");
        boolean highVibration = false;
        boolean highTemp = false;
        boolean lowBearingLife = false;
        for (SimulationScenario scenario : scenarios) {
            SpindleParameters adjustedParams = new SpindleParameters();
            adjustedParams.setSpindleType(params.getSpindleType());
            adjustedParams.setPowerRating(params.getPowerRating());
            adjustedParams.setMaxSpeed((int) (params.getMaxSpeed() * scenario.speedFactor));
            adjustedParams.setWheelDiameter(params.getWheelDiameter());
            adjustedParams.setBearingType(params.getBearingType());
            adjustedParams.setBearingPreload(params.getBearingPreload());
            adjustedParams.setCoolingType(params.getCoolingType());
            adjustedParams.setLubricationType(params.getLubricationType());
            adjustedParams.setToolInterface(params.getToolInterface());
            adjustedParams.setAlignmentTolerance(params.getAlignmentTolerance());

            List<Double> loadProfile = generateDynamicLoadProfile(adjustedParams, scenario.duration, scenario.loadFactor);
            double vibration = estimateVibration(adjustedParams);
            double tempRise = estimateTemperatureRise(adjustedParams);
            double bearingLife = calculateBearingL10Life(adjustedParams, loadProfile);
            double wheelWear = calculateWheelWear(adjustedParams, loadProfile, scenario.duration);
            double wearVibration = calculateWearInducedVibration(adjustedParams, wheelWear);

            if (vibration + wearVibration > 1.0) highVibration = true;
            if (tempRise > 30) highTemp = true;
            if (bearingLife < 20000) lowBearingLife = true;
        }

        if (highVibration) {
            report.append(" - Consider upgrading to Hybrid Ceramic bearings or HSK tool interface to reduce vibration.\n");
        }
        if (highTemp) {
            report.append(" - Switch to Liquid cooling to improve thermal performance.\n");
        }
        if (lowBearingLife) {
            report.append(" - Optimize lubrication type (e.g., Oil-Air) or reduce bearing preload to extend bearing life.\n");
        }
        if (!highVibration && !highTemp && !lowBearingLife) {
            report.append(" - Current configuration is robust across tested scenarios.\n");
        }

        return report.toString();
    }

    public String simulateTimeBased(SpindleParameters params, double duration) {
        String validationResult = validateParameters(params);
        if (!validationResult.equals("Valid")) {
            return validationResult;
        }

        StringBuilder results = new StringBuilder();
        results.append(String.format("=== Time-Based Spindle Simulation (Duration: %.1f s) ===\n\n", duration));

        double timeStep = 0.1;
        int steps = (int) (duration / timeStep);
        List<Double> vibrationHistory = new ArrayList<>();
        List<Double> temperatureHistory = new ArrayList<>();
        List<Double> loadProfile = generateDynamicLoadProfile(params, duration, 1.0);

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

        results.append("\nMaintenance Prediction:\n");
        if (historicalData.isEmpty()) {
            generateHistoricalData();
        }
        double totalVibration = maxVibration + wearVibration;
        double avgLoad = loadProfile.stream().mapToDouble(Double::doubleValue).average().orElse(estimateLoad(params));
        int maintenanceNeeded = predictMaintenance(totalVibration, maxTemp, avgLoad, 
                                                  bearingLifeHours, spindleLifePercentage, wear);
        results.append(maintenanceNeeded == 1 ? 
            "Maintenance Needed: Yes (e.g., bearing replacement, wheel dressing)\n" : 
            "Maintenance Needed: No\n");

        int label = (totalVibration > 1.0 || bearingLifeHours < 5000 || spindleLifePercentage < 0.5 || 
                     wear > initialDiameter * 0.2) ? 1 : 0;
        historicalData.add(new DataPoint(totalVibration, maxTemp, avgLoad, 
                                         bearingLifeHours, spindleLifePercentage, wear, label));

        return results.toString();
    }

    private String validateParameters(SpindleParameters params) {
        if (params.getPowerRating() < 0.5 || params.getPowerRating() > 50.0) {
            return "Error: Power rating must be between 0.5 and 50 kW\n";
        }
        if (params.getMaxSpeed() < 1000 || params.getMaxSpeed() > 30000) {
            return "Error: Max speed must be between 1000 and 30000 RPM\n";
        }
        if (params.getWheelDiameter() < 50 || params.getWheelDiameter() > 1000) {
            return "Error: Wheel diameter must be between 50 and 1000 mm\n";
        }
        if (params.getBearingPreload() < 100 || params.getBearingPreload() > 2000) {
            return "Error: Bearing preload must be between 100 and 2000 N\n";
        }
        if (params.getAlignmentTolerance() < 0.0001 || params.getAlignmentTolerance() > 0.01) {
            return "Error: Alignment tolerance must be between 0.0001 and 0.01 mm\n";
        }
        return "Valid";
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

    public List<Double> generateDynamicLoadProfile(SpindleParameters params, double duration, double loadFactor) {
        List<Double> loadProfile = new ArrayList<>();
        double baseLoad = estimateLoad(params) * loadFactor;
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
        double wearCoefficient = 1e-6;
        double wheelDiameter = params.getWheelDiameter() / 1000.0;
        double wheelThickness = 0.02;
        double avgLoad = loadProfile.stream().mapToDouble(Double::doubleValue).average().orElse(estimateLoad(params));
        double peripheralSpeed = Math.PI * wheelDiameter * params.getMaxSpeed() / 60.0;
        double slidingDistance = peripheralSpeed * duration;
        double wearVolume = wearCoefficient * avgLoad * slidingDistance;
        double diameterReduction = wearVolume / (Math.PI * wheelDiameter * wheelThickness * 1000.0);
        return Math.min(diameterReduction, params.getWheelDiameter() * 0.2);
    }

    public double calculateWearInducedVibration(SpindleParameters params, double wear) {
        double wheelDiameter = params.getWheelDiameter() / 1000.0;
        double wheelThickness = 0.02;
        double density = 2500.0;
        double wearVolume = wear * Math.PI * wheelDiameter * wheelThickness * 1000.0;
        double imbalanceMass = density * wearVolume * 1e-9;
        double wheelMass = density * Math.PI * Math.pow(wheelDiameter / 2, 2) * wheelThickness;
        double eccentricity = (imbalanceMass * (wheelDiameter / 2)) / wheelMass;
        double omega = 2 * Math.PI * params.getMaxSpeed() / 60.0;
        double imbalanceForce = imbalanceMass * Math.pow(omega, 2) * eccentricity;
        double systemStiffness = 1e8;
        double vibrationAmplitude = imbalanceForce / systemStiffness * 1000.0;
        return Math.min(vibrationAmplitude, 2.0);
    }

    private void generateHistoricalData() {
        for (int i = 0; i < 100; i++) {
            double vibration = 0.2 + random.nextDouble() * 2.0;
            double temperature = 20.0 + random.nextDouble() * 30.0;
            double load = 500.0 + random.nextDouble() * 1500.0;
            double bearingLife = 1000.0 + random.nextDouble() * 49000.0;
            double spindleLife = random.nextDouble();
            double wheelWear = random.nextDouble() * 40.0;
            int label = (vibration > 1.0 || bearingLife < 5000 || spindleLife < 0.5 || wheelWear > 40.0 * 0.5) ? 1 : 0;
            historicalData.add(new DataPoint(vibration, temperature, load, bearingLife, spindleLife, wheelWear, label));
        }
    }

    private double calculateEuclideanDistance(DataPoint p1, DataPoint p2) {
        double vibDiff = (p1.vibration - p2.vibration) / 2.0;
        double tempDiff = (p1.temperature - p2.temperature) / 30.0;
        double loadDiff = (p1.load - p2.load) / 1500.0;
        double bearingLifeDiff = (p1.bearingLife - p2.bearingLife) / 50000.0;
        double spindleLifeDiff = (p1.spindleLife - p2.spindleLife);
        double wheelWearDiff = (p1.wheelWear - p2.wheelWear) / 40.0;

        return Math.sqrt(
            vibDiff * vibDiff +
            tempDiff * tempDiff +
            loadDiff * loadDiff +
            bearingLifeDiff * bearingLifeDiff +
            spindleLifeDiff * spindleLifeDiff +
            wheelWearDiff * wheelWearDiff
        );
    }

    public int predictMaintenance(double vibration, double temperature, double load, 
                                 double bearingLife, double spindleLife, double wheelWear) {
        if (historicalData.isEmpty()) {
            generateHistoricalData();
        }

        DataPoint query = new DataPoint(vibration, temperature, load, bearingLife, spindleLife, wheelWear, 0);
        List<double[]> distances = new ArrayList<>();
        for (DataPoint data : historicalData) {
            double distance = calculateEuclideanDistance(query, data);
            distances.add(new double[]{distance, data.label});
        }

        distances.sort((a, b) -> Double.compare(a[0], b[0]));
        int k = 3;
        int yesCount = 0;
        for (int i = 0; i < k && i < distances.size(); i++) {
            if ((int) distances.get(i)[1] == 1) {
                yesCount++;
            }
        }

        return yesCount > k / 2 ? 1 : 0;
    }
}