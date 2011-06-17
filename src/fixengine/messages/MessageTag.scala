/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fixengine.messages

import fixengine.messages.fix43.SeqNumField
import java.lang.{Integer, Character}

abstract class MessageTag[T <: Field](value: Int, clazz: Class[T]) extends Tag[T](value, clazz) {
  val Tag = this
}

abstract class EnumTag[T](value: Int) extends MessageTag[EnumField[Value[T]]](value, classOf[EnumField[Value[T]]]) {
  def parse(v: String) = values.find { value =>
    value.v.toString.equals(v)
  }.getOrElse {
    throw new InvalidValueForTagException(v)
  }
  def valueOf(name: String) = namesAndValues.find { case (n, v) =>
    n.equals(name)
  }.getOrElse {
    throw new Exception("No such enum field with name: " + name)
  }._2
  private def values: Array[Value[T]] = {
    getClass.getDeclaredFields.filter { field =>
      classOf[Value[T]].isAssignableFrom(field.getType)
    }.map{ field =>
      method(field).invoke(this).asInstanceOf[Value[T]]
    }
  }
  private def namesAndValues: Array[(String, Value[T])] = {
    getClass.getDeclaredFields.filter { field =>
      classOf[Value[T]].isAssignableFrom(field.getType)
    }.map{ field =>
      (field.getName, method(field).invoke(this).asInstanceOf[Value[T]])
    }
  }
  private def method(field: java.lang.reflect.Field) = getClass.getDeclaredMethod(field.getName)
}

abstract class Value[T](val v: T) extends Formattable {
  def value = v.toString
}

case class CharValue(char: Character) extends Value[Character](char)
case class IntegerValue(int: Integer) extends Value[Integer](int)
case class StringValue(str: String) extends Value[String](str)
case class BooleanValue(bool: Boolean) extends Value[Boolean](bool)

abstract class BooleanTag(value: Int) extends MessageTag[BooleanField](value, classOf[BooleanField])
abstract class CharTag(value: Int) extends MessageTag[CharField](value, classOf[CharField])
abstract class FloatTag(value: Int) extends MessageTag[FloatField](value, classOf[FloatField])
abstract class IntegerTag(value: Int) extends MessageTag[IntegerField](value, classOf[IntegerField])
abstract class StringTag(value: Int) extends MessageTag[StringField](value, classOf[StringField])

abstract class ExchangeTag(value: Int) extends MessageTag[ExchangeField](value, classOf[ExchangeField])
abstract class LocalMktDateTag(value: Int) extends MessageTag[LocalMktDateField](value, classOf[LocalMktDateField])
abstract class MonthYearTag(value: Int) extends MessageTag[MonthYearField](value, classOf[MonthYearField])
abstract class UtcTimestampTag(value: Int) extends MessageTag[UtcTimestampField](value, classOf[UtcTimestampField])
abstract class NumInGroupTag(value: Int) extends MessageTag[NumInGroupField](value, classOf[NumInGroupField])
abstract class PriceTag(value: Int) extends MessageTag[PriceField](value, classOf[PriceField])
abstract class PriceOffsetTag(value: Int) extends MessageTag[PriceOffsetField](value, classOf[PriceOffsetField])
abstract class QtyTag(value: Int) extends MessageTag[QtyField](value, classOf[QtyField])
abstract class SeqNumTag(value: Int) extends MessageTag[SeqNumField](value, classOf[SeqNumField])

abstract class CurrencyTag(value: Int) extends StringTag(value)
abstract class AmtTag(value: Int) extends FloatTag(value)
