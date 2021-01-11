package com.hankcs.algorithm;

import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AhoCorasickDoubleArrayTrieMain {
	private static final Logger logger = Logger.getLogger(AhoCorasickDoubleArrayTrieMain.class.getName());
	public static void main(String[] args) {
		
		// Collect test data set
        TreeMap<String, String> map = new TreeMap<>();
        String[] keyArray = new String[]
                {
                        "hers",
                        "his",
                        "she",
                        "he"
                };
        for (String key : keyArray){
            map.put(key, key);
        }
        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<>();
        acdat.build(map);
        // Test it
        final String text = "uhers";
        if(text.isEmpty()) {
        	String printEmptyMessage = "Input string is empty!";
        	String logMessage = "Print: " + printEmptyMessage;
        	logger.log(Level.INFO, logMessage);    	
        }
        else {
        
        acdat.parseText(text, new AhoCorasickDoubleArrayTrie.IHit<String>()
        {
            @Override
            public void hit(int begin, int end, String value){
            	int endingValue = end -1;
            	String printOutput = "[" + begin + ":" + endingValue + "] = " + value ;
            	String logMessage = "Print: " + printOutput;
            	logger.log(Level.INFO, logMessage);
            }
        });
        }

	}

}
