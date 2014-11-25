package org.chain;

import org.chain.filters.Action;
import org.chain.filters.ManySelector;
import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ChainTest
{

    private ArrayList<TestClass> testClasses = null;
    ChainBuilder<TestClass> chain = null;

    @Before
    public void setup(){
        testClasses = new ArrayList<TestClass>() {{
            add(new TestClass(1, "string " + 1));
            add(new TestClass(2, "string " + 2));
            add(new TestClass(3, "string " + 3));
            add(new TestClass(4, "string " + 4));
            add(new TestClass(5, "string " + 5));
        }};

        chain = new Chain<TestClass>(testClasses);
    }

    @Test
    public void testShouldModifyEach()
    {
        chain.each(new Action<TestClass>() {
            @Override
            public void preform(TestClass obj) {
                obj.setNum(obj.getNum() + 5);
            }
        });

        Collection<Integer> selected = chain
                .select(new Selector<TestClass, Integer>() {
                    @Override
                    public Integer select(TestClass obj) {
                        return obj.getNum();
                    }
                })
                .toList();

        ArrayList<Integer> select = new ArrayList<Integer>(selected);
        for (int i = 0; i < select.size(); i++){
            assertThat(select.get(i)).isEqualTo(i + 1 + 5);
        }
    }

    @Test
    public void testShouldSort() {
        testClasses = new ArrayList<TestClass>() {{
            add(new TestClass(3, "string " + 3));
            add(new TestClass(5, "string " + 5));
            add(new TestClass(1, "string " + 1));
            add(new TestClass(4, "string " + 4));
            add(new TestClass(2, "string " + 2));
        }};

        Collection<TestClass> sorted = chain
                .sort(new Comparator<TestClass>() {
                    @Override
                    public int compare(TestClass o1, TestClass o2) {
                        return new Integer(o1.getNum()).compareTo(o2.getNum());
                    }
                })
                .toList();

        ArrayList<TestClass> sort = new ArrayList<TestClass>(sorted);
        assertThat(sort).hasSize(testClasses.size());
        for (int i = 0; i < sort.size() - 1; i++){
            assertThat(sort.get(i).getNum()).isEqualTo(i+1);
        }
        assertThat(testClasses.get(0).getNum()).isEqualTo(3);
        assertThat(testClasses.get(1).getNum()).isEqualTo(5);
        assertThat(testClasses.get(2).getNum()).isEqualTo(1);
        assertThat(testClasses.get(3).getNum()).isEqualTo(4);
        assertThat(testClasses.get(4).getNum()).isEqualTo(2);
    }

    @Test
    public void testShouldReverse() {

        List<TestClass> reverse = chain.reverse().toList();

        assertThat(reverse).hasSize(testClasses.size());
        for (int i = 0; i < reverse.size() - 1; i++){
            assertThat(reverse.get(i).getNum()).isEqualTo(reverse.size() - i);
        }
        assertThat(testClasses.get(0).getNum()).isEqualTo(1);
        assertThat(testClasses.get(1).getNum()).isEqualTo(2);
        assertThat(testClasses.get(2).getNum()).isEqualTo(3);
        assertThat(testClasses.get(3).getNum()).isEqualTo(4);
        assertThat(testClasses.get(4).getNum()).isEqualTo(5);
    }

    @Test
    public void testShouldSelect() {
        Collection<Integer> select = chain
                .select(new Selector<TestClass, Integer>() {
                    @Override
                    public Integer select(TestClass obj) {
                        return obj.getNum();
                    }
                })
                .toList();

        assertThat(select)
                .hasSize(5)
                .contains(1,2,3,4,5);
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
            add(new TestWrapper(testClasses));
            add(new TestWrapper(testClasses2));
        }};

        Collection<TestClass> selectMany = new Chain<TestWrapper>(testWrappers)
                .selectMany(new ManySelector<TestWrapper, TestClass>() {
                    @Override
                    public Collection<TestClass> select(TestWrapper obj) {
                        return obj.getTestClasses();
                    }
                })
                .toList();

        assertThat(selectMany)
                .hasSize(testClasses.size() + testClasses2.size())
                .contains(testClasses.get(0),
                        testClasses.get(1),
                        testClasses.get(2),
                        testClasses.get(3),
                        testClasses.get(4),
                        testClasses2.get(0),
                        testClasses2.get(1),
                        testClasses2.get(2),
                        testClasses2.get(3),
                        testClasses2.get(4))
        ;
    }

    @Test
    public void testShouldFilterUsingWhere() {
        Collection<TestClass> where = chain
                .where(new WhereComparator<TestClass>() {
                    @Override
                    public boolean meetsCondition(TestClass obj) {
                        return obj.getNum() > 3;
                    }
                }).toList();

        assertThat(where)
                .hasSize(2)
                .contains(testClasses.get(3),
                        testClasses.get(4));
    }

    @Test
    public void testShouldCheckIfCollectionIsNull() {
        assertThat(new Chain<TestClass>(null).isNullOrEmpty()).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsEmpty() {
        assertThat(new Chain<TestClass>(new ArrayList<TestClass>()).isNullOrEmpty()).isTrue();
    }

    @Test
    public void testShouldCheckIfCollectionIsNotNullOrEmpty() {
        assertThat(new Chain<TestClass>(testClasses).isNullOrEmpty()).isFalse();
    }

    @Test
    public void testShouldCheckAnyMatchCondition() {
        Boolean any = chain
                .any(new WhereComparator<TestClass>()
                {
                    @Override
                    public boolean meetsCondition(TestClass obj)
                    {
                        return obj.getNum() > 3;
                    }
                });

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckAnyInCollection() {
        Boolean any = chain.any();

        assertThat(any).isTrue();
    }

    @Test
    public void testShouldCheckNoneMatchCondition() {
        Boolean none = chain
                .none(new WhereComparator<TestClass>()
                {
                    @Override
                    public boolean meetsCondition(TestClass obj)
                    {
                        return obj.getNum() > 30000;
                    }
                });

        assertThat(none).isTrue();
    }

    @Test
    public void testShouldCheckNoneInCollection() {
        chain = new Chain<TestClass>(new ArrayList<TestClass>());

        Boolean none = chain.none();

        assertThat(none).isTrue();
    }

    @Test
    public void testShouldGetQuantityMatchCondition() {
        Integer count = chain
                .count(new WhereComparator<TestClass>()
                {
                    @Override
                    public boolean meetsCondition(TestClass obj)
                    {
                        return obj.getNum() > 3;
                    }
                });

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testShouldGetQuantityInCollection() {
        Integer count = chain.count();

        assertThat(count).isEqualTo(testClasses.size());
    }

    @Test
    public void testShouldGetFirst() {
        TestClass first = chain.first();

        assertThat(first).isEqualToComparingFieldByField(testClasses.get(0));
    }

    @Test
    public void testShouldGetNullIfNoFirst() {
        TestClass first = chain.where(new WhereComparator<TestClass>()
        {
            @Override
            public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() > 30000;
            }
        }).firstOrNull();

        assertThat(first).isNull();
    }

    @Test
    public void testShouldGetFirstMatchingCondition() {
        TestClass first = chain.first(new WhereComparator<TestClass>()
        {
            @Override
            public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(first).isEqualToComparingFieldByField(testClasses.get(2));
    }

    @Test
    public void testShouldThrowErrorWhenGettingFirstMatchingCondition() {
        NoSuchElementException error = null;
        try
        {
            chain.first(new WhereComparator<TestClass>()
            {
                @Override
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
        TestClass first = chain.firstOrNull(new WhereComparator<TestClass>()
        {
            @Override
            public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(first).isEqualToComparingFieldByField(testClasses.get(2));
    }

    @Test
    public void testShouldGetNullWhenGettingFirstOrNullMatchingCondition() {

        final TestClass testClass = chain
                .firstOrNull(new WhereComparator<TestClass>()
                {
                    @Override
                    public boolean meetsCondition(TestClass obj)
                    {
                        return obj.getNum() == 30000;
                    }
                });

        assertThat(testClass).isNull();
    }

    @Test
    public void testShouldGetLast() {
        TestClass last = chain.last();

        assertThat(last).isEqualToComparingFieldByField(testClasses.get(testClasses.size() - 1));
    }

    @Test
    public void testShouldGetNullIfNoLast() {
        TestClass last = chain.where(new WhereComparator<TestClass>()
        {
            @Override
            public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() > 30000;
            }
        }).lastOrNull();

        assertThat(last).isNull();
    }

    @Test
    public void testShouldGetLastMatchingCondition() {
        TestClass last = chain.last(new WhereComparator<TestClass>()
        {
            @Override
            public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(last).isEqualToComparingFieldByField(testClasses.get(2));
    }

    @Test
    public void testShouldThrowErrorWhenGettingLastMatchingCondition() {
        NoSuchElementException error = null;
        try
        {
            chain.last(new WhereComparator<TestClass>()
            {
                @Override
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
        TestClass last = chain.lastOrNull(new WhereComparator<TestClass>()
        {
            @Override
            public boolean meetsCondition(TestClass obj)
            {
                return obj.getNum() == 3;
            }
        });

        assertThat(last).isEqualToComparingFieldByField(testClasses.get(2));
    }

    @Test
    public void testShouldGetNullWhenGettingLastOrNullMatchingCondition() {

        final TestClass testClass = chain
                .lastOrNull(new WhereComparator<TestClass>()
                {
                    @Override
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

        TestClass at = chain.at(index);

        assertThat(at).isEqualToComparingFieldByField(testClasses.get(index));
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
                    @Override
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
                    @Override
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
            @Override
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
            @Override
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
        Collection<TestClass> skip = chain.skip(2).toList();

        assertThat(skip)
                .hasSize(testClasses.size()-2)
                .contains(testClasses.get(2),
                        testClasses.get(3),
                        testClasses.get(4));
    }

    @Test
    public void testShouldTake() {
        Collection<TestClass> skip = chain.take(2).toList();

        assertThat(skip)
                .hasSize(2)
                .contains(testClasses.get(0),
                        testClasses.get(1));
    }

    public class TestWrapper{
        private Collection<TestClass> testClasses;

        public TestWrapper(Collection<TestClass> testClasses) {
            setTestClasses(testClasses);
        }

        public Collection<TestClass> getTestClasses() {
            return testClasses;
        }

        public void setTestClasses(Collection<TestClass> testClasses) {
            this.testClasses = testClasses;
        }
    }

    public class TestClass
    {
        private int num;
        private String string;

        public TestClass(int num, String string){
            setNum(num);
            setString(string);
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }


}
