/**
 * kevin 2015年9月12日
 */
package com.drive.cool.doc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.drive.cool.rcp.IRcpService;

/**
 * @author kevin
 *
 */
public class DocCreator {
	private static final Log log = LogFactory.getLog(DocCreator.class);
	private static ApplicationContext ctx = null;
	private final static String CONTEXT_PATH = "classpath*:spring/application-base-beans.xml";
	private static final int BASE_LENGTH = "com.drive.cool".length() + 1;
	private StringBuffer linkBuffer = new StringBuffer();
	private StringBuffer contentBuffer = new StringBuffer();
	private StringBuffer headerBuffer = new StringBuffer();
	private StringBuffer footBuffer = new StringBuffer();
	
	private FileOutputStream out;
	private PrintWriter writer;
	
	public static void main(String[] args) {
		DocCreator creator = new DocCreator();
		creator.init();
		creator.createDoc();
		creator.printDoc();
	}
	
	public void init(){
		ctx = new ClassPathXmlApplicationContext(CONTEXT_PATH);
		createBaseInfo();
	}

	/**
	 * 
	 */
	private void createBaseInfo() {
		headerBuffer.append("<!DOCTYPE html>").append("\n");
		headerBuffer.append("<html>").append("\n");
		headerBuffer.append("<meta charset=\"utf-8\">").append("\n");
		headerBuffer.append("<head>").append("\n");
		headerBuffer.append("<title>接口定义</title>").append("\n");
		headerBuffer.append("<style>").append("\n");
		String path = DocCreator.class.getResource(".").getPath();
		File file = new File(path + "DocCss.css");
		BufferedReader br = null;
		InputStream is = null;
		InputStreamReader isr = null; 
		String line;
		if(file.exists()){
			try {
				is = new FileInputStream(file);
				isr = new InputStreamReader(is, "UTF-8");
				br = new BufferedReader(isr);
				while(null != (line = br.readLine())){
					headerBuffer.append(line).append("\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				closeResource(br, is, isr);
			}
		}
		headerBuffer.append("</style>").append("\n");
		headerBuffer.append("</head>").append("\n");
		headerBuffer.append("<body>").append("\n");
		headerBuffer.append("	<h1>接口列表</h1>").append("\n");
		
		footBuffer.append("</body>").append("\n");
		footBuffer.append("</html>").append("\n");
	}

	/**
	 * @param br
	 * @param is
	 * @param isr
	 * @throws IOException
	 */
	private void closeResource(BufferedReader br, InputStream is,
			InputStreamReader isr) {
		if(null != is){
			try {
				is.close();
			} catch (IOException e) {
				log.error("关闭异常", e);
			}
		}
		if(null != isr){
			try {
				isr.close();
			} catch (IOException e) {
				log.error("关闭异常", e);
			}
		}
		if(null != br){
			try {
				br.close();
			} catch (IOException e) {
				log.error("关闭异常", e);
			}
		}
	}
	
	public void printDoc(){
		try {
			String path = this.getClass().getClassLoader().getResource("").getPath();
			out = new FileOutputStream(new File(path + "接口定义.html"));
			writer = new PrintWriter(new OutputStreamWriter(out));
			writer.write(headerBuffer.toString());
			writer.write(linkBuffer.toString());
			writer.write(contentBuffer.toString());
			writer.write(footBuffer.toString());
			writer.flush();
			out.flush();
		} catch (Exception e) {
			log.error("生成文档异常", e);
		} finally {
			closeResource();
		}
		
	}

	/**
	 * 
	 */
	private void closeResource() {
		if(null != out){
			try {
				out.close();
			} catch (IOException e) {
				log.error("关闭异常", e);
			}
		}
		if(null != writer){
			writer.close();
		}
	}

	public void createDoc(){
		linkBuffer.append("<div class=\"link-div\">").append("\n");
		
		contentBuffer.append("<h1>接口明细</h1>").append("\n");
		contentBuffer.append("<div class=\"content-div\">").append("\n");
		Map<String, IRcpService> allService = ctx.getBeansOfType(IRcpService.class);
		for(String beanId : allService.keySet()){
			Object bean = allService.get(beanId);
			//如果不是自动生成的代理类，注册服务
			if(bean.getClass().isAssignableFrom(bean.getClass())){
				createOneClassDoc(bean);
			}
		}
		linkBuffer.append("</div>").append("\n");
		contentBuffer.append("</div>").append("\n");
	}
	
	/**
	 * 创建一个类里的接口文档
	 * @param bean
	 */
	private void createOneClassDoc(Object bean){
		Class claz = AopUtils.getTargetClass(bean);
		if(!Proxy.class.isAssignableFrom(claz)){
			Method[] methods = claz.getDeclaredMethods();
			for(Method method : methods){
				if(Modifier.isPublic(method.getModifiers())){
					createOneFuncDoc(bean, method);
				}
			}
		}
	}
	
	/**
	 * 创建一个方法的接口文档，如果方法无方法名称注解，则不生成内容
	 * @param bean
	 * @param method
	 */
	private void createOneFuncDoc(Object bean, Method method){
		String param = getMethodParam(method);
		Method declareMethod = getMethod(method, bean, param);
		if(declareMethod.isAnnotationPresent(DocFuncDisplay.class)){
			DocFuncDisplay funcDisplay = declareMethod.getAnnotation(DocFuncDisplay.class);
			String func = getFunc(bean, declareMethod, param);
			String display = funcDisplay.display();
			String comment = funcDisplay.comment();
			DocFuncParams params = declareMethod.getAnnotation(DocFuncParams.class);
			addOneFuncDoc(func, display, comment, params);
		}
	}
	
	private void addOneFuncDoc(String func, String display, String comment, DocFuncParams params){
		linkBuffer.append("<div>").append("\n");
		linkBuffer.append("<span>").append(func).append("</span><a href=\"#").append(func).append("\">");
		linkBuffer.append(display).append("</a>\n");
		linkBuffer.append("</div>").append("\n");
		contentBuffer.append("<div id=\"").append(func).append("\">").append("\n");
		contentBuffer.append("<span class=\"func-content\">").append(func).append(" ").append(display).append("</span>\n");
		contentBuffer.append("<span class=\"func-comment\">描述：").append(comment).append("</span>").append("\n");
		if(hasInParam(params)){
			createFuncContent(params, ParamType.PARAM_IN);
		}
		if(hasOutParam(params)){
			createFuncContent(params, ParamType.PARAM_OUT);
		}
		contentBuffer.append("</div>").append("\n");
	}

	private void createFuncContent(DocFuncParams params, ParamType type){
		if(ParamType.PARAM_IN == type){
			contentBuffer.append("<span class=\"param-in\">输入参数</span>");
		}else{
			contentBuffer.append("<span class=\"param-out\">输出参数</span>");
		}
		contentBuffer.append("<div class=\"param-title\"><span class=\"param-id-title\">参数</span><span class=\"param-display-title\">参数名</span><span class=\"param-comment-title\">参数描述</span></div>").append("\n");
		for(DocFuncParam param: params.value()){
			if(param.type() == type){
				contentBuffer.append("<div><span class=\"param-id\">").append(param.name()).append("</span><span class=\"param-display\">").append(param.display()).append("</span><span class=\"param-comment\">").append(param.comment()).append("</span></div>").append("\n");
			}
		}
	}
	/**
	 * 是否有输入参数
	 * @param params
	 * @return
	 */
	private boolean hasInParam(DocFuncParams params){
		boolean result = false;
		for(DocFuncParam param: params.value()){
			if(param.type() == ParamType.PARAM_IN){
				return true;
			}
		}
		return result;
	}
	
	/**
	 * 是否有输出参数
	 * @param params
	 * @return
	 */
	private boolean hasOutParam(DocFuncParams params){
		boolean result = false;
		for(DocFuncParam param: params.value()){
			if(param.type() == ParamType.PARAM_OUT){
				return true;
			}
		}
		return result;
	}
	private String getFunc(Object bean, Method method, String param){
		String methodName = method.getName();
		//去掉最前面的 com.drive.cool 节省点传输资源
		Class declaringClass = getInterface(method, bean, param);
		if(null == declaringClass) return null;
		StringBuffer funcBuffer = new StringBuffer(declaringClass.getName().substring(BASE_LENGTH));
		funcBuffer.append(".").append(methodName);
		funcBuffer.append(param);
		return funcBuffer.toString();
	}
	/**
	 * 根据方法获取参数
	 * @param method
	 * @return
	 */
	private String getMethodParam(Method method){
		Type[] parameterTypes = method.getGenericParameterTypes();
		StringBuffer funcParamBuffer = new StringBuffer();
		String simpleName;
		if(parameterTypes.length > 0){
			for(Type paramType : parameterTypes){
				if(paramType instanceof ParameterizedType){
					simpleName = Class.class.cast(ParameterizedType.class.cast(paramType).getRawType()).getSimpleName();
				}else{
					simpleName = Class.class.cast(paramType).getSimpleName();
				}
				funcParamBuffer.append(".").append(simpleName);
			}
		}
		return funcParamBuffer.toString();
	}
	
	private Method getMethod(Method method, Object bean, String param){
		Class[] allInterfaces = bean.getClass().getInterfaces();
		for(Class currInterface : allInterfaces){
			Method[] allMethod = currInterface.getDeclaredMethods();
			for(Method currMethod : allMethod){
				if(method.getName().equals(currMethod.getName())){
					String currParam = getMethodParam(currMethod);
					if(currParam.equals(param)){
						return currMethod;
					}
				}
			}
		}
		return null;
	}
	/**
	 * 获取定义了该方法的接口
	 * @param method 方法
	 * @param bean 实现类
	 * @return
	 */
	private Class getInterface(Method method, Object bean, String param){
		Class[] allInterfaces = bean.getClass().getInterfaces();
		for(Class currInterface : allInterfaces){
			Method[] allMethod = currInterface.getDeclaredMethods();
			for(Method currMethod : allMethod){
				if(currMethod.equals(method)){
					return currInterface;
				}
			}
		}
		return null;
	}
}
