//
// Created by Thomas Schmeiduch.
// Implementation of CliString for marshaling strings between several data types (Java <-> C++ <-> C#)
//

#ifndef NATIVEHOST_CLI_STRING_H
#define NATIVEHOST_CLI_STRING_H

#if defined(_WIN32)
#define HOSTFXR_CALLTYPE __cdecl
#ifdef _WCHAR_T_DEFINED
        typedef wchar_t char_t;
#else
        typedef unsigned short char_t;
#endif
#else
#define HOSTFXR_CALLTYPE
using char_t = char;
#endif




struct cli_string
{
    const char_t* message;
};

#endif //NATIVEHOST_CLI_STRING_H
