package com.nlp.global;

import java.util.HashMap;
import java.util.Random;

public class Global {

	public static boolean ENABLE_WORD_EMBEDDING = false;
	public static boolean ENABLE_BROWN_CLUSTER = false;
	public static boolean DUMP_FEATURE = false;
	public static boolean ECHO_FEATURE = false;
	public static boolean WEIGHT_PUSH = false;
	public static Random rnd = new Random();
	
	/*** Bilingual ****/
	public static String SourceLang = "";
	public static String TargetLang = "";
	
	
	public static String Lang = "";
	
	
	public static final HashMap<String , String> dataSet = new HashMap<String , String>(){
		{
			put("sentimentspan_latent_semeval", "data//semeval2016//");
			
		}
	};
	
	
	public static String getInPath(String modelname)
	{
		String inPath = dataSet.get(modelname);
		
		if (inPath == null)
		{
			inPath = "data//Twitter_";
		}
		
		return inPath;
	}

	
	
}
