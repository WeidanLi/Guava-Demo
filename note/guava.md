Preconditions

## 一. 集合

### （一）不可变集合
#### 1. 特征：
- 安全
- 并发不怕被破坏
- 不支持拓展 节省时间和空间
- 能够当成常量使用

#### 2. 使用
创建的方式有三种：
1) 使用copyOf方法
2) 使用of方法
3) 使用builder

示例：
```java
/**
 * 创建ImmutableXXX集合的三种方式
 * 这里以ImmutableList做示例
 */
@Test
public void test01() {
    /** 第一种方法: 使用copyOf的方法，从一个已知的集合当中拷贝出来 */
    List<String> stringList = new ArrayList<>();
    stringList.add("aaaa");
    stringList.add("bbbb");
    stringList.add("cccc");
    stringList.add("dddd");

    ImmutableList<String> stringImmutableList = ImmutableList.copyOf(stringList);
    System.out.println(stringImmutableList);
    // stringImmutableList.add("eeee");// UnsupportedOperationException 说明这个集合不可修改

    /** 第二种方式：使用of来创建 */
    ImmutableList<String> strings = ImmutableList.of("aaaa", "bbbb", "cccc");
    System.out.println(strings);

    /** 第三种方式：使用builder创建 */
    ImmutableList<String> immutableList = ImmutableList.<String>builder()
            .addAll(stringList)// 可以将现有的集合存进去
            .add("eeee")// 也可以存入想要存入的元素
            .build();// 通过build 生成不可变集合
    System.out.println(immutableList);

}
```
#### 3. 各种类型集合的不可变类型
| 接口        | JDK或者Guava           | 不可变版本  |
| ------------- |:-------------:| -----:|
|Collection|jdk|ImmutableCollection|
|List|jdk|ImmutableList|
|Set|jdk|ImmutableSet|
|SortedSet/NavigableSet|jdk|ImmutableSortedSet|
|Map|jdk|ImmutableMap|
|SortedMap|jdk|ImmutableSortedMap|
|Multiset|Guava|ImmutableMultiset|
|SortedMultiset|Guava|ImmutableSortedMultiset|
|Multiset|Guava|ImmutableMultiset|
|SortedMultiset|Guava|ImmutableSortedMultiset|
|Multimap|Guava|ImmutableMultimap|
|ListMultimap|Guava|ImmutableListMultimap|
|SetMultimap|Guava|ImmutableSetMultimap|
|BiMap|Guava|ImmutableBiMap|
|ClassToInstanceMap|Guava|ImmutableClassToInstanceMap|
|Table|Guava|ImmutableTable|

### （二）新的集合类型
Guava提供了一系列的功能强大的集合，包括有：
- Multiset
- Multimap
- BiMap
- Table
- ClassToInstanceMap
- RangeSet
- RangeMap

#### 1. Multiset接口
Multiset是一个**可以存入重复对象**的Set集合，并且在这个集合当中，可以获取一个对象在这个集合当中重复的次数，也可以在增加一个元素的时候，显式指定要存入的次数

##### 1) 特征
- 像ArrayList但是没有顺序
- 像Map<E, Integer>，统计着元素以及他的个数

##### 2) 使用MultiSet的时候，大概有两种方式使用它
存入对象的时候（更像`ArrayList`用法）
- 调用`add(E)`方式进行存储
- 调用`iterator()`获取迭代器
- 调用`size()`获取长度
统计的方法有（更像`Map<E, Integer>`）
- `count(Object)`统计一个对象在这个集合中出现的次数
    在`HashMultiset`当中，查询一个对象时间复杂度是`O(1)`, 在TreeMultiset是`O(log n)`
- `entrySet()`返回`Set<Multiset.Entry<E>>`用于遍历
- `elementSet()`用于返回`Set<E>`元素

