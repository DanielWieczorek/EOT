package de.wieczorek.eot.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateHistory {

	private List<TimedExchangeRate> dataPoints;
	
	public ExchangeRateHistory(){
		dataPoints =  new ArrayList<>();
	}
	
	public static ExchangeRateHistory from(List<TimedExchangeRate> exchangeRates){
		ExchangeRateHistory newHistory = new ExchangeRateHistory();
		
		for(TimedExchangeRate exchangeRate : exchangeRates)
			newHistory.add(exchangeRate);
		return newHistory;
		
	}
	
	public ExchangeRateHistory add(TimedExchangeRate exchangeRate) { 
		dataPoints.add(exchangeRate);
		for(int i=0;i<dataPoints.size();i++)
			if(exchangeRate.isBefore(dataPoints.get(i))){
				dataPoints.add(i, exchangeRate);
				break;
			}
				
		return this;
	}

	public List<TimedExchangeRate> getCompleteHistoryData(){
		return dataPoints;
	}
	
	public ExchangeRateHistory getHistoryEntriesBefore(LocalDateTime date, int amount){
		int endIndex = -1;
		int startIndex = -1;
		for(int i=0;i<dataPoints.size();i++)
			if(!date.isAfter(dataPoints.get(i).getTime())){
				endIndex = i-1;
				break;
			}
		startIndex = Math.max(0, endIndex-amount);
				
		if (date.isAfter(dataPoints.get(dataPoints.size()-1).getTime())) 
			endIndex = dataPoints.size()-1;
		
//		System.out.println("getting entries from:"+startIndex+" to "+endIndex);
		if (startIndex <= endIndex && endIndex != -1){
			List<TimedExchangeRate> resultExchangeRates = dataPoints.subList(startIndex, endIndex);
			return ExchangeRateHistory.from(resultExchangeRates);
		}
		else
			return new ExchangeRateHistory();
		
	}
}
