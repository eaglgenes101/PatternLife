package rules;

import base.Engine;
import objects.PatternWrapper;

public class ConwayLifeRule extends Ruleset
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
			}
		return returnCells;
	}
}
