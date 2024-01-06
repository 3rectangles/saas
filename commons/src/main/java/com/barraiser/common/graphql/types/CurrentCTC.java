package com.barraiser.common.graphql.types;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CurrentCTC {
    ZERO_THREE_LACS,
    THREE_FIVE_LACS,
    FIVE_EIGHT_LACS,
    EIGHT_TWELVE_LACS,
    TWELEVE_TWENTY_LACS,
    TWENTY_THIRTY_LACS,
    THIRTY_FOURTY_LACS,
    MORE_THAN_FOURTY_LACS;

    @JsonCreator
    public CurrentCTC fromString(final String val) {
        return CurrentCTC.valueOf(val.toUpperCase());
    }
}
