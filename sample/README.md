[Documentation](/docs) > Clover Android SDK 

#Clover Android SDK - Alpha Preview 

[TOC]

##Getting Started 

The Clover Android SDK is packaged as an Android Library Module. The following steps help you get started with the SDK.

### 1: Download the Clover SDK 

Go to [https://github.com/clover/clover-android-sdk/downloads] (https://github.com/clover/clover-android-sdk/downloads) and download the latest version of the SDK. The download contains the SDK as a Library and an example movie-tickets-app that shows how to integrate the library. 

### 2: Extract the downloaded SDK 

For example ~/android-clover-sdk.

### 3: Update your project.properties 
Include the clover-android-sdk as a library in your application. For more information about Android Library, checkout the documentation at [http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject](http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject)

    android.library.reference.1=/<path_to>/android-clover-sdk/clover


### 4. Update AndroidManifest.xml
Ensure that your add has the __"android.permission.INTERNET"__ permission to ensure that the Clover SDK can connect to the Clover servers. 

         <uses-permission android:name="android.permission.INTERNET"/>


## Using the API

Once you've integrated the clover-sdk, the next step is to incorporate the SDK in your application

### Set up the API 
To set up the API in your purchase activity, you call the following:

   cloverSDK = Clover.init(this, "MERCHANT_API_ID");

Please replace the "MERCHANT_API_ID" in the above line with your merchant api id. 

### Optional: Set the first purchase information

    cloverSDK.createFirstPurchaseInfo()
	.setFullName("app_user's_name").setEmail("user-namge@example.com").setPhoneNumber("...");

The first purchase information is only used when Clover is not installed on the user's device. A web overlay is created for such cases and the first purchase information is only used to pre populate the fields, thus reducing the amount of typing required by your users. 

### Creating a Clover Order Request 

     final CloverOrderRequest order = cloverSDK.createOrderRequestBuilder()
        .setAmount("0.50")
        .setTitle("Movie Ticket")
        .setPermissions(new String[] {"full_name", "email_address"})
        .setClientOrderId("my_client_id") // Specify an ID that identifies this item in your application. (such as an item id)
        .build();

The next step setsup the order request for the item. In this case the amount is set to 0.50, title is set to "Movie Ticket" and the application requests the full_name and the email_address permissions. The setClientOrderId("..") takes in an opaque string that can identify this item in your application. This opaque string is round tripped in the order response. 

### Implement `onActivityResult(..)`

The Clover SDK starts an activity for result from your application's activity and thus gets a response back in the onActivityResult. The following snippet ensures that your activity relays the call back to the Clover SDK so that the response intent can be processed 

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    cloverSDK.onResult(requestCode, resultCode, data);
  }

### Creating the Buy Button implementation 

Add an onClickListener that finally triggers the Authorize Order. 

The __authorizeOrder__ takes in the current context, the order request and an instance of the __CloverOrderListener__. The CloverOrderListener lets you listen in on the various order events such as onOrderAuthorized, onCancel and onFailure.

    final Button buyButton = (Button) findViewById(R.id.<button_id>);
    buyButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // authorizeOrder call on the Clover instance makes a call to the Clover App
        // or a web overlay and returns one of the callbacks of an OrderListener
        cloverSDK.authorizeOrder(TestBuyingActivity.this, order, new CloverOrderListener() {
          @Override
          public void onOrderAuthorized(CloverOrder order) {
            // process successfull order 
          }

          @Override
          public void onCancel() {
            // Triggered when an order is cancelled; or the back button is pressed. 
          }

          @Override
          public void onFailure(Throwable th) {
            // Triggered when an Exception/Error happens while processing an order
          }
        });
      }
    });

## Full Example App `movie-tickets-app`

The `movie-tickets-app` shows a full blown example of integrating the Clover SDK in the application. It is included in the clover-android-sdk download. 

## Open Source

Clover <3 open source. All the Clover SDKs are open sourced and [available on github](https://github.com/clover). For example, here is the Android SDK: [https://github.com/clover/clover-android-sdk](https://github.com/clover/clover-android-sdk).
