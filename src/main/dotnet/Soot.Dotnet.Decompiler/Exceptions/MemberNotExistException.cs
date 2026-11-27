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

namespace Soot.Dotnet.Decompiler.Exceptions
{
    public class MemberNotExistException : Exception
    {
        public MemberNotExistException() : base("Member does not exist!") { }
        
        public MemberNotExistException(Member memberType, string memberName) 
            : base(memberType + " " + memberName + " does not exist!")
        {
        }
        
        public MemberNotExistException(Member memberType, string memberName, string declaringType) 
            : base(memberType + " " + memberName + " at Type " + declaringType + " does not exist!")
        {
        }
        
        public MemberNotExistException(Member memberType, string memberName, int peToken, string declaringType) 
            : base(memberType + " " + memberName + " (PE Token: 0x" + peToken.ToString("x8") + ") at Type " + declaringType + " does not exist!")
        {
        }
        
        public MemberNotExistException(Member memberType) 
            : base(memberType + " does not exist!")
        {
        }
        
        public enum Member
        {
            Method,
            Property,
            Event
        }
    }
}