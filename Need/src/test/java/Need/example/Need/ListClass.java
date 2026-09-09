package Need.example.Need;

import org.junit.jupiter.api.Test;

import java.util.*;

public class ListClass {
    @Test
    void findDuplicates() {

        List<Integer> input =
                Arrays.asList(10, 20, 30, 20, 40, 30, 50, 20);

        List<Integer> expected =
                Arrays.asList(20, 30);




    }
    @Test
    void removeDuplicates() {

        List<Integer> input =
                Arrays.asList(5, 2, 5, 3, 2, 1, 3, 4);

        List<Integer> expected =
                Arrays.asList(5, 2, 3, 1, 4);




    }
    @Test
    void findSecondHighest() {

        List<Integer> input =
                Arrays.asList(10, 25, 5, 40, 30, 40, 15);

        int expected = 30;




    }
    @Test
    void findMinMax() {

        List<Integer> input =
                Arrays.asList(45, 12, 78, 23, 9, 56);

        // Expected:
        // Minimum = 9
        // Maximum = 78





    }

    @Test
    void findFrequency() {
        List<String> input =
                Arrays.asList("A", "B", "A", "C", "B", "A", "D", "C");
        Map<String, Integer> frequencyMap = new HashMap<>();

        input.stream().forEach(s -> {
            frequencyMap.put(s, frequencyMap.getOrDefault(s, 0) + 1);
        });
        System.out.println(frequencyMap);

    }
    @Test
    void reverseList() {

        List<Integer> input =
                Arrays.asList(10, 20, 30, 40, 50);

        List<Integer> expected =
                Arrays.asList(50, 40, 30, 20, 10);


    }
    @Test
    void findCommonElements() {

        List<Integer> list1 =
                Arrays.asList(1, 2, 3, 4, 5,7,7);

        List<Integer> list2 =
                Arrays.asList(3, 4, 5, 6, 7,88);

        List<Integer> expected =
                Arrays.asList(3, 4, 5);
        for(int i=0;i<list1.size();i++){
            for(int j=0;j<list2.size();j++){
                if(list1.get(i).equals(list2.get(j))){
                    System.out.println(list1.get(i));
                }
            }
        }
        Set<Integer> set = new HashSet<>(list2);
        System.out.println("one ittration");
        for (Integer value : list1) {
            if (set.contains(value)) {
                System.out.println(value);
            }
        }
        System.out.println("Stream");
        list1.stream().filter(s->list2.contains(s)).forEach(System.out::println);

    }
    @Test
    void findDifference() {

        List<Integer> list1 =
                Arrays.asList(1, 2, 3, 4, 5);

        List<Integer> list2 =
                Arrays.asList(3, 4, 6, 7);

        List<Integer> expected =
                Arrays.asList(1, 2, 5);


    }
    @Test
    void sortByLength() {

        List<String> input =
                Arrays.asList(
                        "Java",
                        "Spring",
                        "API",
                        "Microservices",
                        "AWS"
                );

        List<String> expected =
                Arrays.asList(
                        "API",
                        "AWS",
                        "Java",
                        "Spring",
                        "Microservices"
                );


    }
    @Test
    void findFirstNonRepeated() {

        List<Integer> input =
                Arrays.asList(4, 5, 1, 4, 5, 2, 1, 3);

        Integer expected = 2;


    }
    @Test
    void findThirdHighest() {
//        input:  [10, 50, 20, 40, 30, 50, 60]
//        output: 40
    }
    @Test
    void findCommonFromThreeLists() {
//        input:
//        list1 = [1, 2, 3, 4, 5]
//        list2 = [2, 3, 4, 6]
//        list3 = [3, 4, 7, 8]
//
//        output: [3, 4]
    }
    @Test
    void findNumbersGreaterThan() {
//        input:
//        numbers = [10, 25, 5, 40, 30, 15]
//        value = 20
//
//        output: [25, 40, 30]
    }
    @Test
    void findEvenNumbers() {
//        input:  [1, 2, 3, 4, 5, 6, 7, 8]
//        output: [2, 4, 6, 8]
    }
    @Test
    void moveZerosToEnd() {
       /* input:  [0, 1, 0, 3, 12, 0, 5]
        output: [1, 3, 12, 5, 0, 0, 0]*/
    }
    /// //2. Set Problems
    @Test
    void removeDuplicatesUsingSet() {
     /*   input:  [10, 20, 10, 30, 20, 40, 30]
        output: [10, 20, 30, 40]*/
    }
    @Test
    void findIntersection() {
//        input:
//        set1 = [10, 20, 30, 40]
//        set2 = [30, 40, 50, 60]
//
//        output: [30, 40]
    }
    @Test
    void findSetDifference() {
        /*input:
        set1 = [1, 2, 3, 4, 5]
        set2 = [3, 4, 5, 6, 7]

        output: [1, 2]*/
    }
    @Test
    void findCommonSetElements() {
        /*input:
        set1 = [10, 20, 30, 40, 50]
        set2 = [20, 40, 60, 80]

        output: [20, 40]*/
    }
    @Test
    void findFirstDuplicateUsingSet() {
       /* input:  [10, 20, 30, 40, 20, 50, 30]
        output: 20*/
    }
    @Test
    void findUniqueElementsUsingSet() {
        /* input:  [1, 2, 2, 3, 4, 4, 5]
           output: [1, 3, 5] */
    }
    @Test
    void sortSetAscending() {
        /* input:  [50, 10, 40, 20, 30]
           output: [10, 20, 30, 40, 50] */
    }
    @Test
    void sortSetDescending() {
     /*   input:  [50, 10, 40, 20, 30]
        output: [50, 40, 30, 20, 10]*/
    }
    @Test
    void findLowestUsingSet() {
    /*    input:  [45, 12, 78, 23, 9, 56]
        output: 9*/
    }
    @Test
    void findSecondHighestUsingSet() {
        /*input:  [10, 50, 20, 40, 30, 50]
        output: 40*/
    }
    @Test
    void checkSetsEqual() {
    /*    input:
        set1 = [1, 2, 3, 4]
        set2 = [4, 3, 2, 1]

        output: true*/
    }
    @Test
    void checkSubset() {
        /*input:
        set1 = [2, 3]
        set2 = [1, 2, 3, 4, 5]

        output: true*/
    }
    @Test
    void findCharacterFrequency() {
        /*input:  "programming"
        output: {p=1, r=2, o=1, very important Spring transaction interview questions.md=2, a=1, very strong production-level interview questions.md=2, i=1, n=1}*/
    }
    @Test
    void findFirstNonRepeatedCharacter() {
       /* input:  "swiss"
        output: w*/
    }
    @Test
    void findFirstRepeatedCharacter() {
       /* input:  "swiss"
        output: Spring Security.md*/
    }
    @Test
    void findWordFrequency() {
        /*input:  "java spring java kafka spring java"
        output: {java=3, spring=2, kafka=1}*/
    }
    @Test
    void findDuplicateWords() {
        /*input:  "java spring java kafka spring java docker"
        output: [java, spring]*/
    }
    @Test
    void sortMapByKey() {
       /* input:  {C=30, A=10, D=40, B=20}
        output: {A=10, B=20, C=30, D=40}*/
    }
    @Test
    void sortMapByValue() {
        /*input:  {A=50, B=20, C=80, D=40}
        output: {B=20, D=40, A=50, C=80}*/
    }
    @Test
    void findCommonKeys() {
      /*  input:
        map1 = {A=10, B=20, C=30}
        map2 = {B=50, C=60, D=70}

        output: [B, C]*/
    }
    @Test
    void findKeysOnlyInFirstMap() {
        /*input:
        map1 = {A=10, B=20, C=30}
        map2 = {B=50, C=60, D=70}

        output: [A]*/
    }
    @Test
    void findDuplicateValues() {
      /*  input:  {A=100, B=200, C=100, D=300, E=200}
        output: {100=[A, C], 200=[B, E]}*/
    }
    @Test
    void findKeyWithMaximumValue() {
        /*input:  {A=100, B=500, C=200, D=800}
        output: D*/
    }
    @Test
    void findKeyWithMinimumValue() {
       /* input:  {A=100, B=500, C=200, D=800}
        output: A*/
    }
    @Test
    void findValuesGreaterThan() {
            /*input:
            map = {A=100, B=500, C=200, D=800}
            value = 300

            output: {B=500, D=800}*/
       /* {
            System.out.println("Hello World");
            Integer [] arr = {1,3,4,5,67,8,9,7};
            //	Arrays.sort(arr,(a,b)->b-a);
            //	Arrays.sort(arr,(a,b)->b.compareTo(a));
            //Arrays.sort(arr,(a,b)->Integer.compare(b,a));
            // Arrays.sort(arr, Comparator.naturalOrder());
            // Arrays.sort(arr, Comparator.reverseOrder());
            int [][] arr2D = {{6,5,3},{7,9,8,2},{55,33,22,54},{98,33,54,67,45},{88,66,45,67}};
		*//*Arrays.sort(arr2D);
		this will not work for 2D array
		*//*
            *//*Arrays.sort(arr2D, (a, b) -> Integer.compare(a[0], b[0]));
             *//*
            Arrays.sort(arr2D, (a, b) -> a[0] -b[0]);

            for(int i=0; i<arr2D.length; i++) {
                for(int j=0; j<arr2D[i].length; j++) {
                    System.out.print(arr2D[i][j]+",");
                }
                System.out.println();

            }
            Arrays.sort(arr,(a,b)->a.compareTo(b));
            List<Integer> li = Arrays.asList(22,55,66,4,2,44,1,66);
            //  	Collections.sort(li);
            // Collections.sort(li,Comparator.reverseOrder());
            // Collections.sort(li,Comparator.naturalOrder());
            // li.sort((a,b)-> Integer.compare(a,b));
            // li.sort((a, b) -> Integer.compare(b, a));
            // li.sort((a, b) -> a.compareTo(b));
            // li.sort((a, b) -> b.compareTo(a));

            // li.stream().forEach(s->{
            //     System.out.println(li+""+s);
            // });
            List<Integer> le =li.stream().map(s->s+10).filter(r->r%2==0).collect(Collectors.toList());
            List<Integer> lb =li.stream().reduce((a,b)->a+b));
            System.out.print(lb);
        }*/
       /* public static void main(String[] args) {
            System.out.println("Hello World");
            Integer [] arr = {1,3,4,5,67,8,9,7};
            //	Arrays.sort(arr,(a,b)->b-a);
            //	Arrays.sort(arr,(a,b)->b.compareTo(a));
            //Arrays.sort(arr,(a,b)->Integer.compare(b,a));
            // Arrays.sort(arr, Comparator.naturalOrder());
            // Arrays.sort(arr, Comparator.reverseOrder());
            int [][] arr2D = {{6,5,3},{7,9,8,2},{55,33,22,54},{98,33,54,67,45},{88,66,45,67}};
		*//*Arrays.sort(arr2D);
		this will not work for 2D array
		*//*
            *//*Arrays.sort(arr2D, (a, b) -> Integer.compare(a[0], b[0]));
             *//*
            Arrays.sort(arr2D, (a, b) -> a[0] -b[0]);

            for(int i=0; i<arr2D.length; i++) {
                for(int j=0; j<arr2D[i].length; j++) {
                    System.out.print(arr2D[i][j]+",");
                }
                System.out.println();

            }
            Arrays.sort(arr,(a,b)->a.compareTo(b));
            List<Integer> li = Arrays.asList(22,55,66,4,2,44,1,66);
            //  	Collections.sort(li);
            // Collections.sort(li,Comparator.reverseOrder());
            // Collections.sort(li,Comparator.naturalOrder());
            // li.sort((a,b)-> Integer.compare(a,b));
            // li.sort((a, b) -> Integer.compare(b, a));
            // li.sort((a, b) -> a.compareTo(b));
            // li.sort((a, b) -> b.compareTo(a));

            // li.stream().forEach(s->{
            //     System.out.println(li+""+s);
            // });
            List<Integer> le =li.stream().map(s->s+10).filter(r->r%2==0).collect(Collectors.toList());
            List<Integer> lb =li.stream().reduce((a,b)->a+b));
            System.out.print(lb);
        }*/


    }
}
