#include "inc/Settings.h"

#ifdef WINDOWS
#define STR(s) L##s
#define CH(c) L##c
#else
#define STR(s) s
#define CH(c) c
#endif

/*
 * A settings class for storing relevant options for the dotnet host
 */
Settings::Settings() {
  theSettings[RuntimeConfigFileName] =
      STR("Soot.Dotnet.Decompiler.runtimeconfig.json");
  theSettings[AssemblyFileName] = STR("Soot.Dotnet.Decompiler.dll");
  theSettings[MethodFullyQualifiedClassName] =
      STR("Soot.Dotnet.Decompiler.AssemblyProvider");
  theSettings[MethodNamespace] = STR("Soot.Dotnet.Decompiler");
  // is replaced by Soot Framework (Java) at NativeHost.cpp
  theSettings[PathToAssembly] = STR(
      "/Users/user/RiderProjects/Soot.Dotnet/Soot.Dotnet.NativeHost/bin/Debug/")
      STR("libNativeHost.dylib");

  theSettings[MethodGetManifestResourceStreamName] = STR("GetManifestResourceStream");
  theSettings[MethodGetManifestResourceStreamDelegateName] = STR("GetManifestResourceStreamDelegate");
  theSettings[MethodIsAssemblyName] = STR("IsAssembly");
  theSettings[MethodIsAssemblyDelegateName] = STR("IsAssemblyDelegate");
  theSettings[MethodAllTypesName] = STR("GetAllTypesMsg");
  theSettings[MethodAllTypesDelegateName] = STR("GetAllTypesMsgDelegate");
  theSettings[MethodMethodBodyName] = STR("GetMethodBodyMsg");
  theSettings[MethodMethodBodyDelegateName] = STR("GetMethodBodyMsgDelegate");
  theSettings[MethodMethodBodyPropertyName] = STR("GetMethodBodyOfPropertyMsg");
  theSettings[MethodMethodBodyPropertyDelegateName] =
      STR("GetMethodBodyOfPropertyMsgDelegate");
  theSettings[MethodMethodBodyEventName] = STR("GetMethodBodyOfEventMsg");
  theSettings[MethodMethodBodyEventDelegateName] =
      STR("GetMethodBodyOfEventMsgDelegate");
  theSettings[MethodAssemblyContentName] = STR("GetAssemblyContent");
  theSettings[MethodAssemblyContentDelegateName] =
      STR("GetAssemblyContentDelegate");
}

string_t &Settings::operator[](const Settings::SettingName &theName) {
  return theSettings[theName];
}
