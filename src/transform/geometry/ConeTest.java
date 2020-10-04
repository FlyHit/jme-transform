package transform.geometry;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;

/**
 * @author Chen Jiongyu
 */
public class ConeTest extends SimpleApplication {
    public static void main(String[] args) {
        ConeTest app = new ConeTest();
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
        float arrowHeight = 0.4f;
        float arrowRadius = 0.1f;
        CylinderGeometry arrow = new CylinderGeometry(arrowRadius, 0.001f, arrowHeight);
        arrow.setLocalTranslation(0, 0, 0);
        Material baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseMat.setColor("Color", ColorRGBA.Red);
        arrow.setMaterial(baseMat);
        rootNode.attachChild(arrow);
    }
}
