package com.example.java_eloadas_spring_boot.forex;

import com.example.java_eloadas_spring_boot.MessageActPrice;
import com.oanda.v20.Context;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.InstrumentName;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class PricesController {

    @GetMapping("/actual_prices")
    public String actual_prices(Model model) {
        model.addAttribute("par", new MessageActPrice());
        return "form_actual_prices";
    }

    @PostMapping("/actual_prices")
    public String actual_prices2(@ModelAttribute MessageActPrice messageActPrice, Model model) {
        model.addAttribute("instr", messageActPrice.getInstrument());
        return "result_actual_prices"; // a chartos oldal
    }

    // --- ÚJ: JSON végpont, amit a Chart.js fog hívni ---
    @ResponseBody
    @GetMapping("/api/prices")
    public ClientPriceDto getPrice(@RequestParam("instrument") String instrument) {
        try {
            List<String> instruments = Collections.singletonList(instrument);
            PricingGetRequest request = new PricingGetRequest(Config.ACCOUNTID, instruments);
            Context ctx = new Context(Config.URL, Config.TOKEN);
            PricingGetResponse resp = ctx.pricing.get(request);

            ClientPrice cp = resp.getPrices().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No price for " + instrument));

            ClientPriceDto dto = new ClientPriceDto();
            dto.setInstrument(cp.getInstrument().toString());
            dto.setTime(cp.getTime().toString());
            dto.setStatus(String.valueOf(cp.getStatus()));
            dto.setTradeable(cp.getTradeable());

            // closeout árak + mid
            dto.setCloseoutBid(cp.getCloseoutBid().doubleValue());
            dto.setCloseoutAsk(cp.getCloseoutAsk().doubleValue());
            dto.setMid((cp.getCloseoutBid().doubleValue() + cp.getCloseoutAsk().doubleValue()) / 2.0);

            // order book (ár + likviditás)
            List<PricePoint> bids = new ArrayList<>();
            for (com.oanda.v20.pricing_common.PriceBucket b : cp.getBids()) {
                bids.add(new PricePoint(b.getPrice().doubleValue(), b.getLiquidity().longValue()));
            }
            List<PricePoint> asks = new ArrayList<>();
            for (com.oanda.v20.pricing_common.PriceBucket a : cp.getAsks()) {
                asks.add(new PricePoint(a.getPrice().doubleValue(), a.getLiquidity().longValue()));
            }
            dto.setBids(bids);
            dto.setAsks(asks);
            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Pricing error: " + e.getMessage(), e);
        }
    }

    // --- DTO-k a JSON-hoz ---
    public static class ClientPriceDto {
        private String instrument;
        private String time;
        private String status;
        private boolean tradeable;
        private double closeoutBid;
        private double closeoutAsk;
        private double mid;
        private List<PricePoint> bids;
        private List<PricePoint> asks;
        // getters/setters
        public String getInstrument() { return instrument; }
        public void setInstrument(String instrument) { this.instrument = instrument; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isTradeable() { return tradeable; }
        public void setTradeable(boolean tradeable) { this.tradeable = tradeable; }
        public double getCloseoutBid() { return closeoutBid; }
        public void setCloseoutBid(double closeoutBid) { this.closeoutBid = closeoutBid; }
        public double getCloseoutAsk() { return closeoutAsk; }
        public void setCloseoutAsk(double closeoutAsk) { this.closeoutAsk = closeoutAsk; }
        public double getMid() { return mid; }
        public void setMid(double mid) { this.mid = mid; }
        public List<PricePoint> getBids() { return bids; }
        public void setBids(List<PricePoint> bids) { this.bids = bids; }
        public List<PricePoint> getAsks() { return asks; }
        public void setAsks(List<PricePoint> asks) { this.asks = asks; }
    }
    public static class PricePoint {
        private double price;
        private long liquidity;
        public PricePoint() {}
        public PricePoint(double price, long liquidity) { this.price = price; this.liquidity = liquidity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public long getLiquidity() { return liquidity; }
        public void setLiquidity(long liquidity) { this.liquidity = liquidity; }
    }
    @GetMapping("/open_position")
    public String open_position(Model model) {
        model.addAttribute("param", new MessageOpenPosition());
        return "form_open_position";
    }
    @PostMapping("/open_position")
    public String open_position2(@ModelAttribute MessageOpenPosition messageOpenPosition, Model
            model) {
        String strOut;
        try {
            InstrumentName instrument = new InstrumentName(messageOpenPosition.getInstrument());
            OrderCreateRequest request = new OrderCreateRequest(Config.ACCOUNTID);
            MarketOrderRequest marketorderrequest = new MarketOrderRequest();
            marketorderrequest.setInstrument(instrument);
            marketorderrequest.setUnits(messageOpenPosition.getUnits());
            request.setOrder(marketorderrequest);
            Context ctx = new Context(Config.URL, Config.TOKEN);
            OrderCreateResponse response = ctx.order.create(request);
            strOut="tradeId: "+response.getOrderFillTransaction().getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("instr", messageOpenPosition.getInstrument());
        model.addAttribute("units", messageOpenPosition.getUnits());
        model.addAttribute("id", strOut);
        return "result_open_position";
    }
}
