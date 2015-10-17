package base;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import org.apache.commons.collections4.map.*;
import org.apache.commons.lang3.tuple.Pair;

import objects.PatternEntry;
import objects.PatternInstance;
import objects.PatternWrapper;
import rules.ConwayLifeRule;
import rules.Ruleset;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

	SpriteBatch batch;
	
	boolean willStep = true;

	int genCounter;

	Viewport viewport;

	byte[][] GOSPER_GLIDER_CELLS = { {1, 0, 1}, {0, 1, 1}, {0, 1, 0}};

	byte[][] R_PENTOMINO = { {0, 1, 1}, {1, 1, 0}, {0, 1, 0}};

	byte[][] PENTADECATHON_GRANDPARENT = { {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}};

	byte[][] MOVE_PUFFER = { {1, 0}, {0, 1}, {0, 1}, {0, 1}, {0, 1}, {1, 0}};

	byte[][] BLOCK_LAYING_SWITCH_ENGINE = { {1, 1, 1, 0, 1}, {1, 0, 0, 0, 0}, {0, 0, 0, 1, 1}, {0, 1, 1, 0, 1},
			{1, 0, 1, 0, 1}};

	byte[][] HIGHLIFE_REPLICATOR = { {1, 1, 1, 0, 0}, {1, 0, 0, 1, 0}, {1, 0, 0, 0, 1}, {0, 1, 0, 0, 1},
			{0, 0, 1, 1, 1}};

	byte[][] GLIDERS_X_THE_DOZEN = { {1, 1, 0, 0, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 1, 1}};

	byte[][] EMPTY_PATTERN = {{}};
	
	byte[][] ONE_CELL = {{1}};

	Ruleset actingRule = new ConwayLifeRule();

	@Override
	public void create()
	{
		currentPatterns = new LinkedList<PatternInstance>();
		knownPatterns = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.SOFT,
				AbstractReferenceMap.ReferenceStrength.SOFT);
		currentPatterns.add(new PatternInstance(400, -200, R_PENTOMINO, knownPatterns));
		batch = new SpriteBatch();
		genCounter = 0;

		viewport = new ScreenViewport();
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
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
		{
			willStep = !willStep;
		}
		
		if (Gdx.input.isTouched())
		{
			currentPatterns.add(new PatternInstance(Gdx.input.getX(), Gdx.input.getY()-Gdx.graphics.getHeight(), ONE_CELL, knownPatterns));
		}

		for (ListIterator<PatternInstance> iter = currentPatterns.listIterator(); iter.hasNext();)
		{
			PatternInstance currentPatternInstance = iter.next();
			if (willStep)
			{
				PatternInstance[] successors = currentPatternInstance.step(actingRule, knownPatterns);
				for (PatternInstance i : successors)
					newList.add(i);
			}
			else
			{
				newList.add(currentPatternInstance);
			}

			Texture tx = Engine.generatePatternTexture(currentPatternInstance.getEntry(), Color.WHITE);

			batch.draw(tx, currentPatternInstance.getX(), -currentPatternInstance.getY()
					- currentPatternInstance.getRectangle().getHeight());
			
		}
		batch.end();
		
		currentPatterns = newList; // We can afford to be sloppy, the garbage
									// collector will do it.

		TreeSet<TreeSet<PatternInstance>> collisionList = Engine.findCollisions(newList, 5);
		currentPatterns = Engine.cleanList(Engine.massMerge(collisionList, newList, knownPatterns, actingRule));

		genCounter++;

		// System.gc();

	}

	@Override
	public void resize(int width, int height)
	{
		viewport.update(width, height, true);
		viewport.apply(true);
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