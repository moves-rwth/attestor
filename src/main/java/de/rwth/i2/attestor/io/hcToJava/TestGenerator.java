package de.rwth.i2.attestor.io.hcToJava;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.types.Type;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestGenerator {

    private static final String INDENT = "    ";

    private final OutputStreamWriter writer;
    private final String methodName;
    private final String className;
    private final String packageName;
    private final HeapConfiguration input;
    private final HcToJava inputTranslator;

    private final List<String> parameterNames = new ArrayList<>();
    private final List<String> parameterTypeNames = new ArrayList<>();

    public TestGenerator(String methodName, String className, String packageName, HeapConfiguration input, OutputStreamWriter writer) {

        this.writer = writer;
        this.methodName = methodName;
        this.className = className;
        this.packageName = packageName;

        // TODO derive concrete input
        this.input = input;
        this.inputTranslator = new HcToJava(input, writer, INDENT + INDENT);

        extractMethodParameters();

        checkTestedClass();
    }

    private void checkTestedClass() {

        try {

            Class<?> testedClass = Class.forName(packageName + "." + className);
            Class<?>[] parameterTypes = new Class[parameterTypeNames.size()];
            for (int i = 0; i < parameterTypeNames.size(); i++) {
                parameterTypes[i] = Class.forName(parameterTypeNames.get(i));
            }
            Method testedMethod = testedClass.getMethod(methodName, parameterTypes);
            assert testedMethod != null;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The provided class to test could not be found.");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The provided method to test could not be found.");
        }
    }

    private void extractMethodParameters() {

        int counter = 0;
        int varId = input.variableWith("@param" + counter);
        while (varId != HeapConfiguration.INVALID_ELEMENT) {
            String name = input.nameOf(varId);
            int varTarget = input.targetOf(varId);
            Type targetType = input.nodeTypeOf(varTarget);
            parameterNames.add(name);
            parameterTypeNames.add(targetType.toString());
            ++counter;
        }

    }

    public void translate() throws IOException {

        definePackage();
        defineImports();
        beginClass();
        inputTranslator.declareVariables("private");
        initializeHeap();
        defineTest();
        endClass();
    }

    private void definePackage() throws IOException {

        String packageDefinition = "package " + packageName + ";";
        writer.write(packageDefinition);
    }

    private void defineImports() throws IOException {

        writer.write(System.lineSeparator());
        writer.write("import org.junit.Before;");
        writer.write(System.lineSeparator());
        writer.write("import org.junit.Test;");
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());
    }

    private void beginClass() throws IOException {

        StringBuilder builder = new StringBuilder();
        builder.append("class ")
                .append(className)
                .append("Test")
                .append(methodName)
                .append("_")
                .append(input.hashCode())
                .append(" {");
        writer.write(builder.toString());
    }

    private void endClass() throws IOException {

        writer.write("}");
    }

    private void initializeHeap() throws IOException {

        StringBuilder builder = new StringBuilder()
                .append(INDENT)
                .append("@Before")
                .append(System.lineSeparator())
                .append("public void setup")
                .append("() {")
                .append(System.lineSeparator());
        writer.write(builder.toString());

        inputTranslator.translateHc();

        writer.write(System.lineSeparator() + "}");
    }

    private void defineTest() throws IOException {

        StringBuilder builder = new StringBuilder()
                .append(INDENT)
                .append("@Test")
                .append(System.lineSeparator())
                .append("public void test")
                .append(methodName.substring(0, 1).toUpperCase())
                .append(methodName.substring(1))
                .append("() {")
                .append(System.lineSeparator())
                .append(INDENT)
                .append("try {")
                .append(System.lineSeparator())
                .append(INDENT)
                .append(INDENT)
                .append(methodName)
                .append("(");
        for (int i = 0; i < parameterNames.size(); i++) {
            builder.append(parameterNames.get(i));
            if (i < parameterNames.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(");")
                .append(System.lineSeparator())
                .append(INDENT)
                .append("} catch(Exception e) {")
                .append(System.lineSeparator())
                .append(INDENT)
                .append(INDENT)
                .append("e.printStackTrace();")
                .append(System.lineSeparator())
                .append(INDENT)
                .append(INDENT)
                .append("fail()")
                .append(System.lineSeparator())
                .append(INDENT)
                .append("} catch(Exception e) {")
                .append("}")
        ;
        writer.write(builder.toString());
    }


}
