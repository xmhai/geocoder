package com.lin;

public class AbbreviationUtils {
	public static String normalizeField(String s) {
		if (s==null) return null;
		
		return s.toLowerCase().replace("\"", "").replace(".", "");
	}

	public static String trimDoubleQuote(String s) {
		if (s==null) return null;
		
		return s.replaceAll("\"$", "").replaceAll("^\"", "");
	}

	public static String formatAddress(final String str) {
		return capitalize(str);
	}
	
	// copy from Apache WordUtils
    public static String capitalize(final String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        final char[] buffer = str.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (Character.isWhitespace(ch)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(buffer);
    }

	public static void main(String[] args) {
		String s = "hello, world";
		String result = capitalize(s);
		if (!"Hello, World".equals(result)) {
			System.out.println("Test failed");
		} else {
			System.out.println("Test passed");
		}
	}
}
