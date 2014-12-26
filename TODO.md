* Should grep be case insensitive?
* Translate field numbers into names on output
 - needs a data dictionary. perhaps use ISO 8583 defaults (public spec?)
* Dictionary could be used in queries too. Eg -f deviceId=42abcd where deviceId maps to 48.x or 48.y depending on (MTI,NMIC)
* Tail mode (-F)
* Progress logger on stderr
 - number of files, number of records rate and completion time
* Hint about reading stdin if no input after 0.5 second?
* Multi-project with spark and sbt-io examples as children?
* Add --splitBy field to Main? Use case would be ip address or transaction id or link
 - using scalaz streams?
* JSON output format
* Output to websocket and/or server sent events subscribers?
* Apache Spark module
* Output with gnuplot header?
* Can LogFieldExpr allow access to more specific type
 * Override toXsv() on LogEntry?
* Group like uniq -c when count in field list for --tsv or --csv
 - show percentiles for group
 - min/max explode when not parseable as integers
 - HDR histogram: use cases other than rtt and lifespan?
* Output as
 - HTML table
  - colour rows by a field like socket
  - colour rows by match condition like lifespan>1000
 - as ASCII table (need field width?)
 - jira markup (other common table format)
* More icons: session start and end, error
* Add row number field
* Value in multiple threads? actually CPU bound
* Remove unicode dog. Not well supported...
* Output as something readable by PCP (http://www.pcp.io/docs/pcpintro.html)?
* Ascii bar chart
* Validate only one of delay or count is passed
* Build an index and/or process data into more compact/faster to re-read format
 - might ease path to Spark

Bugs:
 * Calculate delays if referenced in --field but not in -t

* Deploy artifact to maven central with fat .jar attachment
* Codeship.io free account
* bintray/sonatype snapshots for artifact hosting?
* List available fields in --help
 - mention regex for ~//
 - mention numeric comparison for >,< with fallback to string
 - rtt
 - delay
 - count - like uniq -c
 - count(condition) like count(rtt>100)
 - concat like mysql's group_concat
 - min,max,sum (maybe useless)
 - time with format - Eg {HH:mm} plus HH:m0 and HH:mm:s0
 - regex mode is partial match by default (link to javadoc)
 - field(/regex/$1/)

use cases:
* rtt per outgoing link
* send time per client
* max concurrent requests?
* stalls -> max delay between messages
* throughput: incoming/sec outgoing/sec