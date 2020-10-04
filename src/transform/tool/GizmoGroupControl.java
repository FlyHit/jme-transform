package transform.tool;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In a gizmo group, when a gizmo is hovered, other gizmo will fade
 *
 * @author Chen Jiongyu
 */
public class GizmoGroupControl extends AbstractControl {
    public static final String NONE_HOVERED = "no_gizmo_hovered";
    public static final String GIZMO_HOVERED = "gizmo_hovered";
    private final List<Spatial> gizmoGroup;
    private final Map<String, List<Spatial>> gizmoCouple;
    private String gizmoName = NONE_HOVERED;

    public GizmoGroupControl(List<Spatial> gizmoGroup) {
        this.gizmoGroup = gizmoGroup;
        gizmoCouple = new HashMap<>();
    }

    @Override
    protected void controlUpdate(float tpf) {
        String gizmoHoveredName = spatial.getUserData(GIZMO_HOVERED);
        if (!gizmoName.equals(gizmoHoveredName)) {
            gizmoName = gizmoHoveredName;
            if (gizmoName.equals(NONE_HOVERED)) {
                for (Spatial gizmo : gizmoGroup) {
                    gizmo.setUserData(GizmoControl.STATE, GizmoControl.NON_FOCUS);
                }
            } else {
                List<Spatial> couple = new ArrayList<>();
                for (Spatial gizmo : gizmoGroup) {
                    if (gizmoHoveredName.equals(gizmo.getName())) {
                        gizmo.setUserData(GizmoControl.STATE, GizmoControl.HOVERED);
                        if (gizmoCouple.containsKey(gizmoHoveredName)) {
                            couple = gizmoCouple.get(gizmoHoveredName);
                            for (Spatial c : couple) {
                                c.setUserData(GizmoControl.STATE, GizmoControl.HOVERED);
                            }
                        }
                        continue;
                    }

                    if (!couple.contains(gizmo)) {
                        gizmo.setUserData(GizmoControl.STATE, GizmoControl.FADED);
                    }
                }
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void addGizmoCouple(String gizmoName, List<Spatial> couple) {
        gizmoCouple.put(gizmoName, couple);
    }
}
