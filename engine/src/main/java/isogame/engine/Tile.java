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

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;
import ssjsjs.annotations.As;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;
import static isogame.engine.TilePrerenderer.OFFSETX;
import static isogame.engine.TilePrerenderer.OFFSETY;
import static isogame.GlobalConstants.TILEH;
import static isogame.GlobalConstants.TILEW;

/**
 * Represents a single tile in a stage.
 * */
public class Tile extends VisibleObject implements JSONable {
	public final int elevation;
	public final TerrainTexture tex;
	public final Optional<CliffTexture> cliffTexture;
	public final SlopeType slope;
	public final boolean isManaZone;
	public final StartZoneType startZone;
	public final MapPoint pos;

	public final List<Point2D> shapeUL;
	public final List<Point2D> shapeUR;
	public final List<Point2D> shapeLL;
	public final List<Point2D> shapeLR;

	private final boolean even;

	private final Text debugText = new Text();
	private final static Font debugFont = new Font(TILEH / 2);

	public final PrioritizedGroup subGraph;
	private Optional<Shape> highlightNode = Optional.empty();
	private Optional<Paint> highlightColor = Optional.empty();

	public Object userData = null;

	public Tile(final MapPoint p, final TerrainTexture texture) {
		this(p, 0, SlopeType.NONE, false, StartZoneType.NONE,
			texture, null, new PrioritizedGroup(PrioritizedGroup.TILE));
	}

	public Tile(
		final int elevation,
		final SlopeType slope,
		final TerrainTexture texture,
		final Optional<CliffTexture> cliffTexture
	) {
		this(new MapPoint(0, 0), elevation, slope,
			false, StartZoneType.NONE, texture, cliffTexture,
			new PrioritizedGroup(PrioritizedGroup.TILE));
	}

	private final String textureID;
	private final Optional<String> cliffTextureID;

	@JSON
	public Tile(
		@Implicit("library") final Library lib,
		@Field("pos")@As("p") final MapPoint pos,
		@Field("elevation") final int elevation,
		@Field("slope") final SlopeType slope,
		@Field("isManaZone") final boolean isManaZone,
		@Field("startZone") final StartZoneType startZone,
		@Field("textureID")@As("texture") final String textureID,
		@Field("cliffTextureID")@As("cliffTexture") final Optional<String> cliffTextureID
	) throws CorruptDataException {
		this(pos, elevation, slope, isManaZone, startZone, lib.getTerrain(textureID),
			cliffTextureID.isPresent()?
				Optional.of(lib.getCliffTexture(cliffTextureID.get())) : Optional.empty(),
			new PrioritizedGroup(PrioritizedGroup.TILE));
	} 

	/**
	 * @param pos the position
	 * @param elevation the elevation
	 * @param slope the slope type
	 * @param isManaZone true if this is a mana zone, otherwise false
	 * @param startZone the start zone type
	 * @param texture the main texture
	 * @param cliffTexture the cliff texture
	 * @param subGraph the child nodes of this node in the scene graph
	 * */
	public Tile(
		final MapPoint pos,
		final int elevation,
		final SlopeType slope,
		final boolean isManaZone,
		final StartZoneType startZone,
		final TerrainTexture texture,
		final Optional<CliffTexture> cliffTexture,
		final PrioritizedGroup subGraph
	) {
		this.elevation = elevation;
		this.pos = pos;

		this.textureID = texture.id;
		this.cliffTextureID = cliffTexture.map(x -> x.id);

		tex = texture;
		even = (pos.x + pos.y) % 2 == 0;

		this.cliffTexture = cliffTexture;

		this.slope = slope;
		this.isManaZone = isManaZone;
		this.startZone = startZone;

		this.subGraph = subGraph;
		this.subGraph.setCache(true);

		final StringBuilder debug = new StringBuilder();
		if (isManaZone) debug.append("M");
		if (isManaZone && startZone != StartZoneType.NONE) debug.append(", ");
		if (startZone != StartZoneType.NONE)
			debug.append(startZone == StartZoneType.PLAYER? "S1" : "S2");
		debugText.setText(debug.toString());

		debugText.setX(0);
		debugText.setY(TILEH / 2);
		debugText.setWrappingWidth(TILEW);
		debugText.setFont(debugFont);
		debugText.setTextAlignment(TextAlignment.CENTER);
		debugText.setFill(Color.RED);

		if (slope == SlopeType.NONE) {
			this.shapeUL = generateShape(CameraAngle.UL);
			this.shapeUR = this.shapeUL;
			this.shapeLL = this.shapeUL;
			this.shapeLR = this.shapeUL;
		} else {
			this.shapeUL = generateShape(CameraAngle.UL);
			this.shapeUR = generateShape(CameraAngle.UR);
			this.shapeLL = generateShape(CameraAngle.LL);
			this.shapeLR = generateShape(CameraAngle.LR);
		}
	}

