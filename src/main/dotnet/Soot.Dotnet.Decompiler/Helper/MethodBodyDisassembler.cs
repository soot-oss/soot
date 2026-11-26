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
using System.Linq;
using System.Reflection.Metadata;
using System.Reflection.PortableExecutable;
using System.Threading;
using ICSharpCode.Decompiler;
using ICSharpCode.Decompiler.CSharp;
using ICSharpCode.Decompiler.DebugInfo;
using ICSharpCode.Decompiler.IL;
using ICSharpCode.Decompiler.IL.ControlFlow;
using ICSharpCode.Decompiler.IL.Transforms;
using ICSharpCode.Decompiler.Metadata;
using ICSharpCode.Decompiler.TypeSystem;
using NLog;

namespace Soot.Dotnet.Decompiler.Helper
{
    /// <summary>
    ///     Based on the CSharpDecompiler of ILSpy
    ///     https://github.com/icsharpcode/ILSpy/blob/master/ICSharpCode.Decompiler/CSharp/CSharpDecompiler.cs
    /// </summary>
    public class MethodBodyDisassembler
    {
        private static readonly Logger Logger = LogManager.GetCurrentClassLogger();
        private readonly MetadataReader _metadata;
        private readonly MetadataModule _module;
        private readonly DecompilerSettings _settings;

        private readonly IDecompilerTypeSystem _typeSystem;

        public MethodBodyDisassembler(string fileName, DecompilerSettings settings) : this(
            CreateTypeSystemFromFile(fileName, settings), settings)
        {
        }

        private MethodBodyDisassembler(DecompilerTypeSystem typeSystem, DecompilerSettings settings)
        {
            _typeSystem = typeSystem ?? throw new ArgumentNullException(nameof(typeSystem));
            _settings = settings;
            _module = typeSystem.MainModule;
            _metadata = _module.MetadataFile.Metadata;
            if (_module.TypeSystemOptions.HasFlag(TypeSystemOptions.Uncached))
                throw new ArgumentException("Cannot use an uncached type system in the decompiler.");
        }

        /// <summary>
        ///     Token to check for requested cancellation of the decompilation.
        /// </summary>
        private CancellationToken CancellationToken { get; set; }

        /// <summary>
        ///     Gets or sets the optional provider for debug info.
        /// </summary>
        private IDebugInfoProvider DebugInfoProvider { get; set; }

        private static DecompilerTypeSystem CreateTypeSystemFromFile(string fileName, DecompilerSettings settings)
        {
            settings.LoadInMemory = true;
            var file = LoadPeFile(fileName, settings);
            var resolver = new UniversalAssemblyResolver(fileName, settings.ThrowOnAssemblyResolveErrors,
                file.DetectTargetFrameworkId(), file.DetectRuntimePack(),
                settings.LoadInMemory ? PEStreamOptions.PrefetchMetadata : PEStreamOptions.Default,
                settings.ApplyWindowsRuntimeProjections
                    ? MetadataReaderOptions.ApplyWindowsRuntimeProjections
                    : MetadataReaderOptions.None);
            return new DecompilerTypeSystem(file, resolver);
        }

        private static PEFile LoadPeFile(string fileName, DecompilerSettings settings)
        {
            settings.LoadInMemory = true;
            return new PEFile(
                fileName,
                new FileStream(fileName, FileMode.Open, FileAccess.Read),
                settings.LoadInMemory ? PEStreamOptions.PrefetchEntireImage : PEStreamOptions.Default,
                settings.ApplyWindowsRuntimeProjections
                    ? MetadataReaderOptions.ApplyWindowsRuntimeProjections
                    : MetadataReaderOptions.None
            );
        }

        public ILFunction DecompileBody(IMethod method)
        {
            try
            {
                var ilReader = new ILReader(_typeSystem.MainModule)
                {
                    UseDebugSymbols = _settings.UseDebugSymbols,
                    DebugInfo = DebugInfoProvider
                };
                var methodDef = _metadata.GetMethodDefinition((MethodDefinitionHandle)method.MetadataToken);
                MethodBodyBlock methodBody;
                try
                {
                    methodBody = _module.MetadataFile.GetMethodBody(methodDef.RelativeVirtualAddress);
                }
                catch (BadImageFormatException e)
                {
                    Logger.Error(e);
                    return null;
                }

                var function = ilReader.ReadIL((MethodDefinitionHandle)method.MetadataToken, methodBody,
                    cancellationToken: CancellationToken);

                var localSettings = _settings.Clone();
                if (IsWindowsFormsInitializeComponentMethod(method))
                {
                    localSettings.UseImplicitMethodGroupConversion = false;
                    localSettings.UsingDeclarations = false;
                    localSettings.AlwaysCastTargetsOfExplicitInterfaceImplementationCalls = true;
                    localSettings.NamedArguments = false;
                }

                var context = new ILTransformContext(function, _typeSystem, DebugInfoProvider, localSettings)
                {
                    CancellationToken = CancellationToken
                };
                context.Settings.UseRefLocalsForAccurateOrderOfEvaluation = false;
                context.Settings.AggressiveInlining = false;
                context.Settings.NamedArguments = false;

                foreach (var transform in CSharpDecompiler.GetILTransforms())
                {
                    if (transform is ILInlining
                        || transform is BlockILTransform
                        || transform is CopyPropagation
                        || transform is DetectPinnedRegions
                        || transform is YieldReturnDecompiler
                        || transform is AsyncAwaitDecompiler
                        || transform is SwitchOnStringTransform
                        || transform is LocalFunctionDecompiler
                        || transform is TransformExpressionTrees
                        || transform is DynamicCallSiteTransform
                        || transform is DelegateConstruction
                        )
                        continue;
                    CancellationToken.ThrowIfCancellationRequested();

                    try
                    {
                        transform.Run(function, context);
                    }
                    catch (Exception)
                    {
                        // ignored
                    }
                }

                var transform2 = new BlockILTransform { // per-block transforms
					PostOrderTransforms = {
						new StatementTransform(
							new TransformArrayInitializers()
						),
					}
				};

                try
                {
                    transform2.Run(function, context);
                }
                catch (Exception)
                {
                    // ignored
                }
                return function;
            }
            catch (Exception innerException) when (!(innerException is OperationCanceledException ||
                                                     innerException is DecompilerException))
            {
                throw new DecompilerException(_module, method, innerException);
            }
        }

        private static bool IsWindowsFormsInitializeComponentMethod(IMethod method)
        {
            return method.ReturnType.Kind == TypeKind.Void && method.Name == "InitializeComponent" && method
                .DeclaringTypeDefinition.GetNonInterfaceBaseTypes()
                .Any(t => t.FullName == "System.Windows.Forms.Control");
        }
    }
}