package org.sample;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Fork (value = 2, warmups = 1)
@Warmup (iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement (iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode (Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class MyBenchmark {

    /**
     * 1. Change year
     * 2. Stay same month
     * 3. Change month
     * 4. Change month to February
     * 5. Leap year
     */
    @Param ({"1/1/2022", "15/1/2022", "4/2/2022", "5/3/2022", "14/3/2024"})
    public String date;

    int day;
    int month;
    int year;

    @Setup
    public void setup() {
        String[] tmp = date.split("/");
        day = Integer.parseInt(tmp[0]);
        month = Integer.parseInt(tmp[1]);
        year = Integer.parseInt(tmp[2]);

        testCustomMethod();
    }

    @Benchmark
    public LocalDate benchLocalDate() {
        LocalDate ld = LocalDate.of(year, month, day);
        return ld.minusDays(14);
    }

    @Benchmark
    public DateRecord benchCustomMethodReturnRecord() {
        return getDateBeforeAsRecord(day, month, year, 14);
    }

    @Benchmark
    public int[] benchCustomMethodReturnArray() {
        return getDateBefore(day, month, year, 14);
    }

    @Benchmark
    public int[] benchCustomMethodReturnArrayNoIf() {
        return getDateBeforeNoIf(day, month, year, 14);
    }

    private static final record DateRecord(int day, int month, int year) {}

    private static DateRecord getDateBeforeAsRecord(int day, int month, int year, int daysBefore) {
        final int diff = day - daysBefore;
        if (diff > 0) {
            return new DateRecord(diff, month, year);
        }

        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            --prevYear;
        }
        // + diff, because it's <= 0
        int prevDay = getLastDayOfMonth(prevMonth, prevYear) + diff;

        return new DateRecord(prevDay, prevMonth, prevYear);
    }

    private static int[] getDateBefore(int day, int month, int year, int daysBefore) {
        if (daysBefore > 28)
            throw new RuntimeException("Use LocalDate for more than 28 days.");

        final int diff = day - daysBefore;
        if (diff > 0) {
            return new int[]{diff, month, year};
        }

        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            --prevYear;
        }
        // + diff, because it's <= 0
        int prevDay = getLastDayOfMonth(prevMonth, prevYear) + diff;

        return new int[]{prevDay, prevMonth, prevYear};
    }

    private static int[] getDateBeforeNoIf(int day, int month, int year, int daysBefore) {
        final int diff = day - daysBefore;
        if (diff > 0) {
            return new int[]{diff, month, year};
        }

        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth == 0) {
            prevMonth = 12;
            --prevYear;
        }
        // + diff, because it's <= 0
        int prevDay = getLastDayOfMonth(prevMonth, prevYear) + diff;

        return new int[]{prevDay, prevMonth, prevYear};
    }

    private static int getLastDayOfMonth(int month, int year) {
        return switch (month) {
            case 4, 6, 9, 11 -> 30;
            case 2 -> isLeapYear(year) ? 29 : 28;
            default -> 31;
        };
    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    private void testCustomMethod() {
        LocalDate prevLd = LocalDate.of(2022, 1, 15).minusDays(14);
        int[] prev = getDateBefore(15, 1, 2022, 14);
        assert prev[0] == prevLd.getDayOfMonth() &&
                prev[1] == prevLd.getMonthValue() &&
                prev[2] == prevLd.getYear();

        prevLd = LocalDate.of(2022, 1, 14).minusDays(14);
        prev = getDateBefore(14, 1, 2022, 14);
        assert prev[0] == prevLd.getDayOfMonth() &&
                prev[1] == prevLd.getMonthValue() &&
                prev[2] == prevLd.getYear();

        prevLd = LocalDate.of(2022, 1, 10).minusDays(14);
        prev = getDateBefore(10, 1, 2022, 14);
        assert prev[0] == prevLd.getDayOfMonth() &&
                prev[1] == prevLd.getMonthValue() &&
                prev[2] == prevLd.getYear();

        prevLd = LocalDate.of(2022, 2, 10).minusDays(14);
        prev = getDateBefore(10, 2, 2022, 14);
        assert prev[0] == prevLd.getDayOfMonth() &&
                prev[1] == prevLd.getMonthValue() &&
                prev[2] == prevLd.getYear();

        prevLd = LocalDate.of(2022, 3, 14).minusDays(14);
        prev = getDateBefore(14, 3, 2022, 14);
        assert prev[0] == prevLd.getDayOfMonth() &&
                prev[1] == prevLd.getMonthValue() &&
                prev[2] == prevLd.getYear();
    }
}
