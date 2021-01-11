/*
 * AhoCorasickDoubleArrayTrie Project
 *      https://github.com/hankcs/AhoCorasickDoubleArrayTrie
 * Copyright 2008-2016 hankcs <me@hankcs.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie.Hit;

import junit.framework.TestCase;
import org.ahocorasick.trie.Trie;

import java.io.*;
import java.util.*;

/**
 * @author hankcs
 */
public class TestAhoCorasickDoubleArrayTrie extends TestCase
{
	
	
    private AhoCorasickDoubleArrayTrie<String> buildASimpleAhoCorasickDoubleArrayTrie()
    {
        // Collect test data set
        TreeMap<String, String> map = new TreeMap<>();
        String[] keyArray = new String[]
                {
                        "hers",
                        "his",
                        "she",
                        "he"
                };
        for (String key : keyArray)
        {
            map.put(key, key);
        }
        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(map);
        return acdat;
    }

    private void validateASimpleAhoCorasickDoubleArrayTrie(AhoCorasickDoubleArrayTrie<String> acdat)
    {
        // Test it
        final String text = "uhers";
        acdat.parseText(text, new AhoCorasickDoubleArrayTrie.IHit<String>()
        {
            @Override
            public void hit(int begin, int end, String value)
            {
            	System.out.printf("[%d:%d]=%s%n", begin, end, value);
                assertEquals(text.substring(begin, end), value);
            }
        });
        // Or simply use
        List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = acdat.parseText(text);
        System.out.println(wordList);
    }

    public void testBuildAndParseSimply()
    {
        AhoCorasickDoubleArrayTrie<String> acdat = buildASimpleAhoCorasickDoubleArrayTrie();
        validateASimpleAhoCorasickDoubleArrayTrie(acdat);
    }

    public void testBuildVeryLongWord() throws IOException
    {
        TreeMap<String, String> map = new TreeMap<>();

        int longWordLength = 20000;

        String word = loadText("cn/text.txt");
        map.put(word.substring(10, longWordLength), word.substring(10, longWordLength));
        map.put(word.substring(30, 40), null);

        word = loadText("en/text.txt");
        map.put(word.substring(10, longWordLength), word.substring(10, longWordLength));
        map.put(word.substring(30, 40), null);

        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<String>();
        acdat.build(map);
        
        List<Hit<String>> result = acdat.parseText(word);
        
        assertEquals(2, result.size());
        assertEquals(30, result.get(0).begin);
        assertEquals(40, result.get(0).end);
        assertEquals(10, result.get(1).begin);
        assertEquals(longWordLength, result.get(1).end);
    }

    public void testBuildAndParseWithBigFile() throws IOException
    {
        // Load test data from disk
        Set<String> dictionary = loadDictionary("cn/dictionary.txt");
        final String text = loadText("cn/text.txt");
        // You can use any type of Map to hold data
        Map<String, String> map = new TreeMap<>();
        for (String key : dictionary)
        {
            map.put(key, key);
        }
        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(map);
        // Test it
        acdat.parseText(text, new AhoCorasickDoubleArrayTrie.IHit<String>()
        {
            @Override
            public void hit(int begin, int end, String value)
            {
                assertEquals(text.substring(begin, end), value);
            }
        });
    }

    private static class CountHits implements AhoCorasickDoubleArrayTrie.IHitCancellable<String>
    {
        private int count;
        private boolean countAll;

        CountHits(boolean countAll)
        {
            this.count = 0;
            this.countAll = countAll;
        }


        @Override
        public boolean hit(int begin, int end, String value)
        {
            count += 1;
            return countAll;
        }
    }

    public void testMatches()
    {
        Map<String, Integer> map = new HashMap<>();
        map.put("space", 1);
        map.put("keyword", 2);
        map.put("ch", 3);
        AhoCorasickDoubleArrayTrie<Integer> trie = new AhoCorasickDoubleArrayTrie<>();
        trie.build(map);

        assertTrue(trie.matches("space"));
        assertTrue(trie.matches("keyword"));
        assertTrue(trie.matches("ch"));
        assertTrue(trie.matches("  ch"));
        assertTrue(trie.matches("chkeyword"));
        assertTrue(trie.matches("oooospace2"));
        assertFalse(trie.matches("c"));
        assertFalse(trie.matches(""));
        assertFalse(trie.matches("spac"));
        assertFalse(trie.matches("nothing"));
    }

    public void testFirstMatch()
    {
        Map<String, Integer> map = new HashMap<>();
        map.put("space", 1);
        map.put("keyword", 2);
        map.put("ch", 3);
        AhoCorasickDoubleArrayTrie<Integer> trie = new AhoCorasickDoubleArrayTrie<>();
        trie.build(map);

        AhoCorasickDoubleArrayTrie.Hit<Integer> hit = trie.findFirst("space");
        assertEquals(0, hit.begin);
        assertEquals(5, hit.end);
        assertEquals(1, hit.value.intValue());

        hit = trie.findFirst("a lot of garbage in the space ch");
        assertEquals(24, hit.begin);
        assertEquals(29, hit.end);
        assertEquals(1, hit.value.intValue());

        assertNull(trie.findFirst(""));
        assertNull(trie.findFirst("value"));
        assertNull(trie.findFirst("keywork"));
        assertNull(trie.findFirst(" no pace"));
    }

