package com.seam.blog.common.cache;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {

    long expire() default 1 * 60 * 1000;

    String name() default "";
}