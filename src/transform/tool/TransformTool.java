package transform.tool;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import transform.Axis;
import transform.ModelUtil;
import transform.Space;
import transform.TransformMode;
import transform.geometry.CylinderGeometry;
import transform.shape.Semicircle;

import java.util.*;

/**
 * @author Chen Jiongyu
 */
public class TransformTool {
    public static final String AXIS = "transform_axis";
    public static final String TRANSFORMING = "object_transforming";
    public static final Map<Axis, String> MOVE_AXIS_TO_NAMES = new HashMap<Axis, String>() {{
        put(Axis.X, "xAxis");
        put(Axis.Y, "yAxis");
        put(Axis.Z, "zAxis");
        put(Axis.XY, "xy_plane");
        put(Axis.XZ, "xz_plane");
        put(Axis.YZ, "yz_plane");
        put(Axis.XYZ, "xAxis");
    }};
    public static final Map<Axis, String> ROTATE_AXIS_TO_NAMES = new HashMap<Axis, String>() {{
        put(Axis.X, "xRotate");
        put(Axis.Y, "yRotate");
        put(Axis.Z, "zRotate");
    }};
    private static final ColorRGBA LIGHT_GREEN = new ColorRGBA(139f / 255, 220f / 255, 0, 1);
    private static final ColorRGBA FADED_GREEN = new ColorRGBA(139f / 255, 220f / 255, 0, 0.2f);
    private static final ColorRGBA GREEN = new ColorRGBA(139f / 255, 220f / 255, 0, 1);
    private static final ColorRGBA TRANSPARENT_GREEN = new ColorRGBA(139f / 255, 220f / 255, 0, 0.3f);
    private static final ColorRGBA LIGHT_BLUE = new ColorRGBA(40f / 255, 144f / 255, 1, 1);
    private static final ColorRGBA FADED_BLUE = new ColorRGBA(40f / 255, 144f / 255, 1, 0.2f);
    private static final ColorRGBA BLUE = new ColorRGBA(40f / 255, 144f / 255, 255f / 255, 1);
    private static final ColorRGBA TRANSPARENT_BLUE = new ColorRGBA(40f / 255, 144f / 255, 255f / 255, 0.3f);
    private static final ColorRGBA LIGHT_RED = new ColorRGBA(1, 51f / 255, 82f / 255, 1);
    private static final ColorRGBA FADED_RED = new ColorRGBA(1, 51f / 255, 82f / 255, 0.2f);
    private static final ColorRGBA RED = new ColorRGBA(229f / 255, 72f / 255, 96f / 255, 1);
    private static final ColorRGBA TRANSPARENT_RED = new ColorRGBA(229f / 255, 72f / 255, 96f / 255, 0.3f);
    private static Material[] redMatGroup1;
    private static Material[] redMatGroup2;
    private static Material[] greenMatGroup1;
    private static Material[] greenMatGroup2;
    private static Material[] blueMatGroup1;
    private static Material[] blueMatGroup2;
    private final Node transformNode;
    private final Node toolNode;
    private final Node moveAxes;
    private final Node movePlanes;
    private final Node scaleNode;
    private final Node rotateTool;
    private final Camera toolCamera;
    private final Node auxiliaryNode;
    private final Node rotationAuxNode;
    private final Node moveAuxNode;
    private final Camera objectCamera;
    private final AssetManager assetManager;
    private Node xAxis;
    private Node xAxisNegative;
    private Node yAxis;
    private Node yAxisNegative;
    private Node zAxis;
    private Node zAxisNegative;
    private Spatial[][][] planes;
    private Node xSemicircle;
    private Node ySemicircle;
    private Node zSemicircle;
    private RotationAuxiliary xRotationAux;
    private RotationAuxiliary yRotationAux;
    private RotationAuxiliary zRotationAux;
    private Spatial connectionLine;
    private Spatial xAuxiliary;
    private Spatial yAuxiliary;
    private Spatial zAuxiliary;
    private Spatial object;
    private Material baseMat;
    private Material whiteMat;
    private Material orangeMat;
    private Space space;
    private TransformMode mode;
    private boolean transforming;
    private Vector3f rotationStartPoint = Vector3f.ZERO;
    private float rotationDeltaAngle = 0;
    private Vector3f objectOriginalPosition;

