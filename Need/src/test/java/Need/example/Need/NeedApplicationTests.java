package Need.example.Need;

import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.tags.EscapeBodyTag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class NeedApplicationTests {

    @Data
    class Employee {
        private int id;
        private String name;
        private double salary;

        public Employee(int id, String name, double salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getSalary() {
            return salary;
        }
    }
    @Test
    void contextLoads() {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "John", 50000),
                new Employee(2, "Peter", 70000),
                new Employee(3, "David", 90000),
                new Employee(4, "Sam", 80000),
           new Employee(4, "Sam", 80000)
        );
System.out.print(employees);
//sort employess
        employees.stream().distinct().forEach(System.out::println);
    }






}
