# TravelDBM

#### TravelDBM is an Android app for searching hotels in a determined city. It will help you find hotels by giving the name of the city and the check-in and check-out dates.

#### You can save hotels as favorites, share hotel information with family and friends, and see hotel details as room prices, services, location, contact information and so on.

#### The app also features a widget which shows your favorite hotels on your home screen and gives you direct access to each of them.

## Getting Started

#### These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

## Prerequisites

#### TravelDBM was developed using Android Studio IDE so you must install it in your computer before proceeding:

###### https://developer.android.com/studio/

#### Additionally, this application uses the Google Maps API and the Travel Innovation Sandbox API. Therefore, in order to use TravelDBM, you will need both API keys:

###### https://console.developers.google.com/ (for Google Maps API key)
###### https://sandbox.amadeus.com/api-catalog (create an account and follow the instructions to generate a Travel Innovation Sandbox API key)

## Next Steps

#### Once you have both API keys (Google Maps key and Travel Innovation Sandbox key), you can proceed to clone the project. DON'T START ANDROID STUDIO YET. After the project is cloned and before entering Android Studio, navigate to the project location and create a file called ***keys.xml*** in the values folder (app/src/main/res/values). In this file add a string resource named MAPS_API_KEY with the Google Maps API key. Follow the example below:

```
<resources>
  <string name="MAPS_API_KEY">Insert your Google Maps API Key</string>
</resources>
```

#### The next step is that you create in the project directory a ***gradle.properties*** file and you need to add your Travel Innovation Sandbox API key to this file with the name API_KEY. Follow the example below:

```
API_KEY = Insert your Travel Innovation Sandbox API key here using double quotes
```

#### Finally, you can start Android Studio, import and build your project and install it on your Android device.

## Compatibility

#### Minimum Android SDK: TravelDBM requires a minimum API level of 15.
#### Compile Android SDK: TravelDBM requires you to compile against API 27 or later.

## Getting Help

#### To report a specific problem or feature request, open a new issue on Github. For questions, suggestions, or anything else, email to: arturo_lpc@hotmail.com.

## Author

#### Daniel Bedoya - @engspa12 on GitHub

## License

#### See the LICENSE file for details.
