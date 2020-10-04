package transform.shape;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.nio.FloatBuffer;

/**
 * @author Chen Jiongyu
 */
public class Semicircle extends Mesh {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    /**
     * The center.
     */
    private final Vector3f center;
    /**
     * The radius.
     */
    private final float radius;
    /**
     * The samples.
     */
    private final int samples;
    private final int axis;

    public Semicircle(float radius) {
        this(Vector3f.ZERO, radius, 16);
    }

    public Semicircle(float radius, int samples) {
        this(Vector3f.ZERO, radius, samples);
    }

    public Semicircle(Vector3f center, float radius, int samples) {
        super();
        this.center = center;
        this.radius = radius;
        this.samples = samples;
        this.axis = X;

        setMode(Mode.Lines);
        updateGeometry();
    }

    public Semicircle(int axis, Vector3f center, float radius, int samples) {
        super();
        this.center = center;
        this.radius = radius;
        this.samples = samples;
        this.axis = axis;

        setMode(Mode.Lines);
        updateGeometry();
    }

    protected void updateGeometry() {
        FloatBuffer positions = BufferUtils.createFloatBuffer(samples * 3);
        FloatBuffer normals = BufferUtils.createFloatBuffer(samples * 3);
        short[] indices = new short[samples * 2];

        float rate = FastMath.PI / (float) samples;
        float angle = 0;
        int idc = 0;
        for (int i = 0; i < samples; i++) {
            float x, y, z;
            switch (axis) {
                case X:
                    x = center.x;
                    y = FastMath.cos(angle) * radius + center.y;
                    z = FastMath.sin(angle) * radius + center.z;
                    break;
                case Y:
                    x = FastMath.cos(angle) * radius + center.x;
                    y = center.y;
                    z = FastMath.sin(angle) * radius + center.z;
                    break;
                case Z:
                    x = FastMath.sin(angle) * radius + center.x;
                    y = FastMath.cos(angle) * radius + center.y;
                    z = center.z;
                    break;
                default:
                    x = y = z = 0;
                    break;
            }

            positions.put(x).put(y).put(z);
            normals.put(new float[]{0, 1, 0});

            indices[idc++] = (short) i;
            if (i < samples - 1) {
                indices[idc++] = (short) (i + 1);
            } else {
                indices[idc++] = (short) i;
            }

            angle += rate;
        }

        setBuffer(VertexBuffer.Type.Position, 3, positions);
        setBuffer(VertexBuffer.Type.Normal, 3, normals);
        setBuffer(VertexBuffer.Type.Index, 2, indices);

        setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0, 0, 1, 1});

        updateBound();
    }

    @Override
    public void updateBound() {
        super.updateBound();
    }
}
