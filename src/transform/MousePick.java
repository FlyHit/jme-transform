package transform;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;

/**
 * @author Chen Jiongyu
 */
public class MousePick extends SimpleApplication {
    Node shootables;
    Geometry mark;

    public MousePick() {
    }

    public static void main(String[] args) {
        MousePick app = new MousePick();
        app.start();
    }

    public void simpleInitApp() {
        this.flyCam.setEnabled(false);
        this.initMark();
        this.shootables = new Node("Shootables");
        this.rootNode.attachChild(this.shootables);
        this.shootables.attachChild(this.makeCube("a Dragon", -2.0F, 0.0F, 1.0F));
        this.shootables.attachChild(this.makeFloor());
    }

    public void simpleUpdate(float tpf) {
        Vector3f origin = this.cam.getWorldCoordinates(this.inputManager.getCursorPosition(), 0.0F);
        Vector3f direction = this.cam.getWorldCoordinates(this.inputManager.getCursorPosition(), 0.3F);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        this.shootables.collideWith(ray, results);
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            this.mark.setLocalTranslation(closest.getContactPoint());
            Quaternion q = new Quaternion();
            q.lookAt(closest.getContactNormal(), Vector3f.UNIT_Y);
            this.mark.setLocalRotation(q);
            this.rootNode.attachChild(this.mark);
        } else {
            this.rootNode.detachChild(this.mark);
        }

    }

    protected Geometry makeCube(String name, float x, float y, float z) {
        Box box = new Box(1.0F, 1.0F, 1.0F);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }

    protected Geometry makeFloor() {
        Box box = new Box(15.0F, 0.2F, 15.0F);
        Geometry floor = new Geometry("the Floor", box);
        floor.setLocalTranslation(0.0F, -4.0F, -5.0F);
        Material mat1 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat1);
        return floor;
    }

    protected void initMark() {
        Arrow arrow = new Arrow(Vector3f.UNIT_Z.mult(2.0F));
        this.mark = new Geometry("BOOM!", arrow);
        Material mark_mat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.getAdditionalRenderState().setLineWidth(3.0F);
        mark_mat.setColor("Color", ColorRGBA.Red);
        this.mark.setMaterial(mark_mat);
    }
}
