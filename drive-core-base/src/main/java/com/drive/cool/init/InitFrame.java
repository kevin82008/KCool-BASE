package com.drive.cool.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 初始化框架
 * @author kevin
 *
 */
public class InitFrame{
	private final static String CONTEXT_PATH = "classpath*:spring/application-base-beans.xml";
	private static ApplicationContext ctx = null;
	
	public static void init(){
		ctx = new ClassPathXmlApplicationContext(CONTEXT_PATH);
		Map<String, FrameInitedListener> beans = ctx.getBeansOfType(FrameInitedListener.class);
		List<Map.Entry<String,FrameInitedListener>> list = new ArrayList<Map.Entry<String,FrameInitedListener>>(beans.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,FrameInitedListener>>() {
			@Override
			public int compare(Entry<String, FrameInitedListener> o1,
					Entry<String, FrameInitedListener> o2) {
				return o1.getValue().getOrder() - o2.getValue().getOrder();
			}

        });

        for(Map.Entry<String,FrameInitedListener> mapping:list){ 
        	beans.get(mapping.getKey()).init();   
        } 
	}
	
	public static void main(String[] args) {
		init();
	}
}
