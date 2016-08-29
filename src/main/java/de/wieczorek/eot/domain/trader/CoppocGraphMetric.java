package de.wieczorek.eot.domain.trader;

import de.wieczorek.eot.domain.ExchangeRateHistory;
import de.wieczorek.eot.domain.TimedExchangeRate;

public class CoppocGraphMetric extends AbstractGraphMetric {

	@Override
	public double getRating(ExchangeRateHistory history) {
		
		
		   ExchangeRateHistory input = history;
	        ExchangeRateHistory racOutput = new ExchangeRateHistory();
	        
	        for (int i=15;i<input.getCompleteHistoryData().size();i++){
	        	TimedExchangeRate currentDatapoint = input.getCompleteHistoryData().get(i);
	       // 	System.out.println(currentDatapoint.getTime().toString());
	        	ExchangeRateHistory roc11Set = input.getHistoryEntriesBefore(currentDatapoint.getTime(), 11);
	        	
	        	double roc11 = ((currentDatapoint.getToPrice()-  roc11Set.getCompleteHistoryData().get(0).getToPrice()) / roc11Set.getCompleteHistoryData().get(0).getToPrice())*100;
	       // 	System.out.println("roc 11:"+roc11);
	        	ExchangeRateHistory roc14Set = input.getHistoryEntriesBefore(currentDatapoint.getTime(), 14);
	        	double roc14 = ((currentDatapoint.getToPrice() - roc14Set.getCompleteHistoryData().get(0).getToPrice()) / roc14Set.getCompleteHistoryData().get(0).getToPrice())*100;
	      //  	System.out.println("roc 14:"+roc14);
	        	racOutput.add(new TimedExchangeRate(currentDatapoint.getFrom(), currentDatapoint.getTo(), roc11+roc14, currentDatapoint.getTime()));
	      //  	System.out.println("roc 14+11:"+roc14+roc11);
	        }
	        
	     
	        	TimedExchangeRate currentDatapoint = racOutput.getCompleteHistoryData().get(racOutput.getCompleteHistoryData().size()-1);
	        	ExchangeRateHistory weightedAverageSet = racOutput.getHistoryEntriesBefore(currentDatapoint.getTime(), 10);
	     //   	System.out.println(currentDatapoint.getTime().toString());
	        	
	        	double average = 0;
	        	
	        	for(int j=0;j< weightedAverageSet.getCompleteHistoryData().size();j++){
	        		average += weightedAverageSet.getCompleteHistoryData().get(j).getToPrice()*(double)(j+1.0);
	        //		System.out.println("avg: "+j+":"+weightedAverageSet.getCompleteHistoryData().get(j).getToPrice());
	        	}
	       // 	System.out.println(weightedAverageSet.getCompleteHistoryData().size());
	        	average = average / (double)(1+2+3+4+5+6+7+8+9+10);
	      //  	System.out.println(average);
	        	return new TimedExchangeRate(currentDatapoint.getFrom(), currentDatapoint.getTo(), -average, currentDatapoint.getTime()).getToPrice();
	}


}
