package transform.shape.fill;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Quad;
import com.jme3.util.BufferUtils;

import java.util.Arrays;

import static com.jme3.scene.VertexBuffer.Type;

public class ShapeFiller {
    private final Material mat;
    private RenderQueue.Bucket visibility;
    private int radicalSamples;
    private float xOrRadius;
    private float y;
    private float angle;
    private ColorRGBA outerColor;
    private ColorRGBA innerColor;
    private float edgeWidth;
    private Geometry geom;
    private Shape shape;

    public ShapeFiller(AssetManager assetManager, float xOrRadius, float y,
                       float angle, int radicalSamples,
                       String matPath, RenderQueue.Bucket visibility, Shape shape,
                       ColorRGBA outerColor, ColorRGBA innerColor, float edgeWidth) {
        this.visibility = visibility;
        this.radicalSamples = radicalSamples;
        this.xOrRadius = xOrRadius;
        this.angle = angle;
        if (shape.equals(Shape.Quad)) {
            this.y = y;
        } else {
            this.y = 1;
        }
        this.shape = shape;
        this.outerColor = outerColor;
        this.innerColor = innerColor;
        this.edgeWidth = edgeWidth;

        if (shape.equals(Shape.Quad)) {
            geom = new Geometry("Quad", new Quad(xOrRadius, y));
            geom.setQueueBucket(visibility);
        }
        switch (shape) {
            case Quad:
                geom = new Geometry("Quad", new Quad(xOrRadius, y));
                geom.setQueueBucket(visibility);
                break;
            case Circle:
                UvCircle c = new UvCircle(xOrRadius, radicalSamples, angle);
                geom = new Geometry("Circle", c);
                geom.setQueueBucket(visibility);
                break;
        }

        mat = new Material(assetManager, matPath);
        buildGeometry();
    }

    public ShapeFiller(AssetManager assetManager, float xOrRadius, float y,
                       float angle, int radicalSamples, String matPath,
                       RenderQueue.Bucket visibility, Shape shape) {
        this(assetManager, xOrRadius, y, angle, radicalSamples, matPath, visibility, shape,
                new ColorRGBA(64f / 255, 94f / 255, 247f / 255, 1), new ColorRGBA(200f / 255,
                        230f / 255, 251f / 255, 1), 0.008f);
    }

    private void buildGeometry() {
        float outR = outerColor.getRed();
        float outG = outerColor.getGreen();
        float outB = outerColor.getBlue();
        float outA = outerColor.getAlpha();

        float inR = innerColor.getRed();
        float inG = innerColor.getGreen();
        float inB = innerColor.getBlue();
        float inA = innerColor.getAlpha();

        mat.setVector4("InnerColor", new Vector4f(inR, inG, inB, inA));
        mat.setVector4("OuterColor", new Vector4f(outR, outG, outB, outA));
        mat.setFloat("EdgeWidth", edgeWidth);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        switch (shape) {
            case Circle:
                mat.setInt("RadicalSamples", radicalSamples);
                break;
            case Quad:
                mat.setInt("RadicalSamples", 4);
        }

        geom.setMaterial(mat);
    }

    public void setVisibility(RenderQueue.Bucket visibility) {
        this.visibility = visibility;
        geom.setQueueBucket(visibility);
    }

    public void setOuterColor(ColorRGBA outerColor) {
        this.outerColor = outerColor;
        float outR = outerColor.getRed();
        float outG = outerColor.getGreen();
        float outB = outerColor.getBlue();
        float outA = outerColor.getAlpha();
        mat.setVector4("OuterColor", new Vector4f(outR, outG, outB, outA));
    }

    public void setInnerColor(ColorRGBA innerColor) {
        this.innerColor = innerColor;
        float inR = innerColor.getRed();
        float inG = innerColor.getGreen();
        float inB = innerColor.getBlue();
        float inA = innerColor.getAlpha();
        mat.setVector4("InnerColor", new Vector4f(inR, inG, inB, inA));
    }

    public void setEdgeWidth(float edgeWidth) {
        this.edgeWidth = edgeWidth;
        mat.setFloat("EdgeWidth", edgeWidth);
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        switch (shape) {
            case Quad:
                geom = new Geometry("Quad", new Quad(xOrRadius, y));
                geom.setQueueBucket(visibility);
                this.radicalSamples = 4;
                break;
            case Circle:
                UvCircle c = new UvCircle(xOrRadius, radicalSamples, angle);
                geom = new Geometry("Circle", c);
                geom.setQueueBucket(visibility);
                this.radicalSamples = 60;
                break;
        }
        buildGeometry();
    }

    public void setRadicalSamples(int radicalSamples) {
        this.radicalSamples = radicalSamples;
    }

    public void setxOrRadius(float xOrRadius) {
        this.xOrRadius = xOrRadius;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        ((UvCircle) geom.getMesh()).setAngle(angle);
        buildGeometry();
    }

    public enum Shape {
        Quad,
        Circle
    }
}

class UvCircle extends Mesh {
    // 半径
    private float radius = 1f;
    // 段数
    private int samples = 100;
    private float angle;


    public UvCircle() {
        super();
        updateGeometry();
    }

    public UvCircle(float radius, int samples, float angle) {
        super();
        this.radius = radius;
        this.samples = samples;
        this.angle = angle;
        updateGeometry();
    }

    public void updateGeometry() {
        Vector3f center = new Vector3f(radius, radius, 0); // 圆心
        // 单位角度
        float deltaAngle = angle / samples * 0.0174532930555555f;
        float currentAngle = 0f; // 角度从0以单位角度位为增量加至目标角度angle

        //顶点
        Vector3f[] vertices = new Vector3f[samples * 3];
        for (int i = 0; i < samples; i++) {
            float cosA = (float) Math.cos(currentAngle);
            float sinA = (float) Math.sin(currentAngle);
            currentAngle += deltaAngle;
            float cosA2 = (float) Math.cos(currentAngle);
            float sinA2 = (float) Math.sin(currentAngle);

            vertices[i * 3] = center;
            vertices[i * 3 + 1] = new Vector3f(center.x + radius * cosA, center.y + radius * sinA, 0);
            vertices[i * 3 + 2] = new Vector3f(center.x + radius * cosA2, center.y + radius * sinA2, 0);
        }


        //法向量
        Vector3f[] normals = new Vector3f[vertices.length];
        Arrays.fill(normals, Vector3f.UNIT_Z);

        // 顶点索引
        int[] indices = new int[vertices.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        //贴图坐标（U,V）
        float[] textureCoords = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            textureCoords[i * 2] = vertices[i].x / (2 * radius);
            textureCoords[i * 2 + 1] = vertices[i].y / (2 * radius);
        }

        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoords));
        setBuffer(Type.Index, 3, indices);

        updateBound();
        updateCounts();
        setStatic();
    }

    public void setAngle(float angle) {
        this.angle = angle;
        updateGeometry();
    }
}