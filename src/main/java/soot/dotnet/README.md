# soot.dotnet

`soot.dotnet` analyzes .NET assemblies. It calls into a native JNI bridge (`NativeHost`) that hosts a .NET runtime and
the `Soot.Dotnet.Decompiler` library. The wrapper sources live in [`src/main/dotnet`](../../../../dotnet); the Maven
build compiles and bundles them.

### Example Parameter Configuration
The NativeHost library and its managed dependencies are bundled in Soot's jar and extracted automatically. Only the
analyzed assembly needs to be configured:

```
-allow-phantom-refs
-src-prec dotnet
# ignore unsafe methods
-no-resolve-all-dotnet-methods
# create empty Jimple Body if error occurs while jimplifying
-ignore-methodsource-error
-verbose
-debug
-debug-resolver
-throw-analysis dotnet
-validate
-f J
MyAssembly.dll
```

With `-src-prec dotnet`, Soot automatically adds the local .NET runtime directory to the classpath so that framework
assemblies resolve. Pass `-cp` only to add further reference assemblies.

To use a wrapper outside the jar, set `-dotnet-nativehost-path <library-or-directory>`,
`-Dsoot.dotnet.wrapper.path=<library-or-directory>` or `SOOT_DOTNET_WRAPPER=<library-or-directory>`. To disable the
bundled wrapper at build time, build Soot with `-DskipDotnet`.

We need phantom-refs because there are some statements which cannot be handled at the moment.
