package org.chain;

import java.math.BigDecimal;

public interface NumberChainBuilder<T extends Number & Comparable> extends ChainBuilder<T>
{
    //access
    T min();
    T max();
    BigDecimal sum();
    BigDecimal average();
}
