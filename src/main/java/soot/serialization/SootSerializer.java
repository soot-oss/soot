package soot.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.objenesis.strategy.StdInstantiatorStrategy;

import soot.G;
import soot.Scene;
import soot.Singletons;
import soot.UnitPatchingChain;
import soot.util.HashChain;
import soot.util.Numberable;
import soot.util.NumberedString;
import soot.util.SmallNumberedMap;

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

    this.register(NumberedString.class, new Serializer<NumberedString>() {
      @Override
      public void write(Kryo kryo, Output output, NumberedString object) {
        output.writeString(object.getString());
      }

      @Override
      public NumberedString read(Kryo kryo, Input input, Class type) {
        // we have to create a new one and register it to the scene
        return Scene.v().getSubSigNumberer().findOrAdd(input.readString());
      }
    });

    this.register(SmallNumberedMap.class, new Serializer<SmallNumberedMap>() {
      @Override
      public void write(Kryo kryo, Output output, SmallNumberedMap object) {
        ArrayList<Numberable> keys = new ArrayList<>();
        ArrayList values = new ArrayList<>();

        Iterator<Numberable> iterator = object.keyIterator();
        while (iterator.hasNext()) {
          Numberable key = iterator.next();
          keys.add(key);
          values.add(object.get(key));
        }

        writeObject(output, keys);
        writeObject(output, values);
      }

      @Override
      public SmallNumberedMap read(Kryo kryo, Input input, Class type) {
        List<Numberable> keys = readObject(input, ArrayList.class);
        List values = readObject(input, ArrayList.class);

        SmallNumberedMap map = new SmallNumberedMap();

        for (int i = 0; i < keys.size(); i++) {
          map.put(keys.get(i), values.get(i));
        }

        return map;
      }
    });
  }

  public <T> T readObject(Input in, T instance) {
    Class<?> instanceType = instance.getClass();
    Registration oldReg = this.register(instanceType);

    // make sure we take the given instance as the "created" one
    oldReg.setInstantiator(() -> {
      // reset registration so we do not keep the instantiator for all instances of instanceType.
      // unregister does not work since the reg is not mapped to an int when we are running in
      // no-registration-required-mode
      // this just overwrites the registration and thereby resets the instantiator
      getClassResolver().registerImplicit(instance.getClass());
      return instance;
    });

    return (T) this.readObject(in, instanceType);
  }

  public static SootSerializer v() {
    return G.v().soot_serialization_SootSerializer();
  }
}
