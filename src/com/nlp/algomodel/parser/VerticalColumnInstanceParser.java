package com.nlp.algomodel.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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


public class VerticalColumnInstanceParser extends InstanceParser {
	
	public int InputColIndex = -1;
	public int OutputColIndex = -1;

	public VerticalColumnInstanceParser(AlgoModel algomodel) {
		super(algomodel);
		separator = "\t";
		clear();
	}

	@Override
	public void BuildInstances() throws FileNotFoundException {
		
		
		ArrayList<LinearInstance> instances_arr = new ArrayList<LinearInstance>();
		
		
		String filename_input = (String) getParameters("filename_input");
		
		//training
		String line;
		Scanner scan = new Scanner(new File(filename_input), "UTF-8");
		int id = 0;
		String sentence;
		ArrayList<String> lines = new ArrayList<String>();
		
		while(scan.hasNextLine())
		{
	
			sentence = "";
			
			lines.clear();
			
			while(scan.hasNextLine())
			{
			
				line = scan.nextLine().trim();
				if (line.equals(""))
				{
					break;
				}
				else if (line.startsWith("##Tweet"))
				{
					continue;
				}
				
				lines.add(line);

				
			}
			
			
			id++;
			
			InputToken[] input = new WordWithFeatureToken[lines.size()];
			OutputToken[] output = new OutputTag[lines.size()];
			int num_entity = 0;
			
			for(int i = 0; i < lines.size(); i++)
			{
				String fields[] = lines.get(i).split(separator);
				
				input[i] = new WordWithFeatureToken(fields, (InputColIndex == -1 ? 0 : InputColIndex), 0, fields.length - 2);
				
				output[i] = new OutputTag(fields[(OutputColIndex == -1) ? fields.length - 1:OutputColIndex]);
				if (!output[i].getName().startsWith("O"))
				{
					num_entity++;
				}
			}
	
			LinearInstance instance = new LinearInstance(id, 1.0, input, output);
			instance.setLabeled();
			if (num_entity > 0 && algomodel.useTheIntance(instance))
			{
				instances_arr.add(instance);
			}
		}
		
		scan.close();
		
		this.algomodel.Instances = new LinearInstance[instances_arr.size()];
		
		for(int i = 0; i < instances_arr.size(); i++)
		{
			this.algomodel.Instances[i] = instances_arr.get(i);
		}
		
	}
	
	
	void clear()
	{
		
	}
	
	

}
