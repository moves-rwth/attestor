package de.rwth.i2.attestor.main.environment;

import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.settings.OptionSettings;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;

public class DefaultScene implements Scene {

    private final GeneralType.Factory typeFactory = new GeneralType.Factory();
    private final BasicSelectorLabel.Factory basicSelectorLabelFactory = new BasicSelectorLabel.Factory();
    private final OptionSettings options = new OptionSettings();

    @Override
    public Type getType(String name) {
        return typeFactory.get(name);
    }

    @Override
    public SelectorLabel getSelectorLabel(String name) {

        if(options.isIndexedMode()) {
            SelectorLabel sel = basicSelectorLabelFactory.get(name);
            return new AnnotatedSelectorLabel(sel, "");
        } else {
            return basicSelectorLabelFactory.get(name);
        }
    }

    @Override
    public OptionSettings options() {
        return options;
    }
}
