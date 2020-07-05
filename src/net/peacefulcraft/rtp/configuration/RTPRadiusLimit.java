package net.peacefulcraft.rtp.configuration;

public class RTPRadiusLimit {
  private int minRadius;
  public int getMinRadius() { return minRadius; }
  
  private int maxRadius;
  public int getMaxRadius() { return maxRadius; }

  public RTPRadiusLimit(int minRadius, int maxRadius) {
    this.minRadius = minRadius;
    this.maxRadius = maxRadius;
  }
}