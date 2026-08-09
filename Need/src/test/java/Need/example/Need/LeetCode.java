package Need.example.Need;

import org.junit.jupiter.api.Test;

import java.util.*;

public class LeetCode {
//    @Test
//    Void  twoSum(){

    /// /        int n = arr.length;
//        int[] arr = {2, 1, 8, 6, 4, 6, 5, 5};
//        for (int i = 0; i < n; i++) {
//
//            // For each element arr[i], check every
//            // other element arr[j] that comes after it
//            for (int j = i + 1; j < n; j++) {
//
//                // Check if the sum of the current pair
//                // equals the target
//                if (arr[i] + arr[j] == target) {
//                    System.out.println("true");
//                }
//            }
//        }
//
//        // If no pair is found after checking
//        // all possibilities
//
//    }
    @Test
    void maxWater() {
        int[] arr = {2, 1, 8, 6, 4, 6, 5, 5};
        int n = arr.length;
        int res = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                // calculate the amount of water
                int amount =
                        Math.min(arr[i], arr[j]) * (j - i);

                // keep track of maximum amount of water
                res = Math.max(amount, res);
            }
        }
        System.out.println(res);
    }

    //    maxprofit stocks
    @Test
    void maxprofit() {
        int[] arr = {7, 10, 1, 3, 6, 9, 2};
        int maxprofit = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                maxprofit = Math.max(maxprofit, arr[i] - arr[j]);
            }
        }
        System.out.println(maxprofit);
    }

    //arrayPairSum
    @Test
    void ArrayPartition() {


        int[] nums = {6, 2, 6, 5, 1, 2, 7};
        //1225667
        Arrays.sort(nums);
        int a = 0;
        for (int i = 0; i < nums.length; i = i + 2) {
            a = a + nums[i];
        }
        System.out.println(a);
    }

    /// To Sum
    @Test
    void twoSum() {
        int[] arr = {0, -1, 2, -3, 1};
        int target = -2;
        int n = arr.length;

        for (int i = 0; i < n; i++) {

            // For each element arr[i], check every
            // other element arr[j] that comes after it
            for (int j = i + 1; j < n; j++) {

                // Check if the sum of the current pair
                // equals the target
                if (arr[i] + arr[j] == target) {
                    System.out.println(i + " " + j);
                    break;
                }
            }
        }
        System.out.println("Not present");
    }
    //minimumCost

    @Test
    void minimumCost() {


        int[] cost = {6, 5, 7, 9, 2, 2};
//        225679
        Arrays.sort(cost);
        int took = 0;
        int ans = 0;
        for (int i = cost.length - 1; i >= 0; i--) {
            if (took == 2) {
                took = 0;
            } else {
                ans = ans + cost[i];
                took++;
            }

        }
        System.out.println(ans);
    }

    @Test
    void movethezerostoEND() {
        int[] arr = {1, 2, 0, 4, 3, 0, 5, 0};
        int temp[] = new int[arr.length];
        int a = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                temp[a] = arr[i];
                a++;
            }
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {
                temp[a] = arr[i];
                a++;
            }
        }
        for (int i = 0; i < temp.length; i++) {
            System.out.print(temp[i]);
        }
    }

    @Test
    void containsduplicates() {
        // int [] arr = {0,1,0,3,1,12};
        //int []arr = {1};
        int[] arr = {1, 2, 3, 4, 6, 4};

        int a = 0;
        int b = arr.length - 1;
        boolean check = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[a] != arr[b]) {
                check = true;
            }
            a++;
            b--;
        }
        if (check) {
            System.out.println("yes duplicate array");
        } else {
            System.out.println("No yes duplicate array");

        }
    }

    @Test
    void findthemissingnumberinArray() {
        int arr[] = {1, 3, 4, 5, 6};
        int n = arr.length + 1;
        int a = n * (n + 1) / 2;
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        System.out.print(a - sum);
    }

    @Test
    void validateAnagrams() {

        String s1 = "geeks";
        String s2 = "kseeg";
        if (s1.length() != s2.length()) {
            System.out.println("Not anagram");
        }

        // Sort both strings
        char[] s1Array = s1.toCharArray();
        char[] s2Array = s2.toCharArray();
        Arrays.sort(s1Array);
        Arrays.sort(s2Array);

        // Compare sorted strings
        System.out.println(Arrays.equals(s1Array, s2Array));
    }

    @Test
    public void permute() {
        int[] nums = {1, 2, 3};
        List<List<Integer>> resultList = new ArrayList<>();

        backtrack(resultList, new ArrayList<>(), nums);
        System.out.println(resultList);
    }

    private void backtrack(List<List<Integer>> resultList,
                           ArrayList<Integer> tempList, int[] nums) {
        // If we match the length, it is a permutation
        if (tempList.size() == nums.length) {
            resultList.add(new ArrayList<>(tempList));
            return;
        }

        for (int number : nums) {
            // Skip if we get same element
            if (tempList.contains(number))
                continue;

            // Add the new element
            tempList.add(number);

            // Go back to try other element
            backtrack(resultList, tempList, nums);

            // Remove the element
            tempList.remove(tempList.size() - 1);
        }
    }

    @Test
    public void groupAnagrams() {
        String txt = "BACDGABCDA";
        String pat = "ABCD";
        int n = txt.length(), m = pat.length();

        //sortedpat stores the sorted version of pat
        char[] sortedpatArr = pat.toCharArray();
        Arrays.sort(sortedpatArr);
        String sortedpat = new String(sortedpatArr);

        // to store the matching indices
        List<Integer> res = new ArrayList<>();

        for (int i = 0; i <= n - m; i++) {

            // renamed from temp to curr
            String curr = txt.substring(i, i + m);
            char[] currArr = curr.toCharArray();
            Arrays.sort(currArr);
            curr = new String(currArr);

            //checking if sorted versions are equal
            if (sortedpat.equals(curr)) {
                res.add(i);
            }
        }

        System.out.print(res);
    }

    @Test
    public void mergeIntervels() {
        int[][] arr = {{7, 8}, {1, 5}, {2, 4}, {4, 6}};

        int n = arr.length;

        Arrays.sort(arr, (a, b) -> Integer.compare(a[0], b[0]));
        ArrayList<int[]> res = new ArrayList<>();

        // Checking for all possible overlaps
        for (int i = 0; i < n; i++) {
            int start = arr[i][0];
            int end = arr[i][1];

            // Skipping already merged intervals
            if (!res.isEmpty() && res.get(res.size() - 1)[1] >= end) {
                continue;
            }

            // Find the end of the merged range
            for (int j = i + 1; j < n; j++) {
                if (arr[j][0] <= end) {
                    end = Math.max(end, arr[j][1]);
                }
            }
            res.add(new int[]{start, end});
        }
        System.out.println(res);
        for (int[] interval : res) {
            System.out.println(interval[0] + " " + interval[1]);
        }
    }

    @Test
    public void productofArrayExceptitself() {

    }

    @Test
    public void rearrangeStringnoTochararactersshouldbeconsective() {
        int[] hash = new int[26];
        String str = "aabbccrr";
        for (int i = 0; i < str.length(); i++) hash[str.charAt(i) - 'a']++;

        int max = 0, letter = 0;
        for (int i = 0; i < hash.length; i++)
            if (hash[i] > max) {
                max = hash[i];
                letter = i;
            }

        if (max > (str.length() + 1) / 2) System.out.println(String.valueOf(" "));
        ;

        char[] res = new char[str.length()];

        // Fill all even places with majority character
        int idx = 0;
        while (hash[letter]-- > 0) {
            res[idx] = (char) (letter + 'a');
            idx += 2;
        }

        // Fill the remaining characters
        for (int i = 0; i < hash.length; i++)
            while (hash[i]-- > 0) {
                if (idx >= res.length) idx = 1;

                res[idx] = (char) (i + 'a');
                idx += 2;
            }

        System.out.println(String.valueOf(res));

    }

    @Test
    public void kthNonRepeatingChar() {
        String str = "geeksforgeeks";
        int k = 3;
        int count = 0;
        char result = '\0';
        for (int i = 0; i < str.length(); i++) {
            boolean repeating = false;
            for (int j = i + 1; j < str.length(); j++) {
                if (str.charAt(i) == str.charAt(j)) {
                    repeating = true;
                    break;
                }
            }
            if (!repeating) {
                count++;
                if (count == k) {
                    System.out.println(str.charAt(i));
                }
            }
        }
    }

    @Test
    public void minimumdiffrences_BetweenHighestand_lowestScores() {

    }

    @Test
    public void MaxFruit_In_Basket() {
        int[] A = {1, 2, 3, 2, 2};
        int n = A.length;
        int maxFruitsCollected = 0;

        for (int i = 0; i < n; i++) {
            Map<Integer, Integer> basket = new HashMap<>(); // track types of fruits
            int count = 0;

            for (int j = i; j < n; j++) {
                basket.put(A[j], basket.getOrDefault(A[j], 0) + 1);

                // Stop if more than 2 types
                if (basket.size() > 2) break;

                count++;
            }

            maxFruitsCollected = Math.max(maxFruitsCollected, count);
        }

        System.out.println(maxFruitsCollected);
    }

    @Test
    public void Coinchange() {

        int amount = 1;
        int coins[] = {2, 5, 3, 6};


        // Check edge case
        if (amount < 1) {
            System.out.println(0);
        }

        // Create DP array
        int[] minCoinsDP = new int[amount + 1];

        for (int i = 1; i <= amount; i++) {

            minCoinsDP[i] = Integer.MAX_VALUE;

            // Try each coin
            for (int coin : coins) {
                if (coin <= i && minCoinsDP[i - coin] != Integer.MAX_VALUE)
                    minCoinsDP[i] = Math.min(minCoinsDP[i], 1 + minCoinsDP[i - coin]);
            }
        }

        System.out.println(minCoinsDP[amount] == Integer.MAX_VALUE ? -1 : minCoinsDP[amount]);

    }

    @Test
    void Spiral_Matrix() {

    }

    ///  Two pointers approach
    @Test
    void findTriplets() {
        int[] arr = {1, 2, 3, 4, 5, 6, 9, 10};
        int target = 11;
        int n = arr.length;

        for (int i = 0; i < n - 2; i++) {
            int left = i + 1;
            int right = n - 1;

            while (left < right) {
                int sum = arr[i] + arr[left] + arr[right];

                if (sum == target) {
                    System.out.println("(" + arr[i] + "," + arr[left] + "," + arr[right] + ")");
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
    }
}
