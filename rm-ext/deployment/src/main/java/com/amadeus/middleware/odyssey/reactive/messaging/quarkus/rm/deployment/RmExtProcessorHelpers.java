package com.amadeus.middleware.odyssey.reactive.messaging.quarkus.rm.deployment;

import com.amadeus.middleware.odyssey.reactive.messaging.core.Invoker;
import io.quarkus.arc.processor.BeanInfo;
import io.quarkus.deployment.util.HashUtil;
import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.lang.reflect.Modifier;

// A lot of code is taken from:
// https://github.com/quarkusio/quarkus/blob/master/extensions/smallrye-reactive-messaging/deployment/src/main/java/io/quarkus/smallrye/reactivemessaging/deployment/QuarkusMediatorConfigurationUtil.java

public class RmExtProcessorHelpers {
  static final String INVOKER_SUFFIX = "_OdysseyInvoker";

  public static String generateInvoker(BeanInfo bean, MethodInfo method, ClassOutput classOutput) {
    String baseName;
    if (bean.getImplClazz()
      .enclosingClass() != null) {
      baseName = io.quarkus.arc.processor.DotNames.simpleName(bean.getImplClazz()
        .enclosingClass()) + "_" + io.quarkus.arc.processor.DotNames.simpleName(
        bean.getImplClazz()
          .name());
    } else {
      baseName = io.quarkus.arc.processor.DotNames.simpleName(bean.getImplClazz()
        .name());
    }
    StringBuilder sigBuilder = new StringBuilder();
    sigBuilder.append(method.name())
      .append("_")
      .append(method.returnType()
        .name()
        .toString());
    for (Type i : method.parameters()) {
      sigBuilder.append(i.name()
        .toString());
    }
    String targetPackage = io.quarkus.arc.processor.DotNames.packageName(bean.getImplClazz()
      .name());
    String generatedName = targetPackage.replace('.', '/') + "/" + baseName + INVOKER_SUFFIX + "_" + method.name() + "_"
      + HashUtil.sha1(sigBuilder.toString());

    try (ClassCreator invoker = ClassCreator.builder()
      .classOutput(classOutput)
      .className(generatedName)
      .interfaces(Invoker.class)
      .build()) {

      FieldDescriptor beanInstanceField = invoker.getFieldCreator("beanInstance", Object.class)
        .getFieldDescriptor();

      // generate a constructor that bean instance an argument
      try (MethodCreator ctor = invoker.getMethodCreator("<init>", void.class, Object.class)) {
        ctor.setModifiers(Modifier.PUBLIC);
        ctor.invokeSpecialMethod(MethodDescriptor.ofConstructor(Object.class), ctor.getThis());
        ResultHandle self = ctor.getThis();
        ResultHandle config = ctor.getMethodParam(0);
        ctor.writeInstanceField(beanInstanceField, self, config);
        ctor.returnValue(null);
      }

      try (MethodCreator invoke = invoker
        .getMethodCreator(MethodDescriptor.ofMethod(generatedName, "invoke", Object.class, Object[].class))) {

        int parametersCount = method.parameters()
          .size();
        String[] argTypes = new String[parametersCount];
        ResultHandle[] args = new ResultHandle[parametersCount];
        for (int i = 0; i < parametersCount; i++) {
          // the only method argument of io.smallrye.reactive.messaging.Invoker is an object array so we need to pull
          // out
          // each argument and put it in the target method arguments array
          args[i] = invoke.readArrayValue(invoke.getMethodParam(0), i);
          argTypes[i] = method.parameters()
            .get(i)
            .name()
            .toString();
        }
        ResultHandle result = invoke.invokeVirtualMethod(MethodDescriptor.ofMethod(method.declaringClass()
            .name()
            .toString(), method.name(),
          method.returnType()
            .name()
            .toString(),
          argTypes), invoke.readInstanceField(beanInstanceField, invoke.getThis()), args);
        if (DotNames.VOID.equals(method.returnType()
          .name())) {
          invoke.returnValue(invoke.loadNull());
        } else {
          invoke.returnValue(result);
        }
      }
    }
    return generatedName.replace('/', '.');
  }

  public static Class<?> load(String className, ClassLoader cl) {
    switch (className) {
      case "boolean":
        return boolean.class;
      case "byte":
        return byte.class;
      case "short":
        return short.class;
      case "int":
        return int.class;
      case "long":
        return long.class;
      case "float":
        return float.class;
      case "double":
        return double.class;
      case "char":
        return char.class;
      case "void":
        return void.class;
      //missing default case
      default:
         // add default case
        break;

    }
    try {
      return Class.forName(className, false, cl);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }
}
