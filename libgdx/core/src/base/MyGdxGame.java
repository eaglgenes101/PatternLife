package base;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.function.Function;

import org.apache.commons.collections4.map.*;
import org.apache.commons.lang3.tuple.Pair;

import objects.PatternEntry;
import objects.PatternInstance;
import objects.PatternWrapper;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

/*
The LibGDX "main" method
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

public class MyGdxGame implements ApplicationListener
{

	LinkedList<PatternInstance> currentPatterns;

	ReferenceMap<PatternWrapper, PatternEntry> knownPatterns;

	Stage stage;

	SpriteBatch batch;

	private static class conwayLifeRule implements
			Function<boolean[][], Boolean>
	{
		public Boolean apply(boolean[][] t)
		{
			int counter = 0;
			if (t[0][0])
				counter++;
			if (t[0][1])
				counter++;
			if (t[0][2])
				counter++;
			if (t[1][2])
				counter++;
			if (t[2][2])
				counter++;
			if (t[2][1])
				counter++;
			if (t[2][0])
				counter++;
			if (t[1][0])
				counter++;

			if (counter == 2 && t[1][1])
				return true;
			if (counter == 3)
				return true;
			return false;

		}
	};

	private static class highLifeRule implements Function<boolean[][], Boolean>
	{
		public Boolean apply(boolean[][] t)
		{
			int counter = 0;
			if (t[0][0])
				counter++;
			if (t[0][1])
				counter++;
			if (t[0][2])
				counter++;
			if (t[1][2])
				counter++;
			if (t[2][2])
				counter++;
			if (t[2][1])
				counter++;
			if (t[2][0])
				counter++;
			if (t[1][0])
				counter++;

			if (counter == 2 && t[1][1])
				return true;
			if (counter == 3)
				return true;
			if (counter == 6 && !t[1][1])
				return true;
			return false;

		}
	};

	private static class moveRule implements Function<boolean[][], Boolean>
	{
		public Boolean apply(boolean[][] t)
		{
			int counter = 0;
			if (t[0][0])
				counter++;
			if (t[0][1])
				counter++;
			if (t[0][2])
				counter++;
			if (t[1][2])
				counter++;
			if (t[2][2])
				counter++;
			if (t[2][1])
				counter++;
			if (t[2][0])
				counter++;
			if (t[1][0])
				counter++;

			if ((counter == 2 || counter == 4 || counter == 5) && t[1][1])
				return true;
			if ((counter == 3 || counter == 6 || counter == 8) && !t[1][1])
				return true;
			return false;

		}
	};

	boolean[][] GOSPER_GLIDER_CELLS = { {true, false, true},
			{false, true, true}, {false, true, false}};

	boolean[][] R_PENTOMINO = { {false, true, true}, {true, true, false},
			{false, true, false}};

	boolean[][] PENTADECATHON_GRANDPARENT = { {true}, {true}, {true}, {true},
			{true}, {true}, {true}, {true}, {true}, {true}};

	boolean[][] MOVE_PUFFER = { {true, false}, {false, true}, {false, true},
			{false, true}, {false, true}, {true, false}};

	boolean[][] BLOCK_LAYING_SWITCH_ENGINE = { {true, true, true, false, true},
			{true, false, false, false, false},
			{false, false, false, true, true},
			{false, true, true, false, true}, {true, false, true, false, true}};

	boolean[][] HIGHLIFE_REPLICATOR = { {true, true, true, false, false},
			{true, false, false, true, false},
			{true, false, false, false, true},
			{false, true, false, false, true}, {false, false, true, true, true}};

	boolean[][] GLIDERS_X_THE_DOZEN = { {true, true, false, false, true},
			{true, false, false, false, true}, {true, false, false, true, true}};

	boolean[][] EMPTY_PATTERN = {};

	@Override
	public void create()
	{
		currentPatterns = new LinkedList<PatternInstance>();
		knownPatterns = new ReferenceMap<>(
				AbstractReferenceMap.ReferenceStrength.SOFT,
				AbstractReferenceMap.ReferenceStrength.SOFT);
		currentPatterns.add(new PatternInstance(600, -100, BLOCK_LAYING_SWITCH_ENGINE,
				knownPatterns));
		batch = new SpriteBatch();
	}

	@Override
	public void dispose()
	{
		batch.dispose();
	}

	@Override
	public void render()
	{
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT); // clears the buffer
		batch.begin();
		LinkedList<PatternInstance> newList = new LinkedList<>();

		for (ListIterator<PatternInstance> iter = currentPatterns
				.listIterator(); iter.hasNext();)
		{
			PatternInstance currentPatternInstance = iter.next();
			PatternInstance[] successors = currentPatternInstance.step(
					new conwayLifeRule(), knownPatterns);
			for (PatternInstance i : successors)
				newList.add(i);

			batch.draw(
					Engine.generatePatternTexture(
							currentPatternInstance.getEntry(), Color.WHITE),
					currentPatternInstance.getX(),
					-currentPatternInstance.getY()
							- currentPatternInstance.getRectangle().getHeight());
		}
		batch.end();
		currentPatterns = newList; // We can afford to be sloppy, the garbage
									// collector will do it.

		TreeSet<Pair<PatternInstance, PatternInstance>> collisionList = Engine
				.findCollisions(newList, 5);
		currentPatterns = Engine.cleanList(Engine.massMerge(collisionList,
				newList, knownPatterns));

		System.out.println(Runtime.getRuntime().freeMemory()); 
		
		//System.gc();

	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
}