package transform.translate;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import transform.ModelUtil;
import transform.RawInputAdapter;

/**
 * translate an object along XZ, XY or YZ plane
 *
 * @author Chen Jiongyu
 */
public class TranslatePlane extends SimpleApplication {

    private Geometry cube;
    private Geometry floor;
    private Vector3f pointStart;
    private Vector3f pointEnd;
    private boolean mouseDown = false;
    private Plane plane;
    private Vector3f normal;

    public static void main(String[] args) {
        TranslatePlane app = new TranslatePlane();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(-15, 8f, 15));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        cube = ModelUtil.makeCube(assetManager, "cube", 1, 0, 0);
        rootNode.attachChild(cube);
        floor = ModelUtil.makeFloor(assetManager, 10, .2f, 10);
        normal = Vector3f.UNIT_Y;
        rootNode.attachChild(floor);

        inputManager.addRawInputListener(new RawInputAdapter() {

            @Override
            public void onKeyEvent(KeyInputEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyInput.KEY_X:
                        rootNode.detachChild(floor);
                        floor = ModelUtil.makeFloor(assetManager, .2f, 10, 10);
                        rootNode.attachChild(floor);
                        normal = Vector3f.UNIT_X;
                        break;
                    case KeyInput.KEY_Y:
                        rootNode.detachChild(floor);
                        floor = ModelUtil.makeFloor(assetManager, 10, .2f, 10);
                        rootNode.attachChild(floor);
                        normal = Vector3f.UNIT_Y;
                        break;
                    case KeyInput.KEY_Z:
                        rootNode.detachChild(floor);
                        floor = ModelUtil.makeFloor(assetManager, 10, 10, .2f);
                        rootNode.attachChild(floor);
                        normal = Vector3f.UNIT_Z;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                if (mouseDown) {
                    Ray ray = ModelUtil.makeRayFromCamera(getCamera(), inputManager.getCursorPosition());
                    pointEnd = new Vector3f();
                    ray.intersectsWherePlane(plane, pointEnd);
                    Vector3f offset = pointEnd.subtract(pointStart);
                    cube.move(offset);
                    pointStart = pointEnd;
                }
            }

            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                if (evt.getButtonIndex() == 0) {
                    mouseDown = evt.isPressed();
                    if (mouseDown) {
                        CollisionResults results = new CollisionResults();
                        Ray ray = ModelUtil.makeRayFromCamera(getCamera(), inputManager.getCursorPosition());
                        cube.collideWith(ray, results);
                        if (results.size() == 0) {
                            return;
                        }
                        CollisionResult closet = results.getClosestCollision();
                        pointStart = closet.getContactPoint();
                        plane = new Plane(normal, pointStart);
                    }
                }
            }
        });
    }
}
