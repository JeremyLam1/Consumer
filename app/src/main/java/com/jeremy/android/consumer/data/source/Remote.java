package com.jeremy.android.consumer.data.source;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Jeremy on 2017/1/25.
 */

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {
    
}
