package base;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.lang3.tuple.Pair;

import objects.PatternEntry;
import objects.PatternInstance;
import objects.PatternWrapper;
import rules.Ruleset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/*
 PatternLife utility methods
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

public class Engine
{

	public static final byte[][] EMPTY_PATTERN = {};

	public static final PatternEntry nullPattern = new PatternEntry(EMPTY_PATTERN);

	public static LinkedList<PatternInstance> cleanList(LinkedList<PatternInstance> toClean)
	{
		PatternInstance nowPatternInstance = null;
		for (ListIterator<PatternInstance> li = toClean.listIterator(); li.hasNext();)
		{
			nowPatternInstance = li.next();
			if (nowPatternInstance.getEntry().equals(nullPattern))
			{
				li.remove();
			}
		}
		return toClean;
	}

	public static Pixmap generatePatternPixmap(PatternEntry basedOn, Color color)
	{
		Pixmap returnPixmap = new Pixmap(basedOn.getWrapper().getW(), basedOn.getWrapper().getH(),
				Pixmap.Format.RGBA8888);
		returnPixmap.setColor(Color.rgba8888(0, 0, 0, 0));
		returnPixmap.fill();
		returnPixmap.setColor(color);

		for (int x = 0; x < basedOn.getWrapper().getW(); x++)
			for (int y = 0; y < basedOn.getWrapper().getH(); y++)
				if (basedOn.getCells()[x][y] != 0)
					returnPixmap.drawPixel(x, y);

		return returnPixmap;
	}

	public static Texture generatePatternTexture(PatternEntry basedOn, Color color)
	{
		Pixmap px = generatePatternPixmap(basedOn, color);
		Texture tx = new Texture(px);
		px.dispose();
		return tx;
	}

	// Returns patterns that are likely to be collisions
	public static TreeSet<TreeSet<PatternInstance>> findCollisions(List<PatternInstance> instanceList, int caseThreshold)
	{
		TreeSet<TreeSet<PatternInstance>> returnSet = new TreeSet<>(new TreeComparator());

		if (instanceList.size() < 2)
			return returnSet; // Our base case

		if (instanceList.size() < caseThreshold)
		{
			PatternInstance currentInstance = null;
			for (ListIterator<PatternInstance> i = instanceList.listIterator(); i.hasNext();)
			{
				currentInstance = i.next();
				PatternInstance otherInstance = null;
				for (ListIterator<PatternInstance> j = instanceList.listIterator(i.nextIndex()); j.hasNext();)
				{
					otherInstance = j.next();
					if (currentInstance != otherInstance
							&& currentInstance.getRectangle().overlaps(otherInstance.getRectangle()))
					{
						returnSet = addPair(returnSet, currentInstance, otherInstance);
					}
				}
			}
			return returnSet; // Our other base case
		}

		Rectangle getRect = instanceList.get(0).getRectangle();
		// Create a super-rectangle that encompasses all the objects
		PatternInstance currentInstance = null;
		for (ListIterator<PatternInstance> i = instanceList.listIterator(1); i.hasNext();)
		{
			currentInstance = i.next();
			getRect = getRect.merge(currentInstance.getRectangle());
		}

		List<PatternInstance> listQ1 = new LinkedList<>();
		List<PatternInstance> listQ2 = new LinkedList<>();
		List<PatternInstance> listQ3 = new LinkedList<>();
		List<PatternInstance> listQ4 = new LinkedList<>();

		Rectangle rectQ1 = new Rectangle(getRect.getX(), getRect.getY(), getRect.getWidth() / 2 + 1,
				getRect.getHeight() / 2 + 1);
		Rectangle rectQ2 = new Rectangle(getRect.getX(), getRect.getY() + getRect.getHeight() / 2 + 1,
				getRect.getWidth() / 2 + 1, getRect.getHeight() / 2 + 1);
		Rectangle rectQ3 = new Rectangle(getRect.getX() + getRect.getWidth() / 2 + 1, getRect.getY()
				+ getRect.getHeight() / 2 + 1, getRect.getWidth() / 2 + 1, getRect.getHeight() / 2 + 1);
		Rectangle rectQ4 = new Rectangle(getRect.getX() + getRect.getWidth() / 2 + 1, getRect.getY(),
				getRect.getWidth() / 2 + 1, getRect.getHeight() / 2 + 1);

		for (ListIterator<PatternInstance> iter = instanceList.listIterator(); iter.hasNext();)
		{
			currentInstance = iter.next();
			if (currentInstance.getRectangle().overlaps(rectQ1))
				listQ1.add(currentInstance);
			if (currentInstance.getRectangle().overlaps(rectQ2))
				listQ2.add(currentInstance);
			if (currentInstance.getRectangle().overlaps(rectQ3))
				listQ3.add(currentInstance);
			if (currentInstance.getRectangle().overlaps(rectQ4))
				listQ4.add(currentInstance);
		}

		for (TreeSet<PatternInstance> t1 : findCollisions(listQ1, caseThreshold*2))
			resolveTree(returnSet, t1);
		for (TreeSet<PatternInstance> t2 : findCollisions(listQ2, caseThreshold*2))
			resolveTree(returnSet, t2);
		for (TreeSet<PatternInstance> t3 : findCollisions(listQ3, caseThreshold*2))
			resolveTree(returnSet, t3);
		for (TreeSet<PatternInstance> t4 : findCollisions(listQ4, caseThreshold*2))
			resolveTree(returnSet, t4);

		return returnSet;

	}

	static TreeSet<TreeSet<PatternInstance>> resolveTree(TreeSet<TreeSet<PatternInstance>> tree,
			TreeSet<PatternInstance> colls)
	{
		TreeSet<PatternInstance> workingTree = new TreeSet<>(new InstanceComparator());
		for (PatternInstance p : colls)
		{
			TreeSet<PatternInstance> onThisTree = null;
			for (TreeSet<PatternInstance> it : tree)
			{
				if (it.contains(p))
				{
					onThisTree = it;
					break;
				}
			}
			
			if (onThisTree == null)
			{
				workingTree.add(p);
			}
			else
			{
				workingTree.addAll(onThisTree);
				tree.remove(onThisTree);
			}
		}
		tree.add(workingTree);
		return tree;
	}

	static TreeSet<TreeSet<PatternInstance>> addPair(TreeSet<TreeSet<PatternInstance>> tree, PatternInstance p1,
			PatternInstance p2)
	{
		TreeSet<PatternInstance> t1 = null;
		TreeSet<PatternInstance> t2 = null;
		for (TreeSet<PatternInstance> aTree : tree)
		{
			if (aTree.contains(p1))
			{
				t1 = aTree;
				break;
			}
		}
		for (TreeSet<PatternInstance> aTree : tree)
		{
			if (aTree.contains(p2))
			{
				t2 = aTree;
				break;
			}
		}

		if (t1 == null && t2 == null)
		{
			TreeSet<PatternInstance> newTree = new TreeSet<>(new InstanceComparator());
			newTree.add(p1);
			newTree.add(p2);
			tree.add(newTree);
		}
		else if (t1 == null && t2 != null)
		{
			t2.add(p1);
		}
		else if (t1 != null && t2 == null)
		{
			t1.add(p2);
		}
		else if (t1 != t2)
		{
			t1.addAll(t2);
			tree.remove(t2);
		}
		return tree;
	}

	static class TreeComparator implements Comparator<TreeSet<PatternInstance>>, Serializable
	{
		public int compare(TreeSet<PatternInstance> t1, TreeSet<PatternInstance> t2)
		{
			InstanceComparator i = new InstanceComparator();
			PatternInstance v1 = t1.first();
			PatternInstance v2 = t2.first();
			return i.compare(v1, v2);
		}
	}

	static class InstanceComparator implements Comparator<PatternInstance>, Serializable
	{
		public int compare(PatternInstance p1, PatternInstance p2)
		{
			EntryComparator e = new EntryComparator();
			if (e.compare(p1.getEntry(), p2.getEntry()) != 0)
			{
				return e.compare(p1.getEntry(), p2.getEntry());
			}
			if (p1.getX() > p2.getX())
				return 1;
			if (p1.getX() < p2.getX())
				return -1;
			if (p1.getY() > p2.getY())
				return 1;
			if (p1.getY() < p2.getY())
				return -1;
			return 0;
		}
	}

	static public class EntryComparator implements Comparator<PatternEntry>, Serializable
	{
		public int compare(PatternEntry p1, PatternEntry p2)
		{
			if (p1 == null && p2 == null)
				return 0;
			else if (p1 == null)
				return 1;
			else if (p2 == null)
				return -1;
			else if (p1.getWrapper().getW() != p2.getWrapper().getW())
				return p1.getWrapper().getW() - p2.getWrapper().getW();
			else if (p1.getWrapper().getH() != p2.getWrapper().getH())
				return p1.getWrapper().getH() - p2.getWrapper().getH();
			else if (!Arrays.deepEquals(p1.getCells(), p2.getCells()))
			{
				for (int x = 0; x < p1.getWrapper().getW(); x++)
				{
					for (int y = 0; y < p1.getWrapper().getH(); y++)
					{
						if (p1.getCells()[x][y] > p2.getCells()[x][y])
							return 1;
						if (p1.getCells()[x][y] < p2.getCells()[x][y])
							return -1;
					}
				}
			}
			return 0;
		}
	}

	static LinkedList<PatternInstance> massMerge(TreeSet<TreeSet<PatternInstance>> collisions,
			LinkedList<PatternInstance> instances, ReferenceMap<PatternWrapper, PatternEntry> knownPatterns,
			Ruleset rule)
	{
		PatternInstance currentInstances = null;
		for (ListIterator<PatternInstance> i = instances.listIterator(); i.hasNext();)
		{
			currentInstances = i.next();
			boolean shouldRemove = false;
			for (TreeSet<PatternInstance> groups : collisions)
			{
				if (groups.contains(currentInstances))
				{
					shouldRemove = true;
					break;
				}
			}
			if (shouldRemove)
				i.remove();
		}

		// If this blows up in my face, I'm not surprised.

		for (TreeSet<PatternInstance> group : collisions)
		{
			PatternInstance seed = group.first();
			for (PatternInstance pattern : group)
			{
				seed = seed.merge(knownPatterns, pattern);
			}
			PatternInstance[] parts = seed.segment(knownPatterns, rule);
			for (PatternInstance part : parts)
			{
				instances.add(part);
			}
		}

		return instances;

	}

	public static byte getByte(byte[][] array, int xCoord, int yCoord)
	{
		if (xCoord < 0 || xCoord >= array.length)
			return 0;
		if (yCoord < 0 || yCoord >= array[0].length)
			return 0;
		return array[xCoord][yCoord];
	}

	public static boolean isActive(boolean[][] array, int xCoord, int yCoord)
	{
		if (xCoord < 0 || xCoord >= array.length)
			return false;
		if (yCoord < 0 || yCoord >= array[0].length)
			return false;
		return array[xCoord][yCoord];
	}
}
