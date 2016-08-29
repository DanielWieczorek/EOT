package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.ExchangableType;
import de.wieczorek.eot.domain.TimedExchangeRate;
import de.wieczorek.eot.domain.exchange.IExchange;
import de.wieczorek.eot.domain.exchange.impl.SimulatedExchangeImpl;

public class Trader implements IIndiviual{

	private Wallet wallet;
	private IExchange exchange;
	private TradingRule tradingRule;
	
	
	public Trader(Wallet wallet, IExchange exchange, TradingRule tradingRule) {
		super();
		this.wallet = wallet;
		this.exchange = exchange;
		this.tradingRule = tradingRule;
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
		if(tradingRule.evaluate(exchange.getExchangeRateHistory(tradingRule.getFromExchangable(), tradingRule.getToExchangable(), 24))){
			ExchangableSet from = wallet.countAllExchangablesOfType(tradingRule.getFromExchangable());
			ExchangableSet to = wallet.countAllExchangablesOfType(tradingRule.getToExchangable());
			if(from.getAmount() > 0){
				Order order = new Order();
				order.setFrom(from);
				order.setTo(to);
				order.setType(tradingRule.getType());
				
				exchange.performOrder(order);
				
				if(order.getType().equals(OrderType.BUY)){
					wallet.withdraw(order.getFrom());
					wallet.deposit(new ExchangableAmount(order.getTo(), order.getFrom().getAmount()/order.getTo().getAmount()));
				}
				else if(order.getType().equals(OrderType.SELL)){
					wallet.withdraw(order.getTo());
					wallet.deposit(new ExchangableAmount(order.getFrom(), order.getTo().getAmount()/order.getFrom().getAmount()));
				}
			
			System.out.println("ETH -> BTC: "+((SimulatedExchangeImpl) exchange).getHistory().getCompleteHistoryData().get(((SimulatedExchangeImpl) exchange).getIndexUsedLast()).getToPrice());
			 from = wallet.countAllExchangablesOfType(tradingRule.getFromExchangable());
			to = wallet.countAllExchangablesOfType(tradingRule.getToExchangable());
			System.out.println("ETH: "+from.getAmount());
			System.out.println("BTC: "+to.getAmount());
			}	
		}
		else {
			ExchangableSet from = wallet.countAllExchangablesOfType(tradingRule.getFromExchangable());
			ExchangableSet to = wallet.countAllExchangablesOfType(tradingRule.getToExchangable());
			if(to.getAmount() > 0){
				Order order = new Order();
				order.setFrom(from);
				order.setTo(to);
				if(tradingRule.getType().equals(OrderType.BUY))
					order.setType(OrderType.SELL);
				else 
					order.setType(OrderType.BUY);
				
				exchange.performOrder(order);
				
				if(order.getType().equals(OrderType.BUY)){
					wallet.withdraw(order.getFrom());
					wallet.deposit(new ExchangableAmount(order.getTo(), order.getFrom().getAmount()/order.getTo().getAmount()));
				}
				else if(order.getType().equals(OrderType.SELL)){
					wallet.withdraw(order.getTo());
					wallet.deposit(new ExchangableAmount(order.getFrom(), order.getTo().getAmount()/order.getFrom().getAmount()));
				}
		
				System.out.println("ETH -> BTC: "+((SimulatedExchangeImpl) exchange).getHistory().getCompleteHistoryData().get(((SimulatedExchangeImpl) exchange).getIndexUsedLast()).getToPrice());
				 from = wallet.countAllExchangablesOfType(tradingRule.getFromExchangable());
				to = wallet.countAllExchangablesOfType(tradingRule.getToExchangable());
				System.out.println("ETH: "+from.getAmount());
				System.out.println("BTC: "+to.getAmount());
			}
		}

	}
	
	
}
