//
// Main class of this NativeHost Library
// It sets up a native host and starts the managed DLL
// Created by Thomas Schmeiduch
// Based on: https://github.com/dotnet/samples/tree/main/core/hosting/src
//

#include "NativeHost.h"

NativeHost::NativeHost(const std::string& path_to_nativehost) {
    // Set path to the nativehost lib, which is passed by the Soot framework
    // (Java)
    settings[Settings::SettingName::PathToAssembly] =
        string_t(path_to_nativehost.begin(), path_to_nativehost.end());

    init();
}

void NativeHost::init() {
    // Get the current executable's directory
    // This sample assumes the managed assembly to load and its runtime
    // configuration file are next to the host
    char_t host_path[MAX_PATH];
#if WINDOWS
    auto size = ::GetFullPathNameW(
        settings[Settings::SettingName::PathToAssembly].c_str(),
        sizeof(host_path) / sizeof(char_t), host_path, nullptr);
    assert(size != 0);
#else
    auto resolved = realpath(
        settings[Settings::SettingName::PathToAssembly].c_str(), host_path);
    assert(resolved != nullptr &&
        "Failure: The path to the NativeHost library is wrong!");
#endif

    root_path = host_path;
    auto pos = root_path.find_last_of(DIR_SEPARATOR);
    assert(pos != string_t::npos);
    root_path = root_path.substr(0, pos + 1);

    //
    // STEP 1: Load HostFxr and get exported hosting functions
    //
    if (!load_hostfxr()) {
        assert(false &&
            "Failure: Failed to load the HostFxr and their Hosting functions! "
            "(load_hostfxr())");
    }

    //
    // STEP 2: Initialize and start the .NET Core runtime
    //
    const string_t config_path =
        root_path + (settings[Settings::SettingName::RuntimeConfigFileName]);
    load_assembly_and_get_function_pointer =
        get_dotnet_load_assembly(config_path.c_str());
    assert(load_assembly_and_get_function_pointer != nullptr &&
        "Failure: Could not init and start dotnet core runtime. Maybe the "
        "runtime config file is missing! (get_dotnet_load_assembly())");
}

cli_byte_array NativeHost::AssemblyProvider_getManifestResourceStream(const std::string& assembly_path, const std::string& name) {
    //
    // STEP 3: Load managed assembly and get function pointer to a managed method
    //
    const string_t dotnetlib_path =
        root_path + (settings[Settings::SettingName::AssemblyFileName]);
    string_t dotnetClassStr =
        settings[Settings::SettingName::MethodFullyQualifiedClassName] +
        STR(", ") + settings[Settings::SettingName::MethodNamespace];
    const char_t* dotnet_type = dotnetClassStr.c_str();

    // Function pointer to managed delegate with non-default signature
    typedef cli_byte_array (CORECLR_DELEGATE_CALLTYPE* lib_csharp_fn)(cli_string arg1,
        cli_string arg2);

    lib_csharp_fn csharp_method = nullptr;
    int rc = load_assembly_and_get_function_pointer(
        dotnetlib_path.c_str(), dotnet_type,
        (settings[Settings::SettingName::MethodGetManifestResourceStreamName]
            .c_str()) /*method_name*/,
        ((settings[Settings::SettingName::MethodFullyQualifiedClassName] +
            STR("+") +
            settings[Settings::SettingName::MethodGetManifestResourceStreamDelegateName] +
            STR(", ") + settings[Settings::SettingName::MethodNamespace])
            .c_str()) /*delegate_type_name*/,
        nullptr, (void**)&csharp_method);
    assert(rc == 0 && csharp_method != nullptr &&
        "Failure: Could not run the managed method of the managed library! "
        "(load_assembly_and_get_function_pointer())");

    //
    // STEP 4: Run managed code
    //
    string_t asname = string_t(name.begin(), name.end());
    string_t aspath = string_t(assembly_path.begin(), assembly_path.end());
    cli_string path{ aspath.c_str() };
    cli_string aname{ (asname.c_str()) };

    cli_byte_array ret = csharp_method(path, aname);
    return ret;
}

