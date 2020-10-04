package transform.translate;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import transform.ModelUtil;
import transform.RawInputAdapter;

/**
 * translate an object along X, Y or Z axis
 *
 * @author Chen Jiongyu
 */
public class TranslateAxis extends SimpleApplication {
    private Geometry cube;
    private Geometry floor;
    private Geometry pickGeometry;
    private Vector3f pointStart;
    private Vector3f pointEnd;
    private boolean mouseDown = false;
    private Mode mode;

    public static void main(String[] args) {
        TranslateAxis app = new TranslateAxis();
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
        rootNode.attachChild(floor);
        mode = Mode.X;

        inputManager.addRawInputListener(new RawInputAdapter() {

            @Override
            public void onKeyEvent(KeyInputEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyInput.KEY_X:
                        mode = Mode.X;
                        break;
                    case KeyInput.KEY_Y:
                        mode = Mode.Y;
                        break;
                    case KeyInput.KEY_Z:
                        mode = Mode.Z;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                if (mouseDown) {
                    pointEnd = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                            cam.getScreenCoordinates(pickGeometry.getWorldTranslation()).z);
                    Vector3f offset = pointEnd.subtract(pointStart);
                    switch (mode) {
                        case X:
                            offset.y = 0;
                            offset.z = 0;
                            break;
                        case Y:
                            offset.x = 0;
                            offset.z = 0;
                            break;
                        case Z:
                            offset.x = 0;
                            offset.y = 0;
                            break;
                    }
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
                        pickGeometry = results.getClosestCollision().getGeometry();
                        pointStart = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                                cam.getScreenCoordinates(pickGeometry.getWorldTranslation()).z);
                    }
                }
            }
        });
    }

    private enum Mode {
        X,
        Y,
        Z
    }
}
