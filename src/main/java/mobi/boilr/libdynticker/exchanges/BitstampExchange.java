package mobi.boilr.libdynticker.exchanges;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.boilr.libdynticker.core.Exchange;
import mobi.boilr.libdynticker.core.Pair;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class BitstampExchange extends Exchange {
	private static final List<Pair> pairs;
	static {
		List<Pair> tempPairs = new ArrayList<Pair>();
		tempPairs.add(new Pair("BTC", "USD"));
		pairs = Collections.unmodifiableList(tempPairs);
	}

	public BitstampExchange(long experiedPeriod) {
		super("Bitstamp", experiedPeriod);
	}

	protected String getTickerURL(Pair pair) {
		return "https://www.bitstamp.net/api/ticker/";
	}

	@Override
	public String parseJSON(JsonNode node, Pair pair) throws IOException {
		if(pair.getExchange().equals("USD") && pair.getCoin().equals("BTC")) {
			return node.get("last").getTextValue();
		} else {
			throw new IOException("Invalid Pair");
		}
	}

	@Override
	protected String getTicker(Pair pair) throws IOException {
		return parseJSON(new ObjectMapper().readTree(new URL(this.getTickerURL(pair))), pair);
	}

	@Override
	protected List<Pair> getPairsFromAPI() {
		return pairs;
	}
}