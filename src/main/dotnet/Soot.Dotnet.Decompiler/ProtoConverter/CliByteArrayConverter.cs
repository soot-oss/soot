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
using Google.Protobuf;
using NLog;
using Soot.Dotnet.Decompiler.Models.Cli;
using Soot.Dotnet.Decompiler.Models.Protobuf;

namespace Soot.Dotnet.Decompiler.ProtoConverter
{
    public static class CliByteArrayConverter
    {
        public static AnalyzerParamsMsg ToAnalyzerParamsMsg(CliByteArray protoParams)
        {
            // Deserialize Analyzer Params of proto message
            var logger = LogManager.GetCurrentClassLogger();
            var paramsMsg = new AnalyzerParamsMsg();
            try
            {
                var protoBytes = protoParams.GetArray();
                if (protoBytes == null || protoBytes.Length == 0)
                    throw new Exception("No Analyzer parameter were passed!");
                paramsMsg.MergeFrom(protoBytes);
                return paramsMsg;
            }
            catch (Exception e)
            {
                logger.Error(e);
                return null;
            }
        }

        public static CliByteArray FromAnalyzerParamsMsg(AnalyzerParamsMsg paramsMsg)
        {
            var returnValue = new CliByteArray();
            returnValue.SetArray(paramsMsg.ToByteArray());
            return returnValue;
        }
    }
}