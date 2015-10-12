package objects;

import java.util.Arrays;
import java.util.Random;

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
	
	public int hashCode()
	{
		//Variation on the Zobrist hash. 
		//Yes, I'm lazy. So is everyone else. 
		Random r = new Random(65536*getW()+getH());
		int returnInt = 0;
		for (int x = 0; x < getW(); x++)
		{
			for (int y = 0; y < getH(); y++)
			{
				int n = r.nextInt();
				if (pattern[x][y])
					returnInt = returnInt ^ n;
			}
		}
		return returnInt;
	}
	
	public boolean equals(PatternWrapper other)
	{
		return Arrays.deepEquals(this.pattern, other.pattern);
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof PatternWrapper)
			return this.equals((PatternWrapper)other);
		return false;
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
