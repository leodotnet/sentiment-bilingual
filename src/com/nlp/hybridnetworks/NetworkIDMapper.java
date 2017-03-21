/** Statistical Natural Language Processing System
    Copyright (C) 2014-2016  Lu, Wei

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.nlp.hybridnetworks;

import java.io.Serializable;
import java.util.Arrays;

public class NetworkIDMapper implements Serializable{
	
	private static final long serialVersionUID = -7101734566617861789L;

	public static int[] _CAPACITY_FOREST = new int[]{NetworkConfig._FOREST_MAX_HEIGHT+1, NetworkConfig._FOREST_MAX_WIDTH+1};
	
	//for both sides, the max height and max width are both 10000, and the max depth is 100
	public static int[] _CAPACITY_NETWORK = new int[]{_CAPACITY_FOREST[0],_CAPACITY_FOREST[1],
													  _CAPACITY_FOREST[0],_CAPACITY_FOREST[1],
													  NetworkConfig._NETWORK_MAX_DEPTH};
	
	public static void main(String args[]){
		int[] capacity = new int[]{1000,1000,1000,1000,1000,1000};
		int[] v = new int[]{1,2,3,4,10,6};
		NetworkIDMapper.setCapacity(capacity);
		long u = NetworkIDMapper.toHybridNodeID(v);
		if(u>Long.MAX_VALUE){
			System.err.println(u+">"+Long.MAX_VALUE);
		}
		int[] w = NetworkIDMapper.toHybridNodeArray(u);
		System.err.println(u);
		System.err.println(Arrays.toString(w));
	}
	
	public static void setCapacity(int[] capacity){
		_CAPACITY_NETWORK = capacity;
		//now we check if the capacity is valid.
		int[] v = new int[capacity.length];
		for(int k = 0; k<v.length; k++)
			v[k] = capacity[k]-1;
		int[] u = NetworkIDMapper.toHybridNodeArray(toHybridNodeID(v));
		if(!Arrays.equals(u, v)){
			throw new RuntimeException("The capacity appears to be too large:"+Arrays.toString(capacity));
		}
		System.err.println("Capacity successfully set to: "+Arrays.toString(capacity));
	}
	
	public static int[] getCapacity(){
		return _CAPACITY_NETWORK;
	}
	
	public static long maxHybridNodeID(){
		int[] _RESULT = new int[_CAPACITY_NETWORK.length];
		for(int k = 0; k<_CAPACITY_NETWORK.length; k++)
			_RESULT[k] = _CAPACITY_NETWORK[k]-1;
		return toHybridNodeID(_RESULT);
	}
	
	public static int[] toHybridNodeArray(long value){
		int[] _RESULT = new int[_CAPACITY_NETWORK.length];
		for(int k = _RESULT.length-1 ; k>=1; k--){
			long v = value / _CAPACITY_NETWORK[k];
			_RESULT[k] = (int) (value % _CAPACITY_NETWORK[k]);
			value = v;
		}
		_RESULT[0] = (int)value;
		return _RESULT;
	}
	
	public static long toHybridNodeID(int[] array){
		if(array.length!=_CAPACITY_NETWORK.length){
			throw new RuntimeException("array size is "+array.length);
		}
		long v = array[0];
		for(int k = 1 ; k<array.length; k++){
			if(array[k]>=_CAPACITY_NETWORK[k]){
				throw new RuntimeException("Invalid: capacity for "+k+" is "+_CAPACITY_NETWORK[k]+" but the value is "+array[k]);
			}
			v = v* _CAPACITY_NETWORK[k] + array[k];
		}
		return v;
	}
	
	public static long toHybridNodeID(long srcId, long tgtId, int patternId){
		long v = srcId * _CAPACITY_NETWORK[2] * _CAPACITY_NETWORK[3] + tgtId;
		v = v* _CAPACITY_NETWORK[4] + patternId;
		return v;
	}
	
}