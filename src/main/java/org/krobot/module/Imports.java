package org.krobot.module;

import org.krobot.KrobotModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Imports
{
    Class<? extends KrobotModule>[] value();
}
