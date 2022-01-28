package volk.stopit.storage

import java.time.LocalDateTime

trait Storable {
  def id: Long
  def date: LocalDateTime
}

case class FailLine(id: Long, date: LocalDateTime, reason: String, prevDayCount: Int, toWhat: String, satisfied: Boolean) extends Storable