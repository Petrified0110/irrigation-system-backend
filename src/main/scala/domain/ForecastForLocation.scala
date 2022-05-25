package domain

import java.time.LocalDate

case class Condition(
  text: String
)

case class Day(
  maxtemp_c: Float,
  mintemp_c: Float,
  avgtemp_c: Float,
  maxwind_kph: Float,
  totalprecip_mm: Float,
  avghumidity: Float,
  daily_will_it_rain: Int,
  daily_chance_of_rain: Int,
  daily_will_it_snow: Int,
  daily_chance_of_snow: Int,
  condition: Condition
)

case class ForecastDay(
  date: LocalDate,
  day: Day,
)

case class Forecast(
  forecastday: Seq[ForecastDay],
)

case class Location(
  name: String,
  region: String,
  country: String,
  localtime: String
)

case class Current(
  last_updated: String,
  condition: Condition,
  temp_c: Float,
  is_day: Int,
  humidity: Int,
  wind_mph: Float,
)

case class ForecastForLocation(
  forecast: Forecast,
  location: Location,
  current: Current
)
