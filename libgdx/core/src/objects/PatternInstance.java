package objects;

import java.util.function.Function;

import org.apache.commons.collections4.map.ReferenceMap;

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

	public PatternInstance(int startX, int startY, PatternEntry startBase)
	{
		x = startX;
		y = startY;
		basedOn = startBase;
	}

	public PatternInstance(int startX, int startY, boolean[][] startCells,
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
	public PatternInstance[] step(Function<boolean[][], Boolean> ruleset,
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
	
	public PatternInstance[] segment(ReferenceMap<PatternWrapper, PatternEntry> lookUpMap)
	{
		PatternEntry[] parts = basedOn.segment();
		PatternInstance[] returnArray = new PatternInstance[parts.length];
		for (int i = 0; i < parts.length; i++)
		{
			int[] offsets = parts[i].trim();
			returnArray[i] = new PatternInstance(x+offsets[0], y+offsets[1], parts[i]);
		}
		return returnArray;
	}

	// When two PatternInstances collide, use this to merge them
	// 
	public PatternInstance merge(
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap,
			PatternInstance other)
	{
		Rectangle nextRectangle = getRectangle().merge(other.getRectangle());
		boolean[][] combinedCells = new boolean[Math.round(nextRectangle.getWidth())][Math.round(nextRectangle
				.getHeight())];
		int thisOffsetX = x - Math.round(nextRectangle.getX());
		int thisOffsetY = y - Math.round(nextRectangle.getY());
		int otherOffsetX = other.getX() - Math.round(nextRectangle.getX());
		int otherOffsetY = other.getY() - Math.round(nextRectangle.getY());

		for (int x = 0; x < Math.round(getRectangle().getWidth())-2; x++)
			for (int y = 0; y < Math.round(getRectangle().getHeight())-2; y++)
			{
				if (basedOn.getCells()[x][y])
					combinedCells[x + thisOffsetX][y + thisOffsetY] = true;
			}

		for (int x = 0; x < Math.round(other.getRectangle().getWidth())-2; x++)
			for (int y = 0; y < Math.round(other.getRectangle().getHeight())-2; y++)
			{
				if (other.getEntry().getCells()[x][y])
					combinedCells[x + otherOffsetX][y + otherOffsetY] = true;
			}

		return new PatternInstance(
				Math.round(nextRectangle.getX()), Math.round(nextRectangle.getY()),
				combinedCells, lookUpMap);

	}

}
