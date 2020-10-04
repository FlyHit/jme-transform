package transform;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import transform.tool.GizmoGroupControl;
import transform.tool.TransformTool;

import java.util.HashMap;
import java.util.Map;

import static transform.TransformMode.MOVE_ONLY;
import static transform.TransformMode.ROTATE_ONLY;

/**
 * a simple example to transform an object
 *
 * @author Chen Jiongyu
 */
public class TransformWithTool extends SimpleApplication {
    private static final Map<Axis, Vector3f> AXIS_MAP = new HashMap<Axis, Vector3f>() {{
        put(Axis.X, Vector3f.UNIT_X.clone());
        put(Axis.Y, Vector3f.UNIT_Y.clone());
        put(Axis.Z, Vector3f.UNIT_Z.clone());
    }};
    private Node editorNode;
    private Node transformNode;
    private TransformTool transformTool;
    private Node sceneNode;
    private Node modelNode;
    private Geometry cube;
    private Spatial objectPicked;
    private Vector3f pointStart = new Vector3f();
    private Axis axis = Axis.X;
    private Space space = Space.World;
    private TransformMode mode = MOVE_ONLY;
    private boolean leftMouseBtnDown;
    private Plane movePlane;
    private boolean moveAlongPlane;
    private Camera editorCamera;
    private ChaseCamera chaseCamera;
    private Vector2f cursorPositionStart;
    private float tpf;

