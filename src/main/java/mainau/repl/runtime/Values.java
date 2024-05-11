package mainau.repl.runtime;

public interface Values {
    interface RuntimeValue {
        ValueType type();
    }

    interface NumberValue extends RuntimeValue {
        @Override default ValueType type() { return ValueType.NUMBER; }
        float value();
    }
    interface BooleanValue extends RuntimeValue {
        @Override default ValueType type() { return ValueType.BOOLEAN; }
        boolean value();
    }
    interface NullValue extends RuntimeValue {
        @Override default ValueType type() { return ValueType.NULL; }
    }
}
