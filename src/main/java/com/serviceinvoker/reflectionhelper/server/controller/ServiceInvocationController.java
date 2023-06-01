package com.serviceinvoker.reflectionhelper.server.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceInvocationController {
    private final ReflectionHelper reflectionHelper;

    @PostMapping("/internal/invoke")
    public Response processRequest(@RequestBody Request request) throws Exception {
        Object result = reflectionHelper.callMethod(
                request.getBeanName(),
                request.getMethodName(),
                request.getMethodParams(),
                request.getParamTypes());

        Response response = new Response(result);
        return response;
    }
}

@Component
@RequiredArgsConstructor
class ReflectionHelper {

    private final ApplicationContext applicationContext;
    ObjectMapper mapper = new ObjectMapper();

    public Object callMethod(String beanName, String methodName, Object[] methodParams, Class<?>[] paramTypes) throws Exception {
        Object bean = applicationContext.getBean(beanName);
        Method method = paramTypes == null
                ? getMethodWithParameters(bean.getClass(), methodName, methodParams)
                : getMethodWithParameterTypes(bean.getClass(), methodName, paramTypes);

        return method.invoke(bean, getCastedObjs(methodParams, method.getParameterTypes(), mapper));
    }

    private static Method getMethodWithParameterTypes(Class<?> beanClass, String methodName, Class<?>[] paramTypes) throws InvocationException {
        var providedParamTypeNames = getParameterTypeNames(paramTypes);
        var method = Arrays.stream(beanClass.getDeclaredMethods())
                .filter(m -> {
                    var providedPrmCount = paramTypes == null ? 0 : paramTypes.length;
                    var nameCheck = m.getName().equals(methodName);
                    var countCheck = m.getParameterCount() == providedPrmCount;
                    var typeCheck = getParameterTypeNames(m.getParameterTypes()).equals(providedParamTypeNames);
                    return nameCheck && countCheck && typeCheck;
                })
                .findFirst()
                .orElseThrow(() -> new InvocationException("Method not found with the given parameters and types"));

        method.setAccessible(true);
        return method;
    }

    private static Method getMethodWithParameters(Class<?> beanClass, String methodName, Object[] methodParams) throws InvocationException {
        var providedPrmCount = methodParams == null ? 0 : methodParams.length;
        var methods = Arrays.stream(beanClass.getDeclaredMethods())
                .filter(m -> (m.getName().equals(methodName) && m.getParameterCount() == providedPrmCount)).toList();

        if (methods.size() == 0) {
            throw new InvocationException("Method with the provided name not found");
        }

        if (methods.size() > 1) {
            throw new InvocationException("There are more than one method with the given parameters, parameter types need to be provided to invoke for this method");
        } else {
            Method method = methods.get(0);
            method.setAccessible(true);
            return method;
        }
    }

    private static List<String> getParameterTypeNames(Class<?>[] paramTypes) {
        return Arrays.stream(paramTypes).map(Class::getName).toList();
    }

    private static Object[] getCastedObjs(Object[] methodParams, Class<?>[] paramTypes, ObjectMapper mapper) throws JsonProcessingException, ClassNotFoundException {
        ArrayList<Object> castedObjs = new ArrayList<>();

        for (int i = 0; i < methodParams.length; i++) {
            Class clazz = paramTypes[i];
            if (clazz.isEnum()) {
                castedObjs.add(Enum.valueOf(clazz, (String) methodParams[i]));
            } else if (clazz.isPrimitive()) {
                castedObjs.add(methodParams[i]);
            } else if (isJsonValid(methodParams[i], mapper)) {


                castedObjs.add(mapper.readValue(methodParams[i].toString(), clazz));
            } else {
                Object castedObj = paramTypes[i].cast(methodParams[i]);
                castedObjs.add(castedObj);
            }
        }
        return castedObjs.toArray();
    }

    private static boolean isJsonValid(Object json, ObjectMapper mapper) {
        try {
            mapper.readTree(json.toString());
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }
}

@RequiredArgsConstructor
class InvocationException extends Exception{
    private final String message;
}

@Data
class Request {
    String beanName;
    String methodName;
    Object[] methodParams;
    Class<?>[] paramTypes;
}

@Data
@RequiredArgsConstructor
class Response {
    private final Object returnValue;
}


