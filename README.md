## Notify
Notify is an event reminder app, which adds the reminder of events just by scanning the posters. Using OCR, NLP (DateParser) and QR Scanner the information such as location, date and time of the event is extracted by scanning the posters of the events.

**Inspiration**: This idea is inspired by Google automatic calendar notifications.    


## Installation Notes
* Clone the git Repo using `git clone https://github.com/sharmilathirumalai/Notify.git`.
* Open the project in Android Studio.
* Build the project and click on run to run the project.
* And the app can be used by using emulator or by connecting the mobile phone.

**Note**:	Minimum android version to run the application is 6.0 (Marshmallow).


## Device Features
* Camera - required to scan the poster images and to scan the QR codes of the posters.
* Storage - required to store the images of posters captured by the application
* GPS (Global Positioning System) -required to help the users navigate to the event location. This is done by integrating with google maps.


## Libraries Used
*	dm77-barcodescanner: It is a barcode scanner library that reads a barcode and returns the encoded value as a String. (https://github.com/dm77/barcodescanner) [3]
*	joestelmach-natty: natty library is a natural language date parser. This library accepts a String as input and returns List of DateGroups object that has methods to extract the required date. (https://github.com/joestelmach/natty) [4]
*	baoyongzhang-SwipeMenuListView: This library provides option to add functions(share and delete) on swiping a list. (https://github.com/baoyongzhang/SwipeMenuListView) [5]
*	lopspower-CircularImageView: CircularImageView library is used to make circular ImageView in android layout xml. (https://github.com/lopspower/CircularImageView) [6]
*	googlesamples-android-vision: We have used the Google mobile vision api to convert text in image into string. (https://github.com/googlesamples/android-vision) [7]


## Authors
1. Sharmila Thirumalainathan (Owner)
2. Arjun Chandra Balaji Balaraman (Contributor)
3. Dharmambal SureshKumar(Contributor)


## References
1] C. Cao, "android alarmmanager alarms after reboot 2016", Stack Overflow. [Online]. Available: https://stackoverflow.com/questions/38365325/android-alarmmanager-alarms-after-reboot-2016. [Accessed: 26- Jul- 2019].

[2] "Implementing Android App Shortcuts", Medium, 2019. [Online]. Available: https://medium.com/@andreworobator/implementing-android-app-shortcuts-74feb524959b. [Accessed: 26- Jul- 2019].

[3] "dm77/barcodescanner", GitHub, 2019. [Online]. Available: https://github.com/dm77/barcodescanner. [Accessed: 26- Jul- 2019].

[4] "joestelmach/natty", GitHub, 2019. [Online]. Available: https://github.com/joestelmach/natty. [Accessed: 26- Jul- 2019].

[5] "baoyongzhang/SwipeMenuListView", GitHub, 2019. [Online]. Available: https://github.com/baoyongzhang/SwipeMenuListView. [Accessed: 26- Jul- 2019].

[6] "lopspower/CircularImageView", GitHub, 2019. [Online]. Available: https://github.com/lopspower/CircularImageView. [Accessed: 26- Jul- 2019].

[7] "googlesamples/android-vision", GitHub, 2019. [Online]. Available: https://github.com/googlesamples/android-vision. [Accessed: 26- Jul- 2019].

[8] "Flaticon, the largest database of free vector icons", Flaticon, 2019. [Online]. Available: https://www.flaticon.com/. [Accessed: 26- Jul- 2019].

[9] "Login | Piktochart", Create.piktochart.com, 2019. [Online]. Available: https://create.piktochart.com/dashboard. [Accessed: 26- Jul- 2019].

[10] "Ape Tools: Don't Go Ape - Go Ape Tools. App Icon and Splashscreen Generator. Make your app happen.", Apetools.webprofusion.com, 2019. [Online]. Available: https://apetools.webprofusion.com/#/. [Accessed: 26- Jul- 2019].
