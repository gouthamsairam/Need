package Need.example.Need;

import org.junit.jupiter.api.Test;

import java.util.*;

public class LeetCode {

    @Test
    void maxWater() {
        int[] arr = {2, 1, 8, 6, 4, 6, 5, 5};
        int n = arr.length;
        int res = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                // calculate the amount of water
                int amount = Math.min(arr[i], arr[j]) * (j - i);
//System.out.print("arr[" + i + "] = " + arr[i] + ", arr[" + j + "] = " + arr[j] + " j" + j + " i" + i + " i-j " + (j - i) + ", amount = " + amount + "\n");
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
    void palandromicString() {
        String str = "LEVELA";
        int left = 0;
        int right = str.length() - 1;
        boolean b = true;
        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                b = false;
                break;
            }

            left++;
            right--;
        }
        if (b) {
            System.out.println("Palindrome");
        } else {
            System.out.println("Not a Palindrome");
        }
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
    public void groupAnagrams() {
        String[] strs = {"act", "god", "cat", "dog", "tac", "rat", "tar"};

        Map<String, List<String>> map = new HashMap<>();

        for (String str : strs) {

            // Convert string to character array
            char[] chars = str.toCharArray();

            // Sort characters
            Arrays.sort(chars);

            // Create key
            String key = new String(chars);

            // Add string to corresponding group
            //1st way
          /*  map.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(str);*/
            //2nd way
            if (map.containsKey(key)) {
                map.get(key).add(str);
            } else {
                List<String> list = new ArrayList<>();
                list.add(str);
                map.put(key, list);
            }
        }
        System.out.print(map.values());
    }

    @Test
    void revrseString() {
        ///  using StringBuilder
        String str = "Goutham";
        StringBuilder s = new StringBuilder(str);
        System.out.println(s.reverse());
    }

