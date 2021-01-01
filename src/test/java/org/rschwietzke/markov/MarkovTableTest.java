package org.rschwietzke.markov;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class MarkovTableTest
{
    @Test
    void _1x1()
    {
        {
            var mt = new MarkovTable<Character, Character>();
            var mtr = mt.train('A', 'a').finish();
            
            assertEquals(1, mtr.rows.size());
            assertEquals(1l, mtr.count);
    
            var row = mtr.getRow('A').get();
            assertEquals(1, row.columns.size());
            assertEquals(1, row.count);
            assertTrue(1.0d == row.probability);
            
            var entry = mtr.getEntry('A', 'a');
            assertTrue('a' == entry.get().type);
            assertTrue(1 ==   entry.get().count);
            assertTrue(1.0 == entry.get().probability);
        }
        {
            var mt = new MarkovTable<Character, Character>();
            var mtr = mt.train('A', 'a').train('A', 'a').finish();
            
            assertEquals(1, mtr.rows.size());
            assertEquals(2l, mtr.count);
    
            var row = mtr.getRow('A').get();
            assertEquals(1, row.columns.size());
            assertEquals(2l, row.count);
            assertTrue(1.0d == row.probability);
            
            var entry = mtr.getEntry('A', 'a');
            assertTrue('a' == entry.get().type);
            assertTrue(2 ==   entry.get().count);
            assertTrue(1.0d == entry.get().probability);
        }
    }
    
    @Test
    void _2x2_2entries()
    {
        var mt = new MarkovTable<Character, Character>();
        var mtr = mt.train('A', 'a').train('B', 'b').finish();
        
        assertEquals(2, mtr.rows.size());
        assertEquals(2l, mtr.count);

        var rowA = mtr.getRow('A').get();
        assertEquals(1, rowA.columns.size());
        assertEquals(1, rowA.count);
        assertTrue(0.5d == rowA.probability);

        var rowB = mtr.getRow('B').get();
        assertEquals(1, rowB.columns.size());
        assertEquals(1, rowB.count);
        assertTrue(0.5d == rowB.probability);
        
        var entryA = mtr.getEntry('A', 'a');
        assertTrue('a' == entryA.get().type);
        assertTrue(1 ==   entryA.get().count);
        assertTrue(1.0 == entryA.get().probability);
        
        var entryB = mtr.getEntry('B', 'b');
        assertTrue('b' == entryB.get().type);
        assertTrue(1 ==   entryB.get().count);
        assertTrue(1.0 == entryB.get().probability);
    }

    @Test
    void _2x2_4entries()
    {
        var mt = new MarkovTable<Character, Character>();
        mt.train('A', 'a').train('B', 'b');
        mt.train('A', 'c').train('B', 'c');
        var mtr = mt.finish();
        
        assertEquals(2, mtr.rows.size());
        assertEquals(4l, mtr.count);

        var rowA = mtr.getRow('A').get();
        assertEquals(2, rowA.columns.size());
        assertEquals(2, rowA.count);
        assertTrue(0.5d == rowA.probability);

        var rowB = mtr.getRow('B').get();
        assertEquals(2, rowB.columns.size());
        assertEquals(2, rowB.count);
        assertTrue(0.5d == rowB.probability);
        
        var entryA1 = mtr.getEntry('A', 'a');
        assertTrue('a' == entryA1.get().type);
        assertTrue(1 ==   entryA1.get().count);
        assertTrue(0.5d == entryA1.get().probability);
        
        var entryA2 = mtr.getEntry('A', 'c');
        assertTrue('c' == entryA2.get().type);
        assertTrue(1 ==   entryA2.get().count);
        assertTrue(0.5d  == entryA2.get().probability);
        
        var entryB1 = mtr.getEntry('B', 'b');
        assertTrue('b' == entryB1.get().type);
        assertTrue(1 ==   entryB1.get().count);
        assertTrue(0.5d == entryB1.get().probability);

        var entryB2 = mtr.getEntry('B', 'c');
        assertTrue('c' == entryB2.get().type);
        assertTrue(1 ==   entryB2.get().count);
        assertTrue(0.5d  == entryB2.get().probability);
        
        // ask for something that does not exist
        assertFalse(mtr.getRow('C').isPresent());
        assertFalse(mtr.getEntry('A', 'x').isPresent());
    }
}
