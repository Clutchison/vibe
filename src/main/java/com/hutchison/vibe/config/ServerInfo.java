package com.hutchison.vibe.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerInfo implements ApplicationListener<WebServerInitializedEvent> {

    private static String hostname;
    private static int port;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        WebServer webServer = webServerInitializedEvent.getWebServer();
        if(webServer instanceof TomcatWebServer) {
            TomcatWebServer tc = (TomcatWebServer) webServer;
            hostname = tc.getTomcat().getHost().getName();
            port = tc.getPort();
        }
    }

    public static String getHostname() {
        return hostname;
    }

    public static int getPort() {
        return port;
    }
}
