package org.cobbzilla.telephony;

public interface TelephonyClient {

    public void setConfiguration (TelephonyClientConfiguration configuration);

    public void send(TelephonyMessage message) throws Exception;

    public String render(TelephonyMessage message) throws Exception;

}