package de.wieczorek.eot.ui.trader;

import javax.xml.bind.annotation.XmlRootElement;

import de.wieczorek.eot.domain.exchangable.ExchangablePair;

@XmlRootElement
public class TraderConfiguration {
    private NeuralNetworkConfiguration buyNetwork;
    private NeuralNetworkConfiguration sellNetwork;

    private ExchangablePair exchangablesToTrade;

    private int numberOfChunks;

    private boolean isStopLossActivated;

    public NeuralNetworkConfiguration getBuyNetwork() {
	return buyNetwork;
    }

    public void setBuyNetwork(NeuralNetworkConfiguration buyNetwork) {
	this.buyNetwork = buyNetwork;
    }

    public NeuralNetworkConfiguration getSellNetwork() {
	return sellNetwork;
    }

    public void setSellNetwork(NeuralNetworkConfiguration sellNetwork) {
	this.sellNetwork = sellNetwork;
    }

    public ExchangablePair getExchangablesToTrade() {
	return exchangablesToTrade;
    }

    public void setExchangablesToTrade(ExchangablePair exchangablesToTrade) {
	this.exchangablesToTrade = exchangablesToTrade;
    }

    public int getNumberOfChunks() {
	return numberOfChunks;
    }

    public void setNumberOfChunks(int numberOfChunks) {
	this.numberOfChunks = numberOfChunks;
    }

    public boolean isStopLossActivated() {
	return isStopLossActivated;
    }

    public void setStopLossActivated(boolean isStopLossActivated) {
	this.isStopLossActivated = isStopLossActivated;
    }
}
