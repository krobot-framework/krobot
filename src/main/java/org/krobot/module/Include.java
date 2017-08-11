package org.krobot.module;

import org.krobot.KrobotModule;
import org.krobot.command.CommandFilter;
import org.krobot.command.runtime.ICommandHandler;

public @interface Include
{
    Class<? extends KrobotModule>[] imports() default {};
    Class<? extends CommandFilter>[] filters() default {};
    Class<? extends ICommandHandler>[] commands() default {};
}
