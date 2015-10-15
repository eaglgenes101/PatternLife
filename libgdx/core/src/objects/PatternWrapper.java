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
	public byte[][] pattern;
	
	public PatternWrapper(byte[][] startPattern)
	{
		pattern = startPattern.clone();
	}
	
	public PatternWrapper(PatternWrapper pw)
	{
		pattern = pw.pattern.clone();
	}
	
	public int hashCode()
	{
		//Variation on the Zobrist hash. 
		//Yes, I'm lazy. So is everyone else.
		long currentSeed = 65536*getW()+getH();
		Random r1 = new Random(currentSeed+0);
		Random r2 = new Random(currentSeed+1);
		Random r3 = new Random(currentSeed+2);
		Random r4 = new Random(currentSeed+3);
		Random r5 = new Random(currentSeed+4);
		Random r6 = new Random(currentSeed+5);
		Random r7 = new Random(currentSeed+6);
		Random r8 = new Random(currentSeed+7);
		int returnInt = 0;
		for (int x = 0; x < getW(); x++)
		{
			for (int y = 0; y < getH(); y++)
			{
				int n1 = r1.nextInt();
				int n2 = r2.nextInt();
				int n3 = r3.nextInt();
				int n4 = r4.nextInt();
				int n5 = r5.nextInt();
				int n6 = r6.nextInt();
				int n7 = r7.nextInt();
				int n8 = r8.nextInt();
				if ((pattern[x][y]&1)!= 0)
					returnInt = returnInt ^ n1;
				if ((pattern[x][y]&2)!= 0)
					returnInt = returnInt ^ n2;
				if ((pattern[x][y]&4)!= 0)
					returnInt = returnInt ^ n3;
				if ((pattern[x][y]&8)!= 0)
					returnInt = returnInt ^ n4;
				if ((pattern[x][y]&16)!= 0)
					returnInt = returnInt ^ n5;
				if ((pattern[x][y]&32)!= 0)
					returnInt = returnInt ^ n6;
				if ((pattern[x][y]&64)!= 0)
					returnInt = returnInt ^ n7;
				if ((pattern[x][y]&128)!= 0)
					returnInt = returnInt ^ n8;
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
