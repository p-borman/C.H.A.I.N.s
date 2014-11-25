package org.chain;

import org.chain.filters.Action;
import org.chain.filters.ManySelector;
import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public interface ChainBuilder<T> {
    /**
     * @return The result of the {@link Collection} chain as an {@link List}.
     */
    List<T> toList();

    //mod
    /**
     * Preforms an action using each element in the {@link Collection}.
     * @param action The action or modification to preform on each element.
     */
    void each(final Action<T> action);
    /**
     * Sorts the {@link Collection} based on the {@link Comparator} that is passed in.
     * <br/>{ [ 3, 1, 4, 2 ] } => { [ 1, 2, 3, 4 ] }
     * @param comparator Defines the order to sort the list by.
     * @return A {@link ChainBuilder} that contains a new sorted version of the {@link Collection}.
     */
    ChainBuilder<T> sort(final Comparator<T> comparator);
    /**
     * Reverses the order of the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } => { [ 4, 3, 2, 1 ] }
     * @return A {@link ChainBuilder} that contains a new reversed order version of the {@link Collection}.
     */
    ChainBuilder<T> reverse();
    /**
     * Appends a {@link Collection} to the end of the contained {@link Collection}.
     * <br/>{ [ 1, 2 ] } concatenate { [ 3, 4 ] } => { [ 1, 2, 3, 4 ] }
     * @param collection2 The {@link Collection} to append to the end of the contained {@link Collection}.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} that is a concatenated version of the contained {@link Collection} and the {@link Collection} passed in.
     */
    ChainBuilder<T> concatenate(Collection<T> collection2);
    /**
     * Builds a new {@link Collection} that contains all distinct elements from both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } diverge { [ 2, 3, 4 ] } => { [ 1, 2, 3, 4 ] }
     * @param collection2 The {@link Collection} to union with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is distinct.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all distinct elements from both {@link Collection}s.
     */
    ChainBuilder<T> union(Collection<T> collection2, final Comparator<T> comparator);
    /**
     * Builds a new {@link Collection} that contains all elements found in both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } intersect { [ 2, 3, 4 ] } => { [ 2, 3 ] }
     * @param collection2 The {@link Collection} to intersect with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is found in both {@link Collection}s.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements found in both {@link Collection}s.
     */
    ChainBuilder<T> intersect(Collection<T> collection2, final Comparator<T> comparator);
    /**
     * Builds a new {@link Collection} that contains all elements not found in both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } diverge { [ 2, 3, 4 ] } => { [ 1, 4 ] }
     * @param collection2 The {@link Collection} to diverge with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is found in both {@link Collection}s.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements not found in both {@link Collection}s.
     */
    ChainBuilder<T> diverge(Collection<T> collection2, Comparator<T> comparator);
    /**
     * Removes all elements found in the {@link Collection} already.
     * <br/>{ [ 1, 1, 2, 2, 3 ] } => { [ 1, 2, 3 ] }
     * @param comparator Defines how to check if an element is found in the {@link Collection} already.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements not found in the {@link Collection} already.
     */
    ChainBuilder<T> distinct(final Comparator<T> comparator);

    //query
    /**
     * Selects a field from each element and concatenates them all into a new {@link Collection}.
     * <br/>{ [ {a:1}, {a:2}, {a:3}, {a:4} ]  } select(a) => { [ 1, 2, 3, 4 ] }
     * @param selector Defines which field to select from each element.
     * @param <TD> The type of the field to be selected from each element.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all of the fields that were selected from each element.
     */
    <TD> ChainBuilder<TD> select(final Selector<T, TD> selector);
    /**
     * Selects a field containing a {@link Collection} from each element and concatenates all subelements into a new {@link Collection}.
     * <br/>{ [ {a:[1,  2]}, {a:[3, 4]} ] } select(a) => { [ 1, 2, 3, 4 ] }
     * @param selector Defines which field to select from each element.
     * @param <TD> The type of the field to be selected from each element.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all of the fields that were selected from each element.
     */
    <TD> ChainBuilder<TD> selectMany(final ManySelector<T, TD> selector);
    /**
     * Selects all elements that meet the condition specified into a new {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } where(a >= 2) => { [ 2, 3, 4 ] }
     * @param comparator Defines the condition to be met for each element.
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements that meet the condition specified.
     */
    ChainBuilder<T> where(final WhereComparator<T> comparator);
    /**
     * Checks if the {@link Collection} is either null or empty.
     * <br/>{ [ 1, 2, 3, 4 ] } isNullOrEmpty => { false }
     * <br/>{ [ ] } isNullOrEmpty => { true }
     * <br/>{ } isNullOrEmpty => { true }
     * @return True if the {@link Collection} is null or contains zero elements.
     */
    Boolean isNullOrEmpty();
    /**
     * Checks if any element meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } any(a >= 2) => { true }
     * <br/>{ [ 1, 2, 3, 4 ] } any(a >= 200) => { false }
     * @param comparator Defines the condition to be met for each element.
     * @return True if any element meet the condition specified in the {@link Collection}.
     */
    Boolean any(final WhereComparator<T> comparator);
    /**
     * Checks if any elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } any => { true }
     * <br/>{ [ ] } any => { false }
     * @return True if any elements are in the {@link Collection}.
     */
    Boolean any();
    /**
     * Checks if no elements meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } none(a >= 2) => { false }
     * <br/>{ [ 1, 2, 3, 4 ] } none(a >= 200) => { true }
     * @param comparator Defines the condition to be met for each element.
     * @return True if no elements meet the condition specified in the {@link Collection}.
     */
    Boolean none(final WhereComparator<T> comparator);
    /**
     * Checks if any elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } none => { false }
     * <br/>{ [ ] } none => { true }
     * @return True if no elements are in the {@link Collection}.
     */
    Boolean none();
    /**
     * Counts how many elements meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } count(a >= 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } count(a >= 200) => { 0 }
     * @param comparator Defines the condition to be met for each element.
     * @return How many elements meet the condition specified in the {@link Collection}.
     */
    Integer count(final WhereComparator<T> comparator);
    /**
     * Counts how many elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } count => { 4 }
     * @return How many elements are in the {@link Collection}.
     */
    Integer count();

    //access
    /**
     * <br/>{ [ 1, 2, 3, 4 ] } first => { 1 }
     * <br/>{ [ ] } first => IndexOutOfBoundsException
     * @return The first element from the collection.
     * @throws IndexOutOfBoundsException
     */
    T first();
    /**
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull => { 1 }
     * <br/>{ [ ] } firstOrNull => { null }
     * @return The first element of the {@link Collection} or null if the {@link Collection} is empty.
     */
    T firstOrNull();
    /**
     * Selects {@link Collection} for the first element that meets the condition.
     * <br/>{ [ 1, 2, 3, 4 ] } first(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } first(a > 200) => NoSuchElementException
     * <br/>{ [ ] } first(a > 2) => NoSuchElementException
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The first element that meets the condition specified.
     * @throws NoSuchElementException If no matching element found.
     */
    T first(final WhereComparator<T> comparator) throws NoSuchElementException;
    /**
     * Selects {@link Collection} for the first element that meets the condition, and returns null if no match found.
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull(a > 200) => { null }
     * <br/>{ [ ] } firstOrNull(a > 2) => { null }
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The first element that meets the condition specified, or null if no match found.
     */
    T firstOrNull(final WhereComparator<T> comparator);
    /**
     * <br/>{ [ 1, 2, 3, 4 ] } last => { 4 }
     * <br/>{ [ ] } last => IndexOutOfBoundsException
     * @return The last element from the collection.
     * @throws IndexOutOfBoundsException
     */
    T last();
    /**
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull => { 4 }
     * <br/>{ [ ] } lastOrNull => { null }
     * @return The last element of the {@link Collection} or null if the {@link Collection} is empty.
     */
    T lastOrNull();
    /**
     * Selects {@link Collection} for the last element that meets the condition.
     * <br/>{ [ 1, 2, 3, 4 ] } last(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } last(a > 200) => NoSuchElementException
     * <br/>{ [ ] } last(a > 2) => NoSuchElementException
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The last element that meets the condition specified.
     * @throws NoSuchElementException If no matching element found.
     */
    T last(final WhereComparator<T> comparator) throws NoSuchElementException;
    /**
     * Selects {@link Collection} for the first element that meets the condition, and returns null if no match found.
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull(a > 200) => { null }
     * <br/>{ [ ] } lastOrNull(a > 2) => { null }
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The last element that meets the condition specified, or null if no match found.
     */
    T lastOrNull(final WhereComparator<T> comparator);
    /**
     * <br/>{ [ 1, 2, 3, 4 ] } at(2) => { 3 }
     * @param index
     * @return The element from the collection at the given index.
     */
    T at(final int index);
    /**
     * Skips the given number of elements in the {@link Collection} and returns the rest in a new {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } skip(2) => { [ 3, 4 ] }
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing all elements with the specified number removed from the front of the array.
     */
    ChainBuilder<T> skip(final int numberToSkip);
    /**
     * Takes the given number of elements from the front of the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } take(2) => { [ 1, 2 ] }
     * @return A {@link ChainBuilder} that contains a new {@link Collection} containing the given number of elements from the front of the {@link Collection}.
     */
    ChainBuilder<T> take(final int numberToTake);
}
