package org.chain;

import org.chain.filters.Action;
import org.chain.filters.ManySelector;
import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;
import org.chain.model.TestClass;
import org.chain.model.TestWrapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class ChainEngineImplTest
{

    private ArrayList<TestClass> elements = null;
    ChainEngine<TestClass> chainEngine = new ChainEngineImpl<TestClass>();

    @Before
    public void setup()
    {
        elements = new ArrayList<TestClass>()
        {{
                add(new TestClass(1, "string " + 1));
                add(new TestClass(2, "string " + 2));
                add(new TestClass(3, "string " + 3));
                add(new TestClass(4, "string " + 4));
                add(new TestClass(5, "string " + 5));
            }};
    }

    @Test
    public void testShouldModifyEach()
    {
        chainEngine.each(elements, new Action<TestClass>() {
                        public void perform(TestClass obj) {
                obj.setNum(obj.getNum() + 5);
            }
        });

        ArrayList<Integer> select = new ArrayList<Integer>(chainEngine.select(elements, new Selector<TestClass, Integer>() {
                        public Integer select(TestClass obj) {
                return obj.getNum();
            }
        }));
        for (int i = 0; i < select.size(); i++){
            assertThat(select.get(i)).isEqualTo(i + 1 + 5);
        }
    }

    @Test
    public void testShouldSort() {
        elements = new ArrayList<TestClass>() {{
            add(new TestClass(3, "string " + 3));
            add(new TestClass(5, "string " + 5));
            add(new TestClass(1, "string " + 1));
            add(new TestClass(4, "string " + 4));
            add(new TestClass(2, "string " + 2));
        }};

        ArrayList<TestClass> sort = new ArrayList<TestClass>(chainEngine.sort(elements,
                new Comparator<TestClass>()
                {
                                        public int compare(TestClass o1, TestClass o2)
                    {
                        return new Integer(o1.getNum()).compareTo(o2.getNum());
                    }
                }));

        assertThat(sort).hasSize(elements.size());
        for (int i = 0; i < sort.size() - 1; i++){
            assertThat(sort.get(i).getNum()).isEqualTo(i+1);
        }
        assertThat(elements.get(0).getNum()).isEqualTo(3);
        assertThat(elements.get(1).getNum()).isEqualTo(5);
        assertThat(elements.get(2).getNum()).isEqualTo(1);
        assertThat(elements.get(3).getNum()).isEqualTo(4);
        assertThat(elements.get(4).getNum()).isEqualTo(2);
    }

    @Test
    public void testShouldReverse() {

        ArrayList<TestClass> reverse = new ArrayList<TestClass>(chainEngine.reverse(elements));

        assertThat(reverse).hasSize(elements.size());
        for (int i = 0; i < reverse.size() - 1; i++){
            assertThat(reverse.get(i).getNum()).isEqualTo(reverse.size() - i);
        }
        assertThat(elements.get(0).getNum()).isEqualTo(1);
        assertThat(elements.get(1).getNum()).isEqualTo(2);
        assertThat(elements.get(2).getNum()).isEqualTo(3);
        assertThat(elements.get(3).getNum()).isEqualTo(4);
        assertThat(elements.get(4).getNum()).isEqualTo(5);
    }

    @Test
    public void testShouldSelect() {
        Collection<Integer> select = chainEngine.select(elements, new Selector<TestClass, Integer>() {
                        public Integer select(TestClass obj) {
                return obj.getNum();
            }
        });

        assertThat(select)
                .hasSize(5)
                .contains(1, 2, 3, 4, 5);
    }

    @Test
    public void testShouldSelectMany() {

        final ArrayList<TestClass> testClasses2 = new ArrayList<TestClass>() {{
            add(new TestClass(6, "string " + 6));
            add(new TestClass(7, "string " + 7));
            add(new TestClass(8, "string " + 8));
            add(new TestClass(9, "string " + 9));
            add(new TestClass(10, "string " + 10));
        }};
        final ArrayList<TestWrapper> testWrappers = new ArrayList<TestWrapper>() {{
            add(new TestWrapper(elements));
            add(new TestWrapper(testClasses2));
        }};

        Collection<TestClass> selectMany = new ChainEngineImpl<TestWrapper>().selectMany(
                testWrappers, new ManySelector<TestWrapper, TestClass>()
        {
                        public Collection<TestClass> select(TestWrapper obj)
            {
                return obj.getTestClasses();
            }
        });

        assertThat(selectMany)
                .hasSize(elements.size() + testClasses2.size())
                .contains(elements.get(0),
                        elements.get(1),
                        elements.get(2),
                        elements.get(3),
                        elements.get(4),
                        testClasses2.get(0),
                        testClasses2.get(1),
                        testClasses2.get(2),
                        testClasses2.get(3),
                        testClasses2.get(4))
        ;
    }

    @Test
    public void testShouldFilterUsingWhere() {
        Collection<TestClass> where = chainEngine.where(elements,
                new WhereComparator<TestClass>()
                {
                                        public boolean meetsCondition(TestClass obj)
                    {
                        return obj.getNum() > 3;
                    }
                });

        assertThat(where)
                .hasSize(2)
                .contains(elements.get(3),
                        elements.get(4));
    }

    @Test
    public void testShouldCheckIfCollectionIsNull() {
        assertThat(chainEngine.isNullOrEmpty(null)).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsEmpty() {
        assertThat(chainEngine.isNullOrEmpty(new ArrayList<TestClass>())).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsNotNullOrEmpty() {
        assertThat(chainEngine.isNullOrEmpty(elements)).isFalse();
    }

    @Test
    public void testShouldCheckAllMatchCondition() {
        Boolean any = chainEngine.all(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() >= 0;
            }
        });

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckAllDoNotMatchCondition() {
        Boolean any = chainEngine.all(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() >= 2;
            }
        });

        assertThat(any).isFalse();
    }

    @Test
    public void testShouldCheckAnyMatchCondition() {
        Boolean any = chainEngine.any(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() > 3;
            }
        });

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckAnyInCollection() {
        Boolean any = chainEngine.any(elements);

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckNoneMatchCondition() {
        Boolean none = chainEngine.none(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() > 30000;
            }
        });

        assertThat(none).isTrue();
    }

    @Test
    public void testShouldCheckNoneInCollection() {
        Boolean none = chainEngine.none(new ArrayList<TestClass>());

        assertThat(none).isTrue();
    }

    @Test
    public void testShouldGetQuantityMatchCondition() {
        Integer count = chainEngine.count(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() > 3;
            }
        });

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testShouldGetQuantityInCollection() {
        Integer count = chainEngine.count(elements);

        assertThat(count).isEqualTo(elements.size());
    }

    @Test
    public void testShouldGetFirst() {
        TestClass first = chainEngine.first(elements);

        assertThat(first).isEqualToComparingFieldByField(elements.get(0));
    }

    @Test
    public void testShouldGetNullIfNoFirst()
    {
        final TestClass testClass = chainEngine.firstOrNull(new ArrayList<TestClass>());

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetFirstMatchingCondition() {
        TestClass first = chainEngine.first(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(first).isEqualToComparingFieldByField(elements.get(2));
    }

    @Test
    public void testShouldThrowErrorWhenGettingFirstMatchingCondition() {
        NoSuchElementException error = null;
        try
        {
            chainEngine.first(elements, new WhereComparator<TestClass>()
            {
                                public boolean meetsCondition(TestClass obj)
                {
                    return obj.getNum() == 30000;
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
        TestClass first = chainEngine.firstOrNull(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(first).isEqualToComparingFieldByField(elements.get(2));
    }

    @Test
    public void testShouldGetNullWhenGettingFirstOrNullMatchingCondition() {

        final TestClass testClass = chainEngine
                .firstOrNull(elements, new WhereComparator<TestClass>()
                {
                                        public boolean meetsCondition(TestClass obj)
                    {
                        return obj.getNum() == 30000;
                    }
                });

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetLast() {
        TestClass last = chainEngine.last(elements);

        assertThat(last).isEqualToComparingFieldByField(elements.get(elements.size() - 1));
    }

    @Test
    public void testShouldGetNullIfNoLast()
    {
        final TestClass testClass = chainEngine.lastOrNull(new ArrayList<TestClass>());

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetLastMatchingCondition() {
        TestClass last = chainEngine.last(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(last).isEqualToComparingFieldByField(elements.get(2));
    }

    @Test
    public void testShouldThrowErrorWhenGettingLastMatchingCondition() {
        NoSuchElementException error = null;
        try
        {
            chainEngine.last(elements, new WhereComparator<TestClass>()
            {
                                public boolean meetsCondition(TestClass obj)
                {
                    return obj.getNum() == 30000;
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
        TestClass last = chainEngine.lastOrNull(elements, new WhereComparator<TestClass>()
        {
                        public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(last).isEqualToComparingFieldByField(elements.get(2));
    }

    @Test
    public void testShouldGetNullWhenGettingLastOrNullMatchingCondition() {

        final TestClass testClass = chainEngine
                .lastOrNull(elements, new WhereComparator<TestClass>()
                {
                                        public boolean meetsCondition(TestClass obj)
                    {
                        return obj.getNum() == 30000;
                    }
                });

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetValueAtIndex() {
        int index = 1;

        TestClass at = chainEngine.at(elements, index);

        assertThat(at).isEqualToComparingFieldByField(elements.get(index));
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
        Collection<TestClass> skip = chainEngine.skip(elements, 2);

        assertThat(skip)
                .hasSize(elements.size()-2)
                .contains(elements.get(2),
                        elements.get(3),
                        elements.get(4));
    }

    @Test
    public void testShouldTake() {
        Collection<TestClass> skip = chainEngine.take(elements, 2);

        assertThat(skip)
                .hasSize(2)
                .contains(elements.get(0),
                        elements.get(1));
    }
}
