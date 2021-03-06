package transform.shape;



/*You can get transforms from *.blend files and use your models for it.
 * Blender could be used as a World Editor or scene composer.
 * Names of JME objects and blend objects should be like:
 * JME names - Box, Sphere
 * blend names - Box, Box.000, Box.001, Box.002.... Sphere, Sphere.000, Sphere.001...
 */


import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;


public class Circle3dTest extends SimpleApplication {

    public static void main(String[] args) {
        Circle3dTest app = new Circle3dTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        Circle3d circle = new Circle3d(Vector3f.ZERO, 3f, 64);

        Geometry geom = new Geometry("Box", circle);
        geom.updateModelBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0, 2, 1);
        geom.getMaterial().getAdditionalRenderState().setLineWidth(2);
        rootNode.attachChild(geom);

        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("Circle Mesh Example"); // crosshairs
        ch.setColor(new ColorRGBA(1f, 0.8f, 0.1f, 1f));
        ch.setLocalTranslation(settings.getWidth() * 0.3f, settings.getHeight() * 0.1f, 0);
        guiNode.attachChild(ch);


        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setDragToRotate(true);
    }
}




