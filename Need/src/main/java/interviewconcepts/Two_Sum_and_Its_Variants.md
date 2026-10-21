# Two Sum and Its Variants — Java Coding Interview Guide

The Two Sum problem is one of the most important coding interview problems. It helps you understand arrays, HashMap, sorting, two pointers, sliding windows, and prefix sums.

I'll explain the core problem first, followed by its most common variants, Java solutions, time complexities, and when to use each approach.

## 1. Two Sum — Basic Problem

Problem: Given an integer array and a target, return the indices of two elements whose sum equals the target.

Example:

```
int[] nums = {2, 7, 11, 15};
int target = 9;
```

Output:

```
[0, 1]
```

Because `nums[0] + nums[1] = 2 + 7 = 9`.

### Approach 1: Brute force

Check every possible pair.

```
public static int[] twoSum(int[] nums, int target) {
    for (int i = 0; i < nums.length; i++) {
        for (int j = i + 1; j < nums.length; j++) {
            if (nums[i] + nums[j] == target) {
                return new int[]{i, j};
            }
        }
    }
    return new int[]{-1, -1};
}
```

- Time complexity: \\(O(n^2)\\)
- Space complexity: \\(O(1)\\)

### Approach 2: HashMap — Optimal for an unsorted array

Instead of searching for every pair, calculate the complement:

\\[ \text{complement} = \text{target} - \text{current element} \\]

For each element, check whether its complement has already been seen.

### Example walkthrough

Array: [2, 7, 11, 15] · Target: 9

| Current | Complement | HashMap | Result    |
| ------- | ---------- | ------- | --------- |
| 2       | 7          | {2: 0}  | Not found |
| 7       | 2          | {2: 0}  | Found!    |

Answer: indices `[0, 1]`.

Java solution:

```
import java.util.*;

public class Main {
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }

            map.put(nums[i], i);
        }

        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        System.out.println(Arrays.toString(twoSum(nums, 9)));
    }
}
```

- Time complexity: \\(O(n)\\) average
- Space complexity: \\(O(n)\\)

Interview tip: Check the complement before inserting the current element. This ensures an element is not used twice, which matters for inputs such as `[3]` with target `6`.

## 2. Two Sum variants

Select a problem to see its key approach and complexity.

Two Sum II — Sorted ArrayEasyTwo pointersThe array is sorted. Start one pointer at the beginning and another at the end. Move the left pointer when the sum is too small, and the right pointer when the sum is too large.Time: O(n) · Space: O(1)nums = [2, 7, 11, 15], target = 9 → indices [1, 2] (1-based).Two Sum — Return All PairsTwo Sum — Count PairsThree SumFour SumTwo Sum in a BSTTwo Sum — Closest to TargetTwo Sum Less Than KTwo Sum — Difference Equals KSubarray Sum Equals KTwo Sum in a Data StreamTwo Sum — Sorted Input, Return Values

## 3. Important variants with Java code

### Variant A: Two Sum II — Sorted array

Use this when the input array is already sorted and the question asks for indices.

```
public static int[] twoSumSorted(int[] nums, int target) {
    int left = 0;
    int right = nums.length - 1;

    while (left < right) {
        int sum = nums[left] + nums[right];

        if (sum == target) {
            return new int[]{left + 1, right + 1};
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }

    return new int[]{-1, -1};
}
```

Why does this work? Because the array is sorted. If the sum is too small, moving the right pointer left can only decrease the sum, so instead we increase the left pointer. The opposite applies when the sum is too large.

### Variant B: Three Sum

Find all unique triplets whose sum equals the target, commonly zero.

```
public static List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) {
            continue;
        }

        int left = i + 1;
        int right = nums.length - 1;

        while (left < right) {
            int sum = nums[i] + nums[left] + nums[right];

            if (sum == 0) {
                result.add(Arrays.asList(
                    nums[i], nums[left], nums[right]
                ));

                left++;
                right--;

                while (left < right &&
                       nums[left] == nums[left - 1]) left++;

                while (left < right &&
                       nums[right] == nums[right + 1]) right--;

            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }

    return result;
}
```

- Time complexity: \\(O(n^2)\\)
- Main lesson: sort, fix one element, use two pointers, skip duplicates.

### Variant C: Count pairs with sum equal to target

Count pairs of distinct indices, including repeated values at different indices.

```
public static long countPairs(int[] nums, int target) {
    Map<Integer, Integer> freq = new HashMap<>();
    long count = 0;

    for (int num : nums) {
        count += freq.getOrDefault(target - num, 0);
        freq.merge(num, 1, Integer::sum);
    }

    return count;
}
```

Example:

```
int[] nums = {1, 1, 2, 3};
System.out.println(countPairs(nums, 3)); // 2
```

The two pairs are `(index 0, index 2)` and `(index 1, index 2)`.

### Variant D: Subarray Sum Equals K

This is related to Two Sum, but it uses prefix sums because the problem asks about contiguous subarrays, not arbitrary pairs.

```
public static int subarraySum(int[] nums, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1);

    int sum = 0;
    int count = 0;

    for (int num : nums) {
        sum += num;

        count += prefixCount.getOrDefault(sum - k, 0);

        prefixCount.merge(sum, 1, Integer::sum);
    }

    return count;
}
```

Example: `[1, 1, 1]`, `k = 2` returns `2`.

Why initialize `prefixCount` with `{0: 1}`? It accounts for subarrays that start at index zero.

## 4. Which approach should you choose?

| Problem condition                      | Best starting approach                |
| -------------------------------------- | ------------------------------------- |
| Unsorted array, return indices         | HashMap                               |
| Sorted array, return indices           | Two pointers                          |
| Return all unique triplets             | Sorting + two pointers                |
| Return all unique quadruplets          | Sorting + nested loops + two pointers |
| Count index pairs                      | Frequency HashMap                     |
| Find contiguous subarrays summing to K | Prefix sum + HashMap                  |
| Find the closest pair sum              | Sorting + two pointers                |
| Find a pair in a BST                   | HashSet or inorder traversal          |

## 5. Recommended interview practice order

### Practice checklist

0 of 8 completed

Two Sum — LeetCode 1

Two Sum II — LeetCode 167

3Sum — LeetCode 15

4Sum — LeetCode 18

Subarray Sum Equals K — LeetCode 560

Two Sum IV — Input is a BST — LeetCode 653

Max Number of K-Sum Pairs — LeetCode 1679

Count Good Meals — LeetCode 1711

My recommendation: Master the HashMap solution, then the sorted two-pointer solution, and then Three Sum. Once you understand these three patterns, many Two Sum variants become easier to recognize and solve.