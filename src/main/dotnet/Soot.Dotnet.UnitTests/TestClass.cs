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

using Xunit;
using System;
using System.Collections;
using System.IO;
using System.Linq;
using System.Reflection.Metadata;
using System.Reflection.Metadata.Ecma335;
using System.Reflection.PortableExecutable;
using System.Runtime.InteropServices;
using Google.Protobuf;
using Soot.Dotnet.Decompiler;
using Soot.Dotnet.Decompiler.Models.Cli;
using Soot.Dotnet.Decompiler.Models.Protobuf;
using Soot.Dotnet.Decompiler.Parser;
using MethodDefinition = Soot.Dotnet.Decompiler.Models.Protobuf.MethodDefinition;
using PropertyDefinition = Soot.Dotnet.Decompiler.Models.Protobuf.PropertyDefinition;
using TypeDefinition = Soot.Dotnet.Decompiler.Models.Protobuf.TypeDefinition;

namespace Soot.Dotnet.UnitTests
{
    /// <summary>
    /// A project for debugging and test ILSpy objects or proto messages
    /// Only debugging and test purpose
    /// </summary>
    public class TestClass
    {
        public static readonly string PathTestSnippetDll;
        public static readonly string PathCoreLib;

        static TestClass() {
            PathTestSnippetDll = Path.GetFullPath(Path.Combine(AppDomain.CurrentDomain.BaseDirectory,
                "..", "..", "..", "..", "Soot.Dotnet.TestSnippets", "bin", "Debug", "net8.0",
                "Soot.Dotnet.TestSnippets.dll"));
            PathCoreLib = RuntimeEnvironment.GetRuntimeDirectory() + "/System.Private.CoreLib.dll";
        }

        [Fact]
        public void TestCompleteClass()
        {
            string dllPath = PathTestSnippetDll;
            string className = "Soot.Dotnet.TestSnippets.Standards";
            AnalyzerParamsMsg protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetTypeDef,
                DebugMode = true,
                AssemblyFileAbsolutePath = dllPath,
                TypeReflectionName = className
            };
            
            CliByteArray protoParamArr = default;
            protoParamArr.SetArray(protoParams.ToByteArray());
            
            var m = AssemblyProvider.GetAssemblyContent(protoParamArr);

            // deserialize test
            var msg = new TypeDefinition();
            if (m.Length == 0)
                throw new Exception("Class not parsed correctly!");

            
            msg.MergeFrom(m.GetArray());
            //Test Method Body
            foreach (MethodDefinition method in msg.Methods)
            {
                if(method.HasBody)
                    TestGetMethodBody(dllPath, msg.Fullname, method.Name, method.PeToken);
            }
            //Test Property
            foreach (PropertyDefinition prop in msg.Properties)
            {
               
                if (prop.CanGet &&  prop.Getter.HasBody)
                    TestGetPropertyBody(dllPath, msg.Fullname, prop.Name, false);
                if (prop.CanSet &&  prop.Setter.HasBody)
                    TestGetPropertyBody(dllPath, msg.Fullname, prop.Name, true);
            }
        }
        
        [Fact]
        public void TestIsAssembly()
        {
            CliString assemblyFile;
            assemblyFile.Message = Marshal.StringToHGlobalAuto(PathTestSnippetDll);
            Assert.True(AssemblyProvider.IsAssembly(assemblyFile, assemblyFile));
        }

        #region Get Types

        [Fact]
        public void TestGetAllTypes()
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

