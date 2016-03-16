/**
 * kevin 2015年8月27日
 */
package com.drive.cool.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

/**
 * @author kevin
 *
 */
public class BeanFactory implements ApplicationContextAware, Ordered {

	private static ApplicationContext ctx;
	
	public static <T> T getBean(String beanId, Class<T> clazz){
		return ctx.getBean(beanId, clazz);
	}
	
	public static Object getBean(String beanId){
		return ctx.getBean(beanId);
	}
	
	public static <T> Map<String, T> getBeansOfType(Class<T> clazz){
		return ctx.getBeansOfType(clazz);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		return 1;
	}
	
}
