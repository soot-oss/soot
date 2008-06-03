#!/usr/bin/perl

## File: backcite.dtx Copyright (C) 1995--1999 Michael Mehlich
## This program can be redistributed and/or modified under the terms
## of the LaTeX Project Public License Distributed from CTAN
## archives in directory macros/latex/base/lppl.txt; either
## version 1 of the License, or any later version.

### DESCRIPTION:
### This script transforms dvi-files to pdf-files using dvihps and ghostscripts pdfwriter
### It has been tested with emtex's dvips and ghostscript 4.01 on WindowsNT
### Usage: dvi2pdf <options>
###        where <options> are the usual dvips options and/or one of the following ones
###        -nopdf    produce only the ps-file not the pdf-file 
###                  (this option is automatically used if output file has suffix .ps)
###        -noborder produces an outputfile where the hyper-links don't have borders
###        -s<x>,<y> shifts the hyper-links by (<x>,<y>)

#Note: This script overwrites eventually existing files named
#        $$.ps, $pdffile.ps, $pdffile.pdf
#      where $$ is the process id (as provided by perl)
#      and $pdffile is the output file name given with the option -o or 
#      derived from the input file name, i.e. the input file name without suffix
 
### CONFIGURATION:
#Access paths for the programs dvihps and ghostscript
$GHOSTSCRIPT = "gs"; #how to call ghostscript
$DVIHPS      = "dvips";    #how to call hyper postscript capable dvips
$REMOVEFILE  = "rm";      #command to delete file

### Probably no changes necessary below this line

#some local initializations
$argno = 0;
$arglist = "";
$dvifile = "";
$psfile  = "";
$pdffile = "";
$xshift = 0; $yshift = 0;  #shift for hyper references
$referenceborder = 1;      #do we want to have a border surrounding hyper references?
$nopdf           = 0;      #we just patch the ps-file if ($nopdf == 1)

