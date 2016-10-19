package com.pobls.lottery.guessers;

import com.pobls.lottery.Guesser;
import com.pobls.lottery.util.LogUtil;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by myltik on 18/10/2016.
 */
abstract class AbstractGuesser implements Guesser {

    // Logger
    private static final Logger logger = Logger.getLogger(AbstractGuesser.class.getName());

    /**
     * Merge guesser properties.
     * @param defaults    Default properties
     * @param custom      Specified (custom) properties
     * @return Merged properties holder instance
     */
    protected Properties mergeProperties(Properties defaults, Properties custom) {
        Properties mergedOpts = (Properties) defaults.clone();
        for (Object key : custom.keySet()) {
            LogUtil.d(logger, "Merging property [" + key + "] with value [" + custom.getProperty((String)key) + "]");
            mergedOpts.setProperty((String)key, custom.getProperty((String)key));
        }

        LogUtil.d(logger, "Guesser configuration: ");
        for (Object key : mergedOpts.keySet()) {
            LogUtil.d(logger, "  - [" + key + "] = [" + mergedOpts.getProperty((String)key) + "]");
        }

        return mergedOpts;
    }

}
