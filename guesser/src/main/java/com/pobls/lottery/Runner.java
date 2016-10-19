package com.pobls.lottery;

import com.pobls.lottery.guessers.LookBehindGuesser;
import com.pobls.lottery.util.LogUtil;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple guesser runner. Good luck :)
 *
 * Created by myltik on 18/10/2016.
 */
public class Runner {

    // Logger
    private static final Logger logger = Logger.getLogger(Runner.class.getName());

    // Guesser property key
    private static final String PARAM_GUESSER_NAME = "guesser.name";
    // Debug logging disabled by default
    private static final String PARAM_LOG_DEBUG = "log.debug";

    /**
     * Runner of the specified guesser.
     * @param args    Guesser name as the only passed parameter (optional)
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, ParseException {
        // Read Guesser configuration from external properties file
        CommandLineParser cliParser = new DefaultParser();
        Options options = new Options();
        Option configFile = Option.builder("c")
                .hasArg()
                .desc("configuration file (.properties). See examples in config/ directory.")
                .argName("properties-file-path")
                .build();
        options.addOption(configFile);
        CommandLine cli = cliParser.parse(options, args);

        if (!cli.hasOption("c")) {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp("lottery-guesser", options);
            System.exit(1);
            return;
        }

        // Load configuration from the file
        Properties opts = loadPropertiesFile(cli.getOptionValue("c"));

        // Enable/Disable debug logging based on user settings, enabled by default
        LogUtil.isDebug = Boolean.valueOf(opts.getProperty(PARAM_LOG_DEBUG, Boolean.FALSE.toString()));

        LogUtil.d(logger, "Using guesser: " + opts.getProperty(PARAM_GUESSER_NAME));
        LogUtil.d(logger, "Custom parameters: " + opts);

        // Run the guesser and print 10 results
        Guesser guesser = GuesserFactory.create(opts.getProperty(PARAM_GUESSER_NAME));
        for (int i = 1; i <= 10; ++i) {
            LogUtil.l(logger, "Result #" + i + ": " + guesser.guess(opts));
        }
    }

    /**
     * Load properties from the file.
     * @param configFilePath    Path to a file
     * @return Read properties object
     */
    private static Properties loadPropertiesFile(String configFilePath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFilePath));
        return properties;
    }
}
