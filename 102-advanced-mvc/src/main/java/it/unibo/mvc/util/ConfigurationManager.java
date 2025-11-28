package it.unibo.mvc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class ConfigurationManager {
    private static final String delim = ":";
    private static final String defaultConf = "config.yml";

    private Configuration loadedConfiguration;

    public void loadConfiguration() throws Exception {
        loadConfiguration(defaultConf);
    }

    public void loadConfiguration(final String resourcePath) throws Exception {
        final InputStream in = ClassLoader.getSystemResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("Couldn't find config file: " + resourcePath);
        }

        final var confBuilder = new Configuration.Builder();
        try (
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
        } catch (final Exception e) {
            throw e;
        } finally {
            in.close();
        }
        
        loadedConfiguration = confBuilder.build();
    }

    /**
     * 
     * @return the loaded coniguration; <code>null</code> if the configuration has not been loaded
     */
    public Configuration getloadedConfiguration() {
        return loadedConfiguration;
    }
}
