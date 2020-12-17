package com.myspring.core;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassPathXmlApplicationContext implements ApplicationContext {
    private String configFileName;

    public ClassPathXmlApplicationContext(String configFileName) {
        this.configFileName = configFileName;
    }

    public ClassPathXmlApplicationContext() {
        this.configFileName = "applicationContext.xml";
    }

    //1-解析xml文件（dom sax pull）
    public Map<String, EntityBean> springXmlParser() throws Exception {
        //创建解析器
        XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
        //读入xml配置文件
        InputStream in = ClassPathXmlApplicationContext.class.getClassLoader().getResourceAsStream(this.configFileName);
        pullParser.setInput(in, "utf-8");
        //基于事件机制编写xml解析，并且组装目标对象
        int eventType = pullParser.getEventType();
        Map<String, EntityBean> beans = null;
        EntityBean bean = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    beans = new HashMap<String,EntityBean>();
                    break;
                case XmlPullParser.START_TAG:
                    if("bean".equals(pullParser.getName())){
                        bean = new EntityBean();
                        bean.setId(pullParser.getAttributeValue(null,"id"));
                        bean.setClassName(pullParser.getAttributeValue(null,"class"));
                    }
                    if("property".equals(pullParser.getName())){
                       String attrName = pullParser.getAttributeValue(null,"name");
                       String attrVal = pullParser.getAttributeValue(null,"value");
                        bean.getProps().put(attrName,attrVal);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if("bean".equals(pullParser.getName())){
                        beans.put(bean.getId(),bean);
                    }
                    break;
            }
            eventType = pullParser.next();
        }

        return beans;
    }

//    public static void main(String[] args) throws Exception {
//        ClassPathXmlApplicationContext cpxa = new ClassPathXmlApplicationContext();
//        Map<String,EntityBean> beansInfo = cpxa.springXmlParser();
//       Map<String,Object> results = cpxa.getIOC(beansInfo);
//       for(Map.Entry<String,Object> result:results.entrySet()){
//           System.out.println(result.getKey());
//           System.out.println(result.getValue());
//       }
//    }

    //2-根据解析来的信息，通过反射返程对象的组装assemble
    public Map<String,Object> getIOC(Map<String, EntityBean> beansInfo) throws Exception {
        Map<String,Object> results = new HashMap<String,Object>();
        for(Map.Entry<String,EntityBean> beanInfo:beansInfo.entrySet()){
            String resultId = beanInfo.getKey();//生成key值
            EntityBean  bean = beanInfo.getValue();
            String className = bean.getClassName();//class
            Map<String,String> props = bean.getProps();//property的集合
            //反射 -- 输入字符串，返回对象
            Class clazz = Class.forName(className);
            Object obj = clazz.newInstance();
            for(Map.Entry<String,String> prop:props.entrySet()){
                String propName = prop.getKey();
                String propValue = prop.getValue();
                StringBuilder buffer = new StringBuilder("set");
                buffer.append(propName.substring(0,1).toUpperCase());
                buffer.append(propName.substring(1));
                String setterMethodName = buffer.toString();
                Field field = clazz.getDeclaredField(propName);
                Method setMethod = clazz.getDeclaredMethod(setterMethodName,field.getType());
                if("int".equals(field.getType().getName())){
                    setMethod.invoke(obj,Integer.parseInt(propValue));
                }else if("java.lang.String".equals(field.getType().getName())){
                    setMethod.invoke(obj,propValue);
                }
            }
            results.put(resultId,obj);

        }
        return results;
    }
//kkkkkkkkkkkkkkkkkkkk
    @Override
    public Object getBean(String beanId) {
        Object result = null;
        try {
            Map<String,EntityBean> beansInfo = springXmlParser();
            Map<String,Object> results = getIOC(beansInfo);
            result = results.get(beanId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
