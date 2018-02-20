package de.rwth.i2.attestor.main.scene;

public abstract class SceneObject {

    private final Scene scene;

    protected SceneObject(Scene scene) {

        this.scene = scene;
    }

    protected SceneObject(SceneObject otherObject) {

        this.scene = otherObject.scene();
    }

    public Scene scene() {

        return scene;
    }
}