    @Test
    public void print_TwoDArraywithSorted() {
        int[][] arr = {{10, 1}, {7, 8}, {1, 5}, {2, 4}, {4, 6}};
        int[] t = {3, 1, 77, 1};
        Arrays.sort(arr, (a, b) -> a[0] - b[0]);
        Arrays.sort(arr, (a, b) -> Integer.compare(a[0], b[0]));
        Arrays.sort(arr, (a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j]);
            }
            System.out.println();
        }
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
    public void rotatedArray() {
        /*Input: nums = [1,2,3,4,5,6,7], k = 3
Output: [5,6,7,1,2,3,4]

Explanation:
rotate 1 steps to the right: [7,1,2,3,4,5,6]
rotate 2 steps to the right: [6,7,1,2,3,4,5]
rotate 3 steps to the right: [5,6,7,1,2,3,4]*/


        int arr[] = {1, 2, 3, 4, 5, 6, 7};
        int k = 3;

        for (int i = 0; i < k; i++) {
            int frist = arr[0];
            for (int j = 0; j < arr.length - 1; j++) {
                arr[j] = arr[j + 1];
            }
            arr[arr.length - 1] = frist;
        }
        System.out.println("left rotate" + Arrays.toString(arr));
        int arr1[] = {1, 2, 3, 4, 5, 6, 7};
        for (int i = 0; i < k; i++) {
            int last = arr1[arr1.length - 1];
            for (int j = arr1.length - 1; j > 0; j--) {
                arr1[j] = arr1[j - 1];
            }
            arr1[0] = last;
        }
        System.out.println("right rotate" + Arrays.toString(arr1));

    }

    @Test
    public void productofArrayExceptitself() {
/*Example 1:

Input: nums = [1,2,3,4]
Output: [24,12,8,6]
Example 2:

Input: nums = [-1,1,0,-3,3]
Output: [0,0,9,0,0]
* */
        int arr[] = {-1, 1, 0, -3, 3};
        int output[] = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int a = 1;
            for (int j = 0; j < arr.length; j++) {
                if (arr[i] == arr[j]) {
                    continue;
                } else {
                    a = a * arr[j];
                }
                output[i] = a;
            }
        }
        System.out.print(Arrays.toString(output));

    }

    @Test
    public void find_all_numbers_disappeared() {
        /*Example 1:

Input: nums = [4,3,2,7,8,2,3,1]
Output: [5,6]
Example 2:

Input: nums = [1,1]
Output: [2]*/
    }

    @Test
    public void rearrangeStringNoCharactersShouldBeConsecutive() {

        String str = "aabbccrrt";

        Map<Character, Integer> map = new HashMap<>();

        // Step 1: Count the frequency of each character
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            map.put(ch, map.getOrDefault(ch, 0) + 1);
        }

        // Step 2: Find the most frequent character
        char maxChar = '\0';
        int max = 0;

        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxChar = entry.getKey();
            }
        }

        // Step 3: Check whether rearrangement is possible
        if (max > (str.length() + 1) / 2) {
            System.out.print("Rearrangement is not possible");
        }

        char[] res = new char[str.length()];
        int idx = 0;

        // Step 4: Place the most frequent character at even indexes
        for (int i = 0; i < max; i++) {
            res[idx] = maxChar;
            idx += 2;
        }

        map.put(maxChar, 0);

        // Step 5: Place all remaining characters
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {

            char ch = entry.getKey();
            int count = entry.getValue();

            while (count-- > 0) {

                if (idx >= res.length) {
                    idx = 1;
                }

                res[idx] = ch;
                idx += 2;
            }
        }

        String result = new String(res);
        System.out.println(result);
    }

    @Test
    public void median_of_Two_Sorted_Arrays() {

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

        int amount = 12;
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
        int[][] matrix = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        List<Integer> res = new ArrayList<>();

        if (matrix.length == 0) {
            System.out.println(res);
        }

        int rowBegin = 0;
        int rowEnd = matrix.length - 1;
        int colBegin = 0;
        int colEnd = matrix[0].length - 1;

        while (rowBegin <= rowEnd && colBegin <= colEnd) {
            // Traverse Right
            for (int j = colBegin; j <= colEnd; j++) {
                res.add(matrix[rowBegin][j]);
            }
            rowBegin++;

            // Traverse Down
            for (int j = rowBegin; j <= rowEnd; j++) {
                res.add(matrix[j][colEnd]);
            }
            colEnd--;

            if (rowBegin <= rowEnd) {
                // Traverse Left
                for (int j = colEnd; j >= colBegin; j--) {
                    res.add(matrix[rowEnd][j]);
                }
            }
            rowEnd--;

            if (colBegin <= colEnd) {
                // Traverse Up
                for (int j = rowEnd; j >= rowBegin; j--) {
                    res.add(matrix[j][colBegin]);
                }
            }
            colBegin++;
        }

        System.out.print(res);
    }


    ///  Two pointers approach
    @Test
    void findTripletsThreeSum() {
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

    @Test
    public void findcommon_prifix() {
        /*
        * Input: arr[] = [“geeksforgeeks”, “geeks”, “geek”, “geezer”]
Output: “gee”
Explanation: “gee” is the longest common prefix in all the given strings: “geeksforgeeks”, “geeks”, “geeks” and “geezer”.

Input: arr[] = [“apple”, “ape”, “april”]
Output : “ap”
Explanation: “ap” is the longest common prefix in all the given strings: “apple”, “ape” and “april”.

Input: arr[] = [“hello”, “world”]
Output: “”
Explanation: There’s.md no common prefix in the given strings.*/
        List<String> arr = Arrays.asList("geeksforgeeks", "geeks", "geek", "geezer");
        Collections.sort(arr);
        int r = 0;
        while (arr.get(0).charAt(r) == arr.get(arr.size() - 1).charAt(r)) {
            r++;
        }
        System.out.println(arr.get(0).substring(0, r));
        // with for loop
        int t = 0;
        for (int i = 0; i < arr.get(0).length(); i++) {
            if (arr.get(0).charAt(i) == arr.get(arr.size() - 1).charAt(i)) {
                System.out.print((arr.get(0).charAt(t)));

                t++;
            }
        }
    }

    @Test
    public void Gas_Station() {

        int[] gas = {1, 2, 3, 4, 5};
        int[] cost = {3, 4, 5, 1, 2};

        int totalGas = 0;
        int totalCost = 0;
        int tank = 0;
        int startIndex = 0;

        for (int i = 0; i < gas.length; i++) {
            totalGas += gas[i];
            totalCost += cost[i];
            tank += gas[i] - cost[i];

            // If tank is negative, we cannot start from the current index
            if (tank < 0) {
                startIndex = i + 1; // Move to the next index
                tank = 0; // Reset the tank
            }
        }

        if (totalGas < totalCost) {
            System.out.println(-1 + "Not possible to complete the circuit"); // Not possible to complete the circuit
        } else {
            System.out.println(startIndex); // Starting index to complete the circuit
        }
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
    void NumberOfIslands() {
        char[][] grid = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };
        System.out.print(numIslands(grid));
    }

    int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++)
                if (grid[i][j] == '1') {
                    dfs(grid, i, j);
                    count++;
                }

        return count;
    }


    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length
                || j < 0 || j >= grid[0].length
                || grid[i][j] == '0') {
            return;
        }

        grid[i][j] = '0'; // Mark the cell as visited

        // Explore all four directions
        dfs(grid, i + 1, j);
        dfs(grid, i - 1, j);
        dfs(grid, i, j + 1);
        dfs(grid, i, j - 1);
    }

    //Least Recently Used
    public static class LRU {

        LinkedHashSet<Integer> list; //Data Structure to implement cache
        int MAXSIZE;                //Maximum size of the cache
        static int faults;            //Count the total number of page faults

        public LRU(int cacheSize) {
            list = new LinkedHashSet<Integer>();
            MAXSIZE = cacheSize;
        }

        public void faultHandle(int page) {
            if (list.size() == MAXSIZE) {
                //eviction policy
                int first = list.iterator().next();
                list.remove(first);
                list.add(page);
            } else {
                list.add(page);
            }
            faults++;
        }

        public void shuffle(int page) { // 2 3 4 (3)
            list.remove(page);            // 2 4
            list.add(page);                // 2 4 3
        }

        public void refer(int page) {
            if (list.contains(page)) { //if the hashset already contains the page
                shuffle(page);
            } else {
                faultHandle(page);
            }
        }

        public static void main(String[] args) {

            int pages[] = {3, 2, 1, 3, 4, 1, 6, 2, 4, 3, 4, 2, 1, 4, 5, 2, 1, 3, 4};

            LRU cache = new LRU(3);

            for (int page : pages) {
                cache.refer(page);
            }

            System.out.println("Pages in cache: " + cache.list);
            System.out.println("Number of page faults: " + faults);


        }

    }


    @Test
    public void fibonacci() {
        int n = 1;
        if (n <= 1) {
            System.out.println(5);

        }

        int a = 0, b = 1;

        for (int i = 2; i <= n; i++) {
            int c = a + b;
            a = b;
            b = c;
        }

        System.out.print(b);
    }

}
