package objects;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.map.ReferenceMap;

import rules.Ruleset;
import base.Engine;

import com.badlogic.gdx.math.Rectangle;

/*
PatternLife pattern instances
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

public class PatternInstance
{
	PatternEntry basedOn;

	int x;

	int y;
	
	public int hashCode()
	{
		return basedOn.hashCode()^x^y;
	}

	public PatternInstance(int startX, int startY, PatternEntry startBase)
	{
		x = startX;
		y = startY;
		basedOn = startBase;
	}

	public PatternInstance(int startX, int startY, byte[][] startCells,
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap)
	{
		PatternEntry basedPattern = new PatternEntry(startCells);
		int[] offsets = basedPattern.trim();
		x = startX + offsets[0];
		y = startY + offsets[1];
		basedOn = basedPattern.tryEnter(lookUpMap);
	}

	// Returns next generation if it doesn't split, multiple new instantiations
	// if it does
	public PatternInstance[] step(Ruleset ruleset,
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap)
	{
		PatternEntry[] newBase = basedOn.findNext(ruleset, lookUpMap);
		int[] newXOffs = basedOn.getXOffsets();
		int[] newYOffs = basedOn.getYOffsets();
		PatternInstance[] returnArray = new PatternInstance[newBase.length];
		for (int i = 0; i < returnArray.length; i++)
		{
			returnArray[i] = new PatternInstance(newXOffs[i] + x, newYOffs[i]
					+ y, newBase[i]);
		}
		return returnArray;
	}

	public PatternEntry getEntry()
	{
		return basedOn;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Rectangle getRectangle()
	{
		return new Rectangle(x, y, basedOn.cells.getW() + 2,
				basedOn.cells.getH() + 2);
	}
	
	public PatternInstance[] segment(ReferenceMap<PatternWrapper, PatternEntry> lookUpMap, Ruleset rule)
	{
		PatternEntry[] parts = basedOn.segment(rule);
		PatternInstance[] returnArray = new PatternInstance[parts.length];
		for (int i = 0; i < parts.length; i++)
		{
			int[] offsets = parts[i].trim();
			returnArray[i] = new PatternInstance(x+offsets[0], y+offsets[1], parts[i].getCells(), lookUpMap);
		}
		return returnArray;
	}
	
	// Determine definitively if two patterns collide.
	// 
	public boolean collides(Ruleset rule, PatternInstance other)
	{
		int dx = x - other.getX(); 
		int dy = y - other.getY();
		
		Set<int[]> coordinates = new HashSet<int[]>();
		
		//Come up with all possible collision points
		for (int x = 0; x < Math.round(getRectangle().getWidth())-2; x++)
			for (int y = 0; y < Math.round(getRectangle().getHeight())-2; y++)
				if (getEntry().getCells()[x][y] != 0)
				{
					for (int[] diffs : rule.getNeighborField())
					{
						int[] toAdd = {x+diffs[0], y+diffs[1]};
						coordinates.add(toAdd);
					}
				}
		
		for (int[] point : coordinates)
		{
			if (Engine.getByte(other.getEntry().getCells(), point[0]+dx, point[1]+dy) != 0)
				return true;
		}
		
		return false;
		
	}

	// When two PatternInstances collide, use this to merge them
	// 
	public PatternInstance merge(
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap,
			PatternInstance other)
	{
		Rectangle nextRectangle = getRectangle().merge(other.getRectangle());
		byte[][] combinedCells = new byte[Math.round(nextRectangle.getWidth())][Math.round(nextRectangle
				.getHeight())];
		int thisOffsetX = x - Math.round(nextRectangle.getX());
		int thisOffsetY = y - Math.round(nextRectangle.getY());
		int otherOffsetX = other.getX() - Math.round(nextRectangle.getX());
		int otherOffsetY = other.getY() - Math.round(nextRectangle.getY());

		for (int x = 0; x < Math.round(getRectangle().getWidth())-2; x++)
			for (int y = 0; y < Math.round(getRectangle().getHeight())-2; y++)
			{
				if (getEntry().getCells()[x][y]!=0)
					combinedCells[x + thisOffsetX][y + thisOffsetY] = basedOn.getCells()[x][y];
			}

		for (int x = 0; x < Math.round(other.getRectangle().getWidth())-2; x++)
			for (int y = 0; y < Math.round(other.getRectangle().getHeight())-2; y++)
			{
				if (other.getEntry().getCells()[x][y]!=0)
					combinedCells[x + otherOffsetX][y + otherOffsetY] = other.getEntry().getCells()[x][y];
			}

		return new PatternInstance(
				Math.round(nextRectangle.getX()), Math.round(nextRectangle.getY()),
				combinedCells, lookUpMap);

	}

}
