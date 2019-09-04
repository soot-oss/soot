package soot.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.StdInstantiatorStrategy;

import soot.G;
import soot.Scene;
import soot.Singletons;
import soot.UnitPatchingChain;
import soot.util.HashChain;
import soot.util.NumberedString;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

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
    registerSpecialInstantiators();
  }

  private void registerSpecialInstantiators() {
    this.getRegistration(UnitPatchingChain.class).setInstantiator(() -> new UnitPatchingChain(new HashChain<>()));
  }

  private void registerSpecialSerializers() {
    this.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
    this.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
    this.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
    this.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
    this.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
    this.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
    this.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
    this.register(GregorianCalendar.class, new GregorianCalendarSerializer());
    this.register(InvocationHandler.class, new JdkProxySerializer());
    UnmodifiableCollectionsSerializer.registerSerializers(this);
    SynchronizedCollectionsSerializer.registerSerializers(this);

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
    Registration oldReg = this.register(instanceType);

    // make sure we take the given instance as the "created" one
    oldReg.setInstantiator(new ExistingObjectInstantiator(instance));

    return (T) this.readObject(in, instanceType);
  }

  public static SootSerializer v() {
    return G.v().soot_serialization_SootSerializer();
  }

  private class ExistingObjectInstantiator<T> implements ObjectInstantiator<T> {

    private final T instance;

    public ExistingObjectInstantiator(T instance) {
      this.instance = instance;
    }

    @Override
    public T newInstance() {
      // reset registration so we do not keep the instantiator for all instances of instanceType.
      // unregister does not work since the reg is not mapped to an int when we are running in no-registration-required-mode
      // this just overwrites the registration and thereby resets the instantiator
      getClassResolver().registerImplicit(instance.getClass());
      return instance;
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
