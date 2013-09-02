package org.cobbzilla.telephony;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class TelephonyClientBase implements TelephonyClient {

    @Getter(value=AccessLevel.PROTECTED, lazy=true) private final HttpClient httpClient = buildHttpClient();

    protected DefaultHttpClient buildHttpClient() {
        return new DefaultHttpClient();
    }

    @Setter protected TelephonyClientConfiguration configuration;

}
