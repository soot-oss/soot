package soot.util.annotations;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jboss.util.Classes;

import com.google.common.reflect.AbstractInvocationHandler;

import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.util.annotations.AnnotationElemSwitch.AnnotationElemResult;

/**
 * 
 * A simple helper class with the ability to create an instance of {@link Proxy}
 * implementing the annotation interface represented by the given
 * {@link AnnotationTag}.
 * 
 * 
 * @author Florian Kuebler
 *
 */
public class AnnotationInstanceCreator {

	/**
	 * Creates an instance of the Annotation represented by <code>tag</code>.
	 * 
	 * @param tag
	 *            the soot internal representation of the annotation to be
	 *            created.
	 * @return an Object extending {@link Proxy} and implementing the type of
	 *         <code>tag</code>
	 * @throws RuntimeException
	 *             if
	 *             <ul>
	 *             <li>the class defined in {@link AnnotationTag#getType()} of
	 *             <code>tag</code> could not be loaded.</li>
	 * 
	 *             <li><code>tag</code> does not define all required methods of
	 *             the annotation loaded.</li>
	 * 
	 *             <li>a class defined within a {@link AnnotationElem} could not
	 *             be loaded.</li>
	 * 
	 *             <li>the enum defined in {@link AnnotationEnumElem} is no
	 *             instance of {@link Enum}.</li>
	 *             </ul>
	 */
	public Object create(AnnotationTag tag) {

		ClassLoader cl = this.getClass().getClassLoader();

		try {
			// load the class of the annotation to be created
			final Class<?> clazz = Classes.loadClass(tag.getType().replace('/', '.'));
			final Map<String, Object> map = new HashMap<String, Object>();

			// for every element generate the result
			for (AnnotationElem elem : tag.getElems()) {
				AnnotationElemSwitch sw = new AnnotationElemSwitch();
				elem.apply(sw);

				@SuppressWarnings("unchecked")
				AnnotationElemResult<Object> result = (AnnotationElemResult<Object>) sw.getResult();

				map.put(result.getKey(), result.getValue());
			}

			// create the instance
			Object result = Proxy.newProxyInstance(cl, new Class[] { clazz }, new AbstractInvocationHandler() {

				@SuppressWarnings("unchecked")
				@Override
				protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
					String name = method.getName();
					Class<?> retType = method.getReturnType();

					// if the method being called is #annotationType return the
					// clazz of the annotation
					if (name.equals("annotationType")) {
						return clazz;
					}

					// get the precomputed result for the method being called.
					Object result = map.get(name);

					if (result != null) {

						// if the result is an Object[], the array has to be
						// transformed to an array of the return type.
						if (result instanceof Object[]) {
							Object[] oa = (Object[]) result;

							return Arrays.copyOf(oa, oa.length, (Class<? extends Object[]>) retType);
						}

						// java bytecode does not know boolean types.
						if ((retType.equals(boolean.class) || retType.equals(Boolean.class))
								&& (result instanceof Integer)) {
							return ((Integer) result) != 0;
						}

						return result;
					} else {
						// if the AnnotationTag does not define a method, try to
						// use the default value.
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
