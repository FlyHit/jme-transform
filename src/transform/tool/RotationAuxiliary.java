package transform.tool;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import transform.shape.Circle3d;
import transform.shape.fill.ShapeFiller;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * an tool that helps indicating rotation angle
 *
 * @author Chen Jiongyu
 */
public class RotationAuxiliary {
    private static final float RADIUS = 1.5f;
    private static final ColorRGBA FILL_COLOR = new ColorRGBA(121f / 255, 125f / 255, 127f / 255, 0.5f);
    private final Node node;
    private final Geometry circleGeo;
    private final ShapeFiller[] baseShapeFiller;
    private final AssetManager assetManager;
    private final List<ShapeFiller> tempFillers;
    private Geometry startLine;
    private Geometry endLine;
    private ShapeFiller shapeFiller;
    private float angle;
    private float deltaAngle;
    private Vector3f startPoint = new Vector3f();
    private int currentCircleNum;
    private ColorRGBA borderColor;
    private Material baseMat;

    public RotationAuxiliary(@Nonnull AssetManager assetManager, @Nonnull ColorRGBA color) {
        this.assetManager = assetManager;
        this.borderColor = color;
        tempFillers = new ArrayList<>();
        initMaterial();
        node = new Node();
        Circle3d circle = new Circle3d(Vector3f.ZERO, RADIUS, 64);
        circleGeo = new Geometry("circle", circle);
        circleGeo.setMaterial(baseMat);
        circleGeo.rotate(FastMath.PI / 2, 0, 0);
        node.attachChild(circleGeo);
        baseShapeFiller = new ShapeFiller[3];
        for (int i = 0; i < baseShapeFiller.length; i++) {
            baseShapeFiller[i] = makeFiller(360, FILL_COLOR);
        }
    }

    private void initMaterial() {
        baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseMat.setColor("Color", borderColor);
    }

    public Vector3f getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Vector3f startPoint) {
        this.startPoint = startPoint;
        if (startLine != null) {
            node.detachChild(startLine);
        }

        if (endLine != null) {
            node.detachChild(endLine);
        }

        startLine = makeLine(startPoint, baseMat);
        startLine.getMaterial().getAdditionalRenderState().setLineWidth(1);
        endLine = makeLine(startPoint, baseMat);
        node.attachChild(startLine);
        node.attachChild(endLine);

        // reset
        angle = 0;
        deltaAngle = 0;
    }

    private Geometry makeLine(Vector3f end, Material material) {
        Line line = new Line(Vector3f.ZERO, node.worldToLocal(end, null).normalize().mult(1.5f));
        Geometry lineGeo = new Geometry("line", line);
        lineGeo.setMaterial(material);
        return lineGeo;
    }

    public void setDeltaAngle(float deltaAngle) {
        endLine.rotate(0, 0, deltaAngle);
        angle += deltaAngle;
    }

    private ShapeFiller makeFiller(float angle, ColorRGBA innerColor) {
        ShapeFiller shapeFiller = new ShapeFiller(assetManager, RADIUS, RADIUS, angle, 64, "MatDefs/QuadDrawing1" +
                "/QuadDrawing.j3md", RenderQueue.Bucket.Transparent, ShapeFiller.Shape.Circle);
        shapeFiller.setEdgeWidth(0);
        shapeFiller.setInnerColor(innerColor);
        shapeFiller.getGeom().setLocalTranslation(-RADIUS, -RADIUS, 0);
        return shapeFiller;
    }

    public Node getNode() {
        return node;
    }

    public void setColor(ColorRGBA color) {
        this.borderColor = color;
        for (ShapeFiller filler : baseShapeFiller) {
            filler.setOuterColor(color);
        }
        if (shapeFiller != null) {
            shapeFiller.setOuterColor(color);
        }
    }
}
