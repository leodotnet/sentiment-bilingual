package com.nlp.targetedsentiment.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.nlp.algomodel.AlgoModel;
import com.nlp.commons.types.InputToken;
import com.nlp.commons.types.Instance;
import com.nlp.hybridnetworks.NetworkConfig;
import com.nlp.targetedsentiment.util.Word2VEC.WordEntry;


public class WordEmbedding {
	
	public static String WORD_EMBEDDING_FEATURE = "<WORD_EMBEDDING>";
	public static WordEmbedding Word2Vec;

	public int Size = -1;
	public int ShapeSize = -1;
	HashMap<String, double[]> word2vec = null;
	HashMap<String, float[]> word2vec_float = null;
	String[] Words;
	HashMap<String, Integer> word2ID;
	public AlgoModel algomodel = null;
	public String[] labels;
	public static int word_embedding_feature_size = -1;
	
	public ArrayList<String> unknown_words = new ArrayList<String>();
	public ArrayList<String> known_words = new ArrayList<String>();
	
	//public static double[] initWeight;
	public static int NUM_INIT_WEIGHT = 10;
	public String suffix = "";
	public String SEPARATOR = "\t";
	
	public static void main(String[] args) throws FileNotFoundException
	{
		String suffix = "en_es";
		WordEmbedding word2vec = new WordEmbedding("bilingual_" + suffix);
		
		/*
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNext())
		{
	       String line = scanner.nextLine();
	       String word = line.trim();
	       if (word.equals("exit"))
	    	   break;

	       Double[] vector = word2vec.getVector(word);
	       Integer ID = word2vec.getWordID(word);
	       
	       if (vector != null)
	       System.out.println(word2vec.getWord(ID) + "(" + ID + ")\t" + Arrays.toString(vector));
	       else
	    	   System.out.println("not exist");
	       
		}

		scanner.close();     */
		
		
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			String word = line.trim();
			if (word.equals("EXIT"))
				break;
			/*
			if (word.startsWith("ID:"))
			{
				String[] tmp = word.split(":");
				int t = Integer.parseInt(tmp[1]);
				
				System.out.println("wordList[" + t + "]=" + word2vec.words[t]);
			}*/

			double[] vector = word2vec.getVector(word);
			// Integer ID = vec.getWords().getWordID(word);

			if (vector != null)
				System.out.println(word + "\t" + Arrays.toString(vector));
			else
				System.out.println("not exist");
			
			/*boolean found = false;
			for(int i = 0; i < word2vec.words; i++)
			{
				if (vec.wordList[i].equals(word))
				{
					System.out.println(word + ":" + i);
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				System.out.println("still no");
			}
			
			System.out.println(vec.distance(word));*/
			
			
	

		}

