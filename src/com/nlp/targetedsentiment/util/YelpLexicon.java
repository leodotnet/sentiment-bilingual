package com.nlp.targetedsentiment.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class YelpLexicon {
	
	String YelpLexiconPath = "";

	HashMap<String, Double> scoreMap = new HashMap<String, Double>();
	
	
	public void load()
	{
		Scanner scan = new Scanner(YelpLexiconPath);
		
		while(scan.hasNextLine())
		{
			String line = scan.nextLine().trim();
			if (line.length() == 0)
				continue;
			String[] tmp = line.split("\t");
			if (tmp.length < 3)
				continue;
			
			String word = tmp[0];
			double score = Double.parseDouble(tmp[1]);
			int npos = Integer.parseInt(tmp[2]);
			int nneg = Integer.parseInt(tmp[3]);
			
			this.scoreMap.put(word, score);
			
		}
		
		scan.close();
		System.out.println("Yelp Lexicon loaded!");
	}
	
	
	public YelpLexicon(String YelpLexiconPath) {
		this.YelpLexiconPath = YelpLexiconPath;
		this.load();
	}
	
	public Double getScore(String word)
	{
		return this.scoreMap.get(word);
	}
	
	
	public Integer getFineScore(String word)
	{
		Double score = this.scoreMap.get(word);
		
		if (score != null)
		{
			return (int)Math.round(score);
		}
		
		return null;
	}

}
