package transform;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;

/**
 * @author Chen Jiongyu
 */
public class TransformCube extends SimpleApplication {
    private Node modelNode;
    private Geometry cube;
    private Vector3f pointStart;
    private Vector3f pointEnd;
    private Axis axis;
    private Space space;
    private Mode mode;
    private boolean translateAlongPlane;
    private String spaceInfo;
    private String axisInfo;
    private String modeInfo;
    private BitmapText hudText;
    private Vector3f currentAxis;
    private Geometry xAxis;
    private Geometry yAxis;
    private Geometry zAxis;
    private Geometry connectionLine;
    private Vector3f originalWorldPosition;
    private Vector3f currentWorldPosition;
    private boolean picked;
    private Plane translatePlane;
    private float tpf;

    public static void main(String[] args) {
        TransformCube app = new TransformCube();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        currentAxis = Vector3f.UNIT_X;
        mode = Mode.TRANSLATE;
        axis = Axis.X;
        space = Space.World;
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setColor(ColorRGBA.White);
        hudText.setLocalTranslation(300, 4 * hudText.getLineHeight(), 0);
        guiNode.attachChild(hudText);
        updateInformation();

        initCam();
        initInputManager();

        cube = ModelUtil.makeCube(assetManager, "cube", 1, 0, 0);
        modelNode = new Node();
        modelNode.setLocalTranslation(-2, 1, 2);
        modelNode.setLocalRotation(new Quaternion().fromAngleAxis(20, Vector3f.UNIT_Y));
        modelNode.attachChild(cube);
        rootNode.attachChild(modelNode);
        rootNode.attachChild(ModelUtil.makeFloor(assetManager, 10, 0.2f, 10));
//        Vector3f worldTranslation = cube.getWorldTranslation();
//        Vector3f sc = cam.getScreenCoordinates(worldTranslation);
//        Vector3f wc = cam.getWorldCoordinates(new Vector2f(sc.x, sc.y), sc.z);
//        System.out.println(worldTranslation);
//        System.out.println(wc);
    }

    private void initCam() {
        flyCam.setDragToRotate(true);
        flyCam.setZoomSpeed(6);
        flyCam.setMoveSpeed(6);
        cam.setParallelProjection(false);
        cam.setLocation(new Vector3f(-15, 8f, 15));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
    }

    private void initInputManager() {
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                TransformCube.this.onMouseMotionEvent(evt);
            }

            @Override
            public void onMouseButtonEvent(MouseButtonEvent evt) {
                if (evt.getButtonIndex() == MouseInput.BUTTON_LEFT) {
                    onLeftMouseBtnEvent(evt);
                }
            }

