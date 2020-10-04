package transform.tool;

import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.control.AbstractControl;

/**
 * for gizmos which have different color in different state
 *
 * @author Chen Jiongyu
 */
public class GizmoControl extends AbstractControl {
    public static final String HOVERED = "gizmo_focus";
    public static final String FADED = "gizmo_faded";
    public static final String NON_FOCUS = "gizmo_non_focus";
    public static final String STATE = "gizmo_state";
    private final Material lightMat;
    private final Material mat;
    private final Material fadedMat;
    private String currentState = NON_FOCUS;

    public GizmoControl(Material lightMat, Material mat, Material fadedMat) {
        this.lightMat = lightMat;
        this.mat = mat;
        this.fadedMat = fadedMat;
    }

    @Override
    protected void controlUpdate(float tpf) {
        String state = spatial.getUserData(STATE);
        if (!currentState.equals(state) && state != null) {
            currentState = state;
            switch (currentState) {
                case HOVERED:
                    spatial.setMaterial(lightMat);
                    spatial.setQueueBucket(RenderQueue.Bucket.Opaque);
                    break;
                case FADED:
                    spatial.setMaterial(fadedMat);
                    spatial.setQueueBucket(RenderQueue.Bucket.Transparent);
                    break;
                case NON_FOCUS:
                    spatial.setMaterial(mat);
                    spatial.setQueueBucket(RenderQueue.Bucket.Opaque);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
