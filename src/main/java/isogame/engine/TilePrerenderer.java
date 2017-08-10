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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

class TilePrerenderer {
	private static final double[] xs = new double[6];
	private static final double[] ys = new double[6];
	private static final SnapshotParameters sp = new SnapshotParameters();

	private static double canvasW = TILEW + 8;
	private static double canvasH = TILEH + (TILEH / 2) + 8;

	public static final double OFFSETX = 4;
	public static final double OFFSETY = 4 + (TILEH / 2);

	public static Map<SlopeType, Image> prerenderTile(final Paint texture) {
		sp.setFill(Color.TRANSPARENT);
		final Canvas canvas = new Canvas(canvasW, canvasH);
		final GraphicsContext cx = canvas.getGraphicsContext2D();
		cx.translate(OFFSETX, OFFSETY);

		final Map<SlopeType, Image> r = new HashMap<>();

		// NONE
		xs[0] = TILEW / 2; ys[0] = -2;
		xs[1] = TILEW + 4; ys[1] = TILEH / 2;
		xs[2] = TILEW / 2; ys[2] = TILEH + 2;
		xs[3] = -4;        ys[3] = TILEH / 2;
		cx.setFill(texture);
		cx.fillPolygon(xs, ys, 4);
		r.put(SlopeType.NONE, canvas.snapshot(sp, null));

		// N
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
		xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
		xs[2] = TILEW + 4; ys[2] = 0;
		xs[3] = TILEW / 2; ys[3] = TILEH + 4;
		cx.setFill(texture);
		cx.fillPolygon(xs, ys, 4);
		r.put(SlopeType.N, canvas.snapshot(sp, null));

		// E
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = -4;        ys[0] = (TILEH / 2) + 2;
		xs[1] = TILEW / 2; ys[1] = -2;
		xs[2] = TILEW + 4; ys[2] = -2;
		xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
		cx.setFill(texture);
		cx.fillPolygon(xs, ys, 4);
		r.put(SlopeType.E, canvas.snapshot(sp, null));

		// S
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = -4;        ys[0] = -2;
		xs[1] = TILEW / 2; ys[1] = -2;
		xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
		xs[3] = TILEW / 2; ys[3] = (TILEH / 2) + 2;
		cx.setFill(texture);
		cx.fillPolygon(xs, ys, 4);
		r.put(SlopeType.S, canvas.snapshot(sp, null));

		// W
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = -4;        ys[0] = 0;
		xs[1] = TILEW / 2; ys[1] = 0 - (TILEH / 2) - 2;
		xs[2] = TILEW + 4; ys[2] = (TILEH / 2) + 2;
		xs[3] = TILEW / 2; ys[3] = TILEH + 4;
		cx.setFill(texture);
		cx.fillPolygon(xs, ys, 4);
		r.put(SlopeType.W, canvas.snapshot(sp, null));

		return r;
	}

	public static Map<SlopeType, Image> prerenderCliff(
		final Function<SlopeType, Paint> texture
	) {
		sp.setFill(Color.TRANSPARENT);
		final Canvas canvas = new Canvas(canvasW, canvasH);
		final GraphicsContext cx = canvas.getGraphicsContext2D();
		cx.translate(OFFSETX, OFFSETY);

		final Map<SlopeType, Image> r = new HashMap<>();

		// NONE
		xs[0] = 0;         ys[0] = 0;
		xs[1] = 0;         ys[1] = (TILEH / 2) + 2;
		xs[2] = TILEW / 2; ys[2] = TILEH + 2;
		xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
		xs[4] = TILEW;     ys[4] = 0;
		xs[5] = TILEW / 2; ys[5] = TILEH / 2;
		cx.setFill(texture.apply(SlopeType.NONE));
		cx.fillPolygon(xs, ys, 6);
		r.put(SlopeType.NONE, canvas.snapshot(sp, null));

		// N
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = TILEW / 2; ys[0] = TILEH;
		xs[1] = TILEW;     ys[1] = 0;
		xs[2] = TILEW;     ys[2] = (TILEH / 2) + 2;
		xs[3] = TILEW / 2; ys[3] = TILEH + 2;
		cx.setFill(texture.apply(SlopeType.N));
		cx.fillPolygon(xs, ys, 4);
		r.put(SlopeType.N, canvas.snapshot(sp, null));

		// E
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = 0;         ys[0] = TILEH / 2;
		xs[1] = TILEW / 2; ys[1] = TILEH / 2;
		xs[2] = TILEW;     ys[2] = 0;
		xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
		xs[4] = TILEW / 2; ys[4] = TILEH + 2;
		xs[5] = 0;         ys[5] = (TILEH / 2) + 2;
		cx.setFill(texture.apply(SlopeType.E));
		cx.fillPolygon(xs, ys, 6);
		r.put(SlopeType.E, canvas.snapshot(sp, null));

		// S
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = 0;         ys[0] = 0;
		xs[1] = TILEW / 2; ys[1] = TILEH / 2;
		xs[2] = TILEW;     ys[2] = TILEH / 2;
		xs[3] = TILEW;     ys[3] = (TILEH / 2) + 2;
		xs[4] = TILEW / 2; ys[4] = TILEH + 2;
		xs[5] = 0;         ys[5] = (TILEH / 2) + 2;
		cx.setFill(texture.apply(SlopeType.S));
		cx.fillPolygon(xs, ys, 6);
		r.put(SlopeType.S, canvas.snapshot(sp, null));

		// W
		cx.clearRect(-OFFSETX, -OFFSETY, canvasW, canvasH);
		xs[0] = 0;         ys[0] = 0;
		xs[1] = TILEW / 2; ys[1] = TILEH;
		xs[2] = TILEW / 2; ys[2] = TILEH + 2;
		xs[3] = 0;         ys[3] = (TILEH / 2) + 2;
		cx.setFill(texture.apply(SlopeType.W));
		cx.fillPolygon(xs, ys, 4);
		r.put(SlopeType.W, canvas.snapshot(sp, null));

		return r;
	}
}

