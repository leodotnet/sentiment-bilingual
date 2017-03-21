package com.nlp.ml.CRF;

import java.util.ArrayList;




import java.util.Arrays;

import com.nlp.commons.ml.gm.linear.LinearInstance;
import com.nlp.commons.ml.gm.linear.LinearNetwork;
import com.nlp.commons.ml.gm.linear.LinearNetworkCompiler;
import com.nlp.commons.ml.gm.linear.OutputTag;
import com.nlp.commons.types.InputToken;
import com.nlp.commons.types.Instance;
import com.nlp.commons.types.OutputToken;
import com.nlp.commons.types.Token;
import com.nlp.hybridnetworks.LocalNetworkParam;
import com.nlp.hybridnetworks.Network;
import com.nlp.hybridnetworks.NetworkIDMapper;
import com.nlp.targetedsentiment.util.TargetSentimentGlobal;


public class MLCRFCompiler extends LinearNetworkCompiler {

	int NEMaxLength = 3;
	int SpanMaxLength = 10;
	boolean visual = true;
	
	//MLCRFViewer viewer = new MLCRFViewer(this, null, 5);
	void visualize(LinearNetwork network, String title, int networkId)
	{
		LinearInstance inst = (LinearInstance) network.getInstance();
		//if (Math.abs(inst.getInstanceId()) == 1)
			//viewer.visualizeNetwork(network, null, title + "[" + networkId + "]");
	}
	
	public MLCRFCompiler() {
		super(null);
		// TODO Auto-generated constructor stub
	}
	
