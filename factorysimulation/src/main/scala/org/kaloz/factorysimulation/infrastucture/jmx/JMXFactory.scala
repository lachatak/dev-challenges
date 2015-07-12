package org.kaloz.factorysimulation.infrastucture.jmx

import java.lang.management.ManagementFactory
import javax.management.ObjectName

import com.typesafe.config.Config
import org.kaloz.factorysimulation.model.MonitorableActivity

import scala.beans.BeanProperty

object JMXFactory {

  lazy val mbs = ManagementFactory.getPlatformMBeanServer

  def register(configType: String, config: Config): MonitorableActivity = {
    val name = new ObjectName(s"factory:type=$configType")
    val mbean = new MonitorableActivityMXBeanImpl(configType, config)
    mbs.registerMBean(mbean, name)
    mbean
  }

  class MonitorableActivityMXBeanImpl(configType: String, config: Config) extends MonitorableActivityMXBean {
    @BeanProperty var activityTimeInMillis: Long = config.getLong(s"$configType.time.in.millis")
    @BeanProperty var faultPercentage: Int = config.getInt(s"$configType.fault.percentage")
  }

  trait MonitorableActivityMXBean extends MonitorableActivity
}
