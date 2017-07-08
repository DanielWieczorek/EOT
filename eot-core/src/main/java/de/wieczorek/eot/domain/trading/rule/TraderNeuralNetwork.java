package de.wieczorek.eot.domain.trading.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.wieczorek.eot.domain.exchangable.rate.ExchangeRateHistory;

public class TraderNeuralNetwork implements INeuralNetworkNode {

    private TradingRulePerceptron perceptron1;
    private TradingRulePerceptron perceptron2;

    private INeuralNetworkNode network;
    private NetworkType type = NetworkType.AND;

    public enum NetworkType {
	AND, OR, XOR
    }

    public TraderNeuralNetwork(TradingRulePerceptron perceptron1, TradingRulePerceptron perceptron2) {
	this(perceptron1, perceptron2, NetworkType.AND);
    }

    public TraderNeuralNetwork(TradingRulePerceptron perceptron1, TradingRulePerceptron perceptron2, NetworkType type) {
	this.perceptron1 = perceptron1;
	this.perceptron2 = perceptron2;
	this.type = type;
	buildAndSetNetworkByType(type);

    }

    private INeuralNetworkNode buildLogicAndNetwork() {

	INeuralNetworkNode intermediateNode1 = new DefaultNeuralNetworkNode(1.5, //
		new NeuralNetworkNodeConnection(1, perceptron1), //
		new NeuralNetworkNodeConnection(1, perceptron2)); //

	INeuralNetworkNode intermediateNode2 = new DefaultNeuralNetworkNode(1.5, //
		new NeuralNetworkNodeConnection(1, perceptron1), //
		new NeuralNetworkNodeConnection(1, perceptron2)); // );

	INeuralNetworkNode exitNode = new DefaultNeuralNetworkNode(1.5,
		new NeuralNetworkNodeConnection(1, intermediateNode1), //
		new NeuralNetworkNodeConnection(1, intermediateNode2));

	return exitNode;
    }

    private INeuralNetworkNode buildLogicOrNetwork() {
	INeuralNetworkNode intermediateNode1 = new DefaultNeuralNetworkNode(0.5, //
		new NeuralNetworkNodeConnection(1, perceptron1), //
		new NeuralNetworkNodeConnection(1, perceptron2)); //

	INeuralNetworkNode intermediateNode2 = new DefaultNeuralNetworkNode(0.5, //
		new NeuralNetworkNodeConnection(1, perceptron1), //
		new NeuralNetworkNodeConnection(1, perceptron2)); // );

	INeuralNetworkNode exitNode = new DefaultNeuralNetworkNode(0.5,
		new NeuralNetworkNodeConnection(1, intermediateNode1), //
		new NeuralNetworkNodeConnection(1, intermediateNode2));

	return exitNode;
    }

    private INeuralNetworkNode buildLogicXorNetwork() {
	INeuralNetworkNode intermediateNode1 = new DefaultNeuralNetworkNode(0.5, //
		new NeuralNetworkNodeConnection(1, perceptron1), //
		new NeuralNetworkNodeConnection(1, perceptron2)); //

	INeuralNetworkNode intermediateNode2 = new DefaultNeuralNetworkNode(-1.5, //
		new NeuralNetworkNodeConnection(-1, perceptron1), //
		new NeuralNetworkNodeConnection(-1, perceptron2)); // );

	INeuralNetworkNode exitNode = new DefaultNeuralNetworkNode(1.5,
		new NeuralNetworkNodeConnection(1, intermediateNode1), //
		new NeuralNetworkNodeConnection(1, intermediateNode2));

	return exitNode;
    }

    @Override
    public boolean isActivated(ExchangeRateHistory history) {
	return network.isActivated(history);
    }

    public List<TraderNeuralNetwork> combineWith(TraderNeuralNetwork other) {
	TraderNeuralNetwork child1 = new TraderNeuralNetwork(this.perceptron1.combineWith(other.perceptron2),
		other.perceptron2, this.type);
	TraderNeuralNetwork child2 = new TraderNeuralNetwork(this.perceptron1,
		other.perceptron2.combineWith(other.perceptron1), other.type);
	List<TraderNeuralNetwork> result = new ArrayList<>();
	result.add(child1);
	result.add(child2);
	return result;
    }

    public INeuralNetworkNode getNetwork() {
	return network;
    }

    public NetworkType getType() {
	return type;
    }

    public TradingRulePerceptron getPerceptron1() {
	return perceptron1;
    }

    public TradingRulePerceptron getPerceptron2() {
	return perceptron2;
    }

    public void setRandomThreshold() {
	perceptron1.setRandomThreshold();
	perceptron2.setRandomThreshold();

    }

    public void randomizeOneComparator() {
	perceptron1.randomizeOneComparator();
	perceptron2.randomizeOneComparator();

    }

    public void randomizeNetworkType() {
	Random r = new Random(System.currentTimeMillis());
	NetworkType type = NetworkType.values()[r.nextInt(NetworkType.values().length)];
	buildAndSetNetworkByType(type);
    }

    private void buildAndSetNetworkByType(NetworkType type) {
	switch (type) {
	case AND:
	    network = buildLogicAndNetwork();
	    this.type = NetworkType.AND;
	    break;
	case OR:
	    network = buildLogicOrNetwork();
	    this.type = NetworkType.OR;
	    break;
	case XOR:
	    network = buildLogicXorNetwork();
	    this.type = NetworkType.XOR;
	    break;
	}
    }

}
