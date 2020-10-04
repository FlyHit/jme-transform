package transform.tool;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;
import transform.RawInputAdapter;


/**
 * @author Chen Jiongyu
 */
public class RotationAuxTest extends SimpleApplication {
    public static void main(String[] args) {
        RotationAuxTest app = new RotationAuxTest();
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
        flyCam.setEnabled(false);
        RotationAuxiliary rotationAuxiliary = new RotationAuxiliary(assetManager, ColorRGBA.Red);
        rootNode.attachChild(rotationAuxiliary.getNode());
        ChaseCamera chaseCamera = new ChaseCamera(cam, rotationAuxiliary.getNode(), inputManager);

        viewPort.setBackgroundColor(ColorRGBA.Gray);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(al);
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);
        directionalLight.setDirection(cam.getDirection());
        rootNode.addLight(directionalLight);
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                if (evt.isPressed()) {
                    rotationAuxiliary.setStartPoint(cam.getWorldCoordinates(inputManager.getCursorPosition(),
                            cam.getScreenCoordinates(rotationAuxiliary.getNode().getWorldTranslation()).z));
                }
            }
        });
    }
}
