package de.rwth.i2.attestor.phases.transformers;

import de.rwth.i2.attestor.phases.communication.ModelCheckingSettings;

public interface MCSettingsTransformer {

    ModelCheckingSettings getMcSettings();
}
