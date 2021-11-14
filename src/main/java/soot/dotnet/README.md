# soot.dotnet
To start soot.dotnet, we also need the counterpart modules Soot.Dotnet. The project is located at https://github.com/schtho/soot-dotnet.
soot.dotnet uses Java JNI such that we need to add VM commands to load the library.

Add to your Java VM options the library option with the path (e.g. `-Djava.library.path=/Users/user/soot-dotnet/src/Soot.Dotnet.NativeHost/bin/Debug`). Otherwise this frontend does not work.

### Example Parameter Configuration
Run Soot with following parameters as example:
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
-cp /usr/local/share/dotnet/shared/Microsoft.NETCore.App/3.1.13/
-dotnet-nativehost-path /Users/user/soot-dotnet/src/Soot.Dotnet.NativeHost/bin/Debug/libNativeHost.dylib
-f J
System.Type
```
We need phantom-refs because there are some statements which cannot be handled at the moment.