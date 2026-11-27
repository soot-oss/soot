//
// Created by Thomas Schmeiduch
//
#include <iostream>
#include <map>
#include <string>
// using namespace std;

#include "cli_string.h"

using string_t = std::basic_string<char_t>;

class Settings {
 public:
  using SettingName = enum {
    RuntimeConfigFileName,
    PathToAssembly,
    AssemblyFileName,
    MethodNamespace,
    MethodFullyQualifiedClassName,

    MethodIsAssemblyName,
    MethodIsAssemblyDelegateName,
    MethodAllTypesName,
    MethodAllTypesDelegateName,
    MethodMethodBodyName,
    MethodMethodBodyDelegateName,
    MethodMethodBodyPropertyName,
    MethodMethodBodyPropertyDelegateName,
    MethodMethodBodyEventName,
    MethodMethodBodyEventDelegateName,
    MethodAssemblyContentName,
    MethodAssemblyContentDelegateName,
    MethodGetManifestResourceStreamName,
    MethodGetManifestResourceStreamDelegateName
  };

 private:
  using SettingCollection = std::map<SettingName, string_t>;
  SettingCollection theSettings;

 public:
  Settings();
  string_t& operator[](const SettingName& theName);
  // void Save ();
};