    public TransformTool(AssetManager assetManager, Camera objectCamera, Camera toolCamera) {
        this.assetManager = assetManager;
        this.objectCamera = objectCamera;
        this.toolCamera = toolCamera;

        initMaterials();
        initPlanes();
        initAxes();
        initSemicircles();
        initAuxiliaries();

        transformNode = new Node();
        toolNode = new Node();
        Node moveTool = new Node();
        movePlanes = new Node();
        moveAxes = new Node();
        moveTool.attachChild(moveAxes);
        moveTool.attachChild(movePlanes);
        rotateTool = new Node();
        scaleNode = new Node();
        toolNode.attachChild(rotateTool);
        toolNode.attachChild(moveTool);
        toolNode.attachChild(scaleNode);

        moveAuxNode = new Node();
        auxiliaryNode = new Node();
        rotationAuxNode = new Node();
        rotationAuxNode.attachChild(xRotationAux.getNode());
        rotationAuxNode.attachChild(yRotationAux.getNode());
        rotationAuxNode.attachChild(zRotationAux.getNode());
        auxiliaryNode.attachChild(moveAuxNode);
        auxiliaryNode.attachChild(rotationAuxNode);

        transformNode.attachChild(toolNode);
        transformNode.attachChild(auxiliaryNode);

        List<Spatial> gizmoGroup = new ArrayList<>();
        for (Spatial[][] plane : planes) {
            for (Spatial[] p : plane) {
                gizmoGroup.addAll(Arrays.asList(p));
            }
        }
        gizmoGroup.add(xAxis);
        gizmoGroup.add(xAxisNegative);
        gizmoGroup.add(yAxis);
        gizmoGroup.add(yAxisNegative);
        gizmoGroup.add(zAxis);
        gizmoGroup.add(zAxisNegative);
        gizmoGroup.add(xSemicircle);
        gizmoGroup.add(ySemicircle);
        gizmoGroup.add(zSemicircle);
        GizmoGroupControl gizmoGroupControl = new GizmoGroupControl(gizmoGroup);
        gizmoGroupControl.addGizmoCouple(MOVE_AXIS_TO_NAMES.get(Axis.YZ), Arrays.asList(yAxis, yAxisNegative, zAxis,
                zAxisNegative));
        gizmoGroupControl.addGizmoCouple(MOVE_AXIS_TO_NAMES.get(Axis.XZ), Arrays.asList(xAxis, xAxisNegative, zAxis,
                zAxisNegative));
        gizmoGroupControl.addGizmoCouple(MOVE_AXIS_TO_NAMES.get(Axis.XY), Arrays.asList(xAxis, xAxisNegative, yAxis,
                yAxisNegative));
        transformNode.addControl(gizmoGroupControl);
        transformNode.setUserData(GizmoGroupControl.GIZMO_HOVERED, GizmoGroupControl.NONE_HOVERED);
        transformNode.addControl(new TransformControl(this));
        transformNode.setLocalScale(0.15f);
    }

    private static Node drawAxis(Axis axis, boolean negative) {
        Node arrow = drawArrow(negative);
        switch (axis) {
            case X:
                arrow.rotate(new Quaternion().fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_Y));
                break;
            case Y:
                arrow.rotate(new Quaternion().fromAngleAxis(-FastMath.PI / 2, Vector3f.UNIT_X));
                break;
            default:
                break;
        }

