package com.pobls.lottery;

import com.pobls.lottery.guessers.LookBehindGuesser;

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

    /**
     * Runner of the specified guesser.
     * @param args    Guesser name as the only passed parameter (optional)
     */
    public static void main(String[] args) {
        // Guesser name can be defined as CLI option
        String guesserName = LookBehindGuesser.NAME;
        if (args.length >= 2) {
            guesserName = args[1];
        }

        // TODO Opts from CLI?
        Properties opts = new Properties();
        logger.log(Level.INFO, "Using guesser: " + guesserName);
        logger.log(Level.INFO, "Custom parameters: " + opts);

        // Run the guesser and print the result
        Guesser guesser = GuesserFactory.create(guesserName);
        logger.log(Level.INFO, "Result: " + guesser.guess(opts));
    }
}
