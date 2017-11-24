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
package isogame.editor;

import isogame.engine.AssetType;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ToolContextMenu extends ContextMenu {
	public ToolContextMenu(
		final LibraryPane library,
		final AssetType type,
		final String id,
		final boolean isGlobal
	) {
		super();

		final MenuItem edit = new MenuItem("Edit " + type.toString());
		edit.setOnAction(event -> {
			switch (type) {
				case TEXTURE: library.editTexture(id); break;
				case SPRITE: library.editSprite(id); break;
				case CLIFF_TEXTURE: library.editCliffTexture(id); break;
			}
		});

		final MenuItem global = new MenuItem("Make global");
		global.setOnAction(event -> {
			switch (type) {
				case TEXTURE: library.makeTextureGlobal(id); break;
				case SPRITE: library.makeSpriteGlobal(id); break;
				case CLIFF_TEXTURE: library.makeCliffTextureGlobal(id); break;
			}
		});

		final MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(event -> {
			// TODO: check that the texture is not in use
			switch (type) {
				case TEXTURE: library.deleteTexture(id); break;
				case SPRITE: library.deleteSprite(id); break;
				case CLIFF_TEXTURE: library.deleteCliffTexture(id); break;
			}
		});

		if (isGlobal) {
			this.getItems().add(edit);
		} else {
			this.getItems().addAll(edit, global, delete);
		}
	}
}