bool NativeHost::AssemblyProvider_isAssembly(const std::string& assembly_path) {
    //
    // STEP 3: Load managed assembly and get function pointer to a managed method
    //
    const string_t dotnetlib_path =
        root_path + (settings[Settings::SettingName::AssemblyFileName]);
    string_t dotnetClassStr =
        settings[Settings::SettingName::MethodFullyQualifiedClassName] +
        STR(", ") + settings[Settings::SettingName::MethodNamespace];
    const char_t* dotnet_type = dotnetClassStr.c_str();

    // Function pointer to managed delegate with non-default signature
    typedef bool* (CORECLR_DELEGATE_CALLTYPE* lib_csharp_fn)(cli_string arg1,
        cli_string arg2);

    lib_csharp_fn csharp_method = nullptr;
    int rc = load_assembly_and_get_function_pointer(
        dotnetlib_path.c_str(), dotnet_type,
        (settings[Settings::SettingName::MethodIsAssemblyName]
            .c_str()) /*method_name*/,
        ((settings[Settings::SettingName::MethodFullyQualifiedClassName] +
            STR("+") +
            settings[Settings::SettingName::MethodIsAssemblyDelegateName] +
            STR(", ") + settings[Settings::SettingName::MethodNamespace])
            .c_str()) /*delegate_type_name*/,
        nullptr, (void**)&csharp_method);
    assert(rc == 0 && csharp_method != nullptr &&
        "Failure: Could not run the managed method of the managed library! "
        "(load_assembly_and_get_function_pointer())");

    //
    // STEP 4: Run managed code
    //
    string_t not_used;
    string_t aspath = string_t(assembly_path.begin(), assembly_path.end());
    cli_string path{ aspath.c_str() };
    cli_string no_use{ (not_used.c_str()) };

    bool ret = csharp_method(path, no_use);
    return ret;
}

cli_byte_array NativeHost::AssemblyProvider_getAllTypesMsg(
    cli_byte_array& disassemblerParams) {
    //
    // STEP 3: Load managed assembly and get function pointer to a managed method
    //
    const string_t dotnetlib_path =
        root_path + (settings[Settings::SettingName::AssemblyFileName]);
    string_t dotnetClassStr =
        settings[Settings::SettingName::MethodFullyQualifiedClassName] +
        STR(", ") + settings[Settings::SettingName::MethodNamespace];
    const char_t* dotnet_type = (dotnetClassStr.c_str());

    // Function pointer to managed delegate with non-default signature
    typedef cli_byte_array(CORECLR_DELEGATE_CALLTYPE*
        lib_csharp_fn)(cli_byte_array args1);

    lib_csharp_fn csharp_method = nullptr;
    int rc = load_assembly_and_get_function_pointer(
        dotnetlib_path.c_str(), dotnet_type,
        (settings[Settings::SettingName::MethodAllTypesName]
            .c_str()) /*method_name*/,
        ((settings[Settings::SettingName::MethodFullyQualifiedClassName] +
            STR("+") + settings[Settings::SettingName::MethodAllTypesDelegateName] +
            STR(", ") + settings[Settings::SettingName::MethodNamespace])
            .c_str()) /*delegate_type_name*/,
        nullptr, (void**)&csharp_method);
    assert(rc == 0 && csharp_method != nullptr &&
        "Failure: Could not run the managed method of the managed library! "
        "(load_assembly_and_get_function_pointer())");

    //
    // STEP 4: Run managed code
    //
    cli_byte_array ret = csharp_method(disassemblerParams);
    return ret;
}