		scanner.close();
		
	      
	}
	
	
	public WordEmbedding(String lang)
	{
		System.out.println("Loading Word Embedding for Language: " + lang);
		if (lang.equals("en"))
			getWord2VecEnglish();
		else if (lang.equals("es"))
			getWord2VecSpanish();
		else if (lang.equals("en_googlenews"))
			this.getWord2VecEnglish_googlenews();
		else if (lang.startsWith("bilingual"))
		{
			//getWord2VecSpanish();
			String[] biling_arr = lang.split("_");
			this.getWord2VecBilingual(biling_arr[1], biling_arr[2]);
		}
		System.out.println("Word Embedding Loaded");
		
			
	}
	
	public double[] getVector(String word) {
		if (word2vec != null)
			return word2vec.get(word);

		/*
		if (word2vec_float != null) {
			float[] v_d = this.word2vec_float.get(word);

			if (v_d != null) {

				Double[] v = new Double[v_d.length];
				for (int i = 0; i < v.length; i++)
					v[i] = (double) v_d[i];

				return v;
			}
		}*/
		return null;
	}
	
	public float[] getVectorFloat(String word)
	{
		if (word2vec_float != null)
			return this.word2vec_float.get(word);
		
		return null;
	}

	public HashMap<String, double[]> getWord2VecEnglish() {
		return getWord2Vec("models//polyplot-en.dict");
	}

	public HashMap<String, double[]> getWord2VecSpanish() {
		return getWord2Vec("models//polyplot-es.dict");
	}
	
	public HashMap<String, double[]> getWord2VecEnglish_googlenews() {
		try {
			loadModel("models//GoogleNews-vectors-negative300.bin");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.word2vec;
	}
	
	public HashMap<String, double[]> getWord2VecBilingual(String lang1, String lang2) {
		SEPARATOR = " ";
		return getWord2Vec("models//out." + lang1 + "_" + lang2);
		/*
		try {
			loadModel("models//out.de.bin");
			//loadModel("models//en-es_embedding_vulicmoens.bin");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.word2vec;*/
	}

	public HashMap<String, double[]> getWord2Vec(String filename) {
		File f = new File(filename);
		Scanner scanner = null;
		String word, line;
		int pWords = 0;
		String[] items = null;
		word2vec = new HashMap<String, double[]>();
		word2ID = new HashMap<String, Integer>();
		try {
			scanner = new Scanner(f);
			
			line = scanner.nextLine();
			items = line.split(SEPARATOR);
			
			Size = Integer.parseInt(items[0]);
			Words = new String[Size];
			ShapeSize =Integer.parseInt(items[1]);
			
			/*
			if (TargetSentimentGlobal.DEFEAULT_SHAPE_SIZE != -1)
				ShapeSize = TargetSentimentGlobal.DEFEAULT_SHAPE_SIZE;
			*/
			System.out.println("Size=" + Size + "\tShapeSize=" + ShapeSize);
			
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
			
				if (line.trim().length() == 0)
					continue;
				items = line.split(SEPARATOR);
				/*
				if (items.length != ShapeSize + 1)
				{
					System.out.println("Discard " + items[0]);
					continue;
				}*/
				word = items[0];
				
				
				Words[pWords] = word;
				word2ID.put(word, pWords);
				
				double[] vector = new double[ShapeSize];
				for (int i = 0; i < vector.length; i++) {
					vector[i] = Double.parseDouble(items[i + 1]);
				}

				word2vec.put(word, vector);
			
				pWords++;
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		scanner.close();
		
		

		return word2vec;

	}
	
	public Integer getWordID(String word)
	{
		return word2ID.get(word);
	}
	
	public String getWord(int id)
	{
		return this.Words[id];
	}
	
	public void loadModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		int num_word = 0;
		word2vec_float = new HashMap<String, float[]>();
		word2vec = new HashMap<String, double[]>();
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			
			Size = Integer.parseInt(readString(dis));
			Words = new String[Size];
			
			ShapeSize = Integer.parseInt(readString(dis));
			System.err.print("#words=" + Size + "\tdimensions=" + ShapeSize);

			String word;
			float[] vectors = null;
			for (int i = 0; i < Size; i++) {
				
				
				word = readString(dis);
				vectors = new float[ShapeSize];
				len = 0;
				for (int j = 0; j < ShapeSize; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;

				}
				len = Math.sqrt(len);
				
				double[] v = new double[vectors.length];

				for (int j = 0; j < vectors.length; j++) {
					vectors[j] = (float) (vectors[j] / len);
					v[j] = (double)vectors[j];
				}
				
				
				word2vec.put(word, v);
				word2vec_float.put(word, vectors);
				Words[num_word] = word;
				//dis.read();
				

				num_word++;

				if (num_word % 100000 == 0)
					System.err.print("..." + (num_word * 100) / Size + "%");
			}

			System.err.println();

		} finally {
			bis.close();
			dis.close();
			System.out.println("num_words=" + num_word);
			
		}
	}

	private static final int MAX_SIZE = 100;

	

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}


	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	
	private static String readString(DataInputStream dis) throws IOException {
		// TODO Auto-generated method stub
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}

	
	
	
	public void setAlgomodel(AlgoModel algomodel)
	{
		this.algomodel = algomodel;
	}
	
	public void createWordEmbeddingFeature()
	{
		
		if (algomodel != null)
		{
			for(int i = 0; i < this.ShapeSize; i++)
			{
				for(String Out : labels)
				{
					int f_index = algomodel.param.toFeature(TargetSentimentGlobal.Word2Vec.WORD_EMBEDDING_FEATURE, Out, "" + i);		
					//System.out.println(word + "-" + Out + ":" + f_index);
					
				}
			}
		}
		
		word_embedding_feature_size = ShapeSize * labels.length;
		
	}
	
	public void setNumOutput(int numOutput)
	{
		this.labels = new String[numOutput];
	}

}
