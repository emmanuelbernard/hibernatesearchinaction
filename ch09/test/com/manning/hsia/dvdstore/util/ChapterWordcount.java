package com.manning.hsia.dvdstore.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.lucene.analysis.WordlistLoader;

import sun.awt.GlobalCursorManager;

public class ChapterWordcount {
	
	static final Set<String> exclusionList;
	
	static {
		Set<String> excludeList = new HashSet<String>();
		excludeList.add("a");
		excludeList.add("the");
		excludeList.add("para");
		excludeList.add("is");
		excludeList.add("of");
		excludeList.add("and");
		excludeList.add("to");
		excludeList.add("in");
		excludeList.add("listitem");
		excludeList.add("title");
		excludeList.add("indexterm");
		excludeList.add("primary");
		excludeList.add("be");
		excludeList.add("will");
		excludeList.add("on");
		excludeList.add("for");
		excludeList.add("by");
		excludeList.add("not");
		excludeList.add("are");
		excludeList.add("this");
		excludeList.add("section");
		excludeList.add("or");
		excludeList.add("The");
		excludeList.add("it");excludeList.add("don");
		excludeList.add("is");excludeList.add("me");excludeList.add("s");
		excludeList.add("from");excludeList.add("t");excludeList.add("id");excludeList.add("long");
		excludeList.add("that");excludeList.add("don'");excludeList.add("her");excludeList.add("puts");
		excludeList.add("an");excludeList.add("additional");excludeList.add("was");excludeList.add("important");
		excludeList.add("have");excludeList.add("They");excludeList.add("comes");excludeList.add("able");
		excludeList.add("with");excludeList.add("easy");excludeList.add("provided");excludeList.add("let's");
		excludeList.add("as");excludeList.add("How");excludeList.add("To");excludeList.add("different");
		excludeList.add("you");excludeList.add("several");excludeList.add("probably");excludeList.add("typical");
		excludeList.add("more");excludeList.add("possible");excludeList.add("three");excludeList.add("no");excludeList.add("understand");
		excludeList.add("book");excludeList.add("which");excludeList.add("because");excludeList.add("need");
		excludeList.add("itemizelist");excludeList.add("see");excludeList.add("fairly");excludeList.add("depending");
		excludeList.add("figure");excludeList.add("Unfortunately");excludeList.add("unfortunately");excludeList.add("higher");
		excludeList.add("very");excludeList.add("loading");excludeList.add("both");excludeList.add("It");
		excludeList.add("when");excludeList.add("much");excludeList.add("end");excludeList.add("go");excludeList.add("some");
		excludeList.add("mediaobject");excludeList.add("make");excludeList.add("want");excludeList.add("given");
		excludeList.add("they");excludeList.add("last");excludeList.add("its");excludeList.add("them");
		excludeList.add("imageobject");excludeList.add("other");excludeList.add("first");excludeList.add("two");
		excludeList.add("This");excludeList.add("could");excludeList.add("those");excludeList.add("between");
		excludeList.add("into");excludeList.add("example");excludeList.add("In");excludeList.add("quite");
		excludeList.add("has");excludeList.add("such");excludeList.add("lot");excludeList.add("using");
		excludeList.add("your");excludeList.add("up");excludeList.add("these");excludeList.add("chapter");
		excludeList.add("what");excludeList.add("would");excludeList.add("does");excludeList.add("do");
		excludeList.add("each");excludeList.add("use");excludeList.add("so");excludeList.add("common");
		excludeList.add("most");excludeList.add("know");excludeList.add("if");excludeList.add("provide");
		excludeList.add("very");excludeList.add("also");excludeList.add("emphasis");excludeList.add("only");
		excludeList.add("their");excludeList.add("than");excludeList.add("used");excludeList.add("fileref");
		excludeList.add("but");excludeList.add("imagedata");excludeList.add("full");excludeList.add("png");
		excludeList.add("might");excludeList.add("I");excludeList.add("about");excludeList.add("through");
		excludeList.add("way");excludeList.add("xref");excludeList.add("needs");excludeList.add("While");
		excludeList.add("how");excludeList.add("all");
		exclusionList = Collections.unmodifiableSet(excludeList);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final Map<String, Word> words = new HashMap<String, Word>(1000);
		final List<String> wordList;
		for (int chapter = 1 ; chapter <= 10 ; chapter++) {
			chapterWordCount(words, chapter);
		}
		
		wordList = new ArrayList<String>( words.keySet() );
		Collections.sort(wordList, new Comparator<String>() {

			public int compare(String o1, String o2) {
				String left = (String) o1;
				String right = (String) o2;
				return - words.get(left).count + words.get(right).count;
			}
			
		});
		
		File export = new File("/Users/manu/Documents/book/docbook/index.csv");
		FileWriter output = new FileWriter(export);
		BufferedWriter writer = new BufferedWriter(output);
		
		for ( String entry : wordList ) {
			StringBuilder builder = new StringBuilder();
			//if ( words.get(entry).equals( new Integer(1) ) ) break; //only one left
			Word word = words.get(entry);
			builder.append(entry).append(";").append(word.count);
			for (int index = 0 ; index < 10 ; index++) {
				builder.append(";").append(word.chapters[index]);
			}
			writer.write(builder.toString());
			writer.newLine();
		}
		
		
		writer.close();
		//System.out.println( builder.toString() );
	}

	private static void chapterWordCount(Map<String, Word> golbalWords, int chapter ) {
		final String number = chapter == 10 ? "10" : "0" + (chapter);
		final String name = "ch" + number + "/ch" + number + ".xml";
		final Map<String, Word> words = golbalWords;
		int wordCount = 0;
		try {
			File file = new File("/Users/manu/Documents/book/docbook/" + name);
			FileReader input = new FileReader(file);
			BufferedReader reader = new BufferedReader(input);
			String line =  reader.readLine();
			while (line != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, " <:>,./\"()?!;*'", false);
				wordCount++;
				while ( tokenizer.hasMoreTokens() ) {
					addToMap(tokenizer.nextToken(), words, chapter);
				}
				line =  reader.readLine();
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Chapter " + name + "raw  word count=" + wordCount);
		
	}

	private static void addToMap(String rawNextToken, Map<String, Word> words, int chapter) {
		String nextToken = rawNextToken.toLowerCase();
		if (exclusionList.contains( nextToken ) ) return;
		Word count = words.get(nextToken);
		if (count == null) {
			count = new Word();
			count.chapters = new int[10];
		}
		count.count++;
		count.chapters[chapter-1]++;
		words.put(nextToken, count);
	}
	
	private static class Word {
		public int count;
		public int[] chapters;
	}
}
