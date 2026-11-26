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
using System.Reflection;
using System.Reflection.Emit;

namespace Soot.Dotnet.TestSnippets.IL
{
    public class LdToken
    {
        /**
         * LdTypeToken Case
         */
        public void DoLdTypeToken()
        {
            MethodInfo loadedMethod = new DynamicMethod("", null, null);
            var getMethodMethod = typeof(MethodBase).GetMethod(
                "GetMethodFromHandle", new[] { typeof(RuntimeMethodHandle) });

            var createdMethod = new DynamicMethod(
                "GetMethodInfo", typeof(MethodInfo), Type.EmptyTypes);

            var il = createdMethod.GetILGenerator();
            il.Emit(OpCodes.Ldtoken, loadedMethod);
            il.Emit(OpCodes.Call, getMethodMethod);
            il.Emit(OpCodes.Castclass, typeof(MethodInfo));
            il.Emit(OpCodes.Ret);

            var func = (Func<MethodInfo>)createdMethod.CreateDelegate(typeof(Func<MethodInfo>));
            Console.WriteLine(func());
        }
    }
}