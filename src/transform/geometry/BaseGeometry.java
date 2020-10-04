package transform.geometry;

import com.jme3.scene.Geometry;

/**
 * @author Chen Jiongyu
 */
public class BaseGeometry extends Geometry {

    protected static final Integer DEFAULT_AXIS_SAMPLES = 16;
    protected static final Integer DEFAULT_RADIAL_SAMPLES = 32;

    public BaseGeometry() {
        super();
        setName(this.getClass().getSimpleName());
//        setMaterial(createMaterial());
    }
}
