package com.pobls.lottery.guessers;

import com.pobls.lottery.GuessResult;
import com.pobls.lottery.util.LogUtil;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * This guesser implements my initial thoughts on UK National Lottery draw history:
 * 1. It's very unlikely that more than 1 number in the next draw will be picked from last two draws.
 * 2. Next lottery draw cannot have 5 or 6 in the winning set which intersecting with a winning draw in the
 *    last 10 games. 4 Numbers is possible.
 * 3. Numbers from the last winning draw has a chance of 5% (1/20) to be in the next winning draw.
 * 4. Numbers from the last two winning draws have a chance of 20% (1/5) to be in the next winning draw.
 * 5. Numbers from the last three winning draws have a chance of 40% to (2/5) be in the next winning draw.
 * 6. Numbers not from the last three winning draws have a chance of 80% (4/5) to be in the next winning draw.
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
 * in the array. (not implemented)
 *
 * @note Simulator based on previous lottery draws: http://graphics.latimes.com/powerball-simulator/
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
    public static final String PARAM_CHANCE_10_WORST     = "chance.10";
    public static final String PARAM_CHANCE_20           = "chance.20";
    public static final String PARAM_CHANCE_30           = "chance.30";
    public static final String PARAM_CHANCE_40           = "chance.40";
    public static final String PARAM_CHANCE_50_AVERAGE   = "chance.50";
    public static final String PARAM_CHANCE_60           = "chance.60";
    public static final String PARAM_CHANCE_70           = "chance.70";
    public static final String PARAM_CHANCE_80           = "chance.80";
    public static final String PARAM_CHANCE_90_BEST      = "chance.90";
    public static final String PARAM_WINNING_DRAWS       = "winning_draws";
    public static final String PARAM_DATA_URL            = "data.url";
    public static final String PARAM_DATA_START_IDX      = "data.start_idx";

    // Logger
    private static final Logger logger = Logger.getLogger(LookBehindGuesser.class.getName());

    // Basically the number of winning draws to consider minus 1.
    // This number MUST BE less than a number of defined chances. Just for this simple algorithm.
    private static final int NUMBER_OF_WINNING_DRAWS_TO_CONSIDER = 3;

    // Randomizer coefficient, i.e. how many times the algorithm should warm-up randomizer prior to getting the result
    private static final int RANDOMIZER_COEFFICIENT = 10;

    // Default parameters
    private static final Properties DEFAULTS;
    static {
        DEFAULTS = new Properties();

        // UK National Lottery CSV URL
        DEFAULTS.setProperty(PARAM_DATA_URL, "https://www.national-lottery.co.uk/results/lotto/draw-history/csv");
        DEFAULTS.setProperty(PARAM_DATA_START_IDX, "1");

        // Number bounds
        DEFAULTS.setProperty(PARAM_NUM_MAX, "59");
        DEFAULTS.setProperty(PARAM_NUM_MIN, "1");
        DEFAULTS.setProperty(PARAM_NUM_COUNT, "6");

        // Winning draws to consider
        DEFAULTS.setProperty(PARAM_WINNING_DRAWS, "3");

        // Chances
        DEFAULTS.setProperty(PARAM_CHANCE_10_WORST,     "1/20");
        DEFAULTS.setProperty(PARAM_CHANCE_20,           "1/5");
        DEFAULTS.setProperty(PARAM_CHANCE_30,           "2/5");
        DEFAULTS.setProperty(PARAM_CHANCE_40,           "4/5");
        DEFAULTS.setProperty(PARAM_CHANCE_50_AVERAGE,   "4/5");
        DEFAULTS.setProperty(PARAM_CHANCE_60,           "4/5");
        DEFAULTS.setProperty(PARAM_CHANCE_70,           "4/5");
        DEFAULTS.setProperty(PARAM_CHANCE_80,           "4/5");
        DEFAULTS.setProperty(PARAM_CHANCE_90_BEST,      "4/5");
    }

    @Override
    public GuessResult guess(Properties opts) throws IOException {
        final Properties mergedOpts = mergeProperties(DEFAULTS, opts);
        final int winningDrawsToConsider = Integer.valueOf(mergedOpts.getProperty(PARAM_WINNING_DRAWS));
        final GuessResult.Builder builder = new GuessResult.Builder();
        builder.add(predictResult(mergedOpts, loadWinningSets(mergedOpts, winningDrawsToConsider)));
        return builder.build();
    }

    /**
     * Predict result of the next draw based on the historical data.
     * @param mergedOpts            Merged options
     * @param winningSetsHistory    History of winning sets
     * @return Predicted results based on the algorithm and assumptions described above in this class's comment
     */
    private Collection<Integer> predictResult(Properties mergedOpts, final Set<Integer>[] winningSetsHistory) {
        // Initialize parameters
        final int maxNum = Integer.valueOf(mergedOpts.getProperty(PARAM_NUM_MAX));
        final int minNum = Integer.valueOf(mergedOpts.getProperty(PARAM_NUM_MIN));
        final int numCount = Integer.valueOf(mergedOpts.getProperty(PARAM_NUM_COUNT));
        final FractionFormat fractionParser = new FractionFormat();
        final Fraction[] chances = new Fraction[]{
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_10_WORST)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_20)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_30)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_40)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_50_AVERAGE)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_60)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_70)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_80)),
            fractionParser.parse(mergedOpts.getProperty(PARAM_CHANCE_90_BEST)),
        };

        // Fill available data array of integers with given probability per number layers
        final List<Integer> availableNumbers = generateAvailableNumbers(minNum, maxNum, winningSetsHistory, chances);

        // Shuffle available data array randomly for few times
        for (int i = 0; i < RANDOMIZER_COEFFICIENT; ++i) {
            Collections.shuffle(availableNumbers);
        }

        // Run few times to warm up the randomizer. why?:)
        Random random = new Random();
        for (int i = 0; i < RANDOMIZER_COEFFICIENT; ++i) {
            pickUniqueRandomNumbers(random, availableNumbers, numCount);
        }
        // Predict next winning match!
        return pickUniqueRandomNumbers(random, availableNumbers, numCount);
    }

    /**
     * Pick some unique random numbers.
     * @param random              Randomizer
     * @param availableNumbers    Available numbers spread set with given availability chance
     * @param numCount            Number of numbers to pick :)
     * @return Unique randomly picked numbers
     */
    private Collection<Integer> pickUniqueRandomNumbers(Random random, List<Integer> availableNumbers, int numCount) {
        Set<Integer> pickedNumbers = new HashSet<>(numCount);

        int tmpNum;
        while (numCount-- > 0) {
            do {
                tmpNum = random.nextInt(availableNumbers.size());
            } while (pickedNumbers.contains(availableNumbers.get(tmpNum)));

            pickedNumbers.add(availableNumbers.get(tmpNum));
        }

        return pickedNumbers;
    }

    /**
     * Generate available numbers collection with given chance of appearance.
     * @param minNum                Minimum possible number
     * @param maxNum                Maximum possible number
     * @param winningSetsHistory    History of winning sets
     * @param chances               Given chances
     * @return Collection of available numbers given chance of appearance
     */
    private List<Integer> generateAvailableNumbers(int minNum, int maxNum, final Set<Integer>[] winningSetsHistory, Fraction[] chances) {
        // Fill in the map of yet unused numbers, inclusive min and max
        Set<Integer> unusedNumbers = new HashSet<>(maxNum-minNum+1);
        for (int i = minNum; i <= maxNum; ++i) {
            unusedNumbers.add(i);
        }

        LogUtil.d(logger, "Numbers in the set: " + Arrays.toString(unusedNumbers.toArray()));
        LogUtil.d(logger, "Numbers count before applying winning sets is " + unusedNumbers.size());

        // First: fill available numbers array based on winning history
        final List<Integer> availableNumbers = new ArrayList<>(maxNum);
        final Fraction worstChance = chances[0];
        for (int iteration = 0; iteration < winningSetsHistory.length; ++iteration) {
            int inclusionFactor = iteration == 0 ? 1 : getInclusionFactor(chances[iteration], worstChance);
            LogUtil.d(logger, "  - inclusion factor for #" + iteration + " chance of " + chances[iteration] + " is " + inclusionFactor);

            while (inclusionFactor-- > 0) {
                availableNumbers.addAll(winningSetsHistory[iteration]);
            }

            unusedNumbers.removeAll(winningSetsHistory[iteration]);
        }

        LogUtil.d(logger, "Unused numbers count after applying winning sets is " + unusedNumbers.size());

        // Second: fill available numbers array using all the rest number and the best chance coefficient
        for (int k = 0; k < getInclusionFactor(chances[chances.length-1], worstChance); ++k) {
            availableNumbers.addAll(unusedNumbers);
        }

        LogUtil.d(logger, "Final available numbers array size is " + availableNumbers.size());
        return availableNumbers;
    }

    /**
     * @param currentIterationChance    Current iteration chance
     * @param worstChance               Worst chance
     * @return Inclusion factor for specified iteration
     */
    private int getInclusionFactor(Fraction currentIterationChance, Fraction worstChance) {
        return (currentIterationChance.getNumerator() * worstChance.getDenominator())
                / (worstChance.getNumerator() * currentIterationChance.getDenominator());
    }

    /**
     * Load required number of last winning sets from Internet.
     * @param mergedOpts             Merged options
     * @param numberOfWinningSets    Number of winning set to load
     * @return Loaded winnings sets history
     * @throws IOException
     */
    private Set<Integer>[] loadWinningSets(Properties mergedOpts, int numberOfWinningSets) throws IOException {
        final int dataStartIndex = Integer.valueOf(mergedOpts.getProperty(PARAM_DATA_START_IDX));
        final int numbersInDraw = Integer.valueOf(mergedOpts.getProperty(PARAM_NUM_COUNT));
        final Set<Integer>[] winningSets = new HashSet[numberOfWinningSets];

        // Load data from CSV
        try (InputStream is = new URL(mergedOpts.getProperty(PARAM_DATA_URL)).openConnection().getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            reader.readLine(); // skip first line as it's CSV header
            String line;
            for (int i = 0; i < numberOfWinningSets && (line = reader.readLine()) != null; ++i) {
                winningSets[i] = parseCsvLine(line, numbersInDraw, dataStartIndex);
            }
        }

        return winningSets;
    }

    /**
     * @param csvLine           CSV line to get the winning match from
     * @param numbersInDraw     Numbers in a draw
     * @param dataStartIndex    An index of the first number in CSV line
     * @return Parsed winning numbers of specified draw
     */
    private Set<Integer> parseCsvLine(String csvLine, int numbersInDraw, int dataStartIndex) {
        String[] values = csvLine.split(",");
        final Set<Integer> drawNumbers = new HashSet<>(numbersInDraw);

        for (int i = dataStartIndex, k = 0; i < dataStartIndex+numbersInDraw; ++i, ++k) {
            drawNumbers.add(Integer.valueOf(values[i]));
        }

        return drawNumbers;
    }
}
