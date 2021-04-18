package org.rschwietzke.markov;

public interface Trainer<D, R>
{
    /**
     * Adds data to the table. You are free to train in any way you want.
     * 
     * @param data the data to process
     */
    public void train(final D data);
    
    /**
     * Creates a generator that is not longer trainable and returns it
     * 
     * @return returns generated data
     */
    public Generator<R> generator();
}
