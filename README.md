# TravelDBM

#### TravelDBM is an Android app for searching hotels in a determined city. Introduce the city name along with the check-in and check-out dates to discover available hotel options for your stay.

#### You can save hotels as favorites, share hotel information with family and friends, and see hotel details as room prices, services, location, contact information and so on.

#### The app also features a widget which shows your favorite hotels on your home screen and gives you direct access to each of them.

</br>

<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F1.png?alt=media&token=3ca97aff-5100-4d8b-b9dd-3289e250c8e9" width="420" height="692" style="margin:4px"> <img/><img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F2.png?alt=media&token=ac984bd1-3de2-47ea-8d24-62bb23e6a328" width="420" height="692" style="margin:4px">
<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F3.png?alt=media&token=927fd20f-d754-4506-8297-902fc0c94d55" width="420" height="692" style="margin:4px"> <img/><img
src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F4.png?alt=media&token=79b289d1-f8a2-44ca-878e-f0bd66a5def4" width="420" height="692" style="margin:4px">
<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F5.png?alt=media&token=84f5a1a0-68e0-46ef-9936-e27365e0ce37" width="420" height="692" style="margin:4px"> <img/><img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F6.png?alt=media&token=83e35cad-d9ed-405f-a321-80ae0aed6f41" width="420" height="692" style="margin:4px">
<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F7.png?alt=media&token=034972d7-57d1-4ae9-a92c-a7e3098a4b63" width="420" height="692" style="margin:4px"> <img/><img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F8.png?alt=media&token=95b9982f-039a-4ba8-ba8c-d8cf4d11afe3" width="420" height="692" style="margin:4px">
<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/TravelDBM%2F9.png?alt=media&token=022d94f9-e83e-41cc-ae5e-12a1c9fe14a5" width="420" height="692" style="margin:4px">

## Getting Started

#### These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

## Prerequisites

#### TravelDBM was developed using Android Studio IDE so you must install it in your computer before proceeding:

###### https://developer.android.com/studio/

#### Additionally, this application uses the Google Maps API and the Travel Innovation Sandbox API. Therefore, in order to use TravelDBM, you will need both API keys:

###### https://console.developers.google.com/ (for Google Maps API key)
###### https://developers.amadeus.com/ (create an account and follow the instructions to generate an Amadeus API KEY and Amadeus KEY SECRET)

## Next Steps

#### Once you have both API keys (Google Maps key and Travel Innovation Sandbox key), you can proceed to clone the project. DON'T START ANDROID STUDIO YET. After the project is cloned and before entering Android Studio, navigate to the project location and create a file called ***keys.xml*** in the values folder (app/src/main/res/values). In this file add a string resource named MAPS_API_KEY with the Google Maps API key. Follow the example below:

```
<resources>
  <string name="MAPS_API_KEY">Insert your Google Maps API Key</string>
</resources>
```

#### The next step is that you create in the project directory a ***gradle.properties*** file and you need to add your Travel Innovation Sandbox API key to this file with the name API_KEY. Follow the example below:

```
API_KEY_AMADEUS = Insert your API key from Amadeus here using double quotes
API_SECRET = Insert your API SECRET from Amadeus here using double quotes
```

#### The API key from Travel Innovation Sandbox website will expire approximately in 30 days since its creation. After this period, an error message will appear indicating a problem with the server. To solve this, go to the Amadeus Sandbox website, request a new API key and replace it in the ***gradle.properties*** file.

#### Finally, you can start Android Studio, import and build your project and install it on your Android device.

## Compatibility

#### Minimum Android SDK: TravelDBM requires a minimum API level of 15.
#### Compile Android SDK: TravelDBM requires you to compile against API 27 or later.

## Getting Help

#### To report a specific problem or feature request, open a new issue on Github. For questions, suggestions, or anything else, email to:

###### arturo_lpc@hotmail.com

## Author

#### Daniel Bedoya - @engspa12 on GitHub

## License

#### See the LICENSE file for details.
