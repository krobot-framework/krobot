/*
 * Copyright 2017 Adrien "Litarvan" Navratil
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
package fr.litarvan.krobot.config;

import java.io.File;

/**
 * The File Config<br/><br/>
 *
 *
 * A config that can be loaded/saved from/to a file.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class FileConfig implements Config
{
    /**
     * If it should save automatically when defining a value
     */
    protected boolean autoSave = true;

    /**
     * The config file
     */
    protected File file;

    /**
     * Empty config, no file set, can't save until set.
     */
    public FileConfig()
    {
    }

    /**
     * Config from a file, if it exists, config will be loaded from it.
     *
     * @param file The file of the config
     */
    public FileConfig(File file)
    {
        this.in(file);

        if (file != null && file.exists())
        {
            this.load();
        }
    }

    /**
     * @return The file of the config
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Define the file of the config
     *
     * @param file The config file
     *
     * @return This
     */
    public FileConfig in(File file)
    {
        this.file = file;
        return this;
    }

    /**
     * Enable or disable automatic saving when setting a value
     *
     * @param autoSave Enable/disable the auto save
     *
     * @return This
     */
    public FileConfig autoSave(boolean autoSave)
    {
        this.autoSave = autoSave;
        return this;
    }

    /**
     * @return If it automatically saves when setting a value
     */
    public boolean isAutoSaveEnabled()
    {
        return autoSave;
    }

    /**
     * @return If saving is supported
     */
    @Override
    public boolean isSavingSupported()
    {
        return true;
    }

    /**
     * Load the config from the file.<br/>
     * Will probably throw an exception if no file is set.
     *
     * @return This
     */
    public abstract FileConfig load();

    /**
     * Save the config from the file.<br/>
     * Will probably throw an exception if no file is set or
     * saving is not supported.
     *
     * @return This
     */
    public abstract FileConfig save();
}
