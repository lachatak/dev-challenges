package org.kaloz.factorysimulation.model

trait MonitorableActivity {
  def getActivityTimeInMillis(): Long

  def setActivityTimeInMillis(time: Long)

  def getFaultPercentage(): Int

  def setFaultPercentage(percentage: Int)

}
