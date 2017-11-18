package mobi.boilr.libdynticker.exchanges;

import mobi.boilr.libdynticker.core.Pair;
import mobi.boilr.libdynticker.core.exception.NoMarketDataException;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BinanceExchange extends mobi.boilr.libdynticker.core.Exchange {
    BinanceExchange(long expiredPeriod) {
        super("Binance", expiredPeriod);
    }

    @Override
    protected List<Pair> getPairsFromAPI() throws IOException {
        JsonNode jsonNode = readJsonFromUrl("https://www.binance.com/api/v1/ticker/allPrices");
        ArrayList<Pair> pairs = new ArrayList<>();
        for (JsonNode node:jsonNode) {
            String symbol = node.get("symbol").asText();
            if(symbol.chars().allMatch(Character::isLetter)){
                String coin = symbol.replace(Exchange.matchExchange(), "");
                String exchange = symbol.replace(coin, "");
                pairs.add(new Pair(coin, exchange));
            }
        }
        return pairs;
    }

    @Override
    protected String getTicker(Pair pair) throws IOException, NoMarketDataException {
        JsonNode node = readJsonFromUrl("https://www.binance.com/api/v1/ticker/24hr?symbol=" + pair.getCoin() + pair.getExchange());
        if(node.has("priceChange")) {
            return parseTicker(node, pair);
        }
        throw new NoMarketDataException(pair);
    }

    @Override
    public String parseTicker(JsonNode node, Pair pair) throws IOException, NoMarketDataException {
        return node.get("lastPrice").asText();
    }

    private enum Exchange {
        BTC,
        ETH,
        BNB,
        USDT;

        public static String matchExchange(){
            StringBuilder sb = new StringBuilder("\\");
            Iterator<Exchange> iterator = Arrays.asList(Exchange.values()).iterator();
            while(iterator.hasNext()){
                sb.append(iterator.next());
                if(iterator.hasNext()){
                    sb.append("|");
                }else {
                    sb.append("$");
                }
            }
            return sb.toString();
        }
    }
}
