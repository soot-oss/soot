/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Feng Qian
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

/**
 * A wrapper for native method side-effect simulation.
 * The caller passes in a native method with parameters, 
 * the corresponding native simulator gets called.
 *
 * @author Feng Qian
 */
package soot.jimple.toolkits.pointer.util;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.nativemethods.*;
import java.util.*;

public class NativeMethodDriver {
    public NativeMethodDriver( Singletons.Global g ) {
        cnameToSim.put("java.lang.Object", JavaLangObjectNative.v());
        cnameToSim.put("java.lang.System", JavaLangSystemNative.v());
        cnameToSim.put("java.lang.Runtime", JavaLangRuntimeNative.v());
        cnameToSim.put("java.lang.Shutdown", JavaLangShutdownNative.v());
        cnameToSim.put("java.lang.String", JavaLangStringNative.v());
        cnameToSim.put("java.lang.Float", JavaLangFloatNative.v());
        cnameToSim.put("java.lang.Double", JavaLangDoubleNative.v());
        cnameToSim.put("java.lang.StrictMath", JavaLangStrictMathNative.v());
        cnameToSim.put("java.lang.Throwable", JavaLangThrowableNative.v());
        cnameToSim.put("java.lang.Class", JavaLangClassNative.v());
        cnameToSim.put("java.lang.Package", JavaLangPackageNative.v());
        cnameToSim.put("java.lang.Thread", JavaLangThreadNative.v());
        cnameToSim.put("java.lang.ClassLoader", JavaLangClassLoaderNative.v());
        cnameToSim.put("java.lang.ClassLoader$NativeLibrary",
                       JavaLangClassLoaderNativeLibraryNative.v());
        cnameToSim.put("java.lang.SecurityManager",
                       JavaLangSecurityManagerNative.v());


        cnameToSim.put("java.lang.reflect.Field",
                       JavaLangReflectFieldNative.v());
        cnameToSim.put("java.lang.reflect.Array",
                       JavaLangReflectArrayNative.v());
        cnameToSim.put("java.lang.reflect.Method",
                       JavaLangReflectMethodNative.v());
        cnameToSim.put("java.lang.reflect.Constructor",
                       JavaLangReflectConstructorNative.v());
        cnameToSim.put("java.lang.reflect.Proxy",
                       JavaLangReflectProxyNative.v());


        cnameToSim.put("java.io.FileInputStream", 
                       JavaIoFileInputStreamNative.v());
        cnameToSim.put("java.io.FileOutputStream", 
                       JavaIoFileOutputStreamNative.v());
        cnameToSim.put("java.io.ObjectInputStream",
                       JavaIoObjectInputStreamNative.v());
        cnameToSim.put("java.io.ObjectOutputStream",
                       JavaIoObjectOutputStreamNative.v());
        cnameToSim.put("java.io.ObjectStreamClass",
                       JavaIoObjectStreamClassNative.v());
        cnameToSim.put("java.io.FileSystem", JavaIoFileSystemNative.v());
        cnameToSim.put("java.io.FileDescriptor", JavaIoFileDescriptorNative.v());


        cnameToSim.put("java.util.ResourceBundle", 
                       JavaUtilResourceBundleNative.v());
        cnameToSim.put("java.util.TimeZone", JavaUtilTimeZoneNative.v());
        

        cnameToSim.put("java.util.jar.JarFile",
                       JavaUtilJarJarFileNative.v());
        
        cnameToSim.put("java.util.zip.CRC32",
                       JavaUtilZipCRC32Native.v());
        cnameToSim.put("java.util.zip.Inflater",
                       JavaUtilZipInflaterNative.v());
        cnameToSim.put("java.util.zip.ZipFile",
                       JavaUtilZipZipFileNative.v());
        cnameToSim.put("java.util.zip.ZipEntry",
                       JavaUtilZipZipEntryNative.v());
        

        cnameToSim.put("java.security.AccessController",
                       JavaSecurityAccessControllerNative.v());
        

        cnameToSim.put("java.net.InetAddress", 
                       JavaNetInetAddressNative.v());
        cnameToSim.put("java.net.InetAddressImpl", 
                       JavaNetInetAddressImplNative.v());


        cnameToSim.put("sun.misc.Signal",
                       SunMiscSignalNative.v());
        cnameToSim.put("sun.misc.NativeSignalHandler",
                       SunMiscSignalHandlerNative.v());
    }

    public static NativeMethodDriver v() { return G.v().NativeMethodDriver(); }

  private HashMap cnameToSim = new HashMap(100);
  private boolean DEBUG = false;

  /**
   * The entry point of native method simulation.
   * @param method, must be a native method
   * @param thisVar, the variable represent @this, 
   *                 it can be null if the method is static
   * @param returnVar, the variable represent @return
   *                 it is null if the method has no return
   * @param params, array of parameters.
   */
  public boolean process(SootMethod method, 
				ReferenceVariable thisVar,
				ReferenceVariable returnVar,
				ReferenceVariable params[]) {

    String cname = method.getDeclaringClass().getName();
    NativeMethodClass clsSim = (NativeMethodClass)cnameToSim.get(cname);

//    G.v().out.println(method.toString());
    if (clsSim == null) {
	  //G.v().out.println("WARNING: it is unsafe to simulate the method ");
	  //G.v().out.println("         "+method.toString());	
      //throw new NativeMethodNotSupportedException(method);
      return true;
    } else {

      try {
	clsSim.simulateMethod(method,
			      thisVar,
			      returnVar,
			      params);
      } catch (NativeMethodNotSupportedException e) {
          if(DEBUG) {
              G.v().out.println("WARNING: it is unsafe to simulate the method ");
              G.v().out.println("         "+method.toString());	
          }
      }
      return true;
    }
  }
}
