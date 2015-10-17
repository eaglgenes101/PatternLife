package input;

import java.util.LinkedList;

import objects.PatternInstance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

public class HighlightInputHandler implements InputProcessor
{
	
	LinkedList<PatternInstance> ourPatterns;
	
	public HighlightInputHandler(LinkedList<PatternInstance> thisPatterns)
	{
		ourPatterns = thisPatterns;
	}

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		for (PatternInstance inst : ourPatterns)
		{
			if (inst.getRectangle().contains(screenX, screenY-Gdx.graphics.getHeight()+1))
				System.out.println(Math.random());
		}
		return true;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

}