#scan arguments for input and output file and preserver rest for forwarding to dvips
while ($argno <= $#ARGV)
  { if ($ARGV[$argno] eq "")
      { #empty argument, ignore
      }
    elsif ($ARGV[$argno] =~ m/^-s([\+\-0-9]+)\,([\+\-0-9]+)$/)
      { $xshift = $1;
        $yshift = $2;
      }
    elsif ($ARGV[$argno] =~ m/^-nopdf$/)
      { $nopdf = 1;
      }
    elsif ($ARGV[$argno] =~ m/^-noborder$/)
      { $referenceborder = 0;
      }
    elsif ($ARGV[$argno] =~ m/^-(.)(.+)$/)
      { #argument follows flag directly 
        if ($1 eq "o")
          { #argument is output file
	    ($pdffile ne "") && die("Sorry, multiple output files cannot be handled");
	    $pdffile = $2;
          }
        else
          { #argument is dvi-argument
	    if ($ARGV[$argno] =~ m/ /)
	      { $arglist .= ' '.'"'.$ARGV[$argno].'"';
	      }
            else
              { $arglist .= ' '.$ARGV[$argno];
              }
          }
      }
    elsif ($ARGV[$argno] =~ m/^-(.)$/)
      { #flag without directly following argument
	if (   ($1 eq "a") || ($1 eq "k") || ($1 eq "m") || ($1 eq "q") || ($1 eq "r") 
	    || ($1 eq "s") || ($1 eq "z") || ($1 eq "A") || ($1 eq "B") || ($1 eq "E")
            || ($1 eq "F") || ($1 eq "K") || ($1 eq "M") || ($1 eq "N") || ($1 eq "R")
            || ($1 eq "U") || ($1 eq "V") || ($1 eq "Z")
	   )
          { #flag without argument
	    if (!($1 eq "z"))
              { $arglist .= ' '.$ARGV[$argno];
	      }
	  }
        elsif ($1 eq "i") 
          { die("Sorry, separate file per section cannot be handled");
          }
        elsif ($1 eq "f")
          { die("Sorry, run in filter mode not possible");
          }
        else
          { #flag with argument
	    if ($1 eq "o")
	      { #argument is output file
	       ($pdffile ne "") && die("Sorry, multiple output files cannot be handled");
                $pdffile = $ARGV[$argno+1];
              }
            else
              { #argument is dvi-argument
                if ($ARGV[$argno+1] =~ m/ /)
		  { $arglist .= ' '.$ARGV[$argno].' '.'"'.$ARGV[$argno+1].'"';
		  }
                else
		  { $arglist .= ' '.$ARGV[$argno].' '.$ARGV[$argno+1];
		  }
	      }
            $argno++;
          }
      }
    else
      { #nonflag argument
	($dvifile ne "") && die("Sorry, multiple input files cannot be handled");
        $dvifile = $ARGV[$argno];
      }
    $argno++;
  }
$arglist = substr($arglist,1,length($arglist)-1);

($dvifile eq "") && die("Sorry, no input file");
if (!($dvifile =~ m/\.[^\.\:\\\/]*$/))
  { $dvifile .= '.dvi';
  }
(!($dvifile =~ m/\.dvi$/)) && die("Sorry, input file has illegal suffix different from .dvi");

if ($pdffile eq "")
  { ($pdffile = $dvifile) =~ s/\.[^\:\\\/\.]*$/\.pdf/;
    if ($pdffile eq $dvifile)
      { $pdffile = $dvifile.'.pdf';
      }
  }
else
  { if ($pdffile =~ m/\.ps$/)
      { $nopdf = 1;
      }
  }

($psfile = $pdffile) =~ s/\.[^\:\\\/\.]*$/\.ps/;

$tmpfile = "$$.ps";

#output arguments
print STDERR "DVI file: $dvifile\n";
print STDERR "PS  file: $psfile\n";
if ($nopdf == 0)
  { print STDERR "PDF file: $pdffile\n";
  }
print STDERR "TMP file: $tmpfile\n";
print STDERR "Arg list: $arglist\n";
print STDERR "Shifting: ($xshift,$yshift)\n";
print STDERR "HyBorder: "; if ($referenceborder) { print STDERR "preserve"; } else { print STDERR "none"; }; print STDERR "\n";

#call dvips
print STDERR "*** $DVIHPS -z $arglist -o $tmpfile $dvifile\n";
system("$DVIHPS -z $arglist -o $tmpfile $dvifile");
(!(-f "$tmpfile")) && die("Error, $DVIHPS could not generate '$tmpfile'");

#path postscript file because we want to use a different fitheight for references to pages
print STDERR "*** PATCH $tmpfile $psfile\n";

open(STDIN,"<$tmpfile") || die("Input file '$tmpfile' not found.\n");
open(STDOUT,">$psfile") || do { close(STDIN); die("Cannot open file '$psfile' for writing.\n") };

while (<STDIN>)
  { if (/^\s*\((.+)\)\s+\[([0-9]+)\s+\[([0-9]+)\s+([0-9]+)\s+([0-9]+)\s+([0-9]+)\]\s+([0-9]+)\]\s+def\s*$/)
      { $name      = $1;       # name of an anchor
        $page      = $2;       # page the anchor is on
        $xa        = $3 + $xshift;       # rectangular box of anchor
        $ya        = $4 + $yshift;
        $xb        = $5 + $xshift;
        $yb        = $6 + $yshift;
	$fitheight = (($ya > $yb) ? $ya : $yb) + 10; # screen top to show when reference to this anchor is choosen
        print STDOUT "($name) [$page [$xa $ya $xb $yb] $fitheight] def\n";
      }
    elsif (/^(.*)\s+\((.+)\)\s+\[\[([0-9]+)\s+([0-9]+)\s+([0-9]+)\s+([0-9]+)\]\s+\[(.+)\]\s+\[(.+)\]\] pdfm\s+(.*)$/)
      { $before         = $1;
        $name           = $2;      # name of the reference
        $xa             = $3 + $xshift;      # rectangular box of reference
        $ya             = $4 + $yshift;
        $xb             = $5 + $xshift;
        $yb             = $6 + $yshift;
        $borderinfo     = $7; #horizontal_corner_radius vertical_corner_radius box_line_width [dash_on_size] [dash_off_size]
        $bordercolor    = $8; #red green blue
        $after          = $9;
	if ($borderinfo =~ m/^\s*([0-9]+)\s+([0-9]+)\s+([0-9]+)\s*\[\s*([0-9]+)\s+([0-9]+)\s*\]\s*$/)
          { $horizontalcornerradius = $1;
            $verticalcornerradius   = $2;
            $boxlinewidth           = $3;
            $dashonsize             = $4;
	    $dashoffsize            = $5;
	    if ($referenceborder == 0)
	      { $boxlinewidth = 0;
	      }
	    $borderinfo = "$horizontalcornerradius $verticalcornerradius $boxlinewidth [$dashonsize $dashoffsize]";
	  }
	print STDOUT "$before ($name) [[$xa $ya $xb $yb] [$borderinfo] [$bordercolor]] pdfm $after\n";
      }
    elsif (/^(.*)\s+\[\[([0-9]+)\s+([0-9]+)\s+([0-9]+)\s+([0-9]+)\]\s+\[(.+)\]\s+\[(.+)\]\]\s+\((.+)\)\s+pdfm\s+(.*)$/)
      { $before         = $1;
        $xa             = $2 + $xshift;      # rectangular box of reference
        $ya             = $3 + $yshift;
        $xb             = $4 + $xshift;
        $yb             = $5 + $yshift;
        $borderinfo     = $6; #horizontal_corner_radius vertical_corner_radius box_line_width [dash_on_size] [dash_off_size]
        $bordercolor    = $7; #red green blue
        $name           = $8;
        $after          = $9;
	if ($borderinfo =~ m/^\s*([0-9]+)\s+([0-9]+)\s+([0-9]+)\s*\[\s*([0-9]+)\s+([0-9]+)\s*\]\s*$/)
          { $horizontalcornerradius = $1;
            $verticalcornerradius   = $2;
            $boxlinewidth           = $3;
            $dashonsize             = $4;
	    $dashoffsize            = $5;
	    if ($referenceborder == 0)
	      { $boxlinewidth = 0;
	      }
	    $borderinfo = "$horizontalcornerradius $verticalcornerradius $boxlinewidth [$dashonsize $dashoffsize]";
	  }
	print STDOUT "$before [[$xa $ya $xb $yb] [$borderinfo] [$bordercolor]] ($name) pdfm $after\n";
      }
    else
     { print STDOUT $_;
     }
  }

close(STDIN);
close(STDOUT);

# delete auxiliary file
print STDERR "*** $REMOVEFILE $tmpfile\n";
system("$REMOVEFILE $tmpfile");

if ($nopdf == 0)
  { # create pdf-file by calling ghostscript
    print STDERR "*** $GHOSTSCRIPT -q -dNOPAUSE -sDEVICE#pdfwrite -sOutputFile#$pdffile $psfile -c quit\n";
    system("$GHOSTSCRIPT -q -dNOPAUSE -sDEVICE#pdfwrite -sOutputFile#$pdffile $psfile -c quit");
    (!(-f "$pdffile")) && die("Error, $DVIHPS could not generate '$pdffile'");
  }
