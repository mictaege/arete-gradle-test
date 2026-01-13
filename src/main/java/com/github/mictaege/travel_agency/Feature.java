package com.github.mictaege.travel_agency;

import static java.util.Optional.ofNullable;

public class Feature {

    public static final String MONITARIZATION = "com.github.mictaege.travel_agency.Feature#monitorization";

    public static boolean monitorization() {
        return ofNullable(System.getProperty(MONITARIZATION)).map(Boolean::parseBoolean).orElse(false);
    }

}
