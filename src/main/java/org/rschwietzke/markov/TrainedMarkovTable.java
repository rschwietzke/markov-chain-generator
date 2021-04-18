package org.rschwietzke.markov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unimi.dsi.util.FastRandom;

/**
 * This is a ready to use Markov Table aka Chain. It is immutable and fit for 
 * concurrent use if needed. It has sorted rows and cols which can be iterated but 
 * the main purpose is a random access with a random source 
 * 
 * @author rschwietzke
 *
 * @param <T>
 * @param <S>
 */
public class TrainedMarkovTable<T, S>
{
    /**
     * Our data set, for easier testing and extensibility, we keep it open
     */
    public final List<Row<T, S>> rows = new ArrayList<>();
    
    /**
     * Our quick access by T
     */
    public final Map<T, Row<T, S>> quickRows = new HashMap<>();
    
    // the total sum of all rows for later random weighted access
    public long rowMax = 0;
    
    public TrainedMarkovTable(final MarkovTable<T, S> src)
    {
        init(src.finish());
    }
    
    /**
     * Build it up
     */
    private void init(final MarkovTable<T, S> src)
    {
        // transform the maps into lists
        src.rows.forEach((t, s) -> 
        {
            var row = new Row<T, S>(s);
            
            this.rows.add(row);
            quickRows.put(t, row);
        });
        
        // sort it and sum it up
        Collections.sort(this.rows);
        
        long total = 0;
        for (var row : this.rows)
        {
            row.aggregatedSum += total;
            total = row.aggregatedSum;
            
            // keep always the last one
            rowMax = total;
        }
    }

    /**
     * Returns a random T from a row
     */
    public T randomRow(final FastRandom r)
    {
        // ok, we need the sum to know the max
        // we always start with 1 because an entry has at least 1 as sum!
        var value = r.nextLong(rowMax + 1);
        
        for (int i = 0; i < rows.size(); i++)
        {
            var row = rows.get(i);
            if (value <= row.aggregatedSum)
            {
                return row.t;
            }
        }
        
        // never get here
        return rows.get(0).t;
    }

    /**
     * Returns a random col for a t
     */
    public Optional<S> randomCol(final FastRandom r, final T t)
    {
        var row = quickRows.get(t);
        if (row != null)
        {
            var value = r.nextLong(row.colMax + 1);
            
            for (int i = 0; i < row.cols.size(); i++)
            {
                var col = row.cols.get(i);
                if (value <= col.aggregatedSum)
                {
                    return Optional.of(col.s);
                }
            }
        }
        return Optional.empty();
    }
    
    public static class Row<T, S> implements Comparable<Row<T, S>>
    {
        public final T t;
        public final List<Col<S>> cols = new ArrayList<>();
        
        // the total sum of all cols for later random weighted access
        public long aggregatedSum;
        public long count;
        public long colMax;
        
        public Row(final MarkovTable.Columns<T, S> row)
        {
            this.t = row.type;
            this.aggregatedSum = row.count;
            this.count = row.count;
            
            row.columns.forEach((k, v) -> 
            {
                cols.add(new Col<S>(v.type, v.count));
            });
            
            // sort it and sum it up
            Collections.sort(cols);
            
            long total = 0;
            for (var col : cols)
            {
                col.aggregatedSum += total;
                total = col.aggregatedSum;
                
                // keep always the last one
                colMax = total;
            }
        }
        
        @Override
        public int compareTo(final Row<T, S> o)
        {
            if (o.count == count)
            {
                return 0;
            }
            else if (count < o.count)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }

        @Override
        public String toString()
        {
            return "Row [t=" + t + ", count=" + count + ", sum=" + aggregatedSum + "]";
        }
    }

    public static class Col<S> implements Comparable<Col<S>>
    {
        public final S s;
        public long aggregatedSum;
        public final long count;
        
        public Col(final S type, final long count)
        {
            this.s = type;
            this.count = count;
            this.aggregatedSum = count;
        }
        
        @Override
        public int compareTo(final Col<S> o)
        {
            if (o.count == count)
            {
                return 0;
            }
            else if (count < o.count)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }

        @Override
        public String toString()
        {
            return "Col [s=" + s + ", count=" + count + ", sum=" + aggregatedSum + "]";
        }
    }
}
