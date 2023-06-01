package com.serviceinvoker.reflectionhelper.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceInvokerTest {

    private final ServiceInvoker serviceInvoker = new ServiceInvoker();

    @Test
    void testInvokeServiceWithParameters() throws Exception {
        String responseBody = serviceInvoker.invokeService("userService", "getUser", "John");
        assertEquals("{\"returnValue\":{\"name\":\"John\",\"surname\":\"Doe\",\"age\":16}}", responseBody);
    }

    @Test
    void testInvokeServiceWithParametersAndTypes() throws Exception {
        String responseBody = serviceInvoker.invokeServiceWithTypes("userService", "getUser", new Object[]{"Jane"}, new String[]{"java.lang.String"});
        assertEquals("{\"returnValue\":{\"name\":\"Jane\",\"surname\":\"Doe\",\"age\":18}}", responseBody);
    }
}