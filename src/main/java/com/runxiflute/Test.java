package com.runxiflute;
import com.spring.RunxiApplicationContext;

public class Test {
	 public static void main( String[] args )
	 {
		 RunxiApplicationContext applicationContext = new RunxiApplicationContext(AppConfig.class);
		 System.out.println(applicationContext.getBean("userService"));
		 System.out.println(applicationContext.getBean("userService"));
		 System.out.println(applicationContext.getBean("userService"));
		
	 }
}
