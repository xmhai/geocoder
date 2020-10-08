package com.lin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Geocoder {
	public static void main(String[] args) {
		System.out.println("Geocoder is running...");
		
		// get input file name
		if (args == null || args.length!=1) {
			System.out.println("Usage: # ./run.sh < {path_to_input_file}.csv");
			return;
		}
		
        // parse the input file
		String fileName = args[0];
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            List<String> result = new ArrayList<String>();
    		AbbreviationManager abbrMgr = new AbbreviationManager();
        	
			stream.forEach(line->result.add(processLine(line, abbrMgr)));
			
	        // output
	        System.out.println("=== RESULT ===");
	        result.forEach(s -> System.out.println(s));
		} catch (IOException e) {
			System.out.println("Error: file %s not found");
		}
	}
	
	static String processLine(String line, AbbreviationManager abbrMgr) {
		if (line == null || line.isBlank() || line.startsWith("\"Abbreviated")) {
			return line;
		}
		
		// split to columns
        String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        if (fields == null || fields.length != 2) {
        	return String.format("%s => ERROR: Invalid entry", line);
        }
        
        String abbrText = AbbreviationUtils.trimDoubleQuote(fields[0]);
        String expandedText = AbbreviationUtils.trimDoubleQuote(fields[1]);
		if (abbrText == null || abbrText.isBlank()) {
        	return String.format("%s => ERROR: Invalid entry", line);
		}
		
		if (expandedText == null || expandedText.isBlank()) {
			// line to expand
			return "\"" + abbrText + "\",\"" + abbrMgr.expand(abbrText) + "\"";
		} else {
			// line to find abbreviations
			try {
				Map<String, String> abbrs = AbbreviationFinder.find(abbrText, expandedText);
				if (!abbrMgr.add(abbrs)) {
		        	return String.format("%s => ERROR - duplicate abbreviation found, entry: ", line);
				}
			} catch (Exception e) {
	        	return String.format("%s => %s", line, e.getMessage());
			}
	        return line;
		}
	}
}
