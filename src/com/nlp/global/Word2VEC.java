package com.nlp.global;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class Word2VEC {

	public static void main(String[] args) throws IOException {
		Word2VEC vec = new Word2VEC();
		vec.loadModel("models//GoogleNews-vectors-negative300.bin");
		
		System.out.println(vec.analogy("tasty", "delicous", "yummy"));
		
	
		int limit = 0;
		
	
			

		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			String word = line.trim();
			if (word.equals("EXIT"))
				break;
			
			if (word.startsWith("ID:"))
			{
				String[] tmp = word.split(":");
				int t = Integer.parseInt(tmp[1]);
				
				System.out.println("wordList[" + t + "]=" + vec.wordList[t]);
			}

			float[] vector = vec.getWordVector(word);
			// Integer ID = vec.getWords().getWordID(word);

			if (vector != null)
				System.out.println(word + "\t" + Arrays.toString(vector));
			else
				System.out.println("not exist");
			
			boolean found = false;
			for(int i = 0; i < vec.words; i++)
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
			
			System.out.println(vec.distance(word));
			
			
	

		}

		scanner.close();

		// 鐢蜂汉 鍥界帇 濂充汉
	}

	private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
	String[] wordList;

	private int words;
	private int size;
	private int topNSize = 40;

	/**
	 * 鍔犺浇妯″瀷
	 * 
	 * @param path
	 *            妯″瀷鐨勮矾寰�
	 * @throws IOException
	 */
	public void loadModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		int num_word = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			// //璇诲彇璇嶆暟
			words = Integer.parseInt(readString(dis));
			wordList = new String[words];
			// //澶у皬
			size = Integer.parseInt(readString(dis));
			System.err.print("#words=" + words);

			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				
				if (num_word == 201)
				{
					int kkk=0;
					kkk++;
				}
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;

				}
				len = Math.sqrt(len);

				for (int j = 0; j < vectors.length; j++) {
					vectors[j] = (float) (vectors[j] / len);
				}
				wordMap.put(word, vectors);
				wordList[num_word] = word;
				//dis.read();
				

				num_word++;

				if (num_word % 100000 == 0)
					System.err.print("..." + (num_word * 100) / words + "%");
			}

			System.err.println();

		} finally {
			bis.close();
			dis.close();
			System.out.println("num_words=" + num_word);
			
		}
	}

	private static final int MAX_SIZE = 100;

	/**
	 * 寰楀埌杩戜箟璇�
	 * 
	 * @param word
	 * @return
	 */
	public Set<WordEntry> distance(String word) {
		float[] wordVector = getWordVector(word);
		if (wordVector == null) {
			return null;
		}
		Set<Entry<String, float[]>> entrySet = wordMap.entrySet();
		float[] tempVector = null;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		String name = null;
		for (Entry<String, float[]> entry : entrySet) {
			name = entry.getKey();
			if (name.equals(word)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	/**
	 * 杩戜箟璇�
	 * 
	 * @return
	 */
	public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
		float[] wv0 = getWordVector(word0);
		float[] wv1 = getWordVector(word1);
		float[] wv2 = getWordVector(word2);

		if (wv1 == null || wv2 == null || wv0 == null) {
			return null;
		}
		float[] wordVector = new float[size];
		for (int i = 0; i < size; i++) {
			wordVector[i] = wv1[i] - wv0[i] + wv2[i];
		}
		float[] tempVector;
		String name;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			name = entry.getKey();
			if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
		// TODO Auto-generated method stub
		if (wordsEntrys.size() < topNSize) {
			wordsEntrys.add(new WordEntry(name, score));
			return;
		}
		float min = Float.MAX_VALUE;
		int minOffe = 0;
		for (int i = 0; i < topNSize; i++) {
			WordEntry wordEntry = wordsEntrys.get(i);
			if (min > wordEntry.score) {
				min = wordEntry.score;
				minOffe = i;
			}
		}

		if (score > min) {
			wordsEntrys.set(minOffe, new WordEntry(name, score));
		}

	}

	public class WordEntry implements Comparable<WordEntry> {
		public String name;
		public float score;

		public WordEntry(String name, float score) {
			this.name = name;
			this.score = score;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.name + "\t" + score;
		}

		@Override
		public int compareTo(WordEntry o) {
			// TODO Auto-generated method stub
			if (this.score > o.score) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	/**
	 * 寰楀埌璇嶅悜閲�
	 * 
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	/**
	 * 璇诲彇涓�涓猣loat
	 * 
	 * @param b
	 * @return
	 */
	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	/**
	 * 璇诲彇涓�涓瓧绗︿覆
	 * 
	 * @param dis
	 * @return
	 * @throws IOException
	 */
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

	public int getTopNSize() {
		return topNSize;
	}

	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public HashMap<String, float[]> getWordMap() {
		return wordMap;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}

}