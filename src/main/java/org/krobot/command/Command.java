package org.krobot.command;

public @interface Command
{
    String path();
    Class<? extends ICommandHandler> handler();

    String desc() default "";
}
