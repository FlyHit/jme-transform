package transform.shape.fill;

import com.jme3.app.SimpleApplication;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import transform.RawInputAdapter;

/**
 * @author Chen Jiongyu
 */
public class ShapeFillerTest extends SimpleApplication {
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1440);
        settings.setHeight(900);
        settings.setFrameRate(60);//修改FPS的值
        settings.setSamples(16);//修改抗锯齿

        ShapeFillerTest app = new ShapeFillerTest();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
//        stateManager.attach(new LightAppState());
        viewPort.setBackgroundColor(ColorRGBA.White);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(al);
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);
        directionalLight.setDirection(cam.getDirection());
        rootNode.addLight(directionalLight);
        flyCam.setMoveSpeed(5);
        flyCam.setDragToRotate(true);

        cam.setLocation(new Vector3f(1.6168175f, 1.4101417f, 2.1551998f));
        cam.setRotation(new Quaternion(-0.015327892f, 0.98448133f, -0.13299417f, -0.11346416f));

        Box b = new Box(1, 1, 1);
        Cylinder c = new Cylinder(6, 60, 1, 1, true, false);
        Geometry geom = new Geometry("Box", c);
//        geom.center();
        geom.move(0, 0, -5f);
//        cam.setFrustumNear(10);

        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        geom.setMaterial(mat2);
//
        rootNode.attachChild(geom);

        // 平面样式绘制
        ShapeFiller shapeFiller =
                new ShapeFiller(
                        assetManager,
                        1, 1, 270, 64,
                        "MatDefs/QuadDrawing1/QuadDrawing.j3md",
                        RenderQueue.Bucket.Translucent,
                        ShapeFiller.Shape.Circle
                );
        shapeFiller.setVisibility(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(shapeFiller.getGeom());
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                shapeFiller.setAngle(360);
            }
        });
    }
}
