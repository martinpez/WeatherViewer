# Weather Viewer App

<center>
  <img src="https://res.cloudinary.com/dznbdyjwy/image/upload/v1716179261/Screenshot_20240519_232718_ljdoq3.png" alt="Weather Forecast App" width="400"/>
</center>

## Description
The Weather Forecast App is an Android application that provides a 5-day weather forecast using an API. Additionally, the app displays a personalized greeting based on the time of day and retrieves the device model.

## Features
- **5-Day Weather Viewer:** The app fetches weather forecast data for the next 5 days via an API.
- **Personalized Greeting:** The app displays a personalized greeting (Good Morning, Good Afternoon, Good Evening, Good Night) based on the time of day.
- **Device Model:** Retrieves and displays the device model.

## Requirements
- Android Studio
- Device or emulator with Android 5.0 (Lollipop) or higher
- Internet connection
- A valid API key from OpenWeatherMap

## Permissions
Make sure to add the following permission in your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.INTERNET" />
