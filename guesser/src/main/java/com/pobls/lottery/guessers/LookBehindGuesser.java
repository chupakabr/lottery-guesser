package com.pobls.lottery.guessers;

import com.pobls.lottery.GuessResult;
import com.pobls.lottery.Guesser;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This guesser implements my initial thoughts on UK National Lottery draw history:
 * 1. It's very unlikely that more than 1 number in the next draw will be picked from last two draws.
 * 2. Next lottery draw cannot have 5 or 6 in the winning set which intersecting with a winning draw in the
 *    last 10 games. 4 Numbers is possible.
 * 3. Numbers from the last winning draw has a chance of 5% (1/20) to be in the next winning draw.
 * 4. Numbers from the last two winning draws have a chance of 20% (1/5) to be in the next winning draw.
 * 5. Numbers from the last three winning draws have a chance of 40% to (2/5) be in the next winning draw.
 * 6. Numbers not from the last three winning draws have a change of 80% (4/5) to be in the next winning draw.
 *
 * Note: all these chances came up from my head after I was watching on the winning numbers for a long period
 * of time. The percentage doesn't mean than number XX will be in the next winning draw with a chance of 80%, but
 * they mean that number XX has a better chance to appear in the next draw than the ones with lower chance. These
 * percentages will be used in the algorithm reduce the possibility of the numbers to appear in the results.
 *
 * Large array approach will be used to get the resulting set based on the previous draws results and the chances
 * defined above. For example a number which wwere in the last winning draw will be presented in the array just once,
 * and the number which hasn't been selected in the last three wining draws will be presented in the array 16 times.
 * Calculation is simple:
 *   Worst chance is 5% (1/20), best chance is 80% (4/5).
 *   Let's assume that worst chance will have just one appearance in the array, so we multiply all chances by 20:
 *      Worst = 1/20 * 20 = 1
 *      Best = 4/5 * 20 = 16
 *
 * Then the algorithm will fill an array with the numbers from MIN (1) to MAX (59) given their chances, i.e.
 * for worst chance the only one number will present in the array, for the best chance - 16 numbers.
 *
 * Then to generate resulting set we are going to generate 6 random numbers in range of 1 to the size of the array
 * to get the index in our numbers array.
 *
 * NOTE: worst chance number could be changed from 1 to another position value, say 3, so the size of Big Array
 * will be increased by 3. Worst numbers will have 3 numbers in the array, while Best numbers will have 16*3 numbers
 * in the array.
 *
 * Created by myltik on 18/10/2016.
 */
public class LookBehindGuesser extends AbstractGuesser {

    /**
     * Name of the guesser
     */
    public static final String NAME = LookBehindGuesser.class.getSimpleName();

    //
    // Parameters specific for this Guesser only, see the explanation above.
    //
    public static final String PARAM_NUM_WORST_BASE     = "number.worst_base";
    public static final String PARAM_CHANCE_10_WORST     = "chance.10";
    public static final String PARAM_CHANCE_20           = "chance.20";
    public static final String PARAM_CHANCE_30_AVERAGE   = "chance.30";
    public static final String PARAM_CHANCE_40           = "chance.40";
    public static final String PARAM_CHANCE_50_BEST      = "chance.50";

    // Logger
    private static final Logger logger = Logger.getLogger(LookBehindGuesser.class.getName());

    // Default parameters
    private static final Properties DEFAULTS;
    static {
        DEFAULTS = new Properties();

        // UK National Lottery CSV URL
        DEFAULTS.setProperty("data_url", "https://www.national-lottery.co.uk/results/lotto/draw-history/csv");

        // Number bounds
        DEFAULTS.setProperty(PARAM_NUM_WORST_BASE, "1");
        DEFAULTS.setProperty(PARAM_NUM_MAX, "59");
        DEFAULTS.setProperty(PARAM_NUM_MIN, "1");
        DEFAULTS.setProperty(PARAM_NUM_COUNT, "6");

        // Chances
        DEFAULTS.setProperty(PARAM_CHANCE_10_WORST, "1/20");
        DEFAULTS.setProperty(PARAM_CHANCE_20, "1/5");
        DEFAULTS.setProperty(PARAM_CHANCE_30_AVERAGE, "2/5");
        DEFAULTS.setProperty(PARAM_CHANCE_40, "4/5");
        DEFAULTS.setProperty(PARAM_CHANCE_50_BEST, "4/5");
    }

    @Override
    public GuessResult guess(Properties opts) {
        final Properties mergedOpts = mergeProperties(DEFAULTS, opts);
        final GuessResult.Builder builder = new GuessResult.Builder();

        // TODO


        return builder.build();
    }
}
