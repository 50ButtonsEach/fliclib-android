# fliclib-android

This SDK library can be used to create your own functionality for the first generation of Flic buttons ONLY. Documentation for our new Flic 2 buttons can be found at https://github.com/50ButtonsEach/flic2-documentation. Buy buttons at https://flic.io.

# Tutorial

## Get the Flic app
The first thing we need to do is to make sure you have the Flic app installed.

**Why do I need the Flic app?** The fliclib works with the Flic app so that you don't have to worry about handling the Flics, scanning the Flics, or monitoring the communication with them. All of that is taken care of by the Flic app.

1. Download and install the Flic app. It's free and you can find it in the [Google Play Store](https://play.google.com/store/apps/details?id=io.flic.app).

2. Connect all your Flics to the app.

3. That's it! Now you will be able to use the Flic Grabber and get access to the Flics inside your own app. Don't worry, we'll go through how this is done soon.

## Setting up Android Studio
Next we have to import the fliclib to our project in Android Studio.

1. Create a new project or open an existing one. The target of the project must be at least API 19 (Android 4.4).

2. Now open **File -> New -> Import Module...** and select the **fliclib-android** directory which can be checked out from https://github.com/50ButtonsEach/fliclib-android.

3. Add a reference to the `fliclib` in the menu by clicking **File -> Project -> Structure -> app (in the left sidebar) -> Dependencies tab -> The + button in the rightmost section -> Module dependency -> fliclib -> OK**

## Integrate Flic
We will now go through the steps needed in order to fully integrate Flic in your code. We will do it in a blank Android Studio project.

All imported classes are in the package `io.flic.lib`. There are two important classes, `FlicManager` and `FlicButton`. The singleton class `FlicManager` keeps track of all the buttons and `FlicButton` represents a single button. To retrieve buttons, we need to use the **button grabber** and to be able to receive button events in the background we need to register a `FlicBroadcastReceiver`.

1. Before we initiate the `FlicManager` we need to set app credentials using the method `FlicManager.setAppCredentials`. The most appropriate place to do this is either in the creation of the `MainActivity` class or, in a larger application in the Application class. The `appId` and `appSecret` are unique for every application and can be generated on our [developer portal](https://partners.flic.io/partners/developers/credentials). The `appName` should be the friendly name of your app.

```java
// Replace appId and appSecret with your credentials 
// and appName with a friendly name of your app

FlicManager.setAppCredentials("[appId]", "[appSecret]", "[appName]");
```

2. We will now use the manager that can be used to grab a button from the Flic app. The Flic app will be opened up, and the user will be prompted to select one of his/her connected buttons. It will then send information about the button back to our app so that we can start using it. In an activity, this code is used to grab a button.

```java
try {
  FlicManager.getInstance(this, new FlicManagerInitializedCallback() {
    @Override
    public void onInitialized(FlicManager manager) {
      manager.initiateGrabButton(MainActivity.this);
    }
  });
} catch (FlicAppNotInstalledException err) {
    Toast.makeText(this, "Flic App is not installed", Toast.LENGTH_SHORT).show();
}
```

3. To receive the button object, we must feed the result into the manager which then returns the button object. With the button object, we register for notifications. In this example, we’re only interested in down, up and remove events.

```java
@Override
public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
  FlicManager.getInstance(this, new FlicManagerInitializedCallback() {
    @Override
    public void onInitialized(FlicManager manager) {
      FlicButton button = manager.completeGrabButton(requestCode, resultCode, data);
      if (button != null) {
        button.registerListenForBroadcast(FlicBroadcastReceiverFlags.UP_OR_DOWN | FlicBroadcastReceiverFlags.REMOVED);
        Toast.makeText(MainActivity.this, "Grabbed a button", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(MainActivity.this, "Did not grab any button", Toast.LENGTH_SHORT).show();
      }
    }
  });
}
```

4. We are now ready to receive events. The events will be sent to a broadcast receiver. This way the app will receive events even if the Android system has shut down the app process. The intent name to listen for is `io.flic.FLICLIB_EVENT`, which we register in the AndroidManifest file.

```xml
<receiver
  android:name=".ExampleBroadcastReceiver"
  android:enabled="true"
  android:exported="true" >
    <intent-filter>
      <action android:name="io.flic.FLICLIB_EVENT" />
    </intent-filter>
</receiver>
```

5. The broadcast receiver should extend the `FlicBroadcastReceiver` class. It has a method `onRequestAppCredentials` which must be implemented that sets the app credentials. Note that since the app process might be started upon an event, the main activity is not started so it is not enough to provide the app credentials there. We override the methods that will be called upon receiving the corresponding events.

```java
public class ExampleBroadcastReceiver extends FlicBroadcastReceiver {
  @Override
  protected void onRequestAppCredentials(Context context) {
    // Set app credentials by calling FlicManager.setAppCredentials here
  }
  
  @Override
  public void onButtonUpOrDown(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isUp, boolean isDown) {
    if (isUp) {
      // Code for button up event here
    } else {
      // Code for button down event here
    }
  }
  
  @Override
  public void onButtonRemoved(Context context, FlicButton button) {
    // Button was removed
  }
}
```

That’s it! This example project can be found at https://github.com/50ButtonsEach/android-background-example. Also check out the [API documentation](https://api.flic.io/partners/developers/documentation/android/index.html) for more functionality.
