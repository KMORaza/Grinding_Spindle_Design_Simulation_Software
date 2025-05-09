package code.grindlespindledesign.simulation;

public class SpindleParameters {
    private String spindleType;
    private double powerRating;
    private int maxSpeed;
    private double wheelDiameter;
    private String bearingType;
    private double bearingPreload;
    private String coolingType;
    private String lubricationType;
    private String toolInterface;
    private double alignmentTolerance;
    
    // Getters and Setters
    public String getSpindleType() { return spindleType; }
    public void setSpindleType(String spindleType) { this.spindleType = spindleType; }
    
    public double getPowerRating() { return powerRating; }
    public void setPowerRating(double powerRating) { this.powerRating = powerRating; }
    
    public int getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(int maxSpeed) { this.maxSpeed = maxSpeed; }
    
    public double getWheelDiameter() { return wheelDiameter; }
    public void setWheelDiameter(double wheelDiameter) { this.wheelDiameter = wheelDiameter; }
    
    public String getBearingType() { return bearingType; }
    public void setBearingType(String bearingType) { this.bearingType = bearingType; }
    
    public double getBearingPreload() { return bearingPreload; }
    public void setBearingPreload(double bearingPreload) { this.bearingPreload = bearingPreload; }
    
    public String getCoolingType() { return coolingType; }
    public void setCoolingType(String coolingType) { this.coolingType = coolingType; }
    
    public String getLubricationType() { return lubricationType; }
    public void setLubricationType(String lubricationType) { this.lubricationType = lubricationType; }
    
    public String getToolInterface() { return toolInterface; }
    public void setToolInterface(String toolInterface) { this.toolInterface = toolInterface; }
    
    public double getAlignmentTolerance() { return alignmentTolerance; }
    public void setAlignmentTolerance(double alignmentTolerance) { this.alignmentTolerance = alignmentTolerance; }
}