package transform.translate;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

/**
 * @author Chen Jiongyu
 */
public class MoveTest extends SimpleApplication {
    public static void main(String[] args) {
        MoveTest app = new MoveTest();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.setSamples(16);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {

    }
}
