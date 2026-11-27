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
using System.Reflection;
using System.Runtime.ExceptionServices;
using System.Threading;
using System.Runtime.Caching;
using NLog;
using Soot.Dotnet.Decompiler.Helper;
using Soot.Dotnet.Decompiler.Models.Cli;
using Soot.Dotnet.Decompiler.Models.Protobuf;
using Soot.Dotnet.Decompiler.Parser;
using Soot.Dotnet.Decompiler.ProtoConverter;

namespace Soot.Dotnet.Decompiler
{
    /// <summary>
    ///     Main Class for the Bridge Soot.Dotnet.NativeHost
    /// </summary>
    public static class AssemblyProvider
    {
        /// <summary>
        ///     Stack size of the thread that runs the decompiler. The JVM threads calling into this library may have
        ///     stacks that are too small for deeply nested CIL, and a managed stack overflow terminates the process.
        ///     The stack is only reserved, pages are committed when used (~2 KiB per nesting level, i.e. this covers
        ///     about 8 000 levels).
        /// </summary>
        private const int DecompilerStackSize = 16 * 1024 * 1024;

        private static readonly Thread DecompilerThread;
        private static readonly SemaphoreSlim WorkAvailable = new SemaphoreSlim(0);
        private static readonly SemaphoreSlim WorkFinished = new SemaphoreSlim(0);
        private static Func<CliByteArray> nextWork;
        private static CliByteArray workResult;
        private static Exception workError;

        static AssemblyProvider()
        {
            // Init Logger, because cannot find nlog.config while running with Soot.Dotnet.NativeHost
            LoggerUtils.InitNlog();

            DecompilerThread = new Thread(DecompilerLoop, DecompilerStackSize)
            {
                IsBackground = true,
                Name = "soot-dotnet-decompiler"
            };
            DecompilerThread.Start();
        }

        /// <summary>
        ///     Runs the given work on the dedicated decompiler thread and returns its result.
        ///     Calls are serialized by the NativeHost, so a single worker thread is sufficient.
        /// </summary>
        private static CliByteArray RunOnDecompilerThread(Func<CliByteArray> work)
        {
            nextWork = work;
            workError = null;
            workResult = default;
            WorkAvailable.Release();
            WorkFinished.Wait();
            if (workError != null)
            {
                ExceptionDispatchInfo.Capture(workError).Throw();
            }

            return workResult;
        }

        private static void DecompilerLoop()
        {
            while (true)
            {
                WorkAvailable.Wait();
                try
                {
                    workResult = nextWork();
                }
                catch (Exception e)
                {
                    workError = e;
                }
                finally
                {
                    WorkFinished.Release();
                }
            }
        }

        #region NativeHost Delegates

        public delegate bool IsAssemblyDelegate(CliString arg, CliString arg2);

        public delegate CliByteArray GetAllTypesMsgDelegate(CliByteArray arg);

        public delegate CliByteArray GetMethodBodyMsgDelegate(CliByteArray arg);

        public delegate CliByteArray GetMethodBodyOfPropertyMsgDelegate(CliByteArray arg);

        public delegate CliByteArray GetMethodBodyOfEventMsgDelegate(CliByteArray arg);

        public delegate CliByteArray GetAssemblyContentDelegate(CliByteArray arg);

        public delegate CliByteArray GetManifestResourceStreamDelegate(CliString arg, CliString arg2);

        #endregion



        /// <summary>
        ///     Returns a resource from the assembly file
        /// </summary>
        /// <param name="assemblyFileAbsolutePath">assembly with absolute path</param>
        /// <param name="name">the name</param>
        /// <returns>resource or null</returns>
        public static CliByteArray GetManifestResourceStream(CliString assemblyFileAbsolutePath, CliString name) {
            Assembly asm = Assembly.LoadFrom(assemblyFileAbsolutePath.ToString());
            
            var s = asm.GetManifestResourceStream(name.ToString());
            CliByteArray result = new CliByteArray();
            if (s != null)
                result.SetArray(ReadStreamFully(s));
            return result;
        }

        public static byte[] ReadStreamFully(Stream input)
        {
            using (MemoryStream ms = new MemoryStream())
            {
                input.CopyTo(ms);
                return ms.ToArray();
            }
        }

        /// <summary>
        ///     Check if given assembly is an assembly
        /// </summary>
        /// <param name="assemblyFileAbsolutePath">assembly with absolute path</param>
        /// <param name="arg2">not used</param>
        /// <returns></returns>
        public static bool IsAssembly(CliString assemblyFileAbsolutePath, CliString arg2)
        {
            return AssemblyUtils.FileIsAssembly(assemblyFileAbsolutePath.ToString());
        }

        /// <summary>
        ///     Load an assembly and return a list of given classes/types with their member definitions
        /// </summary>
        /// <param name="disassemblerParamsMsg">parameters with details about the given assembly and procedure</param>
        /// <returns>list of types</returns>
        public static CliByteArray GetAllTypesMsg(CliByteArray disassemblerParamsMsg)
        {
            var paramsMsg = CliByteArrayConverter.ToAnalyzerParamsMsg(disassemblerParamsMsg);
            if (paramsMsg == null)
                return new CliByteArray();
            paramsMsg.AnalyzerMethodCall = AnalyzerMethodCall.GetAllTypes;
            var newParams = CliByteArrayConverter.FromAnalyzerParamsMsg(paramsMsg);
            return GetAssemblyContent(newParams);
        }

