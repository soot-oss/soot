cmake_minimum_required(VERSION 3.17)
project(NativeHost)


set(CMAKE_CXX_STANDARD 17)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/bin/Debug)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/bin/Debug)

file(MAKE_DIRECTORY ${CMAKE_SOURCE_DIR}/bin/Debug)
add_custom_target(build-time-make-directory ALL
        COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_SOURCE_DIR}/bin/Debug)

include_directories(inc)
include_directories($ENV{JAVA_HOME}/include)

if (APPLE)
    include_directories(inc.vs)

    include_directories(/usr/local/share/dotnet/packs/Microsoft.NETCore.App.Host.osx-x64/6.0.0/runtimes/osx-x64/native)
    include_directories($ENV{JAVA_HOME}/include/darwin)
    link_directories(/usr/local/share/dotnet/packs/Microsoft.NETCore.App.Host.osx-x64/6.0.0/runtimes/osx-x64/native)
elseif(WIN32)
    include_directories(Microsoft.NETCore.App.Host.win-x64/8.0.7/runtimes/win-x64/native/)

    add_definitions(-D_WCHAR_T_DEFINED)


link_directories(${CMAKE_SOURCE_DIR}/Microsoft.NETCore.App.Host.win-x64/8.0.7/runtimes/win-x64/native/)
    # TODO

set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY NotNeeded)
set(JAVA_INCLUDE_PATH2 NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
include_directories(${CMAKE_SOURCE_DIR}/win-includes-java11)
    find_package(JNI)
    if (JNI_FOUND)
        include_directories(${JNI_INCLUDE_DIRS})
        message (STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    endif()


    # may use VisualStudio VC++ Compiler and the .csproj VS project instead of CMake
elseif(UNIX)
    include_directories(inc.vs)

    if(DEFINED $ENV{JAVA_HOME})
        message(FATAL_ERROR "Env JAVA_HOME is not set!") 
    endif()

    find_package(JNI)
    if (JNI_FOUND)
        include_directories(${JNI_INCLUDE_DIRS})
        message (STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    endif()

    if(NOT (IS_DIRECTORY $ENV{DOTNET_RUNTIME_PACK}))
        # readlink -f $(which dotnet)
        message(FATAL_ERROR "Env DOTNET_RUNTIME_PACK is not set correctly, example /usr/share/dotnet/packs/Microsoft.NETCore.App.Host.linux-x64/6.0.13/runtimes/linux-x64/native!") 
    endif()

    include_directories($ENV{DOTNET_RUNTIME_PACK})
    link_directories($ENV{DOTNET_RUNTIME_PACK})

endif()

# only for debug reason
# add_executable(NativeHost inc/coreclr_delegates.h inc/hostfxr.h inc.vs/nethost.h NativeHost.h Settings.cpp inc/Settings.h soot_dotnet_AssemblyFile.cpp NativeHost.cpp inc/cli_string.h inc/cli_byte_array.h Main.cpp Main.h)
if(WIN32)
add_library(NativeHost SHARED
        inc/coreclr_delegates.h
        inc/hostfxr.h
        Microsoft.NETCore.App.Host.win-x64/8.0.7/runtimes/win-x64/native/nethost.h
        NativeHost.h NativeHost.cpp
        Settings.cpp inc/Settings.h
        soot_dotnet_AssemblyFile.cpp
        inc/cli_string.h inc/cli_byte_array.h)

link_directories(NativeHost ${CMAKE_SOURCE_DIR}/Microsoft.NETCore.App.Host.win-x64/8.0.7/runtimes/win-x64/native/
        )
target_link_libraries(NativeHost ${CMAKE_SOURCE_DIR}/Microsoft.NETCore.App.Host.win-x64/8.0.7/runtimes/win-x64/native/libnethost.dll.a
        )
else()
add_library(NativeHost SHARED
        inc/coreclr_delegates.h
        inc/hostfxr.h
        inc.vs/nethost.h
        NativeHost.h NativeHost.cpp
        Settings.cpp inc/Settings.h
        soot_dotnet_AssemblyFile.cpp
        inc/cli_string.h inc/cli_byte_array.h)

target_link_libraries(NativeHost
        dl
        nethost
        )
endif()

set( CMAKE_VERBOSE_MAKEFILE on )

if (APPLE)
    set (CMAKE_SHARED_LINKER_FLAGS "-Wl,-rpath,'@loader_path'")
    add_compile_options(-D OSX)
elseif(UNIX)
    set (CMAKE_SHARED_LINKER_FLAGS "-Wl,-rpath,'$ORIGIN',--disable-new-dtags")
    add_compile_options(-D )
endif()
