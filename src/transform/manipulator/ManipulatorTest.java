package transform.manipulator;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import transform.ModelUtil;

/**
 * create some manipulators which would not change their position and scale with camera
 *
 * @author Chen Jiongyu
 */
public class ManipulatorTest extends SimpleApplication {
    private Node moveTool;

    public static void main(String[] args) {
        ManipulatorTest app = new ManipulatorTest();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        app.setSettings(settings);
        app.start();
    }

    private void createManipulators() {
        final Material redMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMaterial.setColor("Color", ColorRGBA.Red);

        final Material blueMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueMaterial.setColor("Color", ColorRGBA.Blue);

        final Material greenMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        greenMaterial.setColor("Color", ColorRGBA.Green);

        moveTool = (Node) assetManager.loadModel("Models/manipulators/manipulators_move.j3o");
        moveTool.getChild("move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setCullHint(Spatial.CullHint.Always);
        moveTool.scale(0.2f);

        // use the second camera to observe the moveTool so that we can prevent the move tool
        // from scaling with the first camera(attach to scene) and obstructing by other models
        Camera camera = cam.clone();
        ViewPort editorViewPort = renderManager.createMainView("editor", camera);
        editorViewPort.setClearDepth(true);
        editorViewPort.attachScene(moveTool);
    }

    @Override
    public void simpleInitApp() {
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(0, 0, 5));
        createManipulators();
        moveTool.setLocalTranslation(0, 0, 0);
        Node modelNode = new Node();
        Geometry cube = ModelUtil.makeCube(assetManager, "cube", 0, 0, 0);
        modelNode.attachChild(cube);
        viewPort.attachScene(modelNode);
        rootNode.attachChild(modelNode);
        rootNode.attachChild(moveTool);
    }
}
