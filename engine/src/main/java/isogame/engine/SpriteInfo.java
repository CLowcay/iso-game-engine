/* © Callum Lowcay 2015, 2016

This file is part of iso-game-engine.

iso-game-engine is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

iso-game-engine is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with iso-game-engine.  If not, see <http://www.gnu.org/licenses/>.

*/
package isogame.engine;

import isogame.resource.ResourceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ssjsjs.annotations.As;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

/**
 * Information that is shared between all sprites of the same kind.
 * */
public class SpriteInfo implements JSONable {
	public final Map<String, SpriteAnimation> animations;
	private final List<SpriteAnimation> animationsOrdered;

	public final SpriteAnimation defaultAnimation;
	public final String id;

	// sprites are rendered in order of priority starting with 0
	public final int priority;

	public SpriteInfo(
		final String id, final int priority, final SpriteAnimation defaultAnimation
	) throws CorruptDataException {
		if (defaultAnimation == null)
			throw new CorruptDataException("No animations define for sprite " + id);
		this.id = id;
		this.priority = priority;
		this.defaultAnimation = defaultAnimation;
		animations = new HashMap<>();
		animationsOrdered = new ArrayList<>();
		if (defaultAnimation != null) addAnimation(defaultAnimation);
	}

	@JSONConstructor
	public SpriteInfo(
		@Implicit("locator") final ResourceLocator loc,
		@Field("id") final String id,
		@Field("priority") final int priority,
		@Field("animationsOrdered")@As("animations") final List<SpriteAnimation> animations
	) throws CorruptDataException {
		this(id, priority, animations.size() < 1 ? null : animations.get(0));
		animations.remove(0);
		for (final SpriteAnimation animation : animations) this.addAnimation(animation);
	}

	/**
	 * Get all the animations defined for this sprite.
	 * @return a Collection containing all the animations for this sprite
	 * */
	public Collection<SpriteAnimation> getAllAnimations() {
		return animations.values();
	}

	/**
	 * Add an animation to this sprite.
	 * @param animation the animation to add
	 * */
	public void addAnimation(final SpriteAnimation animation) {
		animations.put(animation.id, animation);
		animationsOrdered.add(animation);
	}

	@Override public String toString() {
		return "Sprite: " + id + ":" + priority;
	}
}

