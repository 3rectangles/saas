package com.barraiser.onboarding.search.dao;

public enum CompanySize {
    SMALL, // start up less than 20
    MEDIUM, // startup less than 100
    LARGE; // more than 100

    public static CompanySize fromString(String str) {
        switch (str) {
            case "Starups < 20":
                return SMALL;
            case "big 100 - 1000":
                return LARGE;
            default:
                return MEDIUM;
        }
    }
}
