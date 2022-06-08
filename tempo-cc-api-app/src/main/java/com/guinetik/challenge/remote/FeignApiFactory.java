package com.guinetik.challenge.remote;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class responsible for creating and firing Feign Api requests.
 * @param <T> Uses generics to infer which Api to use in Feign.
 */
public class FeignApiFactory<T extends RemoteApi> {

    final RemoteApiConfig apiConfig;
    final FeignClientConfig feign;
    private final Class<T> feignTargetClass;

    public FeignApiFactory(Class<T> feignTargetClass, RemoteApiConfig apiConfig, FeignClientConfig feign) {
        this.apiConfig = apiConfig;
        this.feign = feign;
        this.feignTargetClass = feignTargetClass;
    }

    /**
     * Creates a new Feign client based on the FeignClientConfig class
     * @param name - name of the request for logging purposes
     * @return An instance of Feign client for the interface provided on generics
     */
    private T create(String name) {
        return feign.createDefault(name).target(this.feignTargetClass, apiConfig.baseUrl);
    }

    /**
     * Uses reflection to execute feign client methods that consume the remote API in a generic way, but keeping it type-safe.
     * @param methodName - which method to execute
     * @param args - method arguments
     * @return The original method's return type, parameterized with <E>
     */
    public <E> E execute(String methodName, Object... args) {
        try {
            T api = this.create(methodName);
            Class c = api.getClass();
            Method method = null;
            Method[] methods = c.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method m = methods[i];
                if (methodName.equalsIgnoreCase(m.getName())) method = m;
            }
            if (method != null) {
                return (E) method.invoke(api, args);
            } else {
                throw new RemoteApiException("Method doesn't exist");
            }
        } catch (IllegalAccessException e) {
            throw new RemoteApiException(e.getClass().getName() + ": " + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RemoteApiException(e.getTargetException().getClass().getName() + ": " + e.getTargetException().getMessage());
        }
    }
}
