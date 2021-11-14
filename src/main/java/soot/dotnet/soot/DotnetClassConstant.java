package soot.dotnet.soot;

import soot.jimple.ClassConstant;

/**
 * Expand ClassConstant with .NET type name converter
 */
public class DotnetClassConstant extends ClassConstant {

    private DotnetClassConstant(String s) {
        super(convertDotnetClassToJvmDescriptor(s));
    }

    /**
     * Convert Dotnet Class to Java Descriptor
     * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3
     * @param s class to convert
     * @return converted to descriptor
     */
    private static String convertDotnetClassToJvmDescriptor(String s) {
        try {
            return "L" + s.replace(".", "/").replace("+", "$") + ";";
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert Dotnet class \"" + s + "\" to JVM Descriptor: " + e);
        }
    }

    public static DotnetClassConstant v(String value) {
        return new DotnetClassConstant(value);
    }

    // In this case, equals should be structural equality.
    @Override
    public boolean equals(Object c) {
        return (c instanceof ClassConstant && ((ClassConstant) c).value.equals(this.value));
    }
}
