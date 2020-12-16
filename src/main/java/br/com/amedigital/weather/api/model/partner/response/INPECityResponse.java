package br.com.amedigital.weather.api.model.partner.response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "cidades")
public class INPECityResponse {

    private List<City> city;

    @XmlElement(name = "cidade")
    public List<City> getCity() {
        return city;
    }

    public void setCity(List<City> city) {
        this.city = city;
    }

    public static class City {

        private String state;
        private Integer cityCode;
        private String name;


        @XmlElement(name = "nome")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlElement(name = "uf")
        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }


        @XmlElement(name = "id")
        public Integer getCityCode() {
            return cityCode;
        }

        public void setCityCode(Integer cityCode) {
            this.cityCode = cityCode;
        }
    }
}