	/**
	 * Get a string describing the special properties of this tile.
	 * @return May be null
	 * */
	public String specialStatusString() {
		String r;
		switch (startZone) {
			case PLAYER: r = "1"; break;
			case AI: r = "2"; break;
			default: r = null;
		}

		if (isManaZone) {
			if (r == null) r = "M"; else r += "M"; 
		}

		return r;
	}

	/**
	 * Make a new tile with a different texture.
	 * @param tex the texture for the new tile
	 * @return a new Tile which is a clone of this one but with a different texture
	 * */
	public Tile newTexture(final TerrainTexture tex) {
		return new Tile(pos, elevation, slope,
			isManaZone, startZone, tex, cliffTexture, subGraph);
	}

	/**
	 * Make a new tile with different elevation characteristics.
	 * @param elevation the new elevation
	 * @param slope the new slope type
	 * @param cliffTexture the new cliff type
	 * @return a new Tile which is a clone of this one but with different
	 * elevation characteristics
	 * */
	public Tile newElevation(
		final int elevation,
		final SlopeType slope,
		final CliffTexture cliffTexture
	) {
		return new Tile(pos, elevation, slope,
			isManaZone, startZone, tex, Optional.of(cliffTexture), subGraph);
	}

	/**
	 * Make a new tile with a different mana zone property.
	 * @param isManaZone true if this is a mana zone, otherwise false
	 * @return a new Tile which is a clone of this one but the mana zone property
	 * is different
	 * */
	public Tile newManaZone(final boolean isManaZone) {
		return new Tile(pos, elevation, slope,
			isManaZone, startZone, tex, cliffTexture, subGraph);
	}

	/**
	 * Make a new tile with a different start zone type.
	 * @param startZone the kind of start zone
	 * @return a new Tile which is a clone of this one but it's a different kind
	 * of start zone
	 * */
	public Tile newStartZone(final StartZoneType startZone) {
		return new Tile(pos, elevation, slope,
			isManaZone, startZone, tex, cliffTexture, subGraph);
	}

	/**
	 * Make a new tile with the mana zone type and start zone type cleared
	 * @return a new Tile which is a clone of this one but the mana zone and
	 * start zone types are cleared.
	 * */
	public Tile clearSpecialProperties() {
		return new Tile(pos, elevation, slope,
			false, StartZoneType.NONE, tex, cliffTexture, subGraph);
	}

	/**
	 * Get the visual slope type for this Tile given a particular camera angle
	 * @param angle the camera angle
	 * @return the visual slope type given the camera angle
	 * */
	public SlopeType adjustSlopeForCameraAngle(final CameraAngle angle) {
		int s;
		int d;

		switch (slope) {
			case N: s = 0; break;
			case E: s = 1; break;
			case S: s = 2; break;
			case W: s = 3; break;
			case NONE: return SlopeType.NONE;
			default: throw new RuntimeException(
				"Invalid slope type, this cannot happen");
		}

		switch (angle) {
			case UL: d = 0; break;
			case LL: d = 1; break;
			case LR: d = 2; break;
			case UR: d = 3; break;
			default: throw new RuntimeException(
				"Invalid camera angle, this cannot happen");
		}

		switch ((s + d) % 4) {
			case 0: return SlopeType.N;
			case 1: return SlopeType.E;
			case 2: return SlopeType.S;
			case 3: return SlopeType.W;
			default: throw new RuntimeException(
				"Computed invalid slope type, this cannot happen");
		}
	}

