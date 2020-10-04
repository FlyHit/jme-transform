package transform.tool;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * @author Chen Jiongyu
 */
public class TransformControl extends AbstractControl {
    private final TransformTool transformTool;

    public TransformControl(TransformTool transformTool) {
        this.transformTool = transformTool;
    }

    @Override
    protected void controlUpdate(float tpf) {
        transformTool.updateTool();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
