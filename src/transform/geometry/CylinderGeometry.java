package transform.geometry;

import com.jme3.scene.shape.Cylinder;

/**
 * @author Chen Jiongyu
 */
public class CylinderGeometry extends BaseGeometry {


    public CylinderGeometry() {
        this(.4f, .4f);
    }

    public CylinderGeometry(float radius, float height) {
        this(radius, radius, height);
    }

    public CylinderGeometry(float topRadius, float bottomRadius, float height) {
        this(DEFAULT_AXIS_SAMPLES, DEFAULT_RADIAL_SAMPLES, topRadius, bottomRadius, height, true);
    }

    public CylinderGeometry(int axisSamples, int radialSamples, float topRadius, float bottomRadius, float height, boolean close) {
        super();
        Cylinder cylinder = new Cylinder(axisSamples, radialSamples, topRadius, bottomRadius, height, close, false);
        setMesh(cylinder);
    }

    public Cylinder getCylinder() {
        return (Cylinder) getMesh();
    }

    public int getAxisSamples() {
        return getCylinder().getAxisSamples();
    }

    public int getRadialSamples() {
        return getCylinder().getRadialSamples();
    }

    public float getHeight() {
        return getCylinder().getHeight();
    }

    public float getTopRadius() {
        return getCylinder().getRadius();
    }

    public float getBottomRadius() {
        return getCylinder().getRadius2();
    }

    public void updateCylinder(float height) {
        updateCylinder(getTopRadius(), height);
    }

    public void updateCylinder(float topRadius, float height) {
        updateCylinder(topRadius, topRadius, height);
    }

    public void updateCylinder(float topRadius, float bottomRadius, float height) {
        updateCylinder(getAxisSamples(), getRadialSamples(), topRadius, bottomRadius, height, true);
    }

    public void updateCylinder(int axisSamples, int radialSamples, float topRadius, float bottomRadius, float height, boolean close) {
        getCylinder().updateGeometry(axisSamples, radialSamples, topRadius, bottomRadius, height, close, false);
        updateModelBound();
        getCylinder().clearCollisionData();
    }
}

