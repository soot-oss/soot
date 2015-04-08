package soot.util.annotations;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.AbstractInvocationHandler;

import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.util.annotations.AnnotationElemSwitch.KeyValuePair;
import static soot.util.annotations.AnnotationElemSwitch.toQuallifiedClassName;

public class AnnotationInstanceCreator {

	public Object create(AnnotationTag tag) {

		ClassLoader cl = this.getClass().getClassLoader();

		try {
			final Class<?> clazz = cl.loadClass(toQuallifiedClassName(tag.getType()));
			final Map<String, Object> map = new HashMap<>();

			for (AnnotationElem elem : tag.getElems()) {
				AnnotationElemSwitch sw = new AnnotationElemSwitch();
				elem.apply(sw);

				@SuppressWarnings("unchecked")
				KeyValuePair<Object> result = (KeyValuePair<Object>) sw.getResult();

				map.put(result.getKey(), result.getValue());
			}

			Object result = Proxy.newProxyInstance(cl, new Class[] { clazz }, new AbstractInvocationHandler() {

				@SuppressWarnings("unchecked")
				@Override
				protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
					String name = method.getName();
					Class<?> retType = method.getReturnType();
					if (name.equals("annotationType")) {
						return clazz;
					}

					Object result = map.get(name);

					if (result != null) {
						if (result instanceof Object[]) {
							Object[] oa = (Object[]) result;

							return Arrays.copyOf(oa, oa.length, (Class<? extends Object[]>) retType);
						}

						if ((retType.equals(boolean.class) || retType.equals(Boolean.class)) && (result instanceof Integer)) {
							return ((Integer) result) != 0;
						}

						return result;
					} else {
						result = method.getDefaultValue();
						if (result != null) {
							return result;
						}
					}

					throw new RuntimeException("No value for " + name + " declared in the annotation " + clazz);
				}
			});

			return result;

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load class: " + tag.getType());
		}
	}

}
