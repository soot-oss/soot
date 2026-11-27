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

namespace Soot.Dotnet.Decompiler.Helper
{
    public static class AssemblyUtils
    {
        /// <summary>
        /// Check if given file with absolute path is an assembly
        /// </summary>
        /// <param name="assemblyFileAbsolutePath">Path to the assembly file</param>
        /// <returns></returns>
        public static bool FileIsAssembly(string assemblyFileAbsolutePath)
        {
            try
            {
                if (!assemblyFileAbsolutePath.EndsWith(".exe") && !assemblyFileAbsolutePath.EndsWith(".dll")) 
                    return false;
                // Attempt to resolve the assembly
                AssemblyName.GetAssemblyName(assemblyFileAbsolutePath);
                // Nothing blew up, so it's an assembly
                return true;
            }
            catch(Exception)
            {
                // Something went wrong, it is not an assembly (specifically a 
                // BadImageFormatException will be thrown if it could be found
                // but it was NOT a valid assembly
                return false;
            }   
        }

    }
}