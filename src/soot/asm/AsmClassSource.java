/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2014 Raja Vallee-Rai and others
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.asm;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

import soot.ClassSource;
import soot.SootClass;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * ASM class source implementation.
 * 
 * @author Aaloan Miftah
 */
class AsmClassSource extends ClassSource {

	private InputStream data;
	
	/**
	 * Constructs a new ASM class source.
	 * @param cls fully qualified name of the class.
	 * @param data stream containing data for class.
	 */
	AsmClassSource(String cls, InputStream data) {
		super(cls);
		this.data = data;
	}
	
	private ClassReader read() throws IOException {
		InputStream d = data;
		if (d == null)
			throw new IllegalStateException();
		data = null;
		try {
			return new ClassReader(d);
		} finally {
			d.close();
		}
	}
	
	@Override
	public Dependencies resolve(SootClass sc) {
		ClassReader clsr;
		try {
			clsr = read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		SootClassBuilder scb = new SootClassBuilder(sc);
		clsr.accept(scb, ClassReader.SKIP_FRAMES);
		Dependencies deps = new Dependencies();
		deps.typesToSignature.addAll(scb.deps);
		return deps;
	}
}