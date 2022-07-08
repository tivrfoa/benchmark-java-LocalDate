

Hi folks!

So, I needed to get a date 14 days before the given date,
eg: if input 2022-01-15, it would return 2022-01-01

`LocalDate` is a cool class in the Java standard library (java.time)
and it provides a nice method for that: `minusDays`

But my requirement is very specific and it doesn't need to be as
generic as LocalDate, so I did a little benchmark using JMH.

I hope I did the benchmark correctly. Please, open an issue if you
spot something wrong.

For Java 8, my method is always 2x faster than LocalDate.

For Java 17, my method is (1) half the time as fast as LocalDate and (2) half the time 5x faster:

1. when it stays in the same month/year
2. when it changes month

*It's interesting that the performance of my method in Java 17 is 2x faster
than in Java 8, and LocalDate performance for (1) improved a lot, but it's
basically the same for (2).*

This 5x faster for (2) in Java 17 is only about <s>27 microseconds. So now I wonder if
it's actually worth it or if I just should use the standard library.</s>

The '`,`' is actually a decimal separator there, so that's actually nanoseconds. Java is really fast. =)<br>
In this case, it's much better to just use the standard library.
The benefits are:
1. Battle-tested code;
2. Less code for you to test;
3. You can automatically get improvements when you update to a modern Java version.

---

The `if` below seems to cause no harm to performance,
as it's a private method, and I guess the JVM is able to remove this `if`,
because the parameter `daysBefore` never receives anything greater than 28:

```java
    if (daysBefore > 28)
        throw new RuntimeException("Use LocalDate for more than 28 days.");
```

## Java 8

```txt
java version "1.8.0_291"
Java(TM) SE Runtime Environment (build 1.8.0_291-b10)
Java HotSpot(TM) 64-Bit Server VM (build 25.291-b10, mixed mode)

Benchmark                                        (date)  Mode  Cnt  Score    Error  Units
MyBenchmark.benchCustomMethodReturnArray       1/1/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray      15/1/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray       4/2/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray       5/3/2022  avgt    6  0,015 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray      14/3/2024  avgt    6  0,015 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf   1/1/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf  15/1/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf   4/2/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf   5/3/2022  avgt    6  0,015 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf  14/3/2024  avgt    6  0,015 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord      1/1/2022  avgt    6  0,013 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord     15/1/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord      4/2/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord      5/3/2022  avgt    6  0,014 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord     14/3/2024  avgt    6  0,015 ±  0,001  us/op
MyBenchmark.benchLocalDate                     1/1/2022  avgt    6  0,034 ±  0,001  us/op
MyBenchmark.benchLocalDate                    15/1/2022  avgt    6  0,034 ±  0,003  us/op
MyBenchmark.benchLocalDate                     4/2/2022  avgt    6  0,033 ±  0,001  us/op
MyBenchmark.benchLocalDate                     5/3/2022  avgt    6  0,034 ±  0,001  us/op
MyBenchmark.benchLocalDate                    14/3/2024  avgt    6  0,038 ±  0,001  us/op
```

## Java 17

```txt
openjdk 17 2021-09-14 LTS
OpenJDK Runtime Environment Zulu17.28+13-CA (build 17+35-LTS)
OpenJDK 64-Bit Server VM Zulu17.28+13-CA (build 17+35-LTS, mixed mode, sharing)

Benchmark                                        (date)  Mode  Cnt  Score    Error  Units
MyBenchmark.benchCustomMethodReturnArray       1/1/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray      15/1/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray       4/2/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray       5/3/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArray      14/3/2024  avgt    6  0,008 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf   1/1/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf  15/1/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf   4/2/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf   5/3/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnArrayNoIf  14/3/2024  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord      1/1/2022  avgt    6  0,006 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord     15/1/2022  avgt    6  0,006 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord      4/2/2022  avgt    6  0,006 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord      5/3/2022  avgt    6  0,006 ±  0,001  us/op
MyBenchmark.benchCustomMethodReturnRecord     14/3/2024  avgt    6  0,006 ±  0,001  us/op
MyBenchmark.benchLocalDate                     1/1/2022  avgt    6  0,032 ±  0,001  us/op
MyBenchmark.benchLocalDate                    15/1/2022  avgt    6  0,007 ±  0,001  us/op
MyBenchmark.benchLocalDate                     4/2/2022  avgt    6  0,033 ±  0,001  us/op
MyBenchmark.benchLocalDate                     5/3/2022  avgt    6  0,033 ±  0,001  us/op
MyBenchmark.benchLocalDate                    14/3/2024  avgt    6  0,035 ±  0,001  us/op
```

One interesting thing about this benchmark is that returning a record
with 3 ints seems to be 1us faster than returning an `int[]` with 3 elements.


## Running

```sh
mvn clean verify

java -ea -jar target/benchmarks.jar
```