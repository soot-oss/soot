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

using ICSharpCode.Decompiler.IL;
using Soot.Dotnet.Decompiler.Models.Protobuf;

namespace Soot.Dotnet.Decompiler.ProtoConverter
{
    public partial class ProtoConverter
    {
        /// <summary>
        ///     Convert Variable Definitions to proto message
        /// </summary>
        /// <param name="variable"></param>
        /// <returns></returns>
        private IlVariableMsg ToIlVariableMsg(ILVariable variable)
        {
            var v = new IlVariableMsg
            {
                Name = variable.Name,
                Type = ToTypeDefinitionMessage(variable.Type),
                HasInitialValue = variable.UsesInitialValue,
                VariableKind = EnumConverter.ToIlVariableKindEnum(variable.Kind)
            };
            // rename "result" variable in try filter block container, because one method body
            if (_isTryFilter && variable.Name.Equals("result"))
                v.Name = "resultOfFilter";
            return v;
        }
    }
}