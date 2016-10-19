package com.pobls.lottery;

import com.pobls.lottery.guessers.LookBehindGuesser;

/**
 * Created by myltik on 18/10/2016.
 */
public class GuesserFactory {

    /**
     * @param guesserName    Guesser name
     * @return Guesser instance
     * @throws IllegalStateException if specified guesser is not supported
     */
    static Guesser create(String guesserName) throws IllegalStateException {
        if (LookBehindGuesser.NAME.equals(guesserName)) {
            return new LookBehindGuesser();
        }

        throw new IllegalStateException();
    }
}
