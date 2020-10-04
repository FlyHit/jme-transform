package transform.shape;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import transform.RawInputAdapter;

/**
 * @author Chen Jiongyu
 */
public class CircleTest extends SimpleApplication {
    public static void main(String[] args) {
        CircleTest app = new CircleTest();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.setSamples(16);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Circle circle = new Circle(Vector3f.ZERO, 360, 5, 60, true);
        Geometry geometry = new Geometry("circle", circle);
//        geometry.rotate(FastMath.HALF_PI, 0, 0);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        geometry.setMaterial(mat1);
        rootNode.attachChild(geometry);
        ChaseCamera chaseCamera = new ChaseCamera(cam, geometry, inputManager);
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onKeyEvent(KeyInputEvent evt) {
                if (evt.getKeyCode() == KeyInput.KEY_X) {
                    circle.setAngle(180);
                }
            }
        });
    }
}
