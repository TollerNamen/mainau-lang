package mainau.repl.runtime;

import mainau.compiler.error.ErrorType;
import mainau.compiler.logging.MessageType;
import mainau.compiler.logging.Output;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private Session parent = null;
    private ProcessTask task;
    private final Map<String, ValuesImpl.RuntimeValue> variableMap = new HashMap<>();

    private void putVariable(String name, ValuesImpl.RuntimeValue value) {
        variableMap.put(name, value);
    }

    private ValuesImpl.RuntimeValue getVariable(String name) {
        return variableMap.get(name);
    }

    public Session(Session parent, ProcessTask task) {
        this.parent = parent;
        this.task = task;
    }
    public Session(Session parent) {
        this.parent = parent;
    }
    public Session() {}

    public void setParent(Session parent) {
        this.parent = parent;
    }

    public void setTask(ProcessTask task) {
        this.task = task;
    }

    public ValuesImpl.RuntimeValue lookUpVariable(String name) {
        Session session = resolveVariable(name);
        if (session == null) {
            task.insertError(new RuntimeError(ErrorType.NOT_FOUND, "Variable '" + name + "' not found"));
            return new ValuesImpl.NullValue();
        }
        var value = session.getVariable(name);
        if (value == null)
            task.insertError(new RuntimeError(ErrorType.UNASSIGNED_VARIABLE_QUERIED, "Variable '" + name + "' has not been assigned yet"));
        return value;
    }

    public ValuesImpl.RuntimeValue assignToVariable(String name, ValuesImpl.RuntimeValue value) {
        if (!variableMap.containsKey(name)) {
            task.insertError(new RuntimeError(ErrorType.INVALID_ACTION, "Variable '" + name + "' does not exist"));
            return value;
        }
        variableMap.replace(name, value);
        return value;
    }

    public ValuesImpl.RuntimeValue declareVariable(String name, ValuesImpl.RuntimeValue value) {
        if (variableMap.containsKey(name)) {
            task.insertError(new RuntimeError(ErrorType.INVALID_ACTION, "Already existing variable '" + name + "' cannot be redefined"));
            return value;
        }
        variableMap.put(name, value);
        return value;
    }

    public Session resolveVariable(String name) {
        Output.simplyLog(MessageType.DEV, "\nname is: " + name +
                "\nvar found in first layer session: " + variableMap.containsKey(name) +
                "\n" + variableMap);
        if (variableMap.containsKey(name))
            return this;

        if (parent == null)
            return null;

        return parent.resolveVariable(name);
    }
}
