package currencyconverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable value object describing a single currency: ISO code, display
 * name, symbol, country and the flag emoji used in the UI.
 */
public final class Currency {

    private final String code;
    private final String name;
    private final String symbol;
    private final String country;
    private final String flag;

    public Currency(String code, String name, String symbol, String country, String flag) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.country = country;
        this.flag = flag;
    }

    public String getCode()    { return code; }
    public String getName()    { return name; }
    public String getSymbol()  { return symbol; }
    public String getCountry() { return country; }
    public String getFlag()    { return flag; }

    /** True when the search term matches the code, name or country. */
    public boolean matches(String term) {
        if (term == null || term.isBlank()) return true;
        String t = term.trim().toLowerCase();
        return code.toLowerCase().contains(t)
                || name.toLowerCase().contains(t)
                || country.toLowerCase().contains(t);
    }

    @Override
    public String toString() {
       return  code + "  -  " + name;
    }

    /** The catalogue of currencies supported by the application. */
    public static List<Currency> catalogue() {
        List<Currency> list = new ArrayList<>();
        list.add(new Currency("USD", "United States Dollar", "$",  "United States", "\uD83C\uDDFA\uD83C\uDDF8"));
        list.add(new Currency("INR", "Indian Rupee",         "\u20B9", "India",      "\uD83C\uDDEE\uD83C\uDDF3"));
        list.add(new Currency("EUR", "Euro",                 "\u20AC", "European Union", "\uD83C\uDDEA\uD83C\uDDFA"));
        list.add(new Currency("GBP", "British Pound Sterling","\u00A3","United Kingdom", "\uD83C\uDDEC\uD83C\uDDE7"));
        list.add(new Currency("AED", "UAE Dirham",           "\u062F.\u0625", "United Arab Emirates", "\uD83C\uDDE6\uD83C\uDDEA"));
        list.add(new Currency("AUD", "Australian Dollar",    "A$", "Australia",     "\uD83C\uDDE6\uD83C\uDDFA"));
        list.add(new Currency("CAD", "Canadian Dollar",      "C$", "Canada",        "\uD83C\uDDE8\uD83C\uDDE6"));
        list.add(new Currency("JPY", "Japanese Yen",         "\u00A5", "Japan",     "\uD83C\uDDEF\uD83C\uDDF5"));
        list.add(new Currency("CNY", "Chinese Yuan",         "\u00A5", "China",     "\uD83C\uDDE8\uD83C\uDDF3"));
        list.add(new Currency("CHF", "Swiss Franc",          "Fr", "Switzerland",   "\uD83C\uDDE8\uD83C\uDDED"));
        list.add(new Currency("SGD", "Singapore Dollar",     "S$", "Singapore",     "\uD83C\uDDF8\uD83C\uDDEC"));
        list.add(new Currency("NZD", "New Zealand Dollar",   "NZ$", "New Zealand",  "\uD83C\uDDF3\uD83C\uDDFF"));
        list.add(new Currency("SAR", "Saudi Riyal",          "\uFDFC", "Saudi Arabia", "\uD83C\uDDF8\uD83C\uDDE6"));
        list.add(new Currency("QAR", "Qatari Riyal",         "QR", "Qatar",         "\uD83C\uDDF6\uD83C\uDDE6"));
        list.add(new Currency("KWD", "Kuwaiti Dinar",        "KD", "Kuwait",        "\uD83C\uDDF0\uD83C\uDDFC"));
        list.add(new Currency("ZAR", "South African Rand",   "R",  "South Africa",  "\uD83C\uDDFF\uD83C\uDDE6"));
        list.add(new Currency("BRL", "Brazilian Real",       "R$", "Brazil",        "\uD83C\uDDE7\uD83C\uDDF7"));
        list.add(new Currency("RUB", "Russian Ruble",        "\u20BD", "Russia",    "\uD83C\uDDF7\uD83C\uDDFA"));
        list.add(new Currency("MXN", "Mexican Peso",         "Mex$", "Mexico",      "\uD83C\uDDF2\uD83C\uDDFD"));
        list.add(new Currency("MYR", "Malaysian Ringgit",    "RM", "Malaysia",      "\uD83C\uDDF2\uD83C\uDDFE"));
        list.add(new Currency("THB", "Thai Baht",            "\u0E3F", "Thailand",  "\uD83C\uDDF9\uD83C\uDDED"));
        list.add(new Currency("KRW", "South Korean Won",     "\u20A9", "South Korea", "\uD83C\uDDF0\uD83C\uDDF7"));
        list.add(new Currency("SEK", "Swedish Krona",        "kr", "Sweden",        "\uD83C\uDDF8\uD83C\uDDEA"));
        list.add(new Currency("NOK", "Norwegian Krone",      "kr", "Norway",        "\uD83C\uDDF3\uD83C\uDDF4"));
        list.add(new Currency("DKK", "Danish Krone",         "kr", "Denmark",       "\uD83C\uDDE9\uD83C\uDDF0"));
        list.add(new Currency("PLN", "Polish Zloty",         "z\u0142", "Poland",   "\uD83C\uDDF5\uD83C\uDDF1"));
        list.add(new Currency("TRY", "Turkish Lira",         "\u20BA", "Turkey",    "\uD83C\uDDF9\uD83C\uDDF7"));
        list.add(new Currency("IDR", "Indonesian Rupiah",    "Rp", "Indonesia",     "\uD83C\uDDEE\uD83C\uDDE9"));
        list.add(new Currency("PHP", "Philippine Peso",      "\u20B1", "Philippines", "\uD83C\uDDF5\uD83C\uDDED"));
        list.add(new Currency("BDT", "Bangladeshi Taka",     "\u09F3", "Bangladesh", "\uD83C\uDDE7\uD83C\uDDE9"));
        list.add(new Currency("LKR", "Sri Lankan Rupee",     "Rs", "Sri Lanka",     "\uD83C\uDDF1\uD83C\uDDF0"));
        list.add(new Currency("PKR", "Pakistani Rupee",      "\u20A8", "Pakistan",  "\uD83C\uDDF5\uD83C\uDDF0"));
        list.add(new Currency("NPR", "Nepalese Rupee",       "\u20A8", "Nepal",     "\uD83C\uDDF3\uD83C\uDDF5"));
        list.add(new Currency("HKD", "Hong Kong Dollar",     "HK$", "Hong Kong",    "\uD83C\uDDED\uD83C\uDDF0"));
        list.add(new Currency("EGP", "Egyptian Pound",       "E\u00A3", "Egypt",    "\uD83C\uDDEA\uD83C\uDDEC"));
        return list;
    }
}
