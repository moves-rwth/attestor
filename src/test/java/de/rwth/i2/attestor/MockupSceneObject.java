package de.rwth.i2.attestor;

import de.rwth.i2.attestor.main.scene.DefaultScene;
import de.rwth.i2.attestor.main.scene.SceneObject;

public class MockupSceneObject extends SceneObject {

    public MockupSceneObject() {

        super(new DefaultScene());

        scene().options().setRemoveDeadVariables(false);
    }
}
