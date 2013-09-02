package org.cobbzilla.telephony.tropo;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.cobbzilla.telephony.TelephonyClientBase;
import org.cobbzilla.telephony.TelephonyClientConfiguration;
import org.cobbzilla.telephony.TelephonyMessage;
import org.cobbzilla.util.http.HttpStatusCodes;
import org.cobbzilla.util.mustache.LocaleAwareMustacheFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;

import static java.net.URLEncoder.encode;
import static org.cobbzilla.util.string.StringUtil.UTF8;

public class TropoTelephonyClient extends TelephonyClientBase {

    public static final String PROP_TOKEN = "token";
    public static final String PROP_LABEL = "label";

    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_DESTINATION = "destination";
    public static final String PARAM_CONTENT = "content";

    private final File fileRoot;
    private final String token;
    private final String label;
    private final String labelSuffix;

    public TropoTelephonyClient(TelephonyClientConfiguration configuration) {
        setConfiguration(configuration);
        token = configuration.getProperties().getProperty(PROP_TOKEN);
        label = configuration.getProperties().getProperty(PROP_LABEL);
        labelSuffix = "." + label;
        fileRoot = new File(configuration.getTemplateBaseDir());
        if (!fileRoot.exists() || !fileRoot.canRead()) {
            throw new IllegalArgumentException("Cannot read templateBaseDir: "+configuration.getTemplateBaseDir());
        }
    }

    @Override
    public void send(TelephonyMessage message) throws Exception {

        final LocaleAwareMustacheFactory mustache = LocaleAwareMustacheFactory.getFactory(fileRoot, message.getLocale());
        final String body = mustache.render(message.getTemplate()+labelSuffix, message.getScope());

        final StringBuilder uri = new StringBuilder(configuration.getEndpoint()).append("?action=create");
        addParam(uri, PARAM_TOKEN, token);
        addParam(uri, PARAM_DESTINATION, message.getDestination());
        addParam(uri, PARAM_CONTENT, body);

        final HttpResponse response = getHttpClient().execute(new HttpGet(uri.toString()));
        final StatusLine statusLine = response.getStatusLine();

        if (statusLine.getStatusCode() != HttpStatusCodes.OK) {
            throw new IllegalStateException(label+" send failed: "+ statusLine.getStatusCode()+": "+statusLine.getReasonPhrase());
        }
    }

    private void addParam(StringBuilder uri, String param, String value) throws UnsupportedEncodingException {
        uri.append("&").append(param).append("=").append(encode(value, UTF8));
    }

}
