#!/usr/bin/awk -f
#/* Soot - a J*va Optimization Framework
# * Copyright (C) 2002 Ondrej Lhotak
# *
# * This library is free software; you can redistribute it and/or
# * modify it under the terms of the GNU Lesser General Public
# * License as published by the Free Software Foundation; either
# * version 2.1 of the License, or (at your option) any later version.
# *
# * This library is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# * Lesser General Public License for more details.
# *
# * You should have received a copy of the GNU Lesser General Public
# * License along with this library; if not, write to the
# * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
# * Boston, MA 02111-1307, USA.
# */

BEGIN {
    print "% THIS FILE IS AUTO-GENERATED FROM THE FILE options."
    print "% DO NOT MODIFY"
    print "\\documentclass{article}"
    print "\\title{Spark Options}"
    print "\\author{Ond\\v{r}ej Lhot\'ak}"
    print "\\begin{document}"
    print "\\maketitle"
    print "This documents describes the command-line options to Spark."
    print "Values for options are specified on the Soot command-line, following"
    print "the switch \\texttt{-p wjtp.Spark}. For example:"
    print ""
    print "\\noindent \\texttt{java soot.Main -a --app -p wjtp.Spark disabled:false,verbose:true hello}"
    print ""
    print "Spark is still under active development, so these options are subject"
    print "to change. For the most current, automatically generated version of"
    print "this document, please see the file"
    print "\\texttt{src/soot/jimple/spark/opts.ps}"
    print "in your Soot directory."
    print "\\tableofcontents"

}
END {
    print "\\end{document}"
}
/^SECTION/ {
    gsub("^SECTION ","");
    print "\\section{"$0"}";
}
/^BOPT/ {
    option = $2;
    getline default;
    allowed = " true false";
    print "\\subsection{Option \\tt "option"}";
    print "\\begin{itemize}";
    print "\\item Allowed values: {\\tt "allowed"}";
    print "\\item Default value: {\\tt "default"}";
    print "\\end{itemize}";
    while( 1 ) {
        getline comment;
        if( comment == "END" ) break;
        print comment;
    }
}

/^MOPT/ {
    option = $2;
    i = 3;
    allowed = "";
    while( $i != "" ) {
        allowed = allowed " " $i;
        i++;
    }
    getline default;
    print "\\subsection{Option \\tt "option"}";
    print "\\begin{itemize}";
    print "\\item Allowed values: {\\tt "allowed"}";
    print "\\item Default value: {\\tt "default"}";
    print "\\end{itemize}";
    while( 1 ) {
        getline comment;
        if( comment == "END" ) break;
        print comment;
    }
}

