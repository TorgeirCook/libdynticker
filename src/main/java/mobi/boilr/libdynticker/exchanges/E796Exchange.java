package mobi.boilr.libdynticker.exchanges;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.boilr.libdynticker.core.Exchange;
import mobi.boilr.libdynticker.core.Pair;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;

public final class E796Exchange extends Exchange {
	private static final List<Pair> pairs;
	static {
		List<Pair> tempPairs = new ArrayList<Pair>();
		tempPairs.add(new Pair("BTC Weekly Futures", "USD"));
		tempPairs.add(new Pair("LTC Weekly Futures", "USD"));
		tempPairs.add(new Pair("MRI", "BTC"));
		tempPairs.add(new Pair("ASICMINER", "BTC"));
		tempPairs.add(new Pair("RSM", "BTC"));
		pairs = Collections.unmodifiableList(tempPairs);
	}

	public E796Exchange(long expiredPeriod) {
		super("796 Exchange", expiredPeriod);
	}

	@Override
	protected List<Pair> getPairsFromAPI() {
		return pairs;
	}

	@Override
	protected String getTicker(Pair pair) throws JsonProcessingException, MalformedURLException,
	IOException {
		/*
		 * http://api.796.com/v3/futures/ticker.html?type=weekly
		 * http://api.796.com/v3/futures/ticker.html?type=ltc
		 * 
		 * http://api.796.com/v3/stock/ticker.html?type=mri
		 */
		if(!pairs.contains(pair))
			throw new IOException("Invalid pair: " + pair);
		String url = "http://api.796.com/v3/";
		String coin = pair.getCoin();
		boolean isFutures = false;
		if(coin.contains("Futures")) {
			url += "futures/";
			isFutures = true;
		} else {
			url += "stock/";
		}
		url += "ticker.html?type=";
		if(isFutures) {
			if(coin.contains("BTC")) {
				url += "weekly";
			} else if(coin.contains("LTC")) {
				url += "ltc";
			}
		} else {
			url += coin.toLowerCase();
		}
		JsonNode node = readJsonFromUrl(url);
		return parseTicker(node, pair);
	}

	@Override
	public String parseTicker(JsonNode node, Pair pair) {
		return node.get("ticker").get("last").getTextValue();
	}

}
