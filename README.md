C.H.A.I.N.s - Collection Helpers And IteratioNs
===========

C.H.A.I.N.s Provides a fluent api to work with collections of data.

```
ArrayList<Integer> numsWithDupes = new ArrayList<Integer>() {{
    add(3);
    add(2);
    add(1);
    add(2);
    add(4);
    add(1);
}}; 
//<[3, 2, 1, 2, 4, 1]>


final Comparator<Integer> intComparator = new Comparator<Integer>()
{
    public int compare(Integer o1, Integer o2)
    { return o1.compareTo(o2);
    }
};
final WhereComparator<Integer> intGtOrEqTo2 = new WhereComparator<Integer>()
{
    public boolean meetsCondition(Integer integer)
    { return integer >= 2;
    }
};

List<Integer> integers = new Chain<Integer>(numsWithDupes)
    .distinct(intComparator)
    .sort(intComparator)
    .toList();//<[1, 2, 3, 4]>


NumberChain<Integer> integerNumberChain = new NumberChain<Integer>(numsWithDupes);
ChainBuilder<Integer> distinct = integerNumberChain
    .distinct(intComparator)
    .where(intGtOrEqTo2); //<[3, 2, 4]>

integerNumberChain.sum(); //9
integerNumberChain.average(); //3
integerNumberChain.max(); //4
```