        arrow.setName(MOVE_AXIS_TO_NAMES.get(axis));
        for (Spatial child : arrow.getChildren()) {
            child.setUserData(AXIS, axis.toString());
            child.setName(MOVE_AXIS_TO_NAMES.get(axis));
        }
        return arrow;
    }

    private static Node drawArrow(boolean inverse) {
        Vector3f start = Vector3f.ZERO;
        float axisLength = inverse ? -2f : 2f;
        Vector3f end = new Vector3f(0, 0, axisLength);

        // draw a line
        Line line = new Line(start, end);
        Geometry lineGeometry = new Geometry("line", line);
        // draw a cone
        CylinderGeometry cone = new CylinderGeometry(0.1f, 0.001f, 0.4f);
        cone.rotate(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_X));
        cone.setLocalTranslation(end);
        // draw a box for picking
        Box box = new Box(0.15f, 0.15f, 1.2f);
        Geometry boxGeometry = new Geometry("box", box);
        float boxOffsetZ = inverse ? -1.2f : 1.2f;
        boxGeometry.setLocalTranslation(0, 0, boxOffsetZ);
        boxGeometry.setCullHint(Spatial.CullHint.Always);

        Node axisNode = new Node();
        axisNode.attachChild(lineGeometry);
        axisNode.attachChild(cone);
        axisNode.attachChild(boxGeometry);
        return axisNode;
    }

    private void initMaterials() {
        baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        whiteMat = baseMat.clone();
        whiteMat.setColor("Color", ColorRGBA.White);
        orangeMat = baseMat.clone();
        orangeMat.setColor("Color", ColorRGBA.Orange);
        redMatGroup1 = initMatGroup(LIGHT_RED, RED, FADED_RED);
        redMatGroup2 = initMatGroup(LIGHT_RED, TRANSPARENT_RED, FADED_RED);
        greenMatGroup1 = initMatGroup(LIGHT_GREEN, GREEN, FADED_GREEN);
        greenMatGroup2 = initMatGroup(LIGHT_GREEN, TRANSPARENT_GREEN, FADED_GREEN);
        blueMatGroup1 = initMatGroup(LIGHT_BLUE, BLUE, FADED_BLUE);
        blueMatGroup2 = initMatGroup(LIGHT_BLUE, TRANSPARENT_BLUE, FADED_BLUE);
    }

    private Material[] initMatGroup(ColorRGBA lightColor, ColorRGBA color, ColorRGBA fadedColor) {
        Material mat = baseMat.clone();
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Material lightMat = baseMat.clone();
        lightMat.setColor("Color", lightColor);
        Material fadedMat = baseMat.clone();
        fadedMat.setColor("Color", fadedColor);
        fadedMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return new Material[]{lightMat, mat, fadedMat};
    }

    private void initAuxiliaries() {
        xAuxiliary = makeLine(new Vector3f(-100000, 0, 0), new Vector3f(100000, 0, 0), redMatGroup2[0]);
        yAuxiliary = makeLine(new Vector3f(0, -100000, 0), new Vector3f(0, 100000, 0), greenMatGroup2[0]);
        zAuxiliary = makeLine(new Vector3f(0, 0, -100000), new Vector3f(0, 0, 100000), blueMatGroup2[0]);
        xRotationAux = new RotationAuxiliary(assetManager, LIGHT_RED);
        xRotationAux.getNode().setLocalRotation(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
        yRotationAux = new RotationAuxiliary(assetManager, LIGHT_GREEN);
        yRotationAux.getNode().setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        zRotationAux = new RotationAuxiliary(assetManager, LIGHT_BLUE);
    }

    private Geometry makeSphere(float radius, int sample, Material material) {
        Sphere sphere = new Sphere(sample, sample, radius);
        Geometry sphereGeo = new Geometry("origin", sphere);
        sphereGeo.setMaterial(material);
        return sphereGeo;
    }

    private Geometry makeLine(Vector3f start, Vector3f end, Material material) {
        Line line = new Line(start, end);
        Geometry lineGeo = new Geometry("line", line);
        lineGeo.setMaterial(material);
        return lineGeo;
    }

    private void initAxes() {
        // x
        xAxis = initAxis(Axis.X, false, redMatGroup1);
        xAxisNegative = initAxis(Axis.X, true, redMatGroup1);
        // y
        yAxis = initAxis(Axis.Y, false, greenMatGroup1);
        yAxisNegative = initAxis(Axis.Y, true, greenMatGroup1);
        // z
        zAxis = initAxis(Axis.Z, false, blueMatGroup1);
        zAxisNegative = initAxis(Axis.Z, true, blueMatGroup1);
    }

    private Node initAxis(Axis axis, boolean negative, Material[] materials) {
        Node axisNode = drawAxis(axis, negative);
        axisNode.setMaterial(materials[1]);
        GizmoControl gizmoControl = new GizmoControl(materials[0], materials[1], materials[2]);
        axisNode.addControl(gizmoControl);
        return axisNode;
    }

    private Spatial drawPlane(Axis axis, boolean negative1, boolean negative2) {
        Vector3f origin = new Vector3f();
        float value1 = negative1 ? -0.2f : 0.2f;
        float value2 = negative2 ? -0.2f : 0.2f;
        switch (axis) {
            case XY:
                origin = new Vector3f(value1, value2, 0);
                break;
            case XZ:
                origin = new Vector3f(value1, 0, value2);
                break;
            case YZ:
                origin = new Vector3f(0, value1, value2);
                break;
            default:
                break;
        }
        Box box = new Box(0.2f, 0.2f, 0.001f);
        Geometry planeGeometry = new Geometry("plane", box);
        switch (axis) {
            case XZ:
                planeGeometry.rotate(new Quaternion().fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_X));
                break;
            case YZ:
                planeGeometry.rotate(new Quaternion().fromAngleAxis(-FastMath.PI / 2, Vector3f.UNIT_Y));
                break;
        }
        planeGeometry.setLocalTranslation(origin);
        planeGeometry.setName(MOVE_AXIS_TO_NAMES.get(axis));
        planeGeometry.setUserData(AXIS, axis.toString());
        return planeGeometry;
    }

    private void initPlanes() {
        planes = new Spatial[3][2][2];
        Spatial[][] xyPlanes = new Spatial[2][2];
        xyPlanes[0][0] = initPlane(Axis.XY, false, false, blueMatGroup2);
        xyPlanes[0][1] = initPlane(Axis.XY, false, true, blueMatGroup2);
        xyPlanes[1][0] = initPlane(Axis.XY, true, false, blueMatGroup2);
        xyPlanes[1][1] = initPlane(Axis.XY, true, true, blueMatGroup2);
        Spatial[][] xzPlanes = new Spatial[2][2];
        xzPlanes[0][0] = initPlane(Axis.XZ, false, false, greenMatGroup2);
        xzPlanes[0][1] = initPlane(Axis.XZ, false, true, greenMatGroup2);
        xzPlanes[1][0] = initPlane(Axis.XZ, true, false, greenMatGroup2);
        xzPlanes[1][1] = initPlane(Axis.XZ, true, true, greenMatGroup2);
        Spatial[][] yzPlanes = new Spatial[2][2];
        yzPlanes[0][0] = initPlane(Axis.YZ, false, false, redMatGroup2);
        yzPlanes[0][1] = initPlane(Axis.YZ, false, true, redMatGroup2);
        yzPlanes[1][0] = initPlane(Axis.YZ, true, false, redMatGroup2);
        yzPlanes[1][1] = initPlane(Axis.YZ, true, true, redMatGroup2);
        planes[0] = yzPlanes;
        planes[1] = xzPlanes;
        planes[2] = xyPlanes;
    }

    private Spatial initPlane(Axis axis, boolean n1, boolean n2, Material[] materials) {
        Spatial plane = drawPlane(axis, n1, n2);
        plane.setMaterial(materials[1]);
        plane.addControl(new GizmoControl(materials[0], materials[1], materials[2]));
        return plane;
    }

    private void initSemicircles() {
        xSemicircle = initSemicircle(Axis.X, redMatGroup1);
        ySemicircle = initSemicircle(Axis.Y, greenMatGroup1);
        zSemicircle = initSemicircle(Axis.Z, blueMatGroup1);
    }

    private Node initSemicircle(Axis axis, Material[] materials) {
        int _axis = Semicircle.X;
        switch (axis) {
            case Y:
                _axis = Semicircle.Y;
                break;
            case Z:
                _axis = Semicircle.Z;
                break;
        }
        Semicircle semiCircle = new Semicircle(_axis, Vector3f.ZERO, 1.5f, 32);
        Geometry semicircleGeo = new Geometry("semicircle", semiCircle);
        semicircleGeo.setMaterial(materials[1]);
        semicircleGeo.getMaterial().getAdditionalRenderState().setLineWidth(2);
        // load a semi torus for picking
        Node semiTorus = (Node) assetManager.loadModel("Models/semiTorus.j3o").clone();
        semiTorus.setMaterial(materials[1]);
        switch (axis) {
            case X:
                semiTorus.rotateUpTo(Vector3f.UNIT_Z);
                semiTorus.rotate(new Quaternion().fromAngleAxis(FastMath.HALF_PI,
                        semiTorus.getLocalRotation().inverse().mult(Vector3f.UNIT_Y)));
                break;
            case Y:
                semiTorus.rotate(0, FastMath.HALF_PI, 0);
                break;
            case Z:
                semiTorus.rotateUpTo(Vector3f.UNIT_Z);
                semiTorus.rotate(0, -FastMath.PI, 0);
                break;
        }
        semiTorus.getChild("SemiTorus").setUserData(AXIS, axis.toString());
        semiTorus.getChild("SemiTorus").setName(ROTATE_AXIS_TO_NAMES.get(axis));
        semiTorus.setCullHint(Spatial.CullHint.Always);

        Node semicircleNode = new Node();
        semicircleNode.attachChild(semicircleGeo);
        semicircleNode.attachChild(semiTorus);
        semicircleNode.setName(ROTATE_AXIS_TO_NAMES.get(axis));
        semicircleNode.addControl(new GizmoControl(materials[0], materials[1], materials[2]));
        return semicircleNode;
    }

    public void updateTool(Spatial objectPicked, Space space, TransformMode mode) {
        this.object = objectPicked;
        this.space = space;
        this.mode = mode;
    }

    public void updateTool() {
        resetTool();
        if (object == null) {
            return;
        }

        // make editor's camera follow the scene's camera (location, rotation but not the scaling)
        // this would make rotate the axes correctly easier and keep the axes not zoom with the scene's camera
        toolCamera.setLocation(objectCamera.getLocation());
        toolCamera.setRotation(objectCamera.getRotation());
        updateToolPosition();
        updateMoveAuxiliaries();

        switch (mode) {
            case MOVE_ONLY:
                updateMoveTool();
                break;
            case SCALE_ONLY:
                updateScaleTool();
                break;
            case ROTATE_ONLY:
                updateRotateTool();
                updateRotationAuxiliaries();
                break;
            default:
                updateRotateTool();
                updateMoveTool();
                updateScaleTool();
                updateRotationAuxiliaries();
                break;
        }
    }

    private void updateMoveAuxiliaries() {
        if (transforming) {
            if (connectionLine != null) {
                moveAuxNode.detachChild(connectionLine);
            }
            connectionLine = makeLine(Vector3f.ZERO,
                    moveAuxNode.worldToLocal(object.getWorldTranslation(), null), whiteMat);
            moveAuxNode.attachChild(connectionLine);
            return;
        }

        String axisSelected = transformNode.getUserData(TRANSFORMING);
        if (axisSelected != null) {
            auxiliaryNode.setLocalTransform(toolNode.getLocalTransform());
            transforming = true;
            Axis axis = Axis.valueOf(axisSelected);
            switch (axis) {
                case X:
                    moveAuxNode.attachChild(xAuxiliary);
                    break;
                case Y:
                    moveAuxNode.attachChild(yAuxiliary);
                    break;
                case Z:
                    moveAuxNode.attachChild(zAuxiliary);
                    break;
                case XY:
                    moveAuxNode.attachChild(xAuxiliary);
                    moveAuxNode.attachChild(yAuxiliary);
                    break;
                case XZ:
                    moveAuxNode.attachChild(xAuxiliary);
                    moveAuxNode.attachChild(zAuxiliary);
                    break;
                case YZ:
                    moveAuxNode.attachChild(yAuxiliary);
                    moveAuxNode.attachChild(zAuxiliary);
                    break;
            }
        }
    }

    private void updateRotationAuxiliaries() {
        String axisSelected = transformNode.getUserData(TRANSFORMING);
        if (axisSelected != null) {
            Axis axis = Axis.valueOf(axisSelected);
            switch (axis) {
                case X:
                    rotationAuxNode.attachChild(xRotationAux.getNode());
                    if (!xRotationAux.getStartPoint().equals(rotationStartPoint)) {
                        xRotationAux.setStartPoint(rotationStartPoint);
                    }
                    if (rotationDeltaAngle != 0) {
                        xRotationAux.setDeltaAngle(rotationDeltaAngle);
                        rotationDeltaAngle = 0;
                    }
                    break;
                case Y:
                    rotationAuxNode.attachChild(yRotationAux.getNode());
                    if (!yRotationAux.getStartPoint().equals(rotationStartPoint)) {
                        yRotationAux.setStartPoint(rotationStartPoint);
                    }
                    if (rotationDeltaAngle != 0) {
                        yRotationAux.setDeltaAngle(rotationDeltaAngle);
                        rotationDeltaAngle = 0;
                    }
                    break;
                case Z:
                    rotationAuxNode.attachChild(zRotationAux.getNode());
                    if (!zRotationAux.getStartPoint().equals(rotationStartPoint)) {
                        zRotationAux.setStartPoint(rotationStartPoint);
                    }
                    if (rotationDeltaAngle != 0) {
                        zRotationAux.setDeltaAngle(rotationDeltaAngle);
                        rotationDeltaAngle = 0;
                    }
                    break;
            }
            transforming = true;
        }
    }

    private void updateToolPosition() {
        Vector3f objectWorldTranslation = object.getWorldTranslation().clone();
        Quaternion objectWorldRotation = object.getWorldRotation().clone();
        Vector3f sc1 = objectCamera.getScreenCoordinates(objectWorldTranslation, null);
        Vector3f toolOrigin = toolCamera.getWorldCoordinates(new Vector2f(sc1.x, sc1.y), 0.5f);

        // rotate tool
        if (space == Space.LOCAL) {
            toolNode.setLocalRotation(objectWorldRotation);
        } else {
            // reset rotation
            toolNode.setLocalRotation(new Quaternion());
        }

        // translate tool
        ModelUtil.setWorldTranslation(toolNode, toolOrigin);
    }

    private void updateMoveTool() {
        // axes should always face to screen
        Vector3f unitX = Vector3f.UNIT_X.clone();
        Vector3f unitY = Vector3f.UNIT_Y.clone();
        Vector3f unitZ = Vector3f.UNIT_Z.clone();
        if (space == Space.LOCAL) {
            unitX = object.getWorldRotation().mult(unitX);
            unitY = object.getWorldRotation().mult(unitY);
            unitZ = object.getWorldRotation().mult(unitZ);
        }
        Vector3f eye = toolCamera.getLocation().subtract(object.getWorldTranslation()).normalize();
        int xNegative, yNegative, zNegative;
        if (unitX.dot(eye) < 0) {
            moveAxes.attachChild(xAxisNegative);
            xNegative = 1;
        } else {
            moveAxes.attachChild(xAxis);
            xNegative = 0;
        }
        if (unitY.dot(eye) < 0) {
            moveAxes.attachChild(yAxisNegative);
            yNegative = 1;
        } else {
            moveAxes.attachChild(yAxis);
            yNegative = 0;
        }
        if (unitZ.dot(eye) < 0) {
            moveAxes.attachChild(zAxisNegative);
            zNegative = 1;
        } else {
            moveAxes.attachChild(zAxis);
            zNegative = 0;
        }


        // planes
        movePlanes.attachChild(planes[0][yNegative][zNegative]);
        movePlanes.attachChild(planes[1][xNegative][zNegative]);
        movePlanes.attachChild(planes[2][xNegative][yNegative]);
    }

    private void updateRotateTool() {
        rotateTool.attachChild(xSemicircle);
        rotateTool.attachChild(ySemicircle);
        rotateTool.attachChild(zSemicircle);
        Vector3f eye = toolCamera.getLocation().subtract(object.getWorldTranslation()).normalize();
        Vector3f alignVector = space == Space.LOCAL ? object.getWorldRotation().inverse().mult(eye) : eye;
        xSemicircle.setLocalRotation(xSemicircle.getWorldRotation().clone().inverse());
        xSemicircle.setLocalRotation(new Quaternion().fromAngleAxis((float) Math.atan2(-alignVector.y,
                alignVector.z), Vector3f.UNIT_X));
        ySemicircle.setLocalRotation(ySemicircle.getWorldRotation().clone().inverse());
        ySemicircle.setLocalRotation(new Quaternion().fromAngleAxis((float) Math.atan2(alignVector.x,
                alignVector.z), Vector3f.UNIT_Y));
        zSemicircle.setLocalRotation(zSemicircle.getWorldRotation().clone().inverse());
        zSemicircle.setLocalRotation(new Quaternion().fromAngleAxis((float) Math.atan2(alignVector.y,
                alignVector.x), Vector3f.UNIT_Z));
    }

    private void updateScaleTool() {
    }

    private void resetTool() {
        moveAxes.getChildren().stream().iterator().forEachRemaining(moveAxes::detachChild);
        movePlanes.getChildren().stream().iterator().forEachRemaining(movePlanes::detachChild);
        rotateTool.getChildren().stream().iterator().forEachRemaining(rotateTool::detachChild);
        String axisSelected = transformNode.getUserData(TRANSFORMING);
        if (axisSelected == null) {
            moveAuxNode.getChildren().stream().iterator().forEachRemaining(moveAuxNode::detachChild);
            rotationAuxNode.getChildren().stream().iterator().forEachRemaining(rotationAuxNode::detachChild);
            transforming = false;
        }
    }

    public Node getTransformNode() {
        return transformNode;
    }

    public void setObject(Spatial object) {
        this.object = object;
    }

    public void setRotationStartPoint(Vector3f rotationStartPoint) {
        this.rotationStartPoint = rotationStartPoint;
//        yRotationAux.setStartPoint(rotateStartPoint);
////        zRotationAux.setStartPoint(rotateStartPoint);
    }

    public void setRotationDeltaAngle(float rotationDeltaAngle) {
        this.rotationDeltaAngle = rotationDeltaAngle;
    }
}
