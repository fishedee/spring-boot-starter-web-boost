package com.fishedee.web_boost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fishedee.reflection_boost.GenericActualArgumentExtractor;
import com.fishedee.reflection_boost.GenericFormalArgumentFiller;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Native;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.*;

//参考这里
//https://blog.csdn.net/WoddenFish/article/details/82593317
/**
 * Created by fish on 2021/4/29.
 */
@Slf4j
public class WebBoostRequestResolver implements HandlerMethodArgumentResolver {

    final static String JSONBODY_ATTRIBUTE = "com.fishedee.web_boost.jsonbody";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if( hasRequestParamAnnotation(parameter) ||
                isServletRequest(parameter) ){
            return false;
        }
        return true;
    }

    private boolean isServletRequest(MethodParameter parameter){
        return parameter.getParameterType() == HttpServletRequest.class ||
                parameter.getParameterType() == ServletRequest.class ||
                parameter.getParameterType() == HttpServletResponse.class;
    }
    private boolean hasRequestParamAnnotation(MethodParameter parameter){
        return parameter.hasParameterAnnotation(RequestParam.class) == true;
    }

    private Type extractPrameterType(NativeWebRequest webRequest,MethodParameter parameter){
        Type genericParameterType = parameter.getGenericParameterType();
        //普通参数
        if( genericParameterType instanceof Class) {
            return genericParameterType;
        }

        //泛型模板参数
        Class genericClazz = parameter.getDeclaringClass();
        Class beanClazz = (Class)webRequest.getAttribute(PreSaveRequestBeanInteceptor.PRESAVE_BEAN_CLASS,0);
        GenericActualArgumentExtractor extractor = new GenericActualArgumentExtractor(beanClazz,genericClazz);
        GenericFormalArgumentFiller filler = new GenericFormalArgumentFiller(extractor);
        return filler.fillType(genericParameterType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String jsonString = this.getRequestBody(webRequest);
        Type genericParameterType = this.extractPrameterType(webRequest,parameter);
        Class valueType = this.getParameterType(genericParameterType);
        //json反序列化
        Object result = null;
        if( jsonString.isEmpty() ){
            jsonString = "{}";
        }
        boolean isBasicValueType = this.basicTypeSet.contains(valueType);
        if( isBasicValueType ){
            //基础类型
            String key = alwaysGetParameterKey(parameter);
            result = this.getFromKeyBasicType(jsonString,key,valueType);
        }else{
            //复合类型
            if( collectionTypeSet.contains(valueType)){
                //集合类型
                String key = alwaysGetParameterKey(parameter);
                result = this.getFromKey(jsonString,key,genericParameterType);
            }else{
                //非集合类型
                String key = getAnnotationKey(parameter);
                if( Strings.isNotBlank(key)){
                    result = this.getFromKey(jsonString,key,genericParameterType);
                }else{
                    result = this.getFromWhole(jsonString,genericParameterType);
                }
            }
        }
        //兜底赋默认值
        if( result == null ){
            result = newDefaultTypeValue(valueType);
            this.check(result);
        }
        return result;
    }

    @Autowired
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    public void check(Object target){
        Set<ConstraintViolation<Object>> validateSet = localValidatorFactoryBean.validate(target);
        if (!CollectionUtils.isEmpty(validateSet)) {
            Iterator<ConstraintViolation<Object>> iterator = validateSet.iterator();
            List<String> msgList = new ArrayList<>();
            while (iterator.hasNext()) {
                ConstraintViolation<?> cvl = iterator.next();
                msgList.add(cvl.getPropertyPath()+":"+cvl.getMessage());
            }
            throw new WebBoostException(1,msgList.toString(),null);

        }
    }

    private Object getFromWhole(String jsonString,Type valueType){
        try {
            Object result = objectMapper.readValue(jsonString, objectMapper.getTypeFactory().constructType(valueType));
            this.check(result);
            return result;
        }catch(IOException e){
            throw new WebBoostException(1,"格式错误:"+e.getMessage(),null);
        }
    }

    private Object getFromKey(String jsonString,String key,Type valueType){
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            Iterator<Map.Entry<String, JsonNode>> elements = root.fields();
            while (elements.hasNext()) {
                Map.Entry<String, JsonNode> node = elements.next();
                String nodeKey = node.getKey();
                if (nodeKey.equals(key)) {
                    Object result = objectMapper.readValue(node.getValue().toString(), objectMapper.getTypeFactory().constructType(valueType));
                    this.check(result);
                    return result;
                }
            }
            return null;
        }catch(IOException e){
            throw new WebBoostException(1,"格式错误:"+e.getMessage(),null);
        }
    }

    private Object getFromKeyBasicType(String jsonString,String key,Class valueType){
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            Iterator<Map.Entry<String, JsonNode>> elements = root.fields();
            while (elements.hasNext()) {
                Map.Entry<String, JsonNode> node = elements.next();
                String nodeKey = node.getKey();
                if (nodeKey.equals(key)) {
                    Object result = readBasicType(valueType, node.getValue());
                    this.check(result);
                    return result;
                }
            }
            return null;
        }catch(IOException e){
            throw new WebBoostException(1,"格式错误:"+e.getMessage(),null);
        }
    }
    private Object newDefaultTypeValue(Class clazz)throws Exception {
        if (clazz.equals(int.class) ||
            clazz.equals(Integer.class)){
            return 0;
        }else if( clazz.equals(long.class)||
            clazz.equals(Long.class)){
            return (long)0;
        }else if ( clazz.equals(short.class)||
            clazz.equals(Short.class)){
            return (short)0;
        }else if ( clazz.equals(float.class)||
            clazz.equals(Float.class)){
            return (float)0;
        }else if( clazz.equals(double.class) ||
            clazz.equals(Double.class)){
            return (double)0;
        }else if( clazz.equals(boolean.class)||
            clazz.equals(Boolean.class)){
            return false;
        }else if( clazz.equals(List.class)){
            return new ArrayList<>();
        }else if( clazz.equals(Set.class)){
            return new HashSet<>();
        }else if( clazz.equals(Map.class)){
            return new HashMap();
        }else{
            return clazz.newInstance();
        }
    }

    private Object readBasicType(Class clazz, JsonNode node){
        if( clazz.equals(String.class)){
            return node.asText();
        }else if (clazz.equals(Integer.class)||
            clazz.equals(int.class)){
            return node.asInt();
        }else if( clazz.equals(Long.class)||
            clazz.equals(long.class)){
            return node.asLong();
        }else if ( clazz.equals(Short.class)||
            clazz.equals(short.class)){
            return (short)node.asInt();
        }else if ( clazz.equals(Float.class)||
            clazz.equals(float.class)){
            return (float)node.asDouble();
        }else if( clazz.equals(Double.class)||
            clazz.equals(double.class)){
            return node.asDouble();
        }else if( clazz.equals(Boolean.class)||
            clazz.equals(boolean.class)){
            return node.asBoolean();
        }else if( clazz.equals(BigDecimal.class)){
            return node.decimalValue();
        }else{
            throw new WebBoostException(1,"未知的BasicType:"+clazz,null);
        }
    }

    private Set<Class> basicTypeSet= new HashSet<>();

    private Set<Class> collectionTypeSet = new HashSet<>();

    private String getAnnotationKey(MethodParameter parameter){
        RequestJson parameterAnnotation = parameter.getParameterAnnotation(RequestJson.class);
        return parameterAnnotation != null? parameterAnnotation.value():"";
    }

    private String alwaysGetParameterKey(MethodParameter parameter){
        RequestJson parameterAnnotation = parameter.getParameterAnnotation(RequestJson.class);
        String key = parameterAnnotation != null? parameterAnnotation.value():"";
        if(!StringUtils.isEmpty(key) ){
            return key;
        }

        return parameter.getParameterName();
    }

    @PostConstruct
    private void init(){
        basicTypeSet.add(BigDecimal.class);
        basicTypeSet.add(String.class);
        basicTypeSet.add(Integer.class);
        basicTypeSet.add(int.class);
        basicTypeSet.add(Long.class);
        basicTypeSet.add(long.class);
        basicTypeSet.add(Short.class);
        basicTypeSet.add(short.class);
        basicTypeSet.add(Float.class);
        basicTypeSet.add(float.class);
        basicTypeSet.add(Double.class);
        basicTypeSet.add(double.class);
        basicTypeSet.add(Boolean.class);
        basicTypeSet.add(boolean.class);

        collectionTypeSet.add(List.class);
        collectionTypeSet.add(LinkedList.class);
        collectionTypeSet.add(ArrayList.class);
        collectionTypeSet.add(Collection.class);
        collectionTypeSet.add(Set.class);
        collectionTypeSet.add(HashSet.class);
        collectionTypeSet.add(TreeSet.class);
        collectionTypeSet.add(Map.class);
        collectionTypeSet.add(HashMap.class);
        collectionTypeSet.add(TreeMap.class);
    }

    private Class getParameterType(Type type) {
        if( type instanceof Class){
            return (Class)type;
        }else{
            ParameterizedType t = (ParameterizedType)type;
            return (Class)t.getRawType();
        }
    }

    private String getRealRequestJson(NativeWebRequest webRequest){
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if( servletRequest.getMethod().toLowerCase().equals("get")){
            //get请求固定从data字段获取
            String json = servletRequest.getParameter("data");
            return json == null?"":json;
        }else {
            //post请求固定从body获取
            String contentType = servletRequest.getContentType();
            if (contentType != null&&
                    contentType.contains("application/x-www-form-urlencoded") == true) {
                return "";
            }
            try {
                BufferedReader input = servletRequest.getReader();
                StringBuilder stringBuilder = new StringBuilder();
                char[] b = new char[4096];
                for (int n = 0; (n = input.read(b)) != -1; ) {
                    stringBuilder.append(b,0,n);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private String getRequestBody(NativeWebRequest webRequest) {
        String jsonBody = (String) webRequest.getAttribute(JSONBODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        if (jsonBody == null) {
            jsonBody = getRealRequestJson(webRequest);
            webRequest.setAttribute(JSONBODY_ATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
        }
        return jsonBody;
    }
}