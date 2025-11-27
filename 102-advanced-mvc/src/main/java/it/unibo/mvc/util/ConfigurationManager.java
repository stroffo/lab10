package it.unibo.mvc.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class ConfigurationManager {
    private static final String delim = ":";

    public static Configuration getConfigFromFile() {
        return getConfigFromFile("config.yml");
    }

    public static Configuration getConfigFromFile(final String resourcePath) {
        final var confBuilder = new Configuration.Builder();

        try (
            final InputStream in = ClassLoader.getSystemResourceAsStream(resourcePath);
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        ) { 
            StringTokenizer tkn = new StringTokenizer(br.readLine(), delim);
            if (tkn.nextToken().equals("minimum")) {
                confBuilder.setMin(Integer.parseInt(tkn.nextToken().trim()));
            }

            tkn = new StringTokenizer(br.readLine(), delim);
            if (tkn.nextToken().equals("maximum")) {
                confBuilder.setMax(Integer.parseInt(tkn.nextToken().trim()));
            }

            tkn = new StringTokenizer(br.readLine(), delim);
            if (tkn.nextToken().equals("attempts")) {
                confBuilder.setAttempts(Integer.parseInt(tkn.nextToken().trim()));
            }
            
            in.close();
        } catch (Exception e) {
            // Yet to find a way to properly handle exceptions...
        }

        return confBuilder.build();
    }
}
