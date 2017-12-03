package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.main.phases.communication.ModelCheckingSettings;

public interface MCSettingsTransformer {

    ModelCheckingSettings getMcSettings();
}
