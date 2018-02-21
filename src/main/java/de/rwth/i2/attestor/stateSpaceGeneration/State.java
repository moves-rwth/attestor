package de.rwth.i2.attestor.stateSpaceGeneration;

public interface State {

    int INVALID_STATE_SPACE_ID = -1;

    int getStateSpaceId();

    void setStateSpaceId(int id);

    int size();
}
