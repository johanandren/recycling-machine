package com.example;

import com.google.protobuf.Empty;
import kalix.javasdk.valueentity.ValueEntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecyclingMachineEntity extends AbstractRecyclingMachineEntity {

  private static final Logger logger = LoggerFactory.getLogger(RecyclingMachineEntity.class);

  private final String machineId;

  public RecyclingMachineEntity(ValueEntityContext context) {
    this.machineId = context.entityId();
  }

  @Override
  public RecyclingMachineApi.MachineState emptyState() {
    return RecyclingMachineApi.MachineState.getDefaultInstance();
  }

  @Override
  public Effect<Empty> recycleCans(RecyclingMachineApi.MachineState currentState, RecyclingMachineApi.RecycleCansRequest recycleCansRequest) {
    logger.info("Request for machine [{}] to recycle [{}] cans", machineId, currentState.getCans());
    var newState = currentState.toBuilder()
        .setCans(currentState.getCans() + recycleCansRequest.getCans())
        .build();
    if (newState.getCans() > 100)
      return effects().error("Machine is full");
    else
      return effects().updateState(newState).thenReply(Empty.getDefaultInstance());
  }

  @Override
  public Effect<RecyclingMachineApi.MachineState> getMachineState(RecyclingMachineApi.MachineState currentState, RecyclingMachineApi.GetMachineStateRequest getMachineStateRequest) {
    logger.info("Request for inspecting machine [{}] state", machineId);
    return effects().reply(currentState);
  }
}
