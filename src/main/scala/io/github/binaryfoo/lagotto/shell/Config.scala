package io.github.binaryfoo.lagotto.shell

import io.github.binaryfoo.lagotto._

/**
 * What should main do? See Options for what each wonderful flag does.
 */
case class Config (filters: Seq[LogFilter] = Seq(),
                   input: Seq[String] = Seq(),
                   format: OutputFormat = FullText,
                   pair: Boolean = false,
                   header: Boolean = true,
                   beforeContext: Int = 0,
                   afterContext: Int = 0,
                   sortBy: Option[FieldExpr] = None,
                   sortDescending: Boolean = false,
                   strict: Boolean = false,
                   progressMeter: ProgressMeter = NullProgressMeter,
                   histogramFields: Seq[String] = Seq(),
                   gnuplotFileName: Option[String] = None) {
  
  def requiresDelayCalculation(): Boolean = includesDelayInFieldList() || includesDelayInFilters()
  
  def includesDelayInFieldList() = {
    format match {
      case Tabular(fields, _) if fields.contains(DelayExpr) => true
      case _ => false
    }
  }
  
  def includesDelayInFilters(): Boolean = {
    filters.collectFirst {
      case FieldFilterOn(DelayExpr) => true
    }.isDefined
  }

  /**
   * Split the output fields (if output is tabular) into the a set of aggregates and a set of key fields.
   * Also include aggregates used in filter expressions in the set of aggregates to support filtering on aggregates
   * that aren't actually output.
   */
  def aggregationConfig(): AggregationSpec = {
    val aggregatesUsedInFilters = filters.flatMap {
      case FieldFilterOn(HasAggregateExpressions(exprs)) => exprs
      case _ => Seq()
    }
    val outputFields = format match {
      case Tabular(fields, _) => fields
      case _ => Seq()
    }
    AggregationSpec.fromExpressions(outputFields ++ aggregatesUsedInFilters)
  }
}
