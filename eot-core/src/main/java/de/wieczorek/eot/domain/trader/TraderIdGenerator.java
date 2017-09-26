package de.wieczorek.eot.domain.trader;

public class TraderIdGenerator {

    private static long traderId;

    public static synchronized long getNextId() {
	traderId = Math.floorMod(traderId + 1, Long.MAX_VALUE);
	return traderId;
    }

}
