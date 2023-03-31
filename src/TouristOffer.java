public class TouristOffer {
    String nameOfTour;
    int price;
    int duration;
    Transport transport;
    public void setNameOfTour(String nameOfTour) {this.nameOfTour = nameOfTour;}

    public void setPrice(int price) { this.price = price;}

    public void setDuration(int duration) { this.duration = duration;}

    public void setTransport(Transport transport) { this.transport = transport;}

    public String getNameOfTour() {return nameOfTour;}

    public int getPrice() {return price;}

    public int getDuration() {return duration;}

    public Transport getTransport() {return transport;}
    public String toString()
    {
        return "Name of tour: " + this.nameOfTour + "; "
                + "Price: " + this.price + "; "
                + "Dutation: " + this.duration + " days; "
                + "Transport: " + this.defineTransport() + "\n";
    }

    TouristOffer(String nameOfTour, int price, int duration, Transport transport)
    {
        this.duration = duration;
        this.price = price;
        this.nameOfTour = nameOfTour;
        this.transport = transport;
    }
    public String defineTransport()
    {
        return switch (this.transport) {
            case Bus -> "Bus";
            case Ship -> "Ship";
            case Plane -> "Plane";
        };
    }


}
enum Transport
{
   Bus,
   Plane,
   Ship
}
