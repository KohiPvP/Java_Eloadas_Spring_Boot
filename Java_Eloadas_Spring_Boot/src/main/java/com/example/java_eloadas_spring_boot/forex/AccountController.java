package com.example.java_eloadas_spring_boot.forex;

import com.example.java_eloadas_spring_boot.MessageActPrice;
import com.oanda.v20.Context;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AccountController {

    private AccountSummary getAccountSummary() {
        Context ctx =  new Context(Config.URL, Config.TOKEN);

        try {
            return ctx.account.summary(Config.ACCOUNTID).getAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/forex_accounts")
    public String summary(Model model) {
        AccountSummary summary = getAccountSummary();
        assert summary != null;
        AccountSummaryView view = AccountSummaryView.fromApi(summary);
        model.addAttribute("accountSummary", view);
        return "forex_accounts";
    }
}
