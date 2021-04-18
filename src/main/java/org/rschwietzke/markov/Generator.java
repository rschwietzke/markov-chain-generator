package org.rschwietzke.markov;

public interface Generator<R>
{
    /**
     * Creates a generator that is not longer trainable and returns it
     * 
     * @return returns generated data
     */
    public R generate();
    
}
