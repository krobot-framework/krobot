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
