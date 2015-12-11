package base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.map.*;

import objects.FrozenPatternInstance;
import objects.PatternEntry;
import objects.PatternInstance;
import objects.PatternWrapper;
import rules.ConwayLifeRule;
import rules.Ruleset;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
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
	boolean willStep = false;
	boolean oneStep = false;
	ScreenViewport viewport;

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
		currentPatterns.add(new PatternInstance(200, 200-Gdx.graphics.getHeight()-1, R_PENTOMINO, knownPatterns));
		batch = new SpriteBatch();

		viewport = new ScreenViewport();
		viewport.apply(true);
	}

	@Override
	public void dispose()
	{
		batch.dispose();
	}

	@Override
	public void render()
	{
		ArrayList<Texture> textures = new ArrayList<>();
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT); // clears the buffer
		batch.begin();
		LinkedList<PatternInstance> newList = new LinkedList<>();
		
		int x = Gdx.input.getX();
		int y = Gdx.input.getY();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			willStep = !willStep;
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.TAB))
			oneStep = true;
		
		if (Gdx.input.isTouched())
		{
			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
				currentPatterns.add(new FrozenPatternInstance(new PatternInstance(x, y-Gdx.graphics.getHeight()+1, ONE_CELL, knownPatterns)));
			else
				currentPatterns.add(new PatternInstance(x, y-Gdx.graphics.getHeight()+1, ONE_CELL, knownPatterns));
		
		}
		
		for (ListIterator<PatternInstance> iter = currentPatterns.listIterator(); iter.hasNext();)
		{

			PatternInstance currentPatternInstance = iter.next();
			
			if (Gdx.input.isKeyJustPressed(Input.Keys.A))
				currentPatternInstance = new PatternInstance(currentPatternInstance.getX(), currentPatternInstance.getY(), currentPatternInstance.getEntry());
			
			Texture tx = null;
			
			if (currentPatternInstance instanceof FrozenPatternInstance)
				tx = Engine.generatePatternTexture(currentPatternInstance.getEntry(), Color.CYAN);
			else
				tx = Engine.generatePatternTexture(currentPatternInstance.getEntry(), Color.YELLOW);
			
			if (currentPatternInstance.getRectangle().contains(x, y-Gdx.graphics.getHeight()+1))
			{
				tx = Engine.generatePatternTexture(currentPatternInstance.getEntry(), Color.MAGENTA);
				if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE))
				{
					iter.remove();
					continue;
				}
			}
			
			if (willStep || oneStep)
			{
				PatternInstance[] successors = currentPatternInstance.step(actingRule, knownPatterns);
				for (PatternInstance i : successors)
					newList.add(i);
				batch.draw(tx, currentPatternInstance.getX(), -currentPatternInstance.getY()
						- currentPatternInstance.getRectangle().getHeight()+1);
			}
			else
			{
				newList.add(currentPatternInstance);
				batch.draw(tx, currentPatternInstance.getX(), -currentPatternInstance.getY()
						- currentPatternInstance.getRectangle().getHeight()+1);
			}
			
			textures.add(tx);
			
		}
		
		batch.end();
		
		Set<TreeSet<PatternInstance>> collisionSet = Engine.findCollisions(newList, 5);
		
		currentPatterns = Engine.cleanList(Engine.massMerge(collisionSet, newList, knownPatterns, actingRule));
		
		oneStep = false;
		
		for (Texture t : textures)
		{
			if (t != null)
				t.dispose();
		}

	}

	@Override
	public void resize(int width, int height)
	{
		viewport.update(width, height, true);
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