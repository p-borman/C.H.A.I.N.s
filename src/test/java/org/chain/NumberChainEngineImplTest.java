package org.chain;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberChainEngineImplTest
{
    private final int one = 1;
    private final int two = 2;
    private final int three = 3;
    private final int four = 4;
    private final int five = 5;
    private final int min = one;
    private final int max = five;
    private ArrayList<Integer> ints = null;
    NumberChainEngineImpl<Integer> numberChainEngine = new NumberChainEngineImpl<Integer>();

    @Before
    public void setup(){
        ints = new ArrayList<Integer>() {{
            add(three);
            add(four);
            add(min);
            add(max);
            add(two);
        }};
    }

    @Test
    public void testShouldGetMax() {
        assertThat(numberChainEngine.max(ints)).isEqualTo(max);
    }

    @Test
    public void testShouldGetMin() {
        assertThat(numberChainEngine.min(ints)).isEqualTo(min);
    }

    @Test
    public void testShouldSumInts() {
        assertThat(numberChainEngine.sum(ints).intValue()).isEqualTo(one+two+three+four+five);
    }

    @Test
    public void testShouldSumDoubles() {
        ArrayList<Double> collection = new ArrayList<Double>() {{
            add(1.12345D);
            add(2.23456D);
            add(3.34567D);
            add(4.45678D);
            add(5.56789D);
        }};
        assertThat(new NumberChainEngineImpl<Double>().sum(collection).doubleValue()).isEqualTo(1.12345D + 2.23456D + 3.34567D + 4.45678D + 5.56789D);
    }

    @Test
    public void testShouldAverageInts() {
        BigDecimal average = numberChainEngine.average(ints);

        BigDecimal sum = new BigDecimal(0D);
        for (Integer aDouble : ints) {
            sum = sum.add(new BigDecimal(aDouble));
        }
        BigDecimal expected = sum.divide(new BigDecimal(ints.size()));
        assertThat(average).isEqualTo(expected);
    }

    @Test
    public void testShouldAverageDoubles() {
        final double v1 = 1.0012345D;
        final double v2 = 2.0123456D;
        final double v3 = 3.1234567D;
        final double v4 = 4.2345678D;
        final double v5 = 5.3456789D;
        ArrayList<Double> collection = new ArrayList<Double>() {{
            add(v1);
            add(v2);
            add(v3);
            add(v4);
            add(v5);
        }};

        BigDecimal average = new NumberChainEngineImpl<Double>().average(collection);

        BigDecimal sum = new BigDecimal(0D);
        for (Double aDouble : collection) {
            sum = sum.add(new BigDecimal(aDouble));
        }
        BigDecimal expected = sum.divide(new BigDecimal(collection.size()));
        assertThat(average.doubleValue()).isEqualTo(expected.doubleValue());
    }
}
