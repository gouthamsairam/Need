package Need.example.Need;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class NeedSubArray {
    @Test
    void printAllSubStrings() {
        String  str  = "abcde";
        for(int i=0;i<=str.length();i++){
            for(int j=i+1;j<=str.length();j++){
                System.out.println(str.substring(i,j));
            }
           // System.out.println("");
        }
    }
    @Test
    void subarrays(){
        int arr [] = {1,2,3,4,5};
        for(int i=0;i<=arr.length;i++){
            for(int j =i+1;j<=arr.length;j++){
                for(int k=i;k<j;k++){
                    System.out.print(arr[k]);
                }
                System.out.println();
            }
        }
    }

    @Test
    void LongestnonrepeatedString(){
        String str = "Gomuutthamm";
        List<String> list = new ArrayList<>();
        for(int i=0;i<=str.length();i++){
            for(int j=i+1;j<=str.length();j++){
                list.add(str.substring(i,j));
            }

        }
        int result = 0;
        String ans ="";
        for(String s: list){
            boolean check = false;
            for(int i=0;i<s.length();i++){
              for(int j=i+1;j<s.length();j++){
                  if(s.charAt(i)==s.charAt(j)){
                      check = true;
                      break;
                  }
              }
            }
            if(!check && s.length()>result){
                result = s.length();
                 ans = s;
            }
        }
        System.out.print(ans + ""+ result);
    }
@Test
    void sameKsizesum(){
        int k =3;
        int [] arr = {1,2,3,4,5};
        for(int i=0;i<=arr.length-k;i++){
            for(int j =0;j<k;j++){
                System.out.print(arr[i+j]);
            }
            System.out.println();
        }
    }
    @Test
    void sameKsizesumgreater(){
        int k =3;
        int [] arr = {1,2,3,4,5};
        int result = 0;
        for(int i=0;i<=arr.length-k;i++){
            int a =0;
            for(int j =0;j<k;j++){

               a = a +arr[i+j];
            }
            if(a>result){
                result = a;
            }
            System.out.println();
        }
        System.out.println(result);

    }

}
