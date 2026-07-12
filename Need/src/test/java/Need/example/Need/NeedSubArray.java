package Need.example.Need;

import org.junit.jupiter.api.Test;

import java.util.*;

public class NeedSubArray {
    @Test
    void printAllSubStrings() {
        String str = "abcde";
        for (int i = 0; i <= str.length(); i++) {
            for (int j = i + 1; j <= str.length(); j++) {
                System.out.println(str.substring(i, j));
            }
            // System.out.println("");
        }
    }

    @Test
    void subarrays() {
        int arr[] = {1, 2, 3, 4, 5};
        for (int i = 0; i <= arr.length; i++) {
            for (int j = i + 1; j <= arr.length; j++) {
                for (int k = i; k < j; k++) {
                    System.out.print(arr[k]);
                }
                System.out.println();
            }
        }
    }

    @Test
    void LongestnonrepeatedString() {
        String str = "Gomuutthamm";
        List<String> list = new ArrayList<>();
        for (int i = 0; i <= str.length(); i++) {
            for (int j = i + 1; j <= str.length(); j++) {
                list.add(str.substring(i, j));
            }

        }
        int result = 0;
        String ans = "";
        for (String s : list) {
            boolean check = false;
            for (int i = 0; i < s.length(); i++) {
                for (int j = i + 1; j < s.length(); j++) {
                    if (s.charAt(i) == s.charAt(j)) {
                        check = true;
                        break;
                    }
                }
            }
            if (!check && s.length() > result) {
                result = s.length();
                ans = s;
            }
        }
        System.out.print(ans + "" + result);
    }

    @Test
    void sameKsizesum() {
        int k = 3;
        int[] arr = {1, 2, 3, 4, 5};
        for (int i = 0; i <= arr.length - k; i++) {
            for (int j = 0; j < k; j++) {
                System.out.print(arr[i + j]);
            }
            System.out.println();
        }
    }

    @Test
    void sameKsizesumgreater() {
        int k = 3;
        int[] arr = {1, 2, 3, 4, 5};
        int result = 0;
        for (int i = 0; i <= arr.length - k; i++) {
            int a = 0;
            for (int j = 0; j < k; j++) {

                a = a + arr[i + j];
            }
            if (a > result) {
                result = a;
            }
            System.out.println();
        }
        System.out.println(result);

    }
    @Test
void Higestsbuarrayaveargevalue_of_K_Size(){
        int[] arr = {12,33,3,4,5,6,7,8,8,11,65,9};
        int k =3;int sum =0;
        for(int i=0;i<k;i++){
            sum = sum+arr[i];
        }
        int start = 0;
        int end = k;
        int max = sum;
        while(end<arr.length){
            for(int i=start;i<end;i++){
                System.out.print(arr[i]+",");
            }
            sum = sum - arr[start];
            start++;
            sum = sum+arr[end];
            end++;
           max = Math.max(max,sum);
            System.out.println();

        }

        System.out.println(max/k);
}
    @Test
    void tostrings() {


        String a = "cat";
        String b = "zer";

        // Broot force approach

        //Omptmized approach
        Set<Character> set1 = new HashSet<>();

        Set<Character> set2 = new HashSet<>();
for(Character c : a.toCharArray()){
    set1.add(c);
}
        for(Character c : b.toCharArray()){
            set2.add(c);
        }
        set1.retainAll(set2);
       if(set1.isEmpty()){
           System.out.print("no");
       }else {
           System.out.print("yes");
       }
    }

}
