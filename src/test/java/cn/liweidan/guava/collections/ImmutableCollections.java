package cn.liweidan.guava.collections;

import com.google.common.collect.*;
import org.junit.Test;

import java.util.*;

/**
 * <p>Desciption:</p>
 * CreateTime : 2017/10/8 上午10:52
 * Author : Weidan
 * Version : V1.0
 */
public class ImmutableCollections {

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

        List<String> stringList2 = Lists.newArrayList("aaaa", "bbbb", "cccc", "dddd");

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

    /**
     * MultiMap用法
     */
    @Test
    public void test03() {
        // 相当于Map<String, List<Integer>> map = new HashMap<>();
        ListMultimap<String, Integer> listMultimap = ArrayListMultimap.create();
        listMultimap.put("ssss", 10);
        listMultimap.put("ssss", 11);
        System.out.println(listMultimap);// {ssss=[10, 11]}

        /** entries用法 */
        Collection<Map.Entry<String, Integer>> entries = listMultimap.entries();
        System.out.println(entries);// [ssss=10, ssss=11]
        Set<Map.Entry<String, Collection<Integer>>> entries1 = listMultimap.asMap().entrySet();
        System.out.println(entries1);// [ssss=[10, 11]]

        List<Integer> integers = listMultimap.get("ssss");
        System.out.println(integers);// 返回List集合, [10, 11]

        Multiset<String> keys = listMultimap.keys();
        System.out.println(keys);// [ssss x 2]
        keys.remove("ssss");
        System.out.println(keys);// [ssss]
        System.out.println(listMultimap);// {ssss=[11]}

    }

    /**
     * BiMap集合
     */
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

}
