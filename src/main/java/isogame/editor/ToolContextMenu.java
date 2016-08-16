package isogame.editor;

import isogame.engine.AssetType;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ToolContextMenu extends ContextMenu {
	public ToolContextMenu(
		LibraryPane library, AssetType type, String id, boolean isGlobal
	) {
		super();

		MenuItem edit = new MenuItem("Edit Sprite");
		edit.setOnAction(event -> {
			switch (type) {
				case TEXTURE: library.editTexture(id); break;
				case SPRITE: library.editSprite(id); break;
				case CLIFF_TEXTURE: library.editCliffTexture(id); break;
			}
		});

		MenuItem global = new MenuItem("Make global");
		global.setOnAction(event -> {
			switch (type) {
				case TEXTURE: library.makeTextureGlobal(id); break;
				case SPRITE: library.makeSpriteGlobal(id); break;
				case CLIFF_TEXTURE: library.makeCliffTextureGlobal(id); break;
			}
		});

		MenuItem delete = new MenuItem("Delete");
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

