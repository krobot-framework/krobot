package org.krobot.runtime;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import org.apache.commons.lang3.time.DurationFormatUtils;


import static org.fusesource.jansi.Ansi.*;

public class StateBar extends Thread
{
    private KrobotRuntime runtime;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat uptimeFormat = new SimpleDateFormat("dd'd' HH'h' mm'm' ss's'");

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

        // Date
        result += "@|bold (" + timeFormat.format(new Date()) + ") |@";

        // "Running"
        result += "@|bold,green Running >> |@";

        // Uptime
        result += "@|bold Uptime : |@@|bold,blue " + DurationFormatUtils.formatDuration(runtime.getUptime(), "dd'd' HH'h' mm'm' ss's'") + "|@ @|bold | |@";

        // Ping
        long ping = runtime.jda().getPing();
        result += "@|bold Ping : |@@|bold," + colored(ping, 175, 450) + " " + runtime.jda().getPing() + "|@@|bold ms | |@";

        // Threads
        int active = runtime.getThreadPool().getActiveCount();
        int max = runtime.getThreadPool().getMaximumPoolSize();
        result += "@|bold," + colored(active, max / 3,max - max / 4) + " " + active + "|@@|bold /" + max + " execution threads |@";

        // Execution time
        long lastExec = runtime.getLastExecutionTime();
        result += "@|bold | last execution time : |@@|bold," + colored(lastExec, 300, 1000) + " " + lastExec + "|@@|bold ms|@ ";

        // Result !
        System.out.print(ansi().render(result).reset() + "\r");
    }

    protected String colored(long value, int low, int high)
    {
        String color = "green";

        if (value >= low)
        {
            color = "yellow";
        }
        if (value >= high)
        {
            color = "red";
        }

        return color;
    }
}
