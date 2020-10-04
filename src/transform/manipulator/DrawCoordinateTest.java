package transform.manipulator;

import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.system.AppSettings;
import transform.ModelUtil;

/**
 * @author Chen Jiongyu
 */
public class DrawCoordinateTest extends SimpleApplication {
    public static void main(String[] args) {
        DrawCoordinateTest app = new DrawCoordinateTest();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setDragToRotate(true);
        rootNode.attachChild(ModelUtil.drawAxis(ModelUtil.Y_AXIS, false));
        rootNode.attachChild(ModelUtil.drawAxis(ModelUtil.Y_AXIS, true));
        rootNode.attachChild(ModelUtil.drawAxis(ModelUtil.X_AXIS, false));
        rootNode.attachChild(ModelUtil.drawAxis(ModelUtil.X_AXIS, true));
        rootNode.attachChild(ModelUtil.drawAxis(ModelUtil.Z_AXIS, false));
        rootNode.attachChild(ModelUtil.drawAxis(ModelUtil.Z_AXIS, true));
        rootNode.attachChild(ModelUtil.drawPlane(ModelUtil.XZ_PLANE, false, false));
        rootNode.attachChild(ModelUtil.drawPlane(ModelUtil.XZ_PLANE, false, true));
        rootNode.attachChild(ModelUtil.drawPlane(ModelUtil.XZ_PLANE, true, false));
        rootNode.attachChild(ModelUtil.drawPlane(ModelUtil.XZ_PLANE, true, true));
//        rootNode.attachChild(ModelUtil.drawPlane(ModelUtil.XZ_PLANE, assetManager, false, false));
//        rootNode.attachChild(ModelUtil.drawPlane(ModelUtil.YZ_PLANE, assetManager, false, false));
        rootNode.rotate(-FastMath.PI / 3, -FastMath.PI / 3, 0);
    }
}
