package sw614f21.p6project.DataStructures;

public enum EventType {
    HeatPumpOn(7.0), PowerPlugsDiningRoomOn(0.0), PowerPlugsAtticOn(0.0), PowerPlugsMasterBedroomOn(0.0), PowerPlugsBedroom2On(23.0), PowerPlugsBedroom4On(0.0), PowerPlugsBedroom3On(0.0),
    PowerClothesWasherOn(0.0), DishwasherOn(0.0), FirstFloorLightsOn(0.0), SecondFloorLightsOn(0.0), ClothesWasherOn(3.0), DryerOn(0.0),
    Bedroom3LaptopOn(0.0), DesktopPCMonitorOn(0.0), FanOn(0.0), HeatingPadOn(0.0), LivingRoomBlueRayOn(0.0), LivingRoomTVOn(0.0), MasterBedroomBlueRayOn(0.0),
    MasterBedroomTVOn(0.0), SlowCookerOn(0.0), VacuumOn(0.0), VideoGameOn(0.0) , Bedroom4LightsOn(0.0), MasterBedroomLightsOn(0.0), Bathroom2LightsOn(0.0),
    Bedroom2LightsOn(0.0), Bathroom1LightsOn(0.0), Bedroom3LightsOn(0.0),  ParentBDownstairs(0.0), ParentAUpstairs(0.0), KitchenLightsAOn(0.0), KitchenLightsBOn(0.0),
    LightsBasementOn(0.0) , LightsAtticOn(0.0), OvenOn(0.0), Bedroom2LaptopOn(0.0), RooftopWindSpeedWindy(4.0), ChildAUpstairs(0.0), ChildBUpstairs(0.0),
    ChildBDownstairs(0.0), ChildADownstairs(0.0);


    public double eventThreshold;

    EventType(double eventThreshold){
        this.eventThreshold = eventThreshold;
    }
}