            Assert.True(true);
        }
        
        class TypeDefinitionTests : IEnumerable<object[]>
        {
            public IEnumerator<object[]> GetEnumerator()
            {
                yield return new object[] { PathCoreLib, "System.Environment"};
                yield return new object[] { PathTestSnippetDll, "Soot.Dotnet.TestSnippets.CallByRefVal" }; ;
            }

            IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();
        }
        
        [Theory]
        [ClassData(typeof(TypeDefinitionTests))]
        public void TestTypeDefinition(string pathToDll, string typeName)
        {
            AnalyzerParamsMsg protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetTypeDef,
                DebugMode = true,
                AssemblyFileAbsolutePath = pathToDll,
                TypeReflectionName = typeName
            };
            
            CliByteArray protoParamArr = default;
            protoParamArr.SetArray(protoParams.ToByteArray());
            
            var m = AssemblyProvider.GetAssemblyContent(protoParamArr);
            
            // deserialize test
            var msg = new TypeDefinition();
            if (m.Length != 0)
            {
                msg.MergeFrom(m.GetArray());
                Assert.Equal(typeName, msg.Fullname);
            }
            else
                throw new Exception("TypeDefinition message not parsed correctly!");
        }

        #endregion


        /// <summary>
        ///     Resolves the PE token of a method, so that adding snippets does not require updating hardcoded tokens.
        /// </summary>
        /// <param name="typeFullName">full name of the declaring type, nested types separated by '+'</param>
        /// <param name="methodName">name of the method</param>
        public static int PeToken(string typeFullName, string methodName)
        {
            using FileStream stream = File.OpenRead(PathTestSnippetDll);
            using var peReader = new PEReader(stream);
            var metadata = peReader.GetMetadataReader();

            string[] parts = typeFullName.Split('+');
            string @namespace = parts[0].Substring(0, parts[0].LastIndexOf('.'));
            parts[0] = parts[0].Substring(parts[0].LastIndexOf('.') + 1);

            System.Reflection.Metadata.TypeDefinitionHandle typeHandle = default;
            bool found = false;
            foreach (System.Reflection.Metadata.TypeDefinitionHandle candidate in metadata.TypeDefinitions)
            {
                System.Reflection.Metadata.TypeDefinition type = metadata.GetTypeDefinition(candidate);
                if (metadata.GetString(type.Name) == parts[0] && metadata.GetString(type.Namespace) == @namespace)
                {
                    typeHandle = candidate;
                    found = true;
                    break;
                }
            }

            Assert.True(found, "Type " + typeFullName + " not found");
            for (int i = 1; i < parts.Length; i++)
            {
                System.Reflection.Metadata.TypeDefinitionHandle[] nested = metadata.GetTypeDefinition(typeHandle)
                    .GetNestedTypes()
                    .Where(h => metadata.GetString(metadata.GetTypeDefinition(h).Name) == parts[i]).ToArray();
                Assert.Single(nested);
                typeHandle = nested[0];
            }

            System.Reflection.Metadata.MethodDefinitionHandle method = metadata.GetTypeDefinition(typeHandle).GetMethods()
                .Single(h => metadata.GetString(metadata.GetMethodDefinition(h).Name) == methodName);
            return MetadataTokens.GetToken(method);
        }

        #region Members
        class MethodTests : IEnumerable<object[]>
        {
            public IEnumerator<object[]> GetEnumerator()
            {
                // The token of System.Private.CoreLib moves between runtime versions and the assembly is taken from the
                // runtime that hosts the tests, so resolve it there instead of pinning a number that only matches one.
                yield return new object[] { PathCoreLib, "System.String", "Replace",
                        typeof(string).GetMethod("Replace", new[] { typeof(string), typeof(string) })!.MetadataToken };
                yield return new object[] { PathTestSnippetDll, "Soot.Dotnet.TestSnippets.CallByRefVal", "DoRefOutString", PeToken("Soot.Dotnet.TestSnippets.CallByRefVal", "DoRefOutString") };
                yield return new object[] { PathTestSnippetDll, "Soot.Dotnet.TestSnippets.SubPart.SubClass+NestedClass", "Calc", PeToken("Soot.Dotnet.TestSnippets.SubPart.SubClass+NestedClass", "Calc") };
                yield return new object[] { PathTestSnippetDll, "Soot.Dotnet.TestSnippets.IL.Try", "TryFilter", PeToken("Soot.Dotnet.TestSnippets.IL.Try", "TryFilter") };
                yield return new object[] { PathTestSnippetDll, "Soot.Dotnet.TestSnippets.Types.Struc+S", "Ten", PeToken("Soot.Dotnet.TestSnippets.Types.Struc+S", "Ten") };
            }

            IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();
        }
        
        [Theory]
        [ClassData(typeof(MethodTests))]
        public void TestGetMethodBody(string dllPath, string className, string methodName, int methodPeToken)
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody,
                AssemblyFileAbsolutePath = dllPath,
                TypeReflectionName = className,
                MethodName = methodName,
                MethodPeToken = methodPeToken,
                DebugMode = true
            };
            HelperTestMethodBody(protoParams);
        }

        class PropertyTests : IEnumerable<object[]>
        {
            public IEnumerator<object[]> GetEnumerator()
            {
                yield return new object[] { PathTestSnippetDll, "Soot.Dotnet.TestSnippets.Standards", "PropWithBody", true };
                yield return new object[] { PathTestSnippetDll, "Soot.Dotnet.TestSnippets.Standards", "PropWithBody", false };
            }

            IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();
        }
        
        [Theory]
        [ClassData(typeof(PropertyTests))]
        public void TestGetPropertyBody(string dllPath, string className, string propertyName, bool isSetter)
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBodyOfProperty,
                DebugMode = true,
                AssemblyFileAbsolutePath = dllPath,
                TypeReflectionName = className,
                PropertyName = propertyName,
                PropertyIsSetter = isSetter
            };
            HelperTestMethodBody(protoParams);
        }

        [Fact]
        public void TestEvent()
        {
            var protoParams = new AnalyzerParamsMsg
            {
                AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBodyOfEvent,
                DebugMode = true,
                AssemblyFileAbsolutePath = PathTestSnippetDll,
                TypeReflectionName = "Soot.Dotnet.TestSnippets.Events.Shape",
                EventName = "Soot.Dotnet.TestSnippets.Events.IShape.OnDraw",
                EventAccessorType = EventAccessorType.AddAccessor
            };
            HelperTestMethodBody(protoParams);
        }
        
        #endregion

        
      
        
        private static void HelperTestMethodBody(AnalyzerParamsMsg protoParams)
        {
            CliByteArray protoParamArr = default;
            protoParamArr.SetArray(protoParams.ToByteArray());
            
            var m = AssemblyProvider.GetAssemblyContent(protoParamArr);
            var msg = new IlFunctionMsg();
            if (m.Length != 0)
            {
                msg.MergeFrom(m.GetArray());
            }
            else
                throw new Exception("Methode message not parsed correctly!"); //TODO is there any chance to get error from logging class
        }
    }
    
   

}