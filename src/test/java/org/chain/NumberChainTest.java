package org.chain;

import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;
import org.chain.model.TestClass;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

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
        assertThat(new NumberChain<Double>(collection).sum().doubleValue()).isEqualTo(1.12345D + 2.23456D + 3.34567D + 4.45678D + 5.56789D);
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


    @Test
    public void testShouldModifyEachObject()
    {
        List<Integer> modified = intChain
                .select(new Selector<Integer, Integer>() {
                    public Integer select(Integer obj) {
                        return obj + 5;
                    }
                })
                .toList();

        for (int i = 0; i < modified.size(); i++){
            assertThat(modified.get(i)).isEqualTo(ints.get(i) + 5);
        }
    }

    @Test
    public void testShouldModifyEachNumber() {
        List<Integer> integers = new ArrayList<Integer>();
        final int num = 5;
        for(int i = 0; i < num; i++){
            integers.add(i);
        }

        integers = new Chain<Integer>(integers).select(new Selector<Integer, Integer>() {
            public Integer select(Integer obj) {
                return obj+num;
            }
        }).toList();


        for(int i = 0; i < num; i++){
            assertThat(integers.get(i)).isEqualTo(i + num);
        }
    }

    @Test
    public void testShouldSort() {
        Collection<Integer> sorted = intChain
                .sort(new Comparator<Integer>()
                {
                    public int compare(Integer o1, Integer o2)
                    {
                        return new Integer(o1).compareTo(o2);
                    }
                })
                .toList();

        ArrayList<Integer> sort = new ArrayList<Integer>(sorted);
        assertThat(sort).hasSize(ints.size());
        for (int i = 0; i < sort.size() - 1; i++){
            assertThat(sort.get(i)).isEqualTo(i+1);
        }
    }

    @Test
    public void testShouldReverse() {

        List<Integer> reverse = intChain.reverse().toList();

        assertThat(reverse).hasSize(ints.size());
        for (int i = 0; i < reverse.size() - 1; i++){
            assertThat(reverse.get(i)).isEqualTo(ints.get(reverse.size() - 1 - i));
        }
    }

    @Test
    public void testShouldSelect() {
        Collection<Integer> select = intChain
                .select(new Selector<Integer, Integer>()
                {
                    public Integer select(Integer obj)
                    {
                        return obj;
                    }
                })
                .toList();

        assertThat(select)
                .hasSize(5)
                .contains(1,2,3,4,5);
    }

    @Test
    public void testShouldFilterUsingWhere() {
        Collection<Integer> where = intChain
                .where(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj > 3;
                    }
                }).toList();

        assertThat(where)
                .hasSize(2)
                .contains(ints.get(1),
                        ints.get(3));
    }

    @Test
    public void testShouldCheckIfCollectionIsNull() {
        assertThat(new Chain<Integer>(null).isNullOrEmpty()).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsEmpty() {
        assertThat(new Chain<Integer>(new ArrayList<Integer>()).isNullOrEmpty()).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsNotNullOrEmpty() {
        assertThat(new Chain<Integer>(ints).isNullOrEmpty()).isFalse();
    }

    @Test
    public void testShouldCheckAllMatchCondition() {
        Boolean any = intChain
                .all(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj >= 0;
                    }
                });

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckAllDoNotMatchCondition() {
        Boolean any = intChain
                .all(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj > 3;
                    }
                });

        assertThat(any).isFalse();
    }

    @Test
    public void testShouldCheckAnyMatchCondition() {
        Boolean any = intChain
                .any(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj > 3;
                    }
                });

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckAnyInCollection() {
        Boolean any = intChain.any();

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckNoneMatchCondition() {
        Boolean none = intChain
                .none(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj > 30000;
                    }
                });

        assertThat(none).isTrue();
    }

    @Test
    public void testShouldCheckNoneInCollection() {
        intChain = new NumberChain<Integer>(new ArrayList<Integer>());

        Boolean none = intChain.none();

        assertThat(none).isTrue();
    }

    @Test
    public void testShouldGetQuantityMatchCondition() {
        Integer count = intChain
                .count(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj > 3;
                    }
                });

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testShouldGetQuantityInCollection() {
        Integer count = intChain.count();

        assertThat(count).isEqualTo(ints.size());
    }

    @Test
    public void testShouldGetFirst() {
        Integer first = intChain.first();

        assertThat(first).isEqualTo(ints.get(0));
    }

    @Test
    public void testShouldGetNullIfNoFirst() {
        Integer first = intChain.where(new WhereComparator<Integer>()
        {
            public boolean meetsCondition(Integer obj)
            {
                return obj > 30000;
            }
        }).firstOrNull();

        assertThat(first).isNull();
    }

    @Test
    public void testShouldGetFirstMatchingCondition() {
        Integer first = intChain.first(new WhereComparator<Integer>()
        {
            public boolean meetsCondition(Integer obj)
            {
                return obj == 3;
            }
        });

        assertThat(first).isEqualTo(ints.get(0));
    }

    @Test
    public void testShouldThrowErrorWhenGettingFirstMatchingCondition() {
        NoSuchElementException error = null;
        try
        {
            intChain.first(new WhereComparator<Integer>()
            {
                public boolean meetsCondition(Integer obj)
                {
                    return obj == 30000;
                }
            });
        }
        catch (NoSuchElementException e) { error = e; }

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo(
                "No element matching given comparator was found in the collection.");
    }

    @Test
    public void testShouldGetFirstOrNullMatchingCondition() {
        Integer first = intChain.firstOrNull(new WhereComparator<Integer>()
        {
            public boolean meetsCondition(Integer obj)
            {
                return obj == 3;
            }
        });

        assertThat(first).isEqualTo(ints.get(0));
    }

    @Test
    public void testShouldGetNullWhenGettingFirstOrNullMatchingCondition() {

        final Integer testClass = intChain
                .firstOrNull(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj == 30000;
                    }
                });

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetLast() {
        Integer last = intChain.last();

        assertThat(last).isEqualTo(ints.get(ints.size() - 1));
    }

    @Test
    public void testShouldGetNullIfNoLast() {
        Integer last = intChain.where(new WhereComparator<Integer>()
        {
            public boolean meetsCondition(Integer obj)
            {
                return obj > 30000;
            }
        }).lastOrNull();

        assertThat(last).isNull();
    }

    @Test
    public void testShouldGetLastMatchingCondition() {
        Integer last = intChain.last(new WhereComparator<Integer>()
        {
            public boolean meetsCondition(Integer obj)
            {
                return obj == 3;
            }
        });

        assertThat(last).isEqualTo(ints.get(0));
    }

    @Test
    public void testShouldThrowErrorWhenGettingLastMatchingCondition() {
        NoSuchElementException error = null;
        try
        {
            intChain.last(new WhereComparator<Integer>()
            {
                public boolean meetsCondition(Integer obj)
                {
                    return obj == 30000;
                }
            });
        }
        catch (NoSuchElementException e) { error = e; }

        assertThat(error).isNotNull();
        assertThat(error.getMessage()).isEqualTo(
                "No element matching given comparator was found in the collection.");
    }

    @Test
    public void testShouldGetLastOrNullMatchingCondition() {
        Integer last = intChain.lastOrNull(new WhereComparator<Integer>()
        {
            public boolean meetsCondition(Integer obj)
            {
                return obj == 3;
            }
        });

        assertThat(last).isEqualTo(ints.get(0));
    }

    @Test
    public void testShouldGetNullWhenGettingLastOrNullMatchingCondition() {

        final Integer testClass = intChain
                .lastOrNull(new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj == 30000;
                    }
                });

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetValueAtIndex() {
        int index = 1;

        Integer at = intChain.at(index);

        assertThat(at).isEqualTo(ints.get(index));
    }

    @Test
    public void testShouldFilterDistinct() {
        final int one = 1;
        final int two = 2;
        final int three = 3;
        final int four = 4;
        ArrayList<Integer> numsWithDupes = new ArrayList<Integer>() {{
            add(three);
            add(two);
            add(one);
            add(two);
            add(four);
            add(one);
        }};

        Collection<Integer> distinct = new Chain<Integer>(numsWithDupes)
                .distinct(new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);
                    }
                })
                .toList();

        assertThat(distinct)
                .hasSize(4)
                .contains(one, two, three, four);
    }

    @Test
    public void testShouldConcatenate() {
        final int one = 1;
        final int two = 2;
        final int three = 3;
        final int four = 4;
        ArrayList<Integer> nums1 = new ArrayList<Integer>() {{
            add(one);
            add(two);
        }};
        ArrayList<Integer> nums2 = new ArrayList<Integer>() {{
            add(three);
            add(four);
        }};

        Collection<Integer> concatenate = new Chain<Integer>(nums1)
                .concatenate(nums2)
                .toList();

        assertThat(concatenate)
                .hasSize(4)
                .contains(one, two, three, four);
    }

    @Test
    public void testShouldUnion() {
        final int one = 1;
        final int two = 2;
        final int three = 3;
        final int four = 4;
        ArrayList<Integer> nums1 = new ArrayList<Integer>() {{
            add(one);
            add(two);
            add(three);
        }};
        ArrayList<Integer> nums2 = new ArrayList<Integer>() {{
            add(two);
            add(three);
            add(four);
        }};

        Collection<Integer> concatenate = new Chain<Integer>(nums1)
                .union(nums2, new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);
                    }
                })
                .toList();

        assertThat(concatenate)
                .hasSize(4)
                .contains(one, two, three, four);
    }

    @Test
    public void testShouldIntersect() {
        final int one = 1;
        final int two = 2;
        final int three = 3;
        final int four = 4;
        ArrayList<Integer> nums1 = new ArrayList<Integer>() {{
            add(one);
            add(two);
            add(three);
        }};
        ArrayList<Integer> nums2 = new ArrayList<Integer>() {{
            add(two);
            add(three);
            add(four);
        }};

        Collection<Integer> concatenate = new Chain<Integer>(nums1)
                .intersect(nums2, new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);
                    }
                })
                .toList();

        assertThat(concatenate)
                .hasSize(2)
                .contains(two, three);
    }

    @Test
    public void testShouldDiverge() {
        final int one = 1;
        final int two = 2;
        final int three = 3;
        final int four = 4;
        ArrayList<Integer> nums1 = new ArrayList<Integer>() {{
            add(one);
            add(two);
            add(three);
        }};
        ArrayList<Integer> nums2 = new ArrayList<Integer>() {{
            add(two);
            add(three);
            add(four);
        }};

        Collection<Integer> concatenate = new Chain<Integer>(nums1)
                .diverge(nums2, new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);
                    }
                })
                .toList();

        assertThat(concatenate)
                .hasSize(2)
                .contains(one, four);
    }

    @Test
    public void testShouldSkip() {
        Collection<Integer> skip = intChain.skip(2).toList();

        assertThat(skip)
                .hasSize(ints.size()-2)
                .contains(ints.get(2),
                        ints.get(3),
                        ints.get(4));
    }

    @Test
    public void testShouldTake() {
        Collection<Integer> skip = intChain.take(2).toList();

        assertThat(skip)
                .hasSize(2)
                .contains(ints.get(0),
                        ints.get(1));
    }
}
