package org.rschwietzke.markov;

/**
 * Trains full words by splitting them up into characters that follow characters and enables us to ask for a word
 * later on
 * 
 * @author rschwietzke
 *
 */
public class WordTrainer implements Trainer<String, String>
{
    private final MarkovTable<String, Void> start = new MarkovTable<>();
    private final MarkovTable<String, String> middle = new MarkovTable<>();
    private final MarkovTable<Integer, String> wordCount = new MarkovTable<>();
    
    @Override
    public void train(final String data)
    {
        // drop anything that is too short
        if (data.length() < 3)
        {
            return;
        }
        
        // ok, split the string up and train pairs
        // but not the 
        for (int i = 0; i < data.length() - 1; i++)
        {
            table.train(String.valueOf(data.charAt(i)), String.valueOf(data.charAt(i + 1)));
        }
        
        // end we train the end too
        table.train(String.valueOf(data.charAt(data.length() - 1)), "");
    }

    @Override
    public Generator<String> generator()
    {
        return new WordGenerator();
    }

}