	public MLCRFCompiler(int NEMaxLength, int SpanMaxLength) {
		super(null);
		this.NEMaxLength = NEMaxLength;
		this.SpanMaxLength = SpanMaxLength;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2100499563741744475L;
	
	public enum NodeType {Start, TAG, End};
	
	//public enum SubNodeType {B, e, A}
	
	public enum PolarityType {positive, negative, neutral}
	
	private OutputToken[] _allOutputs;
	
	MLCRFFeatureManager fm;

	
	int PolarityTypeSize = PolarityType.values().length;
	
	//int SubNodeTypeSize = SubNodeType.values().length;
	
	int TagSize = 7;
	
	public static char[] BIO_char = new char[]{'O', 'B', 'I'};
	
	

	
	public void setOutputToken(OutputToken[] allOutputs)
	{
		this._allOutputs = allOutputs;
	}
	
	public void setFeatureManager(MLCRFFeatureManager fm)
	{
		this.fm = fm;
	}
	
	@Override
	public LinearInstance decompile(Network network) {
		//Network network = (Network)network;
		LinearInstance inst = (LinearInstance)network.getInstance();
		int size = inst.size();
		InputToken[] input = inst.getInput();
	
		
		ArrayList<OutputTag> predication_array = new ArrayList<OutputTag>();

		
		int node_k = network.countNodes()-1;
		//node_k = 0;
		while(true){
			//System.out.print(node_k + " ");
			node_k = network.getMaxPath(node_k)[0];
			
			int[] ids = NetworkIDMapper.toHybridNodeArray(network.getNode(node_k));
			//System.out.println("ids:" + Arrays.toString(ids));
			
			if (ids[4] == NodeType.End.ordinal())
			{
				break;
			}
			
			int pos = size - ids[0];
			int polar = ids[1];
			int BIO = ids[2];
			int node_type = ids[4];
			
			if(ids[4] == NodeType.TAG.ordinal()){
				
				if (BIO == 0)
				{
					predication_array.add(new OutputTag("" + BIO_char[0]));
				}
				else
				{
					predication_array.add(new OutputTag("" + BIO_char[BIO] + "-" + PolarityType.values()[polar]));
				}
				
				
				
			}
			
		}

	
		OutputTag[] prediction = new OutputTag[size];
		
		for(int k = 0; k < prediction.length; k++)
		{
			prediction[k] = predication_array.get(k);

		}

		inst.setPrediction(prediction);
		
		return inst;
	}
	
	@Override
	public LinearNetwork compile(int networkId, Instance inst, LocalNetworkParam param) {
		if(inst.isLabeled()){
			return this.compileLabeled(networkId, (LinearInstance)inst, param);
		} else {
			return this.compileUnlabeled(networkId, (LinearInstance)inst, param);
		}
	}
	
	private LinearNetwork compileUnlabeled(int networkId, LinearInstance inst, LocalNetworkParam param){
		LinearNetwork network = new LinearNetwork(networkId, inst, param);
		
		InputToken[] inputs = inst.getInput();

		//OutputToken[] outputs = inst.getOutput();
		
		int size = inst.size();
		
		
		long start = this.toNode_start(size);
		network.addNode(start);
		
		
		long[][] node_array = new long[size][TagSize];
		
		//build node array
				for(int pos = 0; pos < size; pos++)
				{
					long node = this.toNode_Tag(size, pos, 0, 0); //O
					network.addNode(node);
					node_array[pos][0] = node;
					
					for(int tagID = 1; tagID < TagSize; tagID++)
					{
						int polar = (tagID - 1) % BIO_char.length;
						int BIO = (tagID - 1) / BIO_char.length + 1;
						node = this.toNode_Tag(size, pos, polar, BIO);
						network.addNode(node);
						node_array[pos][tagID] = node;
						
					}
				}
		
		long end = this.toNode_end(inst.size());
		network.addNode(end);
		


		long from = -1, to = -1;
		
		
			
		//add first column of span node from start
		for(int j = 0; j < 4; j++) //O B
		{
			from = start;
			to = node_array[0][j];
			
			int[] ids_from = NetworkIDMapper.toHybridNodeArray(from);
			int[] ids_to = NetworkIDMapper.toHybridNodeArray(to);
			
			//System.out.println(Arrays.toString(ids_from) + "\t" + Arrays.toString(ids_to));
			network.addEdge(from, new long[]{to});
		}
		
		

		
		for(int pos = 1; pos < size ; pos++)
		{
			//O to O B 
			from = node_array[pos - 1][0];
			for(int j = 0; j < 4; j++)
			{
				to = node_array[pos][j];
				network.addEdge(from, new long[]{to});
			}
			
			
			//B to O, B, I
			for(int i = 1; i < 4; i++)
			{
				from = node_array[pos - 1][i];
				for(int j = 0; j < TagSize; j++)
				{
					to = node_array[pos][j];
					network.addEdge(from, new long[]{to});
				}
			}
			
			
			
			//I to O, B, I
			for(int i = 4; i < 7; i++)
			{
				from = node_array[pos - 1][i];
				for(int j = 0; j < TagSize; j++)
				{
					to = node_array[pos][j];
					network.addEdge(from, new long[]{to});
				}
			}
			
			
			
		
		}
		
		//add last column of span node to end
		for(int j = 0; j < 7; j++)
		{
			from = node_array[size - 1][j];
			to = end;
			network.addEdge(from, new long[]{to});
		}

	
		network.finalizeNetwork();
		
		if (visual)
			visualize(network, "Sentiment Model:unlabeled", networkId);
		
		
//		System.err.println(network.countNodes()+" nodes.");
//		System.exit(1);
		
		return network;
	}

	private LinearNetwork compileLabeled(int networkId, LinearInstance inst, LocalNetworkParam param){
		
		LinearNetwork network = new LinearNetwork(networkId, inst, param);
		
		InputToken[] inputs = inst.getInput();
		
		OutputToken[] outputs = inst.getOutput();
			
		int size = inst.size();
		
		long start = this.toNode_start(size);
		network.addNode(start);
		
		long[][] node_array = new long[size][TagSize];
		
		//build node array
		for(int pos = 0; pos < size; pos++)
		{
			long node = this.toNode_Tag(size, pos, 0, 0); //O
			network.addNode(node);
			node_array[pos][0] = node;
			
			for(int tagID = 1; tagID < TagSize; tagID++)
			{
				int polar = (tagID - 1) % BIO_char.length;
				int BIO = (tagID - 1) / BIO_char.length + 1;
				node = this.toNode_Tag(size, pos, polar, BIO);
				network.addNode(node);
				node_array[pos][tagID] = node;
				
			}
		}
		
		long end = this.toNode_end(inst.size());
		network.addNode(end);
		
		
		////////////////////////////////////////////
		
		

		PolarityType polar = null;
		long from = start;
		long to = -1;
		int tag_id = -1;

		
		for(int pos = 0; pos < size; pos++)
		{
			String word = inputs[pos].getName();
			String label = outputs[pos].getName();
			
			char BIO = label.charAt(0);
		
			if (BIO == 'O')
			{
				tag_id = 0;

			}
			else
			{
				String sentiment = label.substring(2);
				polar = PolarityType.valueOf(sentiment);
				tag_id = 1 + (BIO == 'B' ? 0 : 1) * BIO_char.length + polar.ordinal();
				
			}
			
			to = node_array[pos][tag_id];
			network.addEdge(from,  new long[]{to});
			
			from = to;
			
		}
		
		from = to;
		to = end;
		network.addEdge(from,  new long[]{to});
		
		
		network.finalizeNetwork();
		
//		System.err.println(network.countNodes()+" nodes.");
//		System.exit(1);
		
		if (visual)
			visualize(network, "Sentiment Model:labeled", networkId);
		
		return network;
		
	}
	
	boolean startOfEntity(int pos, int size, OutputToken[] outputs)
	{
		String label = outputs[pos].getName();
		if (label.startsWith("B"))
			return true;
		
		if (pos == 0 && label.startsWith("I"))
			return true;
		
		if (pos > 0)
		{
			String prev_label =  outputs[pos - 1].getName();
			if (label.startsWith("I") && prev_label.startsWith("O"))
				return true;
		}
		
		
		return false;
	}
	
	boolean endofEntity(int pos, int size, OutputToken[] outputs)
	{
		String label = outputs[pos].getName();
		if (!label.startsWith("O"))
		{
			if (pos == size - 1)
				return true;
			else {
				String next_label =  outputs[pos + 1].getName();
				if (next_label.startsWith("O") || next_label.startsWith("B"))
					return true;
			}
		}
		
		return false;
	}
	
	
	
	private long toNode_start(int size){
		return NetworkIDMapper.toHybridNodeID(new int[]{size + 1, 0, 0, 0, NodeType.Start.ordinal()});
	}
	
	

	
	private long toNode_Tag(int size, int pos, int polar, int BIO){
		//System.out.println("bIndex=" + bIndex);
		return NetworkIDMapper.toHybridNodeID(new int[]{size - pos, polar, BIO, 0, NodeType.TAG.ordinal()});
	}
	
	
	
	/**/
	private long toNode_end(int size){
		return NetworkIDMapper.toHybridNodeID(new int[]{0, 0, 0, 0, NodeType.End.ordinal()});
	}
	
	
	
	
	

}