cli_byte_array NativeHost::AssemblyProvider_GetMethodBodyMsg(
    cli_byte_array& disassemblerParams) {
    //
    // STEP 3: Load managed assembly and get function pointer to a managed method
    //
    const string_t dotnetlib_path =
        root_path + (settings[Settings::SettingName::AssemblyFileName]);
    string_t dotnetClassStr =
        settings[Settings::SettingName::MethodFullyQualifiedClassName] +
        STR(", ") + settings[Settings::SettingName::MethodNamespace];
    const char_t* dotnet_type = (dotnetClassStr.c_str());

    // Function pointer to managed delegate with non-default signature
    typedef cli_byte_array(CORECLR_DELEGATE_CALLTYPE*
        lib_csharp_fn)(cli_byte_array arg);

    lib_csharp_fn csharp_method = nullptr;
    int rc = load_assembly_and_get_function_pointer(
        dotnetlib_path.c_str(), dotnet_type,
        (settings[Settings::SettingName::MethodMethodBodyName]
            .c_str()) /*method_name*/,
        ((settings[Settings::SettingName::MethodFullyQualifiedClassName] +
            STR("+") +
            settings[Settings::SettingName::MethodMethodBodyDelegateName] +
            STR(", ") + settings[Settings::SettingName::MethodNamespace])
            .c_str()) /*delegate_type_name*/,
        nullptr, (void**)&csharp_method);
    assert(rc == 0 && csharp_method != nullptr &&
        "Failure: Maybe the given function/delegate does not exists in the "
        "given assembly! (load_assembly_and_get_function_pointer())");

    //
    // STEP 4: Run managed code
    //
    cli_byte_array ret = csharp_method(disassemblerParams);
    return ret;
}

cli_byte_array NativeHost::AssemblyProvider_GetMethodBodyOfPropertyMsg(
    cli_byte_array& disassemblerParams) {
    //
    // STEP 3: Load managed assembly and get function pointer to a managed method
    //
    const string_t dotnetlib_path =
        root_path + (settings[Settings::SettingName::AssemblyFileName]);
    string_t dotnetClassStr =
        settings[Settings::SettingName::MethodFullyQualifiedClassName] +
        STR(", ") + settings[Settings::SettingName::MethodNamespace];
    const char_t* dotnet_type = (dotnetClassStr.c_str());

    // Function pointer to managed delegate with non-default signature
    typedef cli_byte_array(CORECLR_DELEGATE_CALLTYPE*
        lib_csharp_fn)(cli_byte_array arg);

    lib_csharp_fn csharp_method = nullptr;
    int rc = load_assembly_and_get_function_pointer(
        dotnetlib_path.c_str(), dotnet_type,
        (settings[Settings::SettingName::MethodMethodBodyPropertyName]
            .c_str()) /*method_name*/,
        ((settings[Settings::SettingName::MethodFullyQualifiedClassName] +
            STR("+") +
            settings[Settings::SettingName::MethodMethodBodyPropertyDelegateName] +
            STR(", ") + settings[Settings::SettingName::MethodNamespace])
            .c_str()) /*delegate_type_name*/,
        nullptr, (void**)&csharp_method);
    assert(rc == 0 && csharp_method != nullptr &&
        "Failure: Maybe the given function/delegate does not exists in the "
        "given assembly! (load_assembly_and_get_function_pointer())");

    //
    // STEP 4: Run managed code
    //
    cli_byte_array ret = csharp_method(disassemblerParams);
    return ret;
}

cli_byte_array NativeHost::AssemblyProvider_GetMethodBodyOfEventMsg(
    cli_byte_array& disassemblerParams) {
    //
    // STEP 3: Load managed assembly and get function pointer to a managed method
    //
    const string_t dotnetlib_path =
        root_path + (settings[Settings::SettingName::AssemblyFileName]);
    string_t dotnetClassStr =
        settings[Settings::SettingName::MethodFullyQualifiedClassName] +
        STR(", ") + settings[Settings::SettingName::MethodNamespace];
    const char_t* dotnet_type = (dotnetClassStr.c_str());

    // Function pointer to managed delegate with non-default signature
    typedef cli_byte_array(CORECLR_DELEGATE_CALLTYPE*
        lib_csharp_fn)(cli_byte_array arg);

    lib_csharp_fn csharp_method = nullptr;
    int rc = load_assembly_and_get_function_pointer(
        dotnetlib_path.c_str(), dotnet_type,
        (settings[Settings::SettingName::MethodMethodBodyEventName]
            .c_str()) /*method_name*/,
        ((settings[Settings::SettingName::MethodFullyQualifiedClassName] +
            STR("+") +
            settings[Settings::SettingName::MethodMethodBodyEventDelegateName] +
            STR(", ") + settings[Settings::SettingName::MethodNamespace])
            .c_str()) /*delegate_type_name*/,
        nullptr, (void**)&csharp_method);
    assert(rc == 0 && csharp_method != nullptr &&
        "Failure: Maybe the given function/delegate does not exists in the "
        "given assembly! (load_assembly_and_get_function_pointer())");

    //
    // STEP 4: Run managed code
    //
    cli_byte_array ret = csharp_method(disassemblerParams);
    return ret;
}

