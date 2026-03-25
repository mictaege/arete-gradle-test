package com.github.mictaege.travel_agency;

public enum Country {
    DE("Germany"), AT("Austria"), CH("Switzerland"), FR("France"), IT("Italy"), NL("Netherlands"),
    US("United States"), GB("United Kingdom"), ES("Spain"), BE("Belgium"), CA("Canada"),
    AU("Australia"), BR("Brazil"), CN("China"), DK("Denmark"), FI("Finland"), GR("Greece"),
    HU("Hungary"), IN("India"), IE("Ireland"), JP("Japan"), KR("South Korea"), LU("Luxembourg"),
    MX("Mexico"), NO("Norway"), PL("Poland"), PT("Portugal"), RU("Russia"), SE("Sweden"),
    TR("Turkey"), ZA("South Africa"), AR("Argentina"), CL("Chile"), CO("Colombia"), CZ("Czech Republic"),
    EG("Egypt"), IL("Israel"), MY("Malaysia"), NZ("New Zealand"), PH("Philippines"), SG("Singapore"),
    TH("Thailand"), VN("Vietnam"), AE("United Arab Emirates"), SA("Saudi Arabia"), PK("Pakistan"),
    BD("Bangladesh"), ID("Indonesia"), NG("Nigeria"), UA("Ukraine"), RO("Romania"), PE("Peru");

    private String name;

    Country(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
