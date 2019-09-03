package soot.serialization;

import com.esotericsoftware.kryo.ClassResolver;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;
import soot.G;
import soot.Scene;
import soot.Singletons;
import soot.UnitPatchingChain;
import soot.util.HashChain;
import soot.util.NumberedString;

/**
 * Provides serialization functionality for the binary front- and backend
 * 
 * @author Manuel Benz at 2019-08-27
 */
public class SootSerializer extends Kryo {

  public SootSerializer(Singletons.Global g) {
    this.setReferences(true);
    this.setRegistrationRequired(false);
    this.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
    this.setGenerics(com.esotericsoftware.kryo.util.NoGenericsHandler.INSTANCE);
    registerSpecialSerializers();
  }

  private void registerSpecialSerializers() {
    this.register(UnitPatchingChain.class, new DelegateSerializer() {
      @Override
      public Object read(Kryo kryo, Input input, Class type) {
        return new UnitPatchingChain(new HashChain<>());
      }
    });

    this.register(NumberedString.class, new DelegateSerializer() {
      @Override
      public Object read(Kryo kryo, Input input, Class type) {
        NumberedString read = (NumberedString) super.read(kryo, input, type);
        String string = read.getString();
        // we have to create a new one and register it to the scene
        return Scene.v().getSubSigNumberer().findOrAdd(string);
      }
    });
  }

  public <T> T readObject(Input in, T instance) {
    Class<?> instanceType = instance.getClass();
    ClassResolver classResolver = this.getClassResolver();
    Registration oldReg = classResolver.getRegistration(instanceType);

    // make sure we take the given instance as the "created" one
    this.setInstantiatorStrategy(new ExistingObjectInstantiator(instance, getInstantiatorStrategy()));

    this.readObject(in, instanceType);

    // reset registration so we do not keep the instantiator for all instances of instanceType.
    // unregister does not work since the reg is not mapped to an int when we are running in no-registration-required-mode
    // this just overwrites the registration and thereby resets the instantiator
    classResolver.registerImplicit(instanceType);

    return instance;
  }

  public static SootSerializer v() {
    return G.v().soot_serialization_SootSerializer();
  }

  private static class ExistingObjectInstantiator<T> implements InstantiatorStrategy {

    private final T instance;
    private final InstantiatorStrategy fallBack;

    public ExistingObjectInstantiator(T instance, InstantiatorStrategy fallBack) {
      this.instance = instance;
      this.fallBack = fallBack;
    }

    @Override
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
      // we only want to return the given instance for the very first call
      if (instance != null && type.equals(instance.getClass())) {
        return () -> (T) instance;
      }

      return fallBack.newInstantiatorOf(type);
    }
  }

  private class DelegateSerializer<T> extends Serializer<T> {

    @Override
    public void write(Kryo kryo, Output output, T object) {
      getDefaultSerializer(object.getClass()).write(kryo, output, object);
    }

    @Override
    public T read(Kryo kryo, Input input, Class<? extends T> type) {
      return (T) getDefaultSerializer(type).read(kryo, input, type);
    }
  }
}
