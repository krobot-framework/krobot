package org.krobot.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.krobot.KrobotModule;
import org.krobot.command.CommandFilter;
import org.krobot.command.ICommandHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Include
{
    Class<? extends KrobotModule>[] imports() default {};
    Class<? extends CommandFilter>[] filters() default {};
    Class<? extends ICommandHandler>[] commands() default {};
}
