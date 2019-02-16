package com.github.soyanga.everything.core.interceptor;

import com.github.soyanga.everything.core.model.Thing;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-16 22:24
 * @Version 1.0
 */
@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