            @Override
            public void onKeyEvent(KeyInputEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyInput.KEY_W:
                        space = Space.World;
                        break;
                    case KeyInput.KEY_L:
                        space = Space.LOCAL;
                        break;
                    case KeyInput.KEY_X:
                        currentAxis = Vector3f.UNIT_X;
                        axis = Axis.X;
                        break;
                    case KeyInput.KEY_Y:
                        currentAxis = Vector3f.UNIT_Y;
                        axis = Axis.Y;
                        break;
                    case KeyInput.KEY_Z:
                        currentAxis = Vector3f.UNIT_Z;
                        axis = Axis.Z;
                        break;
                    case KeyInput.KEY_H:
                        axis = Axis.XZ;
                        break;
                    case KeyInput.KEY_J:
                        axis = Axis.XY;
                        break;
                    case KeyInput.KEY_K:
                        axis = Axis.YZ;
                        break;
                    case KeyInput.KEY_3:
                        axis = Axis.XYZ;
                        break;
                    case KeyInput.KEY_R:
                        mode = Mode.ROTATE;
                        break;
                    case KeyInput.KEY_T:
                        mode = Mode.TRANSLATE;
                        break;
                }
                switch (axis) {
                    case XZ:
                    case YZ:
                    case XY:
                        translateAlongPlane = true;
                        break;
                    default:
                        translateAlongPlane = false;
                }
                updateInformation();
            }
        });
    }

    private void onMouseMotionEvent(MouseMotionEvent evt) {
        if (picked) {
            Vector3f offset;
            if (translateAlongPlane) {
                Ray ray = ModelUtil.makeRayFromCamera(getCamera(), new Vector2f(evt.getX(), evt.getY()));
                pointEnd = new Vector3f();
                ray.intersectsWherePlane(translatePlane, pointEnd);
            } else {
                pointEnd = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                        cam.getScreenCoordinates(cube.getWorldTranslation()).z);
            }

            offset = pointEnd.subtract(pointStart);

            if (mode == Mode.TRANSLATE) {
                if (space == Space.LOCAL) {
                    offset = cube.getWorldRotation().inverse().mult(offset);
                }
                switch (axis) {
                    case X:
                        offset = offset.project(Vector3f.UNIT_X);
                        break;
                    case Y:
                        offset = offset.project(Vector3f.UNIT_Y);
                        break;
                    case Z:
                        offset = offset.project(Vector3f.UNIT_Z);
                        break;
                }

                if (space == Space.LOCAL) {
                    offset = cube.getParent().getWorldRotation().inverse().mult(cube.getWorldRotation()).mult(offset);
                } else {
                    offset = cube.getParent().getWorldRotation().inverse().mult(offset);
                }

                cube.move(offset);
            } else {
                Quaternion quaternion = cube.getLocalRotation();
                Vector3f _rotationAxis = currentAxis.clone().add(cube.getWorldTranslation());
                Plane verticalPlane = new Plane(_rotationAxis, pointStart);
                if (axis == Axis.XYZ) {
                    float ROTATION_SPEED = 8;
                    quaternion = quaternion.mult(new Quaternion().fromAngleAxis(evt.getDX() * tpf * ROTATION_SPEED,
                            cube.worldToLocal(Vector3f.UNIT_Y, null))).mult(
                            new Quaternion().fromAngleAxis(-evt.getDY() * tpf * ROTATION_SPEED,
                                    cube.worldToLocal(Vector3f.UNIT_X, null)));
                } else {
                    _rotationAxis = (space == Space.World) ?
                            cube.worldToLocal(_rotationAxis, null) : currentAxis;
                    offset = offset.subtract(offset.project(_rotationAxis));
                    float xAngle = cam.getScreenCoordinates(offset).x * tpf;
                    float yAngle = cam.getScreenCoordinates(offset).y * tpf;
                    quaternion = quaternion.mult(new Quaternion().fromAngleAxis(evt.getDX() * tpf * 8, _rotationAxis));
                }
                cube.setLocalRotation(quaternion);
            }
            pointStart = pointEnd;
        }
    }

    private void onLeftMouseBtnEvent(MouseButtonEvent evt) {
        boolean leftMouseBtnDown = evt.isPressed();

        if (leftMouseBtnDown) {
            CollisionResults results = new CollisionResults();
            Ray ray = ModelUtil.makeRayFromCamera(getCamera(), new Vector2f(evt.getX(), evt.getY()));
            cube.collideWith(ray, results);
            if (results.size() == 0) {
                picked = false;
                return;
            }
            picked = true;

            xAxis = makeAxis(Vector3f.UNIT_X);
            yAxis = makeAxis(Vector3f.UNIT_Y);
            zAxis = makeAxis(Vector3f.UNIT_Z);
            rootNode.attachChild(xAxis);
            rootNode.attachChild(yAxis);
            rootNode.attachChild(zAxis);
            originalWorldPosition = cube.getWorldTranslation().clone();

            if (translateAlongPlane) {
                pointStart = results.getClosestCollision().getContactPoint();
                Vector3f normal = new Vector3f();
                switch (axis) {
                    case XY:
                        normal = Vector3f.UNIT_Z;
                        break;
                    case XZ:
                        normal = Vector3f.UNIT_Y;
                        break;
                    case YZ:
                        normal = Vector3f.UNIT_X;
                        break;
                }
                if (space == Space.LOCAL) {
                    normal = cube.getWorldRotation().mult(normal);
                }
                translatePlane = new Plane(normal, pointStart);
            } else {
                pointStart = cam.getWorldCoordinates(inputManager.getCursorPosition(),
                        cam.getScreenCoordinates(cube.getWorldTranslation()).z);
            }

            flyCam.setEnabled(false);
        } else {
            if (picked) {
                rootNode.detachChild(xAxis);
                rootNode.detachChild(yAxis);
                rootNode.detachChild(zAxis);
                if (connectionLine != null) {
                    rootNode.detachChild(connectionLine);
                }
            }

            picked = false;
            flyCam.setEnabled(true);
            flyCam.setDragToRotate(true);
        }
    }

    private Geometry makeAxis(Vector3f axis) {
        Vector3f start, end;
        ColorRGBA color;
        if (axis.x != 0) {
            start = axis.clone().setX(-10000);
            end = axis.clone().setX(10000);
            color = ColorRGBA.Blue;
        } else if (axis.y != 0) {
            start = axis.clone().setY(-10000);
            end = axis.clone().setY(10000);
            color = ColorRGBA.Green;
        } else {
            start = axis.clone().setZ(-10000);
            end = axis.clone().setZ(10000);
            color = ColorRGBA.Red;
        }

        if (space == Space.LOCAL) {
            start = cube.localToWorld(start, null);
            end = cube.localToWorld(end, null);
        }
        Geometry axisGeometry = makeLine(start, end, color);
        if (space == Space.World) {
            axisGeometry.setLocalTranslation(cube.getWorldTranslation());
        }
        return axisGeometry;
    }

    private Geometry makeLine(Vector3f start, Vector3f end, ColorRGBA color) {
        Line line = new Line(start, end);
        Geometry geometry = new Geometry("line", line);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geometry.setMaterial(mat);
        return geometry;
    }

    private void updateInformation() {
        spaceInfo = space == Space.World ? "world coordinate" : "local coordinate";
        modeInfo = mode == Mode.ROTATE ? "rotate" : "translate";
        switch (axis) {
            case X:
                axisInfo = "x-axis";
                break;
            case Y:
                axisInfo = "y-axis";
                break;
            case Z:
                axisInfo = "z-axis";
                break;
            case XZ:
                axisInfo = "xz-plane";
                break;
            case XY:
                axisInfo = "xy-plane";
                break;
            case YZ:
                axisInfo = "yz-plane";
                break;
            case XYZ:
                axisInfo = "xyz";
                break;
        }
        hudText.setText("Current coordinate: " + spaceInfo + "\nMode: " + modeInfo + "\nCurrent axis: " + axisInfo);
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        this.tpf = tpf;
        if (originalWorldPosition != null && picked) {
            currentWorldPosition = cube.getWorldTranslation().clone();
            if (connectionLine != null) {
                rootNode.detachChild(connectionLine);
            }
            connectionLine = makeLine(originalWorldPosition, currentWorldPosition, ColorRGBA.White);
            rootNode.attachChild(connectionLine);
        }
    }

    public Geometry getCube() {
        return cube;
    }

    public boolean isPicked() {
        return picked;
    }

    private enum Mode {
        /**
         * Rotate
         */
        ROTATE,
        /**
         * Translate
         */
        TRANSLATE
    }

    private enum Axis {
        /**
         * rotate or translate along the x axis
         */
        X,
        /**
         * rotate or translate along the y axis
         */
        Y,
        /**
         * rotate or translate along the z axis
         */
        Z,
        /**
         * translate along XZ plane
         */
        XZ,
        /**
         * translate along XY plane
         */
        XY,
        /**
         * translate along YZ plane
         */
        YZ,
        /**
         * 3D rotate or translation
         */
        XYZ
    }

    private enum Space {
        /**
         * world coordinate
         */
        World,
        /**
         * local coordinate
         */
        LOCAL
    }
}