cli_byte_array
NativeHost::AssemblyProvider_GetAssemblyContent(cli_byte_array& protoParams) {
    //
    // STEP 3: Load managed assembly and get function pointer to a managed method
    //
    const string_t dotnetlib_path =
        root_path + (settings[Settings::SettingName::AssemblyFileName]);
    string_t dotnetClassStr =
        settings[Settings::SettingName::MethodFullyQualifiedClassName] +
        STR(", ") + settings[Settings::SettingName::MethodNamespace];
    const char_t* dotnet_type = (dotnetClassStr.c_str());

    // Function pointer to managed delegate with non-default signature
    typedef cli_byte_array(CORECLR_DELEGATE_CALLTYPE*
        lib_csharp_fn)(cli_byte_array args);

    lib_csharp_fn csharp_method = nullptr;
    int rc = load_assembly_and_get_function_pointer(
        dotnetlib_path.c_str(), dotnet_type,
        (settings[Settings::SettingName::MethodAssemblyContentName]
            .c_str()) /*method_name*/,
        ((settings[Settings::SettingName::MethodFullyQualifiedClassName] +
            STR("+") +
            settings[Settings::SettingName::MethodAssemblyContentDelegateName] +
            STR(", ") + settings[Settings::SettingName::MethodNamespace])
            .c_str()) /*delegate_type_name*/,
        nullptr, (void**)&csharp_method);
    assert(rc == 0 && csharp_method != nullptr &&
        "Failure: Maybe the given function/delegate does not exists in the "
        "given assembly! (load_assembly_and_get_function_pointer())");

    //
    // STEP 4: Run managed code
    //
    cli_byte_array ret = csharp_method(protoParams);
    return ret;
}

// ### PRIVATE ###

void* NativeHost::load_library(const char_t* path) {
#ifdef WINDOWS
    HMODULE h = ::LoadLibraryW(path);
    assert(h != nullptr);
    return (void*)h;
#else
    void* h = dlopen(path, RTLD_LAZY | RTLD_LOCAL);
    assert(h != nullptr);
    return h;
#endif
}

void* NativeHost::get_export(void* h, const char* name) {
#ifdef WINDOWS
    auto f = (void*)::GetProcAddress((HMODULE)h, name);
    assert(f != nullptr);
    return f;
#else
    void* f = dlsym(h, name);
    assert(f != nullptr);
    return f;
#endif
}

