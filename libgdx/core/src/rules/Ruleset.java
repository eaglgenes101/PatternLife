package rules;

import java.util.function.Function;

import objects.PatternWrapper;

public abstract class Ruleset implements Function<PatternWrapper, boolean[][]>
{
	public int[][] neighborField;
}
