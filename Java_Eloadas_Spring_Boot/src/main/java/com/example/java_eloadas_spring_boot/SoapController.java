package com.example.java_eloadas_spring_boot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import soapclient.MNBArfolyamServiceSoap;
import soapclient.MNBArfolyamServiceSoapGetExchangeRatesStringFaultFaultMessage;
import soapclient.MNBArfolyamServiceSoapImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SoapController {
    @GetMapping("/soap")
    public String soap1(Model model) {
        model.addAttribute("param", new MessagePrice());
        return "form";
    }

    @PostMapping("/soap")
    public String soap2(@ModelAttribute MessagePrice messagePrice, Model model)
            throws MNBArfolyamServiceSoapGetExchangeRatesStringFaultFaultMessage {

        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = impl.getCustomBindingMNBArfolyamServiceSoap();

        String xml = service.getExchangeRates(
                messagePrice.getStartDate(),
                messagePrice.getEndDate(),
                messagePrice.getCurrency()
        );

        List<RatePoint> points = parseMnbXml(xml, messagePrice.getCurrency());

        points.sort(Comparator.comparing(RatePoint::getDate));

        List<String> labels = points.stream().map(p -> p.getDate().toString()).collect(Collectors.toList());
        List<BigDecimal> series = points.stream().map(RatePoint::getRate).collect(Collectors.toList());

        String strOut = "Currency: " + messagePrice.getCurrency() + "; " +
                "Start date: " + messagePrice.getStartDate() + "; " +
                "End date: " + messagePrice.getEndDate() + "; " +
                "Items: " + points.size();

        model.addAttribute("sendOut", strOut);
        model.addAttribute("labels", labels);
        model.addAttribute("series", series);
        model.addAttribute("currency", messagePrice.getCurrency());

        return "result";
    }

    private List<RatePoint> parseMnbXml(String xml, String currency) {
        List<RatePoint> out = new ArrayList<>();
        if (xml == null || xml.isEmpty()) return out;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xml)));

            NodeList dayNodes = doc.getElementsByTagName("Day");
            for (int i = 0; i < dayNodes.getLength(); i++) {
                Element dayEl = (Element) dayNodes.item(i);
                String dateStr = dayEl.getAttribute("date"); // pl. 2024-05-03
                LocalDate date = LocalDate.parse(dateStr);

                NodeList rateNodes = dayEl.getElementsByTagName("Rate");
                for (int r = 0; r < rateNodes.getLength(); r++) {
                    Element rateEl = (Element) rateNodes.item(r);
                    String curr = rateEl.getAttribute("curr"); // pl. EUR
                    if (!currency.equalsIgnoreCase(curr)) continue;

                    // MNB sokszor vesszőt használ tizedeselválasztónak
                    String txt = rateEl.getTextContent().trim().replace(",", ".");
                    // Ha unit attribútum > 1, akkor (érték / unit) is lehet – itt feltételezzük unit=1,
                    // de ha biztosra akarsz menni:
                    int unit = 1;
                    String unitAttr = rateEl.getAttribute("unit");
                    if (unitAttr != null && !unitAttr.isEmpty()) {
                        try { unit = Integer.parseInt(unitAttr); } catch (NumberFormatException ignored) {}
                    }

                    BigDecimal raw = new BigDecimal(txt);
                    BigDecimal normalized = (unit > 1) ? raw.divide(BigDecimal.valueOf(unit)) : raw;

                    out.add(new RatePoint(date, normalized));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }
}
