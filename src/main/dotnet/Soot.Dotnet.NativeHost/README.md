# Soot.Dotnet.NativeHost

JNI bridge that hosts a .NET runtime for the `soot.dotnet` frontend. It exports the `soot.dotnet.AssemblyFile` native
methods and forwards their protobuf payloads to `Soot.Dotnet.Decompiler`.

The idea is based on:

- https://docs.microsoft.com/en-us/dotnet/core/tutorials/netcore-hosting
- https://github.com/dotnet/samples/tree/main/core/hosting

## Build

The Maven build (see [../README.md](../README.md)) invokes:

```sh
JAVA_HOME=/path/to/jdk dotnet build Soot.Dotnet.NativeHost.csproj -c Release -p:NativeBinDir=<wrapper-dir>
```

The project compiles `NativeHost.cpp`, `Settings.cpp` and `soot_dotnet_AssemblyFile.cpp` into
`libNativeHost.so`/`libNativeHost.dylib`/`NativeHost.dll` and places `libnethost` next to it. The library resolves
`Soot.Dotnet.Decompiler.dll` and the runtime config from its own directory (rpath `$ORIGIN`/`@loader_path`).

`JAVA_HOME` must point to a JDK; `NativeBinDir` defaults to `bin/<Configuration>/native`.

### Cross-compiling for Windows

The library is built for the machine's own platform unless `NativeTarget` names another RID. For a Windows build from
Linux, install MinGW-w64 and pass both properties:

```sh
dotnet build Soot.Dotnet.NativeHost.csproj -c Release -p:NativeBinDir=<wrapper-dir> \
    -p:NativeTarget=win-x64 -p:NativeCompiler=x86_64-w64-mingw32-g++
```

The Windows arm compiles against the vendored `win-includes-java11/jni_md.h` (a Linux JDK has no `include/win32`) and
against `nethost` from the restored `Microsoft.NETCore.App.Host.win-x64` package, which `dotnet build` fetches on demand.
`libgcc`/`libstdc++` are linked statically, so the output only needs `nethost.dll` beside it.

With Maven, activate the `dotnet-wrapper-windows` profile instead — it sets those two properties and stages the result
next to the host library, so one jar serves both platforms:

```sh
apt-get install mingw-w64   # if not installed yet (debian/ubuntu-based distros)
mvn -Pdotnet-wrapper-windows package
```

The `CMakeLists.txt` is kept for CLion/CMake users. It requires `JAVA_HOME` and `DOTNET_RUNTIME_PACK` (the `native`
folder of the `Microsoft.NETCore.App.Host.*` targeting pack) and writes to `bin/Debug`. For a MinGW cross build configure
`build-win` with `win.toolchain.cmake`.
