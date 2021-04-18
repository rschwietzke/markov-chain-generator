package org.rschwietzke.markov.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.rschwietzke.markov.WordTrainer;

import it.unimi.dsi.util.FastRandom;

public class Planets
{
    @Test
    public void planets() throws URISyntaxException, IOException
    {
        var path = Paths.get(getClass().getClassLoader().getResource("stocks.txt").toURI());
        var lines = Files.lines(path);    

        var trainer = new WordTrainer();
        lines.filter(s -> s.startsWith("#") == false).filter(s -> s.isBlank() == false).map(s -> s.trim()).forEach(trainer::train);

        var table = trainer.getTrainedTable();
        var r = new FastRandom();

        for (int i = 0; i < 20; i++)
        {
            var s = new StringBuilder();

            // ok, let's see what we get and limit the length to avoid infinite loops
            var last = table.randomRow(r).toUpperCase();
            s.append(last);
            while (s.length() < 30)
            {
                var result = table.randomCol(r, last);
                if (result.isPresent())
                {
                    if ("".equals(result.get()))
                    {
                        if (s.length() < 3)
                        {
                            continue;
                        }
                        else
                        {
                            // end reached
                            break;
                        }
                    }

                    // one more
                    s.append(result.get());
                    last = result.get();
                }
                else
                {
                    // ok, the last one was empty, hence get us any 
                    System.out.print("Miss: " + last);
                    last = table.randomRow(r).toLowerCase();
                    s.append(last);
                    System.out.println(" draw: " + last);
                }
            }
            System.out.println(s.toString());

        }
    }
}
