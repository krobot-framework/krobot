package org.krobot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static org.fusesource.jansi.Ansi.*;

public class ColoredLogger
{
    private Logger logger;

    public ColoredLogger(Logger logger)
    {
        this.logger = logger;
    }

    public static ColoredLogger getLogger(String name)
    {
        return new ColoredLogger(LogManager.getLogger(name));
    }

    public void info(Object text, Object... format)
    {
        logger.info(String.valueOf(text), format);
    }

    public void infoBold(Object text, Object... format)
    {
        logger.info(ansi().bold().a(text).reset().toString(), format);
    }

    public void info(Color color, Object text, Object... format)
    {
        logger.info(ansi().fg(color).a(text).reset().toString(), format);
    }

    public void infoBold(Color color, Object text, Object... format)
    {
        logger.info(ansi().bold().fg(color).a(text).reset().toString(), format);
    }

    public void infoAuto(String text, Object... format)
    {
        logger.info(ansi().render(text).reset().toString(), format);
    }

    public void infoAuto(Color color, String text, Object... format)
    {
        logger.info(ansi().fg(color).render(text).reset().toString(), format);
    }

    public void warn(Object text, Object... format)
    {
        logger.warn(String.valueOf(text), format);
    }

    public void warnBold(Object text, Object... format)
    {
        logger.warn(ansi().bold().a(text).reset().toString(), format);
    }

    public void warn(Color color, Object text, Object... format)
    {
        logger.warn(ansi().fg(color).a(text).reset().toString(), format);
    }

    public void warnBold(Color color, Object text, Object... format)
    {
        logger.warn(ansi().bold().fg(color).a(text).reset().toString(), format);
    }

    public void warnAuto(String text, Object... format)
    {
        logger.warn(ansi().render(text).reset().toString(), format);
    }

    public void warnAuto(Color color, String text, Object... format)
    {
        logger.warn(ansi().fg(color).render(text).reset().toString(), format);
    }

    public void error(Object text, Object... format)
    {
        logger.error(String.valueOf(text), format);
    }

    public void error(Object text, Throwable error)
    {
        logger.error(text, error);
    }

    public void error(Object text, Throwable error, Object... format)
    {
        logger.error(String.valueOf(text), error, format);
    }

    public void errorBold(Object text, Object... format)
    {
        logger.error(ansi().bold().a(text).reset().toString(), format);
    }

    public void error(Color color, Object text, Object... format)
    {
        logger.error(ansi().fg(color).a(text).reset().toString(), format);
    }

    public void errorBold(Color color, Object text, Object... format)
    {
        logger.error(ansi().bold().fg(color).a(text).reset().toString(), format);
    }

    public void errorAuto(String text, Object... format)
    {
        logger.error(ansi().render(text).reset().toString(), format);
    }

    public void errorAuto(Color color, String text, Object... format)
    {
        logger.error(ansi().fg(color).render(text).reset().toString(), format);
    }
}
