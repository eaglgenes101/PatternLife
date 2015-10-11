package objects;

import java.util.Arrays;

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