##### 3）示例
```java
/**
 * 测试MultiSet用法
 */
@Test
public void test02() {
    Multiset<String> strings = HashMultiset.create();
    strings.add("aaaaa");
    strings.add("bbbb");
    strings.add("ccc");
    strings.add("aaaaa");

    System.out.println(strings.add("aaaa", 7));// 往集合中存入多少次指定的对象
    System.out.println(strings.size());// 查看长度 -> 11
    System.out.println(strings.count("aaaa"));// 统计对象的个数 -> 7
    System.out.println(strings.elementSet());// 返回所有对象的Set集合 [ccc, aaaaa, aaaa, bbbb]
}
```
##### 4）Multiset不是Map集合
直接的说，`Multiset`就是一个可以存放重复元素的，也可以统计次数的`Collection`

#### 2. Multimap
用于取代我们在开发中需要创建的`Map<String, List<Integer>>`这种Key为对象，value为集合的情况
##### 1）提供的方法：
- `asMap()`转换为Map集合
- `entries()`返回`Collection<Map.Entry<K, V>>`
- `keySet()`返回Multimap中所有的Key
- `keys()`返回Multimap中的key为Multiset，如果Multiset中的Key被移除，将会导致Multimap中的值也被移除(如果一个Key存在多个值，则移除多少个Key就会移除多少个值)
- `values()`返回Multimap中所有的value到一个集合当中去
- `put(K,V)`当我们需要对Map中的某个集合存入值的时候，只需要调用这个方法，相当于以前的`multimap.get(key).add(value)`
- `putAll(K, Iterable<V>)`将另外一个集合整个存入Multimap中
- `remove(K, V)`移除Multimap中指定Key的集合中的某一个值
- `removeAll(K)`移除整个Key中的集合
- `replaceValues(K, Iterable<V>)`替换某个Key中的整个集合为指定的集合

##### 2）示例：
```java
/**
 * 测试MultiMap用法
 */
@Test
public void test03() {
    // 相当于Map<String, List<Integer>> map = new HashMap<>();
    ListMultimap<String, Integer> listMultimap = ArrayListMultimap.create();
    listMultimap.put("ssss", 10);
    listMultimap.put("ssss", 11);
    System.out.println(listMultimap);// {ssss=[10, 11]}

    List<Integer> integers = listMultimap.get("ssss");
    System.out.println(integers);// 返回List集合, [10, 11]

    Multiset<String> keys = listMultimap.keys();
    System.out.println(keys);// [ssss x 2]
    keys.remove("ssss");
    System.out.println(keys);// [ssss]
    System.out.println(listMultimap);// {ssss=[11]}
}
```

##### 3）和Map的区别：
- `Multimap.get(key)`如果key不存在，并不会返回一个null值，而是返回一个空的集合
- 如果更喜欢返回的是null值，则需要通过`asMap()`方法拿到一个原生的`Map<K, Collection<V>>`来取值
- `Multimap.containsKey(key)`判断这个Key的集合是否是空，如果是空，返回`false`，如果存在值则返回`true`
- `Multimap.entries()`返回的是所有的`Key-value`值，如果需要返回`Key-Collection`值则需要调用`asMap()`来获取原生Map再调用`entrySet()`来获取值
```java
/** entries用法 */
Collection<Map.Entry<String, Integer>> entries = listMultimap.entries();
System.out.println(entries);// [ssss=10, ssss=11]
Set<Map.Entry<String, Collection<Integer>>> entries1 = listMultimap.asMap().entrySet();
System.out.println(entries1);// [ssss=[10, 11]]
```
- `Multimap.size()`返回`entries`的值，如上面调用`entries`返回的应该是2.
如果需要获取Key的值则需要通过调用`KeySet().size()`来获取

##### 4）实现类：
|Implementation|Keys behave like...|Values behave like..|
|--------------:|-----------------:|------------------:|
|ArrayListMultimap|HashMap|ArrayList|
|HashMultimap|HashMap|HashSet|
|LinkedListMultimap|LinkedHashMap|LinkedList|
|LinkedHashMultimap|LinkedHashMap|LinkedHashSet|
|TreeMultimap|TreeMap|TreeSet|
|ImmutableListMultimap|ImmutableMap|ImmutableList|
|ImmutableSetMultimap|ImmutableMap|ImmutableSet|

