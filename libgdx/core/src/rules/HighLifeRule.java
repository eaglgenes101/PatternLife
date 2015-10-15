package rules;

import base.Engine;
import objects.PatternWrapper;

/*
HighLife rule
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

public class HighLifeRule extends Ruleset
{
	
	@Override
	public int[][] getNeighborField()
	{
		int[][] returnField = { {-3, -3}, {-3, -2}, {-3, -1},
				{-3, 0}, {-3, 1}, {-3, 2}, {-3, 3}, {-2, -3}, {-2, -2}, {-2, -1},
				{-2, 0}, {-2, 1}, {-2, 2}, {-2, 3}, {-1, -3}, {-1, -2}, {-1, -1},
				{-1, 0}, {-1, 1}, {-1, 2}, {-1, 3}, {0, -3}, {0, -2}, {0, -1},
				{0, 0}, {0, 1}, {0, 2}, {0, 3}, {1, -3}, {1, -2}, {1, -1}, {1, 0},
				{1, 1}, {1, 2}, {1, 3}, {2, -3}, {2, -2}, {2, -1}, {2, 0}, {2, 1},
				{2, 2}, {2, 3}, {3, -3}, {3, -2}, {3, -1}, {3, 0}, {3, 1}, {3, 2},
				{3, 3} };
		return returnField;
	}

	@Override
	public byte[][] apply(PatternWrapper cells)
	{
		byte[][] returnCells = new byte[cells.getW()+2][cells.getH()+2];
		for (int x = -1; x < cells.getW()+1; x++)
			for (int y = -1; y < cells.getH()+1; y++)
			{
				int counter = 0;
				if(Engine.getByte(cells.pattern, x-1, y-1)!=0)
					counter++;
				if(Engine.getByte(cells.pattern, x, y-1)!=0)
					counter++;
				if(Engine.getByte(cells.pattern, x+1, y-1)!=0)
					counter++;
				if(Engine.getByte(cells.pattern, x-1, y)!=0)
					counter++;
				if(Engine.getByte(cells.pattern, x+1, y)!=0)
					counter++;
				if(Engine.getByte(cells.pattern, x-1, y+1)!=0)
					counter++;
				if(Engine.getByte(cells.pattern, x, y+1)!=0)
					counter++;
				if(Engine.getByte(cells.pattern, x+1, y+1)!=0)
					counter++;
				if (counter == 3)
					returnCells[x+1][y+1] = 1;
				if (counter == 2 && Engine.getByte(cells.pattern, x, y)!=0)
					returnCells[x+1][y+1] = 1;
				if (counter == 6 && Engine.getByte(cells.pattern, x, y)==0)
					returnCells[x+1][y+1] = 1;
			}
		return returnCells;
	}
}
