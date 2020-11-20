package ch.yax.connect.quickstart.rest;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.kafka.connect.health.ConnectClusterState;
import org.apache.kafka.connect.health.ConnectorHealth;
import org.apache.kafka.connect.health.ConnectorState;
import org.apache.kafka.connect.health.TaskState;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Builder
@ToString
@Getter
public class HealthResponse {
    private final HealthState status;
    private final List<ConnectorHealthResponse> connectors;

    public static HealthResponse from(final ConnectClusterState clusterState) {
        final List<ConnectorHealthResponse> connectorHealth = new LinkedList<>();
        clusterState.connectors().forEach(connectorName -> {
            connectorHealth.add(ConnectorHealthResponse.from(clusterState.connectorHealth(connectorName)));
        });

        final AtomicBoolean allOk = new AtomicBoolean(true);

        connectorHealth.forEach(connector -> {
            if (HealthState.DOWN.equals(connector.getStatus())) {
                allOk.set(false);
            }

            connector.getTasks().forEach(task -> {
                if (HealthState.DOWN.equals(task.getStatus())) {
                    allOk.set(false);
                }
            });

        });

        final HealthState allState = allOk.get() ? HealthState.UP : HealthState.DOWN;
        return HealthResponse.builder().connectors(connectorHealth).status(allState).build();

    }

}

@Builder
@ToString
@Getter
class ConnectorHealthResponse {
    private final HealthState status;
    private final String connectorName;
    private final String connectorState;
    private final String connectorErrors;
    private final List<TaskHealthResponse> tasks;


    public static ConnectorHealthResponse from(final ConnectorHealth connectorHealth) {
        final HealthState status;

        final String name = connectorHealth.name();
        final ConnectorState connectorState = connectorHealth.connectorState();
        final Map<Integer, TaskState> taskStates = connectorHealth.tasksState();

        final List<TaskHealthResponse> taskHealthResponses = new LinkedList<>();


        taskStates.forEach((k, v) -> {
            taskHealthResponses.add(TaskHealthResponse.from(v));
        });

        if (connectorState.state().equals("RUNNING")) {
            status = HealthState.UP;
        } else {
            status = HealthState.DOWN;
        }

        return ConnectorHealthResponse.builder()
                .connectorName(name)
                .connectorState(connectorState.state())
                .connectorErrors(connectorState.traceMessage())
                .tasks(taskHealthResponses)
                .status(status).build();
    }


}


@Builder
@ToString
@Getter
class TaskHealthResponse {

    private final int taskId;
    private final HealthState status;
    private final String taskState;
    private final String taskErrors;

    public static TaskHealthResponse from(final TaskState taskState) {
        final HealthState status;
        if (taskState.state().equals("RUNNING")) {
            status = HealthState.UP;
        } else {
            status = HealthState.DOWN;
        }

        return TaskHealthResponse.builder()
                .taskId(taskState.taskId())
                .taskState(taskState.state())
                .taskErrors(taskState.traceMessage())
                .status(status).build();

    }
}
