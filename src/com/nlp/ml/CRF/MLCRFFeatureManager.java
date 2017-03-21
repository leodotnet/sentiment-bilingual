package com.nlp.ml.CRF;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.nlp.commons.ml.gm.linear.LinearFeatureManager;
import com.nlp.commons.ml.gm.linear.LinearNetwork;

import com.nlp.commons.types.InputToken;
import com.nlp.commons.types.Instance;
import com.nlp.commons.types.OutputToken;
import com.nlp.global.WordWithFeatureToken;
import com.nlp.hybridnetworks.FeatureArray;
import com.nlp.hybridnetworks.GlobalNetworkParam;
import com.nlp.hybridnetworks.Network;
import com.nlp.hybridnetworks.NetworkConfig;
import com.nlp.hybridnetworks.NetworkIDMapper;
import com.nlp.ml.CRF.MLCRFCompiler.*;
import com.nlp.targetedsentiment.util.TargetSentimentGlobal;


public class MLCRFFeatureManager extends LinearFeatureManager {
	

	public MLCRFFeatureManager(GlobalNetworkParam param_g) {
		super(param_g, null);
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1376229168119316535L;
	
	public enum FEATURE_TYPES {Unigram, Bigram, Trigram, Fourgram, Transition}
	

	int PolarityTypeSize = PolarityType.values().length;
	

	protected InputToken[] _inputTokens;
	protected OutputToken[] _outputTokens;
	
	public void setInputTokens(InputToken[] inputTokens)
	{
		this._inputTokens = inputTokens;
	}
	
	public void setOutputTokens(OutputToken[] outputTokens)
	{
		this._outputTokens = outputTokens;
	}
	
	
	
	@Override
	public FeatureArray extract_helper(Network network, int parent_k, int[] children_k) {
		
		if(children_k.length>1)
			throw new RuntimeException("The number of children should be at most 1, but it is "+children_k.length);
		
		long node_parent = ((LinearNetwork)network).getNode(parent_k);
		
		if(children_k.length == 0)
			return FeatureArray.EMPTY;
		
		Instance inst = network.getInstance();
		
		this.setInputTokens((InputToken[])inst.getInput());
		this.setOutputTokens((OutputToken[])inst.getOutput());
		
		int size = inst.size();
		
		long node_child = ((LinearNetwork)network).getNode(children_k[0]);
		
		int[] ids_parent = NetworkIDMapper.toHybridNodeArray(node_parent);
		int[] ids_child = NetworkIDMapper.toHybridNodeArray(node_child);

		InputToken[] input_token = (InputToken[])inst.getInput();
		
		int pos_parent = size - ids_parent[0];
		int pos_child = size - ids_child[0];
		
		int polar_parent = ids_parent[1];
		int polar_child =  ids_child[1];
		
		int BIO_parent = ids_parent[2];
		int BIO_child = ids_child[2];
		

		int nodetype_parent = ids_parent[4];
		int nodetype_child = ids_child[4];
		
		int num_sent = 0;

		String word_parent = "<START>";
		String word_child = "<END>";
		
		if (pos_parent >= 0 && pos_parent < size)
			word_parent = input_token[pos_parent].getName();
		
		if (pos_child >= 0 && pos_child < size)
			word_child = input_token[pos_child].getName();
		

	
		FeatureArray fa = FeatureArray.EMPTY;
		
		ArrayList<Integer> feature = new ArrayList<Integer>();
		ArrayList<String[]> featureArr = new ArrayList<String[]>();
		
		
		String Out = BIO_child == 0 ? "O" : MLCRFCompiler.BIO_char[BIO_child] + "-" + PolarityType.values()[polar_child];
		
		String lastOut = BIO_parent == 0 ? "O" : MLCRFCompiler.BIO_char[BIO_parent] + "-" + PolarityType.values()[polar_parent];
		
		LinearNetwork mynetwork = (LinearNetwork) network;
		
		if (nodetype_child == NodeType.TAG.ordinal())
		{
			String prev_word = word_parent;
			String word = word_child;
			
			
			feature.add(this._param_g.toFeature(FEATURE_TYPES.Unigram + "_word:" , Out,  word));
			//feature.add(this._param_g.toFeature(FEATURE_TYPES.Bigram + "_word:" , Out,  prev_word + "|||" + word));
			
			feature.add(this._param_g.toFeature(FEATURE_TYPES.Transition + ":" , Out,  lastOut));
			
		}
		

		
		if (feature.size() > 0)
		{
			
			{
				int f[] = new int[feature.size()];
				
				for(int i = 0; i < f.length; i++)
					f[i] = feature.get(i);
		
				fa =  new FeatureArray(f);
			}
		}
		return fa;
	}
	

};
	
	
	
	




