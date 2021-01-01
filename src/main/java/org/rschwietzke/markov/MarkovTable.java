package org.rschwietzke.markov;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * @author rschwietzke
 *
 */
public class MarkovTable<T, S>
{
    public final Map<T, Columns<T, S>> rows = new HashMap<>();

    // counts the total row usage
    public long count = 0;

    public MarkovTable<T, S> train(final T t, final S s)
    {
        count++;
        
        rows.compute(t, (k, v) -> 
        {
            if (v == null)
            {
                v = new Columns<T, S>(k);
            }
            v.record(s);

            return v;
        });
        
        return this;
    }
    
    /**
     * Calculates intermediate probabilities for testing
     * 
     * @return the instance of this table with set or refreshed probabilities
     */
    public MarkovTable<T, S> finish()
    {
        final double total = count;
        
        rows.forEach((k, v) -> 
        {
            v.probability = v.count / total;
            final double colCount = v.count;
            
            v.columns.forEach((s, e) -> 
            {
                e.probability = e.count / colCount;
            });
        });
        
        return this;
    }

    /**
     * Freezes the table and returns a fully usable and speedy version of this table
     */
    public TrainedMarkovTable<T, S> freeze()
    {
        return new TrainedMarkovTable<T, S>(this);
    }
    
    public Optional<Entry<S>> getEntry(final T t, final S s)
    {
        return getRow(t).map(c -> c.entry(s));
    }
    
    public Optional<Columns<T, S>> getRow(final T t)
    {
        return Optional.ofNullable(rows.get(t));
    }

    public static class Columns<T, S>
    {
        public T type;

        // counts how often each column is used
        public long count = 0;

        public double probability = 0;

        public Map<S, Entry<S>> columns = new HashMap<>();

        public Columns(final T type)
        {
            this.type = type;
        }

        public void record(final S s)
        {
            // increase counter for row
            count++;

            // get column and store data
            columns.compute(s, (k, v) ->
            {
                if (v == null)
                {
                    v = new Entry<S>(k);
                }   
                v.record();

                return v;
            });
        }

        public Entry<S> entry(final S s)
        {
            return columns.get(s);
        }
    }

    public static class Entry<S>
    {
        public S type;
        public double probability = 0;

        // counts how often that entry in the row is used
        public long count = 0;

        public Entry(final S type)
        {
            this.type = type;
        }

        public void record()
        {
            count++;
        }
    }
}
