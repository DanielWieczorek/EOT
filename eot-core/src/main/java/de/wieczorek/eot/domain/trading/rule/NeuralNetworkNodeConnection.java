package de.wieczorek.eot.domain.trading.rule;

public class NeuralNetworkNodeConnection {
    private double strength;
    private INeuralNetworkNode origin;

    public NeuralNetworkNodeConnection(double strength, INeuralNetworkNode origin) {
	this.strength = strength;
	this.origin = origin;
    }

    public double getStrength() {
	return strength;
    }

    public void setStrength(double strength) {
	this.strength = strength;
    }

    public INeuralNetworkNode getOrigin() {
	return origin;
    }

    public void setOrigin(INeuralNetworkNode origin) {
	this.origin = origin;
    }
}
