package com.lin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Geocoder {
	private Abbreviation abbr;
	private AbbreviationFinder abbrFinder;
	private AbbreviationExpander abbrExpander;

	public Geocoder() {
		this.abbr = new Abbreviation();
		this.abbrFinder = new AbbreviationFinder();
		this.abbrExpander = new AbbreviationExpander(this.abbr);
	}
	
	public void processCsv(String filename) {
        List<String> result = new ArrayList<String>();
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			stream.forEach(line->result.add(processLine(line)));
			
	        // output
	        result.forEach(s -> System.out.println(s));
		} catch (IOException e) {
			System.out.println("Error: file " + filename + " not found");
		}
	}

	private String processLine(String line) {
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
			return "\"" + abbrText + "\",\"" + abbrExpander.expand(abbrText) + "\"";
		} else {
			// line to find abbreviations
			try {
				Map<String, String> map = abbrFinder.find(abbrText, expandedText);
				if (!abbr.add(map)) {
		        	return String.format("%s => ERROR - duplicate abbreviation found, entry: ", line);
				}
			} catch (Exception e) {
	        	return String.format("%s => %s", line, e.getMessage());
			}
	        return line;
		}
	}
	
	public static void main(String[] args) {
		// get input file name
		if (args == null || args.length!=1) {
			System.out.println("ERROR: Missing filename argument");
			return;
		}
		
        // parse the input file
		new Geocoder().processCsv(args[0]);
	}
}