    public void testCancellation()
    {
        // Collect test data set
        TreeMap<String, String> map = new TreeMap<>();
        String[] keyArray = new String[]
                {
                        "foo",
                        "bar"
                };
        for (String key : keyArray)
        {
            map.put(key, key);
        }
        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(map);
        // count matches
        String haystack = "sfwtfoowercwbarqwrcq";
        CountHits cancellingMatcher = new CountHits(false);
        CountHits countingMatcher = new CountHits(true);
        System.out.println("Testing cancellation");
        acdat.parseText(haystack, cancellingMatcher);
        acdat.parseText(haystack, countingMatcher);
        assertEquals(cancellingMatcher.count, 1);
        assertEquals(countingMatcher.count, 2);
    }

    private String loadText(String path) throws IOException
    {
        StringBuilder sbText = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null)
        {
            sbText.append(line).append("\n");
        }
        br.close();

        return sbText.toString();
    }

    private Set<String> loadDictionary(String path) throws IOException
    {
        Set<String> dictionary = new TreeSet<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null)
        {
            dictionary.add(line);
        }
        br.close();

        return dictionary;
    }

    private void runTest(String dictionaryPath, String textPath) throws IOException
    {
        Set<String> dictionary = loadDictionary(dictionaryPath);
        String text = loadText(textPath);
        // Build a ahoCorasickNaive implemented by robert-bor
        Trie ahoCorasickNaive = new Trie();
        for (String word : dictionary)
        {
            ahoCorasickNaive.addKeyword(word);
        }
        ahoCorasickNaive.parseText(""); // More fairly, robert-bor's implementation needs to call this to build ac automata.
        // Build a AhoCorasickDoubleArrayTrie implemented by hankcs
        AhoCorasickDoubleArrayTrie<String> ahoCorasickDoubleArrayTrie = new AhoCorasickDoubleArrayTrie<>();
        TreeMap<String, String> dictionaryMap = new TreeMap<>();
        for (String word : dictionary)
        {
            dictionaryMap.put(word, word);  // we use the same text as the property of a word
        }
        ahoCorasickDoubleArrayTrie.build(dictionaryMap);
        // Let's test the speed of the two Aho-Corasick automata
        System.out.printf("Parsing document which contains %d characters, with a dictionary of %d words.%n", text.length(), dictionary.size());
        long start = System.currentTimeMillis();
        ahoCorasickNaive.parseText(text);
        long costTimeNaive = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        ahoCorasickDoubleArrayTrie.parseText(text, new AhoCorasickDoubleArrayTrie.IHit<String>()
        {
            @Override
            public void hit(int begin, int end, String value)
            {
            		//just to get log of hit 
            }
        });
        long costTimeACDAT = System.currentTimeMillis() - start;
      //  System.out.printf("%-15s\t%-15s\t%-15s%n", "", "Naive", "ACDAT");
       // System.out.printf("%-15s\t%-15d\t%-15d%n", "time", costTimeNaive, costTimeACDAT);
       // System.out.printf("%-15s\t%-15.2f\t%-15.2f%n", "char/s", (text.length() / (costTimeNaive / 1000.0)), (text.length() / (costTimeACDAT / 1000.0)));
       // System.out.printf("%-15s\t%-15.2f\t%-15.2f%n", "rate", 1.0, costTimeNaive / (double) costTimeACDAT);
        //System.out.println("===========================================================================");
    }

    public void testBenchmark() throws IOException  			//test for just checking status
    {
        runTest("en/dictionary.txt", "en/text.txt");
        runTest("cn/dictionary.txt", "cn/text.txt");
    }

    public void testSaveAndLoad() throws IOException, ClassNotFoundException // test for checking proper saving and loading
    {
    	
    	AhoCorasickDoubleArrayTrie<String> acdat = buildASimpleAhoCorasickDoubleArrayTrie();
        final String tmpPath = System.getProperty("java.io.tmpdir").replace("\\\\", "/") + "/acdat.tmp";
        System.out.println("Saving acdat to: " + tmpPath);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpPath));
        out.writeObject(acdat);
        System.out.println("Loading acdat from: " + tmpPath);
        out.close();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpPath));
        acdat = (AhoCorasickDoubleArrayTrie<String>)in.readObject();
        validateASimpleAhoCorasickDoubleArrayTrie(acdat);
        in.close();
    }

    public void testBuildEmptyTrie()    // test for building empty tree
    {
         AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();			// object for main class
         TreeMap<String, String> map = new TreeMap<>();											// object for generating default tree
        acdat.build(map);
        assertEquals(0, acdat.size());
    }
    
}
