/*
 * Copyright 2017 The Krobot Contributors
 *
 * This file is part of Krobot.
 *
 * Krobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krobot.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    public void errorBold(Color color, Object text, Throwable t)
    {
        logger.error(ansi().bold().fg(color).a(text).reset().toString(), t);
    }

    public void errorAuto(String text, Object... format)
    {
        logger.error(ansi().render(text).reset().toString(), format);
    }

    public void errorAuto(String text, Throwable t)
    {
        logger.error(ansi().render(text).reset().toString(), t);
    }

    public void errorAuto(Color color, String text, Object... format)
    {
        logger.error(ansi().fg(color).render(text).reset().toString(), format);
    }

    public void errorAuto(Color color, String text, Throwable t)
    {
        logger.error(ansi().fg(color).render(text).reset().toString(), t);
    }
}
