package com.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import com.spring.ComponentScan;
import com.spring.Component;

public class RunxiApplicationContext {
	
	private Class configClass;
	private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
	
	public RunxiApplicationContext(Class configClass) {
		this.configClass = configClass;
		scan(configClass);
		
		for (String beanName : beanDefinitionMap.keySet()) {
			BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
			if (beanDefinition.getScope().equals("singleton")) {
				Object bean = createBean(beanDefinition);
				singletonObjects.put(beanName, bean);
			}
		}
	}
	
	public Object createBean(BeanDefinition beanDefinition) {
		Class  beanClass = beanDefinition.getBeanClass();
		
		try {
			Object instance = beanClass.getDeclaredConstructor().newInstance();
			return instance;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void scan(Class configClass) {
		
		
		ComponentScan componentScanAnnotation =  (ComponentScan)configClass.getDeclaredAnnotation(ComponentScan.class);
		String path = componentScanAnnotation.value();
		path = path.replace(".", "/");
		ClassLoader classLoader = RunxiApplicationContext.class.getClassLoader();
		URL resource = classLoader.getResource(path);
		File file = new File(resource.getFile());
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				String absPath = f.getAbsolutePath();
				if (!absPath.endsWith(".class")) continue;
				
				String className = absPath.substring(absPath.indexOf("com"), absPath.indexOf(".class"));
				className = className.replace("/", ".");
				
				try {
					Class<?> beanClass = classLoader.loadClass(className);
					if (beanClass.isAnnotationPresent(Component.class)) {
						Component componentAnnotation = beanClass.getDeclaredAnnotation(Component.class);
						String beanName = componentAnnotation.value();
						
						BeanDefinition beanDefinition = new BeanDefinition();
						
						beanDefinition.setBeanClass(beanClass);
						if (beanClass.isAnnotationPresent(Scope.class)) {
							Scope scopeAnnotation = beanClass.getDeclaredAnnotation(Scope.class);
							beanDefinition.setScope(scopeAnnotation.value());
						} else {
							beanDefinition.setScope("singleton");
						}
						beanDefinitionMap.put(beanName, beanDefinition);
						
					}
				} catch (ClassNotFoundException e) {
				
					e.printStackTrace();
				}

			}
		}
}
	
	public Object getBean(String beanName) {
        if (singletonObjects.containsKey(beanName)) {
            return singletonObjects.get(beanName);
        } else {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            return createBean(beanDefinition);
        }
	}
	
}
