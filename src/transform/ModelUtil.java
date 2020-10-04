package transform;

import com.jme3.asset.AssetManager;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Line;
import com.jme3.util.SafeArrayList;

/**
 * @author Chen Jiongyu
 */
public class ModelUtil {
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;
    public static final int Z_AXIS = 2;
    public static final int XY_PLANE = 3;
    public static final int XZ_PLANE = 4;
    public static final int YZ_PLANE = 5;

    /**
     * A cube object for target practice
     */
    public static Geometry makeCube(AssetManager assetManager, String name, float x, float y, float z) {
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }

    /**
     * A floor to show that the "shot" can go through several objects.
     */
    public static Geometry makeFloor(AssetManager assetManager, float x, float y, float z) {
        Box box = new Box(x, y, z);
        Geometry floor = new Geometry("the Floor", box);
        floor.setLocalTranslation(0, -4, -5);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat1);
        return floor;
    }

    public static Ray makeRayFromCamera(Camera camera, Vector2f coords) {
        Vector3f origin = camera.getWorldCoordinates(coords, 0f);
        Vector3f direction = camera.getWorldCoordinates(coords, 1f);
        direction.subtractLocal(origin).normalizeLocal();
        return new Ray(origin, direction);
    }

    public static void setWorldTranslation(Spatial spatial, Vector3f worldTranslation) {
        spatial.setLocalTranslation(spatial.getParent().worldToLocal(worldTranslation, null));
    }

    public static void setWorldTranslation(Spatial spatial, Vector3f worldTranslation, Camera firstCam,
                                           Camera secondCam) {
        Vector3f sc1 = firstCam.getScreenCoordinates(worldTranslation, null);
        Vector3f _worldTranslation = secondCam.getWorldCoordinates(new Vector2f(sc1.x, sc1.y), 0.5f);
        setWorldTranslation(spatial, _worldTranslation);
    }

    public static Vector3f mapWorldTranslation(Vector3f worldTranslation, Camera firstCam, Camera secondCam) {
        Vector3f sc1 = firstCam.getScreenCoordinates(worldTranslation, null);
        return secondCam.getWorldCoordinates(new Vector2f(sc1.x,
                sc1.y), 0.5f);
    }

    public static int collideWith(Node node, Collidable other, CollisionResults results, boolean invisible) {
        if (invisible) {
            return node.collideWith(other, results);
        }

        int total = 0;
        SafeArrayList<Spatial> children = (SafeArrayList<Spatial>) node.getChildren();
        for (Spatial child : children.getArray()) {
            if (child.getCullHint() != Spatial.CullHint.Always) {
                if (child instanceof Node) {
                    total += collideWith((Node) child, other, results, false);
                } else {
                    total += child.collideWith(other, results);
                }
            }
        }

        return total;
    }

    public static float getLengthInScreen(Vector3f start, Vector3f end, Camera camera) {
        Vector3f startPoint = camera.getScreenCoordinates(start);
        Vector3f endPoint = camera.getScreenCoordinates(end);
        return (float) Math.sqrt(Math.pow((startPoint.x - endPoint.x), 2) + Math.pow((startPoint.y - endPoint.y), 2));
    }

    public static Node drawAxis(int axis, boolean inverse) {
        Vector3f start = Vector3f.ZERO;
        float axisLength = inverse ? -1.5f : 1.5f;
        Vector3f end;
        switch (axis) {
            case X_AXIS:
                end = new Vector3f(axisLength, 0, 0);
                break;
            case Y_AXIS:
                end = new Vector3f(0, axisLength, 0);
                break;
            case Z_AXIS:
                end = new Vector3f(0, 0, axisLength);
                break;
            default:
                throw new IllegalArgumentException("axis must be one of X_AXIS, Y_AXIS, Z_AXIS");
        }

        // draw a line
        Line line = new Line(start, end);
        Geometry lineGeometry = new Geometry("line" + axis, line);
        // draw a cone
        Dome cone = new Dome(Vector3f.ZERO, 2, 32, 0.1f, false);
        Geometry coneGeometry = new Geometry("cone" + axis, cone);
        coneGeometry.setLocalScale(1, 4, 1);
        coneGeometry.setLocalTranslation(end);
        switch (axis) {
            case X_AXIS:
                coneGeometry.rotate(new Quaternion().fromAngleAxis(-FastMath.PI / 2, Vector3f.UNIT_Z));
                if (inverse) {
                    coneGeometry.move(-0.4f, 0, 0);
                }
                break;
            case Y_AXIS:
                if (inverse) {
                    coneGeometry.move(0, -0.4f, 0);
                }
                break;
            case Z_AXIS:
                coneGeometry.rotate(new Quaternion().fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_X));
                if (inverse) {
                    coneGeometry.move(0, 0, -0.4f);
                }
                break;
        }

        Node axisNode = new Node();
        axisNode.attachChild(lineGeometry);
        axisNode.attachChild(coneGeometry);
        return axisNode;
    }

    public static Geometry drawPlane(int plane, boolean negative1, boolean negative2) {
        Vector3f origin;
        float value1 = negative1 ? -0.2f : 0.2f;
        float value2 = negative2 ? -0.2f : 0.2f;
        switch (plane) {
            case XY_PLANE:
                origin = new Vector3f(value1, value2, 0);
                break;
            case XZ_PLANE:
                origin = new Vector3f(value1, 0, value2);
                break;
            case YZ_PLANE:
                origin = new Vector3f(0, value1, value2);
                break;
            default:
                throw new IllegalArgumentException("axis must be one of XY_PLANE, XZ_PLANE, YZ_PLANE");
        }
        Box box = new Box(0.2f, 0.2f, 0.001f);
        Geometry planeGeometry = new Geometry("plane" + plane, box);
        switch (plane) {
            case XZ_PLANE:
                planeGeometry.rotate(new Quaternion().fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_X));
                break;
            case YZ_PLANE:
                planeGeometry.rotate(new Quaternion().fromAngleAxis(-FastMath.PI / 2, Vector3f.UNIT_Y));
                break;
        }
        planeGeometry.setLocalTranslation(origin);
        return planeGeometry;
    }
}
