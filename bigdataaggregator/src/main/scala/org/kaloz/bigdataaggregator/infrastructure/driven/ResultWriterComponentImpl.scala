package org.kaloz.bigdataaggregator.infrastructure.driven

import java.io.{File, FileOutputStream, PrintWriter}

import org.kaloz.bigdataaggregator.Domain.{PartnerAmountSummary, ResultWriterComponent}

import scalaz.Scalaz._

trait ResultWriterComponentImpl extends ResultWriterComponent {

  class FileResultWriterImpl(resultFileName: String = "target/aggregate.csv") extends ResultWriter {

    def write(result: PartnerAmountSummary): Option[PartnerAmountSummary] = {

      new File(resultFileName).delete()

      val printWriter = new PrintWriter(new FileOutputStream((resultFileName), true))
      try {
        result
          .collect {
          case (partner, amount) => s"$partner,$amount"
        }
          .foreach(printWriter.println(_))
      }
      finally printWriter.close()

      result.some
    }
  }

}
