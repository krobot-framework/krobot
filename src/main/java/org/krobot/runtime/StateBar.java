package org.krobot.runtime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StateBar extends Thread
{
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

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

        result += "(" + timeFormat.format(new Date()) + ")";

        System.out.print(result + "\r");
    }
}
