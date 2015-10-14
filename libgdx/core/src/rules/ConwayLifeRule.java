package rules;

import base.Engine;
import objects.PatternWrapper;

public class ConwayLifeRule extends Ruleset
{
	public int[][] neighborField = { {-3, -3}, {-3, -2}, {-3, -1},
			{-3, 0}, {-3, 1}, {-3, 2}, {-3, 3}, {-2, -3}, {-2, -2}, {-2, -1},
			{-2, 0}, {-2, 1}, {-2, 2}, {-2, 3}, {-1, -3}, {-1, -2}, {-1, -1},
			{-1, 0}, {-1, 1}, {-1, 2}, {-1, 3}, {0, -3}, {0, -2}, {0, -1},
			{0, 0}, {0, 1}, {0, 2}, {0, 3}, {1, -3}, {1, -2}, {1, -1}, {1, 0},
			{1, 1}, {1, 2}, {1, 3}, {2, -3}, {2, -2}, {2, -1}, {2, 0}, {2, 1},
			{2, 2}, {2, 3}, {3, -3}, {3, -2}, {3, -1}, {3, 0}, {3, 1}, {3, 2},
			{3, 3} };

	@Override
	public boolean[][] apply(PatternWrapper cells)
	{
		boolean[][] returnCells = new boolean[cells.getW()+2][cells.getH()+2];
		for (int x = 0; x < returnCells.length; x++)
			for (int y = 0; y < returnCells[0].length; y++)
			{
				int counter = 0;
				if(Engine.isActive(cells.pattern, x, y))
					counter++;
				if(Engine.isActive(cells.pattern, x+1, y))
					counter++;
				if(Engine.isActive(cells.pattern, x+2, y))
					counter++;
				if(Engine.isActive(cells.pattern, x, y+1))
					counter++;
				if(Engine.isActive(cells.pattern, x+2, y+1))
					counter++;
				if(Engine.isActive(cells.pattern, x, y+2))
					counter++;
				if(Engine.isActive(cells.pattern, x+1, y+2))
					counter++;
				if(Engine.isActive(cells.pattern, x+2, y+2))
					counter++;
				if (counter == 3)
					returnCells[x][y] = true;
				if (counter == 2 && Engine.isActive(cells.pattern, x+1, y+1))
					returnCells[x][y] = true;
			}
		return returnCells;
	}
}
