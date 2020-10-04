package transform.rotate;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import transform.ModelUtil;
import transform.RawInputAdapter;

/**
 * @author Chen Jiongyu
 */
public class MouseRotateAxis extends SimpleApplication {
    private Spatial cube;
    private boolean mouseDown = false;
    private float tpf;
    private Space space;
    private Vector3f rotationAxis;
    private Geometry axis;
    private Mode mode;

    public static void main(String[] args) {
        MouseRotateAxis app = new MouseRotateAxis();
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
        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onKeyEvent(KeyInputEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyInput.KEY_W:
                        space = Space.W;
                        break;
                    case KeyInput.KEY_L:
                        space = Space.L;
                        break;
                    case KeyInput.KEY_X:
                        rotationAxis = Vector3f.UNIT_X;
                        mode = Mode.X;
                        break;
                    case KeyInput.KEY_Y:
                        rotationAxis = Vector3f.UNIT_Y;
                        mode = Mode.Y;
                        break;
                    case KeyInput.KEY_Z:
                        rotationAxis = Vector3f.UNIT_Z;
                        mode = Mode.Z;
                        break;
                }
            }

            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                if (mouseDown) {
                    Quaternion quaternion = cube.getLocalRotation();
                    Vector3f _rotationAxis = space == Space.W ? cube.worldToLocal(rotationAxis, null) : rotationAxis;
                    quaternion = quaternion.mult(new Quaternion().fromAngleAxis(evt.getDX() * tpf * 8,
                            _rotationAxis));
                    cube.setLocalRotation(quaternion);
                }
            }

            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                if (evt.getButtonIndex() == 0) {
                    mouseDown = evt.isPressed();
                    if (mouseDown) {
                        Vector3f begin = new Vector3f();
                        Vector3f end = new Vector3f();
                        switch (mode) {
                            case X:
                                // if you want to get a vector's value and modify it, you have to clone it to prevent
                                // direct operations on the vector
                                begin = rotationAxis.clone().setX(-10000);
                                end = rotationAxis.clone().setX(10000);
                                break;
                            case Y:
                                begin = rotationAxis.clone().setY(-10000);
                                end = rotationAxis.clone().setY(10000);
                                break;
                            case Z:
                                begin = rotationAxis.clone().setZ(-10000);
                                end = rotationAxis.clone().setZ(10000);
                                break;
                        }

                        if (space == Space.L) {
                            begin = cube.localToWorld(begin, null);
                            end = cube.localToWorld(end, null);
                        }

                        Line line = new Line(begin, end);
                        axis = new Geometry("rotationAxis", line);
                        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        axis.setMaterial(mat);
                        rootNode.attachChild(axis);
                    } else {
                        rootNode.detachChild(axis);
                    }
                }
            }
        });

        cube = ModelUtil.makeCube(assetManager, "cube", 0, 0, 0);
        cube.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(cube);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        rotationAxis = Vector3f.UNIT_X;
        space = Space.W;
        mode = Mode.X;
    }

    @Override
    public void simpleUpdate(float tpf) {
        this.tpf = tpf;
    }

    private enum Mode {
        /**
         * rotate along the x axis
         */
        X,
        /**
         * rotate along the y axis
         */
        Y,
        /**
         * rotate along the z axis
         */
        Z
    }

    private enum Space {
        /**
         * world coordinate
         */
        W,
        /**
         * local coordinate
         */
        L
    }
}
