//
// Created by Thomas Schmeiduch.
// Implementation of CliByteArray for marshaling byte array between several data types (Java <-> C++ <-> C#)
//

#ifndef NATIVEHOST_CLI_BYTE_ARRAY_H
#define NATIVEHOST_CLI_BYTE_ARRAY_H

struct cli_byte_array
{
    cli_byte_array()
    {
    }

    /// Define length and pointer of array
    /// \param _len
    /// \param _arr_ptr
    cli_byte_array(int _len, const uint8_t* _arr_ptr)
    {
        length = _len;
        byte_arr_ptr = _arr_ptr;
    }

    int length;
    const uint8_t* byte_arr_ptr;
};

#endif //NATIVEHOST_CLI_BYTE_ARRAY_H
