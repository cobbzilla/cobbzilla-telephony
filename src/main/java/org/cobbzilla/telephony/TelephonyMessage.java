package org.cobbzilla.telephony;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor @AllArgsConstructor
public class TelephonyMessage {

    @Getter @Setter private String destination;
    @Getter @Setter private String template;
    @Getter @Setter private String locale;
    @Getter @Setter private Map<String, Object> scope;

}
