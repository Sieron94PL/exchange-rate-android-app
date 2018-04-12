package pl.dmcs.a187538.exchangerate;

/**
 * Created by Damian on 17.12.2016.
 */

public class CurrencyDetails {
    private String currency;
    private String code;
    private String bid;
    private String ask;

    public CurrencyDetails(String currency, String code, String bid, String ask) {
        this.currency = currency;
        this.code = code;
        this.bid = bid;
        this.ask = ask;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public String getBid() {
        return bid;
    }

    public String getAsk() {
        return ask;
    }


}
