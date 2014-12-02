package org.chain;

import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;
import org.chain.model.TestClass;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

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




    @Test
    public void testShouldModifyEach()
    {
        final int increment = 5;
        final List<Integer> moddedInts = new ArrayList<Integer>(numberChainEngine
                .select(ints, new Selector<Integer, Integer>()
                {
                    @Override
                    public Integer select(Integer obj)
                    {
                        return obj + increment;
                    }
                }));
        for (int i = 0; i < moddedInts.size(); i++){
            assertThat(moddedInts.get(i)).isEqualTo(ints.get(i)+increment);
        }
    }

    @Test
    public void testShouldSort() {
        ints = new ArrayList<Integer>() {{
            add(3);
            add(5);
            add(1);
            add(4);
            add(2);
        }};

        ArrayList<Integer> sort = new ArrayList<Integer>(numberChainEngine.sort(ints,
                new Comparator<Integer>()
                {
                    public int compare(Integer o1, Integer o2)
                    {
                        return new Integer(o1).compareTo(o2);
                    }
                }));

        assertThat(sort).hasSize(ints.size());
        for (int i = 0; i < sort.size() - 1; i++){
            assertThat(sort.get(i)).isEqualTo(i+1);
        }
        assertThat(ints.get(0)).isEqualTo(3);
        assertThat(ints.get(1)).isEqualTo(5);
        assertThat(ints.get(2)).isEqualTo(1);
        assertThat(ints.get(3)).isEqualTo(4);
        assertThat(ints.get(4)).isEqualTo(2);
    }

    @Test
    public void testShouldReverse() {

        ArrayList<Integer> reverse = new ArrayList<Integer>(numberChainEngine.reverse(ints));

        assertThat(reverse).hasSize(ints.size());
        for (int i = 0; i < reverse.size() - 1; i++){
            assertThat(reverse.get(i)).isEqualTo(ints.get(ints.size() - 1 - i));
        }
    }

    @Test
    public void testShouldSelect() {
        Collection<Integer> select = numberChainEngine.select(ints, new Selector<Integer, Integer>()
        {
            public Integer select(Integer obj)
            {
                return obj;
            }
        });

        assertThat(select)
                .hasSize(5)
                .contains(1, 2, 3, 4, 5);
    }

    @Test
    public void testShouldFilterUsingWhere() {
        Collection<Integer> where = numberChainEngine.where(ints,
                new WhereComparator<Integer>()
                {
                    public boolean meetsCondition(Integer obj)
                    {
                        return obj > 3;
                    }
                });

        assertThat(where)
                .hasSize(2)
                .contains(ints.get(1),
                        ints.get(3));
    }

    @Test
    public void testShouldCheckIfCollectionIsNull() {
        assertThat(numberChainEngine.isNullOrEmpty(null)).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsEmpty() {
        assertThat(numberChainEngine.isNullOrEmpty(new ArrayList<Integer>())).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsNotNullOrEmpty() {
        assertThat(numberChainEngine.isNullOrEmpty(ints)).isFalse();
    }


    @Test
    public void testShouldCheckAllMatchCondition() {
        Boolean any = numberChainEngine.all(ints, new WhereComparator<Integer>()
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
        Boolean any = numberChainEngine.all(ints, new WhereComparator<Integer>()
        {
            public boolean meetsCondition(Integer obj)
            {
                return obj >= 2;
            }
        });

        assertThat(any).isFalse();
    }

    @Test
    public void testShouldCheckAnyMatchCondition() {
        Boolean any = numberChainEngine.any(ints, new WhereComparator<Integer>()
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
        Boolean any = numberChainEngine.any(ints);

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckNoneMatchCondition() {
        Boolean none = numberChainEngine.none(ints, new WhereComparator<Integer>()
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
        Boolean none = numberChainEngine.none(new ArrayList<Integer>());

        assertThat(none).isTrue();
    }

    @Test
    public void testShouldGetQuantityMatchCondition() {
        Integer count = numberChainEngine.count(ints, new WhereComparator<Integer>()
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
        Integer count = numberChainEngine.count(ints);

        assertThat(count).isEqualTo(ints.size());
    }

    @Test
    public void testShouldGetFirst() {
        Integer first = numberChainEngine.first(ints);

        assertThat(first).isEqualTo(ints.get(0));
    }

    @Test
    public void testShouldGetNullIfNoFirst()
    {
        final Integer testClass = numberChainEngine.firstOrNull(new ArrayList<Integer>());

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetFirstMatchingCondition() {
        Integer first = numberChainEngine.first(ints, new WhereComparator<Integer>()
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
            numberChainEngine.first(ints, new WhereComparator<Integer>()
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
        Integer first = numberChainEngine.firstOrNull(ints, new WhereComparator<Integer>()
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

        final Integer testClass = numberChainEngine
                .firstOrNull(ints, new WhereComparator<Integer>()
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
        Integer last = numberChainEngine.last(ints);

        assertThat(last).isEqualTo(ints.get(ints.size() - 1));
    }

    @Test
    public void testShouldGetNullIfNoLast()
    {
        final Integer testClass = numberChainEngine.lastOrNull(new ArrayList<Integer>());

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetLastMatchingCondition() {
        Integer last = numberChainEngine.last(ints, new WhereComparator<Integer>()
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
            numberChainEngine.last(ints, new WhereComparator<Integer>()
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
        Integer last = numberChainEngine.lastOrNull(ints, new WhereComparator<Integer>()
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

        final Integer testClass = numberChainEngine
                .lastOrNull(ints, new WhereComparator<Integer>()
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

        Integer at = numberChainEngine.at(ints, index);

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

        Collection<Integer> distinct = new ChainEngineImpl<Integer>().distinct(numsWithDupes, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

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

        Collection<Integer> concatenate = new ChainEngineImpl<Integer>().concatenate(nums1, nums2);

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

        Collection<Integer> concatenate = new ChainEngineImpl<Integer>()
                .union(nums1, nums2, new Comparator<Integer>()
                {
                    public int compare(Integer o1, Integer o2)
                    {
                        return o1.compareTo(o2);
                    }
                });

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

        Collection<Integer> concatenate = new ChainEngineImpl<Integer>().intersect(nums2, nums1,
                new Comparator<Integer>()
                {
                    public int compare(Integer o1, Integer o2)
                    {
                        return o1.compareTo(o2);
                    }
                });

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

        Collection<Integer> concatenate = new ChainEngineImpl<Integer>().diverge(nums2, nums1,
                new Comparator<Integer>()
                {
                    public int compare(Integer o1, Integer o2)
                    {
                        return o1.compareTo(o2);
                    }
                });

        assertThat(concatenate)
                .hasSize(2)
                .contains(one, four);
    }

    @Test
    public void testShouldSkip() {
        Collection<Integer> skip = numberChainEngine.skip(ints, 2);

        assertThat(skip)
                .hasSize(ints.size()-2)
                .contains(ints.get(2),
                        ints.get(3),
                        ints.get(4));
    }

    @Test
    public void testShouldTake() {
        Collection<Integer> skip = numberChainEngine.take(ints, 2);

        assertThat(skip)
                .hasSize(2)
                .contains(ints.get(0),
                        ints.get(1));
    }
    
}
