package de.wieczorek.eot.ui.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IndividualInfo {

    private String name;
    private double btc;
    private double eth;
    private double netProfit;
    private double sellsAtLoss;
    private double netProfitPercent;
    private double numberOfTrades;

    @XmlElement(name = "name")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @XmlElement(name = "btc")
    public double getBtc() {
	return btc;
    }

    public void setBtc(double btc) {
	this.btc = btc;
    }

    @XmlElement(name = "eth")
    public double getEth() {
	return eth;
    }

    public void setEth(double eth) {
	this.eth = eth;
    }

    @XmlElement(name = "netProfit")
    public double getNetProfit() {
	return netProfit;
    }

    public void setNetProfit(double netProfit) {
	this.netProfit = netProfit;
    }

    @XmlElement(name = "sellsAtLoss")
    public double getSellsAtLoss() {
	return sellsAtLoss;
    }

    public void setSellsAtLoss(double sellsAtLoss) {
	this.sellsAtLoss = sellsAtLoss;
    }

    @XmlElement(name = "netProfitPercent")
    public double getNetProfitPercent() {
	return netProfitPercent;
    }

    public void setNetProfitPercent(double netProfitPercent) {
	this.netProfitPercent = netProfitPercent;
    }

    @XmlElement(name = "numberOfTrades")
    public double getNumberOfTrades() {
	return numberOfTrades;
    }

    public void setNumberOfTrades(double numberOfTrades) {
	this.numberOfTrades = numberOfTrades;
    }

}
