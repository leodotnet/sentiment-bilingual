/** Statistical Natural Language Processing System
    Copyright (C) 2014  Lu, Wei

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
package com.nlp.commons.types;

public abstract class HiddenToken implements Token{
	
	private static final long serialVersionUID = -6993316481825882477L;
	
	protected int _id = -1;
	protected String _name;
	
	public HiddenToken(String name) {
		this._name = name;
	}
	
	public void setId(int id){
		this._id = id;
	}
	
	@Override
	public int getId(){
		return this._id;
	}
	
	@Override
	public String getName() {
		return this._name;
	}
	
	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract String toString();
}