        /// <summary>
        ///     For given Method Definition load method body
        /// </summary>
        /// <param name="disassemblerParamsMsg">parameters with details about the given assembly and procedure</param>
        /// <returns>set of instructions of method body</returns>
        public static CliByteArray GetMethodBodyMsg(CliByteArray disassemblerParamsMsg)
        {
            var paramsMsg = CliByteArrayConverter.ToAnalyzerParamsMsg(disassemblerParamsMsg);
            if (paramsMsg == null)
                return new CliByteArray();
            paramsMsg.AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBody;
            var newParams = CliByteArrayConverter.FromAnalyzerParamsMsg(paramsMsg);
            return GetAssemblyContent(newParams);
        }

        /// <summary>
        ///     For given Property Definition load method body (set, get)
        /// </summary>
        /// <param name="disassemblerParamsMsg">parameters with details about the given assembly and procedure</param>
        /// <returns>set of instructions of method body</returns>
        public static CliByteArray GetMethodBodyOfPropertyMsg(CliByteArray disassemblerParamsMsg)
        {
            var paramsMsg = CliByteArrayConverter.ToAnalyzerParamsMsg(disassemblerParamsMsg);
            if (paramsMsg == null)
                return new CliByteArray();
            paramsMsg.AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBodyOfProperty;
            var newParams = CliByteArrayConverter.FromAnalyzerParamsMsg(paramsMsg);
            return GetAssemblyContent(newParams);
        }

        /// <summary>
        ///     For given Property Definition load method body (add, remove, invoke)
        /// </summary>
        /// <param name="disassemblerParamsMsg">parameters with details about the given assembly and procedure</param>
        /// <returns>set of instructions of method body</returns>
        public static CliByteArray GetMethodBodyOfEventMsg(CliByteArray disassemblerParamsMsg)
        {
            var paramsMsg = CliByteArrayConverter.ToAnalyzerParamsMsg(disassemblerParamsMsg);
            if (paramsMsg == null)
                return new CliByteArray();
            paramsMsg.AnalyzerMethodCall = AnalyzerMethodCall.GetMethodBodyOfEvent;
            var newParams = CliByteArrayConverter.FromAnalyzerParamsMsg(paramsMsg);
            return GetAssemblyContent(newParams);
        }


        private static readonly MemoryCache _cache = new MemoryCache("assemblyCache");



        public static AssemblyParser GetOrParseAssembly(string filename)
        {
            AssemblyParser p = (AssemblyParser)_cache.Get(filename);
            if (p != null) {
                return p;
            }


            //debug mode: false
            AssemblyParser newValue = new AssemblyParser(filename, false);

            var logger = LogManager.GetCurrentClassLogger();
            logger.Info("About to parse assembly " + filename);

            _cache.Set(filename, newValue, DateTimeOffset.Now.AddSeconds(30));
            return newValue;
        }

        /// <summary>
        ///     Base method for getting requested information of Soot (Java)
        ///     Can be used if Bridge (C++) does not implement respective method, which can then used in Soot (Java),
        ///     due to the fact that every proto message is a byte array
        /// </summary>
        /// <param name="disassemblerParamsMsg">parameters with details about the given assembly and procedure</param>
        /// <returns>proto message response of requested action</returns>
        public static CliByteArray GetAssemblyContent(CliByteArray disassemblerParamsMsg)
        {
            return RunOnDecompilerThread(() => GetAssemblyContentCore(disassemblerParamsMsg));
        }

        private static CliByteArray GetAssemblyContentCore(CliByteArray disassemblerParamsMsg)
        {
            var paramsMsg = CliByteArrayConverter.ToAnalyzerParamsMsg(disassemblerParamsMsg);
            if (paramsMsg == null)
                return new CliByteArray();

            var logger = LogManager.GetCurrentClassLogger();
            var assemblyParser = GetOrParseAssembly(paramsMsg.AssemblyFileAbsolutePath);
            try
            {
                return paramsMsg.AnalyzerMethodCall switch
                {
                    AnalyzerMethodCall.GetMethodBody => assemblyParser.GetMethodBody(paramsMsg.TypeReflectionName,
                        paramsMsg.MethodName, paramsMsg.MethodNameSuffix, paramsMsg.MethodPeToken),
                    AnalyzerMethodCall.GetMethodBodyOfEvent => assemblyParser.GetMethodBodyOfEvent(
                        paramsMsg.TypeReflectionName, paramsMsg.EventName, paramsMsg.EventAccessorType),
                    AnalyzerMethodCall.GetMethodBodyOfProperty => assemblyParser.GetMethodBodyOfProperty(
                        paramsMsg.TypeReflectionName, paramsMsg.PropertyName, paramsMsg.PropertyIsSetter),
                    AnalyzerMethodCall.GetTypeDef => assemblyParser.GetTypeDefinition(paramsMsg.TypeReflectionName),
                    AnalyzerMethodCall.GetAllTypes => assemblyParser.GetAllTypeDefinitions(),
                    AnalyzerMethodCall.NoCall => throw new SystemException("AssemblyContent Method was not set!"),
                    _ => throw new SystemException("AssemblyContent Method was not set!")
                };
            }
            catch (Exception e)
            {
                //TODO hier werden fehler nur geloggt und nicht abgefangen macht das so Sinn? --> Der Protobuff aufruffer kriegt nichts von dem Log mit und bekommt Leere Nachricht zurück!
                logger.Error(e);
                return new CliByteArray();
            }
        }

    }
}