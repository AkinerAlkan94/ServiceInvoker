# Service Invoker

## Overview
This project consists of both server and client components that facilitate the invocation of methods located in remote microservices without the need to handle data transfer objects. It provides a convenient way to access and control methods in a distributed system.

## Server Side
The server side of this project provides an invocation endpoint that allows you to invoke any method declared in the server-side beans. This endpoint utilizes reflection-based calls to execute the specified methods. By opening this endpoint, you can access and trigger the desired functionality within the server.

## Client Side
The client side of this project includes the Service Invoker REST Client, which enables you to call methods located on the server side via REST calls. This client simplifies the process of interacting with remote microservices by providing a straightforward interface for invoking methods.

### Usage
It is important to note that this implementation can be considered an anti-pattern, as it may lead to a messy codebase and make the project difficult to maintain. However, in certain scenarios, it can serve as a temporary workaround, granting you control over other microservices without the need to implement different endpoints for each case.

### Security Considerations
Since the server endpoint allows the invocation of any method, it is essential to address potential security vulnerabilities. It is highly recommended to implement proper authorization mechanisms on top of this endpoint. By enforcing authorization, you can ensure that only authorized users or systems can access and execute methods through the server-side endpoint, mitigating the risk of unauthorized access and potential misuse.

Please exercise caution when using this implementation and carefully evaluate its suitability for your specific use case.
