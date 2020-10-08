package com.lin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AbbreviationFinder {
	// compare abbr with full version to generate abbr map
	public static Map<String, String> find(String abbrText, String expandedText) throws Exception {
		if (abbrText == null || expandedText == null) {
			throw new Exception("ERROR: Invalid entry");
		}
		
        String abbr = AbbreviationUtils.normalizeField(abbrText);
        String full = AbbreviationUtils.normalizeField(expandedText);
		
        String[] abbrWords = abbr.split("\\s+");
        String[] fullWords = full.split("\\s+");
        
        if (abbrWords.length == fullWords.length) {
        	// one to one abbreviation
        	return findOneToOne(abbrWords, fullWords);
        } else if (abbrWords.length < fullWords.length) {
        	// one to many abbreviation
        	return findOneToMany(abbrWords, fullWords);
        } else {
        	// should not happen, abbr version should be shorter than expanded version
        	throw new Exception("ERROR: Invalid entry");
        }
	}

	static Map<String, String> findOneToOne(String[] abbrWords, String[] fullWords) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
    	for (int i=0; i<abbrWords.length; i++) {
    		String abbr = abbrWords[i];
    		String full = fullWords[i];
    		if (abbr.length() == full.length()) {
        		if (abbr.equals(full)) {
        			// not an abbreviation, skip
        		} else {
                	// should not happen, must be the same
        			throw new Exception(String.format("ERROR - Not a correct abbreviation, abbr: %s, full: %s", abbr, full));
        		}
    		} else if (abbr.length()<full.length()) {
    			// abbreviation
    			if (isAbbreviation(abbr, full)) {
    				map.put(abbr, full);
    			} else {
    				throw new Exception(String.format("ERROR - Not a correct abbreviation, abbr: %s, full: %s", abbr, full));
    			}
    		} else {
    			// should not happen, abbr word must be shorter than expanded word
    			throw new Exception(String.format("ERROR - Not a correct abbreviation, abbr: %s, full: %s", abbr, full));
    		}
    	}
		
        return map;
	}

	static Map<String, String> findOneToMany(String[] abbrWords, String[] fullWords) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
    	int j=0;
    	for (int i=0; i<abbrWords.length; i++) {
    		String abbr = abbrWords[i];
    		if (abbr.equals(fullWords[j])) {
    			// not an abbreviation, skip
    			j++;
    			continue;
    		}
    		
    		// check if the word is the abbreviation of many words
    		if (isMultiWordAbbreviation(abbr, fullWords, j)) {
    			String fullWord = String.join(" ", Arrays.copyOfRange(fullWords, j, j + abbr.length()));
				map.put(abbr, fullWord);
    			j = j + abbr.length();
    			continue;
    		}
    		
    		// check if the work is the abbreviation of one word
    		if (isAbbreviation(abbr, fullWords[j])) {
				map.put(abbr, fullWords[j]);
    			j++;
    			continue;
    		}
    		
    		// problematic word
    		throw new Exception(String.format("ERROR - invalid data, abbr: %s", abbr));
    	}
		
        return map;
	}
	
	// check whether all the character are inside the expanded version, return false if not
	static boolean isAbbreviation(String abbr, String full) {
		if (abbr==null || abbr.isBlank() || full==null || full.isBlank()) return false;

		StringBuilder sb = new StringBuilder();
		abbr.chars().forEach(c->sb.append((char)c).append(".*"));
		
		return full.matches(sb.toString());
	}

	static boolean isMultiWordAbbreviation(String abbr, String[] full, int startIndex) {
		if (abbr==null || abbr.isBlank() || full==null ) return false;

		if (full.length < abbr.length()) return false;
		
		for (int i=0; i<abbr.length(); i++) {
			if (full[startIndex + i].charAt(0) != abbr.charAt(i)) return false;
		}
		
		return true;
	}

	public static void main(String[] args) throws Exception {
		// test find()
		String abbrText = "18 AMK";
		String expandedText = "18 Ang Mo Kio";
		Map<String, String> map = AbbreviationFinder.find(abbrText, expandedText);
		String value = map.get("amk");
		if ("ang mo kio".equals(value)) {
			System.out.println("Test passed");
		} else {
			System.out.println("Test failed");
		}

		abbrText = "abc st ctr";
		expandedText = "abc Street Center";
		map = AbbreviationFinder.find(abbrText, expandedText);
		value = map.get("st");
		if ("street".equals(value)) {
			System.out.println("Test passed");
		} else {
			System.out.println("Test failed");
		}

		abbrText = "st ctr error version";
		expandedText = "Street Center";
		try {
			map = AbbreviationFinder.find(abbrText, expandedText);
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
