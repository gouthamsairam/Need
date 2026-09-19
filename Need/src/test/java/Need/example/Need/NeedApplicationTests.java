package Need.example.Need;

import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.tags.EscapeBodyTag;

import java.util.*;

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
       ///  1 add,deleate ,update,fetch
        List<Employee> list = new ArrayList<>();
        Employee e1 =

                list.forEach(s->{
                    Employee e =new Employee();
//                    list.add(e.getSalary()).add;
                });
        list.stream().filter(s->)
        {
            /*
            * Employee table
            * write a query to featch all employees in Hireache
            * l1 --> delevery mamages( mangers(employess
            *
            *Select Employeename , managename, DelevrymanageName  from Employee
            * Groupby Managername && DeleveryMangername
            * orderBy EmployeeName ASE;
            *
            * 
            *
            * */

        }


    }






}
