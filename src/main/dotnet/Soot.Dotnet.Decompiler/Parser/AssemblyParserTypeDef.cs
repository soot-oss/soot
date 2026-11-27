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
using System.Linq;
using Google.Protobuf;
using ICSharpCode.Decompiler.TypeSystem;
using Soot.Dotnet.Decompiler.Helper;
using Soot.Dotnet.Decompiler.Models.Cli;
using Soot.Dotnet.Decompiler.Models.Protobuf;

namespace Soot.Dotnet.Decompiler.Parser
{
    public partial class AssemblyParser
    {
        public CliByteArray GetTypeDefinition(string typeReflectionName)
        {
            var returnValue = new CliByteArray();
            var protoConverter = new ProtoConverter.ProtoConverter();

            var type = GetType(typeReflectionName);
            var typeDefinitionMessage = protoConverter.ToTypeDefinitionMessage(type, true);

            var protoArray = typeDefinitionMessage.ToByteArray();
            returnValue.SetArray(protoArray);
            return returnValue;
        }

        /// <summary>
        ///     Get all Types of a given assembly
        /// </summary>
        /// <returns></returns>
        public CliByteArray GetAllTypeDefinitions()
        {
            var returnValue = new CliByteArray();
            var aacMessage = new AssemblyAllTypes();

            try
            {
                var protoConverter = new ProtoConverter.ProtoConverter();
                foreach (var type in Decompiler.TypeSystem.MainModule.TypeDefinitions)
                {
                    if (type.Name.Equals("<Module>"))
                        continue;

                    aacMessage.ListOfTypes.Add(protoConverter.ToTypeDefinitionMessage(type, true));
                }
                // TODO add support for multi assembly with .netmodule files, after editing ILSpy API

                // add all referenced types
                // TODO replace loop with sth like ICSharpCode.ILSpy.Metadata.TypeRefTableTreeNode or System.Reflection.Metadata.MetadataReader.GetTypeReference()
                foreach (var module in Decompiler.TypeSystem.Modules)
                foreach (var typeDefinition in module.TypeDefinitions)
                {
                    if (typeDefinition.Name.Equals("<Module>"))
                        continue;
                    aacMessage.AllReferencedModuleTypes.Add(
                        DefinitionUtils.ConvertCilToJvmNaming(DefinitionUtils.GetTypeFullname(typeDefinition)));
                }

                var protoArray = aacMessage.ToByteArray();
                returnValue.SetArray(protoArray);
            }
            catch (Exception e)
            {
                Logger.Error(e);
            }

            return returnValue;
        }

        private ITypeDefinition GetType(string typeReflectionName)
        {
            typeReflectionName = DefinitionUtils.ConvertJvmToCilNaming(typeReflectionName);

            var type = Decompiler.TypeSystem.MainModule.TypeDefinitions
                .FirstOrDefault(x => x.ReflectionName.Equals(typeReflectionName));
            if (type == null) {
                string found = "";
                foreach (var s in Decompiler.TypeSystem.MainModule.TypeDefinitions)
                    found += s.ReflectionName + "\n";
                throw new Exception("Given type " + typeReflectionName + " does not exist in the assembly: " +
                                    AssemblyFileAbsolutePath + "!\nFound: " + found);
            }
            return type;
        }
    }
}