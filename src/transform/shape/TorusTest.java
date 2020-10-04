package transform.shape;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Torus;

/**
 * @author Chen Jiongyu
 */
public class TorusTest extends SimpleApplication {
    public static void main(String[] args) {
        TorusTest app = new TorusTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Torus torus = new Torus(16, 16, 0.1f, 1.5f);
        Geometry torusGeo = new Geometry("torus", torus);
        Material baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseMat.setColor("Color", ColorRGBA.Red);
        torusGeo.setMaterial(baseMat);
        rootNode.attachChild(torusGeo);
    }
}
