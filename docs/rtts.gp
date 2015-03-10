#!/usr/bin/env gnuplot
set datafile separator ','
set terminal svg enhanced mouse standalone size 1280,960
set output 'rtts.svg'
set xdata time
set timefmt '%H:%M:%S'
set format x '%H:%M:%S'
set multiplot layout 1,1 title 'Auto-generated by lago'
set lmargin 5
set xrange ['15:12:40.288':'15:12:40.917']
#set xrange [] writeback
do for [i=2:2] {
    plot 'rtts.csv' using 1:i w lines lt 3 t column(i)
    #set xrange restore
}
