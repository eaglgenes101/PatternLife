package objects;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

import org.apache.commons.lang3.*;
import org.apache.commons.collections4.map.*;

import rules.Ruleset;
import base.Engine;
import base.Engine.EntryComparator;

/*
 PatternLife pattern entries
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

public class PatternEntry
{

	PatternWrapper cells;

	PatternEntry[] nextPatterns;

	int[] nextXOffsets;

	int[] nextYOffsets;

	public PatternEntry(boolean[][] cellPattern)
	{
		cells = new PatternWrapper(cellPattern);
		nextPatterns = null; // We haven't found this yet, but we will in good
								// time
	}

	public int hashCode()
	{
		return cells.hashCode();
	}

	public boolean equals(PatternEntry p)
	{
		return this.cells.equals(p.getWrapper());
	}

	public boolean equals(Object other)
	{
		if (other instanceof PatternEntry)
			return this.equals((PatternEntry) other);
		return false;
	}

	// We've trimmed already, see if we can get an entry!
	public PatternEntry tryEnter(
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap)
	{
		if (lookUpMap.containsKey(cells))
		{
			return lookUpMap.get(cells);
		}
		lookUpMap.put(cells, this);
		return this;
	}

	// Returns itself if it is in one piece
	// Returns its components if it can be split into multiple patterns
	public PatternEntry[] segment(Ruleset rule)
	{
		if (cells.getW() == 0 || cells.getH() == 0)
		{
			PatternEntry[] returnArray = {};
			return returnArray;
		}
		int startX = 0;
		int startY = 0;
		for (int x = 0; x < cells.getW(); x++)
			for (int y = 0; y < cells.getH(); y++)
				if (cells.pattern[x][y])
				{
					startX = x;
					startY = y;
					break;
				}
		boolean[][] isPart = new boolean[cells.getW()][cells.getH()];
		Queue<int[]> knownPoints = new LinkedList<int[]>();
		int[] starterPoint = {startX, startY};
		knownPoints.add(starterPoint);
		isPart[startX][startY] = true;
		while (!knownPoints.isEmpty())
		{
			int[] pt = knownPoints.poll();
			for (int[] dP : rule.neighborField)
			{
				int[] thispt = {pt[0] + dP[0], pt[1] + dP[1]};
				if (Engine.isActive(cells.pattern, thispt[0], thispt[1])
						&& !Engine.isActive(isPart, thispt[0], thispt[1]))
				{
					knownPoints.add(thispt);
					isPart[thispt[0]][thispt[1]] = true;
				}
			}
		}

		boolean canSplit = false;
		for (int x = 0; x < cells.getW(); x++)
			for (int y = 0; y < cells.getH(); y++)
				if (cells.pattern[x][y] && !isPart[x][y]) // There are active
															// cells not covered
															// by this
															// segmentation
					canSplit = true;

		if (canSplit)
		{
			boolean[][] cellsOne = new boolean[cells.getW()][cells.getH()];
			boolean[][] cellsTwo = new boolean[cells.getW()][cells.getH()];
			for (int x = 0; x < cells.getW(); x++)
				for (int y = 0; y < cells.getH(); y++)
				{
					if (cells.pattern[x][y] && isPart[x][y])
						cellsOne[x][y] = true;
					if (cells.pattern[x][y] && !isPart[x][y])
						cellsTwo[x][y] = true;
				}
			PatternEntry componentPatternOne = new PatternEntry(cellsOne);
			PatternEntry[] getOtherParts = new PatternEntry(cellsTwo).segment(rule);
			return ArrayUtils.addAll(getOtherParts, componentPatternOne);
		}

		PatternEntry[] returnArray = {this};
		return returnArray;

	}

	// Find the next generation of this pattern.
	// Calling this more than once is allowed, but redundant
	private void obtainNext(Ruleset ruleset,
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap)
	{
		boolean[][] nextCells = ruleset.apply(getWrapper());

		// Run it through processing for cleanup
		PatternEntry candidatePattern = new PatternEntry(nextCells);
		nextPatterns = candidatePattern.segment(ruleset);
		nextXOffsets = new int[nextPatterns.length];
		nextYOffsets = new int[nextPatterns.length];
		for (int i = 0; i < nextPatterns.length; i++)
		{
			int[] gottenOffsets = nextPatterns[i].trim();
			nextXOffsets[i] = gottenOffsets[0] - 1; // I wouldn't be surprised
													// if an off-by-1 error
													// emerged here
			nextYOffsets[i] = gottenOffsets[1] - 1;
			nextPatterns[i] = nextPatterns[i].tryEnter(lookUpMap); // It's
																	// trimmed,
																	// let's
																	// enter it
																	// now!
		}
		return;

	}

	public PatternEntry[] findNext(Ruleset ruleset,
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap)
	{
		if (nextPatterns == null)
		{
			obtainNext(ruleset, lookUpMap);
		}
		return this.nextPatterns;
	}

	public int[] getXOffsets()
	{
		return nextXOffsets;
	}

	public int[] getYOffsets()
	{
		return nextYOffsets;
	}

	public boolean[][] getCells()
	{
		return cells.pattern.clone();
	}

	public PatternWrapper getWrapper()
	{
		return cells;
	}

	// This has to be called right when a patternEntry is constructed
	// otherwise, we'll break the cellular automaton system
	// Returns how much of an offset the caller should give as an array
	public int[] trim()
	{
		int bottomTrim = 0; // This is how the pattern could possibly grow
		int topTrim = 0;
		int leftTrim = 0;
		int rightTrim = 0;

		boolean canBottomTrim = true;
		boolean canTopTrim = true;
		boolean canLeftTrim = true;
		boolean canRightTrim = true;

		while (canBottomTrim && bottomTrim < cells.getH())
		{
			for (int x = 0; x < cells.getW(); x++)
				if (cells.pattern[x][bottomTrim])
					canBottomTrim = false;
			if (canBottomTrim)
				bottomTrim++;
		}

		while (canTopTrim && bottomTrim + topTrim < cells.getH())
		{
			for (int x = 0; x < cells.getW(); x++)
				if (cells.pattern[x][cells.getH() - 1 - topTrim])
					canTopTrim = false;
			if (canTopTrim)
				topTrim++;
		}

		while (canLeftTrim && leftTrim < cells.getW())
		{
			for (int y = 0; y < cells.getH(); y++)
				if (cells.pattern[leftTrim][y])
					canLeftTrim = false;
			if (canLeftTrim)
				leftTrim++;
		}

		while (canRightTrim && leftTrim + rightTrim < cells.getW())
		{
			for (int y = 0; y < cells.getH(); y++)
				if (cells.pattern[cells.getW() - 1 - rightTrim][y])
					canRightTrim = false;
			if (canRightTrim)
				rightTrim++;
		}

		if (!(bottomTrim == 0 && topTrim == 0 && leftTrim == 0 && rightTrim == 0))
		{
			boolean[][] newCell = new boolean[cells.getW() - leftTrim
					- rightTrim][cells.getH() - bottomTrim - topTrim];
			for (int x = leftTrim; x < cells.getW() - rightTrim; x++)
				for (int y = bottomTrim; y < cells.getH() - topTrim; y++)
					newCell[x - leftTrim][y - bottomTrim] = cells.pattern[x][y];
			cells = new PatternWrapper(newCell);
		}

		int[] returnArray = {leftTrim, bottomTrim};
		return returnArray;

	}
}
