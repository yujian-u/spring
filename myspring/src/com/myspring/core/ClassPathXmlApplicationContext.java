package com.myspring.core;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
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

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext cpxa = new ClassPathXmlApplicationContext();
        Map<String,EntityBean> beans = cpxa.springXmlParser();
        for(Map.Entry<String,EntityBean> entry:beans.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            System.out.println("***********************************");
        }
    }

    //2-根据解析来的信息，通过反射返程对象的组装assemble


    @Override
    public Object getBean(String beanId) {
        return null;
    }
}