	/**
	 * Render this tile at (0,0).  If you need to draw the tile somewhere else,
	 * do a translation before calling this method.
	 * @param cx the graphics context
	 * @param angle the current camera angle
	 * */
	public void render(
		final GraphicsContext cx,
		final CameraAngle angle
	) {
		final SlopeType slope = adjustSlopeForCameraAngle(angle);

		cx.drawImage(tex.getTexture(even, slope), -OFFSETX, -OFFSETY);
		if (slope != SlopeType.NONE && cliffTexture.isPresent()) {
			cx.drawImage(cliffTexture.get().getPreTexture(slope), -OFFSETX, -OFFSETY);
		}

		if (elevation != 0 && cliffTexture.isPresent()) {
			final Image epaint = cliffTexture.get().getPreTexture(SlopeType.NONE);
			for (int i = 0; i < elevation; i++) {
				cx.translate(0, TILEH / 2);
				cx.drawImage(epaint, -OFFSETX, -OFFSETY);
			}
		}
	}

	/**
	 * Get the index of this Tile in a list of scene graph nodes
	 * @param graph the scene graph nodes to examine
	 * @param priority the priority of the element to get the index for
	 * @return a valid index into the scene graph nodes, or -1 if it is empty
	 * */
	public int getSceneGraphIndex(
		final ObservableList<Node> graph, final int priority
	) {
		final int base = graph.indexOf(subGraph);
		if (priority == PrioritizedGroup.TILE) return base;
		int i = base + 1;
		while (i < graph.size() && graph.get(i) instanceof PrioritizedGroup) {
			final PrioritizedGroup n = (PrioritizedGroup) graph.get(i);
			if (n.priority == PrioritizedGroup.TILE || priority < n.priority) {
				break;
			}

			i += 1;
		}
		return i - 1;
	}

	/**
	 * Rebuild this part of the scenegraph
	 * @param isDebug true to show extra debugging information
	 * @param angle the current camera angle
	 * */
	public void rebuildSceneGraph(
		final ObservableBooleanValue isDebug,
		final CameraAngle angle
	) {
		final ObservableList<Node> graph = subGraph.getChildren();
		graph.clear();
		highlightNode = Optional.empty();

		final SlopeType slope = adjustSlopeForCameraAngle(angle);

		final ImageView base = new ImageView(tex.getTexture(even, slope));
		base.setClip(getHighlightShape(angle));
		base.setX(-OFFSETX);
		base.setY(-OFFSETY);
		graph.add(base);

		if (slope != SlopeType.NONE && cliffTexture.isPresent()) {
			final ImageView cliff = new ImageView(cliffTexture.get().getPreTexture(slope));
			cliff.setX(-OFFSETX);
			cliff.setY(-OFFSETY);
			base.setClip(getHighlightShape(angle));
			graph.add(cliff);
		}

		if (elevation != 0 && cliffTexture.isPresent()) {
			final Image epaint = cliffTexture.get().getPreTexture(SlopeType.NONE);
			for (int i = 1; i <= elevation; i++) {
				final ImageView cliff2 = new ImageView(epaint);
				cliff2.setX(-OFFSETX);
				cliff2.setY(-OFFSETY + (i * (TILEH / 2)));
				base.setClip(getHighlightShape(angle));
				graph.add(cliff2);
			}
		}

		debugText.visibleProperty().bind(isDebug);
		graph.add(debugText);

		setHighlight0(angle);

		onChange.accept(subGraph);
	}

	/**
	 * Get the shape of a highlighter for this Tile
	 * @param angle the current camera angle
	 * @return the shape of a highlighter for this Tile
	 * */
	private Polygon getHighlightShape(final CameraAngle angle) {
		final List<Point2D> shape;
		switch (angle) {
			case UL: shape = shapeUL; break;
			case UR: shape = shapeUR; break;
			case LL: shape = shapeLL; break;
			case LR: shape = shapeLR; break;
			default:
				throw new RuntimeException("Invalid angle, this cannot happen");
		}

		final double[] pts = new double[shape.size() * 2];
		for (int i = 0; i < shape.size(); i++) {
			pts[i * 2] = shape.get(i).getX();
			pts[(i * 2) + 1] = shape.get(i).getY();
		}

		return new Polygon(pts);
	}

	/**
	 * Update the highlight color for this tile
	 * @param highlight The highlight color
	 * */
	public void setHighlight(
		final CameraAngle angle,
		final Optional<Paint> highlight
	) {
		this.highlightColor = highlight;
		setHighlight0(angle);
	}

