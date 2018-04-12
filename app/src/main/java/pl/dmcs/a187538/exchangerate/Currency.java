package pl.dmcs.a187538.exchangerate;

/**
 * Created by Damian on 18.12.2016.
 */

public class Currency {
    private String code;
    private String mid;
    private String description;

    public Currency(String code, String mid, String description) {
        this.code = code;
        this.mid = mid;
        this.description = description;
    }
    @Override
    public boolean equals(Object object) {

        if (object != null && object instanceof Currency) {
            Currency currency = (Currency) object;
            if (code == null) {
                return (currency.code == null);
            }
            else {
                return code.equals(currency.code);
            }
        }

        return false;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