#ifdef WINDOWS
/// Resolves hostfxr below a .NET installation the registry does not know about: the DOTNET_ROOT* variables, the
/// directory of dotnet.exe on PATH, or the per-user / machine-wide install locations. Returns false if none of them
/// contains a usable hostfxr.
bool NativeHost::find_hostfxr_from_dotnet_root(char_t* buffer, size_t* buffer_size) {
    std::vector<string_t> roots;

    // Values may carry a trailing separator; strip it so the patterns below do not double it up.
    auto add_root = [&roots](string_t root) {
        while (!root.empty() && (root.back() == L'\\' || root.back() == L'/')) {
            root.pop_back();
        }
        if (!root.empty()) {
            roots.push_back(root);
        }
    };
    auto add_env = [&add_root](const char_t* name, const char_t* suffix) {
        char_t value[MAX_PATH];
        const DWORD len = ::GetEnvironmentVariableW(name, value, MAX_PATH);
        if (len != 0 && len < MAX_PATH) {
            add_root(string_t(value, len) + suffix);
        }
    };

    add_env(STR("DOTNET_ROOT_X64"), STR(""));
    add_env(STR("DOTNET_ROOT"), STR(""));

    char_t exe[MAX_PATH];
    const DWORD found = ::SearchPathW(nullptr, STR("dotnet.exe"), nullptr, MAX_PATH, exe, nullptr);
    if (found != 0 && found < MAX_PATH) {
        string_t directory(exe, found);
        const size_t separator = directory.find_last_of(STR("\\/"));
        if (separator != string_t::npos) {
            add_root(directory.substr(0, separator));
        }
    }

    // dotnet-install.ps1 defaults, in case dotnet is neither on PATH nor in the registry.
    add_env(STR("LOCALAPPDATA"), STR("\\Microsoft\\dotnet"));
    add_env(STR("ProgramFiles"), STR("\\dotnet"));

    for (const string_t& root : roots) {
        const string_t pattern = root + STR("\\host\\fxr\\*");
        WIN32_FIND_DATAW entry;
        HANDLE search = ::FindFirstFileW(pattern.c_str(), &entry);
        if (search == INVALID_HANDLE_VALUE) {
            continue;
        }
        std::vector<string_t> versions;
        do {
            if ((entry.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) && entry.cFileName[0] != L'.') {
                versions.push_back(entry.cFileName);
            }
        } while (::FindNextFileW(search, &entry));
        ::FindClose(search);

        // Newest first: a version string sorts numerically, not lexicographically ("10" > "9").
        std::sort(versions.begin(), versions.end(),
            [](const string_t& left, const string_t& right) { return version_compare(left, right) > 0; });
        for (const string_t& version : versions) {
            const string_t candidate = root + STR("\\host\\fxr\\") + version + STR("\\hostfxr.dll");
            if (::GetFileAttributesW(candidate.c_str()) == INVALID_FILE_ATTRIBUTES) {
                continue;
            }
            if (candidate.size() + 1 > *buffer_size) {
                continue;
            }
            ::memcpy(buffer, candidate.c_str(), (candidate.size() + 1) * sizeof(char_t));
            *buffer_size = candidate.size() + 1;
            return true;
        }
    }
    return false;
}
#endif

/// SnippetLoadHostFxr: Using the nethost library, discover the location of
/// hostfxr and get exports \return
bool NativeHost::load_hostfxr() {
    // Pre-allocate a large buffer for the path to hostfxr
    char_t buffer[MAX_PATH];
    size_t buffer_size = sizeof(buffer) / sizeof(char_t);
    int rc = get_hostfxr_path(buffer, &buffer_size, nullptr);
#ifdef WINDOWS
    if (rc != 0) {
        // nethost only knows the registry key and the default installation directory. A user-local or unpacked
        // .NET (dotnet-install.ps1 into %LOCALAPPDATA%, winget) registers neither, so look it up ourselves.
        buffer_size = sizeof(buffer) / sizeof(char_t);
        if (find_hostfxr_from_dotnet_root(buffer, &buffer_size)) {
            rc = 0;
        } else {
            std::cerr << "Failure: Could not locate hostfxr. Install the .NET runtime, or set DOTNET_ROOT to its "
                "installation directory (for example C:\\Program Files\\dotnet)." << std::endl;
        }
    }
#endif
    if (rc != 0)
        return false;

    // Load hostfxr and get desired exports
    void* lib = load_library(buffer);
    // managed DLL is called and managed by the runtime.config.json file
    init_fptr = (hostfxr_initialize_for_runtime_config_fn)get_export(
        lib, "hostfxr_initialize_for_runtime_config");
    get_delegate_fptr = (hostfxr_get_runtime_delegate_fn)get_export(
        lib, "hostfxr_get_runtime_delegate");
    close_fptr = (hostfxr_close_fn)get_export(lib, "hostfxr_close");

    return (init_fptr && get_delegate_fptr && close_fptr);
}

