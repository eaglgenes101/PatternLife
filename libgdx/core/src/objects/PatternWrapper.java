package objects;

import java.util.Arrays;

/*
PatternLife boolean array wrappers
Copyright (C) 2015 Eugene "eaglgenes101" Wang

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

//Because some idiot at Oracle decided that it was fine for the equals method for arrays to be worthless... 
public class PatternWrapper
{
	public static boolean[][] NO_PATTERN = {};
	
	public boolean[][] pattern;
	
	public PatternWrapper(boolean[][] startPattern)
	{
		pattern = startPattern.clone();
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof PatternWrapper)
			return equals((PatternWrapper)other);
		return false;
	}
	
	public boolean equals(PatternWrapper other)
	{
		return Arrays.deepEquals(this.pattern, other.pattern);
	}
	
	public int getW()
	{
		return pattern.length;
	}
	
	public int getH()
	{
		if(pattern == null)
			return 0;
		if(pattern.length == 0)
			return 0;
		return pattern[0].length;
	}
}
