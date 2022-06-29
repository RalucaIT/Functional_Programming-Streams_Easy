package com.homework.exercises;

import com.homework.utils.Employee;
import com.homework.utils.Employees;
import org.junit.Ignore;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;

@SuppressWarnings("all")
public class Pack_4_Streams_Difficult {

    private static final List<Employee> EMPLOYEES = Employees.allEmployees();

   // @Ignore
    @Test
    public void exercise_1_findFirst() {
        // find whether there are two employees with the same first name and surname and return the name.
        // the solution has to be a single statement, complexity O(n^2) is acceptable //  2 for-uri

        String result = null;
        result = EMPLOYEES.stream().filter(firstEmployee -> EMPLOYEES.stream().anyMatch(secondEmployee -> firstEmployee.getFirstName().equals(secondEmployee.getFirstName()) && firstEmployee.getSurname().equals(secondEmployee.getSurname()) && !firstEmployee.equals(secondEmployee)))
                        .map(employee -> employee.getFirstName() + " " + employee.getSurname()).findFirst() // first True
                        .orElse(null);

        System.out.println(result);
        assertThat(result, sameBeanAs("Holly Davies"));
    }

    @Test
    public void exercise_2_groupingBy_counting() {
        // find the total number of groups of at least 5 employees living close to each other
        // consider all employees with the same 2 first characters of the home address post code a single group
        // you can collect to map and then stream over it, however the solution has to be a single statement

        long result = 0;
        result = EMPLOYEES.stream()
                .collect(groupingBy(employees -> employees.getHomeAddress().getPostCode().substring(0, 2)))
                        .values().stream().filter(employees -> employees.size() >= 5).count(); // count() returneaza din Lista in Long

        System.out.println(result);
        assertThat(result, sameBeanAs(110L));
    }

    @Test
    public void exercise_3_flatMap() {
        // find the total number of all different home and correspondence addresses

        long result = 0;
        result = EMPLOYEES.stream()
        .flatMap(employees -> Stream.of(employees.getHomeAddress(),employees.getCorrespondenceAddress().get())).distinct().count(); // flatMap (grupeaza) creeaza o noua matrice din home + addresses
        System.out.println(result); // employees.getCorrespondenceAddress().get() = cu .get() luam / returneaza valorile adreselor angajatilor
        assertThat(result, sameBeanAs(1820L)); // optional se fol. pt creare instante / obiecte goale
    }

    @Test
    public void exercise_4_groupingBy_summingInt() {
        // find how much in total each company pays (annually) to their employees, order result by amount
        // you can convert the salaries to ints using BigDecimal#intValue method
        // you can collect to map and then stream over it, however the solution has to be a single statement

        DecimalFormat decimalFormat = new DecimalFormat("£#,###.00");
        List<String> result = null;

        result = EMPLOYEES.stream()
                .collect(groupingBy(company -> company.getCompany().getName(), // key - string - company
                        summingInt(salary -> salary.getSalary().intValue()))) // value - int - salary
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // compare by value - salary
                .map(s -> s.getKey() + " - " + decimalFormat.format(s.getValue()))
                .collect(toList());

        System.out.println(result);

        assertThat(result, sameBeanAs(asList(
                "Anglo American - £12,119,153.00",
                "HSBC - £11,469,144.00",
                "Royal Bank of Scotland Group - £11,127,807.00",
                "BP - £10,925,088.00",
                "AstraZeneca - £10,507,305.00",
                "HBOS - £10,428,819.00",
                "Royal Dutch Shell - £10,100,098.00",
                "Barclays plc - £10,071,534.00",
                "Vodafone Group - £10,029,401.00",
                "GlaxoSmithKline - £9,499,235.00"
        )));
    }

    @Test
    public void exercise_5_patternCompileSplitAsStream() {
        // count the instances of words and output a list of formatted strings
        // output the strings sorted lexicographically by name
        // you can use collect twice
        // as always, a single statement solution is expected
        // hint: look at Pattern.compile(regex).splitAsStream(string) // split-uie String-ul dupa ce Pattern ii dau (regex)

        String string = "dog" + "\n" + "bird" + "\n" + "cat" + "\n" + "cat" + "\n" + "dog" + "\n" + "cat";
        List<String> result = null;

        result = Pattern.compile("\n").splitAsStream(string)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                // face un Map de perechi cu String-uri = keys = animale si Long = values = nr. animale;
                // Collectors.groupingBy grupeaza cuv. // Collectors.counting() numara aparitiile fiecarui grup.
                .entrySet() // fol. fct. entrySet() ca apoi sa folosim .Stream() == aplicam functiile de Stream, pt ca pe Map nu putem aplica fct.
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByKey()) // comparingByKey()
                .map(s -> s.getKey() + " - " + s.getValue())
                .collect(toList());

        System.out.println(result);
        assertThat(result, sameBeanAs(asList(
                "bird - 1",
                "cat - 3",
                "dog - 2"
        )));
    }


    @Test
    public void exercise_6_mapToLong() {
        // the rows and columns of the chess board are assigned arbitrary numbers (instead of letters and digits)
        // the value of the square is a multiplication of the numbers assigned to corresponding row and column
        // e.g. square A1 has a value 6,432 * 6,199 = 39,871,968
        // calculate the sum of values of all squares
        int[] rows = {6432, 8997, 8500, 7036, 9395, 9372, 9715, 9634};
        int[] columns = {6199, 9519, 6745, 8864, 8788, 7322, 7341, 7395};
        long result = 0;

        result = Arrays.stream(rows)
                .flatMap(a -> Arrays.stream(columns).map(b -> a * b))
                .mapToLong(n -> n)
                .reduce(0, (a, b) -> a + b); // reduce afisarea calculului sumei produselor la un singur rezultat.

        System.out.println(result);
        assertThat(result, sameBeanAs(4294973013L));
    }


    @Test
    public void exercise_7_randomLongs_concat_toArray() {
        // concatenate two random streams of numbers (seed is fixed for testing purposes),
        // then revert the sign of the negative ones
        // then sort them and pick 10 middle elements (hint: you can use skip and limit)
        // then do modulo 1000 (remainder of division by 1000)
        // and finally collect the result into an array
        LongStream longs = new Random(0).longs(10);
        IntStream ints = new Random(0).ints(10);
        int[] result = null;

        result = LongStream.concat(longs, ints.asLongStream())// am convertit Stream de int la Long ca sa le putem concatena.
                .boxed()
                .map(n -> Math.abs(n)) // abs = absolute value, from negative to positive.
                .sorted()
                .skip(5)
                .limit(10) // 10 middle elements picked.
                .map(n -> n % 1000)
                .mapToInt(x -> Math.toIntExact(x)) // nr. convertite in int
                .toArray(); // si apoi colectate in Array
        for (int i : result) {
            System.out.println(i);
        }
        
        assertThat(result, sameBeanAs(new long[] {106, 266, 402, 858, 313, 688, 303, 137, 766, 896}));
    }

}
