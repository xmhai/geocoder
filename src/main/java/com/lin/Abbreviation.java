package com.lin;

import java.util.HashMap;
import java.util.Map;

public class AbbreviationManager {
	private Map<String, String> dict = new HashMap<String, String>();
	
	// Add new abbreviations to dictionary
	public boolean add(Map<String, String> map) {
        for (Map.Entry<String,String> entry : map.entrySet()) {
        	if (dict.containsKey(entry.getKey()) && !dict.get(entry.getKey()).equals(entry.getValue())) {
        		// if the abbr already exists but with a different expanded value, means the data is problematic
        		System.out.println(String.format("ERROR: duplicated abbreviation exists %s, %s", dict.get(entry.getKey()), entry.getValue()));
    			return false;
        	}
        }
		dict.putAll(map);;
		return true;
	}

	public String expand(String s) {
        String[] words = AbbreviationUtils.normalizeField(s).split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<words.length; i++) {
        	String expandedText = dict.containsKey(words[i]) ? dict.get(words[i]) : words[i];
        	sb.append(expandedText);
        	if (i<words.length-1) {
            	sb.append(" ");
        	}
        }
        return AbbreviationUtils.capitalize(sb.toString());
	}
}
