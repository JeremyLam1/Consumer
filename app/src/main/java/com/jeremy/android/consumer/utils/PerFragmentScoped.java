package com.jeremy.android.consumer.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jeremy on 2017/1/25.
 */

@Scope
@Documented
@Retention(RUNTIME)
public @interface PerFragmentScoped {
}
