package com.nlp.targetedsentiment.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class NRCLexicon {
	
	String NRCLexiconPath = "";

	HashMap<String, String> emotionMap = new HashMap<String, String>();
	
	HashMap<String, Boolean> associMap = new HashMap<String, Boolean>();
	
	public static final HashSet<String> posEmotion = new HashSet<String>(){
		{
			add("anticipation");
			add("joy");
			add("surprise");
			add("trust");
		}
	};
	
	public void load()
	{
		Scanner scan = new Scanner(NRCLexiconPath);
		
		while(scan.hasNextLine())
		{
			String line = scan.nextLine().trim();
			if (line.length() == 0)
				continue;
			String[] tmp = line.split("\t");
			if (tmp.length < 3)
				continue;
			
			String word = tmp[0];
			String emotion = tmp[1];
			boolean associ = tmp[2].equals("1");
			
			this.emotionMap.put(word, emotion);
			this.associMap.put(word, associ);
		}
		
		scan.close();
		System.out.println("NRC Lexicon loaded!");
	}
	
	
	public NRCLexicon(String NRCLexiconPath) {
		this.NRCLexiconPath = NRCLexiconPath;
		this.load();
	}
	
	public String getEmotion(String word)
	{
		return this.emotionMap.get(word);
	}
	
	
	public String Polarity(String word)
	{
		String emotion = this.getEmotion(word);
		
		if (emotion != null)
		{
			return (posEmotion.contains(emotion)) ? "positive" : "negative";
		}
		
		return null;
	}

}
