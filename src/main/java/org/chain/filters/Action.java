package org.chain.filters;

public interface Action<T>{
    void perform(T obj);
}
