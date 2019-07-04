package com.liugeng.bigdata.spider.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanUtils implements ApplicationContextAware {

	private static final ApplicationContextHolder contextHolder = new ApplicationContextHolder();

	public static Object getBean(String name, Object... obj) {
		checkContext();
		return contextHolder.context.getBean(name, obj);
	}
	
	public static <T> T getBean(String name, Class<T> clazz) {
		checkContext();
		return contextHolder.context.getBean(name, clazz);
	}

	public static <T> T getBean(Class<T> clazz, Object... obj) {
		checkContext();
		return contextHolder.context.getBean(clazz, obj);
	}

	public static void initializeBean(Object obj) {
		checkContext();
		contextHolder.context.getAutowireCapableBeanFactory().autowireBean(obj);
	}

	public static String getActiveProfile() {
		checkContext();
		return contextHolder.context.getEnvironment().getActiveProfiles()[0];
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		contextHolder.context = applicationContext;
	}

	private static void checkContext() {
		if (contextHolder.context == null) {
			throw new RuntimeException("Spring context not initialized!");
		}
	}

	private static class ApplicationContextHolder {
		private ApplicationContext context;
	}
}