package com.nlp.global;

import java.util.ArrayList;

import com.nlp.commons.types.WordToken;

public class WordWithFeatureToken extends WordToken {

	/*public WordWithFeatureToken(String name) {
		super(name);
	}*/
	
	
	public WordWithFeatureToken(String[] tuple, int word_column, int feature_begin, int feature_end) {
		super(tuple[word_column]);
		feature.clear();
		for(int i = feature_begin; i <= feature_end; i++)
		{
			feature.add(tuple[i]);
		}
	}
	
	public WordWithFeatureToken(String word)
	{
		super(word);
		feature.clear();
	}
	
	
	public void addFeature(String f)
	{
		feature.add(f);
	}
	

	public void addFeature(ArrayList<String> f)
	{
		feature.addAll(f);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7468581184302005096L;
	
	
	public ArrayList<String> feature = new ArrayList<String>();

}
