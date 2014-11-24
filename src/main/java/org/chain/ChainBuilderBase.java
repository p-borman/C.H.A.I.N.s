package org.chain;


import org.chain.filters.Action;
import org.chain.filters.ManySelector;
import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;

import java.util.*;

/**
 * Provides a fluent API to operate on a any kind of collection.
 * @param <T> The type found in the {@link Collection} in the chain.
 * @param <E> The type of {@link ChainEngine} to use
 */
public abstract class ChainBuilderBase<T,E extends ChainEngine<T>> implements ChainBuilder<T> {
    protected E engine;
    protected Collection<T> collection;

    /**
     * @param engine The {@link ChainEngine} that will be used to preform all actions or modifications
     * @param collection The {@link Collection} that actions or modifications will be made on.
     */
    protected ChainBuilderBase(E engine, Collection<T> collection)
    {
        this.engine = engine;
        this.collection = collection;
    }

    /**
     * @return The result of the {@link Collection} chain as an {@link List}.
     */
    @Override
    public List<T> toList() {
        return new ArrayList<T>(collection);
    }

    /**
     * Preforms an action or modification for each element in the {@link Collection}.
     * @param action The action or modification to preform on each element.
     */
    @Override
    public void each(Action<T> action) {
        engine.each(collection,action);
    }

    /**
     * Sorts the {@link Collection} based on the {@link Comparator} that is passed in.
     * <br/>{ [ 3, 1, 4, 2 ] } => { [ 1, 2, 3, 4 ] }
     * @param comparator Defines the order to sort the list by.
     * @return A {@link ChainBuilder} that contains a new sorted version of the {@link Collection}.
     */
    @Override
    public ChainBuilder<T> sort(Comparator<T> comparator) {
        collection = engine.sort(collection, comparator);
        return this;
    }

    /**
     * Reverses the order of the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } => { [ 4, 3, 2, 1 ] }
     * @return A {@link ChainBuilder} that contains a new reversed order version of the {@link Collection}.
     */
    @Override
    public ChainBuilder<T> reverse() {
        collection = engine.reverse(collection);
        return this;
    }

    /**
     * Appends a {@link Collection} to the end of the contained {@link Collection}.
     * <br/>{ [ 1, 2 ] } concatenate { [ 3, 4 ] } => { [ 1, 2, 3, 4 ] }
     * @param collection2 The {@link Collection} to append to the end of the contained {@link Collection}.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} that is a concatenated version of the contained {@link Collection} and the {@link Collection} passed in.
     */
    @Override
    public ChainBuilder<T> concatenate(Collection<T> collection2) {
        collection = engine.concatenate(collection, collection2);
        return this;
    }

    /**
     * Builds a new {@link Collection} that contains all distinct elements from both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } diverge { [ 2, 3, 4 ] } => { [ 1, 2, 3, 4 ] }
     * @param collection2 The {@link Collection} to union with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is distinct.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all distinct elements from both {@link Collection}s.
     */
    @Override
    public ChainBuilder<T> union(Collection<T> collection2,Comparator<T> comparator) {
        collection = engine.union(collection, collection2, comparator);
        return this;
    }

    /**
     * Builds a new {@link Collection} that contains all elements found in both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } intersect { [ 2, 3, 4 ] } => { [ 2, 3 ] }
     * @param collection2 The {@link Collection} to intersect with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is found in both {@link Collection}s.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements found in both {@link Collection}s.
     */
    @Override
    public ChainBuilder<T> intersect(Collection<T> collection2,Comparator<T> comparator) {
        collection = engine.intersect(collection, collection2, comparator);
        return this;
    }

    /**
     * Builds a new {@link Collection} that contains all elements not found in both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } diverge { [ 2, 3, 4 ] } => { [ 1, 4 ] }
     * @param collection2 The {@link Collection} to diverge with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is found in both {@link Collection}s.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements not found in both {@link Collection}s.
     */
    @Override
    public ChainBuilder<T> diverge(Collection<T> collection2, Comparator<T> comparator) {
        collection = engine.diverge(collection, collection2, comparator);
        return this;
    }

    /**
     * Removes all elements found in the {@link Collection} already.
     * <br/>{ [ 1, 1, 2, 2, 3 ] } => { [ 1, 2, 3 ] }
     * @param comparator Defines how to check if an element is found in the {@link Collection} already.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements not found in the {@link Collection} already.
     */
    @Override
    public ChainBuilder<T> distinct(Comparator<T> comparator) {
        collection = engine.distinct(collection, comparator);
        return this;
    }

    /**
     * Selects a field from each element and concatenates them all into a new {@link Collection}.
     * <br/>{ [ {a:1}, {a:2}, {a:3}, {a:4} ]  } select(a) => { [ 1, 2, 3, 4 ] }
     * @param selector Defines which field to select from each element.
     * @param <TD> The type of the field to be selected from each element.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all of the fields that were selected from each element.
     */
    @Override
    public <TD> ChainBuilder<TD> select(Selector<T, TD> selector) {
        return new Chain<TD>(engine.select(collection, selector));
    }

    /**
     * Selects a field containing a {@link Collection} from each element and concatenates all subelements into a new {@link Collection}.
     * <br/>{ [ {a:[1,  2]}, {a:[3, 4]} ] } select(a) => { [ 1, 2, 3, 4 ] }
     * @param selector Defines which field to select from each element.
     * @param <TD> The type of the field to be selected from each element.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all of the fields that were selected from each element.
     */
    @Override
    public <TD> ChainBuilder<TD> selectMany(ManySelector<T, TD> selector) {
        return new Chain<TD>(engine.selectMany(collection, selector));
    }

