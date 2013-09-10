package org.cobbzilla.telephony.tropo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.cobbzilla.util.io.FileUtil;
import org.cobbzilla.util.string.StringUtil;
import org.cobbzilla.telephony.TelephonyClientConfiguration;
import org.cobbzilla.telephony.TelephonyMessage;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

@Slf4j
public class TropoTelephonyClientTest {

    public static final int TEST_PORT = 19071;
    public static final String TEST_TEMPLATE = "testTemplate";
    public static final String CLIENT_LABEL = "sms";
    public static final String TEST_TEMPLATE_MUSTACHE = TEST_TEMPLATE + "." + CLIENT_LABEL + ".mustache";
    public static final String TEST_PARAM = "param";

    private Server server;
    private TropoHandler tropoSmsHandler;
    private File templateBaseDir;

    class TropoHandler extends AbstractHandler {

        @Getter private String lastToken;
        @Getter private String lastDestination;
        @Getter private String lastContent;

        @Override
        public synchronized void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            // do nothing
            TropoTelephonyClientTest.log.info("handle called.");
            response.setStatus(200);
            final Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                response.addHeader(headerName, request.getHeader(headerName));
            }
            lastToken = request.getParameter(TropoTelephonyClient.PARAM_TOKEN);
            lastDestination = request.getParameter(TropoTelephonyClient.PARAM_DESTINATION);
            lastContent = request.getParameter(TropoTelephonyClient.PARAM_CONTENT);
            String output = "<session><success>true</success><token>" + lastToken + "</token><id>fa799203dabb4b7b2ac49cb4c64e919f</id></session>";
            response.getWriter().write(output);
            response.getWriter().flush();
            response.setContentLength(output.length());
        }
    }

    @Before public void writeTempTemplate() throws Exception {
        templateBaseDir = FileUtil.createTempDir(getClass().getSimpleName());
        final String resourcePath = TEST_TEMPLATE_MUSTACHE;
        final File outFile = new File(templateBaseDir, TEST_TEMPLATE_MUSTACHE);
        FileUtil.writeResourceToFile(resourcePath, outFile, getClass());
    }

    @Before public void startServer () throws Exception {
        server = new Server(TEST_PORT);
        tropoSmsHandler = new TropoHandler();
        server.setHandler(tropoSmsHandler);
        server.start();
    }

    @After public void stopServer () throws Exception { server.stop(); }

    @Test public void testSendSms () throws Exception {

        final String token = RandomStringUtils.randomAlphanumeric(30);
        final String destination = "+1"+RandomStringUtils.randomNumeric(10);
        final String content = RandomStringUtils.randomAlphanumeric(30);

        final TelephonyClientConfiguration configuration = new TelephonyClientConfiguration();
        configuration.setEndpoint("http://127.0.0.1:" + TEST_PORT + "/1.0/sessions");
        configuration.setTemplateBaseDir(templateBaseDir.getAbsolutePath());
        final Properties props = new Properties();
        props.setProperty(TropoTelephonyClient.PROP_TOKEN, token);
        props.setProperty(TropoTelephonyClient.PROP_LABEL, CLIENT_LABEL);
        configuration.setProperties(props);

        final TropoTelephonyClient tropoClient = new TropoTelephonyClient(configuration);
        final Map<String, Object> scope = new HashMap<>();
        scope.put(TEST_PARAM, content);
        tropoClient.send(new TelephonyMessage(destination, TEST_TEMPLATE, StringUtil.DEFAULT_LOCALE, scope));

        Assert.assertEquals(token, tropoSmsHandler.getLastToken());
        Assert.assertEquals(destination, tropoSmsHandler.getLastDestination());
        assertTrue(tropoSmsHandler.getLastContent().contains(content));
    }

}