#### 3. BiMap
##### 1） 特征
- 一个允许Key和Value倒过来访问的Map集合(通过调用`inverse()`方法)
- 一个保证value值唯一性的Map
- 是一个`Map`集合，所以`Map`拥有的方法他都有
##### 2）示例
```java
@Test
public void test04() {
    BiMap<String, Integer> userId = HashBiMap.create();// 假设String就是常用的User对象，Integer是用户Id
    userId.put("Xiaoming", 100);
    userId.put("Xiaodong", 101);
    userId.put("Xiaohone", 102);
    /** 当我们需要回过头来通过id获取用户的时候 */
    String user = userId.inverse().get(100);
    System.out.println(user);

    /** 当put一个已经存在的value值的时候回报错(这就是保证value唯一性)，如果需要直接替换，需要使用BiMap.forcePut(key, value)代替 */
    // userId.put("Xiaohone2", 102);// java.lang.IllegalArgumentException
    /** 如果需要强制put进去，那之前的key值就会丢失 */
    userId.forcePut("Xiaohone2", 102);
    System.out.println(userId);// {Xiaoming=100, Xiaodong=101, Xiaohone2=102}
}
```
##### 3）实现类
|Key-Value Map实现|Value-Key Map 实现|双向Map BiMap|
|-----------------:|-----------------:|-----------------:|
|HashMap|HashMap|HashBiMap|
|ImmutableMap|ImmutableMap|ImmutableBiMap|
|EnumMap|EnumMap|EnumBiMap|
|EnumMap|HashMap|EnumHashBiMap|

#### 4. Table
##### 1） 特征
- 可以构建三个泛型的Map集合，相当于原生的`Map<String, Map<String, String>>`，提供更加方便的访问方法
- 泛型`Table<R, C, V>`
##### 2） 方法
- `row(R)`返回后面两个参数的Map集合`Map<C, V>`
- `column(C)`返回R=V的值
- `rowMap()`返回原生状态，即`Map<R, Map<C, V>>`形式
- `rowKeySet()`就是在上面方法的基础上，返回R的Set集合
- `columnMap()`返回原生`Map<String, Map<String, String>>`当中的Value的Map集合
- `columnKeySet()`以上方法的Key值
- `cellSet()`返回`Table.Cell<R, C, V>`的Set集合，相当于`Map`集合中的`Entry`
##### 3）示例
```java
/**
 * Table集合
 */
@Test
public void test05() throws ClassNotFoundException, InterruptedException {
    // DateOfBirth, LastName, PersonalRecord
    Table<Date, String, String> records = HashBasedTable.create();

    Date d1 = new Date();
    Thread.sleep(1000L);// 为了d1和下面的时间不一样

    Date d2 = new Date();
    Date d3 = new Date();

    records.put(d1, "Schmo", "Java程序员");
    records.put(d2, "Doe", "C#程序员");
    records.put(d3, "Doe3", "C++程序员");

    Map<String, String> stringStringMap = records.row(d1);
    System.out.println(stringStringMap);// {Schmo=Java程序员}

    Map<Date, String> column = records.column("Doe");
    System.out.println(column);// {Sun Oct 08 15:51:13 CST 2017=C#程序员}

    Map<Date, String> stringMap = records.column("C++程序员");
    System.out.println(stringMap);// {}
}
```
##### 4）实现
|实现|描述|
|---:|----:|
|HashBasedTable|HashMap<R, HashMap<C, V>>|
|TreeBasedTable|TreeMap<R, TreeMap<C, V>>|
|ImmutableTable|ImmutableMap<R, ImmutableMap<C, V>>，无论数据量大小，都对其进行了优化|
|ArrayTable|使用数组的二维特性，对于数据量大的情况下有显著的速度和内存的优化|


