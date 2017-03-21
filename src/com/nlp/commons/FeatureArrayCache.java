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
/**
 * 
 */
package com.nlp.commons;

import java.util.HashMap;

import com.nlp.hybridnetworks.FeatureArray;


/**
 * @author wei_lu
 *
 */
public class FeatureArrayCache {
	
	private HashMap<FeatureArray, FeatureArray> _map;
	private int _numFAs;
	private boolean _cache_enabled = true;
	
	public FeatureArrayCache(){
		this._map = new HashMap<FeatureArray, FeatureArray>();
		this._numFAs = 0;
	}
	
	public synchronized FeatureArray toFeatureArray(FeatureArray fa){
		this._numFAs ++;
		if(!_cache_enabled)
			return fa;
		if(this._map.containsKey(fa)){
			return this._map.get(fa);
		}
		this._map.put(fa, fa);
		return fa;
	}
	
	public int size(){
		return this._map.size();
	}
	
	public int numFAs(){
		return this._numFAs;
	}
	
}