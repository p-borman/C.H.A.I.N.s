package org.chain;

import java.math.BigDecimal;
import java.util.Collection;

interface NumberChainEngine<T extends Number & Comparable> extends ChainEngine<T>
{
    //access
    T min(final Collection<T> collection);
    T max(final Collection<T> collection);
    BigDecimal sum(final Collection<T> collection);
    BigDecimal average(final Collection<T> collection);
}
