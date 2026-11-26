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

namespace Soot.Dotnet.TestSnippets.Types
{
    /// <summary>
    ///     Produce enum stuff, also for attributes
    /// </summary>
    public class Enumm
    {
        public enum MyEnum
        {
            Dies,
            Das = 1337,
            Ananas
        }

        public static void DoSth(MyEnum e = MyEnum.Ananas)
        {
            var c = e;
            if (c == MyEnum.Dies)
            {
                var x = 0;
            }
        }

        public static void CallDoSth()
        {
            var e = MyEnum.Das;
            DoSth(e);
        }

        #region Attributes

        public class MyCustomAttribute : Attribute
        {
            public MyCustomAttribute(int[] values)
            {
                Values = values;
            }

            public MyCustomAttribute(MyEnum myEnum)
            {
                Enu = myEnum;
            }

            public MyCustomAttribute(object myAttrCls)
            {
            }

            public int[] Values { get; set; }

            private MyEnum Enu { get; }

            private void DoSth()
            {
                var en = MyEnum.Ananas;
                InvokeWithEnum(en);
            }

            private void InvokeWithEnum(MyEnum myEnum)
            {
            }
        }

        // [MyCustomAttribute(new[] { 3, 4, 5 })]
        // [MyCustomAttribute(MyEnum.Ananas)]
        // [MyCustomAttribute(typeof(Typ))]
        public class MyArr
        {
        }

        public interface IMyAttrCls
        {
        }

        #endregion
    }
}