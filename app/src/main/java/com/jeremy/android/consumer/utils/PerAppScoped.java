package com.jeremy.android.consumer.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jeremy on 2017/3/1.
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface PerAppScoped {
}
