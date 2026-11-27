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
using Soot.Dotnet.Decompiler.Exceptions;
using Soot.Dotnet.Decompiler.Helper;
using Soot.Dotnet.Decompiler.Models.Cli;

namespace Soot.Dotnet.Decompiler.Parser
{
    public partial class AssemblyParser
    {
        public CliByteArray GetMethodBodyOfProperty(string typeReflectionName, string propertyName, bool isSetter)
        {
            typeReflectionName = DefinitionUtils.ConvertJvmToCilNaming(typeReflectionName);

            var returnValue = new CliByteArray();
            try
            {
                var declaringType = GetType(typeReflectionName);
                
                var propertyDefinition = declaringType.Properties.FirstOrDefault(x => x.Name.Equals(propertyName));
                if (propertyDefinition == null)
                    throw new MemberNotExistException(MemberNotExistException.Member.Property, propertyName);

                var methodDefinition = isSetter switch
                {
                    true when propertyDefinition.CanSet => propertyDefinition.Setter,
                    false when propertyDefinition.CanGet => propertyDefinition.Getter,
                    _ => null
                };

                if (!(methodDefinition is { HasBody: true }))
                    throw new MethodBodyNotExistException(typeReflectionName, propertyDefinition.Name, isSetter);

                var methodBody = ExtractMethodBody(methodDefinition);
                returnValue = ProtoConverter.ProtoConverter.ConvertMethodBody(methodBody);
            }
            catch (MethodBodyNotExistException e)
            {
                Logger.Warn(e.Message);
            }
            catch (MemberNotExistException e)
            {
                Logger.Warn(e.Message);
            }
            catch (Exception e)
            {
                Logger.Error(e);
            }

            return returnValue;
        }
    }
}