/// SnippetInitialize: Load and initialize .NET Core and get desired function
/// pointer for scenario \param config_path \return
load_assembly_and_get_function_pointer_fn
NativeHost::get_dotnet_load_assembly(const char_t* config_path) {
    // Load .NET Core
    void* load_assembly_and_get_function_pointer = nullptr;
    hostfxr_handle cxt = nullptr;
    // managed DLL is called and managed by the runtime.config.json file
    int rc = init_fptr(config_path, nullptr, &cxt);
    // if 1 already init -
    // https://github.com/dotnet/runtime/blob/main/docs/design/features/host-error-codes.md
    if ((rc != 1 && rc != 0) || cxt == nullptr) {
        std::cerr << "Failure: (get_dotnet_load_assembly()) Init failed: "
            << std::hex << std::showbase << rc << std::endl;
        std::cerr << "Check "
            "https://github.com/dotnet/runtime/blob/main/docs/design/"
            "features/host-error-codes.md for error code!"
            << std::endl;
        close_fptr(cxt);
        return nullptr;
    }

    /*  // TODO change C++ stack from initialize_for_runtime_config to
       initialize_for_dotnet_command_line,
        //    to load "self-contained" managed DLL instead of
       "framework-dependent"
        //    this leads to portable mode of this native host, where we may not
       need to install dotnet on the target system
        //    .NET Core 2 had TPA List in CoreCLR, but .NET 3 with
       additionalProbingPaths does not work
        // Currently not working
        // Example project:
       https://github.com/V3rzeT/EasyCrossH2/tree/master/Bridge
        // Doc:
       https://github.com/dotnet/core-setup/blob/master/Documentation/design-docs/native-hosting.md
        // Some issues: https://github.com/dotnet/runtime/issues/35465 ,
       https://github.com/dotnet/runtime/issues/35329

        // insert in bool NativeHost::load_hostfxr():
        run_app_fptr = (hostfxr_run_app_fn)get_export(lib, "hostfxr_run_app");
        // same with hostfxr_initialize_for_dotnet_command_line_fn init_fptr_new;
       hostfxr_get_runtime_properties_fn hostfxr_get_runtime_properties
       (NativeHost.h)

        // change above to this
        // const char_t *argv[2] = {
       "/Users/thomasschmeiduch/RiderProjects/Soot.Dotnet/Soot.Dotnet.Decompiler/bin/Debug/netcoreapp3.1/publish/Soot.Dotnet.Decompiler.dll",
       "" }; const char_t *argv[2] =
       {"/Users/thomasschmeiduch/RiderProjects/ConsoleApp2/ConsoleApp2/bin/Debug/netcoreapp3.1/publish/ConsoleApp2.dll"
       }; hostfxr_initialize_parameters params {
            sizeof(hostfxr_initialize_parameters),
            "/Users/thomasschmeiduch/RiderProjects/Soot.Dotnet/Soot.Dotnet.NativeHost/bin/Debug/",
            "/Users/thomasschmeiduch/RiderProjects/ConsoleApp2/ConsoleApp2/bin/Debug/netcoreapp3.1/publish/"
        };
        int rc = init_fptr(1, argv, &params, &cxt);

        // Log properties
        size_t prop_count = 64;
        const char_t *prop_keys[64];
        const char_t *prop_values[64];
        rc = hostfxr_get_runtime_properties(cxt, &prop_count, prop_keys,
       prop_values); if (0 != rc) { std::cerr << "hostfxr did not set up
       properties" << std::hex
                      << std::showbase << rc << std::endl;
            close_fptr(cxt);
        }

        rc = run_app_fptr(cxt);
        std::cout << "Run the app: " << rc << std::endl;
    */

    // Get the load assembly function pointer
    rc = get_delegate_fptr(cxt, hdt_load_assembly_and_get_function_pointer,
        &load_assembly_and_get_function_pointer);
    if (rc != 0 || load_assembly_and_get_function_pointer == nullptr)
        std::cerr << "Failure: (get_dotnet_load_assembly()) Get delegate failed: "
        << std::hex << std::showbase << rc << std::endl;

    close_fptr(cxt);
    return (load_assembly_and_get_function_pointer_fn)
        load_assembly_and_get_function_pointer;
}
