package org.rschwietzke.markov;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.unimi.dsi.util.FastRandom;

class TrainedMarkovTableTest
{
    @Test
    void _2x2_2entries()
    {
        var mt = new MarkovTable<Character, Character>();
        var mtr = mt.train('A', 'a').train('B', 'b').finish();
        
        var tt = new TrainedMarkovTable<>(mtr);
        verifyTable(tt, 2, 2L);
        
        verifyRow(tt, 0, 'A', 1, 1L);
        verifyCol(tt, 0, 0, 'a', 1L);
        
        verifyRow(tt, 1, 'B', 1, 2L);
        verifyCol(tt, 1, 0, 'b', 1L);
    }

    @Test
    void _2x2_4entries()
    {
        var mt = new MarkovTable<Character, Character>();
        mt.train('A', 'a').train('B', 'b');
        mt.train('A', 'c').train('B', 'c');
        var tt = new TrainedMarkovTable<>(mt.finish());

        verifyTable(tt, 2, 4L);

        verifyRow(tt, 0, 'A', 2, 2L);
        verifyCol(tt, 0, 0, 'a', 1L);
        verifyCol(tt, 0, 1, 'c', 2L);

        verifyRow(tt, 1, 'B', 2, 4L);
        verifyCol(tt, 1, 0, 'b', 1L);
        verifyCol(tt, 1, 1, 'c', 2L);
    }

    @Test
    void _4x1_entries()
    {
        var mt = new MarkovTable<String, String>().train("A", "a").train("B", "a").train("C", "a").train("D", "a");
        var tt = new TrainedMarkovTable<>(mt);
        
        verifyTable(tt, 4, 4L);

        verifyRow(tt, 0, "A", 1, 1L);
        verifyCol(tt, 0, 0, "a", 1L);

        verifyRow(tt, 1, "B", 1, 2L);
        verifyCol(tt, 1, 0, "a", 1L);

        verifyRow(tt, 2, "C", 1, 3L);
        verifyCol(tt, 2, 0, "a", 1L);

        verifyRow(tt, 3, "D", 1, 4L);
        verifyCol(tt, 3, 0, "a", 1L);
    }
    
    @Test
    void _1x1_access()
    {
        var mt = new MarkovTable<Character, Character>().train('A', 'a');
        var tt = new TrainedMarkovTable<>(mt);
        
        assertEquals(Character.valueOf('A'), tt.randomRow(LongFastRandom.get(1)));
        assertEquals(Character.valueOf('a'), tt.randomCol(LongFastRandom.get(1), 'A').get());

        // real random is the same
        assertEquals(Character.valueOf('A'), tt.randomRow(FastRandom.get(1232)));
        assertEquals(Character.valueOf('a'), tt.randomCol(FastRandom.get(12342), 'A').get());
        
        // ask for something that is not in
        assertEquals(Optional.empty(), tt.randomCol(FastRandom.get(234234), 'B'));
    }
    
    @Test
    void _4x1_access_equalDist()
    {
        var mt = new MarkovTable<String, String>().train("A", "a").train("B", "a").train("C", "a").train("D", "a");
        var tt = new TrainedMarkovTable<>(mt);
        
        assertEquals("A", tt.randomRow(LongFastRandom.get(1)));
        assertEquals("B", tt.randomRow(LongFastRandom.get(2)));
        assertEquals("C", tt.randomRow(LongFastRandom.get(3)));
        assertEquals("D", tt.randomRow(LongFastRandom.get(4)));
    }
    
    @Test
    void _4x1_access_unequalDist()
    {
        var mt = new MarkovTable<String, String>().train("A", "a").train("A", "a").train("A", "a").train("D", "a");
        var tt = new TrainedMarkovTable<>(mt);
        
        assertEquals("A", tt.randomRow(LongFastRandom.get(1)));
        assertEquals("A", tt.randomRow(LongFastRandom.get(2)));
        assertEquals("A", tt.randomRow(LongFastRandom.get(3)));
        assertEquals("D", tt.randomRow(LongFastRandom.get(4)));
    }    
    
    @Test
    void _1x4_access_equalDist()
    {
        var mt = new MarkovTable<String, String>().train("A", "a").train("A", "b").train("A", "c").train("A", "d");
        var tt = new TrainedMarkovTable<>(mt);
        
        assertEquals("A", tt.randomRow(LongFastRandom.get(1)));
        assertEquals("A", tt.randomRow(LongFastRandom.get(11)));
        
        assertEquals("a", tt.randomCol(LongFastRandom.get(1), "A").get());
        assertEquals("b", tt.randomCol(LongFastRandom.get(2), "A").get());
        assertEquals("c", tt.randomCol(LongFastRandom.get(3), "A").get());
        assertEquals("d", tt.randomCol(LongFastRandom.get(4), "A").get());
    }
    
    @Test
    void _1x3_access_unequalDist()
    {
        var mt = new MarkovTable<String, String>().train("A", "d").train("A", "b").train("A", "d").train("A", "d").train("A", "e").train("A", "b");
        var tt = new TrainedMarkovTable<>(mt);
        
        assertEquals("A", tt.randomRow(LongFastRandom.get(1)));
        
        assertEquals("d", tt.randomCol(LongFastRandom.get(1), "A").get());
        assertEquals("d", tt.randomCol(LongFastRandom.get(2), "A").get());
        assertEquals("d", tt.randomCol(LongFastRandom.get(3), "A").get());
        assertEquals("b", tt.randomCol(LongFastRandom.get(4), "A").get());
        assertEquals("b", tt.randomCol(LongFastRandom.get(5), "A").get());
        assertEquals("e", tt.randomCol(LongFastRandom.get(6), "A").get());
    }
    
    /*
     * Helper methods for testing
     */
    private static <T, S> void verifyTable(TrainedMarkovTable<T, S> table, int rowCount, long rowSum)
    {
        assertEquals(rowCount, table.rows.size());
        assertEquals(rowSum, table.sum);    
    }

    private static <T, S> void verifyRow(TrainedMarkovTable<T, S> table, int row, T t, int colCount, long sum)
    {
        var r = table.rows.get(row);
        assertEquals(t, r.t);
        assertEquals(sum, r.sum);
        assertEquals(colCount, r.cols.size());    
    }
    
    private static <T, S> void verifyCol(TrainedMarkovTable<T, S> table, int row, int col, S s, long sum)
    {
        var r = table.rows.get(row);
        var c = r.cols.get(col);
        assertEquals(s, c.s);
        assertEquals(sum, c.sum);
    }
}
