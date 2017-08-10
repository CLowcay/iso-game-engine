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

import isogame.engine.CorruptDataException;
import isogame.engine.MapPoint;
import isogame.engine.StageInfo;
import isogame.engine.TerrainTexture;
import isogame.engine.Tile;
import isogame.gui.PositiveIntegerField;


import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Dialog box to create new terrain textures
 * */
public class NewMapDialog extends Dialog<StageInfo> {
	public NewMapDialog(final TerrainTexture blank) {
		super();

		// Set up the header and footer
		this.setTitle("New map");
		this.setHeaderText("Set the dimensions of the map");
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// The dialog content
		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		final PositiveIntegerField width = new PositiveIntegerField();
		final PositiveIntegerField height = new PositiveIntegerField();

		grid.add(new Label("Width"), 0, 0);
		grid.add(width, 1, 0);

		grid.add(new Label("Height"), 0, 1);
		grid.add(height, 1, 1);

		this.getDialogPane().setContent(grid);

		this.setResultConverter(clickedButton -> {
			if (clickedButton == ButtonType.OK) {
				final int w = width.getValue();
				final int h = height.getValue();
				if (w == 0 || h == 0) return null;

				final Tile[] tiles = new Tile[w * h];
				for (int y = 0; y < w; y++) {
					for (int x = 0; x < h; x++) {
						tiles[(y * w) + x] = new Tile(new MapPoint(x, y), blank);
					}
				}

				try {
					return new StageInfo(w, h, tiles);
				} catch (CorruptDataException e) {
					return null;
				}
			} else {
				return null;
			}
		});
	}
}