	/**
	 * Create or destroy the highlight node as necessary.
	 * @param angle the current camera angle
	 * */
	private void setHighlight0(final CameraAngle angle) {
		final ObservableList<Node> graph = subGraph.getChildren();

		if (highlightColor.isPresent()) {
			final Shape n = highlightNode.orElseGet(() -> {
				final Shape r = getHighlightShape(angle);
				r.setCache(true);

				// the highlighter goes in the second to last position, so that it
				// always appears behind the debug text
				graph.add(graph.size() - 1, r);
				highlightNode = Optional.of(r);
				return r;
			});

			n.setFill(highlightColor.get());
		} else {
			highlightNode.ifPresent(n -> {
				graph.remove(n);
				highlightNode = Optional.empty();
			});
		}
	}

	/**
	 * Get the shape of this tile.
	 * @param a the current camera angle
	 * @return A list of points representing the outline of this tile
	 * */
	private List<Point2D> generateShape(final CameraAngle a) {
		final double extension = (TILEH * ((double) elevation)) / 2;
		final List<Point2D> r = new ArrayList<>();
		switch (adjustSlopeForCameraAngle(a)) {
			case NONE:
				if (elevation == 0) {
					r.add(new Point2D(TILEW / 2, -2       ));
					r.add(new Point2D(TILEW + 4, TILEH / 2));
					r.add(new Point2D(TILEW / 2, TILEH + 2));
					r.add(new Point2D(-4       , TILEH / 2));
				} else if (elevation > 0) {
					r.add(new Point2D(TILEW / 2, -2                         ));
					r.add(new Point2D(TILEW + 4,  TILEH / 2                 ));
					r.add(new Point2D(TILEW + 4, (TILEH / 2) + extension + 2));
					r.add(new Point2D(TILEW / 2,       TILEH + extension + 2));
					r.add(new Point2D(-4       , (TILEH / 2) + extension + 2));
					r.add(new Point2D(-4       ,  TILEH / 2                 ));
				} else {
					throw new RuntimeException("Negative elevation not supported");
				}
				break;
			case N:
				r.add(new Point2D(-4       ,     (TILEH / 2) + 2        ));
				r.add(new Point2D(TILEW / 2, 0 - (TILEH / 2) - 2        ));
				r.add(new Point2D(TILEW + 4, 0                          ));
				r.add(new Point2D(TILEW + 4, (TILEH / 2) + extension + 2));
				r.add(new Point2D(TILEW / 2,       TILEH + extension + 4));
				r.add(new Point2D(-4       , (TILEH / 2) + extension + 2));
				break;
			case E:
				r.add(new Point2D(-4       , (TILEH / 2) + 2            ));
				r.add(new Point2D(TILEW / 2, -2                         ));
				r.add(new Point2D(TILEW + 4, -2                         ));
				r.add(new Point2D(TILEW + 4, (TILEH / 2) + extension + 2));
				r.add(new Point2D(TILEW / 2,       TILEH + extension + 2));
				r.add(new Point2D(-4       , (TILEH / 2) + extension + 2));
				break;
			case S:
				r.add(new Point2D(-4       , -2                         ));
				r.add(new Point2D(TILEW / 2, -2                         ));
				r.add(new Point2D(TILEW + 4, (TILEH / 2) + 2            ));
				r.add(new Point2D(TILEW + 4, (TILEH / 2) + extension + 2));
				r.add(new Point2D(TILEW / 2,       TILEH + extension + 2));
				r.add(new Point2D(-4       , (TILEH / 2) + extension + 2));
				break;
			case W:
				r.add(new Point2D(-4       , 0                          ));
				r.add(new Point2D(TILEW / 2, 0 - (TILEH / 2) - 2        ));
				r.add(new Point2D(TILEW + 4,     (TILEH / 2) + 2        ));
				r.add(new Point2D(TILEW + 4, (TILEH / 2) + extension + 2));
				r.add(new Point2D(TILEW / 2,       TILEH + extension + 4));
				r.add(new Point2D(-4       , (TILEH / 2) + extension + 2));
				break;
			default: throw new RuntimeException(
				"Invalid slope type. This cannot happen");
		}

		return r;
	}

	@Override public String toString() {
		return pos.toString();
	}
}