    /**
     * Selects all elements that meet the condition specified into a new {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } where(a >= 2) => { [ 2, 3, 4 ] }
     * @param comparator Defines the condition to be met for each element.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements that meet the condition specified.
     */
    @Override
    public ChainBuilder<T> where(WhereComparator<T> comparator) {
        collection = engine.where(collection, comparator);
        return this;
    }

    /**
     * Checks if any element meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } any(a >= 2) => true
     * <br/>{ [ 1, 2, 3, 4 ] } any(a >= 200) => false
     * @param comparator Defines the condition to be met for each element.
     * @return True if any element meet the condition specified in the {@link Collection}.
     */
    @Override
    public Boolean any(WhereComparator<T> comparator) {
        return engine.any(collection, comparator);
    }

    /**
     * Checks if any elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } any => true
     * <br/>{ [ ] } any => false
     * @return True if any elements are in the {@link Collection}.
     */
    @Override
    public Boolean any() {
        return engine.any(collection);
    }

    /**
     * Checks if no elements meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } none(a >= 2) => false
     * <br/>{ [ 1, 2, 3, 4 ] } none(a >= 200) => true
     * @param comparator Defines the condition to be met for each element.
     * @return True if no elements meet the condition specified in the {@link Collection}.
     */
    @Override
    public Boolean none(WhereComparator<T> comparator) {
        return engine.none(collection, comparator);
    }

    /**
     * Checks if any elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } none => false
     * <br/>{ [ ] } none => true
     * @return True if no elements are in the {@link Collection}.
     */
    @Override
    public Boolean none() {
        return engine.none(collection);
    }

    /**
     * Counts how many elements meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } count(a >= 2) => 3
     * <br/>{ [ 1, 2, 3, 4 ] } count(a >= 200) => 0
     * @param comparator Defines the condition to be met for each element.
     * @return How many elements meet the condition specified in the {@link Collection}.
     */
    @Override
    public Integer count(WhereComparator<T> comparator) {
        return engine.count(collection, comparator);
    }

    /**
     * Counts how many elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } count => 4
     * @return How many elements are in the {@link Collection}.
     */
    @Override
    public Integer count() {
        return engine.count(collection);
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } first => { 1 }
     * <br/>{ [ ] } first => IndexOutOfBoundsException
     * @return The first element from the collection.
     * @throws IndexOutOfBoundsException
     */
    @Override
    public T first() throws IndexOutOfBoundsException{
        return engine.first(collection);
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull => { 1 }
     * <br/>{ [ ] } firstOrNull => { null }
     * @return The first element of the {@link Collection} or null if the {@link Collection} is empty.
     */
    @Override
    public T firstOrNull()
    {
        return engine.firstOrNull(collection);
    }

    /**
     * Selects {@link Collection} for the first element that meets the condition.
     * <br/>{ [ 1, 2, 3, 4 ] } first(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } first(a > 200) => NoSuchElementException
     * <br/>{ [ ] } first(a > 2) => NoSuchElementException
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The first element that meets the condition specified.
     * @throws NoSuchElementException If no matching element found.
     */
    @Override
    public T first(WhereComparator<T> comparator) throws NoSuchElementException
    {
        return engine.first(collection, comparator);
    }

    /**
     * Selects {@link Collection} for the first element that meets the condition, and returns null if no match found.
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull(a > 200) => { null }
     * <br/>{ [ ] } firstOrNull(a > 2) => { null }
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The first element that meets the condition specified, or null if no match found.
     */
    @Override
    public T firstOrNull(WhereComparator<T> comparator)
    {
        return engine.firstOrNull(collection,comparator);
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } last => { 4 }
     * <br/>{ [ ] } last => IndexOutOfBoundsException
     * @return The last element from the collection.
     * @throws IndexOutOfBoundsException
     */
    @Override
    public T last() throws IndexOutOfBoundsException {
        return engine.last(collection);
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull => { 4 }
     * <br/>{ [ ] } lastOrNull => { null }
     * @return The last element of the {@link Collection} or null if the {@link Collection} is empty.
     */
    @Override
    public T lastOrNull()
    {
        return engine.lastOrNull(collection);
    }

    /**
     * Selects {@link Collection} for the last element that meets the condition.
     * <br/>{ [ 1, 2, 3, 4 ] } last(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } last(a > 200) => NoSuchElementException
     * <br/>{ [ ] } last(a > 2) => NoSuchElementException
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The last element that meets the condition specified.
     * @throws NoSuchElementException If no matching element found.
     */
    @Override
    public T last(WhereComparator<T> comparator) throws NoSuchElementException
    {
        return engine.last(collection,comparator);
    }

    /**
     * Selects {@link Collection} for the first element that meets the condition, and returns null if no match found.
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull(a > 200) => { null }
     * <br/>{ [ ] } lastOrNull(a > 2) => { null }
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The last element that meets the condition specified, or null if no match found.
     */
    @Override
    public T lastOrNull(WhereComparator<T> comparator)
    {
        return engine.lastOrNull(collection,comparator);
    }

    /**
     * @param index
     * @return The element from the collection at the given index.
     */
    @Override
    public T at(int index) {
        return engine.at(collection, index);
    }

    /**
     * Skips the given number of elements in the {@link Collection} and returns the rest in a new {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } skip(2) => { [ 3, 4 ] }
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements with the specified number removed from the front of the array.
     */
    @Override
    public ChainBuilder<T> skip(int numberToSkip) {
        collection = engine.skip(collection, numberToSkip);
        return this;
    }

    /**
     * Takes the given number of elements from the front of the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } take(2) => { [ 1, 2 ] }
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing the given number of elements from the front of the {@link Collection}.
     */
    @Override
    public ChainBuilder<T> take(int numberToTake) {
        collection = engine.take(collection, numberToTake);
        return this;
    }
}
