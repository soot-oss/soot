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
using System.Reflection.Metadata.Ecma335;
using ICSharpCode.Decompiler.IL;
using ICSharpCode.Decompiler.TypeSystem;
using Soot.Dotnet.Decompiler.Exceptions;
using Soot.Dotnet.Decompiler.Helper;
using Soot.Dotnet.Decompiler.Models.Cli;

namespace Soot.Dotnet.Decompiler.Parser
{
    public partial class AssemblyParser
    {
        public CliByteArray GetMethodBody(string typeReflectionName,
            string methodName, string methodNameSuffix, int peToken)
        {
            methodName = DefinitionUtils.ConvertConstructorMethodName(methodName);
            var logger = Logger.WithProperty("TypeName", typeReflectionName)
                .WithProperty("Method", methodName + methodNameSuffix);

            var returnValue = new CliByteArray();
            try
            {
                var declaringType = GetType(typeReflectionName);

                // Get method definition by PE Token
                IMethod methodDefinition;
                if (peToken != 0)
                    methodDefinition = GetMethodDefinition(declaringType, peToken, methodName);
                else if (DefinitionUtils.GetPeTokenOfMethodSuffix(methodNameSuffix) != 0)
                    methodDefinition = GetMethodDefinition(declaringType,
                        DefinitionUtils.GetPeTokenOfMethodSuffix(methodNameSuffix), methodName);
                else
                    throw new MemberNotExistException(MemberNotExistException.Member.Method, methodName, peToken,
                        declaringType.ReflectionName);

                if (!(methodDefinition is { HasBody: true }))
                    throw new MethodBodyNotExistException(typeReflectionName, methodName, peToken);

                var methodBody = ExtractMethodBody(methodDefinition);
                returnValue = ProtoConverter.ProtoConverter.ConvertMethodBody(methodBody);
            }
            catch (MethodBodyNotExistException e)
            {
                logger.Warn(e.Message);
            }
            catch (SystemException e)
            {
                logger.Error(e);
            }
            catch (Exception e)
            {
                logger.Error(e);
            }

            return returnValue;
        }

        private static IMethod GetMethodDefinition(ITypeDefinition declaringType, int peToken, string methodName)
        {
            var methodDefinitions = declaringType.Methods
                .Where(x => MetadataTokens.GetToken(x.MetadataToken) == peToken).ToList();
            if (methodDefinitions.Count == 0)
                throw new MemberNotExistException(MemberNotExistException.Member.Method, methodName, peToken,
                    declaringType.ReflectionName);
            return methodDefinitions.First();
        }

        /// <summary>
        ///     Extract Method Body of given Method Definition
        /// </summary>
        /// <param name="methodDefinition"></param>
        /// <returns></returns>
        private ILFunction ExtractMethodBody(IMethod methodDefinition)
        {
            if (DebugMode)
                Logger.WithProperty("Method", methodDefinition.ReflectionName).Info("Extracting method body");

            return Disassembler.DecompileBody(methodDefinition);
        }
    }
}