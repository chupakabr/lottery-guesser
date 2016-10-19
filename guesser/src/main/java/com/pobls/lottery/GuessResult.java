package com.pobls.lottery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Basic results holder for {@link Guesser}.
 *
 * Created by myltik on 18/10/2016.
 */
public class GuessResult {

    private final List<Collection<Integer>> results;

    /**
     * @param results    Built result set holder
     */
    GuessResult(List<Collection<Integer>> results) {
        this.results = results;
    }

    /**
     * Results builder. Simple helper class.
     */
    public static class Builder {

        private final List<Collection<Integer>> results = new ArrayList<>();

        /**
         * @param resultSet    Add result set into the holder
         * @return This instance
         */
        public Builder add(Collection<Integer> resultSet) {
            results.add(resultSet);
            return this;
        }

        /**
         * Produce result set holder.
         * @return Result set holder
         */
        public GuessResult build() {
            return new GuessResult(results);
        }
    }

    /**
     * @return Numeric resulting set, found first
     */
    public Collection<Integer> numbers() {
        return numbers(0);
    }

    /**
     * @param index    Index of the result set, starting from 0 up to count()
     * @return Numeric resulting set identified by index
     */
    public Collection<Integer> numbers(int index) {
        if (index > count()) {
            throw new IndexOutOfBoundsException("Cannot get results for index " + index + ", results count is " + count());
        }
        return results.get(index);
    }

    /**
     * @return True is there are at least one result set available, false otherwise
     */
    public boolean hasResults() {
        return results != null && results.size() > 0;
    }

    /**
     * @return Number of resulting sets available
     */
    public int count() {
        return results != null ? results.size() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < count(); ++i) {
            sb.append("\t" + i + ": ");
            sb.append(Arrays.toString(numbers(i).toArray()));
            sb.append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }
}
