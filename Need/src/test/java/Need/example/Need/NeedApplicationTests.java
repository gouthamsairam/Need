package Need.example.Need;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.tags.EscapeBodyTag;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class NeedApplicationTests {

    @Test
    void contextLoads() {

    }

    @Test
    void PrintNth_Non_repaeating_character() {
        String str = "GGowthamsairamz";
        int k = 1;
        int a = 0;
        for (int i = 0; i < str.length(); i++) {
            boolean check = false;
            for (int j = 0; j < str.length(); j++) {
                if (i!=j && str.charAt(i) == str.charAt(j)) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                a++;
                if (a == k) {
                    System.out.println(str.charAt(i));
                    break;
                }
            }
        }
        Map<Character,Integer> charCount = new HashMap<>();
        for (char c : str.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }
        int count = 0;
        for (char c : str.toCharArray()) {
            if (charCount.get(c) == 1) {
                count++;
                if (count == k) {
                    System.out.println(c);
                    break;
                }
            }
        }
    }



}
