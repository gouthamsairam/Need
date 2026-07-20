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
    @Test
    public void allSubStrings(){
        String str = "abcd";
        for(int i =0;i<str.length();i++){
            for(int j =i;j<str.length();j++){
                System.out.println(str.substring(i,j));
            }
            // System.out.println(str.substring(0,i));
        }
    }
    @Test
    public void allSubArrays(){
        int a [] = {1,2,3,4,5};
        for(int i =0;i<a.length;i++){
            for(int  j=0;j<a.length;j++){

                for(int k =i;k<=j;k++){
                    System.out.print(a[k]+" ");
                }
                System.out.println();
            }
        }
    }
    @Test
    public void longestSubstringnonrepeted(){
        String str = "pwwkew";
        int a =0;
        int max =0;
        int start = 0;

        Set<Character> set = new HashSet<>();
        for(int i =0;i<str.length();i++){
            while (set.contains(str.charAt(i))) {
                set.remove(str.charAt(a));
                a++;
            }
            if (i - a + 1 > max) {
                max = i - a + 1;
                start = a;
            }
            set.add(str.charAt(i));
            System.out.println(set);
            //max = Math.max(max,i-a+1);

        }
        System.out.print(max);
        System.out.println("Longest substring: " + str.substring(start, start + max));


    }
    @Test
    public void maxSubArrays() {
        // int arr [] = {1,2,3,4,5};
/*
*
*
1
1 2
1 2 3
1 2 3 4
1 2 3 4 5
2
2 3
2 3 4
2 3 4 5
3
3 4
3 4 5
4
4 5
5
* */
        int arr[] = {2, 3, -8, 7, -1, 2, 3};
        int n = arr.length;
        int k =3;
        int minsum=1;
        int maxsum =0;
        int minstart =0;
        int minEnd=0;
        int maxstart =0;
        int maxEnd =0;

        for(int  i=0;i<n;i++){
            int cursum =0;
            for(int j=i;j<n;j++){
                cursum += arr[j];
                if(cursum>maxsum){
                    maxsum = cursum;
                    maxstart = i;
                    maxEnd = j;
                }
                if(cursum<minsum){
                    minsum = cursum;
                    minstart = i;
                    minEnd = j;
                }
                // minsum = Math.min(minsum,cursum);
                // maxsum  = Math.max(maxsum,cursum);
            }
        }
        for(int i=maxstart;i<=maxEnd;i++){
            System.out.print(arr[i]);
        }
        System.out.println("this"+minsum + " " + maxsum);

    }



    @Test
    public void subarraywithklength() {
        int arr[] = {1, 2, 3, 4, 5};
        int k = 3;
        int ans = 0;
        for (int i = 0; i <= arr.length - k; i++) {
            int temp = 0;
            for (int j = 0; j < k; j++) {
                temp = temp + arr[i + j];
            }
            ans = Math.max(ans, temp);
        }
        System.out.print(ans);
    }
    @Test
    public void printAllSubarrayswihSameLength(){
        int arr[]  = {1,2,3,4,5};
        int k =3;

        for(int i=0 ;i<=arr.length-k;i++){
            for(int j =0;j<k;j++){
                System.out.print(arr[i+j]);
            }
            System.out.println();
        }
    }
    @Test
    public void printlongestSubstringnonrepeted(){
        String str = "abcdefabbace";
        List<String> allsub = new ArrayList<>();
        for(int  i =0;i<str.length();i++){
            allsub.add(str.substring(0,i));
        }
        boolean check = false;
        String ans = "";
        for(String s : allsub){
            for(int i =0;i<s.length();i++){
                for(int j=i+1;j<s.length();j++){
                    if (s.charAt(i) == s.charAt(j)) {
                        check = true;
                        break;
                    }
                }
            }
            if(!check){
                ans = s;
            }
        }
        System.out.println(allsub);
        System.out.println(ans);

    }





    @Test
    public void Number_of_Sub_arrays_of_Size_K_and_Average_Greater_than_or_Equal_to_Threshold(){

    }



    @Test
    public void LongestSubarrayWith_Sum_K(){
        int[] arr = {10, 5, 2, 7, 1, -10};
        int k = 15;
        int sum =0;
        for(int i=0;i<arr.length;i++){

            for(int j =i;j<arr.length;j++){
                sum = sum +arr[j];
                if(sum == k){
                    System.out.print("Sum"+j);
                    System.out.println();
                    for(int l =i;l<=j;l++){
                        System.out.print(arr[l]);
                    }
                    int subLen = j - i + 1;
                    sum = Math.max(sum, subLen);
                }
                System.out.println();
            }

        }
        String s ="i am goutham sai ram";
        String[] d = s.split(" ");

    }

}
