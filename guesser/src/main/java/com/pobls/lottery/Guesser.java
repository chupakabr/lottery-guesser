package com.pobls.lottery;

import java.util.Properties;

/**
 * Created by myltik on 18/10/2016.
 */
public interface Guesser {

    String PARAM_NUM_MAX = "number.max";
    String PARAM_NUM_MIN = "number.min";
    String PARAM_NUM_COUNT = "number.count";

    /**
     * Guess lottery results as much precisely as we can imagine :D
     *
     * @param opts    Options to be used to the guesser
     * @return Guessed numbers
     */
    GuessResult guess(Properties opts);

}
