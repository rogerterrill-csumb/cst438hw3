package cst438hw2.service;

import java.util.List;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cst438hw2.domain.*;

@Service
public class CityService {

  @Autowired
  private CityRepository cityRepository;
  @Autowired
  private WeatherService weatherService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private FanoutExchange fanout;

  public void requestReservation(
      String cityName,
      String level,
      String email) {

    String msg = "{\"cityName\": \"" + cityName +
        "\" \"level\": \"" + level +
        "\" \"email\": \"" + email + "\"}";
    System.out.println("Sending message:" + msg);
    rabbitTemplate.convertSendAndReceive(
        fanout.getName(),
        "",
        msg);
  }

  public CityInfo getCityInfo(String cityName) {
    List<City> c = cityRepository.findByName(cityName);
    if (c.size() == 0) {
      return null;
    }
    City city = c.get(0);
    TempAndTime cityTemp = weatherService.getTempAndTime(city.getName());

    return new CityInfo(city, cityTemp);
  }
}
