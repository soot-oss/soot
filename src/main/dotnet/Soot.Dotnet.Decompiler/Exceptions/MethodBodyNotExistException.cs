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
using Soot.Dotnet.Decompiler.Models.Protobuf;

namespace Soot.Dotnet.Decompiler.Exceptions
{
    public class MethodBodyNotExistException : Exception
    {
        public MethodBodyNotExistException() : base("Method Body does not exist!")
        {
        }

        public MethodBodyNotExistException(string className,
            string method) : base("Method Body of " + className + "." + method + " does not exist!")
        {
        }

        public MethodBodyNotExistException(string className, string method, int peToken) : base("Method Body of " +
            className + "." + method + " (PE Token: " + peToken + ") does not exist!")
        {
        }
        
        public MethodBodyNotExistException(string className, string property, bool isSetter) : base("Method Body of property " +
            className + "." + property + " (Setter: " + isSetter + ") does not exist!")
        {
        }
        
        public MethodBodyNotExistException(string className, string eventName, EventAccessorType eventType) : base("Method Body of event " +
            className + "." + eventName + " (EventType: " + eventType + ") does not exist!")
        {
        }
        
        
    }
}