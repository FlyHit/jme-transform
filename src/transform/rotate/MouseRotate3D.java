package transform.rotate;

import com.jme3.app.SimpleApplication;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import transform.ModelUtil;
import transform.RawInputAdapter;

/**
 * @author Chen Jiongyu
 */
public class MouseRotate3D extends SimpleApplication {
    private Spatial cube;
    private boolean mouseDown = false;
    private float tpf;
    private Vector3f pointStart;
    private Vector3f pointEnd;

    public static void main(String[] args) {
        MouseRotate3D app = new MouseRotate3D();
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
        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                if (mouseDown) {
                    pointEnd = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                            cam.getScreenCoordinates(cube.getWorldTranslation()).z);
                    Vector3f eye = cam.getLocation().subtract(cube.getWorldTranslation()).normalize();
                    Vector3f offset = pointEnd.subtract(pointStart);
                    Vector3f rotationAxis = offset.cross(eye).normalize();
                    float rotationSpeed = 20f / cube.getWorldTranslation().distance(cam.getLocation());
                    float rotationAngle = offset.dot(rotationAxis.clone().cross(eye)) * rotationSpeed * 0.6f;
                    rotationAxis = cube.getParent().getWorldRotation().inverse().mult(rotationAxis);
                    Quaternion quaternion = cube.getWorldRotation().clone();
                    Quaternion quaternion1 =
                            cube.getParent().getWorldRotation().inverse().mult(new Quaternion().fromAngleAxis(rotationAngle, rotationAxis).mult(quaternion));
                    cube.setLocalRotation(quaternion1);
                    pointStart = pointEnd;
                }
            }

            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                if (evt.getButtonIndex() == 0) {
                    mouseDown = evt.isPressed();
                    if (mouseDown) {
                        pointStart = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                                cam.getScreenCoordinates(cube.getWorldTranslation()).z);
                    }
                }
            }
        });

        Node node = new Node();
        cube = ModelUtil.makeCube(assetManager, "cube", 0, 0, 0);
        cube.setLocalTranslation(0, 0, 3);
        cube.rotate(1, 3, 2);
        node.setLocalTranslation(2, 1, 3);
        node.rotate(1, 3, 3);
        node.attachChild(cube);
        rootNode.attachChild(node);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
    }

    @Override
    public void simpleUpdate(float tpf) {
        this.tpf = tpf;
//        Vector3f worldTranslation = cube.getWorldTranslation();
//        Vector3f origin = cam.getScreenCoordinates(worldTranslation);
//        Vector3f xEnd = cam.getWorldCoordinates(new Vector2f(origin.x + 1000, origin.y), origin.z);
//        Vector3f yEnd = cam.getWorldCoordinates(new Vector2f(origin.x, origin.y + 1000), origin.z);
//        Vector3f horizontalAxis = xEnd.subtract(worldTranslation);
//        Vector3f verticalAxis = yEnd.subtract(worldTranslation);
//        cube.rotate(new Quaternion().fromAngleAxis(tpf, verticalAxis));
    }
}






