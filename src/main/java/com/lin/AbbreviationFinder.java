package com.lin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AbbreviationFinder {
	// compare abbr with full version to generate abbr map
	public Map<String, String> find(String abbrText, String expandedText) throws Exception {
		if (abbrText == null || expandedText == null) {
			throw new Exception("ERROR: Invalid entry");
		}
		
        String[] abbrWords = AbbreviationUtils.normalizeField(abbrText).split("\\s+");
        String[] expandedWords = AbbreviationUtils.normalizeField(expandedText).split("\\s+");
        
        if (abbrWords.length == expandedWords.length) {
        	// one to one abbreviation
        	return findOneToOne(abbrWords, expandedWords);
        } else if (abbrWords.length < expandedWords.length) {
        	// one to many abbreviation
        	return findOneToMany(abbrWords, expandedWords);
        } else {
        	// should not happen, abbr version should be shorter than expanded version
        	throw new Exception("ERROR: Invalid entry");
        }
	}

	private Map<String, String> findOneToOne(String[] abbrWords, String[] expandedWords) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
    	for (int i=0; i<abbrWords.length; i++) {
    		String abbrWord = abbrWords[i];
    		String expandedWord = expandedWords[i];
    		if (abbrWord.length() == expandedWord.length()) {
        		if (abbrWord.equals(expandedWord)) {
        			// not an abbreviation, skip
        		} else {
                	// should not happen, must be the same
        			throw new Exception(String.format("ERROR - Not a correct abbreviation, abbr: %s, expanded: %s", abbrWord, expandedWord));
        		}
    		} else if (abbrWord.length()<expandedWord.length()) {
    			// abbreviation
    			if (isAbbreviation(abbrWord, expandedWord)) {
    				map.put(abbrWord, expandedWord);
    			} else {
    				throw new Exception(String.format("ERROR - Not a correct abbreviation, abbr: %s, expanded: %s", abbrWord, expandedWord));
    			}
    		} else {
    			// should not happen, abbr word must be shorter than expanded word
    			throw new Exception(String.format("ERROR - Not a correct abbreviation, abbr: %s, expanded: %s", abbrWord, expandedWord));
    		}
    	}
		
        return map;
	}

	private Map<String, String> findOneToMany(String[] abbrWords, String[] expandedWords) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
    	int j=0;
    	for (int i=0; i<abbrWords.length; i++) {
    		String abbrWord = abbrWords[i];
    		if (abbrWord.equals(expandedWords[j])) {
    			// not an abbreviation, skip
    			j++;
    			continue;
    		}
    		
    		// check if the word is the abbreviation of many words
    		if (isMultiWordAbbreviation(abbrWord, expandedWords, j)) {
    			String fullWord = String.join(" ", Arrays.copyOfRange(expandedWords, j, j + abbrWord.length()));
				map.put(abbrWord, fullWord);
    			j = j + abbrWord.length();
    			continue;
    		}
    		
    		// check if the work is the abbreviation of one word
    		if (isAbbreviation(abbrWord, expandedWords[j])) {
				map.put(abbrWord, expandedWords[j]);
    			j++;
    			continue;
    		}
    		
    		// problematic word
    		throw new Exception(String.format("ERROR - invalid data, abbr: %s", abbrWord));
    	}
		
        return map;
	}
	
	// check whether all the character are inside the expanded version, return false if not
	private boolean isAbbreviation(String abbrWord, String expandedWord) {
		if (abbrWord==null || abbrWord.isBlank() || expandedWord==null || expandedWord.isBlank()) return false;

		StringBuilder regex = new StringBuilder();
		abbrWord.chars().forEach(c->regex.append((char)c).append(".*"));
		
		return expandedWord.matches(regex.toString());
	}

	private boolean isMultiWordAbbreviation(String abbrWord, String[] expandedWords, int startIndex) {
		if (abbrWord==null || abbrWord.isBlank() || expandedWords==null ) return false;

		if (expandedWords.length < abbrWord.length()) return false;
		
		for (int i=0; i<abbrWord.length(); i++) {
			if (expandedWords[startIndex + i].charAt(0) != abbrWord.charAt(i)) return false;
		}
		
		return true;
	}

	public static void main(String[] args) throws Exception {
		AbbreviationFinder abbrFinder = new AbbreviationFinder();
		
		// test find()
		String abbrText = "18 AMK";
		String expandedText = "18 Ang Mo Kio";
		Map<String, String> map = abbrFinder.find(abbrText, expandedText);
		String value = map.get("amk");
		if ("ang mo kio".equals(value)) {
			System.out.println("Test passed");
		} else {
			System.out.println("Test failed");
		}

		abbrText = "abc st ctr";
		expandedText = "abc Street Center";
		map = abbrFinder.find(abbrText, expandedText);
		value = map.get("st");
		if ("street".equals(value)) {
			System.out.println("Test passed");
		} else {
			System.out.println("Test failed");
		}

		abbrText = "st ctr error version";
		expandedText = "Street Center";
		try {
			map = abbrFinder.find(abbrText, expandedText);
			System.out.println("Test failed");
		} catch (Exception e) {
			if (e.getMessage().startsWith("ERROR")) {
				System.out.println("Test passed");
			} else {
				System.out.println("Test failed");
			}
		}
	}
}
