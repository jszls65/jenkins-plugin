package com.zls.jenkinsplugin;

import lombok.Data;
import org.junit.Test;

public class MyTest {
    @Test
    public void test01(){
        String txt = "Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 25.602 sec";

        String[] list = txt.split(",");
        TestObj obj = new TestObj();
        for (int i=0; i<list.length;i++){

            String ky = list[i];
            String key = ky.trim().split(": ")[0];
            String value = ky.trim().split(": ")[1];
            switch (key){
                case "Tests run":
                    obj.setTotal(value);
                    break;
                case "Failures":
                    obj.setFailures(value);
                    break;
                case "Errors":
                    obj.setErrors(value);
                    break;


            }
        }


        System.out.println(obj.toString());


    }
}

@Data
class TestObj{
    private String total;
    private String failures;
    private String errors;
    private String skipped;

    @Override
    public String toString() {
        return "TestObj{" +
                "total='" + total + '\'' +
                ", failures='" + failures + '\'' +
                ", errors='" + errors + '\'' +
                ", skipped='" + skipped + '\'' +
                '}';
    }
}