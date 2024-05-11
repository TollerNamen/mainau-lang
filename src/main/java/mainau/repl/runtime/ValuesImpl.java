package mainau.repl.runtime;

public class ValuesImpl {
    public static class RuntimeValue implements Values.RuntimeValue {
        @Override public ValueType type() { return ValueType.GENERIC; }
    }

    public static class NumberValue extends RuntimeValue implements Values.NumberValue {
        private final float value;
        public NumberValue(float value) {
            this.value = value;
        }

        @Override public ValueType type() { return ValueType.NUMBER; }
        @Override public float value() { return value; }
        @Override public String toString() { return "NumberValue(type=" + type() + ",value=" + value + ")"; }
    }
    public static class BooleanValue extends RuntimeValue implements Values.BooleanValue {
        private final boolean value;
        public BooleanValue(boolean value) {
            this.value = value;
        }

        @Override public ValueType type() { return ValueType.BOOLEAN; }
        @Override public boolean value() { return value; }
    }
    public static class NullValue extends RuntimeValue implements Values.NullValue {
        @Override public ValueType type() { return ValueType.NULL; }
        @Override public String toString() { return "NullValue(type=" + type() + ")"; }
    }
}
