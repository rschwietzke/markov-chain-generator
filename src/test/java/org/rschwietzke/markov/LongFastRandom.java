package org.rschwietzke.markov;

import it.unimi.dsi.util.FastRandom;

public class LongFastRandom extends FastRandom
{
    private final long value;
    
    public LongFastRandom(final long value)
    {
        this.value = value;
    }
    
    /**
     * Just for testing!!!
     */
    public long nextLong(long l)
    {
        return value;
    }
    
    public static LongFastRandom get(final long value)
    {
        return new LongFastRandom(value);
    }
}
