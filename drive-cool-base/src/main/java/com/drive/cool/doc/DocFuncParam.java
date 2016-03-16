/**
 * kevin 2015年9月11日
 */
package com.drive.cool.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kevin
 *
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DocFuncParam {
	/**
	 * 参数名
	 * @return
	 */
	String name();
	/**
	 * 参数中文名
	 * @return
	 */
	String display();
	/**
	 * 参数类型，输入或者输出
	 * @return
	 */
	ParamType type() default ParamType.PARAM_IN;
	/**
	 * 参数描述
	 * @return
	 */
	String comment() default "";
	/**
	 * 参数对应的类，备用
	 * @return
	 */
	Class ref() default Object.class;
}
