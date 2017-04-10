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

public abstract class FileConfig implements Config
{
    protected boolean autoSave;
    protected File file;

    public FileConfig()
    {
    }

    public FileConfig(File file)
    {
        this.in(file);

        if (file.exists())
        {
            this.load();
        }
    }

    public File getFile()
    {
        return file;
    }

    public FileConfig in(File file)
    {
        this.file = file;
        return this;
    }

    public FileConfig autoSave(boolean autoSave)
    {
        this.autoSave = autoSave;
        return this;
    }

    public boolean isAutoSaveEnabled()
    {
        return autoSave;
    }

    public boolean isSavingSupported()
    {
        return true;
    }

    public abstract FileConfig load();
    public abstract FileConfig save();
}
