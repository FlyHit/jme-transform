package transform.translate;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import transform.ModelUtil;
import transform.RawInputAdapter;

/**
 * translate an object in the 3D space
 *
 * @author Chen Jiongyu
 */
public class Translate3D extends SimpleApplication {
    private Geometry cube;
    private Geometry pickGeometry;
    private Vector3f pointStart;
    private Vector3f pointEnd;
    private boolean mouseDown = false;

    public static void main(String[] args) {
        Translate3D app = new Translate3D();
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
        cam.setParallelProjection(false);
        cam.setLocation(new Vector3f(-15, 8f, 15));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                if (mouseDown) {
                    // 1. calculating collision result takes time, so when the mouse moves too fast,
                    // the object may not be able to follow the mouse. To solve this, use previous collision result
                    // 2. when we move an object with mouse, its z coordinate is supposed to be immobile, so we use
                    // its z coordinate directly
                    pointEnd = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                            cam.getScreenCoordinates(pickGeometry.getWorldTranslation()).z);
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
                        pickGeometry = results.getClosestCollision().getGeometry();
                        pointStart = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                                cam.getScreenCoordinates(pickGeometry.getWorldTranslation()).z);
                    }
                }
            }
        });

        cube = ModelUtil.makeCube(assetManager, "cube", 1, 0, 0);
        rootNode.attachChild(cube);
        rootNode.attachChild(ModelUtil.makeFloor(assetManager, 10, 0.2f, 10));
    }
}
