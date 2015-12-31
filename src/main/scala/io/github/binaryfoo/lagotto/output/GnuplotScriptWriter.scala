package io.github.binaryfoo.lagotto.output

import io.github.binaryfoo.lagotto.MsgPairFieldAccess
import org.joda.time.DateTime

import scala.annotation.tailrec

object GnuplotScriptWriter {

  val ToSecondPrecision = """time\(HH:[m0]{2}:[s0]{2}\)""".r
  val ToMinutePrecision = """time\(HH:[m0]{2}\)""".r
  val DateTimeToSecondPrecision = """time\(yyyy-MM-dd HH:[m0]{2}:[s0]{2}\)""".r
  val DateTimeToMinutePrecision = """time\(yyyy-MM-dd HH:[m0]{2}\)""".r

  def write(fields: Seq[String], csvFileName: String, plotFileName: String, xRange: (DateTime, DateTime), plotStyle: PlotStyle, timeFormat: Option[String]): String = {
    val (gnuplotTimeFormat, jodaTimeFormat) = toTimeFormats(fields.head)
    val displayTimeFormat = timeFormat.map(toTimeFormats).map(_._1).getOrElse(gnuplotTimeFormat)
    val columns = fields.tail
    val (firstTime, lastTime) = (xRange._1.toString(jodaTimeFormat), xRange._2.toString(jodaTimeFormat))

    // using tab delimited data fails on empty cells: \t\t gets merged
    // line types cheat sheet: http://kunak.phsx.ku.edu/~sergei/Gnuplot/line_point_types.html

    val (body, plotCount) = plotStyle match {
      case ChartPerColumn =>
        (s"""|set multiplot layout ${columns.size},1 title '$csvFileName'
             |do for [i=2:${fields.size}] {
             |    plot '$csvFileName' using 1:i w lines lt 3 t column(i)
             |}
             |""".stripMargin, columns.size)
      case SingleChart =>
        (s"""|plot for [i=2:${fields.size}] '$csvFileName' using 1:i w lines lt i t column(i)
             |""".stripMargin, 1)
      case ChartPerCluster(clusters) =>
        (clusters.map { cluster =>
          s"""plot for [j in "${cluster.mkString(" ")}"] i=j+0 '$csvFileName' using 1:i w lines lt i t column(i)"""
        }.mkString(s"set multiplot layout ${clusters.size},1 title '$csvFileName'\n", "\n", ""), clusters.size)
    }

    val height = 960 * math.max(1, plotCount / 6)

    val header = s"""#!/usr/bin/env gnuplot
                     |set datafile separator ','
                     |set terminal svg enhanced mouse standalone size 1280,$height
                     |set output '$plotFileName.svg'
                     |#set terminal pngcairo size 1280,$height
                     |#set output '$plotFileName.png'
                     |set xdata time
                     |set timefmt '$gnuplotTimeFormat'
                     |set format x '$displayTimeFormat'
                     |set format y '%.1s %c'
                     |set lmargin 10
                     |set xrange ['$firstTime':'$lastTime']
                     |""".stripMargin

    header + body
  }

  @tailrec
  private def toTimeFormats(field: String): (String, String) = {
    field match {
      case "time" | ToSecondPrecision() => ("%H:%M:%S", "HH:mm:ss")
      case ToMinutePrecision() => ("%H:%M", "HH:mm")
      case "date" => ("%Y-%m-%d", "yyyy-MM-dd")
      case "datetime" | DateTimeToSecondPrecision() => ("%Y-%m-%d %H:%M:%S", "yyyy-MM-dd HH:mm:ss")
      case DateTimeToMinutePrecision() => ("%Y-%m-%d %H:%M", "yyyy-MM-dd HH:mm")
      case MsgPairFieldAccess(_, expr) => toTimeFormats(expr)
    }
  }
}

sealed trait PlotStyle
object ChartPerColumn extends PlotStyle
object SingleChart extends PlotStyle
case class ChartPerCluster(clusters: Seq[Seq[Int]]) extends PlotStyle
