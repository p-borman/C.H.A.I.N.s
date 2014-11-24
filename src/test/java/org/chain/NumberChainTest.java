package org.chain;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberChainTest
{
    private final int one = 1;
    private final int two = 2;
    private final int three = 3;
    private final int four = 4;
    private final int five = 5;
    private final int min = one;
    private final int max = five;
    private ArrayList<Integer> ints = null;
    NumberChainBuilder<Integer> intChain = null;

    @Before
    public void setup(){
        ints = new ArrayList<Integer>() {{
            add(three);
            add(four);
            add(min);
            add(max);
            add(two);
        }};
        intChain = new NumberChain<Integer>(ints);
    }

    @Test
    public void testShouldGetMax() {
        assertThat(intChain.max()).isEqualTo(max);
    }

    @Test
    public void testShouldGetMin() {
        assertThat(intChain.min()).isEqualTo(min);
    }

    @Test
    public void testShouldSumInts() {
        assertThat(intChain.sum().intValue()).isEqualTo(one+two+three+four+five);
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
        assertThat(new NumberChain(collection).sum().doubleValue()).isEqualTo(1.12345D + 2.23456D + 3.34567D + 4.45678D + 5.56789D);
    }

    @Test
    public void testShouldAverageInts() {
        double actual = intChain.average().doubleValue();

        double expected = intChain.sum().divide(new BigDecimal(ints.size())).doubleValue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testShouldAverageDoubles() {
        ArrayList<Double> collection = new ArrayList<Double>() {{
            add(1.12345D);
            add(2.23456D);
            add(3.34567D);
            add(4.45678D);
            add(5.56789D);
        }};
        NumberChain<Double> numberChain = new NumberChain<Double>(collection);

        double actual = numberChain.average().doubleValue();

        double expected = numberChain.sum().divide(new BigDecimal(collection.size())).doubleValue();
        assertThat(actual).isEqualTo(expected);
    }
}
