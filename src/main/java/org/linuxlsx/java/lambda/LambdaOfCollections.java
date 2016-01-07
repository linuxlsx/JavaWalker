package org.linuxlsx.java.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * http://www.cnblogs.com/WJ5888/p/4618465.html
 * Created by rongruo.lsx on 16/1/6.
 */
public class LambdaOfCollections {

    static List<String> friends = Arrays.asList("Brian");

    public static void forEach() {
        //标准写法
        for (String friend : friends) {
            System.out.println(friend);
        }

        //forEach + 匿名内部类
        friends.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });

        //使用 标准 lambda 预发
        friends.forEach((final String name) -> System.out.println(name));

        //编译器推导出类型, 可以省略类型
        friends.forEach((name) -> System.out.println(name));

        //使用 Method References(可以是实例方法也可以是静态方法)
        //当lambda 直接调用某个实例方法或者静态方法的时候, Method References 是一个很好的替代品
        friends.forEach(System.out::println);
    }

    public static void transform() {

        friends.stream()  //所有的 Collection 类都可以使用,
                .map(String::toUpperCase) // 将输入转化成指定的输出, 并返回结果集
                .forEach(System.out::println);
    }

    public static void filter() {

        List<String> startWithB = friends.stream()
                .filter(value -> value.startsWith("B"))  //lambda 类型为 Predicate, filter 过滤返回结果为false 的记录
                //lambda 类型为  Collector<T, A, R>  T 输入类型, A 结果收集器, R 输出类型
                //<R> R collect(Supplier<R> supplier,BiConsumer<R, ? super T> accumulator,BiConsumer<R, R> combiner);
                .collect(Collectors.toList());


        startWithB.forEach(System.out::println);

    }

    public static void duplicate() {

        List<String> comrades = Arrays.asList("Kate", "Ken", "Nick", "Paula", "Zach");
        List<String> editors = Arrays.asList("Brian", "Jackie", "John", "Mike");

        //出现重复的 lambda 表达式
        long countComradesStartN = comrades.stream().filter(name -> name.startsWith("N")).count();
        long countEditorsStartN = editors.stream().filter(name -> name.startsWith("N")).count();

        //可以通过创建 Predicate 变量减少代码重复
        Predicate<String> predicate = name -> name.startsWith("N");
        countComradesStartN = comrades.stream().filter(predicate).count();
        countEditorsStartN = editors.stream().filter(predicate).count();

    }

    public static void duplicateMore() {
        List<String> comrades = Arrays.asList("Kate", "Ken", "Nick", "Paula", "Zach");
        List<String> editors = Arrays.asList("Brian", "Jackie", "John", "Mike");

        //出现重复的 lambda 表达式
        long countComradesStartN = comrades.stream().filter(name -> name.startsWith("N")).count();
        long countEditorsStartB = editors.stream().filter(name -> name.startsWith("B")).count();

        //需要定义两个 Predicate 变量
        Predicate<String> predicateN = name -> name.startsWith("N");
        Predicate<String> predicateB = name -> name.startsWith("B");
        countComradesStartN = comrades.stream().filter(predicateB).count();
        countEditorsStartB = editors.stream().filter(predicateN).count();

        //可以通过创建函数的方式来减少 重复定义类似的变量
        countComradesStartN = comrades.stream().filter(checkIfStartsWith("N")).count();
        countEditorsStartB = editors.stream().filter(checkIfStartsWith("B")).count();

        //也可以通过使用Function<T, R> 来避免定义函数
        //Function 可以将 输入 T 转换为 输出 R
        Function<String, Predicate<String>> startsWithLetter = letter -> name -> name.startsWith(letter);

//        Function<String, Predicate<String>> startsWithLetter = (String letter) -> {
//            Predicate<String> checkStartsWith = (String name) -> name.startsWith(letter);
//            return checkStartsWith;
//        };
        countComradesStartN = comrades.stream().filter(startsWithLetter.apply("N")).count();
        countEditorsStartB = editors.stream().filter(startsWithLetter.apply("B")).count();
    }

    //返回函数的方法
    //lambda lexical scoping(词法作用域)
    //java 发现 letter 不在 lambda 表达式的匿名作用域中, 他会向上寻找定义该lambda 表达式的作用域中是否有该变量.
    //对于变量 从java 8 开始放宽限制, 允许不使用 final 关键词修饰, 但是需要变量实际有用 final 修饰的效果, 即该变量不能被修改
    public static Predicate<String> checkIfStartsWith(String letter) {
        return name -> name.startsWith(letter);
    }

    public static void pickAnElement() {
        //传统的从列表中取一个元素的方式如下
        //为了获取一个元素, 必须写一个循环 并且要对结果进行判断以防止可能的NPE
        String foundName = null;
        for (String name : friends) {
            if (name.startsWith("N")) {
                foundName = name;
                break;
            }
        }

        if(foundName != null){
            //do something
        }else {
            //do something else
        }


        Optional<String> foundNameOption = friends.stream()
                .filter(checkIfStartsWith("N"))
                .findFirst(); //只命中第一个

        //如果没有找到想用的元素, 则使用 "No name found" 代替
        foundName = foundNameOption.orElse("No name found");

    }

    public static void main(String[] args) {

        LambdaOfCollections.filter();

    }
}
