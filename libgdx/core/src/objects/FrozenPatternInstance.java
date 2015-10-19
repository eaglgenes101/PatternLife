package objects;

import org.apache.commons.collections4.map.ReferenceMap;

import rules.Ruleset;

/*
PatternLife frozen pattern instances
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

public class FrozenPatternInstance extends PatternInstance
{
	public FrozenPatternInstance(PatternInstance basedInstance)
	{
		super(basedInstance.getX(), basedInstance.getY(), basedInstance.getEntry());
	}
	
	public PatternInstance[] step(Ruleset ruleset,
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap)
	{
		PatternInstance[] returnArray = {new FrozenPatternInstance(this)};
		return returnArray;
	}
	
	public PatternInstance merge(
			ReferenceMap<PatternWrapper, PatternEntry> lookUpMap,
			PatternInstance other)
	{
		PatternInstance returnInstance = super.merge(lookUpMap, other);
		if (other instanceof FrozenPatternInstance)
		{
			return new FrozenPatternInstance(returnInstance);
		}
		return returnInstance;
	}
}
