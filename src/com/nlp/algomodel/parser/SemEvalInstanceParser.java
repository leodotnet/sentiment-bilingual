package com.nlp.algomodel.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.nlp.algomodel.AlgoModel;
import com.nlp.commons.ml.gm.linear.LinearInstance;
import com.nlp.commons.ml.gm.linear.LinearNetwork;
import com.nlp.commons.ml.gm.linear.OutputTag;
import com.nlp.commons.types.InputToken;
import com.nlp.commons.types.Instance;
import com.nlp.commons.types.OutputToken;
import com.nlp.commons.types.WordToken;
import com.nlp.global.WordWithFeatureToken;
import com.nlp.hybridnetworks.DiscriminativeNetworkModel;
import com.nlp.hybridnetworks.GenerativeNetworkModel;
import com.nlp.hybridnetworks.LocalNetworkParam;
import com.nlp.hybridnetworks.NetworkConfig;
import com.nlp.hybridnetworks.NetworkModel;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class SemEvalInstanceParser extends InstanceParser {

	public int InputColIndex = -1;
	public int OutputColIndex = -1;

	public SemEvalInstanceParser(AlgoModel algomodel) {
		super(algomodel);

		clear();
	}

	@Override
	public void BuildInstances() throws FileNotFoundException {

		ArrayList<LinearInstance> instances_arr = new ArrayList<LinearInstance>();

		String filename_input = (String) getParameters("filename_input");

		// training
		String line;
		Scanner scan = new Scanner(new File(filename_input));
		int id = 0;
		String sentence;

		try {
			File inputFile = new File(filename_input);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("sentence");
			System.out.println("----------------------------");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				sentence = eElement.getElementsByTagName("text").item(0).getTextContent();
				sentence = sentence.replaceAll("  ", " ");
				System.out.println(sentence);
				String words[] = sentence.split(separator);

				int[] spaces = new int[sentence.length()];
				Arrays.fill(spaces, 0);
				for (int i = 1; i < sentence.length(); i++) {
					char ch = sentence.charAt(i);
					if (ch == ' ') {
						spaces[i] = spaces[i - 1] + 1;
					} else {
						spaces[i] = spaces[i - 1];
					}
				}

				String[][] features = new String[words.length][10];

				for (int i = 0; i < words.length; i++) {

					// input[i] = new WordWithFeatureToken(words[i]);

					// output[i] = new OutputTag("O");

					features[i][0] = words[i];
					features[i][1] = "O"; // entity-type
					features[i][2] = "000";
					features[i][3] = "NONE";
					features[i][4] = "000";
					features[i][5] = "_";
					features[i][6] = "_";
					features[i][7] = "_"; // sentiment
					features[i][8] = "_"; // pos tag
					features[i][9] = "O"; // B-sentiment
				}

				NodeList opinionList = eElement.getElementsByTagName("Opinion");

				for (int i = 0; i < opinionList.getLength(); i++) {
					Node opinionNode = opinionList.item(i);
					Element opinion = (Element) opinionNode;
					System.out.println(opinionNode);

					String target = opinion.getAttribute("target");
					String category = opinion.getAttribute("category");
					String polarity = opinion.getAttribute("polarity");
					int from = Integer.parseInt(opinion.getAttribute("from"));
					int to = Integer.parseInt(opinion.getAttribute("to"));

					ArrayList<String> feature = new ArrayList<String>();

					if (!target.equals("NULL")) {

						int wordIndex_from = spaces[from];
						int wordIndex_to = spaces[to - 1];

						for (int j = wordIndex_from; j <= wordIndex_to; j++) {
							features[j][1] = (j == wordIndex_from ? "B-" : "I-") + category;
							features[j][7] = polarity;
							features[j][9] = (j == wordIndex_from ? "B-" : "I-") + polarity;

						}
					}

				}

				InputToken[] input = new WordWithFeatureToken[words.length];
				OutputToken[] output = new OutputTag[words.length];
				for (int i = 0; i < words.length; i++) {
					input[i] = new WordWithFeatureToken(features[i], (InputColIndex == -1 ? 0 : InputColIndex), 0,
							features[i].length - 2);
					output[i] = new OutputTag(features[i][(OutputColIndex == -1) ? features[i].length - 1
							: OutputColIndex]);
				}

				id++;
				LinearInstance instance = new LinearInstance(id, 1.0, input, output);
				instance.setLabeled();
				if (algomodel.useTheIntance(instance)) {
					instances_arr.add(instance);
				}

			}
			
			this.algomodel.Instances = new LinearInstance[instances_arr.size()];
			
			for(int i = 0; i < instances_arr.size(); i++)
			{
				this.algomodel.Instances[i] = instances_arr.get(i);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		


	}

	void clear() {

	}

	/*
	 * public static void main(String[] args) { try { File inputFile = new
	 * File("data//semeval2016//restaurants_trial_english_sl.xml");
	 * DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	 * DocumentBuilder dBuilder = dbFactory.newDocumentBuilder(); Document doc =
	 * dBuilder.parse(inputFile); doc.getDocumentElement().normalize();
	 * System.out.println("Root element :" +
	 * doc.getDocumentElement().getNodeName()); NodeList nList =
	 * doc.getElementsByTagName("sentence");
	 * System.out.println("----------------------------"); for (int temp = 0;
	 * temp < nList.getLength(); temp++) { Node nNode = nList.item(temp);
	 * Element eElement = (Element) nNode;
	 * System.out.println(eElement.getElementsByTagName
	 * ("text").item(0).getTextContent());
	 * 
	 * 
	 * NodeList opinionList = eElement.getElementsByTagName("Opinion");
	 * 
	 * for(int i = 0; i < opinionList.getLength(); i++) { Node opinionNode =
	 * opinionList.item(i); Element opinion = (Element)opinionNode;
	 * System.out.println(opinionNode);
	 * 
	 * 
	 * String target = opinion.getAttribute("target"); String category =
	 * opinion.getAttribute("category"); String polarity =
	 * opinion.getAttribute("polarity"); int from =
	 * Integer.parseInt(opinion.getAttribute("from")); int to =
	 * Integer.parseInt(opinion.getAttribute("to"));
	 * 
	 * System.out.println(target + " " + category + " " + polarity); }
	 * 
	 * /* if (nNode.getNodeType() == Node.ELEMENT_NODE) { Element eElement =
	 * (Element) nNode; System.out.println("Student roll no : " +
	 * eElement.getAttribute("rollno")); System.out.println("First Name : " +
	 * eElement.getElementsByTagName("firstname").item(0).getTextContent());
	 * System.out.println("Last Name : " +
	 * eElement.getElementsByTagName("lastname").item(0).getTextContent());
	 * System.out.println("Nick Name : " +
	 * eElement.getElementsByTagName("nickname").item(0).getTextContent());
	 * System.out.println("Marks : " +
	 * eElement.getElementsByTagName("marks").item(0).getTextContent()); } } }
	 * catch (Exception e) { e.printStackTrace(); } }
	 */
}