#### 5. ClassToInstanceMap
##### 1） 特征
- 多在反射的时候使用，Key值是类型，Value值是示例
- Guava 还提供了两个不错的类： `MutableClassToInstanceMap` 以及 `ImmutableClassToInstanceMap`.
- `Map<Class<? extends B>, B>`的实现，通常`B`是`Object`
##### 2）示例
```java
ClassToInstanceMap<Number> numberDefaults = MutableClassToInstanceMap.create();
numberDefaults.putInstance(Integer.class, Integer.valueOf(0));
```

#### 6. RangeSet
##### 1） 特征
- 这是一个区间合并的Set集合，当需要存入一个区间的时候，会分析集合当中所有的区间，然后把区间进行计算取出交集或者并集
##### 2）示例
```java
@Test
public void test06() {
    TreeRangeSet<Long> rangeSet = TreeRangeSet.create();
    rangeSet.add(Range.closed(1L, 10L));// [1, 10]
    // 把[11, 15)进行合并 就是[1, 10] U [11, 15) = {[1, 10], [11, 15)}
    rangeSet.add(Range.closedOpen(11L, 15L));
    System.out.println(rangeSet);// [[1..10], [11..15)]
    // 再合并(13, 16] [1..10], [11..15) U (13, 16] = [1..10], [11..16]
    rangeSet.add(Range.openClosed(13L, 16L));
    System.out.println(rangeSet);// [[1..10], [11..16]]

    // 移除(9, 13), [[1..10], [11..16]] ∩ (9, 13) = [1...9], [13...16]
    rangeSet.remove(Range.open(9L, 13L));
    System.out.println(rangeSet);// [[1..9], [13..16]]

    System.out.println(rangeSet.complement());// 不在范围内的所有元素 [(-∞..1), (9..13), (16..+∞)]

    // 再并集一个Range对象进行取值
    System.out.println(rangeSet.subRangeSet(Range.closed(2L, 14L)));

    // 转换成可遍历的集合
    Set<Range<Long>> ranges = rangeSet.asRanges();
    System.out.println(ranges);

    System.out.println(rangeSet.contains(10L));// false
    System.out.println(rangeSet.contains(14L));// true
    System.out.println(rangeSet.rangeContaining(14L));// [13..16]
    System.out.println(rangeSet.encloses(Range.closed(17L, 18L)));// false
    System.out.println(rangeSet.encloses(Range.closed(14L, 18L)));// false
    System.out.println(rangeSet.encloses(Range.closed(14L, 16L)));// true
    System.out.println(rangeSet.span());// 返回一个最接近区间集合的的最小的区间
}
```
##### 3）方法
- `complement()`打印出不在区间内的所有元素
- `subRangeSet(Range)`取出再并集一个Range对象进行取值，并不会修改原区间的值
- `asRanges()`将区间集合转换成`Set<Range<C>>`以便能够进行遍历
- `contains(C)`查询一个值有没有在这个集合当中
- `rangeContaining(C)`返回值在区间内的那个区间
- `encloses(Range<C>)`查看一个区间有没有在该集合当中
- `span()`返回一个最接近区间集合的的最小的区间
#### 7. RangeMap
##### 1） 特征
- 用来描述区间的映射集合，是`Map<TreeRangeSet, String>`的封装，但是不会合并相邻的区间
- 即可以存入`Key`为区间，`Value`为描述的`Map`
- 当调用`put`的时候，直接把区间和描述存入集合，当调用`remove(range)`的时候，则会把集合中所在区间给移除

##### 2）官方示例
```java
RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
rangeMap.put(Range.closed(1, 10), "foo"); // {[1, 10] => "foo"}
rangeMap.put(Range.open(3, 6), "bar"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo"}
rangeMap.put(Range.open(10, 20), "foo"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo", (10, 20) => "foo"}
rangeMap.remove(Range.closed(5, 11)); // {[1, 3] => "foo", (3, 5) => "bar", (11, 20) => "foo"}
```


## 缓存

## Strings

## 包装类

## 区间比较大小

## IO工具

## 数学工具

## 反射工具

