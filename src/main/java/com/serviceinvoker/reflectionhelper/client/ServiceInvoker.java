package com.serviceinvoker.reflectionhelper.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceInvoker {
    RestTemplate restTemplate = new RestTemplate();
    String serviceUrl = "http://localhost:8080/internal/invoke";

    public String invokeService(String beanName, String methodName, Object... methodParameters) {
        return sendRequest(serviceUrl, new Request(beanName, methodName, methodParameters, null));
    }

    public String invokeServiceWithTypes(String beanName, String methodName, Object[] methodParameters, Object[] paramTypes) {
        return sendRequest(serviceUrl, new Request(beanName, methodName, methodParameters, paramTypes));
    }

    private String sendRequest(String url, Request request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Request> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Request {
    String beanName;
    String methodName;
    Object[] methodParams;
    Object[] paramTypes;
}
