package Need.example.Need;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stream {
    List<String> stringList = new ArrayList<>();
    @Test
    void test(){
        List<Integer> stockList = Arrays.asList(70,50,20,50,80,10,50);
        int result = 0;
//        for(int i=0;i<stockList.size();i++){
//            for(int j=i+1;j<stockList.size();j++){
//                result = Math.max(result,stockList.get(j)-stockList.get(i));//// i=0
//            }
//        }


        int i =0;
        int j=i+1;

        while(i<stockList.size()-1){
          //  System.out.println(stockList.get(j)-stockList.get(i)+i+"i"+j+"j");
            result = Math.max(result,stockList.get(j)-stockList.get(i));

            j++;
            i++;


        }
        System.out.print(result);
    }

}
