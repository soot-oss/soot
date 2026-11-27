/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Thomas Schmeiduch
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

using System;
using System.IO;
using System.Runtime.InteropServices;
using Google.Protobuf;
using Soot.Dotnet.Decompiler;
using Soot.Dotnet.Decompiler.Models.Cli;
using Soot.Dotnet.Decompiler.Models.Protobuf;
using Soot.Dotnet.Decompiler.Parser;

namespace Soot.Dotnet.TestConsole
{
    /// <summary>
    ///     A project for debugging ILSpy objects or proto messages
    ///     Only debugging purpose
    /// </summary>
    internal static class Program
    {
        // TODO must change by each user to local path of ŧNETCore Library --> only relevant for core types
        // Linux
        private const string PathCoreLib =
            "/usr/share/dotnet/shared/Microsoft.NETCore.App/8.0.34/System.Private.CoreLib.dll";

        private static string PathTestSnippetDll = "";

        private static void Main(string[] args)
        {
            PathTestSnippetDll =
                Directory.GetParent(AppDomain.CurrentDomain.BaseDirectory).Parent.Parent.Parent.Parent +
                "/Soot.Dotnet.TestSnippets/bin/Debug/net8.0/Soot.Dotnet.TestSnippets.dll";
            TestGetAllTypes1(); // TODO no dynmaic handling for dotnet core libary path
            // TestTypeDefinition();
            TestTypeDefinition3();
            
              
            //TODO MethodPEToken or MethodSuffix is not set, so the tests don't work
            // TestGetMethodBody();
            //TestGetMethodBody1();
            //TestGetMethodBody2();
            // System.AttributeTargets test;
            // System.Runtime.CompilerServices.MethodImplOptions hello;
            // System.Runtime.InteropServices.CharSet hulu;
            // System.Runtime.InteropServices.LayoutKind hans;
            // System.Runtime.CompilerServices.NullableAttribute test2;

            // TestGetMethodBody4();

            // TestProperty();
            // TestIsAssembly();
        }

        private static void TestIsAssembly()
        {
            CliString assemblyFile;
            assemblyFile.Message = Marshal.StringToHGlobalAuto(PathTestSnippetDll);

            var isAssembly = AssemblyProvider.IsAssembly(assemblyFile, assemblyFile);
        }

        private static void HelperTestMethodBody(AnalyzerParamsMsg protoParams)
        {
            CliByteArray protoParamArr = default;
            protoParamArr.SetArray(protoParams.ToByteArray());

            var m = AssemblyProvider.GetAssemblyContent(protoParamArr);
            var msg = new IlFunctionMsg();
            if (m.Length != 0)
                msg.MergeFrom(m.GetArray());
        }
        // OSX
        // private const string PathCoreLib = "/usr/local/share/dotnet/shared/Microsoft.NETCore.App/3.1.13/System.Private.CoreLib.dll";


        #region Get Types

        private static void TestGetAllTypes1()
        {
            var decompilerParams = new AnalyzerParamsMsg
            {
                AssemblyFileAbsolutePath = PathCoreLib,
                AnalyzerMethodCall = AnalyzerMethodCall.GetAllTypes
            };
            var paramArr = new CliByteArray();
            paramArr.SetArray(decompilerParams.ToByteArray());
            var allTypes = AssemblyProvider.GetAllTypesMsg(paramArr);

            // deserialize test
            var a = new AssemblyAllTypes();
            a.MergeFrom(allTypes.GetArray());

            var assemblyParser = new AssemblyParser(PathCoreLib);
            var allTypesWithOutProtobuff = assemblyParser.GetAllTypeDefinitions();
        }

        private static void TestTypeDefinition()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetTypeDef,
                DebugMode = true,
                AssemblyFileAbsolutePath = PathCoreLib,
                TypeReflectionName = "System.Environment"
            };

            CliByteArray protoParamArr = default;
            protoParamArr.SetArray(protoParams.ToByteArray());

            var m = AssemblyProvider.GetAssemblyContent(protoParamArr);

            // deserialize test
            var msg = new TypeDefinition();
            if (m.Length != 0)
                msg.MergeFrom(m.GetArray());
        }

        private static void TestTypeDefinition3()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetTypeDef,
                DebugMode = true,
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.Types.Inherit"
            };

            CliByteArray protoParamArr = default;
            protoParamArr.SetArray(protoParams.ToByteArray());

            var m = AssemblyProvider.GetAssemblyContent(protoParamArr);

            // deserialize test
            if (m.Length != 0)
            {
                var msg = new TypeDefinition();
                msg.MergeFrom(m.GetArray());
                var method = new MethodDefinition();
                method.MergeFrom(msg.Methods[0]);
                
            }
        }

        #endregion

        #region Members

        private static void TestGetMethodBody()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody,
                DebugMode = true,
                AssemblyFileAbsolutePath = PathCoreLib,
                TypeReflectionName = "System.String",
                MethodName = "Replace",
                MethodPeToken = 0x0
            };
            HelperTestMethodBody(protoParams);
        }

        private static void TestGetMethodBody1()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody,
                MethodName = "ToDictionary[[(``0),(``0,``1),(``0,``2)]]",
                MethodPeToken = 0x0,
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.Types.Typ",
                DebugMode = true
            };
            HelperTestMethodBody(protoParams);
        }

        private static void TestGetMethodBody2()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody,
                MethodName = "TryFilter",
                MethodPeToken = 0x0,
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.IL.Try",
                DebugMode = true
            };
            HelperTestMethodBody(protoParams);
        }

        private static void TestGetMethodBody3()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody,
                MethodName = "Calc",
                MethodPeToken = 0x0,
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.SubPart.SubClass+NestedClass",
                DebugMode = true
            };
            HelperTestMethodBody(protoParams);
        }

        private static void TestGetMethodBody4()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody,
                MethodName = "MyFunction",
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.CallByRefVal",
                MethodPeToken = 0x06000005,
                DebugMode = true
            };
            HelperTestMethodBody(protoParams);
        }

        private static void TestGetMethodBody5()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody,
                MethodName = "DoAlloc",
                MethodPeToken = 0x0,
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.IL.LocAlloc",
                DebugMode = true
            };
            HelperTestMethodBody(protoParams);
        }

        private static void TestProperty()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBodyOfProperty,
                DebugMode = true,
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.Standards",
                PropertyName = "PropWithBody",
                PropertyIsSetter = true
            };
            HelperTestMethodBody(protoParams);
        }

        #endregion
    }
}