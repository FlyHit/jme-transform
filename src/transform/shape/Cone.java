package transform.shape;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

/**
 * @author Chen Jiongyu
 */
public class Cone extends Mesh {
    private final Vector3f center;
    private final Vector3f upVector;
    private final float radius;
    private final int sample;

    public Cone(Vector3f center, Vector3f upVector, float radius, int sample) {
        this.center = center;
        this.upVector = upVector;
        this.radius = radius;
        this.sample = sample;

        setMode(Mode.Triangles);
    }
}
