package com.lin;

import java.util.HashMap;
import java.util.Map;

public class Abbreviation {
	private Map<String, String> abbrMap = new HashMap<String, String>();
	
	// Add new abbreviations to dictionary
	public boolean add(Map<String, String> map) {
        for (Map.Entry<String,String> entry : map.entrySet()) {
        	if (abbrMap.containsKey(entry.getKey()) && !abbrMap.get(entry.getKey()).equals(entry.getValue())) {
        		// if the abbr already exists but with a different expanded value, means the data is problematic
        		System.out.println(String.format("ERROR: duplicated abbreviation exists %s, %s", abbrMap.get(entry.getKey()), entry.getValue()));
    			return false;
        	}
        }
		abbrMap.putAll(map);;
		return true;
	}

	public String get(String string) {
		return abbrMap.get(string);
	}

	public boolean containsKey(String string) {
		return abbrMap.containsKey(string);
	}
	
	public String getExpanded(String abbrWord) {
		return abbrMap.containsKey(abbrWord) ? abbrMap.get(abbrWord) : abbrWord;
	}
}
