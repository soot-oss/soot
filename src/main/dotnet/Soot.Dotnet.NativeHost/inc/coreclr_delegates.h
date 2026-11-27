// Licensed to the .NET Foundation under one or more agreements.
// The .NET Foundation licenses this file to you under the MIT license.

#ifndef __CORECLR_DELEGATES_H__
#define __CORECLR_DELEGATES_H__

#include <stdint.h>

#if defined(_WIN32)
    #define CORECLR_DELEGATE_CALLTYPE __stdcall
#ifdef _WCHAR_T_DEFINED
        typedef wchar_t char_t;
#else
        typedef unsigned short char_t;
#endif
#else
#define CORECLR_DELEGATE_CALLTYPE
using char_t = char;
#endif

#define UNMANAGEDCALLERSONLY_METHOD ((const char_t*)-1)

// Signature of delegate returned by coreclr_delegate_type::load_assembly_and_get_function_pointer
using load_assembly_and_get_function_pointer_fn = int(CORECLR_DELEGATE_CALLTYPE *)(
    const char_t* assembly_path /* Fully qualified path to assembly */,
    const char_t* type_name /* Assembly qualified type name */,
    const char_t* method_name /* Public static method name compatible with delegateType */,
    const char_t* delegate_type_name /* Assembly qualified delegate type name or null
                                        or UNMANAGEDCALLERSONLY_METHOD if the method is marked with
                                        the UnmanagedCallersOnlyAttribute. */,
    void* reserved /* Extensibility parameter (currently unused and must be 0) */,
    /*out*/ void* delegate /* Pointer where to store the function pointer result */);

// Signature of delegate returned by load_assembly_and_get_function_pointer_fn when delegate_type_name == null (default)
using component_entry_point_fn = int(CORECLR_DELEGATE_CALLTYPE *)(void* arg, int32_t arg_size_in_bytes);

using get_function_pointer_fn = int(CORECLR_DELEGATE_CALLTYPE *)(
    const char_t* type_name /* Assembly qualified type name */,
    const char_t* method_name /* Public static method name compatible with delegateType */,
    const char_t* delegate_type_name /* Assembly qualified delegate type name or null,
                                        or UNMANAGEDCALLERSONLY_METHOD if the method is marked with
                                        the UnmanagedCallersOnlyAttribute. */,
    void* load_context /* Extensibility parameter (currently unused and must be 0) */,
    void* reserved /* Extensibility parameter (currently unused and must be 0) */,
    /*out*/ void** delegate /* Pointer where to store the function pointer result */);

#endif // __CORECLR_DELEGATES_H__
