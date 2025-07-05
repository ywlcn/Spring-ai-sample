package com.sample;


import java.io.File;

import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;

/**
 * With stdio transport, the MCP server is automatically started by the client.
 * But you
 * have to build the server jar first:
 *
 * <pre>
 * ./mvnw clean install -DskipTests
 * </pre>
 */
public class ClientStdio {

    public static void main(String[] args) {

        System.out.println(new File(".").getAbsolutePath());

        var stdioParams = ServerParameters.builder("java")
                .args("-Dspring.ai.mcp.server.stdio=true", "-Dspring.main.web-application-type=none",
                        "-Dlogging.pattern.console=", "-jar",
                        "F:\\VSWorkSpace\\000-ai\\SpringAI\\MCP-weather\\build\\libs\\MCP-weather-0.0.1-SNAPSHOT.jar")
                .build();

        var transport = new StdioClientTransport(stdioParams);

        new SampleClient(transport).run();
    }

}