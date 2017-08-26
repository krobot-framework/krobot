package org.krobot.runtime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StateBar extends Thread
{
    private KrobotRuntime runtime;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public StateBar(KrobotRuntime runtime)
    {
        this.runtime = runtime;
    }

    @Override
    public void run()
    {
        while (!this.isInterrupted())
        {
            try
            {
                sleep(1000L);
            }
            catch (InterruptedException ignored)
            {
            }

            update();
        }
    }

    public void update()
    {
        String result = "";

        result += "(" + timeFormat.format(new Date()) + ") ";
        result += "Running >> ";
        result += "Ping : " + runtime.jda().getPing() + "ms | ";
        result += runtime.getThreadPool().getActiveCount() + "/" + runtime.getThreadPool().getMaximumPoolSize() + " execution threads ";
        result += "| last execution time : " + runtime.getLastExecutionTime() + "ms ";

        System.out.print(result + "\r");
    }
}
