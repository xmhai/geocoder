package com.lin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbbreviationExpander {
	private Abbreviation abbr;
	
	public AbbreviationExpander(Abbreviation abbr) {
		this.abbr = abbr;
	}

	public String expand(String abbrText) {
		if (abbrText==null || abbrText.isBlank()) return "";
		
        List<String> expandedWords = new ArrayList<String>();
        
        String[] abbrWords = AbbreviationUtils.normalizeField(abbrText).split("\\s+");
        Arrays.stream(abbrWords).forEach(s->expandedWords.add(abbr.getExpanded(s)));
        
        return AbbreviationUtils.formatAddress(String.join(" ", expandedWords));
	}
}
