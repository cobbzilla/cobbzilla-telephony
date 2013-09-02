package org.cobbzilla.telephony;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Properties;

@NoArgsConstructor
public class TelephonyClientConfiguration {

    @NotNull @Getter @Setter private String endpoint;
    @NotNull @Getter @Setter private String templateBaseDir;
    @NotNull @Getter @Setter private Properties properties;

}