    public static void main(String[] args) {
        TransformWithTool app = new TransformWithTool();
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
        // mode node
        cube = ModelUtil.makeCube(assetManager, "cube", 1, 0, 0);
        modelNode = new Node();
        modelNode.setLocalTranslation(-2, 1, 2);
        modelNode.setLocalRotation(new Quaternion().fromAngleAxis(20, Vector3f.UNIT_Y));
        modelNode.attachChild(cube);

        editorNode = new Node();
        createViewPortForEditorNode();
        transformTool = new TransformTool(assetManager, cam, editorCamera);
        transformNode = transformTool.getTransformNode();
        editorNode.attachChild(transformNode);

        sceneNode = new Node();
        sceneNode.attachChild(modelNode);
        Spatial floor = ModelUtil.makeFloor(assetManager, 10, 0.2f, 10);
        sceneNode.attachChild(floor);
        rootNode.attachChild(sceneNode);
        rootNode.attachChild(editorNode);

        initInputManager();
        initCamera();

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(al);
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White);
        directionalLight.setDirection(cam.getDirection());
        rootNode.addLight(directionalLight);
    }

    private void initCamera() {
        flyCam.setEnabled(false);
        chaseCamera = new ChaseCamera(cam, sceneNode, inputManager);
        chaseCamera.setSmoothMotion(false);
        chaseCamera.setMaxVerticalRotation(FastMath.TWO_PI);
        chaseCamera.setDragToRotate(true);
        chaseCamera.setRotationSpeed(2);
        chaseCamera.setInvertVerticalAxis(true);
        viewPort.detachScene(rootNode);
        viewPort.attachScene(sceneNode);
        viewPort.setBackgroundColor(ColorRGBA.White);
    }

    private void createViewPortForEditorNode() {
        editorCamera = cam.clone();
        ViewPort transformViewPort = renderManager.createMainView("transform", editorCamera);
        transformViewPort.setClearDepth(true);
        transformViewPort.setEnabled(true);
        transformViewPort.attachScene(editorNode);
    }

    private void initInputManager() {
        inputManager.addRawInputListener(new RawInputAdapter() {
            @Override
            public void onMouseMotionEvent(MouseMotionEvent evt) {
                TransformWithTool.this.onMouseMotionEvent(evt);
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
                    case KeyInput.KEY_R:
                        mode = TransformMode.ROTATE_ONLY;
                        break;
                    case KeyInput.KEY_T:
                        mode = MOVE_ONLY;
                        break;
                    case KeyInput.KEY_M:
                        mode = TransformMode.TRANSFORM;
                        break;
                }
                transformTool.updateTool(objectPicked, space, mode);
            }

        });
    }

    private void onLeftMouseBtnEvent(MouseButtonEvent evt) {
        leftMouseBtnDown = evt.isPressed();

        if (leftMouseBtnDown) {
            CollisionResults results = new CollisionResults();
            cursorPositionStart = new Vector2f(evt.getX(), evt.getY());
            Ray ray = ModelUtil.makeRayFromCamera(cam, cursorPositionStart);
            rootNode.collideWith(ray, results);
            CollisionResult closetCollision = getClosetCollision(results);
            // nothing was picked
            if (closetCollision == null) {
                objectPicked = null;
                return;
            }

            Geometry _objectPicked = closetCollision.getGeometry();
            // objects which are not intend to be picked were picked (like floor)
            if (!modelNode.hasChild(_objectPicked) && !transformNode.hasChild(_objectPicked)) {
                objectPicked = null;
                return;
            }

            if (modelNode.hasChild(_objectPicked)) {
                objectPicked = _objectPicked;
                axis = Axis.XYZ;
                moveAlongPlane = false;
            } else if (transformNode.hasChild(_objectPicked)) {
                String axisSelected = _objectPicked.getUserData(TransformTool.AXIS);
                assert axisSelected != null;
                axis = Axis.valueOf(axisSelected);
                moveAlongPlane = axis == Axis.XY || axis == Axis.XZ || axis == Axis.YZ;
                transformNode.setUserData(GizmoGroupControl.GIZMO_HOVERED, _objectPicked.getName());
            }

            Vector3f worldTranslation = objectPicked.getWorldTranslation();
            if (moveAlongPlane) {
                // we expect to move an object along a plane in the viewport of object's camera, but
                // the plane we clicked on is in the viewport of editor camera, so we need to do some conversion
                pointStart = closetCollision.getContactPoint();
                Vector3f sc1 = editorCamera.getScreenCoordinates(pointStart, null);
                pointStart = cam.getWorldCoordinates(new Vector2f(sc1.x, sc1.y),
                        cam.getScreenCoordinates(worldTranslation, null).z);
                updateMovePlane(pointStart);
            } else {
                if (mode == ROTATE_ONLY) {
                    transformTool.setRotationStartPoint(closetCollision.getContactPoint());
                }
                pointStart = cam.getWorldCoordinates(cursorPositionStart, cam.getScreenCoordinates(worldTranslation).z);
            }

            chaseCamera.setEnabled(false);
        } else {
            transformNode.setUserData(TransformTool.TRANSFORMING, null);
            chaseCamera.setEnabled(true);
        }

        transformTool.updateTool(objectPicked, space, mode);
    }

    private CollisionResult getClosetCollision(CollisionResults results) {
        for (CollisionResult result : results) {
            Geometry geometry = result.getGeometry();
            if (transformNode.hasChild(geometry)) {
                return result;
            }
        }

        return results.getClosestCollision();
    }

    private void updateMovePlane(Vector3f displacement) {
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
            default:
                break;
        }
        if (space == Space.LOCAL) {
            normal = cube.getWorldRotation().mult(normal);
        }

        if (movePlane == null) {
            movePlane = new Plane(normal, displacement);
        } else {
            movePlane.setNormal(normal);
            movePlane.setConstant(displacement.dot(normal));
        }
    }

    private void onMouseMotionEvent(MouseMotionEvent evt) {
        Vector2f cursorPositionEnd = new Vector2f(evt.getX(), evt.getY());
        Ray ray = ModelUtil.makeRayFromCamera(cam, cursorPositionEnd);
        if (leftMouseBtnDown && objectPicked != null) {
            Vector3f offset;
            Vector3f pointEnd = new Vector3f();
            Vector3f objectWorldPosition = objectPicked.getWorldTranslation();
            Vector3f eye = cam.getLocation().subtract(objectWorldPosition).normalize();
            if (mode == MOVE_ONLY) {
                if (moveAlongPlane) {
                    ray.intersectsWherePlane(movePlane, pointEnd);
                } else {
                    pointEnd = cam.getWorldCoordinates(cursorPositionEnd,
                            cam.getScreenCoordinates(objectWorldPosition).z);
                }
                offset = pointEnd.subtract(pointStart);
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
                pointStart = pointEnd;
            } else if (mode == ROTATE_ONLY) {
                pointEnd = cam.getWorldCoordinates(cursorPositionEnd, cam.getScreenCoordinates(objectWorldPosition).z);
                offset = pointEnd.subtract(pointStart);
                Vector3f rotationAxis = new Vector3f();
                float rotationAngle = 0;
                if (axis == Axis.XYZ) {
                    rotationAxis = offset.cross(eye).normalize();
                    rotationAngle = offset.dot(rotationAxis.cross(eye).normalize());
                } else if (axis == Axis.X || axis == Axis.Y || axis == Axis.Z) {
                    rotationAxis = AXIS_MAP.get(axis).clone();

                    Vector3f tempVector = rotationAxis.clone();
                    if (space == Space.LOCAL) {
                        tempVector = objectPicked.getWorldRotation().mult(tempVector);
                    }

                    Vector3f objectScreenPosition = cam.getScreenCoordinates(objectWorldPosition);
                    // in screen coordinate, the positive direction of y-axis is downward, so we need
                    // to invert the y-offset
                    Vector2f tempVector1 = new Vector2f(cursorPositionEnd.x - objectScreenPosition.x,
                            -(cursorPositionEnd.y - objectScreenPosition.y));
                    Vector2f tempVector2 = new Vector2f(cursorPositionStart.x - objectScreenPosition.x,
                            -(cursorPositionStart.y - objectScreenPosition.y));
                    rotationAngle = tempVector1.angleBetween(tempVector2);
                    // To rotate an object in the correct direction,
                    // we need negate rotation angle when rotation axis was inverted
                    if (tempVector.dot(eye) < 0) {
                        rotationAngle = -rotationAngle;
                    }
                }
                if (space == Space.World || axis == Axis.XYZ) {
                    rotationAxis.addLocal(objectWorldPosition);
                    rotationAxis = objectPicked.worldToLocal(rotationAxis, null);
                }
                objectPicked.rotate(new Quaternion().fromAngleAxis(rotationAngle, rotationAxis));
                transformTool.setRotationDeltaAngle(rotationAngle);
                pointStart = pointEnd;
            }
            cursorPositionStart = cursorPositionEnd;
            transformNode.setUserData(TransformTool.TRANSFORMING, axis.toString());
            transformTool.updateTool(objectPicked, space, mode);
            return;
        }

        // mouse hovered
        CollisionResults results = new CollisionResults();
        transformNode.collideWith(ray, results);
        if (results.getClosestCollision() == null) {
            transformNode.setUserData(GizmoGroupControl.GIZMO_HOVERED, GizmoGroupControl.NONE_HOVERED);
            return;
        }
        Geometry _objectPicked = results.getClosestCollision().getGeometry();
        String axisSelected = _objectPicked.getUserData(TransformTool.AXIS);
        if (axisSelected == null) {
            return;
        }
        axis = Axis.valueOf(axisSelected);
        moveAlongPlane = axis == Axis.XY || axis == Axis.XZ || axis == Axis.YZ;
        transformNode.setUserData(GizmoGroupControl.GIZMO_HOVERED, _objectPicked.getName());
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        this.tpf = tpf;
    }
}
