package domain

case class Temperature(
  current: Option[String],
  min: String,
  max: String
)
case class ForecastForDay(
  date: String,
  description: String,
  icon: String,
  temperature: Temperature,
  wind: String,
  humidity: Int
)
case class ForecastForFrontend(
  forecast: List[ForecastForDay],
  current: ForecastForDay,
)

case class ForecastAndLocation(
  forecast: ForecastForFrontend,
  location: String
)
