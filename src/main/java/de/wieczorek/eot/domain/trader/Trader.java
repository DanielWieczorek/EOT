package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.ExchangablePair;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;

public class Trader implements IIndiviual {

	private Wallet wallet;
	private IExchange exchange;
	private TradingRule buyRule;
	private TradingRule sellRule;
	private ExchangablePair exchangablesToTrade;
	private double lastSeenRate = 0.0;

	public Trader(Wallet wallet, IExchange exchange, TradingRule buyRule, TradingRule sellRule,
			ExchangablePair exchangablesToTrade) {
		super();
		this.setWallet(wallet);
		this.exchange = exchange;
		this.buyRule = buyRule;
		this.sellRule = sellRule;
		this.exchangablesToTrade = exchangablesToTrade;
	}

	@Override
	public double calculateFitness() {

		return 0;
	}

	@Override
	public void performAction() {
		trade();

	}

	private void trade() {
		if (exchange.getCurrentExchangeRate(exchangablesToTrade).getToPrice() != lastSeenRate) {
			ExchangableSet from = getWallet().countAllExchangablesOfType(exchangablesToTrade.getFrom());
			ExchangableSet to = getWallet().countAllExchangablesOfType(exchangablesToTrade.getTo());
			if (buyRule.evaluate(exchange.getExchangeRateHistory(exchangablesToTrade, 24)) && from.getAmount() > 0) {
				buy();
			} else if (sellRule.evaluate(exchange.getExchangeRateHistory(exchangablesToTrade, 24))
					&& to.getAmount() > 0) {
				sell();
			}
		}
		lastSeenRate = exchange.getCurrentExchangeRate(exchangablesToTrade).getToPrice();

	}

	private void buy() {
		ExchangableSet from = getWallet().countAllExchangablesOfType(exchangablesToTrade.getFrom());
		if (from.getAmount() > 0) {
			Order order = new Order(exchangablesToTrade, from.getAmount(), OrderType.BUY);
			ExchangableSet returnOfInvestment = exchange.performOrder(order);

			getWallet().withdraw(new ExchangableSet(order.getPair().getFrom(), order.getAmount()));
			getWallet().deposit(
					new ExchangableAmount(returnOfInvestment, order.getAmount() / returnOfInvestment.getAmount()));

			printWalletInfo();

		}
	}

	private void sell() {
		ExchangableSet to = getWallet().countAllExchangablesOfType(exchangablesToTrade.getTo());
		if (to.getAmount() > 0) {
			Order order = new Order(exchangablesToTrade, to.getAmount(), OrderType.SELL);
			ExchangableSet returnOfInvestment = exchange.performOrder(order);

			getWallet().withdraw(new ExchangableSet(order.getPair().getTo(), order.getAmount()));
			getWallet().deposit(
					new ExchangableAmount(returnOfInvestment, order.getAmount() / returnOfInvestment.getAmount()));

			printWalletInfo();

		}

	}

	private void printWalletInfo() {
		ExchangableSet from = getWallet().countAllExchangablesOfType(exchangablesToTrade.getFrom());
		ExchangableSet to = getWallet().countAllExchangablesOfType(exchangablesToTrade.getTo());
		System.out.println("ETH -> BTC: "
				+ ((SimulatedExchangeImpl) exchange).getCurrentExchangeRate(exchangablesToTrade).getToPrice());
		from = getWallet().countAllExchangablesOfType(exchangablesToTrade.getFrom());
		to = getWallet().countAllExchangablesOfType(exchangablesToTrade.getTo());
		System.out.println("ETH: " + from.getAmount());
		System.out.println("BTC: " + to.getAmount());
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

}
